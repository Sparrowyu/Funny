package com.sortinghat.funny.thirdparty.alibaba.http;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.HeadObjectRequest;
import com.alibaba.sdk.android.oss.model.HeadObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.model.Range;
import com.blankj.utilcode.util.LogUtils;
import com.sortinghat.funny.interfaces.RequestCallback;

/**
 * 阿里云存储服务
 * Created by wzy on 2021/8/26
 */
public class OssService {

    public OSS oss;
    private String bucketName;

    public OssService(OSS oss, String bucketName) {
        this.oss = oss;
        this.bucketName = bucketName;
    }

    public OSSAsyncTask<HeadObjectResult> asyncGetFileInfo(String objectKey, RequestCallback<HeadObjectResult> requestCallback) {
        HeadObjectRequest head = new HeadObjectRequest(bucketName, objectKey);

        return oss.asyncHeadObject(head, new OSSCompletedCallback<HeadObjectRequest, HeadObjectResult>() {
            @Override
            public void onSuccess(HeadObjectRequest request, HeadObjectResult result) {
                requestCallback.onSuccess(result);
            }

            @Override
            public void onFailure(HeadObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                printErrorLog(clientExcepion, serviceException);
                requestCallback.onFailure();
            }
        });
    }

    public void asyncUploadFile(String objectKey, String uploadFilePath, RequestCallback<PutObjectResult> requestCallback) {
        PutObjectRequest put = new PutObjectRequest(bucketName, objectKey, uploadFilePath);

        put.setProgressCallback((request, currentSize, totalSize) -> requestCallback.updateProgress((int) (100 * currentSize / totalSize)));

        oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                requestCallback.onSuccess(result);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                printErrorLog(clientExcepion, serviceException);
                requestCallback.onFailure();
            }
        });
    }

    public OSSAsyncTask<GetObjectResult> asyncRangeDownloadFile(String objectKey, long begin, long end, RequestCallback<GetObjectResult> requestCallback) {
        GetObjectRequest get = new GetObjectRequest(bucketName, objectKey);

        get.setRange(new Range(begin, end));

        return oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                requestCallback.onSuccess(result);
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                printErrorLog(clientExcepion, serviceException);
                requestCallback.onFailure();
            }
        });
    }

    private void printErrorLog(ClientException clientExcepion, ServiceException serviceException) {
        if (clientExcepion != null) {
            LogUtils.e(Log.getStackTraceString(clientExcepion));
        }
        if (serviceException != null) {
            LogUtils.e("ErrorCode", serviceException.getErrorCode());
            LogUtils.e("RequestId", serviceException.getRequestId());
            LogUtils.e("HostId", serviceException.getHostId());
            LogUtils.e("RawMessage", serviceException.getRawMessage());
        }
    }
}
