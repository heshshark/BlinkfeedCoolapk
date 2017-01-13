package ce.hesh.blinkfeedcoolapk.ui;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.aisen.android.common.utils.Utils;
import org.aisen.android.component.bitmaploader.BitmapLoader;
import org.aisen.android.component.bitmaploader.core.ImageConfig;
import org.aisen.android.component.bitmaploader.display.DefaultDisplayer;
import org.aisen.android.support.inject.ViewInject;
import org.aisen.android.ui.fragment.ABaseFragment;
import org.aisen.android.ui.fragment.adapter.ARecycleViewItemView;

import ce.hesh.blinkfeedcoolapk.Common;
import ce.hesh.blinkfeedcoolapk.R;
import ce.hesh.blinkfeedcoolapk.bean.FeedInfo;
import ce.hesh.blinkfeedcoolapk.ui.widget.CoolTextView;
import ce.hesh.blinkfeedcoolapk.ui.widget.TimelinePicsView;
import ce.hesh.blinkfeedcoolapk.util.NetUtil;
import ce.hesh.blinkfeedcoolapk.util.InfoUtil;


public class CommentHeaderItemView extends ARecycleViewItemView<FeedInfo> implements View.OnClickListener {

    public static final int COMMENT_HEADER_RES = R.layout.item_timeline_comment_header;

    @ViewInject(id = R.id.imgPhoto)
    ImageView imgPhoto;
    @ViewInject(id = R.id.txtName)
    TextView txtName;
    @ViewInject(id = R.id.timeInfo)
    TextView timeInfo;
    @ViewInject(id = R.id.txtDesc)
    TextView txtDesc;
    @ViewInject(id = R.id.txtFrom)
    TextView textFrom;
    @ViewInject(id = R.id.txtContent)
    CoolTextView txtContent;
    @ViewInject(id = R.id.layRe)
    View layRe;
    @ViewInject(id = R.id.txtReContent)
    CoolTextView txtReContent;
    @ViewInject(id = R.id.layPicturs)
    public TimelinePicsView layPicturs;


    @ViewInject(id = R.id.txtVisiable)
    TextView txtVisiable;

    @ViewInject(id = R.id.layReStatusContainer)
    View layReStatusContainer;


    private ABaseFragment fragment;

    private FeedInfo statusContent;


    public CommentHeaderItemView(ABaseFragment fragment, View itemView, FeedInfo statusContent) {
        super(fragment.getActivity(), itemView);
        this.fragment = fragment;
        this.statusContent = statusContent;
        onBindView(itemView);
        onBindData(itemView, null, 0);
    }

    @Override
    public void onBindData(View convertView, FeedInfo comment, int position) {
        //用户名
        txtName.setText(statusContent.getUsername());

        //用户头像
        BitmapLoader.getInstance().display(fragment, statusContent.getUserAvatar(), imgPhoto, getLargePhotoConfig());


        final Uri userUri = Uri.parse("http://coolapk.com/u/" + statusContent.getUsername());
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(userUri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Common.isHomeKey = false;
                getContext().startActivity(intent);
            }
        });

        // 描述
        String createAt = "";
        if (!TextUtils.isEmpty(statusContent.getDateline()))
            createAt = InfoUtil.convDate(statusContent.getDateline());
        timeInfo.setText(createAt + " ");

        if (!TextUtils.isEmpty(statusContent.getInfo()) && !statusContent.getType().equals("0") && !TextUtils.isEmpty(statusContent.getTurl())) {
            String commentInfo = statusContent.getInfo() + "   ";
            final Uri apkDetailUri = Uri.parse("http://coolapk.com" + statusContent.getTurl());
            SpannableStringBuilder apkStyle = new SpannableStringBuilder(commentInfo);
            apkStyle.setSpan(new ForegroundColorSpan(Color.parseColor("#009688")), 5, commentInfo.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            txtDesc.setText(apkStyle);
            txtDesc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(apkDetailUri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Common.isHomeKey = false;
                    getContext().startActivity(intent);
                }
            });
        }


        if (!TextUtils.isEmpty(statusContent.getDevice_title())) {
            final Uri topicUri = Uri.parse("http://coolapk.com/t/" + statusContent.getDevice_title());
            String from = String.format("来自  %s", statusContent.getDevice_title());
            SpannableStringBuilder topicStyle = new SpannableStringBuilder(from);
            topicStyle.setSpan(new ForegroundColorSpan(Color.parseColor("#009688")), 2, from.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            textFrom.setText(topicStyle);
            textFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(topicUri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Common.isHomeKey = false;
                    getContext().startActivity(intent);
                }
            });
        }


/*        String desc;
        if (TextUtils.isEmpty(from)) {
            desc = String.format("%s  %s", createAt, commentInfo);
        } else {
            desc = String.format("%s  %s 来自 %s", createAt, commentInfo, from);
        }

        txtDesc.setContent(desc);*/

        // 文本
        txtContent.setContent(statusContent.getMessage().replace("<!--break-->", ""));


        // reContent
        FeedInfo reContent = statusContent.getSourceFeed();
        if (reContent == null) {
            layRe.setVisibility(View.GONE);
        } else {
            layRe.setVisibility(View.VISIBLE);
            layRe.setTag(reContent);

            String reUser = reContent.getUsername();

            String reUserName = "";
            if (!TextUtils.isEmpty(reUser))
                reUserName = String.format("@%s :", reUser);
            txtReContent.setContent(reUserName + reContent.getMessage());
            // 正文
        }

        // pictures
        final FeedInfo s = statusContent.getSourceFeed() != null ? statusContent.getSourceFeed() : statusContent;

        if (!TextUtils.isEmpty(s.getPic())) {
            layPicturs.setPics(s, fragment);
        } else {
            layPicturs.setVisibility(View.GONE);
        }

        // 有转发内容时时，设置查看原feed评论的事件
        if (statusContent.getSourceFeed() != null && statusContent.getSourceFeed().getUsername() != null) {
            layReStatusContainer.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String detailUrl = Common.FEED_DETAIL_URL_PREF + statusContent.getSourceFeed().getId();
                            FeedInfo feedInfo = NetUtil.getFeedInfo(detailUrl);
                            if (feedInfo == null)
                                return;
                            Common.isHomeKey = false;
                            DetailFragment.launch(fragment.getActivity(), feedInfo);
                        }
                    }).start();
                }

            });
        }

        // 如果没有原feed和图片，把bottom的间隙都去掉
        if (statusContent.getSourceFeed() == null && TextUtils.isEmpty(statusContent.getPic())) {
            txtContent.setPadding(txtContent.getPaddingLeft(), txtContent.getPaddingTop(), txtContent.getPaddingRight(), 0);
            layReStatusContainer.setVisibility(View.GONE);
        }
        // 如果没有图片，有原feed，底部加点空隙
        if (statusContent.getSourceFeed() != null && TextUtils.isEmpty(statusContent.getPic())) {
            txtReContent.setPadding(txtReContent.getPaddingLeft(), txtReContent.getPaddingTop(), txtReContent.getPaddingRight(), Utils.dip2px(getContext(), 8));
        }
    }

    @Override
    public void onClick(View v) {

    }

    public static ImageConfig getLargePhotoConfig() {
        ImageConfig config = new ImageConfig();
        config.setId("large");
        config.setDisplayer(new DefaultDisplayer());
        config.setLoadingRes(R.drawable.user_placeholder);
        config.setLoadfaildRes(R.drawable.user_placeholder);
        return config;
    }

}
