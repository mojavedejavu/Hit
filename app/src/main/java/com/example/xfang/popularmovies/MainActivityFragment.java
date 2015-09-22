package com.example.xfang.popularmovies;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.support.v4.app.Fragment;
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

    //private CursorLoader
    private static final int MOVIES_LOADER_ID = 0;

    View mRootView;
    ImageAdapter mAdapter;

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
                null,
                null,
                null,
                null
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

        GridView gridView = (GridView) mRootView.findViewById(R.id.movies_grid_view);
        gridView.setAdapter(mAdapter);

//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ImageAdapter imageAdapter = (ImageAdapter) parent.getAdapter();
//                Movie movie = imageAdapter.getItem(position);
//
//                if (movie == null){
//                    return;
//                }
//                Intent intent = new Intent(getActivity(),DetailActivity.class);
//                intent.putExtra(Movie.EXTRA_MOVIE, movie.toBundle());
//                startActivity(intent);
//            }
//        });

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity());
        fetchMoviesTask.execute();

        return mRootView;
    }
}
