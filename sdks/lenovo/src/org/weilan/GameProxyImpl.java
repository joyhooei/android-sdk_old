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

import com.lenovo.lsf.gamesdk.LenovoGameApi;
import com.lenovo.lsf.gamesdk.LenovoGameApi.IQuitCallback;
import com.lenovo.lsf.gamesdk.LenovoGameApi.GamePayRequest;
import com.lenovo.lsf.gamesdk.LenovoGameApi.IPayResult;

public class GameProxyImpl extends GameProxy{

    private static final int MSG_LOGIN_AUTO_ONEKEY_SUCCESS = 2;
    private static final int MSG_LOGIN_AUTO_ONEKEY_FAILED  = 3;

    private Object mCustomParams;
    /**
     * 功能： 该方法的作用是Handler对传递过来的不同消息，进行不同的处理。 当传递过来的消息是MSG_LOGIN时，则对登录进行相应的处理；
     */
    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case MSG_LOGIN_AUTO_ONEKEY_SUCCESS:
                // 登录成功
                onSdkLoginSuccess((String)msg.obj);
                break;
            case MSG_LOGIN_AUTO_ONEKEY_FAILED:
                // 登录失败
                onSdkLoginFail("");
                break;
            default:
                break;
            }
        };
    };

    private Activity    mCurActivity;
    private PayCallBack mPayCallBack;
    private IPayResult mPayCallResult = new IPayResult() {
        @Override
            public void onPayResult(int resultCode, String signValue,
                    String resultInfo) {// resultInfo = 应用编号&商品编号&外部订单号

                if (LenovoGameApi.PAY_SUCCESS == resultCode) {
                    //支付成功
                    mPayCallBack.onSuccess(resultInfo);
                    Toast.makeText(mCurActivity, "sample:支付成功",Toast.LENGTH_SHORT).show();
                } else if (LenovoGameApi.PAY_CANCEL == resultCode) {
                    mPayCallBack.onFail("return cancel");
                    Toast.makeText(mCurActivity, "sample:取消支付",Toast.LENGTH_SHORT).show();
                    // 取消支付处理，默认采用finish()，请根据需要修改
                    Log.e("cocos2d", "return cancel");
                } else {
                    mPayCallBack.onFail("return Error");
                    Toast.makeText(mCurActivity, "sample:支付失败",Toast.LENGTH_SHORT).show();
                    // 计费失败处理，默认采用finish()，请根据需要修改
                    Log.e("cocos2d", "return Error");
                }

            }
    };

    private ExitCallback  mExitCallback;
    private IQuitCallback mQuitCallback = new IQuitCallback() {

        @Override
        public void onFinished(String data) {
            mExitCallback.onExit();
        }

    };

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    public void applicationInit(Activity activity) {
        // SDK初始化
        LenovoGameApi.doInit(activity,SdkConfig.OPEN_APPID);
        mCustomParams = null;
    }

    public void login(Activity activity,Object customParams) {
        // 登录，customParams透传给回调
        //请不要在回调函数里进行UI操作，如需进行UI操作请使用handler将UI操作抛到主线程
        Log.d("cocos2d", "login start=========================");
        mCustomParams = customParams;

        LenovoGameApi.doAutoLogin(activity, new LenovoGameApi.IAuthResult() {

            @Override
            public void onFinished(boolean ret, String data) {
                if (ret) {
                    final Message msg = new Message();
                    msg.what = MSG_LOGIN_AUTO_ONEKEY_SUCCESS;
                    msg.obj = data;
                    myHandler.sendMessage(msg);

                } else {
                    final Message msg = new Message();
                    msg.what = MSG_LOGIN_AUTO_ONEKEY_FAILED;
                    myHandler.sendMessage(msg);
                }
            }
        });
    }

    public void logout(Activity activity,Object customParams) {
        // 登出，customParams透传给回调
    }


    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        // 支付 ID：商品ID，name：商品名，orderID：CP订单号，price：金额（单位元），callBackInfo：需要透传给服务器回调，roleInfo：角色信息json，payCallBack：支付回调
        /*
         * local roleInfo = {
         *  id = g_player.entityID,
         *  name = g_player.name,
         *  faction = '',
         *  vip = g_player.vip,
         *  level = g_player.level,
         *  serverID = self.server_id,
         *  raw_username = g_sdk_username,
         *}
         */
        /***********
         *  支付LenovoGameApi.doPay（） 接口 调用
         */
        mCurActivity = activity;
        mPayCallBack = payCallBack;
        GamePayRequest payRequest = new GamePayRequest();
        // 请填写商品自己的参数
        payRequest.addParam("notifyurl", "");//当前版本暂时不用，传空String
        payRequest.addParam("appid", SdkConfig.OPEN_APPID);
        payRequest.addParam("waresid", ID);
        payRequest.addParam("exorderno", orderID);
        payRequest.addParam("price", (int)(price * 100));
        payRequest.addParam("cpprivateinfo", callBackInfo);

        LenovoGameApi.doPay(activity, SdkConfig.PAY_APPKEY, payRequest, mPayCallResult); 
    } 

    public void exit(Activity activity, ExitCallback callback) {
        // 点返回键退出，默认调用游戏内的退出界面，如果sdk有要求则重载
        mCurActivity = activity;
        mExitCallback = callback;
        LenovoGameApi.doQuit(activity,  mQuitCallback);
    }

    private void onSdkLoginSuccess(String data)
    {
        Log.d("cocos2d", "login success:" + data);
        User usr = new User();
        usr.token = data;

        userListerner.onLoginSuccess(usr, mCustomParams);
    }

    private void onSdkLoginFail(String reason)
    {
        Log.d("cocos2d", "login failed:" + reason);
        userListerner.onLoginFailed(reason, mCustomParams);
    }

}
