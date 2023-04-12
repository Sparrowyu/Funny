package com.sortinghat.funny.ui.my;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.UserMoodReportBean;
import com.sortinghat.funny.databinding.ActivityMyMoodReportBinding;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.util.PermissionHandler;
import com.sortinghat.funny.viewmodel.MyFragmentViewModel;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MoodReportActivity extends BaseActivity<MyFragmentViewModel, ActivityMyMoodReportBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_mood_report;
    }

    @Override
    protected void initViews() {
        initTitleBar("心情报告");
        ConstantUtil.createUmEvent("Laugh_out_loud");//笑出声-pv
        titleBarBinding.vDividerLine.setVisibility(View.GONE);
        contentLayoutBinding.reportSaveBt.setOnClickListener(quickClickListener);
        contentLayoutBinding.reportCompareBt.setOnClickListener(quickClickListener);
        contentLayoutBinding.shareWechat.setOnClickListener(quickClickListener);
        contentLayoutBinding.shareWechatCircle.setOnClickListener(quickClickListener);
        contentLayoutBinding.shareQq.setOnClickListener(quickClickListener);
        contentLayoutBinding.shareQqZone.setOnClickListener(quickClickListener);
    }


    @Override
    protected void initData() {

        //心情报告
        viewModel.getUserMoodReport().observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    UserMoodReportBean reportBean = resultBean.getData();
                    contentLayoutBinding.reportName.setText(reportBean.getNickName() + "的心情报告");
                    contentLayoutBinding.reportTitle.setText("今天是你成为笑星人的第" + reportBean.getRegisterDays() + "天，\n在这段时间你累计(次)：");
                    contentLayoutBinding.reportContent.setText(reportBean.getContent());

                    contentLayoutBinding.moodTx1.setText(reportBean.getMovedToTearsCount() + "");
                    contentLayoutBinding.moodTx2.setText(reportBean.getLaughCount() + "");
                    contentLayoutBinding.moodTx3.setText(reportBean.getComeHertCount() + "");

                    String pendantUrl = reportBean.getPendantUrl();
                    if (!TextUtils.isEmpty(pendantUrl)) {
                        ConstantUtil.glideLoadPendantUrl(pendantUrl, contentLayoutBinding.ivBoxUserIcon);
                    }
                    GlideUtils.loadCircleImage(reportBean.getAvatar(), R.mipmap.user_icon_default, contentLayoutBinding.ivUserIcon);

                }
            }
        });
    }


    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.report_save_bt://笑出声-保存相册按钮
                    ConstantUtil.createUmEvent("Laugh_out_loud_click_save_pic_btn");//
                    savePicture(mContext, viewConversionBitmap(contentLayoutBinding.rlView), System.currentTimeMillis() + ".jpg");
                    break;
                case R.id.report_compare_bt://笑出声-与我相同
//                    ConstantUtil.createUmEvent("Laugh_out_loud_click_save_pic_btn");//
//                    savePicture(mContext, viewConversionBitmap(contentLayoutBinding.rlView), System.currentTimeMillis() + ".jpg");
                    break;
                case R.id.share_wechat://笑出声-分享到微信按钮
                    ConstantUtil.createUmEvent("Laugh_out_loud_click_wx_btn");
                    share(mContext, viewConversionBitmap(contentLayoutBinding.rlView), SHARE_MEDIA.WEIXIN);
                    break;
                case R.id.share_wechat_circle://笑出声-分享到微信朋友圈按钮
                    ConstantUtil.createUmEvent("Laugh_out_loud_click_wx_pyq_btn");
                    share(mContext, viewConversionBitmap(contentLayoutBinding.rlView), SHARE_MEDIA.WEIXIN_CIRCLE);
                    break;
                case R.id.share_qq://笑出声-分享到QQ按钮
                    ConstantUtil.createUmEvent("Laugh_out_loud_click_qq_btn");
                    share(mContext, viewConversionBitmap(contentLayoutBinding.rlView), SHARE_MEDIA.QQ);
                    break;
                case R.id.share_qq_zone://笑出声-分享到QQ空间按钮
                    ConstantUtil.createUmEvent("Laugh_out_loud_click_qq_space_btn");
                    share(mContext, viewConversionBitmap(contentLayoutBinding.rlView), SHARE_MEDIA.QZONE);
                    break;

            }
        }
    };


    private UMShareListener shareListener = new UMShareListener() {

        MaterialDialog progressDialog;

        @Override
        public void onStart(SHARE_MEDIA platform) {
            Log.d("share-mood", "start:");
            //成功
            progressDialog = MaterialDialogUtil.showCustomProgressDialog(mContext, "分享中", true);
            ThreadUtils.runOnUiThreadDelayed(() -> {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }, 3000);
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("share-mood", "result:");
            //成功
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Log.d("share-mood", "error:");
            //失败
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Log.d("share-mood", "cancel:");
            //取消
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    };


    private void share(Context mContext, Bitmap bitmap, SHARE_MEDIA shareMedia) {
        if (!ConstantUtil.isWxQQInstall(mContext, shareMedia, FunnyApplication.mTencent, mContext.getString(R.string.weixin_appid))) {
            return;
        }
        if (null == bitmap) {
            Log.i("xing", "save: ------------------图片为空------");
            return;
        }

        try {
            UMImage image = new UMImage(this, bitmap);//bitmap文件
//            UMImage thumb = new UMImage(this, R.mipmap.icon_straight);
//            image.setThumb(thumb);
            new ShareAction(this).
                    setPlatform(shareMedia).
                    withText("img")
                    .setCallback(shareListener).
                    withMedia(image).share();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void starActivity(Context mContext, String from) {
        Intent intent = new Intent(mContext, MoodReportActivity.class);
        intent.putExtra("from", from);
        ActivityUtils.startActivity(intent);
    }

    /**
     * view转bitmap
     */
    public Bitmap viewConversionBitmap(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }


    public void savePicture(Context mContext, Bitmap bitmap, String fileName) {

        if (Build.VERSION.SDK_INT >= 23 && !PermissionHandler.isHandlePermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            CommonUtils.showShort("请检查存储权限");
            return;
        }


        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            CommonUtils.showShort("请检查SD卡是否可用");
            return;
        }

        if (null == bitmap) {
            Log.i("xing", "savePicture: ------------------图片为空------");
            return;
        }
//        File foder = new File(DownLoadUtil.showDownloadFolder());
        File foder = new File(Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/");
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(foder, fileName);
        try {
            if (!myCaptureFile.exists()) {
                myCaptureFile.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            //压缩保存到本地
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap = null;
            }
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveImage(mContext, myCaptureFile);
        CommonUtils.showShort("图片保存成功!");
    }

    public static void saveImage(Context context, File file) {
        ContentResolver localContentResolver = context.getContentResolver();
        ContentValues localContentValues = getImageContentValues(context, file, System.currentTimeMillis());
        localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, localContentValues);

        Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        final Uri localUri = Uri.fromFile(file);
        localIntent.setData(localUri);
        context.sendBroadcast(localIntent);
    }

    public static ContentValues getImageContentValues(Context paramContext, File paramFile, long paramLong) {
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "image/jpeg");
        localContentValues.put("datetaken", Long.valueOf(paramLong));
        localContentValues.put("date_modified", Long.valueOf(paramLong));
        localContentValues.put("date_added", Long.valueOf(paramLong));
        localContentValues.put("orientation", Integer.valueOf(0));
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", Long.valueOf(paramFile.length()));
        return localContentValues;
    }


}

