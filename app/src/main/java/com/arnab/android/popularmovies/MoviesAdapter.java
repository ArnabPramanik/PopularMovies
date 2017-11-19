package com.arnab.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import com.arnab.android.popularmovies.model.Movie;
import com.arnab.android.popularmovies.utils.NetworkUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by arnab on 11/12/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    public ArrayList<Movie> movies;
    private final MovieAdapterOnClickHandler mClickHandler;
    private Context mContext;
    boolean mBigwidth;
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MoviesAdapter(MovieAdapterOnClickHandler clickHandler,Context context){
        mClickHandler = clickHandler;
        this.mContext = context;
        movies = new ArrayList<Movie>();

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
        Movie singleMovie = movies.get(position);
        moviesAdapterViewHolder.movieTitleView.setText(singleMovie.getTitle());
        if(singleMovie.getVote_average() == 10.0){
            moviesAdapterViewHolder.movieRatingView.setText("10");
        }else {
            moviesAdapterViewHolder.movieRatingView.setText(String.valueOf(singleMovie.getVote_average()));
        }
        if(mBigwidth == false) {
            //moviesAdapterViewHolder.thumbnail.getLayoutParams().height = 450;
            Picasso.with(mContext).load(NetworkUtils.IMAGE_BASE_URL + NetworkUtils.IMAGE_WIDTH_s + singleMovie.getPoster_path()).into(moviesAdapterViewHolder.thumbnail);
        }
        else{
            //moviesAdapterViewHolder.thumbnail.getLayoutParams().height = 450;
            Picasso.with(mContext).load(NetworkUtils.IMAGE_BASE_URL + NetworkUtils.IMAGE_WIDTH_s + singleMovie.getPoster_path()).into(moviesAdapterViewHolder.thumbnail);
        }

    }

    @Override
    public int getItemCount() {
        if(movies == null){
            return 0;
        }
        else{
            return movies.size();
        }
    }

    public void setMovieData(ArrayList<Movie> movies, boolean bigwidth){
        if(movies == null){Log.wtf("IS","NULL");}
        if(this.movies == null){Log.wtf("IS ALSO","NULL ALSO");};
        this.movies.addAll(movies);
        mBigwidth = bigwidth;
        notifyDataSetChanged();
    }
    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView movieTitleView;
        public final ImageView thumbnail;
        public final TextView movieRatingView;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            movieTitleView = (TextView)  itemView.findViewById(R.id.tv_movie_title_main);
            thumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail_main);
            movieRatingView = (TextView) itemView.findViewById(R.id.tv_movie_rating_main);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            int adapterPosition = getAdapterPosition();
            Movie singleMovie = movies.get(adapterPosition - 1); //weird bug here (-1) required
            mClickHandler.onClick(singleMovie);
        }
    }

}
