package com.sortinghat.funny.ui.my;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.OtherUserInfoBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.databinding.FragmentOtherHeaderBinding;
import com.sortinghat.funny.util.CommonUserInfo;
import com.sortinghat.funny.util.ConstantUtil;

public class OtherUserHeaderView extends RelativeLayout {

    private FragmentOtherHeaderBinding mBinding;
    private Context mContext;

    public OtherUserHeaderView(Context context) {
        super(context);
        init(context);
    }

    public OtherUserHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OtherUserHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context mContext) {
        this.mContext = mContext;
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_other_header, null);
        mBinding = DataBindingUtil.bind(rootView);
        addView(rootView);
        initData();
        setTopCoverImage();

    }

    public void initData() {

        //是否显示报告
        if (SPUtils.getInstance(Constant.SP_CONFIG_INFO).getInt(Constant.ANDROID_MOOD_REPORT, 0) == 1) {
            mBinding.userCommonTopRl.layoutUserMoodReport.setVisibility(VISIBLE);
            mBinding.userCommonTopRl.lineMoodReport.setVisibility(VISIBLE);
        } else {
            mBinding.userCommonTopRl.layoutUserMoodReport.setVisibility(GONE);
            mBinding.userCommonTopRl.lineMoodReport.setVisibility(GONE);
        }

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

    public void setShowData(OtherUserInfoBean infoBean) {
        try {
            if (infoBean != null) {
                if (quickClickListener != null) {
                    mBinding.userCommonTopRl.layoutUserLike.setOnClickListener(quickClickListener);
                    mBinding.tvAttention.setOnClickListener(quickClickListener);
                    mBinding.ivHeaderBack.setOnClickListener(quickClickListener);
                    mBinding.tvFeedbackOther.setOnClickListener(quickClickListener);
                    mBinding.ivSetOtherMore.setOnClickListener(quickClickListener);
                    mBinding.userCommonTopRl.ivUserIcon.setOnClickListener(quickClickListener);
                    mBinding.userCommonTopRl.layoutUserMoodReport.setOnClickListener(quickClickListener);
                }
                mBinding.layoutMineLogin.setVisibility(View.VISIBLE);
                String pendantUrl = infoBean.getPendantUrl();
                updateUserIcon(pendantUrl);
                GlideUtils.loadCircleImage(infoBean.getAvatar() == null ? "" : infoBean.getAvatar(), R.mipmap.user_icon_default, mBinding.userCommonTopRl.ivUserIcon);

                if (infoBean.getGender() == 1) {
                    mBinding.ivMineTagSex.setImageResource(R.mipmap.tag_boy_bg);
                } else if (infoBean.getGender() == 2) {
                    mBinding.ivMineTagSex.setImageResource(R.mipmap.tag_girl_bg);
                } else {
                    mBinding.ivMineTagSex.setImageResource(0);
                }

                if (infoBean.getAge() > 0) {
                    mBinding.tvMineTagYear.setText(infoBean.getAge() + "岁");
                }

                mBinding.mineName.setText(infoBean.getNickname());
                mBinding.userCommonTopRl.tvLikeCount.setText(ConstantUtil.getLikeNumString(infoBean.getLikedCount(), "0") + "");
                mBinding.userCommonTopRl.tvFansCount.setText(ConstantUtil.getLikeNumString(infoBean.getFansCount(), "0") + "");
                mBinding.userCommonTopRl.tvFollowCount.setText(infoBean.getFollowCount() + "");
                mBinding.userCommonTopRl.tvMoodReportCount.setText(ConstantUtil.getLikeNumString(infoBean.getLaughCount(), "0") + "");

                if (!TextUtils.isEmpty(infoBean.getSlogan())) {
                    mBinding.tvSlogan.setVisibility(View.VISIBLE);
                    mBinding.tvSlogan.setText(infoBean.getSlogan());
                } else {
                    mBinding.tvSlogan.setVisibility(View.GONE);
                    mBinding.tvSlogan.setText("");
                }

                setAttention(infoBean.getMutualFollow(), infoBean.getFansCount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserIcon(String pendantUrl) {
        CommonUserInfo.userIconImgBox = pendantUrl;
        if (!TextUtils.isEmpty(pendantUrl)) {
            GlideUtils.loadImageNoPlaceholder(pendantUrl, mBinding.userCommonTopRl.ivBoxUserIcon);
        }
    }

    public void setAttention(int follow, int fansCount) {
        mBinding.userCommonTopRl.tvFansCount.setText(ConstantUtil.getLikeNumString(fansCount, "") + "");
        if (follow == 1) {
            mBinding.tvAttention.setBackgroundResource(R.drawable.shape_white_bg_gray_border);
            mBinding.tvAttention.setTextColor(CommonUtils.getColor(R.color.color_333333));
            mBinding.tvAttention.setText("互相关注");
        } else if (follow == 2) {
            mBinding.tvAttention.setTextColor(CommonUtils.getColor(R.color.color_333333));
            mBinding.tvAttention.setBackgroundResource(R.drawable.shape_white_bg_gray_border);
            mBinding.tvAttention.setText("已关注");
        } else {
            mBinding.tvAttention.setBackgroundResource(R.drawable.shape_orange_bg_with_corner);
            mBinding.tvAttention.setText("关注");
            mBinding.tvAttention.setTextColor(CommonUtils.getColor(R.color.white));
        }
    }

    public void setListener(QuickClickListener quickClickListener) {
        if (quickClickListener != null) {
            this.quickClickListener = quickClickListener;
        }
    }

    private QuickClickListener quickClickListener;

}
