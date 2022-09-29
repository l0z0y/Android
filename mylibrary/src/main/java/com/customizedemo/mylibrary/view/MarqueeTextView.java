package com.customizedemo.mylibrary.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;


public class MarqueeTextView extends AppCompatTextView {

    public MarqueeTextView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSingleLine(true);
        // 循环次数，-1无限循环
        setMarqueeRepeatLimit(-1);

    }

    /**
     * 判断是否处于焦点，处于焦点时才会有滚动效果
     * @return
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
