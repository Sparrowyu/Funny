package com.sortinghat.funny.ui.topic;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.adapter.FragmentPagerAdapter;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.event.TopicListEvent;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityTopicDetailsBinding;
import com.sortinghat.funny.ui.my.LoginActivity;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.TopicViewModel;

import java.util.ArrayList;

/**
 * Created by wzy on 2021/7/18
 */
public class TopicDetailsActivity extends BaseActivity<TopicViewModel, ActivityTopicDetailsBinding> {

    private ArrayList<String> titleList = new ArrayList<>(6);
    private ArrayList<Fragment> fragmentList = new ArrayList<>(6);

    private String topicId, topicName;

    /**
     * 0默认，1喜欢，2屏蔽
     */
    private int topicStatus = -1;

    @Override
    protected int getLayoutId() {
        titleBarBinding.getRoot().setVisibility(View.GONE);
        return R.layout.activity_topic_details;
    }

    @Override
    protected void initViews() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            topicId = bundle.getString("TOPIC_ID");
            topicName = bundle.getString("TOPIC_NAME");
        }
        subscibeRxBus();
        initFragmentList();
        initViewPagerAdapter();
    }

    private void initFragmentList() {
        titleList.add("热贴");
        titleList.add("最新");
        titleList.add("群聊");
        titleList.add("交友");
        titleList.add("CP滴滴");
        titleList.add("探索");
        fragmentList.add(TopicDetailsFragment.newInstance(0,topicId, topicName));
        fragmentList.add(TopicDetailsFragment.newInstance(1,topicId, topicName));
        fragmentList.add(TopicDetailsFragment.newInstance(2,"", topicName));
        fragmentList.add(TopicDetailsFragment.newInstance(3,"", topicName));
        fragmentList.add(TopicDetailsFragment.newInstance(4,"", topicName));
        fragmentList.add(TopicDetailsFragment.newInstance(5,"", topicName));
    }

    private void initViewPagerAdapter() {
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titleList);
        contentLayoutBinding.viewPager.setAdapter(pagerAdapter);
        contentLayoutBinding.tabLayout.setupWithViewPager(contentLayoutBinding.viewPager);
    }

    QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back_top:
                    finish();
                    break;
                case R.id.tv_like_topic: {
                    if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                        LoginActivity.starActivity(0);
                        return;
                    }
                    contentLayoutBinding.tvLikeTopic.setEnabled(false);
                    contentLayoutBinding.tvShieldTopic.setEnabled(false);
                    int likeStatus = topicStatus;
                    if (likeStatus == 1) {
                        likeStatus = 0;
                        setTopicStatus(likeStatus);
                        likeOrShieldTopic(2, likeStatus);
                    } else {
                        likeStatus = 1;
                        setTopicStatus(likeStatus);
                        likeOrShieldTopic(1, likeStatus);
                    }
                    break;
                }
                case R.id.tv_shield_topic: {
                    if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                        LoginActivity.starActivity(0);
                        return;
                    }
                    contentLayoutBinding.tvLikeTopic.setEnabled(false);
                    contentLayoutBinding.tvShieldTopic.setEnabled(false);
                    int shieldStatus = topicStatus;
                    if (shieldStatus == 2) {
                        shieldStatus = 0;
                        setTopicStatus(shieldStatus);
                        likeOrShieldTopic(4, shieldStatus);
                    } else {
                        shieldStatus = 2;
                        setTopicStatus(shieldStatus);
                        likeOrShieldTopic(3, shieldStatus);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    protected void setListener() {
        contentLayoutBinding.ivBackTop.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvLikeTopic.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvShieldTopic.setOnClickListener(quickClickListener);
    }

    @Override
    protected void initData() {
        contentLayoutBinding.tvTopicName.setText(topicName);
    }

    /**
     * @param operateType 1喜欢 2取消喜欢 3屏蔽 4取消屏蔽
     */
    private void likeOrShieldTopic(int operateType, int likeStatus) {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);
        jsonObject.addProperty("type", operateType);
        jsonObject.addProperty("topicIds", topicId);
        jsonObject.addProperty("topicNames", topicName);

        viewModel.likeOrShieldTopic(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    switch (operateType) {
                        case 1:
                            CommonUtils.showShort("已喜欢");
                            break;
                        case 2:
                            CommonUtils.showShort("已取消喜欢");
                            break;
                        case 3:
                            CommonUtils.showShort("已屏蔽");
                            break;
                        case 4:
                            CommonUtils.showShort("已取消屏蔽");
                            break;
                        default:
                            break;
                    }
                    topicStatus = likeStatus;
                    RxBus.getDefault().post(RxCodeConstant.UPDATE_LIKE_OR_SHIELD_TOPIC_LIST, new TopicListEvent("TopicDetailsActivity", topicId, topicName, operateType));
                } else {
                    LogUtils.e(resultBean.getMsg());
                    setTopicStatus(topicStatus);
                }
            } else {
                setTopicStatus(topicStatus);
            }
            contentLayoutBinding.tvLikeTopic.setEnabled(true);
            contentLayoutBinding.tvShieldTopic.setEnabled(true);
        });
    }

    private void setTopicStatus(int topicStatus) {
        if (topicStatus == 1) {
            contentLayoutBinding.tvLikeTopic.setText("已喜欢");
            contentLayoutBinding.tvShieldTopic.setText("屏蔽");
            contentLayoutBinding.tvLikeTopic.setTextColor(CommonUtils.getColor(R.color.color_666666));
            contentLayoutBinding.tvLikeTopic.setBackground(CommonUtils.getDrawable(R.drawable.shape_white_bg_gray_border));
            contentLayoutBinding.tvShieldTopic.setTextColor(CommonUtils.getColor(R.color.white));
            contentLayoutBinding.tvShieldTopic.setBackground(CommonUtils.getDrawable(R.drawable.shape_orange_bg_with_corner));
        } else if (topicStatus == 2) {
            contentLayoutBinding.tvLikeTopic.setText("喜欢");
            contentLayoutBinding.tvShieldTopic.setText("已屏蔽");
            contentLayoutBinding.tvLikeTopic.setTextColor(CommonUtils.getColor(R.color.white));
            contentLayoutBinding.tvLikeTopic.setBackground(CommonUtils.getDrawable(R.drawable.shape_orange_bg_with_corner));
            contentLayoutBinding.tvShieldTopic.setTextColor(CommonUtils.getColor(R.color.color_666666));
            contentLayoutBinding.tvShieldTopic.setBackground(CommonUtils.getDrawable(R.drawable.shape_white_bg_gray_border));
        } else {
            contentLayoutBinding.tvLikeTopic.setText("喜欢");
            contentLayoutBinding.tvShieldTopic.setText("屏蔽");
            contentLayoutBinding.tvLikeTopic.setTextColor(CommonUtils.getColor(R.color.white));
            contentLayoutBinding.tvLikeTopic.setBackground(CommonUtils.getDrawable(R.drawable.shape_orange_bg_with_corner));
            contentLayoutBinding.tvShieldTopic.setTextColor(CommonUtils.getColor(R.color.white));
            contentLayoutBinding.tvShieldTopic.setBackground(CommonUtils.getDrawable(R.drawable.shape_orange_bg_with_corner));
        }
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.LOGIN_STATUS_CHANGE, Boolean.class)
                .subscribe(isLogin -> {
                    if (isLogin) {
                        contentLayoutBinding.viewPager.setCurrentItem(0);
                        TopicDetailsFragment topicDetailsFragment = (TopicDetailsFragment) fragmentList.get(0);
                        if (topicDetailsFragment != null && topicDetailsFragment.isInitData()) {
                            topicDetailsFragment.refreshData();
                        }
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));
    }

    public void updateTopicStatus(int topicStatus) {
        this.topicStatus = topicStatus;
        setTopicStatus(topicStatus);
    }

    public static void starActivity(String topicId, String topicName) {
        Bundle bundle = new Bundle();
        bundle.putString("TOPIC_ID", topicId);
        bundle.putString("TOPIC_NAME", topicName);
        ActivityUtils.startActivity(bundle, TopicDetailsActivity.class);
    }
}
