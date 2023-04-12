package com.sortinghat.funny.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.sortinghat.common.base.BaseListViewModel;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.http.HttpClient;
import com.sortinghat.funny.util.business.RequestParamUtil;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by wzy on 2021/9/26
 */
public class FollowViewModel extends BaseListViewModel {

    public FollowViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * @param opType 1：加关注 0：取消
     */
    public MutableLiveData<BaseResultBean<Object>> getUserFollowList(long followUserId, int opType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("followUserId", followUserId);
        jsonObject.addProperty("opType", opType);
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
