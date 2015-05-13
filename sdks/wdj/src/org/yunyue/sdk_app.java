package org.yunyue;

import android.content.Context;
import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi;

/**
 * @author liuxv@wandoujia.com
 */
public class sdk_app extends app {

    private static final long APP_KEY = ${APPKEY};
    private static final String SECURITY_KEY = "${SECURITY_KEY}";

    private static WandouGamesApi wandouGamesApi;

    public static WandouGamesApi getWandouGamesApi() {
        return wandouGamesApi;
    }


    @Override
    protected void attachBaseContext(Context base) {
        WandouGamesApi.initPlugin(base, APP_KEY, SECURITY_KEY);
        super.attachBaseContext(base);
    }


    @Override
    public void onCreate() {
        wandouGamesApi = new WandouGamesApi.Builder(this, APP_KEY, SECURITY_KEY).create();
        wandouGamesApi.setLogEnabled(false);
        super.onCreate();
    }
}
