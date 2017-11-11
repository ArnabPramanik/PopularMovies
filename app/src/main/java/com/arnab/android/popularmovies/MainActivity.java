package com.arnab.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arnab.android.popularmovies.data.JsonParser;
import com.arnab.android.popularmovies.model.Movie;
import com.arnab.android.popularmovies.utils.NetworkUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView mMovieListView;
    private TextView mErrorMessageView;
    private ProgressBar mLoadingIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMovieListView = (TextView) findViewById(R.id.tv_movie_data);
        mErrorMessageView = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        loadMovieData();
    }

    private void loadMovieData(){
        new MyAsyncTask().execute("");
    }
    private void showMovieDataView() {

        mErrorMessageView.setVisibility(View.INVISIBLE);

        mMovieListView.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage() {

        mMovieListView.setVisibility(View.INVISIBLE);

        mErrorMessageView.setVisibility(View.VISIBLE);
    }

    private class MyAsyncTask extends AsyncTask<String,Void,Movie[]>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String ... params) {
            URL url = NetworkUtils.buildUrl_popular();
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
                for (int count = 0; count < movies.length; count++) {
                    mMovieListView.append(movies[count].getTitle() + " " + movies[count].getVote_average() + "\n\n");
                }
            }
            else{
                showErrorMessage();
            }
        }


    }

}


