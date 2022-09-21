package com.customizedemo.customizeview;

import android.app.Activity;
import android.os.Bundle;

import com.customizedemo.mylibrary.recyclervideo.RecyclerVideoView;
import com.customizedemo.mylibrary.todayinhistory.RecyclerInfoView;

public class VideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerVideoView view = new RecyclerVideoView(this);
        setContentView(view);

    }
}