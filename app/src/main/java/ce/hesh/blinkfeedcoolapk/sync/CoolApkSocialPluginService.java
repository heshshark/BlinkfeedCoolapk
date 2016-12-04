package ce.hesh.blinkfeedcoolapk.sync;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.htc.lib2.opensense.social.AbstractSocialPlugin;
import com.htc.lib2.opensense.social.SocialManager;
import com.htc.lib2.opensense.social.SocialPluginResponse;
import com.htc.sphere.intent.GsonUtils;
import com.htc.sphere.intent.MenuUtils;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import ce.hesh.blinkfeedcoolapk.Common;
import ce.hesh.blinkfeedcoolapk.bean.FeedInfo;
import ce.hesh.blinkfeedcoolapk.ui.LoginActivity;
import ce.hesh.blinkfeedcoolapk.util.GetCoolJson;
import ce.hesh.blinkfeedcoolapk.util.MergeHelper;


/**
 * Created by Hesh on 2016/11/29.
 */


public class CoolApkSocialPluginService extends Service {

    private static final String LOG_TAG = "SocialPluginService";
    private Context mContext = null;
    private SocialPluginStub mStub = new SocialPluginStub();


    private class SocialPluginStub extends AbstractSocialPlugin {
        private SocialPluginStub() {
        }

        @Override
        public Bundle getDataSources(SocialPluginResponse response, String[] features) {
            KLog.d("getDataSources");
            Account[] accounts = AccountManager.get(CoolApkSocialPluginService.this.mContext).getAccountsByType(Common.ACCOUNT_TYPE);
            KLog.d("Account exist:"+String.valueOf(accounts.length>0));
            Bundle bundle = new Bundle();
            bundle.putParcelableArray(SocialManager.KEY_ACCOUNTS, accounts);
            Bundle properties = new Bundle();
            properties.putString(SocialManager.KEY_PROP_POST_ACT_INTENT_URI,
                    new Intent(CoolApkSocialPluginService.this.mContext, LoginActivity.class).toUri(0));
            bundle.putBundle(SocialManager.KEY_PROPERTIES, properties);
            return bundle;

        }

        @Override
        public Bundle syncActivityStreams(SocialPluginResponse socialPluginResponse, Account[] accounts, Bundle bundle) {
            KLog.d("syncActivityStreams");
            if (bundle.getBoolean("triggerSyncManager", false)) {
                Bundle result = new Bundle();
                Bundle extras = new Bundle();
                extras.putBoolean("expedited", true);
                ContentResolver.requestSync(accounts[0], Common.ACCOUNT_TYPE, extras);
                result.putBoolean(SocialManager.KEY_BOOLEAN_RESULT, true);
                return result;
            }
            return CoolApkSocialPluginService.this.syncSnsActivityStreams(bundle.getLong(SocialManager.KEY_OFFSET), accounts, bundle);

        }

        @Override
        public Bundle publishActivityStream(SocialPluginResponse response, Account[] accounts, Bundle options) {
            KLog.d("publishActivityStream");
            return super.publishActivityStream(response, accounts, options);
        }

        @Override
        public Bundle syncContacts(SocialPluginResponse response, Account[] accounts, Bundle options) {
            KLog.d("syncContacts");
            return super.syncContacts(response, accounts, options);
        }
    }


    public void onCreate() {
        super.onCreate();
        KLog.d( "CoolApkSocialPluginService----onCreate");
    }


    private Bundle syncSnsActivityStreams(long offset, Account[] accounts, Bundle options) {
        KLog.d(" stream with offset:" + offset);
        Bundle bundle = new Bundle();
        List<ContentValues> feeds = getStream(offset);
        if (feeds == null || feeds.size() <= 0) {
            KLog.d("sync stream fail");
            bundle.putBoolean(SocialManager.KEY_BOOLEAN_RESULT, false);
        } else {
            long j;
            long currentTime = System.currentTimeMillis();
            KLog.d("start writing to db");
            MergeHelper instance = MergeHelper.getInstance(this);
            if (offset == 0) {
                j = currentTime;
            } else {
                j = offset;
            }
            instance.mergeStreamToDb(j,
                    ((ContentValues) feeds.get(feeds.size() - 1)).getAsLong(Common.StreamColumn.COLUMN_TIMESTAMP_LONG),
                    accounts[0], feeds,
                    new String[]{SocialManager.SYNC_TYPE_HIGHLIGHTS});
            KLog.d("finish writing to db");
            bundle.putBoolean(SocialManager.KEY_BOOLEAN_RESULT, true);
        }
        return bundle;
    }


