package com.example.xfang.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import java.util.*;

import com.example.xfang.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by xfang on 7/19/15.
 */
public class ImageAdapter extends BaseAdapter {

    final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<Movie> mMovies;
    private final int mHeight;
    private final int mWidth;

    public ImageAdapter(Context c) {
        mContext = c;
        mMovies = new ArrayList<>();
        mHeight = Math.round(mContext.getResources().getDimension(R.dimen.poster_height));
        mWidth = Math.round(mContext.getResources().getDimension(R.dimen.poster_width));

    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Movie getItem(int position) {
        if (position < 0 || position >= mMovies.size()) {
            return null;
        }
        return mMovies.get(position);
    }


    @Override
    public long getItemId(int position) {
//        Movie movie = (Movie) getItem(position);
//        if (movie == null) {
//            return -1L;
//        }
//
//        return movie.id;
        return 47;
    }

    public void clear(){
        mMovies.clear();
    }

    public void addAll(Collection<Movie> movies){
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        if (movie == null) {
            return null;
        }

        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            DisplayMetrics displayMetrics=mContext.getResources().getDisplayMetrics();
            int screen_width=displayMetrics.widthPixels;    //width of the device screen
            int view_width=screen_width/2;   //width for imageview
            int view_height = (int) Math.round(view_width * 1.5);
            Log.d(LOG_TAG, "screen width: "+ screen_width + " view_width: " + view_width +
                    "mWidth: " + mWidth);

            imageView.setLayoutParams(new GridView.LayoutParams(view_width, view_height));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) convertView;
        }

        Uri posterUri = movie.getUri("w" + Movie.API_POSTER_SIZE);
        Log.d(LOG_TAG, "Image URL: " + posterUri);
        Picasso.with(mContext)
                .load(posterUri)
                .into(imageView);

        return imageView;
    }
}
