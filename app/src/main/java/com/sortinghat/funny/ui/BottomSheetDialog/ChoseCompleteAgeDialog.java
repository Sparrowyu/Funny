package com.sortinghat.funny.ui.BottomSheetDialog;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.databinding.DialogHomeCompleteAgeBinding;
import com.sortinghat.funny.view.BaseBottomSheetDialog;

import java.lang.reflect.Field;

public class ChoseCompleteAgeDialog extends BaseBottomSheetDialog {
    DialogHomeCompleteAgeBinding dialogBinding;
    private OnDialogSureListener listener;

    public interface OnDialogSureListener {
        void choseAge(int i, String ge);
    }

    public void setOnDialogSureListener(OnDialogSureListener listener) {
        this.listener = listener;
    }

    public ChoseCompleteAgeDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_home_complete_age, container);
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
        return SizeUtils.dp2px(310);
    }

    String[] valueArr = {"07-12 岁", "13-17 岁", "18-24 岁", "25-30 岁", "31-40 岁", "40岁以上"};

    private void init() {
        dialogBinding.sure.setOnClickListener(quickClickListener);
        dialogBinding.numberPicker.setDisplayedValues(valueArr);
        dialogBinding.numberPicker.setMinValue(0);
        dialogBinding.numberPicker.setMaxValue(valueArr.length - 1);
        dialogBinding.numberPicker.setValue(2);
        dialogBinding.numberPicker.setWrapSelectorWheel(false);//当待显示的条目数大于3时，设置是否可以循环滚动，注意该行应该放在上面三行的下面

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dialogBinding.numberPicker.setTextColor(getResources().getColor(R.color.color_666666));
        }

        dialogBinding.numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            picker.getValue();
        });
        setNumberPickerDivider(dialogBinding.numberPicker);
        dialogBinding.numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);//关掉编辑模式
    }

   //源码看目前这两个属于只有P（9.0）以下可以
    private void setNumberPickerDivider(NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if(pf.getName().equals("mSelectionDivider")) {//设置分割线颜色
                pf.setAccessible(true);
                ColorDrawable colorDrawable =new ColorDrawable(0xffBD8A00);//选择自己喜欢的颜色
                try {
                    pf.set(numberPicker, colorDrawable);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if(pf.getName().equals("mSelectionDividerHeight")) {//设置高度
                pf.setAccessible(true);
                try {
                    int result = 1;//要设置的高度
                    pf.set(picker, result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            picker.invalidate();
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
                case R.id.sure:
                    int value = dialogBinding.numberPicker.getValue();
                    listener.choseAge(value, valueArr[value]);
                    break;
            }
        }
    };
}
