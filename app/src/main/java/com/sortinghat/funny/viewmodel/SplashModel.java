package com.sortinghat.funny.viewmodel;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BaseListViewModel;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.ClientConfigBean;
import com.sortinghat.funny.bean.ClientSystemConfigBean;
import com.sortinghat.funny.bean.MyOwnerUserInfoBean;
import com.sortinghat.funny.bean.RefreshTokenBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.http.HttpClient;
import com.sortinghat.funny.http.LogApi;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.DateUtil;
import com.sortinghat.funny.util.business.RequestParamUtil;
import com.sortinghat.fymUpdate.common.ICallBack;
import com.sortinghat.fymUpdate.retrofit.api.RentApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SplashModel extends BaseListViewModel {

    public SplashModel(@NonNull Application application) {
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

    /**
     * 埋点统计 帖子日志，行为日志，前后台日志统一的
     *
     * @param jsonString jsonString
     */
    public MutableLiveData<Object> setAppUnifyLog(String jsonString, Context context) {
        JSONObject jsonObject = new JSONObject();
        RequestParamUtil.addLogkitCommonParam(jsonObject, context);

        String start_log_string = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString(Constant.START_LOG_STRING_NEW);
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(Constant.START_LOG_STRING_NEW, "");
        if (!TextUtils.isEmpty(start_log_string)) {
            jsonString = start_log_string + ConstantUtil.LogListTag + jsonString;
        }

        try {
            JSONArray jsonArrayData = new JSONArray();
            String[] spArrayString = jsonString.split(ConstantUtil.LogListTag);
            if (spArrayString.length > 0) {
                for (int i = 0; i < spArrayString.length; i++) {
                    JSONObject myjson = null;
                    myjson = new JSONObject(spArrayString[i]);
                    jsonArrayData.put(myjson);
                }
            }
            jsonObject.putOpt("data", jsonArrayData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("start-log--app-success", "splashModel:" + jsonString);
        final MutableLiveData<Object> setAppUnifyLog = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        String finalJsonString = jsonString;
        addDisposable(execute(LogApi.Builder.getUnifyLogServer().setAppUnifyLog(requestBody),
                resultBean -> {
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(Constant.START_LOG_STRING_NEW, "");
                    setAppUnifyLog.setValue(resultBean);
                },
                throwable -> {
                    SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(Constant.START_LOG_STRING_NEW, finalJsonString);
                    setAppUnifyLog.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return setAppUnifyLog;
    }

    /**
     * 埋点统计 帖子日志，行为日志，前后台日志统一的
     *
     * @param jsonObject jsonString
     */
    public MutableLiveData<Object> setAppUnifyLogNew(JSONObject jsonObject, Context context) {
//        JSONObject jsonObject = new JSONObject();
        RequestParamUtil.addLogkitCommonParam(jsonObject, context);
        Log.d("start-log--app-success", "splashModel:" + jsonObject.toString());
        final MutableLiveData<Object> setAppUnifyLog = new MutableLiveData<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(LogApi.Builder.getUnifyLogServer().setAppUnifyLog(requestBody),
                resultBean -> {
                    setAppUnifyLog.setValue(resultBean);
                },
                throwable -> {
                    setAppUnifyLog.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return setAppUnifyLog;
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
    //配置 获取配置接口,不传参数的话全部返回
    public MutableLiveData<BaseResultBean<ClientSystemConfigBean>> getSystemClientConfig() {
        JsonObject jsonObject = new JsonObject();
        RequestParamUtil.addCommonRequestParam(jsonObject);
        final MutableLiveData<BaseResultBean<ClientSystemConfigBean>> configBean = new MutableLiveData<>();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        addDisposable(execute(HttpClient.Builder.getGeneralServer().getSystemClientConfig(requestBody),
                resultBean -> {
                    configBean.setValue(resultBean);
                    ConstantUtil.setSystemConfigSp(resultBean);
                },
                throwable -> {
                    configBean.setValue(null);
                    printServerExceptionLog(throwable);
                }));
        return configBean;
    }

    public void getCityBySohu(Context mContext) {
        //7天重新获取一个
        String sCode = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("area_code");
        String time = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getString("city_time");
        if (!TextUtils.isEmpty(sCode) && DateUtil.getDateFromNetToProgressDay(time) < 7) {
            return;
        }
        RentApi.get(mContext, "https://pv.sohu.com/cityjson", new ICallBack() {
            @Override
            public void result(String str) {
                //var returnCitySN = {"cip": "124.204.41.66", "cid": "110000", "cname": "北京市"};
                try {
                    if (!TextUtils.isEmpty(str) && str.contains("cid")) {

                        String strJson = str.substring(str.indexOf("{"), str.length() - 1);
                        JSONObject jsonObject = new JSONObject(strJson.toString());
                        String cityCode = jsonObject.optString("cid");
                        String cityName = jsonObject.optString("cname");

                        if (!TextUtils.isEmpty(cityCode) && !TextUtils.isEmpty(cityName)) {
                            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("area_code", cityCode);
                            SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put("city_time", DateUtil.getTodayDateStringToServer());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
