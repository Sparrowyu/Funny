package com.sortinghat.funny.ui.home;

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
import com.sortinghat.funny.databinding.DialogHomeShareBinding;
import com.sortinghat.funny.view.BaseBottomSheetDialog;

public class ShareDialog extends BaseBottomSheetDialog {
    DialogHomeShareBinding dialogHomeLikeBinding;
    private OnShareDialogListener listener;
    private boolean isUserOwner;//是否是用户作品页进来的
    private int applyStatus;//是否是用户作品页进来的
    private boolean showSave;//是否显示保存

    public void setShareDialogListener(OnShareDialogListener listener) {
        this.listener = listener;
    }

    public ShareDialog(){

    }

    public ShareDialog(boolean isUserOwner, int applyStatus) {
        this.isUserOwner = isUserOwner;
        this.applyStatus = applyStatus;
    }

    public ShareDialog(boolean isUserOwner, int applyStatus, boolean showSave) {
        this.isUserOwner = isUserOwner;
        this.applyStatus = applyStatus;
        this.showSave = showSave;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_home_share, container);
        dialogHomeLikeBinding = DataBindingUtil.bind(view, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void setStyle(int type) {
        super.setStyle(2);
    }

    @Override
    public int height() {
        return applyStatus == 1 ? SizeUtils.dp2px(280) :SizeUtils.dp2px(200) ;
    }

    private void init() {
        dialogHomeLikeBinding.shareWechat.setOnClickListener(quickClickListener);
        dialogHomeLikeBinding.shareWechatCircle.setOnClickListener(quickClickListener);
        dialogHomeLikeBinding.shareQq.setOnClickListener(quickClickListener);
        dialogHomeLikeBinding.shareQqZone.setOnClickListener(quickClickListener);
        dialogHomeLikeBinding.shareReport.setOnClickListener(quickClickListener);
        dialogHomeLikeBinding.shareDownload.setOnClickListener(quickClickListener);
        dialogHomeLikeBinding.dialogCancel.setOnClickListener(quickClickListener);
        if (isUserOwner) {
            dialogHomeLikeBinding.shareReportLl.setVisibility(View.GONE);
            dialogHomeLikeBinding.shareDelete.setOnClickListener(quickClickListener);
            dialogHomeLikeBinding.shareDelete.setVisibility(View.VISIBLE);
        } else {
            dialogHomeLikeBinding.shareDeleteLl.setVisibility(View.GONE);
            dialogHomeLikeBinding.shareReportLl.setVisibility(View.VISIBLE);
        }

        dialogHomeLikeBinding.shareDownload.setVisibility(showSave ? View.VISIBLE : View.GONE);
        if (applyStatus == 1) {
            dialogHomeLikeBinding.shareTopLl.setVisibility(View.VISIBLE);
        } else {
            dialogHomeLikeBinding.shareTopLl.setVisibility(View.GONE);
            dialogHomeLikeBinding.shareTopTx.setText("");
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
                case R.id.share_wechat:
                    listener.onWechatShare();
                    break;
                case R.id.share_wechat_circle:
                    listener.onWechatCircleShare();
                    break;
                case R.id.share_qq:
                    listener.onQQShare();
                    break;
                case R.id.share_qq_zone:
                    listener.onQQZoneShare();
                    break;
                case R.id.share_report:
                    listener.onShareReport();
                    break;
                case R.id.share_delete:
                    listener.onShareDelete();
                    break;
                case R.id.share_download:
                    listener.onShareDownload();
                    dismiss();
                    break;
                case R.id.dialog_cancel:
                    dismiss();
                    break;

            }
        }
    };
}
