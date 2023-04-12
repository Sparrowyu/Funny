package com.sortinghat.funny.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BaseListViewModel;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.HomeVideoDisLikeBean;
import com.sortinghat.funny.bean.HomeVideoLikeBean;
import com.sortinghat.funny.bean.MyFansAttentionListBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.http.HttpClient;
import com.sortinghat.funny.util.business.RequestParamUtil;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MyFansAttentionViewModel extends BaseListViewModel {

    public MyFansAttentionViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<BaseResultBean<List<MyFansAttentionListBean>>> getFansAttentionList(int tabType, int pageNum) {

        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addPagingParam(jsonObject, pageNum, 12);

        RequestParamUtil.addCommonRequestParam(jsonObject);
        final MutableLiveData<BaseResultBean<List<MyFansAttentionListBean>>> fansListBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        if (tabType == 0) {
            addDisposable(execute(HttpClient.Builder.getGeneralServer().getFansList(requestBody),
                    resultBean -> fansListBean.setValue(resultBean),
                    throwable -> {
                        fansListBean.setValue(null);
                        disposeServerException(throwable);
                    }));
        } else {
            addDisposable(execute(HttpClient.Builder.getGeneralServer().getAttentionList(requestBody),
                    resultBean -> fansListBean.setValue(resultBean),
                    throwable -> {
                        fansListBean.setValue(null);
                        disposeServerException(throwable);
                    }));
        }

        return fansListBean;
    }


    public MutableLiveData<BaseResultBean<Object>> getUserFollowList(long followUserId, int opType) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("followUserId", followUserId);
        jsonObject.addProperty("opType", opType);//1： 加关注 0： 取消
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<Object>> publishReviewLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getUserFollowList(requestBody),
                resultBean -> publishReviewLiveData.setValue(resultBean),
                throwable -> {
                    publishReviewLiveData.setValue(null);
                    disposeServerException(throwable);
                }));
        return publishReviewLiveData;
    }

}
