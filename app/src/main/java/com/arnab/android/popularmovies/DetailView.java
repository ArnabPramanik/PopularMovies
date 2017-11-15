package com.arnab.android.popularmovies;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);


        mPoster = (ImageView)findViewById(R.id.iv_poster);
        mTitle = (TextView)findViewById(R.id.tv_title);
        mRating = (TextView)findViewById(R.id.tv_rating);
        mSynopsis = (TextView)findViewById(R.id.tv_synopsis);
        mDate = (TextView)findViewById(R.id.tv_release_date);

        getSupportActionBar().setTitle("Details");
        //Get intent and populate fields
        Intent intentThatStartedThisActivity = getIntent();
        Movie movie = null;
        if(intentThatStartedThisActivity.hasExtra(MainActivity.EXTRA_MOVIE_OBJECT)){
             movie = (Movie) intentThatStartedThisActivity.getSerializableExtra(MainActivity.EXTRA_MOVIE_OBJECT);
        }
        if(movie != null){
            Picasso.with(this)
                .load(NetworkUtils.IMAGE_BASE_URL + NetworkUtils.IMAGE_WIDTH + movie.getPoster_path())
                .into(mPoster);
            mTitle.setText(movie.getTitle());
            mRating.setText(String.valueOf(movie.getVote_average()));
            mDate.setText(String.valueOf(movie.getRelease_date()));
            mSynopsis.setText(movie.getOverview());
        }

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
}
}
