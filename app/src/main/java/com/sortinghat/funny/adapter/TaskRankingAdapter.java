package com.sortinghat.funny.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.bean.TaskRankListBean;
import com.sortinghat.funny.databinding.ItemTaskRankBinding;
import com.sortinghat.funny.util.ConstantUtil;


/**
 * Created by wzy on 2021/6/28
 */
public class TaskRankingAdapter extends BaseBindingAdapter<TaskRankListBean, ItemTaskRankBinding> {
    private int hadDressIndex = -1;
    private Context mContext;

    public TaskRankingAdapter(Context mContext) {
        super(R.layout.item_task_rank);
        this.mContext = mContext;
    }

    @Override
    protected void bindView(BaseBindingHolder holder, TaskRankListBean infoBean, ItemTaskRankBinding binding, int position) {

        if (infoBean != null) {
            String rank = infoBean.getRank();
            binding.taskItemRank.setText(infoBean.getRank());
            if (rank.equals("1")) {
                binding.taskItemRank.setText("");
                binding.taskItemRank.setBackgroundResource(R.mipmap.task_rank_first);
            } else if (rank.equals("2")) {
                binding.taskItemRank.setText("");
                binding.taskItemRank.setBackgroundResource(R.mipmap.task_rank_second);
            } else if (rank.equals("3")) {
                binding.taskItemRank.setText("");
                binding.taskItemRank.setBackgroundResource(R.mipmap.task_rank_third);
            } else {
                binding.taskItemRank.setText(infoBean.getRank());
                binding.taskItemRank.setBackgroundResource(0);
            }

            if (rank.equals("1")) {
                binding.taskGoldNum.setTextColor(CommonUtils.getColor(R.color.color_FE6937));
            } else {
                binding.taskGoldNum.setTextColor(CommonUtils.getColor(R.color.color_333333));
            }

            binding.taskGoldNum.setText(infoBean.getStarNoteCount() + "");
            String name = infoBean.getNickname();
            if (!TextUtils.isEmpty(name) && name.length() > 8) {
                name = name.substring(0, 8) + "...";
            }
            binding.taskItemName.setText(name);

            GlideUtils.loadCircleImage(infoBean.getAvatar(), R.mipmap.user_icon_default, binding.ivUserIcon);
            if (!TextUtils.isEmpty(infoBean.getPendantUrl())) {
                binding.ivBoxUserIcon.setVisibility(View.VISIBLE);
                ConstantUtil.glideLoadPendantUrl(infoBean.getPendantUrl(), binding.ivBoxUserIcon);
            } else {
                binding.ivBoxUserIcon.setVisibility(View.GONE);
            }
            binding.itemView.setOnClickListener(v -> onItemClickListener.onClick(position, infoBean.getUserId(), 0));

        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // clickType 0 :删除 1装备
    public interface OnItemClickListener {
        void onClick(int position, long userID, int clickType);
    }
}
