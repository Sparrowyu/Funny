package com.sortinghat.funny.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
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
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bytedance.msdk.api.v2.GMAdConstant;
import com.bytedance.msdk.api.v2.GMDislikeCallback;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeAd;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMNativeExpressAdListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMVideoListener;
import com.bytedance.msdk.api.v2.ad.nativeAd.GMViewBinder;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.google.gson.JsonObject;
import com.kwad.sdk.api.KsDrawAd;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.StorageUtil;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.common.utils.statusbar.StatusBarUtil;
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
import com.sortinghat.funny.databinding.ItemHomeAdBinding;
import com.sortinghat.funny.databinding.ItemHomeGmoreAdBinding;
import com.sortinghat.funny.databinding.ItemHomeOtherCompleteInfoBinding;
import com.sortinghat.funny.databinding.ItemHomeVideoBinding;
import com.sortinghat.funny.thirdparty.video.OnVideoControllerListener;
import com.sortinghat.funny.ui.BottomSheetDialog.BigImgDialog;
import com.sortinghat.funny.ui.BottomSheetDialog.DownloadVideoDialog;
import com.sortinghat.funny.ui.BottomSheetDialog.DownloadVideoResultDialog;
import com.sortinghat.funny.ui.MainActivity;
import com.sortinghat.funny.ui.home.HomeImageTextFragment;
import com.sortinghat.funny.ui.home.HomeVideoFragment;
import com.sortinghat.funny.ui.home.OnShareDialogListener;
import com.sortinghat.funny.ui.home.PostReviewDialog;
import com.sortinghat.funny.ui.home.ReportActivity;
import com.sortinghat.funny.ui.home.ShareDialog;
import com.sortinghat.funny.ui.my.LoginActivity;
import com.sortinghat.funny.ui.my.MyOtherUserInfoActivity;
import com.sortinghat.funny.util.AdManager;
import com.sortinghat.funny.util.CommonUtil;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.ImageEditUtils;
import com.sortinghat.funny.util.ListenerUtils;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.utils.ShareLibraryUM;
import com.sortinghat.funny.view.LikeView;
import com.sortinghat.funny.viewmodel.HomeViewModel;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.jingbin.library.ByRecyclerView;
import me.jingbin.library.adapter.BaseByRecyclerViewAdapter;
import me.jingbin.library.adapter.BaseByViewHolder;

public class HomeVideoAdapter extends BaseByRecyclerViewAdapter<HomeVideoImageListBean.ListBean, BaseByViewHolder<HomeVideoImageListBean.ListBean>> {

    public static final double BUFFER_MAX_INTERVAL = 15000f;
    private Context mContext;
    private HomeViewModel viewModel;
    private FragmentManager childFragmentManager;
    private int tab; //1 video, 2 img
    private Handler handler;
    private Runnable runnable;
    private int dialogCancelTime = 7000;
    public static final int TYPE_POST_VIEW = 1001;
    private static final int TYPE_AD_VIEW = 1002;
    private static final int TYPE_SELECT_VIEW = 1003;
    private static final int TYPE_GMORE_AD_VIEW = 1004;//穿山甲聚合广告

    private DownloadVideoDialog downloadVideoDialog;
    private DownloadVideoResultDialog downloadVideoResultDialog;

    private boolean isLaterSeeLike, isTouchHeartLike, isTouchCryLike, isLoveSeeLike, isLaughOutLike;

    public HomeVideoAdapter(Context mContext, FragmentManager childFragmentManager, int tab, Handler handler) {
        this.mContext = mContext;
        this.childFragmentManager = childFragmentManager;
        this.tab = tab;
        this.handler = handler;
    }

    public void setViewModel(HomeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = TYPE_POST_VIEW;
        switch (getItemData(position).getContent().getPostType()) {
            case 1:
            case 2:
                viewType = TYPE_POST_VIEW;
                break;
            case 3:
                viewType = TYPE_AD_VIEW;
                break;
            case 4:
                viewType = TYPE_SELECT_VIEW;
                break;
        }
        if (viewType == TYPE_AD_VIEW && !TextUtils.isEmpty(getItemData(position).getContent().getAdType()) && getItemData(position).getContent().getAdType().equals("gMore")) {
            viewType = TYPE_GMORE_AD_VIEW;
        }
        return viewType;
    }

