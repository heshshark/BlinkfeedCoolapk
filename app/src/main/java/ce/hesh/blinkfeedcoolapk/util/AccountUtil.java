package ce.hesh.blinkfeedcoolapk.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.socks.library.KLog;

import ce.hesh.blinkfeedcoolapk.Common;

/**
 * Created by Hesh on 2016/12/2.
 */

public class AccountUtil {

    public static void addAccount(Context context, String str) {
        Account account = new Account(str, Common.ACCOUNT_TYPE);
        AccountManager.get(context).addAccountExplicitly(account, null, null);
        ContentResolver.setIsSyncable(account, "ce.hesh.blinkfeedcoolapk.provider.MainProvider", 1);
        ContentResolver.setSyncAutomatically(account, "ce.hesh.blinkfeedcoolapk.provider.MainProvider", true);
        if (!ContentResolver.getMasterSyncAutomatically()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("expedited", true);
            bundle.putBoolean("force", true);
            KLog.d("RequestSync  BlinkfeedCoolapk Provider");
            ContentResolver.requestSync(account, "ce.hesh.blinkfeedcoolapk.provider.MainProvider", bundle);
        }
    }

}
