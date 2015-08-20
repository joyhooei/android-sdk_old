package org.yunyue;

import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import cn.egame.terminal.paysdk.EgamePay;
import cn.egame.terminal.paysdk.EgamePayListener;
import cn.egame.terminal.sdk.log.EgameAgent;
import cn.play.dserv.CheckTool;
import cn.play.dserv.ExitCallBack;
import egame.terminal.usersdk.CallBackListener;
import egame.terminal.usersdk.EgameUser;

public class GameProxyImpl extends GameProxy{
    private static final int CLIENT_ID = ${CLIENTID};

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return true;
    }

    public boolean supportPay() {
        return true;
    }

    public void login(Activity activity, final Object customParams) {
        EgameUser.start(activity, CLIENT_ID, new CallBackListener() {
            public void onSuccess(String info) {
                User u = new User();
                u.token = info;
                userListerner.onLoginSuccess(u, customParams);
            }
            public void onFailed(int code) {
                Log.v("sdk", "login failed:"+code);
                userListerner.onLoginFailed("登录失败", customParams);
            }
            public void onCancel() {
                userListerner.onLoginFailed("取消登录", customParams);
            }
        });
    }

    public void applicationInit(Activity activity) {
        EgamePay.init(activity);
    }

    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
        EgameAgent.onPause(activity);
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        EgameAgent.onResume(activity);
    }

    public void exit(final Activity activity, ExitCallback callback) {
        activity.runOnUiThread(new Runnable() {		
            @Override
            public void run() {
                CheckTool.exit(activity, new ExitCallBack() { 

                    @Override
                    public void exit() {
                        poem.quitApplication();
                    }

                    @Override
                    public void cancel() {
                    }
                });	
            }
        });
    }

    public void openCommunity(final Activity activity) {
        activity.runOnUiThread(new Runnable() {        
                @Override
                    public void run() {
                        CheckTool.more(activity);   
                    }
        });
    }

    public void pay(final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        HashMap<String, String> payParams=new HashMap<String, String>();
        payParams.put(EgamePay.PAY_PARAMS_KEY_TOOLS_PRICE, Integer.toString((int)price));
        String serverID;
        try {
            serverID = roleInfo.getString("serverID");
        } catch (JSONException e) {
            return;
        }
        payParams.put(EgamePay.PAY_PARAMS_KEY_CP_PARAMS, serverID + "_" + orderID);
        payParams.put(EgamePay.PAY_PARAMS_KEY_TOOLS_DESC, name);
        //payParams.put(EgamePay.PAY_PARAMS_KEY_PRIORITY, "other");//优先第三方支付
        EgamePay.pay(activity, payParams, new EgamePayListener() {
            @Override
            public void paySuccess(Map params) {
                //dialog.setMessage("道具"+params.get(EgamePay.PAY_PARAMS_KEY_TOOLS_PRICE)+"支付成功");
                //dialog.show();
                EgameAgent.onEvent(activity, "payment success");
                payCallBack.onSuccess("支付成功");
            }
            
            @Override
            public void payFailed(Map params, int errorInt) {
                //dialog.setMessage("道具"+params.get(EgamePay.PAY_PARAMS_KEY_TOOLS_PRICE)+"支付失败：错误代码："+errorInt);
                //dialog.show();
                Log.v("sdk", "pay fail:" + errorInt);
                HashMap values= new HashMap();
                values.put("errorInt",""+errorInt);
                EgameAgent.onEvent(activity, "errorInt",values);
                payCallBack.onFail("支付失败");
            }
            
            @Override
            public void payCancel(Map params) {
                //dialog.setMessage("道具"+params.get(EgamePay.PAY_PARAMS_KEY_TOOLS_PRICE)+"支付已取消");
                //dialog.show();
                Log.v("sdk", "pay canceled");
                EgameAgent.onEvent(activity, "payment cancelled");
                payCallBack.onFail("支付取消");
            }
        });
    }
}
