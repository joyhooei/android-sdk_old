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

import cn.zcpay.sdk.app.OrderInfos;
import cn.zcpay.sdk.app.ZcPay;


public class GameProxyImpl extends GameProxy{
    private ZcPay ac;
    private OrderInfos or;
    private String cp_RSA="${ZC_RSA}";
    private String App_id = "${ZC_APPID}";
    private PayCallBack payCallBack;

    @Override
    public void onCreate(Activity activity) {
        super.onCreate(activity);

        ac = ZcPay.getInstance();
        ac.Init(activity, MyHandler, cp_RSA);
    }

    public boolean supportLogin() {
        return false;
    }

    public boolean supportCommunity() {
        return false;
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        this.payCallBack = payCallBack;
        //String call_backurl = "http://sdk.nataku.yunyuegame.com/sdk/android/sdk/zcgame/pay_callback";
        or = new OrderInfos();
        or.setApp_id(App_id);
        or.setChannel_id("0");
        or.setProduct_name(name);
        or.setProduct_description("可以用于购买物品");
        or.setProduct_price(String.valueOf(((int)price) * 100));

        String serverID, entityID;
        try {
            serverID = roleInfo.getString("serverID");
            entityID = roleInfo.getString("id");
        } catch (JSONException e) {
            return;
        }

        String callbackurl = "http://sdk.nataku.yunyuegame.com/sdk/android/zcgame/pay_callback/" + serverID + "/" + orderID + "/SDK_YYZCLH";
        or.setNotify_url(callbackurl);
        Log.v("sdk", "backurl:" + callbackurl);
        //or.setNotify_url(callbackurl); 

        or.setUser_id(entityID);

        ac.Pay(or);
    }

    Handler MyHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 10000:
                    payCallBack.onSuccess("");
                    break;
                case 10001:
                    payCallBack.onFail("");
                    break;
                case 10002:
                    payCallBack.onFail("");
                    break;
                case 10003:
                    payCallBack.onFail("");
                    break;
                case 10004:
                    payCallBack.onFail("");
                    break;
            }
        }
    };
}
