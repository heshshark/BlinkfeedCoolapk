package ce.hesh.blinkfeedcoolapk.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
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
import ce.hesh.blinkfeedcoolapk.ui.DetailActivity;
import ce.hesh.blinkfeedcoolapk.ui.DetailFragment;
import ce.hesh.blinkfeedcoolapk.ui.LoginActivity;
import ce.hesh.blinkfeedcoolapk.util.InfoUtil;
import ce.hesh.blinkfeedcoolapk.util.MergeHelper;

import static ce.hesh.blinkfeedcoolapk.Common.morePattern;
import static ce.hesh.blinkfeedcoolapk.Common.normalUrlPattern;
import static ce.hesh.blinkfeedcoolapk.Common.qemotionPattern;
import static ce.hesh.blinkfeedcoolapk.Common.topicPattern;
import static ce.hesh.blinkfeedcoolapk.Common.userPattern;
import static ce.hesh.blinkfeedcoolapk.util.NetUtil.getFeedInfo;
import static ce.hesh.blinkfeedcoolapk.util.NetUtil.getFeedInfos;


/**
 * blinkfeed社交插件服务
 * Created by Hesh on 2016/11/29.
 */

public class CoolApkSocialPluginService extends Service {

    private static final String LOG_TAG = "SocialPluginService";
    private Context mContext = null;
    private SocialPluginStub mStub = new SocialPluginStub();


    /**
     * 继承自blinkfeed的社交插件接口，执行获取账号，更新feed流，发布动态的功能
     */
    private class SocialPluginStub extends AbstractSocialPlugin {
        private SocialPluginStub() {
        }

        @Override
        public Bundle getDataSources(SocialPluginResponse response, String[] features) {
            KLog.d("getDataSources");
            Account[] accounts = AccountManager.get(CoolApkSocialPluginService.this.mContext).getAccountsByType(Common.ACCOUNT_TYPE);
            KLog.d("Account exist:" + String.valueOf(accounts.length > 0));
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
            return CoolApkSocialPluginService.this.syncSnsActivityStreams(bundle.getLong(SocialManager.KEY_OFFSET), accounts);

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
        KLog.d("CoolApkSocialPluginService----onCreate");
    }

    /**
     * 将获取的feedinfo信息写入blinkfeed数据库
     *
     * @param offset   刷新的偏移量，从头开始刷新为零，从尾刷新为当前显示最后一条feed的时间戳
     * @param accounts 要写入的账户
     * @return 带有写入成功与否信息的bundle
     */
    private Bundle syncSnsActivityStreams(long offset, Account[] accounts) {
        KLog.d(" stream with offset:" + offset);
        Bundle bundle = new Bundle();
        if (offset != 0) {
            KLog.d("sync stream before,do nothing");
            bundle.putBoolean(SocialManager.KEY_BOOLEAN_RESULT, false);
            return bundle;
        }
        List<ContentValues> feeds = getStream();
        if (feeds == null || feeds.size() <= 0) {
            KLog.d("sync stream fail");
            bundle.putBoolean(SocialManager.KEY_BOOLEAN_RESULT, false);
        } else {
            long j;
            long currentTime = System.currentTimeMillis();
            KLog.d("start writing to db");
            MergeHelper instance = MergeHelper.getInstance(this);
            j = currentTime;
            instance.deleteAllFromDb(Common.ACCOUNT_TYPE);
            instance.mergeStreamToDb(j,
                    feeds.get(feeds.size() - 1).getAsLong(Common.StreamColumn.COLUMN_TIMESTAMP_LONG),
                    accounts[0], feeds,
                    new String[]{SocialManager.SYNC_TYPE_HIGHLIGHTS});
            KLog.d("finish writing to db");
            bundle.putBoolean(SocialManager.KEY_BOOLEAN_RESULT, true);
        }
        return bundle;
    }

    /**
     * 获取当前登录的账户，若有则返回
     *
     * @param context 当前服务的context
     * @return 返回账户
     */
    private static Account getLoginAccount(Context context) {
        Account[] accounts = AccountManager.get(context).getAccountsByType(Common.ACCOUNT_TYPE);
        KLog.d("get account of type length = ce.hesh.blinkfeedcoolapk.account " + accounts.length);
        if (accounts.length <= 0) {
            return null;
        }
        return accounts[0];
    }

