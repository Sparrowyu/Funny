package com.sortinghat.funny.http;

import com.sortinghat.common.http.HttpUtils;
import com.sortinghat.common.utils.BuildFactory;
import com.sortinghat.funny.bean.AuditNoticeMessageBean;
import com.sortinghat.funny.bean.BannerBean;
import com.sortinghat.funny.bean.BaseResultBean;
import com.sortinghat.funny.bean.ClientConfigBean;
import com.sortinghat.funny.bean.ClientSystemConfigBean;
import com.sortinghat.funny.bean.FansMessageBean;
import com.sortinghat.funny.bean.HomeVideoDisLikeBean;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.bean.HomeVideoLikeBean;
import com.sortinghat.funny.bean.LoginBean;
import com.sortinghat.funny.bean.MessageCountBean;
import com.sortinghat.funny.bean.MyFansAttentionListBean;
import com.sortinghat.funny.bean.MyOwnerUserInfoBean;
import com.sortinghat.funny.bean.OtherUserInfoBean;
import com.sortinghat.funny.bean.PostReviewBean;
import com.sortinghat.funny.bean.PraiseMessageBean;
import com.sortinghat.funny.bean.RefreshTokenBean;
import com.sortinghat.funny.bean.ReviewAndReplyMessageBean;
import com.sortinghat.funny.bean.StoreTaskListBean;
import com.sortinghat.funny.bean.StoreTaskMessageListBean;
import com.sortinghat.funny.bean.TaskCentralBean;
import com.sortinghat.funny.bean.TaskCommonToastBean;
import com.sortinghat.funny.bean.TaskMessageBean;
import com.sortinghat.funny.bean.TaskPackageItemBean;
import com.sortinghat.funny.bean.TaskRankHeaderBean;
import com.sortinghat.funny.bean.TaskRankListBean;
import com.sortinghat.funny.bean.TaskTodayItemBean;
import com.sortinghat.funny.bean.TopicBean;
import com.sortinghat.funny.bean.TopicHomeChoseDialogBean;
import com.sortinghat.funny.bean.TopicListBean;
import com.sortinghat.funny.bean.UserMoodReportBean;
import com.sortinghat.funny.bean.event.SystemNoticeBean;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 网络请求类（一个接口一个方法）
 * Created by wzy on 2021/3/10
 */
public interface HttpClient {

    /**
     * 创建接口API对象（一个 baseUrl一个方法）
     */
    class Builder {
        public static HttpClient getGeneralServer() {
            return BuildFactory.getInstance().create(HttpClient.class, HttpUtils.funnyBaseUrl);
        }

        public static HttpClient getGeneralTaskServer() {
            return BuildFactory.getInstance().create(HttpClient.class, HttpUtils.funnyTaskBaseUrl);
        }
    }

    /**
     * 登录
     */
    @POST("login")
    Observable<BaseResultBean<MyOwnerUserInfoBean>> setLogin(@Body RequestBody body);

    /**
     * 绑定手机号
     */
    @POST("account/bindMobile")
    Observable<BaseResultBean<LoginBean>> setBingPhone(@Body RequestBody body);

    /**
     * 注销
     */
    @POST("account/delete")
    Observable<BaseResultBean<Object>> setAccountDelete(@Body RequestBody body);

    /**
     * 登录-取消注销
     */
    @POST("account/cancelDelete")
    Observable<BaseResultBean<Object>> setCancelDelete(@Body RequestBody body);

    /**
     * 首页-视频列表
     */
    @POST("getHomePosts")
    Observable<BaseResultBean<HomeVideoImageListBean>> getHomeVideoList(@Body RequestBody body);

    /**
     * 我的页面获取我的评论列表
     */
    @POST("user/userCommentPosts")
    Observable<BaseResultBean<List<HomeVideoImageListBean.ListBean>>> getWorksCommentList(@Body RequestBody body);

    /**
     * 我的页面获取我的发布，或者别人的作品
     */
    @POST("user/userPost")
    Observable<BaseResultBean<List<HomeVideoImageListBean.ListBean>>> getWorksMyList(@Body RequestBody body);

    /**
     * 我的页面完善用户信息
     */
    @POST("user/completeUserInfo")
    Observable<BaseResultBean<MyOwnerUserInfoBean.UserBaseBean>> completeUserInfo(@Body RequestBody body);

    /**
     * 首页-视频点赞
     */
    @POST("updateLike")
    Observable<BaseResultBean<HomeVideoLikeBean>> setHomeVideoLike(@Body RequestBody body);

    /**
     * 首页-视频点踩
     */
    @POST("updateDisLike")
    Observable<BaseResultBean<HomeVideoDisLikeBean>> setHomeVideoDisLike(@Body RequestBody body);

    /**
     * 首页-获取帖子评论
     */
    @POST("queryComment")
    Observable<BaseResultBean<List<PostReviewBean>>> getPostReview(@Body RequestBody requestBody);

