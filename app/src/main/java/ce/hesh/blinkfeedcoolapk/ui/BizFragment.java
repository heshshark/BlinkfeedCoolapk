package ce.hesh.blinkfeedcoolapk.ui;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.socks.library.KLog;

import org.aisen.android.common.utils.ViewUtils;
import org.aisen.android.network.task.TaskException;
import org.aisen.android.network.task.WorkTask;
import org.aisen.android.ui.activity.basic.BaseActivity;
import org.aisen.android.ui.fragment.ABaseFragment;

import ce.hesh.blinkfeedcoolapk.Common;
import ce.hesh.blinkfeedcoolapk.R;
import ce.hesh.blinkfeedcoolapk.bean.Favority;
import ce.hesh.blinkfeedcoolapk.bean.FeedInfo;
import ce.hesh.blinkfeedcoolapk.util.NetUtil;

public class BizFragment extends ABaseFragment {

    private Activity mActivity;

    @Override
    public int inflateContentView() {
        return -1;
    }

    private Activity getRealActivity() {
        if (getActivity() != null)
            return getActivity();

        return mActivity;
    }

    private String getRealString(int resId) {
        if (getActivity() != null && getResources() != null) {
            return getString(resId);
        }

        return mActivity.getString(resId);
    }

    public static BizFragment createBizFragment(ABaseFragment fragment) {
        try {
            if (fragment != null && fragment.getActivity() != null) {
                BizFragment bizFragment = (BizFragment) fragment.getActivity().getFragmentManager().findFragmentByTag("org.aisen.android.ui.BizFragment");

                if (bizFragment == null) {
                    bizFragment = new BizFragment();
                    bizFragment.mActivity = fragment.getActivity();
                    fragment.getActivity().getFragmentManager().beginTransaction().add(bizFragment, "org.aisen.android.ui.BizFragment").commit();
                }

                return bizFragment;
            }
        } catch (IllegalStateException e) {

        }

        return null;
    }

    public static BizFragment createBizFragment(Activity activity) {
        BizFragment bizFragment = (BizFragment) activity.getFragmentManager().findFragmentByTag("BizFragment");
        if (bizFragment == null) {
            bizFragment = new BizFragment();
            bizFragment.mActivity = activity;

            if (activity instanceof BaseActivity) {
                if (((BaseActivity) activity).isDestory()) {
                    return bizFragment;
                }
            }

            activity.getFragmentManager().beginTransaction().add(bizFragment, "BizFragment").commit();
        }
        return bizFragment;
    }

    View.OnClickListener PreviousArrOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Object[] tag = (Object[]) v.getTag();
            FeedInfo bean = (FeedInfo) tag[0];
            int selectedIndex = Integer.parseInt(tag[1].toString());
            KLog.d("call picactivity");
            Common.isHomeKey = false;
            PicsActivity.launch(getRealActivity(), bean, selectedIndex);
        }
    };

    public void previousPics(View view, FeedInfo bean, int selectedIndex) {
        Object[] tag = new Object[]{bean, selectedIndex};
        view.setTag(tag);
        view.setOnClickListener(PreviousArrOnClickListener);
    }


    	/* 开始收藏 */

    public void favorityCreate(String favUrl, final OnFavorityCreateCallback callback) {
        new WorkTask<String, Void, Favority>() {

            protected void onPrepare() {
                super.onPrepare();

                ViewUtils.createProgressDialog(getRealActivity(), getRealString(R.string.biz_add_fav), Color.GREEN).show();
            }

            ;

            protected void onFinished() {
                super.onFinished();

                ViewUtils.dismissProgressDialog();
            }

            ;

            protected void onSuccess(Favority result) {
                super.onSuccess(result);
                if (getRealActivity() == null) {
                    return;
                }

                if (result.getData() == 1)
                    ViewUtils.showMessage(getRealActivity(), R.string.biz_fav_success);

                if (callback != null)
                    callback.onFavorityCreate(result);
            }

            protected void onFailure(TaskException exception) {
                super.onFailure(exception);
                if (getRealActivity() == null) {
                    return;
                }

                if (callback == null || !callback.onFaild(exception)) {
                    showMessage(exception.getMessage());
                }
            }

            @Override
            public Favority workInBackground(String... params) throws TaskException {
                return NetUtil.doFav(params[0]);
            }

        }.execute(favUrl);
    }

    public interface OnFavorityCreateCallback {

        public void onFavorityCreate(Favority status);

        public boolean onFaild(TaskException exception);

    }

	/* 结束收藏*/

    // XXX /*取消收藏*/
    /* 开始取消收藏*/

    public void favorityDestory(String unFavUrl, final OnFavorityDestoryCallback callback) {
        new WorkTask<String, Void, Favority>() {

            protected void onPrepare() {
                super.onPrepare();

                ViewUtils.createProgressDialog(getRealActivity(), getRealString(R.string.biz_remove_fav), Color.GREEN).show();
            }

            ;

            protected void onFinished() {
                super.onFinished();

                ViewUtils.dismissProgressDialog();
            }

            ;

            protected void onSuccess(Favority result) {
                super.onSuccess(result);
                if (getRealActivity() == null) {
                    return;
                }

                if (result.getData() == 0)
                    ViewUtils.showMessage(getRealActivity(), R.string.biz_fav_removed);

                if (callback != null)
                    callback.onFavorityDestory(result);
            }

            protected void onFailure(TaskException exception) {
                super.onFailure(exception);
                if (getRealActivity() == null) {
                    return;
                }

                if (callback == null || !callback.onFaild(exception)) {
                    showMessage(exception.getMessage());
                } else {
                    ViewUtils.showMessage(getRealActivity(), R.string.biz_fav_remove_faild);
                }
            }

            @Override
            public Favority workInBackground(String... params) throws TaskException {
                return NetUtil.doFav(params[0]);
            }

        }.execute(unFavUrl);
    }

    public interface OnFavorityDestoryCallback {

        public void onFavorityDestory(Favority status);

        public boolean onFaild(TaskException exception);

    }

	/* 结束取消收藏*/
}