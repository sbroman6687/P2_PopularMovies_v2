package coursesbr.examples;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Soledad on 5/25/2016.
 */
public class Utility {
    public static String getPreferedSorting(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_movies_sort_key),(context.getString(R.string.pref_sort_value_popular)));
    }
}
