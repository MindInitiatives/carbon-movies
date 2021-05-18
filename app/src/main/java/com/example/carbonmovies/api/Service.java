package com.example.carbonmovies.api;

import com.example.carbonmovies.model.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {

    @GET("movie/popular")
    Call <MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call <MoviesResponse> getTopMovies(@Query("api_key") String apiKey);
}
