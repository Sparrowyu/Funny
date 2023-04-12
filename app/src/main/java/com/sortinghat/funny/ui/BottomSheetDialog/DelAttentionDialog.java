package com.sortinghat.funny.ui.BottomSheetDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.databinding.DialogMyDelAttentionBinding;
import com.sortinghat.funny.view.BaseBottomSheetDialog;

public class DelAttentionDialog extends BaseBottomSheetDialog {
    DialogMyDelAttentionBinding dialogMyDelAttentionBinding;
    private OnDialogSureListener listener;
    private int followStatus = 0;

    public interface OnDialogSureListener {
        void sure();
    }

    public void setOnDialogSureListener(OnDialogSureListener listener) {
        this.listener = listener;
    }

    public DelAttentionDialog() {
    }

    public DelAttentionDialog(int followStatus) {
        this.followStatus = followStatus;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_my_del_attention, container);
        dialogMyDelAttentionBinding = DataBindingUtil.bind(view, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public int height() {
        return SizeUtils.dp2px(200);
    }

    private void init() {
        if (followStatus == 0 || followStatus == 3) {
            dialogMyDelAttentionBinding.ivTitle.setText("确定关注");
            dialogMyDelAttentionBinding.sure.setText("关注");
        } else {
            dialogMyDelAttentionBinding.ivTitle.setText("确定取消关注");
            dialogMyDelAttentionBinding.sure.setText("取消关注");
        }

        dialogMyDelAttentionBinding.sure.setOnClickListener(quickClickListener);
        dialogMyDelAttentionBinding.sure.setOnClickListener(quickClickListener);
        dialogMyDelAttentionBinding.dialogCancel.setOnClickListener(quickClickListener);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            if (listener == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.sure:
                    listener.sure();
                    break;
                case R.id.dialog_cancel:
                    dismiss();
                    break;

            }
        }
    };
}
