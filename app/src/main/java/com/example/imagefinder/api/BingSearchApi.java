package com.example.imagefinder.api;

import com.example.imagefinder.mvp.model.gson.BingSearchResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Interface for Bing Image Search API.
 */
public interface BingSearchApi {
    String BASE_URL = "https://api.cognitive.microsoft.com/bing/v5.0/images/";
    String API_KEY = "fde9e0613dbb463ca00a25f80263b00e";

    @Headers("Ocp-Apim-Subscription-Key:" + API_KEY)
    @GET("search?responseFilter=images")
    Call<BingSearchResults> searchImages(@Query("q") String keyword, @Query("count") int count, @Query("offset") int offset);
}
