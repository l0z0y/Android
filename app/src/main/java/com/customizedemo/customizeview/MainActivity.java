package com.customizedemo.customizeview;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.alibaba.ha.adapter.service.tlog.TLogService;
import com.alibaba.sdk.android.logger.ILog;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.customizedemo.mylibrary.api.RequestController;
import com.customizedemo.mylibrary.dialog.WebDialog;
import com.customizedemo.mylibrary.qiniu.ALiUploadManager;
import com.customizedemo.mylibrary.qiniu.PicUplod;
import com.customizedemo.mylibrary.view.MusicView;

public class MainActivity extends Activity {

    private static final int IMAGE_REQUEST_CODE = 222;
    private String url1 = "file:///android_asset/webJs.html";
    private String url2 = "http://static.bliiblii.com/static-m/pro1/float-ball.html?from=active&v=20220520";
    private String url3 = "http://static.himengyou.com/static-m/pro1/float-ball.html?from=active&amp;v=20220520";
    private String url4 = "https://www.twle.cn/";

    private LinearLayout.LayoutParams params;
    private LinearLayout linearLayout;
    private MusicView musicView;


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
//                RequestController.getInstance().getJoke(new ResultCallback() {
//                    @Override
//                    public void callback(String result) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(result);
//                            String text = jsonObject.optString("text");
//                            System.out.println(DecodeUtil.unicodeToCN(text));
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//                RequestController.getInstance().addPicUrl("{\"url\":\"789\"}");
                RequestController.getInstance().getAllPicUrl();
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
//                try {
//                    throw new NullPointerException();
//                } catch (Exception e) {
//                    Toast.makeText(MainActivity.this, "捕获到了异常", Toast.LENGTH_SHORT).show();
//                }
                //上报日志
                TLogService.positiveUploadTlog("COMMIT");
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


        musicView = new MusicView(this);
        addContentView(musicView, params);
        Button upload = new Button(this);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ALiUploadManager.getInstance().init(MainActivity.this);
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1);
                }
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });
        upload.setText("选择图片上传");
        upload.setAllCaps(false);
        upload.setTextSize(18);
        linearLayout.addView(upload, params);

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
        musicView.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE) {
            // 从相册返回的数据
            Log.e(this.getClass().getName(), "Result:" + data.toString());
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                String path = PicUplod.getPath(this, uri);
                Log.e(this.getClass().getName(), "Uri:" + path);
//                PicUplod.getIntence().upload(path);
                ALiUploadManager.getInstance().uploadFile(path, new ALiUploadManager.ALiCallBack() {
                    @Override
                    public void onSuccess(PutObjectRequest request, PutObjectResult result, String url) {
                        Log.i("ALiUploadManger","上传阿里云成功:" + url);
                    }

                    @Override
                    public void onError(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                        Log.i("ALiUploadManger","上传阿里云失败clientExcepion:" + clientExcepion.getMessage() + ",serviceException:" + serviceException);

                    }

                    @Override
                    public void process(long currentSize, long totalSize) {
                        Log.i("ALiUploadManger","上传中:" + (currentSize * 100) / totalSize + "%");
                    }
                });
            }
        }
    }
}