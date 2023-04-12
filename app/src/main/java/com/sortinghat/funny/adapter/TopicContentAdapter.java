package com.sortinghat.funny.adapter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.TopicListBean;
import com.sortinghat.funny.databinding.ItemTopicContentBinding;

/**
 * Created by wzy on 2021/6/28
 */
public class TopicContentAdapter extends BaseBindingAdapter<TopicListBean, ItemTopicContentBinding> {

    private int topicListType;

    public TopicContentAdapter(int topicListType) {
        super(R.layout.item_topic_content);
        this.topicListType = topicListType;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindView(BaseBindingHolder holder, TopicListBean likesBean, ItemTopicContentBinding binding, int position) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.ivTopicPostImage.getLayoutParams();
        int imageSize = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(40)) / 3;
        params.width = imageSize;
        params.height = imageSize;
        binding.ivTopicPostImage.setLayoutParams(params);

        if (likesBean != null) {
            if (topicListType == 1) {
                GlideUtils.loadRoundImage(likesBean.getTopicIcon(), R.mipmap.default_topic_post_image, 10, binding.ivTopicPostImage);
            } else {
                if (!TextUtils.isEmpty(likesBean.getThumb())) {
                    GlideUtils.loadRoundImage(likesBean.getThumb(), R.mipmap.default_topic_post_image, 10, binding.ivTopicPostImage);
                } else if (!TextUtils.isEmpty(likesBean.getUrl())) {
                    GlideUtils.loadRoundImage(likesBean.getUrl(), R.mipmap.default_topic_post_image, 10, binding.ivTopicPostImage);
                } else {
                    binding.ivTopicPostImage.setImageResource(R.mipmap.default_topic_post_image);
                }
            }
            binding.tvTopicName.setText(likesBean.getName());
            binding.tvTopicName.setVisibility(TextUtils.isEmpty(likesBean.getName()) ? View.GONE : View.VISIBLE);
        }
    }
}
