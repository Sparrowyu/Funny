package com.sortinghat.fymUpdate.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sortinghat.fymUpdate.R;
import com.sortinghat.fymUpdate.updateapp.UpdateInfoBean;

public class UpdateMustUpdataDialog extends Dialog {

    private TextView tv_updata_dialog_version;
    private TextView updata_dialog_bt;
    private TextView updata_dialog_cancel;
    private TextView updata_dialog_tx;
    private LinearLayout updata_loading_layout;
    private UpdataAPPProgressBar updata_loading_progress;
    private View.OnClickListener onClickListener;
    private UpdateInfoBean.DataBean upinfo;
    private Context cn;
    private boolean isCancel;

    public UpdateMustUpdataDialog(@NonNull Context context) {
        super(context);
        cn = context;
    }

    public UpdateMustUpdataDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        cn = context;
    }

    protected UpdateMustUpdataDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        cn = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.updatamust_dialog_view);
        initWindowView();
        initView();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private void initView() {
        tv_updata_dialog_version = findViewById(R.id.updata_dialog_version);
        updata_loading_layout = findViewById(R.id.updata_loading_layout);
        updata_loading_progress = findViewById(R.id.updata_loading_progress);
        updata_dialog_tx = findViewById(R.id.updata_dialog_tx);
        updata_dialog_bt = findViewById(R.id.updata_dialog_bt);
        updata_dialog_cancel = findViewById(R.id.updata_dialog_cancel);
        updata_dialog_bt.setOnClickListener(onClickListener);
        updata_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        if (upinfo != null) {
            tv_updata_dialog_version.setText("发现新版本，搞笑星球" + upinfo.getVersionName());
            isCancel = upinfo.getIsForceUpgrade() != 1;//1是强制更新，此时是不能点击空白处返回的
            updata_dialog_tx.setText(upinfo.getUpgradeDescription());
        }
        if (isCancel) {
            updata_dialog_cancel.setVisibility(View.VISIBLE);
        } else {
            updata_dialog_cancel.setVisibility(View.GONE);
        }
        updata_dialog_cancel.setEnabled(isCancel);
        setCanceledOnTouchOutside(isCancel);
        setCancelable(isCancel);
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

    public void setUpInfoBean(UpdateInfoBean.DataBean upinfo) {
        this.upinfo = upinfo;
    }

    public void showProgress(int pro) {
        updata_loading_progress.setProgress(pro);
    }

    public void showDiaOrBt(int type) {
        if (type == 1) {
            updata_loading_layout.setVisibility(View.GONE);
            updata_dialog_bt.setVisibility(View.VISIBLE);
            if (isCancel) {
                updata_dialog_cancel.setVisibility(View.VISIBLE);
            } else {
                updata_dialog_cancel.setVisibility(View.GONE);
            }
        } else {
            updata_loading_layout.setVisibility(View.VISIBLE);
            updata_dialog_bt.setVisibility(View.GONE);
            updata_dialog_cancel.setVisibility(View.GONE);
        }

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
