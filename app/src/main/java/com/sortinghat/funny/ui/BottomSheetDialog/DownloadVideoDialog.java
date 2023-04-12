package com.sortinghat.funny.ui.BottomSheetDialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.databinding.DialogDownloadVideoBinding;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.view.BaseBottomSheetDialog;

public class DownloadVideoDialog extends BaseBottomSheetDialog {
    DialogDownloadVideoBinding binding;
    private DownloadVideoDialogListener listener;
    boolean isVip;

    public interface DownloadVideoDialogListener {
        void onDownloadNoWaterMark(boolean isShowAd);//VIP不用显示广告

        void onDownloadWithWaterMark();
    }

    public void setOnDialogListener(DownloadVideoDialogListener listener) {
        this.listener = listener;
    }

    public DownloadVideoDialog() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_download_video, container);
        binding = DataBindingUtil.bind(view, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ConstantUtil.createUmEvent("save_btn");//保存
        init();
    }

    @Override
    public int height() {
        return SizeUtils.dp2px(225);
    }

    private void init() {
        binding.tvDownloadNoWatermark.setOnClickListener(quickClickListener);
        binding.tvDownload.setOnClickListener(quickClickListener);
        binding.dialogCancel.setOnClickListener(quickClickListener);
        isVip = SPUtils.getInstance(Constant.SP_CONFIG_INFO).getBoolean(Constant.USER_VIP_TAG);
        if (isVip) {
            binding.tvDownloadNoWatermark.setText(getString(R.string.download_vip));
        } else {
            binding.tvDownloadNoWatermark.setText(getString(R.string.download_ad));
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
                case R.id.tv_download:
                    ConstantUtil.createUmEvent("save_btn_with_logo");//保存含水印
                    listener.onDownloadWithWaterMark();
                    dismiss();
                    break;
                case R.id.tv_download_no_watermark:
                    ConstantUtil.createUmEvent("save_btn_without_logo");//保存不含水印
                    listener.onDownloadNoWaterMark(!isVip);
                    dismiss();
                    break;
                case R.id.dialog_cancel:
                    dismiss();
                    break;

            }
        }
    };
}
