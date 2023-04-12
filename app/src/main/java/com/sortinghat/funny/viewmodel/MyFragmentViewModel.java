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
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.bean.MyOwnerUserInfoBean;
import com.sortinghat.funny.bean.OtherUserInfoBean;
import com.sortinghat.funny.bean.TaskMessageBean;
import com.sortinghat.funny.bean.UserMoodReportBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.http.HttpClient;
import com.sortinghat.funny.util.business.RequestParamUtil;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class MyFragmentViewModel extends BaseListViewModel {

    public MyFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * tabType 0:我的发布 1:我的评论 2：我的喜欢
     */
    public MutableLiveData<BaseResultBean<List<HomeVideoImageListBean.ListBean>>> getWorksLikeList(int tabType, int pageNum, int likeType, long otherUserId,int pageSize) {

        JsonObject jsonObject = new JsonObject();
        if (tabType == 2) {
            jsonObject.addProperty("likeType", likeType);
        }
        jsonObject.addProperty("otherUserId", otherUserId == 0 ? SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id") : otherUserId);//查询别人的就传别人的id
        RequestParamUtil.addPagingParam(jsonObject, pageNum, pageSize);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<List<HomeVideoImageListBean.ListBean>>> fansListBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        if (tabType == 0) {
            addDisposable(execute(HttpClient.Builder.getGeneralServer().getWorksMyList(requestBody),
                    resultBean -> fansListBean.setValue(resultBean),
                    throwable -> {
                        fansListBean.setValue(null);
                        disposeServerException(throwable);
                    }));
        } else if (tabType == 1) {
            addDisposable(execute(HttpClient.Builder.getGeneralServer().getWorksCommentList(requestBody),
                    resultBean -> fansListBean.setValue(resultBean),
                    throwable -> {
                        fansListBean.setValue(null);
                        disposeServerException(throwable);
                    }));

        } else {
            addDisposable(execute(HttpClient.Builder.getGeneralServer().getWorksLikeList(requestBody),
                    resultBean -> fansListBean.setValue(resultBean),
                    throwable -> {
                        fansListBean.setValue(null);
                        disposeServerException(throwable);
                    }));

        }
        return fansListBean;
    }

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
                    printServerExceptionLog(throwable);
                }));
        return reportData;
    }

    /**
     * 查询他人信息
     */
    public MutableLiveData<BaseResultBean<OtherUserInfoBean>> getOtherUserInfo(long otherUserId) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("otherUserId", otherUserId);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<OtherUserInfoBean>> reportData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getOtherUserInfo(requestBody),
                resultBean -> reportData.setValue(resultBean),
                throwable -> {
                    reportData.setValue(null);
                    disposeServerException(throwable);
                }));
        return reportData;
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

    public MutableLiveData<BaseResultBean<TaskMessageBean>> getTaskMessageCount() {

        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<TaskMessageBean>> messageCountLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralTaskServer().getTaskMessageCount(requestBody),
                resultBean -> messageCountLiveData.setValue(resultBean),
                throwable -> {
                    messageCountLiveData.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return messageCountLiveData;
    }

    public MutableLiveData<BaseResultBean<UserMoodReportBean>> getUserMoodReport() {

        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<UserMoodReportBean>> messageCountLiveData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getUserMoodReport(requestBody),
                resultBean -> messageCountLiveData.setValue(resultBean),
                throwable -> {
                    messageCountLiveData.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return messageCountLiveData;
    }

}
