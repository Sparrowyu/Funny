package com.sortinghat.funny.constant;

import com.blankj.utilcode.util.SPUtils;

/**
 * Created by wzy on 2021/3/23
 */
public class Constant {

    public static final String SP_USER_FILE_NAME = "user_info";
    public static final String SP_CONFIG_INFO = "config_info";
    public static final String SP_PERMISSION_INFO = "permission_info";


    //用户信息SP
    public static final String USER_ID = "user_id";


    //sp字段说明
    public static final String PUBLISH_POST_EDUCATION = "publish_post_education";//帖子教育引导是否显示，-1不显示,如果本地发了帖子，则继续修改记为-1
    public static final String PUBLISH_POST_EDUCATION_SHOWED = "publish_post_education_showed";//帖子教育引导是否已经显示过
    public static final String PUBLISH_POST_EDUCATION_SHOWED_TEN = "publish_post_education_showed_ten";//帖子教育三天后是第十次才重新
    public static final String PUBLISH_POST_EDUCATION_DAY = "publish_post_education_day";//3天后
    public static final String PUBLISH_POST_EDUCATION_NUM = "publish_post_education_num";//发帖子教育弹框次数，最多三次
    public static final String POST_VIEW_NUM = "post_view_num";//刷帖子数
    public static final String POST_VIEW_NUM_DAY = "post_view_num_day";//当天刷帖子数
    public static final String POST_VIEW_NUM_DAY_DATE = "post_view_num_day_date";//当天刷帖子数的日期
    public static final String THROUGH_REVIEW_STATE = "through_review_state";//是否通过审核模式，默认通过返回true，可以出广告
    public static final String AD_SPLASH_SHOW = "ad_splash_show";//是否显示开屏广告
    public static final String HOME_AD_IMG_SHOW_SPACE_NUM = "home_ad_img_show_space_num";//是否显示图文列表的广告
    public static final String HOME_AD_SHOW_SPACE_NUM = "home_ad_show_space_num";//是否显示视频列表的广告
    public static final String HOME_VIDEO_AD_NEW_AB = "home_video_ad_new_ab";//是否是视频列表新版AB第三个出的广告
    public static final String AD_FORE_SPLASH_SHOW = "ad_fore_splash_show";//是否显示前台开屏广告

    public static final String START_LOG_STRING_NEW = "start_log_string_new";//日志存到本地的string
    public static final String ANDROID_TASK_SYS_AB = "android_task_sys_ab";//是否任务AB

    public static final String ANDROID_SPLASH_AD_NUM = "android_splash_ad_num";//安卓开屏广告的次数包括后台回来的
    public static final String ANDROID_MOOD_REPORT = "android_mood_report";//安卓报告】

    public static final String PUBLISH_POST_GRADE = "publish_post_grade";//帖子教育引导是否显示，-1不显示,如果本地发了帖子，则继续修改记为-1
    public static final String PUBLISH_POST_GRADE_SHOWED = "publish_post_grade_showed";//显示过弹框
    public static final String PUBLISH_POST_GRADE_CLICKED = "publish_post_grade_clicked";//点过本页【给好评】或【我要吐槽】按钮
    public static final String PUBLISH_POST_GRADE_DAY = "publish_post_grade_day";//3天后
    public static final String PUBLISH_POST_GRADE_NUM = "publish_post_grade_num";//发帖子教育弹框次数，最多三次

    public static final String INSTALLED_AD_COLLECT_DAY = "installed_ad_collect_day";//15天采集一次应用列表
    public static final String HOME_TASK_SHOW = "home_task_show";//首页左上角是否显示任务
    public static final String USER_VIP_TAG = "user_vip_tag";//用户vip标识


    public static long getUserID() {
        return SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong(USER_ID);
    }

    public static void setUserID(long userID) {
        SPUtils.getInstance(Constant.SP_USER_FILE_NAME).put(USER_ID, userID);
    }

}
