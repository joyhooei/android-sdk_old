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

import com.youku.gamesdk.act.YKCallBack;
import com.youku.gamesdk.act.YKCallBackWithContext;
import com.youku.gamesdk.act.YKInit;
import com.youku.gamesdk.act.YKPlatform;
import com.youku.gamesdk.data.Bean;
import com.youku.gamesdk.data.YKGameUser;
import com.youku.gamesdk.data.YKPayBean;

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

    public void applicationInit(final Activity activity) {
        new YKInit(activity).init(new YKCallBack() {
            @Override
            public void onSuccess(Bean bean) {
            }

            @Override
            public void onFailed(String failReason) {
                Toast.makeText(activity, "初始化失败："+failReason, Toast.LENGTH_LONG).show();
                //poem.quitApplication();
            }
        });
    }

    public void exit(Activity activity, ExitCallback callback) {
        YKPlatform.quit(activity, new YKCallBack() {
            @Override
            public void onSuccess(Bean bean) {
                poem.quitApplication();
            }
            @Override
            public void onFailed(String failReason) {
                //针对对话框dismiss的监听回调的方法，此处可以增添取消对话框后的其他处理
            }
        });
    }

    private void startYKFloat(Activity activity){
        //登陆成功后调用悬浮窗接口
        YKPlatform.startYKFloat(activity, new YKCallBackWithContext() {
            @Override
            public void callback(Context context) {
                YKPlatform.logout(context);//注销账号方法
                userListerner.onLogout(null);
            }
        });
        //YKPlatform.startYKFloat(MainActivity.this);//启动无切换账号回调功能的悬浮窗
    }

    public void login(final Activity activity, final Object customParams) {
        YKPlatform.autoLogin(new YKCallBack() {
            @Override
            public void onSuccess(Bean bean) {
                YKGameUser ykGameUser = (YKGameUser) bean;
                User u = new User();
                u.token = ykGameUser.getSession();
                //u.userID = '';
                userListerner.onLoginSuccess(u, customParams);
                startYKFloat(activity);//登陆成功后启动悬浮窗
            }
        @Override
        public void onFailed(String failReason) {
            Toast.makeText(activity, "登录失败" + failReason,
                Toast.LENGTH_LONG).show();
        }
        }, activity);
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        YKPayBean ykPayBean = new YKPayBean();
        ykPayBean.setAmount(String.valueOf((int)price * 100));//金额（以分为单位，只能传整数值，不能有小数）
		ykPayBean.setAppOrderId(orderID);////cp自己生成的订单号，不能为空，不能重复（若是单机游戏没有订单号，则传"defaultapporderid"）
		ykPayBean.setNotifyUri("http://sdk.nataku.yunyuegame.com/sdk/android/sdk/youku/pay_callback");//cp的支付回调通知地址，不能为空，（目前优酷后台不提供设置通知地址的功能）
		ykPayBean.setProductId(ID);//cp的物品ID（没有可以传"0"）
		ykPayBean.setProductName(name);//物品名称（没有就传"游戏道具"）
        ykPayBean.setAppExt1(callBackInfo);//cp透传参数（没有透传参数就注销本行,支持最多64位,不支持中文）

        YKPlatform.doPay(activity, ykPayBean, new YKCallBack(){
            @Override
            public void onSuccess(Bean bean) {
                //Toast.makeText(activity, "操作成功", 2000).show();
                payCallBack.onSuccess("支付成功");
            }
            @Override
            public void onFailed(String failReason) {
                //进行支付失败操作，failReason为失败原因
                //Toast.makeText(PayActivity.this, failReason, 2000).show();
                payCallBack.onFail("支付失败");
            }

        });
    }

    public void logout(Activity activity,Object customParams) {
        YKPlatform.logout(activity);
        userListerner.onLogout(customParams);
    }
}
