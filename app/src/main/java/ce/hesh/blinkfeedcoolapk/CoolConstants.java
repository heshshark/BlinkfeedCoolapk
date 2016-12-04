package ce.hesh.blinkfeedcoolapk;

/**
 * Created by Hesh on 2016/11/28.
 */


import android.net.Uri;

public interface CoolConstants {
    public static final String ACCOUNT_TYPE = "com.tencent.mm.account";
    public static final String ACTION_LOGIN = "com.tencent.mm.login.ACTION_LOGIN";
    public static final String ACTION_LOGOUT = "com.tencent.mm.login.ACTION_LOGOUT";
    public static final String ACTION_SHARE = "com.htc.socialnetwork.wechatplugin.ACTION_SHARE";
    public static final String ACTION_VIEW_DETAIL = "com.htc.socialnetwork.wechatplugin.ACTION_VIEW_DETAIL";
    public static final String APP_ID = "wx41dd4f6ef137bd0b";
    public static final Uri DETAIL_URI = Uri.parse("content://com.tencent.mm.plugin.ext.entry/sns_comment_detail?appid=wx41dd4f6ef137bd0b");
    public static final String FEED_ID = "INTENT_SNS_LOCAL_ID";
    public static final String PLUGIN_PACKAGE_NAME = "com.htc.socialnetwork.wechatplugin";
    public static final Uri QUERY_URI = Uri.parse("content://com.tencent.mm.plugin.ext.SNS/snsInfo?appid=wx41dd4f6ef137bd0b");
    public static final Uri SHARE_URI = Uri.parse("content://com.tencent.mm.plugin.ext.entry/share_time_line?appid=wx41dd4f6ef137bd0b");
    public static final Uri SOCIAL_ACCOUNT_URI = Uri.parse("content://com.htc.socialnetwork.accounts/com.tencent.mm.account");
    public static final int TYPE_LINK = 6;
    public static final int TYPE_MUSIC = 3;
    public static final int TYPE_PHOTO = 2;
    public static final int TYPE_VIDEO = 4;
}