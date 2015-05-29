package org.yunyue;

import java.util.UUID;
import java.util.Map;
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

import com.qtplay.gamesdk.QTPlay;
import com.qtplay.gamesdk.callback.QTPayCallback;
import com.qtplay.gamesdk.callback.LoginCallback;
import com.qtplay.gamesdk.util.LogDebugger;
import com.qtplay.gamesdk.util.ResourceUtil;
import com.qtplay.gamesdk.util.ToastUtil;

public class GameProxyImpl extends GameProxy{
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
        QTPlay.registerGame(activity, "${APPID}", QTPlay.SCREEN_PORTRAIT, false);
		QTPlay.qt_setGameArea("1");
    }

    public void pay(final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        QTPlay.qt_payProductByQTPlayFaFa(activity, orderID, name, Float.toString( price ), callBackInfo, new QTPayCallback() {
            @Override
            public void onQTPayStart(Activity act, String pJsonStr) {
            }

            @Override
            public void onQTPaySuccess(Context context, String code, String msg, String data) {
                payCallBack.onSuccess("支付成功");
                //Toast.makeText(activity.getApplicationContext(), "支付成功", Toast.LENGTH_SHORT).show();
                //LogDebugger.info("onPaySuccess", "code " + code + " msg " + msg + " data " + data);
                //ToastUtil.showToast(QTDemoActivity0.this, msg);
            }

            @Override
            public void onQTPayFailed(Context context, String code, String msg, String data) {
                payCallBack.onFail(msg);
                Log.v("sdk", "支付失败：" + msg);
                //Toast.makeText(activity.getApplicationContext(), "支付失败：" + msg, Toast.LENGTH_SHORT).show();
                //LogDebugger.info("onPayFailed", "code " + code + " msg " + msg + " data " + data);
                //ToastUtil.showToast(QTDemoActivity0.this, msg);
            }
        });
    }

    public void login(Activity activity, final Object customParams) {
        QTPlay.qt_loginView(activity, new LoginCallback(){//登录回调
            @Override
            public void callback(int code, String message, Map<String, String> data) {
                if(code == 0 ){//code==0为登录成功，其他为失败
                    String userid =(String)data.get("userid");//用户社区id
                    //String username =(String)data.get("username");//用户社区名字
                    //String userface =(String)data.get("userface");//用户社区头像
                    User u = new User();
                    u.userID = userid;
                    u.token = "";
                    userListerner.onLoginSuccess(u, customParams);
                }
                else {
                    Log.v("sdk", "login fail:" + code + "," + message);
                    userListerner.onLoginFailed(message, customParams);
                }
            }
        });
    }

    public void openCommunity(Activity activity) {
        QTPlay.qt_openSNS(activity);
    }
}