    /**
     * 获取json字符串转换为相应的ContentValues
     *
     * @return ContentValues集合
     */
    private List<ContentValues> getStream() {
        Exception e;
        Throwable th;
        List<ContentValues> list = null;

        try {
            KLog.d("Start getting feed from internet");
            String Cookies = InfoUtil.getCookie();
            Account account = getLoginAccount(this);
            //若账户不存在或cookies不存在则跳转至登录界面
            if (account == null || Cookies ==null) {
                Intent intent = new Intent(this.mContext, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            List<FeedInfo> feedInfos = getFeedInfos(Common.LIST_FEED_URL);
            if (feedInfos != null && feedInfos.size() > 0) {
                KLog.d("Feedinfos size = " + feedInfos.size());
                List<ContentValues> list2 = new ArrayList<>(feedInfos.size());
                try {
                    for (FeedInfo feedInfo : feedInfos) {
                        //feed流为发现类型则不入库
                        if (!feedInfo.getType().equals("3")) {
                            ContentValues value = new ContentValues();
                            String feedId = feedInfo.getId();
                            String name = feedInfo.getUsername();
                            value.put(Common.StreamColumn.COLUMN_POST_ID_STR, feedId);
                            value.put(Common.StreamColumn.COLUMN_POSTER_NAME_STR, name);
                            value.put(Common.StreamColumn.COLUMN_AVATAR_URL_STR, feedInfo.getUserAvatar());
                            value.put(Common.StreamColumn.COLUMN_ACCOUNT_NAME_STR, account != null ? account.name : "Error");
                            value.put(Common.StreamColumn.COLUMN_ACCOUNT_TYPE_STR, Common.ACCOUNT_TYPE);
                            value.put(Common.StreamColumn.COLUMN_CLICK_ACTION_STR, getClickActionString(feedId));
                            value.put(Common.StreamColumn.COLUMN_TIMESTAMP_LONG, feedInfo.getDateline() + "000");
                            value.put(Common.StreamColumn.COLUMN_SYNC_TYPE_STR, SocialManager.SYNC_TYPE_HIGHLIGHTS);
                            String image = feedInfo.getPic();
                            if (!TextUtils.isEmpty(image)) {
                                image = feedInfo.getPic() + Common.MIDDLE_PIC_BASE;
                            }
                            if (TextUtils.isEmpty(image) && feedInfo.getSourceFeed() != null && !TextUtils.isEmpty(feedInfo.getSourceFeed().getPic())) {
                                image = feedInfo.getSourceFeed().getPic() + Common.MIDDLE_PIC_BASE;
                            }
                            value.put(Common.StreamColumn.COLUMN_COVER_URI_MQ_STR, image);
                            value.put("stream_type", Common.TYPE_PHOTO);

                            String description = feedInfo.getMessage();

                            if (feedInfo.getSourceFeed() != null && feedInfo.getSourceFeed().getMessage() != null && feedInfo.getSourceFeed().getUsername() != null) {
                                description = description + "//" + "@" + feedInfo.getSourceFeed().getUsername() + ":" + feedInfo.getSourceFeed().getMessage();
                            }

                            description = description.replace("<!--break-->", "");
                            description = description.replace("&#039;", "'");
                            description = description.replace("&lt;", "<");
                            description = description.replace("&gt;", ">");
                            description = qemotionPattern.matcher(description).replaceAll("[$1]");
                            description = userPattern.matcher(description).replaceAll("$1");
                            description = morePattern.matcher(description).replaceAll("$1");
                            description = normalUrlPattern.matcher(description).replaceAll("$1");
                            description = topicPattern.matcher(description).replaceAll("$1");


                            value.put(Common.StreamColumn.COLUMN_TITLE_STR, description);
                            value.put(Common.StreamColumn.COLUMN_POST_ID_STR, feedId);
                            list2.add(value);
                        }
                    }

                    list = list2;
                } catch (Exception e2) {
                    e = e2;
                    Log.e(LOG_TAG, "error=" + e);
                } catch (Throwable th2) {
                    th = th2;
                    Log.e(LOG_TAG, "error=" + th);
                }
            }
        } catch (Exception e3) {
            e = e3;
            Log.e(LOG_TAG, "error=" + e);
        }
        return list;
    }

    /**
     * 返回点击后的操作字符串
     *
     * @param feedID   当前点击feed流id
     * @return 执行的操作字符串, blinkfeed将其转化为点击事件
     */
    private String getClickActionString(String feedID) {
        String detailUrl = Common.FEED_DETAIL_URL_PREF + feedID;
        FeedInfo feedInfo = getFeedInfo(detailUrl);
        if (feedInfo == null)
            return "Error";
        Gson gson = new Gson();
        String feed_string = gson.toJson(feedInfo);
        Intent intent = new Intent(this.mContext, DetailActivity.class);
        intent.putExtra("className", DetailFragment.class.getName());
        intent.putExtra("args", feed_string);
        return GsonUtils.getGson().toJson(new MenuUtils.SimpleMenuItem(0, null, 0, 0, intent));
    }


    @org.jetbrains.annotations.Nullable
    @Override
    public IBinder onBind(Intent intent) {
        KLog.d("onBlind");
        this.mContext = getBaseContext();
        return this.mStub.getIBinder();
    }
}
