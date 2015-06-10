package org.yunyue;

import android.content.Context;
import com.kugou.game.sdk.api.common.ActivityOrientation;
import com.kugou.game.sdk.api.common.OnPlatformEventListener;
import com.kugou.game.sdk.api.online.KGPlatform;
import com.kugou.game.sdk.api.online.OnlineConfig;

public class sdk_app extends app {
    @Override
    public void onCreate() {
        super.onCreate();
        OnlineConfig sdkConfig = new OnlineConfig();

        /** --------填写SDK的必选配置项，参数来自酷狗提供的配置文档------------- */
        // 对应配置文档参数--MerchantId
        sdkConfig.setMerchantId(${MERCHANTID});
        // 对应配置文档参数--AppId
        sdkConfig.setAppId(${APPID});
        // 对应配置文档参数--AppKey
        sdkConfig.setAppKey("${APPKEY}");
        // 对应配置文档参数--GameId
        sdkConfig.setGameId(${GAMEID});
        // 对应配置文档参数--code ( 注意！！code内容里不要有换行)
        sdkConfig.setCode("${CODE}");

        /** --------设置SDK的可选配置项，具体可选项定义参看使用文档------------- */
        // 设置SDK界面的横竖屏
        sdkConfig.setActivityOrientation(ActivityOrientation.PORTRAIT);
    }

}
