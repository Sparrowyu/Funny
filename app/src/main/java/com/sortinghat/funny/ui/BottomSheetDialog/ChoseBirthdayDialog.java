package com.sortinghat.funny.ui.BottomSheetDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.databinding.DialogMyChoseWorkBinding;
import com.sortinghat.funny.databinding.DialogMyChoseBirthdayBinding;
import com.sortinghat.funny.view.BaseBottomSheetDialog;
import com.sortinghat.funny.view.CustomSwitchButton;

import java.util.GregorianCalendar;

public class ChoseBirthdayDialog extends BaseBottomSheetDialog {
    DialogMyChoseBirthdayBinding dialogBinding;
    private OnDialogSureListener listener;

    private int todayYear;
    private int todayMonth;
    private int todayDay;
    private int currentSelectedYear;
    private int currentSelectedMonth;
    private int currentSelectedDay;


    public interface OnDialogSureListener {
        void choseBirthday(int i, String birthday);
    }

    private boolean isOn;

    public void setOnDialogSureListener(OnDialogSureListener listener) {
        this.listener = listener;
    }

    public ChoseBirthdayDialog(boolean isOn) {
        this.isOn = isOn;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_my_chose_birthday, container);
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
                    listener.choseBirthday(0, "");
                } else {
                    listener.choseBirthday(-1, "");
                }

            }
        });

        dialogBinding.datePicker.setOnClickListener(quickClickListener);

        setData(isOn);

        todayYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        todayMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1;
        todayDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
        currentSelectedYear = todayYear;
        currentSelectedMonth = todayMonth - 1;
        currentSelectedDay = todayDay;

        //设置最大日期
        GregorianCalendar g = new GregorianCalendar(currentSelectedYear, currentSelectedMonth, currentSelectedDay);
        dialogBinding.datePicker.setMaxDate(g.getTimeInMillis());
        GregorianCalendar g1 = new GregorianCalendar(1900, 0, 1);
        dialogBinding.datePicker.setMinDate(g1.getTimeInMillis());
        //初始化datePicker,默认写一个2000，1，1
        dialogBinding.datePicker.init(2000, 0, 1, null);
        dialogBinding.sure.setOnClickListener(quickClickListener);
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
                case R.id.sure:
                    String birthday = dialogBinding.datePicker.getYear() + "-" + (dialogBinding.datePicker.getMonth() + 1) + "-" + dialogBinding.datePicker.getDayOfMonth();
                    listener.choseBirthday(1, birthday);
                    break;
            }
        }
    };
}
