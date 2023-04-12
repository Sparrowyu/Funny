package com.shuyu.gsyvideoplayer.player;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.view.Surface;

import com.shuyu.gsyvideoplayer.cache.ICacheManager;
import com.shuyu.gsyvideoplayer.control.LocalProxyVideoControl;
import com.shuyu.gsyvideoplayer.control.PlayerSettings;
import com.shuyu.gsyvideoplayer.model.GSYModel;
import com.shuyu.gsyvideoplayer.model.VideoOptionModel;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.RawDataSourceProvider;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkLibLoader;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

/**
 * IJKPLayer
 * Created by guoshuyu on 2018/1/11.
 */

public class IjkPlayerManager extends BasePlayerManager {

    /**
     * log level
     */
    private static int logLevel = IjkMediaPlayer.IJK_LOG_DEFAULT;

    private static IjkLibLoader ijkLibLoader;

    private IjkMediaPlayer mediaPlayer;

    private List<VideoOptionModel> optionModelList;

    private Surface surface;


    @Override
    public IMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void initVideoPlayer(Context context, Message msg, List<VideoOptionModel> optionModelList, ICacheManager cacheManager) {
        mediaPlayer = (ijkLibLoader == null) ? new IjkMediaPlayer() : new IjkMediaPlayer(ijkLibLoader);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnNativeInvokeListener(new IjkMediaPlayer.OnNativeInvokeListener() {
            @Override
            public boolean onNativeInvoke(int i, Bundle bundle) {
                return true;
            }
        });

        GSYModel gsyModel = (GSYModel) msg.obj;
        String url = gsyModel.getUrl();

        mLocalProxyVideoControl = new LocalProxyVideoControl(this);
        PlayerSettings playerSettings = new PlayerSettings();
        playerSettings.setLocalProxyEnable(gsyModel.isCache());
        initPlayerSettings(playerSettings);

        try {
            //开启硬解码
            if (GSYVideoType.isMediaCodec()) {
                Debuger.printfLog("enable mediaCodec");
                mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
                mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
                mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
            }

            if (gsyModel.isCache() && cacheManager != null) {
                cacheManager.doCacheLogic(context, mLocalProxyVideoControl, mediaPlayer, url, gsyModel.getMapHeadData(), gsyModel.getCachePath());
            } else {
                if (!TextUtils.isEmpty(url)) {
                    Uri uri = Uri.parse(url);
                    if (uri != null && uri.getScheme() != null && uri.getScheme().equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                        RawDataSourceProvider rawDataSourceProvider = RawDataSourceProvider.create(context, uri);
                        mediaPlayer.setDataSource(rawDataSourceProvider);
                    } else if (uri != null && uri.getScheme() != null && uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                        ParcelFileDescriptor descriptor;
                        try {
                            descriptor = context.getContentResolver().openFileDescriptor(uri, "r");
                            FileDescriptor fileDescriptor = descriptor.getFileDescriptor();
                            mediaPlayer.setDataSource(fileDescriptor);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        mediaPlayer.setDataSource(url, gsyModel.getMapHeadData());
                    }
                } else {
                    mediaPlayer.setDataSource(url, gsyModel.getMapHeadData());
                }
            }

            mediaPlayer.setLooping(gsyModel.isLooping());
            if (gsyModel.getSpeed() != 1 && gsyModel.getSpeed() > 0) {
                mediaPlayer.setSpeed(gsyModel.getSpeed());
            }
            mediaPlayer.native_setLogLevel(logLevel);

            // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
            // 设置分析时长
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration",1);
            // 每处理一个packet之后刷新io上下文，通过立即清理数据包来减少等待时长
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
            // 是否开启预缓冲，一般直播项目会开启，达到秒开的效果，不过带来了播放丢帧卡顿的体验
            // mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0L);

            //播放前的探测Size，默认是1M, 改小一点会出画面更快
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,"probesize",1024 * 10);

            // 去掉音频
            //mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "an", 1);
            // 不查询stream_info，直接使用
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"find_stream_info", 0);
            // 等待开始之后才绘制
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "render-wait-start", 1);

            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"reconnect", 5);
            //跳帧处理,放CPU处理较慢时，进行跳帧处理，保证播放流程，画面和声音同步
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"framedrop", 2);

            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"max-fps",30);
            //设置seekTo能够快速seek到指定位置并播放，解决m3u8文件拖动问题
            //   比如:一个3个多少小时的音频文件，开始播放几秒中，然后拖动到2小时左右的时间，要loading 10分钟
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");
            // fix某些视频在SeekTo的时候，会跳回到拖动前的位置
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);

            //清理dns缓存，解决wifi与3g/4g/5g切换视频不能播放问题
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_timeout", -1);

            initIJKOption(mediaPlayer, optionModelList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initSuccess(gsyModel);
    }

    @Override
    public void showDisplay(Message msg) {
        if (msg.obj == null && mediaPlayer != null) {
            mediaPlayer.setSurface(null);
        } else {
            Surface holder = (Surface) msg.obj;
            surface = holder;
            if (mediaPlayer != null && holder.isValid()) {
                mediaPlayer.setSurface(holder);
            }
        }
    }

    @Override
    public void setSpeed(float speed, boolean soundTouch) {
        if (speed > 0) {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.setSpeed(speed);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (soundTouch) {
                VideoOptionModel videoOptionModel =
                        new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
                List<VideoOptionModel> list = getOptionModelList();
                if (list != null) {
                    list.add(videoOptionModel);
                } else {
                    list = new ArrayList<>();
                    list.add(videoOptionModel);
                }
                setOptionModelList(list);
            }

        }
    }

    @Override
    public void setNeedMute(boolean needMute) {
        if (mediaPlayer != null) {
            if (needMute) {
                mediaPlayer.setVolume(0, 0);
            } else {
                mediaPlayer.setVolume(1, 1);
            }
        }
    }

    @Override
    public void setVolume(float left, float right) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(left, right);
        }
    }

    @Override
    public void releaseSurface() {
        if (surface != null) {
            //surface.release();
            surface = null;
        }
    }

    @Override
    public void release() {
        if (mPlayerSettings.getLocalProxyEnable()) {
            mLocalProxyVideoControl.releaseLocalProxyResources();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public int getBufferedPercentage() {
        return -1;
    }

    @Override
    public long getNetSpeed() {
        if (mediaPlayer != null) {
            return mediaPlayer.getTcpSpeed();
        }
        return 0;
    }

    @Override
    public void setSpeedPlaying(float speed, boolean soundTouch) {
        if (mediaPlayer != null) {
            mediaPlayer.setSpeed(speed);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", (soundTouch) ? 1 : 0);
        }
    }

    @Override
    public void start() {
        if (mPlayerSettings.getLocalProxyEnable()) {
            mLocalProxyVideoControl.resumeLocalProxyTask();
        }
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void pause() {
        if (mPlayerSettings.getLocalProxyEnable()) {
            mLocalProxyVideoControl.pauseLocalProxyTask();
        }
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public int getVideoWidth() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoWidth();
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoHeight();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void seekTo(long time) {
        if (mPlayerSettings.getLocalProxyEnable()) {
            mLocalProxyVideoControl.seekToCachePosition(time);
        }
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(time);
        }
    }

    public long getBufferedPosition() {
        if (mPlayerSettings.getLocalProxyEnable()) {
            return (long) (mProxyCachePercent * getDuration() / 100);
        }
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getVideoSarNum() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoSarNum();
        }
        return 1;
    }

    @Override
    public int getVideoSarDen() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoSarDen();
        }
        return 1;
    }


    @Override
    public boolean isSurfaceSupportLockCanvas() {
        return true;
    }


    public IjkTrackInfo[] getTrackInfo() {
        if (mediaPlayer != null) {
            return mediaPlayer.getTrackInfo();
        }
        return null;
    }

    public int getSelectedTrack(int trackType) {
        if (mediaPlayer != null) {
            return mediaPlayer.getSelectedTrack(trackType);
        }
        return -1;
    }

    public void selectTrack(int track) {
        if (mediaPlayer != null) {
            mediaPlayer.selectTrack(track);
        }
    }

    public void deselectTrack(int track) {
        if (mediaPlayer != null) {
            mediaPlayer.deselectTrack(track);
        }
    }

    private void initIJKOption(IjkMediaPlayer ijkMediaPlayer, List<VideoOptionModel> optionModelList) {
        if (optionModelList != null && optionModelList.size() > 0) {
            for (VideoOptionModel videoOptionModel : optionModelList) {
                if (videoOptionModel.getValueType() == VideoOptionModel.VALUE_TYPE_INT) {
                    ijkMediaPlayer.setOption(videoOptionModel.getCategory(),
                            videoOptionModel.getName(), videoOptionModel.getValueInt());
                } else {
                    ijkMediaPlayer.setOption(videoOptionModel.getCategory(),
                            videoOptionModel.getName(), videoOptionModel.getValueString());
                }
            }
        }
    }

    public List<VideoOptionModel> getOptionModelList() {
        return optionModelList;
    }

    public void setOptionModelList(List<VideoOptionModel> optionModelList) {
        this.optionModelList = optionModelList;
    }

    public static IjkLibLoader getIjkLibLoader() {
        return ijkLibLoader;
    }

    public static void setIjkLibLoader(IjkLibLoader ijkLibLoader) {
        IjkPlayerManager.ijkLibLoader = ijkLibLoader;
    }

    public static int getLogLevel() {
        return logLevel;
    }

    public static void setLogLevel(int logLevel) {
        IjkPlayerManager.logLevel = logLevel;
    }
}