package com.arnab.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by arnab on 12/22/17.
 */

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.arnab.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH = "favorites";
    public static final class MoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH)
                .build();
        public static final String TABLE_NAME = "favorites";
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_TITLE = "movie_title";
        public static final String RATING = "rating";
        public static final String IMAGE_URL = "image_url";

    }
}
