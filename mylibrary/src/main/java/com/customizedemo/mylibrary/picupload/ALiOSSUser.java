package com.customizedemo.mylibrary.picupload;

import android.content.Context;

import com.customizedemo.mylibrary.util.ConfigUtil;

import java.io.File;
import java.util.Properties;

public class ALiOSSUser {
    private String appkey;
    private String appId;
    private String bucketName;
    private static ALiOSSUser aLiOSSUser;

    public ALiOSSUser() {
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }


    public static ALiOSSUser get(Context context) {
        if (aLiOSSUser == null) {
            Properties properties = ConfigUtil.readAssetsProperties(context, "config" + File.separator + "config.properties");
            ALiOSSUser user = new ALiOSSUser();
            user.setAppkey(properties.getProperty("appkey"));
            user.setAppId(properties.getProperty("appid"));
            user.setBucketName(properties.getProperty("bucketname"));
            aLiOSSUser = user;
        }
        return aLiOSSUser;
    }
}
