package com.example.xfang.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.support.v4.content.CursorLoader;

import com.example.xfang.popularmovies.data.MovieContract.MovieEntry;
import com.example.xfang.popularmovies.data.MovieContract.VideoEntry;

import com.example.xfang.popularmovies.model.Movie;
import com.example.xfang.popularmovies.model.Video;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int VIDEOS_LOADER_ID = 0;

    CursorAdapter mVideosAdapter;

    final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    String mMovieId;

    TextView mTitleView;
    ImageView mImageView;
    TextView mDateView;
    TextView mRatingView;
    TextView mPlotView;
    ListView mVideosView;

    private String[] MOVIE_DETAIL_ACTIVITY_MOVIE_COLUMNS = {
            MovieEntry._ID,
            MovieEntry.COL_MOVIE_TITLE,
            MovieEntry.COL_POSTER_PATH,
            MovieEntry.COL_MOVIE_ID,
            MovieEntry.COL_DATE,
            MovieEntry.COL_PLOT,
            MovieEntry.COL_RATING
    };

    // NOTE: These are tied to MOVIE_DETAIL_ACTIVITY_MOVIE_COLUMNS. If MOVIE_DETAIL_ACTIVITY_MOVIE_COLUMNS changes,
    // these need to change too.
    public static final int ID_COL_ID = 0;
    public static final int ID_COL_MOVIE_TITLE = 1;
    public static final int ID_COL_POSTER_PATH = 2;
    public static final int ID_COL_MOVIE_ID = 3;
    public static final int ID_COL_DATE = 4;
    public static final int ID_COL_PLOT = 5;
    public static final int ID_COL_RATING = 6;

    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        // TODO: change it to only query for user_pref (popular/top-rated)
        return new CursorLoader(
                getActivity(),
                VideoEntry.buildVideoUriWithMovieId(mMovieId),
                null, // projection
                null,
                null,
                null
        );
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        mVideosAdapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader){
        mVideosAdapter.swapCursor(null);
    }


    public DetailActivityFragment() {
    }

    public void onActivityCreated (Bundle savedInstanceState){
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(VIDEOS_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        mImageView = (ImageView) rootView.findViewById(R.id.detail_image);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date);
        mRatingView = (TextView) rootView.findViewById(R.id.detail_rating);
        mPlotView = (TextView) rootView.findViewById(R.id.detail_plot);
        mVideosView = (ListView) rootView.findViewById(R.id.detail_videos);


        Intent intent = getActivity().getIntent();
        if (intent!= null){
            //Bundle bundle = intent.getBundleExtra(Movie.EXTRA_MOVIE);
            Uri movie_uri = Uri.parse(intent.getStringExtra(Movie.EXTRA_MOVIE_URI));
            Cursor cursor = getActivity().getContentResolver().query(
                    movie_uri,
                    MOVIE_DETAIL_ACTIVITY_MOVIE_COLUMNS, //projection
                    null,
                    null,
                    null);
            //Movie movie = new Movie(bundle);
            cursor.moveToFirst();
            mTitleView.setText(cursor.getString(ID_COL_MOVIE_TITLE));
            mDateView.setText(cursor.getString(ID_COL_DATE));
            mRatingView.setText(String.valueOf(cursor.getDouble(ID_COL_RATING)));
            mPlotView.setText(cursor.getString(ID_COL_PLOT));

            mMovieId = cursor.getString(ID_COL_MOVIE_ID);

            Uri posterUri = Movie.getPosterUri(cursor, "w" + Movie.API_POSTER_SIZE);
            Picasso.with(getActivity())
                    .load(posterUri)
                    .into(mImageView);

        }

        // init and fetch videos
        initVideosView();
        FetchVideosTask fetchVideosTask = new FetchVideosTask(getActivity(), mMovieId);
        fetchVideosTask.execute();

        return rootView;
    }

    public void initVideosView(){
        //mVideosAdapter = new ArrayAdapter<Video>(getActivity(), R.layout.video_item, R.id.textview_video_item);
        mVideosAdapter = new CursorAdapter(getActivity(), null, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView tv = (TextView) view.findViewById(R.id.textview_video_item);
                int ID_COL_VIDEO_TITLE = cursor.getColumnIndex(VideoEntry.COL_VIDEO_TITLE);
                tv.setText(cursor.getString(ID_COL_VIDEO_TITLE));
            }
        };

        mVideosView.setAdapter(mVideosAdapter);

        mVideosView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Video video = mVideosAdapter.getItem(position);
                Cursor cursor = (Cursor) mVideosView.getItemAtPosition(position);

                if (cursor != null) {
                    int video_key_id = cursor.getColumnIndex(VideoEntry.COL_VIDEO_KEY);
                    String video_key = cursor.getString(video_key_id);
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video_key));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("www.youtube.com/watch?v=" + video_key));
                        startActivity(intent);
                    }
                }

            }
        });

    }
}
