package com.arnab.android.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by arnab on 11/11/17.
 */



public class NetworkUtils {
    static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    static final String POPULAR = "/popular";
    static final String TOP_RATED = "/top_rated";
    static final String API_KEY = "";
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_WIDTH_s = "w342/";
    public static final String IMAGE_WIDTH_l = "w780/";
    public static int PAGE = 1;
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
