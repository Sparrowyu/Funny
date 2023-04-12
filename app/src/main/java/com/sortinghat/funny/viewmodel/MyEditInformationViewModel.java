package com.sortinghat.funny.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.sortinghat.common.base.BaseListViewModel;
import com.sortinghat.common.http.ProgressRequestBody;
import com.sortinghat.funny.bean.BaseResultBean;;
import com.sortinghat.funny.bean.MyOwnerUserInfoBean;
import com.sortinghat.funny.bean.UploadVideoOrImageBean;
import com.sortinghat.funny.http.FileApi;
import com.sortinghat.funny.http.HttpClient;
import com.sortinghat.funny.util.business.RequestParamUtil;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MyEditInformationViewModel extends BaseListViewModel {

    public MyEditInformationViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * avatar:图像地址
     * gender:性别 0: 未设置 1: 男 2: 女,示例值(1)
     * birthday:
     */
    /**
     * 查询我的信息
     */
    public MutableLiveData<BaseResultBean<MyOwnerUserInfoBean>> getOwnerUserInfo() {

        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<MyOwnerUserInfoBean>> reportData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getOwnerUserInfo(requestBody),
                resultBean -> reportData.setValue(resultBean),
                throwable -> {
                    reportData.setValue(null);
                    disposeServerException(throwable);
                }));
        return reportData;
    }

    /**
     * avatar:图像地址
     * gender:性别 0: 未设置 1: 男 2: 女,示例值(1)
     * birthday:
     */
    public MutableLiveData<BaseResultBean<MyOwnerUserInfoBean.UserBaseBean>> completeUserInfo(JsonObject jsonObject) {
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<MyOwnerUserInfoBean.UserBaseBean>> reportData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().completeUserInfo(requestBody),
                resultBean -> reportData.setValue(resultBean),
                throwable -> {
                    reportData.setValue(null);
                    disposeServerException(throwable);
                }));
        return reportData;
    }

    public MutableLiveData<UploadVideoOrImageBean> uploadPostFile(String url, ProgressRequestBody.ProgressListener progressListener) {
        final MutableLiveData<UploadVideoOrImageBean> postFileLiveData = new MutableLiveData<>();

        File file = new File(url);
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse(url), file);
        MultipartBody.Part fileMultipartBody = MultipartBody.Part.createFormData("file", file.getName(), fileRequestBody);


        addDisposable(execute(FileApi.Builder.getUploadFileServer(progressListener).uploadPostFile(fileMultipartBody),
//        addDisposable(execute(FileApi.Builder.getUploadFileServer(progressListener).uploadPostFile(fileMultipartBody, fileSizeBody, fileExpandedNameBody, fileNameMd5Body, shardIndexBody, shardTotalBody, shardSizeBody),
                resultBean -> postFileLiveData.setValue(resultBean),
                throwable -> {
                    postFileLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return postFileLiveData;
    }


}
