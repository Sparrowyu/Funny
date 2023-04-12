package com.sortinghat.funny.ui.my;

import static com.sortinghat.funny.thirdparty.clipView.FileUtil.getRealFilePathFromUri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.JsonObject;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.MyOwnerUserInfoBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityMyEditInformationBinding;
import com.sortinghat.funny.interfaces.RequestCallback;
import com.sortinghat.funny.thirdparty.clipView.FileUtil;
import com.sortinghat.funny.ui.BottomSheetDialog.ChoseAvatarDialog;
import com.sortinghat.funny.ui.BottomSheetDialog.ChoseBirthdayDialog;
import com.sortinghat.funny.ui.BottomSheetDialog.ChoseMyCityDialog;
import com.sortinghat.funny.ui.BottomSheetDialog.ChoseSexDialog;
import com.sortinghat.funny.ui.BottomSheetDialog.ChoseWorkDialog;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.util.PermissionHandler;
import com.sortinghat.funny.viewmodel.MyEditInformationViewModel;

import java.io.File;

public class MyEditInformationActivity extends BaseActivity<MyEditInformationViewModel, ActivityMyEditInformationBinding> {
    //请求相机
    private static final int REQUEST_CAPTURE = 100;
    //请求相册
    private static final int REQUEST_PICK = 101;
    //请求截图
    private static final int REQUEST_CROP_PHOTO = 102;
    //请求访问外部存储
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //请求写入外部存储
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    //头像1
//    private CircleImageView headImage1;
    //头像2
//    private ImageView headImage2;
    //调用照相机返回图片文件
    private File tempFile;
    private Uri takePictureUri;
    private int photoType = 0;//0 相机 1相册
    private String sloganHint = "";
    private String nickName = "";
    private long id = 0;
    private int from = 0;//0：我的 1：消息

