package com.customizedemo.mylibrary.recyclervideo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class ItemView extends RelativeLayout {
    private final Context context;
    public VideoView videoView;

    public ItemView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {

        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        videoView = new VideoView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        videoView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                } else {
                    videoView.start();
                }
                return false;
            }
        });
        addView(videoView, params);
    }
}
