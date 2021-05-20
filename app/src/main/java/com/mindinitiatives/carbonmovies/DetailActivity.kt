package com.mindinitiatives.carbonmovies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

class DetailActivity : AppCompatActivity() {
    lateinit var nameOfMovie: TextView
    lateinit var plotSynopsis: TextView
    lateinit var userRating: TextView
    lateinit var releaseDate: TextView
    lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val toolbar = findViewById<Toolbar?>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initCollapsibleToolbar()
        imageView = findViewById(R.id.thumbnail_image_header)
        nameOfMovie = findViewById(R.id.title)
        plotSynopsis = findViewById(R.id.plotSynopsis)
        userRating = findViewById(R.id.userRating)
        releaseDate = findViewById(R.id.releaseDate)
        val intent = intent
        if (intent.hasExtra("original_title")) {
            val thumbnail = getIntent().extras?.getString("poster_path")
            val movieName = getIntent().extras?.getString("original_title")
            val synopsis = getIntent().extras?.getString("overview")
            val rating = getIntent().extras?.getString("vote_average")
            val dateOfRelease = getIntent().extras?.getString("release_date")
            Glide.with(this)
                .load(thumbnail)
                .placeholder(R.drawable.load)
                .into(imageView)
            nameOfMovie.text = movieName
            plotSynopsis.text = synopsis
            userRating.text = rating
            releaseDate.text = dateOfRelease
        } else {
            Toast.makeText(this, "No Available Data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCollapsibleToolbar() {
        val collapsingToolbarLayout = findViewById<CollapsingToolbarLayout?>(R.id.collapsing_toolbar)
        if (collapsingToolbarLayout != null) {
            collapsingToolbarLayout.title = " "
        }
        val appBarLayout = findViewById<AppBarLayout?>(R.id.appbar)
        appBarLayout?.setExpanded(true)
        appBarLayout?.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    if (appBarLayout != null) {
                        scrollRange = appBarLayout.totalScrollRange
                    }
                }
                if (scrollRange + verticalOffset == 0) {
                    if (collapsingToolbarLayout != null) {
                        collapsingToolbarLayout.title = getString(R.string.movie_details)
                    }
                    isShow = true
                } else if (isShow) {
                    if (collapsingToolbarLayout != null) {
                        collapsingToolbarLayout.title = " "
                    }
                    isShow = false
                }
            }
        })
    }
}