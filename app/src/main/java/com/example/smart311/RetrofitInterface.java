package com.example.smart311;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/addSpeech")
    Call<Void> addSpeech(@Body HashMap<String, String> map);

    @GET("/getSpeech")
    Call<List<SpeechReserved>> getSpeech();

    @POST("/signup")
    Call<Void> executeSignup (@Body HashMap<String, String> map);

}