    /**
     * 首页-发布帖子评论或评论回复或评论回复的回复
     */
    @POST("pubComment")
    Observable<BaseResultBean<Long>> publishReview(@Body RequestBody requestBody);

    /**
     * 首页-帖子评论或评论回复或评论回复的回复点赞、点踩
     */
    @POST("updateCommentLike")
    Observable<BaseResultBean> postReviewLikeOrUnlike(@Body RequestBody requestBody);

    /**
     * 首页-删除帖子评论或评论回复或评论回复的回复
     */
    @POST("deleteComment")
    Observable<BaseResultBean> deleteReview(@Body RequestBody requestBody);

    @POST("topic/homePageBanner")
    Observable<BaseResultBean<List<BannerBean>>> getTopicBanners(@Body RequestBody requestBody);

    /**
     * 话题-发现、喜欢、屏蔽的话题列表
     */
    @POST("topic/userLikeList")
    Observable<BaseResultBean<List<TopicListBean>>> getTopicList(@Body RequestBody requestBody);

    /**
     * 话题-全部话题列表
     */
    @POST("topic/getSystemTopicAndThumb")
    Observable<BaseResultBean<List<TopicBean>>> getAllTopicList(@Body RequestBody requestBody);

    /**
     * 话题-话题详情
     */
    @POST("topic/topicRecommendPostList")
    Observable<BaseResultBean<HomeVideoImageListBean>> getTopicRelationPostList(@Body RequestBody requestBody);

   /**
     * 话题-最新
     */
    @POST("topic/topicPostByCreateDayDesc")
    Observable<BaseResultBean<HomeVideoImageListBean>> getTopicByCreateDayDescList(@Body RequestBody requestBody);

    /**
     * 话题-喜欢、屏蔽、取消喜欢、取消屏蔽
     */
    @POST("topic/userLikeManager")
    Observable<BaseResultBean<Object>> likeOrShieldTopic(@Body RequestBody requestBody);

    /**
     * 发布-推荐话题列表
     */
    @POST("topic/getRecommend")
    Observable<BaseResultBean<List<TopicBean.SubTopicBean>>> getRecommendTopicList(@Body RequestBody requestBody);

    /**
     * 发布-系统话题列表
     */
    @POST("topic/getSystem")
    Observable<BaseResultBean<List<TopicBean>>> getSystemTopicList(@Body RequestBody requestBody);

    /**
     * 发布帖子
     */
    @POST("createPost")
    Observable<BaseResultBean<Long>> publishPost(@Body RequestBody requestBody);

    /**
     * 消息-未读消息数
     */
    @POST("message/count")
    Observable<BaseResultBean<MessageCountBean>> getMessageCount(@Body RequestBody requestBody);

    /**
     * 消息-获取粉丝消息
     */
    @POST("message/unReadMessages")
    Observable<BaseResultBean<List<FansMessageBean>>> getFansMessageDetailsData(@Body RequestBody requestBody);

    /**
     * 消息-获取评论与回复消息
     */
    @POST("message/unReadMessages")
    Observable<BaseResultBean<List<ReviewAndReplyMessageBean>>> getReviewAndReplyMessageDetailsData(@Body RequestBody requestBody);

    /**
     * 消息-获取点赞消息
     */
    @POST("message/unReadMessages")
    Observable<BaseResultBean<List<PraiseMessageBean>>> getPraiseMessageDetailsData(@Body RequestBody requestBody);

    /**
     * 消息-获取系统通知消息
     */
    @POST("message/unReadMessages")
    Observable<BaseResultBean<List<SystemNoticeBean>>> getSystemNoticeMessageDetailsData(@Body RequestBody requestBody);

    /**
     * 消息-获取审核通知消息
     */
    @POST("message/unReadMessages")
    Observable<BaseResultBean<List<AuditNoticeMessageBean>>> getAuditNoticeMessageDetailsData(@Body RequestBody requestBody);

    /**
     * 粉丝列表
     */
    @POST("user/myFans")
    Observable<BaseResultBean<List<MyFansAttentionListBean>>> getFansList(@Body RequestBody body);

    /**
     * 关注列表
     */
    @POST("user/myFollow")
    Observable<BaseResultBean<List<MyFansAttentionListBean>>> getAttentionList(@Body RequestBody body);

    /**
     * 举报帖子
     */
    @POST("pickDislikeReason")
    Observable<BaseResultBean<Object>> sendReportContent(@Body RequestBody requestBody);

    /**
     * 举报评论
     */
    @POST("comment/pickDislikeReason")
    Observable<BaseResultBean<Object>> sendCommentReportContent(@Body RequestBody requestBody);

    /**
     * 查询-我的信息
     */
    @POST("user/ownerUserInfo")
    Observable<BaseResultBean<MyOwnerUserInfoBean>> getOwnerUserInfo(@Body RequestBody requestBody);

    /**
     * 查询-他人的信息
     */
    @POST("user/otherUserInfo")
    Observable<BaseResultBean<OtherUserInfoBean>> getOtherUserInfo(@Body RequestBody requestBody);

