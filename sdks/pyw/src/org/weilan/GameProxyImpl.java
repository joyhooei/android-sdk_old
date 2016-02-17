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

import com.pengyouwan.sdk.api.OrientationInfo;
import com.pengyouwan.sdk.api.SDKConfig;
import com.pengyouwan.sdk.utils.FloatViewTool;
import com.pengyouwan.sdk.api.ISDKEventCode;
import com.pengyouwan.sdk.api.ISDKEventExtraKey;
import com.pengyouwan.sdk.api.OnSDKEventListener;
import com.pengyouwan.sdk.api.PYWPlatform;
import com.pengyouwan.sdk.api.User;

public class GameProxyImpl extends GameProxy{
    private Activity currentActivity;
    private Object loginCustomParams;
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

    private void onSDKEvent(int eventCode, Bundle data){
        switch (eventCode) {
            case ISDKEventCode.CODE_LOGIN_SUCCESS:
                // 登录成功通知，bundle中会带有user信息
                if (data != null) {
                    User user = (User) data.getSerializable(ISDKEventExtraKey.EXTRA_USER);
                    if (user != null) {
                        // show float view
                        FloatViewTool.instance(activity).showFloatView();
                        org.weilan.User usr = new org.weilan.User();
                        usr.token  = user.getToken();
                        usr.userID = user.getUserId(); // 朋友玩为用户分配的唯一标识;
                        userListerner.onLoginSuccess(usr, loginCustomParams);
                    }
                }
                break;
            case ISDKEventCode.CODE_BACK_TO_GAME:
                break;
            case ISDKEventCode.CODE_LOGIN_FAILD:
                String erroMsg = data.getString(ISDKEventExtraKey.EXTRA_ERRO);
                userListerner.onLoginFailed(erroMsg, loginCustomParams);
                break;
            case ISDKEventCode.CODE_CHARGE_SUCCESS:
                // 充值成功
                payCallBack.onSuccess("充值成功:" + data.getString(ISDKEventExtraKey.EXTRA_ORDERID));
                break;
            case ISDKEventCode.CODE_CHARGE_FAIL:
                // 充值失败
                payCallBack.onFail("充值失败:" + data.getString(ISDKEventExtraKey.EXTRA_ORDERID));
                break;
            case ISDKEventCode.CODE_CHANGE_ACCOUNT_SUCCESS:
                // 切换账号成功(原本已经处于登录状态，成功更换登录账号后触发)
                if (data != null) {
                    User user = (User) data.getSerializable(ISDKEventExtraKey.EXTRA_USER);
                    if (user != null) {
                        //userListerner.onLogout(null);
                        // show float view
                        FloatViewTool.instance(activity).showFloatView();
                        org.weilan.User usr = new org.weilan.User();
                        usr.token  = user.getToken();
                        usr.userID = user.getUserId(); // 朋友玩为用户分配的唯一标识;
                        userListerner.onLoginSuccess(usr, loginCustomParams);
                    }
                }
                break;
        }
    }

    @Override
    public void applicationInit(Activity activity) {
        SDKConfig sdkconfig = new SDKConfig();
        /** 必填**，请替换成朋友玩提供的GameKey,此处为测试值 **/
        sdkconfig.setGameKey("${APPID}");

        /******************** 非必填项 ****************************/
        // SDK横竖屏配置
        // 横竖屏常量声明: OrientationInfo.PORTRAIT(竖屏)
        // OrientationInfo.LANDSCAPE(横屏)
        // 默认值-OrientationInfo.LANDSCAPE(横屏)
        sdkconfig.setActivityOrientation(OrientationInfo.PORTRAIT);

        // 是否开启切换账号功能
        // true-允许切换 false-不支持切换
        // 若不配置，默认为true-支持
        sdkconfig.allowChangeAccount(false);

        // 切换账号时是否需要重启游戏
        // true-重启 false-不重启
        // 若不配置，默认为false-不重启
        sdkconfig.setRebootOnChangeAccount(false);

        PYWPlatform.initSDK(activity, sdkconfig, new OnSDKEventListener(){
            @Override
            public void onEvent(int eventCode, Bundle data){
                onSDKEvent(eventCode, data);
            }
        });
    }


    @Override
    public void login(Activity activity,Object customParams) {
        // 调用SDK执行登陆操作
        Log.v("sdk", "login");
        currentActivity = activity;
        loginCustomParams = customParams;

        PYWPlatform.openLogin(activity);
    }

    @Override
    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        currentActivity = activity;
        this.payCallBack = payCallBack;

        // 打开充值中心,money为充值金额
        JSONObject jobj = new JSONObject();
        try {
            // 订单id，此项必须要填写，并且参数名必须为"order_id"，否则会出错
            jobj.put("order_id", orderID);
            // 产品id，此项必须要填写，并且参数名必须为"product_id"，否则会出错(提示:商品id需要是整数值)
            // ：如果是任意充可以不填
            //jobj.put("product_id", 9); // 6660
            // 以下为非必要参数，只供参考，厂商可根据自身需求决定传什么参数与值或者不传
            jobj.put("area_num", callBackInfo);
            // ……
            jobj.put("product_desc", name);
        } catch (JSONException e) {
            e.printStackTrace();
            payCallBack.onFail("订单信息生成失败");
            return;
        }

        // 厂商需要朋友玩回调时回传的参数，届时会原样返回
        String jsonParam = jobj.toString();

        PYWPlatform.openChargeCenter(activity, price, (int) (price * 10), jsonParam, true);
    }

    @Override
    public void onResume(Activity activity){
        super.onResume(activity);
        if (null != PYWPlatform.getCurrentUser()) { // 表示当前登录状态
            FloatViewTool.instance(activity).showFloatView();
        }
    }

    @Override
    public void onPause(Activity activity){
        FloatViewTool.instance(activity).hideFloatView();
        super.onPause(activity);
    }

    @Override
    public void onDestroy(Activity activity) {
        /**
         * 释放资源，必须调用，true-结束游戏进程，false-不处理游戏进程
         * 注意：当此方法传入true时，同时又在onDestroy()方法内调用此方法，
         * 请确保此Activity在Mainifest中配置了android:configChanges=
         * "keyboardHidden|orientation|screenSize"
         * 否则当界面进行横竖屏切换时会导致onDestroy()被多次调用，进而使PYWPlatform.release杀死游戏进程，
         * 导致游戏界面无法正常开启
         */
        PYWPlatform.release(false);
        super.onDestroy(activity);
    }

}
