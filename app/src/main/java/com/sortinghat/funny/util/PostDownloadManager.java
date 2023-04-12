package com.sortinghat.funny.util;

import android.os.FileUtils;

import com.blankj.utilcode.util.ThreadUtils;
import com.jeffmony.videocache.okhttp.OkHttpControl;
import com.jeffmony.videocache.okhttp.OkHttpManager;
import com.sortinghat.funny.bean.HomeVideoImageListBean;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

public class PostDownloadManager {
    public ConcurrentHashMap<HomeVideoImageListBean.ListBean, DownloadListener> listenerMaps = new ConcurrentHashMap<>();
    private PostDownloadManager() {}

    private static class Holder {
        private static PostDownloadManager instance = new PostDownloadManager();

    }

    public static PostDownloadManager getInstance() {
        return Holder.instance;
    }

    public void download(HomeVideoImageListBean.ListBean videoInfo, String path, DownloadListener downloadListener) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Void>() {
            @Override
            public Void doInBackground() throws Throwable {
                download(videoInfo.getContent().getUrl(), 0, path, downloadListener);
                listenerMaps.put(videoInfo, downloadListener);
                return null;
            }

            @Override
            public void onSuccess(Void result) {

            }


        });
    }

    private void download(String url, long contentLen, String path, DownloadListener listener) {
        if (contentLen <= 0) {
            try {
                OkHttpControl control = OkHttpManager.getInstance().createOkHttpControl(url, null, true);
                contentLen = control.getContentLength();
            } catch (Exception e) {
                listener.onFail(e.getMessage());
                return;
            }
        }
        File file;
        try {
            file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (Exception e) {
            listener.onFail("create file exception: " + e.getMessage());
            return;
        }

        InputStream inputStream = null;
        OutputStream os = null;
        long currentLength = 0;
        try {
            inputStream = OkHttpManager.getInstance().getResponseBody(url, null, contentLength -> {

            });
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte data[] = new byte[1024 * 8];
            int len;
            while ((len = inputStream.read(data)) != -1) {
                os.write(data, 0, len);
                currentLength += len;
                //计算当前下载进度
                listener.onProgress((int) (100 * currentLength / contentLen));
            }
            if (listener != null) {
                listener.onFinish(path);
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onFail(e.getMessage());
            }
        } finally {
            FileUtils.closeQuietly(inputStream);
            FileUtils.closeQuietly(os);
        }
    }

    public DownloadListener getListener(HomeVideoImageListBean.ListBean videoInfo) {
        return listenerMaps.get(videoInfo);
    }

    public DownloadListener removeListener(HomeVideoImageListBean.ListBean videoInfo) {
        return listenerMaps.remove(videoInfo);
    }


    public interface DownloadListener {

        void onProgress(int progress);//下载进度

        void onFinish(String path);//下载完成

        void onFail(String errorInfo);//下载失败
    }


}
