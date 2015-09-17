package com.example.xfang.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.xfang.popularmovies.data.MovieContract.MovieEntry;
import com.example.xfang.popularmovies.data.MovieContract.VideoEntry;

/**
 * Created by xfang on 8/14/15.
 */
public class MovieDbHelper extends SQLiteOpenHelper {


    // version 2 : added _ID column
    public static final int DB_VERSION = 2;
    public static final String DB_NAME = "data.db";

    public MovieDbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COL_MOVIE_ID + " TEXT," +
                MovieEntry.COL_MOVIE_TITLE + " TEXT NOT NULL," +
                MovieEntry.COL_POSTERPATH + " TEXT NOT NULL," +
                MovieEntry.COL_DATE + " TEXT NOT NULL," +
                MovieEntry.COL_PLOT + " TEXT NOT NULL," +
                MovieEntry.COL_RATING + " DECIMAL(1,1) NOT NULL" +
                ");";

        final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY," +
                VideoEntry.COL_VIDEO_KEY + " TEXT," +
                VideoEntry.COL_VIDEO_TITLE + " TEXT NOT NULL," +
                VideoEntry.COL_SITE + " TEXT NOT NULL," +
                VideoEntry.COL_MOVIE_ID + " TEXT NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        onCreate(db);
    }

}
