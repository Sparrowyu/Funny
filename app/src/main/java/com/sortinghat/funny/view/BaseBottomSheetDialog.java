package com.sortinghat.funny.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sortinghat.funny.R;

public class BaseBottomSheetDialog extends BottomSheetDialogFragment {

    private FrameLayout bottomSheet;
    private BottomSheetBehavior<FrameLayout> behavior;

    public BaseBottomSheetDialog() {
    }

    @Override
    public void onStart() {
        super.onStart();

        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        bottomSheet = dialog.getDelegate().findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            layoutParams.height = height();
            bottomSheet.setLayoutParams(layoutParams);
            behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(height());
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public int height() {
        return getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(1);
    }

    //重写此方法 1 有阴影 2 没有
    public void setStyle(int type) {
        if (type == 2) {
            setStyle(STYLE_NORMAL, R.style.my_fragment_dialog);
        } else {
            setStyle(STYLE_NORMAL, R.style.my_fragment_dialog_shadow);
        }
        setCancelable(true);
    }

    /**
     * 如果想要点击外部消失的话 重写此方法
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //设置点击外部可消失
        dialog.setCanceledOnTouchOutside(true);
        //设置使软键盘弹出的时候dialog不会被顶起
        Window win = dialog.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        win.setSoftInputMode(params.SOFT_INPUT_ADJUST_NOTHING);
        //这里设置dialog的进出动画
        win.setWindowAnimations(R.style.dialog_animation);
        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void dismiss() {
        //跳过checkStateLoss()方法，避免异常
       dismissAllowingStateLoss();
    }
}
