package com.sortinghat.funny.ui.my;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.databinding.ActivityClipImageBinding;
import com.sortinghat.funny.viewmodel.MyEditInformationViewModel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ClipImageActivity extends BaseActivity<MyEditInformationViewModel, ActivityClipImageBinding> {

    private int type = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_clip_image;
    }

    @Override
    protected void initViews() {
        initTitleBar("移动和缩放");

        //设置点击事件监听器
        contentLayoutBinding.btnCancel.setOnClickListener(quickClickListener);
        contentLayoutBinding.btOk.setOnClickListener(quickClickListener);

    }

    @Override
    protected void initData() {

    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    finish();
                    break;
                case R.id.bt_ok:
                    generateUriAndReturn();
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null && getIntent().getData() != null) {
            if (type == 1) {
                contentLayoutBinding.clipViewLayout1.setVisibility(View.VISIBLE);
                contentLayoutBinding.clipViewLayout2.setVisibility(View.GONE);
                //设置图片资源
                contentLayoutBinding.clipViewLayout1.setImageSrc(getIntent().getData());
            } else {
                contentLayoutBinding.clipViewLayout2.setVisibility(View.VISIBLE);
                contentLayoutBinding.clipViewLayout1.setVisibility(View.GONE);
                //设置图片资源
                contentLayoutBinding.clipViewLayout2.setImageSrc(getIntent().getData());
            }
        }
    }

    /**
     * 生成Uri并且通过setResult返回给打开的activity
     */
    private void generateUriAndReturn() {
        //调用返回剪切图
        Bitmap zoomedCropBitmap;
        if (type == 1) {
            zoomedCropBitmap = contentLayoutBinding.clipViewLayout1.clip();
        } else {
            zoomedCropBitmap = contentLayoutBinding.clipViewLayout2.clip();
        }
        if (zoomedCropBitmap == null) {
            Log.e("android", "zoomedCropBitmap == null");
            return;
        }
        Uri mSaveUri = Uri.fromFile(new File(getCacheDir(), "cropped_" + System.currentTimeMillis() + ".jpg"));
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = getContentResolver().openOutputStream(mSaveUri);
                if (outputStream != null) {
                    zoomedCropBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                }
            } catch (IOException ex) {
                Log.e("android", "Cannot open file: " + mSaveUri, ex);
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Intent intent = new Intent();
            intent.setData(mSaveUri);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

}

