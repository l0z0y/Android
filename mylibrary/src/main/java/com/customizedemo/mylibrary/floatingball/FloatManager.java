package com.customizedemo.mylibrary.floatingball;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;


public class FloatManager {


    private FloatingBallView floatView;
    private WindowManager windowManager;
    /**
     * 窗口管理器是否添加虚浮窗
     */
    private Boolean isAdd = false;
    private final Handler mainHandle = new Handler(Looper.getMainLooper());
    private Activity activity;
    public WindowManager.LayoutParams layoutParams;

    /**
     * 悬浮球显示状态 true--显示  false--未显示
     */
    private Boolean isShowing = false;

    private static FloatManager floatManager;


    /**
     * 获取FloatManager实例
     *
     * @return FloatManager
     */
    public static FloatManager getInstance() {
        if (floatManager == null) {
            synchronized (FloatManager.class) {
                if (floatManager == null) {
                    floatManager = new FloatManager();
                }
            }
        }
        return floatManager;
    }

    /**
     * 创建悬浮窗
     *
     *
     */

    public void create(Activity activity, Boolean isHalfHide, FloatingBallView.ActionListener listener) {
        if (windowManager == null) {
            windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        }
        if (this.floatView != null) {
            return;
        }
        floatView = new FloatingBallView(activity, isHalfHide, true);

        if (listener != null) {
            floatView.setListener(listener);
        }

        this.activity = activity;

    }

    /**
     * 获取WindowManager参数
     *
     * @return LayoutParams
     */

    public WindowManager.LayoutParams getLayoutParams() {

        // 设置LayoutParam
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        //设置flags 不然悬浮窗出来后整个屏幕都无法获取焦点，
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 设置初始位置
        layoutParams.x = floatView.setXPosition(FloatingBallView.RIGHT);
        layoutParams.y = floatView.getScreenHeight()/2;
        if (Build.VERSION.SDK_INT >= 28) {
            layoutParams.layoutInDisplayCutoutMode = activity.getWindow().getAttributes().layoutInDisplayCutoutMode;
        }

        return layoutParams;
    }


    /**
     * 获取悬浮窗View
     *
     * @return floatView
     */
    public View getFloatView() {
        if (floatView != null) {
            return floatView;
        }
        return null;
    }

    /**
     * 隐藏悬浮球
     */
    public void hide() {
        if (floatView != null && isShowing) {
            mainHandle.post(new Runnable() {
                @Override
                public void run() {
                    floatView.setVisibility(View.INVISIBLE);
                    isShowing = false;
                }
            });
        }
    }

    /**
     * 显示悬浮球
     */
    public void display() {
        if (floatView != null && !isShowing) {
            if (!isAdd) {
                WindowManager.LayoutParams layoutParams = getLayoutParams();
                windowManager.addView(floatView, layoutParams);
                isShowing = true;
                isAdd = true;
                halfHide();
            }
            mainHandle.post(new Runnable() {
                @Override
                public void run() {
                    floatView.setVisibility(View.VISIBLE);
                    isShowing = true;
                    halfHide();
                }
            });
        }
    }

    /**
     * 显示悬浮球进度条
     */
    public void displayProgress() {
        if (floatView != null && isAdd) {
            mainHandle.post(new Runnable() {
                @Override
                public void run() {
                    floatView.showProgress();
                }
            });

        }
    }

    /**
     * 隐藏悬浮球进度条
     */

    public void hideProgress() {
        if (floatView != null && isAdd) {
            mainHandle.post(new Runnable() {
                @Override
                public void run() {
                    floatView.hideProgress();
                }
            });

        }
    }

    /**
     * 设置进度条值
     *
     * @param value = 100 切换icon value>100 递归
     */
    public void setProgress(int value) {
        if (floatView != null && isShowing) {
            if (floatView.getProgressIsShowing()) {
                if (value >= 0 && value < 100) {
                    floatView.setProgress(value);
                    floatView.setDrawable("ls_icon_receive_cash");

                } else if (value >= 100) {
                    double i = value / 100.00;
                    double floor = Math.floor(i);
                    value = (int) ((i - floor) * 100);
                    if (value == 0) {
                        floatView.setProgress(100);
                        floatView.setDrawable("ls_icon_waiting_receive");
                        halfShow();
                        return;
                    }
                    setProgress(value);
                }
            }
        }
    }

    /**
     * 获取进度条值
     */
    public int getProgress() {
        if (floatView != null && isShowing) {
            FloatingBallView floatingBallView = (FloatingBallView) floatView;
            if (floatingBallView.getProgressIsShowing()) {
                return floatingBallView.getProgress();
            }
        }
        return 0;
    }

    /**
     * 进度条增加一定数值
     *
     * @param value int
     */
    public void addProgress(int value) {
        value = getProgress() + value;
        setProgress(value);
    }

    /**
     * 进度条减少一定数值
     */
    public void reduceProgress(int value) {
        value = getProgress() - value;
        setProgress(value);
    }

    /**
     * 关闭悬浮球
     */
    public void close() {
        if (floatView != null && isAdd) {
            windowManager.removeViewImmediate(floatView);
            isShowing = false;
            isAdd = false;
        }
    }

    /**
     * 刷新悬浮球
     */
    public void updateView() {
        if (floatView != null && isShowing) {
            windowManager.updateViewLayout(floatView, layoutParams);
        }
    }

    /**
     * 正常显示悬浮球且不自动半隐藏
     */
    public void halfShow() {
        if (floatView != null && isShowing) {
            floatView.halfShow();
        }
    }

    /**
     * 半隐藏悬浮球
     */
    public void halfHide() {
        if (floatView != null && isShowing) {
            floatView.halfHide();
        }

    }

    /**
     * 获取悬浮球显示状态
     *
     * @return
     */
    public Boolean getShowing() {
        return isShowing;
    }

    /**
     * 销毁操作
     */
    public void destroy() {
        if (floatView != null) {
            floatView.destroy();
        }
        if (windowManager != null) {
            if (isAdd) {
                try {
                    windowManager.removeViewImmediate(floatView);
                    isShowing = false;
                    isAdd = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                floatView = null;
            }
            layoutParams = null;
            windowManager = null;
        }
    }


}