    JsonObject jsonObject;
    private boolean isEditUpdateMyFragment = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_edit_information;
    }

    @Override
    protected void initViews() {
        initTitleBar("编辑资料");
        if (null != getIntent()) {
            from = getIntent().getIntExtra("from", 0);
        }
        initListener();
        subscibeRxBus();
    }

    private void initListener() {
        contentLayoutBinding.editIcon.setOnClickListener(quickClickListener);
        contentLayoutBinding.editId.setOnClickListener(quickClickListener);
        contentLayoutBinding.editPhone.setOnClickListener(quickClickListener);
        contentLayoutBinding.editName.setOnClickListener(quickClickListener);
        contentLayoutBinding.editSex.setOnClickListener(quickClickListener);
        contentLayoutBinding.editWork.setOnClickListener(quickClickListener);
        contentLayoutBinding.editSign.setOnClickListener(quickClickListener);
        contentLayoutBinding.editBirthday.setOnClickListener(quickClickListener);
        contentLayoutBinding.editCity.setOnClickListener(quickClickListener);
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.MY_COMPLETEMENT_INFO, MyOwnerUserInfoBean.UserBaseBean.class)
                .subscribe(dataBean -> {
                    initViewData(dataBean);
                }));
    }

    private void initViewData(MyOwnerUserInfoBean.UserBaseBean dataBean) {
        if (dataBean == null) {
            return;
        }

        if (from == 1) {
            //从消息页面过来需要显示审核的结果
//        if (DeviceUtils.getSDKVersionCode() >= 21) {
//            //当有审核未通过时的文字提示
//            contentLayoutBinding.applyEditText.setVisibility(View.GONE);
//            contentLayoutBinding.applyRejectLayout.setVisibility(View.VISIBLE);
//        } else {
//            contentLayoutBinding.applyEditText.setVisibility(View.GONE);
//            contentLayoutBinding.applyRejectLayout.setVisibility(View.VISIBLE);
//        }
//        if (DeviceUtils.getSDKVersionCode() >= 21) {
//            //当图像审核未通过
//            contentLayoutBinding.iconRejectTx.setText("未通过");
//            contentLayoutBinding.iconRejectTx.setTextColor(Color.RED);
//        } else {
//            contentLayoutBinding.iconRejectTx.setText("点击更换图像");
//            contentLayoutBinding.iconRejectTx.setTextColor(getResources().getColor(R.color.color_d7d7d7));
//        }
//
//        if (DeviceUtils.getSDKVersionCode() >= 21) {
//            //当昵称审核未通过
//            contentLayoutBinding.userNameReject.setText("未通过");
//        } else {
//            contentLayoutBinding.userNameReject.setText("");
//        }
//
//        if (DeviceUtils.getSDKVersionCode() >= 21) {
//            //当签名审核未通过
//            contentLayoutBinding.tvSloganReject.setText("未通过");
//        } else {
//            contentLayoutBinding.tvSloganReject.setText("");
//        }
        }

        if (DeviceUtils.getSDKVersionCode() >= 21) {
            contentLayoutBinding.editCity.setVisibility(View.VISIBLE);
        } else {
            contentLayoutBinding.editCity.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(dataBean.getAvatar()) && dataBean.getAvatar().contains("http")) {
            GlideUtils.loadCircleImage(dataBean.getAvatar(), R.mipmap.user_icon_default, contentLayoutBinding.ivIcon);
        }
        id = dataBean.getId();
        contentLayoutBinding.tvId.setText(dataBean.getId() + "");
        if (!TextUtils.isEmpty(dataBean.getNickname())) {
            nickName = dataBean.getNickname();
            contentLayoutBinding.userName.setText(dataBean.getNickname());
            contentLayoutBinding.userName.setTextColor(CommonUtils.getColor(R.color.color_333333));
        }
        if (dataBean.getGender() != 0) {
            contentLayoutBinding.editSex.setEnabled(false);
            contentLayoutBinding.tvSex.setText(dataBean.getGender() == 1 ? "男" : "女");
            contentLayoutBinding.tvSex.setTextColor(CommonUtils.getColor(R.color.color_333333));
            contentLayoutBinding.ivSex.setVisibility(View.GONE);
        } else {
            contentLayoutBinding.editSex.setEnabled(true);
            contentLayoutBinding.ivSex.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(dataBean.getSlogan())) {
            sloganHint = dataBean.getSlogan();
            contentLayoutBinding.tvSlogan.setText(dataBean.getSlogan());
            contentLayoutBinding.tvSlogan.setTextColor(CommonUtils.getColor(R.color.color_333333));

        }
        if (!TextUtils.isEmpty(dataBean.getBirthday())) {
            contentLayoutBinding.tvBirthday.setText(dataBean.getBirthday());
            contentLayoutBinding.tvBirthday.setTextColor(CommonUtils.getColor(R.color.color_333333));

        }
        if (!TextUtils.isEmpty(dataBean.getCity())) {
            contentLayoutBinding.tvCity.setText(dataBean.getCity());
            contentLayoutBinding.tvCity.setTextColor(CommonUtils.getColor(R.color.color_333333));
        }
        if (!TextUtils.isEmpty(dataBean.getProfessional())) {
            contentLayoutBinding.tvProfessional.setText(dataBean.getProfessional());
            contentLayoutBinding.tvProfessional.setTextColor(CommonUtils.getColor(R.color.color_333333));
        }
        if (!TextUtils.isEmpty(dataBean.getPhoneNumShow())) {
            contentLayoutBinding.editPhone.setEnabled(false);
            contentLayoutBinding.tvPhone.setText(dataBean.getPhoneNumShow());
            contentLayoutBinding.tvPhone.setTextColor(CommonUtils.getColor(R.color.color_333333));
            contentLayoutBinding.ivPhone.setVisibility(View.GONE);
        } else {
            contentLayoutBinding.editPhone.setEnabled(true);
            contentLayoutBinding.ivPhone.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void initData() {
        jsonObject = new JsonObject();
        getOwerData();
    }

    private void getOwerData() {
        if (progressDialog == null) {
            progressDialog = MaterialDialogUtil.showCustomProgressDialog(this);
        }
        viewModel.getOwnerUserInfo().observe(MyEditInformationActivity.this, resultBean -> {
            closeProgressDialog();
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    initViewData(resultBean.getData().getUserBase());
                }
            }
        });
    }

    private void getData(JsonObject jsonObject) {
        getData(jsonObject, false);
    }

    private void getData(JsonObject jsonObject, boolean isShowToast) {
        if (jsonObject == null) {
            return;
        }
        isEditUpdateMyFragment = true;
        if (progressDialog == null) {
            progressDialog = MaterialDialogUtil.showCustomProgressDialog(this);
        }
        if (isShowToast) {
            CommonUtils.showLong("图像审核中，请耐心等待");
        }
        viewModel.completeUserInfo(jsonObject).observe(MyEditInformationActivity.this, resultBean -> {
            closeProgressDialog();
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (isShowToast) {
                        CommonUtils.showLong("图像审核中，请耐心等待");
                    } else {
                        initViewData(resultBean.getData());
                    }
                }
            }
        });
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.edit_icon:
                    choseAvatarDialog();
                    break;
                case R.id.edit_id:
                    ClipboardManager cmb = (ClipboardManager) FunnyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText("" + id);
                    CommonUtils.showShort("已复制ID");
                    break;
                case R.id.edit_name:
                    MyEditNameActivity.starActivity(mContext, nickName);
                    break;
                case R.id.edit_sex:
                    choseSexDialog();
                    break;
                case R.id.edit_work:
                    choseWorkDialog();
                    break;
                case R.id.edit_sign:
                    MySignActivity.starActivity(mContext, sloganHint);
                    break;
                case R.id.edit_birthday:
                    choseBirthdayDialog();
                    break;
                case R.id.edit_city:
                    choseMyCityDialog();
                    break;
                case R.id.edit_phone:
                    ActivityUtils.startActivity(BindPhoneActivity.class);
                    break;

            }
        }
    };


    /**
     * 获取图片路径
     *
     * @param context Context
     * @param uri     图片 Uri
     * @return 图片路径
     */
    public static String getImagePath(Context context, Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case REQUEST_CAPTURE: //调用系统相机返回
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        gotoClipActivity(takePictureUri);
                    } else {
                        gotoClipActivity(Uri.fromFile(tempFile));
                    }
                }
                break;
            case REQUEST_PICK:  //调用系统相册返回
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    gotoClipActivity(uri);
                }
                break;
            case REQUEST_CROP_PHOTO:  //剪切图片返回
                if (resultCode == RESULT_OK) {
                    final Uri uri = intent.getData();
                    if (uri == null) {
                        return;
                    }
                    String cropImagePath = getRealFilePathFromUri(getApplicationContext(), uri);
                    Bitmap bitMap = BitmapFactory.decodeFile(cropImagePath);
                    contentLayoutBinding.ivIcon.setImageBitmap(bitMap);
                    contentLayoutBinding.iconRejectTx.setText("图像审核中");
                    //此处后面可以将bitMap转为二进制上传后台网络
                    //......
//                    setAvatar(cropImagePath);
                    startUploadAvatarFile(cropImagePath);
                }
                break;
            default:
                break;
        }
    }

    //上传图像
    private void setAvatar(String cropImagePath) {
        viewModel.uploadPostFile(cropImagePath, null).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    jsonObject.addProperty("avatar", resultBean.getMsg());
                    getData(jsonObject);
                } else {
                    CommonUtils.showShort("失败请重试");
                }
            } else {
                CommonUtils.showShort("失败请重试");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void startUploadAvatarFile(String cropImagePath) {
//        if (progressDialog == null) {
//            progressDialog = MaterialDialogUtil.showCustomProgressDialog(this);
//        }
        long userId = SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getLong("user_id");

        String fileUniqueName = userId <= 0 ? "noUSerId" : userId + "/" + System.currentTimeMillis() + "." + FileUtils.getFileExtension(cropImagePath);

        if (TextUtils.isEmpty(fileUniqueName)) {
            return;
        }

        FunnyApplication.getUploadOssService().asyncUploadFile("userIcon/" + fileUniqueName, cropImagePath, new RequestCallback<PutObjectResult>() {
            @Override
            public void updateProgress(int progress) {
            }

            @Override
            public void onSuccess(PutObjectResult result) {
                closeProgressDialog();
                if (jsonObject != null) {
                    jsonObject.addProperty("avatar", fileUniqueName);
                    getData(jsonObject, true);
                }
            }

            @Override
            public void onFailure() {
                closeProgressDialog();
                CommonUtils.showShort("失败请重试");
            }
        });
    }


    /**
     * 打开截图界面
     */
    public void gotoClipActivity(Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(this, ClipImageActivity.class);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_CROP_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionHandler.onRequestPermissionsResult(this, "", requestCode, permissions, grantResults, new PermissionHandler.OnHandlePermissionListener() {
            @Override
            public void granted() {
                if (photoType == 0) {
                    takePhoto();
                } else {
                    selectGallery();
                }
            }

            @Override
            public void denied() {
            }

            @Override
            public void deniedAndAskNoMore() {
                if (photoType == 0) {
                    if (!SPUtils.getInstance(Constant.SP_PERMISSION_INFO).getBoolean("camera_permission")) {
                        SPUtils.getInstance(Constant.SP_PERMISSION_INFO).put("camera_permission", true);
                        return;
                    }
                    MaterialDialogUtil.showAlert(MyEditInformationActivity.this, "需要去权限设置页面打开相机权限", "去设置", "取消", (dialog, which) -> {
                        AppUtils.launchAppDetailsSettings(MyEditInformationActivity.this, requestCode);
                    });
                } else {
                    if (!SPUtils.getInstance(Constant.SP_PERMISSION_INFO).getBoolean("storage_permission")) {
                        SPUtils.getInstance(Constant.SP_PERMISSION_INFO).put("storage_permission", true);
                        return;
                    }
                    MaterialDialogUtil.showAlert(MyEditInformationActivity.this, "需要去权限设置页面打开存储权限", "去设置", "取消", (dialog, which) -> {
                        AppUtils.launchAppDetailsSettings(MyEditInformationActivity.this, requestCode);
                    });
                }
            }
        });
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void checkPermission() {
        if (photoType == 0) {
            if (PermissionHandler.isHandlePermission(this, Manifest.permission.CAMERA)) {
                takePhoto();
            }
        } else {
            if (PermissionHandler.isHandlePermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                selectGallery();
            }
        }
    }

    private void takePhoto() {
        try {
            //跳转到调用系统相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //创建拍照存储的图片文件
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //适配 Android Q
                String displayName = String.valueOf(System.currentTimeMillis());
                ContentValues values = new ContentValues(2);
                values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { //SD 卡是否可用，可用则用 SD 卡，否则用内部存储
                    takePictureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    takePictureUri = getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, takePictureUri);
            } else {
                tempFile = new File(FileUtil.checkDirPath(Environment.getExternalStorageDirectory().getPath() + "/image/"), System.currentTimeMillis() + ".jpg");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //设置7.0中共享文件，分享路径定义在xml/file_paths.xml
                    intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(MyEditInformationActivity.this, mContext.getPackageName() + ".fileprovider", tempFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                } else {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
                }
            }
            startActivityForResult(intent, REQUEST_CAPTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectGallery() {
        //跳转到调用系统图库
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "请选择图片"), REQUEST_PICK);
    }

    ChoseAvatarDialog choseAvatarDialog;

    private void choseAvatarDialog() {
        if (choseAvatarDialog != null && choseAvatarDialog.isVisible()) {
            LogUtils.d("shareDialog", "isShowing");
            return;
        }

        choseAvatarDialog = new ChoseAvatarDialog();
        choseAvatarDialog.show(getSupportFragmentManager(), "");
        choseAvatarDialog.setOnDialogSureListener(new ChoseAvatarDialog.OnDialogSureListener() {
            @Override
            public void choseTakePhone() {
                photoType = 0;
                choseAvatarDialog.dismiss();
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                    return;
                }
                takePhoto();
            }

            @Override
            public void choseGallery() {
                photoType = 1;
                choseAvatarDialog.dismiss();
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                    return;
                }
                selectGallery();
            }
        });
    }

    ChoseSexDialog choseSexDialog;

    private void choseSexDialog() {
        if (choseSexDialog != null && choseSexDialog.isVisible()) {
            LogUtils.d("shareDialog", "isShowing");
            return;
        }

        choseSexDialog = new ChoseSexDialog();
        choseSexDialog.show(getSupportFragmentManager(), "");
        choseSexDialog.setOnDialogSureListener(new ChoseSexDialog.OnDialogSureListener() {
            @Override
            public void choseBoy() {
                CommonUtils.showShort("选择男");
                jsonObject.addProperty("gender", 1);
                getData(jsonObject);
                choseSexDialog.dismiss();
            }

            @Override
            public void choseGirl() {
                CommonUtils.showShort("选择女");
                jsonObject.addProperty("gender", 2);
                getData(jsonObject);
                choseSexDialog.dismiss();
            }
        });
    }

    ChoseWorkDialog choseWorkDialog;
    private boolean choseWorkState = false;

    private void choseWorkDialog() {
        if (choseWorkDialog != null && choseWorkDialog.isVisible()) {
            LogUtils.d("shareDialog", "isShowing");
            return;
        }
        choseWorkDialog = new ChoseWorkDialog(choseWorkState);
        choseWorkDialog.show(getSupportFragmentManager(), "");
        choseWorkDialog.setOnDialogSureListener((i, professional) -> {
            if (i == 0) {
                choseWorkState = true;
                CommonUtils.showShort("展示");
            } else if (i == -1) {
                choseWorkState = false;
                CommonUtils.showShort("不展示");
            } else {
                CommonUtils.showShort("选择工作" + professional);
                jsonObject.addProperty("professional", professional);
                getData(jsonObject);
                choseWorkDialog.dismiss();
            }
        });
    }

    ChoseBirthdayDialog choseBirthdayDialog;
    private boolean choseBirthdayState = false;

    private void choseBirthdayDialog() {
        if (choseBirthdayDialog != null && choseBirthdayDialog.isVisible()) {
            LogUtils.d("shareDialog", "isShowing");
            return;
        }
        choseBirthdayDialog = new ChoseBirthdayDialog(choseBirthdayState);
        choseBirthdayDialog.show(getSupportFragmentManager(), "");
        choseBirthdayDialog.setOnDialogSureListener((i, birthday) -> {
            if (i == 0) {
                choseBirthdayState = true;
                CommonUtils.showShort("展示");
            } else if (i == -1) {
                choseBirthdayState = false;
                CommonUtils.showShort("不展示");
            } else {
                jsonObject.addProperty("birthday", birthday);
                getData(jsonObject);
                CommonUtils.showShort("选择年龄" + birthday);
                choseBirthdayDialog.dismiss();
            }
        });
    }

    ChoseMyCityDialog choseMyCityDialog;

    private void choseMyCityDialog() {


        if (choseMyCityDialog != null && choseMyCityDialog.isVisible()) {
//            LogUtils.d("shareDialog", "isShowing");
            return;
        }
        choseMyCityDialog = new ChoseMyCityDialog(this);
        choseMyCityDialog.show(getSupportFragmentManager(), "");
        choseMyCityDialog.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                String citySting = province.getName() + city.getName() + district.getName();
                jsonObject.addProperty("city", citySting);
                getData(jsonObject);
                CommonUtils.showShort(citySting);
                choseMyCityDialog.dismiss();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (from == 0 && isEditUpdateMyFragment) {
            RxBus.getDefault().post(RxCodeConstant.UPDATE_MYFRAGMENT_HEADER, 1);
        }
    }

    public static void starActivity(Context mContext, int from) {
        Intent intent = new Intent(mContext, MyEditInformationActivity.class);
        intent.putExtra("from", from);//0：我的 1：消息
        ActivityUtils.startActivity(intent);
    }
}

