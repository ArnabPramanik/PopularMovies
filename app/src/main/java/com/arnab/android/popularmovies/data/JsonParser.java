package com.arnab.android.popularmovies.data;

import android.util.Log;

import com.arnab.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by arnab on 11/12/17.
 */

public class JsonParser {

    public static ArrayList<Movie> getMovieObjects(String data){
        try {
            JSONObject allData = new JSONObject(data);
            //Log.v("ALL DATA", allData.toString());
            JSONArray popularMovies = allData.getJSONArray("results");
            ArrayList<Movie> movies= new ArrayList<Movie>();
            for(int count = 0; count < popularMovies.length(); count++){
                JSONObject movieData = popularMovies.getJSONObject(count);
                Movie movie = new Movie();
                movie.setVote_count(movieData.getInt("vote_count"));
                movie.setId(movieData.getInt("id"));
                movie.setTitle(movieData.getString("title"));
                movie.setVote_average(movieData.getDouble("vote_average"));
                movie.setPoster_path(movieData.getString("poster_path"));
                movie.setRelease_date(movieData.getString("release_date"));
                movie.setOverview(movieData.getString("overview"));
                movie.setBackdrop_path(movieData.getString("backdrop_path"));
                movies.add(movie);
            }
            return movies;
            //Log.v("POPULAR", popular_movies.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        return null;
    }
}
