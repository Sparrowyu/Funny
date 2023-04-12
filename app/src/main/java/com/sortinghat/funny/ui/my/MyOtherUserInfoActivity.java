package com.sortinghat.funny.ui.my;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.sortinghat.common.adapter.FragmentPagerAdapter;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.DetailListBean;
import com.sortinghat.funny.bean.OtherUserInfoBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityMyOtherUserInfoBinding;
import com.sortinghat.funny.ui.BottomSheetDialog.BigImgDialog;
import com.sortinghat.funny.ui.home.FeedBackActivity;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.MyFragmentViewModel;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;

public class MyOtherUserInfoActivity extends BaseActivity<MyFragmentViewModel, ActivityMyOtherUserInfoBinding> {
    private ArrayList<String> titleList = new ArrayList<>(2);
    private ArrayList<Fragment> fragmentList = new ArrayList<>(2);
    private int currentIndex = 0;

    private OtherUserHeaderView headerView;

    private long otherUserId = 0;
    private long postId = 0;
    private String otherLikeCount = "";

    @Override
    protected int getLayoutId() {
        titleBarBinding.getRoot().setVisibility(View.GONE);
        return R.layout.activity_my_other_user_info;
    }

    @Override
    protected void initViews() {
        if (getIntent() != null) {
            otherUserId = getIntent().getLongExtra("otherUserId", 0);
            postId = getIntent().getLongExtra("postId", 0);
        }
        setStatusBar(true);
        setStatusBarMode(true);
        subscibeRxBus();
        initFragmentList();
        initViewPagerAdapter();
        setappbarlayoutPercent();
        headerView = new OtherUserHeaderView(this);
        if (headerView.getParent() == null) {
            contentLayoutBinding.header.removeAllViews();
            contentLayoutBinding.header.addView(headerView);
            headerView.setListener(quickClickListener);
        }
    }

    private void initFragmentList() {
        titleList.clear();
        fragmentList.clear();
        titleList.add("作品");
//        titleList.add("评论");
        titleList.add("喜欢");
        fragmentList.add(new MyLikeImgFragment(0, otherUserId, postId));
//        fragmentList.add(new MyLikeImgFragment(1, otherUserId, postId));
        fragmentList.add(new MyLikeImgFragment(1, otherUserId, postId));
    }

