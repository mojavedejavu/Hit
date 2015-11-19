package com.example.xfang.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;


import com.example.xfang.popularmovies.model.Movie;
import com.example.xfang.popularmovies.data.MovieContract.MovieEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    // CursorLoader ID
    private static final int MOVIES_LOADER_ID = 0;

    View mRootView;
    ImageAdapter mAdapter;

    // projection array, part of our query on all the movies to be displayed
    // Since we only need movie title and poster path on this screen, we only
    // ask for these so that our app runs a little bit faster.
    // NOTE: if this change, the column IDs need to change too
    private static final String[] MOVIE_MAINFRAGMENT_COLUMNS = {
            MovieEntry._ID,
            MovieEntry.COL_MOVIE_TITLE,
            MovieEntry.COL_POSTER_PATH,
            MovieEntry.COL_MOVIE_ID

    };

    // NOTE: These are tied to MOVIE_MAINFRAGMENT_COLUMNS. If MOVIE_MAINFRAGMENT_COLUMNS changes,
    // these need to change too.
    public static final int ID_COL_ID = 0;
    public static final int ID_COL_MOVIE_TITLE = 1;
    public static final int ID_COL_POSTER_PATH = 2;
    public static final int ID_COL_MOVIE_ID = 3;


    public MainActivityFragment() {

    }

    public void onActivityCreated (Bundle savedInstanceState){
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        // TODO: change it to only query for user_pref (popular/top-rated)
        return new CursorLoader(
                getActivity(),
                MovieEntry.CONTENT_URI,
                MOVIE_MAINFRAGMENT_COLUMNS, // projection
                MovieEntry.COL_SOURCE + " = ?", // selection
                new String[]{getUserSortPref()}, // selection args
                null
                );
    }

    public String getUserSortPref(){
        Context c = getActivity().getApplicationContext();
        return PreferenceManager
                .getDefaultSharedPreferences(c)
                .getString(
                        c.getString(R.string.pref_sorting_key),
                        c.getString(R.string.pref_sorting_default_value)
                );
    }


    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        mAdapter.swapCursor(data);
    }

    public void onLoaderReset(Loader<Cursor> loader){
        mAdapter.swapCursor(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        mAdapter = new ImageAdapter(getActivity(),null, 0);

        final GridView gridView = (GridView) mRootView.findViewById(R.id.movies_grid_view);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) gridView.getItemAtPosition(position);
//                ImageAdapter imageAdapter = (ImageAdapter) parent.getAdapter();
//                Movie movie = imageAdapter.getItem(position);
                if (cursor != null){
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
//                intent.putExtra(Movie.EXTRA_MOVIE, movie.toBundle());
                    Uri movieUri = MovieEntry.buildMovieUriWithMovieId(cursor.getString(ID_COL_MOVIE_ID));
                    Log.d(LOG_TAG, "Movie clicked on. Uri: " + movieUri);
                    intent.putExtra(Movie.EXTRA_MOVIE_URI, movieUri.toString());
                    startActivity(intent);
                }


            }
        });

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity());
        fetchMoviesTask.execute();

        return mRootView;
    }
}
