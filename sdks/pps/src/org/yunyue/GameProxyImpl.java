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

import com.pps.sdk.platform.PPSGamePlatformInitListener;
import com.pps.sdk.platform.PPSPlatform;
import com.pps.sdk.platform.PPSPlatformListener;
import com.pps.sdk.platform.PPSUser;

public class GameProxyImpl extends GameProxy{

	private static PPSPlatform ppsPlatform;
	private static String gameId = "${GAMEID}";

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return true;
    }

    public boolean supportPay() {
        return true;
    }

    public void applicationInit(Activity activity) {
        ppsPlatform = PPSPlatform.getInstance();
        ppsPlatform.initPlatform(activity, gameId,new PPSGamePlatformInitListener() {
				@Override
				public void onSuccess() {
				}
				@Override
				public void onFail(String arg0) {
				}
			});
    }

    public void login(final Activity activity, final Object customParams) {
        ppsPlatform.ppsLogin(activity, new PPSPlatformListener() {
				@Override
				public void leavePlatform() {  
					super.leavePlatform();
					Log.d("sdk", "ppsLogin leavePlatform");
				}

				@Override
				public void loginResult(int result, PPSUser user) {
					super.loginResult(result, user);
					ppsPlatform.initSliderBar(activity);
					Log.d("PPSSDKPlatfrom", "ppsLogin loginResult");
                    User u = new User();
                    u.userID = user.uid;
                    u.token = "{\"token\": \""+user.sign+"\", \"time\": \""+user.timestamp+"\"}";
                    userListerner.onLoginSuccess(u, customParams);
				}

			});
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        String roleID;
        String serverID;
        try {
            roleID = roleInfo.getString("id");
            serverID = "ppsmobile_s" + roleInfo.getString("serverID");
        } catch (JSONException e) {
            return;
        }
        ppsPlatform.ppsPayment(activity, (int)price, roleID, serverID, callBackInfo + "_" + orderID, new PPSPlatformListener() {
				@Override
				public void leavePlatform() {
					// TODO Auto-generated method stub
					super.leavePlatform();
					Log.d("PPSSDKPlatfrom", "ppsPayment leavePlatform");
				}

				@Override
				public void paymentResult(int result) {
					// TODO Auto-generated method stub
					super.paymentResult(result);
					Log.d("PPSSDKPlatfrom", "ppsPayment paymentResult : " + result);
                    if (result == 0) {
                        payCallBack.onSuccess("");
                    }
                    else {
                        payCallBack.onFail("");
                    }
				}
				
			});
    }

    public void openCommunity(Activity activity) {
        ppsPlatform.ppsAccountCenter(activity, new PPSPlatformListener() {

				@Override
				public void leavePlatform() {
					// TODO Auto-generated method stub
					super.leavePlatform();
					Log.d("PPSSDKPlatfrom", "ppsAccountCenter leavePlatform");
				}

				@Override
				public void loginResult(int result, PPSUser user) {
					// TODO Auto-generated method stub
					super.loginResult(result, user);
                    User u = new User();
                    u.userID = user.uid;
                    u.token = "{\"token\": \""+user.sign+"\", \"time\": \""+user.timestamp+"\"}";
                    userListerner.onLoginSuccess(u, null);
					Log.d("PPSSDKPlatfrom", "ppsAccountCenter loginResult");
				}

				@Override
				public void logout() {
					// TODO Auto-generated method stub
					super.logout();
                    userListerner.onLogout(null);
					Log.d("PPSSDKPlatfrom", "ppsAccountCenter logout");
				}

			});
    }
}
