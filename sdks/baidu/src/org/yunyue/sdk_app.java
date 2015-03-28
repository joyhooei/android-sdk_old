package org.yunyue;
import com.baidu.gamesdk.BDGameApplication;

public class sdk_app extends BDGameApplication{

    static {
        System.loadLibrary("game");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
