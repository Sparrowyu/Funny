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

public class DeletePostDialog extends BaseBottomSheetDialog {
    DialogMyDelAttentionBinding dialogMyDelAttentionBinding;
    private OnDialogSureListener listener;

    public interface OnDialogSureListener {
        void sure();
    }

    public void setOnDialogSureListener(OnDialogSureListener listener) {
        this.listener = listener;
    }

    public DeletePostDialog() {

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
            dialogMyDelAttentionBinding.ivTitle.setText("确定要删除么？");
            dialogMyDelAttentionBinding.sure.setText("确认");

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
