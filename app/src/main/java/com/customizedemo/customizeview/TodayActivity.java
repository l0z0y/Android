package com.customizedemo.customizeview;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.customizedemo.mylibrary.todayinhistory.RecyclerInfoView;

public class TodayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerInfoView view = new RecyclerInfoView(this);
        setContentView(view);
    }
}