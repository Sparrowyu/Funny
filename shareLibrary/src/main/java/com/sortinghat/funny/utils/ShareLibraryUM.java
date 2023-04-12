package com.sortinghat.funny.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sortinghat.common.utils.image.GlideUtils;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

public class ShareLibraryUM {

    public static String umengUrl = "http://mobile.umeng.com/social";

    public static void setConfig(String weixin_appid, String weixin_appsecret, String qq_appid, String qq_key, String packageName) {
        // 微信设置
        PlatformConfig.setWeixin(weixin_appid, weixin_appsecret);
        PlatformConfig.setWXFileProvider(packageName + ".fileprovider");
        // QQ设置
        PlatformConfig.setQQZone(qq_appid, qq_key);
        PlatformConfig.setQQFileProvider(packageName + ".fileprovider");

    }
    public static void shareWebUrl(Context mContext, UMShareListener shareListener, int icon, String thumbUrl, SHARE_MEDIA shareMedia, String title, String name, long postId, long shareUserId, int postType) {

        String shareUrl = "https://www.gaoxiaoxingqiu.com";//公司网址
        String shareType = "qq";
        switch (shareMedia) {
            case QQ:
                shareType = "share_qq";
                break;
            case QZONE:
                shareType = "share_qqzone";
                break;
            case SINA:
                shareType = "share_weibo";
                break;
            case WEIXIN:
                shareType = "share_wx";
                break;
            case WEIXIN_CIRCLE:
                shareType = "share_wxmoment";
                break;
            default:
                break;
        }

        shareUrl = "https://share.gaoxiaoxingqiu.com/share.html" + "?postId=" + postId + "&shareTime=" + System.currentTimeMillis()
                + "&shareUserId=" + shareUserId + "&shareType=" + shareType + "&postType=" + postType;
        String shareTitle = title;
        String shareName = name;
        if (TextUtils.isEmpty(title)) {
            shareTitle = "欢迎来到搞笑星球";
        } else {
            if (title.contains("#")) {

            } else {
                shareTitle = title;
            }
        }
        UMWeb web = new UMWeb(shareUrl);
        web.setTitle(shareTitle);//标题
        web.setDescription(shareName);//描述
        try {
            if (TextUtils.isEmpty(thumbUrl)) {
                web.setThumb(new UMImage(mContext, icon));

                new ShareAction((Activity) mContext)
                        .setPlatform(shareMedia)
                        .setCallback(shareListener)
                        .withMedia(web)
                        .share();
            } else {
                GlideUtils.loadImageToBitmap(mContext, thumbUrl, bitmap -> {
                    if (bitmap == null || bitmap.isRecycled()) {
                        web.setThumb(new UMImage(mContext, icon));
                    } else {
                        web.setThumb(new UMImage(mContext, bitmap));
                    }

                    new ShareAction((Activity) mContext)
                            .setPlatform(shareMedia)
                            .setCallback(shareListener)
                            .withMedia(web)
                            .share();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
