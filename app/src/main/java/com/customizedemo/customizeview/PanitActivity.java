package com.customizedemo.customizeview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.customizedemo.mylibrary.view.PaintView;

public class PanitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaintView paintView = new PaintView(this);
        setContentView(paintView);
    }
}