package org.weilan;

import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import sy07073.mobile.game.sdk.SDKCallBack;
import sy07073.mobile.game.sdk.SY07073API;
import sy07073.mobile.game.sdk.lib.view.FloatWindow;

public class GameProxyImpl extends GameProxy{
    private String user_name;
    private String token;
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

    @Override
    public void applicationInit( final Activity activity ) {
        Log.v( "sdk", "applicationInit" );

        SY07073API.getInstance().init( activity, ${PID}, ${APP_ID}, new SDKCallBack() {
            @Override
            public void callback(String param) {
                try {
                    JSONObject jsonObject = new JSONObject( param );
                    int code = jsonObject.getInt( "state" );
                    String msg = jsonObject.getString( "msg" );	    
                    if( code == 1 ) { //成功
                        Toast.makeText( activity, "初始化成功", 0 ).show();
                    } else { // 失败
                        Toast.makeText( activity, msg, 0 ).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText( activity, "SDK 初始化失败", 0 ).show();
                } 	    
            }										
        });
    }

    @Override
    public void login( final Activity activity, final Object customParams ) {
        Log.v("sdk", "login");

        SY07073API.getInstance().login( activity, new SDKCallBack() {
            @Override
            public void callback( String s ) {
                try {
                    JSONObject jsonObject = new JSONObject( s );
                    int code = jsonObject.getInt( "state" );
                    String msg = jsonObject.getString( "msg" );				 		    
                    if( code == 1 ) { //成功
                        JSONObject returnData = jsonObject.getJSONObject( "data" );
                        user_name = returnData.getString("username");
                        token = returnData.getString( "token" );

                        User u = new User();
                        u.userID = user_name;
                        u.token = token;
                        userListerner.onLoginSuccess( u, customParams );
                    } else { // 失败
                        Toast.makeText( activity, msg, 0 ).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText( activity, "登录失败，请重试！", 0 ).show();
                } 	    
            }
        });
    }

    public void switchAccount( final Activity activity, final Object customParams ) {
        SY07073API.getInstance().switchAccount( activity, new SDKCallBack() {
            @Override
            public void callback(String param) {
                try {
                    JSONObject jsonObject = new JSONObject(param);
                    int code = jsonObject.getInt( "state" );
                    String msg = jsonObject.getString( "msg" );			    		    
                    if( code == 1 ) { //成功
                        userListerner.onLogout( null );

                        JSONObject returnData = jsonObject.getJSONObject( "data" );
                        user_name = returnData.getString( "username" );
                        token = returnData.getString( "token" );

                        User u = new User();
                        u.userID = user_name;
                        u.token = token;
                        userListerner.onLoginSuccess( u, customParams );
                    } else { // 失败
                        Toast.makeText( activity, msg, 0 ).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText( activity, "登录失败，请重试！", 0 ).show();
                } 	    
            }
        }, user_name, token);
    }

    @Override
    public void logout( final Activity activity, final Object customParams ) {
        Log.v("sdk", "logout");
        userListerner.onLogout( null );
    }

    @Override
    public void pay( final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        Log.v("sdk", "pay:" + ID + "," + name + "," + price + "," + callBackInfo + "," + roleInfo.toString());
        this.payCallBack = payCallBack;

        String ext = "";
        String server_id = "1";
        try {
            server_id = roleInfo.getString("serverID");
            ext = server_id + "_" + roleInfo.getString("id") + "_" + orderID;
        } catch (JSONException e) {
            Log.e("sdk", "roleInfo parse failed, ignore");
        }

        try {
            SY07073API.getInstance().pay( activity, new SDKCallBack(){
                @Override
                public void callback(String param) {
                    try {
                        JSONObject jsonObject = new JSONObject( param );
                        int code = jsonObject.getInt( "state" );
                        if( code == 1 ) { //成功
                            Toast.makeText( activity, "支付成功", Toast.LENGTH_LONG ).show();
                        } else if( code == 1029 ) {
                            Toast.makeText( activity, "支付取消", Toast.LENGTH_LONG ).show();
                        } else {
                            String msg = jsonObject.getString( "msg" );				 		    
                            Toast.makeText( activity, "支付结果 : " + msg, Toast.LENGTH_LONG ).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText( activity, "支付失败", Toast.LENGTH_LONG ).show();
                    } 	    
                }
            }, user_name, token, Integer.parseInt( server_id ), price, URLEncoder.encode( ext, "utf-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            e.printStackTrace();
            Toast.makeText( activity, "支付异常", Toast.LENGTH_LONG ).show();
        }
    }

    public void exit( Activity activity, final ExitCallback callback ) {
        SY07073API.getInstance().exitSDK( activity, new SDKCallBack() {
            @Override
            public void callback(String s) {
                if( s.equals( "CY_EXIT" ) ) {
                    callback.onExit();
                }
            }
        });
    }

    public void onResume( Activity activity ) {
		super.onResume( activity );
		FloatWindow.getInstance( activity ).show();
    }

    public void onPause(Activity activity) {
		super.onPause( activity );
		FloatWindow.getInstance( activity ).hide();	
    }


}
