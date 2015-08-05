package org.yunyue;

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

import com.future.playgame.coin.listenter.PlayGCInitSDKListener;
import com.future.playgame.coin.listenter.PlayGCPayListener;
import com.future.playgame.coin.listenter.PlayGCQueryInfoListener;
import com.future.playgame.coin.manager.PlayGCConfigManager;
import com.future.playgame.coin.manager.PlayGCManager;
import com.future.playgame.coin.manager.PlayGCMessageUtils;

public class GameProxyImpl extends GameProxy{

    private static boolean inited = false;
    private static String appid = "${APPID}";
    private static String appkey = "${APPNAME}";

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    public void login(final Activity activity, final Object customParams) {
		PlayGCManager.instance().initSDK(activity,
				new PlayGCInitSDKListener() {

					@Override
					public void onInitRstCode(int rstCode) {
                        if (rstCode == PlayGCMessageUtils.INIT_SDK_SUCCESS) {
                            inited = true;
                            showFloatView(activity);
                            User u = new User();
                            u.token = PlayGCConfigManager.instance().getAuthCode();
                            u.userID = PlayGCConfigManager.instance().getMMId();
                            userListerner.onLoginSuccess(u, customParams);
                        } else if (rstCode == PlayGCMessageUtils.INIT_SDK_REPEAT){
                            User u = new User();
                            u.token = PlayGCConfigManager.instance().getAuthCode();
                            u.userID = PlayGCConfigManager.instance().getMMId();
                            userListerner.onLoginSuccess(u, customParams);
                        } else {
                            userListerner.onLoginFailed("", customParams);
                        }
					}
				});
	}

    public void onDestroy(Activity activity) {
        dismissFloatView(activity);
        PlayGCManager.instance().closeSDK();
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        PlayGCManager.instance().intentPayActivity(
                new PlayGCPayListener() {
                    @Override
                    public void onPayResult(int resultCode, Object payObj,int type) {
                        Log.i("tag", "code:"+resultCode+" result:"+payObj.toString()+" type:"+type);
                        if (resultCode == PlayGCMessageUtils.PAY_SUCCESS) {
                            payCallBack.onSuccess("");
                        } else {
                            payCallBack.onFail("");
                        }
                    }
                }, PlayGCManager.ACTIVITY_MMUNIPAY_FLAG, orderID, appid, "${APPNAME}",
                    appkey, name, "1", String.valueOf((int)price), ID, "可用于购买道具");
    }

    /**
     * 显示悬浮窗
     */
    private void showFloatView(Activity activity) {
        if (inited) {
            PlayGCManager.instance().managerFloatView(activity,
                    PlayGCManager.MANAGER_FLOATVIEW_SHOW);
        }
    }

    /**
     * 移除悬浮窗
     */
    private void dismissFloatView(Activity activity) {
        if (inited) {
            PlayGCManager.instance().managerFloatView(activity,
                    PlayGCManager.MANAGER_FLOATVIEW_DISMISS);
        }
    }

    public void onResume(Activity activity) {
        showFloatView(activity);
    }

    public void onPause(Activity activity) {
        dismissFloatView(activity);
    }
}
