package com.customizedemo.mylibrary.api;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NetworkRequest {

    private static NetworkRequest instance;

    public static NetworkRequest getInstance() {

        if (instance == null) {
            synchronized (NetworkRequest.class) {
                if (instance == null) {
                    instance = new NetworkRequest();
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
        RetrofitService.getInstance().api.getMp4("http://api.botwl.cn/api/weishi").enqueue(new Callback<ResponseBody>() {
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
        RetrofitService.getInstance().api.getMp3("https://api.uomg.com/api/rand.music?sort=热歌榜&format=json").enqueue(new Callback<ResponseBody>() {
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

}
