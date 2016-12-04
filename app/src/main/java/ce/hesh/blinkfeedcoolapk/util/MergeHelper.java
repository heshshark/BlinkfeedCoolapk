package ce.hesh.blinkfeedcoolapk.util;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.htc.lib2.opensense.social.SocialContract;
import com.htc.lib2.opensense.social.SocialContract.Stream;
import com.htc.lib2.opensense.social.SocialContract.SyncCursors;
import com.htc.lib2.opensense.social.SocialContract.SyncTypeContract;
import com.htc.lib2.opensense.social.SyncType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import ce.hesh.blinkfeedcoolapk.Common;

public class MergeHelper {
    private static final int BATCH_LIMIT = 30;
    private static final String LOG_TAG = MergeHelper.class.getSimpleName();
    private static MergeHelper sInstance = null;
    private ContentResolver mResolver;

    private MergeHelper(Context context) {
        this.mResolver = context.getContentResolver();
    }

    public static MergeHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MergeHelper(context);
        }
        return sInstance;
    }

    public void insertStreamToDb(List<ContentValues> values) {
        if (values == null || values.isEmpty()) {
            Log.e(LOG_TAG, "insertToDB , values is null or empty");
            return;
        }
        ArrayList<ContentProviderOperation> operations = new ArrayList();
        String accountType = ((ContentValues) values.get(0)).getAsString(Common.StreamColumn.COLUMN_ACCOUNT_TYPE_STR);
        String accountName = ((ContentValues) values.get(0)).getAsString(Common.StreamColumn.COLUMN_ACCOUNT_NAME_STR);
        if (!(TextUtils.isEmpty(accountName) || TextUtils.isEmpty(accountType))) {
            handleInsertStream(new Account(accountName, accountType), operations, values);
        }
        if (operations.size() > 0) {
            applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
        }
        this.mResolver.notifyChange(Stream.CONTENT_URI, null);
    }

    public void mergeStreamToDb(long endTime, long startTime, Account account, List<ContentValues> values, String[] syncTypes) {
        if (syncTypes == null || syncTypes.length < 1) {
            Log.e(LOG_TAG, "mergeToDB , syncTypes is null or empty, do nothing");
        } else if (account == null) {
            Log.e(LOG_TAG, "mergeToDB , account is null, do nothing");
        } else {
            ArrayList<ContentProviderOperation> operations = new ArrayList();
            if (values != null) {
                handleInsertStream(account, operations, values);
            }
            for (String syncType : syncTypes) {
                addInsertOrMergeSyncCursorsOperation(operations, account.name, account.type, syncType, startTime, endTime);
            }
            if (operations.size() > 0) {
                applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
            }
            this.mResolver.notifyChange(Stream.CONTENT_URI, null);
        }
    }

    private void handleInsertStream(Account account, ArrayList<ContentProviderOperation> operations, List<ContentValues> values) {
        if (values != null) {
            HashMap<String, String> existingSyncTypeMap = buildExistingSyncTypeMap(account, values);
            HashMap<String, String> existingBundleIdMap = buildExistingBundleIdMap(account, values);
            for (ContentValues value : values) {
                mergeAndSplitSyncTypes(existingSyncTypeMap, value);
                handleBundleId(existingBundleIdMap, value);
                addInsertStreamOperations(operations, value);
                if (operations.size() >= BATCH_LIMIT) {
                    applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
                }
            }
        }
    }

    private HashMap<String, String> buildExistingSyncTypeMap(Account account, List<ContentValues> values) {
        HashMap<String, String> existingSyncTypeMap = new HashMap();
        if (!(account == null || values == null)) {
            ArrayList<String> idList = new ArrayList(values.size());
            for (ContentValues value : values) {
                if (value.containsKey(Common.StreamColumn.COLUMN_POST_ID_STR)) {
                    idList.add(value.getAsString(Common.StreamColumn.COLUMN_POST_ID_STR));
                }
            }
            Cursor cursor = null;
            try {
                cursor = this.mResolver.query(Stream.buildUriWithAccounts(new Account[]{account}, false), new String[]{Common.StreamColumn.COLUMN_POST_ID_STR, Common.StreamColumn.COLUMN_SYNC_TYPE_STR}, generateWhereClause(Common.StreamColumn.COLUMN_POST_ID_STR, (String[]) idList.toArray(new String[idList.size()])), null, Stream.DEFAULT_SORT);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        existingSyncTypeMap.put(cursor.getString(0), cursor.getString(1));
                    }
                }
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "error when close cursor" + e);
                    }
                }
            } catch (Exception e2) {
                Log.e(LOG_TAG, "error when query db" + e2);
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception e22) {
                        Log.e(LOG_TAG, "error when close cursor" + e22);
                    }
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception e222) {
                        Log.e(LOG_TAG, "error when close cursor" + e222);
                    }
                }
            }
        }
        return existingSyncTypeMap;
    }

    private HashMap<String, String> buildExistingBundleIdMap(Account account, List<ContentValues> values) {
        HashMap<String, String> existingBundleIdMap = new HashMap();
        if (!(account == null || values == null)) {
            ArrayList<String> idList = new ArrayList(values.size());
            for (ContentValues value : values) {
                if (value.containsKey(Common.StreamColumn.COLUMN_POST_ID_STR)) {
                    idList.add(value.getAsString(Common.StreamColumn.COLUMN_POST_ID_STR));
                }
            }
            Cursor cursor = null;
            try {
                cursor = this.mResolver.query(Stream.buildUriWithAccounts(new Account[]{account}, false), new String[]{Common.StreamColumn.COLUMN_POST_ID_STR, Common.StreamColumn.COLUMN_BUNDLE_ID_STR}, generateWhereClause(Common.StreamColumn.COLUMN_POST_ID_STR, (String[]) idList.toArray(new String[idList.size()])), null, Stream.DEFAULT_SORT);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        existingBundleIdMap.put(cursor.getString(0), cursor.getString(1));
                    }
                }
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "error when close cursor" + e);
                    }
                }
            } catch (Exception e2) {
                Log.e(LOG_TAG, "error when query db" + e2);
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception e22) {
                        Log.e(LOG_TAG, "error when close cursor" + e22);
                    }
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception e222) {
                        Log.e(LOG_TAG, "error when close cursor" + e222);
                    }
                }
            }
        }
        return existingBundleIdMap;
    }

    private void mergeAndSplitSyncTypes(HashMap<String, String> existingSyncTypeMap, ContentValues value) {
        if (value.containsKey(Common.StreamColumn.COLUMN_POST_ID_STR) && value.containsKey(Common.StreamColumn.COLUMN_SYNC_TYPE_STR)) {
            String insertingPostId = value.getAsString(Common.StreamColumn.COLUMN_POST_ID_STR);
            String insertingSyncTypes = value.getAsString(Common.StreamColumn.COLUMN_SYNC_TYPE_STR);
            HashSet<String> finalSyncTypes = new HashSet();
            if (!TextUtils.isEmpty(insertingSyncTypes)) {
                for (String insertingSyncType : insertingSyncTypes.split(",")) {
                    finalSyncTypes.add(encodeSyncType(insertingSyncType));
                }
            }
            if (!TextUtils.isEmpty(insertingPostId) && existingSyncTypeMap.containsKey(insertingPostId)) {
                String existingSyncTypes = (String) existingSyncTypeMap.get(insertingPostId);
                if (!TextUtils.isEmpty(existingSyncTypes)) {
                    for (String splitedSyncType : existingSyncTypes.split(",")) {
                        finalSyncTypes.add(splitedSyncType);
                    }
                }
            }
            if (!finalSyncTypes.isEmpty()) {
                value.remove(Common.StreamColumn.COLUMN_SYNC_TYPE_STR);
                value.put(Common.StreamColumn.COLUMN_SYNC_TYPE_STR, TextUtils.join(",", finalSyncTypes));
            }
        }
    }

    private void handleBundleId(HashMap<String, String> existingBundleIdMap, ContentValues value) {
        if ((value != null && !value.containsKey(Common.StreamColumn.COLUMN_BUNDLE_ID_STR)) || TextUtils.isEmpty(value.getAsString(Common.StreamColumn.COLUMN_BUNDLE_ID_STR))) {
            String postId = value.getAsString(Common.StreamColumn.COLUMN_POST_ID_STR);
            if (!TextUtils.isEmpty(postId) && existingBundleIdMap.containsKey(postId)) {
                value.put(Common.StreamColumn.COLUMN_BUNDLE_ID_STR, (String) existingBundleIdMap.get(postId));
            }
        }
    }

    public int updateStreamType(String accountType, String accountName, int streamType, int TypeToUpdate, boolean set) {
        String setString;
        Uri rawUri = Uri.parse("content://" + SocialContract.CONTENT_AUTHORITY).buildUpon().appendPath("raw").build();
        if (set) {
            setString = "%s=(%s | %d)";
        } else {
            setString = "%s=(%s & (~%d))";
        }
        int ret = this.mResolver.update(rawUri, new ContentValues(), String.format(Locale.US, "UPDATE %s SET " + setString + " WHERE %s=%s AND %s=%s AND (%s & %d)=%d", new Object[]{"stream", "stream_type", "stream_type", Integer.valueOf(TypeToUpdate), Common.StreamColumn.COLUMN_ACCOUNT_TYPE_STR, DatabaseUtils.sqlEscapeString(accountType), Common.StreamColumn.COLUMN_ACCOUNT_NAME_STR, DatabaseUtils.sqlEscapeString(accountName), "stream_type", Integer.valueOf(streamType), Integer.valueOf(streamType)}), null);
        this.mResolver.notifyChange(Stream.CONTENT_URI, null);
        return ret;
    }

    public int updateStreamType(String accountType, String accountName, String what, String[] ids, int TypeToUpdate, boolean set) {
        String setString;
        Uri rawUri = Uri.parse("content://" + SocialContract.CONTENT_AUTHORITY).buildUpon().appendPath("raw").build();
        if (set) {
            setString = "%s=(%s | %d)";
        } else {
            setString = "%s=(%s & (~%d))";
        }
        int ret = this.mResolver.update(rawUri, new ContentValues(), String.format(Locale.US, "UPDATE %s SET " + setString + " WHERE %s=%s AND %s=%s AND (" + generateWhereClause(what, ids) + ")", new Object[]{"stream", "stream_type", "stream_type", Integer.valueOf(TypeToUpdate), Common.StreamColumn.COLUMN_ACCOUNT_TYPE_STR, DatabaseUtils.sqlEscapeString(accountType), Common.StreamColumn.COLUMN_ACCOUNT_NAME_STR, DatabaseUtils.sqlEscapeString(accountName)}), null);
        this.mResolver.notifyChange(Stream.CONTENT_URI, null);
        return ret;
    }

    private String generateWhereClause(String what, String[] ids) {
        if (ids == null || ids.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(what);
        builder.append(" in (");
        for (String id : ids) {
            builder.append(DatabaseUtils.sqlEscapeString(id));
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(")");
        return builder.toString();
    }

    public void deleteStreamFromDb(String accountType, String accountName, String what, String[] ids) {
        if (what == null || ids == null || ids.length == 0) {
            throw new RuntimeException("selection or selectionArg is null!");
        }
        ArrayList<ContentProviderOperation> operations = new ArrayList();
        Uri uri = Stream.buildUriWithAccounts(new Account[]{new Account(accountName, accountType)}, false);
        StringBuilder builder = new StringBuilder();
        for (String sqlEscapeString : ids) {
            if (builder.length() > 0) {
                builder.append(",");
            }
            builder.append(DatabaseUtils.sqlEscapeString(sqlEscapeString));
        }
        if (Common.StreamColumn.COLUMN_SYNC_TYPE_STR.equals(what)) {
            for (String id : ids) {
                operations.add(ContentProviderOperation.newDelete(uri).withSelection("stream_sync_type_str like '%" + encodeSyncType(id) + "%'", null).build());
            }
        } else {
            operations.add(ContentProviderOperation.newDelete(uri).withSelection(what + " in (" + builder.toString() + ")", null).build());
        }
        if (Common.StreamColumn.COLUMN_SYNC_TYPE_STR.equals(what)) {
            operations.add(ContentProviderOperation.newDelete(SyncCursors.CONTENT_URI).withSelection("cursors_sync_type in (" + builder.toString() + ")", null).build());
        }
        if (operations.size() > 0) {
            applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
        }
        this.mResolver.notifyChange(Stream.CONTENT_URI, null);
    }

    private String encodeSyncType(String syncType) {
        return "[" + syncType + "]";
    }

    public void deleteAllFromDb(String accountType) {
        ArrayList<ContentProviderOperation> operations = new ArrayList();
        operations.add(ContentProviderOperation.newDelete(Stream.CONTENT_URI).withSelection("stream_account_type=?", new String[]{accountType}).build());
        operations.add(ContentProviderOperation.newDelete(SyncCursors.CONTENT_URI).withSelection("cursors_account_type=?", new String[]{accountType}).build());
        operations.add(ContentProviderOperation.newDelete(SyncTypeContract.CONTENT_URI).withSelection("account_type=?", new String[]{accountType}).build());
        if (operations.size() > 0) {
            applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
        }
        this.mResolver.notifyChange(Stream.CONTENT_URI, null);
    }

    public void deleteAllFromDb(String accountType, String accountName) {
        ArrayList<ContentProviderOperation> operations = new ArrayList();
        operations.add(ContentProviderOperation.newDelete(Stream.buildUriWithAccounts(new Account[]{new Account(accountName, accountType)}, false)).build());
        operations.add(ContentProviderOperation.newDelete(SyncCursors.CONTENT_URI).withSelection("cursors_account_type=? AND cursors_account_name=?", new String[]{accountType, accountName}).build());
        operations.add(ContentProviderOperation.newDelete(SyncTypeContract.CONTENT_URI).withSelection("account_type=? AND account_name=?", new String[]{accountType, accountName}).build());
        if (operations.size() > 0) {
            applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
        }
        this.mResolver.notifyChange(Stream.CONTENT_URI, null);
    }

    public void insertSyncTypeToDb(List<SyncType> syncTypes, String accountName, String accountType, boolean wipeOldData) {
        ArrayList<ContentProviderOperation> operations = new ArrayList();
        if (wipeOldData) {
            operations.add(ContentProviderOperation.newDelete(SyncTypeContract.buildUriWithAccount(accountName, accountType)).build());
        }
        if (!(syncTypes == null || syncTypes.isEmpty())) {
            for (SyncType syncType : syncTypes) {
                ContentValues value = new ContentValues();
                value.put("_id", syncType.getId());
                value.put("account_name", accountName);
                value.put("account_type", accountType);
                value.put(Common.SyncTypeColumn.COLUMN_PACKAGE_NAME_STR, syncType.getPackageName());
                value.put(Common.SyncTypeColumn.COLUMN_TITLE_STR, syncType.getTitle());
                value.put(Common.SyncTypeColumn.COLUMN_TITLE_RES_NAME_STR, syncType.getTitleResName());
                value.put(Common.SyncTypeColumn.COLUMN_SUB_TITLE_STR, syncType.getSubTitle());
                value.put(Common.SyncTypeColumn.COLUMN_SUB_TITLE_RES_NAME_STR, syncType.getSubTitleResName());
                value.put(Common.SyncTypeColumn.COLUMN_EDITION_STR, syncType.getEdition());
                value.put(Common.SyncTypeColumn.COLUMN_EDITION_RES_NAME_STR, syncType.getEditionResName());
                value.put(Common.SyncTypeColumn.COLUMN_CATEGORY_STR, syncType.getCategory());
                value.put(Common.SyncTypeColumn.COLUMN_CATEGORY_RES_NAME_STR, syncType.getCategoryResName());
                value.put(Common.SyncTypeColumn.COLUMN_ICON_RES_NAME_STR, syncType.getIconResName());
                value.put(Common.SyncTypeColumn.COLUMN_ICON_URL_STR, syncType.getIconUrl());
                operations.add(ContentProviderOperation.newInsert(SyncTypeContract.CONTENT_URI).withValues(value).build());
                if (operations.size() >= BATCH_LIMIT) {
                    applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
                }
            }
        }
        if (operations.size() > 0) {
            applyBatchAndReset(SocialContract.CONTENT_AUTHORITY, operations);
        }
        this.mResolver.notifyChange(SyncTypeContract.CONTENT_URI, null);
    }

    private void addInsertStreamOperations(ArrayList<ContentProviderOperation> operations, ContentValues value) {
        if (value == null) {
            Log.e(LOG_TAG, "addInsertOperation, values is null");
        } else {
            operations.add(ContentProviderOperation.newInsert(Stream.CONTENT_URI).withValues(value).build());
        }
    }

    private void addInsertOrMergeSyncCursorsOperation(ArrayList<ContentProviderOperation> operations, String accountName, String accountType, String syncType, long startTime, long endTime) {
        ContentValues cursorsValue = new ContentValues();
        cursorsValue.put(Common.SyncCursorsColumn.COLUMN_ACCOUNT_NAME_STR, accountName);
        cursorsValue.put(Common.SyncCursorsColumn.COLUMN_ACCOUNT_TYPE_STR, accountType);
        cursorsValue.put(Common.SyncCursorsColumn.COLUMN_SYNC_TYPE, syncType);
        cursorsValue.put(Common.SyncCursorsColumn.COLUMN_START_TIME_LONG, Long.valueOf(startTime));
        cursorsValue.put(Common.SyncCursorsColumn.COLUMN_END_TIME_LONG, Long.valueOf(endTime));
        operations.add(ContentProviderOperation.newInsert(SyncCursors.CONTENT_URI).withValues(cursorsValue).build());
    }

    private void applyBatchAndReset(String authorities, ArrayList<ContentProviderOperation> operations) {
        if (operations != null && operations.size() != 0) {
            try {
                this.mResolver.applyBatch(authorities, operations);
                Log.i(LOG_TAG, "applyBatchAndReset completed " + operations.size() + " ops successfully.");
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "applyBatchAndReset failed!", e);
            } catch (OperationApplicationException e2) {
                Log.e(LOG_TAG, "applyBatchAndReset failed!", e2);
            } catch (NullPointerException e3) {
                Log.e(LOG_TAG, "applyBatchAndReset failed!", e3);
            } catch (Exception e4) {
                Log.e(LOG_TAG, "applyBatchAndReset failed!", e4);
            }
            operations.clear();
        }
    }
}