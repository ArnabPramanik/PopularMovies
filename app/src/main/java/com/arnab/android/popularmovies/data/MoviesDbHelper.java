package com.arnab.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by arnab on 12/22/17.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;
    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry.MOVIE_ID + " INTEGER PRIMARY KEY," +
                MoviesContract.MoviesEntry.MOVIE_TITLE+ " TEXT NOT NULL," +
                MoviesContract.MoviesEntry.RATING+ " INTEGER," +
                MoviesContract.MoviesEntry.IMAGE_URL+ " TEXT" + ");";

        db.execSQL(SQL_CREATE_WEATHER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
