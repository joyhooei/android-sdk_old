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

import com.anzhi.usercenter.sdk.AnzhiUserCenter;
import com.anzhi.usercenter.sdk.inter.AnzhiCallback;
import com.anzhi.usercenter.sdk.inter.InitSDKCallback;
import com.anzhi.usercenter.sdk.item.CPInfo;
import com.anzhi.usercenter.sdk.item.UserGameInfo;
import com.anzhi.usercenter.sdk.inter.KeybackCall;

import com.yunyue.nzgl.anzhi.R;

public class GameProxyImpl extends GameProxy implements KeybackCall, InitSDKCallback, AnzhiCallback{
    private AnzhiUserCenter mAnzhiCenter;
    private Activity currentActivity;

    private Object loginCustomParams;
    private Object logoutCustomParams;
    private PayCallBack payCallBack;

    private String TAG = "sdk";

    @Override
    public void applicationInit(Activity activity) {
        Log.v("sdk", "applicationInit");
        currentActivity = activity;
        initInfo(activity);
    }

    @Override
    public void onCreate(Activity activity) {
        Log.v("sdk", "onCreate");
    }

    @Override
    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        mAnzhiCenter.gameOver(activity);// 销毁悬浮球，在退出需要调用
    }

    @Override
    public void openCommunity(Activity activity) {
        mAnzhiCenter.viewUserInfo(activity);
    }

    @Override
    public void login(Activity activity,Object customParams) {
        loginCustomParams = customParams;
        mAnzhiCenter.login(activity, true);
    }

    @Override
    public void logout(Activity activity,Object customParams) {
        logoutCustomParams = customParams;
        mAnzhiCenter.logout(activity);
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        this.payCallBack = payCallBack;
        String s = callBackInfo + "_" + orderID.replace('-', '_');
        Log.v("sdk", "pay:" + s);
        mAnzhiCenter.pay(activity, 0, price, name, s);
    }

    /*
     * 初始化SDK信息
     */
    private void initInfo(Activity activity) {
        final CPInfo info = new CPInfo();
        // info.setOpenOfficialLogin(false);// 是否开启游戏官方账号登录，默认为关闭
        info.setAppKey("${APPKEY}");
        info.setSecret("${APPSECRET}");
        info.setChannel("AnZhi");
        info.setGameName(activity.getResources().getString(R.string.app_name));
        mAnzhiCenter = AnzhiUserCenter.getInstance();
        mAnzhiCenter.azinitSDK(activity, info, this);       // 初始化方法
        mAnzhiCenter.setOpendTestLog(true);                     // 调试log，开关
        mAnzhiCenter.setKeybackCall(this);                  //设置返回游戏的接口
        mAnzhiCenter.setCallback(this);                     //设置登录、登出、支付通知
        mAnzhiCenter.setActivityOrientation(1);                 // 0横屏,1竖屏,4根据物理感应来选择方向
    }

    // 登录、登出、支付回调接口
    @Override
    public void onCallback(CPInfo cpInfo, String result) {
        Log.e(TAG, "result " + result);
        try {
            JSONObject json = new JSONObject(result);
            String key = json.optString("callback_key");
            if ("key_pay".equals(key)) {
                payCallBack.onSuccess("");
            } else if ("key_logout".equals(key)) {
                userListerner.onLogout(logoutCustomParams);
            } else if ("key_login".equals(key)) {
                int code = json.optInt("code");
                String sid = json.optString("sid");
                String uid = json.optString("uid");//uid为安置账号唯一标示
                User u = new User();
                u.userID = uid;
                u.token = sid;
                userListerner.onLoginSuccess(u, loginCustomParams);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //
    @Override
    public void KeybackCall(String st) {
        //Log.e(TAG, "st===========" + st);// 根据st来判断返回页面，st可能为空
    }

    // 初始化接口所需实现的方法，SDK初始化之后回调此方法，在此方法中可以调用登录方法，完成自动登录的流程；
    @Override
    public void ininSdkCallcack() {
        //mAnzhiCenter.login(this, true);// 登录方法，第二个参数暂时不起任何作用，传true 即可
    }

}
