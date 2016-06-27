package coursesbr.examples.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Soledad on 5/19/2016.
 */
public class MoviesDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MoviesDBHelper.class.getSimpleName();

    //name&version. If you change the database schema, you must increment the database version
    private static final String DATABASE_NAME = "movies11.db";
    private static final int DATABASE_VERSION = 11;

    public MoviesDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //Create the database

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MoviesContract.MovieEntry.TABLE_MOVIES + " (" + MoviesContract.MovieEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_POSTER + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_BACKPOSTER + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_RATING + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_POPULARITY + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_FAVOURITE + " INTEGER, " +
                MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT ); ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    //Code to add favourites to the table

    public void addFavourite(String favourite, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.MovieEntry.COLUMN_FAVOURITE, 1);

        //Which row to update based on the ID
        String selection = MoviesContract.MovieEntry._ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(favourite)};

        //db.insert(MoviesContract.MovieEntry.TABLE_MOVIES, null, contentValues);

        db.update(MoviesContract.MovieEntry.TABLE_MOVIES, contentValues,selection,selectionArgs);

        Log.e("DATABASE FAVOURITES","One favourite movie is inserted");

    }

    //public static int getFavouriteNumber(){
        //return 47933;
    //}


    //Upgrade database when version is changed
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");

        //Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_MOVIES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                MoviesContract.MovieEntry.TABLE_MOVIES + "'");

        // re-create database
        onCreate(sqLiteDatabase);
    }

}

