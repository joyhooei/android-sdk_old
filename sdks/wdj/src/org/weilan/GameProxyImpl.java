package org.weilan;

import java.util.UUID;
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

import com.wandoujia.mariosdk.plugin.api.api.WandouGamesApi;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnMessageReceivedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnPayFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnSinglePayFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.model.MessageEntity;
import com.wandoujia.mariosdk.plugin.api.model.model.PayResult;

import com.wandoujia.mariosdk.plugin.api.model.callback.ScreenShotFun;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnCheckLoginCompletedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnLoginFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnLogoutFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.callback.OnUserInfoSettingFinishedListener;
import com.wandoujia.mariosdk.plugin.api.model.model.LoginFinishType;
import com.wandoujia.mariosdk.plugin.api.model.model.LogoutFinishType;
import com.wandoujia.mariosdk.plugin.api.model.model.UnverifiedPlayer;
import com.wandoujia.mariosdk.plugin.api.model.model.WandouPlayer;
import com.wandoujia.mariosdk.plugin.api.model.model.result.UserInfoSettingResult;

public class GameProxyImpl extends GameProxy {
    private WandouGamesApi wandouGamesApi;

    @Override
    public void applicationInit(Activity activity) {
        Log.v("sdk", "applicationInit");
    }

    @Override
    public void onCreate(Activity activity) {
        Log.v("sdk", "onCreate");
        wandouGamesApi = sdk_app.getWandouGamesApi();
        wandouGamesApi.init(activity);

        wandouGamesApi.setScreenShotFun(new ScreenShotFun() {
            @Override
            public void saveScreen(Context context, String filePath) {
                // 如果是coco2d-x 游戏，则保持为空实现即可。实体通过jni调用。
            }
        });

        wandouGamesApi.addWandouAccountListener( new WandouAccountListener() {

            @Override
            public void onLoginFailed(int arg0, String arg1) {
                userListerner.onLoginFailed("用户失败", customParams);
            }

            @Override
            public void onLoginSuccess() {
                WandouPlayer info = wandouGamesApi.getCurrentPlayerInfo();
                long duration = System.currentTimeMillis();

                User u = new User();
                u.userID = info.getId();
                u.token = wandouGamesApi.getToken();
                userListerner.onLoginSuccess(u, customParams);
            }

            @Override
            public void onLogoutSuccess() {
                userListerner.onLogout(null);
            }

        });
    }

    @Override
    public void login(Activity activity, final Object customParams) {
        // 调用SDK执行登陆操作
        Log.v("sdk", "login");

        wandouGamesApi.login(new OnLoginFinishedListener() {
            @Override
            public void onLoginFinished(LoginFinishType loginFinishType, UnverifiedPlayer unverifiedPlayer) {
                if (loginFinishType == LoginFinishType.CANCEL) {
                    userListerner.onLoginFailed("用户取消", customParams);
                }
                else {
                    User u = new User();
                    u.userID = unverifiedPlayer.getId();
                    u.token = unverifiedPlayer.getToken();
                    userListerner.onLoginSuccess(u, customParams);
                }
            }
        });
    }

    @Override
    public void logout(Activity activity, final Object customParams) {
        Log.v("sdk", "logout");
        wandouGamesApi.logout(new OnLogoutFinishedListener() {
            @Override
            public void onLoginFinished(LogoutFinishType logoutFinishType) {
                userListerner.onLogout(customParams);
            }
        });
    }

    @Override
    public void switchAccount(Activity activity, Object customParams) {
    }

    @Override
    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        Log.v("sdk", "pay:" + ID + "," + name + "," + price + "," + callBackInfo + "," + roleInfo.toString());
        wandouGamesApi.pay(activity, name, (long)(price*100),
                orderID + "_" + callBackInfo, new OnPayFinishedListener() {
                    @Override
                    public void onPaySuccess(PayResult payResult) {
                        if (payResult.getSuccess()) {
                            payCallBack.onSuccess("支付成功");
                        }
                        else {
                            payCallBack.onFail("支付失败");
                        }
                    }

                    @Override
                    public void onPayFail(PayResult payResult) {
                        payCallBack.onFail("支付失败");
                    }
        });
    }

    @Override
    public void exit(Activity activity, ExitCallback callback) {
        Log.v("sdk", "exit");
        callback.onNo3rdExiterProvide();
    }

    @Override
    public void applicationDestroy(Activity activity) {
        Log.v("sdk", "applicationDestroy");
    }

    @Override
    public void setExtData(Context context, String ext) {
        Log.v("sdk", "setExtData:" + ext);
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        wandouGamesApi.onResume(activity);
    }

    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
        wandouGamesApi.onPause(activity);
    }

    @Override
    public boolean supportCommunity() {
        return false;
    }

    public boolean supportLogout() {
        return true;
    }

}
