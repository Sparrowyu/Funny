package com.sortinghat.funny.ui.message;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.sortinghat.common.base.BaseFragment;
import com.sortinghat.common.base.NoViewModel;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.MessageCountBean;
import com.sortinghat.funny.databinding.FragmentMessageBinding;

/**
 * Created by wzy on 2021/6/14
 */
public class MessageFragment extends BaseFragment<NoViewModel, FragmentMessageBinding> {

    private MessageCountBean messageCountBean;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    protected void initViews() {
        contentLayoutBinding.getRoot().setVisibility(View.VISIBLE);
        subscibeRxBus();
    }

    QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.cl_message_fans:
                    ActivityUtils.startActivity(FansMessageDetailsActivity.class);
                    if (messageCountBean != null && messageCountBean.getUserFollow() != 0) {
                        messageCountBean.setUserFollow(0);
                    }
                    break;
                case R.id.cl_message_review_and_reply:
                    ActivityUtils.startActivity(ReviewAndReplyMessageDetailsActivity.class);
                    if (messageCountBean != null && messageCountBean.getPostComment() != 0) {
                        messageCountBean.setPostComment(0);
                    }
                    break;
                case R.id.cl_message_praise:
                    ActivityUtils.startActivity(PraiseMessageDetailsActivity.class);
                    if (messageCountBean != null && (messageCountBean.getPostLike() != 0 || messageCountBean.getCommentLike() != 0)) {
                        messageCountBean.setPostLike(0);
                        messageCountBean.setCommentLike(0);
                    }
                    break;
                case R.id.cl_message_system_notice:
                    ActivityUtils.startActivity(SystemNoticeMessageDetailsActivity.class);
                    if (messageCountBean != null && messageCountBean.getSysBroadcast() != 0) {
                        messageCountBean.setSysBroadcast(0);
                    }
                    break;
                case R.id.cl_message_audit_notice:
                    ActivityUtils.startActivity(AuditNoticeMessageDetailsActivity.class);
                    if (messageCountBean != null && messageCountBean.getApplySys() != 0) {
                        messageCountBean.setApplySys(0);
                    }
                    break;
                case R.id.cl_message_topic_optimize_remind:
                    ActivityUtils.startActivity(TopicOptimizeRemindMessageDetailsActivity.class);
                    break;
                default:
                    break;
            }
            if (messageCountBean != null) {
                RxBus.getDefault().postSticky(messageCountBean);
            }
        }
    };

    @Override
    protected void setListener() {
        contentLayoutBinding.clMessageFans.setOnClickListener(quickClickListener);
        contentLayoutBinding.clMessageReviewAndReply.setOnClickListener(quickClickListener);
        contentLayoutBinding.clMessagePraise.setOnClickListener(quickClickListener);
        contentLayoutBinding.clMessageSystemNotice.setOnClickListener(quickClickListener);
        contentLayoutBinding.clMessageAuditNotice.setOnClickListener(quickClickListener);
        contentLayoutBinding.clMessageTopicOptimizeRemind.setOnClickListener(quickClickListener);
    }

    @Override
    protected void initData() {
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservableSticky(MessageCountBean.class).subscribe(messageCountBean -> {
            if (messageCountBean != null) {
                this.messageCountBean = messageCountBean;
                contentLayoutBinding.tvMessageFansDynamic.setText(TextUtils.isEmpty(messageCountBean.getUserFollowRemind()) ? "暂无消息" : messageCountBean.getUserFollowRemind());
                if (messageCountBean.getUserFollow() > 0) {
                    contentLayoutBinding.ivFansMessageDynamicMark.setVisibility(View.VISIBLE);
                } else {
                    contentLayoutBinding.ivFansMessageDynamicMark.setVisibility(View.GONE);
                }
                contentLayoutBinding.tvMessageReviewAndReplyDynamic.setText(TextUtils.isEmpty(messageCountBean.getPostCommentRemind()) ? "暂无消息" : messageCountBean.getPostCommentRemind());
                if (messageCountBean.getPostComment() > 0) {
                    contentLayoutBinding.ivReviewAndReplyMessageDynamicMark.setVisibility(View.VISIBLE);
                } else {
                    contentLayoutBinding.ivReviewAndReplyMessageDynamicMark.setVisibility(View.GONE);
                }
                contentLayoutBinding.tvMessagePraiseDynamic.setText(TextUtils.isEmpty(messageCountBean.getLikeRemind()) ? "暂无消息" : messageCountBean.getLikeRemind());
                if (messageCountBean.getPostLike() > 0 || messageCountBean.getCommentLike() > 0) {
                    contentLayoutBinding.ivPraiseMessageDynamicMark.setVisibility(View.VISIBLE);
                } else {
                    contentLayoutBinding.ivPraiseMessageDynamicMark.setVisibility(View.GONE);
                }
                contentLayoutBinding.tvMessageSystemNoticeDynamic.setText(TextUtils.isEmpty(messageCountBean.getSysBroadcastRemind()) ? "通知" : messageCountBean.getSysBroadcastRemind());
                if (messageCountBean.getSysBroadcast() > 0) {
                    contentLayoutBinding.ivSystemNoticeMessageDynamicMark.setVisibility(View.VISIBLE);
                } else {
                    contentLayoutBinding.ivSystemNoticeMessageDynamicMark.setVisibility(View.GONE);
                }
                contentLayoutBinding.tvMessageAuditNoticeDynamic.setText(TextUtils.isEmpty(messageCountBean.getApplySysRemind()) ? "暂无消息" : messageCountBean.getApplySysRemind());
                if (messageCountBean.getApplySys() > 0) {
                    contentLayoutBinding.ivAuditNoticeMessageDynamicMark.setVisibility(View.VISIBLE);
                } else {
                    contentLayoutBinding.ivAuditNoticeMessageDynamicMark.setVisibility(View.GONE);
                }
            }
        }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));
    }
}
