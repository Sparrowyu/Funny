package com.sortinghat.funny.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.http.HttpClient;
import com.sortinghat.funny.util.business.RequestParamUtil;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by wzy on 2021/9/26
 */
public class PostViewModel extends FollowViewModel {

    public PostViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<BaseResultBean<HomeVideoImageListBean.ListBean>> getPostInfo(long postId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("postId", postId);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<HomeVideoImageListBean.ListBean>> postInfoLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getPostInfo(requestBody),
                resultBean -> postInfoLiveData.setValue(resultBean),
                throwable -> {
                    postInfoLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return postInfoLiveData;
    }
}
