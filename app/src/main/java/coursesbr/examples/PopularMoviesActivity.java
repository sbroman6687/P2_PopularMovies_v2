package coursesbr.examples;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

public class PopularMoviesActivity extends AppCompatActivity implements PopularMoviesActivityFragment.Callback {

    private boolean mTwoPane;
    private String mSortCriteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSortCriteria = Utility.getPreferedSorting(this);

        //si comento estas lineas se rellena mi grid
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                //new PopularMoviesActivityFragment()).commit();

        if (findViewById(R.id.movie_detail_container)!=null){
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container,new MovieDetailActivityFragment())
                        .commit();

            }
        } else {
            mTwoPane = false;
        }

        // Stetho is a tool created by facebook to view your database in chrome inspect.
        // The code below integrates Stetho into your app. More information here:
        // http://facebook.github.io/stetho/
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());

        ///////////////


    }

    @Override
    protected void onResume(){
        super.onResume();
        String sortCriteria = Utility.getPreferedSorting(this);

        if (sortCriteria != null && !sortCriteria.equals(mSortCriteria)){
            PopularMoviesActivityFragment ff = (PopularMoviesActivityFragment)getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
            if (null != ff){
                ff.onSortCriteriaChanged();
            }
            mSortCriteria = sortCriteria;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popular_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri dateUri) {

        if (mTwoPane){
            // In two-pane mode, show the detail view in this activity by
//             adding or replacing the detail fragment using a
//             fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailActivityFragment.DETAIL_URI, dateUri);
            //args.putInt(MovieDetailActivityFragment.MOVIE_ID, MovieId);

            MovieDetailActivityFragment fragment = new MovieDetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container,fragment)
                    .commit();

        }else{
            Intent intent = new Intent(this,MovieDetailActivity.class).setData(dateUri);
            startActivity(intent);
        }

    }
}
