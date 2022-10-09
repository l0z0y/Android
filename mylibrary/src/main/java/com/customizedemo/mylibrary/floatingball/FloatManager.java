package com.customizedemo.mylibrary.floatingball;


import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.customizedemo.mylibrary.util.ResUtil;
import com.customizedemo.mylibrary.util.ScreenUtil;


public class FloatManager {


    private FloatingBallView floatView;
    private WindowManager windowManager;
    private final Handler mainHandle = new Handler(Looper.getMainLooper());
    public WindowManager.LayoutParams layoutParams;
    // 防止多次display()时isAttachedToWindow还为false重复添加View
    private Boolean isAdded = false;

    //ture 是展示红包icon
    private boolean isRedPacket = true;

    private static final class FloatManagerHolder {
        static final FloatManager floatManager = new FloatManager();
    }

    public static FloatManager getInstance() {
        return FloatManagerHolder.floatManager;
    }


    public void create(Activity activity, FloatingBallView.ActionListener listener) {
        if (windowManager == null) {
            windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        }
        if (this.floatView == null) {
            floatView = new FloatingBallView(activity);
        }
        floatView.setListener(listener);
    }

    /**
     * 获取参数
     *
     * @return LayoutParams
     */
    private WindowManager.LayoutParams getLayoutParams(Activity activity) {
        if (layoutParams == null) {
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
            layoutParams.x = activity.getResources().getDisplayMetrics().widthPixels - ScreenUtil.dip2px(activity, FloatingBallView.WIDTH_DP);
            layoutParams.y = (activity.getResources().getDisplayMetrics().heightPixels - ScreenUtil.dip2px(activity, FloatingBallView.HEIGHT_DP)) / 2;
            layoutParams.token = activity.getWindow().getDecorView().getWindowToken();
            if (Build.VERSION.SDK_INT >= 28) {
                layoutParams.layoutInDisplayCutoutMode = activity.getWindow().getAttributes().layoutInDisplayCutoutMode;
            }
        }
        return layoutParams;
    }


    /**
     * 获取floatView
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
        if (floatView != null && isAttachedToWindow(floatView)) {
            mainHandle.post(new Runnable() {
                @Override
                public void run() {
                    isAdded = false;
                    windowManager.removeView(floatView);
                }
            });
        }
    }

    /**
     * 显示悬浮球
     */
    public void display() {
        if (isDestroyed()) {
            mainHandle.post(new Runnable() {
                @Override
                public void run() {
                    floatView.initStatus();
                    floatView.initLocation();
                    floatView.halfHide();
                    if (!isAttachedToWindow(floatView) || isAdded) {
                        isAdded = true;
                        windowManager.addView(floatView, getLayoutParams(floatView.activity));
                    }
                }
            });
        }
    }

    public static boolean isAttachedToWindow(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= 19) {
            return view.isAttachedToWindow();
        } else {
            return view.getWindowToken() != null;
        }
    }

    public boolean isShowing() {
        return floatView != null && isAttachedToWindow(floatView);
    }

    /**
     * 设置进度条值
     *
     * @param value [0-100]
     */
    public void setProgress(final int value) {
        if (floatView != null && !floatView.activity.isFinishing() && !floatView.activity.isDestroyed()) {
            mainHandle.post(new Runnable() {
                @Override
                public void run() {
                    if (value <= 0) {
                        floatView.setProgress(0);
                        if (isRedPacket)
                            floatView.setIconDrawable(ResUtil.drawable(floatView.getContext(), "ls_icon_receive_cash"));
                    } else if (value < 100) {
                        floatView.setProgress(value);
                        if (isRedPacket)
                            floatView.setIconDrawable(ResUtil.drawable(floatView.getContext(), "ls_icon_receive_cash"));
                    } else {
                        floatView.setProgress(100);
                        if (isRedPacket)
                            floatView.setIconDrawable(ResUtil.drawable(floatView.getContext(), "ls_icon_waiting_receive"));
                        floatView.popup();
                    }
                }
            });
        }
    }

    /**
     * 设置是否红包icon（是红包icon在改变进度时会自动改icon）
     *
     * @param redPacket ture:是
     */
    public void setRedPacket(boolean redPacket) {
        isRedPacket = redPacket;
    }

    /**
     * 设置图标
     *
     * @param url 图标链接
     */
    public void setIcon(final String url) {
        if (isDestroyed()) {
            mainHandle.post(new Runnable() {
                @Override
                public void run() {
                    floatView.setIconDrawable(url);
                }
            });
        }
    }

    /**
     * 设置图标
     *
     * @param resourceId 资源id
     */
    public void setIcon(final int resourceId) {
        if (isDestroyed()) {
            mainHandle.post(new Runnable() {
                @Override
                public void run() {
                    floatView.setIconDrawable(resourceId);
                }
            });
        }
    }

    /**
     * 获取进度条值
     */
    public int getProgress() {
        if (isDestroyed()) {
            return floatView.getProgress();
        }
        return 0;
    }

    /**
     * 刷新悬浮球
     */
    public void updateView() {
        if (isShowing()) {
            windowManager.updateViewLayout(floatView, layoutParams);
        }
    }

    public void halfHide() {
        if (isShowing()) {
            floatView.halfHide();
        }
    }

    private boolean isDestroyed() {
        return floatView != null && !floatView.activity.isFinishing() && !floatView.activity.isDestroyed();
    }

    public void destroy() {
        if (floatView != null) {
            floatView.destroy();
        }
        if (windowManager != null) {
            if (isShowing()) {
                try {
                    isAdded = false;
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