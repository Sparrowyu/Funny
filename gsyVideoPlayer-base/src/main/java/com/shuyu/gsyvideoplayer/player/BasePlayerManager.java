package com.shuyu.gsyvideoplayer.player;

import androidx.annotation.NonNull;

import com.jeffmony.videocache.common.ProxyMessage;
import com.jeffmony.videocache.common.VideoParams;
import com.jeffmony.videocache.utils.LogUtils;
import com.jeffmony.videocache.utils.VideoParamsUtils;
import com.jeffmony.videocache.utils.VideoProxyThreadUtils;
import com.shuyu.gsyvideoplayer.control.LocalProxyVideoControl;
import com.shuyu.gsyvideoplayer.control.PlayerSettings;
import com.shuyu.gsyvideoplayer.model.GSYModel;

import java.util.Map;

/**
 * 播放器差异管理接口
 * Created by guoshuyu on 2018/1/11.
 */

public abstract class BasePlayerManager implements IPlayerManager {

    protected IPlayerInitSuccessListener mPlayerInitSuccessListener;

    public IPlayerInitSuccessListener getPlayerPreparedSuccessListener() {
        return mPlayerInitSuccessListener;
    }

    public void setPlayerInitSuccessListener(IPlayerInitSuccessListener listener) {
        this.mPlayerInitSuccessListener = listener;
    }

    protected void initSuccess(GSYModel gsyModel) {
        if (mPlayerInitSuccessListener != null) {
            mPlayerInitSuccessListener.onPlayerInitSuccess(getMediaPlayer(), gsyModel);
        }
    }

    protected LocalProxyVideoControl mLocalProxyVideoControl;

    protected PlayerSettings mPlayerSettings;

    protected float mProxyCachePercent = 0f;
    protected float mPlayPercent = 0f;

    private IPlayerManager.OnProxyCacheInfoListener mOnProxyCacheInfoListener;

    public void setOnProxyCacheInfoListener(IPlayerManager.OnProxyCacheInfoListener onProxyCacheInfoListener) {
        mOnProxyCacheInfoListener = onProxyCacheInfoListener;
    }

    public LocalProxyVideoControl getLocalProxyVideoControl() {
        return mLocalProxyVideoControl;
    }

    public void initPlayerSettings(@NonNull PlayerSettings settings) {
        mPlayerSettings = settings;
    }

    public float getProxyCachePercent() {
        return mProxyCachePercent;
    }

    public float getPlayPercent() {
        return mPlayPercent;
    }

    public void setPlayPercent(float playPercent) {
        this.mPlayPercent = playPercent;
    }

    public void notifyOnProxyCacheInfo(int msg, Map<String, Object> params) {
        VideoProxyThreadUtils.runOnUiThread(() -> {
            if (mOnProxyCacheInfoListener != null) {
                mOnProxyCacheInfoListener.onProxyCacheInfo(msg, params);
            }
            if (msg == ProxyMessage.MSG_VIDEO_PROXY_PROGRESS || msg == ProxyMessage.MSG_VIDEO_PROXY_COMPLETED
                    || msg == ProxyMessage.MSG_VIDEO_PROXY_RANGE_COMPLETED) {
                mProxyCachePercent = VideoParamsUtils.getFloatValue(params, VideoParams.PERCENT);
            } else if (msg == ProxyMessage.MSG_VIDEO_PROXY_FORBIDDEN) {
                mPlayerSettings.setLocalProxyEnable(false);
            }
        });
    }
}