    private static Account getLoginAccount(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Account[] accounts = AccountManager.get(context).getAccountsByType(Common.ACCOUNT_TYPE);
        KLog.d("get account of type length = com.tencent.mm.account " + (accounts == null ? null : Integer.valueOf(accounts.length)));
        if (accounts == null || accounts.length <= 0) {
            return null;
        }
        return accounts[0];
    }


    private List<ContentValues> getStream(long offset) {
        Exception e;
        Throwable th;
        List<ContentValues> list = null;
        try {
            KLog.d("start getting feed from internet");
            String json = GetCoolJson.getJsonFromInternet();
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(json).getAsJsonObject();
            JsonArray array = obj.getAsJsonArray("data");
            Gson gson = new Gson();
            List<FeedInfo> feedInfos = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                JsonElement j = array.get(i);
                FeedInfo feedInfo = gson.fromJson(j, FeedInfo.class);
                feedInfos.add(feedInfo);
            }
            if (feedInfos.size() > 0) {
                KLog.d("feedinfos size = " + feedInfos.size());
                List<ContentValues> list2 = new ArrayList(feedInfos.size());
                try {
                    Account account = getLoginAccount(this);
                    for (FeedInfo feedInfo : feedInfos) {
                        String feedId = feedInfo.getId();
                        ContentValues value = new ContentValues();
                        String name = feedInfo.getUsername();
                        value.put(Common.StreamColumn.COLUMN_POSTER_NAME_STR, name);
                        value.put(Common.StreamColumn.COLUMN_ACCOUNT_NAME_STR, account.name);
                        value.put(Common.StreamColumn.COLUMN_ACCOUNT_TYPE_STR, Common.ACCOUNT_TYPE);
                        value.put(Common.StreamColumn.COLUMN_CLICK_ACTION_STR, getClickActionString(feedId));
                        value.put(Common.StreamColumn.COLUMN_TIMESTAMP_LONG, System.currentTimeMillis());
                        value.put(Common.StreamColumn.COLUMN_SYNC_TYPE_STR, SocialManager.SYNC_TYPE_HIGHLIGHTS);
                        String description = feedInfo.getMessage();
                        String image = feedInfo.getPic();
                        if (image != null && image.startsWith("/storage")) {
                            image = "file://" + image;
                        }
                        value.put(Common.StreamColumn.COLUMN_COVER_URI_MQ_STR, image);
                        value.put("stream_type", 2);
                        value.put(Common.StreamColumn.COLUMN_TITLE_STR, description);
                        value.put(Common.StreamColumn.COLUMN_POST_ID_STR, feedId);
                        list2.add(value);
                    }

                    list = list2;
                } catch (Exception e2) {
                    e = e2;
                    list = list2;
                } catch (Throwable th2) {
                    th = th2;
                    list = list2;
                }
            }
        } catch (Exception e3) {
            e = e3;
            try {
                Log.e(LOG_TAG, "error=" + e);
                return list;
            } catch (Throwable th3) {
                th = th3;
            }
        }
        return list;
    }


    private String getClickActionString(String feedId) {
        Intent intent = new Intent("");
        intent.putExtra("ID", feedId);
        return GsonUtils.getGson().toJson(new MenuUtils.SimpleMenuItem(0, Common.PLUGIN_PACKAGE_NAME, 0, 0, intent));
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        KLog.d("onBlind");
        this.mContext = getBaseContext();
        return this.mStub.getIBinder();
    }
}
