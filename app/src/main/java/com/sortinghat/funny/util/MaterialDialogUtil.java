package com.sortinghat.funny.util;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sortinghat.funny.R;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.interfaces.CommonConfirmDialogListener;

/**
 * MaterialDialog
 * alert 暂时只提供两个按钮对话框，OnDismissListener和cancelable在调用类里自行添加
 * progress
 * confirm
 * list
 * <p>
 * Created by wzy on 2018/12/12
 */
public class MaterialDialogUtil {

    public static MaterialDialog showProgress(Context context) {
        return new MaterialDialog.Builder(context)
                .content("加载中…")
                .progress(true, 0)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show();
    }

    public static MaterialDialog showProgress(Context context, int content) {
        return new MaterialDialog.Builder(context)
                .content(content)
                .progress(true, 0)
                .show();
    }

    public static MaterialDialog showProgress(Context context, String content) {
        return new MaterialDialog.Builder(context)
                .content(content)
                .progress(true, 0)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .show();
    }

    public static MaterialDialog showConfirm(Context context, int title, int content, int agree) {
        return new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(agree)
                .show();
    }

    public static MaterialDialog showConfirm(Context context, int titleColorRes, int contentColorRes, int positiveColorRes, int backgroundColorRes, String title, String content, String agree, String disagree, MaterialDialog.SingleButtonCallback positive) {
        return new MaterialDialog.Builder(context)
                .titleColorRes(titleColorRes)
                .contentColorRes(contentColorRes)
                .positiveColorRes(positiveColorRes)
                .backgroundColorRes(backgroundColorRes)
                .title(title)
                .content(content)
                .positiveText(agree)
                .negativeText(disagree)
                .onPositive(positive)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .show();
    }

    public static MaterialDialog showConfirm(Context context, int content, int agree) {
        return new MaterialDialog.Builder(context)
                .content(content)
                .positiveText(agree)
                .show();
    }

    public static MaterialDialog showConfirm(Context context, String content, int agree) {
        return new MaterialDialog.Builder(context)
                .content(content)
                .positiveText(agree)
                .show();
    }

    public static MaterialDialog showConfirm(Context context, int content, int agree, MaterialDialog.SingleButtonCallback positive) {
        return new MaterialDialog.Builder(context)
                .content(content)
                .positiveText(agree)
                .onPositive(positive)
                .show();
    }

    public static MaterialDialog showAlert(Context context, int content, int agree, int disagree, MaterialDialog.SingleButtonCallback positive) {
        return new MaterialDialog.Builder(context)
                .content(content)
                .positiveText(agree)
                .negativeText(disagree)
                .onPositive(positive)
                .show();
    }

    public static MaterialDialog showAlert(Context context, String content, int agree, int disagree, MaterialDialog.SingleButtonCallback positive) {
        return new MaterialDialog.Builder(context)
                .content(content)
                .positiveText(agree)
                .negativeText(disagree)
                .onPositive(positive)
                .show();
    }

    public static MaterialDialog showAlert(Context context, String content, String agree, String disagree, MaterialDialog.SingleButtonCallback positive) {
        return new MaterialDialog.Builder(context)
                .content(content)
                .positiveText(agree)
                .negativeText(disagree)
                .onPositive(positive)
                .show();
    }

    /**
     * 添加取消Callback
     */
    public static MaterialDialog showAlert(Context context, String content, int agree, int disagree, MaterialDialog.SingleButtonCallback positive, MaterialDialog.SingleButtonCallback negative) {
        return new MaterialDialog.Builder(context)
                .content(content)
                .positiveText(agree)
                .negativeText(disagree)
                .onPositive(positive)
                .onNegative(negative)
                .show();
    }

    public static MaterialDialog showAlert(Context context, int content, int agree, int disagree, MaterialDialog.SingleButtonCallback positive, MaterialDialog.SingleButtonCallback negative) {
        return new MaterialDialog.Builder(context)
                .content(content)
                .positiveText(agree)
                .negativeText(disagree)
                .onPositive(positive)
                .onNegative(negative)
                .show();
    }

    public static MaterialDialog showList(Context context, int items, MaterialDialog.ListCallback callback) {
        return new MaterialDialog.Builder(context)
                .items(items)
                .itemsCallback(callback)
                .show();
    }

    public static MaterialDialog showList(Context context, String[] items, MaterialDialog.ListCallback callback) {
        return new MaterialDialog.Builder(context)
                .items(items)
                .itemsCallback(callback)
                .show();
    }

    public static MaterialDialog showList(Context context, String[] items, int selectedIndex, MaterialDialog.ListCallbackSingleChoice callback) {
        return new MaterialDialog.Builder(context)
                .items(items)
                .itemsCallbackSingleChoice(selectedIndex, callback)
                .show();
    }

    public static MaterialDialog showCustomProgressDialog(Context context) {
        return showCustomProgressDialog(context, "加载中…");
    }

    public static MaterialDialog showCustomProgressDialog(Context context, String progressBarText) {
        return showCustomProgressDialog(context, progressBarText, false);
    }

    //isCancel,默认不可取消
    public static MaterialDialog showCustomProgressDialog(Context context, String progressBarText, boolean isCancel) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_progress, false)
                .cancelable(isCancel)
                .canceledOnTouchOutside(isCancel)
                .build();

        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(layoutParams);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getCustomView().setLayoutParams(params);

        dialog.getCustomView().getBackground().setAlpha(180);
        ((ProgressBar) dialog.getCustomView().findViewById(R.id.progress_bar)).getIndeterminateDrawable().setColorFilter(CommonUtils.getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        ((TextView) dialog.getCustomView().findViewById(R.id.tv_progress_bar_text)).setText(progressBarText);

        if (!((BaseActivity) context).isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    public static MaterialDialog showCustomBaseDialog(Context context, int layoutId) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(layoutId, false)
                .cancelable(false)
                .build();

        dialog.getCustomView().getRootView().setBackground(CommonUtils.getDrawable(R.drawable.shape_white_bg_with_corner));

        return dialog;
    }

    public static MaterialDialog showCustomWithCloseButtonDialog(Context context, int layoutId) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(layoutId, false)
                .cancelable(false)
                .build();

        dialog.getCustomView().getRootView().setBackgroundColor(CommonUtils.getColor(R.color.transparent));

        return dialog;
    }

    public static MaterialDialog showCustomCommonConfirmDialog(Context context, String content, CommonConfirmDialogListener listener) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_common_confirm, false)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .build();

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        dialog.getCustomView().setLayoutParams(params);

        TextView textView = dialog.getCustomView().findViewById(R.id.dialog_content);
        textView.setText(content);

        if (listener != null) {
            dialog.getCustomView().findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDialogCancel();
                    if (!((BaseActivity) context).isFinishing()) {
                        dialog.dismiss();
                    }
                }
            });
            dialog.getCustomView().findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDialogSure();
                    if (!((BaseActivity) context).isFinishing()) {
                        dialog.dismiss();
                    }
                }
            });
        }

        dialog.getCustomView().getRootView().setBackgroundColor(CommonUtils.getColor(R.color.transparent));
        if (!((BaseActivity) context).isFinishing()) {
            dialog.show();
        }
        return dialog;
    }
}