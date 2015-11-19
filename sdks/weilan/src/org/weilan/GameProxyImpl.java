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

import com.zz.sdk.LoginCallbackInfo;
import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.ParamChain;
import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.SDKDIY;
import com.zz.sdk.SDKManager;

public class GameProxyImpl extends GameProxy{
    private static final boolean sb_AutoGetUserInfo = false;

    private PayCallBack mPayCallBack;
    private Object      mCustomParam;

    /* 自定义消息 */
    private static final int _MSG_USER_ = 2013;
    private static final int MSG_LOGIN_CALLBACK = _MSG_USER_ + 1;
    private static final int MSG_PAYMENT_CALLBACK = _MSG_USER_ + 2;
    private static final int MSG_ORDER_CALLBACK = _MSG_USER_ + 3;

    private SDKManager mSDKManager;

    private Handler mSdkHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                /*
                case MSG_LOGIN_CALLBACK: {
                    if (msg.arg1 == MSG_TYPE.LOGIN) {
                        if (msg.arg2 == MSG_STATUS.SUCCESS) {
                            if (msg.obj instanceof LoginCallbackInfo) {
                                LoginCallbackInfo info = (LoginCallbackInfo) msg.obj;
                                String tip = " - 登录成功, 用户名:"
                                        + String.valueOf(mSDKManager.getAccountName())
                                        + "，游戏用户:"
                                        + String.valueOf(mSDKManager.getGameUserName())
                                        + ", \n\t详细信息: " + info.toString();
                                Log.d("cocos2dx",tip);
                                onSdkLoginSuccess( info );
                            } else {
                                // 设计上这里是不可能到达的
                                onSdkLoginFail(" ! 登录成功，但没有用户数据");
                            }
                        } else if (msg.arg2 == MSG_STATUS.CANCEL) {
                            onSdkLoginFail(" - 用户取消了登录.");
                        } else if (msg.arg2 == MSG_STATUS.EXIT_SDK) {
                            onSdkLoginFail(" - 登录业务结束。");
                        } else {
                            onSdkLoginFail(" ! 未知登录结果，请检查：s=" + msg.arg2 + " info:" + msg.obj);
                        }
                    } else {
                        Log.d("cocos2dx"," # 未知类型 t=" + msg.arg1 + " s=" + msg.arg2 + " info:" + msg.obj);
                    }
                }
                break;
                */

                case MSG_PAYMENT_CALLBACK: {
                    if (msg.arg1 == MSG_TYPE.PAYMENT) {
                        PaymentCallbackInfo info; // 支付信息
                        if (msg.obj instanceof PaymentCallbackInfo) {
                            info = (PaymentCallbackInfo) msg.obj;
                        } else {
                            info = null;
                        }

                        String tip = "";
                        if (msg.arg2 == MSG_STATUS.SUCCESS) {
                            tip = " - 充值成功, 详细信息: " + (info == null ? "未知" : info);
                            onSdkPaySuccess( tip );
                        } else if (msg.arg2 == MSG_STATUS.FAILED) {
                            tip = " ! 充值失败, 详细信息: " + (info == null ? "未知" : info);
                            onSdkPayFail( tip );
                        } else if (msg.arg2 == MSG_STATUS.CANCEL) {
                            tip = " - 充值取消, 详细信息: " + (info == null ? "未知" : info);
                            onSdkPayFail( tip );
                        } else if (msg.arg2 == MSG_STATUS.EXIT_SDK) {
                            tip = " ! 充值业务结束。";
                            onSdkPayFail( tip );
                        } else {
                            tip = " ! 未知登录结果，请检查：s=" + msg.arg2 + " info:" + msg.obj;
                            onSdkPayFail( tip );
                        }
                    } else {
                        Log.d("cocos2dx"," # 未知类型 t=" + msg.arg1 + " s=" + msg.arg2
                                        + " info:" + msg.obj
                        );
                    }

                }
                break;
            }
        }
    };

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
        // SDK初始化
        WLSdkConfig.initParam(activity);

        mSDKManager = SDKManager.getInstance(activity);

        // 静默登陆
        WLSdkConfig.setAutoGetUserInfo( true );
        mSDKManager.setConfigInfo( false, false ,false );
    }

    /*
    public void login(Activity activity,Object customParams) {
        // 登录，customParams透传给回调
        mCustomParam = customParams;

        WLSdkConfig.setAutoGetUserInfo( sb_AutoGetUserInfo );

        mSDKManager.showLoginView(mSdkHandler, MSG_LOGIN_CALLBACK, true);
    }

    public void logout(Activity activity,Object customParams) {
        // 登出，customParams透传给回调
        mCustomParam = customParams;

        WLSdkConfig.setAutoGetUserInfo( sb_AutoGetUserInfo );

        mSDKManager.showLoginView(mSdkHandler, MSG_LOGIN_CALLBACK, false);
    }
    */

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
        mPayCallBack = payCallBack;

        String serverId = mSDKManager.getGameServerId(activity);

        // 当前只测试花费人民币
        boolean isZyCoin = false;

        // 充值成功后是否自动关闭
        boolean isCloseWindow = true;

        // 设置模式，true表示购买支付，false表示充值卓越币到个人账户
        boolean isBuyMode = true;

        String extendStr = roleInfo.optString("serverID") + "_" + roleInfo.optString("id") + "_" + orderID;
        Log.d("cocos2dx", "extendStr = " + extendStr);
        mSDKManager.showPaymentView(mSdkHandler, MSG_PAYMENT_CALLBACK, serverId, extendStr, (int)(price * 100), isZyCoin, isCloseWindow, isBuyMode, null, name);
    }

    /*
    private void onSdkLoginSuccess(LoginCallbackInfo info)
    {
        User usr = new User();
        if( sb_AutoGetUserInfo )
        {
            usr.token = info.accessToken;
        }
        else
        {
            usr.token = info.authCode;
        }
        userListerner.onLoginSuccess( usr, mCustomParam );
    }

    private void onSdkLoginFail(String reason)
    {
        userListerner.onLoginFailed( reason, mCustomParam );
    }
    */

    private void onSdkPaySuccess(String paymentInfo)
    {
        if( mPayCallBack != null )
            mPayCallBack.onSuccess(paymentInfo);
    }

    private void onSdkPayFail(String reason)
    {
        if( mPayCallBack != null )
            mPayCallBack.onFail(reason);
    }

}
