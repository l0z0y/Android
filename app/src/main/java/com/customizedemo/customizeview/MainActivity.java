package com.customizedemo.customizeview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.customizedemo.mylibrary.dialog.WebDialog;

public class MainActivity extends Activity {

    String url1 = "file:///android_asset/webJs.html";
    String url2 = "http://static.bliiblii.com/static-m/pro1/float-ball.html?from=active&v=20220520";
    String url3 = "http://static.himengyou.com/static-m/pro1/float-ball.html?from=active&amp;v=20220520";
    String url4 = "https://www.twle.cn/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        addContentView(button, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }
}