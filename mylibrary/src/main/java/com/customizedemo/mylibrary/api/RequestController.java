package com.customizedemo.mylibrary.api;

import android.util.Log;

import com.customizedemo.mylibrary.MyApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RequestController {

    private static RequestController instance;

    public static RequestController getInstance() {

        if (instance == null) {
            synchronized (RequestController.class) {
                if (instance == null) {
                    instance = new RequestController();
                }
            }
        }
        return instance;
    }


    public void getJoke(ResultCallback resultCallback) {

        RetrofitService.getInstance().api.getTextJoke(1).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    resultCallback.callback(response.body() != null ? response.body().string() : null);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public void getHitokoto(ResultCallback resultCallback) {
        RetrofitService.getInstance().api.getHitokoto("https://api.wrdan.com/hitokoto").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    resultCallback.callback(response.body() != null ? response.body().string() : null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }

    public void getMp4(ResultCallback resultCallback) {
        String[] api = {"vs.php", "kuaishou.php"};
        RetrofitService.getInstance().api.getMp4("http://api.wuxixindong.cn/api/" + api[new Random().nextInt(2)]).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        resultCallback.callback(body.string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    public void todayInHistory(ResultCallback resultCallback) {
        RetrofitService.getInstance().api.todayInHistory("https://api.asilu.com/today").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        resultCallback.callback(response.body().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void getMp3(ResultCallback resultCallback) {
        final String[] types = {"热歌榜", "新歌榜", "飙升榜", "抖音榜", "电音榜"};
        Random random = new Random();
        int num = random.nextInt(5);
        RetrofitService.getInstance().api.getMp3("http://api.uomg.com/api/rand.music?sort=" + types[num] + "&format=json").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        resultCallback.callback(response.body().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }


    public void getSongFromPlaylist(ResultCallback resultCallback) {
        if (MyApplication.playlists != null && MyApplication.playlists.size() > 0) {
            String s = MyApplication.playlists.get(new Random().nextInt(4));
            Log.d("getSongFromPlaylist: ", s);
            RetrofitService.getInstance().api.getSongFromPlaylist("https://lzy-musics.eu.org/playlist/detail?id=" + s).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONObject playlists = jsonObject.optJSONObject("playlist");
                        JSONArray tracks = playlists.optJSONArray("tracks");
                        if (tracks != null && tracks.length() > 0) {
                            JSONObject track = tracks.optJSONObject(new Random().nextInt(tracks.length() - 1));
                            String name = track.optString("name");
                            String trackId = track.optString("id");
                            JSONArray ar = track.optJSONArray("ar");
                            JSONObject arJsonObject = ar.optJSONObject(0);
                            String artistsName = arJsonObject.optString("name");
                            JSONObject al = track.optJSONObject("al");
                            String picUrl = al.optString("picUrl", "");
                            getSongUrlFromId(trackId, name, artistsName, picUrl, resultCallback);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    private void getSongUrlFromId(String trackId, String name, String artistsName, String picUrl, ResultCallback resultCallback) {
        RetrofitService.getInstance().api.getSongFromId("https://lzy-musics.eu.org/song/url?id=" + trackId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray data = jsonObject.optJSONArray("data");
                    JSONObject trackInfo = data.optJSONObject(0);
                    String url = trackInfo.optString("url", "");
                    JSONObject d = new JSONObject();
                    JSONObject b = new JSONObject();
                    b.put("url", url);
                    b.put("name", name);
                    b.put("picurl", picUrl);
                    b.put("artistsname", artistsName);
                    d.put("data", b);

                    ResponseHandling.mp3ResponseHandling("{\"data\":{\"url\":\"" + url + "\",\"name\":\"" + name + "\",\"picurl\":\"" + picUrl + "\",\"artistsname\":\"" + artistsName + "\"}}", resultCallback);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void getPlaylists() {
        RetrofitService.getInstance().api.getPlaylists("https://lzy-musics.eu.org/top/playlist?limit=4&order=hot").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray playlist = jsonObject.optJSONArray("playlists");
                    if (playlist != null && playlist.length() > 0) {
                        for (int i = 0; i < playlist.length(); i++) {
                            JSONObject data = playlist.optJSONObject(i);
                            String id = data.optString("id", "0");
                            String name = data.optString("name");
                            Log.i(ResponseHandling.RESPONSE_HANDLER, ResponseHandling.URL_ADD_SUCCESS + " --> 添加歌单：" + name + " —— " + "id: " + id);
                            MyApplication.playlists.add(id);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void addPicUrl(String data) {
        RetrofitService.getInstance().api.addPicUrl("https://hn216.api.yesapi.cn/?s=App.Table.Create&return_data=0&model_name=pic&data=" + data + "&app_key=7C365D9721D66F47FC31741903341E36&sign=A3B5FD9488BDB2F6893C033D0E3EBE11").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.i("addPicUrl", "onResponse: " + response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void getAllPicUrl() {
        RetrofitService.getInstance().api.getAllPicUrl("" +
                "http://hn216.api.yesapi.cn/?model_name=pic&where=%5B%5B%22id%22%2C%22%3E%22%2C0%5D%5D&order=%5B%22id+DESC%22%5D&perpage=100&page=1&select=id%2Curl&s=App.Table.FreeQuery&app_key=7C365D9721D66F47FC31741903341E36&sign=BFA0564EFE80FDE3DE86443623E15BEB").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Log.i("addPicUrl", "onResponse: " + response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}
