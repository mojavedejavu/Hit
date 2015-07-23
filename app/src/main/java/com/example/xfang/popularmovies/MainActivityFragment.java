package com.example.xfang.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.xfang.popularmovies.model.Movie;

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

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
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

    public class FetchMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        ArrayList<Movie> getMoviesFromJson(String JSONString) throws JSONException {
            ArrayList<Movie> movies = new ArrayList<>();

            // Now we have a String representing the complete forecast in JSON Format.
            // Fortunately parsing is easy:  constructor takes the JSON string and converts it
            // into an Object hierarchy for us.

            // These are the names of the JSON objects that need to be extracted.

            // Location information
            final String API_LIST = "results";
            final String API_TITLE = "original_title";
            final String API_PATH = "poster_path";
            final String API_PLOT = "overview";
            final String API_RATING = "vote_average";
            final String API_DATE = "release_date";

            try {
                JSONObject moviesJson = new JSONObject(JSONString);
                JSONArray moviesArray = moviesJson.getJSONArray(API_LIST);

                for (int i = 0; i < moviesArray.length(); i++) {
                    // These are the values that will be collected.
                    String title;
                    String imagePath;
                    String plot;
                    double rating;
                    String date;

                    // Get the JSON object representing the day
                    JSONObject movie = moviesArray.getJSONObject(i);

                    title = movie.getString(API_TITLE);
                    imagePath = movie.getString(API_PATH);
                    plot = movie.getString(API_PLOT);
                    rating = movie.getDouble(API_RATING);
                    date = movie.getString(API_DATE);

                    Movie movieObject = new Movie(title, imagePath, plot, rating, date);
                    movies.add(movieObject);
                    Log.d(LOG_TAG, "Added new movie: " + movieObject.toString());
                }

                Log.d(LOG_TAG, "FetchMoviesTask Complete. ");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }


            return movies;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                mAdapter.clear();
                mAdapter.addAll(movies);
                // New data is back from the server.  Hooray!
            }
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... params){
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            ArrayList<Movie> movies = new ArrayList<>();

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, "popularity.desc")
                        .appendQueryParameter(API_KEY_PARAM, "af1cb7b82656a58d970263211175ce1f")
                        .build();

                URL url = new URL(builtUri.toString());

                Log.d(LOG_TAG, "URL is: " + url);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                String moviesJsonStr = buffer.toString();

                Log.d(LOG_TAG, moviesJsonStr);
                movies = getMoviesFromJson(moviesJsonStr);


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return movies;
        }

    }
}
