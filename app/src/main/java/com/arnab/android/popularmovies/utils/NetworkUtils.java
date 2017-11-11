package com.arnab.android.popularmovies.utils;

import android.net.Uri;

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
    static final String API_KEY = "";

    public static URL buildUrl_popular(){
        Uri buildUri = Uri.parse(BASE_URL + POPULAR + "?api_key=" + API_KEY);
        try {
            URL url = new URL(buildUri.toString());
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getResponseFromHttpUrl(URL url) throws IOException {
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

}
