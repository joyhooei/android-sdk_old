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

import com.snowfish.cn.ganga.helper.SFOnlineHelper;
import com.snowfish.cn.ganga.helper.SFOnlineLoginListener;
import com.snowfish.cn.ganga.helper.SFOnlineExitListener;
import com.snowfish.cn.ganga.helper.SFOnlineInitListener;
import com.snowfish.cn.ganga.helper.SFOnlineUser;
import com.snowfish.cn.ganga.helper.SFOnlinePayResultListener;

public class GameProxyImpl extends GameProxy{
    private Object loginCustomParams;

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    public void applicationInit(final Activity activity) {
        SFOnlineHelper.onCreate(activity, new SFOnlineInitListener() {
            @Override
            public void onResponse(String tag, String value) {
                if(tag.equalsIgnoreCase("success")) {
                    //初始化成功的回调
                } else if(tag.equalsIgnoreCase("fail")) {
                    //初始化失败的回调,value:如果SDK返回了失败的原因,会给value赋值
                    Log.v( "sdk", "sdk init failed : " + value );
                }
            }
        });

        // 该方法为登录的监听函数需要在调用 login 函数之前调用,调用用例:
        SFOnlineHelper.setLoginListener(activity, new SFOnlineLoginListener() {
            @Override
            public void onLoginSuccess(SFOnlineUser user, Object customParams) {
                //登陆成功回调
                User u = new User();
                u.channelID = user.getChannelId();
                u.userID = user.getChannelUserId();
                u.token = user.getToken();
                userListerner.onLoginSuccess(u, loginCustomParams);
            }
            @Override
            public void onLoginFailed(String reason, Object customParams) {
                //登陆失败回调
                Toast.makeText(activity, "登陆失败", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLogout(Object customParams) {
                //登出回调
                userListerner.onLogout(null);
            }
        });
    }

    public void onStop(Activity activity) {
		SFOnlineHelper.onStop(activity);
    }

    public void onDestroy(Activity activity) {
		SFOnlineHelper.onDestroy(activity);
    }

    public void onResume(Activity activity) {
		SFOnlineHelper.onResume(activity);
    }

    public void onPause(Activity activity) {
		SFOnlineHelper.onPause(activity);
    }

    public void onRestart(Activity activity) {
		SFOnlineHelper.onRestart(activity);
    }

    public void exit(Activity activity, final ExitCallback callback) {
        SFOnlineHelper.exit(activity, new SFOnlineExitListener() {
            /*  onSDKExit
             *  @description　当SDK有退出方法及界面，回调该函数
             *  @param bool   是否退出标志位  
             */
            public void onSDKExit(boolean bool) {
                if( bool ) {
                    callback.onExit();
                }
            }
            /*  onNoExiterProvide
             *  @description　SDK没有退出方法及界面，回调该函数，可在此使用游戏退出界面
             */
            public void onNoExiterProvide() {
                callback.onNo3rdExiterProvide();
            }
        });
    }

    public void login(Activity activity,Object customParams) {
        // 登录，customParams透传给回调
        loginCustomParams = customParams;
		SFOnlineHelper.login(activity, "Login");
    }

    public void logout(Activity activity,Object customParams) {
        // 登出，customParams透传给回调
        SFOnlineHelper.logout(activity, "LoginOut");
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

            String role_id = roleInfo.optString( "id" );
            String role_name = roleInfo.optString( "name" );
            String role_level = roleInfo.optString( "level" );
            String server_id = roleInfo.optString( "serverID" );
            String server_name = roleInfo.optString( "serverName" );

            SFOnlineHelper.setRoleData( context, role_id, role_name, role_level, server_id, server_name );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void pay(final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        String s = callBackInfo + "_" + orderID.replace('-', '_');
        Log.v("sdk", "pay:" + s);

        SFOnlineHelper.pay( activity, (int)(price * 100), name, 1, s, "${PAY_URL}", new SFOnlinePayResultListener() {
            public void onSuccess(String remain) {
                payCallBack.onSuccess(null);
                Toast.makeText( activity, "支付成功", Toast.LENGTH_SHORT ).show();
            }

            public void onFailed(String remain) {
                payCallBack.onFail(null);
                Toast.makeText( activity, "支付失败 : " + remain, Toast.LENGTH_SHORT ).show();
            }

            public void onOderNo(String orderNo) {
                Toast.makeText( activity, "订单号 : " + orderNo, Toast.LENGTH_SHORT ).show();
            }
        });
    }
}
