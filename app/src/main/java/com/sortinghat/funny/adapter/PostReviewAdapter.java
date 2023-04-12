package com.sortinghat.funny.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.ItemBean;
import com.sortinghat.funny.bean.PostReviewBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.databinding.ItemPostReviewBinding;
import com.sortinghat.funny.databinding.ItemPostReviewReplyBinding;
import com.sortinghat.funny.databinding.ItemReviewLikeOrUnlikeBinding;
import com.sortinghat.funny.ui.my.LoginActivity;
import com.sortinghat.funny.ui.my.MyOtherUserInfoActivity;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.view.CustomPopWindow;

import java.util.ArrayList;
import java.util.List;

import me.jingbin.library.ByRecyclerView;

/**
 * Created by wzy on 2021/6/28
 */
public class PostReviewAdapter extends BaseBindingAdapter<PostReviewBean, ItemPostReviewBinding> {

    private final Activity activity;

    private final long postId, authorId;

    private RequestDataCallback requestCallback;

    private ByRecyclerView likeRecyclerView, unlikeRecyclerView;

    private CustomPopWindow likePopupWindow, unLikePopupWindow;

    private int postReviewPosition, reviewReplyPosition;
    private ItemPostReviewBinding itemPostReviewBinding;
    private ItemPostReviewReplyBinding itemPostReviewReplyBinding;

    private int likeOrUnlikeType;

    public void setRequestCallback(RequestDataCallback requestCallback) {
        this.requestCallback = requestCallback;
    }

    public interface RequestDataCallback {

        void setPostReviewType(int postReviewPosition, PostReviewBean postReviewBean, int postReviewType);

        void setPostReviewType(int postReviewPosition, int reviewReplyPosition, PostReviewBean.SubCommentsBean reviewReplyBean, int postReviewType);

        void postReviewLikeOrUnlike(int postReviewPosition, int reviewReplyPosition, long postId, int likeOrUnlikeType, ItemPostReviewBinding itemPostReviewBinding, ItemPostReviewReplyBinding itemPostReviewReplyBinding);

        void deletePostReview(int postReviewPosition, int reviewReplyPosition);
    }

