package com.example.xfang.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xfang.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    TextView mTitleView;
    ImageView mImageView;
    TextView mDateView;
    TextView mRatingView;
    TextView mPlotView;
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        mTitleView = (TextView) rootView.findViewById(R.id.detail_title);
        mImageView = (ImageView) rootView.findViewById(R.id.detail_image);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date);
        mRatingView = (TextView) rootView.findViewById(R.id.detail_rating);
        mPlotView = (TextView) rootView.findViewById(R.id.detail_plot);

        if (intent!= null){
            Bundle bundle = intent.getBundleExtra(Movie.EXTRA_MOVIE);
            Movie movie = new Movie(bundle);

            mTitleView.setText(movie.title);
            mDateView.setText(movie.date);
            mRatingView.setText(Double.toString(movie.rating));
            mPlotView.setText(movie.plot);


            Uri posterUri = movie.getUri("w" + Movie.API_POSTER_SIZE);
            Picasso.with(getActivity())
                    .load(posterUri)
                    .into(mImageView);

        }
        return rootView;
    }
}
