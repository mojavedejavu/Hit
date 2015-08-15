package com.example.xfang.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import com.example.xfang.popularmovies.data.MovieContract.MovieEntry;
import com.example.xfang.popularmovies.data.MovieContract.VideoEntry;

/**
 * Created by xfang on 8/14/15.
 */
public class TestUtils {

    public final static String LOG_TAG = TestUtils.class.getSimpleName();

    static ContentValues createInsideOutMovieValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieEntry.COL_MOVIE_ID, "150540");
        testValues.put(MovieEntry.COL_MOVIE_TITLE, "Inside Out");
        testValues.put(MovieEntry.COL_DATE,"2015-06-19");
        testValues.put(MovieEntry.COL_POSTERPATH,"/aAmfIX3TT40zUHGcCKrlOZRKC7u.jpg");
        testValues.put(MovieEntry.COL_PLOT,"Growing up can be a bumpy road, and it's no exception for Riley, who i…");
        testValues.put(MovieEntry.COL_RATING,"8.2");
        return testValues;
    }


    static ContentValues createInsideOutTrailerValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(VideoEntry.COL_MOVIE_ID, "150540");
        testValues.put(VideoEntry.COL_VIDEO_KEY,"_MC3XuMvsDI");
        testValues.put(VideoEntry.COL_SITE, "YouTube");
        testValues.put(VideoEntry.COL_VIDEO_TITLE,"Inside Out Trailer 2");

        return testValues;
    }

    static void validateCurrentRecord(String error, Cursor c, ContentValues expectedValues){
        Set<String> keys = expectedValues.keySet();
        for (String key: keys){
            int idx = c.getColumnIndex(key);
            assertTrue("Error: Column " + key + " not found. " + error, idx != -1);
            String found = c.getString(idx).toString();
            String expected = expectedValues.get(key).toString();
            assertEquals("Error: " + found +" doesn't match expected value " + expected + ". " + error,
                    expected, found);
        }
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static long insertInsideOutTrailerValues(Context context) {
        // insert our test records into the database
        SQLiteDatabase db = new MovieDbHelper(context).getWritableDatabase();
        ContentValues testValues = createInsideOutTrailerValues();

        long rowId;
        rowId = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Inside Out trailer Values", rowId != -1);
        Log.d(LOG_TAG, "Row id: " + rowId);

        return rowId;
    }

    static long insertInsideOutMovieValues(Context context) {
        // insert our test records into the database
        SQLiteDatabase db = new MovieDbHelper(context).getWritableDatabase();
        ContentValues testValues = createInsideOutMovieValues();

        long rowId = db.insert(MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Inside Out movie Values", rowId != -1);
        Log.d(LOG_TAG, "Row id: " + rowId);

        return rowId;
    }
}
