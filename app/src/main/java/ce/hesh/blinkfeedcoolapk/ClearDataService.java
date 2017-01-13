package ce.hesh.blinkfeedcoolapk;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.socks.library.KLog;

import ce.hesh.blinkfeedcoolapk.util.MergeHelper;

/**
 * Created by Hesh on 2016/12/5.
 */

public class ClearDataService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            KLog.i("intent == null");
        } else {
            String operation = intent.getStringExtra("operation");
            if (!TextUtils.isEmpty(operation) && Common.ACTION_LOGOUT.equals(operation)) {
                new AsyncTask<Void, Void, Void>() {
                    protected Void doInBackground(Void... arg0) {
                        try {
                            MergeHelper.getInstance(ClearDataService.this).deleteAllFromDb(Common.ACCOUNT_TYPE);
                        } catch (Exception e) {
                            KLog.e( "error=" + e);
                        }
                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        ClearDataService.this.stopSelf();
                    }
                }.execute();
            }
        }
        return 1;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
