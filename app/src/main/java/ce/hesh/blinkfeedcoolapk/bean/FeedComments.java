package ce.hesh.blinkfeedcoolapk.bean;

import org.aisen.android.support.bean.ResultBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Hesh on 2016/12/19.
 */

public class FeedComments extends ResultBean implements Serializable {

    private static final long serialVersionUID = 2420923454169920046L;

    public List<FeedComment> getComments() {
        return comments;
    }

    public void setComments(List<FeedComment> comments) {
        this.comments = comments;
    }

    private List<FeedComment> comments;
}
