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

import com.mappn.sdk.common.utils.BaseUtils;
import com.mappn.sdk.common.utils.ToastUtil;
import com.mappn.sdk.pay.GfanChargeCallback;
import com.mappn.sdk.pay.GfanConfirmOrderCallback;
import com.mappn.sdk.pay.GfanPay;
import com.mappn.sdk.pay.GfanPayCallback;
import com.mappn.sdk.pay.model.Order;
import com.mappn.sdk.statitistics.GfanPayAgent;
import com.mappn.sdk.uc.GfanUCCallback;
import com.mappn.sdk.uc.GfanUCenter;
import com.mappn.sdk.uc.User;
import com.mappn.sdk.uc.util.StringUtil;
import com.mappn.sdk.uc.util.UserUtil;

public class GameProxyImpl extends GameProxy{
    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    public boolean supportLogout() {
        return true;
    }

    public void applicationInit(Activity activity) {
        GfanPay.getInstance(activity.getApplicationContext()).init();
    }

    public void login(Activity activity,final Object customParams) {
        // 登录，customParams透传给回调
        GfanUCenter.login(activity, new GfanUCCallback() {
            @Override
            public void onSuccess(User user, int loginType) {
                Log.v("cocos sdk login", "login success");
                //// 由登录页登录成功
                //if (GfanUCenter.RETURN_TYPE_LOGIN == loginType) {
                //    // TODO登录成功处理
                //    // 由登录页注册成功
                //} else {
                //    // TODO注册成功处理
                //}
                org.yunyue.User yy_user = new org.yunyue.User();
                yy_user.userID = user.getUid() + "";
                yy_user.token = user.getToken();
                userListerner.onLoginSuccess(yy_user, customParams);
            }
            @Override
            public void onError(int loginType) {
                Log.v("cocos sdk login", "login failed");
                // TODO失败处理
            }
        });
    }

    public void logout(Activity activity,Object customParams) {
        // 登出，customParams透传给回调
        userListerner.onLogout(customParams);
        GfanUCenter.logout(activity);
    }

    public void switchAccount(Activity activity,Object customParams) {
        // 切换帐号（目前没用到），customParams透传给回调
    }

    public void pay(final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        int money = (int)(price * 10);
        Order order = new Order(ID, name, money, callBackInfo + "_" + orderID);
        GfanPay.getInstance(activity.getApplicationContext()).pay(order, new GfanPayCallback() {
            @Override
            public void onSuccess(User user, Order order) {
                payCallBack.onSuccess(null);
                //Toast.makeText(activity.getApplicationContext(), "支付成功 user：" + user.getUserName() + "金额：" + order.getMoney(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(User user) {
                payCallBack.onFail(null);
                if (user != null) {
                    //Toast.makeText(activity.getApplicationContext(), "支付失败 user：" + user.getUserName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity.getApplicationContext(), "用户未登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}