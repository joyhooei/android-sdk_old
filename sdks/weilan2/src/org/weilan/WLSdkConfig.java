package org.weilan;

import android.content.Context;

import com.zz.sdk.SDKManager;

public class WLSdkConfig {
    private static final String APP_KEY    = "${APP_KEY}";
    private static final String APP_SECRET = "${APP_SECRET}";

    private static final String WEICHATPAY = "";

    public static void initParam(Context ctx)
    {
        // 配置 APP_KEY，必须在API调用之前设置
        SDKManager.setAppKey(APP_KEY);

        // -KEY_SUPPORT_WXPAY:
        // 配置微信支付的参数，也可以由运营来配置
        if( WEICHATPAY.length() > 0 )
            SDKManager.setWeichatPay(ctx, WEICHATPAY);
        // +KEY_SUPPORT_WXPAY:

        // 使用 assets 下的配置，仅用于简化多渠道打包，不是必须调用的。
        SDKManager.resetFromAssets(ctx);
    }

    public static void setAutoGetUserInfo(boolean autoGet) {
        SDKManager.setAppSecretKey(autoGet ? APP_SECRET : null);
    }
}
