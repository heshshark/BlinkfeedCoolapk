package ce.hesh.blinkfeedcoolapk;

import android.net.Uri;

import java.util.regex.Pattern;

/**
 * Created by Hesh on 2016/11/30.
 */

public class Common {

    public static final String LIST_FEED_URL = "https://api.coolapk.com/v6/topic/feedList?type=all&page=1";
    public static final String DOFAVURL_BASE = "https://api.coolapk.com/v6/feed/Favorite?id=";
    public static final String UNFAVURL_BASE = "https://api.coolapk.com/v6/feed/unFavorite?id=";
    public static final String FEED_DETAIL_URL_PREF = "https://api.coolapk.com/v6/feed/detail?id=";
    public static final String ACCOUNT_TYPE = "ce.hesh.blinkfeedcoolapk.account";
    public static final String ACTION_LOGOUT = "ce.hesh.blinkfeedcoolapk.ACTION_LOGOUT";
    public static final String FEED_ID = "INTENT_SNS_LOCAL_ID";
    public static final Uri SOCIAL_ACCOUNT_URI = Uri.parse("content://com.htc.socialnetwork.accounts/ce.hesh.blinkfeedcoolapk.account");
    public static final int TYPE_PHOTO = 2;
    public static final String MIDDLE_PIC_BASE = ".m.jpg";
    public static boolean isHomeKey = false;

    public static final Pattern qemotionPattern = Pattern.compile("<img src=\"http://static.coolapk.com/emoticons/default/\\d{1,2}.gif\" alt=\"(.{1,3})\"/>");
    public static final Pattern userPattern = Pattern.compile("<a class=\"feed-link-uname\" href=\"/u/.{1,20}\">(.{1,20})</a>");
    public static final Pattern morePattern = Pattern.compile("<a href=\"\">(.*)</a>$");
    public static final Pattern normalUrlPattern = Pattern.compile("<a class=\"feed-link-url\" href=\"(.{10,20})\" title=\"[^<|>|\"]{1,200}\" target=\"_blank\" rel=\"nofollow\">.{1,20}</a>");
    public static final Pattern topicPattern = Pattern.compile("<a class=\"feed-link-tag\" href=\"/t/.{1,20}\">(.{1,20})</a>");



    public interface StreamColumn {
        String COLUMN_ACCOUNT_NAME_STR = "stream_account_name";
        String COLUMN_ACCOUNT_TYPE_STR = "stream_account_type";
        String COLUMN_ATTACHMENT_CLICK_ACTION_STR = "stream_attachment_click_action_str";
        String COLUMN_AVATAR_URL_STR = "stream_avatar_url";
        String COLUMN_BODY_STR = "stream_body_str";
        String COLUMN_BUNDLE_ID_STR = "stream_bundle_id";
        String COLUMN_BUNDLE_ORDER_INT = "stream_bundle_order";
        String COLUMN_CLICK_ACTION_STR = "stream_click_action_str";
        String COLUMN_CONTEXT_ACTION_STR = "stream_context_action_str";
        String COLUMN_COVER_URI_HQ_STR = "stream_cover_uri_hq_str";
        String COLUMN_COVER_URI_LQ_STR = "stream_cover_uri_lq_str";
        String COLUMN_COVER_URI_MQ_STR = "stream_cover_uri_mq_str";
        String COLUMN_EXTRA_STR = "stream_extra_str";
        String COLUMN_OWNER_UID_INT = "stream_owner_uid_int";
        String COLUMN_POSTER_ID_STR = "stream_poster";
        String COLUMN_POSTER_NAME_STR = "stream_poster_name_str";
        String COLUMN_POST_ID_STR = "stream_post_id";
        String COLUMN_PROVIDER_ICON_URI_STR = "stream_provider_icon_str";
        String COLUMN_STREAM_TYPE_INT = "stream_type";
        String COLUMN_SYNC_TYPE_STR = "stream_sync_type_str";
        String COLUMN_TIMESTAMP_LONG = "stream_timestamp";
        String COLUMN_TITLE_FORMAT_STR = "stream_title_format_str";
        String COLUMN_TITLE_STR = "stream_title_str";
    }

    public interface SyncCursorsColumn {
        String COLUMN_ACCOUNT_NAME_STR = "cursors_account_name";
        String COLUMN_ACCOUNT_TYPE_STR = "cursors_account_type";
        String COLUMN_END_TIME_LONG = "cursors_end_time";
        String COLUMN_PAGE_TOKEN = " cursors_page_token";
        String COLUMN_START_TIME_LONG = "cursors_start_time";
        String COLUMN_SYNC_TYPE = "cursors_sync_type";
    }

    public interface SyncTypeColumn {
        String COLUMN_ACCOUNT_NAME_STR = "account_name";
        String COLUMN_ACCOUNT_TYPE_STR = "account_type";
        String COLUMN_CATEGORY_RES_NAME_STR = "category_res";
        String COLUMN_CATEGORY_STR = "category";
        String COLUMN_COLOR_INT = "color";
        String COLUMN_EDITION_RES_NAME_STR = "edition_res";
        String COLUMN_EDITION_STR = "edition";
        String COLUMN_ENABLED_INT = "enabled";
        String COLUMN_ICON_RES_NAME_STR = "icon_res";
        String COLUMN_ICON_URL_STR = "icon_url";
        String COLUMN_IDENTITY_STR = "_id";
        String COLUMN_PACKAGE_NAME_STR = "package_name";
        String COLUMN_SUB_TITLE_RES_NAME_STR = "sub_title_res";
        String COLUMN_SUB_TITLE_STR = "sub_title";
        String COLUMN_TITLE_RES_NAME_STR = "title_res";
        String COLUMN_TITLE_STR = "title";
    }


}
