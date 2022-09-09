package com.customizedemo.mylibrary.floatingball;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


public class ProgressView extends View {
    private Paint progressPaint;
    private RectF rectf;
    private int progress;


    public ProgressView(Context context) {
        super(context);
        init(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private void init(Context context) {
        progressPaint = new Paint();
        // 描边不填充
        progressPaint.setStyle(Paint.Style.STROKE);
        // 设置圆角
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        // 设置抗锯齿
        progressPaint.setAntiAlias(true);
        // 设置抖动
        progressPaint.setDither(true);
        // 设置画笔颜色
        progressPaint.setColor(ResUtil.colorValue(context, "progress"));
        // 画笔 线条粗细
        progressPaint.setStrokeWidth(dip2px(getContext(), 3));
        //进度条初始值
        progress = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWide = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int viewHigh = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int mRectLength = (int) ((Math.min(viewWide, viewHigh)) - (progressPaint.getStrokeWidth()));
        int mRectL = getPaddingLeft() + (viewWide - mRectLength) / 2;
        int mRectT = getPaddingTop() + (viewHigh - mRectLength) / 2;
        rectf = new RectF(mRectL, mRectT, mRectL + mRectLength, mRectT + mRectLength);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rectf, 270, 360 * progress / 100f, false, progressPaint);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 设置进度条值
     * @param progress int
     */
    public void setProgress(int progress) {
        this.progress = progress;
        // 重新加载 onDraw()
        postInvalidate();
    }

    /**
     * 获取进度条值
     * @return int
     */
    public int getProgress() {
        return progress;
    }


}
