package com.customizedemo.mylibrary.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.AnimRes;
import androidx.annotation.ArrayRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

/**
 *
 * 获取资源ID
 */
public class ResUtil {
    private static final String TAG = "ResUtil";

    /**
     * 获取资源
     *
     * @param context
     * @param packageName
     * @param resourcesName
     * @return
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
     * @param context
     * @param resourcesName
     * @return
     */
    @LayoutRes
    public static int layout(Context context, String resourcesName) {
        return getResIdByName(context, "layout", resourcesName);
    }

    /**
     * 获取 color ID
     *
     * @param context
     * @param resourcesName
     * @return
     */
    @ColorRes
    public static int color(Context context, String resourcesName) {
        return getResIdByName(context, "color", resourcesName);
    }

    /**
     * 获取 color 颜色
     *
     * @param context
     * @param resourcesName
     * @return
     */
    @ColorInt
    public static int colorValue(Context context, String resourcesName) {
        int colorId = color(context, resourcesName);
        return Build.VERSION.SDK_INT >= 23 ? context.getColor(colorId) : context.getResources().getColor(colorId);
    }

    /**
     * 获取 array ID
     *
     * @param context
     * @param resourcesName
     * @return
     */
    @ArrayRes
    public static int array(Context context, String resourcesName) {
        return getResIdByName(context, "array", resourcesName);
    }

    /**
     * 获取String资源ID
     *
     * @param context
     * @param resourcesName
     * @return
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
     * @param context
     * @param resourcesName
     * @return
     */
    @IdRes
    public static int view(Context context, String resourcesName) {
        return getResIdByName(context, "id", resourcesName);
    }

    /**
     * 获取drawable资源ID
     *
     * @param context
     * @param resourcesName
     * @return
     */
    @DrawableRes
    public static int drawable(Context context, String resourcesName) {
        return getResIdByName(context, "drawable", resourcesName);
    }

    /**
     * 获取drawable资源
     *
     * @param context
     * @param resourcesName
     * @return
     */
    public static Drawable drawableValue(Context context, String resourcesName) {
        int drawableId = drawable(context, resourcesName);
        return Build.VERSION.SDK_INT >= 23 ? context.getDrawable(drawableId) : context.getResources().getDrawable(drawableId);
    }

    /**
     * 获取anim资源
     *
     * @param context
     * @param resourcesName
     * @return
     */
    @AnimRes
    public static int anim(Context context, String resourcesName) {
        return getResIdByName(context, "anim", resourcesName);
    }

    /**
     * 获取dimen资源
     *
     * @param context
     * @param resourcesName
     * @return
     */
    @DimenRes
    public static int dimen(Context context, String resourcesName) {
        return getResIdByName(context, "dimen", resourcesName);
    }

    /**
     * 获取style资源
     *
     * @param context
     * @param resourcesName
     * @return
     */
    @StyleRes
    public static int style(Context context, String resourcesName) {
        return getResIdByName(context, "style", resourcesName);
    }
}
