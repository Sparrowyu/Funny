package com.sortinghat.funny.ui.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.HeadObjectResult;
import com.alibaba.sdk.android.oss.model.Range;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bytedance.msdk.api.v2.GMMediationAdSdk;
import com.bytedance.msdk.api.v2.GMSettingConfigCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAdLoadCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMUnifiedNativeAd;
import com.bytedance.msdk.api.v2.slot.GMAdOptionUtil;
import com.bytedance.msdk.api.v2.slot.GMAdSlotNative;
//import com.bytedance.msdk.api.AdSlot;
import com.bytedance.msdk.api.v2.slot.paltform.GMAdSlotGDTOption;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jeffmony.videocache.common.VideoType;
import com.jeffmony.videocache.model.VideoCacheInfo;
import com.jeffmony.videocache.task.Mp4CacheTaskForSortingHat;
import com.jeffmony.videocache.utils.ProxyCacheUtils;
import com.jeffmony.videocache.utils.StorageUtils;
import com.jeffmony.videocache.utils.VideoProxyThreadUtils;
import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsDrawAd;
import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsScene;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.comm.util.AdError;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.gmoread.AppConst;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.ad.ConfigAdUtil;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.common.utils.statusbar.StatusBarUtil;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.HomeVideoAdapter;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.bean.VideoFileInfo;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.FragmentHomeVideoImageBinding;
import com.sortinghat.funny.databinding.ItemHomeVideoBinding;
import com.sortinghat.funny.interfaces.RequestCallback;
import com.sortinghat.funny.thirdparty.album.AlbumFile;
import com.sortinghat.funny.util.CommonUserInfo;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DateUtil;
import com.sortinghat.funny.util.ListenerUtils;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.HomeViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by wzy on 2021/6/16
 */
public class HomeVideoFragment extends BaseHomeMediaFragment<HomeViewModel, FragmentHomeVideoImageBinding> {

