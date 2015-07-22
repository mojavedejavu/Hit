package com.example.xfang.popularmovies.model;

import android.media.Image;
import android.net.Uri;

import org.w3c.dom.Text;

import java.util.Date;

/**
 * Created by xfang on 7/19/15.
 */
public class Movie {

    public static int API_POSTER_SIZE = 185;
    String title;
    String imagePath;
    String plot;
    double rating;
    String date;

    public Movie(String title, String imagePath, String plot, double rating, String date){
        this.title = title;
        this.imagePath = imagePath;
        this.plot = plot;
        this.rating = rating;
        this.date = date;
    }

    public String toString(){
        return title + imagePath + plot + rating + date;
    }

    public Uri getUri(String size){

        String baseUrl = "http://image.tmdb.org/t/p/";
        Uri uri = Uri.parse(baseUrl).buildUpon().appendPath(size).appendEncodedPath(this.imagePath).build();

        return uri;
    }

}
