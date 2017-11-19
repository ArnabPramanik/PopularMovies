package com.arnab.android.popularmovies;

import android.content.Context;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arnab.android.popularmovies.data.JsonParser;
import com.arnab.android.popularmovies.model.Movie;
import com.arnab.android.popularmovies.utils.NetworkUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,MoviesAdapter.MovieAdapterOnClickHandler,LoaderManager.LoaderCallbacks<String[]> {

    private TextView mErrorMessageView;
    private ProgressBar mLoadingIndicator;
    private int navMenuItem;
    private Toast mToast;
    ArrayList<Movie> mMovies;
    //Navigation Menu
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    NavigationView mNavigationView;
    private static final String NAVIGATION_CHOICE = "navigation_choice";
    //Recycler View
    private MoviesAdapter mAdapter;
    private XRecyclerView mXRecyclerView;
    private GridLayoutManager layoutManager;

    //Extra tag
    public final static String EXTRA_MOVIE_ID = "extra_movie_id";
    //Saving instance state
    public final static String RECYCLER_STATE = "recycler_state";
    public final static String MOVIE_DATA_MAIN = "movie_data_main";
    public final static String SAVE_PAGE = "save_page";
    private final static int LOADERID = 1;
    private static int currentPage = 1;
    private static int prevPage = 0;
    boolean mBigWidth = false;


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
        mXRecyclerView = (XRecyclerView) findViewById(R.id.rv_movieData);
        mXRecyclerView.setPullRefreshEnabled(false);
        mXRecyclerView.setLoadingMoreEnabled(true);
        setLayout();
        mXRecyclerView.setLayoutManager(layoutManager);
        //mRecyclerView.setHasFixedSize(true);
        mAdapter = new MoviesAdapter(this,this);
        mXRecyclerView.setAdapter(mAdapter);


        mMovies = new ArrayList<Movie>();
        NetworkUtils.PAGE = 1;
        currentPage = NetworkUtils.PAGE;
        prevPage = currentPage - 1;

        if(savedInstanceState != null) {

            mMovies = (ArrayList<Movie>) savedInstanceState.getSerializable(MOVIE_DATA_MAIN);
            navMenuItem = (int) savedInstanceState.getInt(NAVIGATION_CHOICE);
            if(mMovies != null) {

               mAdapter.setMovieData(mMovies, mBigWidth);
            }
            NetworkUtils.PAGE = savedInstanceState.getInt(SAVE_PAGE);

            currentPage = NetworkUtils.PAGE;
            prevPage = currentPage - 1;
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLER_STATE));
        }
        if(navMenuItem == R.id.action_popular_movies) {
            getSupportActionBar().setTitle("Popular Movies");
        }else if(navMenuItem == R.id.action_top_rated_movies) {
            getSupportActionBar().setTitle("Top Rated Movies");

        }
            loadMovieData();

        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener(){

            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                NetworkUtils.PAGE ++;
                currentPage = NetworkUtils.PAGE;
                loadMovieData();
                 mXRecyclerView.loadMoreComplete();
               // mAdapter.notifyDataSetChanged();

            }
        });


    }
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 100);
        return noOfColumns;
    }


    private void setLayout(){

        if(getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE){

            int width = calculateNoOfColumns(this);
            layoutManager = new GridLayoutManager(this,width);
            mBigWidth = true;

        }else{
            int width = calculateNoOfColumns(this);
            layoutManager = new GridLayoutManager(this,width );
            mBigWidth = false;
        }
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

        mXRecyclerView.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage() {

        mXRecyclerView.setVisibility(View.INVISIBLE);

        mErrorMessageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailView.class);
        intent.putExtra(EXTRA_MOVIE_ID,movie.getId());
        startActivity(intent);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putSerializable(MOVIE_DATA_MAIN,mAdapter.getMovies());
        outState.putParcelable(RECYCLER_STATE, mXRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putInt(SAVE_PAGE,NetworkUtils.PAGE);
        outState.putInt(NAVIGATION_CHOICE,navMenuItem);
    }



    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            @Override
            protected void onStartLoading() {

                if(mAdapter.movies.size() != 0 && prevPage == currentPage){
                   // mMovies = new ArrayList<Movie>();
                    deliverResult(new String[]{"A"});
                }
                else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    //mAdapter.setMovieData(null,mBigWidth);
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

                    mMovies = (JsonParser.getMovieObjects(jsonStr));

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
        if(navMenuItem == R.id.action_popular_movies) {
            getSupportActionBar().setTitle("Popular Movies");
        }else if(navMenuItem == R.id.action_top_rated_movies) {
            getSupportActionBar().setTitle("Top Rated Movies");
        }
        if(mMovies != null ) {

            if(prevPage != currentPage) {

                prevPage = currentPage;
                showMovieDataView();
                mAdapter.setMovieData(mMovies, mBigWidth);
            }
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
            mAdapter.movies.clear();
            mAdapter.notifyDataSetChanged();
            NetworkUtils.PAGE = 1;
            currentPage = 1;
            prevPage = 0;
            loadMovieData();
        }
        if(itemId == R.id.action_popular_movies){
            navMenuItem = R.id.action_popular_movies;

            mToast = Toast.makeText(this,"Popular Movies",Toast.LENGTH_SHORT);
            mToast.show();

            mAdapter.movies.clear();
            mAdapter.notifyDataSetChanged();
            NetworkUtils.PAGE = 1;
            currentPage = 1;
            prevPage = 0;
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


