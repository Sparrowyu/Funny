package com.sortinghat.funny.ui.publish;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.TopicBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityPublishPostBinding;
import com.sortinghat.funny.databinding.ItemRecommendTopicBinding;
import com.sortinghat.funny.databinding.ItemTwoLevelTopicBinding;
import com.sortinghat.funny.thirdparty.album.AlbumFile;
import com.sortinghat.funny.ui.my.UploadVideoViewActivity;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.business.TopicIdentifyUtil;
import com.sortinghat.funny.viewmodel.PublishTopicViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wzy on 2021/6/30
 */
public class PublishPostActivity extends BaseActivity<PublishTopicViewModel, ActivityPublishPostBinding> {

    private BaseBindingAdapter<TopicBean.SubTopicBean, ItemRecommendTopicBinding> recommendTopicAdapter;
    private BaseBindingAdapter<String, ItemTwoLevelTopicBinding> searchTopicAdapter;

    private MyTextWathcer myTextWathcer;

    private AlbumFile albumFile;

    private boolean isGetRecommendTopicFailed;

    private List<TopicBean> systemTopicList = new ArrayList<>();
    private List<String> topicNameList = new ArrayList<>();
    private ArrayMap<Integer, Integer> topicNameMap = new ArrayMap<>(5);

    private int editTextCursorPosition;
    private String searchTopicName;

