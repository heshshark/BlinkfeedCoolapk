package ce.hesh.blinkfeedcoolapk.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.aisen.android.common.context.GlobalContext;
import org.aisen.android.common.utils.Utils;
import org.aisen.android.component.bitmaploader.BitmapLoader;
import org.aisen.android.support.inject.ViewInject;
import org.aisen.android.ui.fragment.ABaseFragment;
import org.aisen.android.ui.fragment.adapter.ARecycleViewItemView;
import org.aisen.android.ui.widget.MDButton;

import ce.hesh.blinkfeedcoolapk.R;
import ce.hesh.blinkfeedcoolapk.bean.FeedComment;
import ce.hesh.blinkfeedcoolapk.bean.FeedInfo;
import ce.hesh.blinkfeedcoolapk.ui.BizFragment;
import ce.hesh.blinkfeedcoolapk.ui.CommentHeaderItemView;
import ce.hesh.blinkfeedcoolapk.util.InfoUtil;

public class TimelineCommentItemView extends ARecycleViewItemView<FeedComment> implements View.OnClickListener {

    public static final int LAYOUT_RES = R.layout.item_timeline_comment;

    @ViewInject(id = R.id.imgPhoto)
    ImageView imgPhoto;
    @ViewInject(id = R.id.txtName)
    TextView txtName;
    @ViewInject(id = R.id.txtDesc)
    TextView txtDesc;
    @ViewInject(id = R.id.txtContent)
    CoolTextView txtContent;
    @ViewInject(id = R.id.pic)
    CommentPictureView imgPic;

    @ViewInject(id = R.id.btnReply)
    MDButton btnReply;

    private ABaseFragment mFragment;
    private BizFragment bizFragment;
    int firstTop;
    int normalTop;
    private Context context;

    public TimelineCommentItemView(ABaseFragment fragment, View itemView) {
        super(fragment.getActivity(), itemView);

        this.mFragment = fragment;
        this.context = GlobalContext.getInstance();
        bizFragment = BizFragment.createBizFragment(fragment);

        firstTop = Utils.dip2px(getContext(), 16);
        normalTop = Utils.dip2px(getContext(), 8);
    }

    private void setLikeBtn(FeedComment comment) {

        btnReply.setText(String.format(context.getString(R.string.comment_format),comment.getReplynum()));

    }

    @Override
    public void onClick(View v) {
        Toast.makeText(context,"目前仅为展示作用",Toast.LENGTH_SHORT);
/*
        if (comment != null && v.getId() == R.id.btnLike) {
            LikeBean likeBean = DoLikeAction.likeCache.get(comment.getLikeId());
            boolean liked = likeBean == null || !likeBean.isLiked();
            comment.setLiked(liked);

            setLikeBtn(comment);

            bizFragment.doLike(comment, liked, v, this);
        }*/
    }

    @Override
    public void onBindData(View convertView, FeedComment feedComment, int position) {

        BitmapLoader.getInstance().display(mFragment, feedComment.getUserAvatar(),
                imgPhoto, CommentHeaderItemView.getLargePhotoConfig());

        final Uri userUri = Uri.parse("http://coolapk.com/u/"+feedComment.getUsername());
        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(userUri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });

        String userName;
        if (feedComment.getIsFeedAuthor()==1){
            userName = feedComment.getUsername() + "\uD83D\uDC51";
        }else {
            userName = feedComment.getUsername();
        }
        if (TextUtils.isEmpty(feedComment.getRusername())){
            txtName.setText(userName);
        }else{
            String header = String.format("%s 回复 %s", userName,feedComment.getRusername());
            txtName.setText(header);
        }

        txtContent.setContent(feedComment.getMessage());

        String createAt = InfoUtil.convDate(feedComment.getDateline());
        String desc = String.format("%s", createAt);
        txtDesc.setText(desc);

        int top = position == 0 ? firstTop : normalTop;
        convertView.setPadding(convertView.getPaddingLeft(), top, convertView.getPaddingRight(), convertView.getPaddingBottom());

        if (!TextUtils.isEmpty(feedComment.getPic())) {
            imgPic.setVisibility(View.VISIBLE);
            FeedInfo feedInfo = new FeedInfo();
            feedInfo.setPicArr(new  String[1]);
            feedInfo.getPicArr()[0] = feedComment.getPic();
            imgPic.display(feedComment.getPic()+".xs.jpg",feedInfo);
        }
        else {
            imgPic.setVisibility(View.GONE);
        }

        btnReply.setTag(feedComment);
        setLikeBtn(feedComment);
        btnReply.setOnClickListener(this);

    }

/*    @Override
    public void onLikeFaild(BizFragment.ILikeBean data) {
        ((StatusComment) data).setLiked(!((StatusComment) data).isLiked());

        setLikeBtn(((StatusComment) data));
    }*/

/*
    @Override
    public void onLikeSuccess(BizFragment.ILikeBean data, View likeView) {
        if (likeView.getTag() == data) {
            bizFragment.animScale(likeView);

            setLikeBtn((FeedInfo) data);
        }
    }
*/

}
