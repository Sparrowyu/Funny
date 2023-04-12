package com.sortinghat.funny.ui.home;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.google.gson.JsonObject;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.base.BaseFragment;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.TTAdManagerHolder;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.common.utils.statusbar.StatusBarUtil;
import com.sortinghat.common.view.GridSpacingItemDecoration;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.HomeDialogChoseObjectAdapter;
import com.sortinghat.funny.adapter.HomeVideoAdapter;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.bean.TopicHomeChoseDialogBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.DialogHomeChoseObjectBinding;
import com.sortinghat.funny.databinding.DialogHomeCompleteInfoBinding;
import com.sortinghat.funny.databinding.DialogHomeGradeGuideBinding;
import com.sortinghat.funny.databinding.DialogHomeObjectFootviewBinding;
import com.sortinghat.funny.databinding.DialogHomePublishGuideBinding;
import com.sortinghat.funny.databinding.ItemHomeVideoBinding;
import com.sortinghat.funny.ui.BottomSheetDialog.ChoseCompleteAgeDialog;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DateUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.viewmodel.HomeViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wzy on 2021/9/16
 */
public class BaseHomeMediaFragment<VM extends AndroidViewModel, SV extends ViewDataBinding> extends BaseFragment<VM, SV> {

    /**
     * 1:video 2:image
     */
    protected int tabType;

    protected ViewPager2 viewPager;

    protected HomeVideoAdapter homeVideoAdapter;

    protected List<Long> noLookPostIdList;

    private HomeViewModel homeViewModel;
    protected int mCurPos = 0;
    protected TTAdNative mTTAdNative;
    protected float expressViewWidth = 350;
    protected float expressViewHeight = 350;
    protected final String csjAdloadTag = "csjAdloadTag";

