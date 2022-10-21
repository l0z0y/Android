package com.customizedemo.mylibrary.api;

import static com.customizedemo.mylibrary.view.MusicView.songInfos;

import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;

import com.customizedemo.mylibrary.recyclervideo.RecyclerVideoView;
import com.customizedemo.mylibrary.util.UrlUtil;
import com.customizedemo.mylibrary.view.MusicView;

import org.json.JSONObject;

import java.util.ArrayList;

public class ResponseHandling {

    public static final String RESPONSE_HANDLER = "ResponseHandling";
    public static final String URL_ADD_SUCCESS = "Success";

    public static void mp4ResponseHandling(String result, ResultCallback callback) {
        if (result.startsWith("随身助手API微视短视频")) {
            String pic = result.substring(result.indexOf("图片") + 3, result.indexOf("\n播放"));
            String url = result.substring(result.indexOf("播放") + 5, result.lastIndexOf("\n━━━━━━━━━"));
            if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(pic)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (RecyclerVideoView.URLs == null) {
                            RecyclerVideoView.URLs = new ArrayList<>();
                        }
                        RecyclerVideoView.URLs.add(new RecyclerVideoView.UriPic(url, UrlUtil.loadImageFromNetwork(pic)));
                        callback.callback(URL_ADD_SUCCESS+"\n"+url);
                    }
                }).start();

            }
        } else if (result.startsWith("随身助手API快手短视频")) {
            String url = result.substring(result.indexOf("\n播放链接") + 6, result.lastIndexOf("\n━━━━━━━━━"));
            if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (RecyclerVideoView.URLs == null) {
                            RecyclerVideoView.URLs = new ArrayList<>();
                        }
                        RecyclerVideoView.URLs.add(new RecyclerVideoView.UriPic(url, new BitmapDrawable(UrlUtil.getNetVideoBitmap(url))));
                        callback.callback(URL_ADD_SUCCESS+"\n"+url);
                    }
                }).start();

            }
        }
    }

    public static void mp3ResponseHandling(String result, ResultCallback callback) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            JSONObject data = jsonObject.optJSONObject("data");
            if (data != null) {
                String url = data.optString("url");
                String name = data.optString("name");
                String picurl = data.optString("picurl");
                String artistsname = data.optString("artistsname");
                if (songInfos == null) {
                    songInfos = new ArrayList<>();
                }
                songInfos.add(new MusicView.SongInfo(name, artistsname, url, picurl));
                Log.i(RESPONSE_HANDLER, URL_ADD_SUCCESS + " --> "+ "歌曲: " + name + " —— " + artistsname);
                callback.callback(URL_ADD_SUCCESS );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
