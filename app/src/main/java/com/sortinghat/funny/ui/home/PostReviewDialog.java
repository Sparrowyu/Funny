package com.sortinghat.funny.ui.home;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.PostReviewAdapter;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.bean.PostReviewBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.DialogPostReviewBinding;
import com.sortinghat.funny.databinding.ItemPostReviewBinding;
import com.sortinghat.funny.databinding.ItemPostReviewReplyBinding;
import com.sortinghat.funny.ui.my.LoginActivity;
import com.sortinghat.funny.util.business.BusinessUtils;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.funny.util.business.TopicIdentifyUtil;
import com.sortinghat.funny.view.BaseBottomSheetDialog;
import com.sortinghat.funny.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import me.jingbin.library.ByRecyclerView;

/**
 * 2021/7/9
 * 帖子评论弹框
 */
public class PostReviewDialog extends BaseBottomSheetDialog implements PostReviewAdapter.RequestDataCallback {

    private DialogPostReviewBinding dialogPostReviewBinding;
    private ImageView likeOrUnlikeAnimationView;
    private PostReviewAdapter postReviewAdapter;

    private HomeViewModel viewModel;

    private int tab;

    private HomeVideoImageListBean.ListBean.ContentBean videoOrImageContent;

    /**
     * 帖子评论类型：1帖子的评论 2帖子评论的回复 3帖子评论回复的回复
     */
    private int postReviewType = 1;
    private int fromPage = 1;// 1首页 2：其他页面

    private PostReviewBean clickPostReviewInfo;
    private PostReviewBean.SubCommentsBean clickReviewReplyInfo;
    private int clickPostReviewPosition, clickReviewReplyPosition;

    public PostReviewDialog() {

    }

    public PostReviewDialog(HomeViewModel viewModel, int tab, HomeVideoImageListBean.ListBean videoOrImageInfo, int fromPage) {
        super();
        this.viewModel = viewModel;
        this.tab = tab;
        this.fromPage = fromPage;
        if (videoOrImageInfo != null) {
            videoOrImageContent = videoOrImageInfo.getContent();
        }
    }

