package com.sortinghat.funny.ui.my;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.base.BaseFragment;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.MyLikeImgAdapter;
import com.sortinghat.funny.bean.DetailListBean;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.constant.Constant;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.FragmentMyImgBinding;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.viewmodel.MyFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

import me.jingbin.library.decoration.GridSpaceItemDecoration;

public class MyLikeImgFragment extends BaseFragment<MyFragmentViewModel, FragmentMyImgBinding> {
    private int tabType = 0;//0:我的发布 1:我的评论 2：我的喜欢
    private int likeType = 0;//0:全部 2:以后看 3：触动内心
    private long userIdTag = 0;//0：我的页面 其他:别的用户
    private long postId = 0;//0：我的页面 其他:别的用户ID
    private int pageNum = 1;
    private boolean isCanLoadMore = true;
    private List<TextView> tabLikeList = new ArrayList<TextView>();

    public MyLikeImgFragment() {

    }

    public MyLikeImgFragment(int pos, long userIdTag, long postId) {
        this.tabType = pos;
        this.userIdTag = userIdTag;
        this.postId = postId;
    }

    private MyLikeImgAdapter videoOrImageAdapter;
    private List<HomeVideoImageListBean.ListBean> homeVideoBeanList = new ArrayList<>();
    private int singleSize = 24;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_img;
    }

    @Override
    protected void initViews() {
        initAdapter();
    }

    private void onTabLikeChanged(int position) {
        likeType = position;
        for (int i = 0; i < tabLikeList.size(); i++) {
            if ((i == position && position == 0) || (i == position - 1)) {
                tabLikeList.get(i).setTextColor(getResources().getColor(R.color.color_333333));
            } else {
                tabLikeList.get(i).setTextColor(getResources().getColor(R.color.color_999999));
            }
        }
        refreshData();
    }

    private void initAdapter() {
        if (tabType == 2) {
            tabLikeList.add(contentLayoutBinding.tvLikeType0);
            tabLikeList.add(contentLayoutBinding.tvLikeType1);
            tabLikeList.add(contentLayoutBinding.tvLikeType2);
            tabLikeList.add(contentLayoutBinding.tvLikeType3);
            tabLikeList.add(contentLayoutBinding.tvLikeType4);
            tabLikeList.add(contentLayoutBinding.tvLikeType5);
            contentLayoutBinding.likeTypeScrollView.setVisibility(View.VISIBLE);
            initListener();
        } else {
            contentLayoutBinding.likeTypeScrollView.setVisibility(View.GONE);
        }
        contentLayoutBinding.recyclerView.setLayoutManager(new GridLayoutManager(activity, 3));
        // 四周也有间距
        GridSpaceItemDecoration itemDecoration = new GridSpaceItemDecoration(
                SizeUtils.dp2px(2), true);
        // 去掉首尾的分割线 (刷新头部和加载更多尾部)
        contentLayoutBinding.recyclerView.addItemDecoration(itemDecoration.setNoShowSpace(1, 1));
//        contentLayoutBinding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, SizeUtils.dp2px(4), false));
        videoOrImageAdapter = new MyLikeImgAdapter(activity, userIdTag == 0 && tabType == 0);
        contentLayoutBinding.recyclerView.setAdapter(videoOrImageAdapter);
        contentLayoutBinding.recyclerView.setEmptyView(getEmptyView(true));
        contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
        contentLayoutBinding.recyclerView.setOnRefreshListener(() -> refreshData());
        contentLayoutBinding.recyclerView.setOnLoadMoreListener(() -> {
            pageNum++;
            getVideoList();
        }, 100);
    }

    private void initListener() {

        contentLayoutBinding.tvLikeType0.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvLikeType1.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvLikeType2.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvLikeType3.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvLikeType4.setOnClickListener(quickClickListener);
        contentLayoutBinding.tvLikeType5.setOnClickListener(quickClickListener);

    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {

            switch (v.getId()) {
                case R.id.tv_like_type0:
                    onTabLikeChanged(0);
                    break;
                case R.id.tv_like_type1:
                    onTabLikeChanged(2);
                    break;
                case R.id.tv_like_type2:
                    onTabLikeChanged(3);
                    break;
                case R.id.tv_like_type3:
                    onTabLikeChanged(4);
                    break;
                case R.id.tv_like_type4:
                    onTabLikeChanged(5);
                    break;
                case R.id.tv_like_type5:
                    onTabLikeChanged(6);
                    break;
                case R.id.tv_tip_bt:
                case R.id.iv_empty_content:
                    ConstantUtil.createUmEvent("my_fragment_click_mine_publish");//我的页点击图片或者按钮发布
                    RxBus.getDefault().post(RxCodeConstant.PUBLISH_VIDEO_OR_IMG, 1);
                    break;
            }
        }
    };

    @Override
    protected void initData() {
        getVideoList();
    }

    private void getVideoList() {
        getVideoList(tabType);
    }

    private void getVideoList(int currentPos) {
        try {
            if (contentLayoutBinding != null && contentLayoutBinding.getRoot() != null) {
                contentLayoutBinding.getRoot().setVisibility(View.VISIBLE);
            }

            if (SPUtils.getInstance(Constant.SP_USER_FILE_NAME).getInt("user_status", 0) == 0 && userIdTag == 0 && tabType < 2) {
                if (contentLayoutBinding.recyclerView.isRefreshing()) {
                    contentLayoutBinding.recyclerView.setRefreshing(false);
                }
                //未登录状态
                pageNum = 1;
                homeVideoBeanList.clear();
                videoOrImageAdapter.setNewData(null);
                if (userIdTag == 0 && tabType == 0) {
                    showEmptyView(" 把逗笑你的内容发布上来吧~ 逗笑更多人", R.mipmap.empty_bg_no_fans, true, quickClickListener);
                } else {
                    showEmptyView("暂无数据", R.mipmap.empty_bg_normal, true);
                }
                contentLayoutBinding.recyclerView.setEmptyViewEnabled(true);
                contentLayoutBinding.recyclerView.setLoadMoreEnabled(false);
                return;
            }
            //他人喜欢页面，显示喜欢的内容不可见
            if (userIdTag != 0 && tabType != 0) {
                if (contentLayoutBinding.recyclerView != null) {
                    contentLayoutBinding.recyclerView.setRefreshing(false);
                }
                contentLayoutBinding.recyclerView.loadMoreEnd();
                isCanLoadMore = false;
                showEmptyView("喜欢的内容不可见", R.mipmap.empty_bg_normal);
                contentLayoutBinding.recyclerView.setEmptyViewEnabled(true);
                contentLayoutBinding.recyclerView.setLoadMoreEnabled(false);
                return;
            }
            LogUtils.d("MyWorksfrag--", currentPos + "");
            viewModel.getWorksLikeList(currentPos, pageNum, likeType, userIdTag, singleSize
            ).observe(this, resultBean -> {
                if (resultBean != null) {
                    if (resultBean.getCode() == 0) {
                        if (contentLayoutBinding.recyclerView != null) {
                            contentLayoutBinding.recyclerView.setRefreshing(false);
                        }
                        List<HomeVideoImageListBean.ListBean> videoList = resultBean.getData();
                        if (videoList != null && !videoList.isEmpty()) {
                            contentLayoutBinding.recyclerView.setLoadMoreEnabled(true);
                            contentLayoutBinding.recyclerView.setEmptyViewEnabled(false);
                            showContentView();
                            //因为可以下拉刷新，videoOrImageAdapter.clear()报错了，所以先这样
                            if (pageNum <= 1) {
                                homeVideoBeanList.clear();
                                videoOrImageAdapter.setNewData(videoList);
                            } else {
                                videoOrImageAdapter.addData(videoList);
                            }
                            homeVideoBeanList.addAll(videoList);
                            contentLayoutBinding.recyclerView.loadMoreComplete();
                            if (videoList.size() < singleSize && tabType != 2) {//目前个人喜欢页删除后有可能当前页面不够页数，但是下一页还有数据
                                isCanLoadMore = false;
                                contentLayoutBinding.recyclerView.loadMoreEnd();
                            }
                        } else {
                            contentLayoutBinding.recyclerView.loadMoreEnd();
                            if (pageNum == 1 && homeVideoBeanList.size() == 0) {
                                contentLayoutBinding.recyclerView.setLoadMoreEnabled(false);
                                if (userIdTag == 0 && tabType == 0) {
                                    showEmptyView(" 把逗笑你的内容发布上来吧~ 逗笑更多人", R.mipmap.empty_bg_no_fans, true, quickClickListener);
                                } else {
                                    showEmptyView("暂无数据", R.mipmap.empty_bg_normal, true);
                                }
                                contentLayoutBinding.recyclerView.setEmptyViewEnabled(true);
                            }
                            isCanLoadMore = false;
                        }
                    } else {
                        CommonUtils.showShort(resultBean.getMsg());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updataListInfoFollowStatus(int followStatus) {
        if (videoOrImageAdapter == null || homeVideoBeanList == null || homeVideoBeanList.size() < 1) {
            return;
        }
        if (postId != 0 && homeVideoBeanList.size() > 0 && userIdTag != 0) {
            for (int i = 0; i < homeVideoBeanList.size(); i++) {
                homeVideoBeanList.get(i).getContent().setFollowStatus(followStatus);
            }
        }
    }

    public void updataListInfo(DetailListBean detailListBean) {

        if (videoOrImageAdapter == null || detailListBean == null || detailListBean.getHomeVideoBeanList() == null || detailListBean.getHomeVideoBeanList().size() < 1) {
            return;
        }

        try {
            homeVideoBeanList.clear();
            homeVideoBeanList.addAll(detailListBean.getHomeVideoBeanList());
            pageNum = detailListBean.getPageNum();

            videoOrImageAdapter.clear();
            videoOrImageAdapter.addData(detailListBean.getHomeVideoBeanList());

            if (postId != 0 && homeVideoBeanList.size() > 0 && userIdTag != 0) {
                boolean isSend = false;
                for (int i = 0; i < homeVideoBeanList.size(); i++) {
                    if (postId == homeVideoBeanList.get(i).getContent().getPostId()) {
                        if (homeVideoBeanList.get(i).getContent().getPostType() == 1) {
                            RxBus.getDefault().post(RxCodeConstant.HOME_VIDEO_UPDATA_AUTHOR_INFO, homeVideoBeanList.get(i));
                        } else {
                            RxBus.getDefault().post(RxCodeConstant.HOME_IMG_UPDATA_AUTHOR_INFO, homeVideoBeanList.get(i));
                        }
                        return;
                    } else {
                        //这个时候更新是否关注作者
                        if (!isSend) {
                            isSend = true;
                            int followStatus = homeVideoBeanList.get(i).getContent().getFollowStatus();
                            String sendS = homeVideoBeanList.get(i).getContent().getAuthorId() + "," + followStatus;
                            RxBus.getDefault().post(RxCodeConstant.HOME_VIDEO_UPDATA_AUTHOR_FOLLOW, sendS);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshData() {
        if (videoOrImageAdapter != null) {
            homeVideoBeanList.clear();
            videoOrImageAdapter.getData().clear();
            videoOrImageAdapter.notifyDataSetChanged();
            pageNum = 1;
            getVideoList();
            RxBus.getDefault().post(RxCodeConstant.UPDATE_MYFRAGMENT_HEADER, 1);//刷新数据时更新一下我的页面头部信息
        }
    }

    public void removeData(int position) {
        if (videoOrImageAdapter != null && postId == 0 && userIdTag == 0) {
            if (!homeVideoBeanList.isEmpty() && homeVideoBeanList.size() > position) {
                homeVideoBeanList.remove(position);
                videoOrImageAdapter.removeData(position);
            }
        }
    }

    /**
     * 更新帖子的点赞或点踩状态或评论数
     */
    protected void updatePostLikeOrUnlikeOrReview(HomeVideoImageListBean.ListBean.ContentBean videoOrImageContent) {
        for (int i = 0; i < videoOrImageAdapter.getData().size(); i++) {
            HomeVideoImageListBean.ListBean listBean = videoOrImageAdapter.getItemData(i);
            if (listBean != null && listBean.getContent() != null && videoOrImageContent != null && listBean.getContent().getPostId() == videoOrImageContent.getPostId()) {
                listBean.getContent().setReplyCount(videoOrImageContent.getReplyCount());
                videoOrImageAdapter.notifyItemChanged(i + 1);
                break;
            }
        }
    }
}