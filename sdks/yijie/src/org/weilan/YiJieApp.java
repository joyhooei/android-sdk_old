package org.weilan;
import android.app.Application;

import com.snowfish.cn.ganga.helper.SFOnlineApplication;

public class YiJieApp extends SFOnlineApplication {

    static {
        System.loadLibrary("game");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
