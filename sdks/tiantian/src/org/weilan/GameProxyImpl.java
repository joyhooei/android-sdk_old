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

import com.ltsdkgame.sdk.SDKManager;
import com.ltsdkgame.sdk.SDKState;
import com.ltsdkgame.sdk.listener.LTCallback;
import com.ltsdkgame.sdk.model.GameUserInfo;

public class GameProxyImpl extends GameProxy{
    private static boolean TEST_MODE = true;

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    /*
    public void onResume(Activity activity) {
        super.onResume(activity);
        SDKManager.showFloatWindow();
    }

    public void onPause(Activity activity) {
        SDKManager.hideFloatWindow();
        super.onPause(activity);
    }
    */

    public void applicationInit(final Activity activity) {
		SDKManager.iniSDK(activity, TEST_MODE,new LTCallback() {
			@Override
			public void callback(int code, String msg) {
                if( SDKState.Success != code ) {
                    Toast.makeText( activity, "SDK初始化失败", Toast.LENGTH_SHORT ).show();
                }
			}
		});
    }

    public void login(final Activity activity,final Object customParams) {
        SDKManager.userLogin(activity, new LTCallback() {
            @Override
            public void callback(int code, String msg){
                if( code == SDKState.Success ){
                    User usr = new User();
                    usr.token = msg;
                    userListerner.onLoginSuccess(usr, customParams);
                    SDKManager.initializeFloatWindow(activity);
                    SDKManager.showFloatWindow();
                } else {
                    userListerner.onLoginFailed(msg, customParams);
                }
            }
        });
    }

    public void logout(Activity activity,final Object customParams) {
        SDKManager.logout(new LTCallback() {
            @Override
            public void callback(int code, String msg){
                if( code == SDKState.Success ){
                    SDKManager.hideFloatWindow();
                    userListerner.onLogout(customParams);
                }
            }
        });
    }


    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo,final PayCallBack payCallBack) {
		GameUserInfo gameUserInfo = new GameUserInfo();
        gameUserInfo.setCpId("${APPID}");
		gameUserInfo.setZoneName(roleInfo.optString("serverID"));
		gameUserInfo.setRoleName(roleInfo.optString("name"));
		SDKManager.startPay(activity, gameUserInfo, Double.parseDouble(price + ""),name, "${PAY_URL}", callBackInfo, new LTCallback() {
			@Override
			public void callback(int code, String msg) {
				if(code==SDKState.Success){
                    payCallBack.onSuccess(msg);
				}else{
                    payCallBack.onFail(msg);
				}
			}
			
		});
    }


    public void setExtData(Context context, String ext) {
        try {
            JSONObject src = new JSONObject(ext);
            GameUserInfo gameUserInfo = new GameUserInfo();
            gameUserInfo.setCpId("${APPID}");
            gameUserInfo.setZoneName(src.getString("serverID"));
            gameUserInfo.setRoleName(src.getString("name"));
            SDKManager.submitZoneInfo(gameUserInfo, new LTCallback() {
                @Override
                public void callback(int code, String msg) {
                }
            });
        } catch (Exception e) {
            // 处理异常
            Log.e("sdk", "set Ext Data exception");
            e.printStackTrace();
        }
    }
}
