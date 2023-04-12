package com.sortinghat.funny.util.business;

import android.content.Context;
import android.os.Build;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.utils.CheckNetwork;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.RequestInfo;
import com.sortinghat.funny.constant.Constant;

import org.json.JSONObject;

/**
 * Created by wzy on 2021/7/12
 */
public class RequestParamUtil {

    public static void addCommonRequestParam(JsonObject jsonObject) {
        jsonObject.addProperty("userId", SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"));
        jsonObject.addProperty("deviceId", DeviceUtils.getUniqueDeviceId());
    }

    public static void addPagingParam(JsonObject jsonObject, int pageNumber, int pageSize) {
        JsonObject pagingParamJsonObject = new JsonObject();
        pagingParamJsonObject.addProperty("pageNum", pageNumber);
        pagingParamJsonObject.addProperty("pageSize", pageSize);
        jsonObject.add("pageRequest", pagingParamJsonObject);
    }

    public static void addCommonDeviceParam(JsonObject jsonObject, Context context) {
        JsonObject commonParamJsonObject = new JsonObject();
        commonParamJsonObject.addProperty("channel", RequestInfo.getInstance(context).getUmChannel());//渠道
        commonParamJsonObject.addProperty("model", RequestInfo.getInstance(context).getModel());//手机型号
        commonParamJsonObject.addProperty("os", "android");//操作系统
        commonParamJsonObject.addProperty("net", CheckNetwork.isWifiOr4G(context));//网络类型
        commonParamJsonObject.addProperty("appver", RequestInfo.getInstance(context).getVersionName());//App版本号
        jsonObject.add("clientInfo", commonParamJsonObject);
    }

    public static void addLogkitErrParam(JsonObject jsonObject, String errorMsg) {
        JsonObject errParamJsonObject = new JsonObject();
        errParamJsonObject.addProperty("error_code", "crash");//错误码: 崩溃：crash
        errParamJsonObject.addProperty("msg", errorMsg);//错误信息

        jsonObject.add("err", errParamJsonObject);
    }

    public static void addStartLogHeadParam(JsonObject jsonObject, String type, String stype, String path, String provider) {
        jsonObject.addProperty("type", type);//行为类型  string，切前台foreground，切后台background,收集话题 ：userLikeTopic 收集年龄性别信息：information
        jsonObject.addProperty("stype", stype);//分享的用share，前台日志可以不传
        jsonObject.addProperty("path", path.equals("splash") ? "index" : path);////日志发生位置，启动时界面停留位置，（splash）index：首页；mine：个人页；author：作者页，兴趣页:topic
        jsonObject.addProperty("provider", provider);//来源 兴趣页:topic
    }

    //新的埋点通用的设备信息
    public static void addLogkitCommonParam(JSONObject jsonObject, Context context) {
        try {
            jsonObject.putOpt("userId", SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id"));//用户id
            jsonObject.putOpt("app_version", RequestInfo.getInstance(context).getVersionName());//app版本号 例如1.1.4

            jsonObject.putOpt("deviceId", DeviceUtils.getUniqueDeviceId());//设备唯一标识符，目前取android_id
            jsonObject.putOpt("device_type", "android");//
            jsonObject.putOpt("device_model", RequestInfo.getInstance(context).getModel());//手机厂商加信号例如xiaomi_mz10410

            jsonObject.putOpt("device_pixel", RequestInfo.getInstance(context).getScreenWidth() + "*" + RequestInfo.getInstance(context).getScreenHeight());//设备分辨率
            jsonObject.putOpt("device_screensize", "");//设备尺寸，比如：5.0，6.3

            jsonObject.putOpt("device_cpu", "");//ip
            jsonObject.putOpt("device_memory", "");//ip
            jsonObject.putOpt("oaid", SPUtils.getInstance("user_info").getString("umeng_oaid"));//oaid
            jsonObject.putOpt("android_id", DeviceUtils.getUniqueDeviceId());//

            jsonObject.putOpt("is_crack", RequestInfo.getInstance(context).getIsRoot());//是否模拟器
            jsonObject.putOpt("is_simulator", RequestInfo.getInstance(context).isEmulator());//是否模拟器
            jsonObject.putOpt("channel", RequestInfo.getInstance(context).getUmChannel() + "");//友盟渠道
            jsonObject.putOpt("ua_system", CommonUtils.getUserAgent());//ua
            jsonObject.putOpt("os", "android");//
            jsonObject.putOpt("os_version", Build.VERSION.SDK_INT + "");//sdk版本
            jsonObject.putOpt("ip", CheckNetwork.getIpAddress(context));//ip
            jsonObject.putOpt("lang", "" + CheckNetwork.getLocalLanguage());//语言
            jsonObject.putOpt("net", CheckNetwork.isWifiOr4G(context));//网络类型
            jsonObject.putOpt("carrier", "");//
            jsonObject.putOpt("timezone", "");//

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
