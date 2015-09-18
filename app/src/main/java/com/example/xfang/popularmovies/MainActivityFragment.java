package com.example.xfang.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.xfang.popularmovies.model.Movie;
import com.example.xfang.popularmovies.data.MovieContract.MovieEntry;

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
public class MainActivityFragment extends Fragment {

    final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    View mRootView;
    ImageAdapter mAdapter;

    public MainActivityFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        initGridView();

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity(), mAdapter);
        fetchMoviesTask.execute();

        return mRootView;
    }

    public void initGridView(){
        mAdapter = new ImageAdapter(getActivity());
        GridView gridView = (GridView) mRootView.findViewById(R.id.movies_grid_view);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageAdapter imageAdapter = (ImageAdapter) parent.getAdapter();
                Movie movie = imageAdapter.getItem(position);

                if (movie == null){
                    return;
                }
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra(Movie.EXTRA_MOVIE, movie.toBundle());
                startActivity(intent);
            }
        });
    }
}
