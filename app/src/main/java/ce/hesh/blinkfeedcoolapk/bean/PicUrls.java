package ce.hesh.blinkfeedcoolapk.bean;

import java.io.Serializable;

/**
 * 图片url
 * Created by Hesh on 2016/12/14.
 */

public class PicUrls implements Serializable {

    private static final long serialVersionUID = 2354439978931122615L;

    private String thumbnail_pic;

    //获取原图url
    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    //获取中图url
    public String getMidpic(){
        return thumbnail_pic+".m.jpg";
    }
    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }
}
