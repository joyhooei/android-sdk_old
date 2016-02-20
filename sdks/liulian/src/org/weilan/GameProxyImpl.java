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

import com.liulian.game.sdk.SdkManager;
import com.liulian.game.sdk.LiulianSdkSetting;
import com.liulian.game.sdk.SDKStatusCode;
import com.cd.ll.game.sdk.SDKCallBackListener;
import com.cd.ll.game.sdk.SDKCallbackListenerNullException;

public class GameProxyImpl extends GameProxy{
    private Activity curActivity;

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    public void applicationInit( final Activity activity ) {
        curActivity = activity;
		try {
			LiulianSdkSetting liulianSdkSetting = new LiulianSdkSetting();
			// 设置榴莲APPID
			liulianSdkSetting.setAppid( "${APPID}" );
			// 设置榴莲APPKEY
			liulianSdkSetting.setAppkey( "${APPKEY}" );
			// 设置PRIVATEKEY
			liulianSdkSetting.setPrivateKey( "${PRIVATEKEY}" );
			// 设置对接平台[应用宝=yingyongbao]，如果只榴莲平台那么请设置为"liulian"
			liulianSdkSetting.setPlatform( "liulian" );
			// 设置榴莲通知信息距离屏幕左边的距离(480*800分辨率标准)
			liulianSdkSetting.setNotifyToX( 50 );
			// 设置榴莲通知信息距离屏幕上边的距离(480*800分辨率标准)
			liulianSdkSetting.setNotifyToY( 50 );
			// 设置榴莲SDK调试模式是否开启
			// 设置true表示目前在测试环境中，正式上线时请修改为false
			liulianSdkSetting.setDEBUG( false );

			SdkManager.defaultSDK().initSDK( activity, liulianSdkSetting, new SDKCallBackListener() {
                @Override
                public void callBack( int code, String msg ) {
                    switch( code ) {
                        case SDKStatusCode.INIT_FAIL:
                            Log.v( "sdk", "SDK 初始化失败" );
                            Toast.makeText( activity, "SDK 初始化失败", Toast.LENGTH_SHORT ).show();
                            break;
                        case SDKStatusCode.INIT_SUCC:
                            Log.v( "sdk", "SDK 初始化成功" );
                            break;
                        default:
                            break;
                    }
                }
            });
        } catch( SDKCallbackListenerNullException e ) {
            e.printStackTrace();
        }
    }

    public void onResume(Activity activity) {
        SdkManager.defaultSDK().onResume(activity);
    }

    public void onPause(Activity activity) {
        SdkManager.defaultSDK().onPause(activity);
    }

    public void onRestart(Activity activity) {
        SdkManager.defaultSDK().onRestart(activity);
    }

    public void onStop(Activity activity) {
        SdkManager.defaultSDK().onStop(activity);
    }

    public void onDestroy(Activity activity) {
        SdkManager.defaultSDK().onDestroy(activity);
    }

    public void login( final Activity activity,final Object customParams ) {
        try {
            SdkManager.defaultSDK().login( activity, new SDKCallBackListener() {
                @Override
                public void callBack( int code, String msg ) {
                    if( code == SDKStatusCode.LOGIN_SUCCESS ) {
                        // 登录成功，返回相关数据(json)，游戏进行后续操作
                        // msg为json数据格式，解析可以获得sid值，sid传递给游戏服务器
                        // 最后由游戏服务器与榴莲服务器验证可获得玩家基本信息
                        try {
                            JSONObject src = new JSONObject( msg );
                            User usr = new User();
                            usr.userID = "";
                            usr.token = src.getString( "sid" );
                            userListerner.onLoginSuccess( usr, customParams );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if( code == SDKStatusCode.LOGIN_ERROR ) {
                        // 登录错误，判断相关信息
                        Toast.makeText( activity, "登录失败:" + msg, Toast.LENGTH_SHORT ).show();
                        Log.v( "sdk", "登录失败:" + msg );
                    } else {
                        Toast.makeText( activity, "登录失败:" + msg, Toast.LENGTH_SHORT ).show();
                        Log.v( "sdk", "登录失败:" + msg );
                    }
                }
            });
        } catch( SDKCallbackListenerNullException e ) {
            e.printStackTrace();
        }
    }

    public void pay( final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo,final PayCallBack payCallBack ) {
		try {
            String server_id = roleInfo.getString( "serverID" );
            String entity_id = roleInfo.getString( "id" );
            String role_name = roleInfo.getString( "name" );
            String extInfo = server_id + "_" + entity_id + "_" + orderID;

			SdkManager.defaultSDK().pay( activity, price, name, server_id, entity_id, role_name, extInfo, new SDKCallBackListener() {
                @Override
                public void callBack( int code, String msg ) {
                    switch( code ) {
                        case SDKStatusCode.PAY_SUCCESS:
                            Toast.makeText( activity, "支付成功", Toast.LENGTH_LONG ).show();
                            break;
                        case SDKStatusCode.PAY_ERROR:
                            Toast.makeText( activity, "支付失败 : " + msg, Toast.LENGTH_LONG ).show();
                            break;
                        case SDKStatusCode.PAY_CANCEL:
                            Toast.makeText( activity, "支付取消", Toast.LENGTH_LONG ).show();
                            break;
                        default:
                            break;
                    }
                }
            });
        } catch( SDKCallbackListenerNullException e ) {
            e.printStackTrace();
        } catch( JSONException e ) {
            e.printStackTrace();
        }
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
            JSONObject roleInfo = new JSONObject( ext );

            String server_id = roleInfo.optString( "serverID" );
            String server_name = roleInfo.optString( "serverName" );
            String role_id = roleInfo.optString( "id" );
            String role_name = roleInfo.optString( "name" );
            String role_level = roleInfo.optString( "level" );

            SdkManager.defaultSDK().setGameRole( curActivity, server_id, server_name, role_id, role_name, role_level, new SDKCallBackListener() {
                @Override
                public void callBack( int code, String msg ) {
                    switch( code ) {
                        case SDKStatusCode.GAME_ROLE_SUCCESS:
                            Log.v( "sdk", "setGameRole 成功" + msg );
                            break;
                        case SDKStatusCode.GAME_ROLE_FAIL:
                            Log.v( "sdk", "setGameRole 失败" + msg );
                            break;
                        default:
                            break;
                    }
                }
            });
        } catch ( SDKCallbackListenerNullException e ) {
            e.printStackTrace();
        } catch( JSONException e ) {
            e.printStackTrace();
        }
    }


}
