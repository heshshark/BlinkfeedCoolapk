package ce.hesh.blinkfeedcoolapk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.socks.library.KLog;

import ce.hesh.blinkfeedcoolapk.ClearDataService;
import ce.hesh.blinkfeedcoolapk.Common;
import ce.hesh.blinkfeedcoolapk.util.AccountUtil;

/**
 * Created by Hesh on 2016/12/1.
 */

public class SnsReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        KLog.i( "action " + action);
        KLog.i("intent " + intent.toString());
        if ("android.accounts.LOGIN_ACCOUNTS_CHANGED".equals(action) && AccountUtil.getAccount(context) == null) {
            KLog.i("Account logout deleteAllFromDb");
            context.getContentResolver().notifyChange(Common.SOCIAL_ACCOUNT_URI, null);
            doLogout(context,intent);
        }
    }

    public void doLogout(Context context, Intent intent) {
        context.startService(new Intent(context, ClearDataService.class).putExtra("operation", Common.ACTION_LOGOUT));
    }

}
