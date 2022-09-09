package com.customizedemo.mylibrary.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.customizedemo.mylibrary.R;


public class WebDialog extends Dialog {

    private String url;
    private Context context;
    private WebView webView;


    public WebDialog(@NonNull Context context) {
        this(context, R.style.Dialog);
        this.context = context;
    }

    public WebDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);

    }


    public static WebDialog create(Context context, Boolean cancelable, String url) {
        if (context == null) {
            return null;
        }

        WebDialog dialog = new WebDialog(context);
        dialog.setCancelable(cancelable);
        dialog.setUrl(url);
        dialog.init();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return dialog;
    }

    private void init() {
        if (getWindow() != null) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }
        initView();
        setCanceledOnTouchOutside(false);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
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
        webView.addJavascriptInterface(new JsInterface(new JsInterface.JsInterfaceListener() {
            @Override
            public void hide() {
                System.out.println("js调用hide()");
                dismiss();
            }

            @Override
            public void called(String methodName, String parameter) {
                switch (methodName) {
                    case "showTime":
                        Toast.makeText(context, parameter, Toast.LENGTH_SHORT).show();
                        break;
                    case "showInfo":
                        Log.d("Info", "Info:"+parameter);


                    default:
                        break;
                }
            }
        }), "JS");
        setContentView(webView);

    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void onBackPressed() {
        if (webView != null) {
            if (webView.canGoBack()) {
                webView.goBack();
                return;
            } else {
                webView.removeAllViews();
                webView.destroy();
            }
        }
        super.onBackPressed();
        dismiss();
    }
}
