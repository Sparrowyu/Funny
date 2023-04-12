package com.sortinghat.funny.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.sortinghat.common.adapter.FragmentPagerAdapter;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.base.BaseFragment;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.bean.TaskMessageBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.DialogHomePublishGuideBinding;
import com.sortinghat.funny.databinding.FragmentHomeBinding;
import com.sortinghat.funny.interfaces.RequestCallback;
import com.sortinghat.funny.thirdparty.album.AlbumFile;
import com.sortinghat.funny.thirdparty.album.util.AlbumUtils;
import com.sortinghat.funny.ui.my.BotChatWebActivity;
import com.sortinghat.funny.ui.my.TaskCentralActivity;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DateUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.PublishViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by wzy on 2021/6/14
 */
public class HomeFragment extends BaseFragment<PublishViewModel, FragmentHomeBinding> {

    private ArrayList<String> titleList = new ArrayList<>(2);
    private ArrayList<Fragment> fragmentList = new ArrayList<>(2);

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initViews() {
        contentLayoutBinding.getRoot().setVisibility(View.VISIBLE);
        initFragmentList();
        initViewPagerAdapter();
        subscibeRxBus();
    }

    private void initFragmentList() {
        titleList.add("视频");
        titleList.add("图文");
        fragmentList.add(new HomeVideoFragment());
        fragmentList.add(new HomeImageTextFragment());
        showSharePost(ConstantUtil.isShareInShowPostId, ConstantUtil.isShareParamType);
    }

