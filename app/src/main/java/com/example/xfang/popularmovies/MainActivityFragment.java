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

        String convertContentValuesToString(ContentValues values) {
                return values.getAsString(MovieEntry.COL_MOVIE_ID) + " " +
                        values.getAsString(MovieEntry.COL_MOVIE_TITLE);
        }

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
            final String API_ID = "id";

            try {
                JSONObject moviesJson = new JSONObject(JSONString);
                JSONArray moviesArray = moviesJson.getJSONArray(API_LIST);

                // Insert the new movie information into the database
                Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());


                for (int i = 0; i < moviesArray.length(); i++) {
                    // These are the values that will be collected.
                    String title;
                    String imagePath;
                    String plot;
                    double rating;
                    String date;
                    String id;

                    // Get the JSON object representing the day
                    JSONObject movie = moviesArray.getJSONObject(i);

                    title = movie.getString(API_TITLE);
                    imagePath = movie.getString(API_PATH);
                    plot = movie.getString(API_PLOT);
                    rating = movie.getDouble(API_RATING);
                    date = movie.getString(API_DATE);
                    id = movie.getString(API_ID);

                    Log.d("poster", imagePath);

                    // insert into the database
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieEntry.COL_MOVIE_TITLE, title);
                    movieValues.put(MovieEntry.COL_POSTER_PATH, imagePath);
                    movieValues.put(MovieEntry.COL_PLOT, plot);
                    movieValues.put(MovieEntry.COL_RATING, rating);
                    movieValues.put(MovieEntry.COL_DATE, date);
                    movieValues.put(MovieEntry.COL_MOVIE_ID, id);

                    cVVector.add(movieValues);
                }

                // add to database
                if ( cVVector.size() > 0 ) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    int bulkInsertCount = getActivity().getContentResolver().bulkInsert(
                            MovieEntry.CONTENT_URI, cvArray);
                }

                Cursor cur = getActivity().getContentResolver().query(MovieEntry.CONTENT_URI,
                        null, null, null, null);

                if ( cur.moveToFirst() ) {
                    do {
                        ContentValues cv = new ContentValues();
                        DatabaseUtils.cursorRowToContentValues(cur, cv);
                        Movie movieObject = new Movie(cv);
                        Log.d("poster", "reading out from db: " + movieObject.toString());
                        movies.add(movieObject);
                        Log.d(LOG_TAG, "Successfully read new movie from the database: "+ movieObject.toString());
                    } while (cur.moveToNext());
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
                final String BASE_URL =
                        "http://api.themoviedb.org/3/movie";
                final String PARAM_PAGE = "page";
                final String API_KEY = "api_key";


                String userSortPref = PreferenceManager
                        .getDefaultSharedPreferences(getActivity())
                        .getString(
                                getString(R.string.pref_sorting_key),
                                getString(R.string.pref_sorting_default_value)
                        );

                Log.d(LOG_TAG, "userSortPref: " + userSortPref);

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(userSortPref)
                        .appendQueryParameter(PARAM_PAGE, String.valueOf(1))
                        .appendQueryParameter(API_KEY, "af1cb7b82656a58d970263211175ce1f")
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

                Log.d("poster", "FINE HERE 1");
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                Log.d("poster", "FINE HERE 2");

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                Log.d("poster", "FINE HERE 3");

                String moviesJsonStr = buffer.toString();

                Log.d(LOG_TAG, moviesJsonStr);
                movies = getMoviesFromJson(moviesJsonStr);


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                e.printStackTrace();
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
