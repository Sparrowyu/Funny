package com.sortinghat.funny.ui.my;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sortinghat.common.base.BaseFragment;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.MyFansAttentionAdapter;
import com.sortinghat.funny.bean.MyFansAttentionListBean;
import com.sortinghat.funny.databinding.FragmentMyFansBinding;
import com.sortinghat.funny.viewmodel.MyFansAttentionViewModel;

import java.util.List;

public class MyFansFragment extends BaseFragment<MyFansAttentionViewModel, FragmentMyFansBinding> {
    private MyFansAttentionAdapter fansAttentionAdapter;
    private int tabType = 0;//0:我的粉丝 1:关注
    private int pageNum = 1;
    private int singleSize = 12;

    public MyFansFragment() {

    }

    public MyFansFragment(int pos) {
        this.tabType = pos;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_fans;
    }

    @Override
    protected void initViews() {
        initViewPagerAdapter();
    }

    private void initViewPagerAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        contentLayoutBinding.recyclerView.setLayoutManager(linearLayoutManager);
        fansAttentionAdapter = new MyFansAttentionAdapter(getActivity(), getChildFragmentManager());
        contentLayoutBinding.recyclerView.setAdapter(fansAttentionAdapter);
        contentLayoutBinding.recyclerView.setEmptyView(getEmptyView());
        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
        contentLayoutBinding.recyclerView.setOnLoadMoreListener(() -> {
            pageNum++;
            getDataList();
        }, 100);

    }

    @Override
    protected void initData() {
        fansAttentionAdapter.setViewModel(viewModel);
        getDataList();
    }

    private void getDataList() {
//        contentLayoutBinding.getRoot().setVisibility(View.VISIBLE);
        viewModel.getFansAttentionList(tabType, pageNum).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    List<MyFansAttentionListBean> beanList = resultBean.getData();
                    if (beanList != null && !beanList.isEmpty() && beanList.size() > 0) {
                        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
                        showContentView();
                        fansAttentionAdapter.addData(beanList);
                        contentLayoutBinding.recyclerView.loadMoreComplete();
                        if (beanList.size() < singleSize) {
                            contentLayoutBinding.recyclerView.loadMoreEnd();
                        }
                    } else {
                        contentLayoutBinding.recyclerView.loadMoreEnd();
                        if (pageNum == 1 && beanList.size() == 0) {
                            if (tabType == 0) {
                                showEmptyView("单枪匹马你别怕，发文就把粉留下", R.mipmap.empty_bg_no_fans);
                            } else {
                                showEmptyView("不求门当户对，只求关注到位", R.mipmap.empty_bg_add_attention);
                            }
                            contentLayoutBinding.recyclerView.setEmptyViewEnabled(true);
                        }
                    }
                }
            }
        });
    }
}