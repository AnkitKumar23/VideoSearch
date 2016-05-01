package com.github.ankitkumar23.videosearch.model;

import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.QueryMap;

/**
 * Interface for configuring retrofit for fetching
 * the images
 * Created by AnkitKumar23 on 10/23/2015.
 */
public interface QueryResultFetcher {
    @GET("/videos")
    Call<QueryResult> query(@QueryMap Map<String, String> options, @Header("Authorization") String authorization);
}
