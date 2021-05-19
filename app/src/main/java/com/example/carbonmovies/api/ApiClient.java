package com.example.carbonmovies.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static Retrofit retrofit = null;
    public static final String token = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzNTc4NWE2YjNjZjQwODIxMGJlN2ZiYmI2ZjdkMWMzNSIsInN1YiI6IjYwYTJkMWIyYjBiYTdlMDA3OTQyMjlkMyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._-y24DafvRqrUhDr5LGC_74Xn_G0QMXJSKErqP5_6ws";

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @NotNull
                @Override
                public Response intercept(@NotNull Chain chain) throws IOException {
                    Request newRequest  = chain.request().newBuilder()
                            .addHeader("Authorization", token)
                            .build();
                    return chain.proceed(newRequest);
                }
            }).build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;

    }

}
