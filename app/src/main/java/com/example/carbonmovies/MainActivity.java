package com.example.carbonmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.carbonmovies.adapter.MoviesAdapter;
import com.example.carbonmovies.api.Service;
import com.example.carbonmovies.common.common;
import com.example.carbonmovies.model.Movie;
import com.example.carbonmovies.model.MoviesResponse;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private List<Movie> movieList;
    ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static  final String LOG_TAG = MoviesAdapter.class.getName();
    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final int cacheSize = 10 * 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        swipeRefreshLayout = findViewById(R.id.main_content);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initView();
                Toast.makeText(MainActivity.this, "Movies refreshed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Activity getActivity() {
        Context context = this;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Movies...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //initialising Recyclerview
        recyclerView = findViewById(R.id.recycler_view);

        //initialising Adapter
        movieList = new ArrayList<>();
        adapter = new MoviesAdapter(this, movieList);

        //responsiveness
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        loadJSON();
    }

    private void loadJSON() {

        try {
            if (BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()) {

                Toast.makeText(getApplicationContext(), "Kindly obtain API key from themoviedb.org", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }

            // Initialize Request Logs
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Initialize Caching
            Cache cache = new Cache(getCacheDir(), cacheSize);
            OkHttpClient client = new OkHttpClient.Builder()
                    .cache(cache)
                    .addInterceptor(new Interceptor() {
                        @NotNull
                        @Override
                        public okhttp3.Response intercept(@NotNull Chain chain) throws IOException {
                            Request newRequest  = chain.request();

                            //Check if Connected to Internet
                            if (!(common.isConnectedToInternet(getBaseContext()))) {
                                int maxStale = 60 * 60 * 24 * 28; //tolerate 4 weeks stale
                                newRequest = newRequest
                                        .newBuilder()
                                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                        .addHeader("Authorization", BuildConfig.THE_MOVIE_DB_API_TOKEN)
                                        .build();
                            }
                            return chain.proceed(newRequest);
                        }
                    }).build();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.build();
            Service apiService =retrofit.create(Service.class);
            Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_KEY);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    Log.i("api response", response.toString());
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error! Could not Fetch Data", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}