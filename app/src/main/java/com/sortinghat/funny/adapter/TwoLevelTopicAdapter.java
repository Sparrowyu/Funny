package com.sortinghat.funny.adapter;

import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.TopicBean;
import com.sortinghat.funny.databinding.ItemTwoLevelTopicBinding;

/**
 * Created by wzy on 2021/6/28
 */
public class TwoLevelTopicAdapter extends BaseBindingAdapter<TopicBean.SubTopicBean, ItemTwoLevelTopicBinding> {

    public TwoLevelTopicAdapter() {
        super(R.layout.item_two_level_topic);
    }

    @Override
    protected void bindView(BaseBindingHolder holder, TopicBean.SubTopicBean subTopicBean, ItemTwoLevelTopicBinding binding, int position) {
        if (subTopicBean != null) {
            binding.tvTwoLevelTopicName.setText(subTopicBean.getName());
        }
    }
}
