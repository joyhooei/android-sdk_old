package org.weilan;
import android.app.Application;
import com.haima.loginplugin.bdpush.HMPushApplication;

public class sdk_app extends HMPushApplication {

    static {
        System.loadLibrary("game");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
