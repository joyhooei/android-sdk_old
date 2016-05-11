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

import com.jodo.paysdk.JodoPlaySDKManager;
import com.jodo.paysdk.JodoPlaySDKStatusCode;
import com.jodo.paysdk.interfaces.FaceBookLikeCallbaskListener;
import com.jodo.paysdk.interfaces.FaceBookShareCallbaskListener;
import com.jodo.paysdk.interfaces.InitCallbackListener;
import com.jodo.paysdk.interfaces.LoginCallbackListener;
import com.jodo.paysdk.interfaces.LoginWithActivityCallbackListener;
import com.jodo.paysdk.interfaces.OrderCallbackListener;
import com.jodo.paysdk.interfaces.RoleLoadedCallbackListener;
import com.jodo.paysdk.interfaces.UnzipCallbackListener;
import com.jodo.paysdk.model.JodoPayInfo;
import com.jodo.paysdk.model.UnzipInfo;

public class GameProxyImpl extends GameProxy{

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    public void onCreate(final Activity activity) {
        // 以下为activity生命周期，有些sdk会要求在里面加入调用。
		JodoPlaySDKManager.onCreate(activity);

        JodoPlaySDKManager.setScreenPortrait(true);// true:竖屏;false:横屏
		JodoPlaySDKManager.initSDK(activity, new InitCallbackListener() {
			public void onSdkInitFinished(int code, String message) {
				switch (code) {
					case JodoPlaySDKStatusCode.SUCCESS:
						//Toast.makeText(activity, "Initialize SDK success~~~", Toast.LENGTH_SHORT).show();
						// SDK初始化成功
						break;
					case JodoPlaySDKStatusCode.INIT_FAIL:
						// SDK初始化失败
						Toast.makeText(activity, "Initialize jodo SDK failed~~~", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		}, "${FB_APP_ID}");
    }

    public void login(final Activity activity, final Object customParams) {
        JodoPlaySDKManager.showLoginView(activity, new LoginCallbackListener() {
            public void onLoginCallback(int code, String uid, String sessionToken) {
                switch (code) {
                    case JodoPlaySDKStatusCode.SUCCESS:
                        User u = new User();
                        u.userID = uid;
                        u.token = sessionToken;
                        userListerner.onLoginSuccess(u, customParams);
                        break;
                    case JodoPlaySDKStatusCode.LOGIN_CLOSE:
                        Toast.makeText(activity, "LoginFailed", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    public void pay(final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        if (!JodoPlaySDKManager.isLogin(activity)) {
            Toast.makeText(activity, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        String ext = "";
        String server_id = "1";
        String role_name = "";
        String role_level = "1";
        String server_name = "";
        try {
            server_id = roleInfo.getString("serverID");
            role_name = roleInfo.getString("name");
            role_level = roleInfo.getString("level");
            server_name = roleInfo.getString("serverName");
            ext = server_id + "_" + roleInfo.getString("id");
        } catch (JSONException e) {
            Log.e("sdk", "roleInfo parse failed, ignore");
        }

        JodoPayInfo jodoPayInfo = new JodoPayInfo();
        // 服务器号:角色所在服的编号
        jodoPayInfo.setServerid(server_id);
        // 服务器名：角色所在服务器名称
        jodoPayInfo.setServername(server_name);
        // 角色名:用户在游戏中的角色名
        jodoPayInfo.setRolename(role_name);
        // 角色等级
        jodoPayInfo.setRolelevel(Integer.parseInt(role_level));
        // 请求充值金额（单位为人民币CNY），仅供参考，用户可以在支付页面修改金额，所以请以卓动调用cp的发货接口时的金额为准！！！
        jodoPayInfo.setPrice((int)price);
        // cp充值透传信息,在回调时会回传给cp,如果不需要,传入null
        jodoPayInfo.setExt(ext);
        // 游戏厂商的订单号，要求必须由开发者的业务服务器生成，因订单支付成功后游戏平台服务器会直接将支付结果通知给开发者的业务服务器，通知参数的cporderid是重要信息。
        jodoPayInfo.setCporderid(orderID); // *该函数由CP实现

        /*
         * 填好参数，跳转到支付页面
         */
        JodoPlaySDKManager.showPayView(activity, jodoPayInfo, new OrderCallbackListener() {
            @Override
            public void onOrderCallback(String cpOrderId, int statusCode, String msg) {
                switch (statusCode) {
                    case JodoPlaySDKStatusCode.ORDER_SUCCESS:
                        payCallBack.onSuccess(null);
                        Toast.makeText( activity, "支付成功", Toast.LENGTH_SHORT ).show();
                        break;
                    case JodoPlaySDKStatusCode.ORDER_FAILED:
                        payCallBack.onFail(null);
                        Toast.makeText( activity, "支付失败", Toast.LENGTH_SHORT ).show();
                        break;
                        //支付状态需要重新确认，请引导玩家重新点击支付按钮，sdk会重新确认支付状态
                    case JodoPlaySDKStatusCode.ORDER_NEED_RETRY:
                        Toast.makeText( activity, "请重新点击支付按钮以确认支付状态", Toast.LENGTH_SHORT ).show();
                        break;
                        //其他支付异常
                    case JodoPlaySDKStatusCode.ORDER_EXCEPTION:
                        payCallBack.onFail(null);
                        Toast.makeText( activity, "支付失败", Toast.LENGTH_SHORT ).show();
                        break;
                }
            }
        });
    }

    public void onStart(Activity activity) {
        super.onStart( activity );

		JodoPlaySDKManager.onStart(activity);
    }

    public void onStop(Activity activity) {
        super.onStop( activity );

		JodoPlaySDKManager.onStop(activity);
    }

    public void onResume(Activity activity) {
        super.onResume( activity );

		JodoPlaySDKManager.onResume(activity);
    }

    public void onPause(Activity activity) {
        super.onPause( activity );

		JodoPlaySDKManager.onPause(activity);
    }

    public void onDestroy(Activity activity) {
		JodoPlaySDKManager.onDestroy(activity);

        super.onDestroy( activity );
    }

	public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		super.onActivityResult(activity, requestCode, resultCode, data);

		JodoPlaySDKManager.onActivityResult(activity,requestCode, resultCode, data);
	}
	
	public void onBackPressed(Activity activity) {
        super.onBackPressed( activity );

		JodoPlaySDKManager.onBackPressed(activity);
		JodoPlaySDKManager.showQuitGameView(activity);
	}
}
