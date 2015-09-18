package com.example.xfang.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.xfang.popularmovies.data.MovieContract.MovieEntry;
import com.example.xfang.popularmovies.data.MovieContract.VideoEntry;

/**
 * Created by xfang on 8/14/15.
 */
public class MovieProvider extends ContentProvider {

    public static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private static final UriMatcher mUriMatcher = buildUriMatcher();
    private MovieDbHelper mDbHelper;

    static final int ALL_MOVIES = 100;
    static final int SINGLE_MOVIE = 101;
    static final int ALL_VIDEOS = 200;
    static final int SINGLE_VIDEO = 201;
    static final int VIDEOS_SAME_MOVIE = 202;

    static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, ALL_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", SINGLE_MOVIE);

        matcher.addURI(authority, MovieContract.PATH_VIDEO, ALL_VIDEOS);
        matcher.addURI(authority, MovieContract.PATH_VIDEO + "/*", VIDEOS_SAME_MOVIE);
        matcher.addURI(authority,MovieContract.PATH_VIDEO + "/*/*", SINGLE_VIDEO);
        return matcher;
    }

    @Override
    public String getType(Uri uri){
        int match = mUriMatcher.match(uri);

        switch(match){
            case ALL_MOVIES:
                return MovieEntry.CONTENT_TYPE;
            case SINGLE_MOVIE:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case ALL_VIDEOS:
                return VideoEntry.CONTENT_TYPE;
            case VIDEOS_SAME_MOVIE:
                return VideoEntry.CONTENT_TYPE;
            case SINGLE_VIDEO:
                return VideoEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int rows;
        switch (match){
            case ALL_MOVIES: {
                rows = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case ALL_VIDEOS:{
                rows = db.update(VideoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }

            default:{
                throw new UnsupportedOperationException("Update: Unknown uri " + uri);
            }
        }

        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case ALL_MOVIES: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case ALL_VIDEOS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(VideoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Uri insert (Uri uri, ContentValues values){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case ALL_MOVIES: {
                long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    //String movieId = (String) values.get(MovieEntry.COL_MOVIE_ID);
                    returnUri = MovieEntry.buildMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case ALL_VIDEOS:{
                long _id = db.insert(VideoEntry.TABLE_NAME, null, values);
                if (_id > 0 ){
                    //String video_key = (String) values.get(VideoEntry.COL_VIDEO_KEY);
                    returnUri = VideoEntry.buildVideoUri(_id);
                }
                else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            default:{
                throw new UnsupportedOperationException("Insert: Unknown uri " + uri);
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate (){
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public int delete (Uri uri, String selection, String[] selectionArgs){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int match = mUriMatcher.match(uri);
        int rows;
        // makes delete all rows return the number of rows deleted
        if (selection == null){
            selection = "1";
        }
        switch (match){
            case ALL_MOVIES: {
                rows = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case ALL_VIDEOS:{
                rows = db.delete(VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }

            default:{
                throw new UnsupportedOperationException("Delete: Unknown uri " + uri);
            }
        }
        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor c;
        switch (mUriMatcher.match(uri)){
            case ALL_MOVIES: {
                c = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case ALL_VIDEOS: {
                c = mDbHelper.getReadableDatabase().query(
                        VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case SINGLE_MOVIE: {
                String movie_id = MovieEntry.getMovieIdFromUri(uri);
                Log.d(LOG_TAG, "Querying movie with id " + movie_id);

                c = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        new String(MovieEntry.COL_MOVIE_ID + " = ? "),
                        new String[]{movie_id},
                        null,
                        null,
                        sortOrder);
                break;
            }

            case VIDEOS_SAME_MOVIE: {
                String movie_id = VideoEntry.getMovieIdFromUri(uri);
                Log.d(LOG_TAG, "Querying videos from movie with id " + movie_id);

                c = mDbHelper.getReadableDatabase().query(
                        VideoEntry.TABLE_NAME,
                        projection,
                        new String(VideoEntry.COL_MOVIE_ID + " = ? "),
                        new String[]{movie_id},
                        null,
                        null,
                        sortOrder);
                break;
            }

            case SINGLE_VIDEO: {
                String movie_id = VideoEntry.getMovieIdFromUri(uri);
                String video_key = VideoEntry.getVideoKeyFromUri(uri);
                Log.d(LOG_TAG, "Querying video " + video_key + " from movie " + movie_id);

                c = mDbHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        new String(VideoEntry.COL_MOVIE_ID + " = ? AND " +
                                VideoEntry.COL_VIDEO_KEY   + " = ?"),
                        new String[]{movie_id, video_key},
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Query: Unknown uri " + uri);
        }

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
}
