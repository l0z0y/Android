package com.customizedemo.mylibrary.floatingball;


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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.customizedemo.mylibrary.util.ResUtil;

public class FloatingBallView extends FrameLayout {


    private ProgressView progress;
    private LayoutParams progressParams;
    private ImageView imageView;
    private Activity activity;
    private ActionListener listener;

    //在边上状态静止不动x秒，则半透明靠边隐藏
    private static final short ALPHA_HIDE_TIME_THRESHOLD = 5000;

    //是否显示进度条
    private boolean isShowProgress = true;
    //是否执行半透明靠边隐藏   true:执行
    private boolean isHalfHide = true;
    // 是否已经靠边隐藏
    private boolean isHalfHidding = false;
    //半透明靠边隐藏时的透明度
    private static final float ALPHA_HIDE_ALPHA = 0.5f;
    //半透明靠边隐藏时悬浮球的隐藏比例
    private static final float ALPHA_HIDE_PROP = 0.5f;
    //handler消息类型
    private static final int MSG_HALF_HIDE = 1;
    private static final int MSG_HALF_SHOW = 2;
    //当点击与松开位置小于此阀值视为点击事件
    private static int clickThreshold = 15;
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

    //悬浮球位置
    public final static int RIGHT = 1;
    public final static int LEFT = 0;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("RtlHardcoded")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_HALF_HIDE) {
                //贴边半隐藏悬浮球
                try {
                    if (isHalfHide && !isHalfHidding && FloatManager.getInstance().getShowing()) {
                        if (isNearestLeft()) {
                            final float offset_x = ((float) FloatingBallView.this.getMeasuredWidth()) * ALPHA_HIDE_PROP;
                            final int x = FloatManager.getInstance().layoutParams.x;
                            FloatingBallView.this.animate().alpha(ALPHA_HIDE_ALPHA).setInterpolator(halfHideInterpolator).setDuration(500).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    try {
                                        FloatManager.getInstance().layoutParams.x = Math.round(x - offset_x * (float) animation.getAnimatedValue());
                                        FloatManager.getInstance().updateView();
                                        isHalfHidding = true;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            final float offset_x = ((float) FloatingBallView.this.getMeasuredWidth()) * ALPHA_HIDE_PROP;
                            final int x = FloatManager.getInstance().layoutParams.x;
                            FloatingBallView.this.animate().alpha(ALPHA_HIDE_ALPHA).setInterpolator(halfHideInterpolator).setDuration(500).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    try {
                                        FloatManager.getInstance().layoutParams.x = Math.round(x + offset_x * (float) animation.getAnimatedValue());
                                        FloatManager.getInstance().updateView();
                                        isHalfHidding = true;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (msg.what == MSG_HALF_SHOW) {
                //显示悬浮球
                try {
                    if (isHalfHide && isHalfHidding && FloatManager.getInstance().getShowing()) {
                        if (isNearestLeft()) {
                            final float offset_x = ((float) FloatingBallView.this.getMeasuredWidth()) * ALPHA_HIDE_PROP;
                            final int x = FloatManager.getInstance().layoutParams.x;
                            FloatingBallView.this.animate().alpha(ALPHA_HIDE_ALPHA).setInterpolator(halfHideInterpolator).setDuration(500).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    try {
                                        FloatManager.getInstance().layoutParams.x = Math.round(x + offset_x * (float) animation.getAnimatedValue());
                                        FloatManager.getInstance().updateView();
                                        initStatus();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            final float offset_x = ((float) FloatingBallView.this.getMeasuredWidth()) * ALPHA_HIDE_PROP;
                            final int x = FloatManager.getInstance().layoutParams.x;
                            FloatingBallView.this.animate().alpha(ALPHA_HIDE_ALPHA).setInterpolator(halfHideInterpolator).setDuration(500).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    try {
                                        FloatManager.getInstance().layoutParams.x = Math.round(x - offset_x * (float) animation.getAnimatedValue());
                                        FloatManager.getInstance().updateView();
                                        initStatus();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    };


    public FloatingBallView(Activity activity, Boolean isHalfHide, Boolean isShowProgress) {
        this(activity, (AttributeSet) null);
        this.activity = activity;
        this.isHalfHide = isHalfHide;
        this.isShowProgress = isShowProgress;
    }

    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    public FloatingBallView(Activity activity, @Nullable AttributeSet attrs) {
        super(activity, attrs);
        init(activity);
    }


    private void init(Context context) {

        setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        imageView = new ImageView(context);
        imageView.setImageDrawable(ResUtil.drawableValue(getContext(), "ls_icon_receive_cash"));
        LayoutParams layoutParams = new LayoutParams(ProgressView.dip2px(getContext(), 46), ProgressView.dip2px(getContext(), 46));
        addView(imageView, layoutParams);

        progress = new ProgressView(context);
        progressParams = new LayoutParams(ProgressView.dip2px(getContext(), 46), ProgressView.dip2px(getContext(), 46));
        if (!isShowProgress) {
            progress.setVisibility(INVISIBLE);
        }
        addView(progress, progressParams);


    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        post(new Runnable() {
            @Override
            public void run() {

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
        FloatingBallView.this.setAlpha(1f);
        isHalfHidding = false;
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
        halfHide();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY();

                if (endAnimation != null && endAnimation.isRunning()) {
                    endAnimation.cancel();
                }

                FloatingBallView.this.animate().cancel();
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
                if (Math.abs(Math.abs(xDownInScreen) - Math.abs(event.getRawX())) < clickThreshold
                        && Math.abs(Math.abs(yDownInScreen) - Math.abs(event.getRawY())) < clickThreshold) {
                    listener.onClick();

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
     */
    private void moveEndView() {

        final int x = FloatManager.getInstance().layoutParams.x;
        final int y = FloatManager.getInstance().layoutParams.y;
        final int desX = isNearestLeft() ? 0 : frame.width() - getMeasuredWidth();
        final float desY = getLimitY(y);

        endAnimation = ValueAnimator.ofFloat(0, 1);
        endAnimation.setDuration(600);
        endAnimation.setInterpolator(endInterpolator);

        endAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                FloatManager.getInstance().layoutParams.x = Math.round(x - (x - desX) * animatedValue);
                FloatManager.getInstance().layoutParams.y = Math.round(y - (y - desY) * animatedValue);
                FloatManager.getInstance().updateView();
            }
        });
        endAnimation.start();
    }


    public void setProgress(int progress) {
        if (isShowProgress) {
            this.progress.setProgress(progress);
        }
    }

    public Boolean getProgressIsShowing() {
        return isShowProgress;
    }

    public int getProgress() {
        return this.progress.getProgress();
    }

    public boolean getIsHalfHiding() {
        return isHalfHidding;
    }


    /**
     * 隐藏进度条
     */
    public void hideProgress() {
        if (isShowProgress) {
            progress.setVisibility(INVISIBLE);
            isShowProgress = false;
        }
    }

    /**
     * 显示进度条
     */
    public void showProgress() {
        if (!isShowProgress) {
            progress.setVisibility(VISIBLE);
            isShowProgress = true;
        }
    }

    /**
     * 设置图标
     *
     * @param drawable
     */
    public void setDrawable(String drawable) {
        imageView.setImageDrawable(ResUtil.drawableValue(getContext(), drawable));
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
    public void halfShow() {
        if (handler != null) {
            if (!handler.hasMessages(MSG_HALF_SHOW)) {
                handler.sendEmptyMessage(MSG_HALF_SHOW);
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
        //触点在屏幕外 以防万一
        if (xInScreen == 0) {
            return FloatManager.getInstance().layoutParams.x < frame.centerX();
        }
        return xInScreen < frame.centerX();
    }

    /**
     * 获取不超出显示区域的合适高度
     *
     * @param y y
     * @return 合适高度
     */
    private float getLimitY(float y) {
        if (y < 0) {
            return 0;
        } else if (y > frame.height() - getMeasuredHeight()) {
            return frame.height() - getMeasuredHeight();
        } else {
            return y;
        }

    }

    public void destroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (endAnimation != null && endAnimation.isRunning()) {
            endAnimation.cancel();
        }
        this.clearAnimation();

        if (listener != null) {
            listener.onDestroy();
        }
    }

    /**
     *
     * 获取整个屏幕高度 *i
     *
     * @param
     * @return px
     */
    public int getScreenHeight() {
        if (activity != null) {
            return (int) (activity.getResources().getDisplayMetrics().heightPixels - progressParams.height) ;
        } else {
            return 1920 ;
        }
    }

    /**
     * 设定悬浮球X位置
     * 默认LEFT
     *
     * @param position
     * @return px
     */
    public int setXPosition(int position) {
        if (activity != null) {
            if (position == RIGHT) {
                return (int) (activity.getResources().getDisplayMetrics().widthPixels - progressParams.width);
            } else if (position == LEFT) {
                return 0;
            }
        }
        return 0;
    }


    public interface ActionListener {
        void onClick();

        void onDestroy();
    }

}
