package com.arnab.android.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.arnab.android.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


/**
 * Created by arnab on 11/11/17.
 */



public class NetworkUtils {
    static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    static final String POPULAR = "/popular";
    static final String TOP_RATED = "/top_rated";
    static final String SEARCH_URL = "https://api.themoviedb.org/3/search/movie?api_key=";
    static final String REVIEWS = "/reviews";
    static final String API_KEY = BuildConfig.API_KEY;;
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_WIDTH_s = "w342/";
    public static final String IMAGE_WIDTH_l = "w780/";
    public static int PAGE = 1;
    public static int SEARCH_PAGE = 1;
    public static int TOTAL_PAGES = 1;
    public static int REVIEW_PAGE = 1;
    public static int TOTAL_REVIEW_PAGES = 1;
    public static String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    public static String YOUTUBE_THUMB = "http://img.youtube.com/vi/";
    public static String YOUTUBE_HIGH_QUALITY = "/hqdefault.jpg";

    public static URL buildUrl_popular(){

        Uri buildUri = Uri.parse(BASE_URL + POPULAR + "?api_key=" + API_KEY + "&language=en-US&page=" + String.valueOf(PAGE));
        try {
            URL url = new URL(buildUri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildUrl_highestRated(){
        Uri buildUri = Uri.parse(BASE_URL + TOP_RATED + "?api_key=" + API_KEY + "&language=en-US&page=" + String.valueOf(PAGE));
        try {
            URL url = new URL(buildUri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildUrl_details(int movieId){
        Uri buildUri = Uri.parse(BASE_URL + "/" + movieId + "?api_key=" + API_KEY);
        try{
            URL url = new URL(buildUri.toString());
            return url;
        } catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildUrl_search(String query){
        Uri buildUri = Uri.parse(SEARCH_URL + API_KEY + "&query=" + query + "&page=" + NetworkUtils.SEARCH_PAGE);
        try{
            URL url = new URL(buildUri.toString());
            return url;
        } catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildUrlReviews(int movieId){
        Uri buildUri = Uri.parse(BASE_URL+ "/" + movieId + "/reviews?api_key=" + API_KEY + "&language=en-US&page=" + NetworkUtils.REVIEW_PAGE);
        try{
            URL url = new URL(buildUri.toString());
            return url;
        } catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static URL buildUrlVideos(int movieId){
        Uri buildUri = Uri.parse(BASE_URL+ "/" + movieId + "/videos?api_key=" + API_KEY + "&language=en-US");
        try{
            URL url = new URL(buildUri.toString());
            return url;
        } catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public static String getResponseFromHttpUrl(URL url, Context context) throws IOException {
        if(isOnline(context)) {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();

                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } finally {
                urlConnection.disconnect();
            }
        }
        else{
            return null;
        }
    }


}
