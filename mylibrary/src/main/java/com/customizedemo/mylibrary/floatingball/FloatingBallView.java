package com.customizedemo.mylibrary.floatingball;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FloatingBallView extends FrameLayout {

    // 改变状态
    private boolean configurationChanged = false;
    private Integer portraitWidth;
    private Integer landscapeWidth;
    // 横竖屏状态未改变时状态
    private int orientation;

    private ProgressView progressView;
    private ImageView imageView;
    public final Activity activity;
    @Nullable
    private ActionListener listener;

    //在边上状态静止不动x秒，则半透明靠边隐藏
    private static final short ALPHA_HIDE_TIME_THRESHOLD = 5000;

    //半透明靠边隐藏时的透明度
    private static final float ALPHA_HIDE_ALPHA = 0.5f;
    //半透明靠边隐藏时悬浮球的隐藏比例
    private static final float ALPHA_HIDE_PROP = 0.5f;
    //handler消息类型
    private static final int MSG_HALF_HIDE = 1;
    private static final int MSG_POPUP = 2;
    // 动画属性
    private final TimeInterpolator halfHideInterpolator = new OvershootInterpolator(3);
    private final TimeInterpolator endInterpolator = new DecelerateInterpolator();

    private final Rect frame = new Rect();

    //记录当前手指位置在屏幕上的横坐标值
    private float xInScreen;
    //当前手指位置在屏幕上的纵坐标值
    private float yInScreen;
    //手指按下时在屏幕上的横坐标的值
    private float xDownInScreen;
    //手指按下时在屏幕上的纵坐标的值
    private float yDownInScreen;
    //手指按下时在悬浮球View上的横坐标的值
    private float xInView;
    //手指按下时在悬浮球View上的纵坐标的值
    private float yInView;

    private ValueAnimator endAnimation;

    public static final int WIDTH_DP = 46;
    public static final int HEIGHT_DP = 46;
    //动画线程
    private ExecutorService singleAnimatorThreadPool = null;

    private final Object endAnimatorLock = new Object();

    public static ExecutorService genSingleThreadPool() {
        final ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread newThread = Executors.defaultThreadFactory().newThread(r);
                newThread.setName("factoryThread");
                return newThread;
            }
        };
        return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("RtlHardcoded")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_HALF_HIDE) {
                //贴边半隐藏悬浮球
                try {
                    if (!isHalfHiding() && isAttachedToWindow()) {
                        final int x = FloatManager.getInstance().layoutParams.x;
                        final float offset_x;
                        if (isNearestLeft()) {
                            offset_x = -((float) FloatingBallView.this.getMeasuredWidth()) * ALPHA_HIDE_PROP;
                        } else {
                            offset_x = ((float) FloatingBallView.this.getMeasuredWidth()) * ALPHA_HIDE_PROP;
                        }
                        FloatingBallView.this.animate().alpha(ALPHA_HIDE_ALPHA).setInterpolator(halfHideInterpolator).setDuration(500).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                try {
                                    FloatManager.getInstance().layoutParams.x = Math.round(x + offset_x * (float) animation.getAnimatedValue());
                                    FloatManager.getInstance().updateView();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (msg.what == MSG_POPUP) {
                if (isHalfHiding() && isAttachedToWindow()) {
                    //进度100弹出悬浮球
                    FloatingBallView.this.setAlpha(1f);
                    moveEndView(halfHideInterpolator);
                    halfHide();
                }
            }
        }
    };


    public FloatingBallView(Activity activity) {
        super(activity);
        this.activity = activity;
        init(activity);
    }

    public void setListener(@Nullable ActionListener listener) {
        this.listener = listener;
    }

    private void init(Context context) {
        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        imageView = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(ProgressView.dip2px(getContext(), WIDTH_DP - 1), ProgressView.dip2px(getContext(), HEIGHT_DP - 1));
        addView(imageView, layoutParams);

        progressView = new ProgressView(context);
        LayoutParams progressParams = new LayoutParams(ProgressView.dip2px(getContext(), WIDTH_DP), ProgressView.dip2px(getContext(), HEIGHT_DP));
        addView(progressView, progressParams);
        // 初始化View完成更新屏幕大小
        updateSize(activity);
        orientation = getResources().getConfiguration().orientation;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        post(new Runnable() {
            @Override
            public void run() {
                if (orientation != getResources().getConfiguration().orientation) {
                    configurationChanged = true;
                    orientation = getResources().getConfiguration().orientation;
                }
                initStatus();
                halfHide();
                moveEndView();
            }
        });
    }

    /**
     * 初始化悬浮球成正常的状态（不透明）
     */
    public void initStatus() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        animate().cancel();
        FloatingBallView.this.setAlpha(1f);
        if (activity != null && Build.VERSION.SDK_INT >= 28) {
            int layoutInDisplayCutoutMode = activity.getWindow().getAttributes().layoutInDisplayCutoutMode;
            // mParams 为null? 为空判断
            if (FloatManager.getInstance().layoutParams != null) {
                if (FloatManager.getInstance().layoutParams.layoutInDisplayCutoutMode != layoutInDisplayCutoutMode) {
                    FloatManager.getInstance().layoutParams.layoutInDisplayCutoutMode = layoutInDisplayCutoutMode;
                    FloatManager.getInstance().updateView();
                }
            }
        }
        updateSize(activity);
    }

    /**
     * 初始化悬浮球x轴位置（半隐藏时显示到窗口可见位置）
     */
    public void initLocation() {
        if (isShown()) {
            post(new Runnable() {
                @Override
                public void run() {
                    FloatManager.getInstance().layoutParams.x = isNearestLeft() ? 0 : frame.width() - getMeasuredWidth();
                    FloatManager.getInstance().updateView();
                }
            });
        }
    }

    private long downTime;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();
                if (endAnimation != null && endAnimation.isRunning()) {
                    endAnimation.cancel();
                }
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
                initStatus();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY();
                moveView();
                break;
            case MotionEvent.ACTION_UP:
                int scaledWindowTouchSlop = ViewConfiguration.get(getContext()).getScaledWindowTouchSlop();
                if (Math.abs(Math.abs(xDownInScreen) - Math.abs(event.getRawX())) < scaledWindowTouchSlop
                        && Math.abs(Math.abs(yDownInScreen) - Math.abs(event.getRawY())) < scaledWindowTouchSlop
                        && (System.currentTimeMillis() - downTime) < ViewConfiguration.getTapTimeout() * 2L) {
                    if (listener != null) listener.onClick();
                }
                moveEndView();
                halfHide();
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * 移动悬浮球
     */
    private void moveView() {
        FloatManager.getInstance().layoutParams.x = Math.round(xInScreen - xInView - frame.left);
        FloatManager.getInstance().layoutParams.y = Math.round(yInScreen - yInView - frame.top);
        FloatManager.getInstance().updateView();
    }

    /**
     * 移动悬浮球结束（松开手指）
     * 当moveEndView动画开始时 动画线程阻塞 动画结束后流通
     * 防止短时间多次调用moveEndView，动画未结束其他动画开始 影响悬浮球最终位置
     */
    private void moveEndView() {
        if (singleAnimatorThreadPool == null) {
            singleAnimatorThreadPool = genSingleThreadPool();
        }
        singleAnimatorThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moveEndView(endInterpolator);
                    }
                });

                synchronized (endAnimatorLock) {
                    try {
                        endAnimatorLock.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void moveEndView(TimeInterpolator interpolator) {
        final int x = FloatManager.getInstance().layoutParams.x;
        final int y = FloatManager.getInstance().layoutParams.y;
        final int desX = isNearestLeft() ? 0 : frame.width() - getMeasuredWidth();
        final int desY = getLimitY(y);

        endAnimation = ValueAnimator.ofFloat(0, 1);
        endAnimation.setDuration(600);
        endAnimation.setInterpolator(interpolator);

        endAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                FloatManager.getInstance().layoutParams.x = Math.round(x - (x - desX) * animatedValue);
                FloatManager.getInstance().layoutParams.y = Math.round(y - (y - desY) * animatedValue);
                FloatManager.getInstance().updateView();
            }
        });
        endAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                synchronized (endAnimatorLock) {
                    // 动画结束 线程流通
                    endAnimatorLock.notifyAll();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }
        });
        endAnimation.start();
    }


    public void setProgress(int progress) {
        this.progressView.setProgress(progress);
    }

    public int getProgress() {
        return this.progressView.getProgress();
    }

    public boolean isHalfHiding() {
        return getAlpha() != 1F;
    }

    /**
     * 设置图标
     *
     * @param resourceId 资源id
     */
    public void setIconDrawable(int resourceId) {
        if (!(activity == null || activity.isFinishing() || activity.isDestroyed())) {
            Glide.with(getContext()).load(resourceId).into(imageView);
        }
    }

    /**
     * 设置图标
     *
     * @param url 图标链接
     */
    public void setIconDrawable(String url) {
        if (!(activity == null || activity.isFinishing() || activity.isDestroyed())) {
            Glide.with(getContext()).load(url).into(imageView);
        }
    }

    @SuppressLint("RtlHardcoded")
    public void halfHide() {
        if (handler != null) {
            if (!handler.hasMessages(MSG_HALF_HIDE)) {
                handler.sendEmptyMessageDelayed(MSG_HALF_HIDE, ALPHA_HIDE_TIME_THRESHOLD);
            }
        }
    }


    @SuppressLint("RtlHardcoded")
    public void popup() {
        if (handler != null) {
            if (!handler.hasMessages(MSG_POPUP)) {
                handler.sendEmptyMessage(MSG_POPUP);
            }
        }

    }

    /**
     * 更新窗口宽高值
     */
    protected void updateSize(Activity activity) {
        if (activity == null) {
            return;
        }
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (landscapeWidth == null) {
                landscapeWidth = frame.right;
            }
        } else {
            if (portraitWidth == null) {
                portraitWidth = frame.right;
            }
        }
    }

    /**
     * 判断当前x坐标是否在左边
     *
     * @return true:左边
     */
    protected boolean isNearestLeft() {
        if (frame.right == 0) {
            return FloatManager.getInstance().layoutParams.x <= 0;
        }
        if (configurationChanged) {
            configurationChanged = false;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return FloatManager.getInstance().layoutParams.x < portraitWidth / 2;
            } else {
                return FloatManager.getInstance().layoutParams.x < landscapeWidth / 2;
            }
        }
        //触点在屏幕外 以防万一
        if (xInScreen == 0) {
            return (FloatManager.getInstance().layoutParams.x + getMeasuredWidth() / 2) < frame.centerX();
        }
        return (FloatManager.getInstance().layoutParams.x + getMeasuredWidth() / 2) < frame.centerX();
    }

    /**
     * 获取不超出显示区域的合适高度
     *
     * @param y y
     * @return 合适高度
     */
    private int getLimitY(int y) {
        return y <= 0 ? 0 : Math.min(y, (frame.height() - getMeasuredHeight()));
    }

    public void destroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (endAnimation != null && endAnimation.isRunning()) {
            endAnimation.cancel();
        }
        if (singleAnimatorThreadPool != null) {
            singleAnimatorThreadPool.shutdownNow();
            singleAnimatorThreadPool = null;
        }
        this.clearAnimation();

        if (listener != null) {
            listener.onDestroy();
        }
    }

    public interface ActionListener {
        void onClick();

        void onDestroy();
    }

}
