package com.sortinghat.funny.push;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.statusbar.StatusBarFontColorUtil;
import com.sortinghat.common.utils.statusbar.StatusBarUtil;
import com.sortinghat.funny.R;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

//厂商辅助弹框，小米，华为，oppo，vivo
public class MfrMessageActivity extends UmengNotifyClickActivity {
    private static final String TAG = "app-push";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        setTheme(R.style.AppThemePush);
        StatusBarUtil.setColor(this, CommonUtils.getColor(R.color.white), 0);
        StatusBarFontColorUtil.setLightMode(this);
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);
        final String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);//playload内的内容
        if (!TextUtils.isEmpty(body)) {
            Log.d(TAG, "body: " + body);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject json = null;
                        json = new JSONObject(body);

                        Intent intent = new Intent();
                        if (null != json.optJSONObject("extra")) {
                            String postId = json.getJSONObject("extra").getString("postId");
                            if (null != postId && !TextUtils.isEmpty(postId))
                                intent.putExtra("pushPostId", postId);//948501046528
                        }
                        ComponentName name = new ComponentName("com.sortinghat.funny"
                                , "com.sortinghat.funny.ui.MainActivity");
                        intent.setComponent(name);
                        startActivity(intent);
                    } catch (JSONException e) {
                        Log.d(TAG, "bodyerror: " + e.getMessage().toString());
                        e.printStackTrace();
                    }
                    finish();
                }
            });
        } else {
            finish();
        }
    }
}

/*
*
* {
  "appkey": "60deb38526a57f101843d62b",
  "timestamp": 1635300115321,
  "description": "测试1027",
  "payload": {
    "display_type": "notification",
    "body": {
      "ticker": "帖子播放",
      "text": "这是搞笑星球内容",
      "title": "帖子播放",
      "after_open": "go_app",
      "play_sound": true
    },
    "extra": {
      "postId": "948501046528"
    }
  },
  "type": "broadcast",
  "mipush": true,
  "mi_activity": "com.sortinghat.funny.push.MfrMessageActivity",
  "production_mode": false,
  "policy": {
    "expire_time": "2021-10-28 10:01:55"
  }
}
* */
