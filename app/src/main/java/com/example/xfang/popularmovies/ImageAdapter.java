package com.example.xfang.popularmovies;

import android.content.Context;
import android.net.Uri;
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
    // references to our images
    private String[] eatFoodyImages = {
            "http://i.imgur.com/rFLNqWI.jpg",
            "http://i.imgur.com/C9pBVt7.jpg",
            "http://i.imgur.com/rT5vXE1.jpg",
            "http://i.imgur.com/aIy5R2k.jpg",
            "http://i.imgur.com/MoJs9pT.jpg",
            "http://i.imgur.com/S963yEM.jpg",
            "http://i.imgur.com/rLR2cyc.jpg",
            "http://i.imgur.com/SEPdUIx.jpg",
            "http://i.imgur.com/aC9OjaM.jpg",
            "http://i.imgur.com/76Jfv9b.jpg",
            "http://i.imgur.com/fUX7EIB.jpg",
            "http://i.imgur.com/syELajx.jpg",
            "http://i.imgur.com/COzBnru.jpg",
            "http://i.imgur.com/Z3QjilA.jpg",
    };

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
            imageView.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
            //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }

        Uri posterUri = movie.getUri(mContext.getString(R.string.api_poster_default_size));
        Log.d(LOG_TAG, "Image URL: " + posterUri);
        Picasso.with(mContext)
                .load(posterUri)
                .into(imageView);

        return imageView;
    }
}
