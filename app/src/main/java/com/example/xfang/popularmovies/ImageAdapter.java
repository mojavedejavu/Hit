package com.example.xfang.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import java.util.*;

import com.example.xfang.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

/**
 * Created by xfang on 7/19/15.
 */
public class ImageAdapter extends CursorAdapter {

    final String LOG_TAG = ImageAdapter.class.getSimpleName();

    //private Context mContext;
    //private ArrayList<Movie> mMovies;
    private final int mHeight;
    private final int mWidth;


//    public ImageAdapter(Context c) {
//        mContext = c;
//        mMovies = new ArrayList<>();
//        mHeight = Math.round(mContext.getResources().getDimension(R.dimen.poster_height));
//        mWidth = Math.round(mContext.getResources().getDimension(R.dimen.poster_width));
//
//    }

    public ImageAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mHeight = Math.round(context.getResources().getDimension(R.dimen.poster_height));
        mWidth = Math.round(context.getResources().getDimension(R.dimen.poster_width));

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.movie_image_view);
        DisplayMetrics displayMetrics= context.getResources().getDisplayMetrics();
        int screen_width=displayMetrics.widthPixels;    //width of the device screen
        int view_width=screen_width/2;   //width for imageview
        int view_height = (int) Math.round(view_width * 1.5);
        Log.d(LOG_TAG, "screen width: "+ screen_width + " view_width: " + view_width +
                "mWidth: " + mWidth);

        imageView.setLayoutParams(new GridView.LayoutParams(view_width, view_height));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Uri posterUri = Movie.getPosterUri(cursor, "w" + Movie.API_POSTER_SIZE);
        Log.d(LOG_TAG, "Image URL: " + posterUri);
        Picasso.with(context)
                .load(posterUri)
                .into(imageView);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        return view;
    }

//    @Override
//    public int getCount() {
//        return mMovies.size();
//    }

//    @Override
//    public Movie getItem(int position) {
//        if (position < 0 || position >= mMovies.size()) {
//            return null;
//        }
//        return mMovies.get(position);
//    }


    @Override
    public long getItemId(int position) {
//        Movie movie = (Movie) getItem(position);
//        if (movie == null) {
//            return -1L;
//        }
//
//        return movie.movieId;
        return 47;
    }

//    public void clear(){
//        mMovies.clear();
//    }
//
//    public void addAll(Collection<Movie> movies){
//        mMovies.addAll(movies);
//        notifyDataSetChanged();
//    }
}
