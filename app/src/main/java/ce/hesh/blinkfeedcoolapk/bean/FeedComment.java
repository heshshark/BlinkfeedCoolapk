package ce.hesh.blinkfeedcoolapk.bean;

import java.io.Serializable;

/**
 * 酷安评论
 * Created by Hesh on 2016/12/19.
 */

public class FeedComment implements Serializable{

    private static final long serialVersionUID = -8876057032378860108L;

    private String id;
    private String fid;
    private int rid;
    private String uid;
    private String username;
    private int ruid;
    private String rusername;
    private String pic;
    private String message;
    private String replynum;
    private String reportnum;
    private String dateline;
    private String index_name;
    private int feedUid;
    private String fetchType;
    private String avatarFetchType;
    private String userAvatar;
    private String entityType;
    private int isFeedAuthor;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getRuid() {
        return ruid;
    }

    public void setRuid(int ruid) {
        this.ruid = ruid;
    }

    public String getRusername() {
        return rusername;
    }

    public void setRusername(String rusername) {
        this.rusername = rusername;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReplynum() {
        return replynum;
    }

    public void setReplynum(String replynum) {
        this.replynum = replynum;
    }

    public String getReportnum() {
        return reportnum;
    }

    public void setReportnum(String reportnum) {
        this.reportnum = reportnum;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getIndex_name() {
        return index_name;
    }

    public void setIndex_name(String index_name) {
        this.index_name = index_name;
    }

    public int getFeedUid() {
        return feedUid;
    }

    public void setFeedUid(int feedUid) {
        this.feedUid = feedUid;
    }

    public String getFetchType() {
        return fetchType;
    }

    public void setFetchType(String fetchType) {
        this.fetchType = fetchType;
    }

    public String getAvatarFetchType() {
        return avatarFetchType;
    }

    public void setAvatarFetchType(String avatarFetchType) {
        this.avatarFetchType = avatarFetchType;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public int getIsFeedAuthor() {
        return isFeedAuthor;
    }

    public void setIsFeedAuthor(int isFeedAuthor) {
        this.isFeedAuthor = isFeedAuthor;
    }
}
