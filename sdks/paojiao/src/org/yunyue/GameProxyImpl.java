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

import com.paojiao.sdk.CallbackListener;
import com.paojiao.sdk.ExitInterface;
import com.paojiao.sdk.PJApi;
import com.paojiao.sdk.PJError;
import com.paojiao.sdk.PJUser;
import com.paojiao.sdk.bean.RoleInfo;
import com.paojiao.sdk.utils.HttpPost.HttpPostResponse;
import com.paojiao.sdk.utils.Prints;

public class GameProxyImpl extends GameProxy{
    /** 泡椒提供给合作方的gameId或appId */
	public static final int APP_ID = ${APPID};
	/** 泡椒提供给合作方的privateKey或appKey */
	public static final String APP_KEY = "${APPKEY}";
    /** true为调试环境；false为正式上线环境 */
    public  static final boolean SDK_DEBUG = false;
    /** 启动游戏时，是否显示泡椒网的闪屏，默认开启。 */
    public static final boolean SHOW_SPLASH = true;
    /** SDK对象 */
    private PJApi pjApi;
    private String ext;

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
        /** 调用API初始化SDK */
        pjApi = PJApi.newInstance(activity, APP_ID, APP_KEY, SHOW_SPLASH, SDK_DEBUG);
    }

    public void login(final Activity activity, final Object customParams) {
        pjApi.openLoginDialog(new CallbackListener(activity) {

            public void onError(Throwable error) {
                super.onError(error);
            }
        
            public void onLoginSuccess(Bundle bundle) {
                super.onLoginSuccess(bundle);

                //挂载悬浮窗
                PJApi.showFloatSecondActivity(activity, true);

                User u = new User();
                u.token = bundle.getString(PJUser.TOKEN);
                u.userID = bundle.getString(PJUser.UID);
                userListerner.onLoginSuccess(u, customParams);
            }

            public void onLoginError(PJError error) {
                super.onLoginError(error);
                userListerner.onLoginFailed("", customParams);
            }
        });
    }

    public void pay(final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        pjApi.openPayActivity(name, price, "可用于购买道具", callBackInfo + "_" + orderID,
                new CallbackListener(activity) {
                    public void onError(Throwable error) {
                        super.onError(error);
                    }

                    public void onPaymentCancel(Bundle info) {
                        Prints.d("TEST-LOG", "支付取消 info=" + info);
                        super.onPaymentCancel(info);
                        Toast.makeText(activity, "支付取消",
                            Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void openCommunity(final Activity activity) {
        // 参数失败回调
        pjApi.openUcenterActivity(new CallbackListener(activity) {

            public void onError(Throwable error) {
                super.onError(error);
            }

            // 用户意外登出时回调，例如更改用户名或密码等信息时。
            public void onLogout(String uid, String username) {
                userListerner.onLogout(null);
            }

            public void onOpenError(PJError error) {
                super.onOpenError(error);
                Toast.makeText(activity, error.getMErrorMessage(),
                    Toast.LENGTH_SHORT).show();
            }

        });
    }

	@Override
	public void onDestroy(Activity activity) {
		super.onDestroy(activity);
		//销毁悬浮窗
		PJApi.removeFloatSecondActivity(activity);
	}

    public void exit(Activity activity, ExitCallback callback) {
        RoleInfo roleInfo = null;
        try {
            JSONObject jExt = new JSONObject(ext);
            roleInfo = new RoleInfo(jExt.getString("name"), Integer.parseInt(jExt.getString("level")), jExt.getString("serverName"), Integer.parseInt(jExt.getString("gold")));
        } catch (JSONException e) {
            return;
        }
        pjApi.exitGame(roleInfo, true,
                new ExitInterface() {
                    public void onExit() {
                        poem.quitApplication();
                    }
                });
    }

    public void setExtData(Context context, String ext) {
        this.ext = ext;
    }
}
