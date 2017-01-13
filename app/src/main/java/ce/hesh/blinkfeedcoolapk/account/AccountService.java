package ce.hesh.blinkfeedcoolapk.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 账户服务，用于添加酷安登录账户
 */
public class AccountService extends Service {
    private Authenticator a;
    private final static String LOG_TAG = "AccountService";

    public IBinder onBind(Intent intent) {
        return this.a.getIBinder();
    }

    public void onCreate() {
        Log.i(LOG_TAG,"AccountService--onCreate");
        super.onCreate();
        this.a = new Authenticator(this);
    }
}