package com.sortinghat.funny.thirdparty.video;

import android.widget.ImageView;

public interface OnVideoControllerListener {
    void onHeadClick();

    void onAddClick();

    void onLikeClick();

    void onFeelLikeClick(ImageView ivFeelLike, int likeType);

    void onDislikeClick();

    void onCommentClick();

    void onRewardClick();

    void onShareClick();

    void onPostRejectClick();
}
