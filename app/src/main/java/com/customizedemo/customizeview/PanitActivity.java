package com.customizedemo.customizeview;


import android.app.Activity;
import android.os.Bundle;

import com.customizedemo.mylibrary.view.PaintView;

public class PanitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaintView paintView = new PaintView(this);
        setContentView(paintView);
    }
}