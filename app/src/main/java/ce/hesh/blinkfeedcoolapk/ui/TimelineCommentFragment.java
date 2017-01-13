
package ce.hesh.blinkfeedcoolapk.ui;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.aisen.android.network.task.TaskException;
import org.aisen.android.support.paging.IPaging;
import org.aisen.android.ui.fragment.AListFragment;
import org.aisen.android.ui.fragment.itemview.BasicFooterView;
import org.aisen.android.ui.fragment.itemview.IITemView;
import org.aisen.android.ui.fragment.itemview.IItemViewCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ce.hesh.blinkfeedcoolapk.R;
import ce.hesh.blinkfeedcoolapk.bean.FeedComment;
import ce.hesh.blinkfeedcoolapk.bean.FeedComments;
import ce.hesh.blinkfeedcoolapk.bean.FeedInfo;
import ce.hesh.blinkfeedcoolapk.ui.widget.CommentPaging;
import ce.hesh.blinkfeedcoolapk.ui.widget.CoolTextView;
import ce.hesh.blinkfeedcoolapk.ui.widget.TimelineCommentItemView;
import ce.hesh.blinkfeedcoolapk.util.NetUtil;


public class TimelineCommentFragment extends AListFragment<FeedComment, FeedComments, FeedComment> {

    private int pageCount = 1;

    public static TimelineCommentFragment newInstance(FeedInfo status) {
        Bundle arts = new Bundle();
        arts.putSerializable("status", status);
        TimelineCommentFragment fragment = new TimelineCommentFragment();
        fragment.setArguments(arts);
        return fragment;
    }

    FeedInfo mStatusContent;

    @Override
    public int inflateContentView() {
        return R.layout.ui_timeline_comment;
    }

    @Override
    protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceSate) {
        super.layoutInit(inflater, savedInstanceSate);

        getContentView().setBackgroundColor(Color.WHITE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStatusContent = savedInstanceState != null ? (FeedInfo) savedInstanceState.getSerializable("status")
                : (FeedInfo) getArguments().getSerializable("status");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //BizFragment.createBizFragment(getActivity()).getFabAnimator().attachToListView(getRefreshView(), null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("status", mStatusContent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);

    }

    @Override
    protected IItemViewCreator<FeedComment> configFooterViewCreator() {
        return new IItemViewCreator<FeedComment>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(BasicFooterView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<FeedComment> newItemView(View convertView, int viewType) {
                return new BasicFooterView<FeedComment>(getActivity(), convertView, TimelineCommentFragment.this) {

                    @Override
                    protected String endpagingText() {
                        return getString(R.string.disable_comments);
                    }

                    @Override
                    protected String loadingText() {
                        return getString(R.string.loading_cmts);
                    }

                };
            }

        };
    }

    @Override
    public IItemViewCreator<FeedComment> configItemViewCreator() {
        return new IItemViewCreator<FeedComment>() {

            @Override
            public View newContentView(LayoutInflater inflater, ViewGroup parent, int viewType) {
                return inflater.inflate(TimelineCommentItemView.LAYOUT_RES, parent, false);
            }

            @Override
            public IITemView<FeedComment> newItemView(View convertView, int viewType) {
                return new TimelineCommentItemView(TimelineCommentFragment.this, convertView);
            }

        };
    }

    @Override
    protected IPaging<FeedComment, FeedComments> newPaging() {
        return new CommentPaging();
    }

    @Override
    public void requestData(RefreshMode mode) {
        new CommentTask(mode).execute();
    }

    class CommentTask extends APagingTask<Void, Void, FeedComments> {

        public CommentTask(RefreshMode mode) {
            super(mode);
        }

        @Override
        protected List<FeedComment> parseResult(FeedComments feedComments) {
            return feedComments.getComments();
        }

        @Override
        protected boolean handleResult(RefreshMode mode, List<FeedComment> datas) {
            // 如果是重置或者刷新数据，加载数据大于分页大小，则清空之前的数据
            if (mode == RefreshMode.reset || mode == RefreshMode.refresh)
                if (datas.size() >= 20) {
                    setAdapterItems(new ArrayList<FeedComment>());
                    return true;
                }
            return super.handleResult(mode, datas);
        }

        @Override
        protected FeedComments workInBackground(RefreshMode mode, String previousPage, String nextPage, Void... p) throws TaskException {

            FeedComments feedComments = new FeedComments();
            try {
                String detail_url = "https://api.coolapk.com/v6/feed/replyList?id=" + mStatusContent.getId() + "&page=" + pageCount;
                String detail_string = NetUtil.getJsonFromInternet(detail_url);
                if (detail_string == null)
                    return null;
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(detail_string).getAsJsonObject();
                JsonArray array = obj.getAsJsonArray("data");
                if (array == null || array.size() == 0)
                    return null;
                Gson gson = new Gson();
                List<FeedComment> feedCommentList = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    JsonElement j = array.get(i);
                    FeedComment feedComment = gson.fromJson(j, FeedComment.class);
                    feedCommentList.add(feedComment);
                }
                feedComments.setComments(feedCommentList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //statusComments.setEndPaging(statusComments.getComments().size() <= 5);

            for (FeedComment content : feedComments.getComments()) {
                CoolTextView.addText(content.getMessage());
            }
            pageCount++;
            return feedComments;
        }

        @Override
        protected void onFailure(TaskException exception) {
            super.onFailure(exception);

            showMessage(exception.getMessage());
        }

        @Override
        protected void onFinished() {
            super.onFinished();

            if (getActivity() != null) {
                Fragment fragment = getActivity().getFragmentManager().findFragmentByTag(DetailActivity.FRAGMENT_TAG);
                if (fragment != null && fragment instanceof DetailFragment) {
                    //((DetailFragment) fragment).refreshEnd();
                }
            }
        }

    }

    @Override
    public boolean onToolbarDoubleClick() {
        return false;
    }

}

