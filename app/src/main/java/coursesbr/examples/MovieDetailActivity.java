package coursesbr.examples;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import coursesbr.examples.data.MoviesContract;
import coursesbr.examples.data.MoviesDBHelper;
import coursesbr.examples.data.MoviesProvider;

public class MovieDetailActivity extends AppCompatActivity {

    Context context = this;
    MoviesDBHelper moviesDBHelper;
    SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        AddFavouriteMovie();

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailActivityFragment.DETAIL_URI, getIntent().getData());

            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    public void AddFavouriteMovie(){
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Add to Favourites", Snackbar.LENGTH_LONG)
                        //.setAction("Action", null).show();

                //Poner el Id de la favouriteMovie Aqui

                String favouriteMovieId = MovieDetailActivityFragment.getFavouriteId();

                moviesDBHelper = new MoviesDBHelper(context);
                sqLiteDatabase = moviesDBHelper.getWritableDatabase();
                //Add favourites into the database
                moviesDBHelper.addFavourite(favouriteMovieId,sqLiteDatabase);

                Toast.makeText(MovieDetailActivity.this, "Favourite  Saved", Toast.LENGTH_SHORT).show();

                moviesDBHelper.close();

            }
        });

    }

}

