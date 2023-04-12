package com.sortinghat.funny.ui.BottomSheetDialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.databinding.DialogDownloadVideoResultBinding;

public class DownloadVideoResultDialog extends Dialog {
    DialogDownloadVideoResultBinding binding;
    private DownloadVideoResultDialogListener listener;
    private HomeVideoImageListBean.ListBean listBean;

    public static final int STATE_SUCCESS = 0;
    public static final int STATE_FAIL = 1;
    public static final int STATE_PROGRESS = 2;
    public static final int PROGRESS_INIT = 10;
    public static final int PROGRESS_DOWNLOAD_SUCCESS = 50;

    private int state = STATE_PROGRESS;
    private int progess = PROGRESS_INIT;

    public interface DownloadVideoResultDialogListener {
        default void onRetry(HomeVideoImageListBean.ListBean listBean) {}
        default void goToGallery() {}
        default void dismiss() {}
    }


    public void setOnDialogListener(DownloadVideoResultDialogListener listener) {
        this.listener = listener;
    }

    public DownloadVideoResultDialog(Activity activity) {
        super(activity);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);
        getWindow().setWindowAnimations(R.style.DownloadDialogAnim);

        View view = getLayoutInflater().inflate(R.layout.dialog_download_video_result, null);
        binding = DataBindingUtil.bind(view, null);
        setContentView(view);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(true);
        init();
    }


    public HomeVideoImageListBean.ListBean getListBean() {
        return listBean;
    }

    public void setListBean(HomeVideoImageListBean.ListBean listBean) {
        this.listBean = listBean;
    }

    public void updateState(int state) {
        this.state = state;
        if (state == STATE_SUCCESS) {
            showSuccess();
        } else if (state == STATE_FAIL){
            showFail();
        } else {
            updateProgress(progess);
        }
    }

    public int getProgress() {
        return progess;
    }

    public void updateProgress(int progress) {
        Log.d("c", "updateProgress..." + progress);
        if (progress >=0 && progress <= 100 && this.progess != progress) {
            getWindow().setBackgroundDrawableResource(R.drawable.shape_black_40_bg_with_angle);
            this.progess = progress;
            if (binding != null) {
                binding.root.setVisibility(View.VISIBLE);
                binding.llResult.setVisibility(View.GONE);
                binding.llProgress.setVisibility(View.VISIBLE);
                binding.progress.setCurrent(progress);
                binding.tvProgress.setText(progress + "");
            }
        }
    }

    private void showSuccess() {
        if (binding != null) {
            getWindow().setBackgroundDrawableResource(R.drawable.dialog_common_bg);
            binding.root.setVisibility(View.VISIBLE);
            binding.llProgress.setVisibility(View.GONE);
            binding.llResult.setVisibility(View.VISIBLE);
            binding.ivTip.setImageResource(R.drawable.ic_download_success);
            binding.tvTip.setText(R.string.save_success);
            binding.tvNext.setText(R.string.go_to_album);
        }
    }

    private void showFail() {
        if (binding != null) {
            getWindow().setBackgroundDrawableResource(R.drawable.dialog_common_bg);
            binding.root.setVisibility(View.VISIBLE);
            binding.llProgress.setVisibility(View.GONE);
            binding.llResult.setVisibility(View.VISIBLE);
            binding.ivTip.setImageResource(R.drawable.ic_download_fail);
            binding.tvTip.setText(R.string.save_fail);
            binding.tvNext.setText(R.string.retry);
        }
    }


    private void init() {
        binding.tvNext.setOnClickListener(quickClickListener);
        binding.ivClose.setOnClickListener(quickClickListener);
        binding.root.setVisibility(View.GONE);
        binding.llProgress.setVisibility(View.GONE);
        binding.llResult.setVisibility(View.GONE);
        updateState(state);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (listener != null) {
            listener.dismiss();
        }
    }


    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            if (listener == null) {
                return;
            }
            switch (v.getId()) {
                case R.id.tv_next:
                    if (listener != null) {
                        if (state == STATE_SUCCESS) {
                            listener.goToGallery();
                        } else {
                            listener.onRetry(listBean);
                        }
                    }
                    dismiss();
                    break;
                case R.id.iv_close:
                    dismiss();
                    break;

            }
        }
    };
}
