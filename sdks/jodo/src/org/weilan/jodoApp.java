package org.weilan;

import android.app.Application;
import com.jodo.paysdk.JodoPlaySDKManager;

public class jodoApp extends Application {

    static {
        System.loadLibrary("game");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        JodoPlaySDKManager.appInit( this );
    }
}
