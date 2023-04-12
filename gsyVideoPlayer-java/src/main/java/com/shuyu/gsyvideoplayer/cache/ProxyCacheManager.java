package com.shuyu.gsyvideoplayer.cache;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.jeffmony.videocache.VideoProxyCacheManager;
import com.jeffmony.videocache.utils.ProxyCacheUtils;
import com.jeffmony.videocache.utils.StorageUtils;
import com.shuyu.gsyvideoplayer.control.LocalProxyVideoControl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 代理缓存管理器
 * Created by guoshuyu on 2018/5/18.
 */
public class ProxyCacheManager implements ICacheManager {

    @Override
    public void doCacheLogic(Context context, LocalProxyVideoControl mLocalProxyVideoControl, IMediaPlayer mediaPlayer, String url, Map<String, String> header, File cachePath) {
        String playUrl = ProxyCacheUtils.getProxyUrl(url, null, null);
        //请求放在客户端,非常便于控制
        mLocalProxyVideoControl.startRequestVideoInfo(url, null, null);
        try {
            mediaPlayer.setDataSource(context, Uri.parse(playUrl), header);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearCache(Context context, File cachePath, String url) {
        try {
            if (TextUtils.isEmpty(url)) {
                VideoProxyCacheManager.getInstance().clear();
                File path = StorageUtils.getIndividualCacheDirectory(context);
                StorageUtils.deleteFile(path);

            } else {
                if (cachePath != null) {
                    VideoProxyCacheManager.getInstance().stopCacheTask(url);
                    File path = new File(cachePath, ProxyCacheUtils.computeMD5(url) + StorageUtils.NON_M3U8_SUFFIX);
                    StorageUtils.deleteFile(path);
                }
            }
        } catch (Exception e) {
            Log.d("ProxyCacheManager", "clearCache: ", e);
            e.printStackTrace();
        }

    }

    @Override
    public void release() {
    }

    @Override
    public boolean hadCached() {
        return false;
    }

    @Override
    public boolean cachePreview(Context context, File cacheDir, String url) {
        return false;
    }

    @Override
    public void setCacheAvailableListener(ICacheAvailableListener cacheAvailableListener) {
    }
}
