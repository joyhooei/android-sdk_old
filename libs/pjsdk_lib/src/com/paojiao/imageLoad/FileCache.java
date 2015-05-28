package com.paojiao.imageLoad;

import java.io.File;

import android.content.Context;

public class FileCache {
    private static final String Cache = "cache";
    private File mCacheDir;
    private FileRepository mFileRepository;

    public FileCache(Context context) {
        mFileRepository = new FileRepository(context, Cache);
        mCacheDir = mFileRepository.getChildDir(Cache);
    }

    public File getFile(String url) {
        // I identify images by hashcode. Not a perfect solution, good for the
        // demo.
        String filename = String.valueOf(url.hashCode());
        // Another possible solution (thanks to grantland)
        // String filename = URLEncoder.encode(url);
        File f = new File(mCacheDir, filename);
        return f;

    }

    public void clearFiles() {
        FileUtil.delAllFile(mCacheDir);
    }

    public void clearFilesWithFloder() {
        FileUtil.delAllFileWithFloder(mCacheDir);
    }

}