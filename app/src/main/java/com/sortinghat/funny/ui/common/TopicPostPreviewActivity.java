package com.sortinghat.funny.ui.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.gson.JsonObject;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.PostPreviewAdapter;
import com.sortinghat.funny.bean.DetailListBean;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityPostPreviewBinding;
import com.sortinghat.funny.databinding.ItemPostPreviewBinding;
import com.sortinghat.funny.ui.home.HomeVideoFragment;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.TopicViewModel;
import com.umeng.socialize.UMShareAPI;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wzy on 2021/7/20
 */
public class TopicPostPreviewActivity extends BaseActivity<TopicViewModel, ActivityPostPreviewBinding> {

    private PostPreviewAdapter postPreviewAdapter;

    public static String direction = "appuse";
    public static boolean isCurrentQuit = true; //是否是当前页面退出
    public static boolean isPlayed;

    private List<HomeVideoImageListBean.ListBean> homeVideoBeanList;
    private String topicId, topicName;
    private String from;
    private int fromPage = 0; //0:我的 1：其他人页面 2：消息通知页面 3：话题页面
    private int tabType = 0;  //0:我的发布 1:我的评论 2：我的喜欢
    private int topicTab = 0;  //0:热帖 1:最新 2：等等//话题分类
    private int homeposition;

    private int pageNumber = 1;
    private int pageSize = 18;

    private int playErrorPosition = -1;
    private int viewPagerScrollState;

    private boolean isCanLoadMore = true;

