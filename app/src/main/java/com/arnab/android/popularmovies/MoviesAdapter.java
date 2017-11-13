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

    public interface MovieAdapterOnClickHandler {
        void onClick(String movie);
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
        String singleMovie = movies[position].toString();
        moviesAdapterViewHolder.movieDataView.setText(singleMovie);
        Picasso.with(mContext).load(NetworkUtils.IMAGE_BASE_URL + NetworkUtils.IMAGE_WIDTH + movies[position].getPoster_path()).into(moviesAdapterViewHolder.thumbnail);


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

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView movieDataView;
        public final ImageView thumbnail;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            movieDataView = (TextView)  itemView.findViewById(R.id.tv_movie_data);
            thumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            int adapterPosition = getAdapterPosition();
            String singleMovie = movies[adapterPosition].toString();
            mClickHandler.onClick(singleMovie);
        }
    }

}
