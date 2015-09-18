package com.example.xfang.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.xfang.popularmovies.data.MovieContract.MovieEntry;

import com.example.xfang.popularmovies.data.MovieContract.VideoEntry;

import java.util.Arrays;

/**
 * Created by xfang on 8/14/15.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    /*
           This helper function deletes all records from both database tables using the ContentProvider.
           It also queries the ContentProvider to make sure that the database has been successfully
           deleted, so it cannot be used until the Query and Delete functions have been written
           in the ContentProvider.
           Students: Replace the calls to deleteAllRecordsFromDB with this one after you have written
           the delete functionality in the ContentProvider.
         */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                VideoEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from movie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from video table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
       This helper function deletes all records from both database tables using the database
       functions only.  This is designed to be used to reset the state of the database until the
       delete functionality is available in the ContentProvider.
     */
    public void deleteAllRecordsFromDB() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        db.delete(MovieEntry.TABLE_NAME, null, null);
        db.delete(VideoEntry.TABLE_NAME, null, null);
        db.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {

        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
        This test doesn't touch the database.  It verifies that the ContentProvider returns
        the correct type for each type of URI that it can handle.
        Students: Uncomment this test to verify that your implementation of GetType is
        functioning correctly.
     */
    public void testGetType() {
        //  URI: content://com.example.xfang.popularmovies/movie
        // type: vnd.android.cursor.dir/com.example.xfang.popularmovies/movie
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(VideoEntry.CONTENT_URI);
        assertEquals("Error: the LocationEntry CONTENT_URI should return LocationEntry.CONTENT_TYPE",
                VideoEntry.CONTENT_TYPE, type);

        // URI: content://com.example.xfang.popularmovies/movie/211672
        String testMovieId = "211672";
        type = mContext.getContentResolver().getType(
                MovieEntry.buildMovieUriWithMovieId(testMovieId));
        assertEquals("Error: the MovieEntry CONTENT_URI with movieId should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieEntry.CONTENT_ITEM_TYPE, type);

        // URI: content://com.example.xfang.popularmovies/video/211672/Myv_Z8CReDU
        String testVideoKey = "Myv_Z8CReDU";
        type = mContext.getContentResolver().getType(
                VideoEntry.buildVideoUriWithMovieIdAndKey(testMovieId, testVideoKey));
        assertEquals("Error: the VideoEntry CONTENT_URI with movie id and video key should return VideoEntry.CONTENT_ITEM_TYPE",
                VideoEntry.CONTENT_ITEM_TYPE, type);
    }

    /*
    This test uses the database directly to insert and then uses the ContentProvider to
    read out the data.  Uncomment this test to see if your queries are
    performing correctly.
    */
    public void testBasicVideoQueries() {
        // insert our test records into the database
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues testValues = TestUtilities.createInsideOutTrailerValues();

        long rowId = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Inside Out trailer Values", rowId != -1);
        Log.d(LOG_TAG, "Row id: " + rowId);

        // Test the basic content provider query
        Cursor cursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicVideoQueries, video query", cursor, testValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Video Query did not properly set NotificationUri",
                    cursor.getNotificationUri(), VideoEntry.CONTENT_URI);
        }
    }


    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.  Uncomment this test to see if the basic query functionality
        given in the ContentProvider is working correctly.
     */
    public void testBasicMovieQuery() {
        // insert our test records into the database
        //SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues movieValues = TestUtilities.createInsideOutMovieValues();
        TestUtilities.insertInsideOutMovieValues(mContext);

        //db.close();

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", movieCursor, movieValues);
    }

    /*
    This test uses the provider to insert and then update the data. Uncomment this test to
    see if your update location is functioning correctly.
 */
    public void testUpdateReadVideoEntry() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createInsideOutTrailerValues();

        Uri uri = mContext.getContentResolver().
                insert(VideoEntry.CONTENT_URI, values);
        long rowId = ContentUris.parseId(uri);

        // Verify we got a row back.
        assertTrue(rowId != -1);
        Log.d(LOG_TAG, "New row id: " + rowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(VideoEntry._ID, rowId);
        //final String NEW_VIDEO_TITLE = "XIAOFAN IS FLYING";
        //updatedValues.put(VideoEntry.COL_VIDEO_TITLE, NEW_VIDEO_TITLE);

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor locationCursor = mContext.getContentResolver().query(VideoEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                VideoEntry.CONTENT_URI, updatedValues, VideoEntry._ID + " = ?" ,
                new String[] { Long.toString(rowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null,   // projection
                VideoEntry._ID + " = " + rowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateReadVideoEntry. Error validating video entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    // Make sure we can still delete after adding/updating stuff
    //
    // Student: Uncomment this test after you have completed writing the insert functionality
    // in your provider.  It relies on insertions with testInsertReadProvider, so insert and
    // query functionality must also be complete before this test can be used.
    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createInsideOutTrailerValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(VideoEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(VideoEntry.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating LocationEntry.",
                cursor, testValues);

        // Fantastic.  Now that we've tested VideoEntry, let's test MovieEntry
        ContentValues movieValues = TestUtilities.createInsideOutMovieValues();
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);

        Uri movieInsertUri = mContext.getContentResolver()
                .insert(MovieEntry.CONTENT_URI, movieValues);
        assertTrue(movieInsertUri != null);

        // Did our content observer get called?  Students:  If this fails, your insert weather
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating WeatherEntry insert.",
                weatherCursor, movieValues);
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our location delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        // Register a content observer for our weather delete.
        TestUtilities.TestContentObserver videoObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(VideoEntry.CONTENT_URI, true, videoObserver);

        deleteAllRecordsFromProvider();

        // Students: If either of these fail, you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
        // delete.  (only if the insertReadProvider is succeeding)
        movieObserver.waitForNotificationOrFail();
        videoObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        mContext.getContentResolver().unregisterContentObserver(videoObserver);
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

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertVideoValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];
        Arrays.fill(returnContentValues, TestUtilities.createInsideOutTrailerValues());

        return returnContentValues;
    }

    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    public void testBulkInsert() {

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertVideoValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver videoObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(VideoEntry.CONTENT_URI, true, videoObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(VideoEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        videoObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(videoObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                VideoEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating VideoEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

}
