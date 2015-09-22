package com.example.xfang.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.xfang.popularmovies.data.MovieContract.MovieEntry;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    ArrayAdapter<Video> mVideosAdapter;

    final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    TextView mTitleView;
    ImageView mImageView;
    TextView mDateView;
    TextView mRatingView;
    TextView mPlotView;
    ListView mVideosView;

    String mMovieId;

    private String[] MOVIE_DETAILACTIVITY_COLUMNS = {
            MovieEntry._ID,
            MovieEntry.COL_MOVIE_TITLE,
            MovieEntry.COL_POSTER_PATH,
            MovieEntry.COL_MOVIE_ID,
            MovieEntry.COL_DATE,
            MovieEntry.COL_PLOT,
            MovieEntry.COL_RATING
    };

    // NOTE: These are tied to MOVIE_DETAILACTIVITY_COLUMNS. If MOVIE_DETAILACTIVITY_COLUMNS changes,
    // these need to change too.
    public static final int ID_COL_ID = 0;
    public static final int ID_COL_MOVIE_TITLE = 1;
    public static final int ID_COL_POSTER_PATH = 2;
    public static final int ID_COL_MOVIE_ID = 3;
    public static final int ID_COL_DATE = 4;
    public static final int ID_COL_PLOT = 5;
    public static final int ID_COL_RATING = 6;


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
        mVideosView = (ListView) rootView.findViewById(R.id.detail_videos);

        if (intent!= null){
            //Bundle bundle = intent.getBundleExtra(Movie.EXTRA_MOVIE);
            Uri movie_uri = Uri.parse(intent.getStringExtra(Movie.EXTRA_MOVIE_URI));
            Cursor cursor = getActivity().getContentResolver().query(
                    movie_uri,
                    MOVIE_DETAILACTIVITY_COLUMNS, //projection
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
        FetchVideosTask fetchVideosTask = new FetchVideosTask();
        fetchVideosTask.execute();

        return rootView;
    }

    public void initVideosView(){
        mVideosAdapter = new ArrayAdapter<Video>(getActivity(), R.layout.video_item, R.id.textview_video_item);
        mVideosView.setAdapter(mVideosAdapter);

        mVideosView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Video video = mVideosAdapter.getItem(position);

                if (video == null){
                    return;
                }
                
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.video_key));
                    startActivity(intent);
                }
                catch (ActivityNotFoundException ex){
                    Intent intent=new Intent(Intent.ACTION_VIEW,
                            Uri.parse("www.youtube.com/watch?v=" + video.video_key));
                    startActivity(intent);
                }

            }
        });

    }

    public class FetchVideosTask extends AsyncTask<Void, Void, ArrayList<Video>>{

        String LOG_TAG = FetchVideosTask.class.getSimpleName();
        @Override
        protected void onPostExecute(ArrayList<Video> videos){
            if (videos != null){
                mVideosAdapter.clear();
                mVideosAdapter.addAll(videos);
            }
        }

        @Override
        protected ArrayList<Video> doInBackground(Void... params){
            ArrayList<Video> videos = new ArrayList<>();

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try{
                final String BASE_URL = "https://api.themoviedb.org/3/movie/";
                final String PATH_VIDEOS = "videos";
                final String PARAM_API_KEY ="api_key";
                final String VALUE_API_KEY ="af1cb7b82656a58d970263211175ce1f";

                Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(mMovieId)
                        .appendPath(PATH_VIDEOS)
                        .appendQueryParameter(PARAM_API_KEY, VALUE_API_KEY)
                        .build();

                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream stream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (stream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while( (line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    return null;
                }
                String videosJsonString = buffer.toString();
                videos = getVideosFromJsonString(videosJsonString);


            }
            catch(IOException e){
                Log.e(LOG_TAG, "Error ", e);
            }
            catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            finally{
                if ( urlConnection != null){
                    urlConnection.disconnect();
                }
                if( reader != null){
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return videos;
        }

        public ArrayList<Video> getVideosFromJsonString(String jsonString) throws JSONException{
            ArrayList<Video> videos = new ArrayList<>();

            try{

                final String API_ARRAY = "results";
                final String API_VIDEO_NAME = "name";
                final String API_VIDEO_KEY = "key";
                final String API_VIDEO_SITE = "site";

                JSONObject videosJson = new JSONObject(jsonString);
                JSONArray videosJsonArray = videosJson.getJSONArray(API_ARRAY);

                for (int i = 0; i < videosJsonArray.length(); i++){
                    JSONObject videoObject = videosJsonArray.getJSONObject(i);
                    String name = videoObject.getString(API_VIDEO_NAME);
                    String key = videoObject.getString(API_VIDEO_KEY);
                    String site = videoObject.getString(API_VIDEO_SITE);

                    Video video = new Video(mMovieId, name, site, key);
                    videos.add(video);
                    Log.d(LOG_TAG, "Added new video: " + video.toStringWithVideoKey());
                }

            }
            catch(JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return videos;
        }
    }
}