    private final int HIDE_LOADING_ANIMATION = 100;
    private final int SHARE_ANIMATION_SHOW = 101;
    private final int SHOW_SHARE_ANIMATION_TIME = 20000;
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
                case HIDE_LOADING_ANIMATION:
                    hideLoadingAnimation();
                    break;
            }
        }
    };

    private long mPauseCurrentPosition = 0;//当其余页面播放视频时，首页的退出时的当前进度时间

    public static List<Integer> pagePositionList = new ArrayList<>();//每个的size
    public static int singleSize = 12;//单页请求数量，继续加载时用到
    public static String direction = "appuse";
    public static String requestListDirection = "appuse";

    public static boolean isShowCommentDialog = false;

    public static boolean isCurrentQuit = true; //是否是当前页面退出
    private boolean isLoadingData; //是否正在加载数据

    private boolean isVideoFromRefresh = false;//只用来判断当前视频是否由刷新来的

    private AlbumFile albumFile;

    private ArrayMap<String, OSSAsyncTask<HeadObjectResult>> videoInfoMap = new ArrayMap<>();
    private ArrayMap<String, VideoFileInfo> downloadVideoFileMap = new ArrayMap<>();

    private long playDelayMillis = 280;//小米11卡顿延时

    private String bottomEmotion = "panda"; //感受赞实验策略

    private boolean isShowing = true;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home_video_image;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isShowing = !hidden;
    }

    @Override
    public void onStop() {
        super.onStop();
        isShowing = false;
    }

    @Override
    protected void initViews() {
        if (ConstantUtil.isXiaomiKa()) {
            playDelayMillis = 280;
        } else {
            playDelayMillis = 50;
        }
        isVideoNewAdAB = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getInt(Constant.HOME_VIDEO_AD_NEW_AB, 3);
        initBaseViews();
        setHomeViewModel(viewModel);
        noLookPostIdList = new ArrayList<>();//未看的list
        tabType = 1;
        viewPager = contentLayoutBinding.viewPager;
        contentLayoutBinding.gifHomeLoading.setVisibility(View.VISIBLE);
        GlideUtils.loadGifImageFromResource(R.drawable.home_loading, contentLayoutBinding.gifHomeLoading);
        initViewPagerAdapter();
        RxBus.getDefault().toObservable(RxCodeConstant.NETWORK_CONNECT, Boolean.class)
                .subscribe(connected -> {
                    if (connected && (isCurrentQuit && isShowing)) {
                        RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(lastPosition);
                        if (viewHolder != null) {
                            BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding>) viewHolder;
                            if (showVideoPostType(lastPosition) && bindingViewHolder.binding != null) {
                                bindingViewHolder.binding.gsyVideoPlayer.setPlayPosition(lastPosition);
                                if (bindingViewHolder.binding.gsyVideoPlayer.getGSYVideoManager().isPlaying()) {
                                    bindingViewHolder.binding.gsyVideoPlayer.getGSYVideoManager().start();
                                } else {
                                    bindingViewHolder.binding.gsyVideoPlayer.startPlayLogic();
                                }
                                mPauseCurrentPosition = 0;
                            }
                        }
                    }
                });

    }

    private void initViewPagerAdapter() {
        noLookPostIdList = ConstantUtil.getSPList("home_video_request_list");
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_video_request_list", "");

        direction = "appuse";
        ListenerUtils.getInstance().setHomeRemoveVideoPostIdListener(postId -> {
            if (noLookPostIdList.size() > 0 && noLookPostIdList.contains(postId)) {
                noLookPostIdList.remove(postId);
            }
        });
        homeVideoAdapter = new HomeVideoAdapter(activity, getChildFragmentManager(), tabType, mHandler);
        contentLayoutBinding.viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        contentLayoutBinding.viewPager.setAdapter(homeVideoAdapter);
        contentLayoutBinding.viewPager.setOffscreenPageLimit(3);
        contentLayoutBinding.refreshLayout.setOnRefreshListener(() -> refreshData(1));
        contentLayoutBinding.refreshLayout.setColorSchemeColors(
                Color.RED,
                Color.BLUE,
                Color.YELLOW);
        subscibeRxBus();
    }

    /**
     * from 0:homebutton 1:refresh下拉刷新的，2退出或者重新登录
     */
    public void refreshData(int from) {
        requestListDirection = "refresh";
        if (from == 0) {
            requestListDirection = "homebutton";
            contentLayoutBinding.gifHomeLoading.setVisibility(View.VISIBLE);
            GlideUtils.loadGifImageFromResource(R.drawable.home_loading, contentLayoutBinding.gifHomeLoading);
        }
        if (from == 2) {
            requestListDirection = "outlogin";
            contentLayoutBinding.gifHomeLoading.setVisibility(View.VISIBLE);
            GlideUtils.loadGifImageFromResource(R.drawable.home_loading, contentLayoutBinding.gifHomeLoading);
            noLookPostIdList.clear();
        }
        if (!isLoadingData) {
            isLoadingData = true;
            int playPosition = GSYVideoManager.instance().getPlayPosition();
            if ((playPosition >= 0)) {
                playInfoNormal(playPosition);
            }
            getVideoList(1);
        }
    }

    private void clearListData() {
        hasLoadPosList.clear();
        videoInfoMap.clear();
        downloadVideoFileMap.clear();

        uploadPlay(false);
        isVideoFromRefresh = true;

        homeVideoAdapter.getData().clear();
        pagePositionList.clear();

        albumFile = null;
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
                if (mCurPos > 0 && homeVideoAdapter.getData().size() > 0 && mCurPos < homeVideoAdapter.getData().size()) {
                    ConstantUtil.homeVideoIsAd = homeVideoAdapter.getItemData(mCurPos).getContent().getPostType() == 3;
                }
                Log.d("homevideofrag", "pos:" + position);
                if (!SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(Constant.USER_VIP_TAG)) {
                    //VIP免广告
                    if (isVideoNewAdAB > 1) {
                        if (isVideoToLoadAd(position)) {
                            isLoadingAd = true;
                            loadExpressDrawNativeAd(position);
                        }
                    } else {
                        if (isToLoadAd(position)) {
                            isLoadingAd = true;
                            loadExpressDrawNativeAd(position);
                        }
                    }
                }
//                setExperimentStrategy(position);
                showGuideLayer(position, contentLayoutBinding.viewPager);
//                cancelCacheVideoTask(position);

                int playPosition = GSYVideoManager.instance().getPlayPosition();
                uploadPlay(false);
                if ((position != playPosition)) {
                    playInfoNormal(playPosition);
                }

                if (playPosition >= -1000) {
//                    uploadPlay(false);
                    videoStartTime = System.currentTimeMillis();
                    if (isVideoFromRefresh) {
                        direction = "homebutton";
                    } else {
                        if (position > playPosition) {
                            direction = "up";
                        } else if (position < playPosition) {
                            direction = "down";
                        }
                    }
                    isVideoFromRefresh = false;
                    boolean lastPositionIsAd = false;
                    if (lastPosition > 0 && lastPosition < homeVideoAdapter.getData().size()) {
                        lastPositionIsAd = homeVideoAdapter.getItemData(lastPosition).getContent().getPostType() >= 3;
                    }
                    //对应的播放列表TAG
//                    if (GSYVideoManager.instance().getPlayTag().equals(HomeVideoAdapter.class.getSimpleName()) && (position != playPosition)) {
                    if ((position != playPosition || lastPositionIsAd)) {
                        removeHandlerMessage();
                        mHandler.sendEmptyMessageDelayed(SHARE_ANIMATION_SHOW, SHOW_SHARE_ANIMATION_TIME);
//                        Log.e("release-home230", "pre:");
                        mHandler.postDelayed(() -> playPosition(position, false), playDelayMillis);
                    }

                    if (singleSize != 0) {
                        //索引大于已加载出来的列表数量,才可以加载方法
                        if ((position + 1) >= homeVideoAdapter.getData().size() & !isLoadingData) {
                            if (position >= 0 && homeVideoAdapter.getData().size() > position) {
                                long post_id = homeVideoAdapter.getItemData(position).getContent().getPostId();
                                if (noLookPostIdList.size() > 0 && noLookPostIdList.contains(post_id)) {
                                    noLookPostIdList.remove(post_id);
                                }
                            }
                            isLoadingData = true;
//                            noLookPostIdList.clear();
                            requestListDirection = "up";
                            getVideoList(2);
                        }
                    }
                }


                if (position > playPosition) {
//                    if (position % 2 == 0) {
                    cacheVideoFile(position);
//                    }
                }
                lastPosition = position;
            }
        });
    }

    public void removeHandlerMessage() {
        if (mHandler != null) {
            mHandler.removeMessages(SHARE_ANIMATION_SHOW);
        }
    }

    //把预加载的300k存到缓存框架
    private void saveCacheVideoTask(String url, String savePath, long downloadLength, long fileLength) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        VideoCacheInfo videoCacheInfo = new VideoCacheInfo(url);
        String fileName = url;
        if (!TextUtils.isEmpty(fileName)) {
            fileName = fileName.toLowerCase();
            if (fileName.endsWith(".m3u8")) {
                //当前是M3U8类型
                videoCacheInfo.setVideoType(VideoType.M3U8_TYPE);
            } else {
                //不是M3U8类型，说明是整视频
                videoCacheInfo.setVideoType(VideoType.OTHER_TYPE);
            }
        }

        File downloadPathFile = new File(savePath);
        if (downloadPathFile.exists()) {
            if (downloadLength < 1) {
                downloadLength = downloadPathFile.length();
            }
            videoCacheInfo.setCachedSize(downloadLength);
            videoCacheInfo.setPercent(downloadLength * 1.0f * 100 / fileLength);
            videoCacheInfo.setIsCompleted(downloadLength >= fileLength);
            LinkedHashMap<Long, Long> videoSegMap = new LinkedHashMap<>();
            videoSegMap.put(0L, downloadLength);
            videoCacheInfo.setVideoSegMap(videoSegMap);
        }
        videoCacheInfo.setTotalSize(fileLength);
        videoCacheInfo.setMd5(ProxyCacheUtils.computeMD5(url));
        videoCacheInfo.setSavePath(ProxyCacheUtils.getConfig().getFilePath() + File.separator + videoCacheInfo.getMd5());
        Log.e("onProxyCacheInfo-home", "downloadLength:" + downloadLength + "\n-url:" + url);
        VideoProxyThreadUtils.submitRunnableTask(() -> StorageUtils.saveVideoCacheInfo(videoCacheInfo, new File(videoCacheInfo.getSavePath())));
