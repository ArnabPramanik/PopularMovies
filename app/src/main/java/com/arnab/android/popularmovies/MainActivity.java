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
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
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
    public final static String SAVE_PAGE_CURRENT = "save_page";
    public final static String SAVE_PAGE_PREVIOUS = "save_page";

    private final static int LOADERID = 1;
    private static int currentPage = 1;
    private static int prevPage = 0;

    //Search
    private String mQuery;
    private final static int LOADERSEARCHID = 2;
    private static int currPageS;
    private static int prevPageS;
    private static final String SAVE_PAGE_PREVIOUS_SEARCH = "save_page_previous_search";
    private static final String SAVE_PAGE_SEARCH = "save_page_search";
    private static final String SAVE_PAGE_CURRENT_SEARCH = "save_page_current_search";
    private boolean mSearch;
    private String mQueryF;
    private static final String SAVE_SEARCH = "save_search";
    private static final String SAVE_SEARCH_QUERY = "save_search_query";

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

        NetworkUtils.SEARCH_PAGE = 1;
        currPageS = NetworkUtils.SEARCH_PAGE;
        prevPageS = currPageS - 1;

        mSearch = false;
        if(savedInstanceState != null) {

            mMovies = (ArrayList<Movie>) savedInstanceState.getSerializable(MOVIE_DATA_MAIN);
            navMenuItem = savedInstanceState.getInt(NAVIGATION_CHOICE);
            if(mMovies != null) {

               mAdapter.setMovieData(mMovies);
            }
            NetworkUtils.PAGE = savedInstanceState.getInt(SAVE_PAGE);
            currentPage = savedInstanceState.getInt(SAVE_PAGE_CURRENT);
            prevPage = savedInstanceState.getInt(SAVE_PAGE_PREVIOUS);

            NetworkUtils.SEARCH_PAGE = savedInstanceState.getInt(SAVE_PAGE_SEARCH);
            currPageS = savedInstanceState.getInt(SAVE_PAGE_CURRENT_SEARCH);
            prevPageS = savedInstanceState.getInt(SAVE_PAGE_PREVIOUS_SEARCH);
            mSearch = savedInstanceState.getBoolean(SAVE_SEARCH);
            mQuery = savedInstanceState.getString(SAVE_SEARCH_QUERY);
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLER_STATE));
        }
        if(navMenuItem == R.id.action_popular_movies) {
            getSupportActionBar().setTitle("Popular Movies");
        }else if(navMenuItem == R.id.action_top_rated_movies) {
            getSupportActionBar().setTitle("Top Rated Movies");

        }
        if(mSearch == true){
            getSupportActionBar().setTitle("Search Result(s)");
        }
        if(mSearch == false) {
            loadMovieData();
        }
        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener(){

            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                if(mSearch == false) {

                    NetworkUtils.PAGE++;
                    if(NetworkUtils.PAGE <= NetworkUtils.TOTAL_PAGES) {
                        currentPage = NetworkUtils.PAGE;
                        loadMovieData();

                    }
                    mXRecyclerView.loadMoreComplete();
                }
                else{

                    NetworkUtils.SEARCH_PAGE ++;
                    if(NetworkUtils.SEARCH_PAGE <= NetworkUtils.TOTAL_PAGES) {
                        currPageS = NetworkUtils.SEARCH_PAGE;
                        loadSearchResults();

                    }
                    mXRecyclerView.loadMoreComplete();
                }

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

        int width = calculateNoOfColumns(this);
        layoutManager = new GridLayoutManager(this,width);
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
        outState.putInt(SAVE_PAGE_CURRENT,currentPage);
        outState.putInt(SAVE_PAGE_PREVIOUS,prevPage);
        outState.putInt(SAVE_PAGE_CURRENT_SEARCH,currPageS);
        outState.putInt(SAVE_PAGE_PREVIOUS_SEARCH,prevPageS);
        outState.putInt(SAVE_PAGE_SEARCH,NetworkUtils.SEARCH_PAGE);
        outState.putString(SAVE_SEARCH_QUERY,mQuery);
        outState.putBoolean(SAVE_SEARCH,mSearch);
    }



    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 1:
                return new AsyncTaskLoader<String[]>(this) {
                    @Override
                    protected void onStartLoading() {
                        if (navMenuItem == R.id.action_popular_movies) {
                            getSupportActionBar().setTitle("Popular Movies");
                        } else if (navMenuItem == R.id.action_top_rated_movies) {
                            getSupportActionBar().setTitle("Top Rated Movies");
                        }
                        if (mAdapter.getItemCount() != 0 && prevPage == currentPage) {
                            deliverResult(new String[]{"A"});
                        } else {
                            mLoadingIndicator.setVisibility(View.VISIBLE);

                            forceLoad();
                        }

                    }

                    @Override
                    public String[] loadInBackground() {

                        URL url = NetworkUtils.buildUrl_popular();
                        if (navMenuItem == R.id.action_popular_movies) {

                            url = NetworkUtils.buildUrl_popular();
                        } else if (navMenuItem == R.id.action_top_rated_movies) {

                            url = NetworkUtils.buildUrl_highestRated();
                        }
                        try {
                            String jsonStr = NetworkUtils.getResponseFromHttpUrl(url, MainActivity.this);

                            mMovies = (JsonParser.getMovieObjects(jsonStr));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return new String[]{"A"};
                    }

                    @Override
                    public void deliverResult(String[] data) {


                        super.deliverResult(data);
                    }
                };
            case 2:
                return new AsyncTaskLoader<String[]>(this) {
                    @Override
                    protected void onStartLoading() {
                        getSupportActionBar().setTitle("Search Result(s)");
                        if(mAdapter.getItemCount() != 0 && currPageS == prevPageS){
                            deliverResult(new String[0]);

                        }else {
                            mLoadingIndicator.setVisibility(View.VISIBLE);
                            String tempQuery = "";
                            String[] strArr = mQuery.split(" ");
                            for (int count = 0; count < strArr.length; count++) {
                                if (count == strArr.length - 1) {
                                    tempQuery += strArr[count];
                                } else {
                                    tempQuery += strArr[count] + "+";
                                }
                            }
                            mQueryF = tempQuery;
                            forceLoad();
                        }
                    }

                    @Override
                    public String[] loadInBackground() {

                        URL url = NetworkUtils.buildUrl_search(mQueryF);
                        try {
                            String jsonStr = NetworkUtils.getResponseFromHttpUrl(url, MainActivity.this);

                            mMovies = (JsonParser.getMovieObjects(jsonStr));

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
        switch (loader.getId()) {
            case 1:{

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (mMovies != null && mMovies.size() != 0) {

                if (prevPage != currentPage) {

                    prevPage = currentPage;
                    showMovieDataView();
                    mAdapter.setMovieData(mMovies);
                }
            } else {

                showErrorMessage();
            }
            /*
            There is bug in xRecyclerView (the infinite scroll recyclerview)
            library that is being used. If movies < screen height then it doesn't work.
            This is a problem in tablets. This is why page 2 and 3 are being loaded.
             */
            if (NetworkUtils.PAGE == 1 || NetworkUtils.PAGE == 2) {

                NetworkUtils.PAGE++;
                if(NetworkUtils.PAGE <= NetworkUtils.TOTAL_PAGES) {
                    currentPage = NetworkUtils.PAGE;
                    loadMovieData();
                }
            }
            break;
            }
            case 2:{

                mLoadingIndicator.setVisibility(View.INVISIBLE);
                if (mMovies != null && mMovies.size()!= 0) {

                    if (prevPageS != currPageS) {

                        prevPageS = currPageS;
                        showMovieDataView();

                        mAdapter.setMovieData(mMovies);
                    }
                } else {

                    showErrorMessage();
                }
                 /*
            There is bug in xRecyclerView (the infinite scroll recyclerview)
            library that is being used. If movies < screen height then it doesn't work.
            This is a problem in tablets. This is why page 2 and 3 are being loaded.
             */
                if (NetworkUtils.SEARCH_PAGE == 1 || NetworkUtils.SEARCH_PAGE == 2) {
                    NetworkUtils.SEARCH_PAGE++;

                    if(NetworkUtils.SEARCH_PAGE <= NetworkUtils.TOTAL_PAGES) {
                        currPageS = NetworkUtils.SEARCH_PAGE;
                        loadSearchResults();
                    }

                }
                break;
            }

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
            mSearch = false;
            mToast = Toast.makeText(this, "Top Rated Movies", Toast.LENGTH_SHORT);
            mToast.show();
            mAdapter.clean();
            NetworkUtils.PAGE = 1;
            currentPage = 1;
            prevPage = 0;
            loadMovieData();
        }
        if(itemId == R.id.action_popular_movies){

            navMenuItem = R.id.action_popular_movies;
            mSearch = false;
            mToast = Toast.makeText(this,"Popular Movies",Toast.LENGTH_SHORT);
            mToast.show();

            mAdapter.clean();
            NetworkUtils.PAGE = 1;
            currentPage = 1;
            prevPage = 0;
            loadMovieData();

        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search_bar);
        SearchView searchView =   (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                mQuery = query;
                mAdapter.clean();
                mSearch = true;
                NetworkUtils.SEARCH_PAGE = 1;
                currPageS = NetworkUtils.SEARCH_PAGE;
                prevPageS = currPageS - 1;
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                loadSearchResults();

                return false;
            }
        });
    return super.onCreateOptionsMenu(menu);
    }

    private void loadSearchResults(){
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> loader = loaderManager.getLoader(LOADERSEARCHID);
        if(loader == null){
            loaderManager.initLoader(LOADERSEARCHID,null,this);

        }
        else{
            loaderManager.restartLoader(LOADERSEARCHID,null,this);
        }

    }
}


