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

public class ReportViewModel extends BaseListViewModel {

    public ReportViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * idea:图片已经不能表达我的想法意见,示例值(看不顺眼)
     * complain:举报的其他原因,示例值(太血腥)
     * topics:屏蔽话题ID列表，逗号分隔,示例值(1,10004)
     * kind:举报id
     */
    public MutableLiveData<BaseResultBean<Object>> sendReportContent(long postId, String complain, String idea, int kind) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("complain", complain);
        jsonObject.addProperty("idea", idea);
        jsonObject.addProperty("kind", kind);
        jsonObject.addProperty("postId", postId);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<Object>> reportData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().sendReportContent(requestBody),
                resultBean -> reportData.setValue(resultBean),
                throwable -> {
                    reportData.setValue(null);
                    disposeServerException(throwable);
                }));
        return reportData;
    }

    public MutableLiveData<BaseResultBean<Object>> sendCommentReportContent(long commentId, String complain, int kind) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("commentId", commentId);
        jsonObject.addProperty("complain", complain);
        jsonObject.addProperty("kind", kind);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<Object>> reportData = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().sendCommentReportContent(requestBody),
                resultBean -> reportData.setValue(resultBean),
                throwable -> {
                    reportData.setValue(null);
                    disposeServerException(throwable);
                }));
        return reportData;
    }

}
