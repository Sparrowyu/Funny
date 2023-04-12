package com.sortinghat.funny.ui.topic;

import android.annotation.SuppressLint;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BaseFragment;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.MetaverseTwoLevelTopicAdapter;
import com.sortinghat.funny.adapter.OneLevelTopicAdapter;
import com.sortinghat.funny.bean.TopicBean;
import com.sortinghat.funny.bean.event.TopicListEvent;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.databinding.ActivityTopicListBinding;
import com.sortinghat.funny.viewmodel.TopicViewModel;

import java.util.List;

/**
 * Created by wzy on 2021/10/14
 */
public class AllTopicListFragment extends BaseFragment<TopicViewModel, ActivityTopicListBinding> {

    private OneLevelTopicAdapter oneLevelTopicAdapter;
    private MetaverseTwoLevelTopicAdapter twoLevelTopicAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_topic_list;
    }

    @Override
    protected void initViews() {
        initAdapter();
    }

    private void initAdapter() {
        contentLayoutBinding.rvOneLevelTopic.setLayoutManager(new LinearLayoutManager(activity));
        oneLevelTopicAdapter = new OneLevelTopicAdapter();
        contentLayoutBinding.rvOneLevelTopic.setAdapter(oneLevelTopicAdapter);

        contentLayoutBinding.rvTwoLevelTopic.setLayoutManager(new LinearLayoutManager(activity));
        twoLevelTopicAdapter = new MetaverseTwoLevelTopicAdapter((LifecycleOwner) this, viewModel);
        contentLayoutBinding.rvTwoLevelTopic.setAdapter(twoLevelTopicAdapter);
    }

    @Override
    protected void setListener() {
        contentLayoutBinding.rvOneLevelTopic.setOnItemClickListener((v, position) -> {
            updateTopicData(position);
            TopicBean topicBean = oneLevelTopicAdapter.getItemData(position);
            if (topicBean != null) {
                twoLevelTopicAdapter.setNewData(topicBean.getSubTopic());
                contentLayoutBinding.rvTwoLevelTopic.scrollToPosition(0);
            }
        });

        contentLayoutBinding.rvTwoLevelTopic.setOnItemClickListener((v, position) -> {
            TopicBean.SubTopicBean subTopicBean = twoLevelTopicAdapter.getItemData(position);
            if (subTopicBean != null) {
                TopicDetailsActivity.starActivity(String.valueOf(subTopicBean.getId()), subTopicBean.getName());
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
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
        showLoading();
        getAllTopicList();
    }

    @Override
    protected void onReload() {
        if (!NetworkUtils.isConnected()) {
            showError();
            CommonUtils.showShort(getString(R.string.network_connect_fail_prompt));
            return;
        }
        getAllTopicList();
    }

    private void getAllTopicList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"));
        jsonObject.addProperty("deviceId", DeviceUtils.getUniqueDeviceId());

        viewModel.getAllTopicList(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    List<TopicBean> topicList = resultBean.getData();
                    if (topicList != null && !topicList.isEmpty()) {
                        oneLevelTopicAdapter.setNewData(topicList);
                        updateTopicData(0);
                        twoLevelTopicAdapter.setNewData(topicList.get(0) != null ? topicList.get(0).getSubTopic() : null);
                        showContentView();
                    } else {
                        showEmptyView();
                    }
                } else {
                    LogUtils.e(resultBean.getMsg());
                }
            } else {
                showError();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateLikeStatus(TopicListEvent topicListEvent) {
        for (TopicBean topicBean : oneLevelTopicAdapter.getData()) {
            if (topicBean != null && topicBean.getSubTopic() != null) {
                for (TopicBean.SubTopicBean subTopicBean : topicBean.getSubTopic()) {
                    if (subTopicBean != null && String.valueOf(subTopicBean.getId()).equals(topicListEvent.topicId) && subTopicBean.getName().equals(topicListEvent.topicName)) {
                        if (topicListEvent.operateType == 1) {
                            subTopicBean.setLikeStatus(1);
                        } else if (topicListEvent.operateType == 2) {
                            subTopicBean.setLikeStatus(0);
                        } else if (topicListEvent.operateType == 3) {
                            subTopicBean.setLikeStatus(2);
                        }
                        twoLevelTopicAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        }
    }

    public void refreshData() {
        getAllTopicList();
    }
}
