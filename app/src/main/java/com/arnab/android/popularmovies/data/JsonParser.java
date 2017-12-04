package com.arnab.android.popularmovies.data;

import android.util.Log;

import com.arnab.android.popularmovies.DetailView;
import com.arnab.android.popularmovies.model.Movie;
import com.arnab.android.popularmovies.model.MovieDetails;
import com.arnab.android.popularmovies.model.MovieReview;
import com.arnab.android.popularmovies.utils.NetworkUtils;

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
            NetworkUtils.TOTAL_PAGES = allData.getInt("total_pages");
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
        } catch (JSONException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        return null;
    }

    public static MovieDetails getMovieDetailObject(String data){
        try {
            JSONObject movieData = new JSONObject(data);
            MovieDetails movieDetails = new MovieDetails();
            movieDetails.setVote_count(movieData.getInt("vote_count"));
            movieDetails.setId(movieData.getInt("id"));
            movieDetails.setTitle(movieData.getString("title"));
            movieDetails.setVote_average(movieData.getDouble("vote_average"));
            movieDetails.setPoster_path(movieData.getString("poster_path"));
            movieDetails.setRelease_date(movieData.getString("release_date"));
            movieDetails.setOverview(movieData.getString("overview"));
            movieDetails.setBackdrop_path(movieData.getString("backdrop_path"));
            JSONArray genreArray = movieData.getJSONArray("genres");
            String strArr [] = new String[genreArray.length()];
            for (int i = 0; i < genreArray.length(); i++) {
                String genre = genreArray.getJSONObject(i).getString("name");
                strArr[i] = genre;
            }
            movieDetails.setGenre(strArr);
            movieDetails.setRuntime(movieData.getInt("runtime"));
            movieDetails.setTagline(movieData.getString("tagline"));
            return movieDetails;

        } catch (JSONException e) {
            e.printStackTrace();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<MovieReview> getMovieReviewArrayList(String data){
        try {
            JSONObject allData = new JSONObject(data);
            NetworkUtils.TOTAL_REVIEW_PAGES = allData.getInt("total_pages");
            if(NetworkUtils.TOTAL_REVIEW_PAGES > 1){
                DetailView.showButton = true;
            }
            else{
                DetailView.showButton = false;
            }
            JSONArray movieReviews = allData.getJSONArray("results");
            ArrayList<MovieReview> listMovieReviews = new ArrayList<MovieReview>();
            for(int count = 0; count < movieReviews.length(); count++){
                JSONObject jo = movieReviews.getJSONObject(count);
                MovieReview movieReview = new MovieReview();
                movieReview.setAuthor(jo.getString("author"));
                movieReview.setContent(jo.getString("content"));
                listMovieReviews.add(movieReview);
            }

            return listMovieReviews;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
