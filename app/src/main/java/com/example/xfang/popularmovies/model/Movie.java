package com.example.xfang.popularmovies.model;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import org.w3c.dom.Text;

import java.util.Date;

/**
 * Created by xfang on 7/19/15.
 */
public class Movie {

    public static final String EXTRA_MOVIE ="com.example.xfang.movie";

    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGEPATH = "imagepath";
    public static final String KEY_PLOT = "plot";
    public static final String KEY_RATING = "rating";
    public static final String KEY_DATE = "date";

    public static int API_POSTER_SIZE = 185;
    public String title;
    public String imagePath;
    public String plot;
    public double rating;
    public String date;

    public Movie(String title, String imagePath, String plot, double rating, String date){
        this.title = title;
        this.imagePath = imagePath;
        this.plot = plot;
        this.rating = rating;
        this.date = date;
    }

    public Movie(Bundle bundle){
        this.title = bundle.getString(KEY_TITLE);
        this.imagePath = bundle.getString(KEY_IMAGEPATH);
        this.plot = bundle.getString(KEY_PLOT);
        this.rating = bundle.getDouble(KEY_RATING);
        this.date = bundle.getString(KEY_DATE);
    }

    public String toString(){
        return title + imagePath + plot + rating + date;
    }

    public Uri getUri(String size){

        String baseUrl = "http://image.tmdb.org/t/p/";
        Uri uri = Uri.parse(baseUrl).buildUpon().appendPath(size).appendEncodedPath(this.imagePath).build();

        return uri;
    }

    public Bundle toBundle(){
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_IMAGEPATH, imagePath);
        bundle.putString(KEY_PLOT, plot);
        bundle.putDouble(KEY_RATING, rating);
        bundle.putString(KEY_DATE, date);

        return bundle;
    }

}
