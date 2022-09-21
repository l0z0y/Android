package com.customizedemo.mylibrary;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.customizedemo.mylibrary.api.NetworkRequest;
import com.customizedemo.mylibrary.api.ResultCallback;
import com.customizedemo.mylibrary.carsh.CrashManager;
import com.customizedemo.mylibrary.recyclervideo.RecyclerVideoView;
import com.customizedemo.mylibrary.util.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyApplication extends Application {
    private static final String LIFETAG = "Lifecycle";

    private static MyApplication mContext;


    public static MyApplication getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
        NetworkRequest.getInstance().getMp4(new ResultCallback() {
            @Override
            public void callback(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if ("-1".equals(jsonObject.optString("code", "-1"))) {
                        Toast.makeText(mContext, "加载api错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String url = jsonObject.optString("url");
                    String pic = jsonObject.optString("img");
                    if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(pic)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (RecyclerVideoView.URLs == null) {
                                    RecyclerVideoView.URLs = new ArrayList<>();
                                }
                                RecyclerVideoView.URLs.add(new RecyclerVideoView.UriPic(url, UrlUtil.loadImageFromNetwork(pic)));
                            }
                        }).start();

                    }
                } catch (JSONException e) {
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
