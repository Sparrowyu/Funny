package com.sortinghat.funny.ui.publish;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.base.BaseFragment;
import com.sortinghat.common.rxbus.RxBus;
import com.sortinghat.common.view.GridSpacingItemDecoration;
import com.sortinghat.funny.R;
import com.sortinghat.funny.adapter.VideoOrImageAdapter;
import com.sortinghat.funny.constant.RxCodeConstant;
import com.sortinghat.funny.databinding.FragmentVideoOrImageBinding;
import com.sortinghat.funny.thirdparty.album.Album;
import com.sortinghat.funny.thirdparty.album.AlbumFile;
import com.sortinghat.funny.thirdparty.album.AlbumFolder;
import com.sortinghat.funny.thirdparty.album.data.MediaReadTask;
import com.sortinghat.funny.thirdparty.album.data.MediaReader;
import com.sortinghat.funny.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzy on 2021/6/24
 */
public class VideoFragment extends BaseFragment<HomeViewModel, FragmentVideoOrImageBinding> implements MediaReadTask.Callback {

    private VideoOrImageAdapter videoOrImageAdapter;

    private MediaReadTask mMediaReadTask;

    private List<AlbumFolder> albumFolderList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_or_image;
    }

    @Override
    protected void initViews() {
        subscibeRxBus();
        initAdapter();
    }

    private void initAdapter() {
        contentLayoutBinding.recyclerView.setLayoutManager(new GridLayoutManager(activity, 4));
        contentLayoutBinding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, SizeUtils.dp2px(4), false));
        videoOrImageAdapter = new VideoOrImageAdapter();
        contentLayoutBinding.recyclerView.setAdapter(videoOrImageAdapter);
    }

    @Override
    protected void initData() {
        scanMediaData();
    }

    private void scanMediaData() {
        MediaReader mediaReader = new MediaReader(activity, null, attributes -> {
            if ("video/flv".equals(attributes)) {
                return true;
            }
            return false;
        }, null, false);
        mMediaReadTask = new MediaReadTask(Album.FUNCTION_CHOICE_VIDEO, null, mediaReader, this);
        mMediaReadTask.execute();
    }

    @Override
    public void onScanCallback(ArrayList<AlbumFolder> albumFolders, ArrayList<AlbumFile> checkedFiles) {
        mMediaReadTask = null;
        if (albumFolders != null && !albumFolders.isEmpty()) {
            albumFolderList = albumFolders;
            videoOrImageAdapter.setNewData(albumFolders.get(0).getAlbumFiles());
        } else {
            videoOrImageAdapter.setNewData(null);
        }
        contentLayoutBinding.getRoot().setVisibility(View.VISIBLE);
    }

    private void subscibeRxBus() {
        addSubscription(RxBus.getDefault().toObservable(RxCodeConstant.UPDATE_VIDEO_IMAGE_LIST, AlbumFolder.class)
                .subscribe(albumFolder -> {
                    if (albumFolderList != null && !albumFolderList.isEmpty()) {
                        if (getString(R.string.album_all_videos_images).equals(albumFolder.getName())) {
                            videoOrImageAdapter.setNewData(albumFolderList.get(0).getAlbumFiles());
                        } else {
                            int position = albumFolderList.indexOf(albumFolder);
                            if (position != -1) {
                                videoOrImageAdapter.setNewData(albumFolderList.get(position).getAlbumFiles());
                            } else {
                                videoOrImageAdapter.setNewData(null);
                            }
                        }
                    }
                }));
    }

    @Override
    public void onDestroyView() {
        if (mMediaReadTask != null) {
            mMediaReadTask.cancel(true);
        }
        super.onDestroyView();
    }
}
