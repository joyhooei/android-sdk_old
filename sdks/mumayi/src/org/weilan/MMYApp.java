package org.weilan;
import android.app.Application;

import com.mumayi.paymentmain.ui.MMYApplication;

public class MMYApp extends MMYApplication {

    static {
        System.loadLibrary("game");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
