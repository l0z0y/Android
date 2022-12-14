package com.customizedemo.mylibrary.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.customizedemo.mylibrary.MyApplication;
import com.customizedemo.mylibrary.api.RequestController;
import com.customizedemo.mylibrary.api.ResponseHandling;
import com.customizedemo.mylibrary.api.ResultCallback;
import com.customizedemo.mylibrary.util.ResUtil;
import com.customizedemo.mylibrary.util.ScreenUtil;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MusicView extends LinearLayout {

    private TextView title, author, realTime, allTime;
    private SeekBar seekBar;
    private Button refresh, previous, stop, next;
    private MediaPlayer mediaPlayer;
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(5);
    public static List<SongInfo> songInfos;
    private int nowIndex = 0;
    private ImageView imageView;
    private boolean isTrackTouch = false;


    public MusicView(Context context) {
        super(context);
        initView(context);
        for (int i = 0; i < 5; i++) {
            initSongs();
        }
        initMediaPlayer();
    }

    private void initSongs() {
        RequestController.getInstance().getMp3(new ResultCallback() {
            @Override
            public void callback(String result) {
                ResponseHandling.mp3ResponseHandling(result, new ResultCallback() {
                    @Override
                    public void callback(String result) {
                        if (result.startsWith(ResponseHandling.URL_ADD_SUCCESS)) {
                            if (MyApplication.firstCall) {
                                MyApplication.firstCall = false;
                                play(songInfos.get(nowIndex));
                            }
                        }
                    }
                });
            }
        });
    }


    private void initView(Context context) {

        setOrientation(VERTICAL);
        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setBackground(ResUtil.drawableValue(context, "background_shadow"));

        LinearLayout titleLinearLayout = new LinearLayout(context);
        imageView = new ImageView(context);
        LayoutParams imageParams = new LayoutParams(ScreenUtil.dip2px(context, 60), ScreenUtil.dip2px(context, 60));
        imageParams.topMargin = ScreenUtil.dip2px(context, 10);
        imageParams.leftMargin = ScreenUtil.dip2px(context, 20);
        titleLinearLayout.addView(imageView, imageParams);

        LinearLayout infoLinearLayout = new LinearLayout(context);
        LayoutParams infoParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        infoLinearLayout.setOrientation(VERTICAL);
        infoLinearLayout.setGravity(Gravity.END);
        infoParams.leftMargin = ScreenUtil.dip2px(context, 20);
        infoParams.rightMargin = ScreenUtil.dip2px(context, 20);

        title = new MarqueeTextView(context);
        title.setTextSize(17);
        LayoutParams titleParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
        title.setGravity(Gravity.END | Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        titleParams.weight = 3;
        infoLinearLayout.addView(title, titleParams);

        author = new MarqueeTextView(context);
        author.setTextSize(15);
        author.setTextColor(Color.BLACK);
        author.setGravity(Gravity.END | Gravity.CENTER);
        LayoutParams authorParams = new LayoutParams(ScreenUtil.dip2px(context, 100), 0);
        authorParams.weight = 2;
        infoLinearLayout.addView(author, authorParams);

        titleLinearLayout.addView(infoLinearLayout, infoParams);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(titleLinearLayout, params);

        LinearLayout progressLinearLayout = new LinearLayout(context);
        LayoutParams progressParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        progressParams.topMargin = ScreenUtil.dip2px(context, 10);
        progressParams.bottomMargin = ScreenUtil.dip2px(context, 10);

        realTime = new TextView(context);
        realTime.setGravity(Gravity.END);
        LayoutParams realTimeParams = new LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);

        realTimeParams.weight = 1;
        realTime.setText("00:00");
        progressLinearLayout.addView(realTime, realTimeParams);

        seekBar = new SeekBar(context);
        LayoutParams seekBarParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        seekBarParams.weight = 7;
        // ??????????????????progressBar?????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            seekBar.setMaxHeight(ScreenUtil.dip2px(context, 3));
        } else {
            try {
                Class<?> superclass = seekBar.getClass().getSuperclass().getSuperclass();
                Field mMaxHeight = superclass.getDeclaredField("mMaxHeight");
                mMaxHeight.setAccessible(true);
                mMaxHeight.set(seekBar, ScreenUtil.dip2px(context, 4));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        seekBar.setPadding(ScreenUtil.dip2px(context, 10), 0, ScreenUtil.dip2px(context, 10), 0);
        seekBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#0DA5D3")));
        seekBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#c0c0c0")));
        seekBar.setThumb(null);

        progressLinearLayout.addView(seekBar, seekBarParams);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String s = calculateTime(mediaPlayer.getCurrentPosition());
                realTime.setText(s);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTrackTouch = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTrackTouch = false;
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        allTime = new TextView(context);
        allTime.setText("00:00");
        allTime.setGravity(Gravity.START);
        LayoutParams allTimeParams = new LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        allTimeParams.weight = 1;
        progressLinearLayout.addView(allTime, allTimeParams);

        addView(progressLinearLayout, progressParams);

        LinearLayout buttonLinearLayout = new LinearLayout(context);
        LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.gravity = Gravity.CENTER;
        buttonParams.bottomMargin = ScreenUtil.dip2px(context, 10);

        refresh = new Button(context);
        refresh.setBackground(ResUtil.drawableValue(context, "refresh"));
        buttonLinearLayout.addView(refresh, new LayoutParams(ScreenUtil.dip2px(context, 26), ScreenUtil.dip2px(context, 26)));
        refresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewSong();
            }
        });

        previous = new Button(context);
        previous.setBackground(ResUtil.drawableValue(context, "previous"));
        LayoutParams previousParams = new LayoutParams(ScreenUtil.dip2px(context, 26), ScreenUtil.dip2px(context, 26));
        previousParams.leftMargin = ScreenUtil.dip2px(context, 50);
        buttonLinearLayout.addView(previous, previousParams);

        previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setProgress(0);
                if (songInfos.size() > 1 && nowIndex > 0) {
                    nowIndex--;
                    SongInfo songInfo = songInfos.get(nowIndex);
                    play(songInfo);
                } else {
                    Toast.makeText(context, "???????????????????????????", Toast.LENGTH_SHORT).show();
                }
            }
        });

        stop = new Button(context);
        stop.setBackground(ResUtil.drawableValue(context, "stop"));
        LayoutParams stopParams = new LayoutParams(ScreenUtil.dip2px(context, 26), ScreenUtil.dip2px(context, 26));
        stopParams.leftMargin = ScreenUtil.dip2px(context, 60);
        buttonLinearLayout.addView(stop, stopParams);

        stop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    stop.setBackground(ResUtil.drawableValue(context, "stop"));
                } else {
                    mediaPlayer.start();
                    stop.setBackground(ResUtil.drawableValue(context, "play"));
                }
            }
        });

        next = new Button(context);
        next.setBackground(ResUtil.drawableValue(context, "next"));
        LayoutParams nextParams = new LayoutParams(ScreenUtil.dip2px(context, 26), ScreenUtil.dip2px(context, 26));
        nextParams.leftMargin = ScreenUtil.dip2px(context, 60);
        nextParams.rightMargin = ScreenUtil.dip2px(context, 76);
        buttonLinearLayout.addView(next, nextParams);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setProgress(0);
                playNext();
            }
        });

        addView(buttonLinearLayout, buttonParams);
    }


    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        // MediaPlayer????????????
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i("MediaPlayer", "onError");
                Toast.makeText(getContext(), "??????????????????,?????????????????????~", Toast.LENGTH_SHORT).show();
                mediaPlayer.reset();
                playNext();
                return true;
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNext();
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int duration = mediaPlayer.getDuration();
                allTime.setText(calculateTime(duration));
                seekBar.setMax(duration);
                mediaPlayer.start();
                stop.setBackground(ResUtil.drawableValue(getContext(), "play"));
                service.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer.isPlaying() && !isTrackTouch) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                seekBar.setProgress(mediaPlayer.getCurrentPosition(), true);
                            } else {
                                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            }
                        }
                    }
                }, 0, 1, TimeUnit.SECONDS);
            }
        });
    }

    private void playNext() {
        if (songInfos.size() - 3 <= nowIndex) {
            getNewSong();
        }
        if (nowIndex < songInfos.size() - 1) {
            nowIndex++;
            play(songInfos.get(nowIndex));
        } else {
            Toast.makeText(getContext(), "?????????????????????", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ????????????????????????
     */
    private void getNewSong() {
        if (new Random().nextBoolean()) {
            RequestController.getInstance().getSongFromPlaylist(new ResultCallback() {
                @Override
                public void callback(String result) {
//                    Log.i(ResponseHandling.URL_ADD_SUCCESS, result.substring(result.indexOf("\n")));
                }
            });
        } else {
            RequestController.getInstance().getMp3(new ResultCallback() {
                @Override
                public void callback(String result) {
                    try {
                        ResponseHandling.mp3ResponseHandling(result, new ResultCallback() {
                            @Override
                            public void callback(String result) {
//                                Log.i(ResponseHandling.URL_ADD_SUCCESS, result.substring(result.indexOf("\n")));
                            }
                        });
                    } catch (
                            Exception e) {
                        Toast.makeText(getContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * ????????????
     */
    private void play(SongInfo songInfo) {
        try {
            String name = songInfo.name;
            String url = songInfo.url;
            String artistsName = songInfo.artistsname;
            String picurl = songInfo.picurl;
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(artistsName) && !TextUtils.isEmpty(url) && !TextUtils.isEmpty(picurl)) {
                title.setText(name);
                author.setText(artistsName);
                Glide.with(getContext()).load(picurl).into(imageView);
                mediaPlayer.reset();
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();
            } else {
                Toast.makeText(getContext(), "??????????????????", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "??????????????????", Toast.LENGTH_SHORT).show();
            playNext();
            e.printStackTrace();
        }
    }

    //??????????????????
    public String calculateTime(int time) {
        int minute;
        int second;
        if (time >= 60000) {
            minute = time / 60000;
            second = time % 60000 / 1000;
            //?????????0~9
            if (minute < 10) {
                //?????????
                if (second < 10) {
                    return "0" + minute + ":" + "0" + second;
                } else {
                    return "0" + minute + ":" + second;
                }
            } else {
                //????????????10????????????
                if (second < 10) {
                    return minute + ":" + "0" + second;
                } else {
                    return minute + ":" + second;
                }
            }
        } else {
            second = time / 1000;
            if (second >= 0 && second < 10) {
                return "00:" + "0" + second;
            } else {
                return "00:" + second;
            }
        }
    }

    /**
     * ???????????????
     */
    public static class SongInfo {
        String name;
        String artistsname;
        String url;
        String picurl;

        public SongInfo(String name, String artistsname, String url, String picurl) {
            this.name = name;
            this.artistsname = artistsname;
            this.url = url;
            this.picurl = picurl;
        }

    }

    public void destroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        service.shutdownNow();
    }

}