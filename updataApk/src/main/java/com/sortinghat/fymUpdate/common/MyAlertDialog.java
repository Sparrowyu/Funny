package com.sortinghat.fymUpdate.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.sortinghat.fymUpdate.R;
import com.sortinghat.fymUpdate.utils.SPUtils;

class MyAlertDialog extends Dialog {
    private static OnAlertDialogOkListener listenr;
    private Context context;
    private String title;
    private String content;

    private MyAlertDialog(@NonNull Context context, String title,
                          String content, @StyleRes int theme) {
        super(context, theme);
        this.context = context;
        this.title = title;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cy_plugin_alertdialog);
        TextView ok = findViewById(R.id.cy_plugin_ok);
        TextView cancle = findViewById(R.id.cy_plugin_cancle);
        ImageView close = findViewById(R.id.cy_plugin_close);
        TextView tv_title = findViewById(R.id.cy_plugin_title);

        TextView tv_content = findViewById(R.id.cy_plugin_content);
        //由网络状态判断是否等待WIFI
        cancle.setText("取消");
        if (TextUtils.isEmpty(title)) {
            tv_title.setText(context.getString(R.string.cy_plugin_update));
        } else {
            tv_title.setText(title);
        }

        if (TextUtils.isEmpty(content)) {
            tv_content.setText("");
        } else {
            tv_content.setText(content);
        }
        RelativeLayout rldialog = new RelativeLayout(context);
        rldialog.setBackgroundColor(Color.parseColor("#90141416"));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (preferences.getString("browser_model", "day").equals("night")) {
            addContentView(rldialog, params);
        }
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SPUtils.getISDOWNLOADEND(context) == -1) {
                    Toast.makeText(context, context.getString(R.string.cy_plugin_update_noti_loading), Toast.LENGTH_SHORT).show();
                }
                dismiss();
                listenr.handleOkClick();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                listenr.handleDismissClick();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    static void creatAlertDialog(final Activity cx, String title,
                                 String content) {
        MyAlertDialog dlg = new MyAlertDialog(cx, title, content, R.style.dialog);
        if (!cx.isFinishing()) {
            dlg.show();
        }
        dlg.setCancelable(false);
    }

    static void setOnAlertDialogOklistener(OnAlertDialogOkListener l) {
        listenr = l;
    }

    @Override
    public void show() {
        try {
            if (!isShowing() && context != null && !((Activity) context).isFinishing()) {
                super.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();//防止  IllegalArgumentException： View not attached to window manager
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
