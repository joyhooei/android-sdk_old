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

import com.weedong.gamesdkplatform.WdCommplatform;
import com.weedong.gamesdkplatform.WeeDongCallBackListener.OnLoginProcessListener;
import com.weedong.gamesdkplatform.base.WdAppInfo;
import com.weedong.gamesdkplatform.base.WdReturnCode;
import com.weedong.gamesdkplatform.dialog.FloatingService;
import com.weedong.gamesdkplatform.utils.ImageUtils;
import com.weedong.gamesdkplatform.WeeDongCallBackListener;
import com.weedong.gamesdkplatform.status.WdBaseInfo;

public class GameProxyImpl extends GameProxy{
    private PayCallBack payCallBack;
    private Activity currentActivity;
    private Object loginCustomParams;

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return true;
    }

    public boolean supportPay() {
        return true;
    }

    public void openCommunity(Activity activity) {
        // 打开社区
        WdCommplatform.getInstance().wdEnterPersonalCenter( activity );
    }

    @Override
    public void applicationInit(Activity activity) {
        Log.v("sdk", "applicationInit");

        WdCommplatform SDK = WdCommplatform.getInstance();
        WdAppInfo appInfo = new WdAppInfo();
        appInfo.setCtx( activity );
        appInfo.setAppId( "${APP_ID}" );
        appInfo.setAppKey( "${APP_KEY}" );
        int i = SDK.wdInital(appInfo);
        Log.e("setAppId", "inital=======" + i);

        SDK.wdSetScreenOrientation( WdCommplatform.SCREEN_ORIENTATION_PORTRAIT );
    }

    @Override
    public void login( final Activity activity,Object customParams ) {
        Log.v("sdk", "login");
        loginCustomParams = customParams;

        ImageUtils.setSharePreferences( activity, "weedong_is_auto_login", false );

        WdCommplatform.getInstance().wdLogin( activity, activity, new OnLoginProcessListener() {
            @Override
            public void finishLoginProcess(int code) {
                Log.v( "sdk", "login finish code : " + code );
                Log.v( "sdk", "login finish sessionId : " + WdCommplatform.getInstance().wdGetSessionId( activity ) );
                switch( code ) {
                    case WdReturnCode.WD_COM_PLATFORM_SUCCESS:
                        // 登录成功
                        User u = new User();
                        u.userID = WdBaseInfo.gSessionObj.getUid();
                        u.token = WdCommplatform.getInstance().wdGetSessionId( activity );
                        userListerner.onLoginSuccess(u, loginCustomParams);
                        break;
                    case WdReturnCode.WD_COM_PLATFORM_ERROR_LOGIN_FAIL:
                        // 登录失败
                        Toast.makeText( activity, "登录失败", Toast.LENGTH_SHORT ).show();
                        break;
                    case WdReturnCode.WD_COM_PLATFORM_ERROR_CANCELL:
                        // 取消登录
                        Toast.makeText( activity, "取消登录", Toast.LENGTH_SHORT ).show();
                        break;
                    default:
                        // 登录失败
                        Toast.makeText( activity, "登录失败", Toast.LENGTH_SHORT ).show();
                        break;
                }
            }
        });
    }

    @Override
    public void logout( final Activity activity, final Object customParams ) {
        Log.v("sdk", "logout");
        currentActivity = activity;

        WdCommplatform.getInstance().wdLogout( activity, new WeeDongCallBackListener.OnCallbackListener() {
            @Override
            public void callback( int code ) {
                if( code == WdReturnCode.WD_COM_PLATFORM_SUCCESS ) {
                    // 注销成功
                    userListerner.onLogout( customParams );
                } else if (code == WdReturnCode.WD_LOGOUT_ACCOUNT_FAIL) {
                    // 注销失败
                    Toast.makeText( activity, "注销失败", Toast.LENGTH_SHORT ).show();
                } else {
                    // 注销失败
                    Toast.makeText( activity, "注销失败", Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }

	private Handler payHandler = new Handler() {
        public void handleMessage( Message msg ) {
            switch( msg.what ) {
                case WdReturnCode.WD_COM_PLATFORM_SUCCESS:
                    payCallBack.onSuccess(null);
                    Toast.makeText( currentActivity, "支付成功", Toast.LENGTH_SHORT ).show();
                    break;
                case WdReturnCode.WD_COM_PLATFORM_ERROR_PAY_CANCEL:
                    // 取消充值
                    payCallBack.onFail(null);
                    Toast.makeText( currentActivity, "支付被取消", Toast.LENGTH_SHORT ).show();
                    break;
                case WdReturnCode.WD_COM_PLATFORM_ERROR_PAY_FAIL:
                    // 充值失败
                    payCallBack.onFail(null);
                    Toast.makeText( currentActivity, "支付失败", Toast.LENGTH_SHORT ).show();
                    break;
                case WdReturnCode.WD_COM_PLATFORM_PAY_ORDER_SUBMITTED:
                    // 订单已提交
                    payCallBack.onSuccess(null);
                    Toast.makeText( currentActivity, "本次支付订单已提交，请查收到账情况!", Toast.LENGTH_SHORT ).show();
                default:
                    // 充值失败
                    payCallBack.onFail(null);
                    Toast.makeText( currentActivity, "情况不明！", Toast.LENGTH_SHORT ).show();
                    break;
            }
        };
    };


    @Override
    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        Log.v("sdk", "pay:" + ID + "," + name + "," + price + "," + callBackInfo + "," + roleInfo.toString());
        currentActivity = activity;
        this.payCallBack = payCallBack;

        String ext = "";
        String server_id = "1";
        try {
            server_id = roleInfo.getString("serverID");
            ext = server_id + "_" + roleInfo.getString("id") + "_" + orderID;
        } catch (JSONException e) {
            Log.e("sdk", "roleInfo parse failed, ignore");
        }
        WdCommplatform.getInstance().wdPayForCoin( orderID, (int)( price * 10 ), ext, server_id, activity, new WeeDongCallBackListener.OnPayProcessListener() {
            @Override
            public void finishPayProcess( int code ) {
                Log.v( "sdk", "pay status : " + code );
                payHandler.sendEmptyMessage( code );
            }
        });
    }

    @Override
    public void exit(Activity activity, ExitCallback callback) {
        Log.v("sdk", "exit");
        callback.onNo3rdExiterProvide();
    }


}
