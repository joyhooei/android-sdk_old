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

import com.game.sdk.YTSDKManager;
import com.game.sdk.domain.LoginErrorMsg;
import com.game.sdk.domain.LogincallBack;
import com.game.sdk.domain.OnLoginListener;
import com.game.sdk.domain.OnPaymentListener;
import com.game.sdk.domain.PaymentCallbackInfo;
import com.game.sdk.domain.PaymentErrorMsg;
import com.game.sdk.util.Constants;
import com.game.sdk.util.Logger;
import com.game.sdk.util.MResource;
import com.game.sdk.util.Md5Util;

public class GameProxyImpl extends GameProxy{
	public  YTSDKManager sdkmanager;

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    @Override
    public void applicationInit(Activity activity) {
        Log.v("sdk", "applicationInit");
		sdkmanager = YTSDKManager.getInstance(activity);
    }

    @Override
    public void login( final Activity activity, final Object customParams ) {
        Log.v("sdk", "login");
        sdkmanager.showLogin( activity, true, new OnLoginListener() {
            @Override
            public void loginSuccess(LogincallBack logincallback) {
                // 登录成功
                User u = new User();
                u.userID = logincallback.username;
                u.token = logincallback.sign;
                u.loginTime = Long.toString( logincallback.logintime );
                userListerner.onLoginSuccess(u, customParams);

                // 显示浮点
                sdkmanager.showFloatView();
            }

            @Override
            public void loginError(LoginErrorMsg errorMsg) {
                // 登录失败
                Toast.makeText( activity, "登录失败", Toast.LENGTH_SHORT ).show();
            }
        });
    }

    @Override
    public void pay( final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack ) {
        Log.v("sdk", "pay:" + ID + "," + name + "," + price + "," + callBackInfo + "," + roleInfo.toString());

        String role_id = "";
        String server_id = "1";
        String ext = "";
        try {
            role_id = roleInfo.getString("id");
            server_id = roleInfo.getString("serverID");
            ext = server_id + "_" + roleInfo.getString("id") + "_" + orderID;
        } catch (JSONException e) {
            Log.e("sdk", "roleInfo parse failed, ignore");
        }

        sdkmanager.showPay(activity, role_id, String.format( "%d", (int)price ), server_id, ID, name, ext, new OnPaymentListener() {
            @Override
            public void paymentSuccess( PaymentCallbackInfo callbackInfo ) {
                payCallBack.onSuccess( null );
                Toast.makeText( activity, "支付成功", Toast.LENGTH_SHORT ).show();
            }

            @Override
            public void paymentError( PaymentErrorMsg errorMsg ) {
                Log.v( "sdk", "支付失败 code : " + errorMsg.code + " err_msg : " + errorMsg.msg );
                payCallBack.onFail( null );
                Toast.makeText( activity, "支付失败", Toast.LENGTH_SHORT ).show();
            }
        });
    }

	@Override
	public void onDestroy(Activity activity) {
		super.onDestroy(activity);

        sdkmanager.recycle();//游戏退出必须调用
	};

	@Override
	public void onStop(Activity activity) {
       	super.onStop(activity);

		sdkmanager.removeFloatView();//隐藏悬浮窗口
	}

	@Override
	public void onResume(Activity activity) {
		super.onResume(activity);

		sdkmanager.showFloatView();//显示悬浮窗口
    }
}
