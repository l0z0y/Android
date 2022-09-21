package com.customizedemo.mylibrary.api;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {

    private static RetrofitService instance;
    private Retrofit retrofit;
    public Api api;

    public static RetrofitService getInstance() {
        if (instance == null) {
            synchronized (RetrofitService.class) {
                if (instance == null) {
                    instance = new RetrofitService();
                }
            }
        }
        return instance;
    }


    public RetrofitService() {
        initService();
    }


    private void initService() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(2, TimeUnit.SECONDS).readTimeout(3, TimeUnit.SECONDS)
                // 添加http拦截器 打印日志
                .addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        try {
                            Log.i("OKHttp", message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        int maxRetry = 3;//最大重试次数 假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）
                        int retryNum = 0;
                        Request request = chain.request();
                        HttpUrl oldUrl = request.url();
                        HttpUrl newUrl = null;
                        Request.Builder builder = request.newBuilder();
                        String api = request.header("api");
                        if (!TextUtils.isEmpty(api)) {
                            builder.removeHeader("api");
                            if ("aliapi".equals(api)) {
                                newUrl = HttpUrl.parse(Api.BASE_JOKE);
                            } else if ("JOKE_HITOKOTO".equals(api)) {
                                newUrl = HttpUrl.parse(Api.JOKE_HITOKOTO);
                            }
                            // 重建新的HttpUrl，修改需要修改的url部分
                            HttpUrl newFullUrl = oldUrl
                                    .newBuilder()
                                    // 更换网络协议
                                    .scheme(newUrl.scheme())
                                    // 更换主机名
                                    .host(newUrl.host())
                                    // 更换端口
                                    .port(newUrl.port())
                                    .build();
                            // 重建这个request，通过builder.url(newFullUrl).build()；
                            // 然后返回一个response至此结束修改
                            Response response = chain.proceed(builder.url(newFullUrl).build());
                            while (!response.isSuccessful() && retryNum < maxRetry) {
                                retryNum++;
                                Log.i("requestError", "areRetry:"+retryNum);
                                response = chain.proceed(builder.url(newFullUrl).build());
                            }
                            return response;

                        }
                        return chain.proceed(request);
                    }
                })
                .build();

        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Api.BASE_JOKE).client(client).build();
        api = retrofit.create(Api.class);

    }



}
