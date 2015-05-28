package com.paojiao.imageLoad;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class FileRepository {
    private Context mContext;
    private File mRootDir;

    public FileRepository(Context context, String dirName) {
        mContext = context;
        init(dirName);
    }

    private void init(String dirName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mRootDir = mContext.getExternalFilesDir(dirName);
            if (mRootDir == null) {
                mRootDir = mContext.getDir(dirName, Context.MODE_PRIVATE);
                return;
            }
            if (!mRootDir.exists() || !mRootDir.isDirectory()) {
                mRootDir.mkdir();
            }
        }
        else {
            mRootDir = mContext.getDir(dirName, Context.MODE_PRIVATE);
        }
    }

    public File getRootDir() {
        return mRootDir;
    }

    public File getChildDir(String dirName) {
        File dir = new File(mRootDir, dirName);
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }
        return dir;

    }

}
