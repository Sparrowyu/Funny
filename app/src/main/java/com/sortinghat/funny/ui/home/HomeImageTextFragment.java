package com.sortinghat.funny.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.google.gson.Gson;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.common.utils.statusbar.StatusBarUtil;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.HomeVideoAdapter;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.FragmentHomeVideoImageBinding;
import com.sortinghat.funny.databinding.ItemHomeVideoBinding;
import com.sortinghat.funny.thirdparty.album.AlbumFile;
import com.sortinghat.funny.util.CommonUserInfo;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.ListenerUtils;
import com.sortinghat.funny.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzy on 2021/6/14
 */
public class HomeImageTextFragment extends BaseHomeMediaFragment<HomeViewModel, FragmentHomeVideoImageBinding> {

    private final int SHARE_ANIMATION_SHOW = 101;
    private final int SHOW_SHARE_ANIMATION_TIME = 10000;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHARE_ANIMATION_SHOW:
                    RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(mCurPos);
                    if (showVideoPostType(mCurPos) && viewHolder != null) {
                        BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding>) viewHolder;
                        if (bindingViewHolder.binding != null) {
                            bindingViewHolder.binding.controller.showShareAnimation(true);
                        }
                    }
                    break;
            }
        }
    };


    public static List<Integer> pagePositionList = new ArrayList<>();//每个的size
    private int singleSize = 12;//单页请求数量，继续加载时用到

    public static String direction = "appuse";
    public static String requestListDirection = "appuse";

    public static boolean isCurrentQuit = true; //是否是当前页面退出
    private boolean isVideoFromRefresh = false;//只用来判断当前视频是否由刷新来的
    private boolean isLoadingData; //是否正在加载数据

    private String bottomEmotion = "panda"; //感受赞实验策略

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_video_image;
    }

    @Override
    protected void initViews() {
        initBaseViews();
        setHomeViewModel(viewModel);
        noLookPostIdList = new ArrayList<>();//未看的list
        tabType = 2;
        viewPager = contentLayoutBinding.viewPager;
        contentLayoutBinding.gifHomeLoading.setVisibility(View.VISIBLE);
        GlideUtils.loadGifImageFromResource(R.drawable.home_loading, contentLayoutBinding.gifHomeLoading);
        initViewPagerAdapter();
    }

    private void initViewPagerAdapter() {
        noLookPostIdList = ConstantUtil.getSPList("home_img_request_list");
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_img_request_list", "");

        ListenerUtils.getInstance().setHomeRemoveImgPostIdListener(postId -> {
            if (noLookPostIdList.size() > 0 && noLookPostIdList.contains(postId)) {
                noLookPostIdList.remove(postId);
            }
        });
        homeVideoAdapter = new HomeVideoAdapter(getActivity(), getChildFragmentManager(), tabType, mHandler);
        contentLayoutBinding.viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        contentLayoutBinding.viewPager.setAdapter(homeVideoAdapter);
        contentLayoutBinding.viewPager.setOffscreenPageLimit(3);//图片预加载3页
        contentLayoutBinding.refreshLayout.setOnRefreshListener(() -> {
            refreshData(1);
        });
        contentLayoutBinding.refreshLayout.setColorSchemeColors(
                Color.RED,
                Color.BLUE,
                Color.YELLOW);
        subscibeRxBus();
    }

    @Override
    protected void setListener() {
        contentLayoutBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(mCurPos);
                if (showVideoPostType(mCurPos) && viewHolder != null) {
                    BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding>) viewHolder;
                    if (bindingViewHolder.binding != null) {
                        bindingViewHolder.binding.controller.setScrollAlpha(state);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mCurPos = position;
                if (!SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(Constant.USER_VIP_TAG)) {
                    if (isToLoadAd(position)) {
                        isLoadingAd = true;
                        loadExpressDrawNativeAd(position);
                    }
                }
//                setExperimentStrategy(position);
                showGuideLayer(position, contentLayoutBinding.viewPager);
                if (lastPosition >= 0) {
                    uploadPlay(false);
                }

                imgStartTime = System.currentTimeMillis();
                if (isVideoFromRefresh) {
                    direction = "homebutton";
                } else {
                    if (position > lastPosition) {
                        direction = "up";
                    } else if (position < lastPosition) {
                        direction = "down";
                    }
                }
                lastPosition = position;
                isVideoFromRefresh = false;

                playInfoNormal(position);

                removeHandlerMessage();
                mHandler.sendEmptyMessageDelayed(SHARE_ANIMATION_SHOW, SHOW_SHARE_ANIMATION_TIME);

                if (singleSize != 0) {
                    //索引大于已加载出来的列表数量,才可以加载方法
                    if ((position + 1) >= homeVideoAdapter.getData().size() && !isLoadingData) {
                        if (position >= 0 && homeVideoAdapter.getData().size() > position) {
                            long post_id = homeVideoAdapter.getItemData(position).getContent().getPostId();
                            if (noLookPostIdList.size() > 0 && noLookPostIdList.contains(post_id)) {
                                noLookPostIdList.remove(post_id);
                            }
                        }
                        isLoadingData = true;
//                        noLookPostIdList.clear();
                        requestListDirection = "up";
                        getVideoList(2);
                    }
                }
            }
        });
    }

    public void removeHandlerMessage() {
        if (mHandler != null) {
            mHandler.removeMessages(SHARE_ANIMATION_SHOW);
        }
    }

    //上个视频界面回到初始状态
    private void playInfoNormal(int playPosition) {
        RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(playPosition);
        if (viewHolder != null) {
            BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding>) viewHolder;
            if (showVideoPostType(playPosition) && bindingViewHolder.binding != null) {
                bindingViewHolder.binding.rlHomeLikeDialog.setVisibility(View.GONE);
                bindingViewHolder.binding.rlHomeDislikeDialog.setVisibility(View.GONE);
                bindingViewHolder.binding.controller.setScrollAlpha(0);
                bindingViewHolder.binding.controller.showShareAnimation(false);
            }
        }
    }


    @Override
    protected void initData() {
        homeVideoAdapter.setViewModel(viewModel);
        requestListDirection = "appuse";
        getVideoList(0);
    }

    /**
     * from 0:homebutton 1:refresh下拉刷新的 2:用户退出后需要刷新首页图文推荐的帖子（此时如果videoall==0,则不执行）
     */
    public void refreshData(int from) {
        if (from == 2 && homeVideoAdapter.getData().size() < 1) {
            return;
        }
        requestListDirection = "refresh";
        if (from == 0) {
            requestListDirection = "homebutton";
        }
        if (from == 2) {
            requestListDirection = "outlogin";
            noLookPostIdList.clear();
        }
        if (from == 0 || from == 2) {
            contentLayoutBinding.gifHomeLoading.setVisibility(View.VISIBLE);
            GlideUtils.loadGifImageFromResource(R.drawable.home_loading, contentLayoutBinding.gifHomeLoading);
        }
        if (!isLoadingData) {
            isLoadingData = true;
            getVideoList(1);
        }
    }

    private void clearListData() {
        hasLoadPosList.clear();
        uploadPlay(false);
        imgStartTime = System.currentTimeMillis();//刷新后在上条日志传过之后重置当前时间
        isVideoFromRefresh = true;

        homeVideoAdapter.getData().clear();
        pagePositionList.clear();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getVideoList(int getDataType) {
        contentLayoutBinding.getRoot().setVisibility(View.VISIBLE);
        viewModel.getHomeVideoList(getActivity(), 2, noLookPostIdList, requestListDirection).observe(this, resultBean -> {
            contentLayoutBinding.refreshLayout.setRefreshing(false);
            isLoadingData = false;
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_img_request_list", "");
                    if (getDataType == 1) {
                        clearListData();
                    }
                    noLookPostIdList.clear();
                    singleSize = 0;
                    List<HomeVideoImageListBean.ListBean> videoList = resultBean.getData().getList();
                    if (videoList != null && !videoList.isEmpty()) {
                        singleSize = videoList.size();
                        pagePositionList.add(homeVideoAdapter.getData().size() + videoList.size());
                        //把此次请求的数据写到集合中
                        for (int i = homeVideoAdapter.getData().isEmpty() ? 1 : 0; i < videoList.size(); i++) {
                            noLookPostIdList.add(videoList.get(i).getContent().getPostId());
                        }
                        if (homeVideoAdapter.getData().isEmpty()) {
                            hideLoadingAnimation();
                            String testUrl = "https://oss-andriod.gaoxiaoxingqiu.com/trans/h265/20220331/delogo_48bfc85a52534c14a54cbdef8c9d64f2_1648716074.webp";
                            String testUrl1 = "https://oss-andriod.gaoxiaoxingqiu.com/trans/h265/20220331/9add5f2d442441c584538461fa5b1385_1648732009.webp";
                            String testUrl2 = "https://oss-andriod.gaoxiaoxingqiu.com/trans/h265/20220406/gif2webp_delogo_48bfc85a52534c14a54cbdef8c9d64f2_1649231710.gif";
                            if (ConstantUtil.isInfoTest() && videoList.size() > 3) {
                                videoList.get(0).getContent().setUrl(testUrl);
                                videoList.get(1).getContent().setUrl(testUrl1);
                                videoList.get(2).getContent().setUrl(testUrl2);
                            }
                            homeVideoAdapter.setNewData(videoList);
                            if (contentLayoutBinding.viewPager.getCurrentItem() != 0) {
                                contentLayoutBinding.viewPager.setCurrentItem(0, false);
                            }
                        } else {
                            //如果之前已经请求了广告，则插入新的
                            if (!adCacheList.isEmpty()) {
                                for (int adCachei = 0; adCachei < adCacheList.size(); adCachei++) {
                                    int adPositionCurr = adCacheList.get(adCachei).getContent().getAdPos();
                                    LogUtils.d(csjAdloadTag, "cache" + adCacheList.size() + "pos:" + adPositionCurr + "size" + homeVideoAdapter.getData().size());
                                    if (homeVideoAdapter.getData().size() > adPositionCurr) {
                                        LogUtils.d(csjAdloadTag, "cache" + adCacheList.size());
                                        homeVideoAdapter.addData(adPositionCurr, adCacheList.get(adCachei));
                                        adCacheList.remove(adCachei);
                                    }
                                }
                            }
                            homeVideoAdapter.addData(videoList);
                        }
                    } else {
                        if (homeVideoAdapter.getData().isEmpty()) {
                            hideLoadingAnimation();
                            homeVideoAdapter.notifyDataSetChanged();
                        }
                        CommonUtils.showShort("没有更多内容了，请稍后重试");
                    }
                } else {
                    hideLoadingAnimation();
                    LogUtils.e(resultBean.getMsg());
                }
            } else {
                hideLoadingAnimation();
            }
        });
    }

    private void hideLoadingAnimation() {
        GifDrawable gifDrawable = (GifDrawable) contentLayoutBinding.gifHomeLoading.getDrawable();
        if (gifDrawable != null && gifDrawable.isRunning()) {
            gifDrawable.stop();
        }
        contentLayoutBinding.gifHomeLoading.setVisibility(View.GONE);
    }

    protected void playShareVideo(HomeVideoImageListBean.ListBean imageInfo) {
        hideLoadingAnimation();
        if (homeVideoAdapter.getData().isEmpty()) {
            homeVideoAdapter.addData(0, imageInfo);
            contentLayoutBinding.viewPager.setCurrentItem(0, false);
        } else {
            //判断当前是否有这个帖子，避免重复帖子多次出现在列表中
            for (int i = 0; i < homeVideoAdapter.getData().size(); i++) {
                if (imageInfo.getContent().getPostId() == homeVideoAdapter.getData().get(i).getContent().getPostId()) {
                    contentLayoutBinding.viewPager.setCurrentItem(i, false);
                    return;
                }
            }
            homeVideoAdapter.addData(contentLayoutBinding.viewPager.getCurrentItem() + 1, imageInfo);
            contentLayoutBinding.viewPager.setCurrentItem(contentLayoutBinding.viewPager.getCurrentItem() + 1, false);
        }
    }

    protected void playPublishImage(AlbumFile albumFile) {
        HomeVideoImageListBean.ListBean.ContentBean imageContentInfo = new HomeVideoImageListBean.ListBean.ContentBean();
        imageContentInfo.setCreatedAt(System.currentTimeMillis());
        imageContentInfo.setAuthorId(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"));
        imageContentInfo.setAvatar(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("user_avatar", ""));
        imageContentInfo.setNickname(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("user_nike_name", ""));
        imageContentInfo.setPostId(albumFile.getPostId());
        imageContentInfo.setPostType(albumFile.getMediaType());
        imageContentInfo.setUrl(albumFile.getPath());
        imageContentInfo.setThumb(albumFile.getPath());
        imageContentInfo.setTitle(albumFile.getPostTitle());
        imageContentInfo.setTopicIds(albumFile.getTopicIds());
        imageContentInfo.setTopics(albumFile.getTopicNames());
        imageContentInfo.setFollowStatus(1);
        imageContentInfo.setProvider("upload");
        imageContentInfo.setPendantUrl(CommonUserInfo.userIconImgBox);
        HomeVideoImageListBean.ListBean imageInfo = new HomeVideoImageListBean.ListBean();
        imageInfo.setContent(imageContentInfo);
        if (homeVideoAdapter.getData().isEmpty()) {
            homeVideoAdapter.addData(0, imageInfo);
            contentLayoutBinding.viewPager.setCurrentItem(0, false);
        } else {
            homeVideoAdapter.addData(contentLayoutBinding.viewPager.getCurrentItem() + 1, imageInfo);
            contentLayoutBinding.viewPager.setCurrentItem(contentLayoutBinding.viewPager.getCurrentItem() + 1, false);
        }
        ConstantUtil.isUpdataMyFragmentList = true;
    }

    public void updateExperimentStrategy(String bottomEmotion) {
        this.bottomEmotion = bottomEmotion;
        setExperimentStrategy(contentLayoutBinding.viewPager.getCurrentItem());
    }

    private void setExperimentStrategy(int currentPosition) {
        BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding>) ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(currentPosition);
        if (showVideoPostType(currentPosition) && bindingViewHolder != null && bindingViewHolder.binding != null) {
            if ("landScape".equals(bottomEmotion)) {
                bindingViewHolder.binding.controller.getControllerViewBinding().recyclerView.setVisibility(View.VISIBLE);
            } else {
                bindingViewHolder.binding.controller.getControllerViewBinding().recyclerView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 上报play事件,上传上个视频的播放事件
     * isOnPause 退出应用  上滑或者下拉时
     */
    public void uploadPlay(boolean isOnPause) {
        uploadPlayBase(2, lastPosition, isOnPause, direction, pagePositionList, imgStartTime);
    }

    private void loadExpressDrawNativeAd(int adPosition) {
        //step3:创建广告请求参数AdSlot,具体参数含义参考文档
        Log.d(csjAdloadTag, "request，with：" + expressViewWidth + "-height:" + expressViewHeight);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(activity.getResources().getString(R.string.ttad_home_img_id))
                .setExpressViewAcceptedSize(SizeUtils.px2dp(expressViewWidth), SizeUtils.px2dp(expressViewHeight) - 50) //期望模板广告view的size,单位dp
                .setAdCount(1) //请求广告数量为1到3条
//                .setDownloadType(TTAdConstant.DOWNLOAD_TYPE_POPUP)//应用每次下载都需要触发弹窗披露应用信息，目前暂不需要
                .build();
        //step4:请求广告,对请求回调的广告作渲染处理

        mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                isLoadingAd = false;
                Log.d(csjAdloadTag, message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                isLoadingAd = false;
                if (ads == null || ads.isEmpty()) {
                    return;
                }
                Log.d(csjAdloadTag, "success");

                for (final TTNativeExpressAd ad : ads) {
                    //只有adPosition为1的时候做特殊处理，这样避免用户刷新或者刚进来就请求广告，
                    int insertAdPosition = adPosition == 1 ? loadAdSpaceNum : (adPosition + loadAdSpaceNum - 3);
                    isLoadingAd = false;
                    LogUtils.d(csjAdloadTag, "onRenderSuccess" + homeVideoAdapter.getData().size() + "-adPosition:" + adPosition + "-pos:" + insertAdPosition);
                    HomeVideoImageListBean.ListBean.ContentBean videoContentInfo = new HomeVideoImageListBean.ListBean.ContentBean();
                    videoContentInfo.setPostType(3);
                    videoContentInfo.setAd(ad);
                    videoContentInfo.setPostId(ad.hashCode());
                    videoContentInfo.setAdRequestId(ad.getMediaExtraInfo().get("request_id").toString());
                    videoContentInfo.setAdPos(insertAdPosition);
                    videoContentInfo.setProvider("ad");
                    videoContentInfo.setAdType("csj");

                    HomeVideoImageListBean.ListBean videoInfo = new HomeVideoImageListBean.ListBean();
                    videoInfo.setContent(videoContentInfo);
                    lastAdNoShow = videoInfo;
                    if (homeVideoAdapter.getData().size() >= insertAdPosition) {
                        homeVideoAdapter.addData(insertAdPosition, videoInfo);
                    } else {
                        //没有加入列表的先存到本地
                        adCacheList.add(videoInfo);
                    }
                    //点击监听器必须在getAdView之前调
                    //这个监听器adapte里面也有，可以有位置
                    ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {
                            Log.d(csjAdloadTag, "onAdClicked" + ad.getMediaExtraInfo().hashCode());
                            if (homeVideoAdapter.getData().size() > mCurPos && homeVideoAdapter.getData().get(mCurPos).getContent().getPostType() == 3) {
                                homeVideoAdapter.getData().get(mCurPos).getContent().setAdClickNum(1);
                            }
                        }

                        @Override
                        public void onAdShow(View view, int type) {
                            Log.d(csjAdloadTag, "onAdShow" + ad.hashCode());
                            if (null != lastAdNoShow && ad.getMediaExtraInfo().get("request_id").equals(lastAdNoShow.getContent().getAdRequestId())) {
                                //广告展示过了就不用保留最后一个了
                                Log.d(csjAdloadTag, "onAdShow---null");
                                lastAdNoShow = null;
                            }
                        }

                        @Override
                        public void onRenderFail(View view, String msg, int code) {
                            Log.d(csjAdloadTag, "onRenderFail");
                        }

                        @Override
                        public void onRenderSuccess(View view, float width, float height) {
                            Log.d(csjAdloadTag, "response" + "-width:" + width + "-height:" + height);

                        }
                    });
                    ad.render();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCurrentQuit) {
            imgStartTime = System.currentTimeMillis();
            if (isShowCommentDialog) {
                startCommentDialogTime = System.currentTimeMillis();
            }
        }
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_img_request_list", "");

    }

    @Override
    public void onPause() {
        super.onPause();
        uploadPlay(true);
        //每次退出时记录一下list
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_img_request_list", new Gson().toJson(noLookPostIdList));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在页面结束时 清空队列消息
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.HOME_IMG_UPDATA_AUTHOR_INFO, HomeVideoImageListBean.ListBean.class)
                .subscribe(videoInfo -> {
                    for (int i = 0; i < homeVideoAdapter.getData().size(); i++) {
                        HomeVideoImageListBean.ListBean videoInfos = homeVideoAdapter.getItemData(i);
                        if (videoInfos != null && videoInfos.getContent() != null && videoInfos.getContent().getPostId() == videoInfo.getContent().getPostId()) {
                            videoInfos.setContent(videoInfo.getContent());
                            RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(i);
                            if (viewHolder != null && showVideoPostType(i)) {
                                BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding>) viewHolder;
                                if (bindingViewHolder.binding != null) {
                                    bindingViewHolder.binding.controller.setControllerVideoData(videoInfo.getContent(), true);
                                }
                            }
                        }
                    }
                }));

        //更新是否关注作者
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.HOME_VIDEO_UPDATA_AUTHOR_FOLLOW, String.class)
                .subscribe(authorIdAndFollowStatus -> {
                    if (!TextUtils.isEmpty(authorIdAndFollowStatus) && authorIdAndFollowStatus.contains(",")) {
                        String[] authorArr = authorIdAndFollowStatus.split(","); //前为作者ID后为是否关注
                        for (int i = 0; i < homeVideoAdapter.getData().size(); i++) {
                            HomeVideoImageListBean.ListBean videoInfo = homeVideoAdapter.getItemData(i);
                            if (videoInfo != null && videoInfo.getContent() != null && videoInfo.getContent().getAuthorId() == Long.parseLong(authorArr[0])) {
                                videoInfo.getContent().setFollowStatus(Integer.parseInt(authorArr[1]));
                                RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(i);
                                if (viewHolder != null && showVideoPostType(i)) {
                                    BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding>) viewHolder;
                                    if (bindingViewHolder.binding != null) {
                                        bindingViewHolder.binding.controller.setFollowStatus(videoInfo.getContent());
                                    }
                                }
                            }
                        }
                    }
                }));

        //滑到下一个
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.HOME_IMG_UPDATE_DISLIKE, Integer.class)
                .subscribe(position -> {
                    if (position >= 0 && homeVideoAdapter.getData().size() > 0 && position + 1 < homeVideoAdapter.getData().size()) {
                        viewPager.setCurrentItem(position + 1);
                    }
                }));
    }
}
