package com.customizedemo.mylibrary.dialog;

import android.webkit.JavascriptInterface;

public class JsInterface {
    JsInterfaceListener listener;

    public JsInterface(JsInterfaceListener listener) {
        this.listener = listener;
    }

    @JavascriptInterface
    public void hiddenContentView() {
        listener.hide();
    }

    @JavascriptInterface
    public void showTime(String time) {
        // 获取当前方法名
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        listener.called(methodName, time);
    }

    @JavascriptInterface
    public void showInfo(String info) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        listener.called(methodName,info);
    }

    interface JsInterfaceListener {
        void hide();

        void called(String methodName, String parameter);
    }
}
