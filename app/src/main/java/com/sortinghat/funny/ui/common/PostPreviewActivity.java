package com.sortinghat.funny.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.PostPreviewAdapter;
import com.sortinghat.funny.bean.DetailListBean;
import com.sortinghat.funny.bean.HomeVideoImageListBean;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivityPostPreviewBinding;
import com.sortinghat.funny.databinding.ItemPostPreviewBinding;
import com.sortinghat.funny.util.ConstantUtil;
import com.sortinghat.funny.viewmodel.HomeViewModel;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzy on 2021/7/20
 */
public class PostPreviewActivity extends BaseActivity<HomeViewModel, ActivityPostPreviewBinding> {

    private PostPreviewAdapter postPreviewAdapter;

    public static String direction = "appuse";
    public static boolean isCurrentQuit = true; //是否是当前页面退出
    public static boolean isPlayed;

    private List<HomeVideoImageListBean.ListBean> homeVideoBeanList = new ArrayList<>();
    private int homeposition;
    private String from;
    private int fromPage = 0;   //0:我的 1：其他人页面 2：消息通知页面 3：话题页面
    private int tabType = 0;    //0:我的发布 1:我的评论 2：我的喜欢
    private int likeType = 0;   //0:全部 1:以后看 2：触动内心
    private long userIdTag = 0; //0：我页面过来的 其他:别的用户详情页来

    private int pageNum = 1;
    private int singleSize = 24;

    private int playErrorPosition = -1;
    private int viewPagerScrollState;

    private boolean isCanLoadMore;

