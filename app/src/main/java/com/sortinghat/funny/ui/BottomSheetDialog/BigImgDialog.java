package com.sortinghat.funny.ui.BottomSheetDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.ScreenUtils;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.sortinghat.common.glide.ImageLoaderListener;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.common.utils.statusbar.StatusBarUtil;
import com.sortinghat.funny.R;
import com.sortinghat.funny.databinding.DialogHomeBigImgBinding;
import com.sortinghat.funny.util.CommonUtil;
import com.sortinghat.funny.view.BaseBottomSheetDialog;
import com.sortinghat.funny.view.dragImg.DargImgRelativeLayout;

import static com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.ZOOM_FOCUS_CENTER_IMMEDIATE;

public class BigImgDialog extends BaseBottomSheetDialog {
    DialogHomeBigImgBinding bigImgBinding;
    String curUrl;

    public BigImgDialog(String curUrl) {
        super();//父类的构造方法是不会被子类继承的，但是子类的构造方法中会有一个隐式的super()来调用父类中的无参数构造方法。
        this.curUrl = curUrl;
    }

    //报错（could not find Fragment constructor）使用Fragment的时候，因为使用到了有参数的构造函数，没有提供无参的构造函数，有时会报错。
    //Fragment必须有一个无参public的构造函数
    public BigImgDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_home_big_img, container);
        bigImgBinding = DataBindingUtil.bind(view, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void setStyle(int type) {
        super.setStyle(1);
    }

    @Override
    public int height() {
        return -1;
    }

    private void init() {
        bigImgBinding.mainImage.setOnClickListener(quickClickListener);
        bigImgBinding.mainImageGif.setOnClickListener(quickClickListener);
        bigImgBinding.ivClose.setOnClickListener(quickClickListener);

        if (!TextUtils.isEmpty(curUrl)) {
            if (CommonUtil.urlIsGif(curUrl)) {
                bigImgBinding.mainImage.setVisibility(View.GONE);
                bigImgBinding.mainImageGif.setVisibility(View.VISIBLE);
                GlideUtils.loadGifImage(curUrl, 0, bigImgBinding.mainImageGif);
            } else if (curUrl.contains("gif2webp")) {
                bigImgBinding.mainImage.setVisibility(View.GONE);
                bigImgBinding.mainImageGif.setVisibility(View.VISIBLE);
                GlideUtils.loadImageNoPlaceholder(curUrl, bigImgBinding.mainImageGif);
            } else {
                bigImgBinding.rlMain.setIAnimClose(new DargImgRelativeLayout.IAnimClose() {
                    @Override
                    public void onPictureClick() {
                        dismiss();
                    }

                    @Override
                    public void onPictureRelease(View view) {
                        dismiss();
                    }
                });

                bigImgBinding.rlMain.setCurrentShowView(bigImgBinding.mainImage, 0);
                int width = ScreenUtils.getScreenWidth();
                int height = ScreenUtils.getScreenHeight() - StatusBarUtil.getStatusBarHeight(getActivity());
                bigImgBinding.mainImage.setVisibility(View.VISIBLE);
                bigImgBinding.mainImageGif.setVisibility(View.GONE);
                GlideUtils.loadImageToBitmap(getContext(), curUrl, bitmap -> {
                    if (bitmap == null || bitmap.isRecycled()) {
                        return;
                    }
                    int sWidth = bitmap.getWidth();
                    int sHeight = bitmap.getHeight();

                    float scaleW = sWidth <= 0 ? 0 : width / (float) sWidth;
//                    float scaleH = height / (float) sHeight;

                    if (sHeight >= height
                            && sHeight / sWidth >= height / width) {
                        bigImgBinding.mainImage.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                        bigImgBinding.mainImage.setImage(ImageSource.bitmap(bitmap), new ImageViewState(scaleW, new PointF(0, 0), 0));
                    } else {
                        bigImgBinding.mainImage.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
                        bigImgBinding.mainImage.setImage(ImageSource.bitmap(bitmap));
                        bigImgBinding.mainImage.setDoubleTapZoomStyle(ZOOM_FOCUS_CENTER_IMMEDIATE);
                    }
                });
            }
        }
    }

    /**
     * 如果想要点击外部消失的话 重写此方法
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //设置点击外部可消失
        dialog.setCanceledOnTouchOutside(true);
        //设置使软键盘弹出的时候dialog不会被顶起
        Window win = dialog.getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        win.setSoftInputMode(params.SOFT_INPUT_ADJUST_NOTHING);
        //这里设置dialog的进出动画
        win.setWindowAnimations(R.style.img_dialog_animation);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            win.setNavigationBarColor(Color.BLACK);  //设置虚拟键的背景颜色
        }
        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.main_image:
                case R.id.main_image_gif:
                case R.id.iv_close:
                    dismiss();
                    break;

            }
        }
    };
}
