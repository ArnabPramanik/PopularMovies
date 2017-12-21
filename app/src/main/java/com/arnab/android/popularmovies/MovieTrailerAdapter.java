package com.arnab.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.arnab.android.popularmovies.model.MovieTrailer;
import com.arnab.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by arnab on 12/19/17.
 */

public class MovieTrailerAdapter  extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerAdapterViewHolder> {

    private ArrayList<MovieTrailer> mMovieTrailers;
    private Context mContext;
    private TrailerAdapterOnClickHandler mOnClickHandler;

    public MovieTrailerAdapter(Context context, TrailerAdapterOnClickHandler onClickHandler){
        mContext = context;
        mOnClickHandler = onClickHandler;
    }
    public interface TrailerAdapterOnClickHandler {
        void onClick(String url);
    }

    @Override
    public MovieTrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_item;


        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieTrailerAdapter.MovieTrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailerAdapterViewHolder holder, int position) {

        Picasso.with(mContext).load(NetworkUtils.YOUTUBE_THUMB +
                mMovieTrailers.get(position).getKey() + NetworkUtils.YOUTUBE_HIGH_QUALITY).placeholder(R.drawable.ic_action_name).into(holder.trailerThumbnail);
    }

    @Override
    public int getItemCount() {
        if(mMovieTrailers == null){
            return 0;
        }
        return mMovieTrailers.size();
    }


    public void setmMovieTrailers(ArrayList<MovieTrailer> movieTrailers){
        mMovieTrailers = movieTrailers;
        notifyDataSetChanged();
    }


    public class MovieTrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView trailerThumbnail;

        public MovieTrailerAdapterViewHolder(View itemView) {
            super(itemView);
            trailerThumbnail = (ImageView) itemView.findViewById(R.id.iv_trailer_thumbnail_detail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieTrailer trailer = mMovieTrailers.get(adapterPosition); //weird bug here (-1) required
            mOnClickHandler.onClick(NetworkUtils.YOUTUBE_URL + trailer.getKey());
        }
    }
}
