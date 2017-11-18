package com.arnab.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arnab.android.popularmovies.data.JsonParser;
import com.arnab.android.popularmovies.model.Movie;
import com.arnab.android.popularmovies.utils.NetworkUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,MoviesAdapter.MovieAdapterOnClickHandler,LoaderManager.LoaderCallbacks<String[]> {

    private TextView mErrorMessageView;
    private ProgressBar mLoadingIndicator;
    private int navMenuItem;
    private Toast mToast;
    Movie [] mMovies;
    //Navigation Menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    NavigationView mNavigationView;
    private static final String NAVIGATION_CHOICE = "navigation_choice";

    //Recycler View
    private MoviesAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager layoutManager;

    //Extra tag
    public final static String EXTRA_MOVIE_OBJECT = "extra_movie_object";
    //Saving instance state
    public final static String RECYCLER_STATE = "recycler_state";
    public final static String MOVIE_DATA_MAIN = "movie_data_main";
    private Bundle globalBundle;
    private final static int LOADERID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mErrorMessageView = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        //Navigation Menu
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        mNavigationView.setNavigationItemSelectedListener(this);

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movieData);
        layoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MoviesAdapter(this,this);
        mRecyclerView.setAdapter(mAdapter);


        if(savedInstanceState != null) {

            mMovies = (Movie[]) savedInstanceState.getSerializable(MOVIE_DATA_MAIN);
            mAdapter.setMovieData(mMovies);
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLER_STATE));
            navMenuItem = savedInstanceState.getInt(NAVIGATION_CHOICE);
        }
        loadMovieData();
    }



    private void loadMovieData(){
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(LOADERID);
        if(loader == null){
            loaderManager.initLoader(LOADERID,null,this);

        }
        else{
            loaderManager.restartLoader(LOADERID,null,this);
        }

        //getSupportLoaderManager().initLoader(LOADERID, null, this);
    }
    private void showMovieDataView() {

        mErrorMessageView.setVisibility(View.INVISIBLE);

        mRecyclerView.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage() {

        mRecyclerView.setVisibility(View.INVISIBLE);

        mErrorMessageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailView.class);
        intent.putExtra(EXTRA_MOVIE_OBJECT,movie);
        startActivity(intent);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putSerializable(MOVIE_DATA_MAIN,mMovies);
        outState.putParcelable(RECYCLER_STATE, mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putInt(NAVIGATION_CHOICE,navMenuItem);
    }



    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            @Override
            protected void onStartLoading() {
                Log.wtf("ALSO","RUNNING");
                if(mMovies != null){
                    Log.wtf("IN","HERE");
                    deliverResult(new String[]{"A"});
                }
                else {

                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    mAdapter.setMovieData(null);
                    forceLoad();
                }

            }

            @Override
            public String[] loadInBackground() {

                URL url = NetworkUtils.buildUrl_popular();
                if(navMenuItem == R.id.action_popular_movies) {
                    url = NetworkUtils.buildUrl_popular();
                }
                else if(navMenuItem == R.id.action_top_rated_movies){
                    url = NetworkUtils.buildUrl_highestRated();
                }
                try {
                    String jsonStr = NetworkUtils.getResponseFromHttpUrl(url,MainActivity.this);

                    mMovies = JsonParser.getMovieObjects(jsonStr);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return  new String [] {"A"};
            }

            @Override
            public void deliverResult(String[] data) {


                super.deliverResult( data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if(navMenuItem == R.id.action_popular_movies){
            getSupportActionBar().setTitle("Popular Movies");
        }
        else if (navMenuItem == R.id.action_top_rated_movies){
            getSupportActionBar().setTitle("Top Rated Movies");
        }
        if(mMovies != null ) {
            Log.wtf("NULL","NOT NULL");

            showMovieDataView();
            mAdapter.setMovieData(mMovies);

        }
        else{

            showErrorMessage();
        }


    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if(mToast != null){
            mToast.cancel();
        }
        if(itemId == R.id.action_top_rated_movies) {
            navMenuItem = R.id.action_top_rated_movies;

            mToast = Toast.makeText(this, "Top Rated Movies", Toast.LENGTH_SHORT);
            mToast.show();

            mMovies = null;
            loadMovieData();
        }
        if(itemId == R.id.action_popular_movies){
            navMenuItem = R.id.action_popular_movies;

            mToast = Toast.makeText(this,"Popular Movies",Toast.LENGTH_SHORT);
            mToast.show();
            mMovies = null;
            loadMovieData();

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Navigation menu
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}


