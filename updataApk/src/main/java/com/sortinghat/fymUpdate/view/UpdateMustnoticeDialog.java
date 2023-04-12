package com.sortinghat.fymUpdate.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sortinghat.fymUpdate.R;

public class UpdateMustnoticeDialog extends Dialog {

    private TextView notice_tx;
    private Button notice_cancle;
    private Button notice_sure;
    private View.OnClickListener onClickListener;
    private String noticecontentLength;
    private Context cn;

    public UpdateMustnoticeDialog(@NonNull Context context) {
        super(context);
        cn = context;
    }

    public UpdateMustnoticeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        cn = context;
    }

    protected UpdateMustnoticeDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        cn = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.updatamust_network_notice);
        initWindowView();
        initView();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private void initView() {
        notice_tx = findViewById(R.id.notice_tx);
        notice_cancle = findViewById(R.id.notice_cancle);
        notice_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        notice_sure = findViewById(R.id.notice_sure);
        notice_sure.setOnClickListener(onClickListener);
        if (noticecontentLength != null) {
            notice_tx.setText("您当前处于移动数据状态，是否消耗" + noticecontentLength + "数据流量下载安装包？");
        }
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    private void initWindowView() {
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setWindowAnimations(R.style.dialog_new);
        window.setBackgroundDrawableResource(R.color.transparency);
    }

    public void setUpApkLength(String noticecontentLength) {
        this.noticecontentLength = noticecontentLength;
    }

    @Override
    public void show() {
        try {
            if (!isShowing() && cn != null && !((Activity) cn).isFinishing()) {
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
