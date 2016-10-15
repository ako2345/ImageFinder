package com.example.imagefinder.app;

import com.example.imagefinder.mvp.model.gson.GResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleCustomSearchApi {
    @GET("v1?searchType=image")
    Call<GResults> searchImages(@Query("q") String keyword, @Query("key") String apiKey, @Query("cx") String projectId);
}
