package com.sortinghat.funny.ui.my;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.ActivityUtils;
import com.google.gson.JsonObject;
import com.sortinghat.common.base.BaseFragment;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.TaskTodayAdapter;
import com.sortinghat.funny.bean.TaskTodayItemBean;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.DialogTaskInviteCodeBinding;
import com.sortinghat.funny.databinding.FragmentTaskTodayBinding;
import com.sortinghat.funny.ui.MainActivity;
import com.sortinghat.funny.util.CommonUserInfo;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.util.MaterialDialogUtil;
import com.sortinghat.funny.viewmodel.TaskCentralViewModel;

import java.util.List;

public class TaskTodayFragment extends BaseFragment<TaskCentralViewModel, FragmentTaskTodayBinding> {

    private TaskTodayAdapter taskAdapter;
    private int tabType = 0;//0:今日任务 1:新手任务

    public TaskTodayFragment() {
    }

    public TaskTodayFragment(int tabType) {
        this.tabType = tabType;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_task_today;
    }

    @Override
    protected void initViews() {
        contentLayoutBinding.getRoot().setVisibility(View.VISIBLE);
        initAdapter();
    }


    private void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        contentLayoutBinding.recyclerView.setLayoutManager(linearLayoutManager);

        taskAdapter = new TaskTodayAdapter(activity);
        contentLayoutBinding.recyclerView.setAdapter(taskAdapter);
        contentLayoutBinding.recyclerView.setEmptyView(getEmptyView(true));
        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
        contentLayoutBinding.recyclerView.setLoadMoreEnabled(false);
        taskAdapter.setOnItemClickListener((position, clickType) -> {
            if (taskAdapter != null && taskAdapter.getData().size() > position) {
                int taskId = taskAdapter.getData().get(position).getId();
                if (taskId == 7) {
                    //输入邀请码弹框
                    showInviteDialog();
                } else {
                    //0：跳转 1：领取奖励
                    if (clickType == 0) {
                        switch (taskId) {
                            case 1://观看时长 首页
                            case 3://发表评论  首页
                            case 4://分享帖子   首页
                                RxBus.getDefault().post(RxCodeConstant.OTHER_ACTIVITY_TO_HOME, 0);
                                ActivityUtils.startActivity(MainActivity.class);
                                break;
                            case 2://发布帖子 发布页
                                RxBus.getDefault().post(RxCodeConstant.PUBLISH_VIDEO_OR_IMG, 3);
                                ActivityUtils.startActivity(MainActivity.class);
                                break;
                            case 5://邀请好友   toast
                                ClipboardManager cmb = (ClipboardManager) FunnyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                cmb.setText("" + CommonUserInfo.myInviteCode + "  是我的邀请码，请打开搞笑星球app->我->任务中心->新手任务填写，谢谢~");
                                CommonUtils.showLong("邀请码已复制，快去分享给你的好友吧");
                                break;
                            case 6://绑定手机号  个人页
                            case 8://设置头像昵称   个人页
                                RxBus.getDefault().post(RxCodeConstant.OTHER_ACTIVITY_TO_HOME, 4);
                                ActivityUtils.startActivity(MainActivity.class);
                                break;

                            case 7://填写邀请码
                                break;

                            default:
                                break;
                        }

                    } else {
                        getTaskReceiveAward(taskAdapter.getData().get(position).getCompleteNum(), taskId, taskAdapter.getData().get(position).getTaskAward(), taskAdapter.getData().get(position).getTaskType(), position);

                    }
                }
            } else {
                CommonUtils.showShort("请退出重试");
            }
        });
    }

    @Override
    protected void initData() {
        taskAdapter.setViewModel(viewModel);
        getDataList();
    }

    private void getTaskReceiveAward(int completeNum, int taskId, int taskReward, int taskType, int position) {
        progressDialog = MaterialDialogUtil.showCustomProgressDialog(activity);
        viewModel.getTaskReceiveAward(completeNum, taskId, taskType).observe(this, resultBean -> {
            closeProgressDialog();

            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    int awardStarNote = resultBean.getData().getAwardStarNote();
                    RxBus.getDefault().post(RxCodeConstant.TASK_TO_LOAD_REWARD_VIDEO, taskId + "," + awardStarNote);
                    taskAdapter.clear();
                    getDataList();
                    if (null != resultBean.getData() && !TextUtils.isEmpty(resultBean.getData().toString())) {
//                        CommonUtils.showShort(resultBean.getData().getToast());
                    }
                }
            }
        });
    }


    private void showInviteDialog() {
        MaterialDialog dialog = MaterialDialogUtil.showCustomWithCloseButtonDialog(activity, R.layout.dialog_task_invite_code);
        DialogTaskInviteCodeBinding inviteCodeBinding = DataBindingUtil.bind(dialog.getCustomView());

        inviteCodeBinding.dialogCancel.setOnClickListener(view -> {
            dialog.dismiss();
        });

        inviteCodeBinding.dialogSure.setOnClickListener(view -> {
            String inviteCode = inviteCodeBinding.etInviteCode.getText().toString();
            if (TextUtils.isEmpty(inviteCode) || inviteCode.length() < 4) {
                CommonUtils.showShort("请输入正确的邀请码");
                return;
            }
            if (CommonUserInfo.myInviteCode.equals(inviteCode)) {
                CommonUtils.showShort("邀请码无效");
                return;
            }

            getInviteFriendCode(inviteCode);
            dialog.dismiss();
        });

        ConstantUtil.showSoftInputFromWindow(activity, inviteCodeBinding.etInviteCode);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) dialog.getCustomView().getLayoutParams();
        params.gravity = Gravity.CENTER;
        dialog.getCustomView().setLayoutParams(params);
        dialog.show();

    }

    private void getInviteFriendCode(String inviteCode) {
        if (TextUtils.isEmpty(inviteCode) || inviteCode.length() < 4) {
            CommonUtils.showShort("请输入正确的邀请码");
            return;
        }
        progressDialog = MaterialDialogUtil.showCustomProgressDialog(activity);
        viewModel.getInviteFriendCode(inviteCode).observe(this, resultBean -> {
            closeProgressDialog();
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    taskAdapter.clear();
                    getDataList();
                    viewModel.setAppUnifyLog("invitation_code_succ", "taskCenter", activity).observe(this, resultBean1 -> {
                    });
                    if (null != resultBean.getData() && !TextUtils.isEmpty(resultBean.getData().toString())) {
                        CommonUtils.showShort(resultBean.getData().toString());
                    }
                } else {
                    if (null != resultBean.getMsg() && !TextUtils.isEmpty(resultBean.getMsg())) {
                        CommonUtils.showShort(resultBean.getMsg());
                    }
                }

            }
        });
    }

    private void getDataList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("taskType", tabType);
        viewModel.getTaskTodayList(jsonObject).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    List<TaskTodayItemBean> beanList = resultBean.getData();
                    if (beanList != null && !beanList.isEmpty() && beanList.size() > 0) {
                        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
                        showContentView();
                        taskAdapter.addData(beanList);
                        contentLayoutBinding.recyclerView.loadMoreComplete();
                    } else {
                        showEmptyView("没有数据哦", R.mipmap.empty_bg_add_attention);
                        contentLayoutBinding.recyclerView.setEmptyViewEnabled(true);
                    }
                }
            }
        });


    }
}
