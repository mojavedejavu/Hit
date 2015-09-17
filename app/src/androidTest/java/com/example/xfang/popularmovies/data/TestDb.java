package com.example.xfang.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

import com.example.xfang.popularmovies.data.MovieContract.MovieEntry;
import com.example.xfang.popularmovies.data.MovieContract.VideoEntry;

/**
 * Created by xfang on 8/14/15.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DB_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb(){
        HashSet<String> tableNamesSet = new HashSet<>();
        tableNamesSet.add(MovieEntry.TABLE_NAME);
        tableNamesSet.add(VideoEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DB_NAME);
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        assertTrue("Database is not open", db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' ", null);
        assertTrue("Error: Database has not been created correctly",
                c.moveToFirst());

        while (!c.isAfterLast()){
            tableNamesSet.remove(c.getString(0));
            c.moveToNext();
        }
        assertTrue("Error: Database was created without correct tables",
                tableNamesSet.isEmpty());

        // test columns in movieEntry table
        HashSet<String> movieColumnsSet = new HashSet<>();
        movieColumnsSet.add(MovieEntry.COL_DATE);
        movieColumnsSet.add(MovieEntry.COL_POSTERPATH);
        movieColumnsSet.add(MovieEntry.COL_MOVIE_ID);
        movieColumnsSet.add(MovieEntry.COL_MOVIE_TITLE);
        movieColumnsSet.add(MovieEntry.COL_PLOT);
        movieColumnsSet.add(MovieEntry.COL_RATING);

        c = db.rawQuery("PRAGMA table_info(" + MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: We failed to query database for movie table info.",
                c.moveToFirst());

        int columnNameIndex = c.getColumnIndex("name");

        while( !c.isAfterLast()){
            String columnName = c.getString(columnNameIndex);
            movieColumnsSet.remove(columnName);
            c.moveToNext();
        }

        assertTrue("Error: Db doesn't contain all required columns for the movie table",
                movieColumnsSet.isEmpty());

        // repeat the same test for video table
        HashSet<String> videoColumnsSet = new HashSet<>();
        videoColumnsSet.add(VideoEntry.COL_MOVIE_ID);
        videoColumnsSet.add(VideoEntry.COL_SITE);
        videoColumnsSet.add(VideoEntry.COL_VIDEO_KEY);
        videoColumnsSet.add(VideoEntry.COL_VIDEO_TITLE);

        c = db.rawQuery("PRAGMA table_info(" + VideoEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: We failed to query database for video table info.",
                c.moveToFirst());

        columnNameIndex = c.getColumnIndex("name");

        while( !c.isAfterLast()){
            String columnName = c.getString(columnNameIndex);
            videoColumnsSet.remove(columnName);
            c.moveToNext();
        }

        assertTrue("Error: Db doesn't contain all required columns for the video table",
                videoColumnsSet.isEmpty());

        db.close();
    }

    public void testVideoTable(){
        // insert our test records into the database
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        ContentValues testValues = TestUtilities.createInsideOutTrailerValues();

        long rowId;
        rowId = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Inside Out trailer Values", rowId != -1);
        Log.d(LOG_TAG, "Row id: " + rowId);

        Cursor c = db.query(
                VideoEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        assertTrue("Error: no records returned from querying the video table",
                c.moveToFirst());

        TestUtilities.validateCurrentRecord("", c, testValues);

        assertFalse("Error: Video table has more than one record.", c.moveToNext());
        c.close();
        db.close();
    }

    public void testMovieTable(){
        // insert our test records into the database
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        ContentValues testValues = TestUtilities.createInsideOutMovieValues();

        long rowId;
        rowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Inside Out trailer Values", rowId != -1);
        Log.d(LOG_TAG, "Row id: " + rowId);

        Cursor c = db.query(
                MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        assertTrue("Error: no records returned from querying the movie table",
                c.moveToFirst());

        TestUtilities.validateCurrentRecord("", c, testValues);

        assertFalse("Error: Movie table has more than one record.", c.moveToNext());
        c.close();
        db.close();
    }



}
