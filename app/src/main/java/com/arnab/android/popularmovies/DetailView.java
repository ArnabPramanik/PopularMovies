package com.arnab.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arnab.android.popularmovies.data.JsonParser;

import com.arnab.android.popularmovies.data.MoviesContentProvider;
import com.arnab.android.popularmovies.data.MoviesContract;
import com.arnab.android.popularmovies.model.Movie;
import com.arnab.android.popularmovies.model.MovieDetails;
import com.arnab.android.popularmovies.model.MovieReview;
import com.arnab.android.popularmovies.model.MovieTrailer;
import com.arnab.android.popularmovies.utils.NetworkUtils;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.squareup.picasso.Picasso;


import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class DetailView extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<String[]>, MovieTrailerAdapter.TrailerAdapterOnClickHandler{
    private ImageView mPoster;
    private TextView mTitle;
    private TextView mRating;
    private TextView mSynopsis;
    private TextView mDate;
    private ImageView mBackDrop;
    private TextView mVoteCount;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageView;
    private TextView mRuntime;
    private TextView mTagline;
    private LinearLayout layout;
    private MovieDetails mMovieDetails;
    private RecyclerView mGenresRecyclerView;
    private GenreItemAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private int movieId;
    private static final int LOADERID = 3;
    private static final String SAVE_MOVIE_DETAILS_OBJ = "save_movie_details_obj";
    private static final String SAVE_MOVIE_ID = "save_movie_id";

    //Review
    private ArrayList<MovieReview> mMovieReviews;
    private RecyclerView mReviewsRecyclerView;
    private MovieReviewAdapter mMovieReviewAdapter;
    private int LOADERID_REVIEW = 4;
    public static boolean showButton;
    public static int revCurPage;
    public static int revPrevPage;
    private static String SAVE_REVIEW_PAGE = "save_review_page";
    private TextView mErrorMessageReview;
    private ProgressBar mLoadingIndicatorReview;
    private static final String SAVE_MOVIE_REVIEWS = "save_movie_reviews";
    private static final String SAVE_MOVIE_REV_PREV = "save_movie_rev_prev";
    private static final String SAVE_MOVIE_REV_CURR= "save_movie_rev_curr";


    //Videos
    private ArrayList<MovieTrailer> mMovieTrailers;
    private MovieTrailerAdapter mMovieTrailerAdapter;
    private static final String SAVE_MOVIE_TRAILERS = "save_movie_trailers";

    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Toolbar Title");

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle("Details");
        setSupportActionBar(toolbar);



        mRating = (TextView)findViewById(R.id.tv_rating_detail);
        mSynopsis = (ReadMoreTextView)findViewById(R.id.tv_overview_detail);
        mDate = (TextView)findViewById(R.id.tv_release_date_detail);
        mBackDrop = (ImageView) findViewById(R.id.iv_backdrop_detail);
        mTitle = (TextView) findViewById(R.id.tv_title_detail);
        mPoster = (ImageView) findViewById(R.id.iv_poster_detail);
        mVoteCount = (TextView) findViewById(R.id.tv_voter_count_detail);
        layout = (LinearLayout) findViewById(R.id.ll_detail);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator_detail);
        mErrorMessageView = (TextView) findViewById(R.id.tv_error_message_display_detail);
        mRuntime = (TextView) findViewById(R.id.tv_runtime_detail);
        mTagline = (TextView) findViewById(R.id.tv_tagline_detail);
        //Genres recyclerview
        mGenresRecyclerView = (RecyclerView) findViewById(R.id.rv_genres_detail);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mGenresRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new GenreItemAdapter();
        mGenresRecyclerView.setAdapter(mAdapter);

        //Reviews recyclerview
        mReviewsRecyclerView = (RecyclerView)findViewById(R.id.rv_reviews_detail);
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mReviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        mMovieReviewAdapter = new MovieReviewAdapter(DetailView.this);
        mReviewsRecyclerView.setAdapter(mMovieReviewAdapter);

        //Trailer recyclerview
        RecyclerView trailerRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers_detail);
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        trailerRecyclerView.setLayoutManager(trailerLayoutManager);
        mMovieTrailerAdapter = new MovieTrailerAdapter(DetailView.this,this);
        trailerRecyclerView.setAdapter(mMovieTrailerAdapter);

        mLoadingIndicatorReview = (ProgressBar) findViewById(R.id.pb_loading_indicator_review);
        mErrorMessageReview = (TextView)findViewById(R.id.tv_error_message_review);
        //Get intent and populate fields
        Intent intentThatStartedThisActivity = getIntent();
        movieId = -1;
        if (intentThatStartedThisActivity.hasExtra(MainActivity.EXTRA_MOVIE_ID)) {
            movieId =  intentThatStartedThisActivity.getIntExtra(MainActivity.EXTRA_MOVIE_ID,-1);
        }
        if(savedInstanceState != null){
            mMovieDetails = (MovieDetails) savedInstanceState.getSerializable(SAVE_MOVIE_DETAILS_OBJ);
            movieId = savedInstanceState.getInt(SAVE_MOVIE_ID);
            NetworkUtils.REVIEW_PAGE = savedInstanceState.getInt(SAVE_REVIEW_PAGE);
            mMovieReviews = (ArrayList<MovieReview>) savedInstanceState.getSerializable(SAVE_MOVIE_REVIEWS);
            mMovieTrailers = (ArrayList<MovieTrailer>) savedInstanceState.getSerializable(SAVE_MOVIE_TRAILERS);
            revCurPage = savedInstanceState.getInt(SAVE_MOVIE_REV_CURR);
            revPrevPage = savedInstanceState.getInt(SAVE_MOVIE_REV_PREV);
        }

            //Make network request
            loadMovieDetails();
            loadReviewDetails();
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        if(ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        fab =  (FloatingActionButton) findViewById(R.id.fab_favorite_detail);

        fab.setVisibility(View.INVISIBLE);
        if(favorited()){
            fab.setImageDrawable(ContextCompat.getDrawable(DetailView.this,android.R.drawable.ic_input_delete));
        }
        else{
            fab.setImageDrawable(ContextCompat.getDrawable(DetailView.this,android.R.drawable.ic_input_add));
        }

    }


    private void loadMovieDetails(){
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(LOADERID);
        if(loader == null){
            loaderManager.initLoader(LOADERID,null,this);

        }
        else{
            loaderManager.restartLoader(LOADERID,null,this);
        }
    }

    public void loadReviewDetails(){
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(LOADERID_REVIEW);
        if(loader == null){
            loaderManager.initLoader(LOADERID_REVIEW,null,this);

        }
        else{
            loaderManager.restartLoader(LOADERID_REVIEW,null,this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_MOVIE_DETAILS_OBJ,mMovieDetails);
        outState.putInt(SAVE_MOVIE_ID,movieId);
        outState.putInt(SAVE_REVIEW_PAGE,NetworkUtils.REVIEW_PAGE);
        outState.putSerializable(SAVE_MOVIE_REVIEWS,mMovieReviews);
        outState.putSerializable(SAVE_MOVIE_TRAILERS,mMovieTrailers);
        outState.putInt(SAVE_MOVIE_REV_PREV,revPrevPage);
        outState.putInt(SAVE_MOVIE_REV_CURR,revCurPage);
    }

    private void showMovieDataView() {

        mErrorMessageView.setVisibility(View.INVISIBLE);

        layout.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage() {

        layout.setVisibility(View.INVISIBLE);

        mErrorMessageView.setVisibility(View.VISIBLE);
    }

    private void showMovieReviewView() {

        mErrorMessageReview.setVisibility(View.INVISIBLE);
        mReviewsRecyclerView.setVisibility(View.VISIBLE);

    }
    private void showErrorMessageReview() {

        mReviewsRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageReview.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case 3:
                return new AsyncTaskLoader<String[]>(this) {
                @Override
                protected void onStartLoading() {
                    if (mMovieDetails != null && mMovieTrailers != null && mMovieTrailers.size() != 0) {
                        deliverResult(new String[0]);
                    } else {
                        mLoadingIndicator.setVisibility(View.VISIBLE);
                        forceLoad();
                    }

                }

                @Override
                public String[] loadInBackground() {

                    URL urlDetails = NetworkUtils.buildUrl_details(movieId);
                    URL urlVideos = NetworkUtils.buildUrlVideos(movieId);
                    try {
                        String jsonStr = NetworkUtils.getResponseFromHttpUrl(urlDetails, DetailView.this);
                        mMovieDetails = (JsonParser.getMovieDetailObject(jsonStr));
                        String jsonStrVideos = NetworkUtils.getResponseFromHttpUrl(urlVideos,DetailView.this);
                        mMovieTrailers = JsonParser.getMovieTrailer(jsonStrVideos);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new String[0];
                }

                @Override
                public void deliverResult(String[] data) {
                    super.deliverResult(data);
                }
            };
            case 4:
                return new AsyncTaskLoader<String[]>(this) {
                    @Override
                    protected void onStartLoading() {

                        if (mMovieReviews != null && revCurPage == revPrevPage && mMovieReviews.size() != 0) {
                            deliverResult(new String[0]);
                        } else {
                            mLoadingIndicatorReview.setVisibility(View.VISIBLE);
                            forceLoad();
                        }
                    }

                    @Override
                    public String[] loadInBackground() {
                        URL urlReviews = NetworkUtils.buildUrlReviews(movieId);
                        try {
                            String jsonReviews = NetworkUtils.getResponseFromHttpUrl(urlReviews,DetailView.this);
                            mMovieReviews = JsonParser.getMovieReviewArrayList(jsonReviews);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return new String[0];
                    }

                    @Override
                    public void deliverResult(String[] data) {
                        super.deliverResult(data);
                    }

                };

        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        fab.setVisibility(View.VISIBLE);
        switch(loader.getId()) {

            case 3:
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (mMovieDetails != null && mMovieTrailers != null) {
                showMovieDataView();
                setMovieData();
                mMovieTrailerAdapter.setmMovieTrailers(mMovieTrailers);
                activateFAB();
            } else {
                showErrorMessage();
            }
            break;
            case 4:
                mLoadingIndicatorReview.setVisibility(View.INVISIBLE);

                if(mMovieReviews != null && mMovieReviews.size() != 0){
                    showMovieReviewView();
                    revPrevPage = revCurPage;
                    mMovieReviewAdapter.setMovieReviews(mMovieReviews);
                } else {
                    showErrorMessageReview();
                }

                break;
        }
    }

    private boolean favorited(){
        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movieId)).build(),MainActivity.PROJECTION,null,null,null);


       if(cursor.getCount() != 0){
           cursor.close();
            return true;
       }
       cursor.close();
       return false;
    }


    private void activateFAB() {

        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                boolean favorited = favorited();
                if(favorited){
                    //unfavorite it
                    Uri contentUri = MoviesContract.MoviesEntry.CONTENT_URI;
                    getContentResolver().delete(contentUri, "movie_id=?", new String[]{String.valueOf(mMovieDetails.getId())});
                    //set image to outline
                    fab.setImageDrawable(ContextCompat.getDrawable(DetailView.this,android.R.drawable.ic_input_add));
                }
                else{
                    //favorite it
                    ContentValues values = new ContentValues();
                    values.put(MoviesContract.MoviesEntry.MOVIE_ID,mMovieDetails.getId());
                    values.put(MoviesContract.MoviesEntry.MOVIE_TITLE,mMovieDetails.getTitle());
                    values.put(MoviesContract.MoviesEntry.RATING,mMovieDetails.getVote_average());
                    values.put(MoviesContract.MoviesEntry.IMAGE_URL,mMovieDetails.getPoster_path());
                    getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, values);

                    //set image to filled
                    fab.setImageDrawable(ContextCompat.getDrawable(DetailView.this,android.R.drawable.ic_input_delete));
                }
            }
        });
    }


    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }


    private void setMovieData() {

        mAdapter.setGenres(mMovieDetails.getGenre());
        mRating.setText(String.valueOf(mMovieDetails.getVote_average()));
        mSynopsis.setText(mMovieDetails.getOverview());
        mDate.setText(mMovieDetails.getRelease_date());
        mTitle.setText(mMovieDetails.getTitle());
        mVoteCount.setText(String.valueOf(mMovieDetails.getVote_count()));
        mTagline.setText(mMovieDetails.getTagline());
        mRuntime.setText(String.valueOf(mMovieDetails.getRuntime()) + "min");
        Picasso.with(this).load(NetworkUtils.IMAGE_BASE_URL + NetworkUtils.IMAGE_WIDTH_s + mMovieDetails.getPoster_path()).placeholder(R.drawable.ic_action_name).into(mPoster);
        Picasso.with(this).load(NetworkUtils.IMAGE_BASE_URL + NetworkUtils.IMAGE_WIDTH_l + mMovieDetails.getBackdrop_path()).placeholder(R.drawable.ic_action_name).into(mBackDrop);
    }


    @Override
    public void onClick(String url) {
        //CHECK IF BROWSER IS INSTALLED*****************
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
