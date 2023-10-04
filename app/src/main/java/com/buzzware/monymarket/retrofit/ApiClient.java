package com.buzzware.monymarket.retrofit;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiClient {

    // payment api
    @Headers("Accept: application/json")
    @GET("/latest?apikey=77464443dfea43cdbdffd5b09d8a0454")
    Call<String> getCurrencyRate();

    // payment api
    @Headers("Accept: application/json")
    @POST("moneymarketnotification")
    Call<String> setNotification(@Body RequestBody requestBody);

}
