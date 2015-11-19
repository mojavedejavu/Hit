package com.example.xfang.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by xfang on 8/14/15.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.xfang.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_VIDEO = "video";



    public static final class MovieEntry implements BaseColumns{
        public static String TABLE_NAME = "movie";

        public static final String COL_MOVIE_ID = "movie_id";
        public static final String COL_MOVIE_TITLE = "movie_title";
        public static final String COL_PLOT = "plot";
        public static final String COL_POSTER_PATH = "poster_path";
        public static final String COL_RATING = "rating";
        public static final String COL_DATE = "date";
        public static final String COL_SOURCE = "source";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long _id){
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildMovieUriWithMovieId(String movie_id){
            return CONTENT_URI.buildUpon().appendPath(movie_id).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }



    }

    public static final class VideoEntry implements BaseColumns{
        public static String TABLE_NAME = "video";

        public static final String COL_MOVIE_ID = "movie_id";
        public static final String COL_VIDEO_TITLE = "video_title";
        public static final String COL_SITE = "site";
        public static final String COL_VIDEO_KEY = "video_key";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static Uri buildVideoUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildVideoUriWithMovieId(String movie_id){
            return CONTENT_URI.buildUpon().appendPath(movie_id).build();
        }

        public static Uri buildVideoUriWithMovieIdAndKey(String movie_id, String video_key){
            return CONTENT_URI.buildUpon().appendPath(movie_id).appendPath(video_key).build();
        }


        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getVideoKeyFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

    }
}
