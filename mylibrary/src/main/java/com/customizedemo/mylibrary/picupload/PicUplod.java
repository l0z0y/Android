package com.customizedemo.mylibrary.picupload;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.util.Auth;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PicUplod {

    private static PicUplod picUplod;
    private static UploadManager uploadManager;

    public static PicUplod getIntence() {

        if (picUplod == null) {
            Configuration config = new Configuration.Builder()
                    .useHttps(true)               // 是否使用https上传域名
                        // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                    .build();
            uploadManager = new UploadManager(config); // UploadManager对象只需要创建一次重复使用
            picUplod = new PicUplod();
        }
        return picUplod;
    }

    public void upload(String filename) {
        String token = Auth.create("Ms1YMVYKNvE4F2Mqc9FjdsJDp6VkfVKK2MgKj4uy", "71klF0fj9iIjJjZR9tZgQRbilZB2eNLCf6WUQfjP").uploadToken("llzyqqq");
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String key = sdf.format(d);
        uploadManager.put(filename, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    Log.i("上传结果：", "Upload Success");
                } else {
                    Log.i("上传结果：", "Upload Fail");
                    //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                }
                Log.i("key：", key + "\ninfo：" + info + "\nres：" + response);

            }
        }, null);
    }

    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


}
