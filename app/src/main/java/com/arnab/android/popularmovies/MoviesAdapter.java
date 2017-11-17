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
import com.squareup.picasso.Picasso;

/**
 * Created by arnab on 11/12/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {
    private Movie[] movies;
    private final MovieAdapterOnClickHandler mClickHandler;
    private Context mContext;
    boolean mBigwidth;
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MoviesAdapter(MovieAdapterOnClickHandler clickHandler,Context context){
        mClickHandler = clickHandler;
        this.mContext = context;
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
        Movie singleMovie = movies[position];
        moviesAdapterViewHolder.movieTitleView.setText(singleMovie.getTitle());
        moviesAdapterViewHolder.movieRatingView.setText(String.valueOf(singleMovie.getVote_average()));
        if(mBigwidth == false) {
            //moviesAdapterViewHolder.thumbnail.getLayoutParams().height = 450;
            Picasso.with(mContext).load(NetworkUtils.IMAGE_BASE_URL + NetworkUtils.IMAGE_WIDTH_s + movies[position].getPoster_path()).into(moviesAdapterViewHolder.thumbnail);
        }
        else{
            //moviesAdapterViewHolder.thumbnail.getLayoutParams().height = 450;
            Picasso.with(mContext).load(NetworkUtils.IMAGE_BASE_URL + NetworkUtils.IMAGE_WIDTH_s + movies[position].getPoster_path()).into(moviesAdapterViewHolder.thumbnail);
        }

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

    public void setMovieData(Movie[] movies,boolean bigwidth){
        this.movies = movies;
        mBigwidth = bigwidth;
        notifyDataSetChanged();
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
            Movie singleMovie = movies[adapterPosition];
            mClickHandler.onClick(singleMovie);
        }
    }

}
