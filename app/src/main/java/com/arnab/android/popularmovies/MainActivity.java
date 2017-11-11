package com.arnab.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.arnab.android.popularmovies.data.JsonParser;
import com.arnab.android.popularmovies.model.Movie;
import com.arnab.android.popularmovies.utils.NetworkUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView mMovieListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMovieListView = (TextView) findViewById(R.id.tv_movie_data);
        loadMovieData();
    }

    private void loadMovieData(){
        new MyAsyncTask().execute("");
    }

    private class MyAsyncTask extends AsyncTask<String,Void,Movie[]>{

        @Override
        protected Movie[] doInBackground(String ... params) {
            URL url = NetworkUtils.buildUrl_popular();
            try {
                String jsonStr = NetworkUtils.getResponseFromHttpUrl(url);
                Movie [] movies = JsonParser.getMovieObjects(jsonStr);
                return movies;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            for(int count = 0; count < movies.length; count++){
                mMovieListView.append(movies[count].getTitle() + " " + movies[count].getVote_average() + "\n\n");
            }
        }
    }

}


