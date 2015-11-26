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

import com.anzhi.usercenter.sdk.AnzhiUserCenter;
import com.anzhi.usercenter.sdk.inter.AnzhiCallback;
import com.anzhi.usercenter.sdk.inter.InitSDKCallback;
import com.anzhi.usercenter.sdk.item.CPInfo;
import com.anzhi.usercenter.sdk.item.UserGameInfo;
import com.anzhi.usercenter.sdk.inter.KeybackCall;
import com.anzhi.usercenter.sdk.inter.AzOutGameInter;

import ${PACKAGE_NAME}.R;

public class GameProxyImpl extends GameProxy implements KeybackCall, InitSDKCallback, AnzhiCallback, AzOutGameInter{
    private AnzhiUserCenter mAnzhiCenter;
    private Activity currentActivity;

    private Object loginCustomParams;
    private Object logoutCustomParams;
    private PayCallBack payCallBack;
    private ExitCallback mExitCallback;

    public boolean supportLogin() {
        // 是否支持sdk登录
        return true;
    }

    public boolean supportCommunity() {
        // 是否支持打开社区
        return false;
    }

    public boolean supportPay() {
        // 是否支持支付
        return true;
    }

    @Override
    public void applicationInit(Activity activity) {
        currentActivity = activity;
        initInfo(activity);
    }

    @Override
    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
    }

    @Override
    public void openCommunity(Activity activity) {
        //mAnzhiCenter.viewUserInfo(activity);
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

    public void exit(Activity activity, ExitCallback callback) {
        // 点返回键退出，默认调用游戏内的退出界面，如果sdk有要求则重载
        /**
         * 1、调用本方法或触发退出逻辑 2、在账号登录的情况下，首次调用本方法或展示后台配置的推广位，如果没有则弹Toast 3、三秒内重复调用本方法会发出退出游戏的通知回调。
         */
        mExitCallback = callback;
        mAnzhiCenter.azoutGame(true);
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        this.payCallBack = payCallBack;
        String s = callBackInfo + "_" + orderID.replace('-', '_');
        Log.v("sdk", "pay:" + s);
        mAnzhiCenter.pay(activity, 0, price, name, s);
    }

    public void setExtData(Context context, String ext) {
        // 上报角色数据给sdk，ext:json数据，格式如下
        /* local info = {
         *     state = send_type,                   -- type
         *     id = roleId,                      -- roleId
         *     name = roleName,                    -- roleName
         *     level = roleLevel,                   -- roleLevel
         *     serverID = zoneId,                      -- zoneId
         *     serverName = zoneName,                    -- zoneName
         *     gold = balance,                     -- balance
         *     vip = vip,                         -- vip 
         *     factionName = partyName                    -- partyName 
         * }
         */
        try {
            JSONObject roleInfo = new JSONObject(ext);

            UserGameInfo gameInfo = new UserGameInfo();
            gameInfo.setNickName(roleInfo.optString("roleName"));
            gameInfo.setUid(roleInfo.optString("id"));
            gameInfo.setAppName("${APPNAME}");
            gameInfo.setGameArea(roleInfo.optString("serverID"));// 游戏的服务器区
            gameInfo.setGameLevel(roleInfo.optString("level"));// 游戏角色等级
            gameInfo.setUserRole(roleInfo.optString("name"));// 角色名称
            gameInfo.setMemo("");// 备注
            mAnzhiCenter.submitGameInfo(context, gameInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * 初始化SDK信息
     */
    private void initInfo(Activity activity) {
        final CPInfo info = new CPInfo();
        info.setOpenOfficialLogin(false);// 是否开启游戏官方账号登录，默认为关闭
        info.setAppKey("${APPKEY}");
        info.setSecret("${APPSECRET}");
        info.setChannel("AnZhi");
        info.setGameName(activity.getResources().getString(R.string.app_name));
        mAnzhiCenter = AnzhiUserCenter.getInstance();
        mAnzhiCenter.azinitSDK(activity, info, this, this);       // 初始化方法
        //mAnzhiCenter.setOpendTestLog(true);                     // 调试log，开关
        mAnzhiCenter.setKeybackCall(this);                  //设置返回游戏的接口
        mAnzhiCenter.setCallback(this);                     //设置登录、登出、支付通知
        mAnzhiCenter.setActivityOrientation(1);                 // 0横屏,1竖屏,4根据物理感应来选择方向

        mAnzhiCenter.showFloaticon();
    }

    // 登录、登出、支付回调接口
    // AnzhiCallback
    @Override
    public void onCallback(CPInfo cpInfo, String result) {
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

    // KeybackCall
    @Override
    public void KeybackCall(String st) {
        //Log.e(TAG, "st===========" + st);// 根据st来判断返回页面，st可能为空
    }

    // InitSDKCallback
    @Override
    public void initSdkCallcack() {
        //mAnzhiCenter.login(GameActivity.this, true);
    }

    //AzOutGameInter
    @Override
    public void azOutGameInter(int arg) {

        switch (arg) {
            case AzOutGameInter.KEY_OUT_GAME:// 退出游戏
                mAnzhiCenter.removeFloaticon(currentActivity);
                if( mExitCallback != null )
                    mExitCallback.onExit();
                break;
            case AzOutGameInter.KEY_CANCEL: // 取消退出游戏
                Log.e("xugh", "-----");
                break;
            default:
                break;
        }
    }
}
