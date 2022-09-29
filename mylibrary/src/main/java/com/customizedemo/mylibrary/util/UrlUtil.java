package com.customizedemo.mylibrary.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class UrlUtil {

    //根据视频url获取缩略图
    public static Bitmap getNetVideoBitmap(String videoUrl) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    //根据url获取网络图片
    public static Drawable loadImageFromNetwork(String imageUrl) {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(
                    new URL(imageUrl).openStream(), "image.jpg");
        } catch (IOException e) {
            Log.d("loadImageFromNetwork", e.getMessage());
        }
        if (drawable == null) {
            Log.d("loadImageFromNetwork", "null drawable");
        } else {
            Log.d("loadImageFromNetwork", "drawable is ready");
        }

        return drawable;
    }

}