    private final int SHARE_ANIMATION_SHOW = 101;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == SHARE_ANIMATION_SHOW) {
                RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(contentLayoutBinding.viewPager.getCurrentItem());
                if (viewHolder != null) {
                    BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding>) viewHolder;
                    if (bindingViewHolder.binding != null) {
                        bindingViewHolder.binding.controller.showShareAnimation(true);
                    }
                }
            }
        }
    };

    @Override
    protected int getLayoutId() {
        titleBarBinding.getRoot().setVisibility(View.GONE);
        return R.layout.activity_post_preview;
    }

    @Override
    protected void initViews() {
        isPlayed = false;
        initGetBundle();
        ConstantUtil.setNavigationBarColor(this);
        setStatusBar(R.color.black, false);
        initViewPagerAdapter();
        subscibeRxBus();
    }

    private void initGetBundle() {
        GSYVideoManager.releaseAllVideos();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                from = bundle.getString("from", "");
                fromPage = bundle.getInt("fromPage");
                DetailListBean bean = (DetailListBean) bundle.getSerializable("dataList");
                homeVideoBeanList = bean.getHomeVideoBeanList();
                homeposition = bundle.getInt("position");

                tabType = bundle.getInt("tabType");
                likeType = bundle.getInt("likeType");
                pageNum = bundle.getInt("pageNum");
                userIdTag = bundle.getLong("userIdTag");
                isCanLoadMore = bundle.getBoolean("isCanLoadMore");
            }
        }
    }

    private void initViewPagerAdapter() {
        postPreviewAdapter = new PostPreviewAdapter(this, getSupportFragmentManager(), mHandler, fromPage, from, tabType);
        contentLayoutBinding.viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        contentLayoutBinding.viewPager.setAdapter(postPreviewAdapter);
        contentLayoutBinding.viewPager.setOffscreenPageLimit(3); //预加载3次
    }

    private QuickClickListener quickClickListener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            if (v.getId() == R.id.iv_video_back) {
                finish();
            }
        }
    };

    @Override
    protected void setListener() {
        contentLayoutBinding.ivVideoBack.setOnClickListener(quickClickListener);

        contentLayoutBinding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                viewPagerScrollState = state;
                RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(contentLayoutBinding.viewPager.getCurrentItem());
                if (viewHolder != null) {
                    BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding>) viewHolder;
                    if (bindingViewHolder.binding != null) {
                        bindingViewHolder.binding.controller.setScrollAlpha(state);
                    }
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (viewPagerScrollState == ViewPager2.SCROLL_STATE_DRAGGING && position == postPreviewAdapter.getData().size() - 1 && !isCanLoadMore) {
                    CommonUtils.showShort("没有更多内容了");
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int playPosition = GSYVideoManager.instance().getPlayPosition();
                playInfoNormal(playPosition);
                if (position > playPosition) {
                    direction = "up";
                } else if (position < playPosition) {
                    direction = "down";
                }
                playErrorPosition = position;
                mHandler.removeMessages(SHARE_ANIMATION_SHOW);

                if ((position != playPosition)) {
                    mHandler.postDelayed(() -> playPosition(position), 280);
                }

                //索引大于已加载出来的列表数量,才可以加载方法
                if ((position + 1) >= homeVideoBeanList.size() && isCanLoadMore) {
                    pageNum++;
                    getVideoList();
                }
            }
        });
    }

    @Override
    protected void initData() {
        postPreviewAdapter.setViewModel(viewModel);
        try {
            postPreviewAdapter.addData(homeVideoBeanList);
            ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).scrollToPosition(homeposition);

            mHandler.postDelayed(() -> {
                if (homeVideoBeanList.size() > 0) {
                    playPosition(homeposition);
                }
                if (singleSize != 0 && ((homeposition + 1) % singleSize) == (homeVideoBeanList.size() % singleSize)) {
                    //索引大于已加载出来的列表数量,才可以加载方法
                    if ((homeposition + 1) >= homeVideoBeanList.size() && isCanLoadMore) {
                        pageNum++;
                        getVideoList();
                    }
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getVideoList() {
        if (fromPage != 0 && fromPage != 1) {
            return;
        }
        viewModel.getWorksLikeList(tabType, pageNum, likeType, userIdTag, singleSize).observe(this, resultBean -> {
            if (resultBean != null) {
                if (resultBean.getCode() == 0) {
                    List<HomeVideoImageListBean.ListBean> videoList = resultBean.getData();
                    if (videoList != null && !videoList.isEmpty()) {
                        showContentView();
                        homeVideoBeanList.addAll(videoList);
                        postPreviewAdapter.addData(videoList);

                        if (videoList.size() < singleSize) {
                            isCanLoadMore = false;
                        }
                    } else {
                        isCanLoadMore = false;
                        CommonUtils.showShort("没有更多内容了");
                    }
                } else {
                    LogUtils.e(resultBean.getMsg());
                }
            }
        });
    }

    //上个视频界面回到初始状态
    private void playInfoNormal(int playPosition) {
        RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(playPosition);
        if (viewHolder != null) {
            BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding>) viewHolder;
            if (bindingViewHolder.binding != null && homeVideoBeanList.get(playPosition).getContent().getPostType() == 1) {
                bindingViewHolder.binding.rlHomeLikeDialog.setVisibility(View.GONE);
                bindingViewHolder.binding.rlHomeDislikeDialog.setVisibility(View.GONE);
                bindingViewHolder.binding.controller.showControllerInfoToNorman(false);
                bindingViewHolder.binding.likeview.setShow(true);
                bindingViewHolder.binding.gsyVideoPlayer.showStartProgress(false);
                bindingViewHolder.binding.controller.setScrollAlpha(0);
                bindingViewHolder.binding.controller.showShareAnimation(false);
                bindingViewHolder.binding.gsyVideoPlayer.onVideoPause();
            }
        }
    }

    private void playPosition(int position) {
        RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(position);
        if (viewHolder != null) {
            BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding>) viewHolder;
            if (bindingViewHolder.binding != null && position < homeVideoBeanList.size() && homeVideoBeanList.get(position).getContent().getPostType() == 1) {
                isPlayed = true;
                bindingViewHolder.binding.startPlay.setVisibility(View.GONE);
                bindingViewHolder.binding.gsyVideoPlayer.setPlayPosition(position);
                bindingViewHolder.binding.gsyVideoPlayer.startPlayLogic();
                bindingViewHolder.binding.likeview.setShow(true);
                bindingViewHolder.binding.gsyVideoPlayer.showStartProgress(false);

                if (mHandler!=null && homeVideoBeanList.get(position).getContent().getApplyStatus() == 1) {
                    mHandler.sendEmptyMessageDelayed(SHARE_ANIMATION_SHOW, 20000);
                }
            } else {
                if (mHandler !=null && homeVideoBeanList.get(position).getContent().getApplyStatus() == 1) {
                    mHandler.sendEmptyMessageDelayed(SHARE_ANIMATION_SHOW, 10000);
                }
                GSYVideoManager.onPause();
            }
        }
    }

    public static void starActivity(Context mContext, int fromPage, String messageType, List<HomeVideoImageListBean.ListBean> homeVideoBeanList, int position, int tabType, int pageNum, int likeType, long userIdTag, boolean isCanLoadMore) {
        Intent intent = new Intent(mContext, PostPreviewActivity.class);

        DetailListBean bean = new DetailListBean();
        bean.setHomeVideoBeanList(homeVideoBeanList);
        bean.setPageNum(pageNum);
        Bundle bundle = new Bundle();
        bundle.putString("from", messageType);
        bundle.putSerializable("dataList", bean);
        bundle.putInt("fromPage", fromPage);//0:我的 1：其他人页面 2：消息通知页面 3：话题页面
        bundle.putInt("position", position);
        bundle.putInt("tabType", tabType);
        bundle.putInt("pageNum", pageNum);
        bundle.putInt("likeType", likeType);
        bundle.putLong("userIdTag", userIdTag);
        bundle.putBoolean("isCanLoadMore", isCanLoadMore);
        intent.putExtras(bundle);

        ActivityUtils.startActivity(intent);
    }

    //视频sdk会出现错误时
    public void onVideoErrorPlay() {
        if (playErrorPosition >= 0 & isCurrentQuit) {
            playPosition(playErrorPosition);
        }
    }
    private int isFirst = 0;

    @Override
    public void onResume() {
        super.onResume();
        if (isFirst > 0) {
            if (GSYVideoManager.instance().listener() != null) {
                GSYVideoManager.onResume(false);
            } else {
                //修复退出后台再回来可能出现视频丢失的bug
                if (playErrorPosition >= 0) {
                    onVideoErrorPlay();
                    LogUtils.e("gsyPlayer", "-error:" + playErrorPosition);
                }
            }
        }else {
            isFirst = 1;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        GSYVideoManager.onPause();
    }

    private void subscibeRxBus() {
        //更新帖子的点赞或点踩状态或评论数
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.UPDATE_POST_LIKE_OR_UNLIKE_OR_REVIEW, HomeVideoImageListBean.ListBean.ContentBean.class)
                .subscribe(videoOrImageContent -> {
                    for (int i = 0; i < postPreviewAdapter.getData().size(); i++) {
                        HomeVideoImageListBean.ListBean listBean = postPreviewAdapter.getItemData(i);
                        if (listBean != null && listBean.getContent() != null && videoOrImageContent != null && listBean.getContent().getPostId() == videoOrImageContent.getPostId()) {
                            listBean.getContent().setReplyCount(videoOrImageContent.getReplyCount());
                            RecyclerView.ViewHolder viewHolder = ((RecyclerView) contentLayoutBinding.viewPager.getChildAt(0)).findViewHolderForAdapterPosition(i);
                            if (viewHolder != null) {
                                BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding> bindingViewHolder = (BaseBindingHolder<HomeVideoImageListBean.ListBean, ItemPostPreviewBinding>) viewHolder;
                                if (bindingViewHolder.binding != null) {
                                    bindingViewHolder.binding.controller.setLikeOrUnlikeOrReview(listBean.getContent());
                                }
                            }
                        }
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        //点关注后,更新整个列表的id
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.MY_UPDATA_AUTHOR_INFO, HomeVideoImageListBean.ListBean.class)
                .subscribe(videoInfo -> {
                    for (int i = 0; i < homeVideoBeanList.size(); i++) {
                        if (homeVideoBeanList.get(i).getContent().getAuthorId() == videoInfo.getContent().getAuthorId()) {
                            homeVideoBeanList.get(i).getContent().setFollowStatus(videoInfo.getContent().getFollowStatus());
                        }
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));

        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.MY_UPDATA_LIST_INFO_DELETE, Integer.class)
                .subscribe(integer -> {
                    if (!homeVideoBeanList.isEmpty() && homeVideoBeanList.size() > integer) {
                        playInfoNormal(integer);
                        homeVideoBeanList.remove((int) integer);
                        if (!homeVideoBeanList.isEmpty() && homeVideoBeanList.size() > integer && homeVideoBeanList.get(integer).getContent().getPostType() == 1) {
                            ThreadUtils.runOnUiThreadDelayed(() -> playPosition(integer), 1000);
                        }
                    }
                }, throwable -> LogUtils.e(Log.getStackTraceString(throwable))));


        //滑到下一个
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.MY_VIDEO_UPDATE_DISLIKE, Integer.class)
                .subscribe(position -> {
                    if (position >= 0 && postPreviewAdapter.getData().size() > 0 && position + 1 < postPreviewAdapter.getData().size()) {
                        contentLayoutBinding.viewPager.setCurrentItem(position + 1);
                    }
                }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        //只有列表页进来需要发，其余比如消息页面不需要发送更新数据
        if ((fromPage == 0 || fromPage == 1)) {
            DetailListBean detailListBean = new DetailListBean();
            detailListBean.setHomeVideoBeanList(homeVideoBeanList);
            detailListBean.setPageNum(pageNum);
            if (fromPage == 0) {
                RxBus.getDefault().post(RxCodeConstant.MY_UPDATA_LIST_INFO, detailListBean);
            } else if (fromPage == 1) {
                RxBus.getDefault().post(RxCodeConstant.MY_OTHER_UPDATA_LIST_INFO, detailListBean);
            }
        }
        super.onDestroy();
        GSYVideoManager.releaseAllVideos();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }
}
