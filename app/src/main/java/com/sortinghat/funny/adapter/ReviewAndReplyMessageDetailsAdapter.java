package com.sortinghat.funny.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.TimeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.ReviewAndReplyMessageBean;
import com.sortinghat.funny.databinding.ItemMessageReviewAndReplyDetailsBinding;
import com.sortinghat.funny.ui.my.MyOtherUserInfoActivity;
import com.sortinghat.funny.util.ConstantUtil;

/**
 * Created by wzy on 2021/6/28
 */
public class ReviewAndReplyMessageDetailsAdapter extends BaseBindingAdapter<ReviewAndReplyMessageBean, ItemMessageReviewAndReplyDetailsBinding> {

    public ReviewAndReplyMessageDetailsAdapter() {
        super(R.layout.item_message_review_and_reply_details);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindView(BaseBindingHolder holder, ReviewAndReplyMessageBean reviewAndReplyMessageBean, ItemMessageReviewAndReplyDetailsBinding binding, int position) {
        if (reviewAndReplyMessageBean != null && reviewAndReplyMessageBean.getContent() != null) {
            ReviewAndReplyMessageBean.ContentBean contentBean = reviewAndReplyMessageBean.getContent();
            setListener(contentBean, binding);
            setData(reviewAndReplyMessageBean, binding, contentBean);
        }
    }

    private void setListener(ReviewAndReplyMessageBean.ContentBean contentBean, ItemMessageReviewAndReplyDetailsBinding binding) {
        binding.rlUserIcon.setOnClickListener(new MyQuickClickListener(contentBean));
        binding.tvReviewerOrReplierNickName.setOnClickListener(new MyQuickClickListener(contentBean));
    }

    private void setData(ReviewAndReplyMessageBean reviewAndReplyMessageBean, ItemMessageReviewAndReplyDetailsBinding binding, ReviewAndReplyMessageBean.ContentBean contentBean) {
        GlideUtils.loadImageNoPlaceholder(contentBean.getThumb(), binding.ivReviewOrReplyWorksCoverImage);
        binding.tvReviewerOrReplierNickName.setText(contentBean.getNickName());
        binding.tvReviewOrReplyContent.setText(contentBean.getDetails());
        binding.tvReviewOrReplyYourWorks.setText(contentBean.getTips());
        binding.tvReviewOrReplyTime.setText(TimeUtils.millis2String(reviewAndReplyMessageBean.getCrateAt(), "yyyy-MM-dd"));
        GlideUtils.loadImage(contentBean.getAvatar(), R.mipmap.user_icon_default, binding.ivUserIcon);
        ConstantUtil.glideLoadPendantUrl(contentBean.getPendantUrl(), binding.ivBoxUserIcon);
    }

    private class MyQuickClickListener extends QuickClickListener {

        private ReviewAndReplyMessageBean.ContentBean contentBean;

        public MyQuickClickListener(ReviewAndReplyMessageBean.ContentBean contentBean) {
            this.contentBean = contentBean;
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.rl_user_icon:
                case R.id.tv_reviewer_or_replier_nick_name:
                    MyOtherUserInfoActivity.starActivity(v.getContext(), contentBean.getOtherUserId(), contentBean.getPostId());
                    break;
                default:
                    break;
            }
        }
    }
}