    private void initViewPagerAdapter() {
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titleList);
        contentLayoutBinding.viewPager.setAdapter(pagerAdapter);
        contentLayoutBinding.tabLayout.setupWithViewPager(contentLayoutBinding.viewPager);
        contentLayoutBinding.viewPager.setCurrentItem(currentIndex);
        contentLayoutBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setappbarlayoutPercent() {
        coordinatorLayoutBackTop();
        contentLayoutBinding.ivTopBack.setOnClickListener(quickClickListener);
        contentLayoutBinding.toolbar.setContentInsetsAbsolute(0, 0);
        contentLayoutBinding.appbarlayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appbarlayout, int verticalOffset) {
                float percent = Math.abs(verticalOffset * 1.0f) / appbarlayout.getTotalScrollRange(); //滑动比例
                if (percent > 0.8) {
                    contentLayoutBinding.toolbar.setVisibility(View.VISIBLE);
                    contentLayoutBinding.tvName.setVisibility(View.VISIBLE);
                    float alpha = 1 - (1 - percent) * 5; //渐变变换
                    contentLayoutBinding.layoutToolbar.setAlpha(alpha);
                    contentLayoutBinding.tvName.setAlpha(alpha);
                    setStatusBarMode(true);
                } else {
                    contentLayoutBinding.toolbar.setVisibility(View.GONE);
                    contentLayoutBinding.tvName.setVisibility(View.GONE);
                    setStatusBarMode(false);
                }
            }
        });
    }

    private void coordinatorLayoutBackTop() {
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) contentLayoutBinding.appbarlayout.getBehavior();
        if (behavior instanceof AppBarLayout.Behavior) {
            AppBarLayout.Behavior appbarber = behavior;
            int topOff = appbarber.getTopAndBottomOffset();
            if (topOff != 0) {
                appbarber.setTopAndBottomOffset(0);
            }
        }
    }

    @Override
    protected void initData() {

        progressDialog = MaterialDialogUtil.showCustomProgressDialog(this);
        viewModel.getOtherUserInfo(otherUserId).observe(this, resultBean -> {
            closeProgressDialog();
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    otherUserInfoBean = resultBean.getData();
                    headerView.setShowData(resultBean.getData());
                    contentLayoutBinding.tvName.setText(resultBean.getData().getNickname());
                    otherLikeCount = "“" + resultBean.getData().getNickname() + "”" + "共获得" + resultBean.getData().getLikedCount() + "个点赞";
                }
            }
        });
    }

    private OtherUserInfoBean otherUserInfoBean;

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.iv_header_back:
                case R.id.iv_top_back:
                    finish();
                    break;
                case R.id.tv_attention://关注
                    if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                        CommonUtils.showShort("请先登录");
                        return;
                    }
                    if (otherUserInfoBean != null) {
                        viewModel.getUserFollowList(otherUserInfoBean.getUserId(), (otherUserInfoBean.getMutualFollow() == 2 || otherUserInfoBean.getMutualFollow() == 1) ? 0 : 1).observe((LifecycleOwner) mContext, resultBean -> {
                            if (resultBean != null && headerView != null) {
                                if (resultBean.getCode() == 0) {
                                    int fansCount = otherUserInfoBean.getFansCount();
                                    if (otherUserInfoBean.getMutualFollow() == 0) {
                                        otherUserInfoBean.setMutualFollow(2);
                                        otherUserInfoBean.setFansCount(fansCount + 1);
                                        headerView.setAttention(2, fansCount + 1);
                                        CommonUtils.showShort("关注成功");
                                    } else if (otherUserInfoBean.getMutualFollow() == 1) {
                                        otherUserInfoBean.setFansCount(fansCount - 1);
                                        otherUserInfoBean.setMutualFollow(3);
                                        headerView.setAttention(3, fansCount - 1);
                                        CommonUtils.showShort("取消成功");
                                    } else if (otherUserInfoBean.getMutualFollow() == 2) {
                                        otherUserInfoBean.setMutualFollow(0);
                                        otherUserInfoBean.setFansCount(fansCount - 1);
                                        headerView.setAttention(0, fansCount - 1);
                                        CommonUtils.showShort("取消成功");
                                    } else {
                                        otherUserInfoBean.setMutualFollow(1);
                                        otherUserInfoBean.setFansCount(fansCount + 1);
                                        headerView.setAttention(1, fansCount + 1);
                                        CommonUtils.showShort("关注成功");
                                    }
                                    int followStatus = otherUserInfoBean.getMutualFollow();
                                    if (((MyLikeImgFragment) fragmentList.get(currentIndex)) != null) {
                                        ((MyLikeImgFragment) fragmentList.get(currentIndex)).updataListInfoFollowStatus(followStatus);
                                    }

                                    String sendS = otherUserInfoBean.getUserId() + "," + followStatus;
                                    RxBus.getDefault().post(RxCodeConstant.HOME_VIDEO_UPDATA_AUTHOR_FOLLOW, sendS);
                                }
                            }
                        });
                    }
                    break;

                case R.id.tv_feedback_other:
                    ActivityUtils.startActivity(FeedBackActivity.class);
                    break;
                case R.id.iv_set_other_more:
                    ActivityUtils.startActivity(SettingActivity.class);
                    break;
                case R.id.iv_user_icon:
                    showBigImgDialog(otherUserInfoBean.getAvatar());
                    break;
                case R.id.layout_user_like:
                    ConstantUtil.createUmEvent("other_fragment_click_mine_like");//他人页点击个人获赞数
                    if (!TextUtils.isEmpty(otherLikeCount)) {
                        ConstantUtil.showLikeDialog(mContext, otherLikeCount);
                    }
                    break;
                case R.id.layout_user_no_mood_report:
                    CommonUtils.showShort("心情报告只有本人可以看");
                    break;
            }
        }
    };

    BigImgDialog bigImgDialog;

    private void showBigImgDialog(String curUrl) {
        if (TextUtils.isEmpty(curUrl) || (bigImgDialog != null && bigImgDialog.isVisible())) {
            return;
        }
        bigImgDialog = new BigImgDialog(curUrl);
        if (this.isStateEnable()) {
            bigImgDialog.show(getSupportFragmentManager(), "avatar");
        }
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.MY_OTHER_UPDATA_LIST_INFO, DetailListBean.class)
                .subscribe(detailListBean -> {
                    if (((MyLikeImgFragment) fragmentList.get(currentIndex)) != null) {
                        ((MyLikeImgFragment) fragmentList.get(currentIndex)).updataListInfo(detailListBean);
                    }
                }));
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.USERINFO_UPDATA_AUTHOR_FOLLOW, Integer.class)
                .subscribe(followStatus -> {
                    initData();
                }));
    }

    public static void starActivity(Context mContext, long otherUserId, long postId) {
        Intent intent = new Intent(mContext, MyOtherUserInfoActivity.class);
        intent.putExtra("otherUserId", otherUserId);
        intent.putExtra("postId", postId);
        ActivityUtils.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }
}

