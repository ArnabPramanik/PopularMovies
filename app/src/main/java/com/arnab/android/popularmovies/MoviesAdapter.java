package com.arnab.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arnab.android.popularmovies.model.Movie;

/**
 * Created by arnab on 11/12/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {
    private Movie[] movies;
    public MoviesAdapter(){

    }
    @Override
    public MoviesAdapter.MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movies_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.MoviesAdapterViewHolder moviesAdapterViewHolder, int position) {
        String singleMovie = movies[position].toString();
        moviesAdapterViewHolder.movieDataView.setText(singleMovie);

    }

    @Override
    public int getItemCount() {
        if(movies == null){
            return 0;
        }
        else{
            return movies.length;
        }
    }

    public void setMovieData(Movie[] movies){
        this.movies = movies;
        notifyDataSetChanged();
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder{
        public final TextView movieDataView;


        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            movieDataView = (TextView)  itemView.findViewById(R.id.tv_movie_data);

        }
    }
}
