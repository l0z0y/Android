package com.customizedemo.mylibrary.recyclervideo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.customizedemo.mylibrary.api.RequestController;
import com.customizedemo.mylibrary.api.ResponseHandling;
import com.customizedemo.mylibrary.api.ResultCallback;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RecyclerVideoView extends LinearLayout {

    public static List<UriPic> URLs;
    private final Context context;
    private static MyAdapter adapter;
    public int currentPosition = 0;


    public final ResultCallback resultCallback = new ResultCallback() {
        @Override
        public void callback(String result) {
            try {
                ResponseHandling.mp4ResponseHandling(result, new ResultCallback() {
                    @Override
                    public void callback(String result) {
                        RecyclerVideoView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                if (ResponseHandling.URL_ADD_SUCCESS.equals(result)) {
                                    Log.d("RequestController", "mp4ResponseURL_ADD_SUCCESS");
                                    adapter.notifyItemInserted(URLs.size() - 1);
                                }
                            }
                        });
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public RecyclerVideoView(Context context) {
        super(context);
        this.context = context;
        if (URLs == null) {
            URLs = new ArrayList<>();
            Toast.makeText(context, "视频加载失败", Toast.LENGTH_SHORT).show();
        }
        initView();

    }


    /**
     * 初始化View
     */
    private void initView() {

        setBackgroundColor(Color.BLACK);

        RecyclerView recyclerView = new RecyclerView(context);
        addView(recyclerView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        // 滑动状态监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://停止滚动
                        View view = pagerSnapHelper.findSnapView(layoutManager);
                        //当前固定后的item position
                        int position;
                        if (view != null) {
                            position = recyclerView.getChildAdapterPosition(view);
                            if (currentPosition != position) {
                                //如果当前position 和 上一次固定后的position 相同, 说明是同一个, 只不过滑动了一点点, 然后又释放了
                                RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
                                if (viewHolder instanceof MyAdapter.MyViewHolder) {
                                    ((MyAdapter.MyViewHolder) viewHolder).videoView.start();
                                }
                                // 新加载数据到数据源
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addUrl();
                                    }
                                }).start();
                            }
                            currentPosition = position;
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING://拖动
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING://惯性滑动
                        break;
                    default:
                        break;
                }
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        // 首次加载完成时刷新数据源后注销
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                System.out.println("加载完成");
                // 加载完成时刷新数据源
                adapter.notifyDataSetChanged();
                // 注销监听器
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


    }

    // 获取新的url
    private void addUrl() {
        RequestController.getInstance().getMp4(this.resultCallback);
    }

    /**
     * 适配器
     */
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private long downTime;

        class MyViewHolder extends RecyclerView.ViewHolder {
            public VideoView videoView;

            public MyViewHolder(@NonNull ItemView itemView) {
                super(itemView);
                videoView = itemView.videoView;
                // 加载完成回调
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                            @Override
                            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                    //设置背景透明 --预览图消失
                                    videoView.setBackgroundColor(Color.TRANSPARENT);
                                }
                                return true;

                            }
                        });
                    }
                });
                // 播放完成结束回调
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 重新装载播放
                        videoView.resume();
                        videoView.start();
                    }
                });
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            // 获取itemView
            ItemView itemView = new ItemView(context);
            Field mMediaPlayer = null;
            try {
                mMediaPlayer = itemView.videoView.getClass().getDeclaredField("mMediaPlayer");
                mMediaPlayer.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Field finalMMediaPlayer = mMediaPlayer;
            itemView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
                        MediaPlayer o = (MediaPlayer) finalMMediaPlayer.get(itemView.videoView);
                        if (o != null) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                o.setPlaybackParams(o.getPlaybackParams().setSpeed(2));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
            Field finalMMediaPlayer1 = mMediaPlayer;
            itemView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            downTime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_UP:
                            long l = System.currentTimeMillis() - downTime;
                            if (l < 500) {
                                Toast.makeText(context, "点击", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    MediaPlayer o = (MediaPlayer) finalMMediaPlayer1.get(itemView.videoView);
                                    if (o != null) {
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                            o.setPlaybackParams(o.getPlaybackParams().setSpeed(1));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            //获取数据源中预览图 设置为VideoView背景
            Drawable netVideoDrawable = URLs.get(i).bitmapDrawable;
            if (netVideoDrawable != null) {
                myViewHolder.videoView.setBackground(netVideoDrawable);
            }
            // 获取数据源中的视频url
            myViewHolder.videoView.setVideoURI(Uri.parse(URLs.get(i).uri));
            // 当显示的videoView是第一个时自动播放
            if (i == 0) {
                myViewHolder.videoView.start();
            }

        }

        @Override
        public int getItemCount() {
            return URLs.size();
        }


    }

    // 数据源类
    public static class UriPic {
        public String uri;
        public Drawable bitmapDrawable;

        public UriPic(String uri, Drawable drawable) {
            this.uri = uri;
            this.bitmapDrawable = drawable;
        }
    }


}
