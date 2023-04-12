package com.sortinghat.funny.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BaseListViewModel;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.ClientConfigBean;
import com.sortinghat.funny.bean.LoginBean;
import com.sortinghat.funny.bean.MyOwnerUserInfoBean;
import com.sortinghat.funny.bean.RefreshTokenBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.http.HttpClient;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.business.RequestParamUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginViewModel extends BaseListViewModel {

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    //登录类型：游客-0,一键登录-1,手机短信登录-2,微信登录-3,token登录-4,
    public MutableLiveData<BaseResultBean<MyOwnerUserInfoBean>> getLoginId(int loginType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("loginType", loginType);
        jsonObject.addProperty("deviceId", DeviceUtils.getUniqueDeviceId());

        final MutableLiveData<BaseResultBean<MyOwnerUserInfoBean>> loginBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().setLogin(requestBody),
                resultBean -> loginBean.setValue(resultBean),
                throwable -> {
                    loginBean.setValue(null);
                    disposeServerException(throwable);
                }));
        return loginBean;
    }

    //登录类型：0:游客:1一键登录,2:手机短信登录,3:微信登录,4:token登录,5:内部用户名密码登录
    public MutableLiveData<BaseResultBean<MyOwnerUserInfoBean>> getLoginId(int loginType, String mobile, String verifyCode, String opToken, String token, String operator, String account, String passwd) {
        JsonObject jsonObject = new JsonObject();
        String deviceId = DeviceUtils.getUniqueDeviceId();
//        String num1 = "13521929057";//四面
//        String num2 = "15210897562";//我的
//        if (loginType == 2 && (mobile.equals(num1) || mobile.equals(num2))) {
//            verifyCode = "32lk3o";
//        }
        jsonObject.addProperty("loginType", loginType);
        jsonObject.addProperty("mobile", mobile);
        jsonObject.addProperty("verifyCode", verifyCode);

        jsonObject.addProperty("account", account);
        jsonObject.addProperty("passwd", passwd);

        jsonObject.addProperty("opToken", opToken);
        jsonObject.addProperty("token", token);
        jsonObject.addProperty("operator", operator);
        jsonObject.addProperty("deviceId", deviceId);

        final MutableLiveData<BaseResultBean<MyOwnerUserInfoBean>> loginBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().setLogin(requestBody),
                resultBean -> loginBean.setValue(resultBean),
                throwable -> {
                    loginBean.setValue(null);
                    disposeServerException(throwable);
                }));
        return loginBean;
    }

    //取消注销
    public MutableLiveData<BaseResultBean<Object>> setCancelDelete(long userId) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("deviceId", DeviceUtils.getUniqueDeviceId());

        final MutableLiveData<BaseResultBean<Object>> loginBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().setCancelDelete(requestBody),
                resultBean -> loginBean.setValue(resultBean),
                throwable -> {
                    loginBean.setValue(null);
                    disposeServerException(throwable);
                }));
        return loginBean;
    }

    //注销用户:
    public MutableLiveData<BaseResultBean<Object>> setUserDelete(String mobile, String verifyCode) {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("mobile", mobile);
        jsonObject.addProperty("verifyCode", verifyCode);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<Object>> loginBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().setAccountDelete(requestBody),
                resultBean -> loginBean.setValue(resultBean),
                throwable -> {
                    loginBean.setValue(null);
                    disposeServerException(throwable);
                }));
        return loginBean;
    }

    //登录类型：0:游客:一键登录,2:手机短信登录,3:微信登录,4:token登录, 绑定手机号
    public MutableLiveData<BaseResultBean<LoginBean>> getBindPhone(String mobile, String verifyCode) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("mobile", mobile);
        jsonObject.addProperty("verifyCode", verifyCode);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<LoginBean>> loginBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().setBingPhone(requestBody),
                resultBean -> loginBean.setValue(resultBean),
                throwable -> {
                    loginBean.setValue(null);
                    disposeServerException(throwable);
                }));
        return loginBean;
    }

    //退出登录
    public MutableLiveData<BaseResultBean<MyOwnerUserInfoBean>> getLoginOut() {
        JsonObject jsonObject = new JsonObject();
        List<Long> noLookPostIdListVideo = ConstantUtil.getSPList("home_video_request_list");
        List<Long> noLookPostIdListImg = ConstantUtil.getSPList("home_img_request_list");

        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_video_request_list", "");
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("home_img_request_list", "");

        JsonArray jsonArray = new JsonArray();
        for (long i : noLookPostIdListVideo) {
            jsonArray.add(i);
        }
        for (long i : noLookPostIdListImg) {
            jsonArray.add(i);
        }

        jsonObject.add("unexposed_pids", jsonArray);
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<MyOwnerUserInfoBean>> loginBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getLogOut(requestBody),
                resultBean -> loginBean.setValue(resultBean),
                throwable -> {
                    loginBean.setValue(null);
                    disposeServerException(throwable);
                }));
        return loginBean;
    }

    //refreshToken 短期token过期
    public MutableLiveData<BaseResultBean<RefreshTokenBean>> getRefreshToken() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("nickName", "");
        jsonObject.addProperty("longTermToken", SPUtils.getInstance("user_info").getString("longTermToken"));
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<RefreshTokenBean>> loginBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().refreshToken(requestBody),
                resultBean -> loginBean.setValue(resultBean),
                throwable -> {
                    loginBean.setValue(null);
                    disposeServerException(throwable);
                }));
        return loginBean;
    }

    //配置 获取配置接口,不传参数的话全部返回
    public MutableLiveData<BaseResultBean<ClientConfigBean>> getClientConfig() {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);

        final MutableLiveData<BaseResultBean<ClientConfigBean>> configBean = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getClientConfig(requestBody),
                resultBean -> {
                    configBean.setValue(resultBean);
                    ConstantUtil.setConfigSp(resultBean);
                },
                throwable -> {
                    configBean.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return configBean;
    }


}
