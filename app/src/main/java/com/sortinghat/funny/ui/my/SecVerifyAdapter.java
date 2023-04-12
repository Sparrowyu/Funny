package com.sortinghat.funny.ui.my;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.MobSDK;
import com.mob.secverify.SecVerify;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.mob.secverify.ui.component.LoginAdapter;
import com.sortinghat.funny.constant.ConstantWeb;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.util.ConstantUtil;

import java.lang.reflect.Method;

/**
 * 使用Adapter的方式修改授权页面ui，支持使用自己的inflate的xml布局
 */
public class SecVerifyAdapter extends LoginAdapter {
    private Activity activity;
    private ViewGroup vgBody;
    private LinearLayout vgContainer;
    private RelativeLayout rlTitle;
    private Button btnLogin;
    private TextView tvSecurityPhone;
    private TextView tvOwnPhone;
    private TextView tvAgreement;
    private CheckBox cbAgreement;
    private CheckBox cbAgreement1;
    private View contentView;
    //可用于判断展示运营商隐私协议
    private String operator;
    private String url;
    private ImageView imageViewIcon;

    @Override
    public void onCreate() {
        super.onCreate();
        //获取授权页面原有控件
        init();
        //设置授权页面主题
        setImmTheme();
        //设置授权页面方向
        requestOrientation();
        //隐藏 授权页面原有内容
        vgBody.setVisibility(View.GONE);
        rlTitle.setVisibility(View.GONE);
        //获取自己的View
        contentView = View.inflate(activity, R.layout.sec_verify_one_key_login, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        vgContainer.setGravity(Gravity.CENTER);
        vgContainer.setBackgroundColor(activity.getResources().getColor(R.color.black));
        //添加自己的View到授权页面上，注意不要使用Activity来设置
        vgContainer.addView(contentView, params);

        initOwnView();
        initOtherLogin();
    }

    private void initOtherLogin() {
        contentView.findViewById(R.id.sec_verify_close).setOnClickListener(quickClickListener);
        contentView.findViewById(R.id.tv_other_login).setOnClickListener(quickClickListener);
        contentView.findViewById(R.id.iv_wechat_login).setOnClickListener(quickClickListener);
        contentView.findViewById(R.id.iv_qq_login).setOnClickListener(quickClickListener);
        contentView.findViewById(R.id.sec_verify_page_login_use_this_number).setOnClickListener(quickClickListener);
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.sec_verify_close:
                    SecVerify.finishOAuthPage();
                    RxBus.getDefault().post(RxCodeConstant.PRE_LOGIN_TO_LOGIN, 0);
                    break;
                case R.id.tv_other_login:
                    SecVerify.finishOAuthPage();
                    ConstantUtil.createUmEvent("login_sms_login_button");//登录页-其他手机号码登录
                    RxBus.getDefault().post(RxCodeConstant.PRE_LOGIN_TO_LOGIN, 1);
                    break;
                case R.id.iv_wechat_login:
                    CommonUtils.showShort("微信登录");
                    SecVerify.finishOAuthPage();
                    break;
                case R.id.iv_qq_login:
                    CommonUtils.showShort("QQ登录");
                    SecVerify.finishOAuthPage();
                    break;
                case R.id.sec_verify_page_login_use_this_number:
                    cbAgreement1.setChecked(true);
                    cbAgreement.setChecked(true);
                    break;
            }
        }
    };

    private void initOwnView() {
        tvOwnPhone = contentView.findViewById(R.id.sec_verify_page_one_key_login_phone);
        imageViewIcon = contentView.findViewById(R.id.imageView_icon);
        tvOwnPhone.setText(tvSecurityPhone.getText());

        tvAgreement = contentView.findViewById(R.id.sec_verify_page_login_use_this_number);
        tvAgreement.setText(buildSpanString());
        tvAgreement.setHighlightColor(activity.getResources().getColor(android.R.color.transparent));
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        cbAgreement1 = contentView.findViewById(R.id.sec_verify_page_one_key_login_checkbox);
        cbAgreement1.setVisibility(View.VISIBLE);
        contentView.findViewById(R.id.sec_verify_page_login_login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstantUtil.createUmEvent("login_fast_login_button");//登录页-本机一键登录按钮
                //点击自己登录按钮时需要将默认的复选框设置为选中，并且点击原有的授权页面登录按钮
                if (cbAgreement1.isChecked()) {
                    cbAgreement.setChecked(true);
                    btnLogin.performClick();
                } else {
                    CommonUtils.showShort(activity.getString(R.string.login_agree_click));
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //将授权页面本身控件的文本设置到自己的View上面
        tvOwnPhone.setText(tvSecurityPhone.getText());
    }

    private void init() {
        vgBody = getBodyView();
        vgContainer = (LinearLayout) getContainerView();
        activity = getActivity();
        rlTitle = getTitlelayout();
        btnLogin = getLoginBtn();
        tvSecurityPhone = getSecurityPhoneText();
        cbAgreement = getAgreementCheckbox();
        operator = getOperatorName();


    }


    //UI主题为透明，所以Android 8.0不可设置为固定方向为横屏或者竖屏
    private void requestOrientation() {
        if (android.os.Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void setImmTheme() {
        if (Build.VERSION.SDK_INT >= 21) {
            // 设置沉浸式状态栏
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
//			 设置状态栏透明
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT >= 23) {
                activity.getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        //是否占用状态栏的位置，false为占用，true为不占用
        vgContainer.setFitsSystemWindows(false);
        //是否全屏
//		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //如果不想适配P以上的水滴屏和刘海屏，可以在这里设置layoutInDisplayCutoutMode为其他的值
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            activity.getWindow().setAttributes(lp);
        }
    }

    private SpannableString buildSpanString() {
        String operatorText = "电话认证服务条款";
        if (getCellularOperatorType() == 1) {
            operatorText = "《中国移动认证服务条款》";
            url = "https://wap.cmpassport.com/resources/html/contract.html";
        } else if (getCellularOperatorType() == 2) {
            operatorText = "《中国联通认证服务条款》";
            url = "https://ms.zzx9.cn/html/oauth/protocol2.html";
        } else if (getCellularOperatorType() == 3) {
            operatorText = "《中国电信认证服务条款》";
            url = "https://e.189.cn/sdk/agreement/content.do?type=main&appKey=&hidetop=true&returnUrl=";
        }
        final String operatorString = operatorText;
        String ageementText = "登录注册即代表同意" + "《用户协议》" + "《隐私政策》" + "和" + operatorText + "并授权搞笑星球使用本机号码登录";
        String cusPrivacy1 = "《用户协议》";
        String cusPrivacy2 = "《隐私政策》";
        int baseColor = MobSDK.getContext().getResources().getColor(R.color.color_d7d7d7);
        int privacyColor = Color.parseColor("#4180FF");
        SpannableString spanStr = new SpannableString(ageementText);
        int privacyIndex = ageementText.indexOf(operatorText);
        spanStr.setSpan(new ForegroundColorSpan(baseColor)
                , 0, ageementText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spanStr.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View widget) {
                gotoAgreementPage(url, operatorString);
            }
        }, privacyIndex, privacyIndex + operatorText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的前景色
        spanStr.setSpan(new ForegroundColorSpan(privacyColor), privacyIndex, privacyIndex + operatorText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (!TextUtils.isEmpty(cusPrivacy1)) {
            int privacy1Index = ageementText.indexOf(cusPrivacy1);
            //设置文字的单击事件
            spanStr.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }

                @Override
                public void onClick(View widget) {
                    gotoAgreementPage(ConstantWeb.PRIVACY_POLICY, cusPrivacy1);
                }
            }, privacy1Index, privacy1Index + cusPrivacy1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //设置文字的前景色
            spanStr.setSpan(new ForegroundColorSpan(privacyColor), privacy1Index, privacy1Index + cusPrivacy1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (!TextUtils.isEmpty(cusPrivacy2)) {
            int privacy2Index = ageementText.lastIndexOf(cusPrivacy2);
            //设置文字的单击事件
            spanStr.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }

                @Override
                public void onClick(View widget) {
                    gotoAgreementPage(ConstantWeb.PRIVACY_PROTOCOL, cusPrivacy2);
                }
            }, privacy2Index, privacy2Index + cusPrivacy2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //设置文字的前景色
            spanStr.setSpan(new ForegroundColorSpan(privacyColor), privacy2Index, privacy2Index + cusPrivacy2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spanStr;
    }

    //可替换为跳转自己的webview
    private void gotoAgreementPage(String agreementUrl, String titleName) {
        if (TextUtils.isEmpty(agreementUrl)) {
            return;
        }
        CommonWebActivity.starWebActivity(activity, titleName, agreementUrl);
    }

    /**
     * 获取设备蜂窝网络运营商
     *
     * @return ["中国电信CTCC":3]["中国联通CUCC:2]["中国移动CMCC":1]["other":0]["无sim卡":-1]["数据流量未打开":-2]
     */
    public static int getCellularOperatorType() {
        int opeType = -1;
        // No sim
        if (!hasSim()) {
            return opeType;
        }
        // Mobile data disabled
        if (!isMobileDataEnabled(MobSDK.getContext())) {
            opeType = -2;
            return opeType;
        }
        // Check cellular operator
        TelephonyManager tm = (TelephonyManager) MobSDK.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getSimOperator();
        // 中国联通
        if ("46001".equals(operator) || "46006".equals(operator) || "46009".equals(operator)) {
            opeType = 2;
            // 中国移动
        } else if ("46000".equals(operator) || "46002".equals(operator) || "46004".equals(operator) || "46007".equals(operator)) {
            opeType = 1;
            // 中国电信
        } else if ("46003".equals(operator) || "46005".equals(operator) || "46011".equals(operator)) {
            opeType = 3;
        } else {
            opeType = 0;
        }
        return opeType;
    }

    public static boolean hasSim() {
        TelephonyManager tm = (TelephonyManager) MobSDK.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断数据流量开关是否打开
     *
     * @param context
     * @return
     */
    public static boolean isMobileDataEnabled(Context context) {
        try {
            Method method = ConnectivityManager.class.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true);
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return (Boolean) method.invoke(connectivityManager);
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void onDestroy() {
//        try {
//            if (imageViewIcon instanceof ImageView) {
//                Drawable d = imageViewIcon.getDrawable();
//                if (d != null && d instanceof BitmapDrawable) {
//                    Bitmap bmp = ((BitmapDrawable) d).getBitmap();
//                    bmp.recycle();
//                    bmp = null;
//                }
//                imageViewIcon.setImageBitmap(null);
//                imageViewIcon.setBackground(null);
//                if (d != null) {
//                    d.setCallback(null);
//                }
//            }
//            System.gc();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        super.onDestroy();

    }
}
