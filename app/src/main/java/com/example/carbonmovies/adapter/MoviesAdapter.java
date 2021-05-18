package com.example.carbonmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carbonmovies.R;
import com.example.carbonmovies.model.Movie;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter, MyViewHolder> {

    private Context mContext;
    private List<Movie> movieList;

    public MoviesAdapter(Context mContext, List<Movie> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    @NonNull
    @NotNull
    @Override
    public MoviesAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);

        return new MyViewHolder(view)
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull final MoviesAdapter.MyViewHolder holder, int position) {
        holder.title.setText(movieList.get(position).getOriginalTitle());
        String vote = Double.toString(movieList.get(position).getVoteAverage());
        holder.userRating.setText(vote);

        Glide.with(mContext)
                .load(movieList.get(position).getPosterPath())
                .placeholder(R.drawable.load)
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, userRating;
        public ImageView thumbnail;

        public MyViewHolder (View view) {
            super(view);
            title = view.findViewById(R.id.title);
            userRating = view.findViewById(R.id.userRating);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }
}
