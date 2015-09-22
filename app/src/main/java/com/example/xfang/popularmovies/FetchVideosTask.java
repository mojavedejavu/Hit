package com.example.xfang.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.xfang.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by xfang on 9/22/15.
 */
public class FetchVideosTask extends AsyncTask<Void, Void, Void> {

    String LOG_TAG = FetchVideosTask.class.getSimpleName();

    Context mContext;
    String mMovieId;

    public FetchVideosTask(Context c, String movieId){
        mContext = c;
        mMovieId = movieId;
    }

    @Override
    protected Void doInBackground(Void... params){
        //ArrayList<Video> videos = new ArrayList<>();

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
            getVideosFromJsonString(videosJsonString);


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
        return null;
    }

    public void getVideosFromJsonString(String jsonString) throws JSONException{
        try{

            final String API_ARRAY = "results";
            final String API_VIDEO_NAME = "name";
            final String API_VIDEO_KEY = "key";
            final String API_VIDEO_SITE = "site";

            JSONObject videosJson = new JSONObject(jsonString);
            JSONArray videosJsonArray = videosJson.getJSONArray(API_ARRAY);

            int numVideos = videosJsonArray.length();
            Vector<ContentValues> cvVector = new Vector<>(numVideos);
            for (int i = 0; i < numVideos; i++){
                JSONObject videoObject = videosJsonArray.getJSONObject(i);
                String videoTitle = videoObject.getString(API_VIDEO_NAME);
                String key = videoObject.getString(API_VIDEO_KEY);
                String site = videoObject.getString(API_VIDEO_SITE);

                ContentValues videoValues = new ContentValues();
                videoValues.put(MovieContract.VideoEntry.COL_VIDEO_KEY, key);
                videoValues.put(MovieContract.VideoEntry.COL_MOVIE_ID, mMovieId);
                videoValues.put(MovieContract.VideoEntry.COL_SITE, site);
                videoValues.put(MovieContract.VideoEntry.COL_VIDEO_TITLE, videoTitle);
                cvVector.add(videoValues);
                Log.d(LOG_TAG, "Added new video: " + videoTitle);
            }

            // add to database
            int bulkInsertCount = 0;
            ContentValues[] cvArray = new ContentValues[numVideos];
            cvVector.toArray(cvArray);
            if (cvArray.length != 0) {
                bulkInsertCount = mContext.getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, cvArray);
            }
            Log.d(LOG_TAG, "FetchVideosTask Complete. " + bulkInsertCount + " videos inserted");

        }
        catch(JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
