package com.customizedemo.mylibrary.todayinhistory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.customizedemo.mylibrary.activitys.WebViewActivity;
import com.customizedemo.mylibrary.util.ResUtil;
import com.customizedemo.mylibrary.util.ScreenUtil;
import com.customizedemo.mylibrary.view.MarqueeTextView;

public class InfoItemView extends LinearLayout {

    private final Context context;
    public TextView year;
    public TextView title;
    public TextView link;

    public InfoItemView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = ScreenUtil.dip2px(context, 10);
        params.leftMargin = ScreenUtil.dip2px(context, 8);
        params.rightMargin = ScreenUtil.dip2px(context, 8);
        setLayoutParams(params);
        setOrientation(VERTICAL);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setBackground(ResUtil.drawableValue(context, "background_shadow"));
        year = new TextView(context);
        year.setTextSize(20);
        year.setTypeface(Typeface.MONOSPACE);
        year.setGravity(Gravity.CENTER);
        year.setTextColor(Color.BLACK);

        title = new MarqueeTextView(context);
        title.setTextSize(18);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);

        LayoutParams params1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params1.leftMargin = ScreenUtil.dip2px(context, 10);
        linearLayout.addView(year, params1);
        linearLayout.addView(title, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(linearLayout, new LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(context, 50)));


        link = new TextView(context);
        link.setTextSize(15);
        link.setTextColor(Color.parseColor("#1BACBD"));
        link.setSingleLine(true);
        link.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        link.setTypeface(Typeface.MONOSPACE);
        link.setGravity(Gravity.CENTER);
        link.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                if (link.getText() != null && link.getText().length() > 0) {
                    intent.putExtra("url", link.getText());
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "打开网址失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        LinearLayout linearLayout1 = new LinearLayout(context);
        linearLayout1.addView(link, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        addView(linearLayout1, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));


    }
}
