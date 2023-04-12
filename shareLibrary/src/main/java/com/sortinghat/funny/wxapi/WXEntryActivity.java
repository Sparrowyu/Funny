package com.sortinghat.funny.wxapi;

import android.content.ComponentName;
import android.content.Intent;
import android.text.TextUtils;

import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.funny.utils.OnMultiClickUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.umeng.socialize.utils.SLog;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {

    private int SHARE_WEIXIN_TO_HOME = 17;


    public void onResp(BaseResp resp) {
        SLog.I("WXCallbackActivity 分发回调");
        if (this.mWxHandler != null && resp != null) {
            try {
                this.mWxHandler.getWXEventHandler().onResp(resp);
            } catch (Exception var3) {
                SLog.error(var3);
            }
        }
        this.finish();
    }

    public void onReq(BaseReq req) {
        if (this.mWxHandler != null) {
            this.mWxHandler.getWXEventHandler().onReq(req);
        }
        //获取开放标签传递的extinfo数据逻辑
        if (req.getType() == ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX && req instanceof ShowMessageFromWX.Req) {
            ShowMessageFromWX.Req showReq = (ShowMessageFromWX.Req) req;
            WXMediaMessage mediaMsg = showReq.message;
            String extInfo = mediaMsg.messageExt;
//            Log.e("share---2", "wxenty" + extInfo.toString());
            if (!TextUtils.isEmpty(extInfo) && OnMultiClickUtils.isMultiClickClick(2000)) {
                RxBus.getDefault().post(SHARE_WEIXIN_TO_HOME, extInfo);
                Intent intent = new Intent();
                intent.putExtra("shareWxExtInfo", extInfo);
                ComponentName name = new ComponentName("com.sortinghat.funny"
                        , "com.sortinghat.funny.ui.MainActivity");
                intent.setComponent(name);
                startActivity(intent);
            }

        }


        this.finish();
    }

}
