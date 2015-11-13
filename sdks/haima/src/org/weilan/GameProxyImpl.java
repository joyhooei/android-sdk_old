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

import com.haima.loginplugin.callback.OnCheckUpdateListener;
import com.haima.plugin.haima.HMPay;
import com.haima.loginplugin.ZHErrorInfo;
import com.haima.loginplugin.ZHPayUserInfo;
import com.haima.loginplugin.callback.OnCheckTokenListener;
import com.haima.loginplugin.callback.OnCheckUpdateListener;
import com.haima.loginplugin.callback.OnLoginCancelListener;
import com.haima.loginplugin.callback.OnLoginListener;
import com.haima.payPlugin.callback.OnCheckOrderListener;
import com.haima.payPlugin.callback.OnPayCancelListener;
import com.haima.payPlugin.callback.OnPayListener;
import com.haima.payPlugin.infos.ZHPayOrderInfo;
import com.haima.plugin.haima.HMPay;

public class GameProxyImpl extends GameProxy implements OnLoginListener,
        OnCheckOrderListener, OnPayListener, OnCheckUpdateListener,
        OnCheckTokenListener, OnLoginCancelListener, OnPayCancelListener{
    private static String appid = "${APPID}";
    private Activity currentActivity;
    private PayCallBack payCallBack;

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
        currentActivity = activity;
        if (!HMPay.init(activity, false, appid, this,
                    false, HMPay.CHECKUPDATE_FAILED_SHOW_CANCLEANDSURE)) {
                Toast.makeText(activity, "初始化失败，参数不正确",
                        Toast.LENGTH_SHORT).show();
                return;
            }
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        HMPay.onResume(activity);
    }

    @Override
    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        // 取消登陆监听
        HMPay.removeLoginListener(this);
        HMPay.removeLoginCancelListener(this);
        HMPay.removePayCancelListener(this);
    }

    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
        HMPay.onPause();
    }

    public void login(Activity activity,Object customParams) {
        HMPay.login(activity, this);
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        this.payCallBack = payCallBack;
        // 支付
        ZHPayOrderInfo orderInfo = new ZHPayOrderInfo();
        // 商品名称
        orderInfo.goodName = name;
        // 游戏名称
        orderInfo.gameName = "${APPNAME}";
        // 商品价格
        orderInfo.goodPrice = price;
        // 订单号
        orderInfo.orderNo = orderID;
        orderInfo.userParam = callBackInfo;
        HMPay.pay(orderInfo, activity, this);
    }

    @Override
    public void onLoginSuccess(ZHPayUserInfo arg0) {
        User u = new User();
        u.userID = arg0.getUid();
        u.token = arg0.getLoginToken();
        userListerner.onLoginSuccess(u, null);
    }

    @Override
    public void onLoginFailed(ZHErrorInfo arg1) {
        userListerner.onLoginFailed("", null);
    }

    @Override
    public void onLogOut() {
        userListerner.onLogout(null);
    }

    @Override
    public void onCheckOrderFailed(String orderId, ZHErrorInfo errorInfo) {
        Log.v("sdk", "check order failed");
    }

    @Override
    public void onCheckOrderSuccess(String orderId, float money, int status) {
        Log.v("sdk", "check order success");
    }

    @Override
    public void onPaySuccess(ZHPayOrderInfo orderInfo) {
        payCallBack.onSuccess("");
    }

    @Override
    public void onPayFailed(ZHPayOrderInfo orderInfo, ZHErrorInfo errInfo) {
        payCallBack.onFail("");
    }

    @Override
    public void onCheckUpdateSuccess(boolean isNeedUpdate,
                                     boolean isForceUpdate, boolean isTestMode) {
    }

    @Override
    public void onCheckUpdateFailed(ZHErrorInfo errorInfo, int requestType) {
    }

    @Override
    public void onCheckTokenSuccess(boolean isTokenLegal) {
        Log.v("sdk", "check token success");
    }

    @Override
    public void onCheckTokenFailed(ZHErrorInfo errInfo) {
        Log.v("sdk", "check token failed");
    }

    @Override
    public void onLoginCancel() {
        userListerner.onLoginFailed("用户取消", null);
    }

    @Override
    public void onPayCancel() {
        payCallBack.onFail("");
    }
}
