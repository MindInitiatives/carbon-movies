package com.mindinitiatives.carbonmovies.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mindinitiatives.carbonmovies.DetailActivity
import com.mindinitiatives.carbonmovies.R
import com.mindinitiatives.carbonmovies.model.Movie

class MoviesAdapter(private val mContext: Context?, private val movieList: List<Movie>) : RecyclerView.Adapter<MoviesAdapter.MyViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = movieList.get(position).getOriginalTitle()
        val vote = movieList.get(position).getVoteAverage()?.toString()
        holder.userRating.text = vote
        if (mContext != null) {
            holder.thumbnail.let {
                Glide.with(mContext)
                    .load(movieList[position].getPosterPath())
                    .placeholder(R.drawable.load)
                    .into(it)
            }
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    inner class MyViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        lateinit var title: TextView
        lateinit var userRating: TextView
        lateinit var thumbnail: ImageView

        init {
            if (view != null) {
                title = view.findViewById(R.id.title)
                userRating = view.findViewById(R.id.user_rating)
                thumbnail = view.findViewById(R.id.thumbnail)
            }

            view?.setOnClickListener(View.OnClickListener { v ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedItem = movieList.get(position)
                    val intent = Intent(mContext, DetailActivity::class.java)
                    intent.putExtra("original_title", movieList.get(position).getOriginalTitle())
                    intent.putExtra("poster_path", movieList.get(position).getPosterPath())
                    intent.putExtra("overview", movieList.get(position).getOverview())
                    intent.putExtra("vote_average", movieList.get(position).getVoteAverage()?.let { java.lang.Double.toString(it) })
                    intent.putExtra("release_date", movieList.get(position).getReleaseDate())
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    mContext?.startActivity(intent)
                    Toast.makeText(v.context, "You Clicked " + clickedItem.getOriginalTitle(), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}