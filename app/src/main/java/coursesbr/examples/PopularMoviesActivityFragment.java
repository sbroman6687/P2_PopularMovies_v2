package coursesbr.examples;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;


import coursesbr.examples.data.MoviesContract;
import coursesbr.examples.sync.MoviesSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */

public class PopularMoviesActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = PopularMoviesActivityFragment.class.getSimpleName();

    public AndroidMovieAdapter movieAdapter;
    public GridView gridView;
    public View rootView;

    private static final String SELECTED_KEY = "selected_position";



    private int mPosition = GridView.INVALID_POSITION;


    private static final int CURSOR_LOADER_ID = 0;

    //For the main Gridview we are going to show only some of the data stored
    //in the database. Here I specify the columns we need

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_POSTER

    };

    //These constants correspond to the projection defined above, and must change
    //if the projection changes

    private static final int COL_MOVIE_ID = 0;
    private static final int COL_MOVIE_TITLE = 1;
    private static final int COL_MOVIE_POSTER = 2;


    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);

    }
    //private Callback mCallback;


    public PopularMoviesActivityFragment() {
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        //Cursor c =
                //getActivity().getContentResolver().query(MoviesContract.MovieEntry.CONTENT_URI,
                        //new String[]{MoviesContract.MovieEntry._ID},
                        //null,
                        //null,
                        //null);
        //if (c.getCount() == 0) {
        //}

        // initialize loader
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

    }


    private void updateMovies(){
        MoviesSyncAdapter.syncImmediately(getActivity());
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

    }

    @Override
    public void onStart(){
        super.onStart();
        this.updateMovies();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_popular_movies,container,false);

        //initialize your AndroidMovieAdapter


        movieAdapter = new AndroidMovieAdapter(getActivity(),null,0,CURSOR_LOADER_ID);
        //initialize gridView to the GridView in fragment_popular_movies.xml
        gridView = (GridView)rootView.findViewById(R.id.gridView_popularmovies);
        //set gridView adapter to our CursorAdapter

        gridView.setAdapter(movieAdapter);


        //make each item clickable
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //CursorAdapter returns a cursor at the correct position for getItem(), or null
                //if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                Log.d(LOG_TAG, "Grid view item clicked: position: " + position + " movie ID: " + cursor.getInt(COL_MOVIE_ID) + " movie title: " + cursor.getString(COL_MOVIE_TITLE) + " poster path: " + cursor.getString(COL_MOVIE_POSTER));

                if (cursor != null) {
                    //String sortbySetting = Utility.getPreferedSorting(getActivity());
                    ((Callback) getActivity()).onItemSelected(MoviesContract.MovieEntry.buildMoviesUri(cursor.getInt(COL_MOVIE_ID)));
                }
                mPosition = position;

            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }



        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }



    // Attach loader to our database query
    // run when loader is initialized
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] selectionArgs = null;

        String selectionUser = null;

        String sortcriteria = Utility.getPreferedSorting(getContext());

        //Construction of selection  that matches the word that the user entered
        if (sortcriteria.equals(getString(R.string.pref_sort_value_popular))){

            selectionUser =MoviesContract.MovieEntry.COLUMN_POPULARITY + " DESC";
            return new CursorLoader(getActivity(),
                    MoviesContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS, //projection
                    null,
                    null,
                    selectionUser);

        }else if (sortcriteria.equals(getString(R.string.pref_sort_value_toprated))){

            selectionUser = MoviesContract.MovieEntry.COLUMN_RATING + " DESC";
            return new CursorLoader(getActivity(),
                    MoviesContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS, //projection
                    null,
                    null,
                    selectionUser);

        }else{

            selectionArgs = new String [] {"" + 1};
            selectionUser = MoviesContract.MovieEntry.COLUMN_FAVOURITE + " = ?";
            return new CursorLoader(getActivity(),
                    MoviesContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS, //projection
                    selectionUser,
                    selectionArgs,
                    null);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);

        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            gridView.smoothScrollToPosition(mPosition);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

    //Additional code for tablets
    void onSortCriteriaChanged(){
        updateMovies();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

    }


}

