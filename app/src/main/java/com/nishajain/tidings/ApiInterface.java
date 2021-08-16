package com.nishajain.tidings;

import com.nishajain.tidings.Models.HeadlinesModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("top-headlines")
    Call<HeadlinesModel> getHeadlines(
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );

    @GET("everything")
    Call<HeadlinesModel> getSpecificData(
            @Query("q") String query,
            @Query("apiKey") String apiKey
    );
}