    @NonNull
    @Override
    public BaseByViewHolder<HomeVideoImageListBean.ListBean> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (tab == 1) {
        }
        if (TYPE_SELECT_VIEW == viewType) {
            return new HomeVideoAdapter.OtherViewHolder(parent, R.layout.item_home_other_complete_info);
        }
        if (TYPE_AD_VIEW == viewType) {
            return new HomeVideoAdapter.AdViewHolder(parent, R.layout.item_home_ad);
        }
        if (TYPE_GMORE_AD_VIEW == viewType) {
            return new HomeVideoAdapter.GMoreAdViewHolder(parent, R.layout.item_home_gmore_ad);
        } else {
            return new HomeVideoAdapter.HomeViewHolder(parent, R.layout.item_home_video);
        }
    }

    private class OtherViewHolder extends BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeOtherCompleteInfoBinding> {
        OtherViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        protected void onBindingView(BaseBindingHolder holder, HomeVideoImageListBean.ListBean videoInfo, int position) {
            if (null != videoInfo && null != videoInfo.getContent()) {
                if (videoInfo.getContent().getPostType() == 4) {

                    final String[] ageShow = {""};
                    String[] valueArrReturn = {"07-12", "13-17", "18-24", "25-30", "31-40", "41以上"};
                    binding.itemBt.setOnClickListener(view -> {

                        ConstantUtil.createUmEvent("home_user_Info_window_done_click");//首页-采集个人信息页面-完成按钮点击量
                        int checkSex = binding.radioGroupGender.getCheckedRadioButtonId();
                        String sex = "";//1,男 2，女
                        if (checkSex == -1) {
                            //没有选
                            sex = "";
                        } else if (checkSex == binding.radioButtonMale.getId()) {
                            sex = "1";
                            //选择男
                        }
                        if (checkSex == binding.radioButtonFemale.getId()) {
                            sex = "2";
                            //选择女
                        }

                        int checkAge = binding.radioGroupAge.getCheckedRadioButtonId();
                        String age = "";//年龄

                        if (checkAge == -1) {
                            //没有选
                            age = "";
                        }
                        if (checkAge == binding.radioButtonAge1.getId()) {
                            age = valueArrReturn[0];
                            //选择
                        }
                        if (checkAge == binding.radioButtonAge2.getId()) {
                            age = valueArrReturn[1];
                        }

                        if (checkAge == binding.radioButtonAge3.getId()) {
                            age = valueArrReturn[2];
                        }

                        if (checkAge == binding.radioButtonAge4.getId()) {
                            age = valueArrReturn[3];
                        }

                        if (checkAge == binding.radioButtonAge5.getId()) {
                            age = valueArrReturn[4];
                        }

                        if (checkAge == binding.radioButtonAge6.getId()) {
                            age = valueArrReturn[5];
                        }

                        if ((TextUtils.isEmpty(age) && TextUtils.isEmpty(sex))) {
                            CommonUtils.showShort("请选择");
                            return;
                        }
                        goCollectLog(1, "click_done");
                        viewModel.getCollectionInformation(age, sex).observeForever(resultBean -> {
                            if (resultBean != null) {
                                if (resultBean.getCode() == 0) {
                                    CommonUtils.showShort("完成");
                                    binding.itemBt.setText("已完成");
                                    binding.itemBt.setEnabled(false);
                                    binding.itemBt.setTextColor(CommonUtils.getColor(R.color.color_333333));
                                    binding.itemBt.setBackgroundResource(R.drawable.click_no_gray_bt_bg);
//                                    removeData(position);
//                                    RxBus.getDefault().post(RxCodeConstant.MY_UPDATA_LIST_INFO_DELETE, position);
                                }
                            }
                        });
                    });

                }
            }

        }
    }

    //type 收集话题 ：userLikeTopic 收集年龄性别信息：information
    private void goCollectLog(int collection_type, String action_type) {
        if (viewModel == null) {
            return;
        }
        long createTime = System.currentTimeMillis();
        JsonObject startJsonObject = new JsonObject();
        String provider = collection_type == 1 ? "information" : "userLikeTopic";
        startJsonObject.addProperty("collection_type", collection_type == 1 ? "information" : "userLikeTopic");//行为类型userLikeTopic 收集年龄性别信息：information
        startJsonObject.addProperty("action_type", action_type);//show,click_done,click_cancel
        startJsonObject.addProperty("post_type", "4");//帖子类型，video，img
        startJsonObject.addProperty("create_time", createTime);//事件时间

        RequestParamUtil.addStartLogHeadParam(startJsonObject, "collection", "app", "index", provider);

        Log.d("action-log--app-success", "\n" + startJsonObject.toString());
        viewModel.setAppUnifyLog(mContext, startJsonObject.toString(), false).observe((LifecycleOwner) mContext, resultBean -> {
        });

    }


    private class GMoreAdViewHolder extends BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeGmoreAdBinding> {
        GMoreAdViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);

        }

        @Override
        protected void onBindingView(BaseBindingHolder adViewHolder, HomeVideoImageListBean.ListBean videoInfo, int position) {

            if (videoInfo.getContent().getPostType() == 3) {
                if (null != videoInfo.getContent().getmGMNativeAd()) {
                    //TTViewBinder 是必须类,需要开发者在确定好View之后把Id设置给TTViewBinder类，并在注册事件时传递给SDK
                    GMViewBinder viewBinder = new GMViewBinder.Builder(R.layout.item_home_gmore_ad).
                            titleId(R.id.tv_listitem_ad_title).
                            sourceId(R.id.tv_listitem_ad_source).
                            descriptionTextId(R.id.tv_listitem_ad_desc).
                            mediaViewIdId(R.id.iv_listitem_video).
                            callToActionId(R.id.btn_listitem_creative).
                            logoLayoutId(R.id.tt_ad_logo).//logoView 建议传入GroupView类型
                            iconImageId(R.id.iv_listitem_icon).build();


                    GMNativeAd ad = videoInfo.getContent().getmGMNativeAd();
                    String TAG = "csjAdload-gmore";

                    Log.d(TAG, "gm--adapter-11l");

                    try {
                        //判断是否存在dislike按钮
                        if (ad.hasDislike()) {
                            ad.setDislikeCallback((Activity) mContext, new GMDislikeCallback() {
                                @Override
                                public void onSelected(int position, String value) {

                                }

                                @Override
                                public void onCancel() {
                                }

                                /**
                                 * 拒绝再次提交
                                 */
                                @Override
                                public void onRefuse() {

                                }

                                @Override
                                public void onShow() {

                                }
                            });
                        }

                        if (ad != null && ad.isExpressAd()) {

                            binding.customExpressContainer.setVisibility(View.VISIBLE);
                            binding.customContainer.setVisibility(View.GONE);
                            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) binding.customExpressContainer.getLayoutParams();
                            lp.topMargin = StatusBarUtil.getStatusBarHeight(mContext);
                            if (tab == 2) {
                                lp.topMargin = StatusBarUtil.getStatusBarHeight(mContext) + SizeUtils.dp2px(50);
                            }

                            //设置点击展示回调监听
                            ad.setNativeAdListener(new GMNativeExpressAdListener() {
                                @Override
                                public void onAdClick() {
                                }

                                @Override
                                public void onAdShow() {

                                }

                                @Override
                                public void onRenderFail(View view, String msg, int code) {
                                    Log.d("csjAdload", "gm--adapter-onRenderFail");
                                }

                                // ** 注意点 ** 不要在广告加载成功回调里进行广告view展示，要在onRenderSucces进行广告view展示，否则会导致广告无法展示。
                                @Override
                                public void onRenderSuccess(float width, float height) {
                                    Log.d("csjAdload", "gm--adapter-1onRenderSuccess");
                                    //回调渲染成功后将模板布局添加的父View中
                                    if (binding.customExpressContainer != null) {
                                        //获取视频播放view,该view SDK内部渲染，在媒体平台可配置视频是否自动播放等设置。
                                        /**
                                         * 如果存在父布局，需要先从父布局中移除
                                         */
                                        final View gmVideo = ad.getExpressView(); // 获取广告view  如果存在父布局，需要先从父布局中移除

                                        if (gmVideo != null) {
                                            /**
                                             * 如果存在父布局，需要先从父布局中移除
                                             */
                                            Log.d("csjAdload", "gm--adapter111");
                                            removeFromParent(gmVideo);
                                            binding.customExpressContainer.removeAllViews();
                                            binding.customExpressContainer.addView(gmVideo);
                                        }
                                    }
                                }
                            });
                            ad.render();
                        } else if (ad != null) {
                            binding.customExpressContainer.setVisibility(View.GONE);
                            binding.customContainer.setVisibility(View.VISIBLE);
//                            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) binding.customContainer.getLayoutParams();
//                            lp.topMargin = StatusBarUtil.getStatusBarHeight(mContext);
//                            if (tab == 2) {
//                                lp.topMargin = StatusBarUtil.getStatusBarHeight(mContext) + SizeUtils.dp2px(50);
//                            }

                            if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_SMALL_IMG) {
                            } else if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_LARGE_IMG) {
                            } else if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_GROUP_IMG) {
                            } else if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO || ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO_VERTICAL) {//视频
                                ad.setVideoListener(new GMVideoListener() {

                                    @Override
                                    public void onVideoStart() {
                                        Log.d(TAG, "onVideoStart");
                                    }

                                    @Override
                                    public void onVideoPause() {
                                        Log.d(TAG, "onVideoPause");
                                    }

                                    @Override
                                    public void onVideoResume() {
                                        Log.d(TAG, "onVideoResume");
                                    }

                                    @Override
                                    public void onVideoCompleted() {
                                        Log.d(TAG, "onVideoCompleted");
                                    }

                                    @Override
                                    public void onVideoError(com.bytedance.msdk.api.AdError adError) {
                                        Log.d(TAG, "onVideoError");
                                    }
                                });
                            } else if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_VERTICAL_IMG) {
                            } else if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO_VERTICAL) {
                            } else {
                            }

                            //可以被点击的view, 也可以把convertView放进来意味item可被点击
                            List<View> clickViewList = new ArrayList<>();
                            clickViewList.add(itemView);
                            clickViewList.add(binding.adTitleBtnLayout.tvAdTitle);
                            clickViewList.add(binding.customContainer);
                            clickViewList.add(binding.adTitleBtnLayout.btnCreative);
                            clickViewList.add(binding.adTitleBtnLayout.tvListitemAdDesc);
                            clickViewList.add(binding.iconSourceLayout.ivListitemIcon);
                            if (ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO || ad.getAdImageMode() == GMAdConstant.IMAGE_MODE_VIDEO_VERTICAL) {//视频
                                clickViewList.add(binding.ivListitemVideo);//这个id不能变，否则显示不出
                            }

                            //触发创意广告的view（点击下载或拨打电话）
                            List<View> creativeViewList = new ArrayList<>();
                            creativeViewList.add(binding.adTitleBtnLayout.btnCreative);
                            //重要! 这个涉及到广告计费，必须正确调用。**** convertView必须是com.bytedance.msdk.api.format.TTNativeAdView ****
                            ad.registerView((Activity) mContext, (ViewGroup) itemView, clickViewList, creativeViewList, viewBinder);

                            binding.adTitleBtnLayout.tvAdTitle.setText(ad.getTitle()); //title为广告的简单信息提示
                            binding.adTitleBtnLayout.tvListitemAdDesc.setText(ad.getDescription()); //description为广告的较长的说明
                            binding.iconSourceLayout.tvListitemAdSource.setText(TextUtils.isEmpty(ad.getSource()) ? "广告来源" : ad.getSource());

                            String icon = ad.getIconUrl();
                            if (icon != null) {
                                Glide.with(mContext).load(icon).into(binding.iconSourceLayout.ivListitemIcon);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("csjAdload", "gm--adapter-Exception");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class AdViewHolder extends BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeAdBinding> {
        AdViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        protected void onBindingView(BaseBindingHolder holder, HomeVideoImageListBean.ListBean videoInfo, int position) {
            if (videoInfo.getContent().getPostType() == 3) {
                if (null != videoInfo.getContent().getAd()) {
                    binding.csjAdContainer.setVisibility(View.VISIBLE);
                    binding.gdtAdContainer.setVisibility(View.GONE);
                    final String ADTAG = "csjAdload-";
                    Log.d("csjAdloadTag", "cdj-adapter");
                    videoInfo.getContent().getAd().setVideoAdListener(new TTNativeExpressAd.ExpressVideoAdListener() {
                        @Override
                        public void onVideoLoad() {

                        }

                        @Override
                        public void onVideoError(int i, int i1) {

                        }

                        @Override
                        public void onVideoAdStartPlay() {

                        }

                        @Override
                        public void onVideoAdPaused() {

                        }

                        @Override
                        public void onVideoAdContinuePlay() {

                        }

                        @Override
                        public void onProgressUpdate(long l, long l1) {

                        }

                        @Override
                        public void onVideoAdComplete() {
                            videoInfo.getContent().setAdVideoIsComplete(1);
                        }

                        @Override
                        public void onClickRetry() {

                        }
                    });
//
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) binding.csjAdContainer.getLayoutParams();
                    lp.topMargin = StatusBarUtil.getStatusBarHeight(mContext);
                    if (tab == 2) {
                        lp.topMargin = StatusBarUtil.getStatusBarHeight(mContext) + SizeUtils.dp2px(50);
                    }
                    View adView  = videoInfo.getContent().getAd().getExpressAdView();
                    binding.csjAdContainer.removeAllViews();
                    if (adView.getParent() != null) {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }
                    binding.csjAdContainer.addView(adView);
                } else if (null != videoInfo.getContent().getGdtAd()) {
                    binding.csjAdContainer.setVisibility(View.GONE);
                    binding.gdtAdContainer.setVisibility(View.VISIBLE);
                    String TAG = "csjAdload";
                    Log.d(TAG, "ongdtVideo");
                    final NativeUnifiedADData gdtAd = (NativeUnifiedADData) videoInfo.getContent().getGdtAd();
                    gdtAd.setVideoMute(false);

                    binding.adInfoView.setAdInfo(gdtAd);
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) binding.gdtAdContainer.getLayoutParams();
                    lp.topMargin = StatusBarUtil.getStatusBarHeight(mContext);
                    if (tab == 2) {
                        lp.topMargin = StatusBarUtil.getStatusBarHeight(mContext) + SizeUtils.dp2px(50);
                    }

                    List<View> clickableViews = new ArrayList<>();
                    List<View> customClickableViews = new ArrayList<>();
                    clickableViews.addAll(binding.adInfoView.getClickableViews());

                    FrameLayout.LayoutParams adLogoParams = new FrameLayout.LayoutParams(SizeUtils.dp2px(46),
                            SizeUtils.dp2px(14));
                    adLogoParams.gravity = Gravity.END | Gravity.BOTTOM;
                    adLogoParams.rightMargin = SizeUtils.dp2px(10);
                    adLogoParams.bottomMargin = SizeUtils.dp2px(10);
                    //作为customClickableViews传入，点击不进入详情页，直接下载或进入落地页，图文、视频广告均生效，
                    gdtAd.bindAdToView(mContext, binding.gdtAdContainer, null,
                            clickableViews);
                    binding.adInfoView.updateAdAction(gdtAd);


                    if (gdtAd.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                        binding.gdtMediaView.setVisibility(View.VISIBLE);
                        binding.adInfoContainer.setVisibility(View.VISIBLE);

                        //如果需要获得点击view的信息使用NativeADEventListenerWithClickInfo代替NativeADEventListener
                        gdtAd.setNativeAdEventListener(new NativeADEventListener() {
                            @Override
                            public void onADExposed() {
                                Log.d(TAG, "onADExposed: " + gdtAd.getTitle());
                            }

                            @Override
                            public void onADClicked() {
                                Log.d(TAG, "onADClicked: " + gdtAd.getTitle());
                            }

                            @Override
                            public void onADError(AdError error) {
                                Log.d(TAG, "onADError error code :" + error.getErrorCode()
                                        + "  error msg: " + error.getErrorMsg());
                            }

                            @Override
                            public void onADStatusChanged() {
                                binding.adInfoView.updateAdAction(gdtAd);
                            }
                        });

//                    VideoOption videoOption = NativeADUnifiedSampleActivity.getVideoOption(getIntent());
                        VideoOption.Builder builder = new VideoOption.Builder();

                        builder.setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS);
                        builder.setNeedCoverImage(true);

                        VideoOption videoOption = builder.build();

                        gdtAd.bindMediaView(binding.gdtMediaView, videoOption, new NativeADMediaListener() {
                            @Override
                            public void onVideoInit() {
                                Log.d(TAG, "onVideoInit: ");
                            }

                            @Override
                            public void onVideoLoading() {
                                Log.d(TAG, "onVideoLoading: ");
                            }

                            @Override
                            public void onVideoReady() {
                                Log.d(TAG, "onVideoReady ");
                            }

                            @Override
                            public void onVideoLoaded(int videoDuration) {
                                Log.d(TAG, "onVideoLoaded: ");
                            }

                            @Override
                            public void onVideoStart() {
                                Log.d(TAG, "onVideoStart ");
                                binding.adInfoContainer.setVisibility(View.VISIBLE);
                                binding.adInfoView.playAnim();
                            }

                            @Override
                            public void onVideoPause() {
                                Log.d(TAG, "onVideoPause: ");
                            }

                            @Override
                            public void onVideoResume() {
                                Log.d(TAG, "onVideoResume: ");
                            }

                            @Override
                            public void onVideoCompleted() {
                                Log.d(TAG, "onVideoCompleted: ");
                                binding.adInfoContainer.setVisibility(View.GONE);
                                binding.adInfoView.resetUI();
                            }

                            @Override
                            public void onVideoError(AdError error) {
                                Log.d(TAG, "onVideoError: ");
                            }

                            @Override
                            public void onVideoStop() {
                                Log.d(TAG, "onVideoStop");
                            }

                            @Override
                            public void onVideoClicked() {
                                Log.d(TAG, "onVideoClicked");
                            }
                        });
                    } else {
                        Log.d(TAG, "onNoVideo");
                    }


//                binding.adVideoLayout.removeAllViews();
//                if (adView.getParent() != null) {
//                    ((ViewGroup) adView.getParent()).removeView(adView);
//                }
//                binding.adVideoLayout.addView(adView);
//                adView.render();
                } else if (null != videoInfo.getContent().getKsAd()) {
                    binding.csjAdContainer.setVisibility(View.VISIBLE);
                    binding.gdtAdContainer.setVisibility(View.GONE);
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) binding.csjAdContainer.getLayoutParams();
                    lp.topMargin = StatusBarUtil.getStatusBarHeight(mContext);
                    if (tab == 2) {
                        lp.topMargin = StatusBarUtil.getStatusBarHeight(mContext) + SizeUtils.dp2px(50);
                    }
                    KsDrawAd ksDrawAd = videoInfo.getContent().getKsAd();
                    Log.d("csjAdload", "ks--adapter111");
                    View drawVideoView = ksDrawAd.getDrawView(mContext);
                    if (drawVideoView != null && drawVideoView.getParent() == null) {
                        binding.csjAdContainer.removeAllViews();
                        binding.csjAdContainer.addView(drawVideoView);
                    }
                }
            }
        }
    }

    public static void removeFromParent(View view) {
        if (view != null) {
            ViewParent vp = view.getParent();
            if (vp instanceof ViewGroup) {
                ((ViewGroup) vp).removeView(view);
            }
        }
    }

    private class HomeViewHolder extends BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> {
        HomeViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        protected void onBindingView(BaseBindingHolder holder, HomeVideoImageListBean.ListBean videoInfo, int position) {
            if (null != videoInfo && null != videoInfo.getContent()) {
                if (position == 0) {
                    handler.sendEmptyMessage(100);
                }
                hideGuideLayer(binding);
                if (videoInfo.getContent().getPostType() != 3) {
                    binding.gifHomeLoading.setVisibility(View.VISIBLE);
                    GlideUtils.loadGifImageFromResource(R.drawable.home_loading, binding.gifHomeLoading);
                }
                if (tab == 1) {
                } else {
                    if (videoInfo.getContent().getPostType() != 3) {
                        binding.mainImage.setVisibility(View.VISIBLE);
                        if (videoInfo.getContent() != null) {
                            String imgUrl = videoInfo.getContent().getUrl();
//                    String gifTest = "https://server.sortinghat.cn/post/f31b23e3f48d4ec0a36d00fb5a8e7345.gif";
                            if (CommonUtil.urlIsGif(imgUrl)) {
                                GlideUtils.loadGifImage(imgUrl, 0, binding.mainImage, new GifImageRequestListener(binding.gifHomeLoading));
                            } else {
                                GlideUtils.loadImageNoPlaceholder(imgUrl, binding.mainImage, new ImageRequestListener(binding.gifHomeLoading));
                            }
                        }
                    }
                }
                if (videoInfo.getContent().getPostType() != 3) {
                    binding.controller.setControllerVideoData(videoInfo.getContent() == null ? null : videoInfo.getContent(), true);
                    setListener(binding, videoInfo, position);
                }
            }
        }
    }

    private void hideGuideLayer(ItemHomeVideoBinding binding) {
        GifDrawable gifDrawable = (GifDrawable) binding.ivGuideAnimateImage.getDrawable();
        if (gifDrawable != null && gifDrawable.isRunning()) {
            gifDrawable.stop();
        }
        binding.clGuideAnimateImageAndText.setVisibility(View.GONE);
    }

    public class ImageRequestListener implements RequestListener<Drawable> {

        private ImageView gifHomeLoading;

        public ImageRequestListener(ImageView gifHomeLoading) {
            this.gifHomeLoading = gifHomeLoading;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//            LogUtils.e(GlideUtils.class.getSimpleName() + ":" + model + (e == null ? "" : e.toString()));
            hideLoadingAnimation();
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            hideLoadingAnimation();
            return false;
        }

        private void hideLoadingAnimation() {
            GifDrawable gifDrawable = (GifDrawable) gifHomeLoading.getDrawable();
            if (gifDrawable != null && gifDrawable.isRunning()) {
                gifDrawable.stop();
            }
            gifHomeLoading.setVisibility(View.GONE);
        }
    }

    private class GifImageRequestListener implements RequestListener<GifDrawable> {

        private ImageView gifHomeLoading;

        public GifImageRequestListener(ImageView gifHomeLoading) {
            this.gifHomeLoading = gifHomeLoading;
        }

        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
            hideLoadingAnimation();
            return false;
        }

        @Override
        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
            hideLoadingAnimation();
            return false;
        }

        private void hideLoadingAnimation() {
            GifDrawable gifDrawable = (GifDrawable) gifHomeLoading.getDrawable();
            if (gifDrawable != null && gifDrawable.isRunning()) {
                gifDrawable.stop();
            }
            gifHomeLoading.setVisibility(View.GONE);
        }
    }

    private void setListener(ItemHomeVideoBinding binding, HomeVideoImageListBean.ListBean videoInfo, int position) {
        if (tab == 1) {

        } else {
            //进入大图
            binding.likeview.setOnPlayPauseListener((boolen) -> {
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
                if ("landScape".equals(SPUtils.getInstance(Constant.SP_CONFIG_INFO).getString("post_like_strategy", "panda"))) {
                    if (videoInfo.getContent().getLikeType() == 0) {
                        binding.controller.getControllerViewBinding().ivLike.setImageResource(R.mipmap.home_like_click);
                        binding.controller.getControllerViewBinding().ivLike.setEnabled(false);
                        binding.controller.getControllerViewBinding().ivDislike.setEnabled(false);
                        requestPostLike(binding, videoInfo, position, 1);
                    }
                    loadFeelLikeComponentAnimation(binding.controller.getControllerViewBinding().recyclerView);
                } else {
                    showLikeDialog(binding, videoInfo, position, true);
                }
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
//                CommonUtils.showShort("图像");
                if (videoInfo.getContent().getAuthorId() == SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id")) {
                    if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                        CommonUtils.showShort("请登录");
                        return;
                    } else {
                        //我的
                    }
                } else {
                    MyOtherUserInfoActivity.starActivity(mContext, videoInfo.getContent().getAuthorId(), videoInfo.getContent().getPostId());
                }
            }

            @Override
            public void onAddClick() {
                if (viewModel == null || videoInfo.getContent() == null || videoInfo.getContent().getFollowStatus() == 1 || videoInfo.getContent().getFollowStatus() == 2) {
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
                            if (videoInfo.getContent().getFollowStatus() == 0) {
                                videoInfo.getContent().setFollowStatus(2);
                            } else if (videoInfo.getContent().getFollowStatus() == 3) {
                                videoInfo.getContent().setFollowStatus(1);
                            }
                            binding.controller.setControllerVideoData(videoInfo.getContent());
                            ConstantUtil.isUpdataMyFragmentHeader = true;
                        }
                    }
                });
            }

            @Override
            public void onLikeClick() {
                if ("landScape".equals(SPUtils.getInstance(Constant.SP_CONFIG_INFO).getString("post_like_strategy", "panda"))) {
                    binding.controller.getControllerViewBinding().ivLike.setEnabled(false);
                    binding.controller.getControllerViewBinding().ivDislike.setEnabled(false);
                    if (videoInfo.getContent().getLikeType() == 0) {
                        binding.controller.getControllerViewBinding().ivLike.setImageResource(R.mipmap.home_like_click);
                        requestPostLike(binding, videoInfo, position, 1);
                        loadFeelLikeComponentAnimation(binding.controller.getControllerViewBinding().recyclerView);
                    } else {
                        binding.controller.getControllerViewBinding().ivLike.setImageResource(R.mipmap.home_like);
                        requestPostLike(binding, videoInfo, position, 0);
                    }
                } else {
                    showLikeDialog(binding, videoInfo, position, false);
                }
            }

            @Override
            public void onFeelLikeClick(ImageView ivFeelLike, int likeType) {
                loadFeelLikeAnimation(binding, videoInfo, position, ivFeelLike, likeType);
            }

            @Override
            public void onDislikeClick() {
                showDisLikeDialog(binding, videoInfo, position, 1);
            }

            @Override
            public void onCommentClick() {
                showCommentDialog(videoInfo, position);
            }

            @Override
            public void onRewardClick() {
                ConstantUtil.createUmEvent("home_reword_click");//首页赏的点击
                ConstantUtil.showNoOpenDialog(mContext, "抱歉，打赏功能，攻城狮正在\n开发中。");
//                showRewardDialog(videoInfo, position);
            }

            @Override
            public void onShareClick() {
                if (videoInfo.getContent().getAuthorId() == SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id") && !TextUtils.isEmpty(videoInfo.getContent().getProvider()) && "upload".equals(videoInfo.getContent().getProvider())) {
                    CommonUtils.showShort("作品审核中，暂时不支持分享");
                } else {
                    showShareDialog(videoInfo, position);
                }
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

    private void loadFeelLikeComponentAnimation(ByRecyclerView recyclerView) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(recyclerView, "rotation", 0f, -10f, 0f, 10f, 0f);
        objectAnimator.setRepeatCount(1);
        objectAnimator.setDuration(500);
        objectAnimator.start();
    }

    private void loadFeelLikeAnimation(ItemHomeVideoBinding binding, HomeVideoImageListBean.ListBean videoInfo, int position, ImageView ivFeelLike, int likeType) {
        //获取需要进行动画的ImageView
        final ImageView animImg = new ImageView(mContext);
        animImg.setImageDrawable(ivFeelLike.getDrawable());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        ((FrameLayout) binding.getRoot()).addView(animImg, params);

        final int shellLocation[] = new int[2];
        binding.getRoot().getLocationInWindow(shellLocation);
        int animImgLocation[] = new int[2];
        ivFeelLike.getLocationInWindow(animImgLocation);
        int likeLocation[] = new int[2];
        binding.controller.getControllerViewBinding().ivLike.getLocationInWindow(likeLocation);

        // 起始点：图片起始点-父布局起始点+该商品图片的一半-图片的marginTop || marginLeft 的值
        float startX = animImgLocation[0] - shellLocation[0];
        float startY = animImgLocation[1] - shellLocation[1] - ivFeelLike.getHeight() / 2 - SizeUtils.dp2px(40.0f);

        // 商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5
        float endX = likeLocation[0] - shellLocation[0] + binding.controller.getControllerViewBinding().ivLike.getWidth() / 5;
        float endY = likeLocation[1] - shellLocation[1];

        //控制点，控制贝塞尔曲线
        float ctrlX = (startX + endX) / 2;
        float ctrlY = startY - 1000;

        Path path = new Path();
        path.moveTo(startX, startY);
        // 使用二阶贝塞尔曲线
        path.quadTo(ctrlX, ctrlY, endX, endY);
        PathMeasure mPathMeasure = new PathMeasure(path, false);

        ObjectAnimator scaleXanim = ObjectAnimator.ofFloat(animImg, "scaleX", 1, 0.8f, 0.6f, 0.4f);
        ObjectAnimator scaleYanim = ObjectAnimator.ofFloat(animImg, "scaleY", 1, 0.8f, 0.6f, 0.4f);

        float[] mCurrentPosition = new float[2];
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) animation.getAnimatedValue();
                // 获取当前点坐标封装到mCurrentPosition
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距离的坐标点和切线，
                // pos会自动填充上坐标，这个方法很重要。
                // mCurrentPosition此时就是中间距离点的坐标值
                mPathMeasure.getPosTan(value, mCurrentPosition, null);
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                animImg.setTranslationX(mCurrentPosition[0]);
                animImg.setTranslationY(mCurrentPosition[1]);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 把执行动画的商品图片从父布局中移除
                ((FrameLayout) binding.getRoot()).removeView(animImg);
                binding.controller.getControllerViewBinding().ivLike.setImageDrawable(ivFeelLike.getDrawable());
                likeAnimation(binding.controller.getControllerViewBinding().ivLike);
                if (videoInfo.getContent() != null && videoInfo.getContent().getLikeType() != likeType) {
                    switch (likeType) {
                        case 2:
                            if (!isLaterSeeLike) {
                                isLaterSeeLike = true;
                                requestPostLike(binding, videoInfo, position, likeType);
                            }
                            break;
                        case 3:
                            if (!isTouchHeartLike) {
                                isTouchHeartLike = true;
                                requestPostLike(binding, videoInfo, position, likeType);
                            }
                            break;
                        case 4:
                            if (!isTouchCryLike) {
                                isTouchCryLike = true;
                                requestPostLike(binding, videoInfo, position, likeType);
                            }
                            break;
                        case 5:
                            if (!isLoveSeeLike) {
                                isLoveSeeLike = true;
                                requestPostLike(binding, videoInfo, position, likeType);
                            }
                            break;
                        case 6:
                            if (!isLaughOutLike) {
                                isLaughOutLike = true;
                                requestPostLike(binding, videoInfo, position, likeType);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(800);
        animatorSet.setInterpolator(new AccelerateInterpolator());
        animatorSet.playTogether(scaleXanim, scaleYanim, valueAnimator);
        animatorSet.start();
    }

    private void likeAnimation(ImageView ivLike) {
        ObjectAnimator scaleXanim = ObjectAnimator.ofFloat(ivLike, "scaleX", 1.2f, 1.1f, 1);
        ObjectAnimator scaleYanim = ObjectAnimator.ofFloat(ivLike, "scaleY", 1.2f, 1.1f, 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(scaleXanim, scaleYanim);
        animatorSet.start();
    }

    private void requestPostLike(ItemHomeVideoBinding binding, HomeVideoImageListBean.ListBean videoInfo, int position, int likeType) {
        viewModel.setHomeVideoLike(likeType, videoInfo.getContent().getPostId()).observe((LifecycleOwner) mContext, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    HomeVideoLikeBean bean = resultBean.getData();
                    if (videoInfo.getContent().getLikeType() == 0) {
                        videoInfo.getContent().setLikeCount(videoInfo.getContent().getLikeCount() + 1);
                        binding.controller.getControllerViewBinding().likeCount.setText(ConstantUtil.getLikeNumString(videoInfo.getContent().getLikeCount(), "赞"));
                    }
                    if (likeType == 0) {
                        videoInfo.getContent().setLikeCount(videoInfo.getContent().getLikeCount() - 1);
                        binding.controller.getControllerViewBinding().likeCount.setText(ConstantUtil.getLikeNumString(videoInfo.getContent().getLikeCount(), "赞"));
                    }
                    videoInfo.getContent().setLikeType(bean.getLikeType());
                    videoInfo.getContent().setDisLikeType(0);
                    binding.controller.getControllerViewBinding().ivDislike.setImageResource(R.mipmap.home_dislike);
                    ConstantUtil.isUpdataMyFragmentList = true;
                } else {
                    resetLikeImageResource(binding, videoInfo);
                }
            } else {
                resetLikeImageResource(binding, videoInfo);
            }
            resetLikeStatus(binding, likeType);
        });
        clickPost(videoInfo, position, "like", likeType, tab);
    }

    private void resetLikeImageResource(ItemHomeVideoBinding binding, HomeVideoImageListBean.ListBean videoInfo) {
        switch (videoInfo.getContent().getLikeType()) {
            case 0:
                binding.controller.getControllerViewBinding().ivLike.setImageResource(R.mipmap.home_like);
                break;
            case 1:
                binding.controller.getControllerViewBinding().ivLike.setImageResource(R.mipmap.home_like_click);
                break;
            case 2:
                binding.controller.getControllerViewBinding().ivLike.setImageResource(R.mipmap.home_like_later_see);
                break;
            case 3:
                binding.controller.getControllerViewBinding().ivLike.setImageResource(R.mipmap.home_like_touch_heart);
                break;
            case 4:
                binding.controller.getControllerViewBinding().ivLike.setImageResource(R.mipmap.home_like_touch_cry);
                break;
            case 5:
                binding.controller.getControllerViewBinding().ivLike.setImageResource(R.mipmap.home_like_love_see);
                break;
            case 6:
                binding.controller.getControllerViewBinding().ivLike.setImageResource(R.mipmap.home_like_laugh_out);
                break;
            default:
                break;
        }
    }

    private void resetLikeStatus(ItemHomeVideoBinding binding, int likeType) {
        switch (likeType) {
            case 0:
            case 1:
                binding.controller.getControllerViewBinding().ivLike.setEnabled(true);
                binding.controller.getControllerViewBinding().ivDislike.setEnabled(true);
                break;
            case 2:
                isLaterSeeLike = false;
                break;
            case 3:
                isTouchHeartLike = false;
                break;
            case 4:
                isTouchCryLike = false;
                break;
            case 5:
                isLoveSeeLike = false;
                break;
            case 6:
                isLaughOutLike = false;
                break;
            default:
                break;
        }
    }

    BigImgDialog bigImgDialog;

    private void showBigImgDialog(String curUrl, int playPosition) {
        if (bigImgDialog != null && bigImgDialog.isVisible()) {
            return;
        }

        bigImgDialog = new BigImgDialog(curUrl);
        if (((MainActivity) mContext).isStateEnable()) {
            bigImgDialog.show(childFragmentManager, "" + tab);
        }
    }

    ShareDialog shareDialog;

    private void showShareDialog(HomeVideoImageListBean.ListBean videoInfo, int playPosition) {
        if (shareDialog != null && shareDialog.isVisible()) {
            return;
        }
        shareDialog = new ShareDialog(false, 1, videoInfo.getContent() != null && videoInfo.getContent().getPostType() != 3);
        shareDialog.show(childFragmentManager, "" + tab);
        shareDialog.setShareDialogListener(new OnShareDialogListener() {
            @Override
            public void onWechatShare() {
                share(videoInfo, SHARE_MEDIA.WEIXIN);
                clickPost(videoInfo, playPosition, "share", 1, tab);
                shareDialog.dismiss();
            }

            @Override
            public void onWechatCircleShare() {
                share(videoInfo, SHARE_MEDIA.WEIXIN_CIRCLE);
                clickPost(videoInfo, playPosition, "share", 2, tab);
                shareDialog.dismiss();
            }

            @Override
            public void onQQShare() {
                share(videoInfo, SHARE_MEDIA.QQ);
                clickPost(videoInfo, playPosition, "share", 3, tab);
                shareDialog.dismiss();
            }

            @Override
            public void onQQZoneShare() {
                share(videoInfo, SHARE_MEDIA.QZONE);
                clickPost(videoInfo, playPosition, "share", 4, tab);
                shareDialog.dismiss();
            }

            @Override
            public void onShareReport() {
                ReportActivity.startActivity(mContext, videoInfo.getContent().getPostId());
                shareDialog.dismiss();
//                CommonUtils.showShort("举报");
            }

            @Override
            public void onShareDelete() {
//                CommonUtils.showShort("删除");
            }

            @Override
            public void onShareDownload() {
                showDownloadDialog(videoInfo, playPosition);
            }
        });
    }

    public void showDownloadDialog(final HomeVideoImageListBean.ListBean videoInfo, int playPosition) {
        if (downloadVideoDialog != null && downloadVideoDialog.isVisible()) {
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

            }

        });
        downloadVideoDialog.show(childFragmentManager, "" + tab);
    }

    private void downloadVideoNoWaterMark(HomeVideoImageListBean.ListBean videoInfo) {
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

    private void share(HomeVideoImageListBean.ListBean videoInfo, SHARE_MEDIA shareMedia) {
        if (!ConstantUtil.isWxQQInstall(mContext, shareMedia, FunnyApplication.mTencent, mContext.getString(R.string.weixin_appid))) {
            return;
        }
        if (videoInfo.getContent().getPostType() == 3) {
            return;
        }
        String shareTitle = videoInfo.getContent().getTitle();
        String shareName = videoInfo.getContent().getTopics() + "@" + videoInfo.getContent().getNickname();
        String thumbUrl = videoInfo.getContent().getPostType() == 1 ? videoInfo.getContent().getThumb() : videoInfo.getContent().getUrl();
        ShareLibraryUM.shareWebUrl(mContext, shareListener, R.mipmap.icon, thumbUrl, shareMedia, shareTitle, shareName, videoInfo.getContent().getPostId(), SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"), videoInfo.getContent().getPostType());
    }

    private UMShareListener shareListener = new UMShareListener() {

        MaterialDialog progressDialog;

        @Override
        public void onStart(SHARE_MEDIA platform) {
            //成功
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
        HomeImageTextFragment.isShowCommentDialog = true;
        HomeImageTextFragment.startCommentDialogTime = System.currentTimeMillis();
        postReviewDialog = new PostReviewDialog(viewModel, tab, videoInfo, 1);
        postReviewDialog.show(childFragmentManager, "" + tab);
    }

    private void showLikeDialog(ItemHomeVideoBinding binding, HomeVideoImageListBean.ListBean videoInfo, int position, boolean isDoubleClick) {
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
//            CommonUtils.showShort("以后看");
            });
            binding.likeLayout2.setOnClickListener(view -> {
                binding.rlHomeLikeDialog.setVisibility(View.GONE);
                setHomeVideoLike(binding, 3, videoInfo, position);
                removeRunnable();
//            CommonUtils.showShort("触动内心");
            });
            binding.likeLayout3.setOnClickListener(view -> {
                binding.rlHomeLikeDialog.setVisibility(View.GONE);
                setHomeVideoLike(binding, 4, videoInfo, position);
                removeRunnable();
//            CommonUtils.showShort("感动哭了");
            });
            binding.likeLayout4.setOnClickListener(view -> {
                binding.rlHomeLikeDialog.setVisibility(View.GONE);
                setHomeVideoLike(binding, 5, videoInfo, position);
                removeRunnable();
//            CommonUtils.showShort("特别爱看");
            });
            binding.likeLayout5.setOnClickListener(view -> {
                binding.rlHomeLikeDialog.setVisibility(View.GONE);
                setHomeVideoLike(binding, 6, videoInfo, position);
                removeRunnable();
//            CommonUtils.showShort("笑出声");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHomeVideoLike(ItemHomeVideoBinding binding, int likeType, HomeVideoImageListBean.ListBean videoInfo, int playPosition) {
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
                    ConstantUtil.isUpdataMyFragmentList = true;
                }
            }
        });
        binding.controller.showBeautifulBig(1, likeType);
        clickPost(videoInfo, playPosition, "like", likeType, tab);
    }

    private void showDisLikeDialog(ItemHomeVideoBinding homeBinding, HomeVideoImageListBean.ListBean videoInfo, int position, int fromLocal) {
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
                //右边的点踩按钮，点踩并且弹框
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
//                  dialog.dismiss();
//                setHomeVideoDisLike(binding, 4, videoInfo, position);
//                ConstantUtil.hideSoftKeyboardFromActivity((Activity) mContext, binding.dislikeEdit);
//                if (videoInfo.getContent().getTopicIds().equals("") || videoInfo.getContent().getTopicIds().equals("0")) {
//                    CommonUtils.showShort("没有话题哦");
//                } else {
//                    ForbidTopicsActivity.startActivity(mContext, videoInfo.getContent().getPostId(), videoInfo.getContent().getTopicIds(), videoInfo.getContent().getTopics());
//                }
////            CommonUtils.showShort("屏蔽这个话题");
//            });

//            binding.dislikeLayout2.setOnClickListener(view -> {
//                  dialog.dismiss();
//                setHomeVideoDisLike(binding, 3, videoInfo, position);
//                ConstantUtil.hideSoftKeyboardFromActivity((Activity) mContext, binding.dislikeEdit);
////            CommonUtils.showShort("屏蔽作者");
//            });

            binding.dislikeLayout3.setOnClickListener(view -> {
                dialog.dismiss();
                setHomeVideoDisLike(homeBinding, 2, videoInfo, position);
                ConstantUtil.hideSoftKeyboardFromActivity((Activity) mContext, binding.dislikeEdit);
                CommonUtils.showShort("以后不再推荐类似帖子给你了");
                if (videoInfo.getContent().getPostType() == 1){
                    RxBus.getDefault().post(RxCodeConstant.HOME_VIDEO_UPDATE_DISLIKE, position);
                }else {
                    RxBus.getDefault().post(RxCodeConstant.HOME_IMG_UPDATE_DISLIKE, position);
                }
            });

            binding.dislikeFeedback.setOnClickListener(view -> {
                ClipboardManager cmb = (ClipboardManager) FunnyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText("675082738");
                CommonUtils.showShort("已复制QQ群号");
                ConstantUtil.hideSoftKeyboardFromActivity((Activity) mContext, binding.dislikeEdit);
//            CommonUtils.showShort("反馈");
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
                ConstantUtil.hideSoftKeyboardFromActivity((Activity) mContext, binding.dislikeEdit);
                String message = binding.dislikeEdit.getText().toString().trim();
                if (!TextUtils.isEmpty(message)) {
                    dialog.dismiss();
                    viewModel.sendReportContent(videoInfo.getContent().getPostId(), "", message, 0, "").observe((LifecycleOwner) mContext, (Observer<BaseResultBean<Object>>) resultBean -> {
                        if (resultBean != null) {
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

//            dialog.setCancelable(true);
//            dialog.setCanceledOnTouchOutside(true);
//            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
//            params.bottomMargin = SizeUtils.dp2px(110);
//            params.gravity = Gravity.BOTTOM;
//            dialog.getCustomView().setLayoutParams(params);
//
//            Window dialogWindow = dialog.getWindow();
//            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//            dialogWindow.setGravity(Gravity.BOTTOM);//全屏显示不全的问题
//            dialogWindow.setAttributes(lp);
//            dialog.show();
//
//            try {
//                new Handler().postDelayed(() -> {
//                    if (dialog != null && dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }, 7000);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setHomeVideoDisLike(ItemHomeVideoBinding binding, int likeType, HomeVideoImageListBean.ListBean videoInfo, int playPosition) {
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
        ConstantUtil.isUpdataMyFragmentList = true;
        binding.controller.showBeautifulBig(2, likeType);
        clickPost(videoInfo, playPosition, "dislike", likeType, tab);
    }

    private void showRewardDialog(HomeVideoImageListBean.ListBean videoInfo, int position) {
        ConstantUtil.createUmEvent("home_reword_click");//首页赏的点击
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
            }, dialogCancelTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param typeName "like" "dislike" "share"
     * @param tab      1 2
     */
    private void clickPost(HomeVideoImageListBean.ListBean videoInfo, int playPosition, String typeName, int type, int tab) {

        String post_type = tab == 1 ? "video" : "img";

        int[] pageAndPos = HomeVideoFragment.getPageOrPos(tab == 1 ? HomeVideoFragment.pagePositionList : HomeImageTextFragment.pagePositionList, playPosition);

        int page = pageAndPos[0];
        int position = pageAndPos[1];// playPosition % singleSize;

        //点赞或者踩也判断一下，删除回传list里面的当前数据
        if (tab == 1) {
            ListenerUtils.getInstance().getHomeRemoveVideoPostIdListener().onRemovePostId(videoInfo.getContent().getPostId());
        } else {
            ListenerUtils.getInstance().getHomeRemoveImgPostIdListener().onRemovePostId(videoInfo.getContent().getPostId());
        }

        long endTime = System.currentTimeMillis();
        //点击统计
        JsonObject postJsonObject = new JsonObject();
        postJsonObject.addProperty("post_id", videoInfo.getContent().getPostId());
        postJsonObject.addProperty("post_type", post_type);
        postJsonObject.addProperty("tab", tab);
        postJsonObject.addProperty(typeName + "_type", ConstantUtil.changeLikeLogString(typeName, type));
        postJsonObject.addProperty("page", page);
        postJsonObject.addProperty("position", position);
        postJsonObject.addProperty("direction", tab == 1 ? HomeVideoFragment.direction : HomeImageTextFragment.direction);//用户上下滑的方向。up//上滑、down//下拉、homebutton//按钮、appuse//首次进入
        postJsonObject.addProperty("create_time", endTime);
        postJsonObject.addProperty("topic_ids", videoInfo.getContent().getTopicIds());
        postJsonObject.addProperty("op_topic_ids", videoInfo.getContent().getOpTopicIds() == null ? "" : videoInfo.getContent().getOpTopicIds());
        postJsonObject.addProperty("tagId", videoInfo.getContent().getTagId());
        int animation_type = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("home_like_animation_type");
        postJsonObject.addProperty("animation_type", animation_type == 0 ? "panda" : "astronaut");// "panda" : "astronaut"
        String provider = videoInfo.getContent().getProvider();

        RequestParamUtil.addStartLogHeadParam(postJsonObject, typeName, "post", "index", TextUtils.isEmpty(provider) ? "rec" : provider);

        String jsonData = postJsonObject.toString();

        String sss = "{\"topic_ids\":\"10007\",\"device_id\":\"2ecacdbfd60b83124909f1becd9b430ff\",\"create_time\":1627639552052,\"video_total_time\":106467,\"current_position\":41818,\"end_time\":1627639552052,\"quit_app\":0,\"op_topic_ids\":\"10007\",\"duration\":469491,\"start_time\":1627639082561,\"detail_dur\":0,\"post_id\":497490457088,\"tab\":1,\"user_id\":107316316672,\"play_dur\":467686,\"post_type\":\"video\",\"page\":0,\"position\":1,\"net\":\"wifi\",\"direction\":\"homebutton\"}";

        //上报本次播放的视频时长
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