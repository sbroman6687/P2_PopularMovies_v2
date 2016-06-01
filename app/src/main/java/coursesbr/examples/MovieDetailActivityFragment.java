package coursesbr.examples;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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



    private static final int CURSOR_LOADER_ID = 0;

    private static final String[]DETAIL_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_MOVIES + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_POSTER,
            MoviesContract.MovieEntry.COLUMN_BACKPOSTER,
            MoviesContract.MovieEntry.COLUMN_RATING,
            MoviesContract.MovieEntry.COLUMN_RELEASE
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

    public MovieDetailActivityFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailActivityFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

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
            mMovieRating.setText(data.getString(COL_MOVIE_RATING));
            mMovieOverview.setText(data.getString(COL_MOVIE_OVERVIEW));
            String movieposter = data.getString(COL_MOVIE_POSTER);
            Picasso.with(getContext()).load(movieposter).into(mMoviePoster);
            String movieBackground = data.getString(COL_MOVIE_BACKPOSTER);
            Picasso.with(getContext()).load(movieBackground).into(mMovieBackground);
        }


    }
    // reset CursorAdapter on Loader Reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        //mDetailCursor = null;
    }

}