//        StorageUtils.saveVideoCacheInfo(videoCacheInfo, new File(videoCacheInfo.getSavePath()));
    }

    //一次预加载下一个，后期可以预加载整个列表
    @SuppressLint("RestrictedApi")
    private void cacheVideoFile(int position) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Void>() {
            @Override
            public Void doInBackground() {
                if (position + 1 < homeVideoAdapter.getData().size()) {
                    HomeVideoImageListBean.ListBean videoInfo = homeVideoAdapter.getItemData(position + 1);
                    if (videoInfo != null && videoInfo.getContent() != null && videoInfo.getContent().getPostType() == 1) {
                        if (!TextUtils.isEmpty(videoInfo.getContent().getUrl())) {
                            String md5 = ProxyCacheUtils.computeMD5(videoInfo.getContent().getUrl());

                            String name = "";
                            Uri videoUri = Uri.parse(videoInfo.getContent().getUrl());
                            String fileName = videoUri.getLastPathSegment();
                            if (!TextUtils.isEmpty(fileName)) {
                                fileName = fileName.toLowerCase();
                                if (fileName.endsWith(".m3u8")) {
                                    //当前是M3U8类型
                                    name = md5 + StorageUtils.M3U8_SUFFIX;
                                } else {
                                    //不是M3U8类型，说明是整视频
                                    name = md5 + StorageUtils.NON_M3U8_SUFFIX;
                                }
                            }
                            File saveDir = new File(ProxyCacheUtils.getConfig().getFilePath(), md5);
                            if (!saveDir.exists()) {
                                saveDir.mkdirs();
                            }
//                            File filePath = new File(saveDir.getAbsolutePath(), name);
                            String path = saveDir.getAbsolutePath() + File.separator + name;
                            if (!new File(path).exists()) {
                                asyncGetFileInfo(videoInfo.getContent().getUrl(), videoInfo.getContent().getSize(), path);
//                                viewModel.downloadFile(videoInfo.getContent().getUrl(), path, null, isSaveSuccessful -> {
//                                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable)));
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            public void onSuccess(Void result) {
            }
        });
    }

    private void asyncGetFileInfo(String url, long fileSize, String path) {
        int index = -1;
        if (url.contains("postfile")) {
            index = url.indexOf("postfile");
        } else if (url.contains("view_convert")) {
            index = url.indexOf("view_convert");
        }
        if (index == -1) {
            return;
        }
        String objectKey = url.substring(index);
        OSSAsyncTask<HeadObjectResult> task = FunnyApplication.getDownloadOssService().asyncGetFileInfo(objectKey, new RequestCallback<HeadObjectResult>() {
            @Override
            public void updateProgress(int progress) {
            }

            @Override
            public void onSuccess(HeadObjectResult result) {
                startDownloadVideoFile(result.getMetadata().getContentLength(), objectKey, path, url);
            }

            @Override
            public void onFailure() {
                //有很大部分会走这个方法
                LogUtils.e("onProxyCacheInfo-home", "448-precacheon_failure：" + objectKey);//kkk
            }
        });
    }

    private void startDownloadVideoFile(long fileLength, String objectKey, String path, String url) {
        long downloadLength;
        //最大下载300k
        if (fileLength <= Mp4CacheTaskForSortingHat.DEFAULT_FIRST_RANGE_SIZE) {
            downloadLength = Range.INFINITE;
        } else {
            downloadLength = (long) Mp4CacheTaskForSortingHat.DEFAULT_FIRST_RANGE_SIZE;
        }
        downloadVideoFile(objectKey, path, downloadLength, fileLength, url);
    }


    private void downloadVideoFile(String objectKey, String path, long downloadLength, long fileLength, String url) {

        OSSAsyncTask<GetObjectResult> task = FunnyApplication.getDownloadOssService().asyncRangeDownloadFile(objectKey, 0, downloadLength, new RequestCallback<GetObjectResult>() {
            @Override
            public void updateProgress(int progress) {
            }

            @Override
            public void onSuccess(GetObjectResult result) {
                LogUtils.d("onProxyCacheInfo-home", "re:" + result.getContentLength() + "-path" + path + "-url:" + url);
                writeFileToDisk(url, path, result.getObjectContent(), downloadLength, fileLength);
            }

            @Override
            public void onFailure() {
                LogUtils.e("onProxyCacheInfo-home", "start-precacheon_failure");//kkk
            }
        });
    }

    private boolean writeFileToDisk(String url, String savePath, InputStream inputStream, long downloadLength, long fileLength) {
        File file = new File(savePath);
        if (file.exists()) {
            LogUtils.e("onProxyCacheInfo-home", "fileExists");//如果已经存在文件，说明已经缓存过了，则不用写入了，避免冲突错误
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream outputStream = null;

        byte[] buffer = new byte[4096];
        int len;
        try {
            outputStream = new FileOutputStream(file);
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            //保存到本地后更新缓存框架的信息
            saveCacheVideoTask(url, savePath, downloadLength, fileLength);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void initData() {
        homeVideoAdapter.setViewModel(viewModel);
        requestListDirection = "appuse";
        getVideoList(0);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getVideoListJudgeUserIsLogin(int getDataType) {

        viewModel.getHomeVideoList(getActivity(), 1, noLookPostIdList, requestListDirection).observe(this, resultBean -> {
            contentLayoutBinding.refreshLayout.setRefreshing(false);
            isLoadingData = false;
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    long userId = resultBean.getData().getUserId();
                    if (userId > 0) {
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_id", userId);
                        ConstantUtil.setAlias(activity, userId);
                    }
                    if (TextUtils.isEmpty(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("authToken"))) {
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("authToken", resultBean.getData().getAuthToken());
                        if (!TextUtils.isEmpty(resultBean.getData().getLongTermToken())) {
                            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("longTermToken", resultBean.getData().getLongTermToken());
                        }
                    }
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_video_request_list", "");
                    if (getDataType == 1) {
                        clearListData();
                    }
                    noLookPostIdList.clear();
                    singleSize = 0;
                    List<HomeVideoImageListBean.ListBean> videoList = resultBean.getData().getList();
                    if (videoList != null && !videoList.isEmpty()) {
                        pagePositionList.add(homeVideoAdapter.getData().size() + videoList.size());
                        singleSize = videoList.size();
                        //把此次请求的数据写到集合中
                        for (int i = homeVideoAdapter.getData().isEmpty() ? 1 : 0; i < videoList.size(); i++) {
                            noLookPostIdList.add(videoList.get(i).getContent().getPostId());
                        }
                        if (homeVideoAdapter.getData().isEmpty()) {
                            GlideUtils.loadImageToCacheFile(activity, videoList.get(0).getContent() == null ? "" : videoList.get(0).getContent().getThumb(), null);
                            GSYVideoManager.releaseAllVideos();
                            homeVideoAdapter.setNewData(videoList);
                            if (contentLayoutBinding.viewPager.getCurrentItem() != 0) {
                                contentLayoutBinding.viewPager.setCurrentItem(0, false);
                            } else {
//                                cacheVideoFile(0);
                            }
                        } else {
                            homeVideoAdapter.addData(videoList);
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
                        }
                    } else {
                        if (homeVideoAdapter.getData().isEmpty()) {
                            hideLoadingAnimation();
                            GSYVideoManager.releaseAllVideos();
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

    //当userid为-1时，重新调一下登录接口
    @SuppressLint("NotifyDataSetChanged")
    private void getVideoList(int getDataType) {
        contentLayoutBinding.getRoot().setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("authToken")) || SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id") <= 0) {
            viewModel.getLoginId(0).observe(this, resultBean -> {
                if (resultBean != null) {
                    if (resultBean.getCode() == 0) {
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_id", resultBean.getData().getUserBase().getId());
                        ConstantUtil.setAlias(activity, resultBean.getData().getUserBase().getId());
                        //user_status-0：游客 1：注册成功 2:账户已经注销
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("user_status", resultBean.getData().getUserBase().getStatus());
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("authToken", resultBean.getData().getAuthToken());
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("longTermToken", resultBean.getData().getLongTermToken());

                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_days", resultBean.getData().getDays());
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("token_date", DateUtil.getTodayDateStringToServer());


                        //没有成功登录时，当userid为-1时，存一下进入埋点
                        JsonObject startJsonObject = new JsonObject();
                        long createTime = System.currentTimeMillis();
                        startJsonObject.addProperty("create_time", createTime);//事件时间
                        RequestParamUtil.addStartLogHeadParam(startJsonObject, "foreground", "app", "index", "app");
                        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("foreground_time", createTime);
                        ConstantUtil.spStartLog(startJsonObject.toString());

                        viewModel.getClientConfig().observe(this, resultBean1 -> {
                        });
                        getVideoListJudgeUserIsLogin(getDataType);
                    } else {
                        getVideoListJudgeUserIsLogin(getDataType);
                    }
                } else {
                    getVideoListJudgeUserIsLogin(getDataType);
                }
            });
        } else {
            getVideoListJudgeUserIsLogin(getDataType);
        }
    }


    private void hideLoadingAnimation() {
        if (contentLayoutBinding != null && contentLayoutBinding.gifHomeLoading != null) {
            GifDrawable gifDrawable = (GifDrawable) contentLayoutBinding.gifHomeLoading.getDrawable();
            if (gifDrawable != null && gifDrawable.isRunning()) {
                gifDrawable.stop();
            }
            contentLayoutBinding.gifHomeLoading.setVisibility(View.GONE);
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
                bindingViewHolder.binding.controller.showControllerInfoToNorman(false);
                bindingViewHolder.binding.likeview.setShow(true);
                bindingViewHolder.binding.gsyVideoPlayer.showStartProgress(false);
                bindingViewHolder.binding.controller.setScrollAlpha(0);
                bindingViewHolder.binding.controller.showShareAnimation(false);
                bindingViewHolder.binding.gsyVideoPlayer.onVideoPause();
            }
        }
    }

    //iserror  从冲突后回来，从以前进度播放
    private void playPosition(int position, boolean isError) {
        RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding>) viewHolder;
            if (showVideoPostType(position) && bindingViewHolder.binding != null) {
                bindingViewHolder.binding.gsyVideoPlayer.setPlayPosition(position);
                if (isError) {
                    bindingViewHolder.binding.gsyVideoPlayer.setSeekOnStart(mPauseCurrentPosition);
                    bindingViewHolder.binding.gsyVideoPlayer.seekTo(mPauseCurrentPosition);
                }
                bindingViewHolder.binding.gsyVideoPlayer.startPlayLogic();
                mPauseCurrentPosition = 0;
                bindingViewHolder.binding.rlHomeLikeDialog.setVisibility(View.GONE);
                bindingViewHolder.binding.likeview.setShow(true);
                bindingViewHolder.binding.gsyVideoPlayer.showStartProgress(false);
                bindingViewHolder.binding.rlHomeDislikeDialog.setVisibility(View.GONE);
                bindingViewHolder.binding.controller.showControllerInfoToNorman(false);
            } else {
                GSYVideoManager.onPause();
            }
        } else {
            if (isShareVideo) {
                isShareVideo = false;
                startPlayPublishVideo(GSYVideoManager.instance().getPlayPosition() + 1);
            } else if (albumFile != null) {
                startPlayPublishVideo(GSYVideoManager.instance().getPlayPosition() + 1);
                ConstantUtil.isUpdataMyFragmentList = true;
            }
        }
    }

    private boolean isShareVideo = false;

    protected void playShareVideo(HomeVideoImageListBean.ListBean videoInfo) {
        if (contentLayoutBinding.gifHomeLoading != null) {
            hideLoadingAnimation();
        }
        if (homeVideoAdapter.getData().isEmpty()) {
            homeVideoAdapter.addData(0, videoInfo);
        } else {
            //判断当前是否有这个帖子，避免重复帖子多次出现在列表中
            for (int i = 0; i < homeVideoAdapter.getData().size(); i++) {
                if (videoInfo.getContent().getPostId() == homeVideoAdapter.getData().get(i).getContent().getPostId()) {
                    contentLayoutBinding.viewPager.setCurrentItem(i, false);
                    return;
                }
            }
            isShareVideo = true;
            homeVideoAdapter.addData(GSYVideoManager.instance().getPlayPosition() + 1, videoInfo);
            contentLayoutBinding.viewPager.setCurrentItem(GSYVideoManager.instance().getPlayPosition() + 1, false);
        }
    }

    protected void playPublishVideo(AlbumFile albumFiles) {
        albumFile = albumFiles;
        addPublishVideo();
    }

    private void addPublishVideo() {
        HomeVideoImageListBean.ListBean.ContentBean videoContentInfo = new HomeVideoImageListBean.ListBean.ContentBean();
        videoContentInfo.setCreatedAt(System.currentTimeMillis());
        videoContentInfo.setAuthorId(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"));
        videoContentInfo.setAvatar(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("user_avatar", ""));
        videoContentInfo.setNickname(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("user_nike_name", ""));
        videoContentInfo.setPostId(albumFile.getPostId());
        videoContentInfo.setPostType(albumFile.getMediaType());
        videoContentInfo.setUrl(albumFile.getPath());
        videoContentInfo.setThumb(albumFile.getPath());
        videoContentInfo.setTitle(albumFile.getPostTitle());
        videoContentInfo.setTopicIds(albumFile.getTopicIds());
        videoContentInfo.setTopics(albumFile.getTopicNames());
        videoContentInfo.setFollowStatus(1);
        videoContentInfo.setProvider("upload");
        videoContentInfo.setPendantUrl(CommonUserInfo.userIconImgBox);
        HomeVideoImageListBean.ListBean videoInfo = new HomeVideoImageListBean.ListBean();
        videoInfo.setContent(videoContentInfo);
        int getPlayPosition = GSYVideoManager.instance().getPlayPosition();
        if (homeVideoAdapter.getData().isEmpty() || GSYVideoManager.instance().getPlayPosition() < 0) {
            homeVideoAdapter.addData(0, videoInfo);
        } else {
            if (getPlayPosition >= 0 && getPlayPosition + 1 < homeVideoAdapter.getData().size()) {
                homeVideoAdapter.addData(getPlayPosition + 1, videoInfo);
                contentLayoutBinding.viewPager.setCurrentItem(getPlayPosition + 1, false);
            }
        }
        ConstantUtil.isUpdataMyFragmentList = true;
    }

    private void startPlayPublishVideo(int playPosition) {
        ThreadUtils.runOnUiThreadDelayed(() -> {
            BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding>) ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(playPosition);
            if (showVideoPostType(playPosition) && bindingViewHolder != null && bindingViewHolder.binding != null) {
                bindingViewHolder.binding.gsyVideoPlayer.setPlayPosition(playPosition);
                bindingViewHolder.binding.gsyVideoPlayer.startPlayLogic();
            }
        }, 800);
    }

    public void updateExperimentStrategy(String bottomEmotion) {
        this.bottomEmotion = bottomEmotion;
        setExperimentStrategy(GSYVideoManager.instance().getPlayPosition());
    }

    private void setExperimentStrategy(int currentPlayPosition) {
        BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding>) ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(currentPlayPosition);
        if (showVideoPostType(currentPlayPosition) && bindingViewHolder != null && bindingViewHolder.binding != null) {
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
     * 只要离开当前view就离开发日志
     */
    public void uploadPlay(boolean isOnPause) {
        uploadPlayBase(1, lastPosition, isOnPause, direction, pagePositionList, videoStartTime);
    }

    private int csj_ad_pop = 0;

    private void loadExpressDrawNativeAd(int adPosition) {

        int currentAd = ConfigAdUtil.getHomeVideoAdType();
        if (!ConstantUtil.isHuaweiThroughReviewState(activity)) {
            currentAd = 1;
            csj_ad_pop = 1;
        }
        switch (currentAd) {
            case 1:
                loadCSJExpressDrawNativeAd(adPosition);
                break;
            case 2:
                loadGDTExpressDrawNativeAd(adPosition);
                break;
            case 3:
                loadKSExpressDrawNativeAd(adPosition);
                break;
            case 4:
                loadTTGroMoreNativeAd(adPosition);
                break;
            default:
                loadCSJExpressDrawNativeAd(adPosition);
                break;
        }

    }

    private GMUnifiedNativeAd mTTGmAdNative;

    /**
     * config回调
     */
    @SuppressLint("LongLogTag")
    private GMSettingConfigCallback mSettingConfigCallback = new GMSettingConfigCallback() {
        @Override
        public void configLoad() {
            loadGroMoreAd(adGmPosition);
        }
    };

    private void loadTTGroMoreNativeAd(int adPosition) {
        adGmPosition = adPosition;
        /**
         * 判断当前是否存在config 配置 ，如果存在直接加载广告 ，如果不存在则注册config加载回调
         */
        if (GMMediationAdSdk.configLoadSuccess()) {
            Log.e(csjAdloadTag, "load ad 当前config配置存在，直接加载广告");
            loadGroMoreAd(adPosition);
        } else {
            Log.e(csjAdloadTag, "load ad 当前config配置不存在，正在请求config配置....");
            GMMediationAdSdk.registerConfigCallback(mSettingConfigCallback); //不能使用内部类，否则在ondestory中无法移除该回调
        }
    }

    private int adGmPosition = 0;

    private void loadGroMoreAd(int adPosition) {
        String videoAdId = AppConst.gMoreHomeVideoId;
        mTTGmAdNative = new GMUnifiedNativeAd(activity, videoAdId);

        // 针对Gdt Native自渲染广告，可以自定义gdt logo的布局参数。该参数可选,非必须。
        FrameLayout.LayoutParams gdtNativeAdLogoParams =
                new FrameLayout.LayoutParams(
                        SizeUtils.dp2px(40),
                        SizeUtils.dp2px(13),
                        Gravity.RIGHT | Gravity.TOP); // 例如，放在右上角


        GMAdSlotGDTOption.Builder adSlotNativeBuilder = GMAdOptionUtil.getGMAdSlotGDTOption()
                .setNativeAdLogoParams(gdtNativeAdLogoParams);

        /* 创建feed广告请求类型参数GMAdSlotNative,具体参数含义参考文档
         * 备注
         * 1: 如果是信息流自渲染广告，设置广告图片期望的图片宽高 ，不能为0
         * 2:如果是信息流模板广告，宽度设置为希望的宽度，高度设置为0(0为高度选择自适应参数)
         */
        GMAdSlotNative adSlotNative = new GMAdSlotNative.Builder()
                .setGMAdSlotBaiduOption(GMAdOptionUtil.getGMAdSlotBaiduOption().build())//百度相关的配置
                .setGMAdSlotGDTOption(adSlotNativeBuilder.setGDTAutoPlayMuted(false).build())//gdt相关的配置
                .setAdmobNativeAdOptions(GMAdOptionUtil.getAdmobNativeAdOptions())//admob相关配置
                .setAdStyleType(com.bytedance.msdk.api.AdSlot.TYPE_EXPRESS_AD)//必传，表示请求的模板广告还是原生广告，AdSlot.TYPE_EXPRESS_AD：模板广告 ； AdSlot.TYPE_NATIVE_AD：原生广告
                // 备注
                // 1:如果是信息流自渲染广告，设置广告图片期望的图片宽高 ，不能为0
                // 2:如果是信息流模板广告，宽度设置为希望的宽度，高度设置为0(0为高度选择自适应参数)
                .setImageAdSize(SizeUtils.px2dp(expressViewWidth), SizeUtils.px2dp(expressViewHeight + StatusBarUtil.getStatusBarHeight(activity)))// 必选参数 单位dp ，详情见上面备注解释
                .setAdCount(1)//请求广告数量为1到3条
                .build();

        new com.bytedance.msdk.api.AdSlot.Builder()
                .setAdCount(1);

        //请求广告，调用feed广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
        /**
         * 注：每次加载信息流广告的时候需要新建一个GMUnifiedNativeAd，否则可能会出现广告填充问题
         * (例如：mTTAdNative = new GMUnifiedNativeAd(this, mAdUnitId);）
         */
        mTTGmAdNative.loadAd(adSlotNative, new GMNativeAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull List<GMNativeAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    return;
                }
                Log.d(csjAdloadTag, "loadsuess：" + ads.get(0).isExpressAd() + "-mode:" + ads.get(0).getAdImageMode());
//
//                //设置点击展示回调监听
//                ads.get(0).setNativeAdListener(new GMNativeExpressAdListener() {
//                    @Override
//                    public void onAdClick() {
//                    }
//
//                    @Override
//                    public void onAdShow() {
//
//                    }
//
//                    @Override
//                    public void onRenderFail(View view, String msg, int code) {
//                        Log.d("csjAdload", "gm--adapter-onRenderFail");
//                    }
//
//                    // ** 注意点 ** 不要在广告加载成功回调里进行广告view展示，要在onRenderSucces进行广告view展示，否则会导致广告无法展示。
//                    @Override
//                    public void onRenderSuccess(float width, float height) {

                //只有adPosition为1的时候做特殊处理，这样避免用户刷新或者刚进来就请求广告，
                int insertAdPosition = adPosition == 1 ? loadAdSpaceNum : (adPosition + loadAdSpaceNum - 3);//旧版加载的为1，12，12，展示的为2,11,20
                if (isVideoNewAdAB > 1) {
                    insertAdPosition = adPosition == 1 ? isVideoNewAdAB : (adPosition + loadAdSpaceNum - 3);//新版本，展示的为3,12,21 或者 4 13 22 填充为前五个
                }

                isLoadingAd = false;
                LogUtils.d(csjAdloadTag, "onRenderSuccess" + homeVideoAdapter.getData().size() + "-adPosition:" + adPosition + "-pos:" + insertAdPosition);
                HomeVideoImageListBean.ListBean.ContentBean videoContentInfo = new HomeVideoImageListBean.ListBean.ContentBean();
                videoContentInfo.setPostType(3);
                videoContentInfo.setmGMNativeAd(ads.get(0));
                videoContentInfo.setPostId(ads.get(0).hashCode());
                videoContentInfo.setAdRequestId("gMore");
                videoContentInfo.setAdPos(insertAdPosition);
                videoContentInfo.setProvider("ad");
                videoContentInfo.setAdType("gMore");

                HomeVideoImageListBean.ListBean videoInfo = new HomeVideoImageListBean.ListBean();
                videoInfo.setContent(videoContentInfo);
                lastAdNoShow = videoInfo;
                if (homeVideoAdapter.getData().size() >= insertAdPosition) {
                    homeVideoAdapter.addData(insertAdPosition, videoInfo);
                } else {
                    //没有加入列表的先存到本地
                    adCacheList.add(videoInfo);
                }
//                    }
//                });
//                ads.get(0).render();
            }

            //
            @Override
            public void onAdLoadedFail(@NonNull com.bytedance.msdk.api.AdError adError) {
                Log.e(csjAdloadTag, "adError：" + adError.message);
                loadCSJExpressDrawNativeAd(adPosition);
            }
        });


    }

    private NativeUnifiedAD mAdManager;

    private void loadGDTExpressDrawNativeAd(int adPosition) {
        String videoAdId = activity.getResources().getString(R.string.gdtad_home_video_id);
        mAdManager = new NativeUnifiedAD(activity, videoAdId, new NativeADUnifiedListener() {
            @Override
            public void onADLoaded(List<NativeUnifiedADData> list) {
                LogUtils.d(csjAdloadTag, "gdt-onADLoadeds");
                if (null != list && list.size() > 0) {
                    //只有adPosition为1的时候做特殊处理，这样避免用户刷新或者刚进来就请求广告，
                    int insertAdPosition = adPosition == 1 ? loadAdSpaceNum : (adPosition + loadAdSpaceNum - 3);//旧版加载的为1，12，12，展示的为2,11,20
                    if (isVideoNewAdAB > 1) {
                        insertAdPosition = adPosition == 1 ? isVideoNewAdAB : (adPosition + loadAdSpaceNum - 3);//新版本，展示的为3,12,21 或者 4 13 22 填充为前五个
                    }

                    isLoadingAd = false;
                    LogUtils.d(csjAdloadTag, "onRenderSuccess" + homeVideoAdapter.getData().size() + "-adPosition:" + adPosition + "-pos:" + insertAdPosition);
                    HomeVideoImageListBean.ListBean.ContentBean videoContentInfo = new HomeVideoImageListBean.ListBean.ContentBean();
                    videoContentInfo.setPostType(3);
                    videoContentInfo.setGdtAd(list.get(0));
                    videoContentInfo.setPostId(list.get(0).hashCode());
                    videoContentInfo.setAdRequestId("gdt");
                    videoContentInfo.setAdPos(insertAdPosition);
                    videoContentInfo.setProvider("ad");
                    videoContentInfo.setAdType("gdt");

                    HomeVideoImageListBean.ListBean videoInfo = new HomeVideoImageListBean.ListBean();
                    videoInfo.setContent(videoContentInfo);
                    lastAdNoShow = videoInfo;
                    if (homeVideoAdapter.getData().size() >= insertAdPosition) {
                        homeVideoAdapter.addData(insertAdPosition, videoInfo);
                    } else {
                        //没有加入列表的先存到本地
                        adCacheList.add(videoInfo);
                    }
                }
            }

            @Override
            public void onNoAD(AdError adError) {
                LogUtils.d(csjAdloadTag, "gdt-adError" + adError.getErrorCode() + adError.getErrorMsg());
            }
        });
//        mAdManager.setMinVideoDuration(getMinVideoDuration());
//        mAdManager.setMaxVideoDuration(getMaxVideoDuration());

        mAdManager.loadData(1);


    }

    private void loadCSJExpressDrawNativeAd(int adPosition) {
        String videoAdId = (isVideoNewAdAB > 1) ? activity.getResources().getString(R.string.ttad_home_video_id) : activity.getResources().getString(R.string.ttad_home_video_other_id);
        LogUtils.d(csjAdloadTag, "request，with：" + expressViewWidth + "-height:" + expressViewHeight);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(videoAdId)
                .setExpressViewAcceptedSize(SizeUtils.px2dp(expressViewWidth), SizeUtils.px2dp(expressViewHeight)) //期望模板广告view的size,单位dp
                .setAdCount(1) //请求广告数量为1到3条
//                .setDownloadType(csj_ad_pop)//应用每次下载都需要触发弹窗披露应用信息，目前暂不需要
                .build();
        //step4:请求广告,对请求回调的广告作渲染处理

        mTTAdNative.loadExpressDrawFeedAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                isLoadingAd = false;
                LogUtils.e(csjAdloadTag, message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                isLoadingAd = false;
                if (ads == null || ads.isEmpty()) {
                    return;
                }
                LogUtils.d(csjAdloadTag, "onAdLoad"+adPosition);
                for (final TTNativeExpressAd ad : ads) {
                    //点击监听器必须在getAdView之前调
                    ad.setCanInterruptVideoPlay(true);
                    //这个监听器adapte里面也有，可以有位置
                    ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
                        @Override
                        public void onAdClicked(View view, int type) {
                            LogUtils.d(csjAdloadTag, "onAdClicked" + ad.getMediaExtraInfo().hashCode());
                            if (homeVideoAdapter.getData().size() > mCurPos && homeVideoAdapter.getData().get(mCurPos).getContent().getPostType() == 3) {
                                homeVideoAdapter.getData().get(mCurPos).getContent().setAdClickNum(1);
                            }
                        }

                        @Override
                        public void onAdShow(View view, int type) {
                            Log.d(csjAdloadTag, "onAdShow" + ad.hashCode());
                            if (null != lastAdNoShow && ad.getMediaExtraInfo().get("request_id").equals(lastAdNoShow.getContent().getAdRequestId())) {
                                //广告展示过了就不用保留最后一个了
                                LogUtils.d(csjAdloadTag, "onAdShow---null");
                                lastAdNoShow = null;
                            }
                        }

                        @Override
                        public void onRenderFail(View view, String msg, int code) {
                            isLoadingAd = false;
                            LogUtils.e(csjAdloadTag, "onRenderFail");
                        }

                        @Override
                        public void onRenderSuccess(View view, float width, float height) {
                            //只有adPosition为1的时候做特殊处理，这样避免用户刷新或者刚进来就请求广告，
                            int insertAdPosition = adPosition == 1 ? loadAdSpaceNum : (adPosition + loadAdSpaceNum - 3);//旧版加载的为1，12，12，展示的为2,11,20
                            if (isVideoNewAdAB > 1) {
                                insertAdPosition = adPosition == 1 ? isVideoNewAdAB : (adPosition + loadAdSpaceNum - 3);//新版本，展示的为3,12,21 或者 4 13 22 填充为前五个
                            }

                            isLoadingAd = false;
                            LogUtils.d(csjAdloadTag, "onRenderSuccess" + homeVideoAdapter.getData().size() + "-adPosition:" + adPosition + "-pos:" + insertAdPosition);
                            HomeVideoImageListBean.ListBean.ContentBean videoContentInfo = new HomeVideoImageListBean.ListBean.ContentBean();
                            videoContentInfo.setPostType(3);
                            videoContentInfo.setAd(ad);
                            videoContentInfo.setPostId(ad.hashCode());
                            videoContentInfo.setAdRequestId("csj");
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
                        }
                    });
                    ad.render();
                }
            }
        });
    }

    private void loadKSExpressDrawNativeAd(int adPosition) {


        String videoAdId = activity.getResources().getString(R.string.ksad_home_video_id);

        KsScene scene = new KsScene.Builder(Long.valueOf(videoAdId)) // 请联系快⼿平台申请
                .adNum(1) // ⽀持返回多条⼴告，默认1条，最多5条，参数范围1-5
                .build();
        KsAdSDK.getLoadManager().loadDrawAd(scene, new
                KsLoadManager.DrawAdListener() {
                    @Override
                    public void onError(int i, String s) {
                        LogUtils.d(csjAdloadTag, "ks-adError：" + i + "--s-" + s);
                    }

                    @Override
                    public void onDrawAdLoad(@Nullable List<KsDrawAd> adList) {
                        if (adList == null || adList.isEmpty()) {
                            return;
                        }
                        if (adList.size() > 0 && adList.get(0) != null) {

                            //只有adPosition为1的时候做特殊处理，这样避免用户刷新或者刚进来就请求广告，
                            int insertAdPosition = adPosition == 1 ? loadAdSpaceNum : (adPosition + loadAdSpaceNum - 3);//旧版加载的为1，12，12，展示的为2,11,20
                            if (isVideoNewAdAB > 1) {
                                insertAdPosition = adPosition == 1 ? isVideoNewAdAB : (adPosition + loadAdSpaceNum - 3);//新版本，展示的为3,12,21 或者 4 13 22 填充为前五个
                            }

                            isLoadingAd = false;
                            LogUtils.d(csjAdloadTag, "onRenderSuccess" + homeVideoAdapter.getData().size() + "-adPosition:" + adPosition + "-pos:" + insertAdPosition);
                            HomeVideoImageListBean.ListBean.ContentBean videoContentInfo = new HomeVideoImageListBean.ListBean.ContentBean();
                            videoContentInfo.setPostType(3);
                            videoContentInfo.setKsAd(adList.get(0));
                            videoContentInfo.setPostId(adList.get(0).hashCode());
                            videoContentInfo.setAdRequestId("ks");
                            videoContentInfo.setAdPos(insertAdPosition);
                            videoContentInfo.setProvider("ad");
                            videoContentInfo.setAdType("ks");

                            HomeVideoImageListBean.ListBean videoInfo = new HomeVideoImageListBean.ListBean();
                            videoInfo.setContent(videoContentInfo);
                            lastAdNoShow = videoInfo;
                            if (homeVideoAdapter.getData().size() >= insertAdPosition) {
                                homeVideoAdapter.addData(insertAdPosition, videoInfo);
                            } else {
                                //没有加入列表的先存到本地
                                adCacheList.add(videoInfo);
                            }

                            adList.get(0).setAdInteractionListener(new KsDrawAd.AdInteractionListener() {
                                @Override
                                public void onAdClicked() {

                                }

                                @Override
                                public void onAdShow() {

                                }

                                @Override
                                public void onVideoPlayStart() {

                                }

                                @Override
                                public void onVideoPlayPause() {

                                }

                                @Override
                                public void onVideoPlayResume() {

                                }

                                @Override
                                public void onVideoPlayEnd() {

                                }

                                @Override
                                public void onVideoPlayError() {

                                }
                            });

                        }
                    }
                });

    }


    @Override
    public void onResume() {
        super.onResume();
        isShowing = true;
        if (getUserVisibleHint()) {
            //返回可见时更新开始时间
            //如果是当前页面退出，回来计时
            if (HomeVideoFragment.isCurrentQuit) {
                long currentTime = System.currentTimeMillis();
                HomeVideoFragment.videoStartTime = currentTime;
                postVideoPlayDurationTime = currentTime;
                if (isShowCommentDialog) {
                    HomeVideoFragment.startCommentDialogTime = currentTime;
                }
                if (homeVideoAdapter.getData().size() > mCurPos) {
                    if (homeVideoAdapter.getData().get(mCurPos).getContent().getPostType() == 3) {
                        //广点通广告得有resume
                        if (null != homeVideoAdapter.getData().get(mCurPos).getContent().getGdtAd()) {
                            homeVideoAdapter.getData().get(mCurPos).getContent().getGdtAd().resume();
                        }   //广点通广告得有resume
                        if (null != homeVideoAdapter.getData().get(mCurPos).getContent().getmGMNativeAd()) {
                            homeVideoAdapter.getData().get(mCurPos).getContent().getmGMNativeAd().resume();
                        }

                    } else {
                        if (GSYVideoManager.instance().listener() != null) {
                            GSYVideoManager.onResume(false);
                            HomeVideoFragment.startPlayTime = System.currentTimeMillis();
                        } else {
                            //修复退出后台再回来可能出现视频丢失的bug
                            if (lastPosition >= 0) {
                                onVideoErrorPlay();
                                LogUtils.e("gsyPlayerLife-video-frag", "-error:" + lastPosition);
                            }
                        }
                    }
                }

            }
        }
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_video_request_list", "");
    }

    //视频sdk会出现错误时
    public void onVideoErrorPlay() {
        if (lastPosition >= 0 & isCurrentQuit) {
            playPosition(lastPosition, true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
        uploadPlay(true);
        mPauseCurrentPosition = GSYVideoManager.instance().getCurrentPosition();
        //每次退出时记录一下list，崩溃的时候可能onpause存不了，所以取的时候每次都要清空一下
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_video_request_list", new Gson().toJson(noLookPostIdList));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        //在页面结束时 清空队列消息
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (homeVideoAdapter != null) {
            if (!homeVideoAdapter.getData().isEmpty()) {
                for (HomeVideoImageListBean.ListBean item : homeVideoAdapter.getData()) {
                    if (item != null && item.getContent().getPostType() == 3 && null != item.getContent().getAd()) {
                        if (null != item.getContent().getAd()) {
                            item.getContent().getAd().destroy();
                        }
                        if (null != item.getContent().getmGMNativeAd()) {
                            item.getContent().getmGMNativeAd().destroy();
                        }
                    }
                }
            }
        }
        GMMediationAdSdk.unregisterConfigCallback(mSettingConfigCallback);
    }

    private void subscibeRxBus() {
        //更新带过去对帖子本身的操作
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.HOME_VIDEO_UPDATA_AUTHOR_INFO, HomeVideoImageListBean.ListBean.class)
                .subscribe(videoInfo -> {
                    for (int i = 0; i < homeVideoAdapter.getData().size(); i++) {
                        HomeVideoImageListBean.ListBean videoInfos = homeVideoAdapter.getItemData(i);
                        if (videoInfos != null && videoInfos.getContent() != null && videoInfos.getContent().getPostId() == videoInfo.getContent().getPostId()) {
                            videoInfos.setContent(videoInfo.getContent());
                            RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(i);
                            if (showVideoPostType(i) && viewHolder != null) {
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
                                if (showVideoPostType(i) && viewHolder != null) {
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
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.HOME_VIDEO_UPDATE_DISLIKE, Integer.class)
                .subscribe(position -> {
                    if (position >= 0 && homeVideoAdapter.getData().size() > 0 && position + 1 < homeVideoAdapter.getData().size()) {
                        viewPager.setCurrentItem(position + 1);
                    }
                }));
    }
}
