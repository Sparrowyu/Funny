package com.sortinghat.funny.ui.publish;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.OneLevelTopicAdapter;
import com.sortinghat.funny.adapter.TwoLevelTopicAdapter;
import com.sortinghat.funny.bean.TopicBean;
import com.sortinghat.funny.databinding.ActivityTopicListBinding;
import com.sortinghat.funny.viewmodel.PublishTopicViewModel;

import java.util.List;

/**
 * Created by wzy on 2021/7/1
 */
public class TopicListActivity extends BaseActivity<PublishTopicViewModel, ActivityTopicListBinding> {

    private OneLevelTopicAdapter oneLevelTopicAdapter;
    private TwoLevelTopicAdapter twoLevelTopicAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_topic_list;
    }

    @Override
    protected void initViews() {
        initTitleBar("话题");
        initAdapter();
    }

    private void initAdapter() {
        contentLayoutBinding.rvOneLevelTopic.setLayoutManager(new LinearLayoutManager(this));
        oneLevelTopicAdapter = new OneLevelTopicAdapter();
        contentLayoutBinding.rvOneLevelTopic.setAdapter(oneLevelTopicAdapter);

        contentLayoutBinding.rvTwoLevelTopic.setLayoutManager(new LinearLayoutManager(this));
        twoLevelTopicAdapter = new TwoLevelTopicAdapter();
        contentLayoutBinding.rvTwoLevelTopic.setAdapter(twoLevelTopicAdapter);
    }

    @Override
    protected void setListener() {
        contentLayoutBinding.rvOneLevelTopic.setOnItemClickListener((v, position) -> {
            updateTopicData(position);
            TopicBean topicBean = oneLevelTopicAdapter.getItemData(position);
            if (topicBean != null) {
                List<TopicBean.SubTopicBean> subTopicList = topicBean.getSubTopic();
                if (subTopicList != null && !subTopicList.isEmpty()) {
                    twoLevelTopicAdapter.setNewData(subTopicList);
                }
            }
        });

        contentLayoutBinding.rvTwoLevelTopic.setOnItemClickListener((v, position) -> {
            TopicBean.SubTopicBean subTopicBean = twoLevelTopicAdapter.getItemData(position);
            String topicName = subTopicBean == null ? "" : subTopicBean.getName();
            Intent intent = new Intent();
            intent.putExtra("TOPIC_NAME", topicName);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void updateTopicData(int position) {
        List<TopicBean> topicList = oneLevelTopicAdapter.getData();
        if (topicList != null && !topicList.isEmpty()) {
            for (int i = 0; i < topicList.size(); i++) {
                TopicBean topicBean = topicList.get(i);
                if (topicBean != null) {
                    if (i == position) {
                        topicBean.setSelect(true);
                    } else {
                        topicBean.setSelect(false);
                    }
                }
            }
        }
        oneLevelTopicAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initData() {
        List<TopicBean> topicList = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            topicList = (List<TopicBean>) bundle.getSerializable("SYSTEM_TOPIC_LIST");
        }
        if (topicList != null && !topicList.isEmpty()) {
            oneLevelTopicAdapter.setNewData(topicList);
            updateTopicData(0);
            twoLevelTopicAdapter.setNewData(topicList.get(0) != null ? topicList.get(0).getSubTopic() : null);
        }
    }
}
