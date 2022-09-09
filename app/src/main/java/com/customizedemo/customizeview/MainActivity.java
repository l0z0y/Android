package com.customizedemo.customizeview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.customizedemo.mylibrary.dialog.WebDialog;

public class MainActivity extends Activity {

    private String url1 = "file:///android_asset/webJs.html";
    private String url2 = "http://static.bliiblii.com/static-m/pro1/float-ball.html?from=active&v=20220520";
    private String url3 = "http://static.himengyou.com/static-m/pro1/float-ball.html?from=active&amp;v=20220520";
    private String url4 = "https://www.twle.cn/";

    private LinearLayout.LayoutParams params;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        ScrollView scrollView = new ScrollView(this);

        linearLayout = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);


        scrollView.addView(linearLayout,params);

        layout.addView(scrollView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setContentView(layout);

        initView();
    }

    private void initView() {
        initWebDialog();
        initMoveButton();
    }

    private void initMoveButton() {
        Intent intent = new Intent(MainActivity.this, FloatManagerActivity.class);
        Button button = new Button(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        button.setText("跳转悬浮球页面");
        button.setTextSize(18);
        linearLayout.addView(button,params);
    }


    private void initWebDialog() {
        Button button = new Button(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WebDialog webDialog = WebDialog.create(MainActivity.this, true, url1);
                webDialog.show();
            }
        });
        button.setText("弹出内嵌webView的Dialog");
        button.setTextSize(18);
        linearLayout.addView(button,params);
    }
}