package coursesbr.examples;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import coursesbr.examples.data.MoviesContract;

/**
 * Created by Soledad on 5/19/2016.
 */
public class AndroidMovieAdapter extends CursorAdapter {

    private static final String LOG_TAG = AndroidMovieAdapter.class.getSimpleName();
    private Context mContext;
    private static int sLoaderID;


    public static class ViewHolder{
        public final ImageView imageView;
        public final TextView textView;

        public ViewHolder(View view){

            imageView = (ImageView)view.findViewById(R.id.movieView);

            textView = (TextView)view.findViewById(R.id.titlemovie_text);
        }
    }

    public AndroidMovieAdapter(Context context, Cursor c,int flags, int loaderID){
        super(context, c, flags);
        Log.d(LOG_TAG, "AndroidMovieAdapter");
        mContext=context;
        sLoaderID=loaderID;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        int layoutId = R.layout.grid_item_movie;

        Log.d(LOG_TAG, "In new View");

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor){

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Log.d(LOG_TAG, "In bind View");

        //Read data from cursor
        //here is where we are going to insert Picasso

        int titleIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE);
        final String versionTitle = cursor.getString(titleIndex);
        viewHolder.textView.setText(versionTitle);

        int posterIndex = cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER);
        final String poster = cursor.getString(posterIndex);
        Log.i(LOG_TAG, "Image reference extracted: " + poster);


        Picasso.with(mContext).load(poster).into(viewHolder.imageView);

    }

}

