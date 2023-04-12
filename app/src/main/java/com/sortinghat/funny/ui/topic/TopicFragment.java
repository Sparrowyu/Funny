package com.sortinghat.funny.ui.topic;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.adapter.FragmentPagerAdapter;
import com.sortinghat.common.base.BaseFragment;
import com.sortinghat.common.base.NoViewModel;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.BannerBean;
import com.sortinghat.funny.bean.event.TopicListEvent;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.FragmentTopicBinding;
import com.sortinghat.funny.ui.my.CommonWebActivity;
import com.sortinghat.funny.viewmodel.TopicViewModel;
import com.zhpan.bannerview.BaseBannerAdapter;
import com.zhpan.bannerview.BaseViewHolder;
import com.zhpan.indicator.enums.IndicatorStyle;

import java.util.ArrayList;

/**
 * Created by wzy on 2021/6/14
 */
public class TopicFragment extends BaseFragment<TopicViewModel, FragmentTopicBinding> {

    private ArrayList<String> titleList = new ArrayList<>(4);
    private ArrayList<Fragment> fragmentList = new ArrayList<>(4);
    private BaseBannerAdapter<BannerBean> bannerAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_topic;
    }

    @Override
    protected void initViews() {
        contentLayoutBinding.getRoot().setVisibility(View.VISIBLE);
        subscibeRxBus();
        initBanner();
        initFragmentList();
        initViewPagerAdapter();
    }

    private void initBanner() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) contentLayoutBinding.bannerView.getLayoutParams();
        layoutParams.width = ScreenUtils.getScreenWidth();
        layoutParams.height = ScreenUtils.getScreenWidth() * 495 / 1125;
        contentLayoutBinding.bannerView.setLayoutParams(layoutParams);
        contentLayoutBinding.bannerView.setCanLoop(true);
        bannerAdapter = new BaseBannerAdapter<BannerBean>() {
            @Override
            protected void bindData(BaseViewHolder<BannerBean> holder, BannerBean data, int position, int pageSize) {
                GlideUtils.loadImage(data.cover, holder.findViewById(R.id.iv_banner));
            }

            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_banner;
            }
        };
        contentLayoutBinding.bannerView.setAdapter(bannerAdapter)
                .setOnPageClickListener((clickedView, position) -> {
                    CommonWebActivity.starWebActivity(getActivity(), "",
                            ((BannerBean) contentLayoutBinding.bannerView.getData().get(position)).url);
                })
                .setIndicatorStyle(IndicatorStyle.CIRCLE)
                .setLifecycleRegistry(getLifecycle())
                .create();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"));
        jsonObject.addProperty("deviceId", DeviceUtils.getUniqueDeviceId());
        viewModel.getTopicBanners(jsonObject.toString()).observe(getViewLifecycleOwner(), listBaseResultBean -> {
                   if (listBaseResultBean != null && listBaseResultBean.getData() != null) {
                       contentLayoutBinding.ivMetaverseTopCoverImage.setVisibility(View.GONE);
                       contentLayoutBinding.bannerView.refreshData(listBaseResultBean.getData());
                   }
                });
    }

    private void initFragmentList() {
        titleList.add("发现");
        titleList.add("全部");
        titleList.add("喜欢");
        titleList.add("屏蔽");
        fragmentList.add(TopicListFragment.newInstance(1));
        fragmentList.add(new AllTopicListFragment());
        fragmentList.add(TopicListFragment.newInstance(2));
        fragmentList.add(TopicListFragment.newInstance(3));
    }

    private void initViewPagerAdapter() {
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getChildFragmentManager(), fragmentList, titleList);
        contentLayoutBinding.viewPager.setAdapter(pagerAdapter);
        contentLayoutBinding.tabLayout.setupWithViewPager(contentLayoutBinding.viewPager);
    }

    @Override
    protected void initData() {
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.SWITCH_METAVERSE_TAB, Integer.class)
                .subscribe(tabIndex -> contentLayoutBinding.viewPager.setCurrentItem(tabIndex), throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.UPDATE_LIKE_OR_SHIELD_TOPIC_LIST, TopicListEvent.class)
                .subscribe(topicListEvent -> {
                    if (topicListEvent != null) {
                        if ("TopicDetailsActivity".equals(topicListEvent.source)) {
                            updateLikeAndShieldListData();
                            AllTopicListFragment allTopicListFragment = (AllTopicListFragment) fragmentList.get(1);
                            if (allTopicListFragment != null && allTopicListFragment.isInitData()) {
                                allTopicListFragment.updateLikeStatus(topicListEvent);
                            }
                        } else if ("AllTopicListFragment".equals(topicListEvent.source)) {
                            updateLikeAndShieldListData();
                        }
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));
    }

    private void updateLikeAndShieldListData() {
        TopicListFragment likeTopicListFragment = (TopicListFragment) fragmentList.get(2);
        if (likeTopicListFragment != null && likeTopicListFragment.isInitData()) {
            likeTopicListFragment.refreshData();
        }
        TopicListFragment shieldTopicListFragment = (TopicListFragment) fragmentList.get(3);
        if (shieldTopicListFragment != null && shieldTopicListFragment.isInitData()) {
            shieldTopicListFragment.refreshData();
        }
    }

    public void refreshData() {
        updateLikeAndShieldListData();
        AllTopicListFragment allTopicListFragment = (AllTopicListFragment) fragmentList.get(1);
        if (allTopicListFragment != null && allTopicListFragment.isInitData()) {
            allTopicListFragment.refreshData();
        }
    }

    public void selectIndex(int tabIndex) {
        contentLayoutBinding.viewPager.setCurrentItem(tabIndex);
    }
}
