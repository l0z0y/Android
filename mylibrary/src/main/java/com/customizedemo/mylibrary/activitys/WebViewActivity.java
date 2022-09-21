package com.customizedemo.mylibrary.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.net.PortUnreachableException;

public class WebViewActivity extends Activity {
    public WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getStringExtra("url");
        initView(this, url);
        setContentView(webView);
    }

    private void initView(Context context, String url) {

        webView = new WebView(context);
        webView.setBackgroundColor(0);
        WebSettings settings = webView.getSettings();

        // 允许访问 Content:// URL
        settings.setAllowContentAccess(true);
        // 允许访问 file://
        settings.setAllowFileAccess(true);
        // 允许使用JS交互
        settings.setJavaScriptEnabled(true);

        // 启用DomStroage
        settings.setDomStorageEnabled(true);
        // 支持本地存储
        settings.setDatabaseEnabled(true);

        //设置缓存策略 - 无缓存，每次更新
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // 设置最大缓存容量
        settings.setAppCacheMaxSize(1024 * 1024 * 16);
        String appCachePath = context.getApplicationContext().getCacheDir().getAbsolutePath();
        //设置缓存地址
        settings.setAppCachePath(appCachePath);

        settings.setAppCacheEnabled(true);

        // 自适应屏幕-- 不允许缩放 适应屏幕好像都没用
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        webView.loadUrl(url);

    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}