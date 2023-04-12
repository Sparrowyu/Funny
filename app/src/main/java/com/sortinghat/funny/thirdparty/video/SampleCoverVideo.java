package com.sortinghat.funny.thirdparty.video;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.sortinghat.common.base.RootApplication;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.HomeVideoAdapter;
import com.sortinghat.funny.ui.home.HomeVideoFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * 带封面
 * Created by guoshuyu on 2017/9/3.
 */
public class SampleCoverVideo extends StandardGSYVideoPlayer {

    private ImageView mCoverImage;

    private String mCoverOriginUrl;

    private LinearLayout ll_seek_time_text;

    public SampleCoverVideo(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public SampleCoverVideo(Context context) {
        super(context);
    }

    public SampleCoverVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
        mCoverImage = findViewById(R.id.thumbImage);
        ll_seek_time_text = findViewById(R.id.ll_seek_time_text);
        if (mThumbImageViewLayout != null && (mCurrentState == -1 || mCurrentState == CURRENT_STATE_NORMAL || mCurrentState == CURRENT_STATE_ERROR)) {
            mThumbImageViewLayout.setVisibility(VISIBLE);
        }
    }

    private float getXPosition(SeekBar seekBar) {
        float val = (((float) seekBar.getProgress() * (float) (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax());
        float offset = seekBar.getThumbOffset() * 2;
        int textWidth = seekBar.getWidth();
        float textCenter = (textWidth / 2.0f);
        float newX = val + offset - 0;
        return newX;
    }

    @Override
    public int getLayoutId() {
        return R.layout.video_layout_cover;
    }

    //此时视频宽高得不到，所以只能服务端给比例
    public void loadCoverImage(String url, int videoWidth, int videoHeight, HomeVideoAdapter.ImageRequestListener imageRequestListener) {
        mCoverOriginUrl = url;
        if (videoWidth == 0 || videoHeight == 0) {
            //当获取不到宽高时，用bitmap形式获取封面图，
            GlideUtils.loadImageToBitmap(getContext(), url, bitmap -> {
                if (bitmap == null || bitmap.isRecycled()) {
                    if (imageRequestListener != null) {
                        imageRequestListener.onLoadFailed(null, null, null, false);
                    }
                    return;
                }
                int sWidth = bitmap.getWidth();
                int sHeight = bitmap.getHeight();
                float scaleW = sWidth <= 0 ? 0 : sHeight / (float) sWidth;
                if (scaleW > 1.7) {
                    mCoverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    //FIT_CENTER对原图按比例放缩使之等于ImageView的宽高使之居中显示
                    mCoverImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
                mCoverImage.setImageBitmap(bitmap);
                if (imageRequestListener != null) {
                    imageRequestListener.onResourceReady(null, null, null, null, false);
                }
            });

        } else {
            if (videoHeight / (float) videoWidth > 1.7) {
                mCoverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                //FIT_CENTER对原图按比例放缩使之等于ImageView的宽高使之居中显示
                mCoverImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            GlideUtils.loadImageNoPlaceholder(url, mCoverImage, imageRequestListener);
        }
    }

    @Override
    public GSYBaseVideoPlayer startWindowFullscreen(Context context, boolean actionBar, boolean statusBar) {
        GSYBaseVideoPlayer gsyBaseVideoPlayer = super.startWindowFullscreen(context, actionBar, statusBar);
        SampleCoverVideo sampleCoverVideo = (SampleCoverVideo) gsyBaseVideoPlayer;
        sampleCoverVideo.loadCoverImage(mCoverOriginUrl, 0, 0, null);
        return gsyBaseVideoPlayer;
    }

    @Override
    public GSYBaseVideoPlayer showSmallVideo(Point size, boolean actionBar, boolean statusBar) {
        //下面这里替换成你自己的强制转化
        SampleCoverVideo sampleCoverVideo = (SampleCoverVideo) super.showSmallVideo(size, actionBar, statusBar);
        sampleCoverVideo.mStartButton.setVisibility(GONE);
        sampleCoverVideo.mStartButton = null;
        return sampleCoverVideo;
    }

    @Override
    protected void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        super.cloneParams(from, to);
        SampleCoverVideo sf = (SampleCoverVideo) from;
        SampleCoverVideo st = (SampleCoverVideo) to;
        st.mShowFullAnimation = sf.mShowFullAnimation;
    }

    /**
     * 退出window层播放全屏效果
     */
    @SuppressWarnings("ResourceType")
    @Override
    protected void clearFullscreenLayout() {
        if (!mFullAnimEnd) {
            return;
        }
        mIfCurrentIsFullscreen = false;
        int delay = 0;
        if (mOrientationUtils != null) {
            delay = mOrientationUtils.backToProtVideo();
            mOrientationUtils.setEnable(false);
            if (mOrientationUtils != null) {
                mOrientationUtils.releaseListener();
                mOrientationUtils = null;
            }
        }

        if (!mShowFullAnimation) {
            delay = 0;
        }

        final ViewGroup vp = (CommonUtil.scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
        final View oldF = vp.findViewById(getFullId());
        if (oldF != null) {
            //此处fix bug#265，推出全屏的时候，虚拟按键问题
            SampleCoverVideo gsyVideoPlayer = (SampleCoverVideo) oldF;
            gsyVideoPlayer.mIfCurrentIsFullscreen = false;
        }

        if (delay == 0) {
            backToNormal();
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    backToNormal();
                }
            }, delay);
        }
    }

    /******************* 下方两个重载方法，在播放开始前不屏蔽封面，不需要可屏蔽 ********************/
    @Override
    public void onSurfaceUpdated(Surface surface) {
        super.onSurfaceUpdated(surface);
        if (mThumbImageViewLayout != null && mThumbImageViewLayout.getVisibility() == VISIBLE) {
            mThumbImageViewLayout.setVisibility(INVISIBLE);
        }
    }

    @Override
    protected void setViewShowState(View view, int visibility) {
        this.mBottomContainer.setVisibility(VISIBLE);//隐藏底部进度条
        this.mBottomProgressBar.setVisibility(GONE);//隐藏底部进度条
        this.mLoadingProgressBar.setVisibility(GONE);//隐藏loading
        this.mTopContainer.setVisibility(GONE);//隐藏顶部阴影
        showStartButton();
        if (view == mThumbImageViewLayout && visibility != VISIBLE) {
            return;
        }
        super.setViewShowState(view, visibility);
    }

    public void showStartProgress(boolean isShow) {
        showStartButton();
        if (isShow) {
            this.mProgressBar.setVisibility(VISIBLE);//显示
        } else {
            this.mProgressBar.setVisibility(GONE);//隐藏
            resetProgressAndTime();
        }
    }

    public void showStartButton() {
        this.mBottomProgressBar.setVisibility(GONE);
        this.mProgressBar.setVisibility(GONE);
        //大于30s才显示
        if (GSYVideoManager.instance().getDuration() >= 30000) {
            this.mProgressBar.setVisibility(VISIBLE);
        } else {
            this.mProgressBar.setVisibility(GONE);
        }

    }

    @Override
    public void onSurfaceAvailable(Surface surface) {
        super.onSurfaceAvailable(surface);
        if (GSYVideoType.getRenderType() != GSYVideoType.TEXTURE) {
            if (mThumbImageViewLayout != null && mThumbImageViewLayout.getVisibility() == VISIBLE) {
                mThumbImageViewLayout.setVisibility(INVISIBLE);
            }
        }
    }

    /******************* 下方重载方法，在播放开始不显示底部进度和按键，不需要可屏蔽 ********************/

    protected boolean byStartedClick;

    @Override
    protected void onClickUiToggle(MotionEvent e) {
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            setViewShowState(mLockScreen, VISIBLE);
            return;
        }
        byStartedClick = true;
        super.onClickUiToggle(e);

    }

