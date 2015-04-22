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
import com.alipay.sdk.app.PayTask;
import com.tendcloud.appcpa.TalkingDataAppCpa;
import com.tendcloud.appcpa.Order;


public class GameProxyImpl extends GameProxy{
    private ZcPay ac;
    private OrderInfos or;
    private String cp_RSA="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOUust2OfurxzdifqELDYBBM44tUmqa/Sfqn90MkrjpORe78BuiUU25+JWzxizrgLgSicjH/RHZvWZs2CcZ7v2Q+cjlbMeqth/rDk5p2HW0jpEohkguwKt+2RenPTHzvRhLQ0bdYpYVQN6g8qPOVkkd1rsSVaz69PF5ENljJyw3JAgMBAAECgYEAvTRtdF4Ex8Ay+ejtR5j2gN6JaGjDeHAqCiaLCsKImBgwwhkNNwvlSS4ZhbRwBn43X5og/sfIZKKO7oWRUmytVuubR9eXsKTZGnrN8HgxEvGhlDoEBFu1GoEMmrD+XMAMsC2kKBgxjy315Ildz9LXB8tBMh1FjlQWJIXWUOMiSXUCQQD9uY34hHDFn5Fu1cLTRn8xk0vYjQzYp7FUty5k5CUMGhuSG4jXUJBHDoeXla51vyF74Qq1DOSQW6hVDMeSSa7XAkEA5zzOGfOLYbQhkD5SP6UdfBjeBJ6Em8i0lyuM/xTxEHgs9x8x2UdVUy0BSaVkrjxC4SInaM3ZIQN5o+rdvhy0XwJBANkT03KnpXB/eEdSnjBy5Un+EutAqpgGyUKIwynQxB2ZjLMx2Z8WL4qL1NiNWMkm8LfzL1z9neQgd2Hk4C652dsCQCAkramh1yAvv/KjFx/NvfmAI2yU9G4LSj8xSJo0uQXHDskTRwSjC9NSEDnCiepGai2NZ9kDtEkIiKImhchliRUCQHRacVqhfPqVjmx9HugQt1FvDnq1T+nKsYImZSKYi8oGGItXOX+alRIcflOWZk3zUpNO2Sbdx9LaugznUlYzib8=";
    private String App_id = "547347373077037056";
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

    public boolean supportPay() {
        return false;
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        this.payCallBack = payCallBack;
        String call_backurl = "http://sdk.nataku.yunyuegame.com/sdk/android/sdk/zc/pay_callback";
        Log.i("call_backurl", call_backurl);
        or = new OrderInfos(); 
        or.setApp_id(App_id); 
        or.setChannel_id("0"); 
        or.setProduct_name(name); 
        or.setProduct_description("可以用于购买物品"); 
        or.setProduct_price(String.valueOf(((int)price) * 100));
        // or.setNotify_url("http://sdk.nataku.yunyuegame.com/sdk/zcgame/pay_callback/:worldID/:transaction_id"); 
        or.setNotify_url(call_backurl); 

        or.setUser_id(callBackInfo+"_"+orderID);

        Log.i("sdk_ourpalm_info price: ", String.valueOf(((int)price) * 100));

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
