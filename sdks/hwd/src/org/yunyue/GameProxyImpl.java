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

import com.qtplay.gamesdk.QTPlay;
import com.qtplay.gamesdk.callback.QTPayCallback;
import com.qtplay.gamesdk.util.LogDebugger;
import com.qtplay.gamesdk.util.ResourceUtil;
import com.qtplay.gamesdk.util.ToastUtil;

public class GameProxyImpl extends GameProxy{
    public boolean supportLogin() {
        return false;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    public void applicationInit(Activity activity) {
        QTPlay.registerGame(activity, "${APPID}", QTPlay.SCREEN_PORTRAIT, false);
		QTPlay.qt_setGameArea("1");
    }

    public void pay(final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        QTPlay.qt_payProductByQTPlayFaFa(activity, callBackInfo + "_" + orderID, name, Float.toString( price ), "", new QTPayCallback() {
            @Override
            public void onQTPayStart(Activity act, String pJsonStr) {
            }

            @Override
            public void onQTPaySuccess(Context context, String code, String msg, String data) {
                payCallBack.onSuccess(null);
                Toast.makeText(activity.getApplicationContext(), "支付成功", Toast.LENGTH_SHORT).show();
                //LogDebugger.info("onPaySuccess", "code " + code + " msg " + msg + " data " + data);
                //ToastUtil.showToast(QTDemoActivity0.this, msg);
            }

            @Override
            public void onQTPayFailed(Context context, String code, String msg, String data) {
                payCallBack.onFail(null);
                Toast.makeText(activity.getApplicationContext(), "支付失败：" + msg, Toast.LENGTH_SHORT).show();
                //LogDebugger.info("onPayFailed", "code " + code + " msg " + msg + " data " + data);
                //ToastUtil.showToast(QTDemoActivity0.this, msg);
            }
        });
    }
}
