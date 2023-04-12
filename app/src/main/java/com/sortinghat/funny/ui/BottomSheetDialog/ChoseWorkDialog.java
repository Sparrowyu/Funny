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
import com.sortinghat.funny.databinding.DialogMyChoseWorkBinding;
import com.sortinghat.funny.view.BaseBottomSheetDialog;
import com.sortinghat.funny.view.CustomSwitchButton;

public class ChoseWorkDialog extends BaseBottomSheetDialog {
    DialogMyChoseWorkBinding dialogBinding;
    private OnDialogSureListener listener;

    public interface OnDialogSureListener {
        void choseWork(int i, String workString);
    }

    private boolean isOn;

    public void setOnDialogSureListener(OnDialogSureListener listener) {
        this.listener = listener;
    }

    public ChoseWorkDialog(boolean isOn) {
        this.isOn = isOn;
    }

    public ChoseWorkDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_my_chose_work, container);
        dialogBinding = DataBindingUtil.bind(view, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public int height() {
        return SizeUtils.dp2px(380);
    }

    private void init() {

        dialogBinding.switchButton.setOnToggleChanged(new CustomSwitchButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    listener.choseWork(0, "");
                } else {
                    listener.choseWork(-1, "");
                }

            }
        });

        dialogBinding.work1.setOnClickListener(quickClickListener);
        dialogBinding.work2.setOnClickListener(quickClickListener);
        dialogBinding.work3.setOnClickListener(quickClickListener);
        dialogBinding.work4.setOnClickListener(quickClickListener);
        dialogBinding.work5.setOnClickListener(quickClickListener);
        dialogBinding.work6.setOnClickListener(quickClickListener);
        dialogBinding.work7.setOnClickListener(quickClickListener);
        setData(isOn);
    }

    public void setData(boolean isON) {
        if (null != dialogBinding.switchButton) {
            if (isON) {
                dialogBinding.switchButton.setToggleOn();
            } else {
                dialogBinding.switchButton.setToggleOff();
            }
        }
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
                case R.id.work1:
                    listener.choseWork(1, "服务业人员（餐饮服务员/司机/售货员等）");
                    break;
                case R.id.work2:
                    listener.choseWork(1, "专业人士（教师/医生/律师等）");
                    break;
                case R.id.work3:
                    listener.choseWork(1, "事业单位/公务员/政府工作人员");
                    break;
                case R.id.work4:
                    listener.choseWork(1, "公司职员/白领");
                    break;
                case R.id.work5:
                    listener.choseWork(1, "学生");
                    break;
                case R.id.work6:
                    listener.choseWork(1, "个体户");
                    break;
                case R.id.work7:
                    listener.choseWork(1, "其他");
                    break;
            }
        }
    };
}
