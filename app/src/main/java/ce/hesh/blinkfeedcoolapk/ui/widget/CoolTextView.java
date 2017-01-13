package ce.hesh.blinkfeedcoolapk.ui.widget;

/**
 * Created by Hesh on 2016/12/14.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.socks.library.KLog;

import org.aisen.android.common.context.GlobalContext;
import org.aisen.android.common.utils.BitmapUtil;
import org.aisen.android.common.utils.KeyGenerator;
import org.aisen.android.common.utils.Logger;
import org.aisen.android.component.bitmaploader.core.LruMemoryCache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ce.hesh.blinkfeedcoolapk.R;
import ce.hesh.blinkfeedcoolapk.database.EmotionsDB;

import static ce.hesh.blinkfeedcoolapk.Common.morePattern;
import static ce.hesh.blinkfeedcoolapk.Common.normalUrlPattern;
import static ce.hesh.blinkfeedcoolapk.Common.qemotionPattern;
import static ce.hesh.blinkfeedcoolapk.Common.topicPattern;
import static ce.hesh.blinkfeedcoolapk.Common.userPattern;

/**
 * 加载表情，添加链接两个功能<br/>
 *
 * @author wangdan
 */
public class CoolTextView extends TextView {

    static final String TAG = "CoolTextView";

    public static final LruMemoryCache<String, SpannableString> textSpannableCache = new LruMemoryCache<>(200);

    public static final LruMemoryCache<String, String> textNoneSpannableCache = new LruMemoryCache<>(200);

    public static final LruMemoryCache<String, Bitmap> emotionCache = new LruMemoryCache<>(30);

    private static LinkedBlockingQueue<String> textQueue = new LinkedBlockingQueue<>();

    private static List<CoolTextView> textViewList = new ArrayList<>();

    private static InnerThread innerThread;

    private static int lineHeight = 0;

    private static Bitmap normalURLBitmap;

    private String content;

    private static Context mContext;

    private boolean innerWeb = false;

    private String textKey;
    private SpannableString textSpannable;

    static {
        mContext = GlobalContext.getInstance();
    }

    static final class UTTransformFilter implements Linkify.TransformFilter {
        public String transformUrl(Matcher match, String url) {
            if (url.contains("#")) {
                url = url.substring(1, url.length() - 1);
            } else if (url.contains("/apk/")) {
                url = url.substring(url.indexOf("/"), url.length());
            } else {
                url = url.substring(1, url.length());
            }
            return url;
        }
    }

