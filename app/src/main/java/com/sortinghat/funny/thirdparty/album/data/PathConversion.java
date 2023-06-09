package com.sortinghat.funny.thirdparty.album.data;

import android.media.MediaPlayer;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.sortinghat.funny.thirdparty.album.AlbumFile;
import com.sortinghat.funny.thirdparty.album.Filter;
import com.sortinghat.funny.thirdparty.album.util.AlbumUtils;

import java.io.File;

/**
 * Created by YanZhenjie on 2017/10/18.
 */
public class PathConversion {

    private Filter<Long> mSizeFilter;
    private Filter<String> mMimeFilter;
    private Filter<Long> mDurationFilter;

    public PathConversion(Filter<Long> sizeFilter, Filter<String> mimeFilter, Filter<Long> durationFilter) {
        this.mSizeFilter = sizeFilter;
        this.mMimeFilter = mimeFilter;
        this.mDurationFilter = durationFilter;
    }

    @WorkerThread
    @NonNull
    public AlbumFile convert(String filePath) {
        File file = new File(filePath);

        AlbumFile albumFile = new AlbumFile();
        albumFile.setPath(filePath);

        File parentFile = file.getParentFile();
        albumFile.setBucketName(parentFile.getName());

        String mimeType = AlbumUtils.getMimeType(filePath);
        albumFile.setMimeType(mimeType);
        long nowTime = System.currentTimeMillis();
        albumFile.setAddDate(nowTime);
        albumFile.setSize(file.length());
        int mediaType = 0;
        if (!TextUtils.isEmpty(mimeType)) {
            if (mimeType.contains("video")) {
                mediaType = AlbumFile.TYPE_VIDEO;
            }
            if (mimeType.contains("image")) {
                mediaType = AlbumFile.TYPE_IMAGE;
            }
        }
        albumFile.setMediaType(mediaType);

        if (mSizeFilter != null && mSizeFilter.filter(file.length())) {
            albumFile.setDisable(true);
        }
        if (mMimeFilter != null && mMimeFilter.filter(mimeType)) {
            albumFile.setDisable(true);
        }

        if (mediaType == AlbumFile.TYPE_VIDEO) {
            MediaPlayer player = new MediaPlayer();
            try {
                player.setDataSource(filePath);
                player.prepare();
                albumFile.setDuration(player.getDuration());
            } catch (Exception ignored) {
            } finally {
                player.release();
            }

            if (mDurationFilter != null && mDurationFilter.filter(albumFile.getDuration())) {
                albumFile.setDisable(true);
            }
        }
        return albumFile;
    }

}