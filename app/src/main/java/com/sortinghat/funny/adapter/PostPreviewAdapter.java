package com.sortinghat.funny.adapter;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.gson.JsonObject;
import com.jeffmony.videocache.VideoProxyCacheManager;
import com.jeffmony.videocache.common.ProxyMessage;
import com.jeffmony.videocache.common.VideoParams;
import com.jeffmony.videocache.utils.VideoParamsUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CheckNetwork;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.StorageUtil;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.BuildConfig;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.HomeVideoDisLikeBean;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.bean.HomeVideoLikeBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.DialogHomeDislikeBinding;
import com.sortinghat.funny.databinding.DialogHomeRewardBinding;
import com.sortinghat.funny.databinding.ItemPostPreviewBinding;
import com.sortinghat.funny.thirdparty.video.OnVideoControllerListener;
import com.sortinghat.funny.thirdparty.video.SampleCoverVideo;
import com.sortinghat.funny.ui.BottomSheetDialog.BigImgDialog;
import com.sortinghat.funny.ui.BottomSheetDialog.DeletePostDialog;
import com.sortinghat.funny.ui.BottomSheetDialog.DownloadVideoDialog;
import com.sortinghat.funny.ui.BottomSheetDialog.DownloadVideoResultDialog;
import com.sortinghat.funny.ui.common.PostPreviewActivity;
import com.sortinghat.funny.ui.common.TopicPostPreviewActivity;
import com.sortinghat.funny.ui.home.OnShareDialogListener;
import com.sortinghat.funny.ui.home.PostReviewDialog;
import com.sortinghat.funny.ui.home.ReportActivity;
import com.sortinghat.funny.ui.home.ShareDialog;
import com.sortinghat.funny.ui.my.LoginActivity;
import com.sortinghat.funny.ui.my.MyOtherUserInfoActivity;
import com.sortinghat.funny.util.AdManager;
import com.sortinghat.funny.util.CommonUtil;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DownloadSPUtils;
import com.sortinghat.funny.util.EditUtils;
import com.sortinghat.funny.util.ImageEditUtils;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.util.PostDownloadManager;
import com.sortinghat.funny.util.VideoEditUtils;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.utils.ShareLibraryUM;
import com.sortinghat.funny.view.LikeView;
import com.sortinghat.funny.viewmodel.HomeViewModel;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PostPreviewAdapter extends BaseBindingAdapter<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding> {
    public static final double BUFFER_MAX_INTERVAL = 15000f;
    private Context mContext;
    private GSYVideoOptionBuilder gsyVideoOptionBuilder;
    private HomeViewModel viewModel;
    private int tab;//1 video, 2 img

    private final String from;
    private int fromPage = 0;////0:我的 1：其他人页面 2：消息通知页面 3：话题页面
    private int tabType = 0;//0:我的发布 1:我的评论 2：我的喜欢
    private String provider = "server";//"mine" : "author", message,"server"
    private String path = "mine";//"mine" : "author", "server"

    private Handler handler;
    private FragmentManager childFragmentManager;
    private Runnable runnable;
    private int dialogCancelTime = 7000;

    private DownloadVideoDialog downloadVideoDialog;
    private DownloadVideoResultDialog downloadVideoResultDialog;

    public PostPreviewAdapter(Context context, FragmentManager childFragmentManager, Handler handler, int fromPage, String from, int tabType) {
        super(R.layout.item_post_preview);
        this.mContext = context;
        this.handler = handler;
        this.childFragmentManager = childFragmentManager;
        this.fromPage = fromPage;
        this.from = from;
        this.tabType = tabType;
        switch (fromPage) {
            case 0:
                provider = "server";
                path = "mine";
                break;
            case 1:
                provider = "server";
                path = "author";
                break;
            case 2:
                provider = "message";
                path = "message";
                break;
            case 3:
                provider = "topic";
                path = "topic";
                break;
        }

    }

    public void setViewModel(HomeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (tab == 1) {
        gsyVideoOptionBuilder = new GSYVideoOptionBuilder();
//        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    protected void bindView(BaseBindingHolder holder, HomeVideoImageListBean.ListBean videoInfo, ItemPostPreviewBinding binding, int position) {
        if (null != videoInfo && null != videoInfo.getContent()) {
            tab = videoInfo.getContent().getPostType();
            if (tab == 1 && gsyVideoOptionBuilder != null) {
                binding.gsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);
                binding.gsyVideoPlayer.getBackButton().setVisibility(View.GONE);
                binding.gsyVideoPlayer.getFullscreenButton().setVisibility(View.GONE);
                binding.gsyVideoPlayer.setVisibility(View.VISIBLE);
                binding.mainImage.setVisibility(View.GONE);

                gsyVideoOptionBuilder.setIsTouchWiget(false)
                        .setUrl(videoInfo.getContent() == null ? "" : videoInfo.getContent().getUrl())
                        .setVideoTitle("")
                        .setCacheWithPlay(!TextUtils.equals("upload", videoInfo.getContent().getProvider()))
                        .setRotateViewAuto(true)
                        .setLockLand(true)
                        .setLooping(true)
                        .setPlayTag(PostPreviewAdapter.class.getSimpleName())
                        .setMapHeadData(null)
                        .setShowFullAnimation(true)
                        .setNeedLockFull(true)
                        .setPlayPosition(position)
                        .setShowDragProgressTextOnSeekBar(true)
                        .setOnProxyCacheInfoListener((msgWhat, params) -> {
                            if (params != null) {
                                float percent = VideoParamsUtils.getFloatValue(params, VideoParams.PERCENT);
                                if (msgWhat == ProxyMessage.MSG_VIDEO_PROXY_COMPLETED) {
                                    LogUtils.i("onProxyCacheInfoListener", "-----MSG_VIDEO_PROXY_COMPLETED-----percent-----" + percent);
                                }


                                com.jeffmony.videocache.utils.LogUtils.i("onProxyCacheInfoListener", "-----MSG_VIDEO_PROXY_PROGRESS-----percent-----" + percent);
                                if (msgWhat == ProxyMessage.MSG_VIDEO_PROXY_RANGE_COMPLETED) {
                                    IjkPlayerManager playerManager = ((IjkPlayerManager) GSYVideoManager.instance().getCurPlayerManager());
                                    LogUtils.i("onProxyCacheInfoListener", "-----MSG_VIDEO_PROXY_RANGE_COMPLETED-----percent-----" + percent);
                                    float bufferInterval = (playerManager.getProxyCachePercent() - playerManager.getPlayPercent() * 1f) * playerManager.getDuration() / 100;
                                    if (playerManager.getDuration() == 0 || bufferInterval <= BUFFER_MAX_INTERVAL) {
                                        LogUtils.w("PostPreviewAdapter",
                                                "-----MSG_VIDEO_PROXY_RANGE_COMPLETED resumeCacheTask----- duration: " + playerManager.getDuration()
                                                        + " playPercent:" + playerManager.getPlayPercent()
                                                        + " cachePercent:" + playerManager.getProxyCachePercent()
                                                        + " bufferTime:" + bufferInterval);
                                        playerManager.getLocalProxyVideoControl().resumeLocalProxyTask();
                                    } else {
                                        LogUtils.w("PostPreviewAdapter",
                                                "-----MSG_VIDEO_PROXY_RANGE_COMPLETED pauseCacheTask----- duration: " + playerManager.getDuration()
                                                        + " playPercent:" + playerManager.getPlayPercent()
                                                        + " cachePercent:" + playerManager.getProxyCachePercent()
                                                        + " bufferTime:" + bufferInterval);

                                    }
                                }

                            }
                        })
                        .setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> {

                            IjkPlayerManager playerManager = ((IjkPlayerManager) GSYVideoManager.instance().getCurPlayerManager());
                            playerManager.setPlayPercent(progress);

                            float bufferInterval = (playerManager.getProxyCachePercent() - playerManager.getPlayPercent() * 1f) * playerManager.getDuration() / 100;
                            LogUtils.w("PostPreviewAdapter",
                                    "-----VideoProgressListener----- duration: " + playerManager.getDuration()
                                            + " playPercent:" + playerManager.getPlayPercent()
                                            + " cachePercent:" + playerManager.getProxyCachePercent()
                                            + " bufferTime:" + bufferInterval);
                            if (bufferInterval <= BUFFER_MAX_INTERVAL) {
                                LogUtils.w("PostPreviewAdapter",
                                        "-----VideoProgressListener resumeCacheTask----- duration: " + playerManager.getDuration()
                                                + " playPercent:" + playerManager.getPlayPercent()
                                                + " cachePercent:" + playerManager.getProxyCachePercent()
                                                + " bufferTime:" + bufferInterval);
                                playerManager.getLocalProxyVideoControl().resumeLocalProxyTask();
                            }
                        })
                        .setVideoAllCallBack(new GSYSampleCallBack() {
                            @Override
                            public void onPrepared(String url, Object... objects) {
                                super.onPrepared(url, objects);
                                TopicPostPreviewActivity.startPlayTime = System.currentTimeMillis();
                                TopicPostPreviewActivity.postVideoPlayDurationTime = System.currentTimeMillis();
                                TopicPostPreviewActivity.postVideoPlayDurationLastState = 1;
                            }

                            @Override
                            public void onQuitFullscreen(String url, Object... objects) {
                                super.onQuitFullscreen(url, objects);
                                //全屏不静音
                                GSYVideoManager.instance().setNeedMute(true);
                            }

                            @Override
                            public void onEnterFullscreen(String url, Object... objects) {
                                super.onEnterFullscreen(url, objects);
                                GSYVideoManager.instance().setNeedMute(false);
                                binding.gsyVideoPlayer.getCurrentPlayer().getTitleTextView().setText((String) objects[0]);
                            }

                            @Override
                            public void onInfo(int what, int extra) {
                                super.onInfo(what, extra);
                                com.jeffmony.videocache.utils.LogUtils.w("HomeVideoAdapter", "-----onInfo " + what + " " + extra);
                                IjkPlayerManager playerManager = ((IjkPlayerManager) GSYVideoManager.instance().getCurPlayerManager());
                                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START
                                        || what == IjkMediaPlayer.MEDIA_INFO_VIDEO_DECODED_START) {

                                    LogUtils.w("HomeVideoAdapter",
                                            "onInfo -----resumeCacheTask-----currentPosition-----" + playerManager.getPlayPercent() +
                                                    " duration:" + playerManager.getDuration() +
                                                    " cachePercent:" + playerManager.getProxyCachePercent());
                                    playerManager.getLocalProxyVideoControl().resumeLocalProxyTask();
                                } else if (what == MediaPlayer.MEDIA_ERROR_IO) {
                                    if (playerManager != null && playerManager.getLocalProxyVideoControl() != null) {
                                        playerManager.getLocalProxyVideoControl().resumeLocalProxyTask();
                                    }
                                }
                            }
                        }).build(binding.gsyVideoPlayer);
                binding.gsyVideoPlayer.loadCoverImage(videoInfo.getContent() == null ? "" : videoInfo.getContent().getThumb(), videoInfo.getContent().getVideoWidth(), videoInfo.getContent().getVideoHeight(), null);
            } else {
                binding.gsyVideoPlayer.setVisibility(View.GONE);
                binding.mainImage.setVisibility(View.VISIBLE);
                if (videoInfo.getContent() != null) {
                    String imgUrl = videoInfo.getContent().getUrl();
                    if (CommonUtil.urlIsGif(imgUrl)) {
                        GlideUtils.loadGifImage(imgUrl, 0, binding.mainImage);
                    } else {
                        GlideUtils.loadImageNoPlaceholder(imgUrl, binding.mainImage);
                    }
                }
            }
            binding.controller.setControllerVideoData(videoInfo.getContent() == null ? null : videoInfo.getContent(), true);
            binding.controller.showTopBg(false);
            if (fromPage == 0 && tabType == 0) {
                binding.controller.showLikeOrDisLike(videoInfo.getContent().getApplyStatus(), videoInfo.getContent().getAuthorId(), tabType);
            }
            showPostReviewDialog(videoInfo, position);
            setListener(binding, videoInfo, position);
        }
    }

    private void showPostReviewDialog(HomeVideoImageListBean.ListBean videoInfo, int position) {
        switch (TextUtils.isEmpty(from) ? "" : from) {
            case "postComment":
            case "commentLike":
                showCommentDialog(videoInfo, position);
                break;
            default:
                break;
        }
    }

    private void setListener(ItemPostPreviewBinding binding, HomeVideoImageListBean.ListBean videoInfo, int position) {
        if (videoInfo.getContent().getPostType() == 1) {
            binding.likeview.setOnPlayPauseListener((isShowInfo) -> {
                if (videoInfo.getContent().getPostType() == 1) {
                    if (fromPage == 3) {
                        TopicPostPreviewActivity.isPlayed = true;
                    } else {
                        PostPreviewActivity.isPlayed = true;
                    }
                    binding.startPlay.setVisibility(View.GONE);
                    if (binding.gsyVideoPlayer.getCurrentState() == SampleCoverVideo.CURRENT_STATE_PLAYING) {
                        binding.gsyVideoPlayer.onVideoPause();
                    } else if (binding.gsyVideoPlayer.getCurrentState() == SampleCoverVideo.CURRENT_STATE_PAUSE) {
                        binding.gsyVideoPlayer.onVideoResume(false);
                    } else {
                        binding.gsyVideoPlayer.startPlayLogic();
                    }

                }
            });
            binding.gsyVideoPlayer.setVideoBufferingProgressListener(percent -> {
                if (percent > 0) {

                }
            });
            binding.gsyVideoPlayer.setVideoPlayCompleteListener(() -> {
                binding.controller.showShareAnimation(true);
                binding.controller.showHotComment(videoInfo.getHotComment());
            });
            binding.gsyVideoPlayer.setVideoSeekBarIsDragListener(new SampleCoverVideo.VideoSeekBarIsDragListener() {
                @Override
                public void isTrackingTouch(boolean isStart) {
                    binding.controller.showSeekBarListener(isStart);

                }
            });
        } else {
            //进入大图
            binding.likeview.setOnPlayPauseListener((isShow) -> {
                showBigImgDialog(videoInfo.getContent().getUrl(), position);
            });
            if (videoInfo.getHotComment() != null && videoInfo.getHotComment().getIsHot() == 1) {
                new Handler().postDelayed(() -> binding.controller.showHotComment(videoInfo.getHotComment()), 3000);
            }
        }

        binding.likeview.setOnLikeListener(new LikeView.OnLikeListener() {
            @Override
            public void onLikeListener() {
                //当用户已经点赞或者点踩之后，当前的双击或者长按没有效果
//                if (videoInfo.getContent().getLikeType() == 0) {
                showLikeDialog(binding, videoInfo, position, true);
//                }
            }

            @Override
            public void onDisLikeListener() {
                if (videoInfo.getContent().getDisLikeType() == 0) {
                    showDisLikeDialog(binding, videoInfo, position, 0);
                }
            }
        });

        binding.controller.setOnVideoControllerListener(new OnVideoControllerListener() {
            @Override
            public void onHeadClick() {
                //自己或者他人作品页点击可以直接返回
                if ((fromPage == 0 || fromPage == 1) && tabType == 0) {
                    ((Activity) mContext).finish();
                    return;
                }
//                 CommonUtils.showShort("图像");
                if (videoInfo.getContent().getAuthorId() == SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id")) {
                    ((Activity) mContext).finish();
                } else {
                    MyOtherUserInfoActivity.starActivity(mContext, videoInfo.getContent().getAuthorId(), videoInfo.getContent().getPostId());
                }

            }

            @Override
            public void onAddClick() {
                if (viewModel == null || videoInfo.getContent().getFollowStatus() == 1 || videoInfo.getContent().getFollowStatus() == 2) {
                    return;
                }
                if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                    LoginActivity.starActivity();
                    return;
                }

                viewModel.getUserFollowList(videoInfo.getContent().getAuthorId(), 1).observe((LifecycleOwner) mContext, resultBean -> {
                    if (resultBean != null) {
                        if (resultBean.getCode() == 0) {
                            CommonUtils.showShort("关注成功");
                            //点赞之后更新关注
                            videoInfo.getContent().setFollowStatus(1);
                            binding.controller.setControllerVideoData(videoInfo.getContent());
                            RxBus.getDefault().post(RxCodeConstant.MY_UPDATA_AUTHOR_INFO, videoInfo);
                            ConstantUtil.isUpdataMyFragmentHeader = true;
                            RxBus.getDefault().post(RxCodeConstant.USERINFO_UPDATA_AUTHOR_FOLLOW, 1);
                        }
                    }
                });

            }

            @Override
            public void onLikeClick() {
                showLikeDialog(binding, videoInfo, position, false);
            }

            @Override
            public void onFeelLikeClick(ImageView ivFeelLike, int likeType) {
            }

            @Override
            public void onDislikeClick() {
                showDisLikeDialog(binding, videoInfo, position, 1);
            }

            @Override
            public void onCommentClick() {
                showCommentDialog(videoInfo, position);
//                ConstantUtil.showNoOpenDialog(mContext, "抱歉，该功能即将在未来开放");
            }

            @Override
            public void onRewardClick() {
//                showRewardDialog(videoInfo, position);
                ConstantUtil.showNoOpenDialog(mContext, "抱歉，打赏功能，攻城狮正在\n开发中。");
            }

            @Override
            public void onShareClick() {
                showShareDialog(binding, videoInfo, position);
            }

            @Override
            public void onPostRejectClick() {
            }
        });

        binding.rlHomeLikeDialog.setOnClickListener(view -> {
            removeRunnable();
            binding.rlHomeLikeDialog.setVisibility(View.GONE);
        });
        binding.rlHomeDislikeDialog.setOnClickListener(view -> {
            ConstantUtil.hideSoftKeyboardFromActivity((Activity) mContext, binding.dislikeEdit);
            binding.rlHomeDislikeDialog.setVisibility(View.GONE);
        });

    }

    ShareDialog shareDialog;

    private void showShareDialog(ItemPostPreviewBinding binding, HomeVideoImageListBean.ListBean videoInfo, int playPosition) {
        if (shareDialog != null && shareDialog.isVisible()) {
            LogUtils.d("shareDialog", "isShowing");
            return;
        }
        shareDialog = new ShareDialog(tabType == 0 && fromPage == 0, videoInfo.getContent().getApplyStatus(), videoInfo.getContent() != null && videoInfo.getContent().getPostType() != 3);
        shareDialog.show(childFragmentManager, "" + tab);
        shareDialog.setShareDialogListener(new OnShareDialogListener() {
            @Override
            public void onWechatShare() {
                LogUtils.d("shareDialog", "onWechatShare");
                share(videoInfo, SHARE_MEDIA.WEIXIN);
                clickPost(videoInfo, playPosition, "share", 1, tab);
                shareDialog.dismiss();
            }

            @Override
            public void onWechatCircleShare() {
                LogUtils.d("shareDialog", "onWechatCircleShare");
                share(videoInfo, SHARE_MEDIA.WEIXIN_CIRCLE);
                clickPost(videoInfo, playPosition, "share", 2, tab);
                shareDialog.dismiss();
            }

            @Override
            public void onQQShare() {
                LogUtils.d("shareDialog", "onQQShare");
                share(videoInfo, SHARE_MEDIA.QQ);
                clickPost(videoInfo, playPosition, "share", 3, tab);
                shareDialog.dismiss();
            }

            @Override
            public void onQQZoneShare() {
                LogUtils.d("shareDialog", "onQQZoneShare");
                share(videoInfo, SHARE_MEDIA.QZONE);
                clickPost(videoInfo, playPosition, "share", 4, tab);
                shareDialog.dismiss();
            }

            @Override
            public void onShareReport() {
                ReportActivity.startActivity(mContext, videoInfo.getContent().getPostId());
                shareDialog.dismiss();
            }

            @Override
            public void onShareDelete() {
                shareDialog.dismiss();
                showSureDialog(binding, videoInfo, playPosition);
            }

            @Override
            public void onShareDownload() {
                showDownloadDialog(videoInfo, playPosition);
            }
        });
    }

    public void showDownloadDialog(HomeVideoImageListBean.ListBean videoInfo, int playPosition) {
        if (downloadVideoDialog != null && downloadVideoDialog.isVisible()) {
            com.jeffmony.videocache.utils.LogUtils.d("downloadVideoDialog", "isShowing");
            return;
        }
        downloadVideoDialog = new DownloadVideoDialog();

        AdManager.getInstance().init((Activity) mContext);
        downloadVideoDialog.setOnDialogListener(new DownloadVideoDialog.DownloadVideoDialogListener() {
            @Override
            public void onDownloadNoWaterMark(boolean isShowAd) {
                if (isShowAd) {
                    AdManager.AdListener adListener = new AdManager.AdListener() {
                        @Override
                        public void onSuccess() {
                            downloadVideoNoWaterMark(videoInfo);
                        }

                        @Override
                        public void onFail(boolean loaded) {

                        }

                    };
                    AdManager.getInstance().setAdListener(adListener);
                    AdManager.getInstance().showAd(AdManager.AD_TYPE_GM, adListener);
                } else {
                    downloadVideoNoWaterMark(videoInfo);
                }
            }

            @Override
            public void onDownloadWithWaterMark() {
                if (videoInfo == null || videoInfo.getContent().getUrl() == null) {
                    return;
                }
                String downloadPath = isVideo(videoInfo) ? StorageUtil.generateVideoTempPath() : StorageUtil.generateImageTempPath(isGifImage(videoInfo));
                PostDownloadManager.DownloadListener downloadListener = new PostDownloadManager.DownloadListener() {

                    @Override
                    public void onProgress(int progress) {
                        handler.post(() -> {
                            if (downloadVideoResultDialog != null) {
                                downloadVideoResultDialog.updateProgress(DownloadVideoResultDialog.PROGRESS_INIT
                                        + progress * (DownloadVideoResultDialog.PROGRESS_DOWNLOAD_SUCCESS - DownloadVideoResultDialog.PROGRESS_INIT) / 100);
                            }
                        });

                    }

                    @Override
                    public void onFinish(String path) {
                        handler.post(() -> {
                            long start = SystemClock.elapsedRealtime();
                            if (downloadVideoResultDialog != null) {
                                downloadVideoResultDialog.updateProgress(DownloadVideoResultDialog.PROGRESS_DOWNLOAD_SUCCESS);
                            }
                            addWaterMark(videoInfo, path, new VideoEditUtils.EditListener() {
                                @Override
                                public void onSuccess(HomeVideoImageListBean.ListBean videoInfo, String path) {
                                    handler.post(() -> {
                                        if (downloadVideoResultDialog != null) {
                                            downloadVideoResultDialog.updateState(DownloadVideoResultDialog.STATE_SUCCESS);
                                        }
                                        if (BuildConfig.DEBUG) {
                                            CommonUtils.showShort(videoInfo.getDuration() + " " + path + " cost:" + (SystemClock.elapsedRealtime() - start));
                                        }
                                    });

                                }

                                @Override
                                public void onFail(HomeVideoImageListBean.ListBean videoInfo, Throwable e) {
                                    Log.e("HomeVideoAdapter", "addWaterMark", e);
                                    handler.post(() -> {
                                        if (downloadVideoResultDialog != null) {
                                            downloadVideoResultDialog.updateState(DownloadVideoResultDialog.STATE_FAIL);
                                        }
                                    });

                                }

                                @Override
                                public void onProgress(float progress) {
                                    handler.post(() -> {
                                        if (downloadVideoResultDialog != null) {
                                            downloadVideoResultDialog.updateProgress(DownloadVideoResultDialog.PROGRESS_DOWNLOAD_SUCCESS
                                                    + (int) (progress * DownloadVideoResultDialog.PROGRESS_DOWNLOAD_SUCCESS));
                                        }
                                    });
                                }
                            });
                        });

                    }

                    @Override
                    public void onFail(String errorInfo) {
                        Log.d("HomeVideoAdapter", "downloadVideo onFail:" + errorInfo);
                        handler.post(() -> {
                            if (downloadVideoResultDialog != null) {
                                downloadVideoResultDialog.updateState(DownloadVideoResultDialog.STATE_FAIL);
                            }
                        });

                    }
                };
                downloadVideo(videoInfo, downloadPath, true, downloadListener);

            }

        });
        downloadVideoDialog.show(childFragmentManager, "" + tab);
    }


    private void downloadVideoNoWaterMark(HomeVideoImageListBean.ListBean videoInfo) {
        String downloadPath = isVideo(videoInfo) ? StorageUtil.generateVideoSavedPath() : StorageUtil.generateImageSavedPath(isGifImage(videoInfo));
        final PostDownloadManager.DownloadListener downloadListener = new PostDownloadManager.DownloadListener() {
            @Override
            public void onProgress(int progress) {
                handler.post(() -> {
                    if (downloadVideoResultDialog != null) {
                        int init = DownloadVideoResultDialog.PROGRESS_INIT;
                        downloadVideoResultDialog.updateProgress(DownloadVideoResultDialog.PROGRESS_INIT
                                + progress * (100 - init) / 100);
                    }
                });

            }

            @Override
            public void onFinish(String path) {
                StorageUtil.notifyAlbum(mContext, new File(path), isVideo(videoInfo), isGifImage(videoInfo));
                handler.post(() -> {
                    if (downloadVideoResultDialog != null) {
                        downloadVideoResultDialog.updateState(DownloadVideoResultDialog.STATE_SUCCESS);
                    }
                });
            }

            @Override
            public void onFail(String errorInfo) {
                Log.d("HomeVideoAdapter", "downloadVideo onFail:" + errorInfo);
                handler.post(() -> {
                    if (downloadVideoResultDialog != null) {
                        downloadVideoResultDialog.updateState(DownloadVideoResultDialog.STATE_FAIL);
                    }
                });
            }
        };
        downloadVideo(videoInfo, downloadPath, false, downloadListener);
    }

    public void showDownloadResultDialog(HomeVideoImageListBean.ListBean videoInfo, int state,
                                         DownloadVideoResultDialog.DownloadVideoResultDialogListener listener) {
        if (downloadVideoResultDialog != null) {
            downloadVideoResultDialog.dismiss();
            downloadVideoResultDialog = null;
        }
        downloadVideoResultDialog = new DownloadVideoResultDialog((Activity) mContext);
        downloadVideoResultDialog.setListBean(videoInfo);
        downloadVideoResultDialog.updateState(state);
        downloadVideoResultDialog.show();
        downloadVideoResultDialog.setOnDialogListener(listener);
    }


    private void downloadVideo(HomeVideoImageListBean.ListBean videoInfo, String path, boolean addWaterMark, PostDownloadManager.DownloadListener listener) {
        if (videoInfo == null || videoInfo.getContent().getUrl() == null) {
            return;
        }
        int count = DownloadSPUtils.getDownloadCount(mContext);
        if (count >= DownloadSPUtils.MAX_COUNT) {
            CommonUtils.showShort(mContext.getString(R.string.download_count_reach_max));
            return;
        } else {
            DownloadSPUtils.putDownloadCount(mContext, count + 1);
        }
        boolean isVideo = isVideo(videoInfo);
        String url = videoInfo.getContent().getUrl();
        String finalPath = path;
        showDownloadResultDialog(videoInfo, DownloadVideoResultDialog.STATE_PROGRESS, new DownloadVideoResultDialog.DownloadVideoResultDialogListener() {
            @Override
            public void onRetry(HomeVideoImageListBean.ListBean listBean) {
                handler.post(() -> downloadVideo(videoInfo, finalPath, addWaterMark, listener));
            }

            @Override
            public void goToGallery() {
                StorageUtil.goToGallery(mContext);
            }

            @Override
            public void dismiss() {
                PostDownloadManager.getInstance().removeListener(videoInfo);
                if (isVideo) {
                    VideoEditUtils.cancel();
                }
                downloadVideoResultDialog = null;
            }
        });
        if (addWaterMark) {
            if (isVideo && VideoProxyCacheManager.getInstance().isMp4Completed(url)) {
                path = VideoProxyCacheManager.getInstance().getMp4CachedPath(url);
                listener.onFinish(path);
            } else {
                PostDownloadManager.getInstance().download(videoInfo, path, listener);
            }
        } else {
            if (isVideo && VideoProxyCacheManager.getInstance().isMp4Completed(url)) {
                String cachedPath = VideoProxyCacheManager.getInstance().getMp4CachedPath(url);
                ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Void>() {
                    @Override
                    public Void doInBackground() throws Throwable {
                        try {
                            FileUtils.copy(new File(cachedPath), new File(finalPath));
                            if (listener != null) {
                                listener.onFinish(finalPath);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (listener != null) {
                                listener.onFail("download error");
                            }
                        }
                        return null;
                    }

                    @Override
                    public void onSuccess(Void result) {

                    }
                });
            } else {
                PostDownloadManager.getInstance().download(videoInfo, path, listener);
            }
        }

    }

    private void addWaterMark(HomeVideoImageListBean.ListBean videoInfo, String path, EditUtils.EditListener listener) {
        if (isVideo(videoInfo)) {
            VideoEditUtils.addWaterMarkAsync(videoInfo, path, StorageUtil.generateVideoSavedPath(), true, listener);
        } else if (isImage(videoInfo)) {
            ImageEditUtils.addWaterMarkAsync(videoInfo, path, StorageUtil.generateImageSavedPath(isGifImage(videoInfo)), listener);
        }
    }

    public boolean isVideo(HomeVideoImageListBean.ListBean videoInfo) {
        return (videoInfo != null && videoInfo.getContent() != null && videoInfo.getContent().getPostType() == 1);
    }

    public boolean isImage(HomeVideoImageListBean.ListBean videoInfo) {
        return (videoInfo != null && videoInfo.getContent() != null && videoInfo.getContent().getPostType() == 2);
    }

    public boolean isGifImage(HomeVideoImageListBean.ListBean videoInfo) {
        if (isImage(videoInfo) && videoInfo.getContent().getUrl() != null) {
            return CommonUtil.urlIsGifOrWebp(videoInfo.getContent().getUrl());
        } else {
            return false;
        }
    }

    DeletePostDialog deletePostDialog;

    private void showSureDialog(ItemPostPreviewBinding binding, HomeVideoImageListBean.ListBean videoInfo, int playPosition) {
        if (viewModel == null || tabType != 0 || videoInfo.getContent().getAuthorId() != SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id")) {
            return;
        }
        if (deletePostDialog != null && deletePostDialog.isVisible()) {
            LogUtils.d("shareDialog", "isShowing");
            return;
        }

        deletePostDialog = new DeletePostDialog();
        deletePostDialog.show(childFragmentManager, "" + playPosition);
        deletePostDialog.setOnDialogSureListener(() -> {
            viewModel.getDeletePost(videoInfo.getContent().getPostId()).observe((LifecycleOwner) mContext, resultBean -> {
                if (resultBean != null) {
                    if (resultBean.getCode() == 0) {
                        CommonUtils.showShort("删除成功");

                        if (videoInfo.getContent().getPostType() == 1) {
                            binding.gsyVideoPlayer.release();
                        }

                        removeData(playPosition);
                        deletePostDialog.dismiss();
                        RxBus.getDefault().post(RxCodeConstant.MY_UPDATA_LIST_INFO_DELETE, playPosition);
                        RxBus.getDefault().post(RxCodeConstant.MY_HOME_LIST_INFO_DELETE, playPosition);

                    }
                }
            });
        });
    }

    private void share(HomeVideoImageListBean.ListBean videoInfo, SHARE_MEDIA shareMedia) {
        if (!ConstantUtil.isWxQQInstall(mContext, shareMedia, FunnyApplication.mTencent, mContext.getString(R.string.weixin_appid))) {
            return;
        }
        if (fromPage == 0 && videoInfo.getContent().getApplyStatus() != 1 && videoInfo.getContent().getAuthorId() == SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id")) {
            CommonUtils.showShort("作品审核中，暂时不支持分享");
        } else {
            String shareTitle = videoInfo.getContent().getTitle();
            String shareName = videoInfo.getContent().getTopics() + "@" + videoInfo.getContent().getNickname();
            String thumbUrl = videoInfo.getContent().getPostType() == 1 ? videoInfo.getContent().getThumb() : videoInfo.getContent().getUrl();
            ShareLibraryUM.shareWebUrl(mContext, shareListener, R.mipmap.icon, thumbUrl, shareMedia, shareTitle, shareName, videoInfo.getContent().getPostId(), SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"), videoInfo.getContent().getPostType());
        }
    }

    private UMShareListener shareListener = new UMShareListener() {
        MaterialDialog progressDialog;

        @Override
        public void onStart(SHARE_MEDIA platform) {
            progressDialog = MaterialDialogUtil.showCustomProgressDialog(mContext, "分享中", true);
            ThreadUtils.runOnUiThreadDelayed(() -> {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }, 3000);
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            //成功
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            //失败
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            //取消
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    };

    PostReviewDialog postReviewDialog;

    private void showCommentDialog(HomeVideoImageListBean.ListBean videoInfo, int postPosition) {
        if (postReviewDialog != null && postReviewDialog.isVisible()) {
            return;
        }
//        if (tab == 1) {
//            HomeVideoFragment.isShowCommentDialog = true;
//            HomeVideoFragment.startCommentDialogTime = System.currentTimeMillis();
//        } else {
//            HomeImageTextFragment.isShowCommentDialog = true;
//            HomeImageTextFragment.startCommentDialogTime = System.currentTimeMillis();
//        }
        postReviewDialog = new PostReviewDialog(viewModel, videoInfo.getContent().getPostType(), videoInfo, 2);
        postReviewDialog.show(childFragmentManager, "" + videoInfo.getContent().getPostType());
    }

    private void showLikeDialog(ItemPostPreviewBinding binding, HomeVideoImageListBean.ListBean videoInfo, int position, boolean isDoubleClick) {
        try {
            videoInfo.getContent().setDisLikeType(0);
            //当双击时是点赞的，当已有赞，双击时继续点赞
            if (videoInfo.getContent().getLikeType() > 0 && !isDoubleClick) {
                setHomeVideoLike(binding, 0, videoInfo, position);
                videoInfo.getContent().setLikeType(0);
                videoInfo.getContent().setLikeCount(videoInfo.getContent().getLikeCount() - 1);
                binding.controller.setControllerVideoData(videoInfo.getContent());
                return;
            }
            binding.controller.showShareAnimation(true);
            binding.rlHomeLikeDialog.setVisibility(View.VISIBLE);
            runnable = () -> binding.rlHomeLikeDialog.setVisibility(View.GONE);
            handler.postDelayed(runnable, dialogCancelTime);
            //当已有赞，不需要继续请求接口
            if (videoInfo.getContent().getLikeType() == 0) {
                videoInfo.getContent().setLikeType(1);
                videoInfo.getContent().setLikeCount(videoInfo.getContent().getLikeCount() + 1);
                setHomeVideoLike(binding, 1, videoInfo, position);
                binding.controller.setControllerVideoData(videoInfo.getContent());
            }
            //0代表以前的小熊猫，1是新的宇航员
            if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("home_like_animation_type") == 0) {
                GlideUtils.loadGifImageFromResource(R.drawable.home_like_gif_look_after, binding.likeImg1);
                GlideUtils.loadGifImageFromResource(R.drawable.home_like_gif_deep_heart, binding.likeImg2);
                GlideUtils.loadGifImageFromResource(R.drawable.home_like_gif_thanks_cry, binding.likeImg3);
                GlideUtils.loadGifImageFromResource(R.drawable.home_like_gif_very_like, binding.likeImg4);
                GlideUtils.loadGifImageFromResource(R.drawable.home_like_gif_laugh_up, binding.likeImg5);
            } else {
                GlideUtils.loadGifImageFromResource(R.drawable.home_like_gif_look_after_new1, binding.likeImg1);
                GlideUtils.loadGifImageFromResource(R.drawable.home_like_gif_deep_heart_new1, binding.likeImg2);
                GlideUtils.loadGifImageFromResource(R.drawable.home_like_gif_thanks_cry_new1, binding.likeImg3);
                GlideUtils.loadGifImageFromResource(R.drawable.home_like_gif_very_like_new1, binding.likeImg4);
                GlideUtils.loadGifImageFromResource(R.drawable.home_like_gif_laugh_up_new1, binding.likeImg5);
            }
            binding.likeLayout1.setOnClickListener(view -> {
                binding.rlHomeLikeDialog.setVisibility(View.GONE);
                setHomeVideoLike(binding, 2, videoInfo, position);
                removeRunnable();
//             CommonUtils.showShort("以后看");
            });
            binding.likeLayout2.setOnClickListener(view -> {
                binding.rlHomeLikeDialog.setVisibility(View.GONE);
                setHomeVideoLike(binding, 3, videoInfo, position);
                removeRunnable();
//             CommonUtils.showShort("触动内心");
            });
            binding.likeLayout3.setOnClickListener(view -> {
                binding.rlHomeLikeDialog.setVisibility(View.GONE);
                setHomeVideoLike(binding, 4, videoInfo, position);
                removeRunnable();
//             CommonUtils.showShort("感动哭了");
            });
            binding.likeLayout4.setOnClickListener(view -> {
                binding.rlHomeLikeDialog.setVisibility(View.GONE);
                setHomeVideoLike(binding, 5, videoInfo, position);
                removeRunnable();
//             CommonUtils.showShort("特别爱看");
            });
            binding.likeLayout5.setOnClickListener(view -> {
                binding.rlHomeLikeDialog.setVisibility(View.GONE);
                setHomeVideoLike(binding, 6, videoInfo, position);
                removeRunnable();
//             CommonUtils.showShort("笑出声");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHomeVideoLike(ItemPostPreviewBinding binding, int likeType, HomeVideoImageListBean.ListBean videoInfo, int playPosition) {
        if (viewModel == null) {
            return;
        }
        viewModel.setHomeVideoLike(likeType, videoInfo.getContent().getPostId()).observe((LifecycleOwner) mContext, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    HomeVideoLikeBean bean = resultBean.getData();
                    //点赞之后更新id和点赞数
                    videoInfo.getContent().setLikeType(bean.getLikeType());
                    videoInfo.getContent().setDisLikeType(0);
                }
            }
        });
        binding.controller.showBeautifulBig(1, likeType);
        clickPost(videoInfo, playPosition, "like", likeType, videoInfo.getContent().getPostType());
        ConstantUtil.isUpdataMyFragmentList = true;
    }

    private void showDisLikeDialog(ItemPostPreviewBinding homeBinding, HomeVideoImageListBean.ListBean videoInfo, int position, int fromLocal) {
        try {
            if (videoInfo.getContent().getDisLikeType() > 0) {
                videoInfo.getContent().setLikeType(0);
                setHomeVideoDisLike(homeBinding, 0, videoInfo, position);
                videoInfo.getContent().setDisLikeType(0);
                homeBinding.controller.setControllerVideoData(videoInfo.getContent());
                return;
            }

            MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(mContext, R.layout.dialog_home_dislike);
            DialogHomeDislikeBinding binding = DataBindingUtil.bind(dialog.getCustomView());

            if (videoInfo.getContent().getLikeType() != 0) {
                //只有自己点赞过才需要点赞数减一
                videoInfo.getContent().setLikeType(0);
                videoInfo.getContent().setLikeCount(videoInfo.getContent().getLikeCount() - 1);
            }
            if (fromLocal == 1) {
                videoInfo.getContent().setDisLikeType(1);
                setHomeVideoDisLike(homeBinding, 1, videoInfo, position);
                homeBinding.controller.setControllerVideoData(videoInfo.getContent());
            }
            if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("home_like_animation_type") == 0) {
//                GlideUtils.loadGifImageFromResource(R.drawable.home_dislike_gif_forbid_topics, binding.dislikeImg1);
//                GlideUtils.loadGifImageFromResource(R.drawable.home_dislike_gif_forbid_author, binding.dislikeImg2);
                GlideUtils.loadGifImageFromResource(R.drawable.home_dislike_gif_hate_look, binding.dislikeImg3);
            } else {
//                GlideUtils.loadImageFromResource(R.drawable.home_dislike_gif_forbid_topics_new1, binding.dislikeImg1);
//                GlideUtils.loadGifImageFromResource(R.drawable.home_dislike_gif_forbid_author_new1, binding.dislikeImg2);
                GlideUtils.loadGifImageFromResource(R.drawable.home_dislike_gif_hate_look_new1, binding.dislikeImg3);
            }
//            binding.dislikeLayout1.setOnClickListener(view -> {
//            dialog.dismiss();
//                setHomeVideoDisLike(binding, 4, videoInfo, position);
//                ConstantUtil.hideSoftKeyboardFromActivity((Activity) mContext, binding.dislikeEdit);
//                if (videoInfo.getContent().getTopicIds().equals("") || videoInfo.getContent().getTopicIds().equals("0")) {
//                    CommonUtils.showShort("没有话题哦");
//                } else {
//                    ForbidTopicsActivity.startActivity(mContext, videoInfo.getContent().getPostId(), videoInfo.getContent().getTopicIds(), videoInfo.getContent().getTopics());
//                }
////             CommonUtils.showShort("屏蔽这个话题");
//            });

//            binding.dislikeLayout2.setOnClickListener(view -> {
//            dialog.dismiss();
//                setHomeVideoDisLike(binding, 3, videoInfo, position);
//                ConstantUtil.hideSoftKeyboardFromActivity((Activity) mContext, binding.dislikeEdit);
//                CommonUtils.showShort("以后不再推荐类似帖子给你了");
//            });
            binding.dislikeLayout3.setOnClickListener(view -> {
                dialog.dismiss();
                setHomeVideoDisLike(homeBinding, 2, videoInfo, position);
                ConstantUtil.hideSoftKeyboardFromActivity((Activity) mContext, binding.dislikeEdit);
                RxBus.getDefault().post(RxCodeConstant.MY_VIDEO_UPDATE_DISLIKE, position);
//             CommonUtils.showShort("不爱看");
            });
            binding.dislikeFeedback.setOnClickListener(view -> {

                ClipboardManager cmb = (ClipboardManager) FunnyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText("675082738");
                CommonUtils.showShort("已复制QQ群号");

                ConstantUtil.hideSoftKeyboardFromActivity((Activity) mContext, binding.dislikeEdit);
//             CommonUtils.showShort("反馈");
            });
            binding.dislikeEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (binding.dislikeEdit.getText().toString().length() > 0) {
                        binding.dislikeSend.setVisibility(View.VISIBLE);
                        binding.dislikeSend.setEnabled(true);
                    } else {
                        binding.dislikeSend.setVisibility(View.GONE);
                        binding.dislikeSend.setEnabled(false);
                    }
                }
            });
            binding.dislikeSend.setOnClickListener(view -> {
                String message = binding.dislikeEdit.getText().toString().trim();
                if (!TextUtils.isEmpty(message)) {
                    dialog.dismiss();
                    viewModel.sendReportContent(videoInfo.getContent().getPostId(), "", message, 0, "").observe((LifecycleOwner) mContext, (Observer<BaseResultBean<Object>>) resultBean -> {
                        if (resultBean != null) {
                            ConstantUtil.hideSoftKeyboardFromActivity((Activity) mContext, binding.dislikeEdit);
                            if (resultBean.getCode() == 0) {
                                CommonUtils.showShort("已发送");
                            }
                        }
                    });
                }
            });


            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
            params.gravity = Gravity.CENTER;
            dialog.getCustomView().setLayoutParams(params);
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHomeVideoDisLike(ItemPostPreviewBinding binding, int likeType, HomeVideoImageListBean.ListBean videoInfo, int playPosition) {
        if (viewModel == null) {
            return;
        }
        viewModel.setHomeVideoDisLike(likeType, videoInfo.getContent().getPostId()).observe((LifecycleOwner) mContext, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    HomeVideoDisLikeBean bean = resultBean.getData();
                    //点赞之后更新id和点赞数
                    videoInfo.getContent().setDisLikeType(bean.getDisLikeType());
                    videoInfo.getContent().setLikeType(0);
                }
            }
        });
        binding.controller.showBeautifulBig(2, likeType);
        clickPost(videoInfo, playPosition, "dislike", likeType, videoInfo.getContent().getPostType());
        ConstantUtil.isUpdataMyFragmentList = true;
    }

    BigImgDialog bigImgDialog;

    private void showBigImgDialog(String curUrl, int playPosition) {
        if (bigImgDialog != null && bigImgDialog.isVisible()) {
            return;
        }
        bigImgDialog = new BigImgDialog(curUrl);
        if ((fromPage == 3 ? (TopicPostPreviewActivity) mContext : (PostPreviewActivity) mContext).isStateEnable()) {
            bigImgDialog.show(childFragmentManager, "" + tab);
        }
    }

    private void showRewardDialog(HomeVideoImageListBean.ListBean videoInfo, int position) {

        MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(mContext, R.layout.dialog_home_reward);
        DialogHomeRewardBinding dialogHomeDislikeBinding = DataBindingUtil.bind(dialog.getCustomView());

        dialogHomeDislikeBinding.ivClose.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
        params.gravity = Gravity.CENTER;
        dialog.getCustomView().setLayoutParams(params);
        dialog.show();
        try {
            new Handler().postDelayed(() -> {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }, 7000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param typeName "like" "dislike" "share"
     * @param tab      1 2
     *                 暂时都return
     */

    private void clickPost(HomeVideoImageListBean.ListBean videoInfo, int playPosition, String typeName, int type, int tab) {
        //  目前非首页只需要分享
//        if (!typeName.equals("share")) {
//            return;
//        }
        //  个人审核未通过时不用埋点
        if (fromPage == 0 && videoInfo.getContent().getApplyStatus() != 1 && videoInfo.getContent().getAuthorId() == SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id")) {
            return;
        }
        //  个人和作者页才会埋点
//        if (fromPage != 0 && fromPage != 1) {
//            return;
//        }

        String post_type = videoInfo.getContent().getPostType() == 1 ? "video" : "img";
        int position = 0;
        int page = 0;

        long endTime = System.currentTimeMillis();
        //点击统计
        JsonObject postJsonObject = new JsonObject();
        postJsonObject.addProperty("post_id", videoInfo.getContent().getPostId());
        postJsonObject.addProperty("post_type", post_type);
        postJsonObject.addProperty("tab", 0);
        postJsonObject.addProperty(typeName + "_type", ConstantUtil.changeLikeLogString(typeName, type));
        postJsonObject.addProperty("page", page);
        postJsonObject.addProperty("position", position);
        postJsonObject.addProperty("direction", fromPage == 3 ? TopicPostPreviewActivity.direction : PostPreviewActivity.direction);//用户上下滑的方向。up//上滑、down//下拉、homebutton//按钮、appuse//首次进入
        postJsonObject.addProperty("net", CheckNetwork.isWifiOr4G(mContext));
        postJsonObject.addProperty("create_time", endTime);
        postJsonObject.addProperty("topic_ids", videoInfo.getContent().getTopicIds());
        postJsonObject.addProperty("op_topic_ids", videoInfo.getContent().getOpTopicIds() == null ? "" : videoInfo.getContent().getOpTopicIds());
        postJsonObject.addProperty("tagId", videoInfo.getContent().getTagId());
        int animation_type = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("home_like_animation_type");
        postJsonObject.addProperty("animation_type", animation_type == 0 ? "panda" : "astronaut");// "panda" : "astronaut"
//        String sss = "{\"topic_ids\":\"10007\",\"device_id\":\"2ecacdbfd60b83124909f1becd9b430ff\",\"create_time\":1627639552052,\"video_total_time\":106467,\"current_position\":41818,\"end_time\":1627639552052,\"quit_app\":0,\"op_topic_ids\":\"10007\",\"duration\":469491,\"start_time\":1627639082561,\"detail_dur\":0,\"post_id\":497490457088,\"tab\":1,\"user_id\":107316316672,\"play_dur\":467686,\"post_type\":\"video\",\"page\":0,\"position\":1,\"net\":\"wifi\",\"direction\":\"homebutton\"}";
        RequestParamUtil.addStartLogHeadParam(postJsonObject, typeName, "post", path, provider);

        String jsonData = postJsonObject.toString();
        //上报本次播放的视频时长
        LogUtils.d("gsyPlay--showPoint-", "-json：" + jsonData);
        viewModel.setAppUnifyLog(mContext, jsonData.toString(), false).observe((LifecycleOwner) mContext, resultBean -> {
        });
    }

    private void removeRunnable() {
        try {
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}