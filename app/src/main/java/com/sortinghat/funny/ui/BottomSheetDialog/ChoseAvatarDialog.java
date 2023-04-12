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
import com.sortinghat.funny.databinding.DialogMyChoseAvatarBinding;
import com.sortinghat.funny.databinding.DialogMyChoseSexBinding;
import com.sortinghat.funny.view.BaseBottomSheetDialog;

public class ChoseAvatarDialog extends BaseBottomSheetDialog {
    DialogMyChoseAvatarBinding choseSexDialogBinding;
    private OnDialogSureListener listener;

    public interface OnDialogSureListener {
        void choseTakePhone();

        void choseGallery();
    }

    public void setOnDialogSureListener(OnDialogSureListener listener) {
        this.listener = listener;
    }

    public ChoseAvatarDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_my_chose_avatar, container);
        choseSexDialogBinding = DataBindingUtil.bind(view, null);
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
        choseSexDialogBinding.takePhoto.setOnClickListener(quickClickListener);
        choseSexDialogBinding.gallery.setOnClickListener(quickClickListener);
        choseSexDialogBinding.dialogCancel.setOnClickListener(quickClickListener);
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
                case R.id.takePhoto:
                    listener.choseTakePhone();
                    break;
                case R.id.gallery:
                    listener.choseGallery();
                    break;
                case R.id.dialog_cancel:
                    dismiss();
                    break;

            }
        }
    };
}
