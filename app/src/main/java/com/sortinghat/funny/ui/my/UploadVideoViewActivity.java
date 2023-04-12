package com.sortinghat.funny.ui.my;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.HomeVideoAdapter;
import com.sortinghat.funny.adapter.TaskRankingAdapter;
import com.sortinghat.funny.bean.TaskRankListBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.databinding.ActivityTaskRankBinding;
import com.sortinghat.funny.databinding.ActivityUploadVideoViewBinding;
import com.sortinghat.funny.util.CommonUtil;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.viewmodel.TaskCentralViewModel;

import java.util.List;

public class UploadVideoViewActivity extends BaseActivity<TaskCentralViewModel, ActivityUploadVideoViewBinding> {
    private boolean mPrepared;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_upload_video_view;
    }

    @Override
    protected void initViews() {
        initTitleBar("视频/图片");
        ConstantUtil.createUmEvent("video_view_display");//发帖-帖子预览-pv
        titleBarBinding.vDividerLine.setVisibility(View.GONE);
        contentLayoutBinding.videoView.setOnClickListener(quickClickListener);
    }

    @Override
    protected void initData() {
        String pathUrl = "";
        int mediaType = 0;
        if (null != getIntent()) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("pathUrl"))) {
                pathUrl = getIntent().getStringExtra("pathUrl");
                mediaType = getIntent().getIntExtra("mediaType", 0);
                if (mediaType == 1) {
                    contentLayoutBinding.videoView.setVisibility(View.VISIBLE);
                    contentLayoutBinding.imgView.setVisibility(View.GONE);
                    contentLayoutBinding.videoView.setVideoPath(pathUrl);
                    playVideo();
                } else {
                    contentLayoutBinding.videoView.setVisibility(View.GONE);
                    contentLayoutBinding.imgView.setVisibility(View.VISIBLE);
                    if (CommonUtil.urlIsGif(pathUrl)) {
                        GlideUtils.loadGifImage(pathUrl, 0, contentLayoutBinding.imgView);
                    } else {
                        GlideUtils.loadImageNoPlaceholder(pathUrl, contentLayoutBinding.imgView);
                    }
                }
            }
        }
    }

    private void playVideo() {
        contentLayoutBinding.videoView.start();
        contentLayoutBinding.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {//标识准备完毕
                            mPrepared = true;
//                            mFirstFrameImg.setVisibility(View.GONE);//隐藏第一帧，开始展示视频播放
                        }
                        return false;
                    }
                });
            }
        });

        contentLayoutBinding.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                contentLayoutBinding.videoView.start();
//                onPause();
            }
        });

    }


    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.video_view://视频
                    finish();

                    break;

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (contentLayoutBinding.videoView != null) {
            contentLayoutBinding.videoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (contentLayoutBinding.videoView != null) {
            contentLayoutBinding.videoView.pause();
        }
    }

    public static void starActivity(Context mContext, String pathUrl, int mediaType) {
        Intent intent = new Intent(mContext, UploadVideoViewActivity.class);
        intent.putExtra("pathUrl", pathUrl);
        intent.putExtra("mediaType", mediaType);
        ActivityUtils.startActivity(intent);
    }

}

