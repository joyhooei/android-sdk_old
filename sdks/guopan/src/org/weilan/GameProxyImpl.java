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

import com.flamingo.sdk.access.GPApiFactory;
import com.flamingo.sdk.access.GPExitResult;
import com.flamingo.sdk.access.GPPayResult;
import com.flamingo.sdk.access.GPSDKGamePayment;
import com.flamingo.sdk.access.GPSDKInitResult;
import com.flamingo.sdk.access.GPUserResult;
import com.flamingo.sdk.access.IGPExitObsv;
import com.flamingo.sdk.access.IGPPayObsv;
import com.flamingo.sdk.access.IGPSDKInitObsv;
import com.flamingo.sdk.access.IGPUserObsv;

public class GameProxyImpl extends GameProxy{
    private Activity curActivity = null;

    private ExitCallback mExitCallback = null;

    private PayCallBack mPayCallBack = null;

    private Object mCustomParams = null;

    private int initCout = 0;
    private boolean mIsInitSuc = false;
    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    private void tryInitSdk() {
        if(initCout++ > 3) {
            Log.e("cocos","初始化失败，请检查网络");
            return;
        }

        Log.v("cocos","初始化失败，进行第" + initCout + "次初始化重试");
        GPApiFactory.getGPApi().initSdk(curActivity, "${APPID}", "${APPKEY}", mInitObsv);
    }

    /**
     * 初始化回调接口
     */
    private IGPSDKInitObsv mInitObsv = new IGPSDKInitObsv() {
        @Override
        public void onInitFinish(GPSDKInitResult initResult) {
            switch (initResult.mInitErrCode) {
                case GPSDKInitResult.GPInitErrorCodeConfig:
                    Log.e("cocos","初始化回调:初始化配置错误");
                    tryInitSdk();
                    break;
                case GPSDKInitResult.GPInitErrorCodeNeedUpdate:
                    Log.e("cocos","初始化回调:游戏需要更新");
                    break;
                case GPSDKInitResult.GPInitErrorCodeNet:
                    Log.e("cocos","初始化回调:初始化网络错误");
                    tryInitSdk();
                    break;
                case GPSDKInitResult.GPInitErrorCodeNone:
                    mIsInitSuc = true;
                    break;
            }
        }
    };


    public void applicationInit(Activity activity) {
        // SDK初始化
        curActivity = activity;
        tryInitSdk();
    }

    /**
     * 登录回调接口
     */
    private IGPUserObsv mUserObsv = new IGPUserObsv() {
        @Override
        public void onFinish(final GPUserResult result) {
            switch (result.mErrCode) {
                case GPUserResult.USER_RESULT_LOGIN_FAIL:
                    Log.e("cocos","登录回调:登录失败");
                    break;
                case GPUserResult.USER_RESULT_LOGIN_SUCC:
                    {
                        String uid   = GPApiFactory.getGPApi().getLoginUin();
                        String token = GPApiFactory.getGPApi().getLoginToken();
                        User usr = new User();
                        usr.userID = uid;
                        usr.token  = token;

                        userListerner.onLoginSuccess(usr, mCustomParams);
                    }
                    break;
            }
        }
    };

    public void login(Activity activity,Object customParams) {
        // 登录，customParams透传给回调
        mCustomParams = customParams;
        GPApiFactory.getGPApi().login(activity.getApplication(), mUserObsv);
    }

    public void logout(Activity activity,Object customParams) {
        // 登出，customParams透传给回调
        // TODO  sdk logout是个异步接口而没有回调
        GPApiFactory.getGPApi().logout();
        userListerner.onLogout(customParams);
    }

    /**
     * 支付回调接口
     */
    private IGPPayObsv mPayObsv = new IGPPayObsv() {
        @Override
        public void onPayFinish(GPPayResult payResult) {
            if (payResult == null) {
                if( mPayCallBack != null )
                    mPayCallBack.onFail("");
                return;
            }
            mPayCallBack.onSuccess("");
        }
    };

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

        GPSDKGamePayment payParam = new GPSDKGamePayment();
        payParam.mItemName   = name;
        payParam.mPaymentDes = name;
        payParam.mItemPrice  = price;         // 订单的价格（以元为单位）
        payParam.mItemCount  = 1; // 数量
        payParam.mCurrentActivity = activity;// 用户当前的activity
        payParam.mSerialNumber = orderID;// 订单号，这里用时间代替（用户需填写订单的订单号）
        payParam.mItemId      = ID;// 商品编号ID
        payParam.mReserved = callBackInfo;// 透传字段

        GPApiFactory.getGPApi().buy(payParam, mPayObsv);
    }

    /**
     * 退出界面回调接口
     */
    private IGPExitObsv mExitObsv = new IGPExitObsv() {
        @Override
        public void onExitFinish(GPExitResult exitResult) {
            switch (exitResult.mResultCode) {
                case GPExitResult.GPSDKExitResultCodeError:
                    Log.e("cocos","退出回调:调用退出弹框失败");
                    break;
                case GPExitResult.GPSDKExitResultCodeExitGame:
                    Log.d("cocos","退出回调:调用退出游戏，请执行退出逻辑");
                    if( mExitCallback != null )
                        mExitCallback.onExit();
                    break;
                case GPExitResult.GPSDKExitResultCodeCloseWindow:
                    Log.e("cocos","退出回调:调用关闭退出弹框");
                    break;
            }
        }
    };

    public void exit(Activity activity, ExitCallback callback) {
        // 点返回键退出，默认调用游戏内的退出界面，如果sdk有要求则重载
        mExitCallback = callback;
        GPApiFactory.getGPApi().exit(mExitObsv);
    }

}
