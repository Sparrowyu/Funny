package com.sortinghat.funny.ui.my;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.MyOwnerUserInfoBean;
import com.sortinghat.funny.bean.TaskMessageBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.databinding.FragmentMyHeaderBinding;
import com.sortinghat.funny.ui.home.FeedBackActivity;
import com.sortinghat.funny.util.CommonUserInfo;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DateUtil;

public class MyFragmentHeaderView extends RelativeLayout {

    private FragmentMyHeaderBinding mBinding;
    private Context mContext;
    private int likeCount = 0;


    public MyFragmentHeaderView(Context context) {
        super(context);
        init(context);
    }

    public MyFragmentHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyFragmentHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context mContext) {
        this.mContext = mContext;
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_my_header, null);
        mBinding = DataBindingUtil.bind(rootView);
        addView(rootView);
//        StatusBarUtil.setTranslucentForImageView((Activity) mContext,mBinding.ivTop);
        setListener();

    }

    private void setTopCoverImage() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBinding.ivTopBg.getLayoutParams();
        layoutParams.width = ScreenUtils.getScreenWidth();
        layoutParams.height = ScreenUtils.getScreenWidth() * 450 / 1125;
        mBinding.ivTopBg.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) mBinding.ivTopImg.getLayoutParams();
        layoutParams1.width = ScreenUtils.getScreenWidth();
        layoutParams1.height = ScreenUtils.getScreenWidth() * 450 / 1125;
        mBinding.ivTopImg.setLayoutParams(layoutParams1);
    }

    public void initData() {
        if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
            //未登录状态
            mBinding.layoutMineLogin.setVisibility(View.GONE);
            mBinding.layoutMineNoLogin.setVisibility(View.VISIBLE);
            mBinding.nameRl.setVisibility(GONE);
            mBinding.ivMineTagSex.setImageResource(0);
            mBinding.layoutMineNoLogin.setEnabled(true);
            mBinding.txToLogin.setEnabled(true);
            mBinding.tvSlogan.setVisibility(View.GONE);
        } else {
            setTopCoverImage();
            mBinding.layoutMineLogin.setVisibility(View.VISIBLE);
            mBinding.layoutMineNoLogin.setVisibility(View.GONE);
            mBinding.nameRl.setVisibility(VISIBLE);
        }
    }

    public void updateView() {
        if (SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(Constant.ANDROID_TASK_SYS_AB, false)) {
            mBinding.taskRl.setVisibility(VISIBLE);
        } else {
            mBinding.taskRl.setVisibility(GONE);
        }

        //是否显示报告
        if (SPUtils.getInstance(Constant.SP_CONFIG_INFO).getInt(Constant.ANDROID_MOOD_REPORT, 0) == 1) {
            mBinding.layoutUserNoMoodReport.setVisibility(VISIBLE);
            mBinding.lineMoodReport.setVisibility(VISIBLE);
            mBinding.userCommonTopRl.layoutUserMoodReport.setVisibility(VISIBLE);
            mBinding.userCommonTopRl.lineMoodReport.setVisibility(VISIBLE);
        } else {
            mBinding.layoutUserNoMoodReport.setVisibility(GONE);
            mBinding.lineMoodReport.setVisibility(GONE);
            mBinding.userCommonTopRl.layoutUserMoodReport.setVisibility(GONE);
            mBinding.userCommonTopRl.lineMoodReport.setVisibility(GONE);
        }

    }

    public void setShowData(MyOwnerUserInfoBean infoBean) {
        updateView();
        if (infoBean == null) {
            initData();
        } else {
            if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                initData();
                String pendantUrl = infoBean.getUserBase().getPendantUrl();
                updateUserIcon(pendantUrl);
                mBinding.tvNoName.setText(infoBean.getUserBase().getNickname());
                CommonUserInfo.userIconImg = "";
            } else {
                String pendantUrl = infoBean.getUserBase().getPendantUrl();

                updateUserIcon(pendantUrl);
                CommonUserInfo.userIconImg = infoBean.getUserBase().getAvatar() == null ? "" : infoBean.getUserBase().getAvatar();

                mBinding.nameRl.setVisibility(VISIBLE);
                mBinding.layoutMineLogin.setVisibility(View.VISIBLE);
                mBinding.layoutMineNoLogin.setVisibility(View.GONE);
                mBinding.tvEdit.setVisibility(View.VISIBLE);

                GlideUtils.loadCircleImage(infoBean.getUserBase().getAvatar() == null ? "" : infoBean.getUserBase().getAvatar(), R.mipmap.user_icon_default, mBinding.userCommonTopRl.ivUserIcon);

                if (infoBean.getUserBase().getGender() == 1) {
                    mBinding.ivMineTagSex.setImageResource(R.mipmap.tag_boy_bg);
                } else if (infoBean.getUserBase().getGender() == 2) {
                    mBinding.ivMineTagSex.setImageResource(R.mipmap.tag_girl_bg);
                } else {
                    mBinding.ivMineTagSex.setImageResource(0);
                }
                if (!TextUtils.isEmpty(infoBean.getUserBase().getBirthday())) {
                    int year = DateUtil.getDateFromNetToProgressDay(infoBean.getUserBase().getBirthday()) / 365;
                    mBinding.tvMineTagYear.setText(year + "岁");
                } else {
                    mBinding.tvMineTagYear.setText("");
                }
                if (!TextUtils.isEmpty(infoBean.getUserBase().getSlogan())) {
                    mBinding.tvSlogan.setVisibility(View.VISIBLE);
                    mBinding.tvSlogan.setText(infoBean.getUserBase().getSlogan());
                } else {
                    mBinding.tvSlogan.setVisibility(View.GONE);
                    mBinding.tvSlogan.setText("");
                }
                nickname = infoBean.getUserBase().getNickname();
                CommonUserInfo.myName = nickname;
                mBinding.mineName.setText(infoBean.getUserBase().getNickname());

            }

            if (infoBean.getUserStatus() != null) {
                likeCount = infoBean.getUserStatus().getLikedCount();
                mBinding.tvMoodReportCount.setText(ConstantUtil.getLikeNumString(infoBean.getUserStatus().getLaughCount(), "0") + "");
                mBinding.userCommonTopRl.tvMoodReportCount.setText(ConstantUtil.getLikeNumString(infoBean.getUserStatus().getLaughCount(), "0") + "");
                mBinding.userCommonTopRl.tvLikeCount.setText(ConstantUtil.getLikeNumString(likeCount, "0") + "");
                mBinding.userCommonTopRl.tvFansCount.setText(ConstantUtil.getLikeNumString(infoBean.getUserStatus().getFansCount(), "0") + "");
                mBinding.userCommonTopRl.tvFollowCount.setText(infoBean.getUserStatus().getFollowCount() + "");
            }
        }
    }

    public void updateUserIcon(String pendantUrl) {
        CommonUserInfo.userIconImgBox = pendantUrl;
        if (!TextUtils.isEmpty(pendantUrl)) {
            mBinding.userCommonTopRl.ivBoxUserIcon.setVisibility(VISIBLE);
            mBinding.ivNoBoxUserIcon.setVisibility(VISIBLE);
            mBinding.userCommonTopRl.ivHeadBg.setVisibility(INVISIBLE);
            GlideUtils.loadImageNoPlaceholder(pendantUrl, mBinding.userCommonTopRl.ivBoxUserIcon);
            GlideUtils.loadImageNoPlaceholder(pendantUrl, mBinding.ivNoBoxUserIcon);
        } else {
            mBinding.userCommonTopRl.ivHeadBg.setVisibility(VISIBLE);
            mBinding.ivNoBoxUserIcon.setVisibility(INVISIBLE);
            mBinding.userCommonTopRl.ivBoxUserIcon.setVisibility(INVISIBLE);
        }
    }

    private TaskMessageBean taskMessageBean = new TaskMessageBean();

    public void updateTaskMessageRed(TaskMessageBean taskMessageBean) {
        this.taskMessageBean = taskMessageBean;
        int task = taskMessageBean.getTaskRed();
        int store = taskMessageBean.getStoreRed();
        int bag = taskMessageBean.getRankRed();
        if (task > 0) {
            mBinding.ivTaskCentralRed.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivTaskCentralRed.setVisibility(View.GONE);
        }
        if (store > 0) {
            mBinding.ivStoreRed.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivStoreRed.setVisibility(View.GONE);
        }
        if (bag > 0) {
            mBinding.ivPackageRed.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivPackageRed.setVisibility(View.GONE);
        }
    }

    public void setFollowCount(int followStatus) {
        int followCount = Integer.parseInt(mBinding.userCommonTopRl.tvFollowCount.getText().toString());
        if (followStatus == 0 || followStatus == 3) {
            mBinding.userCommonTopRl.tvFollowCount.setText(String.valueOf(Math.max((followCount - 1), 0)));
        } else {
            mBinding.userCommonTopRl.tvFollowCount.setText(String.valueOf(followCount + 1));
        }
    }

    private String nickname = "昵称";


    private void setListener() {
        mBinding.userCommonTopRl.rlUserIcon.setOnClickListener(quickClickListener);
        mBinding.txToLogin.setOnClickListener(quickClickListener);
        mBinding.tvEdit.setOnClickListener(quickClickListener);
        mBinding.ivSetMore.setOnClickListener(quickClickListener);
        mBinding.ivSetNoMore.setOnClickListener(quickClickListener);
        mBinding.userCommonTopRl.layoutMineFans.setOnClickListener(quickClickListener);
        mBinding.userCommonTopRl.layoutMineAttention.setOnClickListener(quickClickListener);
        mBinding.tvMyFeedback.setOnClickListener(quickClickListener);
        mBinding.tvMyFeedbackNologin.setOnClickListener(quickClickListener);
        mBinding.layoutMineNoLogin.setOnClickListener(quickClickListener);
        mBinding.mineName.setOnClickListener(quickClickListener);
        mBinding.mineSexRl.setOnClickListener(quickClickListener);
        mBinding.tvSlogan.setOnClickListener(quickClickListener);
        mBinding.userCommonTopRl.layoutUserLike.setOnClickListener(quickClickListener);
        mBinding.mineTaskRl.setOnClickListener(quickClickListener);
        mBinding.mineStoreRl.setOnClickListener(quickClickListener);
        mBinding.minePackageRl.setOnClickListener(quickClickListener);
        mBinding.userCommonTopRl.layoutUserMoodReport.setOnClickListener(quickClickListener);
        mBinding.layoutUserNoMoodReport.setOnClickListener(quickClickListener);
    }


    private QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {

            switch (v.getId()) {
                case R.id.layout_user_mood_report:
                case R.id.layout_user_no_mood_report:
                    MoodReportActivity.starActivity(mContext, "rank");
                    break;
                case R.id.layout_user_like:
                    ConstantUtil.createUmEvent("my_fragment_click_mine_like");//个人页点击个人获赞数
                    if (likeCount > 0) {
                        String s = "“" + nickname + "”" + "共获得" + likeCount + "个点赞";
                        ConstantUtil.showLikeDialog(mContext, s);
                    }
                    break;
                case R.id.mine_name:
                case R.id.mine_sex_rl:
                case R.id.tv_slogan:
                    ConstantUtil.createUmEvent("my_fragment_click_name_rl");//个人页点击个人文字信息
                case R.id.rl_user_icon:
                case R.id.tv_edit:
                    MyEditInformationActivity.starActivity(mContext, 0);
                    break;
                case R.id.tx_to_login:
                case R.id.layout_mine_no_login:
                    if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                        LoginActivity.starActivity();
                    } else {
                        CommonUtils.showLong("已登录，请检查网络重试");
                    }
                    break;
                case R.id.iv_set_more:
                case R.id.iv_set_no_more:
                    ActivityUtils.startActivity(SettingActivity.class);
                    break;
                case R.id.layout_mine_fans:
                    MyFansActivity.starActivity(mContext, 0, TextUtils.isEmpty(nickname) ? "昵称" : nickname);
                    break;
                case R.id.layout_mine_attention:
                    MyFansActivity.starActivity(mContext, 1, TextUtils.isEmpty(nickname) ? "昵称" : nickname);
                    break;
                case R.id.tv_my_feedback:
                case R.id.tv_my_feedback_nologin:
                    ActivityUtils.startActivity(FeedBackActivity.class);
                    break;
                case R.id.mine_task_rl:
                    taskMessageBean.setTaskRed(0);
                    mBinding.ivTaskCentralRed.setVisibility(View.GONE);
                    TaskCentralActivity.starActivity(mContext, "0");
                    RxBus.getDefault().postSticky(taskMessageBean);
                    break;
                case R.id.mine_store_rl:
                    taskMessageBean.setStoreRed(0);
                    mBinding.ivStoreRed.setVisibility(View.GONE);
                    StoreFunnyActivity.starActivity(mContext, "0");
                    RxBus.getDefault().postSticky(taskMessageBean);
                    break;
                case R.id.mine_package_rl:
                    taskMessageBean.setRankRed(0);
                    mBinding.ivPackageRed.setVisibility(View.GONE);
                    RankingFunnyActivity.starActivity(mContext, "rank");
                    RxBus.getDefault().postSticky(taskMessageBean);
                    break;

            }
        }
    };
}