    private StringBuilder topicIds = new StringBuilder();
    private StringBuilder topicNames = new StringBuilder();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish_post;
    }

    @Override
    protected void initViews() {
        initTitleBar("发布", "发布");
        titleBarBinding.tvRightText.setTextColor(getResources().getColor(R.color.light_orange));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            albumFile = bundle.getParcelable("ALBUM_FILE");
        }
        initAdapter();
    }

    private void initAdapter() {
        recommendTopicAdapter = new BaseBindingAdapter<TopicBean.SubTopicBean, ItemRecommendTopicBinding>(R.layout.item_recommend_topic, null) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void bindView(BaseBindingHolder holder, TopicBean.SubTopicBean subTopicBean, ItemRecommendTopicBinding binding, int position) {
                if (subTopicBean != null) {
                    binding.tvRecommendTopicName.setText("#" + subTopicBean.getName());
                }
            }
        };
        contentLayoutBinding.rvRecommendTopic.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        contentLayoutBinding.rvRecommendTopic.setAdapter(recommendTopicAdapter);

        searchTopicAdapter = new BaseBindingAdapter<String, ItemTwoLevelTopicBinding>(R.layout.item_two_level_topic, null) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void bindView(BaseBindingHolder holder, String topicName, ItemTwoLevelTopicBinding binding, int position) {
                binding.tvTwoLevelTopicName.setTextColor(CommonUtils.getColor(R.color.color_666666));
                if (!TextUtils.isEmpty(topicName)) {
                    binding.tvTwoLevelTopicName.setText("#" + topicName);
                }
            }
        };
        contentLayoutBinding.rvSearchTopic.setLayoutManager(new LinearLayoutManager(this));
        contentLayoutBinding.rvSearchTopic.setAdapter(searchTopicAdapter);
    }

    @Override
    protected void onRightTextClick() {
        publishPost(0);
    }

    private QuickClickListener listener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.rl_add_topic:
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SYSTEM_TOPIC_LIST", (Serializable) systemTopicList);
                    ActivityUtils.startActivityForResult(bundle, PublishPostActivity.this, TopicListActivity.class, 1);
                    break;
                case R.id.rl_save_manuscript:
                    break;
                case R.id.rl_pulish_post:
                    publishPost(1);
                    break;
                case R.id.iv_post_cover_image://点击图片预览
                    if (albumFile != null) {
                        UploadVideoViewActivity.starActivity(mContext,albumFile.getPath(),albumFile.getMediaType());
//                        GlideUtils.loadImageWithPath(albumFile.getPath(), contentLayoutBinding.ivPostCoverImage);
                    }else {
                        CommonUtils.showShort("请选择帖子");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void publishPost(int clickPublishPostType) {
        String postTitle = contentLayoutBinding.etPostTitle.getText().toString();
        if (TextUtils.isEmpty(postTitle)) {
            CommonUtils.showShort("标题不能为空");
            return;
        }
        if (clickPublishPostType == 0) {
            ConstantUtil.createUmEvent("publish_sent_top_click");//发布按钮右上角
        } else {
            ConstantUtil.createUmEvent("publish_sent_bottom_click");//发布按钮底部
        }
        addTopicIdAndName();
        albumFile.setPostTitle(postTitle);
        albumFile.setTopicIds(topicIds.toString());
        albumFile.setTopicNames(topicNames.toString());
        RxBus.getDefault().post(RxCodeConstant.PUBLISH_POST, albumFile);
        finish();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void setListener() {
        contentLayoutBinding.rlAddTopic.setOnClickListener(listener);
        contentLayoutBinding.rlSaveManuscript.setOnClickListener(listener);
        contentLayoutBinding.rlPulishPost.setOnClickListener(listener);
        contentLayoutBinding.ivPostCoverImage.setOnClickListener(listener);

        myTextWathcer = new MyTextWathcer();
        contentLayoutBinding.etPostTitle.addTextChangedListener(myTextWathcer);

        contentLayoutBinding.rvRecommendTopic.setOnItemClickListener((v, position) -> {
            TopicBean.SubTopicBean subTopicBean = recommendTopicAdapter.getItemData(position);
            if (subTopicBean != null) {
                insertTopicNameToEditText(subTopicBean.getName());
            }
        });

        contentLayoutBinding.rvSearchTopic.setOnItemClickListener((v, position) -> {
            searchTopicName = searchTopicAdapter.getItemData(position);
            Editable edit = contentLayoutBinding.etPostTitle.getEditableText();
            int index = contentLayoutBinding.etPostTitle.getSelectionStart();
            if (index == 0) {
                edit.insert(index, "#" + searchTopicAdapter.getItemData(position));
                return;
            } else {
                if (topicNameMap != null && !topicNameMap.isEmpty()) {
                    for (Map.Entry<Integer, Integer> entry : topicNameMap.entrySet()) {
                        if (index >= entry.getKey() && index <= entry.getValue()) {
                            edit.replace(entry.getKey(), entry.getValue(), searchTopicAdapter.getItemData(position));
                            return;
                        }
                    }
                }
            }
            searchTopicAdapter.getData().clear();
            searchTopicAdapter.notifyDataSetChanged();
        });
    }

    private class MyTextWathcer implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 90) {
                CommonUtils.showShort("标题最长可填写90个字");
            }
            topicNameList.clear();
            topicNameMap.clear();
            searchTopicAdapter.getData().clear();
            searchTopicAdapter.notifyDataSetChanged();
            if (s.length() > 0) {
                contentLayoutBinding.etPostTitle.removeTextChangedListener(myTextWathcer);
                editTextCursorPosition = contentLayoutBinding.etPostTitle.getSelectionStart();
                String topicName = TopicIdentifyUtil.identifyTopic(s.toString(), contentLayoutBinding.etPostTitle, topicNameList, topicNameMap, editTextCursorPosition);
                contentLayoutBinding.etPostTitle.setSelection(editTextCursorPosition);
                contentLayoutBinding.etPostTitle.addTextChangedListener(myTextWathcer);
                if (!TextUtils.isEmpty(topicName)) {
                    if (!topicName.equals(searchTopicName)) {
                        searchTopicName = topicName;
                        startSearchTopic();
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void startSearchTopic() {
        searchTopicAdapter.getData().clear();
        for (TopicBean topicBean : systemTopicList) {
            if (topicBean != null) {
                if (!TextUtils.isEmpty(topicBean.getName()) && topicBean.getName().regionMatches(true, 0, searchTopicName, 0, searchTopicName.length())) {
                    searchTopicAdapter.getData().add(topicBean.getName());
                }
                List<TopicBean.SubTopicBean> subTopicList = topicBean.getSubTopic();
                if (subTopicList != null && !subTopicList.isEmpty()) {
                    for (TopicBean.SubTopicBean subTopicBean : subTopicList) {
                        if (subTopicBean != null) {
                            if (!TextUtils.isEmpty(subTopicBean.getName()) && subTopicBean.getName().regionMatches(true, 0, searchTopicName, 0, searchTopicName.length())) {
                                searchTopicAdapter.getData().add(subTopicBean.getName());
                            }
                        }
                    }
                }
            }
        }
        searchTopicAdapter.notifyDataSetChanged();
    }

    private void addTopicIdAndName() {
        for (String topicName : topicNameList) {
            int topicId = -1;
            inputTopic:
            for (TopicBean topicBean : systemTopicList) {
                if (topicBean != null) {
                    if (topicName.equals(topicBean.getName())) {
                        topicId = topicBean.getId();
                        break;
                    }
                    List<TopicBean.SubTopicBean> subTopicList = topicBean.getSubTopic();
                    if (subTopicList != null && !subTopicList.isEmpty()) {
                        for (TopicBean.SubTopicBean subTopicBean : subTopicList) {
                            if (subTopicBean != null) {
                                if (topicName.equals(subTopicBean.getName())) {
                                    topicId = subTopicBean.getId();
                                    break inputTopic;
                                }
                            }
                        }
                    }
                }
            }
            addTopicIdAndName(topicId, topicName);
        }
    }

    private void addTopicIdAndName(int topicId, String topicName) {
        if (topicIds.length() == 0) {
            topicIds.append(topicId);
        } else {
            topicIds.append(",");
            topicIds.append(topicId);
        }
        topicNames.append("#");
        topicNames.append(topicName);
    }

    @Override
    protected void initData() {
        if (albumFile != null) {
            GlideUtils.loadImageWithPath(albumFile.getPath(), contentLayoutBinding.ivPostCoverImage);
        }
        showLoading();
        getRecommendTopicList();
    }

    @Override
    protected void onReload() {
        if (!NetworkUtils.isConnected()) {
            showError();
            CommonUtils.showShort(getString(R.string.network_connect_fail_prompt));
            return;
        }
        if (isGetRecommendTopicFailed) {
            getRecommendTopicList();
        } else {
            getSystemTopicList();
        }
    }

    private void getRecommendTopicList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"));
        jsonObject.addProperty("deviceId", DeviceUtils.getUniqueDeviceId());

        viewModel.getRecommendTopicList(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    List<TopicBean.SubTopicBean> topicList = resultBean.getData();
                    if (topicList != null && !topicList.isEmpty()) {
                        recommendTopicAdapter.setNewData(topicList);
                    }
                    getSystemTopicList();
                } else {
                    LogUtils.e(resultBean.getMsg());
                }
            } else {
                isGetRecommendTopicFailed = true;
                showError();
            }
        });
    }

    private void getSystemTopicList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"));
        jsonObject.addProperty("deviceId", DeviceUtils.getUniqueDeviceId());

        viewModel.getSystemTopicList(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    showContentView();
                    List<TopicBean> topicList = resultBean.getData();
                    if (topicList != null && !topicList.isEmpty()) {
                        systemTopicList = topicList;
                    }
                } else {
                    LogUtils.e(resultBean.getMsg());
                }
            } else {
                showError();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                String topicName = data.getStringExtra("TOPIC_NAME");
                insertTopicNameToEditText(topicName);
            }
        }
    }

    private void insertTopicNameToEditText(String topicName) {
        searchTopicName = topicName;
        Editable edit = contentLayoutBinding.etPostTitle.getEditableText();
        int index = contentLayoutBinding.etPostTitle.getSelectionStart();
        if (index < 0 || index >= edit.length()) {
            edit.append("#" + topicName);
        } else {
            edit.insert(index, "#" + topicName);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                KeyboardUtils.hideSoftInput(this);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // Return whether touch the view
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationOnScreen(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            return !(event.getRawX() > left && event.getRawX() < right
                    && event.getRawY() > top && event.getRawY() < bottom);
        }
        return false;
    }
}
