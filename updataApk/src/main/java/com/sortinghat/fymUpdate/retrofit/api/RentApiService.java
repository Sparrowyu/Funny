package com.sortinghat.fymUpdate.retrofit.api;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface RentApiService {
    //get请求
    @GET
    Call<String> get(@Url String url);

    //下载

    /**
     *
     */
    @Streaming
    @GET
    Call<ResponseBody> down(@Url String url);

    //post请求
    @POST
    @Multipart
    Call<String> post(@Url String url, @PartMap Map<String, RequestBody> args);

    //post请求
    @POST
    Call<String> post(@Url String url, @Body RequestBody body);
}