    private void initViewPagerAdapter() {
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getChildFragmentManager(), fragmentList, titleList);
        contentLayoutBinding.viewPager.setAdapter(pagerAdapter);
        contentLayoutBinding.tabLayout.setupWithViewPager(contentLayoutBinding.viewPager);
        initTabRed();
    }

    private void initTabRed() {
        TabLayout.Tab tab;
        //加载自定义显示小红点的布局
        tab = contentLayoutBinding.tabLayout.getTabAt(1);
        tab.setCustomView(R.layout.tab_layout_red_tx);
        TextView tv_tab_title = tab.getCustomView().findViewById(R.id.tv_tab_title);
        tv_tab_title.setText("图文");
        TextView tv_tab_red = tab.getCustomView().findViewById(R.id.iv_tab_red);

        //图文一天亮一次
        String date = SPUtils.getInstance("user_info").getString("home_image_text_red_date");
        String dateSp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (dateSp.equals(date)) {
            tv_tab_red.setVisibility(View.INVISIBLE);
        } else {
            tv_tab_red.setVisibility(View.VISIBLE);
        }

        //添加tabLayout选中监听
        contentLayoutBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //设置选中时的文字颜色
                if (tab.getCustomView() != null) {
                    tv_tab_title.setTextColor(getResources().getColor(R.color.white));
                    if (tab.getPosition() == 1) {
                        SPUtils.getInstance("user_info").put("home_image_text_red_date", dateSp);
                        tv_tab_red.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //设置未选中时的文字颜色
                if (tab.getCustomView() != null) {
                    tv_tab_title.setTextColor(getResources().getColor(R.color.color_tab_fff));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    protected void setListener() {
        contentLayoutBinding.rlPostUploadProgress.setOnClickListener(quickClickListener);
        if (SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(Constant.HOME_TASK_SHOW, true)) {
            contentLayoutBinding.homeTaskRl.setVisibility(View.VISIBLE);
        } else {
            contentLayoutBinding.homeTaskRl.setVisibility(View.GONE);
        }
        contentLayoutBinding.homeTaskRl.setOnClickListener(quickClickListener);
        contentLayoutBinding.homeChatRl.setOnClickListener(quickClickListener);

        contentLayoutBinding.ivClosePostUploadProgress.setOnClickListener(quickClickListener);

        contentLayoutBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    HomeVideoFragment.videoStartTime = System.currentTimeMillis();
                    HomeVideoFragment.postVideoPlayDurationTime = System.currentTimeMillis();
                    HomeVideoFragment.isCurrentQuit = true;

                    HomeImageTextFragment.isCurrentQuit = false;
                    ((HomeImageTextFragment) fragmentList.get(1)).uploadPlay(true);
                } else {
                    HomeImageTextFragment.imgStartTime = System.currentTimeMillis();
                }
                logOutOrInRefreshVideo();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void initData() {
    }

    public ViewPager getViewPager() {
        return contentLayoutBinding.viewPager;
    }

    public void refreshData() {
        if (getViewPager().getCurrentItem() == 0) {
            ((HomeVideoFragment) fragmentList.get(0)).refreshData(0);
        } else {
            ((HomeImageTextFragment) fragmentList.get(1)).refreshData(0);
        }
    }

    public void updateExperimentStrategy(String bottomEmotion) {
        ((HomeImageTextFragment) fragmentList.get(1)).updateExperimentStrategy(bottomEmotion);
    }

    public void updateLog() {
        if (getViewPager().getCurrentItem() == 0) {
            ((HomeVideoFragment) fragmentList.get(0)).uploadPlay(true);
        } else {
            ((HomeImageTextFragment) fragmentList.get(1)).uploadPlay(true);
        }
    }

    public void onVideoErrorPlay() {
        if (getViewPager().getCurrentItem() == 0) {
            ((HomeVideoFragment) fragmentList.get(0)).onVideoErrorPlay();
        }
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.rl_post_upload_progress:
                    AlbumFile albumFile = (AlbumFile) contentLayoutBinding.rlPostUploadProgress.getTag();
                    setPostUploadStatus(albumFile);
                    if (TextUtils.isEmpty(albumFile.getPostUrl())) {
                        startUploadPostFile(albumFile);
                    } else {
                        publishPost(albumFile);
                    }
                    break;
                case R.id.iv_close_post_upload_progress:
                    contentLayoutBinding.ivClosePostUploadProgress.setVisibility(View.GONE);
                    contentLayoutBinding.rlPostUploadProgress.setVisibility(View.GONE);
                    break;
                case R.id.home_task_rl:
                    ConstantUtil.createUmEvent("home_task_btn");//首页左上角任务
                    contentLayoutBinding.ivTaskCentralRed.setVisibility(View.GONE);
                    TaskCentralActivity.starActivity(activity, "0");
                    if (taskMessageBean != null) {
                        taskMessageBean.setTaskRed(0);
                        RxBus.getDefault().postSticky(taskMessageBean);
                    }
                    break;
                case R.id.home_chat_rl:
                    ConstantUtil.createUmEvent("home_robot");//首页右上角机器人
                    BotChatWebActivity.starWebActivity(activity);
                    break;

                default:
                    break;
            }
        }
    };

    @SuppressLint("SetTextI18n")
    public void uploadPostFile(AlbumFile albumFile) {
        setPostUploadStatus(albumFile);
        contentLayoutBinding.rlPostUploadProgress.setVisibility(View.VISIBLE);
        GlideUtils.loadImageWithPath(albumFile.getPath(), contentLayoutBinding.ivPostUploadProgress);
        startUploadPostFile(albumFile);
    }

    private void setPostUploadStatus(AlbumFile albumFile) {
        contentLayoutBinding.ivClosePostUploadProgress.setVisibility(View.GONE);
        contentLayoutBinding.rlPostUploadProgress.setEnabled(false);
        contentLayoutBinding.rlPostUploadProgress.setTag(albumFile);
        contentLayoutBinding.tvPostUploadProgress.setText(String.valueOf(contentLayoutBinding.pbPostUploadProgress.getProgress()));
        contentLayoutBinding.pbPostUploadProgress.setVisibility(View.VISIBLE);
    }

    @SuppressLint("SetTextI18n")
    private void startUploadPostFile(AlbumFile albumFile) {
        String fileUniqueName = AlbumUtils.getNowDateTime("yyyyMMdd") + "/" + AlbumUtils.getMD5ForString(UUID.randomUUID().toString()) + "." + FileUtils.getFileExtension(albumFile.getPath());

        FunnyApplication.getUploadOssService().asyncUploadFile("postfile/" + fileUniqueName, albumFile.getPath(), new RequestCallback<PutObjectResult>() {
            @Override
            public void updateProgress(int progress) {
                activity.runOnUiThread(() -> {
                    contentLayoutBinding.pbPostUploadProgress.setProgress(progress);
                    contentLayoutBinding.tvPostUploadProgress.setText(progress + "%");
                });
            }

            @Override
            public void onSuccess(PutObjectResult result) {
                activity.runOnUiThread(() -> {
                    albumFile.setPostUrl(fileUniqueName);
                    publishPost(albumFile);
                });
            }

            @Override
            public void onFailure() {
                activity.runOnUiThread(() -> switchPostPublishStatus());
            }
        });

//        viewModel.uploadPostFile(albumFile, (progress, total, done) -> activity.runOnUiThread(() -> {
//            contentLayoutBinding.pbPostUploadProgress.setProgress((int) ((100 * progress) / total));
//            contentLayoutBinding.tvPostUploadProgress.setText((100 * progress) / total + "%");
//        })).observe(this, resultBean -> {
//            if (resultBean != null) {
//                if (resultBean.getCode() == 0) {
//                    albumFile.setPostUrl(resultBean.getMsg());
//                    publishPost(albumFile);
//                } else {
//                    switchPostPublishStatus();
//                }
//            } else {
//                switchPostPublishStatus();
//            }
//        });
    }

    private void publishPost(AlbumFile albumFile) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"));
        jsonObject.addProperty("deviceId", DeviceUtils.getUniqueDeviceId());
        jsonObject.addProperty("title", albumFile.getPostTitle());
        jsonObject.addProperty("url", albumFile.getPostUrl());
        jsonObject.addProperty("location", albumFile.getLongitude() + "," + albumFile.getLatitude());
        jsonObject.addProperty("type", albumFile.getMediaType());
        jsonObject.addProperty("viewLen", albumFile.getDuration() / 1000);
        jsonObject.addProperty("size", albumFile.getSize());
        jsonObject.addProperty("topicIds", albumFile.getTopicIds());
        jsonObject.addProperty("topicNames", albumFile.getTopicNames());
        jsonObject.addProperty("width", albumFile.getWidth());
        jsonObject.addProperty("height", albumFile.getHeight());

//        Log.e("videoImg-", "width:" + albumFile.getWidth() + "-height:" + albumFile.getHeight());

        viewModel.publishPost(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    albumFile.setPostId(resultBean.getData());
                    contentLayoutBinding.rlPostUploadProgress.setVisibility(View.GONE);
                    if (albumFile.getMediaType() == AlbumFile.TYPE_VIDEO) {
                        if (getViewPager().getCurrentItem() != 0) {
                            getViewPager().setCurrentItem(0);
                        }
                        ((HomeVideoFragment) fragmentList.get(0)).playPublishVideo(albumFile);
                    } else if (albumFile.getMediaType() == AlbumFile.TYPE_IMAGE) {
                        if (getViewPager().getCurrentItem() != 1) {
                            if (((HomeImageTextFragment) fragmentList.get(1)).isInitData()) {
                                getViewPager().setCurrentItem(1);
                                ((HomeImageTextFragment) fragmentList.get(1)).playPublishImage(albumFile);
                            } else {
                                getViewPager().setCurrentItem(1);
                                ThreadUtils.runOnUiThreadDelayed(() -> ((HomeImageTextFragment) fragmentList.get(1)).playPublishImage(albumFile), 1000);
                            }
                        } else {
                            ((HomeImageTextFragment) fragmentList.get(1)).playPublishImage(albumFile);
                        }
                    }
                } else {
                    switchPostPublishStatus();
                }
            } else {
                switchPostPublishStatus();
            }
        });
    }

    private void switchPostPublishStatus() {
        CommonUtils.showShort("发布失败，请重试");
        contentLayoutBinding.pbPostUploadProgress.setVisibility(View.GONE);
        contentLayoutBinding.ivClosePostUploadProgress.setVisibility(View.VISIBLE);
        contentLayoutBinding.tvPostUploadProgress.setText("重试");
        contentLayoutBinding.rlPostUploadProgress.setEnabled(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (isInitData() && !hidden) {
            //退出登录或者登录ID不一样的情况后，刷新首页数据
            logOutOrInRefreshVideo();
        }
    }

    public void logOutOrInRefreshVideo() {
        if (getViewPager().getCurrentItem() == 0) {
            if (ConstantUtil.isLogOutRefreshVideo) {
                ConstantUtil.isLogOutRefreshVideo = false;
                if ((fragmentList.get(0)) != null) {
                    ((HomeVideoFragment) fragmentList.get(0)).refreshData(2);
                }
            }
        } else {
            if (ConstantUtil.isLogOutRefreshImg) {
                ConstantUtil.isLogOutRefreshImg = false;
                if ((fragmentList.get(1)) != null)
                    ((HomeImageTextFragment) fragmentList.get(1)).refreshData(2);
            }
        }
    }

    public void showSharePost(long postId, String paramType) {
        long testPostId = 1282126357504l;
        if (ConstantUtil.isShareInShowPost) {
            ConstantUtil.isShareInShowPost = false;
            ThreadUtils.runOnUiThreadDelayed(() -> {
                CommonUtils.showShort("即将跳转到打开内容");
                getPostInfo(postId, paramType);
            }, 800);
        }
    }

    private void getPostInfo(long postId, String paramType) {

        if (((HomeVideoFragment) fragmentList.get(0)) == null || ((HomeImageTextFragment) fragmentList.get(1)) == null || postId == 0) {
            return;
        }
        viewModel.getPostInfo(postId).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    HomeVideoImageListBean.ListBean videoInfo = resultBean.getData();
                    if (videoInfo != null && videoInfo.getContent() != null) {
                        videoInfo.getContent().setProvider(paramType);
                        if (videoInfo.getContent().getPostType() == 1) {
                            if (getViewPager().getCurrentItem() != 0) {
                                getViewPager().setCurrentItem(0);
                            }
                        } else {
                            if (getViewPager().getCurrentItem() != 1) {
                                if (((HomeImageTextFragment) fragmentList.get(1)).isInitData()) {
                                    getViewPager().setCurrentItem(1);
                                    ((HomeImageTextFragment) fragmentList.get(1)).playShareVideo(videoInfo);
                                } else {
                                    getViewPager().setCurrentItem(1);
                                    ThreadUtils.runOnUiThreadDelayed(() -> ((HomeImageTextFragment) fragmentList.get(1)).playShareVideo(videoInfo), 1000);
                                }
                            } else {
                                ((HomeImageTextFragment) fragmentList.get(1)).playShareVideo(videoInfo);
                            }
                        }
                    }
                }
            }
        });
    }

    public void updatePostLikeOrUnlikeOrReview(HomeVideoImageListBean.ListBean.ContentBean videoOrImageContent) {
        if (getViewPager().getCurrentItem() == 0) {
            ((HomeVideoFragment) fragmentList.get(0)).updatePostLikeOrUnlikeOrReview(videoOrImageContent);
        } else if (getViewPager().getCurrentItem() == 1) {
            ((HomeImageTextFragment) fragmentList.get(1)).updatePostLikeOrUnlikeOrReview(videoOrImageContent);
        }
    }

    public void clearNoLookPostIdList() {
        if ((fragmentList.get(0)) != null) {
            ((HomeVideoFragment) fragmentList.get(0)).clearNoLookPostIdList(0);
        }

        if ((fragmentList.get(1)) != null) {
            ((HomeImageTextFragment) fragmentList.get(1)).clearNoLookPostIdList(1);
        }
    }

    public void removeHandlerMessage() {
        if ((fragmentList.get(0)) != null) {
            ((HomeVideoFragment) fragmentList.get(0)).removeHandlerMessage();
        }

        if ((fragmentList.get(1)) != null) {
            ((HomeImageTextFragment) fragmentList.get(1)).removeHandlerMessage();
        }
    }

    private TaskMessageBean taskMessageBean = new TaskMessageBean();

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservableSticky(TaskMessageBean.class).subscribe(taskMessageBean -> {
            if (taskMessageBean != null) {
                this.taskMessageBean = taskMessageBean;
                int task = taskMessageBean.getTaskRed();
                if (task > 0) {
                    contentLayoutBinding.ivTaskCentralRed.setVisibility(View.VISIBLE);
                } else {
                    contentLayoutBinding.ivTaskCentralRed.setVisibility(View.GONE);
                }
            }
        }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));
    }
}
