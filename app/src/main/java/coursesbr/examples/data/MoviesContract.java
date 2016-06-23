package coursesbr.examples.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Soledad on 5/19/2016.
 */
public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "coursesbr.examples";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns{
        //Table name
        public static final String TABLE_MOVIES = "movie8";
        //Columns
        public static final String _ID = "_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_BACKPOSTER = "backposter";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_POPULARITY = "popular";
        public static final String COLUMN_RELEASE = "release";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_FAVOURITE = "favourite";


        //Create content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TABLE_MOVIES).build();

        //Create cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;

        //Create cursor of base type item for single entry
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + TABLE_MOVIES;

        //For building URIs on insertion
        public static Uri buildMoviesUri(long id){
           return ContentUris.withAppendedId(CONTENT_URI,id);
        }

    }
}

