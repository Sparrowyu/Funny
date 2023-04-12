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
import com.sortinghat.fymUpdate.updateapp.UpdateInfoBean;

public class UpdateWifiPagDialog extends Dialog {

    private TextView tv_updata_dialog_version;
    private View.OnClickListener onClickListener;
    private UpdateInfoBean.DataBean upinfo;
    private Button tv_updata_dialog_y_up;
    private Button tv_updata_dialog_n_up;
    private Context cn;

    public UpdateWifiPagDialog(@NonNull Context context) {
        super(context);
        cn = context;
    }

    public UpdateWifiPagDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        cn = context;
    }

    protected UpdateWifiPagDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        cn = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.updatawifi_dialog_view);
        initWindowView();
        initView();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private void initView() {
        tv_updata_dialog_version = (TextView) findViewById(R.id.tv_updata_dialog_version);
        tv_updata_dialog_y_up = findViewById(R.id.tv_updata_dialog_y_up);
        tv_updata_dialog_n_up = findViewById(R.id.tv_updata_dialog_n_up);
        tv_updata_dialog_n_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tv_updata_dialog_y_up.setOnClickListener(onClickListener);

        if (upinfo != null) {
            tv_updata_dialog_version.setText(upinfo.getVersionName());
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

    public void setUpInfoBean(UpdateInfoBean.DataBean upinfo) {
        this.upinfo = upinfo;
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
