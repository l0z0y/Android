package com.customizedemo.customizeview;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.customizedemo.mylibrary.api.NetworkRequest;
import com.customizedemo.mylibrary.api.ResultCallback;
import com.customizedemo.mylibrary.dialog.WebDialog;
import com.customizedemo.mylibrary.util.DecodeUtil;
import com.customizedemo.mylibrary.view.MusicView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    private String url1 = "file:///android_asset/webJs.html";
    private String url2 = "http://static.bliiblii.com/static-m/pro1/float-ball.html?from=active&v=20220520";
    private String url3 = "http://static.himengyou.com/static-m/pro1/float-ball.html?from=active&amp;v=20220520";
    private String url4 = "https://www.twle.cn/";

    private LinearLayout.LayoutParams params;
    private LinearLayout linearLayout;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setGravity(Gravity.CENTER);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ScrollView scrollView = new ScrollView(this);

        linearLayout = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        scrollView.addView(linearLayout, params);

        layout.addView(scrollView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setContentView(layout);

        initView();
    }

    private void initView() {
        initWebDialog();
        initButton();

    }

    private void play() {
        NetworkRequest.getInstance().getMp3(new ResultCallback() {
            @Override
            public void callback(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject data = jsonObject.optJSONObject("data");
                    if (data != null) {
                        String url = data.optString("url");
                        if (!TextUtils.isEmpty(url)) {
                            mediaPlayer.setDataSource(url);
                            mediaPlayer.prepareAsync();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initButton() {
        Intent floatManagerIntent = new Intent(MainActivity.this, FloatManagerActivity.class);
        Button moveFloatManager = new Button(this);
        moveFloatManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(floatManagerIntent);
            }
        });
        moveFloatManager.setText("跳转悬浮球页面");
        moveFloatManager.setTextSize(18);
        linearLayout.addView(moveFloatManager, params);

        Intent panitIntent = new Intent(MainActivity.this, PanitActivity.class);
        Button movePanit = new Button(this);
        movePanit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(panitIntent);
            }
        });
        movePanit.setText("跳转PaintView");
        movePanit.setAllCaps(false);
        movePanit.setTextSize(18);
        linearLayout.addView(movePanit, params);

        Button networkRequest = new Button(this);
        networkRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkRequest.getInstance().getJoke(new ResultCallback() {
                    @Override
                    public void callback(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String text = jsonObject.optString("text");
                            System.out.println(DecodeUtil.unicodeToCN(text));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        networkRequest.setText("网络请求");
        networkRequest.setAllCaps(false);
        networkRequest.setTextSize(18);
        linearLayout.addView(networkRequest, params);

        Button throwException = new Button(this);
        throwException.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new NullPointerException();
            }
        });
        throwException.setText("抛异常");
        throwException.setTextSize(18);
        linearLayout.addView(throwException, params);


        Intent videoIntent = new Intent(MainActivity.this, VideoActivity.class);
        Button moveVideo = new Button(this);
        moveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(videoIntent);
            }
        });
        moveVideo.setText("视频");
        moveVideo.setAllCaps(false);
        moveVideo.setTextSize(18);
        linearLayout.addView(moveVideo, params);

        Intent todayintent = new Intent(MainActivity.this, TodayActivity.class);
        Button moveToday = new Button(this);
        moveToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(todayintent);
            }
        });
        moveToday.setText("历史上的今天");
        moveToday.setAllCaps(false);
        moveToday.setTextSize(18);
        linearLayout.addView(moveToday, params);


        MusicView musicView = new MusicView(this);
        addContentView(musicView,params);
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
        linearLayout.addView(button, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}