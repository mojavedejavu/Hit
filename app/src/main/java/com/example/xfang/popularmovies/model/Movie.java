package com.example.xfang.popularmovies.model;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;

import com.example.xfang.popularmovies.data.MovieContract.MovieEntry;


/**
 * Created by xfang on 7/19/15.
 */
public class Movie {

    public static final String EXTRA_MOVIE ="com.example.xfang.movie";

//    public static final String KEY_TITLE = "title";
//    public static final String KEY_POSTERPATH = "poster_path";
//    public static final String KEY_PLOT = "plot";
//    public static final String KEY_RATING = "rating";
//    public static final String KEY_DATE = "date";
//    public static final String KEY_ID = "movieId";

    public static int API_POSTER_SIZE = 185;
    public String title;
    public String posterPath;
    public String plot;
    public double rating;
    public String date;
    public String movieId;

    public Movie(String title, String posterPath, String plot, double rating, String date, String movieId){
        this.title = title;
        this.posterPath = posterPath;
        this.plot = plot;
        this.rating = rating;
        this.date = date;
        this.movieId = movieId;
    }

    public Movie(Bundle bundle){
        this.title = bundle.getString(MovieEntry.COL_MOVIE_TITLE);
        this.posterPath = bundle.getString(MovieEntry.COL_POSTER_PATH);
        this.plot = bundle.getString(MovieEntry.COL_PLOT);
        this.rating = bundle.getDouble(MovieEntry.COL_RATING);
        this.date = bundle.getString(MovieEntry.COL_DATE);
        this.movieId = bundle.getString(MovieEntry.COL_MOVIE_ID);
    }

    public Movie(ContentValues cv){
        this.title = cv.getAsString(MovieEntry.COL_MOVIE_TITLE);
        this.posterPath = cv.getAsString(MovieEntry.COL_POSTER_PATH);
        this.plot = cv.getAsString(MovieEntry.COL_PLOT);
        this.rating = cv.getAsDouble(MovieEntry.COL_RATING);
        this.date = cv.getAsString(MovieEntry.COL_DATE);
        this.movieId = cv.getAsString(MovieEntry.COL_MOVIE_ID);
    }

    public String toString(){
        return movieId + " " + title + " " + posterPath + " " + plot + " " + rating + " " + date;
    }

    public Uri getUri(String size){

        String baseUrl = "http://image.tmdb.org/t/p/";
        Uri uri = Uri.parse(baseUrl).buildUpon().appendPath(size).appendEncodedPath(this.posterPath).build();

        return uri;
    }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putString(MovieEntry.COL_MOVIE_TITLE, title);
        bundle.putString(MovieEntry.COL_POSTER_PATH, posterPath);
        bundle.putString(MovieEntry.COL_PLOT, plot);
        bundle.putDouble(MovieEntry.COL_RATING, rating);
        bundle.putString(MovieEntry.COL_DATE, date);
        bundle.putString(MovieEntry.COL_MOVIE_ID, movieId);

        return bundle;
    }

}
