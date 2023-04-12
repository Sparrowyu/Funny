package com.sortinghat.funny.ui.publish;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.adapter.FragmentPagerAdapter;
import com.sortinghat.common.base.BaseActivity;
import com.sortinghat.common.base.NoViewModel;
import com.sortinghat.common.databinding.ActivityBaseBinding;
import com.sortinghat.common.databinding.TitleBarWithComboBoxBinding;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.AlbumFolderAdapter;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.ActivitySelectVideoOrImageBinding;
import com.sortinghat.funny.databinding.LayoutPopupListViewBinding;
import com.sortinghat.funny.thirdparty.album.Album;
import com.sortinghat.funny.thirdparty.album.AlbumFile;
import com.sortinghat.funny.thirdparty.album.AlbumFolder;
import com.sortinghat.funny.thirdparty.album.data.MediaReadTask;
import com.sortinghat.funny.thirdparty.album.data.MediaReader;
import com.sortinghat.funny.view.CustomPopWindow;

import java.util.ArrayList;

/**
 * 发布-选择视频或图片页面
 * Created by wzy on 2021/6/24
 */
public class SelectVideoOrImageActivity extends BaseActivity<NoViewModel, ActivitySelectVideoOrImageBinding> implements MediaReadTask.Callback {

    private TitleBarWithComboBoxBinding titleBarBinding;
    private LayoutPopupListViewBinding popupListViewBinding;

    private AlbumFolderAdapter albumFolderAdapter;

    private CustomPopWindow listPopWindow;

    private ArrayList<String> titleList = new ArrayList<>(2);
    private ArrayList<Fragment> fragmentList = new ArrayList<>(2);

    private MediaReadTask mMediaReadTask;

    @Override
    protected void addTitleBarView(ActivityBaseBinding mBaseBinding) {
        titleBarBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.title_bar_with_combo_box, null, false);
        titleBarBinding.getRoot().setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(48)));
        RelativeLayout titleBarRootView = mBaseBinding.getRoot().findViewById(R.id.title_bar_root_view);
        titleBarRootView.addView(titleBarBinding.getRoot());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_video_or_image;
    }

    @Override
    protected void initViews() {
        initTitleBarView();
        initPopupWindowView();
        subscibeRxBus();
        initFragmentList();
        initViewPagerAdapter();
    }

    private void initTitleBarView() {
        titleBarBinding.ivBack.setOnClickListener(v -> finish());
        titleBarBinding.tvTitle.setText(R.string.album_all_videos_images);
        titleBarBinding.tvTitle.setOnClickListener(v -> showPopListView());
    }

    private void initPopupWindowView() {
        popupListViewBinding = DataBindingUtil.bind(LayoutInflater.from(this).inflate(R.layout.layout_popup_list_view, null));
        popupListViewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        albumFolderAdapter = new AlbumFolderAdapter();
        popupListViewBinding.recyclerView.setAdapter(albumFolderAdapter);

        listPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(popupListViewBinding.getRoot())
                .size(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenHeight() - BarUtils.getStatusBarHeight() - SizeUtils.dp2px(250))
                .setOnDissmissListener(() -> titleBarBinding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.title_down_arrow, 0))
                .create();
    }

    private void initFragmentList() {
        titleList.add("视频");
        titleList.add("图片");
        fragmentList.add(new VideoFragment());
        fragmentList.add(new ImageFragment());
    }

    private void initViewPagerAdapter() {
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titleList);
        contentLayoutBinding.viewPager.setAdapter(pagerAdapter);
        contentLayoutBinding.tabLayout.setupWithViewPager(contentLayoutBinding.viewPager);
    }

    @Override
    protected void setListener() {
        popupListViewBinding.recyclerView.setOnItemClickListener((v, position) -> {
            AlbumFolder albumFolder = albumFolderAdapter.getItemData(position);
            titleBarBinding.tvTitle.setText(albumFolder.getName());
            listPopWindow.dissmiss();
            RxBus.getDefault().post(RxCodeConstant.UPDATE_VIDEO_IMAGE_LIST, albumFolder);
        });
    }

    @Override
    protected void initData() {
        scanMediaData();
    }

    private void scanMediaData() {
        MediaReader mediaReader = new MediaReader(this, null, attributes -> {
            if ("video/flv".equals(attributes)) {
                return true;
            }
            return false;
        }, null, false);
        mMediaReadTask = new MediaReadTask(Album.FUNCTION_CHOICE_ALBUM, null, mediaReader, this);
        mMediaReadTask.execute();
    }

    @Override
    public void onScanCallback(ArrayList<AlbumFolder> albumFolders, ArrayList<AlbumFile> checkedFiles) {
        mMediaReadTask = null;
        albumFolderAdapter.setNewData(albumFolders);
    }

    private void showPopListView() {
        if (listPopWindow.getPopupWindow() != null && !listPopWindow.getPopupWindow().isShowing()) {
            titleBarBinding.tvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.title_up_arrow, 0);
        }
        listPopWindow.showAsDropDown(titleBarBinding.getRoot());
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.PUBLISH_POST, AlbumFile.class)
                .subscribe(albumFile -> finish(), throwable -> LogUtils.e(Log.getStackTraceString(throwable))));
    }

    @Override
    public void onBackPressed() {
        if (mMediaReadTask != null) {
            mMediaReadTask.cancel(true);
        }
        super.onBackPressed();
    }
}
