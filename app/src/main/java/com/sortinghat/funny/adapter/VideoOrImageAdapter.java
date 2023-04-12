package com.sortinghat.funny.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.sortinghat.common.adapter.BaseBindingAdapter;
import com.sortinghat.common.adapter.BaseBindingHolder;
import com.sortinghat.common.utils.CommonUtils;
import com.sortinghat.common.utils.QuickClickListener;
import com.sortinghat.common.utils.image.GlideUtils;
import com.sortinghat.funny.R;
import com.sortinghat.funny.databinding.ItemVideoOrImageBinding;
import com.sortinghat.funny.thirdparty.album.AlbumFile;
import com.sortinghat.funny.thirdparty.album.util.AlbumUtils;
import com.sortinghat.funny.ui.publish.PublishPostActivity;

/**
 * Created by wzy on 2021/6/28
 */
public class VideoOrImageAdapter extends BaseBindingAdapter<AlbumFile, ItemVideoOrImageBinding> {

    public VideoOrImageAdapter() {
        super(R.layout.item_video_or_image);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void bindView(BaseBindingHolder holder, AlbumFile albumFile, ItemVideoOrImageBinding binding, int position) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.ivImage.getLayoutParams();
        int imageSize = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(20)) / 4;
        params.width = imageSize;
        params.height = imageSize;
        binding.ivImage.setLayoutParams(params);

        setListener(binding, albumFile);

        if (albumFile != null) {
            if (albumFile.getMediaType() == AlbumFile.TYPE_IMAGE) {
                GlideUtils.loadImageWithPath(albumFile.getPath(), binding.ivImage);
                binding.tvVideoDuration.setVisibility(View.GONE);
            } else if (albumFile.getMediaType() == AlbumFile.TYPE_VIDEO) {
                GlideUtils.loadVideoCoverImageWithUri(albumFile.getPath(), binding.ivImage);
                binding.tvVideoDuration.setText(AlbumUtils.convertDuration(albumFile.getDuration()));
                binding.tvVideoDuration.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setListener(ItemVideoOrImageBinding binding, AlbumFile albumFile) {
        binding.itemView.setTag(albumFile);
        binding.itemView.setOnClickListener(listener);
    }

    private QuickClickListener listener = new QuickClickListener() {
        @Override
        protected void onNoDoubleClick(View v) {
            AlbumFile albumFile = (AlbumFile) v.getTag();
            if (albumFile != null) {
                int mediaType = albumFile.getMediaType();
                if (mediaType == AlbumFile.TYPE_VIDEO) {
                    if (albumFile.getSize() > 300 * 1024 * 1024) {
                        CommonUtils.showShort("视频不能超过300MB");
                        return;
                    }
                } else if (mediaType == AlbumFile.TYPE_IMAGE) {
                    if (albumFile.getSize() > 35 * 1024 * 1024) {
                        CommonUtils.showShort("图片不能超过35MB");
                        return;
                    }
                }
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable("ALBUM_FILE", albumFile);
            ActivityUtils.startActivity(bundle, PublishPostActivity.class);
        }
    };
}
