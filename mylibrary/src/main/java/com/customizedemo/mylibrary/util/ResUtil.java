package com.customizedemo.mylibrary.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import android.support.annotation.AnimRes;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;

/**
 *
 * 获取资源ID
 */
public class ResUtil {
    private static final String TAG = "ResUtil";

    /**
     * 获取资源
     *
     * @param context Context
     * @param packageName String
     * @param resourcesName String
     * @return int
     */
    public static int getResIdByName(Context context, String packageName, String resourcesName) {
        Resources resources = context.getResources();
        int id = resources.getIdentifier(resourcesName, packageName, context.getPackageName());
        if (id == 0) {
            Log.e(TAG, "资源文件读取不到！resourcesName:" + resourcesName);
        }
        return id;
    }

    /**
     * 获取布局ID
     *
     * @param context Context
     * @param resourcesName String
     * @return int
     */
    @LayoutRes
    public static int layout(Context context, String resourcesName) {
        return getResIdByName(context, "layout", resourcesName);
    }

    /**
     * 获取 color ID
     *
     * @param context Context
     * @param resourcesName String
     * @return int
     */
    @ColorRes
    public static int color(Context context, String resourcesName) {
        return getResIdByName(context, "color", resourcesName);
    }

    /**
     * 获取 color 颜色
     *
     * @param context Context
     * @param resourcesName String
     * @return int
     */
    @ColorInt
    public static int colorValue(Context context, String resourcesName) {
        int colorId = color(context, resourcesName);
        return Build.VERSION.SDK_INT >= 23 ? context.getColor(colorId) : context.getResources().getColor(colorId);
    }

    /**
     * 获取 array ID
     *
     * @param context Context
     * @param resourcesName String
     * @return int
     */
    @ArrayRes
    public static int array(Context context, String resourcesName) {
        return getResIdByName(context, "array", resourcesName);
    }

    /**
     * 获取String资源ID
     *
     * @param context Context
     * @param resourcesName String
     * @return int
     */
    @StringRes
    public static int string(Context context, String resourcesName) {
        return getResIdByName(context, "string", resourcesName);
    }

    public static String stringValue(Context context, String resourcesName) {
        String res = "";
        try {
            res = context.getResources().getString(getResIdByName(context, "string", resourcesName));
        } catch (Exception e) {
            Log.e(TAG, "resourcesName:" + resourcesName);
        }
        return res;
    }

    /**
     * 获取view id资源
     *
     * @param context Context
     * @param resourcesName String
     * @return int
     */
    @IdRes
    public static int view(Context context, String resourcesName) {
        return getResIdByName(context, "id", resourcesName);
    }

    /**
     * 获取drawable资源ID
     *
     * @param context Context
     * @param resourcesName String
     * @return int
     */
    @DrawableRes
    public static int drawable(Context context, String resourcesName) {
        return getResIdByName(context, "drawable", resourcesName);
    }

    /**
     * 获取drawable资源
     *
     * @param context Context
     * @param resourcesName String
     * @return Drawable
     */
    public static Drawable drawableValue(Context context, String resourcesName) {
        int drawableId = drawable(context, resourcesName);
        return Build.VERSION.SDK_INT >= 23 ? context.getDrawable(drawableId) : context.getResources().getDrawable(drawableId);
    }

    /**
     * 获取anim资源
     *
     * @param context Context
     * @param resourcesName String
     * @return int
     */
    @AnimRes
    public static int anim(Context context, String resourcesName) {
        return getResIdByName(context, "anim", resourcesName);
    }

    /**
     * 获取dimen资源
     *
     * @param context Context
     * @param resourcesName String
     * @return int
     */
    @DimenRes
    public static int dimen(Context context, String resourcesName) {
        return getResIdByName(context, "dimen", resourcesName);
    }

    /**
     * 获取style资源
     *
     * @param context Context
     * @param resourcesName String
     * @return int
     */
    @StyleRes
    public static int style(Context context, String resourcesName) {
        return getResIdByName(context, "style", resourcesName);
    }
}