    public CoolTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CoolTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CoolTextView(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        textViewList.add(this);
        if (!TextUtils.isEmpty(textKey)) {
            SpannableString spannableString = textSpannableCache.get(textKey);
            if (spannableString != null) {
                setTextSpannable(spannableString);
            } else {
                addText(content);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        textViewList.remove(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void setTextSpannable(SpannableString textSpannable) {
        if (this.textSpannable == textSpannable) {
            return;
        }

        this.textSpannable = textSpannable;

        textViewList.remove(this);

        super.setText(textSpannable);
    }

    public void setContent(String text) {
        if (!TextUtils.isEmpty(text) && text.indexOf("http://t.cn/") != -1) {
            if (text.length() == 19) {
                text = text + " .";
            }
        }

        boolean replace = false;

        if (!replace)
            replace = innerWeb;


        if (!replace && TextUtils.isEmpty(text)) {
            super.setText(text);
            return;
        }

        // 内容未变化
        if (!replace && !TextUtils.isEmpty(content) && content.equals(text))
            return;

        content = text;
        textKey = KeyGenerator.generateMD5(text);
        SpannableString textSpannable = textSpannableCache.get(textKey);
        if (textSpannable == null) {
            super.setText(text);
            addText(text);
        } else {
            setTextSpannable(textSpannable);
        }

        setClickable(false);
        setOnTouchListener(onTouchListener);
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {

        ClickableTextViewMentionLinkOnTouchListener listener = new ClickableTextViewMentionLinkOnTouchListener();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return listener.onTouch(v, event);

        }
    };

    public static void addText(String text) {

        synchronized (textQueue) {
            String key = KeyGenerator.generateMD5(text);

            if (textSpannableCache.get(key) == null) {
                textQueue.add(text);
            }

            if (innerThread == null || !innerThread.isAlive()) {
                innerThread = new InnerThread();
                innerThread.start();
            }
        }
    }

/*    static class CoolHandler extends Handler {
        private final WeakReference<Context> mContext;

        public CoolHandler(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String key = msg.getData().getString("key");
            if (!TextUtils.isEmpty(key)) {
                List<CoolTextView> copyList = new ArrayList<>();
                copyList.addAll(textViewList);
                Iterator<CoolTextView> iterator = copyList.iterator();
                while (iterator.hasNext()) {
                    CoolTextView textView = iterator.next();

                    SpannableString textSpannable = textSpannableCache.get(key);

                    if (key.equals(textView.textKey) && textSpannable != null) {
                        textView.setTextSpannable(textSpannable);
                    }
                }
            }
        }
    }*/

    //static CoolHandler mHandler = new CoolHandler(GlobalContext.getInstance());
    static Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String key = msg.getData().getString("key");
            if (!TextUtils.isEmpty(key)) {
                List<CoolTextView> copyList = new ArrayList<>();
                copyList.addAll(textViewList);
                Iterator<CoolTextView> iterator = copyList.iterator();
                while (iterator.hasNext()) {
                    CoolTextView textView = iterator.next();

                    SpannableString textSpannable = textSpannableCache.get(key);

                    if (key.equals(textView.textKey) && textSpannable != null) {
                        textView.setTextSpannable(textSpannable);
                    }
                }
            }
        }

    };


    static class InnerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    String text = textQueue.poll(60, TimeUnit.SECONDS);

                    if (text == null) {
                        innerThread = null;
                        break;
                    } else {
                        if (GlobalContext.getInstance() == null) {
                            break;
                        }

                        String textKey = KeyGenerator.generateMD5(text);
                        text = text.replace("<!--break-->", "");
                        text = text.replace("&#039;", "'");
                        text = text.replace("&lt;", "<");
                        text = text.replace("&gt;", ">");
                        text = qemotionPattern.matcher(text).replaceAll("[$1]");
                        text = userPattern.matcher(text).replaceAll("$1");
                        text = morePattern.matcher(text).replaceAll("$1");
                        text = normalUrlPattern.matcher(text).replaceAll("$1");
                        text = topicPattern.matcher(text).replaceAll("$1");


                        if (textNoneSpannableCache.get(textKey) != null) {
                            continue;
                        }

                        // 获得行高
                        int lineH = 0;
                        while (lineH == 0) {
                            synchronized (textViewList) {
                                if (textViewList.size() == 0)
                                    continue;

                                TextView textView = textViewList.get(0);
                                if (textView != null)
                                    lineH = textView.getLineHeight();
                            }
                        }
                        if (lineHeight != lineH) {
                            KLog.i("emoioncache");
                            emotionCache.evictAll();
                            lineHeight = lineH;
                        }

                        Resources res = GlobalContext.getInstance().getResources();
                        if (normalURLBitmap == null) {
                            normalURLBitmap = BitmapFactory.decodeResource(res, R.drawable.timeline_card_small_web);
                            normalURLBitmap = BitmapUtil.zoomBitmap(normalURLBitmap, Math.round(lineHeight * 4.0f / 5));
                        }

                        boolean find = false;

                        SpannableString spannableString = SpannableString.valueOf(text);
                        if (text.endsWith("查看更多")) {
                            find = true;
                            SpannableStringBuilder topicStyle = new SpannableStringBuilder(text);
                            topicStyle.setSpan(new ForegroundColorSpan(Color.parseColor("#009688")), text.length() - 4, text.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            spannableString = SpannableString.valueOf(topicStyle);
                        }

                        Matcher localMatcher = Pattern.compile("\\[(\\S+?)\\]|#\\((\\S+?)\\)").matcher(spannableString);
                        while (localMatcher.find()) {
                            KLog.i("localmather");
                            String key = localMatcher.group(0);
                            int k = localMatcher.start();
                            int m = localMatcher.end();

                            if (key.contains("#"))
                                key = "[" + key + "]";
                            byte[] data = EmotionsDB.getEmotion(key);
                            if (data == null)
                                continue;

                            find = true;

                            synchronized (emotionCache) {
                                Bitmap bitmap = emotionCache.get(key);
                                if (bitmap == null) {
                                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    bitmap = BitmapUtil.zoomBitmap(bitmap, lineHeight);
                                    // 添加到内存中
                                    emotionCache.put(key, bitmap);
                                }

                                EmotionSpan l = new EmotionSpan(mContext,bitmap, ImageSpan.ALIGN_BASELINE);
                                spannableString.setSpan(l, k, m, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                        // 用户名称
                        // Pattern pattern = Pattern.compile("@([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+)");
                       Pattern pattern = Pattern.compile("@[\\w\\p{InCJKUnifiedIdeographs}-]{1,26}");
                        String scheme = "http://coolapk.com/u/";
                        Linkify.addLinks(spannableString, pattern, scheme, null, new UTTransformFilter());

                        // 网页链接
                        scheme = "http://";
                        Linkify.addLinks(spannableString, Pattern.compile("http://[a-zA-Z0-9+&@#/%?=~_\\-|!:,\\.;]*[a-zA-Z0-9+&@#/%=~_|]"), scheme);


                        // 话题
                        Pattern dd = Pattern.compile("#[\\p{Print}\\p{InCJKUnifiedIdeographs}&&[^\\)]]+#");
                        //Pattern dd = Pattern.compile("#([a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+)#");
                        scheme = "http://coolapk.com/t/";
                        Linkify.addLinks(spannableString, dd, scheme, null, new UTTransformFilter());


                        URLSpan[] urlSpans = spannableString.getSpans(0, spannableString.length(), URLSpan.class);
                        Object MySpan;
                        for (URLSpan urlSpan : urlSpans) {
                            KLog.i("urlSpan");

                            find = true;
                            int start = spannableString.getSpanStart(urlSpan);
                            int end = spannableString.getSpanEnd(urlSpan);
                            try {
                                spannableString.removeSpan(urlSpan);
                            } catch (Exception e) {
                            }

                            Uri uri = Uri.parse(urlSpan.getURL());
                            String id = KeyGenerator.generateMD5(uri.toString());

                           if (urlSpan.getURL().startsWith("http://t.cn/")) {
                                MySpan = new WebURLEmotionSpan(mContext, normalURLBitmap, urlSpan.getURL(), 1, ImageSpan.ALIGN_BASELINE);
                                Logger.d(TAG, "id[%s], url[%s], normal", id, urlSpan.getURL());
                                spannableString.setSpan(MySpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            } else {
                                Logger.d(TAG, "id[%s], url[%s], none", id, urlSpan.getURL());
                                MySpan = new CoolURLSpan(urlSpan.getURL(),mContext);
                                spannableString.setSpan(MySpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }

                        if (find) {
                            textSpannableCache.put(textKey, spannableString);
                            Message msg = mHandler.obtainMessage();
                            msg.getData().putString("key", textKey);
                            msg.sendToTarget();
                        } else {
                            textNoneSpannableCache.put(textKey, text);
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}