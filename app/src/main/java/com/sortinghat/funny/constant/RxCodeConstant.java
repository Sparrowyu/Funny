package com.sortinghat.funny.constant;

/**
 * Created by wzy on 2021/6/30
 */
public class RxCodeConstant {

    /**
     * 登录或退出登录
     */
    public static final int LOGIN_STATUS_CHANGE = 1000;

    /**
     * 网络连接
     */
    public static final int NETWORK_CONNECT = 1001;

    /**
     * 更新视频、图片列表
     */
    public static final int UPDATE_VIDEO_IMAGE_LIST = 1;

    /**
     * 发布帖子
     */
    public static final int PUBLISH_POST = 2;

    /**
     * 更新我的页面
     */
    public static final int UPDATE_MYFRAGMENT = 3;

    /**
     * 更新我的页面
     */
    public static final int UPDATE_MYFRAGMENT_HEADER = 4;

    /**
     * 完善信息
     */
    public static final int MY_COMPLETEMENT_INFO = 5;

    /**
     * 更新上一页列表数据
     */
    public static final int MY_UPDATA_LIST_INFO = 6;

    /**
     * 视频详情页更新上一页列表数据，在详情页点关注作者后
     */
    public static final int MY_UPDATA_AUTHOR_INFO = 7;

    /**
     * 视频详情页更新上一页视频列表数据，在详情页操作点赞或者点踩或者关注后
     */
    public static final int HOME_VIDEO_UPDATA_AUTHOR_INFO = 8;

    /**
     * 视频详情页更新上一页图文列表数据，在详情页操作点赞或者点踩或者关注后
     */
    public static final int HOME_IMG_UPDATA_AUTHOR_INFO = 9;

    /**
     * 更新喜欢或屏蔽话题列表
     */
    public static final int UPDATE_LIKE_OR_SHIELD_TOPIC_LIST = 10;

    /**
     * 更新他人信息页面的列表数据
     */
    public static final int MY_OTHER_UPDATA_LIST_INFO = 11;

    /**
     * 详情页更新上一页首页的列表数据，只更新作者是否被关注
     */
    public static final int HOME_VIDEO_UPDATA_AUTHOR_FOLLOW = 12;

    /**
     * 详情页更新上一页用户详情页的列表数据，只更新作者是否被关注
     */
    public static final int USERINFO_UPDATA_AUTHOR_FOLLOW = 13;

    /**
     * 退出登录或者登录之后Id不一样的情况，首页刷新
     */
    public static final int SET_LOGOUT_TO_HOME = 14;//1跳到首页刷新 0：等回到首页再刷新，不用跳

    /**
     * 我的发布页删除帖子后更新列表内层
     */
    public static final int MY_UPDATA_LIST_INFO_DELETE = 15;

    /**
     * 我的发布页删除帖子后更新列表外层
     */
    public static final int MY_HOME_LIST_INFO_DELETE = 16;

    /**
     * 分享微信链接
     */
    public static final int SHARE_WEIXIN_TO_HOME = 17;

    /**
     * 更新帖子的点赞或点踩状态或评论数
     */
    public static final int UPDATE_POST_LIKE_OR_UNLIKE_OR_REVIEW = 18;

    /**
     * 切换元宇宙tab到全部
     */
    public static final int SWITCH_METAVERSE_TAB = 19;

    /**
     * AB实验策略变更时，更新相关的业务
     */
    public static final int UPDATE_EXPERIMENT_STRATEGY = 20;

    /**
     * push前台链接
     */
    public static final int PUSH_FORE_TO_HOME = 22;

    /**
     * 我的页面点击发帖按钮
     */
    public static final int PUBLISH_VIDEO_OR_IMG = 23;

    /**
     * 首页选择弹框按钮，点查看更多
     */
    public static final int HOME_CHOSE_DIALOG_TO_OBJECT_MORE = 24;
    /**
     * 一键登录跳到登录
     */
    public static final int PRE_LOGIN_TO_LOGIN = 25;

    /**
     * 显示金币激励视频
     */
    public static final int TASK_TO_LOAD_REWARD_VIDEO = 26;

    /**
     * 更新任务页的图像框
     */
    public static final int TASK_TO_UPDATE_CENTRAL_USER_BOX = 27;

    /**
     * 更新个人页的图像框
     */
    public static final int TASK_TO_UPDATE_MINE_USER_BOX = 28;
    /**
     * 更新商城页的图像框
     */
    public static final int TASK_TO_UPDATE_STORE_USER_BOX = 29;
    /**
     * 任务页跳到首页
     */
    public static final int OTHER_ACTIVITY_TO_HOME = 30;

    /**
     * 首页视频点踩
     */
    public static final int HOME_VIDEO_UPDATE_DISLIKE = 31;

    /**
     * 首页图文点踩
     */
    public static final int HOME_IMG_UPDATE_DISLIKE = 32;

    /**
     * 其他页点踩
     */
    public static final int MY_VIDEO_UPDATE_DISLIKE = 33;
}