    @Override
    public int height() {
        return (int) (ScreenUtils.getScreenHeight() * 0.75);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogPostReviewBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_post_review, null, false);
        return dialogPostReviewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initViews();
        setListener();
        initData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addReviewLikeOrUnlikeAnimationView();
    }

    private void initViews() {
        initAdapter();
    }

    @Override
    public void setStyle(int type) {
        super.setStyle(2);
    }

    private void addReviewLikeOrUnlikeAnimationView() {
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            likeOrUnlikeAnimationView = new ImageView(getActivity());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(SizeUtils.dp2px(120), SizeUtils.dp2px(120));
            layoutParams.gravity = Gravity.CENTER;
            likeOrUnlikeAnimationView.setLayoutParams(layoutParams);
            likeOrUnlikeAnimationView.setVisibility(View.GONE);

            FrameLayout frameLayout = dialog.getDelegate().findViewById(R.id.container);
            frameLayout.addView(likeOrUnlikeAnimationView);
        }
    }

    private void initAdapter() {
        dialogPostReviewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postReviewAdapter = new PostReviewAdapter(getActivity(), videoOrImageContent == null ? -1L : videoOrImageContent.getPostId(), videoOrImageContent == null ? -1L : videoOrImageContent.getAuthorId());
        postReviewAdapter.setRequestCallback(this);
        dialogPostReviewBinding.recyclerView.setAdapter(postReviewAdapter);
        if (viewModel != null) {
            viewModel.setPageNumber(1);
            viewModel.setPageSize(24);
        }
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.iv_close_review_dialog:
                    dismiss();
                    break;
                case R.id.rl_user_icon:
                    postReviewAdapter.jumpMyOrOtherUserInfoPage(videoOrImageContent == null ? -1L : videoOrImageContent.getAuthorId());
                    break;
                case R.id.tv_post_owner_nick_name:
                    postReviewAdapter.jumpMyOrOtherUserInfoPage(videoOrImageContent == null ? -1L : videoOrImageContent.getAuthorId());
                    break;
                case R.id.tv_review_follow:
                    if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                        LoginActivity.starActivity(fromPage == 1 ? 2 : 0);
                        return;
                    }
                    if (videoOrImageContent != null) {
                        dialogPostReviewBinding.tvReviewFollow.setEnabled(false);

                        int followStatus = videoOrImageContent.getFollowStatus();
                        if (followStatus == 0) {
                            followStatus = 2;
                        } else if (followStatus == 1) {
                            followStatus = 3;
                        } else if (followStatus == 2) {
                            followStatus = 0;
                        } else if (followStatus == 3) {
                            followStatus = 1;
                        }
                        BusinessUtils.setFollowStatus(followStatus, dialogPostReviewBinding.tvReviewFollow);

                        addFollow(videoOrImageContent.getAuthorId(), followStatus);
                    }
                    break;
                case R.id.iv_share:
                    break;
                case R.id.tv_send:
                    dialogPostReviewBinding.llCommonInputBox.tvSend.setEnabled(false);
                    String reviewContent = dialogPostReviewBinding.llCommonInputBox.etInputBox.getText().toString();
                    publishReview(reviewContent);
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    private void setListener() {
        dialogPostReviewBinding.ivCloseReviewDialog.setOnClickListener(quickClickListener);
        dialogPostReviewBinding.rlUserIcon.setOnClickListener(quickClickListener);
        dialogPostReviewBinding.tvPostOwnerNickName.setOnClickListener(quickClickListener);
        dialogPostReviewBinding.tvReviewFollow.setOnClickListener(quickClickListener);
        dialogPostReviewBinding.llCommonInputBox.ivShare.setOnClickListener(quickClickListener);
        dialogPostReviewBinding.llCommonInputBox.tvSend.setOnClickListener(quickClickListener);

        dialogPostReviewBinding.llEmptyView.setOnClickListener(() -> getPostReview());

        dialogPostReviewBinding.llCommonInputBox.etInputBox.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0) {
                    LoginActivity.starActivity(fromPage == 1 ? 2 : 0);
                    return true;
                }
            }
            return false;
        });

        dialogPostReviewBinding.llCommonInputBox.etInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 100) {
                    CommonUtils.showShort("评论内容不能超过100个字");
                }
                if (s.length() > 0) {
                    dialogPostReviewBinding.llCommonInputBox.tvSend.setTextColor(CommonUtils.getColor(R.color.light_orange));
                    dialogPostReviewBinding.llCommonInputBox.tvSend.setEnabled(true);
                } else {
                    dialogPostReviewBinding.llCommonInputBox.tvSend.setTextColor(CommonUtils.getColor(R.color.color_999999));
                    dialogPostReviewBinding.llCommonInputBox.tvSend.setEnabled(false);
                }
            }
        });

        KeyboardUtils.registerSoftInputChangedListener(getDialog().getWindow(), height -> {
            if (height > 0) {
                //键盘显示
                int keyboardViewHeight = (int) (height - ScreenUtils.getScreenHeight() * 0.25 + BarUtils.getStatusBarHeight() + SizeUtils.dp2px(40));
                if (dialogPostReviewBinding.llCommonInputBox.keyboardView.getHeight() != keyboardViewHeight) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dialogPostReviewBinding.llCommonInputBox.keyboardView.getLayoutParams();
                    layoutParams.height = keyboardViewHeight;
                    dialogPostReviewBinding.llCommonInputBox.keyboardView.setLayoutParams(layoutParams);
                }
                dialogPostReviewBinding.llCommonInputBox.keyboardView.setVisibility(View.VISIBLE);
                dialogPostReviewBinding.llCommonInputBox.ivShare.setVisibility(View.GONE);
                dialogPostReviewBinding.llCommonInputBox.tvSend.setVisibility(View.VISIBLE);
            } else {
                //键盘隐藏
                dialogPostReviewBinding.llCommonInputBox.keyboardView.setVisibility(View.GONE);
                String reviewContent = dialogPostReviewBinding.llCommonInputBox.etInputBox.getText().toString();
                if (TextUtils.isEmpty(reviewContent)) {
                    resetReviewStatus();
                }
            }
        });
        dialogPostReviewBinding.recyclerView.setOnLoadMoreListener(new ByRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!NetworkUtils.isConnected()) {
                    dialogPostReviewBinding.recyclerView.loadMoreFail();
                    ToastUtils.showLong(R.string.network_connect_fail_prompt);
                    return;
                }
                if (!isLoadMoreFail) {
                    viewModel.setPageNumber(viewModel.getPageNumber() + 1);
                }
                getPostReview();
            }
        });
    }

    private boolean isLoadMoreFail = false;

    private void resetReviewStatus() {
        postReviewType = 1;
        dialogPostReviewBinding.llCommonInputBox.etInputBox.clearFocus();
        dialogPostReviewBinding.llCommonInputBox.etInputBox.setHint(R.string.post_review_hint);
        dialogPostReviewBinding.llCommonInputBox.tvSend.setVisibility(View.GONE);
        dialogPostReviewBinding.llCommonInputBox.ivShare.setVisibility(View.VISIBLE);
    }

    private void initData() {
        setPostBaseInfo();
        dialogPostReviewBinding.llEmptyView.dealRequestData();
        getPostReview();
    }

    @SuppressLint("SetTextI18n")
    private void setPostBaseInfo() {
        if (videoOrImageContent != null) {
            GlideUtils.loadImage(videoOrImageContent.getAvatar(), R.mipmap.user_icon_default, dialogPostReviewBinding.ivUserIcon);
            if (!TextUtils.isEmpty(videoOrImageContent.getPendantUrl())) {
                GlideUtils.loadImageNoPlaceholder(videoOrImageContent.getPendantUrl(), dialogPostReviewBinding.ivBoxUserIcon);
            }
            if (!TextUtils.isEmpty(videoOrImageContent.getNickname())) {
                dialogPostReviewBinding.tvPostOwnerNickName.setText(videoOrImageContent.getNickname());
            } else {
                dialogPostReviewBinding.tvPostOwnerNickName.setVisibility(View.GONE);
            }
            dialogPostReviewBinding.tvPostCreateDate.setText(TimeUtils.millis2String(videoOrImageContent.getCreatedAt(), TimeUtils.getSafeDateFormat("yyyy-MM-dd")));
            long myUserId = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id");
            if (videoOrImageContent.getAuthorId() != -1L && myUserId != -1L) {
                if (videoOrImageContent.getAuthorId() != myUserId) {
                    BusinessUtils.setFollowStatus(videoOrImageContent.getFollowStatus(), dialogPostReviewBinding.tvReviewFollow);
                    dialogPostReviewBinding.tvReviewFollow.setVisibility(View.VISIBLE);
                }
            }
            TopicIdentifyUtil.identifyPostTitleTopic(R.color.color_333333, videoOrImageContent, dialogPostReviewBinding.tvPostTitle);
            if (videoOrImageContent.getLikeType() != 0) {
                dialogPostReviewBinding.tvPostLike.setTextColor(CommonUtils.getColor(R.color.light_orange));
                dialogPostReviewBinding.tvPostLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_select, 0);
            }
            if (videoOrImageContent.getLikeCount() > 0) {
                dialogPostReviewBinding.tvPostLike.setText(String.valueOf(videoOrImageContent.getLikeCount()));
            }
            if (videoOrImageContent.getDisLikeType() != 0) {
                dialogPostReviewBinding.ivPostDislike.setImageResource(R.mipmap.review_dislike_select);
            }
            if (videoOrImageContent.getReplyCount() > 0) {
                dialogPostReviewBinding.tvPostReviewNumber.setText(videoOrImageContent.getReplyCount() + "条");
                dialogPostReviewBinding.tvPostReviewNumber.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addFollow(long authorId, int followStatus) {
        if (viewModel == null) {
            return;
        }
        viewModel.getUserFollowList(authorId, (followStatus == 1 || followStatus == 2) ? 1 : 0).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    videoOrImageContent.setFollowStatus(followStatus);
                    RxBus.getDefault().post(RxCodeConstant.HOME_VIDEO_UPDATA_AUTHOR_FOLLOW, authorId + "," + followStatus);
                } else {
                    BusinessUtils.setFollowStatus(videoOrImageContent.getFollowStatus(), dialogPostReviewBinding.tvReviewFollow);
                }
            } else {
                BusinessUtils.setFollowStatus(videoOrImageContent.getFollowStatus(), dialogPostReviewBinding.tvReviewFollow);
            }
            dialogPostReviewBinding.tvReviewFollow.setEnabled(true);
        });
    }

    @Override
    public void setPostReviewType(int postReviewPosition, PostReviewBean postReviewBean, int postReviewType) {
        this.postReviewType = postReviewType;
        clickPostReviewPosition = postReviewPosition;
        clickPostReviewInfo = postReviewBean;
        dialogPostReviewBinding.llCommonInputBox.etInputBox.setHint("回复 @" + ((postReviewBean.getTinyUser() == null || TextUtils.isEmpty(postReviewBean.getTinyUser().getNickname())) ? "" : postReviewBean.getTinyUser().getNickname()) + " :");
        KeyboardUtils.showSoftInput(dialogPostReviewBinding.llCommonInputBox.etInputBox);
    }

    @Override
    public void setPostReviewType(int postReviewPosition, int reviewReplyPosition, PostReviewBean.SubCommentsBean reviewReplyBean, int postReviewType) {
        this.postReviewType = postReviewType;
        clickPostReviewInfo = postReviewAdapter.getItemData(postReviewPosition);
        clickPostReviewPosition = postReviewPosition;
        clickReviewReplyInfo = reviewReplyBean;
        clickReviewReplyPosition = reviewReplyPosition;
        dialogPostReviewBinding.llCommonInputBox.etInputBox.setHint("回复 @" + ((reviewReplyBean.getTinyUser() == null || TextUtils.isEmpty(reviewReplyBean.getTinyUser().getNickname())) ? "" : reviewReplyBean.getTinyUser().getNickname()) + " :");
        KeyboardUtils.showSoftInput(dialogPostReviewBinding.llCommonInputBox.etInputBox);
    }

    @Override
    public void postReviewLikeOrUnlike(int postReviewPosition, int reviewReplyPosition, long postId, int likeOrUnlikeType, ItemPostReviewBinding itemPostReviewBinding, ItemPostReviewReplyBinding itemPostReviewReplyBinding) {
        long commentId = -1L;
        if (reviewReplyPosition == -1 && itemPostReviewReplyBinding == null) {
            commentId = postReviewAdapter.getItemData(postReviewPosition).getCommentId();
        } else {
            if (postReviewPosition < postReviewAdapter.getData().size()) {
                PostReviewBean postReviewBean = postReviewAdapter.getItemData(postReviewPosition);
                if (postReviewBean != null && postReviewBean.getSubComments() != null && !postReviewBean.getSubComments().isEmpty()) {
                    if (reviewReplyPosition < postReviewBean.getSubComments().size()) {
                        commentId = postReviewBean.getSubComments().get(reviewReplyPosition).getCommentId();
                    }
                }
            }
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("postId", postId);
        jsonObject.addProperty("commentId", commentId);
        jsonObject.addProperty("likeType", (likeOrUnlikeType == -1 || likeOrUnlikeType == 0) ? 0 : likeOrUnlikeType);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        long finalCommentId = commentId;
        if (viewModel == null) {
            return;
        }
        viewModel.postReviewLikeOrUnlike(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    saveReviewLikeOrUnlike(postReviewPosition, reviewReplyPosition, likeOrUnlikeType, itemPostReviewReplyBinding);
                    showReviewLikeOrUnlikeAnimationView(likeOrUnlikeType, finalCommentId);
                } else {
                    LogUtils.e(resultBean.getMsg());
                    resetReviewLikeOrUnlikeStatus(reviewReplyPosition, likeOrUnlikeType, itemPostReviewBinding, itemPostReviewReplyBinding);
                }
            } else {
                resetReviewLikeOrUnlikeStatus(reviewReplyPosition, likeOrUnlikeType, itemPostReviewBinding, itemPostReviewReplyBinding);
            }
            if (reviewReplyPosition == -1 && itemPostReviewReplyBinding == null) {
                itemPostReviewBinding.tvPostReviewLike.setEnabled(true);
                itemPostReviewBinding.ivPostReviewDislike.setEnabled(true);
            } else {
                itemPostReviewReplyBinding.tvReviewReplyLike.setEnabled(true);
                itemPostReviewReplyBinding.ivReviewReplyDislike.setEnabled(true);
            }
        });
    }

    private void showReviewLikeOrUnlikeAnimationView(int likeOrUnlikeType, long commentId) {
        //0代表以前的小熊猫，1是新的宇航员
        int animationType = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("home_like_animation_type");
        int drawableResource;
        switch (likeOrUnlikeType) {
            case 3:
                drawableResource = animationType == 0 ? R.drawable.home_comment_gif_hot : R.drawable.home_comment_gif_hot_new1;
                loadLikeOrUnlikeGifImage(drawableResource);
                break;
            case 4:
                drawableResource = animationType == 0 ? R.drawable.home_comment_gif_have_useful : R.drawable.home_comment_gif_have_useful_new1;
                loadLikeOrUnlikeGifImage(drawableResource);
                break;
            case 5:
                drawableResource = animationType == 0 ? R.drawable.home_dislike_gif_hate_look : R.drawable.home_dislike_gif_hate_look_new1;
                loadLikeOrUnlikeGifImage(drawableResource);
                break;
            case 6:
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                intent.putExtra("commentId", commentId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void loadLikeOrUnlikeGifImage(int drawableResource) {
        likeOrUnlikeAnimationView.setVisibility(View.VISIBLE);
        GlideUtils.loadGifImageFromResource(drawableResource, likeOrUnlikeAnimationView);

        ThreadUtils.runOnUiThreadDelayed(() -> {
            GifDrawable gifDrawable = (GifDrawable) likeOrUnlikeAnimationView.getDrawable();
            if (gifDrawable != null && gifDrawable.isRunning()) {
                gifDrawable.stop();
            }
            likeOrUnlikeAnimationView.setVisibility(View.GONE);
        }, 2500);
    }

    private void resetReviewLikeOrUnlikeStatus(int reviewReplyPosition, int likeOrUnlikeType, ItemPostReviewBinding itemPostReviewBinding, ItemPostReviewReplyBinding itemPostReviewReplyBinding) {
        if (likeOrUnlikeType == -1) {
            if (reviewReplyPosition == -1 && itemPostReviewReplyBinding == null) {
                itemPostReviewBinding.tvPostReviewLike.setTextColor(CommonUtils.getColor(R.color.light_orange));
                itemPostReviewBinding.tvPostReviewLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_select, 0);
            } else {
                itemPostReviewReplyBinding.tvReviewReplyLike.setTextColor(CommonUtils.getColor(R.color.light_orange));
                itemPostReviewReplyBinding.tvReviewReplyLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_select, 0);
            }
        } else if (likeOrUnlikeType == 0) {
            if (reviewReplyPosition == -1 && itemPostReviewReplyBinding == null) {
                itemPostReviewBinding.ivPostReviewDislike.setImageResource(R.mipmap.review_dislike_select);
            } else {
                itemPostReviewReplyBinding.ivReviewReplyDislike.setImageResource(R.mipmap.review_dislike_select);
            }
        } else if (likeOrUnlikeType == 1 || likeOrUnlikeType == 3 || likeOrUnlikeType == 4) {
            if (reviewReplyPosition == -1 && itemPostReviewReplyBinding == null) {
                itemPostReviewBinding.tvPostReviewLike.setTextColor(CommonUtils.getColor(R.color.color_999999));
                itemPostReviewBinding.tvPostReviewLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_normal, 0);
            } else {
                itemPostReviewReplyBinding.tvReviewReplyLike.setTextColor(CommonUtils.getColor(R.color.color_999999));
                itemPostReviewReplyBinding.tvReviewReplyLike.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_like_normal, 0);
            }
        } else if (likeOrUnlikeType == 2 || likeOrUnlikeType == 5 || likeOrUnlikeType == 6) {
            if (reviewReplyPosition == -1 && itemPostReviewReplyBinding == null) {
                itemPostReviewBinding.ivPostReviewDislike.setImageResource(R.mipmap.review_dislike_normal);
            } else {
                itemPostReviewReplyBinding.ivReviewReplyDislike.setImageResource(R.mipmap.review_dislike_normal);
            }
        }
    }

    private void getPostReview() {
        if (viewModel == null) {
            return;
        }
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);
        RequestParamUtil.addPagingParam(jsonObject, viewModel.getPageNumber(), viewModel.getPageSize());
        jsonObject.addProperty("postId", videoOrImageContent == null ? -1L : videoOrImageContent.getPostId());

        viewModel.getPostReview(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (isLoadMoreFail) {
                        isLoadMoreFail = false;
                    }
                    List<PostReviewBean> postReviewList = resultBean.getData();
                    if (postReviewList != null && !postReviewList.isEmpty()) {
                        if (viewModel.getPageNumber() == 1) {
                            postReviewAdapter.setNewData(postReviewList);
                            dialogPostReviewBinding.llEmptyView.setVisibility(View.GONE);
                        } else {
                            postReviewAdapter.addData(postReviewList);
                            dialogPostReviewBinding.recyclerView.loadMoreComplete();
                        }
                        if (postReviewList.size() < viewModel.getPageSize()) {
                            dialogPostReviewBinding.recyclerView.loadMoreEnd();
                        }
                    } else {
                        dialogPostReviewBinding.llEmptyView.dealRequestDataEmpty();
                        dialogPostReviewBinding.recyclerView.loadMoreEnd();
                    }
                } else {
                    LogUtils.e(resultBean.getMsg());
                    dialogPostReviewBinding.recyclerView.loadMoreEnd();
                    dialogPostReviewBinding.llEmptyView.dealRequestDataFail();
                }
            } else {
                dialogPostReviewBinding.llEmptyView.dealRequestDataFail();
                if (viewModel.getPageNumber() != 1) {
                    isLoadMoreFail = true;
                }
            }
        });
    }

    private void publishReview(String reviewContent) {
        if (viewModel == null) {
            return;
        }
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);
        jsonObject.addProperty("postId", videoOrImageContent == null ? -1L : videoOrImageContent.getPostId());
        jsonObject.addProperty("content", reviewContent);
        if (postReviewType == 1) {
            jsonObject.addProperty("parentId", 0);
            jsonObject.addProperty("replyUserId", videoOrImageContent == null ? -1L : videoOrImageContent.getAuthorId());
            jsonObject.addProperty("replyNickname", videoOrImageContent == null ? "" : videoOrImageContent.getNickname());
        } else if (postReviewType == 2) {
            jsonObject.addProperty("parentId", clickPostReviewInfo.getCommentId());
            jsonObject.addProperty("replyUserId", clickPostReviewInfo.getUserId());
            jsonObject.addProperty("replyNickname", (clickPostReviewInfo.getTinyUser() == null || TextUtils.isEmpty(clickPostReviewInfo.getTinyUser().getNickname())) ? "" : clickPostReviewInfo.getTinyUser().getNickname());
        } else if (postReviewType == 3) {
            jsonObject.addProperty("parentId", clickPostReviewInfo.getCommentId());
            jsonObject.addProperty("replyUserId", clickReviewReplyInfo.getUserId());
            jsonObject.addProperty("replyNickname", (clickReviewReplyInfo.getTinyUser() == null || TextUtils.isEmpty(clickReviewReplyInfo.getTinyUser().getNickname())) ? "" : clickReviewReplyInfo.getTinyUser().getNickname());
        }

        viewModel.publishReview(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    long reviewId = resultBean.getData();
                    if (postReviewType == 1) {
                        saveReviewInfo(reviewId, reviewContent);
                    } else if (postReviewType == 2) {
                        saveReviewReplyInfo(reviewId, reviewContent);
                    } else if (postReviewType == 3) {
                        saveReviewReplyReplyInfo(reviewId, reviewContent);
                    }
                    dialogPostReviewBinding.llCommonInputBox.etInputBox.setText("");
                    if (dialogPostReviewBinding.llCommonInputBox.keyboardView.getVisibility() == View.VISIBLE) {
                        KeyboardUtils.hideSoftInput(dialogPostReviewBinding.llCommonInputBox.etInputBox);
                    } else {
                        resetReviewStatus();
                    }
                    CommonUtils.showShort("评论发布成功");
                } else {
                    LogUtils.e(resultBean.getMsg());
                    dialogPostReviewBinding.llCommonInputBox.tvSend.setEnabled(true);
                }
            } else {
                dialogPostReviewBinding.llCommonInputBox.tvSend.setEnabled(true);
            }
        });
    }

    @Override
    public void deletePostReview(int postReviewPosition, int reviewReplyPosition) {
        if (viewModel == null) {
            return;
        }
        long commentId = -1L;
        if (reviewReplyPosition == -1) {
            commentId = postReviewAdapter.getItemData(postReviewPosition).getCommentId();
        } else {
            if (postReviewPosition < postReviewAdapter.getData().size()) {
                PostReviewBean postReviewBean = postReviewAdapter.getItemData(postReviewPosition);
                if (postReviewBean != null && postReviewBean.getSubComments() != null && !postReviewBean.getSubComments().isEmpty()) {
                    if (reviewReplyPosition < postReviewBean.getSubComments().size()) {
                        commentId = postReviewBean.getSubComments().get(reviewReplyPosition).getCommentId();
                    }
                }
            }
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("commentId", commentId);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        viewModel.deleteReview(jsonObject.toString()).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    deleteReview(postReviewPosition, reviewReplyPosition);
                    CommonUtils.showShort("删除成功");
                } else {
                    CommonUtils.showShort("删除失败，请重试");
                    LogUtils.e(resultBean.getMsg());
                }
            }
        });
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void saveReviewInfo(long reviewId, String reviewContent) {
        PostReviewBean postReviewInfo = new PostReviewBean();
        postReviewInfo.setCommentId(reviewId);
        postReviewInfo.setParentId(0);
        postReviewInfo.setContent(reviewContent);
        postReviewInfo.setCreateTime(System.currentTimeMillis());
        postReviewInfo.setReplyUserId(videoOrImageContent == null ? -1L : videoOrImageContent.getAuthorId());
        postReviewInfo.setReplyNickname(videoOrImageContent == null ? "" : videoOrImageContent.getNickname());
        postReviewInfo.setUserId(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"));
        PostReviewBean.TinyUserBean tinyUserBean = new PostReviewBean.TinyUserBean();
        tinyUserBean.setAvatar(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("user_avatar"));
        tinyUserBean.setNickname(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("user_nike_name"));
        postReviewInfo.setTinyUser(tinyUserBean);
        postReviewInfo.setCommentStatus(new PostReviewBean.CommentStatusBean());
        postReviewInfo.setSubComments(null);

        dialogPostReviewBinding.llEmptyView.setVisibility(View.GONE);
        postReviewAdapter.getData().add(0, postReviewInfo);
        postReviewAdapter.notifyDataSetChanged();

        increaseReviewCount();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void saveReviewReplyInfo(long reviewReplyId, String reviewContent) {
        PostReviewBean.SubCommentsBean reviewReplyBean = new PostReviewBean.SubCommentsBean();
        reviewReplyBean.setCommentId(reviewReplyId);
        reviewReplyBean.setParentId(clickPostReviewInfo.getCommentId());
        reviewReplyBean.setContent(reviewContent);
        reviewReplyBean.setCreateTime(System.currentTimeMillis());
        reviewReplyBean.setReplyUserId(clickPostReviewInfo.getUserId());
        reviewReplyBean.setReplyNickname((clickPostReviewInfo.getTinyUser() == null || TextUtils.isEmpty(clickPostReviewInfo.getTinyUser().getNickname())) ? "" : clickPostReviewInfo.getTinyUser().getNickname());
        reviewReplyBean.setUserId(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"));
        PostReviewBean.SubCommentsBean.TinyUserBeanX tinyUserBean = new PostReviewBean.SubCommentsBean.TinyUserBeanX();
        tinyUserBean.setAvatar(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("user_avatar"));
        tinyUserBean.setNickname(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("user_nike_name"));
        reviewReplyBean.setTinyUser(tinyUserBean);
        reviewReplyBean.setCommentStatus(new PostReviewBean.SubCommentsBean.CommentStatusBeanX());

        if (clickPostReviewInfo.getSubComments() == null) {
            List<PostReviewBean.SubCommentsBean> subCommentList = new ArrayList<>();
            subCommentList.add(0, reviewReplyBean);
            clickPostReviewInfo.setSubComments(subCommentList);
        } else {
            clickPostReviewInfo.getSubComments().add(0, reviewReplyBean);
        }

        BaseBindingHolder<PostReviewBean, ItemPostReviewBinding> bindingViewHolder = (BaseBindingHolder<PostReviewBean, ItemPostReviewBinding>) dialogPostReviewBinding.recyclerView.findViewHolderForAdapterPosition(clickPostReviewPosition);
        if (bindingViewHolder != null && bindingViewHolder.binding != null) {
            PostReviewAdapter.ReviewReplyAdapter reviewReplyAdapter = (PostReviewAdapter.ReviewReplyAdapter) bindingViewHolder.binding.recyclerView.getAdapter();
            if (reviewReplyAdapter != null) {
                if (clickPostReviewInfo.getSubComments().size() == 1) {
                    reviewReplyAdapter.setNewData(clickPostReviewInfo.getSubComments());
                } else {
                    reviewReplyAdapter.notifyDataSetChanged();
                }
            }
            setUnfoldPostReviewReply(bindingViewHolder);
        } else {
            postReviewAdapter.notifyItemChanged(clickPostReviewPosition);
            ThreadUtils.runOnUiThreadDelayed(() -> {
                BaseBindingHolder<PostReviewBean, ItemPostReviewBinding> viewHolder = (BaseBindingHolder<PostReviewBean, ItemPostReviewBinding>) dialogPostReviewBinding.recyclerView.findViewHolderForAdapterPosition(clickPostReviewPosition);
                if (viewHolder != null && viewHolder.binding != null) {
                    setUnfoldPostReviewReply(viewHolder);
                }
            }, 500);
        }
    }

    private void setUnfoldPostReviewReply(BaseBindingHolder<PostReviewBean, ItemPostReviewBinding> bindingViewHolder) {
        bindingViewHolder.binding.recyclerView.setVisibility(View.VISIBLE);
        bindingViewHolder.binding.tvUnfoldPostReviewReply.setText("收起");
        bindingViewHolder.binding.tvUnfoldPostReviewReply.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_up_arrow, 0);
        bindingViewHolder.binding.tvUnfoldPostReviewReply.setVisibility(View.VISIBLE);

        increaseReviewCount();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void saveReviewReplyReplyInfo(long reviewReplyId, String reviewContent) {
        PostReviewBean.SubCommentsBean reviewReplyBean = new PostReviewBean.SubCommentsBean();
        reviewReplyBean.setCommentId(reviewReplyId);
        reviewReplyBean.setParentId(clickPostReviewInfo.getCommentId());
        reviewReplyBean.setContent(reviewContent);
        reviewReplyBean.setCreateTime(System.currentTimeMillis());
        reviewReplyBean.setReplyUserId(clickReviewReplyInfo.getUserId());
        reviewReplyBean.setReplyNickname((clickReviewReplyInfo.getTinyUser() == null || TextUtils.isEmpty(clickReviewReplyInfo.getTinyUser().getNickname())) ? "" : clickReviewReplyInfo.getTinyUser().getNickname());
        reviewReplyBean.setUserId(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"));
        PostReviewBean.SubCommentsBean.TinyUserBeanX tinyUserBean = new PostReviewBean.SubCommentsBean.TinyUserBeanX();
        tinyUserBean.setAvatar(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("user_avatar"));
        tinyUserBean.setNickname(SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("user_nike_name"));
        reviewReplyBean.setTinyUser(tinyUserBean);
        reviewReplyBean.setCommentStatus(new PostReviewBean.SubCommentsBean.CommentStatusBeanX());

        if (clickPostReviewInfo.getSubComments() != null) {
            clickPostReviewInfo.getSubComments().add(clickReviewReplyPosition + 1, reviewReplyBean);
        }

        BaseBindingHolder<PostReviewBean, ItemPostReviewBinding> bindingViewHolder = (BaseBindingHolder<PostReviewBean, ItemPostReviewBinding>) dialogPostReviewBinding.recyclerView.findViewHolderForAdapterPosition(clickPostReviewPosition);
        if (bindingViewHolder != null && bindingViewHolder.binding != null) {
            PostReviewAdapter.ReviewReplyAdapter reviewReplyAdapter = (PostReviewAdapter.ReviewReplyAdapter) bindingViewHolder.binding.recyclerView.getAdapter();
            if (reviewReplyAdapter != null) {
                reviewReplyAdapter.notifyDataSetChanged();
            }
        }

        increaseReviewCount();
    }

    @SuppressLint("SetTextI18n")
    private void increaseReviewCount() {
        videoOrImageContent.setReplyCount(videoOrImageContent.getReplyCount() + 1);
        dialogPostReviewBinding.tvPostReviewNumber.setText(videoOrImageContent.getReplyCount() + "条");
        dialogPostReviewBinding.tvPostReviewNumber.setVisibility(View.VISIBLE);

        RxBus.getDefault().post(RxCodeConstant.UPDATE_POST_LIKE_OR_UNLIKE_OR_REVIEW, videoOrImageContent);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void saveReviewLikeOrUnlike(int postReviewPosition, int reviewReplyPosition, int likeOrUnlikeType, ItemPostReviewReplyBinding itemPostReviewReplyBinding) {
        if (likeOrUnlikeType == -1) {
            if (reviewReplyPosition == -1 && itemPostReviewReplyBinding == null) {
                PostReviewBean postReviewBean = postReviewAdapter.getItemData(postReviewPosition);
                postReviewBean.setCurUserLikeType(0);
                PostReviewBean.CommentStatusBean commentStatus = postReviewBean.getCommentStatus();
                commentStatus.setLikeNum(commentStatus.getLikeNum() - 1);
                postReviewAdapter.notifyItemChanged(postReviewPosition);
            } else {
                if (postReviewPosition < postReviewAdapter.getData().size()) {
                    PostReviewBean postReviewBean = postReviewAdapter.getItemData(postReviewPosition);
                    if (postReviewBean != null && postReviewBean.getSubComments() != null && !postReviewBean.getSubComments().isEmpty()) {
                        if (reviewReplyPosition < postReviewBean.getSubComments().size()) {
                            PostReviewBean.SubCommentsBean reviewReplyBean = postReviewBean.getSubComments().get(reviewReplyPosition);
                            reviewReplyBean.setCurUserLikeType(0);
                            PostReviewBean.SubCommentsBean.CommentStatusBeanX commentStatus = reviewReplyBean.getCommentStatus();
                            commentStatus.setLikeNum(commentStatus.getLikeNum() - 1);
                        }
                    }
                }

                BaseBindingHolder<PostReviewBean, ItemPostReviewBinding> bindingViewHolder = (BaseBindingHolder<PostReviewBean, ItemPostReviewBinding>) dialogPostReviewBinding.recyclerView.findViewHolderForAdapterPosition(postReviewPosition);
                if (bindingViewHolder != null && bindingViewHolder.binding != null) {
                    PostReviewAdapter.ReviewReplyAdapter reviewReplyAdapter = (PostReviewAdapter.ReviewReplyAdapter) bindingViewHolder.binding.recyclerView.getAdapter();
                    if (reviewReplyAdapter != null) {
                        reviewReplyAdapter.notifyItemChanged(reviewReplyPosition);
                    }
                }
            }
        } else if (likeOrUnlikeType == 0) {
            if (reviewReplyPosition == -1 && itemPostReviewReplyBinding == null) {
                PostReviewBean postReviewBean = postReviewAdapter.getItemData(postReviewPosition);
                postReviewBean.setCurUserLikeType(0);
                postReviewAdapter.notifyItemChanged(postReviewPosition);
            } else {
                if (postReviewPosition < postReviewAdapter.getData().size()) {
                    PostReviewBean postReviewBean = postReviewAdapter.getItemData(postReviewPosition);
                    if (postReviewBean != null && postReviewBean.getSubComments() != null && !postReviewBean.getSubComments().isEmpty()) {
                        if (reviewReplyPosition < postReviewBean.getSubComments().size()) {
                            PostReviewBean.SubCommentsBean reviewReplyBean = postReviewBean.getSubComments().get(reviewReplyPosition);
                            reviewReplyBean.setCurUserLikeType(0);
                        }
                    }
                }

                BaseBindingHolder<PostReviewBean, ItemPostReviewBinding> bindingViewHolder = (BaseBindingHolder<PostReviewBean, ItemPostReviewBinding>) dialogPostReviewBinding.recyclerView.findViewHolderForAdapterPosition(postReviewPosition);
                if (bindingViewHolder != null && bindingViewHolder.binding != null) {
                    PostReviewAdapter.ReviewReplyAdapter reviewReplyAdapter = (PostReviewAdapter.ReviewReplyAdapter) bindingViewHolder.binding.recyclerView.getAdapter();
                    if (reviewReplyAdapter != null) {
                        reviewReplyAdapter.notifyItemChanged(reviewReplyPosition);
                    }
                }
            }
        } else if (likeOrUnlikeType == 1 || likeOrUnlikeType == 3 || likeOrUnlikeType == 4) {
            if (reviewReplyPosition == -1 && itemPostReviewReplyBinding == null) {
                PostReviewBean postReviewBean = postReviewAdapter.getItemData(postReviewPosition);
                postReviewBean.setCurUserLikeType(likeOrUnlikeType);
                PostReviewBean.CommentStatusBean commentStatus = postReviewBean.getCommentStatus();
                commentStatus.setLikeNum(commentStatus.getLikeNum() + 1);
                postReviewAdapter.notifyItemChanged(postReviewPosition);
            } else {
                if (postReviewPosition < postReviewAdapter.getData().size()) {
                    PostReviewBean postReviewBean = postReviewAdapter.getItemData(postReviewPosition);
                    if (postReviewBean != null && postReviewBean.getSubComments() != null && !postReviewBean.getSubComments().isEmpty()) {
                        if (reviewReplyPosition < postReviewBean.getSubComments().size()) {
                            PostReviewBean.SubCommentsBean reviewReplyBean = postReviewBean.getSubComments().get(reviewReplyPosition);
                            reviewReplyBean.setCurUserLikeType(likeOrUnlikeType);
                            PostReviewBean.SubCommentsBean.CommentStatusBeanX commentStatus = reviewReplyBean.getCommentStatus();
                            commentStatus.setLikeNum(commentStatus.getLikeNum() + 1);
                        }
                    }
                }

                BaseBindingHolder<PostReviewBean, ItemPostReviewBinding> bindingViewHolder = (BaseBindingHolder<PostReviewBean, ItemPostReviewBinding>) dialogPostReviewBinding.recyclerView.findViewHolderForAdapterPosition(postReviewPosition);
                if (bindingViewHolder != null && bindingViewHolder.binding != null) {
                    PostReviewAdapter.ReviewReplyAdapter reviewReplyAdapter = (PostReviewAdapter.ReviewReplyAdapter) bindingViewHolder.binding.recyclerView.getAdapter();
                    if (reviewReplyAdapter != null) {
                        reviewReplyAdapter.notifyItemChanged(reviewReplyPosition);
                    }
                }
            }
        } else if (likeOrUnlikeType == 2 || likeOrUnlikeType == 5 || likeOrUnlikeType == 6) {
            if (reviewReplyPosition == -1 && itemPostReviewReplyBinding == null) {
                PostReviewBean postReviewBean = postReviewAdapter.getItemData(postReviewPosition);
                postReviewBean.setCurUserLikeType(likeOrUnlikeType);
                PostReviewBean.CommentStatusBean commentStatus = postReviewBean.getCommentStatus();
                commentStatus.setLikeNum(commentStatus.getLikeNum() - 1);
                postReviewAdapter.notifyItemChanged(postReviewPosition);
            } else {
                if (postReviewPosition < postReviewAdapter.getData().size()) {
                    PostReviewBean postReviewBean = postReviewAdapter.getItemData(postReviewPosition);
                    if (postReviewBean != null && postReviewBean.getSubComments() != null && !postReviewBean.getSubComments().isEmpty()) {
                        if (reviewReplyPosition < postReviewBean.getSubComments().size()) {
                            PostReviewBean.SubCommentsBean reviewReplyBean = postReviewBean.getSubComments().get(reviewReplyPosition);
                            reviewReplyBean.setCurUserLikeType(likeOrUnlikeType);
                            PostReviewBean.SubCommentsBean.CommentStatusBeanX commentStatus = reviewReplyBean.getCommentStatus();
                            commentStatus.setLikeNum(commentStatus.getLikeNum() - 1);
                        }
                    }
                }

                BaseBindingHolder<PostReviewBean, ItemPostReviewBinding> bindingViewHolder = (BaseBindingHolder<PostReviewBean, ItemPostReviewBinding>) dialogPostReviewBinding.recyclerView.findViewHolderForAdapterPosition(postReviewPosition);
                if (bindingViewHolder != null && bindingViewHolder.binding != null) {
                    PostReviewAdapter.ReviewReplyAdapter reviewReplyAdapter = (PostReviewAdapter.ReviewReplyAdapter) bindingViewHolder.binding.recyclerView.getAdapter();
                    if (reviewReplyAdapter != null) {
                        reviewReplyAdapter.notifyItemChanged(reviewReplyPosition);
                    }
                }
            }
        }
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void deleteReview(int postReviewPosition, int reviewReplyPosition) {
        if (reviewReplyPosition == -1) {
            postReviewAdapter.getData().remove(postReviewPosition);
            postReviewAdapter.notifyDataSetChanged();

            if (postReviewAdapter.getData().isEmpty()) {
                dialogPostReviewBinding.llEmptyView.dealRequestDataEmpty();
                dialogPostReviewBinding.llEmptyView.setVisibility(View.VISIBLE);
            }

            int reviewReplyCount = 0;
            if (postReviewPosition < postReviewAdapter.getData().size()) {
                PostReviewBean postReviewBean = postReviewAdapter.getItemData(postReviewPosition);
                if (postReviewBean != null && postReviewBean.getSubComments() != null) {
                    reviewReplyCount = postReviewBean.getSubComments().size();
                }
            }
            videoOrImageContent.setReplyCount(videoOrImageContent.getReplyCount() - 1 - reviewReplyCount);
        } else {
            if (postReviewPosition < postReviewAdapter.getData().size()) {
                PostReviewBean postReviewBean = postReviewAdapter.getItemData(postReviewPosition);
                if (postReviewBean != null && postReviewBean.getSubComments() != null && !postReviewBean.getSubComments().isEmpty()) {
                    if (reviewReplyPosition < postReviewBean.getSubComments().size()) {
                        postReviewBean.getSubComments().remove(reviewReplyPosition);
                    }
                }
            }

            BaseBindingHolder<PostReviewBean, ItemPostReviewBinding> bindingViewHolder = (BaseBindingHolder<PostReviewBean, ItemPostReviewBinding>) dialogPostReviewBinding.recyclerView.findViewHolderForAdapterPosition(postReviewPosition);
            if (bindingViewHolder != null && bindingViewHolder.binding != null) {
                PostReviewAdapter.ReviewReplyAdapter reviewReplyAdapter = (PostReviewAdapter.ReviewReplyAdapter) bindingViewHolder.binding.recyclerView.getAdapter();
                if (reviewReplyAdapter != null) {
                    reviewReplyAdapter.notifyDataSetChanged();
                    if (reviewReplyAdapter.getData().isEmpty()) {
                        bindingViewHolder.binding.recyclerView.setVisibility(View.GONE);
                        bindingViewHolder.binding.tvUnfoldPostReviewReply.setVisibility(View.GONE);
                    } else {
                        bindingViewHolder.binding.recyclerView.setVisibility(View.VISIBLE);
                        bindingViewHolder.binding.tvUnfoldPostReviewReply.setText("收起");
                        bindingViewHolder.binding.tvUnfoldPostReviewReply.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.review_up_arrow, 0);
                        bindingViewHolder.binding.tvUnfoldPostReviewReply.setVisibility(View.VISIBLE);
                    }
                }
            }

            videoOrImageContent.setReplyCount(videoOrImageContent.getReplyCount() - 1);
        }

        if (videoOrImageContent.getReplyCount() > 0) {
            dialogPostReviewBinding.tvPostReviewNumber.setText(videoOrImageContent.getReplyCount() + "条");
            dialogPostReviewBinding.tvPostReviewNumber.setVisibility(View.VISIBLE);
        } else {
            dialogPostReviewBinding.tvPostReviewNumber.setVisibility(View.GONE);
        }

        RxBus.getDefault().post(RxCodeConstant.UPDATE_POST_LIKE_OR_UNLIKE_OR_REVIEW, videoOrImageContent);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        KeyboardUtils.unregisterSoftInputChangedListener(getDialog().getWindow());
        super.onDismiss(dialog);
        BaseHomeMediaFragment.isShowCommentDialog = false;
        BaseHomeMediaFragment.showCommentDialogTime += System.currentTimeMillis() - BaseHomeMediaFragment.startCommentDialogTime;

    }
}
