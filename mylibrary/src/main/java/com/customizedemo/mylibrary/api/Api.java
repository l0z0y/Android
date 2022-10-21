package com.customizedemo.mylibrary.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Api {

    String BASE_JOKE = "https://ali-joke.showapi.com";
    String JOKE_HITOKOTO = "https://api.wrdan.com/hitokoto";

    @Headers({"Authorization:APPCODE 6377839b302745a4a61ba52a1257cc61","api:JOKE_HITOKOTO"})
    @GET("/textJoke")
    Call<ResponseBody> getTextJoke(@Query("maxResult") int i);

    @Headers({"Authorization:APPCODE 6377839b302745a4a61ba52a1257cc61"})
    @GET()
    Call<ResponseBody> getHitokoto(@Url String url);

    @GET()
    Call<ResponseBody> getMp4(@Url String url);

    @GET()
    Call<ResponseBody> todayInHistory(@Url String url);

    @GET()
    Call<ResponseBody> getMp3(@Url String url);

    @GET()
    Call<ResponseBody> getSongFromId(@Url String url);

    @GET()
    Call<ResponseBody> getSongFromPlaylist(@Url String url);

    @GET()
    Call<ResponseBody> getPlaylists(@Url String url);

    @GET()
    Call<ResponseBody> addPicUrl(@Url String url);

    @GET()
    Call<ResponseBody> getAllPicUrl(@Url String url);

}