    /**
     * 我的页面获取喜欢列表
     */
    @POST("user/userLikePosts")
    Observable<BaseResultBean<List<HomeVideoImageListBean.ListBean>>> getWorksLikeList(@Body RequestBody body);

    /**
     * 关注
     */
    @POST("follow/userFollow")
    Observable<BaseResultBean<Object>> getUserFollowList(@Body RequestBody body);

    /**
     * 退出登录
     */
    @POST("account/logout")
    Observable<BaseResultBean<MyOwnerUserInfoBean>> getLogOut(@Body RequestBody body);


    /**
     * 刷新token
     */
    @POST("refreshToken")
    Observable<BaseResultBean<RefreshTokenBean>> refreshToken(@Body RequestBody body);

    /**
     * 获取帖子信息
     */
    @POST("showPost")
    Observable<BaseResultBean<HomeVideoImageListBean.ListBean>> getPostInfo(@Body RequestBody body);

    /**
     * 删除帖子
     */
    @POST("deletePost")
    Observable<BaseResultBean<Object>> getDeletePost(@Body RequestBody body);

    /**
     * 配置接口
     */
    @POST("clientConfig")
    Observable<BaseResultBean<ClientConfigBean>> getClientConfig(@Body RequestBody body);

    /**
     * 收集话题
     */
    @POST("collect/userLikeTopic")
    Observable<BaseResultBean<List<TopicHomeChoseDialogBean>>> getUserLikeTopic(@Body RequestBody body);

    /**
     * 收集年龄性别信息
     */
    @POST("collection/information")
    Observable<BaseResultBean<Object>> getCollectionInformation(@Body RequestBody body);
   /**
     * 收集吐槽
     */
    @POST("collection/information")
    Observable<BaseResultBean<Object>> getUserCriticizeApp(@Body RequestBody body);

    /**
     * 商城
     */
    @POST("store/list")
    Observable<BaseResultBean<StoreTaskListBean>> getStoreTaskList(@Body RequestBody body);

    /**
     * 背包
     */
    @POST("bag/personalbag/list")
    Observable<BaseResultBean<List<TaskPackageItemBean>>> getPackageList(@Body RequestBody body);

    /**
     * 获取任务列表
     */
    @POST("sysConfig/list")
    Observable<BaseResultBean<List<TaskTodayItemBean>>> getTaskTodayList(@Body RequestBody body);

    /**
     * 获取任务
     */
    @POST("getSignInfo")
    Observable<BaseResultBean<TaskCentralBean>> getTaskCentralData(@Body RequestBody body);

    /**
     * * 签到
     */
    @POST("doSign")
    Observable<BaseResultBean<TaskCommonToastBean>> getTaskDoSign(@Body RequestBody body);

    /**
     * * 背包删除
     */
    @POST("bag/delgoods")
    Observable<BaseResultBean<Object>> getDelPackageIcon(@Body RequestBody body);

    /**
     * * 背包里面装备图像
     */
    @POST("bag/dress/goods")
    Observable<BaseResultBean<TaskCommonToastBean>> setPackageIcon(@Body RequestBody body);

    /**
     * * 任务奖励
     */
    @POST("receiveAward")
    Observable<BaseResultBean<TaskCommonToastBean>> getTaskReceiveAward(@Body RequestBody body);

    /**
     * * 签到双倍奖励
     */
    @POST("double/receiveAward")
    Observable<BaseResultBean<TaskCommonToastBean>> getTaskDoubleReceiveAward(@Body RequestBody body);

    /**
     * 填写邀请码
     */
    @POST("invite/friends")
    Observable<BaseResultBean<Object>> getInviteFriendCode(@Body RequestBody body);

    /**
     * 兑换商品
     */
    @POST("store/exchange/product")
    Observable<BaseResultBean<TaskCommonToastBean>> getExchangeStore(@Body RequestBody body);

    /**
     * 语音播报
     */
    @POST("store/broadcast")
    Observable<BaseResultBean<List<StoreTaskMessageListBean>>> getStoreBroadcastMsg(@Body RequestBody body);

    /**
     * 任务红点逻辑
     */
    @POST("completion/reminder")
    Observable<BaseResultBean<TaskMessageBean>> getTaskMessageCount(@Body RequestBody body);

    /**
     * 我的排行
     */
    @POST("getMyrank")
    Observable<BaseResultBean<TaskRankHeaderBean>> getMyrank(@Body RequestBody body);

    /**
     * 排行榜
     */
    @POST("rank/list")
    Observable<BaseResultBean<List<TaskRankListBean>>> getRankList(@Body RequestBody body);

    /**
     * 配置接口
     */
    @POST("clientSystemConfig")
    Observable<BaseResultBean<ClientSystemConfigBean>> getSystemClientConfig(@Body RequestBody body);
    /**
     * 配置接口
     */
    @POST("user/moodReport")
    Observable<BaseResultBean<UserMoodReportBean>> getUserMoodReport(@Body RequestBody body);
}