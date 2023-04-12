package com.sortinghat.funny.ui.my;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.sortinghat.common.adapter.FragmentPagerAdapter;
import com.sortinghat.common.base.BaseFragment;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.statusbar.StatusBarFontColorUtil;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.DetailListBean;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.bean.TaskMessageBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.FragmentMyBinding;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.viewmodel.MyFragmentViewModel;

import java.util.ArrayList;

/**
 * Created by wzy on 2021/6/14
 */
public class MyFragment extends BaseFragment<MyFragmentViewModel, FragmentMyBinding> {

    private MyFragmentHeaderView headerView;
    private String TAG = "Myfragment---";


    private ArrayList<String> titleList = new ArrayList<>(3);
    private ArrayList<Fragment> fragmentList = new ArrayList<>(3);
    private int currentIndex = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initViews() {
        contentLayoutBinding.getRoot().setVisibility(View.VISIBLE);
        initFragmentList();
        initViewPagerAdapter();
        setappbarlayoutPercent();
        subscibeRxBus();
        headerView = new MyFragmentHeaderView(activity);
        if (headerView.getParent() == null) {
            contentLayoutBinding.header.removeAllViews();
            contentLayoutBinding.header.addView(headerView);
        }
    }

    @Override
    protected void initData() {

        viewModel.getOwnerUserInfo().observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    headerView.setShowData(resultBean.getData());
                    contentLayoutBinding.tvName.setText(resultBean.getData().getUserBase().getNickname());
                    if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) != 0) {
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_bind_phone", TextUtils.isEmpty(resultBean.getData().getUserBase().getPhoneNumShow()) ? 0 : 1);
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_avatar", resultBean.getData().getUserBase().getAvatar());
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_nike_name", resultBean.getData().getUserBase().getNickname());
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_info", GsonUtils.toJson(resultBean));
                    } else {
                        setStatusBarMode(true);
                    }
                }
            }
        });

        if (SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(Constant.ANDROID_TASK_SYS_AB, false)) {
            viewModel.getTaskMessageCount().observeForever(resultBean -> {
                if (resultBean != null) {
                    if (resultBean.getCode() == 0) {
                        if (resultBean.getData() != null) {
                            RxBus.getDefault().postSticky(resultBean.getData());
                        }
                    }
                }
            });
        }
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.UPDATE_MYFRAGMENT, Integer.class)
                .subscribe(integer -> {
                    initData();
                    if (((MyLikeImgFragment) fragmentList.get(currentIndex)) != null) {
                        ((MyLikeImgFragment) fragmentList.get(currentIndex)).refreshData();
                    }
                }));
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.UPDATE_MYFRAGMENT_HEADER, Integer.class)
                .subscribe(integer -> {
                    initData();
                }));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.MY_UPDATA_LIST_INFO, DetailListBean.class)
                .subscribe(detailListBean -> {
                    if (detailListBean == null || detailListBean.getHomeVideoBeanList() == null || detailListBean.getHomeVideoBeanList().size() < 1) {
                        return;
                    }
                    ((MyLikeImgFragment) fragmentList.get(currentIndex)).updataListInfo(detailListBean);
                }));

        //更新关注数
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.HOME_VIDEO_UPDATA_AUTHOR_FOLLOW, String.class)
                .subscribe(authorIdAndFollowStatus -> {
                    if (!TextUtils.isEmpty(authorIdAndFollowStatus) && authorIdAndFollowStatus.contains(",")) {
                        String[] authorArr = authorIdAndFollowStatus.split(","); //前为作者ID后为是否关注
                        if (headerView != null) {
                            headerView.setFollowCount(Integer.parseInt(authorArr[1]));
                        }
                    }
                }));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.MY_HOME_LIST_INFO_DELETE, Integer.class)
                .subscribe(integer -> {
                    if (((MyLikeImgFragment) fragmentList.get(currentIndex)) != null) {
                        ((MyLikeImgFragment) fragmentList.get(currentIndex)).removeData(integer);
                    }
                }));
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.TASK_TO_UPDATE_MINE_USER_BOX, String.class)
                .subscribe(pendantUrl -> {
                    if (headerView != null) {
                        headerView.updateUserIcon(pendantUrl);
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservableSticky(TaskMessageBean.class).subscribe(taskMessageBean -> {
            if (taskMessageBean != null) {
                if (headerView != null) {
                    headerView.updateTaskMessageRed(taskMessageBean);
                }
            }
        }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));
    }

    private void initFragmentList() {
        titleList.clear();
        fragmentList.clear();
        titleList.add("作品");
        titleList.add("评论");
        titleList.add("喜欢");
        fragmentList.add(new MyLikeImgFragment(0, 0, 0));
        fragmentList.add(new MyLikeImgFragment(1, 0, 0));
        fragmentList.add(new MyLikeImgFragment(2, 0, 0));
    }

    private void initViewPagerAdapter() {
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getChildFragmentManager(), fragmentList, titleList);
        contentLayoutBinding.viewPager.setAdapter(pagerAdapter);
        contentLayoutBinding.tabLayout.setupWithViewPager(contentLayoutBinding.viewPager);
        contentLayoutBinding.viewPager.setCurrentItem(currentIndex);
        contentLayoutBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position < fragmentList.size() && fragmentList.get(position) != null) {
//                    ((MyLikeImgFragment) fragmentList.get(position)).refreshData();
                }
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setappbarlayoutPercent() {
        coordinatorLayoutBackTop();
        contentLayoutBinding.ivSetMore.setOnClickListener(quickClickListener);
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
                    if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                        setStatusBarMode(true);
                    } else {
                        setStatusBarMode(false);
                    }

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

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {

            switch (v.getId()) {
                case R.id.iv_set_more:
                case R.id.iv_set_no_more:
                    ActivityUtils.startActivity(SettingActivity.class);
                    break;

            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (isInitData() && !hidden) {
            if (null != headerView) {
                headerView.updateView();
            }
            contentLayoutBinding.viewPager.setCurrentItem(currentIndex);
            if (ConstantUtil.isUpdataMyFragmentList) {
                ConstantUtil.isUpdataMyFragmentList = false;
                if (((MyLikeImgFragment) fragmentList.get(currentIndex)) != null) {
                    ((MyLikeImgFragment) fragmentList.get(currentIndex)).refreshData();
                }
            }
            if (ConstantUtil.isUpdataMyFragmentHeader) {
                ConstantUtil.isUpdataMyFragmentHeader = false;
                initData();
            }
        }
    }

    public void updatePostLikeOrUnlikeOrReview(HomeVideoImageListBean.ListBean.ContentBean videoOrImageContent) {
        if (contentLayoutBinding.viewPager.getCurrentItem() == 0) {
            ((MyLikeImgFragment) fragmentList.get(0)).updatePostLikeOrUnlikeOrReview(videoOrImageContent);
        } else if (contentLayoutBinding.viewPager.getCurrentItem() == 1) {
            ((MyLikeImgFragment) fragmentList.get(1)).updatePostLikeOrUnlikeOrReview(videoOrImageContent);
        } else if (contentLayoutBinding.viewPager.getCurrentItem() == 2) {
            ((MyLikeImgFragment) fragmentList.get(2)).updatePostLikeOrUnlikeOrReview(videoOrImageContent);
        }
    }
}