    public PostReviewAdapter(Activity activity, long postId, long authorId) {
        super(R.layout.item_post_review);
        this.activity = activity;
        this.postId = postId;
        this.authorId = authorId;
        initLikeOrUnlikePopupWindow();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindView(BaseBindingHolder holder, PostReviewBean postReviewBean, ItemPostReviewBinding binding, int position) {
        if (postReviewBean != null) {
            initReviewReplyAdapter(binding, position, postReviewBean.getUserId());
            setListener(binding, position);
            setData(postReviewBean, binding);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setData(PostReviewBean postReviewBean, ItemPostReviewBinding binding) {
        PostReviewBean.TinyUserBean tinyUserBean = postReviewBean.getTinyUser();
        GlideUtils.loadImage(tinyUserBean.getAvatar(), R.mipmap.user_icon_default, binding.ivUserIcon);
        ConstantUtil.glideLoadPendantUrl(tinyUserBean.getPendantUrl(), binding.ivBoxUserIcon);

//        GlideUtils.loadImage(tinyUserBean == null ? "" : tinyUserBean.getAvatar(), R.mipmap.user_icon_default, binding.ivPostReviewerHeadPortrait);
        binding.tvPostReviewerNickName.setText((tinyUserBean == null || TextUtils.isEmpty(tinyUserBean.getNickname())) ? "" : tinyUserBean.getNickname());
        if (postReviewBean.getUserId() == authorId) {
            binding.tvPostOwner.setVisibility(View.VISIBLE);
        } else {
            binding.tvPostOwner.setVisibility(View.GONE);
        }
        binding.tvPostReviewContent.setText(postReviewBean.getContent());
        binding.tvPostReviewDate.setText(TimeUtils.millis2String(postReviewBean.getCreateTime(), TimeUtils.getSafeDateFormat("yyyy-MM-dd")));
        PostReviewBean.CommentStatusBean commentStatus = postReviewBean.getCommentStatus();
        if (commentStatus != null && commentStatus.getLikeNum() > 0) {
            binding.tvPostReviewLike.setText(String.valueOf(commentStatus.getLikeNum()));
        } else {
            binding.tvPostReviewLike.setText("");
        }
        if (postReviewBean.getCurUserLikeType() == 1 || postReviewBean.getCurUserLikeType() == 3 || postReviewBean.getCurUserLikeType() == 4) {
            binding.tvPostReviewLike.setTextColor(CommonUtils.getColor(R.color.light_orange));
            binding.tvPostReviewLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_select, 0);
        } else {
            binding.tvPostReviewLike.setTextColor(CommonUtils.getColor(R.color.color_999999));
            binding.tvPostReviewLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_normal, 0);
        }
        if (postReviewBean.getCurUserLikeType() == 2 || postReviewBean.getCurUserLikeType() == 5 || postReviewBean.getCurUserLikeType() == 6) {
            binding.ivPostReviewDislike.setImageResource(R.mipmap.review_dislike_select);
        } else {
            binding.ivPostReviewDislike.setImageResource(R.mipmap.review_dislike_normal);
        }
        ((ReviewReplyAdapter) binding.recyclerView.getAdapter()).setNewData(postReviewBean.getSubComments());
        if (postReviewBean.getSubComments() == null || postReviewBean.getSubComments().isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.tvUnfoldPostReviewReply.setVisibility(View.GONE);
        } else {
            if (postReviewBean.isReviewReplyListUnfoldStatus()) {
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.tvUnfoldPostReviewReply.setText("收起");
                binding.tvUnfoldPostReviewReply.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_up_arrow, 0);
            } else {
                binding.recyclerView.setVisibility(View.GONE);
                binding.tvUnfoldPostReviewReply.setText("展开" + binding.recyclerView.getAdapter().getItemCount() + "条回复");
                binding.tvUnfoldPostReviewReply.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_down_arrow, 0);
            }
            binding.tvUnfoldPostReviewReply.setVisibility(View.VISIBLE);
        }
    }

    private void initLikeOrUnlikePopupWindow() {
        likeRecyclerView = new ByRecyclerView(activity);
        likeRecyclerView.setBackground(CommonUtils.getDrawable(R.drawable.popupwindow_post_review_like_or_unlike_bg));
        likeRecyclerView.setPadding(SizeUtils.dp2px(2), SizeUtils.dp2px(2), SizeUtils.dp2px(2), SizeUtils.dp2px(2));

        BaseBindingAdapter<ItemBean, ItemReviewLikeOrUnlikeBinding> likeAdapter = new BaseBindingAdapter<ItemBean, ItemReviewLikeOrUnlikeBinding>(R.layout.item_review_like_or_unlike, null) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void bindView(BaseBindingHolder holder, ItemBean itemBean, ItemReviewLikeOrUnlikeBinding binding, int position) {
                GlideUtils.loadGifImageFromResource(itemBean.getIconResource(), binding.ivLikeOrUnlikeType);
                binding.tvLikeOrUnlikeTypeName.setText(itemBean.getMenuName());
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        likeRecyclerView.setLayoutManager(linearLayoutManager);
        likeRecyclerView.setAdapter(likeAdapter);

        //0代表以前的小熊猫，1是新的宇航员
        int animationType = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("home_like_animation_type");
        List<ItemBean> likeList = new ArrayList<>();
        likeList.add(new ItemBean("神评", animationType == 0 ? R.drawable.home_comment_gif_hot : R.drawable.home_comment_gif_hot_new1));
        likeList.add(new ItemBean("有用", animationType == 0 ? R.drawable.home_comment_gif_have_useful : R.drawable.home_comment_gif_have_useful_new1));
        likeAdapter.setNewData(likeList);

        likeRecyclerView.setOnItemClickListener((v, position) -> {
            if (position == 0) {
                likeOrUnlikeType = 3;
            } else if (position == 1) {
                likeOrUnlikeType = 4;
            }
            likePopupWindow.dissmiss();
        });

        likePopupWindow = new CustomPopWindow.PopupWindowBuilder(activity)
                .setView(likeRecyclerView)
                .size(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOnDissmissListener(() -> {
                    if (likePopupWindow.getPopupWindow() != null && !likePopupWindow.getPopupWindow().isShowing()) {
                        if (requestCallback != null) {
                            if (reviewReplyPosition == -1 && itemPostReviewReplyBinding == null) {
                                itemPostReviewBinding.tvPostReviewLike.setEnabled(false);
                                itemPostReviewBinding.ivPostReviewDislike.setEnabled(false);
                            } else {
                                itemPostReviewReplyBinding.tvReviewReplyLike.setEnabled(false);
                                itemPostReviewReplyBinding.ivReviewReplyDislike.setEnabled(false);
                            }
                            requestCallback.postReviewLikeOrUnlike(postReviewPosition, reviewReplyPosition, postId, likeOrUnlikeType, itemPostReviewBinding, itemPostReviewReplyBinding);
                        }
                    }
                })
                .create();

        likeRecyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        unlikeRecyclerView = new ByRecyclerView(activity);
        unlikeRecyclerView.setBackground(CommonUtils.getDrawable(R.drawable.popupwindow_post_review_like_or_unlike_bg));
        unlikeRecyclerView.setPadding(SizeUtils.dp2px(2), SizeUtils.dp2px(2), SizeUtils.dp2px(2), SizeUtils.dp2px(2));

        BaseBindingAdapter<ItemBean, ItemReviewLikeOrUnlikeBinding> unLikeAdapter = new BaseBindingAdapter<ItemBean, ItemReviewLikeOrUnlikeBinding>(R.layout.item_review_like_or_unlike, null) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void bindView(BaseBindingHolder holder, ItemBean itemBean, ItemReviewLikeOrUnlikeBinding binding, int position) {
                if ("举报".equals(itemBean.getMenuName())) {
                    GlideUtils.loadImageFromResource(itemBean.getIconResource(), binding.ivLikeOrUnlikeType);
                } else {
                    GlideUtils.loadGifImageFromResource(itemBean.getIconResource(), binding.ivLikeOrUnlikeType);
                }
                binding.tvLikeOrUnlikeTypeName.setText(itemBean.getMenuName());
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        unlikeRecyclerView.setLayoutManager(layoutManager);
        unlikeRecyclerView.setAdapter(unLikeAdapter);

        List<ItemBean> unLikeList = new ArrayList<>();
        unLikeList.add(new ItemBean("不爱看", animationType == 0 ? R.drawable.home_dislike_gif_hate_look : R.drawable.home_dislike_gif_hate_look_new1));
        unLikeList.add(new ItemBean("举报", animationType == 0 ? R.mipmap.home_dislike_report : R.mipmap.home_dislike_report_new1));
        unLikeAdapter.setNewData(unLikeList);

        unlikeRecyclerView.setOnItemClickListener((v, position) -> {
            if (position == 0) {
                likeOrUnlikeType = 5;
            } else if (position == 1) {
                likeOrUnlikeType = 6;
            }
            unLikePopupWindow.dissmiss();
        });

        unLikePopupWindow = new CustomPopWindow.PopupWindowBuilder(activity)
                .setView(unlikeRecyclerView)
                .size(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOnDissmissListener(() -> {
                    if (unLikePopupWindow.getPopupWindow() != null && !unLikePopupWindow.getPopupWindow().isShowing()) {
                        if (requestCallback != null) {
                            if (reviewReplyPosition == -1 && itemPostReviewReplyBinding == null) {
                                itemPostReviewBinding.tvPostReviewLike.setEnabled(false);
                                itemPostReviewBinding.ivPostReviewDislike.setEnabled(false);
                            } else {
                                itemPostReviewReplyBinding.tvReviewReplyLike.setEnabled(false);
                                itemPostReviewReplyBinding.ivReviewReplyDislike.setEnabled(false);
                            }
                            requestCallback.postReviewLikeOrUnlike(postReviewPosition, reviewReplyPosition, postId, likeOrUnlikeType, itemPostReviewBinding, itemPostReviewReplyBinding);
                        }
                    }
                })
                .create();

        unlikeRecyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    }

    private void initReviewReplyAdapter(ItemPostReviewBinding binding, int postReviewPosition, long userId) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        ReviewReplyAdapter reviewReplyAdapter = new ReviewReplyAdapter(postReviewPosition, userId);
        binding.recyclerView.setAdapter(reviewReplyAdapter);
    }

    private class MyQuickClickListener extends QuickClickListener {

        private ItemPostReviewBinding binding;

        private int position;

        public MyQuickClickListener(ItemPostReviewBinding binding, int position) {
            this.binding = binding;
            this.position = position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onNoDoubleClick(View v) {
            PostReviewBean postReviewBean = getItemData(position);
            if (postReviewBean == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.rl_user_icon:
                    jumpMyOrOtherUserInfoPage(postReviewBean.getUserId());
                    break;
                case R.id.tv_post_reviewer_nick_name:
                    jumpMyOrOtherUserInfoPage(postReviewBean.getUserId());
                    break;
                case R.id.tv_post_review_like:
                    int likeType = postReviewBean.getCurUserLikeType();
                    if (likeType == 1 || likeType == 3 || likeType == 4) {
                        binding.tvPostReviewLike.setTextColor(CommonUtils.getColor(R.color.color_999999));
                        binding.tvPostReviewLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_normal, 0);
                        if (requestCallback != null) {
                            binding.tvPostReviewLike.setEnabled(false);
                            binding.ivPostReviewDislike.setEnabled(false);
                            likeOrUnlikeType = -1;
                            requestCallback.postReviewLikeOrUnlike(position, -1, postId, likeOrUnlikeType, binding, null);
                        }
                    } else {
                        binding.tvPostReviewLike.setTextColor(CommonUtils.getColor(R.color.light_orange));
                        binding.tvPostReviewLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_select, 0);
                        postReviewPosition = position;
                        reviewReplyPosition = -1;
                        itemPostReviewBinding = binding;
                        itemPostReviewReplyBinding = null;
                        likeOrUnlikeType = 1;
                        showLikePopupWindow(v);
                    }
                    break;
                case R.id.iv_post_review_dislike:
                    int unlikeType = postReviewBean.getCurUserLikeType();
                    if (unlikeType == 2 || unlikeType == 5 || unlikeType == 6) {
                        binding.ivPostReviewDislike.setImageResource(R.mipmap.review_dislike_normal);
                        if (requestCallback != null) {
                            binding.tvPostReviewLike.setEnabled(false);
                            binding.ivPostReviewDislike.setEnabled(false);
                            likeOrUnlikeType = 0;
                            requestCallback.postReviewLikeOrUnlike(position, -1, postId, likeOrUnlikeType, binding, null);
                        }
                    } else {
                        binding.ivPostReviewDislike.setImageResource(R.mipmap.review_dislike_select);
                        postReviewPosition = position;
                        reviewReplyPosition = -1;
                        itemPostReviewBinding = binding;
                        itemPostReviewReplyBinding = null;
                        likeOrUnlikeType = 2;
                        showUnlikePopupWindow(v);
                    }
                    break;
                case R.id.tv_unfold_post_review_reply:
                    if (binding.recyclerView.getVisibility() == View.GONE) {
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        binding.tvUnfoldPostReviewReply.setText("收起");
                        binding.tvUnfoldPostReviewReply.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_up_arrow, 0);
                        postReviewBean.setReviewReplyListUnfoldStatus(true);
                    } else {
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.tvUnfoldPostReviewReply.setText("展开" + binding.recyclerView.getAdapter().getItemCount() + "条回复");
                        binding.tvUnfoldPostReviewReply.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_down_arrow, 0);
                        postReviewBean.setReviewReplyListUnfoldStatus(false);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void setListener(ItemPostReviewBinding binding, int positions) {
        binding.rlUserIcon.setOnClickListener(new MyQuickClickListener(binding, positions));
        binding.tvPostReviewerNickName.setOnClickListener(new MyQuickClickListener(binding, positions));
        binding.tvPostReviewLike.setOnClickListener(new MyQuickClickListener(binding, positions));
        binding.ivPostReviewDislike.setOnClickListener(new MyQuickClickListener(binding, positions));
        binding.tvUnfoldPostReviewReply.setOnClickListener(new MyQuickClickListener(binding, positions));

        binding.clPostReviewItemInfo.setOnClickListener(new QuickClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                    LoginActivity.starActivity();
                    return;
                }
                PostReviewBean postReviewBean = getItemData(positions);
                if (postReviewBean == null) {
                    return;
                }
                if (requestCallback != null) {
                    requestCallback.setPostReviewType(positions, postReviewBean, 2);
                }
            }
        });

        binding.clPostReviewItemInfo.setOnLongClickListener(v -> {
            if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                LoginActivity.starActivity();
                return true;
            }
            PostReviewBean postReviewBean = getItemData(positions);
            if (postReviewBean == null) {
                return true;
            }
            if (postReviewBean.getUserId() == SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id")) {
                showOperateReviewDialog(positions, -1);
            }
            return true;
        });

        binding.recyclerView.setOnItemClickListener((v, position) -> {
            if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                LoginActivity.starActivity();
                return;
            }
            PostReviewBean.SubCommentsBean reviewReplyBean = ((ReviewReplyAdapter) binding.recyclerView.getAdapter()).getItemData(position);
            if (reviewReplyBean == null) {
                return;
            }
            if (requestCallback != null) {
                requestCallback.setPostReviewType(positions, position, reviewReplyBean, 3);
            }
        });

        binding.recyclerView.setOnItemLongClickListener((v, position) -> {
            if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                LoginActivity.starActivity();
                return true;
            }
            PostReviewBean.SubCommentsBean reviewReplyBean = ((ReviewReplyAdapter) binding.recyclerView.getAdapter()).getItemData(position);
            if (reviewReplyBean == null) {
                return true;
            }
            if (reviewReplyBean.getUserId() == SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id")) {
                showOperateReviewDialog(positions, position);
            }
            return true;
        });
    }

    private void showLikePopupWindow(View view) {
        if (unLikePopupWindow.getPopupWindow() != null && unLikePopupWindow.getPopupWindow().isShowing()) {
            unLikePopupWindow.dissmiss();
            return;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        likePopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, (location[0] + view.getWidth() / 2) - likeRecyclerView.getMeasuredWidth() / 2, location[1] - likeRecyclerView.getMeasuredHeight());
    }

    private void showUnlikePopupWindow(View view) {
        if (likePopupWindow.getPopupWindow() != null && likePopupWindow.getPopupWindow().isShowing()) {
            likePopupWindow.dissmiss();
            return;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        unLikePopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, (location[0] + view.getWidth() / 2) - unlikeRecyclerView.getMeasuredWidth() / 2, location[1] - unlikeRecyclerView.getMeasuredHeight());
    }

    private void showOperateReviewDialog(int postReviewPosition, int reviewReplyPosition) {
        MaterialDialogUtil.showList(activity, new String[]{"删除"}, (dialog, view, which, text) -> {
            if (which == 0) {
                if (requestCallback != null) {
                    requestCallback.deletePostReview(postReviewPosition, reviewReplyPosition);
                }
            }
        });
    }

    public void jumpMyOrOtherUserInfoPage(long otherUserId) {
        long myUserId = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id");
        if (otherUserId != -1L && myUserId != -1L) {
            if (otherUserId != myUserId) {
                MyOtherUserInfoActivity.starActivity(activity, otherUserId, postId);
            }
        }
    }

    public class ReviewReplyAdapter extends BaseBindingAdapter<PostReviewBean.SubCommentsBean, ItemPostReviewReplyBinding> {

        private int postReviewPositions;
        private long userId;

        public ReviewReplyAdapter(int postReviewPosition, long userId) {
            super(R.layout.item_post_review_reply);
            this.postReviewPositions = postReviewPosition;
            this.userId = userId;
        }

        @Override
        protected void bindView(BaseBindingHolder holder, PostReviewBean.SubCommentsBean reviewReplyBean, ItemPostReviewReplyBinding binding, int position) {
            if (reviewReplyBean != null) {
                setListeners(postReviewPositions, binding, reviewReplyBean, position);
                setDatas(reviewReplyBean, binding);
            }
        }

        private void setDatas(PostReviewBean.SubCommentsBean reviewReplyBean, ItemPostReviewReplyBinding binding) {
            PostReviewBean.SubCommentsBean.TinyUserBeanX tinyUserBean = reviewReplyBean.getTinyUser();
            GlideUtils.loadImage(tinyUserBean == null ? "" : tinyUserBean.getAvatar(), R.mipmap.user_icon_default, binding.ivReviewReplierHeadPortrait);
            binding.tvReviewReplierNickName.setText((tinyUserBean == null || TextUtils.isEmpty(tinyUserBean.getNickname())) ? "" : tinyUserBean.getNickname());
            if (reviewReplyBean.getReplyUserId() == userId) {
                binding.ivReviewReplyIcon.setVisibility(View.GONE);
                binding.tvReviewReviewerNickName.setVisibility(View.GONE);
                if (reviewReplyBean.getUserId() == authorId) {
                    binding.tvPostOwner.setVisibility(View.VISIBLE);
                } else {
                    binding.tvPostOwner.setVisibility(View.GONE);
                }
            } else {
                binding.tvPostOwner.setVisibility(View.GONE);
                binding.ivReviewReplyIcon.setVisibility(View.VISIBLE);
                binding.tvReviewReviewerNickName.setText(reviewReplyBean.getReplyNickname());
                binding.tvReviewReviewerNickName.setVisibility(View.VISIBLE);
            }
            binding.tvReviewReplyContent.setText(reviewReplyBean.getContent());
            binding.tvReviewReplyDate.setText(TimeUtils.millis2String(reviewReplyBean.getCreateTime(), TimeUtils.getSafeDateFormat("yyyy-MM-dd")));
            PostReviewBean.SubCommentsBean.CommentStatusBeanX commentStatus = reviewReplyBean.getCommentStatus();
            if (commentStatus != null && commentStatus.getLikeNum() > 0) {
                binding.tvReviewReplyLike.setText(String.valueOf(commentStatus.getLikeNum()));
            } else {
                binding.tvReviewReplyLike.setText("");
            }
            if (reviewReplyBean.getCurUserLikeType() == 1 || reviewReplyBean.getCurUserLikeType() == 3 || reviewReplyBean.getCurUserLikeType() == 4) {
                binding.tvReviewReplyLike.setTextColor(CommonUtils.getColor(R.color.light_orange));
                binding.tvReviewReplyLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_select, 0);
            } else {
                binding.tvReviewReplyLike.setTextColor(CommonUtils.getColor(R.color.color_999999));
                binding.tvReviewReplyLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_normal, 0);
            }
            if (reviewReplyBean.getCurUserLikeType() == 2 || reviewReplyBean.getCurUserLikeType() == 5 || reviewReplyBean.getCurUserLikeType() == 6) {
                binding.ivReviewReplyDislike.setImageResource(R.mipmap.review_dislike_select);
            } else {
                binding.ivReviewReplyDislike.setImageResource(R.mipmap.review_dislike_normal);
            }
        }

        private void setListeners(int postReviewPosition, ItemPostReviewReplyBinding binding, PostReviewBean.SubCommentsBean reviewReplyBean, int position) {
            binding.ivReviewReplierHeadPortrait.setOnClickListener(new MyQuickClickListener(postReviewPosition, binding, reviewReplyBean, position));
            binding.tvReviewReplierNickName.setOnClickListener(new MyQuickClickListener(postReviewPosition, binding, reviewReplyBean, position));
            binding.tvReviewReplyLike.setOnClickListener(new MyQuickClickListener(postReviewPosition, binding, reviewReplyBean, position));
            binding.ivReviewReplyDislike.setOnClickListener(new MyQuickClickListener(postReviewPosition, binding, reviewReplyBean, position));
        }

        private class MyQuickClickListener extends QuickClickListener {

            private ItemPostReviewReplyBinding binding;

            private PostReviewBean.SubCommentsBean reviewReplyBean;

            private int postReviewPositions, position;

            public MyQuickClickListener(int postReviewPosition, ItemPostReviewReplyBinding binding, PostReviewBean.SubCommentsBean reviewReplyBean, int position) {
                this.postReviewPositions = postReviewPosition;
                this.binding = binding;
                this.reviewReplyBean = reviewReplyBean;
                this.position = position;
            }

            @SuppressLint("NonConstantResourceId")
            @Override
            protected void onNoDoubleClick(View v) {
                if (reviewReplyBean == null) {
                    return;
                }
                switch (v.getId()) {
                    case R.id.iv_review_replier_head_portrait:
                        jumpMyOrOtherUserInfoPage(reviewReplyBean.getUserId());
                        break;
                    case R.id.tv_review_replier_nick_name:
                        jumpMyOrOtherUserInfoPage(reviewReplyBean.getUserId());
                        break;
                    case R.id.tv_review_reply_like:
                        int likeType = reviewReplyBean.getCurUserLikeType();
                        if (likeType == 1 || likeType == 3 || likeType == 4) {
                            binding.tvReviewReplyLike.setTextColor(CommonUtils.getColor(R.color.color_999999));
                            binding.tvReviewReplyLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_normal, 0);
                            if (requestCallback != null) {
                                binding.tvReviewReplyLike.setEnabled(false);
                                binding.ivReviewReplyDislike.setEnabled(false);
                                likeOrUnlikeType = -1;
                                requestCallback.postReviewLikeOrUnlike(postReviewPositions, position, postId, likeOrUnlikeType, null, binding);
                            }
                        } else {
                            binding.tvReviewReplyLike.setTextColor(CommonUtils.getColor(R.color.light_orange));
                            binding.tvReviewReplyLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_select, 0);
                            postReviewPosition = postReviewPositions;
                            reviewReplyPosition = position;
                            itemPostReviewBinding = null;
                            itemPostReviewReplyBinding = binding;
                            likeOrUnlikeType = 1;
                            showLikePopupWindow(v);
                        }
                        break;
                    case R.id.iv_review_reply_dislike:
                        int unlikeType = reviewReplyBean.getCurUserLikeType();
                        if (unlikeType == 2 || unlikeType == 5 || unlikeType == 6) {
                            binding.ivReviewReplyDislike.setImageResource(R.mipmap.review_dislike_normal);
                            if (requestCallback != null) {
                                binding.tvReviewReplyLike.setEnabled(false);
                                binding.ivReviewReplyDislike.setEnabled(false);
                                likeOrUnlikeType = 0;
                                requestCallback.postReviewLikeOrUnlike(postReviewPositions, position, postId, likeOrUnlikeType, null, binding);
                            }
                        } else {
                            binding.ivReviewReplyDislike.setImageResource(R.mipmap.review_dislike_select);
                            postReviewPosition = postReviewPositions;
                            reviewReplyPosition = position;
                            itemPostReviewBinding = null;
                            itemPostReviewReplyBinding = binding;
                            likeOrUnlikeType = 2;
                            showUnlikePopupWindow(v);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
