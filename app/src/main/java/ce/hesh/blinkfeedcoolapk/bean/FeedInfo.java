package ce.hesh.blinkfeedcoolapk.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 酷安Feed流
 * Created by Hesh on 2016/11/30.
 */

public class FeedInfo implements Serializable {

    private static final long serialVersionUID = 4658524234123494L;

    private String id;
    private String uid;
    private String turl;
    private String username;
    private String type;
    private String message;
    private String pic;
    private String userAvatar;
    private String dateline;
    private String infoHtml;
    private String info;
    private String[] picArr;
    private String device_title;
    private String replynum;
    private FeedInfo sourceFeed;
    private List<FeedInfo> replyRows;
    private String[] recentLikeList;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTurl() {
        return turl;
    }

    public void setTurl(String turl) {
        this.turl = turl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getInfoHtml() {
        return infoHtml;
    }

    public void setInfoHtml(String infoHtml) {
        this.infoHtml = infoHtml;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String[] getPicArr() {
        return picArr;
    }

    public void setPicArr(String[] picArr) {
        this.picArr = picArr;
    }

    public String getDevice_title() {
        return device_title;
    }

    public void setDevice_title(String device_title) {
        this.device_title = device_title;
    }

    public String getReplynum() {
        return replynum;
    }

    public void setReplynum(String replynum) {
        this.replynum = replynum;
    }

    public FeedInfo getSourceFeed() {
        return sourceFeed;
    }

    public void setSourceFeed(FeedInfo sourceFeed) {
        this.sourceFeed = sourceFeed;
    }

    public List<FeedInfo> getReplyRows() {
        return replyRows;
    }

    public void setReplyRows(List<FeedInfo> replyRows) {
        this.replyRows = replyRows;
    }

    public String[] getRecentLikeList() {
        return recentLikeList;
    }

    public void setRecentLikeList(String[] recentLikeList) {
        this.recentLikeList = recentLikeList;
    }

    public String getMidPic(){
        return this.pic+".m.jpg";
    }
}
