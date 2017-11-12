package com.arnab.android.popularmovies;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,MoviesAdapter.MovieAdapterOnClickHandler{

    private TextView mErrorMessageView;
    private ProgressBar mLoadingIndicator;
    private int navMenuItem;
    private Toast mToast;
    //Navigation Menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    NavigationView mNavigationView;

    //Recycler View
    private MoviesAdapter mAdapter;
    private RecyclerView mRecyclerView;
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mAdapter);




        loadMovieData();
    }

    private void loadMovieData(){
        new MyAsyncTask().execute("");
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
    public void onClick(String movie) {
        if(mToast != null){
            mToast.cancel();
        }
        mToast = Toast.makeText(this,movie,Toast.LENGTH_SHORT);
        mToast.show();
    }

    private class MyAsyncTask extends AsyncTask<String,Void,Movie[]>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mAdapter.setMovieData(null);

        }

        @Override
        protected Movie[] doInBackground(String ... params) {
            URL url = NetworkUtils.buildUrl_popular();
            if(navMenuItem == R.id.action_popular_movies) {
                url = NetworkUtils.buildUrl_popular();
            }
            else if(navMenuItem == R.id.action_top_rated_movies){
                url = NetworkUtils.buildUrl_highestRated();
            }
            try {
                String jsonStr = NetworkUtils.getResponseFromHttpUrl(url,MainActivity.this);

                Movie [] movies = JsonParser.getMovieObjects(jsonStr);
                return movies;
            } catch (IOException e) {
                e.printStackTrace();
            } catch(NullPointerException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movies != null) {
                showMovieDataView();
                mAdapter.setMovieData(movies);

            }
            else{
                showErrorMessage();
            }
        }

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
            getSupportActionBar().setTitle("Top Rated Movies");
            loadMovieData();
        }
        if(itemId == R.id.action_popular_movies){
            navMenuItem = R.id.action_popular_movies;

            mToast = Toast.makeText(this,"Popular Movies",Toast.LENGTH_SHORT);
            mToast.show();
            getSupportActionBar().setTitle("Popular Movies");
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


