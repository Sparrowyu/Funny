package com.sortinghat.funny.adapter;

import android.annotation.SuppressLint;

import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.databinding.ItemAlbumFolderBinding;
import com.sortinghat.funny.thirdparty.album.AlbumFile;
import com.sortinghat.funny.thirdparty.album.AlbumFolder;

import java.util.ArrayList;

/**
 * Created by wzy on 2021/6/28
 */
public class AlbumFolderAdapter extends BaseBindingAdapter<AlbumFolder, ItemAlbumFolderBinding> {

    public AlbumFolderAdapter() {
        super(R.layout.item_album_folder);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindView(BaseBindingHolder holder, AlbumFolder albumFolder, ItemAlbumFolderBinding binding, int position) {
        if (albumFolder != null) {
            ArrayList<AlbumFile> albumFileList = albumFolder.getAlbumFiles();
            if (albumFileList != null && !albumFileList.isEmpty()) {
                GlideUtils.loadImageWithPathAndCenterCrop(albumFileList.get(0).getPath(), binding.ivAlbumCoverImage);
            }
            binding.tvAlbumName.setText(albumFolder.getName() + " (" + albumFileList.size() + ")");
        }
    }
}
