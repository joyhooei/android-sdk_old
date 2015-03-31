package org.yunyue;

import android.content.Context;
import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi;

/**
 * @author liuxv@wandoujia.com
 */
public class sdk_app extends app {

    private static final long APP_KEY = 100024673;
    private static final String SECURITY_KEY = "20734a7a12cecdd660aba9665a083cb6";

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
        wandouGamesApi.setLogEnabled(true);
        super.onCreate();
    }
}
