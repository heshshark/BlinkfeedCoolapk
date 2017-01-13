package ce.hesh.blinkfeedcoolapk.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.socks.library.KLog;

import org.aisen.android.common.utils.Utils;
import org.aisen.android.support.bean.TabItem;
import org.aisen.android.support.inject.InjectUtility;
import org.aisen.android.support.inject.ViewInject;
import org.aisen.android.ui.activity.basic.BaseActivity;
import org.aisen.android.ui.activity.container.FragmentArgs;
import org.aisen.android.ui.fragment.APagingFragment;
import org.aisen.android.ui.fragment.ATabsTabLayoutFragment;

import java.util.ArrayList;

import ce.hesh.blinkfeedcoolapk.Common;
import ce.hesh.blinkfeedcoolapk.R;
import ce.hesh.blinkfeedcoolapk.bean.FeedInfo;
import ce.hesh.blinkfeedcoolapk.ui.widget.TimelineDetailScrollView;

/**
 * Created by Hesh on 2016/12/9.
 */

public class DetailFragment extends ATabsTabLayoutFragment<TabItem>
        implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    public static void launch(Activity from, FeedInfo status) {
        FragmentArgs args = new FragmentArgs();
        args.add("status", status);
        DetailActivity.launch(from, DetailFragment.class, args);
    }

    @ViewInject(id = R.id.layHeader)
    RelativeLayout layHeader;
    @ViewInject(id = R.id.appbar)
    AppBarLayout appBarLayout;
    @ViewInject(id = R.id.toolbar)
    Toolbar toolbar;
    @ViewInject(id = R.id.layHeaderDivider)
    View layHeaderDivider;
    @ViewInject(id = R.id.txtAttitudes)
    TextView txtAttitudes;
    @ViewInject(id = R.id.action_menu)
    FloatingActionsMenu action_menu;
    @ViewInject(id = R.id.action_a)
    FloatingActionButton action_a;
    @ViewInject(id = R.id.action_b)
    FloatingActionButton action_b;
    @ViewInject(id = R.id.action_c)
    FloatingActionButton action_c;
    @ViewInject(id = R.id.overlay)
    View overlay;
    @ViewInject(id = R.id.laySroll)
    TimelineDetailScrollView laySroll;

    private FeedInfo mStatusContent;

    private BizFragment bizFragment;

    @Override
    public int inflateContentView() {
        return -1;
    }

    @Override
    public int inflateActivityContentView() {
        return R.layout.ui_timeline_detail;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        InjectUtility.initInjectedView(getActivity(), this, ((BaseActivity) getActivity()).getRootView());
        layoutInit(inflater, savedInstanceState);

        // 添加HeaderView
        View itemConvertView = inflater.inflate(CommentHeaderItemView.COMMENT_HEADER_RES, layHeader, false);
        CommentHeaderItemView headerItemView = new CommentHeaderItemView(this, itemConvertView, mStatusContent);
        layHeader.addView(itemConvertView,
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT));

        // TODO: 2016/12/15
        //BizFragment.createBizFragment(getActivity()).createFabAnimator(action_menu);
        return null;
    }

    @Override
    public TabLayout getTablayout() {
        return (TabLayout) getActivity().findViewById(R.id.tabLayout);
    }

    @Override
    public ViewPager getViewPager() {
        return (ViewPager) getActivity().findViewById(R.id.viewPager);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KLog.i("onCreate");
/*        mStatusContent = savedInstanceState != null ? (FeedInfo) savedInstanceState.getSerializable("status")
                : (FeedInfo) getArguments().getSerializable("status");*/
        mStatusContent = (FeedInfo) getArguments().getSerializable("status");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bizFragment = BizFragment.createBizFragment(this);

        BaseActivity activity = (BaseActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.timeline_detail);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);
    }

    @Override
    protected void setupTabLayout(Bundle savedInstanceSate) {
        super.setupTabLayout(savedInstanceSate);

        TabLayout tabLayout = getTablayout();
        tabLayout.setPadding(Utils.dip2px(getActivity(), 8), tabLayout.getPaddingTop(), tabLayout.getPaddingRight(), tabLayout.getPaddingBottom());
        tabLayout.setTabTextColors(getResources().getColor(R.color.text_54),
                getResources().getColor(R.color.text_80));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        KLog.i("onSaveInstanceState");
    }


    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
        super.layoutInit(inflater, savedInstanceState);

        appBarLayout.addOnOffsetChangedListener(this);

