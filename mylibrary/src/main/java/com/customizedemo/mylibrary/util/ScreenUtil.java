package com.customizedemo.mylibrary.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.util.HashMap;

/**
 *
 * 屏幕工具
 */
public class ScreenUtil {

    /**
     * 获取当前窗口的宽（可能会不等于屏幕宽度的）
     *
     * @param context context
     * @return px
     */
    public static int getWindowWidth(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取当前窗口的高度（可能会不等于屏幕高度的）
     *
     * @param context context
     * @return px
     */
    public static int getWindowHeight(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 返回当前屏幕是否为竖屏。
     *
     * @param context context
     * @return 当且仅当当前屏幕为竖屏时返回true, 否则返回false。
     */
    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 获取手机分辨率高
     *
     * @param activity activity
     * @return 高度px
     */
    public static int getPhoneHeight(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
            return outMetrics.heightPixels;
        } else {
            return activity.getWindowManager().getMaximumWindowMetrics().getBounds().height();
        }
    }

    /**
     * 获取手机分辨率宽
     *
     * @param activity activity
     * @return 宽度px
     */
    public static int getPhoneWidth(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
            return outMetrics.widthPixels;
        } else {
            return activity.getWindowManager().getMaximumWindowMetrics().getBounds().width();
        }
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics());
    }


    /**
     * 计算VIEW的高度和宽度
     *
     * @param context
     * @param widthWeightSum  宽度的总等分
     * @param widthWeightNum  离手机左右的长度的占比
     * @param heightWeightSum 高度的总等分
     * @param heightWeightNum 离手机上下的长度的占比
     * @param type            公告框的类型
     * @return
     */
    public static HashMap<String, Object> getViewLength(Context context, int widthWeightSum, int widthWeightNum,
                                                        int heightWeightSum, int heightWeightNum, String type) {

        HashMap<String, Object> viewHashMap = new HashMap<String, Object>();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;//当前屏幕的宽度
        int height = outMetrics.heightPixels;//当前屏幕的高度
        int widthMeanLength = 0;//宽度的每等份的平均值
        int heightMeanLength = 0;//高度的每等份的平均值
        int widthLenght = 0;//界面的宽度
        int heightLenght = 0;//界面的高度


        if (height < width) {//横屏
            widthMeanLength = width / widthWeightSum;
            heightMeanLength = height / heightWeightSum;
            widthLenght = widthMeanLength * (widthWeightSum - widthWeightNum);
            heightLenght = heightMeanLength * (heightWeightSum - heightWeightNum);

        } else {//竖屏
            widthMeanLength = width / 10;
            heightMeanLength = height / 10;
            if ("NoticeDialog".equals(type)) {
                widthLenght = widthMeanLength * (10 - 1);
                heightLenght = heightMeanLength * (10 - 6);
            } else if ("ExitDialog".equals(type)) {
                widthLenght = widthMeanLength * (10 - 1);
                heightLenght = (height / 12) * (10 - 6);
            } else if ("PayWebDialog".equals(type)) {
                widthLenght = widthMeanLength * (10 - 2);
                heightLenght = heightMeanLength * (10 - 3);
            } else {
//            	heightMeanLength = height/22;
                widthLenght = widthMeanLength * (10 - 2);
                heightLenght = height;
//                heightLenght = heightMeanLength * (22 - 8);
            }


        }

        viewHashMap.put("width", widthLenght);
        viewHashMap.put("height", heightLenght);

        return viewHashMap;
    }
}
