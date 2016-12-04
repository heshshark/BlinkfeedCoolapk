package ce.hesh.blinkfeedcoolapk;

import android.net.Uri;

/**
 * Created by Hesh on 2016/11/30.
 */

public class Common {

    public static final String PLUGIN_PACKAGE_NAME = "ce.hesh.blinkfeedcoolapk";

    public static final String ACCOUNT_TYPE = "ce.hesh.blinkfeedcoolapk.account";
    public static final String ACTION_LOGIN = "com.tencent.mm.login.ACTION_LOGIN";
    public static final String ACTION_LOGOUT = "com.tencent.mm.login.ACTION_LOGOUT";
    public static final String ACTION_SHARE = "com.htc.socialnetwork.wechatplugin.ACTION_SHARE";
    public static final String ACTION_VIEW_DETAIL = "com.htc.socialnetwork.wechatplugin.ACTION_VIEW_DETAIL";
    public static final String APP_ID = "wx41dd4f6ef137bd0b";
    public static final Uri DETAIL_URI = Uri.parse("content://com.tencent.mm.plugin.ext.entry/sns_comment_detail?appid=wx41dd4f6ef137bd0b");
    public static final String FEED_ID = "INTENT_SNS_LOCAL_ID";
    public static final Uri QUERY_URI = Uri.parse("content://com.tencent.mm.plugin.ext.SNS/snsInfo?appid=wx41dd4f6ef137bd0b");
    public static final Uri SHARE_URI = Uri.parse("content://com.tencent.mm.plugin.ext.entry/share_time_line?appid=wx41dd4f6ef137bd0b");
    public static final Uri SOCIAL_ACCOUNT_URI = Uri.parse("content://com.htc.socialnetwork.accounts/com.tencent.mm.account");
    public static final int TYPE_LINK = 6;
    public static final int TYPE_MUSIC = 3;
    public static final int TYPE_PHOTO = 2;
    public static final int TYPE_VIDEO = 4;



    public interface StreamColumn {
        public static final String COLUMN_ACCOUNT_NAME_STR = "stream_account_name";
        public static final String COLUMN_ACCOUNT_TYPE_STR = "stream_account_type";
        public static final String COLUMN_ATTACHMENT_CLICK_ACTION_STR = "stream_attachment_click_action_str";
        public static final String COLUMN_AVATAR_URL_STR = "stream_avatar_url";
        public static final String COLUMN_BODY_STR = "stream_body_str";
        public static final String COLUMN_BUNDLE_ID_STR = "stream_bundle_id";
        public static final String COLUMN_BUNDLE_ORDER_INT = "stream_bundle_order";
        public static final String COLUMN_CLICK_ACTION_STR = "stream_click_action_str";
        public static final String COLUMN_CONTEXT_ACTION_STR = "stream_context_action_str";
        public static final String COLUMN_COVER_URI_HQ_STR = "stream_cover_uri_hq_str";
        public static final String COLUMN_COVER_URI_LQ_STR = "stream_cover_uri_lq_str";
        public static final String COLUMN_COVER_URI_MQ_STR = "stream_cover_uri_mq_str";
        public static final String COLUMN_EXTRA_STR = "stream_extra_str";
        public static final String COLUMN_OWNER_UID_INT = "stream_owner_uid_int";
        public static final String COLUMN_POSTER_ID_STR = "stream_poster";
        public static final String COLUMN_POSTER_NAME_STR = "stream_poster_name_str";
        public static final String COLUMN_POST_ID_STR = "stream_post_id";
        public static final String COLUMN_PROVIDER_ICON_URI_STR = "stream_provider_icon_str";
        public static final String COLUMN_STREAM_TYPE_INT = "stream_type";
        public static final String COLUMN_SYNC_TYPE_STR = "stream_sync_type_str";
        public static final String COLUMN_TIMESTAMP_LONG = "stream_timestamp";
        public static final String COLUMN_TITLE_FORMAT_STR = "stream_title_format_str";
        public static final String COLUMN_TITLE_STR = "stream_title_str";
    }

    public interface SyncCursorsColumn {
        public static final String COLUMN_ACCOUNT_NAME_STR = "cursors_account_name";
        public static final String COLUMN_ACCOUNT_TYPE_STR = "cursors_account_type";
        public static final String COLUMN_END_TIME_LONG = "cursors_end_time";
        public static final String COLUMN_PAGE_TOKEN = " cursors_page_token";
        public static final String COLUMN_START_TIME_LONG = "cursors_start_time";
        public static final String COLUMN_SYNC_TYPE = "cursors_sync_type";
    }

    public interface SyncTypeColumn {
        public static final String COLUMN_ACCOUNT_NAME_STR = "account_name";
        public static final String COLUMN_ACCOUNT_TYPE_STR = "account_type";
        public static final String COLUMN_CATEGORY_RES_NAME_STR = "category_res";
        public static final String COLUMN_CATEGORY_STR = "category";
        public static final String COLUMN_COLOR_INT = "color";
        public static final String COLUMN_EDITION_RES_NAME_STR = "edition_res";
        public static final String COLUMN_EDITION_STR = "edition";
        public static final String COLUMN_ENABLED_INT = "enabled";
        public static final String COLUMN_ICON_RES_NAME_STR = "icon_res";
        public static final String COLUMN_ICON_URL_STR = "icon_url";
        public static final String COLUMN_IDENTITY_STR = "_id";
        public static final String COLUMN_PACKAGE_NAME_STR = "package_name";
        public static final String COLUMN_SUB_TITLE_RES_NAME_STR = "sub_title_res";
        public static final String COLUMN_SUB_TITLE_STR = "sub_title";
        public static final String COLUMN_TITLE_RES_NAME_STR = "title_res";
        public static final String COLUMN_TITLE_STR = "title";
    }


}
