package com.sortinghat.funny.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.bean.ItemBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.databinding.ItemHomePostLikeBinding;
import com.sortinghat.funny.databinding.ViewControllerBinding;
import com.sortinghat.funny.thirdparty.video.OnVideoControllerListener;
import com.sortinghat.funny.util.CommonUserInfo;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.business.TopicIdentifyUtil;

import java.util.ArrayList;
import java.util.List;

public class ControllerView extends RelativeLayout {

    private ViewControllerBinding mBinding;

    private OnVideoControllerListener listener;
    private HomeVideoImageListBean.ListBean.ContentBean videoData;

    public void setOnVideoControllerListener(OnVideoControllerListener listener) {
        this.listener = listener;
    }

    public ControllerView(Context context) {
        super(context);
        init(context);
    }

    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.view_controller, null);
        mBinding = DataBindingUtil.bind(rootView);
        addView(rootView);
        initPostLikeView(context);
        setListener();
    }

    private void initPostLikeView(Context context) {
        BaseBindingAdapter<ItemBean, ItemHomePostLikeBinding> likeAdapter = new BaseBindingAdapter<ItemBean, ItemHomePostLikeBinding>(R.layout.item_home_post_like, null) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void bindView(BaseBindingHolder holder, ItemBean itemBean, ItemHomePostLikeBinding binding, int position) {
                GlideUtils.loadImageFromResource(itemBean.getIconResource(), binding.ivHomePostLike);
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(likeAdapter);

        List<ItemBean> likeList = new ArrayList<>();
        likeList.add(new ItemBean("", R.mipmap.home_like_later_see));
        likeList.add(new ItemBean("", R.mipmap.home_like_touch_heart));
        likeList.add(new ItemBean("", R.mipmap.home_like_touch_cry));
        likeList.add(new ItemBean("", R.mipmap.home_like_love_see));
        likeList.add(new ItemBean("", R.mipmap.home_like_laugh_out));
        likeAdapter.setNewData(likeList);

        mBinding.recyclerView.setOnItemClickListener((v, position) -> {
            if (listener != null) {
                ImageView ivFeelLike = null;
                RecyclerView.ViewHolder viewHolder = mBinding.recyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null) {
                    BaseBindingHolder<ItemBean, ItemHomePostLikeBinding> bindingViewHolder = (BaseBindingHolder<ItemBean, ItemHomePostLikeBinding>) viewHolder;
                    if (bindingViewHolder.binding != null) {
                        ivFeelLike = bindingViewHolder.binding.ivHomePostLike;
                    }
                }

                int likeType = 0;
                switch (position) {
                    case 0:
                        likeType = 2;
                        break;
                    case 1:
                        likeType = 3;
                        break;
                    case 2:
                        likeType = 4;
                        break;
                    case 3:
                        likeType = 5;
                        break;
                    case 4:
                        likeType = 6;
                        break;
                    default:
                        break;
                }

                listener.onFeelLikeClick(ivFeelLike, likeType);
            }
        });
    }

    // 1 like 2;dislike
    public void showBeautifulBig(int type, int liketype) {
        if (type == 1 && (liketype < 2)) {
            return;
        }
        if (type == 2 && (liketype < 2)) {
            return;
        }
        //0代表以前的小熊猫，1是新的宇航员
        int animationType = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("home_like_animation_type");
        int drawable = R.drawable.home_like_gif_look_after;
        if (type == 1) {
            if (animationType == 0) {
                switch (liketype) {
                    case 1:
                        drawable = R.drawable.home_like_gif_look_after;
                        break;
                    case 2:
                        drawable = R.drawable.home_like_gif_look_after;
                        break;
                    case 3:
                        drawable = R.drawable.home_like_gif_deep_heart;
                        break;
                    case 4:
                        drawable = R.drawable.home_like_gif_thanks_cry;
                        break;
                    case 5:
                        drawable = R.drawable.home_like_gif_very_like;
                        break;
                    case 6:
                        drawable = R.drawable.home_like_gif_laugh_up;
                        break;
                    default:
                        break;
                }
            } else {
                switch (liketype) {
                    case 1:
                        drawable = R.drawable.home_like_gif_look_after_new1;
                        break;
                    case 2:
                        drawable = R.drawable.home_like_gif_look_after_new1;
                        break;
                    case 3:
                        drawable = R.drawable.home_like_gif_deep_heart_new1;
                        break;
                    case 4:
                        drawable = R.drawable.home_like_gif_thanks_cry_new1;
                        break;
                    case 5:
                        drawable = R.drawable.home_like_gif_very_like_new1;
                        break;
                    case 6:
                        drawable = R.drawable.home_like_gif_laugh_up_new1;
                        break;
                    default:
                        break;
                }
            }

        } else {
            if (animationType == 0) {
                switch (liketype) {
                    case 1:
                        drawable = R.drawable.home_dislike_gif_forbid_topics;
                        break;
                    case 2:
                        drawable = R.drawable.home_dislike_gif_hate_look;
                        break;
                    case 3:
                        drawable = R.drawable.home_dislike_gif_forbid_author;
                        break;
                    case 4:
                        drawable = R.drawable.home_dislike_gif_forbid_topics;
                        break;
                    case 5:
                        drawable = R.drawable.home_dislike_gif_forbid_topics;
                        break;
                    default:
                        break;
                }
            } else {
                switch (liketype) {
                    case 1:
                        drawable = R.drawable.home_dislike_gif_forbid_topics_new1;
                        break;
                    case 2:
                        drawable = R.drawable.home_dislike_gif_hate_look_new1;
                        break;
                    case 3:
                        drawable = R.drawable.home_dislike_gif_forbid_author_new1;
                        break;
                    case 4:
                        drawable = R.drawable.home_dislike_gif_forbid_topics_new1;
                        break;
                    case 5:
                        drawable = R.drawable.home_dislike_gif_forbid_topics_new1;
                        break;
                    default:
                        break;
                }
            }
        }
        if (drawable == R.drawable.home_dislike_gif_forbid_topics_new1) {
            GlideUtils.loadImageFromResource(drawable, mBinding.ivBeautifulBig);
        } else {
            GlideUtils.loadGifImageFromResource(drawable, mBinding.ivBeautifulBig);
        }

        mBinding.ivBeautifulBig.setVisibility(VISIBLE);
        try {
            new Handler().postDelayed(() -> mBinding.ivBeautifulBig.setVisibility(GONE), animationType == 0 ? 2500 : 1600);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setControllerVideoData(HomeVideoImageListBean.ListBean.ContentBean videoData) {
        if (videoData == null) {
            return;
        }
        this.videoData = videoData;
        setControllerVideoData(videoData, false);
    }

    // isNeedUpdate  是否需要属性，比如用户图像在点赞后就没有必要刷新
    public void setControllerVideoData(HomeVideoImageListBean.ListBean.ContentBean videoData, boolean isNeedUpdate) {
        if (videoData == null) {
            return;
        }
        this.videoData = videoData;
        if (isNeedUpdate) {
            GlideUtils.loadImage(videoData.getAvatar() == null ? "" : videoData.getAvatar(), R.mipmap.user_icon_default, mBinding.ivUserIcon);
            if (!TextUtils.isEmpty(videoData.getPendantUrl())) {
//                GlideUtils.loadImageNoPlaceholder(videoData.getPendantUrl() == null ? "" : videoData.getPendantUrl(), mBinding.ivBoxUserIcon);
                GlideUtils.loadImageNoPlaceholder(videoData.getPendantUrl() == null ? "" : videoData.getPendantUrl(), mBinding.ivBoxUserIcon);
                mBinding.ivHeadBg.setVisibility(VISIBLE);
                mBinding.ivBoxUserIcon.setVisibility(VISIBLE);
            } else {
                mBinding.ivHeadBg.setVisibility(VISIBLE);
                mBinding.ivBoxUserIcon.setVisibility(INVISIBLE);
            }
//            GlideUtils.loadImageNoPlaceholder(CommonUserInfo.userIconImgBox, mBinding.ivBoxUserIcon);
//            mBinding.ivHeadBg.setVisibility(INVISIBLE);
        }
        mBinding.tvNickname.setText(TextUtils.isEmpty(videoData.getNickname()) ? "" : "@" + videoData.getNickname());
        TopicIdentifyUtil.identifyPostTitleTopic(R.color.white, videoData, mBinding.tvTitle);

        setFollowStatus(videoData);

        setLikeOrUnlikeOrReview(videoData);
    }

    public void setLikeOrUnlikeOrReview(HomeVideoImageListBean.ListBean.ContentBean videoData) {
        if (videoData == null) {
            return;
        }
        if (videoData.getLikeType() > 0) {
            mBinding.ivLike.setImageResource(R.mipmap.home_like_click);
        } else {
            mBinding.ivLike.setImageResource(R.mipmap.home_like);
        }
        if (videoData.getDisLikeType() > 0) {
            mBinding.ivDislike.setImageResource(R.mipmap.home_dislike_click);
        } else {
            mBinding.ivDislike.setImageResource(R.mipmap.home_dislike);
        }
        mBinding.likeCount.setText(ConstantUtil.getLikeNumString(videoData.getLikeCount(), "赞"));
        mBinding.tvCommentcount.setText(ConstantUtil.getLikeNumString(videoData.getReplyCount(), "评论"));
    }

    public void setFollowStatus(HomeVideoImageListBean.ListBean.ContentBean videoData) {
        if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id") == videoData.getAuthorId()) {
            mBinding.ivAdd.setVisibility(GONE);
        } else if (videoData.getFollowStatus() == 0 || videoData.getFollowStatus() == 3) {
            mBinding.ivAdd.setVisibility(VISIBLE);
            mBinding.ivAdd.setImageResource(R.mipmap.home_focus_on);
        } else {
            mBinding.ivAdd.setVisibility(GONE);
        }
    }

    public void showTopBg(boolean show) {
        if (show) {
            mBinding.topBg.setVisibility(VISIBLE);
        } else {
            mBinding.topBg.setVisibility(GONE);
        }
    }

    ////审核状态：0-待审核，1-审核通过，2-驳回
    public void showLikeOrDisLike(int applyStatus, long userId, int tabType) {
        if (tabType == 0) {
            int applyHeight = SizeUtils.dp2px(80);
            int viewMarginBottom = SizeUtils.dp2px(180);//右边view距离下放的
            if (applyStatus == 2) {
                mBinding.rlLike.setVisibility(GONE);
                mBinding.rlDislike.setVisibility(GONE);
                mBinding.rlComment.setVisibility(GONE);
                mBinding.rlShare.setVisibility(GONE);
                mBinding.ivPostReject.setVisibility(VISIBLE);
                applyHeight = SizeUtils.dp2px(90);
                viewMarginBottom = SizeUtils.dp2px(180);
            } else {
                mBinding.rlLike.setVisibility(VISIBLE);
                mBinding.rlDislike.setVisibility(VISIBLE);
                mBinding.rlComment.setVisibility(VISIBLE);
                mBinding.rlShare.setVisibility(VISIBLE);
                mBinding.ivShare.setImageResource(R.mipmap.share_apply_del);
                mBinding.shareCount.setVisibility(INVISIBLE);
                mBinding.ivPostReject.setVisibility(GONE);
                applyHeight = 0;
                viewMarginBottom = 0;
            }

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.applyShow.getLayoutParams();
            params.height = applyHeight;
            mBinding.applyShow.setLayoutParams(params);

            LinearLayout.LayoutParams paramsRight = (LinearLayout.LayoutParams) mBinding.controllerInfoRight.getLayoutParams();
            params.bottomMargin = viewMarginBottom;
            mBinding.controllerInfoRight.setLayoutParams(paramsRight);
        }
    }

    public void showHotComment(HomeVideoImageListBean.ListBean.HotCommentBean hotCommentBean) {
        if (hotCommentBean != null && hotCommentBean.getIsHot() == 1 && !isShowInfo) {//当全屏时不显示
            hotCommentBean.setIsHot(0);
            mBinding.hotCommentLl.setVisibility(VISIBLE);
            mBinding.txHotCommentName.setText(hotCommentBean.getHotName());
            mBinding.txHotCommentContent.setText(hotCommentBean.getHotContent());
            mBinding.txHotCommentNum.setText(hotCommentBean.getHotLikeSum() + "赞");
            GlideUtils.loadImage(hotCommentBean.getHotUserIcon() == null ? "" : hotCommentBean.getHotUserIcon(), R.mipmap.icon, mBinding.ivHotCommentIcon);
            try {
                new Handler().postDelayed(() -> mBinding.hotCommentLl.setVisibility(GONE), 3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showShareAnimation(boolean startOrEnd) {
        if (videoData == null || null == mBinding.ivShare) {
            return;
        }
        if (videoData.getApplyStatus() != 1) {
            mBinding.ivShare.clearAnimation();
            return;
        }
        if (startOrEnd) {
            try {
                int duration = 700;
                AnimationSet setAnimation = new AnimationSet(true);
                setAnimation.setRepeatMode(Animation.REVERSE);
                Animation scale1 = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scale1.setDuration(duration);
                scale1.setRepeatCount(Animation.INFINITE);
                setAnimation.addAnimation(scale1);
                setAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mBinding.ivShare.setImageResource(R.mipmap.share_animation_wx_bg);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
//                        mBinding.ivShare.setImageResource(R.mipmap.home_share);//end容易照成void android.view.animation.Animation.cancel()' on a null object reference
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                if (setAnimation != null)
                    mBinding.ivShare.startAnimation(setAnimation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mBinding.ivShare.setImageResource(R.mipmap.home_share);
            mBinding.ivShare.clearAnimation();
        }
    }

    //0 : 原位置 1：滑动 2：手指离开过程
    public void setScrollAlpha(int state) {
        if (state == 1 && mBinding.controllerInfoRight.getVisibility() == VISIBLE && mBinding.controllerInfoBottom.getVisibility() == VISIBLE) {
            //初始化
            Animation alphaAnimation = new AlphaAnimation(1.0f, 0.2f);
            alphaAnimation.setDuration(500);
            alphaAnimation.setFillAfter(true);
            mBinding.controllerInfoRight.startAnimation(alphaAnimation);
            mBinding.controllerInfoBottom.startAnimation(alphaAnimation);
        } else {
            mBinding.controllerInfoRight.clearAnimation();
            mBinding.controllerInfoBottom.clearAnimation();
        }
//        Log.e("viewpagerScroll-", "state:" + state);
    }


    private boolean isShowInfo;

    public void showControllerInfoToNorman(boolean isShowInfo1) {
        isShowInfo = isShowInfo1;
        mBinding.controllerInfoBottom.setVisibility(VISIBLE);
        mBinding.controllerInfoRight.setVisibility(VISIBLE);
//        showShareAnimation(false);
    }

    public void showControllerInfo() {
        isShowInfo = !isShowInfo;
        if (!isShowInfo) {
            if ("landScape".equals(SPUtils.getInstance(Constant.SP_CONFIG_INFO).getString("post_like_strategy", "panda"))) {
                mBinding.recyclerView.setVisibility(VISIBLE);
            }
            mBinding.controllerInfoBottom.setVisibility(VISIBLE);
            mBinding.controllerInfoRight.setVisibility(VISIBLE);
            mBinding.ivFull.setImageResource(R.mipmap.home_to_full);
        } else {
            mBinding.controllerInfoBottom.setVisibility(GONE);
            mBinding.controllerInfoRight.setVisibility(GONE);
            mBinding.recyclerView.setVisibility(GONE);
            mBinding.ivFull.setImageResource(R.mipmap.home_to_full_close);
        }
    }

    public void showSeekBarListener(boolean isStart) {
        if (!isStart && !isShowInfo) {
            if ("landScape".equals(SPUtils.getInstance(Constant.SP_CONFIG_INFO).getString("post_like_strategy", "panda"))) {
                mBinding.recyclerView.setVisibility(VISIBLE);
            }
            mBinding.controllerInfoBottom.setVisibility(VISIBLE);
            mBinding.controllerInfoRight.setVisibility(VISIBLE);
        } else {
            mBinding.controllerInfoBottom.setVisibility(GONE);
            mBinding.controllerInfoRight.setVisibility(GONE);
            mBinding.recyclerView.setVisibility(GONE);
        }
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            if (listener == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.iv_user_icon:
                    listener.onHeadClick();
                    break;
                case R.id.ivAdd:
                    listener.onAddClick();
                    break;
                case R.id.rlDislike:
                    listener.onDislikeClick();
                    break;
                case R.id.rlLike:
                    listener.onLikeClick();
                    break;
                case R.id.rlShare:
                    listener.onShareClick();
                    break;
                case R.id.ivReward:
                    listener.onRewardClick();
                    break;
                case R.id.hot_comment_ll:
                case R.id.rlComment:
                    listener.onCommentClick();
                    break;
                case R.id.ivPostReject:
                    listener.onShareClick();
                    break;
                case R.id.llFull:
                    showControllerInfo();
                    break;
                default:
                    break;
            }
        }
    };

    private void setListener() {
        mBinding.hotCommentLl.setOnClickListener(quickClickListener);
        mBinding.ivUserIcon.setOnClickListener(quickClickListener);
        mBinding.ivAdd.setOnClickListener(quickClickListener);
        mBinding.rlLike.setOnClickListener(quickClickListener);
        mBinding.rlDislike.setOnClickListener(quickClickListener);
        mBinding.rlShare.setOnClickListener(quickClickListener);
        mBinding.rlComment.setOnClickListener(quickClickListener);
        mBinding.ivReward.setOnClickListener(quickClickListener);
        mBinding.ivPostReject.setOnClickListener(quickClickListener);
        mBinding.llFull.setOnClickListener(quickClickListener);
    }

    public ViewControllerBinding getControllerViewBinding() {
        return mBinding;
    }
}
