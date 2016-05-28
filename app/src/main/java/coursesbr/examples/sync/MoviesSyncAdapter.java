package coursesbr.examples.p2popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import coursesbr.examples.p2popularmovies.AndroidMovie;
import coursesbr.examples.p2popularmovies.BuildConfig;
import coursesbr.examples.p2popularmovies.R;
import coursesbr.examples.p2popularmovies.Utility;
import coursesbr.examples.p2popularmovies.data.MoviesContract;

/**
 * Created by Soledad on 5/25/2016.
 */
public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the movies, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final int MOVIES_NOTIFICATION_ID = 3004;

    public String sortValue;

    private static final String[] NOTIFY_MOVIES_PROJECTION = new String[]{
            MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_POSTER,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW

    };
    // these indices must match the projection
    private static final int INDEX_ID = 0;
    private static final int INDEX_COLUMN_TITLE = 1;
    private static final int INDEX_COLUMN_POSTER = 2;
    private static final int INDEX_COLUMN_OVERVIEW = 3;

    public MoviesSyncAdapter(Context context, boolean autoInitialize){
        super(context, autoInitialize);
    }
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult){
        Log.d(LOG_TAG, "Starting sync");
        String popularMoviesQuery = Utility.getPreferedSorting(getContext());

        //If statement to sort the movies
        if (popularMoviesQuery.equals(getContext().getString(R.string.pref_sort_value_popular))){
            sortValue = "popularity.desc";
        }else{
            sortValue = "vote_average.desc";
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        try{
            //Construct the URL for the Popular Movies query
            //Possible parameters are available at https://www.themoviedb.org/documentation/api/discover


            final String MOVIES_BASE_URL ="http://api.themoviedb.org/3/discover/movie/";
            final String SORT_PARAM = sortValue;
            final String SORT_BY = "sort_by";
            final String APIKEY_PARAM = "api_key";
            final String API_PAGE = "page";

            Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                    .appendQueryParameter(API_PAGE, "1")
                    .appendQueryParameter(SORT_BY,sortValue)
                    .appendQueryParameter(APIKEY_PARAM, BuildConfig.OPEN_POPULAR_MOVIES_API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            //Create the request to themoviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                movieJsonStr = null;

            }else{
                reader = new BufferedReader(new InputStreamReader(inputStream));
            }

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            movieJsonStr = buffer.toString();
            getMovieDataFromJson(movieJsonStr,popularMoviesQuery);
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
        return;
    }

    /**
     * Take the String representing the complete movies data in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getMovieDataFromJson(String movieJsonStr,String movieSetting) throws JSONException{
        //These are the names of the JSON object that need to be extracted
        final String OWM_RESULTS = "results";
        final String OWM_POSTER_PATH = "poster_path";
        final String OWM_ORIGINAL_TITLE = "original_title";
        final String OWM_SYPNOSIS = "overview";
        final String OWM_MOVIE_RELEASE = "release_date";
        final String OWM_MOVIE_RATING = "vote_average";
        final String OWM_BACKDROP = "backdrop_path";
        final String OWM_MOVIE_ID = "id";

        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULTS);

            //Insert new movie data information into the database
            Vector<ContentValues>cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i<movieArray.length();i++){
                //AndroidMovie[] resultStr = new AndroidMovie[i];
                String img_path;
                String title;
                String sypnosis;
                String movie_release;
                String movie_rating;
                String movie_backdrop;
                int movie_id;

                JSONObject moviedata = movieArray.getJSONObject(i);
                img_path="http://image.tmdb.org/t/p/w500/" + moviedata.getString(OWM_POSTER_PATH);
                title=moviedata.getString(OWM_ORIGINAL_TITLE);
                sypnosis = moviedata.getString(OWM_SYPNOSIS);
                movie_release=moviedata.getString(OWM_MOVIE_RELEASE);
                movie_rating=moviedata.getString(OWM_MOVIE_RATING);
                movie_backdrop= "http://image.tmdb.org/t/p/w780/" + moviedata.getString(OWM_BACKDROP);
                movie_id = moviedata.getInt(OWM_MOVIE_ID);


                ContentValues movieValues = new ContentValues();

                movieValues.put(MoviesContract.MovieEntry._ID,movie_id);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE,title);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER,img_path);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW,sypnosis);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_BACKPOSTER,movie_backdrop);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_RATING,movie_rating);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE,movie_release);

                cVVector.add(movieValues);
            }
            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);

                // delete old data so we don't build up an endless history
                //getContext().getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,)

            }

            Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }
    //Notifications
    //private void notifyMovies(){

    //}////

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }
    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
