package com.mindinitiatives.carbonmovies.api

import com.mindinitiatives.carbonmovies.model.MoviesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Service {
    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String?): Call<MoviesResponse?>?
    @GET("movie/top_rated")
    fun getTopMovies(@Query("api_key") apiKey: String?): Call<MoviesResponse?>?
}