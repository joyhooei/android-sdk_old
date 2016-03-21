package org.weilan;

import java.util.UUID;
import java.util.Random;

import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;
import android.text.TextUtils;

import com.nearme.game.sdk.*;
import com.nearme.game.sdk.callback.ApiCallback;
import com.nearme.game.sdk.callback.GameExitCallback;
import com.nearme.game.sdk.common.model.biz.PayInfo;
import com.nearme.game.sdk.common.model.biz.ReportUserGameInfoParam;
import com.nearme.game.sdk.common.model.biz.ReqUserInfoParam;
import com.nearme.game.sdk.GameCenterSDK;
import com.nearme.game.sdk.common.model.biz.GameCenterSettings;
import com.nearme.game.sdk.common.util.AppUtil;
import com.nearme.platform.opensdk.pay.PayResponse;

public class GameProxyImpl extends GameProxy {

    public boolean supportCommunity() {
        // 是否支持打开社区
        return false;
    }

    @Override
    public void applicationInit(Activity activity) {
        // 因为sdk插件中service activity跑在独立进程，这里只需要在主进程做一次初始化操作。
        //if("${PACKAGE_NAME}".equals(AppUtil.getCurrentProcessName(activity))){
        String appKey = "${APPKEY}";
        String appSecret = "${APPSECRET}";
        GameCenterSettings gameCenterSettings = new GameCenterSettings(
                false,    // 网游固定为false
                appKey,
                appSecret,
                false,     // 调试开关 true 打印log，false 关闭log，正式上线请设置为false
                true);   // 将游戏横竖屏状态传递给sdk， true为竖屏  false为横屏
        GameCenterSDK.init(gameCenterSettings, activity);
        //}
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        GameCenterSDK.getInstance().onResume(activity);
    }

    @Override
    public void onPause(Activity activity) {
        GameCenterSDK.getInstance().onPause();
        super.onPause(activity);
    }

    @Override
    public void login(final Activity activity, final Object customParams) {
        // 调用SDK执行登陆操作
        GameCenterSDK.getInstance().doLogin(activity, new ApiCallback() {
            @Override
            public void onSuccess(String content) {
                GameCenterSDK.getInstance().doGetTokenAndSsoid(new ApiCallback() {
                    @Override
                    public void onSuccess(String resultMsg) {
                        try {
                            JSONObject json = new JSONObject(resultMsg);
                            String token = json.getString("token");
                            String ssoid = json.getString("ssoid");

                            User usr = new User();
                            usr.token = token;
                            usr.userID = ssoid;
                            userListerner.onLoginSuccess(usr, customParams);
                        } catch (JSONException e) {
                            userListerner.onLoginFailed("parser token json failed", customParams);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String resultMsg, int resultCode) {
                        userListerner.onLoginFailed("get token failed", customParams);
                    }
                });
            }

            @Override
            public void onFailure(String content, int code) {
                userListerner.onLoginFailed(content, customParams);
            }
        });
    }

    @Override
    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo,final PayCallBack payCallBack) {
        final PayInfo payInfo = new PayInfo(orderID, callBackInfo, (int)price * 100);
        payInfo.setProductDesc(name);
        payInfo.setProductName(name);
        payInfo.setCallbackUrl("${PAY_URL}");
        GameCenterSDK.getInstance().doPay(activity, payInfo, new ApiCallback() {
            @Override
            public void onSuccess(String resultMsg) {
                // 支付成功
                payCallBack.onSuccess(resultMsg);
            }

            @Override
            public void onFailure(String resultMsg, int resultCode) {
                // 支付失败
                payCallBack.onFail(resultMsg);
            }
        });
    }

    @Override
    public void exit(final Activity activity,final ExitCallback callback) {
        GameCenterSDK.getInstance().onExit(activity, new GameExitCallback(){
            @Override
            public void exitGame() {
                // CP 实现游戏退出操作，也可以直接调用 AppUtil工具类里面的实现直接强杀进程~
                AppUtil.exitGameProcess(activity);
            }
        });
    }

    @Override
    public void setExtData(Context context, String ext) {
        try {
            JSONObject src = new JSONObject(ext);

            GameCenterSDK.getInstance().doReportUserGameInfoData(
                    new ReportUserGameInfoParam("${APPID}"
                        , src.getString("serverID")
                        , src.getString("name")
                        , src.getString("level")),new ApiCallback() {

                        @Override
                        public void onSuccess(String resultMsg) {
                        }

                        @Override
                        public void onFailure(String resultMsg, int resultCode) {
                        }
            });
        } catch (Exception e) {
            // 处理异常
            Log.e("sdk", "set Ext Data exception");
            e.printStackTrace();
        }
    }
}
