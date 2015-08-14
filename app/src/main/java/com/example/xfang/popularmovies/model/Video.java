package com.example.xfang.popularmovies.model;

/**
 * Created by xfang on 8/13/15.
 */
public class Video {

    public String movie_id;
    public String name;
    public String site;
    public String video_key;

    public Video(String id, String name, String site, String key){
        this.movie_id = id;
        this.name = name;
        this.site = site;
        this.video_key = key;
    }

    public String toString(){
        return site + ": " + name;
    }
}
