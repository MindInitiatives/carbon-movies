package com.mindinitiatives.carbonmovies

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mindinitiatives.carbonmovies.adapter.MoviesAdapter
import com.mindinitiatives.carbonmovies.api.Service
import com.mindinitiatives.carbonmovies.common.Common
import com.mindinitiatives.carbonmovies.model.Movie
import com.mindinitiatives.carbonmovies.model.MoviesResponse
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.ArrayList

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MoviesAdapter
    private lateinit var movieList: List<Movie>
    var progressDialog: ProgressDialog? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        swipeRefreshLayout = findViewById(R.id.main_content)
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark)
        swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            initView()
            Toast.makeText(this@MainActivity, "Movies refreshed", Toast.LENGTH_SHORT).show()
        })
    }

    fun getActivity(): Activity? {
        var context: Context? = this
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = (context as ContextWrapper?)?.baseContext
        }
        return null
    }

    private fun initView() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Fetching Movies...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()

        //initialising Recyclerview
        recyclerView = findViewById(R.id.recycler_view)

        //initialising Adapter
        movieList = ArrayList()
        adapter = MoviesAdapter(this, movieList)

        //responsiveness
        if (getActivity()?.resources!!.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.layoutManager = GridLayoutManager(this, 2)
        } else {
            recyclerView.layoutManager = GridLayoutManager(this, 4)
        }
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
        checkSortOrder()
    }

    private fun loadPopularMovies() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()) {
                Toast.makeText(applicationContext, "Kindly obtain API key from themoviedb.org", Toast.LENGTH_SHORT).show()
                progressDialog?.dismiss()
                return
            }

            // Initialize Request Logs
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            // Initialize Caching
            val cache = Cache(cacheDir, cacheSize.toLong())
            val client: OkHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(object : Interceptor {
                    @Throws(IOException::class)
                    override fun intercept(chain: Interceptor.Chain): Response {
                        var newRequest: Request = chain.request()

                        //Check if Connected to Internet
                        if (!Common.isConnectedToInternet(baseContext)) {
                            val maxStale = 60 * 60 * 24 * 28 //tolerate 4 weeks stale
                            newRequest = newRequest
                                .newBuilder()
                                .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                                .addHeader("Authorization", BuildConfig.THE_MOVIE_DB_API_TOKEN)
                                .build()
                        }
                        return chain.proceed(newRequest)
                    }
                }).build()
            val builder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
            val retrofit = builder.build()
            val apiService = retrofit.create(Service::class.java)
            val call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_KEY)
            call?.enqueue(object : Callback<MoviesResponse?> {
                override fun onResponse(call: Call<MoviesResponse?>?, response: retrofit2.Response<MoviesResponse?>?) {
                    Log.i("api response", response.toString())
                    val movies = response?.body()?.getResults()
                    recyclerView.adapter = MoviesAdapter(applicationContext, movies as List<Movie>)
                    recyclerView.smoothScrollToPosition(0)
                    if (swipeRefreshLayout.isRefreshing) {
                        swipeRefreshLayout.isRefreshing = false
                    }
                    progressDialog?.dismiss()
                }

                override fun onFailure(call: Call<MoviesResponse?>?, t: Throwable?) {
                    t?.message?.let { Log.d("Error", it) }
                    Toast.makeText(this@MainActivity, "Error! Could not Fetch Data", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            e.message?.let { Log.d("Error", it) }
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadTopRatedMovies() {
        try {
            if (BuildConfig.THE_MOVIE_DB_API_KEY.isEmpty()) {
                Toast.makeText(applicationContext, "Kindly obtain API key from themoviedb.org", Toast.LENGTH_SHORT).show()
                progressDialog?.dismiss()
                return
            }

            // Initialize Request Logs
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            // Initialize Caching
            val cache = Cache(cacheDir, cacheSize.toLong())
            val client: OkHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(Interceptor { chain ->
                    var newRequest: Request = chain.request()

                    //Check if Connected to Internet
                    if (!Common.isConnectedToInternet(baseContext)) {
                        val maxStale = 60 * 60 * 24 * 28 //tolerate 4 weeks stale
                        newRequest = newRequest
                            .newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                            .addHeader("Authorization", BuildConfig.THE_MOVIE_DB_API_TOKEN)
                            .build()
                    }
                    chain.proceed(newRequest)
                }).build()
            val builder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
            val retrofit = builder.build()
            val apiService = retrofit.create(Service::class.java)
            val call = apiService.getTopMovies(BuildConfig.THE_MOVIE_DB_API_KEY)
            call?.enqueue(object : Callback<MoviesResponse?> {
                override fun onResponse(call: Call<MoviesResponse?>?, response: retrofit2.Response<MoviesResponse?>?) {
                    Log.i("api response", response.toString())
                    val movies = response?.body()?.getResults()
                    recyclerView.adapter = MoviesAdapter(applicationContext, movies as List<Movie>)
                    recyclerView.smoothScrollToPosition(0)
                    if (swipeRefreshLayout.isRefreshing) {
                        swipeRefreshLayout.isRefreshing = false
                    }
                    progressDialog?.dismiss()
                }

                override fun onFailure(call: Call<MoviesResponse?>?, t: Throwable?) {
                    if (t != null) {
                        t.message?.let { Log.d("Error", it) }
                    }
                    Toast.makeText(this@MainActivity, "Error! Could not Fetch Data", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            e.message?.let { Log.d("Error", it) }
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(LOG_TAG, "Prefernces updated")
        checkSortOrder()
    }

    private fun checkSortOrder() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val sortOrder = sharedPreferences.getString(
            this.getString(R.string.pref_sort_order_key),
            this.getString(R.string.pref_most_popular)
        )
        if (sortOrder == this.getString(R.string.pref_most_popular)) {
            Log.d(LOG_TAG, "Sorting by most popular")
            loadPopularMovies()
        } else {
            Log.d(LOG_TAG, "Sorting by vote average")
            loadTopRatedMovies()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        if (movieList.isEmpty()) {
            checkSortOrder()
        } else {
            //Do Nothing
        }
    }

    companion object {
        val LOG_TAG: String = MoviesAdapter::class.java.name
        val BASE_URL: String = "https://api.themoviedb.org/3/"
        const val cacheSize = 10 * 1024 * 1024
    }
}