    /**
     * 失去了Audio Focus，此时不坐任何处理，避免播放H5页面再回来时出错
     * 当播放时暂停，其余不出来
     */
    @Override
    protected void onLossAudio() {
    }

    @Override
    protected void changeUiToNormal() {
        super.changeUiToNormal();
        byStartedClick = false;
        setViewShowState(mStartButton, INVISIBLE);
    }

    @Override
    protected void changeUiToPreparingShow() {
        super.changeUiToPreparingShow();
        Debuger.printfLog("Sample changeUiToPreparingShow");
        setViewShowState(mStartButton, INVISIBLE);
    }

    @Override
    protected void changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow();
        Debuger.printfLog("Sample changeUiToPlayingBufferingShow");
        if (!byStartedClick) {
            setViewShowState(mStartButton, INVISIBLE);
        }
    }

    @Override
    protected void hideAllWidget() {
        super.hideAllWidget();
        showStartButton();
    }

    @Override
    protected void changeUiToPlayingShow() {
        super.changeUiToPlayingShow();
        Debuger.printfLog("Sample changeUiToPlayingShow");
        if (!byStartedClick) {
            setViewShowState(mStartButton, INVISIBLE);
        }
        setViewShowState(mStartButton, INVISIBLE);//seek后也隐藏按钮
    }

    @Override
    public void startAfterPrepared() {
        if (getVideoScale()) {
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL);
        } else {
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT);
        }

        super.startAfterPrepared();
        HomeVideoFragment.startPlayTime = System.currentTimeMillis();
        HomeVideoFragment.postVideoPlayDurationTime = System.currentTimeMillis();
        HomeVideoFragment.postVideoPlayDurationLastState = 1;
        Debuger.printfLog("Sample startAfterPrepared");
        setViewShowState(mStartButton, INVISIBLE);
    }

    private boolean getVideoScale() {
        int height = GSYVideoManager.instance().getVideoHeight();
        int width = GSYVideoManager.instance().getVideoWidth();
        float videoScale = width == 0 ? 0 : height / (float) width;

        if (height > width && videoScale > 1.7) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        super.onProgressChanged(seekBar, progress, fromUser);
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        byStartedClick = true;
        super.onStartTrackingTouch(seekBar);
        ll_seek_time_text.setVisibility(VISIBLE);
        if (videoSeekBarIsDragListener != null) {
            videoSeekBarIsDragListener.isTrackingTouch(true);
        }
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        ll_seek_time_text.setVisibility(INVISIBLE);
        if (videoSeekBarIsDragListener != null) {
            videoSeekBarIsDragListener.isTrackingTouch(false);
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        super.onBufferingUpdate(percent);
        if (videoBufferingProgressListener != null) {
            videoBufferingProgressListener.onBufferingUpdate(percent);
        }
    }

    private VideoBufferingProgressListener videoBufferingProgressListener;

    public void setVideoBufferingProgressListener(VideoBufferingProgressListener videoBufferingProgressListener) {
        this.videoBufferingProgressListener = videoBufferingProgressListener;
    }

    public interface VideoBufferingProgressListener {
        void onBufferingUpdate(int percent);
    }

    @Override
    public void onSeekComplete() {
        videoPlayCompleteListener.complete();
        super.onSeekComplete();
    }

    public VideoPlayCompleteListener videoPlayCompleteListener;

    public interface VideoPlayCompleteListener {
        void complete();
    }

    public void setVideoPlayCompleteListener(VideoPlayCompleteListener videoPlayCompleteListener) {
        this.videoPlayCompleteListener = videoPlayCompleteListener;
    }

    @Override
    protected void showDragProgressTextOnSeekBar(boolean fromUser, int progress) {
        super.showDragProgressTextOnSeekBar(fromUser, progress);
    }

    public VideoSeekBarIsDragListener videoSeekBarIsDragListener;

    public interface VideoSeekBarIsDragListener {
        void isTrackingTouch(boolean isStart);
    }

    public void setVideoSeekBarIsDragListener(VideoSeekBarIsDragListener videoSeekBarIsDragListener) {
        this.videoSeekBarIsDragListener = videoSeekBarIsDragListener;
    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);//当ProxyCache error: Error opening connection for时what：-10000-extra:0
//        Log.e("ProxyCache error-video", "error:" + what + "-extra:" + extra);
        MobclickAgent.reportError(RootApplication.getContext(), "error:" + what + "-extra:" + extra);
        //java.lang.NullPointerException: Attempt to get length of null array，可能出现空指针bug
//        GSYVideoManager.instance().clearDefaultCache(mContext, mCachePath, mOriginUrl);
//        startButtonLogic();
    }
}
