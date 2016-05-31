package coursesbr.examples;


import android.app.Activity;
import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


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
                    mCallback.onItemSelected(MoviesContract.MovieEntry.buildMoviesUri(cursor.getInt(COL_MOVIE_ID)));
                }
                mPosition = position;

            }
        });

        return rootView;
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);

    }
    private Callback mCallback;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if (activity instanceof Callback){
            mCallback = (Callback) activity;
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mCallback = null;
    }



    // Attach loader to our flavors database query
    // run when loader is initialized
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //aqui probablemente haya que hacer algo para el sort. Mirar el ejemplo y sunshine mas adelante

        String sortrated = MoviesContract.MovieEntry.COLUMN_RATING + " DESC";

        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS, //projection
                null,
                null,
                sortrated);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

}

