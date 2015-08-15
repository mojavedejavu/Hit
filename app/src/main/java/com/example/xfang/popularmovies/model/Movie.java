package com.example.xfang.popularmovies.model;

import android.net.Uri;
import android.os.Bundle;

/**
 * Created by xfang on 7/19/15.
 */
public class Movie {

    public static final String EXTRA_MOVIE ="com.example.xfang.movie";

    public static final String KEY_TITLE = "title";
    public static final String KEY_POSTERPATH = "posterpath";
    public static final String KEY_PLOT = "plot";
    public static final String KEY_RATING = "rating";
    public static final String KEY_DATE = "date";
    public static final String KEY_ID = "id";

    public static int API_POSTER_SIZE = 185;
    public String title;
    public String posterPath;
    public String plot;
    public double rating;
    public String date;
    public String id;

    public Movie(String title, String posterPath, String plot, double rating, String date, String id){
        this.title = title;
        this.posterPath = posterPath;
        this.plot = plot;
        this.rating = rating;
        this.date = date;
        this.id = id;
    }

    public Movie(Bundle bundle){
        this.title = bundle.getString(KEY_TITLE);
        this.posterPath = bundle.getString(KEY_POSTERPATH);
        this.plot = bundle.getString(KEY_PLOT);
        this.rating = bundle.getDouble(KEY_RATING);
        this.date = bundle.getString(KEY_DATE);
        this.id = bundle.getString(KEY_ID);
    }

    public String toString(){
        return id + " " + title + " " + posterPath + " " + plot + " " + rating + " " + date;
    }

    public Uri getUri(String size){

        String baseUrl = "http://image.tmdb.org/t/p/";
        Uri uri = Uri.parse(baseUrl).buildUpon().appendPath(size).appendEncodedPath(this.posterPath).build();

        return uri;
    }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_POSTERPATH, posterPath);
        bundle.putString(KEY_PLOT, plot);
        bundle.putDouble(KEY_RATING, rating);
        bundle.putString(KEY_DATE, date);
        bundle.putString(KEY_ID, id);

        return bundle;
    }

}
