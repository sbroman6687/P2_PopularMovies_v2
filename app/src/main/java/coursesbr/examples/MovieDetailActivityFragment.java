package coursesbr.examples;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;

import coursesbr.examples.data.MoviesContract;

public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    //static final String MOVIE_ID = "ID";
    static final String DETAIL_URI = "URI";
    //private Cursor mDetailCursor;
    private int mPosition;
    private Uri mUri;

    private ImageView mMoviePoster;
    private ImageView mMovieBackground;
    private TextView mMovieTitle;
    private TextView mMovieOverview;
    private TextView mMovieRating;
    private TextView mMovieRelease;

    public boolean mIsFavourite;

    //Strings required for reviews
    public String MovieId;
    public ReviewsAdapter reviewsadapter;
    public ListView ListReviews;
    public View rootView;

    //Strings required for trailers
    public ListView ListTrailers;
    public TrailersAdapter trailersadapter;

    private static final int CURSOR_LOADER_ID = 0;

    private static final String[]DETAIL_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_POSTER,
            MoviesContract.MovieEntry.COLUMN_BACKPOSTER,
            MoviesContract.MovieEntry.COLUMN_RATING,
            MoviesContract.MovieEntry.COLUMN_RELEASE,
            MoviesContract.MovieEntry.COLUMN_FAVOURITE

    };
    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_OVERVIEW = 2;
    private static final int COL_MOVIE_POSTER = 3;
    private static final int COL_MOVIE_BACKPOSTER = 4;
    private static final int COL_MOVIE_RATING = 5;
    private static final int COL_MOVIE_RELEASE = 6;
    private static final int COL_MOVIE_FAVOURITE = 7;


    public MovieDetailActivityFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    //Method for Setting the Height of the ListView (listview of trailers and listview of reviews) dynamically.
    // Source:http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
    public static void setListViewHeightBasedOnChildren(ListView listView){
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailActivityFragment.DETAIL_URI);
        }

        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mMoviePoster = (ImageView) rootView.findViewById(R.id.MoviePoster);
        mMovieTitle = (TextView) rootView.findViewById(R.id.MovieTitle);
        mMovieOverview = (TextView) rootView.findViewById(R.id.MovieSypnosis);
        mMovieRating = (TextView) rootView.findViewById(R.id.MovieRating);
        mMovieRelease = (TextView) rootView.findViewById(R.id.MovieRelease);
        mMovieBackground = (ImageView) rootView.findViewById(R.id.MovieBackdrop);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){

        if (null!= mUri){
            return new CursorLoader(getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null);
        }
        return null;
    }

    // Set the cursor in our CursorAdapter once the Cursor is loaded
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()){
            mMovieTitle.setText(data.getString(COL_MOVIE_TITLE));
            mMovieRelease.setText(data.getString(COL_MOVIE_RELEASE));
            mMovieRating.setText(data.getString(COL_MOVIE_RATING)+ "/10") ;
            mMovieOverview.setText(data.getString(COL_MOVIE_OVERVIEW));
            String movieposter = data.getString(COL_MOVIE_POSTER);
            Picasso.with(getContext()).load(movieposter).into(mMoviePoster);
            String movieBackground = data.getString(COL_MOVIE_BACKPOSTER);
            Picasso.with(getContext()).load(movieBackground).into(mMovieBackground);

            mIsFavourite = data.getInt(COL_MOVIE_FAVOURITE)> 0;

            //MovieId to be able to obatin the reviews of each movie
            MovieId = data.getString(COL_MOVIE_ID);
            this.updateReviews();
            this.updateTrailers();

        }

    }

    // reset CursorAdapter on Loader Reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        //mDetailCursor = null;
    }

    //Section of the code where I am going to try to obtain the trailers and reviews

    private void updateReviews(){
        FetchReviewsTask reviewsTask = new FetchReviewsTask();
        reviewsTask.execute(MovieId);
    }

    private void updateTrailers(){
        FetchTrailersTask trailersTask = new FetchTrailersTask();
        trailersTask.execute(MovieId);
    }


    //onStart was here. Trying other thing
    @Override
    public void onStart(){
        super.onStart();

    }

    public class FetchTrailersTask extends AsyncTask<String,Void,MovieTrailers[]>{

        public MovieTrailers[] getTrailerFromJSON(String trailerJsonStr)throws JSONException {
            final String RESULTS = "results";
            final String TRAILER_NAME = "name";
            final String TRAILER_KEY = "key";

            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(RESULTS);
            MovieTrailers[] trailer_resultStr = new MovieTrailers[trailerArray.length()];

            //Here statement to obtent the id of the movie!!!
            //Extract movie review data and build movie objects
            for (int i = 0; i < trailerArray.length(); i++) {

                String trailer_name;
                String trailer_key;

                JSONObject trailerdata = trailerArray.getJSONObject(i);

                trailer_name = trailerdata.getString(TRAILER_NAME);
                trailer_key = trailerdata.getString(TRAILER_KEY);

                MovieTrailers elem = new MovieTrailers(trailer_name, trailer_key);
                trailer_resultStr[i] = elem;

            }
            return trailer_resultStr;
        }


        @Override
        protected MovieTrailers[] doInBackground(String... params) {
            //String MovieId = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String trailerJsonStr = null;

            try{
                //Construct the url
                String URLString = null;
                URLString = "http://api.themoviedb.org/3/movie/" + MovieId + "/videos?api_key=" + BuildConfig.OPEN_POPULAR_MOVIES_API_KEY;
                //URLString = "http://api.themoviedb.org/3/movie/" + "269149" + "/videos?api_key=" + BuildConfig.OPEN_POPULAR_MOVIES_API_KEY;

                URL url = new URL(URLString);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null){
                    trailerJsonStr = null;
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
                    trailerJsonStr = null;
                }
                trailerJsonStr = buffer.toString();

            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try{
                return getTrailerFromJSON(trailerJsonStr);
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;

        }
        //postexecute
        @Override
        protected void onPostExecute(MovieTrailers[] result){
            if (result!=null){
                try{
                    trailersadapter = new TrailersAdapter(getActivity(), Arrays.asList(result));
                    ListTrailers = (ListView)rootView.findViewById(R.id.listview_trailers);
                    ListTrailers.setAdapter(trailersadapter);
                    setListViewHeightBasedOnChildren(ListTrailers);

                    //Code to be able to insert listview inside scrollview from http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
                    ListTrailers.setOnTouchListener(new View.OnTouchListener() {
                        // Setting on Touch Listener for handling the touch inside ScrollView
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // Disallow the touch request for parent scroll on touch of child view
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });

                    ListTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Context context = getActivity();
                            MovieTrailers result = trailersadapter.getItem(position);

                            String trailer_key = result.key;

                            //Intent to start the youtube video

                            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://youtube.com" + "/watch?v=" + trailer_key));
                            startActivity(youtubeIntent);

                        }
                    });

                }catch(NullPointerException e){
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(getContext(), "Nothing to show", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class FetchReviewsTask extends AsyncTask<String, Void, MovieReviews[]>{

        private MovieReviews[] getReviewsDataFromJson(String reviewJsonStr)throws JSONException{

            //these are the names of the JSON object that need to be extracted

            final String RESULTS = "results";
            final String REVIEW_ID = "id";
            final String REVIEW_AUTHOR = "author";
            final String REVIEW_CONTENT = "content";

            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray(RESULTS);

            MovieReviews[] resultStr = new MovieReviews[reviewArray.length()];

            //Here statement to obtent the id of the movie!!!
            //Extract movie review data and build movie objects
            for (int i = 0; i<reviewArray.length();i++){

                String review_id;
                String review_author;
                String review_content;

                JSONObject reviewdata = reviewArray.getJSONObject(i);

                review_id = reviewdata.getString(REVIEW_ID);
                review_author = reviewdata.getString(REVIEW_AUTHOR);
                review_content = reviewdata.getString(REVIEW_CONTENT);

                MovieReviews element = new MovieReviews(review_id,review_author,review_content);
                resultStr[i]= element;


            }
            return resultStr;

        }


        @Override
        protected MovieReviews[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewJsonStr = null;

            try{
                //Construct the url
                String URLString = null;
                URLString = "http://api.themoviedb.org/3/movie/" + MovieId + "/reviews?api_key=" + BuildConfig.OPEN_POPULAR_MOVIES_API_KEY;

                URL url = new URL(URLString);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null){
                    reviewJsonStr = null;
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
                    reviewJsonStr = null;
                }
                reviewJsonStr = buffer.toString();

            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try{
                return getReviewsDataFromJson(reviewJsonStr);
            }catch (JSONException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(MovieReviews[] result){
            if (result!=null){
                try{
                    reviewsadapter = new ReviewsAdapter(getActivity(), Arrays.asList(result));
                    ListReviews = (ListView)rootView.findViewById(R.id.listview_reviews);
                    ListReviews.setAdapter(reviewsadapter);
                    setListViewHeightBasedOnChildren(ListReviews);

                    //Code to be able to insert listview inside scrollview from http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
                    ListReviews.setOnTouchListener(new View.OnTouchListener() {
                        // Setting on Touch Listener for handling the touch inside ScrollView
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // Disallow the touch request for parent scroll on touch of child view
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });

                }catch(NullPointerException e){
                    e.printStackTrace();
                }

            }else{
                Toast.makeText(getContext(), "Nothing to show", Toast.LENGTH_SHORT).show();
            }
        }
    }






}





