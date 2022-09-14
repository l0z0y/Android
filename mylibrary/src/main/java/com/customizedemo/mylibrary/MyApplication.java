package com.customizedemo.mylibrary;

import android.app.Application;
import android.content.Context;

import com.customizedemo.mylibrary.carsh.CrashManager;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        // 加载异常收集
        CrashManager.getInstance().init(this);
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
}
