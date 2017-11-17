package com.arnab.android.popularmovies;

import android.content.Intent;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.arnab.android.popularmovies.model.Movie;
import com.arnab.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class DetailView extends AppCompatActivity {
    private ImageView mPoster;
    private TextView mTitle;
    private TextView mRating;
    private TextView mSynopsis;
    private TextView mDate;
    private ImageView mBackDrop;
    private TextView mVoteCount;

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
        mSynopsis = (TextView)findViewById(R.id.tv_overview_detail);
        mDate = (TextView)findViewById(R.id.tv_release_date_detail);
        mBackDrop = (ImageView) findViewById(R.id.iv_backdrop_detail);
        mTitle = (TextView) findViewById(R.id.tv_title_detail);
        mPoster = (ImageView) findViewById(R.id.iv_poster_detail);
        mVoteCount = (TextView) findViewById(R.id.tv_voter_count_detail);
        // getSupportActionBar().setTitle("Details");
        //Get intent and populate fields
        Intent intentThatStartedThisActivity = getIntent();
        Movie movie = null;
        if (intentThatStartedThisActivity.hasExtra(MainActivity.EXTRA_MOVIE_OBJECT)) {
            movie = (Movie) intentThatStartedThisActivity.getSerializableExtra(MainActivity.EXTRA_MOVIE_OBJECT);
        }
        if (movie != null) {
            Picasso.with(this)
                    .load(NetworkUtils.IMAGE_BASE_URL + NetworkUtils.IMAGE_WIDTH_s + movie.getPoster_path())
                    .into(mPoster);
            mTitle.setText(movie.getTitle());
            Picasso.with(this)
                    .load(NetworkUtils.IMAGE_BASE_URL + NetworkUtils.IMAGE_WIDTH_l + movie.getBackdrop_path())
                    .into(mBackDrop);
            mRating.setText(String.valueOf(movie.getVote_average()));
            mDate.setText(String.valueOf(movie.getRelease_date()));
            mVoteCount.setText(String.valueOf(movie.getVote_count()));
            mSynopsis.setText(movie.getOverview());

            ActionBar ab = getSupportActionBar();

            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
}
