package com.customizedemo.mylibrary.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class ConfigUtil {

    /**
     * 取assets下的配置参数
     *
     * @param context
     * @param file
     * @return
     */
    public static Properties readAssetsProperties(Context context, String file) {
        Properties p = null;
        try {
            InputStream in = context.getResources().getAssets().open(file);
            p = new Properties();
            p.load(new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)));
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

}
