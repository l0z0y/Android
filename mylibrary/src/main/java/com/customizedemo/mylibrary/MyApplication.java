package com.customizedemo.mylibrary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.customizedemo.mylibrary.api.RequestController;
import com.customizedemo.mylibrary.api.ResponseHandling;
import com.customizedemo.mylibrary.api.ResultCallback;
import com.customizedemo.mylibrary.carsh.CrashManager;
import com.customizedemo.mylibrary.picupload.ALiOSSUser;
import com.customizedemo.mylibrary.util.ConfigUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MyApplication extends Application {
    private static final String LIFETAG = "Lifecycle";

    private static MyApplication mContext;
    public static boolean firstCall = true;
    public static List<String> playlists = new ArrayList<String>();


    public static MyApplication getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RequestController.getInstance().getPlaylists();
        // 加载异常收集
        CrashManager.getInstance().init(this);
        // 初始化视频urls
        for (int i = 0; i < 5; i++) {
            initUrls();
        }

        mContext = this;
        initLifecycle();
    }

    private void initUrls() {
        RequestController.getInstance().getMp4(new ResultCallback() {
            @Override
            public void callback(String result) {
                try {
                    ResponseHandling.mp4ResponseHandling(result, new ResultCallback() {
                        @Override
                        public void callback(String result) {
                            if (result.startsWith(ResponseHandling.URL_ADD_SUCCESS)) {
                                Log.d(ResponseHandling.RESPONSE_HANDLER, ResponseHandling.URL_ADD_SUCCESS + " --> " + result.substring(result.indexOf("\n")));
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 初始化 监听生命周期状态
    private void initLifecycle() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                Log.d(LIFETAG, "onCreated: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                Log.d(LIFETAG, "onResumed: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                Log.d(LIFETAG, "onPaused: " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Log.d(LIFETAG, "onStopped: " + activity.getLocalClassName());

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.d(LIFETAG, "onDestroyed: " + activity.getLocalClassName());
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.d("TrimMemory", "onTrimMemory: " + level);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
