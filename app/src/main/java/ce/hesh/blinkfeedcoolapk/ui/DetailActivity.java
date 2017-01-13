package ce.hesh.blinkfeedcoolapk.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.socks.library.KLog;

import org.aisen.android.ui.activity.basic.BaseActivity;
import org.aisen.android.ui.activity.container.FragmentArgs;
import org.aisen.android.ui.fragment.ABaseFragment;

import java.lang.reflect.Method;

import ce.hesh.blinkfeedcoolapk.Common;
import ce.hesh.blinkfeedcoolapk.R;
import ce.hesh.blinkfeedcoolapk.bean.FeedInfo;
import ce.hesh.blinkfeedcoolapk.receiver.HomeWatcherReceiver;

/**
 * Created by wangdan on 15/12/21.
 */
public class DetailActivity extends BaseActivity {

    public static final String FRAGMENT_TAG = "FRAGMENT_CONTAINER";

    private int contentId;
    private int overrideTheme = -1;


    /**
     * 启动一个界面
     *
     * @param activity
     * @param clazz
     * @param args
     */
    public static void launch(Activity activity, Class<? extends Fragment> clazz, FragmentArgs args) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra("className", clazz.getName());
        if (args != null)
            intent.putExtra("args", args);
        activity.startActivity(intent);
    }

    public static void launchForResult(Fragment fragment, Class<? extends Fragment> clazz, FragmentArgs args, int requestCode) {
        if (fragment.getActivity() == null)
            return;
        Activity activity = fragment.getActivity();

        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra("className", clazz.getName());
        if (args != null)
            intent.putExtra("args", args);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void launchForResult(Activity from, Class<? extends Fragment> clazz, FragmentArgs args, int requestCode) {
        Intent intent = new Intent(from, DetailActivity.class);
        intent.putExtra("className", clazz.getName());
        if (args != null)
            intent.putExtra("args", args);
        from.startActivityForResult(intent, requestCode);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentId = savedInstanceState == null ? R.layout.ui_fragment_container
                : savedInstanceState.getInt("contentId");
        overrideTheme = savedInstanceState == null ? -1
                : savedInstanceState.getInt("overrideTheme");

        Fragment fragment = null;
        if (savedInstanceState == null) {
            try {
                String className = getIntent().getStringExtra("className");
                if (TextUtils.isEmpty(className)) {
                    super.onCreate(savedInstanceState);
                    finish();
                    return;
                }

                FragmentArgs values;
                String feedString = getIntent().getStringExtra("args");
                if (feedString != null) {
                    Gson gson = new Gson();
                    FeedInfo feedInfo = gson.fromJson(feedString, FeedInfo.class);
                    values = new FragmentArgs();
                    values.add("status", feedInfo);
                } else {
                    values = (FragmentArgs) getIntent().getSerializableExtra("args");
                }
                Class clazz = Class.forName(className);
                fragment = (Fragment) clazz.newInstance();
                // 设置参数给Fragment
                if (values != null) {
                    try {
                        Method method = clazz.getMethod("setArguments", new Class[]{Bundle.class});
                        method.invoke(fragment, FragmentArgs.transToBundle(values));
                    } catch (Exception e) {
                    }
                }
                // 重写Activity的主题
                try {
                    Method method = clazz.getMethod("setActivityTheme");
                    if (method != null) {
                        int theme = Integer.parseInt(method.invoke(fragment).toString());
                        if (theme > 0) {
                            overrideTheme = theme;
                        }
                    }
                } catch (Exception e) {
                }
                // 重写Activity的contentView
                try {
                    Method method = clazz.getMethod("inflateActivityContentView");
                    if (method != null) {
                        int fragmentConfigId = Integer.parseInt(method.invoke(fragment).toString());
                        if (fragmentConfigId > 0) {
                            contentId = fragmentConfigId;
                        }
                    }
                } catch (Exception e) {
                }
            } catch (Exception e) {
                e.printStackTrace();
                super.onCreate(savedInstanceState);
                finish();
                return;
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(contentId);

        if (fragment != null) {
            if (!(fragment instanceof ABaseFragment) || ((ABaseFragment) fragment).inflateContentView() > 0) {
                getFragmentManager().beginTransaction().add(org.aisen.android.R.id.fragmentContainer, fragment, FRAGMENT_TAG).commit();
            } else {
                getFragmentManager().beginTransaction().add(fragment, FRAGMENT_TAG).commit();
            }
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowHomeEnabled(false);

        BizFragment.createBizFragment(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

       //registerHomeKeyReceiver(this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        KLog.i("onSaveInstanceState");
        outState.putInt("contentId", contentId);
        outState.putInt("overrideTheme", overrideTheme);
        KLog.i("isHomeKey:"+Common.isHomeKey);
        if (Common.isHomeKey)
            finish();
    }

    @Override
    protected int configTheme() {
        if (overrideTheme > 0)
            return overrideTheme;

        return super.configTheme();
    }

    @Override
    protected void onStop() {
        super.onStop();
       // unregisterHomeKeyReceiver(this);
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseActivity.setRunningActivity(null);
    }
}