    public void setHomeViewModel(HomeViewModel homeViewModel) {
        this.homeViewModel = homeViewModel;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void initData() {
    }

    protected void initBaseViews() {
        mTTAdNative = TTAdManagerHolder.get().createAdNative(activity);
        //step3:创建广告请求参数AdSlot,具体参数含义参考文档
        try {
            expressViewWidth = ScreenUtils.getScreenWidth();
            expressViewHeight = ScreenUtils.getScreenHeight() - SizeUtils.dp2px(60) - StatusBarUtil.getStatusBarHeight(activity);
        } catch (Exception e) {
            expressViewHeight = 0; //高度设置为0,则高度会自适应
        }
        ThreadUtils.runOnUiThreadDelayed(() -> {
            updateAdConfig();
        }, 2000);
    }

    protected void showGuideLayer(int position, ViewPager2 viewPager) {
        if (position < 0 || homeVideoAdapter.getData().size() <= position) {
            return;
        }
        setPostViewNumSp(position);
        String doubleClickLike = "";
        if (tabType == 1) {
            doubleClickLike = "show_video_double_click_like_guide";
        } else if (tabType == 2) {
            doubleClickLike = "show_image_double_click_like_guide";
        }
        if (position == 24) {
            if (!SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(doubleClickLike)) {
                loadGuideLayerGifImage(position, viewPager, R.drawable.home_guide_double_click_like, "试试双击屏幕点赞");
                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(doubleClickLike, true);
            }
        } else {
            HomeVideoImageListBean.ListBean videoInfo = homeVideoAdapter.getItemData(position);
            if (videoInfo != null && videoInfo.getContent() != null) {
                if (tabType == 1) {
                    switch (TextUtils.isEmpty(String.valueOf(videoInfo.getContent().getPostId())) ? "" : String.valueOf(videoInfo.getContent().getPostId())) {
                        case "633692588544":
                            if (!SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean("video_post_633692588544")) {
                                loadGuideLayerGifImage(position, viewPager, R.drawable.home_guide_click_full_screen_see, "单击屏幕清屏看");
                                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("video_post_633692588544", true);
                            }
                            break;
                        case "925739971072":
                            if (!SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean("video_post_925739971072")) {
                                loadGuideLayerGifImage(position, viewPager, R.drawable.home_guide_click_full_screen_see, "单击屏幕清屏看");
                                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("video_post_925739971072", true);
                            }
                            break;
                        case "1034959454720":
                            if (!SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean("video_post_1034959454720")) {
                                loadGuideLayerGifImage(position, viewPager, R.drawable.home_guide_click_full_screen_see, "单击屏幕清屏看");
                                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("video_post_1034959454720", true);
                            }
                            break;
                        case "1645420257024":
                            if (!SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean("video_post_1645420257024")) {
                                loadGuideLayerGifImage(position, viewPager, R.drawable.home_guide_click_full_screen_see, "单击屏幕清屏看");
                                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("video_post_1645420257024", true);
                            }
                            break;
                        default:
                            break;
                    }
                } else if (tabType == 2) {
                    switch (TextUtils.isEmpty(String.valueOf(videoInfo.getContent().getPostId())) ? "" : String.valueOf(videoInfo.getContent().getPostId())) {
                        case "2022320780032":
                            if (!SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean("image_post_2022320780032")) {
                                loadGuideLayerGifImage(position, viewPager, R.drawable.home_guide_click_full_screen_see, "单击屏幕清屏看");
                                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("image_post_2022320780032", true);
                            }
                            break;
                        case "1059798399232":
                            if (!SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean("image_post_1059798399232")) {
                                loadGuideLayerGifImage(position, viewPager, R.drawable.home_guide_click_full_screen_see, "单击屏幕清屏看");
                                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("image_post_1059798399232", true);
                            }
                            break;
                        case "1185557331712":
                            if (!SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean("image_post_1185557331712")) {
                                loadGuideLayerGifImage(position, viewPager, R.drawable.home_guide_click_full_screen_see, "单击屏幕清屏看");
                                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("image_post_1185557331712", true);
                            }
                            break;
                        case "1894048747008":
                            if (!SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean("image_post_1894048747008")) {
                                loadGuideLayerGifImage(position, viewPager, R.drawable.home_guide_click_full_screen_see, "单击屏幕清屏看");
                                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put("image_post_1894048747008", true);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        isInfoDialog = 0;
        if (isShowDialogInfo(0)) {
            isInfoDialog = 1;
            loadNewTypeViewAd(position);
//                showHomeCompleteInfoDialog(activity);
        }
        if (isInfoDialog == 0 && isShowDialogInfo(1)) {
            isInfoDialog = 1;
            ThreadUtils.runOnUiThreadDelayed(() -> {
                getUserLikeTopic(activity);
            }, tabType == 1 ? 1000 : 10);
        }
        if (isInfoDialog == 0) {
            showGuidePublishDialog(activity);
        }
        if (isInfoDialog == 0) {
            showGuideGradeDialog(activity);//评分弹框
        }
    }

    private int isSetCompleteType = 0;//是否显示了

    private void loadNewTypeViewAd(int adPosition) {
        if (isSetCompleteType != 0) {
            return;
        }
        isSetCompleteType = 1;
        int insertAdPosition = adPosition + 1;
        HomeVideoImageListBean.ListBean.ContentBean videoContentInfo = new HomeVideoImageListBean.ListBean.ContentBean();
        videoContentInfo.setPostType(4);
        videoContentInfo.setPostId(0l);
        videoContentInfo.setAdPos(insertAdPosition);
        videoContentInfo.setProvider("information");
        HomeVideoImageListBean.ListBean videoInfo = new HomeVideoImageListBean.ListBean();
        videoInfo.setContent(videoContentInfo);
        if (homeVideoAdapter.getData().size() >= insertAdPosition) {
            Log.d("showNewType--", "newtype233");
            homeVideoAdapter.addData(insertAdPosition, videoInfo);
        }
    }

    protected boolean isLoadingAd;
    protected HomeVideoImageListBean.ListBean lastAdNoShow;
    protected List<HomeVideoImageListBean.ListBean> adCacheList = new ArrayList<>();
    protected List<Integer> hasLoadPosList = new ArrayList<>();//位置存到list集合，刷新清除

    //加载的位置是 0 ，3，12，21,在position为0时也加载//提前四个位置时才加载，（3 12 21，0 7 16） 或者（ 4 13 22 所以为 0，8，17）
    protected boolean isVideoToLoadAd(int position) {
        if (position < 0 || homeVideoAdapter.getData().size() <= position) {
            return false;
        }
        if (position >= 1 && isConfigLoadAd && !isLoadingAd && !hasLoadPosList.contains(position)) {
            int firstPos = isVideoNewAdAB;//第一个广告是 3
            if (position == 1) {
                hasLoadPosList.add(position);
                if (null != lastAdNoShow && lastAdNoShow.getContent().getAd() != null) {
                    lastAdNoShow.getContent().setAdPos(firstPos);
                    if (homeVideoAdapter != null && homeVideoAdapter.getData().size() > firstPos) {
                        homeVideoAdapter.addData(firstPos, lastAdNoShow);
                    } else {
                        //没有加入列表的先存到本地
                        adCacheList.add(lastAdNoShow);
                    }
                    Log.d(csjAdloadTag, "load——false");
                    return false;
                }
                Log.d(csjAdloadTag, "ad-pos:" + position);
                return true;
            } else if (position >= (isVideoNewAdAB + 4) && (((position - (isVideoNewAdAB + 4)) % (loadAdSpaceNum + 1)) == 0)) {
                hasLoadPosList.add(position);
                Log.d(csjAdloadTag, "ad-pos:" + position);
                return true;
            }
        }
        return false;
    }

    //加载的位置是 1 ，8，16，24,在position为0时不加载，避免浪费广告,8 17 26 //3 12 21
    protected boolean isToLoadAd(int position) {
        if (position < 0 || homeVideoAdapter.getData().size() <= position) {
            return false;
        }
        if (position >= 3 && isConfigLoadAd && !isLoadingAd && !hasLoadPosList.contains(position)) {
            if (position == 3) {
                hasLoadPosList.add(position);
                if (null != lastAdNoShow && lastAdNoShow.getContent().getAd() != null) {
                    lastAdNoShow.getContent().setAdPos(loadAdSpaceNum);
                    if (homeVideoAdapter != null && homeVideoAdapter.getData().size() > loadAdSpaceNum) {
                        homeVideoAdapter.addData(loadAdSpaceNum, lastAdNoShow);
                    } else {
                        //没有加入列表的先存到本地
                        adCacheList.add(lastAdNoShow);
                    }
                    Log.d(csjAdloadTag, "load——false");
                    return false;
                }
                Log.d(csjAdloadTag, "ad-pos:" + position);
                return true;
            } else if (position >= 12 && ((position - 3) % (loadAdSpaceNum + 1)) == 0) {
                hasLoadPosList.add(position);
                Log.d(csjAdloadTag, "ad-pos:" + position);
                return true;
            }
        }
        return false;
    }

    //当前是否为帖子
    protected boolean showVideoPostType(int playPosition) {
        if (homeVideoAdapter != null && playPosition >= 0 && playPosition < homeVideoAdapter.getData().size() && homeVideoAdapter.getData().get(playPosition).getContent().getPostType() < 3) {
            return true;
        } else {
            return false;
        }
    }

    private void showGuideGradeDialog(Context context) {
//
        boolean clicked = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(Constant.PUBLISH_POST_GRADE_CLICKED);
        if (clicked || !ConstantUtil.isHuaweiThroughReviewState(context)) {
            LogUtils.e("showpublishGrade", "-showing:" + clicked);
            return;//已经点击过，或者是华为审核状态
        }

        boolean showString = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(Constant.PUBLISH_POST_GRADE);//
        if (!showString) {
            LogUtils.e("showpublishGrade", "-showing:" + showString);
            return;
        }

        int dateViewNum = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getInt(Constant.POST_VIEW_NUM_DAY);//当天的帖子次数
        if (dateViewNum < 19) {
            return;
        }
        String date = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getString(Constant.PUBLISH_POST_GRADE_DAY);//3天如果不弹框，继续

        boolean showed = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(Constant.PUBLISH_POST_GRADE_SHOWED);

        int showNum = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getInt(Constant.PUBLISH_POST_GRADE_NUM, 0);
        //16号出了，19号就出第二个
        if (showNum >= 19) {
            return;
        }
        //三天后，是每隔十个继续出
        if (showed) {
            if (DateUtil.getDateFromNetToProgressDay(date) < 3) {
                return;
            }
        }
        SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_GRADE_CLICKED, true);//154版本改为只显示一次
        ConstantUtil.createUmEvent("store_Evaluation");//引导评价弹窗页
        String dateSp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_GRADE_DAY, dateSp);
        SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_GRADE_SHOWED, true);
        SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_GRADE_NUM, showNum + 1);
        ThreadUtils.runOnUiThreadDelayed(() -> {
            setVideoPlayOrPause(true);
        }, 1500);

        MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(activity, R.layout.dialog_home_grade_guide);
        DialogHomeGradeGuideBinding dialogHomeBinding = DataBindingUtil.bind(dialog.getCustomView());

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getCustomView().setLayoutParams(params);

//        dialog.setOnDismissListener(dialog1 -> {
//            setVideoPlayOrPause(false);
//        });

        dialogHomeBinding.btCriticize.setOnClickListener(v -> {
            ConstantUtil.createUmEvent("store_Evaluation_opinion");//引导评价弹窗-吐槽页
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_GRADE_CLICKED, true);
            dialogHomeBinding.dialogContentCriticize.setVisibility(View.VISIBLE);
            dialogHomeBinding.dialogContent.setVisibility(View.GONE);
            ConstantUtil.showSoftInputFromWindow(activity, dialogHomeBinding.etSign);
        });
        dialogHomeBinding.btGrade.setOnClickListener(v -> {
            ConstantUtil.createUmEvent("store_Evaluation_btn_ok");//引导评价弹窗-评价按钮
            SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_GRADE_CLICKED, true);
            ConstantUtil.launchAppDetail(activity, activity.getPackageName(), "");
            if (!((BaseActivity) context).isFinishing()) {
                dialog.dismiss();
            }
        });
        dialog.getCustomView().findViewById(R.id.bt_cancel).setOnClickListener(view -> {
            if (!((BaseActivity) context).isFinishing()) {
                dialog.dismiss();//不了，谢谢
            }
            setVideoPlayOrPause(false);
        });
        dialog.getCustomView().findViewById(R.id.tv_cancel).setOnClickListener(view -> {
            if (!((BaseActivity) context).isFinishing()) {
                dialog.dismiss();//吐槽界面的取消按钮
            }
            setVideoPlayOrPause(false);
        });


        dialogHomeBinding.etSign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (dialogHomeBinding.etSign.getText().toString().trim().length() >= 1) {
                    dialogHomeBinding.tvSure.setEnabled(true);
//                    dialogHomeBinding.tvSure.setBackgroundResource(R.drawable.click_can_orange_bt_bg);
                    dialogHomeBinding.tvSure.setTextColor(getResources().getColor(R.color.color_FF632E));
                } else {
                    dialogHomeBinding.tvSure.setEnabled(false);
//                    dialogHomeBinding.tvSure.setBackgroundResource(R.drawable.click_no_gray_bt_bg);
                    dialogHomeBinding.tvSure.setTextColor(getResources().getColor(R.color.color_333333));
                }
            }
        });

        dialogHomeBinding.tvSure.setOnClickListener(v -> {
            String content = dialogHomeBinding.etSign.getText().toString().trim();
            if ((TextUtils.isEmpty(content))) {
                CommonUtils.showShort("请输入评价");
                return;
            }
            ConstantUtil.createUmEvent("store_Evaluation_opinion_btn_submit");//引导评价弹窗-吐槽页-提交

//            homeViewModel.getUserCriticizeApp(content).observeForever(resultBean -> {
//                if (resultBean != null) {
//                    if (resultBean.getCode() == 0) {
//                        CommonUtils.showLong("已提交");
//                        if (!((BaseActivity) context).isFinishing()) {
//                            dialog.dismiss();//吐槽界面的取消按钮
//                        }
//                    }
//                }
//            });

//            用以前的帖子吐槽
            homeViewModel.sendReportContent(0, "", content, 0, "").observe((LifecycleOwner) activity, (Observer<BaseResultBean<Object>>) resultBean -> {
                if (resultBean != null) {
                    ConstantUtil.hideSoftKeyboardFromActivity((Activity) activity, dialogHomeBinding.etSign);
                    if (resultBean.getCode() == 0) {
                        if (!((BaseActivity) context).isFinishing()) {
                            dialog.dismiss();//吐槽界面的取消按钮
                        }
                        CommonUtils.showShort("感谢你的反馈");
                        setVideoPlayOrPause(false);
                    }
                }
            });
        });

        dialog.getCustomView().getRootView().setBackgroundColor(CommonUtils.getColor(R.color.transparent));
        if (!((BaseActivity) context).isFinishing()) {
            dialog.show();
        }
    }

    private void showGuidePublishDialog(Context context) {
        ConstantUtil.createUmEvent("home_user_publish_guide_window_pv");//首页-发帖引导-pv
        String date = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getString(Constant.PUBLISH_POST_EDUCATION_DAY);//3天如果不发贴，继续

        String showString = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getString(Constant.PUBLISH_POST_EDUCATION);//-1不显示
        long postNum = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getLong(Constant.POST_VIEW_NUM);
        boolean showed = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(Constant.PUBLISH_POST_EDUCATION_SHOWED);
        int showNum = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getInt(Constant.PUBLISH_POST_EDUCATION_NUM, 0);
        LogUtils.e("showpublishEdu", "num:" + postNum + "-showing:" + showString + "isshowed" + showed + "days" + DateUtil.getDateFromNetToProgressDay(date) + "-num:" + showNum);
        //16号出了，19号就出第二个
        if (postNum < 65 || TextUtils.isEmpty(showString) || showString.equals("-1") || showNum >= 3) {
            return;
        }
        int showNumTen = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getInt(Constant.PUBLISH_POST_EDUCATION_SHOWED_TEN, 0);
        //三天后，是每隔十个继续出
        if (showed) {
            if (DateUtil.getDateFromNetToProgressDay(date) < 3) {
                return;
            } else {
                if (showNumTen < 17) {
                    SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_EDUCATION_SHOWED_TEN, showNumTen + 1);
                    return;
                }
            }
        }
        isInfoDialog = 1;
        SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_EDUCATION_SHOWED_TEN, 0);
        String dateSp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_EDUCATION_DAY, dateSp);
        SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_EDUCATION_SHOWED, true);
        SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.PUBLISH_POST_EDUCATION_NUM, showNum + 1);
        ThreadUtils.runOnUiThreadDelayed(() -> {
            setVideoPlayOrPause(true);
        }, 1500);
        MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(activity, R.layout.dialog_home_publish_guide);
        DialogHomePublishGuideBinding dialogHomeBinding = DataBindingUtil.bind(dialog.getCustomView());
        dialogHomeBinding.tvDes.setText(showString);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        dialog.getCustomView().setLayoutParams(params);

        dialog.setOnDismissListener(dialog1 -> {
            setVideoPlayOrPause(false);
        });

        QuickClickListener quickClickListener = new QuickClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                switch (v.getId()) {
                    case R.id.dialog_main:
                        if (!((BaseActivity) context).isFinishing()) {
                            dialog.dismiss();
                        }
                        break;
                    case R.id.tx_ok:
                    case R.id.rl_sure:
                        if (!((BaseActivity) context).isFinishing()) {
                            dialog.dismiss();
                        }
                        RxBus.getDefault().post(RxCodeConstant.PUBLISH_VIDEO_OR_IMG, 2);
                        break;
                }
            }
        };

        dialogHomeBinding.dialogMain.setOnClickListener(quickClickListener);
        dialogHomeBinding.rlSure.setOnClickListener(quickClickListener);
        dialogHomeBinding.txOk.setOnClickListener(quickClickListener);

        dialog.getCustomView().getRootView().setBackgroundColor(CommonUtils.getColor(R.color.transparent));
        if (!((BaseActivity) context).isFinishing()) {
            dialog.show();
        }
    }

    private int isInfoDialog = 0;//区分开两个弹框是否同时显示，已经显示了一个弹框后，下个再显示其他的

    private void loadGuideLayerGifImage(int position, ViewPager2 viewPager,
                                        int drawableResource, String guideText) {
        ThreadUtils.runOnUiThreadDelayed(() -> {
            RecyclerView.ViewHolder viewHolder = ((RecyclerView) viewPager.getChildAt(0)).findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
                BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding>) viewHolder;
                if (bindingViewHolder.binding != null) {
                    bindingViewHolder.binding.clGuideAnimateImageAndText.setVisibility(View.VISIBLE);
                    GlideUtils.loadGifImageFromResource(drawableResource, bindingViewHolder.binding.ivGuideAnimateImage);
                    bindingViewHolder.binding.tvGuideText.setText(guideText);

                    ThreadUtils.runOnUiThreadDelayed(() -> {
                        GifDrawable gifDrawable = (GifDrawable) bindingViewHolder.binding.ivGuideAnimateImage.getDrawable();
                        if (gifDrawable != null && gifDrawable.isRunning()) {
                            gifDrawable.stop();
                        }
                        bindingViewHolder.binding.clGuideAnimateImageAndText.setVisibility(View.GONE);
                    }, 3000);
                }
            }
        }, 3000);
    }

    /**
     * 更新帖子的点赞或点踩状态或评论数
     */
    protected void updatePostLikeOrUnlikeOrReview(HomeVideoImageListBean.ListBean.ContentBean
                                                          videoOrImageContent) {
        for (int i = 0; i < homeVideoAdapter.getData().size(); i++) {
            HomeVideoImageListBean.ListBean listBean = homeVideoAdapter.getItemData(i);
            if (listBean != null && listBean.getContent() != null && videoOrImageContent != null && listBean.getContent().getPostId() == videoOrImageContent.getPostId()) {
                listBean.getContent().setReplyCount(videoOrImageContent.getReplyCount());
                RecyclerView.ViewHolder viewHolder = ((RecyclerView) viewPager.getChildAt(0)).findViewHolderForAdapterPosition(i);
                if (viewHolder != null) {
                    BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemHomeVideoBinding>) viewHolder;
                    if (bindingViewHolder.binding != null) {
                        bindingViewHolder.binding.controller.setLikeOrUnlikeOrReview(listBean.getContent());
                    }
                }
            }
        }
    }

    /**
     * 更新回传list
     */
    protected void clearNoLookPostIdList(int index) {
        if (noLookPostIdList != null)
            noLookPostIdList.clear();
        updateAdConfig();
    }

    protected int loadAdSpaceNum = 10;//广告间隔数
    protected boolean isConfigLoadAd = true;//配置后台是否应该加载广告
    protected int isVideoNewAdAB = 0;//是否是新的视频广告第三个出广告的实验

    /**
     * 更新广告配置
     */
    protected void updateAdConfig() {
        if (tabType == 1) {
            loadAdSpaceNum = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getInt(Constant.HOME_AD_SHOW_SPACE_NUM, 10);
        } else {
            loadAdSpaceNum = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getInt(Constant.HOME_AD_IMG_SHOW_SPACE_NUM, 10);
        }
        //是否通过审核，间隔必须大于2
        if (loadAdSpaceNum > 2) {
            isConfigLoadAd = true;
        } else {
            isConfigLoadAd = false;
        }
    }

    private String PostViewShowChoseAgeHad = "post_view_show_chose_age_had";
    private String PostViewShowChoseAgeHadLocal = "post_view_show_chose_age_had_local";//本地唯一一次
    private String PostViewShowChoseObjectHad = "post_view_show_chose_object_had";
    private String PostViewShowChoseObjectHadLocal = "post_view_show_chose_object_had_local";//本地唯一一次
    private int PostViewAgeShowNum = 8;
    private int PostViewObjectShowNum = 38;

    /**
     * type 0:选择年龄弹框  1：选择话题弹框
     */
    private boolean isShowDialogInfo(int type) {
        long postNum = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getLong(Constant.POST_VIEW_NUM);
        if (type == 0) {
            if (postNum >= PostViewAgeShowNum && SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(PostViewShowChoseAgeHad) && SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(PostViewShowChoseAgeHadLocal, true)) {
                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(PostViewShowChoseAgeHad, false);
                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(PostViewShowChoseAgeHadLocal, false);
                return true;
            }
        } else if (type == 1) {
            if (postNum >= PostViewObjectShowNum && SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(PostViewShowChoseObjectHad) && SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(PostViewShowChoseObjectHadLocal, true)) {
                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(PostViewShowChoseObjectHad, false);
                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(PostViewShowChoseObjectHadLocal, false);
                return true;
            }
        }
        return false;
    }

    String postIdHadAll = "";

    /**
     * 记录帖子数量到Sp，看看是否需要判断是上滑已经看过的帖子
     */
    protected void setPostViewNumSp(int position) {
        if (homeVideoAdapter != null && homeVideoAdapter.getData().size() > position) {
            long postId = homeVideoAdapter.getItemData(position).getContent().getPostId();
            if (!postIdHadAll.contains(postId + "") && showVideoPostType(position)) {
                postIdHadAll = postIdHadAll + "," + postId;
                SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.POST_VIEW_NUM, SPUtils.getInstance(Constant.SP_CONFIG_INFO).getLong(Constant.POST_VIEW_NUM) + 1);
                String dateSp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String date = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getString(Constant.POST_VIEW_NUM_DAY_DATE);//当天
                if (dateSp.equals(date)) {
                    SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.POST_VIEW_NUM_DAY, SPUtils.getInstance(Constant.SP_CONFIG_INFO).getInt(Constant.POST_VIEW_NUM_DAY) + 1);
                } else {
                    SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.POST_VIEW_NUM_DAY, 0);
                    SPUtils.getInstance(Constant.SP_CONFIG_INFO).put(Constant.POST_VIEW_NUM_DAY_DATE, dateSp);
                }

            }
        }
    }

    public void getUserLikeTopic(Context context) {
        homeViewModel.getUserLikeTopic().observeForever(resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    showHomeChoseObjectDialog(context, resultBean.getData());
                }
            }
        });
    }


    private void showHomeChoseObjectDialog(Context
                                                   context, List<TopicHomeChoseDialogBean> listData) {
        if (listData == null || listData.isEmpty()) {
            return;
        }
        goCollectLog(2, "show");
        setVideoPlayOrPause(true);
        ConstantUtil.createUmEvent("home_like_window_pv");//首页-采集喜欢话题页面-pv
        MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(context, R.layout.dialog_home_chose_object);
        DialogHomeChoseObjectBinding dialogHomeBinding = DataBindingUtil.bind(dialog.getCustomView());

        dialog.setOnDismissListener(dialog1 -> {
            setVideoPlayOrPause(false);
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getCustomView().setLayoutParams(params);

        final DialogHomeObjectFootviewBinding footerBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.dialog_home_object_footview, (ViewGroup) dialogHomeBinding.recyclerView.getParent(), false);

//初始化recyclerView
        dialogHomeBinding.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        dialogHomeBinding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, SizeUtils.dp2px(4), false));
        HomeDialogChoseObjectAdapter videoOrImageAdapter = new HomeDialogChoseObjectAdapter(context, listData.size());
        dialogHomeBinding.recyclerView.setAdapter(videoOrImageAdapter);
        dialogHomeBinding.recyclerView.addFooterView(footerBinding.getRoot());

        List<String> listSelect = new ArrayList<>();
        videoOrImageAdapter.setOnItemClickListener((topicId, isCheck) -> {
            if (isCheck) {
                listSelect.add(topicId);
            } else {
                listSelect.remove(topicId);
            }
        });

        videoOrImageAdapter.setNewData(listData);
        dialog.getCustomView().findViewById(R.id.tv_cancel).setOnClickListener(view -> {
            goCollectLog(2, "click_cancel");
            if (!((BaseActivity) context).isFinishing()) {
                dialog.dismiss();
            }
        });

        QuickClickListener quickClickListener = new QuickClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_sure:
                        ConstantUtil.createUmEvent("home_like_window_done_click");//首页-采集喜欢话题页面-完成按钮点击量
                    case R.id.dialog_content:
                        if (!((BaseActivity) context).isFinishing()) {
                            dialog.dismiss();
                        }
                        String select = "";
                        if (listSelect.size() > 0 && null != homeViewModel) {
                            goCollectLog(2, "click_done");
                            for (int i = 0; i < listSelect.size(); i++) {
                                select = listSelect.get(i) + "," + select;
                            }
                            homeViewModel.likeOrShieldTopic(select).observeForever(resultBean -> {
                                if (resultBean != null) {
                                    if (resultBean.getCode() == 0) {
                                        if (v.getId() == R.id.tv_sure) {
                                            CommonUtils.showShort("完成");
                                        }
                                    }
                                }
                            });
                        }
                        if (v.getId() == R.id.dialog_content) {
                            ConstantUtil.createUmEvent("home_like_window_more_click");//首页-采集喜欢话题页面-更多按钮点击量
                            RxBus.getDefault().post(RxCodeConstant.HOME_CHOSE_DIALOG_TO_OBJECT_MORE, 1);
                        }
                        break;
                }
            }
        };
        dialog.getCustomView().findViewById(R.id.tv_sure).setOnClickListener(quickClickListener);
        footerBinding.dialogContent.setOnClickListener(quickClickListener);
        dialog.getCustomView().getRootView().setBackgroundColor(CommonUtils.getColor(R.color.transparent));
        if (!((BaseActivity) context).isFinishing()) {
            dialog.show();
        }
    }

    private void showHomeCompleteInfoDialog(Context context) {
        goCollectLog(1, "show");
        ConstantUtil.createUmEvent("home_user_Info_window_pv");//首页-采集个人信息页面-pv
        setVideoPlayOrPause(true);
        final String[] ageShow = {""};
        String[] valueArrReturn = {"07-12", "13-17", "18-24", "25-30", "31-40", "41以上"};

        MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(activity, R.layout.dialog_home_complete_info);
        DialogHomeCompleteInfoBinding dialogHomeBinding = DataBindingUtil.bind(dialog.getCustomView());

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getCustomView().setLayoutParams(params);

        dialog.setOnDismissListener(dialog1 -> {
            setVideoPlayOrPause(false);
        });

        dialogHomeBinding.completeChoseAge.setOnClickListener(v -> choseCompleteAgeDialog());
        dialog.getCustomView().findViewById(R.id.tv_cancel).setOnClickListener(view -> {
            goCollectLog(1, "click_cancel");
            if (!((BaseActivity) context).isFinishing()) {
                dialog.dismiss();
            }
        });
        dialog.getCustomView().findViewById(R.id.tv_sure).setOnClickListener(view -> {
            if (!((BaseActivity) context).isFinishing()) {
                dialog.dismiss();
            }
            ConstantUtil.createUmEvent("home_user_Info_window_done_click");//首页-采集个人信息页面-完成按钮点击量
            int checkSex = dialogHomeBinding.radioGroupGender.getCheckedRadioButtonId();
            String sex = "";//1,男 2，女
            if (checkSex == -1) {
                //没有选
                sex = "";
            } else if (checkSex == dialogHomeBinding.radioButtonMale.getId()) {
                sex = "1";
                //选择男
            }
            if (checkSex == dialogHomeBinding.radioButtonFemale.getId()) {
                sex = "2";
                //选择女
            }

            if ((TextUtils.isEmpty(ageShow[0]) && TextUtils.isEmpty(sex)) || null == homeViewModel) {
                return;
            }
            goCollectLog(1, "click_done");
            homeViewModel.getCollectionInformation(ageShow[0], sex).observeForever(resultBean -> {
                if (resultBean != null) {
                    if (resultBean.getCode() == 0) {
                        CommonUtils.showShort("完成");
                    }
                }
            });
        });

        dialog.getCustomView().getRootView().setBackgroundColor(CommonUtils.getColor(R.color.transparent));
        if (!((BaseActivity) context).isFinishing()) {
            dialog.show();
        }
        setOnDialogSureAgeListener((i, age) -> {
            dialogHomeBinding.txAge.setText(age + "");
            ageShow[0] = valueArrReturn[i];
        });
    }

    ChoseCompleteAgeDialog choseCompleteAgeDialog;

    public void setOnDialogSureAgeListener(OnDialogAgeSureListener onDialogAgeSureListener) {
        this.onDialogAgeSureListener = onDialogAgeSureListener;
    }

    public interface OnDialogAgeSureListener {
        void choseAge(int i, String age);
    }

    private OnDialogAgeSureListener onDialogAgeSureListener;

    private void choseCompleteAgeDialog() {
        if (choseCompleteAgeDialog != null && choseCompleteAgeDialog.isVisible()) {
            LogUtils.d("shareDialog", "isShowing");
            return;
        }
        choseCompleteAgeDialog = new ChoseCompleteAgeDialog();
        choseCompleteAgeDialog.show(getChildFragmentManager(), "");
        choseCompleteAgeDialog.setOnDialogSureListener((i, age) -> {
//                CommonUtils.showShort("选择年龄" + age);
            choseCompleteAgeDialog.dismiss();
            if (null != onDialogAgeSureListener) {
                onDialogAgeSureListener.choseAge(i, age);
            }

        });
    }

    private void setVideoPlayOrPause(boolean isSetPause) {

    }

    //type 收集话题 ：userLikeTopic 收集年龄性别信息：information
    private void goCollectLog(int collection_type, String action_type) {
        if (homeViewModel == null) {
            return;
        }
        long createTime = System.currentTimeMillis();
        JsonObject startJsonObject = new JsonObject();
        String provider = collection_type == 1 ? "information" : "userLikeTopic";
        startJsonObject.addProperty("collection_type", collection_type == 1 ? "information" : "userLikeTopic");//行为类型userLikeTopic 收集年龄性别信息：information
        startJsonObject.addProperty("action_type", action_type);//show,click_done,click_cancel
        startJsonObject.addProperty("post_type", tabType == 1 ? "video" : "img");//帖子类型，video，img
        startJsonObject.addProperty("create_time", createTime);//事件时间

        RequestParamUtil.addStartLogHeadParam(startJsonObject, "collection", "app", "index", provider);

        Log.d("action-log--app-success", "\n" + startJsonObject.toString());
        homeViewModel.setAppUnifyLog(activity, startJsonObject.toString(), false).observe((LifecycleOwner) activity, resultBean -> {
        });

    }

    protected int lastPosition = -1;

    protected void uploadPlayBase(int tabType, int lastPosition, boolean isOnPause, String
            direction, List<Integer> pagePositionList, long startTime) {
        try {
            long stateTotalTime = 0;
            int playPosition = lastPosition;
            if (playPosition < 0 || homeVideoAdapter.getData().size() < 1) {
                LogUtils.e("HomePostTest-video", "-error:lastPosition=" + lastPosition + "-tab:" + tabType + "-startTime:" + startTime);
                return;
            }

            if (startTime == 0) {
                resetStaticPostTime(tabType);
                return;
            }
            long currentPosition = 0;
            long video_total_time = 0;
            if (tabType == 1) {
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
            if (homeVideoAdapter.getData().size() > playPosition) {
                post_id = homeVideoAdapter.getItemData(playPosition).getContent().getPostId();
            } else {
                return;
            }

            int[] pageAndPos = getPageOrPos(pagePositionList, playPosition);

            int page = pageAndPos[0];
            int position = pageAndPos[1];// playPosition % singleSize;

            if (stateTotalTime <= 300) {
                //快速滑动不上传ups日志，回传未看过的，如果用户往回刷有可能会刷到这个帖子
                LogUtils.e("unexposed_pids----error", postVideoPlayDuration + "-time:" + stateTotalTime + "-id:" + post_id + "-play：" + playPosition + "pos" + position);
                resetStaticPostTime(tabType);
                return;
            }
            if (noLookPostIdList.size() > 0 && noLookPostIdList.contains(post_id)) {
                noLookPostIdList.remove(post_id);
            }
//            if (postVideoPlayDuration > stateTotalTime) {
//                postVideoPlayDuration = stateTotalTime;
//            }
            int postType = homeVideoAdapter.getItemData(playPosition).getContent().getPostType();
            String post_type = postType == 1 ? "video" : postType == 2 ? "img" : postType == 3 ? "ad" : "other";

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
            postJsonObject.addProperty("topic_ids", homeVideoAdapter.getItemData(playPosition).getContent().getTopicIds());
            postJsonObject.addProperty("op_topic_ids", homeVideoAdapter.getItemData(playPosition).getContent().getOpTopicIds());
            postJsonObject.addProperty("tagId", homeVideoAdapter.getItemData(playPosition).getContent().getTagId());
            postJsonObject.addProperty("ad_click_num", homeVideoAdapter.getItemData(playPosition).getContent().getAdClickNum());
            postJsonObject.addProperty("ad_video_is_complete", homeVideoAdapter.getItemData(playPosition).getContent().getAdVideoIsComplete());
            postJsonObject.addProperty("ad_request_id", homeVideoAdapter.getItemData(playPosition).getContent().getAdRequestId());
            postJsonObject.addProperty("ad_type", homeVideoAdapter.getItemData(playPosition).getContent().getAdType());
            homeVideoAdapter.getItemData(playPosition).getContent().setAdClickNum(0);
            homeVideoAdapter.getItemData(playPosition).getContent().setAdVideoIsComplete(0);
            String provider = homeVideoAdapter.getItemData(lastPosition).getContent().getProvider();

            RequestParamUtil.addStartLogHeadParam(postJsonObject, "view", "post", "index", TextUtils.isEmpty(provider) ? "rec" : provider);

            String jsonData = postJsonObject.toString();
            //上报本次播放的视频时长
            LogUtils.d("unexposed_pids-videoimg", "-totalTime:" + stateTotalTime + "\n-complete:" + "-json：" + jsonData + "\nprovider:" + homeVideoAdapter.getItemData(lastPosition).getContent().getProvider());
            resetStaticPostTime(tabType);
            if (homeViewModel == null) {
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
            homeViewModel.setAppUnifyLog(activity, jsonData.toString(), isOnPause).observe((LifecycleOwner) activity, resultBean -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //form  1
    public static int[] getPageOrPos(List<Integer> pagePositionList, int playPosition) {
        int[] arr = new int[2];
        if (pagePositionList.size() < 1) {
            arr[0] = 0;
            arr[1] = 0;
            return arr;
        }

        if (playPosition < pagePositionList.get(0)) {
            arr[0] = 0;
            arr[1] = playPosition;
            return arr;
        }

        for (int i = 0; i < pagePositionList.size(); i++) {
            if (playPosition < pagePositionList.get(i)) {
                arr[0] = i;
                if (i == 0) {
                    arr[1] = playPosition;
                } else {
                    arr[1] = playPosition - pagePositionList.get(i - 1);
                }
                return arr;
            }
        }
        arr[0] = 0;
        arr[1] = 0;
        return arr;
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





