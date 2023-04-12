package com.sortinghat.funny.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.funny.R;

import com.sortinghat.funny.bean.HomeReportBean;
import com.sortinghat.funny.databinding.ItemReportDataBinding;

import java.util.ArrayList;
import java.util.List;

import me.jingbin.library.adapter.BaseByRecyclerViewAdapter;
import me.jingbin.library.adapter.BaseByViewHolder;

/**
 * Created by wzy on 2021/6/28
 */
public class HomeReportAdapter extends BaseByRecyclerViewAdapter<HomeReportBean.ListBean, BaseByViewHolder<HomeReportBean.ListBean>> {
    private Context mContext;
    public static final int TYPE_TITLE_VIEW = 1;
    private static final int TYPE_DATA_VIEW = 2;
    private int preClickPos = 0;
    List<HomeReportBean.ListBean> beanList = new ArrayList<>();

    public HomeReportAdapter(Context mContext, List<HomeReportBean.ListBean> beanList) {
        super(beanList);
        this.beanList = beanList;
        this.mContext = mContext;
    }

    @Override
    public int getItemViewType(int position) {
        Log.e("reportString---", "pos:" + getItemData(position).getDataString() + "--title" + getItemData(position).isTitle());
        if (getItemData(position).isTitle()) {
            return TYPE_TITLE_VIEW;
        } else {
            return TYPE_DATA_VIEW;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    switch (type) {
                        case TYPE_TITLE_VIEW:
                            // title栏显示一列
                            return gridManager.getSpanCount();
                        default:
                            //默认显示2列
                            return 1;
                    }
                }
            });
        }
    }


    @NonNull
    @Override
    public BaseByViewHolder<HomeReportBean.ListBean> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (TYPE_TITLE_VIEW == viewType) {
            return new TitleHolder(parent, R.layout.item_report_title);
        } else {
            return new ViewHolder(parent, R.layout.item_report_data);
        }
    }

    private class TitleHolder extends BaseByViewHolder<HomeReportBean.ListBean> {
        TitleHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        protected void onBaseBindView(BaseByViewHolder<HomeReportBean.ListBean> holder, HomeReportBean.ListBean bean, int position) {
            holder.setText(R.id.tv_title, bean.getDataString());
        }
    }

    private class ViewHolder extends BaseBindingHolder<HomeReportBean.ListBean, ItemReportDataBinding> {
        ViewHolder(ViewGroup viewGroup, int layoutId) {
            super(viewGroup, layoutId);
        }

        @Override
        protected void onBindingView(BaseBindingHolder holder, HomeReportBean.ListBean bean, int position) {
            binding.ckData.setText(bean.getDataString());
            binding.ckData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    beanList.get(preClickPos).setCheck(false);
                    beanList.get(position).setCheck(true);
                    notifyItemChanged(preClickPos);
                    notifyItemChanged(position);
                    preClickPos = position;
                }
            });

            if (bean.isCheck()) {
                binding.ckData.setChecked(true);
//                bean.setCheck(true);
            } else {
                binding.ckData.setChecked(false);
//                bean.setCheck(false);
            }

        }
    }
}
