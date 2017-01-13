package ce.hesh.blinkfeedcoolapk.ui.widget;


import android.text.TextUtils;

import org.aisen.android.support.paging.IPaging;

import ce.hesh.blinkfeedcoolapk.bean.FeedComment;
import ce.hesh.blinkfeedcoolapk.bean.FeedComments;

/**
 * Created by wangdan on 16/1/8.
 */
public class CommentPaging implements IPaging<FeedComment, FeedComments> {

    private static final long serialVersionUID = -2363918217556704381L;

    private String firstId;

    private String lastId;

    @Override
    public void processData(FeedComments newDatas, FeedComment firstData, FeedComment lastData) {
        if (firstData != null)
            firstId = firstData.getId();
        if (lastData != null)
            lastId = lastData.getId();
    }

    @Override
    public String getPreviousPage() {
        return firstId;
    }

    @Override
    public String getNextPage() {
        if (TextUtils.isEmpty(lastId))
            return null;

        return (Long.parseLong(lastId) - 1) + "";
    }

}
