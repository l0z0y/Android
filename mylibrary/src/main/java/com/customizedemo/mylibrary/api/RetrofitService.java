package com.customizedemo.mylibrary.api;

import android.util.Log;

import java.io.IOException;
import java.util.List;
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
    private final String TAG = "RetrofitService";

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
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).retryOnConnectionFailure(true)
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
                }).setLevel(HttpLoggingInterceptor.Level.BASIC))
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //最大重试次数 假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）
                        int maxRetry = 3;
                        //当前重连次数
                        int retryNum = 0;
                        Request request = chain.request();
                        HttpUrl oldUrl = request.url();
                        HttpUrl newUrl = null;
                        Request.Builder builder = request.newBuilder();
                        builder.addHeader("Content-Type", "text/html;charset=UTF-8");
                        List<String> api = request.headers("api");
                        // 根据header 更换url
                        if (api.size() > 0) {
                            builder.removeHeader("api");
                            if ("aliapi".equals(api.get(0))) {
                                newUrl = HttpUrl.parse(Api.BASE_JOKE);
                            } else if ("JOKE_HITOKOTO".equals(api.get(0))) {
                                newUrl = HttpUrl.parse(Api.JOKE_HITOKOTO);
                            }
                            // 重建新的HttpUrl，修改需要修改的url部分
                            HttpUrl newFullUrl = oldUrl
                                    .newBuilder().removeAllQueryParameters("maxResult").removePathSegment(0)
                                    // 更换网络协议
                                    .scheme(newUrl.scheme())
                                    // 更换主机名
                                    .host(newUrl.host())
                                    .addPathSegment(newUrl.pathSegments().get(0))
                                    // 更换端口
                                    .port(newUrl.port())
                                    .build();
                            // 重建这个request，通过builder.url(newFullUrl).build()；
                            request = builder.url(newFullUrl).build();
                        }
                        Response response = chain.proceed(request);
                        //返回结果失败则重新请求3次
                        while (!response.isSuccessful() && retryNum < maxRetry) {
                            retryNum++;
                            Log.i("NetWorkRequest", request.url() + "请求失败\n--响应码:" + response.code() + "-- 重新请求第:" + retryNum + "次");
                            response = chain.proceed(request);
                        }
                        return response;
                    }
                })
                .build();

        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Api.BASE_JOKE).client(client).build();
        api = retrofit.create(Api.class);

    }


}
