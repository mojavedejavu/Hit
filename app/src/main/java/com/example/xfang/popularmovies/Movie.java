package com.example.xfang.popularmovies;

import android.media.Image;

import org.w3c.dom.Text;

import java.util.Date;

/**
 * Created by xfang on 7/19/15.
 */
public class Movie {

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

}
