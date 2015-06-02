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

import com.zhuoyou.pay.sdk.ZYGameManager;
import com.zhuoyou.pay.sdk.entity.PayParams;
import com.zhuoyou.pay.sdk.listener.ZYInitListener;
import com.zhuoyou.pay.sdk.listener.ZYRechargeListener;

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
        ZYGameManager.init(activity, new ZYInitListener() {

            @Override
            public void iniSuccess() {
                // TODO Auto-generated method stub

                //Toast.makeText(MainActivity.this, "初始化成功", 1).show();

            }

            @Override
            public void iniFail(String msg) {
                // TODO Auto-generated method stub
                Log.i("MainActivity", "msg--"+msg);
                //Toast.makeText(MainActivity.this, "初始化失败 "+msg, 1).show();

            }
        });
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        PayParams params=new PayParams();
        params.setAmount((int)price * 10);
        params.setPropsName("元宝");
        params.setOrderId(orderID);
        params.setExtraParam(callBackInfo);

        ZYGameManager.pay(params, activity, new ZYRechargeListener() {

            @Override
            public void success(PayParams params, String zyOrderId) {
                payCallBack.onSuccess("");
            }

            @Override
            public void fail(PayParams params, String erroMsg) {
                Log.v("sdk", "pay fail:"+erroMsg);
                payCallBack.onFail(erroMsg);
            }

        });
    }
}
