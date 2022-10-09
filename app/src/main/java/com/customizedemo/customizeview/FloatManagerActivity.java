package com.customizedemo.customizeview;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.customizedemo.mylibrary.floatingball.FloatManager;
import com.customizedemo.mylibrary.floatingball.FloatingBallView;
import com.customizedemo.mylibrary.util.ResUtil;

public class FloatManagerActivity extends Activity {

    private LinearLayout.LayoutParams params;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setGravity(Gravity.CENTER);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        ScrollView scrollView = new ScrollView(this);
        linearLayout = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        scrollView.addView(linearLayout, params);

        layout.addView(scrollView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        setContentView(layout);

        initView();


    }

    private void initView() {

        Button button = new Button(this);
        button.setText("设置进度为70");
        button.setTextSize(18);
        button.setTextColor(Color.BLACK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatManager.getInstance().setProgress(70);
            }
        });
        linearLayout.addView(button, params);

        Button button2 = new Button(this);
        button2.setText("设置进度为18500");
        button2.setTextSize(18);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatManager.getInstance().setProgress(18500);
            }
        });
        linearLayout.addView(button2, params);

        Button button3 = new Button(this);
        button3.setText("增加进度");
        button3.setTextSize(18);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FloatManager.getInstance().addProgress(10);
            }
        });
        linearLayout.addView(button3, params);

        Button button4 = new Button(this);
        button4.setText("减少进度");
        button4.setTextSize(18);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FloatManager.getInstance().reduceProgress(5);
            }
        });
        linearLayout.addView(button4, params);


        Button button5 = new Button(this);
        button5.setText("显示悬浮球");
        button5.setTextSize(18);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatManager.getInstance().create(FloatManagerActivity.this,  new FloatingBallView.ActionListener() {
                    @Override
                    public void onClick() {
                        Toast.makeText(FloatManagerActivity.this, "点击了悬浮窗", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onDestroy() {
                        Toast.makeText(FloatManagerActivity.this, "悬浮窗销毁", Toast.LENGTH_SHORT).show();

                    }
                });
                FloatManager.getInstance().display();
            }
        });
        linearLayout.addView(button5, params);

        Button button6 = new Button(this);
        button6.setText("半隐藏");
        button6.setTextSize(18);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatManager.getInstance().halfHide();
            }
        });
        linearLayout.addView(button6, params);

        Button button7 = new Button(this);
        button7.setText("显示悬浮球");
        button7.setTextSize(18);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 }
        });
        linearLayout.addView(button7, params);
    }


    @Override
    protected void onDestroy() {
        FloatManager.getInstance().destroy();
        super.onDestroy();
    }
}