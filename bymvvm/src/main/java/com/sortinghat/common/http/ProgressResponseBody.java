package com.sortinghat.common.http;

import android.util.Log;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by wzy on 2021/7/1
 */
public class ProgressResponseBody extends ResponseBody {

    private ResponseBody responseBody;
    private ProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        Log.i("ProgressResponseBody", "--------------------------" + responseBody.contentLength());
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (null == bufferedSource) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                if (progressListener != null) {
                    progressListener.onProgress(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }

    public interface ProgressListener {
        /**
         * @param progress 已经下载字节数
         * @param total    总字节数
         * @param done     是否完成
         */
        void onProgress(long progress, long total, boolean done);
    }
}
