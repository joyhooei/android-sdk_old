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

import com.sijiu7.common.ApiListenerInfo;
import com.sijiu7.common.InitListener;
import com.sijiu7.common.LoginMessageInfo;
import com.sijiu7.common.Sjyx;
import com.sijiu7.common.UserApiListenerInfo;
import com.sijiu7.pay.SjyxPaymentInfo;
import com.sijiu7.user.LoginInfo;

public class GameProxyImpl extends GameProxy{
	public static final String APP_ID = "${APP_ID}";
	public static final String APP_KEY = "${APP_KEY}";
	public static final String APP_AGENT = "${APP_AGENT}";
    // TODO server_id
	public static final String server_id = "";
	private static final String oritation = "portrait";// 控制界面横竖屏

    private Object loginCustomParams;
    private String m_uid;

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    // public void onCreate(Activity activity) {
    public void applicationInit(final Activity activity) {
        // 以下为activity生命周期，有些sdk会要求在里面加入调用。

        // 闪屏接口
        Sjyx.startWelcomanie(activity);
        // onCreate
        Sjyx.applicationInit(activity);
        // ...
        // init
        Sjyx.initInterface(activity, Integer.parseInt(APP_ID), APP_KEY, APP_AGENT, true,
            new InitListener() {
                @Override
                public void fail(String msg) {
                    System.out.println("-----msg:" + msg);
                    // Toast.makeText(activity, msg,
                        // Toast.LENGTH_LONG).show();
                }

                @Override
                public void Success(String msg) {
                    // Success/update
                    System.out.println("-----msg:" + msg);
                    // Toast.makeText(activity, msg,
                        // Toast.LENGTH_LONG).show();
                }
            });

        Sjyx.setUserListener(new UserApiListenerInfo() {
            @Override
            public void onLogout(Object obj) {
                super.onLogout(obj);
                userListerner.onLogout(null, new UserListener.onUserLogoutListener() {
                    public void onLogoutCompleted() {
                        login(activity, null);
                    }
                });
            }
        });
    }

    public void login(Activity activity,Object customParams) {
        loginCustomParams = customParams;
        // 登录，customParams透传给回调

        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setAppid(Integer.parseInt(APP_ID));// 游戏appid
        loginInfo.setAppkey(APP_KEY);// 游戏appkey
        loginInfo.setAgent(APP_AGENT);// 渠道号
        loginInfo.setServer_id(server_id);// 服务器id(游戏区服)
        loginInfo.setOritation(oritation);// 横竖屏控制
        Sjyx.login(activity, loginInfo, new ApiListenerInfo() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj != null) {
                    LoginMessageInfo data = (LoginMessageInfo) obj;
                    String result = data.getResult();
                    String msg = data.getMessage();
                    String userName = data.getUserName();
                    String uid = data.getUid();
                    String timeStamp = data.getTimestamp();
                    String sign = data.getSign();
                    String token = data.getToken();

                    m_uid = uid;

                    // listener
                    if (result.equals("success")) {
                        User u = new User();
                        u.userID = uid;
                        u.token = token + "_" + timeStamp;

                        userListerner.onLoginSuccess(u, loginCustomParams);
                    } else {
                        userListerner.onLoginFailed("get access_token failed!", loginCustomParams);
                    }

                    Log.i("kk", "登陆结果" + "result:" + result + "|msg:" + msg
                        + "|username:" + userName + "|uid:" + uid
                            + "|timeStamp:" + timeStamp + "|sign:" + sign
                            + "|token" + token);
                }
            }
        });
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

        String ext = "";
        try {
            ext = roleInfo.getString("serverID") + "_" + roleInfo.getString("id") + "_" + orderID;
        } catch (JSONException e) {
            Log.e("sdk", "roleInfo parse failed, ignore");
        }

        SjyxPaymentInfo payInfo = new SjyxPaymentInfo();
        try {
            String server_id  = roleInfo.getString("serverID");
            String role_name  = roleInfo.getString("name");
            String role_level = roleInfo.getString("level");
            String role_id    = roleInfo.getString("id");

            payInfo.setAppId(Integer.parseInt(APP_ID));
            payInfo.setAppKey(APP_KEY);
            payInfo.setAgent(APP_AGENT);                   // 渠道号
            payInfo.setServerId(server_id);                // 服务器id
            payInfo.setRolename(role_name);                // 玩家角色名
            payInfo.setLevel(role_level);                  // 玩家等级
            payInfo.setRoleid(role_id);                    // 角色id
            payInfo.setGameuid(m_uid);                     // 游戏用户id
            payInfo.setProductname(name);                  // 支付商品名称
            payInfo.setAmount(String.valueOf((int)price)); // 金额
            payInfo.setBillNo(orderID);                    // 游戏订单号
            payInfo.setExtraInfo(ext);                     // 额外信息
            payInfo.setUid("");                            // 如果为""，说明是接入了我们的登陆sdk，如果要只接入充值sdk，则需要传入对方平台的username
            payInfo.setMultiple(10);                       // 游戏币与人民币兌换比例
            payInfo.setGameMoney("钻石");                  // 游戏币名称
            Sjyx.payment(activity, payInfo, new ApiListenerInfo() {
                @Override
                public void onSuccess(Object obj) {
                    super.onSuccess(obj);
                    if (obj != null) {
                        Log.i("kk", "支付界面关闭" + obj.toString());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setExtData(Context context, String ext) {
        // 上报角色数据给sdk，ext:json数据，格式如下
        /* local info      = {
         *     state       = send_type, -- type
         *     id          = roleId,    -- roleId
         *     name        = roleName,  -- roleName
         *     level       = roleLevel, -- roleLevel
         *     serverID    = zoneId,    -- zoneId
         *     serverName  = zoneName,  -- zoneName
         *     gold        = balance,   -- balance
         *     vip         = vip,       -- vip
         *     factionName = partyName  -- partyName
         * }
         */
        try {
           JSONObject json_obj = new JSONObject(ext);
           String scene_Id     = json_obj.optString("state");
           String roleId       = json_obj.optString("id");
           String roleName     = json_obj.optString("name");
           String roleLevel    = json_obj.optString("level");
           String zoneId       = json_obj.optString("serverID");
           String zoneName     = json_obj.optString("serverName");
           String balance      = json_obj.optString("gold");
           String vip          = json_obj.optString("vip");
           String partyName    = json_obj.optString("factionName");

           if( scene_Id.equals("loginGameRole")==true || scene_Id.equals("createRole")==true || scene_Id.equals("levelUp")==true ) {
               if (scene_Id.equals("loginGameRole")==true) {
                   scene_Id = "enterServer";
               }

               Sjyx.setExtData(context, "", scene_Id, roleId, roleName, roleLevel, zoneId, zoneName, balance, vip, partyName);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStop(Activity activity) {
        Sjyx.onstop(activity);
    }

    public void onDestroy(Activity activity) {
        Sjyx.exit(activity);
        Sjyx.onDestroy(activity);
        // 资源释放在游戏退出前（最后一个Activity的 onDestory() 方法中，退出接口中）调用
        Sjyx.applicationDestroy(activity);
    }

    public void onResume(Activity activity) {
        Sjyx.onResume(activity);
    }

    public void onPause(Activity activity) {
        Sjyx.onPause(activity);
    }

    public void onRestart(Activity activity) {
        Sjyx.onstop(activity);
    }

    public void onNewIntent(Activity activity, Intent intent) {
        Sjyx.onNewIntent(intent);
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Sjyx.onActivityResult(activity, requestCode, resultCode, data);
    }
}
