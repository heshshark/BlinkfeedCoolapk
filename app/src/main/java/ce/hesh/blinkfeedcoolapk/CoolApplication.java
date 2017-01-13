package ce.hesh.blinkfeedcoolapk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

import com.socks.library.KLog;
import com.squareup.leakcanary.LeakCanary;

import org.aisen.android.common.context.GlobalContext;
import org.aisen.android.component.bitmaploader.BitmapLoader;

import java.io.File;

import ce.hesh.blinkfeedcoolapk.database.EmotionsDB;
import ce.hesh.blinkfeedcoolapk.receiver.HomeWatcherReceiver;

/**
 * Created by Hesh on 2016/12/16.
 */

public class CoolApplication extends GlobalContext {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        registerHomeKeyReceiver(this);
        BitmapLoader.newInstance(this, getImagePath());
        try {
            EmotionsDB.checkEmotions();
        } catch (Exception e) {
        }
    }

    public static String getImagePath() {
        return GlobalContext.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator;
    }


    private static HomeWatcherReceiver mHomeKeyReceiver = null;

    private static void registerHomeKeyReceiver(Context context) {
        KLog.i( "registerHomeKeyReceiver");
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    private static void unregisterHomeKeyReceiver(Context context) {
        KLog.i("unregisterHomeKeyReceiver");
        if (null != mHomeKeyReceiver) {
            try {
                context.unregisterReceiver(mHomeKeyReceiver);
            }catch (IllegalArgumentException e){
                KLog.e(e);
            }
        }
    }
}
