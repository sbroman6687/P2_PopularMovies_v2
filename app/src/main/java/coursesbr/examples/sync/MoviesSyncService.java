package coursesbr.examples.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Soledad on 5/25/2016.
 */
public class MoviesSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MoviesSyncAdapter sMoviesSyncAdapter = null;

    @Override
    public void onCreate(){
        Log.d("MoviesSyncService","onCreate - MoviesSyncService");
        synchronized (sSyncAdapterLock){
            if (sMoviesSyncAdapter == null){
                sMoviesSyncAdapter = new MoviesSyncAdapter(getApplicationContext(),true);
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent){
        return sMoviesSyncAdapter.getSyncAdapterBinder();
    }
}
