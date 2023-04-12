package com.sortinghat.funny.ui.my;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.common.view.GridSpacingItemDecoration;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.TaskStoreAdapter;
import com.sortinghat.funny.bean.StoreTaskListBean;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityStoreFunnyBinding;
import com.sortinghat.funny.databinding.DialogTaskStoreConfirmBinding;
import com.sortinghat.funny.util.CommonUserInfo;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.view.ConstantView;
import com.sortinghat.funny.viewmodel.TaskCentralViewModel;

import java.util.List;

public class StoreFunnyActivity extends BaseActivity<TaskCentralViewModel, ActivityStoreFunnyBinding> {
    TaskStoreAdapter taskAdapter;
    private int currentStarCount = 0;
    private int pageNum = 1;
    private int singleSize = 12;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_store_funny;
    }

    @Override
    protected void initViews() {
        subscibeRxBus();
        initTitleBar("搞笑商城");
        titleBarBinding.vDividerLine.setVisibility(View.GONE);
        contentLayoutBinding.tvPackage.setOnClickListener(quickClickListener);
        initAdapter();

    }

    private void initAdapter() {
        //初始化recyclerView
        contentLayoutBinding.recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        contentLayoutBinding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, SizeUtils.dp2px(12), true));
        taskAdapter = new TaskStoreAdapter(mContext, 100);
        contentLayoutBinding.recyclerView.setAdapter(taskAdapter);
        contentLayoutBinding.recyclerView.setOnLoadMoreListener(() -> {
            pageNum++;
            getDataList();
        }, 100);
        taskAdapter.setOnItemClickListener((position, isCheck) -> {
            if (taskAdapter != null && taskAdapter.getData().size() > position) {
                int price = taskAdapter.getData().get(position).getPrice();
                int taskId = taskAdapter.getData().get(position).getId();
                if (currentStarCount < price) {
                    CommonUtils.showShort("星币不够，赶紧做任务吧");
                } else {
                    showConfirmStoreDialog(taskId, price, taskAdapter.getData().get(position).getProductName());
                }
            } else {
                CommonUtils.showShort("请退出重试");
            }
        });
    }

    @Override
    protected void initData() {
        getDataList();
        getStoreTopMsg();
        viewModel.setAppUnifyLog("entry_store_succ", "user_profile", this).observe(this, resultBean -> {
        });
    }

    private void getDataList() {
        viewModel.getStoreTaskList(pageNum, singleSize).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    currentStarCount = resultBean.getData().getStarNoteCount();
                    ConstantView.initGoldAnim(contentLayoutBinding.tvStarCount, currentStarCount + "");
                    String pendantUrl = resultBean.getData().getPendantUrl();
                    updateUserIcon(pendantUrl);
                    if (!TextUtils.isEmpty(CommonUserInfo.userIconImg)) {
                        GlideUtils.loadCircleImage(CommonUserInfo.userIconImg, contentLayoutBinding.ivUserIcon);
                    }
                    List<StoreTaskListBean.ProductBean> beanList = resultBean.getData().getProduct();
                    if (beanList != null && !beanList.isEmpty() && beanList.size() > 0) {
                        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
                        showContentView();
                        taskAdapter.addData(beanList);
                        contentLayoutBinding.recyclerView.loadMoreComplete();
                        if (beanList.size() < singleSize) {
                            contentLayoutBinding.recyclerView.loadMoreEnd();
                        }
                    } else {
                        contentLayoutBinding.recyclerView.loadMoreEnd();
                        if (pageNum == 1 && beanList.size() == 0) {
                            showEmptyView("没有哦", R.mipmap.empty_bg_add_attention);
                            contentLayoutBinding.recyclerView.setEmptyViewEnabled(true);
                        }
                    }
                }
            }
        });
    }

    private void updateUserIcon(String pendantUrl) {
        CommonUserInfo.userIconImgBox = pendantUrl;
        if (!TextUtils.isEmpty(pendantUrl)) {
            contentLayoutBinding.ivBoxUserIcon.setVisibility(View.VISIBLE);
            GlideUtils.loadImageNoPlaceholder(pendantUrl, contentLayoutBinding.ivBoxUserIcon);
        } else {
            contentLayoutBinding.ivBoxUserIcon.setVisibility(View.GONE);
        }
    }

    private void showConfirmStoreDialog(int taskId, int price, String proName) {
        MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(mContext, R.layout.dialog_task_store_confirm);
        DialogTaskStoreConfirmBinding storeConfirmBinding = DataBindingUtil.bind(dialog.getCustomView());

        String s1 = "确认花费" + price + "星币兑换";
        String s2 = "【" + proName + "】吗？";

        SpannableString spanString1 = new SpannableString(s1);
        spanString1.setSpan(new TextAppearanceSpan(mContext, R.style.store_task_tx), 4, s1.length() - 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        storeConfirmBinding.dialogTx1.setText(spanString1);
        storeConfirmBinding.dialogTx2.setText(s2);

        storeConfirmBinding.dialogCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        storeConfirmBinding.dialogSure.setOnClickListener(view -> {
            getExchangeStore(taskId, proName);
            dialog.dismiss();
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
        params.gravity = Gravity.CENTER;
        dialog.getCustomView().setLayoutParams(params);
        dialog.show();

    }

    private void getExchangeStore(int taskId, String proName) {
        progressDialog = MaterialDialogUtil.showCustomProgressDialog(mContext);
        viewModel.getExchangeStore(taskId).observe(this, resultBean -> {
            closeProgressDialog();
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    //0是购买成功
//                    pageNum = 1;
//                    taskAdapter.clear();
//                    getDataList();
                    currentStarCount = resultBean.getData().getStarNote();//余额
                    ConstantView.initGoldAnim(contentLayoutBinding.tvStarCount, currentStarCount + "");

                    viewModel.setAppUnifyLog("products_exchange_succ", "store", this).observe(this, resultBean1 -> {
                    });

                    String showMes = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;恭喜" + CommonUserInfo.myName + "兑换了【" + "<font color='#333333'>" + proName + "</font>" + "】" + "👏" + "👏" + "👏";//空格&nbsp;
                    contentLayoutBinding.tvStoreMes.setText(Html.fromHtml(showMes));
                }
                if (null != resultBean.getData() && !TextUtils.isEmpty(resultBean.getData().getToast())) {
                    CommonUtils.showShort(resultBean.getData().getToast());
                }

            }
        });
    }

    private void getStoreTopMsg() {
        viewModel.getStoreBroadcastMsg().observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    if (null != resultBean.getData() && !TextUtils.isEmpty(resultBean.getData().toString())) {
                        String showMes = "";
                        for (int i = 0; i < resultBean.getData().size(); i++) {
                            String msg1 = resultBean.getData().get(0).getContent();
                            String msg2 = resultBean.getData().get(0).getHighLight();
                            int start = msg1.lastIndexOf(msg2);
                            showMes = showMes + msg1.substring(0, start) + "<font color='#333333'>" + msg2 + "</font>" + msg1.substring(msg1.length() - 1) + "👏" + "👏" + "👏&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;";//空格&nbsp;
                        }
                        contentLayoutBinding.tvStoreMes.setText(Html.fromHtml(showMes));
                        String productUrl = resultBean.getData().get(0).getProductUrl();
                    }
                }
            }
        });
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.TASK_TO_UPDATE_STORE_USER_BOX, String.class)
                .subscribe(pendantUrl -> {
                    pageNum = 1;
                    taskAdapter.clear();
                    getDataList();
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.tv_package:
                    PackageFunnyActivity.starActivity(mContext, "store");
                    LogUtils.d("ForbidTopics---", "-message:");
                    break;

            }
        }
    };

    public static void starActivity(Context mContext, String hint) {
        Intent intent = new Intent(mContext, StoreFunnyActivity.class);
        intent.putExtra("hint", hint);
        ActivityUtils.startActivity(intent);
    }

}

