package com.sortinghat.funny.adapter;

import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.TopicBean;
import com.sortinghat.funny.databinding.ItemOneLevelTopicBinding;

/**
 * Created by wzy on 2021/6/28
 */
public class OneLevelTopicAdapter extends BaseBindingAdapter<TopicBean, ItemOneLevelTopicBinding> {

    public OneLevelTopicAdapter() {
        super(R.layout.item_one_level_topic);
    }

    @Override
    protected void bindView(BaseBindingHolder holder, TopicBean topicBean, ItemOneLevelTopicBinding binding, int position) {
        if (topicBean != null) {
            if (topicBean.isSelect()) {
                binding.getRoot().setBackgroundColor(CommonUtils.getColor(R.color.white));
                binding.tvOneLevelTopicName.setTextColor(CommonUtils.getColor(R.color.color_333333));
            } else {
                binding.getRoot().setBackgroundColor(CommonUtils.getColor(R.color.page_bg));
                binding.tvOneLevelTopicName.setTextColor(CommonUtils.getColor(R.color.color_666666));
            }
            binding.tvOneLevelTopicName.setText(topicBean.getName());
        }
    }
}
