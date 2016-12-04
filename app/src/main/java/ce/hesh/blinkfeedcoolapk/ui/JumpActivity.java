package ce.hesh.blinkfeedcoolapk.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import ce.hesh.blinkfeedcoolapk.CoolConstants;

public class JumpActivity extends Activity {
    private static final String LOG_TAG = "JumpActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... arg0) {
                JumpActivity.this.work();
                return null;
            }
        }.execute(new Void[0]);
    }

    void work() {
        Cursor cursor;
        String action = getIntent().getAction();
        Log.i(LOG_TAG, "in JumpClientActivity");
        if (CoolConstants.ACTION_SHARE.equals(action)) {
            cursor = null;
            try {
                Log.d(LOG_TAG, "start querying share URI");
                cursor = getContentResolver().query(CoolConstants.SHARE_URI, null, null, new String[]{"", ""}, null);
                if (cursor != null && cursor.getColumnCount() == 0) {
                    Log.d(LOG_TAG, "request login");
                    requestLogin();
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                Log.w(LOG_TAG, "jump to share page failed");
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (CoolConstants.ACTION_VIEW_DETAIL.equals(action)) {
            cursor = null;
            try {
                Log.d(LOG_TAG, "start querying detail URI");
                cursor = getContentResolver().query(CoolConstants.DETAIL_URI, null, null, new String[]{getIntent().getStringExtra(CoolConstants.FEED_ID)}, null);
                if (cursor != null && cursor.getColumnCount() == 0) {
                    Log.d(LOG_TAG, "request login");
                    requestLogin();
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e2) {
                Log.w(LOG_TAG, "jump to share page failed");
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable th2) {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        finish();
    }

    private void requestLogin() {
        Intent intent = new Intent(CoolConstants.ACTION_LOGIN);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra("k_login_without_bind_mobile", true);
        intent.setType("vnd.android.cursor.item/vnd.com.tencent.mm.login");
        startActivity(intent);
    }
}