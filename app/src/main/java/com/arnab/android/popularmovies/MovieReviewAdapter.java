package com.arnab.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arnab.android.popularmovies.model.Movie;
import com.arnab.android.popularmovies.model.MovieReview;
import com.arnab.android.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;

/**
 * Created by arnab on 11/25/17.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewAdapterViewHolder> {

    public ArrayList<MovieReview> mMovieReviews;
    private DetailView mDetailView;

    public MovieReviewAdapter(DetailView detailView){

        mDetailView = detailView;
    }
    @Override
    public MovieReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem;
        if(viewType == R.layout.review_buttons){
            layoutIdForListItem = R.layout.review_buttons;
        }
        else{
            layoutIdForListItem = R.layout.review;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieReviewAdapter.MovieReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewAdapterViewHolder holder, int position) {
        if(position != mMovieReviews.size()) {
            holder.reviewAuthor.setText(mMovieReviews.get(position).getAuthor());
            holder.reviewContent.setText(mMovieReviews.get(position).getContent());

        }
        else{

            if(NetworkUtils.REVIEW_PAGE < NetworkUtils.TOTAL_REVIEW_PAGES){
                //Show NEXT button
                holder.next.setVisibility(View.VISIBLE);

            }else{
                holder.next.setVisibility(View.INVISIBLE);
            }
            if(NetworkUtils.REVIEW_PAGE > 1){
                //Show PREVIOUS button
                holder.previous.setVisibility(View.VISIBLE);

            }
            else{
                holder.previous.setVisibility(View.INVISIBLE);
            }
            holder.next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkUtils.REVIEW_PAGE < NetworkUtils.TOTAL_REVIEW_PAGES) {
                        NetworkUtils.REVIEW_PAGE++;
                        DetailView.revCurPage = NetworkUtils.REVIEW_PAGE;
                        mDetailView.loadReviewDetails();
                    }
                }
            });
            holder.previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkUtils.REVIEW_PAGE > 1) {
                        NetworkUtils.REVIEW_PAGE--;
                        DetailView.revCurPage = NetworkUtils.REVIEW_PAGE;
                        mDetailView.loadReviewDetails();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mMovieReviews == null) {
            return 0;
        }
        else if(DetailView.showButton) {
            return mMovieReviews.size() + 1;
        } else{
            return mMovieReviews.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mMovieReviews.size())?R.layout.review_buttons:R.layout.review;
    }

    public void setMovieReviews(ArrayList<MovieReview> movieReviews){
        this.mMovieReviews = movieReviews;
        notifyDataSetChanged();
    }

    public class MovieReviewAdapterViewHolder extends RecyclerView.ViewHolder{
        public final TextView reviewAuthor;
        public final TextView reviewContent;
        public final Button previous;
        public final Button next;
        public MovieReviewAdapterViewHolder(View itemView) {
            super(itemView);
            reviewAuthor = (TextView) itemView.findViewById(R.id.tv_author_detail);
            reviewContent = (TextView) itemView.findViewById(R.id.tv_content_detail);
            previous = (Button) itemView.findViewById(R.id.button_prev_detail);
            next = (Button) itemView.findViewById(R.id.button_next_detail);

        }
    }
}
