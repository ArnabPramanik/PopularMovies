package com.arnab.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by arnab on 11/19/17.
 */

public class GenreItemAdapter extends RecyclerView.Adapter<GenreItemAdapter.GenreItemAdapterViewHolder>{
    private String[] mGenres;

    @Override
    public GenreItemAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.genre_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new GenreItemAdapter.GenreItemAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(GenreItemAdapterViewHolder holder, int position) {
        String genre = mGenres[position];
        holder.genreItem.setText(genre);
    }

    @Override
    public int getItemCount() {
        if(mGenres == null){
            return 0;
        }else{
            return mGenres.length;
        }
    }

    public void setGenres(String [] genres){
        mGenres = genres;
        notifyDataSetChanged();
    }

    public class GenreItemAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView genreItem;
        public GenreItemAdapterViewHolder(View itemView) {
            super(itemView);
            genreItem =  (TextView) itemView.findViewById(R.id.tv_genre_detail);
        }
    }
}
