package com.customizedemo.mylibrary.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Paint 绘制自定义View
 */
public class PaintView extends View {


    public PaintView(Context context) {
        super(context);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paintStroke = getPaint(Paint.Style.STROKE);
        Paint paintFill = getPaint(Paint.Style.FILL);
        // 绘制背景颜色 会覆盖前面的绘图
        canvas.drawColor(Color.CYAN);
        // 绘制线
        canvas.drawLine(0, 0, 200, 300, paintStroke);
        // 绘制圆形
        canvas.drawCircle(200, 300, 150, paintStroke);
        // 绘制矩阵
        canvas.drawRect(50, 500, 350, 800, paintStroke);
        //Rect 和RectF都是提供一个矩形局域。精度不一样 方法也不是完全一致
        Rect rect = new Rect(400, 500, 700, 800);
        canvas.drawRect(rect, paintStroke);
        // 绘制圆角矩阵 比绘制矩阵多了两个参数
        //rx：x方向上的圆角半径。
        //ry：y方向上的圆角半径。
        canvas.drawRoundRect(750, 500, 1050, 800, 50, 50, paintStroke);
        RectF rectf = new RectF(1100, 500, 1400, 800);
        canvas.drawRoundRect(rectf, 50, 50, paintStroke);
        //绘制圆弧
        //float startAngle：弧开始的角度，3点钟方向为 0度，顺时针
        //float sweepAngle：弧持续的角度
        //boolean useCenter:是否包含圆心
        // 不包含圆心
        canvas.drawArc(400, 150, 700, 450, 270, 270, false, paintStroke);
        // 包含圆心 填充
        canvas.drawArc(750, 150, 1050, 450, 270, 270, true, paintFill);
        // 不包含圆心 填充
        RectF rectf1 = new RectF(1100, 150, 1400, 450);
        canvas.drawArc(rectf1, 270, 270, false, paintFill);
        // 绘制文本
        canvas.drawText("Hello World",50,900,paintFill);
        // 根据路径绘制文本
        Path path = new Path();
        // 添加圆形轨迹
        path.addArc(new RectF(400,900,700,1100),220,180);
        //hOffset 文字相对于路径的水平偏移量，用于调整文字的位置
        //vOffset 文字相对于路径竖直偏移量，用于调整文字的位置
        canvas.drawTextOnPath("Hello World",path,0,0,paintFill);


    }

    /**
     * 获取画笔
     *
     * @param style 填充样式
     * @return paint
     */
    @NonNull
    private Paint getPaint(Paint.Style style) {
        Paint paint = new Paint();
        // 设置颜色
        paint.setColor(Color.RED);
        // 设置透明度 0是完全透明，255完全不透明 先调用setColor（），再调用setAlpha才会生效
        paint.setAlpha(255);
        // 抗锯齿
        paint.setAntiAlias(true);
        // 设置画笔宽度
        paint.setStrokeWidth(10);
        // 设置起始点及结束点样式 Cap.ROUND(圆形)、Cap.SQUARE(方形)、Paint.Cap.BUTT(无)
        paint.setStrokeCap(Paint.Cap.ROUND);

        // 设置画笔填充样式
        //Paint.Style.FILL 填充内部，会把闭合区域填充颜色
        //Paint.Style.FILL_AND_STROKE 填充内部和描边
        //Paint.Style.STROKE 仅描边，仅仅绘制边界
        paint.setStyle(style);
        // 设置绘制path连接点的样式
        //Join.MITER（结合处为锐角）、
        //Join.Round(结合处为圆弧)、
        //Join.BEVEL(结合处为直线)
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setTextSize(50);
        return paint;
    }
}
