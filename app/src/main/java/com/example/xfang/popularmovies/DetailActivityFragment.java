package com.example.xfang.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xfang.popularmovies.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    TextView mTitleView;
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        if (intent!= null){
            Bundle bundle = intent.getBundleExtra(Movie.EXTRA_MOVIE);
            Movie movie = new Movie(bundle);
            mTitleView.setText(movie.title);
        }
        return rootView;
    }
}