/*
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
*/

        action_a.setOnClickListener(this);
        action_b.setOnClickListener(this);
        action_c.setOnClickListener(this);
        overlay.setOnClickListener(this);
        for (int i = 0; i < action_menu.getChildCount(); i++) {
            if (action_menu.getChildAt(i) instanceof AddFloatingActionButton) {
                action_menu.getChildAt(i).setOnClickListener(this);
                break;
            }
        }

        mHandler.postDelayed(initCurrentFragment, 100);
    }

    @Override
    public void onResume() {
        super.onResume();
        KLog.i("onResume");
        setLikeText();
    }

    @Override
    public void onPause() {
        super.onPause();
        KLog.i("onPause");
    }

    private void setLikeText() {
        // 点赞数
        if (mStatusContent.getRecentLikeList() != null && mStatusContent.getRecentLikeList().length > 0)
            txtAttitudes.setText(String.format(getString(R.string.attitudes_format), String.valueOf(mStatusContent.getRecentLikeList().length)));
        else
            txtAttitudes.setText("");
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        KLog.i("onPageSelected");
        if (getCurrentFragment() != null && getCurrentFragment() instanceof APagingFragment &&
                ((APagingFragment) getCurrentFragment()).getRefreshView() != null) {
            laySroll.setRefreshView(((APagingFragment) getCurrentFragment()).getRefreshView());
        }

        // 切换了Page就显示Fab
        //BizFragment.createBizFragment(getActivity()).getFabAnimator().show();
    }

    private Handler mHandler = new Handler();

    Runnable initCurrentFragment = new Runnable() {

        @Override
        public void run() {
            if (getCurrentFragment() != null && getCurrentFragment() instanceof APagingFragment &&
                    ((APagingFragment) getCurrentFragment()).getRefreshView() != null) {
                laySroll.setRefreshView(((APagingFragment) getCurrentFragment()).getRefreshView());
            } else {
                mHandler.postDelayed(initCurrentFragment, 100);
            }
        }

    };

    @Override
    protected ArrayList<TabItem> generateTabs() {

        ArrayList<TabItem> tabItems = new ArrayList<>();

        tabItems.add(new TabItem("0", String.format(getString(R.string.comment_format), mStatusContent.getReplynum())));

        return tabItems;
    }

    @Override
    protected Fragment newFragment(TabItem bean) {
        // 热门评论

        // 评论
        if ("0".equals(bean.getType())) {
            return TimelineCommentFragment.newInstance(mStatusContent);
        }
        return null;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int visibility = View.VISIBLE;
        // 如果是AppbarLayout滑动到了最顶端，要把这个divider隐藏掉
        if (getTablayout().getHeight() + toolbar.getHeight() - appBarLayout.getHeight() == verticalOffset) {
            visibility = View.GONE;
        }
        if (layHeaderDivider.getVisibility() != visibility)
            layHeaderDivider.setVisibility(visibility);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

     /*   inflater.inflate(R.menu.menu_cmts, menu);
        menu.removeItem(R.id.fav);
        menu.removeItem(R.id.repost);
        menu.removeItem(R.id.comment);
        if (mStatusContent.getUser() == null ||
                !mStatusContent.getUser().getIdstr().equalsIgnoreCase(AppContext.getAccount().getUser().getIdstr()))
            menu.removeItem(R.id.delete);
        AisenUtils.setStatusShareMenu(menu.findItem(R.id.share), mStatusContent);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //AisenUtils.onMenuClicked(this, item.getItemId(), mStatusContent);

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        // 点击了+按钮
        if (v instanceof AddFloatingActionButton) {
            if (action_menu.isExpanded()) {
                dismissOverlay();
            } else {
                showOverlay();
            }

            action_menu.toggle();

            return;
        }
        // 覆盖层
        else if (v.getId() == R.id.overlay) {
        }
        // 收藏
        else if (v.getId() == R.id.action_a) {
            String doFavUrl  = Common.DOFAVURL_BASE+mStatusContent.getId();
            BizFragment.createBizFragment(this).favorityCreate(doFavUrl, null);
            //AisenUtils.onMenuClicked(this, R.id.fav, mStatusContent);
        }
        // 转发
        else if (v.getId() == R.id.action_b) {
            // AisenUtils.onMenuClicked(this, R.id.repost, mStatusContent);
        }
        // 评论
        else if (v.getId() == R.id.action_c) {
            // AisenUtils.onMenuClicked(this, R.id.comment, mStatusContent);
        }

        dismissOverlay();
        action_menu.collapse();
    }

    private void showOverlay() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(overlay, "alpha", 0.0f, 1.0f);
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                overlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        animator.start();
    }

    private void dismissOverlay() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(overlay, "alpha", 1.0f, 0.0f);
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                overlay.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        animator.start();
    }


}