    private final int SHARE_ANIMATION_SHOW = 101;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == SHARE_ANIMATION_SHOW) {
                RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(contentLayoutBinding.viewPager.getCurrentItem());
                if (viewHolder != null) {
                    BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding>) viewHolder;
                    if (bindingViewHolder.binding != null) {
                        bindingViewHolder.binding.controller.showShareAnimation(true);
                    }
                }
            }
        }
    };

    @Override
    protected int getLayoutId() {
        titleBarBinding.getRoot().setVisibility(View.GONE);
        return R.layout.activity_post_preview;
    }

    @Override
    protected void initViews() {
        isPlayed = false;
        getIntentData();
        ConstantUtil.setNavigationBarColor(this);
        setStatusBar(R.color.black, false);
        initViewPagerAdapter();
        subscibeRxBus();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                from = bundle.getString("FROM", "");
                fromPage = bundle.getInt("FROM_PAGE");
                topicTab = bundle.getInt("TOPIC_TAB");
                tabType = bundle.getInt("TAB_TYPE");
                topicId = bundle.getString("TOPIC_ID");
                topicName = bundle.getString("TOPIC_NAME");
//                homeVideoBeanList = (List<HomeVideoImageListBean.ListBean>) bundle.getSerializable("LIST_DATA");
                DetailListBean bean = (DetailListBean) bundle.getSerializable("LIST_DATA");
                homeVideoBeanList = bean.getHomeVideoBeanList();

                homeposition = bundle.getInt("POSITION");
                pageNumber = bundle.getInt("PAGE_NUMBER", 1);
            }
        }
    }

    private void initViewPagerAdapter() {
        postPreviewAdapter = new PostPreviewAdapter(this, getSupportFragmentManager(), mHandler, fromPage, from, tabType);
        contentLayoutBinding.viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        contentLayoutBinding.viewPager.setAdapter(postPreviewAdapter);
        contentLayoutBinding.viewPager.setOffscreenPageLimit(3); //预加载3次
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            if (v.getId() == R.id.iv_video_back) {
                finish();
            }
        }
    };

    @Override
    protected void setListener() {
        contentLayoutBinding.ivVideoBack.setOnClickListener(quickClickListener);

        contentLayoutBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                viewPagerScrollState = state;
                RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(contentLayoutBinding.viewPager.getCurrentItem());
                if (viewHolder != null) {
                    BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding>) viewHolder;
                    if (bindingViewHolder.binding != null) {
                        bindingViewHolder.binding.controller.setScrollAlpha(state);
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (viewPagerScrollState == ViewPager2.SCROLL_STATE_DRAGGING && position == postPreviewAdapter.getData().size() - 1 && !isCanLoadMore) {
                    CommonUtils.showShort("没有更多内容了");
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int playPosition = GSYVideoManager.instance().getPlayPosition();
                uploadPlay(false);
                playInfoNormal(playPosition);

                videoStartTime = System.currentTimeMillis();

                if (position > playPosition) {
                    direction = "up";
                } else if (position < playPosition) {
                    direction = "down";
                }

                playErrorPosition = position;
                mHandler.removeMessages(SHARE_ANIMATION_SHOW);

                if ((position != playPosition || position == 0)) {
                    mHandler.postDelayed(() -> playPosition(position), 280);
                }

                //索引大于已加载出来的列表数量,才可以加载方法
                if ((position + 1) >= postPreviewAdapter.getData().size() && isCanLoadMore) {
                    pageNumber++;
                    getVideoList();
                }
                lastPosition = position;
            }
        });
    }

    @Override
    protected void initData() {
        postPreviewAdapter.setViewModel(viewModel);

        postPreviewAdapter.addData(homeVideoBeanList);
        ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).scrollToPosition(homeposition);
    }

    private void getVideoList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("tab", 0);
        jsonObject.addProperty("topicId", topicId);
        jsonObject.addProperty("topicName", topicName);
        RequestParamUtil.addCommonRequestParam(jsonObject);
        RequestParamUtil.addPagingParam(jsonObject, pageNumber, pageSize);

        viewModel.getTopicRelationPostList(jsonObject.toString(),topicTab).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    List<HomeVideoImageListBean.ListBean> videoList = resultBean.getData().getList();
                    if (videoList != null && !videoList.isEmpty()) {
                        postPreviewAdapter.addData(videoList);

                        if (videoList.size() < pageSize) {
                            isCanLoadMore = false;
                        }
                    } else {
                        isCanLoadMore = false;
                        CommonUtils.showShort("没有更多内容了");
                    }
                } else {
                    LogUtils.e(resultBean.getMsg());
                }
            }
        });
    }

    //上个视频界面回到初始状态
    private void playInfoNormal(int playPosition) {
        RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(playPosition);
        if (viewHolder != null) {
            BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding>) viewHolder;
            if (bindingViewHolder.binding != null && postPreviewAdapter.getData().get(playPosition).getContent().getPostType() == 1) {
                bindingViewHolder.binding.rlHomeLikeDialog.setVisibility(View.GONE);
                bindingViewHolder.binding.rlHomeDislikeDialog.setVisibility(View.GONE);
                bindingViewHolder.binding.controller.showControllerInfoToNorman(false);
                bindingViewHolder.binding.likeview.setShow(true);
                bindingViewHolder.binding.gsyVideoPlayer.showStartProgress(false);
                bindingViewHolder.binding.gsyVideoPlayer.onVideoPause();
                bindingViewHolder.binding.controller.setScrollAlpha(0);
                bindingViewHolder.binding.controller.showShareAnimation(false);
            }
        }
    }

    private void playPosition(int position) {
        RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding>) viewHolder;
            if (bindingViewHolder.binding != null && position < postPreviewAdapter.getData().size() && postPreviewAdapter.getData().get(position).getContent().getPostType() == 1) {
                isPlayed = true;
                bindingViewHolder.binding.startPlay.setVisibility(View.GONE);
                bindingViewHolder.binding.gsyVideoPlayer.setPlayPosition(position);
                bindingViewHolder.binding.gsyVideoPlayer.startPlayLogic();
                bindingViewHolder.binding.likeview.setShow(true);
                bindingViewHolder.binding.gsyVideoPlayer.showStartProgress(false);

                if (postPreviewAdapter.getData().get(position).getContent().getApplyStatus() == 1) {
                    mHandler.sendEmptyMessageDelayed(SHARE_ANIMATION_SHOW, 20000);
                }
            } else {
                if (postPreviewAdapter.getData().get(position).getContent().getApplyStatus() == 1) {
                    mHandler.sendEmptyMessageDelayed(SHARE_ANIMATION_SHOW, 10000);
                }
                GSYVideoManager.onPause();
            }
        }
    }

    public static void starActivity(String from, int topicTab,int fromPage, int tabType, String topicId, String topicName, List<HomeVideoImageListBean.ListBean> homeVideoBeanList, int position, int pageNumber) {
        Bundle bundle = new Bundle();
        bundle.putString("FROM", from);
        bundle.putInt("FROM_PAGE", fromPage);
        bundle.putInt("TOPIC_TAB", topicTab);
        bundle.putInt("TAB_TYPE", tabType);
        bundle.putString("TOPIC_ID", topicId);
        bundle.putString("TOPIC_NAME", topicName);
        DetailListBean bean = new DetailListBean();
        bean.setHomeVideoBeanList(homeVideoBeanList);
        bundle.putSerializable("LIST_DATA", bean);//
//        bundle.putSerializable("LIST_DATA", (Serializable) homeVideoBeanList);//大容量容易有崩溃bug
        bundle.putInt("POSITION", position);
        bundle.putInt("PAGE_NUMBER", pageNumber);
        ActivityUtils.startActivity(bundle, TopicPostPreviewActivity.class);
    }

    //视频sdk会出现错误时
    public void onVideoErrorPlay() {
        if (playErrorPosition >= 0 & isCurrentQuit) {
            playPosition(playErrorPosition);
        }
    }

    private int isFirst = 0;

    @Override
    public void onResume() {
        super.onResume();
        long currentTime = System.currentTimeMillis();
        videoStartTime = currentTime;
        postVideoPlayDurationTime = currentTime;

        if (isFirst > 0) {
            if (GSYVideoManager.instance().listener() != null) {
                GSYVideoManager.onResume(false);
            } else {
                //修复退出后台再回来可能出现视频丢失的bug
                if (playErrorPosition >= 0) {
                    onVideoErrorPlay();
                    LogUtils.e("gsyPlayer", "-error:" + playErrorPosition);
                }
            }
        } else {
            isFirst = 1;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
        uploadPlay(true);
    }

    private void subscibeRxBus() {
        //更新帖子的点赞或点踩状态或评论数
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.UPDATE_POST_LIKE_OR_UNLIKE_OR_REVIEW, HomeVideoImageListBean.ListBean.ContentBean.class)
                .subscribe(videoOrImageContent -> {
                    for (int i = 0; i < postPreviewAdapter.getData().size(); i++) {
                        HomeVideoImageListBean.ListBean listBean = postPreviewAdapter.getItemData(i);
                        if (listBean != null && listBean.getContent() != null && videoOrImageContent != null && listBean.getContent().getPostId() == videoOrImageContent.getPostId()) {
                            listBean.getContent().setReplyCount(videoOrImageContent.getReplyCount());
                            RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(i);
                            if (viewHolder != null) {
                                BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding>) viewHolder;
                                if (bindingViewHolder.binding != null) {
                                    bindingViewHolder.binding.controller.setLikeOrUnlikeOrReview(listBean.getContent());
                                }
                            }
                        }
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        //点关注后,更新整个列表的id
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.MY_UPDATA_AUTHOR_INFO, HomeVideoImageListBean.ListBean.class)
                .subscribe(videoInfo -> {
                    for (int i = 0; i < postPreviewAdapter.getData().size(); i++) {
                        if (postPreviewAdapter.getData().get(i).getContent().getAuthorId() == videoInfo.getContent().getAuthorId()) {
                            postPreviewAdapter.getData().get(i).getContent().setFollowStatus(videoInfo.getContent().getFollowStatus());
                        }
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.MY_UPDATA_LIST_INFO_DELETE, Integer.class)
                .subscribe(integer -> {
                    if (!postPreviewAdapter.getData().isEmpty() && postPreviewAdapter.getData().size() > integer) {
                        playInfoNormal(integer);
                        postPreviewAdapter.getData().remove((int) integer);
                        if (!postPreviewAdapter.getData().isEmpty() && postPreviewAdapter.getData().size() > integer && postPreviewAdapter.getData().get(integer).getContent().getPostType() == 1) {
                            ThreadUtils.runOnUiThreadDelayed(() -> playPosition(integer), 1000);
                        }
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        //滑到下一个
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.MY_VIDEO_UPDATE_DISLIKE, Integer.class)
                .subscribe(position -> {
                    if (position >= 0 && postPreviewAdapter.getData().size() > 0 && position + 1 < postPreviewAdapter.getData().size()) {
                        contentLayoutBinding.viewPager.setCurrentItem(position + 1);
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        GSYVideoManager.releaseAllVideos();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroy();
    }

    public void uploadPlay(boolean isOnPause) {
        uploadPlayBase(1, lastPosition, isOnPause, direction, null, videoStartTime);
    }

    protected int lastPosition = -1;

    protected void uploadPlayBase(int tabType, int lastPosition, boolean isOnPause, String
            direction, List<Integer> pagePositionList, long startTime) {
        try {
            long stateTotalTime = 0;
            int playPosition = lastPosition;
            if (playPosition < 0 || postPreviewAdapter.getData().size() < 1) {
                LogUtils.e("unexposed_pids-return", "-error:lastPosition=" + lastPosition + "-tab:" + tabType + "-startTime:" + startTime);
                return;
            }

            if (startTime == 0) {
                resetStaticPostTime(tabType);
                return;
            }
            long currentPosition = 0;
            long video_total_time = 0;
            if (tabType == 1) {
                currentPosition = GSYVideoManager.instance().getCurrentPosition();
                video_total_time = GSYVideoManager.instance().getDuration();
            }
            long endTime = System.currentTimeMillis();
            if (startCommentDialogTime != 0) {
                if (isShowCommentDialog) {
                    showCommentDialogTime += endTime - startCommentDialogTime;
                }
            } else {
                showCommentDialogTime = 0;
            }
            if (postVideoPlayDurationLastState == 1) {
                if (postVideoPlayDurationTime == 0) {
                    postVideoPlayDurationTime = startTime;
                }
                postVideoPlayDuration = endTime - postVideoPlayDurationTime + postVideoPlayDuration;
            }
            if (postVideoPlayDurationTime == 0) {
                postVideoPlayDuration = 0;
            }

            stateTotalTime = endTime - startTime;
            long post_id = 0;
            if (postPreviewAdapter.getData().size() > playPosition) {
                post_id = postPreviewAdapter.getItemData(playPosition).getContent().getPostId();
            } else {
                return;
            }

            int page = 0;
            int position = 0;// playPosition % singleSize;

            if (stateTotalTime <= 300) {
                //快速滑动不上传ups日志，回传未看过的，如果用户往回刷有可能会刷到这个帖子
                LogUtils.e("unexposed_pids----error", postVideoPlayDuration + "-time:" + stateTotalTime + "-id:" + post_id + "-play：" + playPosition + "pos" + position);
                resetStaticPostTime(tabType);
                return;
            }
//            if (postVideoPlayDuration > stateTotalTime) {
//                postVideoPlayDuration = stateTotalTime;
//            }
            String post_type = postPreviewAdapter.getItemData(playPosition).getContent().getPostType() == 1 ? "video" : postPreviewAdapter.getItemData(playPosition).getContent().getPostType() == 2 ? "img" : "ad";

            JsonObject postJsonObject = new JsonObject();
            postJsonObject.addProperty("post_id", post_id);
            postJsonObject.addProperty("post_type", post_type);
            postJsonObject.addProperty("tab", tabType);
            postJsonObject.addProperty("page", page);
            postJsonObject.addProperty("position", position);
            postJsonObject.addProperty("direction", direction);//用户上下滑的方向。up//上滑、down//下拉、homebutton//按钮、appuse//首次进入
            postJsonObject.addProperty("duration", stateTotalTime);
            postJsonObject.addProperty("play_dur", tabType == 2 ? 0 : postVideoPlayDuration);
            postJsonObject.addProperty("current_position", currentPosition);
            postJsonObject.addProperty("detail_dur", showCommentDialogTime);//评论浏览时间
            postJsonObject.addProperty("start_time", startTime);
            postJsonObject.addProperty("start_play_time", startPlayTime);
            postJsonObject.addProperty("end_time", endTime);
            postJsonObject.addProperty("create_time", endTime);
            postJsonObject.addProperty("quit_app", isOnPause ? 1 : 2);
            postJsonObject.addProperty("video_total_time", video_total_time);
            postJsonObject.addProperty("topic_ids", postPreviewAdapter.getItemData(playPosition).getContent().getTopicIds());
            postJsonObject.addProperty("op_topic_ids", postPreviewAdapter.getItemData(playPosition).getContent().getOpTopicIds());
            postJsonObject.addProperty("tagId", postPreviewAdapter.getItemData(playPosition).getContent().getTagId());
            postJsonObject.addProperty("ad_click_num", postPreviewAdapter.getItemData(playPosition).getContent().getAdClickNum());
            postJsonObject.addProperty("ad_video_is_complete", postPreviewAdapter.getItemData(playPosition).getContent().getAdVideoIsComplete());
            postJsonObject.addProperty("ad_request_id", postPreviewAdapter.getItemData(playPosition).getContent().getAdRequestId());
            postPreviewAdapter.getItemData(playPosition).getContent().setAdClickNum(0);
            postPreviewAdapter.getItemData(playPosition).getContent().setAdVideoIsComplete(0);
            String provider = postPreviewAdapter.getItemData(lastPosition).getContent().getProvider();
            provider = "topic";

            RequestParamUtil.addStartLogHeadParam(postJsonObject, "view", "post", "topic", provider);

            String jsonData = postJsonObject.toString();
            //上报本次播放的视频时长
            LogUtils.d("unexposed_pids-videoimgtop", "-totalTime:" + stateTotalTime + "\n-complete:" + "-json：" + jsonData + "\nprovider:" + provider);
            resetStaticPostTime(tabType);
            if (viewModel == null) {
                return;
            }

//            String jsonString = jsonData.toString();
//            String start_log_string = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString(Constant.START_LOG_STRING_NEW);
//
//            if (!TextUtils.isEmpty(start_log_string)) {
//                jsonString = start_log_string + ConstantUtil.LogListTag + jsonData.toString();
//            }
//            ConstantUtil.KfpLogSendLogValue++;
//            if (ConstantUtil.KfpLogSendLogValue < ConstantUtil.KfpLogSendMaxDefaultValue && !isOnPause) {
//                SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(Constant.START_LOG_STRING_NEW, "");
//                SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(Constant.START_LOG_STRING_NEW, jsonString);
//                return;
//            }
//            ConstantUtil.KfpLogSendLogValue = 0;
            viewModel.setAppUnifyLog(this, jsonData.toString(), isOnPause).observe((LifecycleOwner) this, resultBean -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //post埋点相关
    public static boolean isShowCommentDialog = false;
    public static long showCommentDialogTime = 0;//评论弹框的总时间
    public static long startCommentDialogTime = 0;//评论弹框的开始时间
    public static long postVideoPlayDuration = 0; //当前总的播放时间,
    public static long postVideoPlayDurationTime = 0; //记录一下暂停和开始的播放时间
    public static long postVideoPlayDurationLastState = 1; //1：播放  0：暂停

    public static long startPlayTime = 0;//开始播的时间，
    public static long videoStartTime = 0;//，
    public static long imgStartTime = 0;//，

    private void resetStaticPostTime(int tabType) {
        showCommentDialogTime = 0;
        postVideoPlayDuration = 0;
        postVideoPlayDurationTime = 0;
        postVideoPlayDurationLastState = 1;
        startCommentDialogTime = 0;
        showCommentDialogTime = 0;
        startCommentDialogTime = 0;
        if (tabType == 1) {
            videoStartTime = 0;
        } else {
            imgStartTime = 0;
        }
    }
}
