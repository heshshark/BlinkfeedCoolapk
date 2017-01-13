package ce.hesh.blinkfeedcoolapk.ui.widget;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import org.aisen.android.component.bitmaploader.BitmapLoader;
import org.aisen.android.component.bitmaploader.core.ImageConfig;
import org.aisen.android.component.bitmaploader.core.MyBitmap;
import org.aisen.android.component.bitmaploader.display.DefaultDisplayer;

import ce.hesh.blinkfeedcoolapk.R;
import ce.hesh.blinkfeedcoolapk.bean.FeedInfo;
import ce.hesh.blinkfeedcoolapk.ui.PicsActivity;


public class CommentPictureView extends ImageView implements View.OnClickListener {

    public CommentPictureView(Context context) {
        super(context);
    }

    public CommentPictureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentPictureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    FeedInfo feedInfo;

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

//        setLayoutParams(new RelativeLayout.LayoutParams(drawable.getBounds().right - drawable.getBounds().left,
//                                                            drawable.getBounds().bottom - drawable.getBounds().top));

        setOnClickListener(this);
    }

    public void display(String url, FeedInfo feedInfo) {
        setTag(url);
        ImageConfig config = new ImageConfig();
        config.setCompressCacheEnable(false);
        config.setId("large");
        //config.setDownloaderClass(PictureDownloader.class);
        config.setLoadingRes(R.drawable.bg_timeline_loading);
        config.setDisplayer(new DefaultDisplayer());

        MyBitmap myBitmap = BitmapLoader.getInstance().getMyBitmapFromMemory(url, config);
        // 内存缓存存在图片，且未释放
        if (myBitmap != null) {
            setImageDrawable(new BitmapDrawable(myBitmap.getBitmap()));
        }
        else {
            BitmapLoader.getInstance().display(null, url, this, config);
            this.feedInfo = feedInfo;
        }
    }

    @Override
    public void onClick(View v) {
/*      Uri uri = Uri.parse("timeline_pic://" + v.getTag());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, getContext().getPackageName());
        getContext().startActivity(intent);*/
        PicsActivity.launch((Activity) getContext(), feedInfo, 0);
    }

}
