package org.weilan;

import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.cyjh.pay.constants.PayConstants;
import com.cyjh.pay.model.ScreenType;
import com.cyjh.pay.util.LogUtil;
import com.kaopu.supersdk.api.KPSuperSDK;
import com.kaopu.supersdk.callback.KPAuthCallBack;
import com.kaopu.supersdk.callback.KPGetCheckUrlCallBack;
import com.kaopu.supersdk.callback.KPLoginCallBack;
import com.kaopu.supersdk.callback.KPLogoutCallBack;
import com.kaopu.supersdk.callback.KPPayCallBack;
import com.kaopu.supersdk.model.UserInfo;
import com.kaopu.supersdk.model.params.PayParams;

public class GameProxyImpl extends GameProxy{
    private Activity curActivity;
    private Object   mCustomParams = null;
    private PayCallBack mPayCallback = null;

    public static String getSystemProperty(String propName){
        String line;
        BufferedReader input = null;
        try
        {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        }
        catch (IOException ex)
        {
            Log.e("cocos", "Unable to read sysprop " + propName, ex);
            return null;
        }
        finally
        {
            if(input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    Log.e("cocos", "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }

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
        // 若在此时传入配置数据，则会相对应的覆盖 kaopu_game_config.json 中的数据
        final HashMap<String, String> configData = new HashMap<String, String>();
        // configData.put("KP_Channel ", "kaopu");// 请不要在这里put此参数.即使put 了，也是读取kaopu_game_config.json中的参数值
        // configData.put("ChannelKey", "k  aopu"); // 请不要在这里put此参数.即使put 了，也是读取kaopu_game_config.json中的参数值
        configData.put("gameName", "${APPNAME}");  // 游戏名 : 将在支付页面中显示, 这里设置的话，将会覆盖kaopu_game_config.json中的参数值
        //configData.put("screenType", "2");     // 屏幕方向   横屏：1  竖屏：2, 这里设置的话，将会覆盖kaopu_game_config.json中的参数值
        //configData.put("fullScreen", "true");  // 是否全屏（全屏即: 不带状态栏） 全屏：true  不全屏：false, 这里设置的话，将会覆盖kaopu_game_config.json中的参数值
        //configData.put("param", "");           // 自定义参数,这里设置的话，将会覆盖kaopu_game_config.json中的参数值
        KPSuperSDK.auth(activity, configData, new KPAuthCallBack() {
            @Override
            public void onAuthSuccess() {
                LogUtil.out("授权成功");
                // 注册注销账号时的回调,在授权成功之后，用户注销账号前调用都可以
                KPSuperSDK.registerLogoutCallBack(new KPLogoutCallBack() {
                    @Override
                    public void onSwitch() {
                        // 在这里实现切换账号的逻辑
                        userListerner.onLogout(null);
                    }

                    @Override
                    public void onLogout() {
                        // 在这里实现注销账号的逻辑
                        userListerner.onLogout(mCustomParams);
                    }

                });

                if(getSystemProperty("ro.miui.ui.version.name") != null){
                    // 是miui
                    KPSuperSDK.startGuide(curActivity);
                }
            }

            @Override
            public void onAuthFailed() {
                LogUtil.out("授权失败");
            }
        });
    }

    public void login(Activity activity,Object customParams) {
        mCustomParams = customParams;
        // 登录，customParams透传给回调
        KPSuperSDK.login(activity, new KPLoginCallBack() {
            @Override
            public void onLoginSuccess(UserInfo info) {
                User usr = new User();
                usr.token   = info.getToken();
                usr.userID  = info.getOpenid();
                usr.username = info.getTag();
                userListerner.onLoginSuccess(usr, mCustomParams);
            }

        @Override
            public void onLoginFailed() {
                userListerner.onLoginFailed("登录失败",mCustomParams);
            }

        @Override
            public void onLoginCanceled() {
                userListerner.onLoginFailed("登录取消",mCustomParams);
            }
        });
    }

    public void logout(Activity activity,Object customParams) {
        // 登出，customParams透传给回调
        mCustomParams = customParams;
        KPSuperSDK.logoutAccount();
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
        mPayCallback = payCallBack;
        PayParams payParams = new PayParams();
        payParams.setAmount((double)price); // 充值金额
        payParams.setGamename(KPSuperSDK.getGameName()); // 充值游戏
        payParams.setGameserver(roleInfo.optString("serverID")); // 充值游戏服务器
        payParams.setRolename(roleInfo.optString("name")); // 充值角色名称
        // 因为没有回传数据故把回传信息放在订单号之前
        orderID = callBackInfo + "_" + orderID;
        payParams.setOrderid(orderID); // 唯一订单号

        // 创建订单配置
        payParams.setCurrencyname("钻石"); // 虚拟货币名称
        payParams.setProportion(10); // RMB和虚拟货币的比,例:10表示比例为1RMB:10虚拟货币
        payParams.setCustomPrice(true);// 这个设置为true， setCustomText才会生效
        payParams.setCustomText(price + "元购买" + name);
        /**
         * 支付接口,需要同时提供支付和登录的回调接口,若用户没用登录,将直接跳转至登录界面 如果不使用回调,传null即可
         */
        KPSuperSDK.pay(activity, payParams, new KPPayCallBack() {
            @Override
            public void onPaySuccess() {
                mPayCallback.onSuccess("充值成功");
            }

        @Override
            public void onPayFailed() {
                mPayCallback.onFail("充值失败");
            }

        @Override
            public void onPayCancle() {
                mPayCallback.onFail("退出支付");
            }
        });
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
            int roleLevel = roleInfo.optInt("level");// 角色等级
            String serviceName = roleInfo.optString("serverName"); // 服务器名称
            String roleName = roleInfo.optString("name"); // 角色名称
            String id = roleInfo.optString("id");// 游戏方对外用户 ID，即：cp自身用户的userid（不可为空）
            KPSuperSDK.setUserGameRole(context, serviceName, roleName, id, roleLevel);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
    }

    @Override
    public void onStop(Activity activity) {
        super.onStop(activity);
        KPSuperSDK.closeFloatView(activity);
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        KPSuperSDK.showFloatView(activity);
    }

    @Override
    public void onDestroy(Activity activity) {
        KPSuperSDK.release();
        super.onDestroy(activity);
    }

}
