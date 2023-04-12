package com.sortinghat.funny.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;

import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.FunnyApplication;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.TaskTodayItemBean;
import com.sortinghat.funny.databinding.ItemTaskTodayBinding;
import com.sortinghat.funny.util.CommonUserInfo;
import com.sortinghat.funny.viewmodel.TaskCentralViewModel;


/**
 * Created by wzy on 2021/6/28
 */
public class TaskTodayAdapter extends BaseBindingAdapter<TaskTodayItemBean, ItemTaskTodayBinding> {
    private Context mContext;
    private TaskCentralViewModel viewModel;

    public void setViewModel(TaskCentralViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public TaskTodayAdapter(Context mContext) {
        super(R.layout.item_task_today);
        this.mContext = mContext;
    }

    @Override
    protected void bindView(BaseBindingHolder holder, TaskTodayItemBean info, ItemTaskTodayBinding binding, int position) {

        if (info != null) {
            int clickType = 0;
            int canComNum = info.getCanCompleteNum();
            //已完成，领取奖励 能点击，已领取和未完成情况下，不能点击
            if (info.getIsFinish() == 1 && info.getIsReceiveAward() == 1) {
                binding.tvBt.setBackgroundResource(R.drawable.task_done_bg_border);
                binding.tvBt.setTextColor(CommonUtils.getColor(R.color.color_999999));
                binding.tvBt.setText("已完成");
                binding.tvBt.setEnabled(false);
            } else {
                binding.tvBt.setEnabled(true);
                binding.tvBt.setBackgroundResource(R.drawable.click_can_orange_bt_bg);
                binding.tvBt.setTextColor(CommonUtils.getColor(R.color.white));
                //领取奖励的状态,-1是邀请码的情况
                if (info.getIsReceiveAward() == 0 && info.getCompleteNum() > 0 && (info.getReceiveNum() < info.getCompleteNum())) {
                    binding.tvBt.setText("领取奖励");
                    clickType = 1;
                } else {
                    binding.tvBt.setText("去完成");
                    clickType = 0;
                }
            }

            if (info.getId() == 7) {
                binding.tvBt.setVisibility(View.VISIBLE);
                if (info.getIsFinish() == 1) {
                    binding.tvBt.setBackgroundResource(R.drawable.task_done_bg_border);
                    binding.tvBt.setTextColor(CommonUtils.getColor(R.color.color_999999));
                    binding.tvBt.setText("已完成");
                    binding.tvBt.setEnabled(false);
                } else {
                    binding.tvBt.setBackgroundResource(R.drawable.click_can_orange_bt_bg);
                    binding.tvBt.setTextColor(CommonUtils.getColor(R.color.white));
                    binding.tvBt.setText("去填写");
                    binding.tvBt.setEnabled(true);
                }
            }

            binding.taskItemNum.setText("(" + info.getCompleteNum() + "/" + (canComNum == -1 ? "*" : canComNum) + ")");
            GlideUtils.loadImage(info.getIcon(), binding.taskItemIcon);
            binding.taskItemName.setText(info.getTaskName());
            binding.taskItemDes.setText(info.getTaskDescription());
            String inviteCode = info.getInviteCode();
            if (!TextUtils.isEmpty(inviteCode)) {
                String infoDes = info.getTaskDescription();

                if (infoDes.contains("300星币")) {
                    int startTag = infoDes.indexOf("300星币");
                    SpannableString spanString1 = new SpannableString(infoDes);
                    spanString1.setSpan(new TextAppearanceSpan(mContext, R.style.invite_gold_task_tx), startTag, startTag + 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    binding.taskItemDes.setText(spanString1);

                }
                CommonUserInfo.myInviteCode = inviteCode;
                binding.tvInvitecode.setVisibility(View.VISIBLE);
                binding.tvInvitecode.setText("我的邀请码：" + inviteCode);
                binding.tvInvitecode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipboardManager cmb = (ClipboardManager) FunnyApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        cmb.setText("" + CommonUserInfo.myInviteCode + "  是我的邀请码，请打开搞笑星球app->我->任务中心->新手任务填写，谢谢~");
                        CommonUtils.showLong("邀请码已复制，快去分享给你的好友吧");
                    }
                });
            }

            int finalClickType = clickType;
            binding.tvBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        //0:跳转其他页面 1：领取奖励
                        onItemClickListener.onClick(position, finalClickType);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(int position, int type);
    }
}
