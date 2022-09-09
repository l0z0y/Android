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
    /** 窗口管理器是否添加虚浮窗 */
    private Boolean isAdd = false;
    private final Handler mainHandle = new Handler(Looper.getMainLooper());
    private Context context;
    private WindowManager.LayoutParams layoutParams;

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
     * @param context
     * @param floatView FloatingBallView
     */
    public void create(Context context, View floatView) {
        if (windowManager == null) {
            initWindowManager(context);
        }
        if (this.floatView != null) {
            return;
        }
        if (floatView == null) {
            throw new NullPointerException("floatView is null");
        }
        this.floatView = (FloatingBallView) floatView;
    }

    /**
     * 获取WindowManager参数
     *
     * @return LayoutParams
     */

    public WindowManager.LayoutParams getLayoutParams() {
        if (layoutParams == null) {
            // 设置LayoutParam
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
            //设置flags 不然悬浮窗出来后整个屏幕都无法获取焦点，
            layoutParams.gravity = Gravity.END | Gravity.CENTER;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.x = 0;
            layoutParams.y = 0;
            if (Build.VERSION.SDK_INT >= 28) {
                layoutParams.layoutInDisplayCutoutMode = ((Activity) context).getWindow().getAttributes().layoutInDisplayCutoutMode;
            }
        }
        return layoutParams;
    }

    /**
     * 初始化WindowManager
     *
     * @param context
     */
    public void initWindowManager(Context context) {
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 获取悬浮窗View
     *
     * @return  floatView
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
        if (floatView != null && isShowing) {
            mainHandle.post(new Runnable() {
                @Override
                public void run() {
                    FloatingBallView floatingBallView = (FloatingBallView) floatView;
                    floatingBallView.showProgress();
                }
            });

        }
    }

    /**
     * 隐藏悬浮球进度条
     */
    public void hideProgress() {
        if (floatView != null && isShowing) {
            mainHandle.post(new Runnable() {
                @Override
                public void run() {
                    FloatingBallView floatingBallView = (FloatingBallView) floatView;
                    floatingBallView.hideProgress();
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
        if (floatView != null && isShowing) {
            windowManager.removeViewImmediate(floatView);
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
            if (isShowing) {
                try {
                    windowManager.removeViewImmediate(floatView);
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


