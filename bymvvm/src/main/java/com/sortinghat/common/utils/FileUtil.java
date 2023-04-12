package com.sortinghat.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;

/**
 * Created by wzy on 2020/4/20
 */
public class FileUtil {

    /**
     * 把文件存到sd卡
     */
    public static boolean writeFileToDisk(String savePath, ResponseBody body) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
//        File file = new File(savePath);//先随便命名，等下载成功后在改名
        File file = new File(savePath + ".download");//先随便命名，等下载成功后在改名
        try {
            byte[] fileReader = new byte[4096];
            inputStream = body.byteStream();
            outputStream = new FileOutputStream(file);
            while (true) {
                int read = inputStream.read(fileReader);
                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
            }
            outputStream.flush();
            renameFile(file, savePath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean writeFileToDisk(String savePath, InputStream inputStream) {
        FileOutputStream outputStream = null;
        File file = new File(savePath);
        byte[] buffer = new byte[4096];
        int len;
        try {
            outputStream = new FileOutputStream(file);
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 重命名文件
     *
     * @param oldFile
     * @return
     */
    public static void renameFile(File oldFile, String newFileName) {
        File newFile = new File(newFileName);
        if (oldFile.exists() && oldFile.isFile()) {
            oldFile.renameTo(newFile);
        }
    }
}