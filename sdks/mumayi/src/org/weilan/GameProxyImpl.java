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

import com.mumayi.paymentmain.business.FindUserDataListener;
import com.mumayi.paymentmain.business.onLoginListener;
import com.mumayi.paymentmain.ui.PaymentCenterInstance;
import com.mumayi.paymentmain.util.PaymentLog;
import com.mumayi.paymentmain.business.ResponseCallBack;
import com.mumayi.paymentmain.business.onTradeListener;
import com.mumayi.paymentmain.ui.PaymentUsercenterContro;
import com.mumayi.paymentmain.ui.pay.MMYInstance;
import com.mumayi.paymentmain.ui.usercenter.PaymentFloatInteface;
import com.mumayi.paymentmain.util.PaymentConstants;
import com.mumayi.paymentmain.vo.UserBean;


public class GameProxyImpl extends GameProxy implements onLoginListener, onTradeListener {
    private PaymentCenterInstance instance = null;
	private PaymentUsercenterContro userCenter = null;
    private PaymentFloatInteface floatInteface = null;
    private Object loginCustomParams;
    private Activity currentActivity;
    private PayCallBack payCallBack;

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return true;
    }

    public boolean supportPay() {
        return true;
    }

    @Override
    public void applicationInit( Activity activity ) {
        Log.v( "sdk", "applicationInit" );
        currentActivity = activity;

        instance = PaymentCenterInstance.getInstance( activity );
        instance.initial( "${APP_KEY}", "${APPNAME}" );

        // 设置是否开启bug模式， true打开可以显示Log日志， false不显示
        instance.setTestMode( true );
        // 设置监听器
        instance.setListeners( this );
        instance.setTradeListener( this );
        // 设置切换完账号后是否自动跳转登陆
        instance.setChangeAccountAutoToLogin( true );

        // 悬浮窗
        floatInteface = instance.createFloat();

        // 用户中心
        userCenter = instance.getUsercenterApi( activity );
    }

    @Override
    public void login( final Activity activity, Object customParams ) {
        Log.v("sdk", "login");
        loginCustomParams = customParams;

        // 确保每次在调用登陆接口时都会检测本地数据
        instance.findUserData(new FindUserDataListener() {
            @Override
            public void findUserDataComplete()
            {
                //在账号查找流程完成后，由开发者主动调用此接口进入登陆界面
                Log.v("sdk", "go2Login");
                instance.go2Login( activity );
            }
        });
    }

    @Override
    public void logout( final Activity activity, final Object customParams ) {
        Log.v("sdk", "logout");

        // 获取当前用户信息
		UserBean user = PaymentConstants.NOW_LOGIN_USER;
        userCenter.loginOut( activity, user.getName(), new ResponseCallBack() {
			@Override
			public void onSuccess( Object obj ) {
				try {
					JSONObject loginoutJson = (JSONObject) obj;
					String loginoutCode = loginoutJson.getString( "loginOutCode" );
					if( loginoutCode.equals( "success" ) ) {
                        userListerner.onLogout( null );
                        if( floatInteface != null ) {
                            floatInteface.close();
                        }
					} else {
                        Toast.makeText( activity, "注销失败", Toast.LENGTH_SHORT ).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
                    Toast.makeText( activity, "注销失败", Toast.LENGTH_SHORT ).show();
				}
			}

			@Override
			public void onFail( Object obj ) {
                Toast.makeText( activity, "注销失败", Toast.LENGTH_SHORT ).show();
			}
		});
    }

    /**
     * 登录接口回调
     */
    @Override
    public void onLoginFinish( String loginResult ) {
        try {
            if( loginResult != null ) {
                JSONObject loginJson = new JSONObject( loginResult );
                String loginState = loginJson.getString( PaymentConstants.LOGIN_STATE );
                if( loginState != null && loginState.equals( PaymentConstants.STATE_SUCCESS ) ) {
                    // 登陆成功
                    // String uname = loginJson.getString("uname");
                    // String uid = loginJson.getString("uid");
                    // String token = loginJson.getString("token");
                    // String session = loginJson.getString("session");

                    // uname:用户名， uid:用户ID
                    // ,token:是用来服务器验证登录，注册是不是成功，用seesion来解签,解签方法见文档
                    // 所有注册，一键注册，登录的接口成功最后都会走这个回调接口
                    // 在这里进入游戏

                    User u = new User();
                    u.userID = loginJson.getString( "uid" );
                    u.token = loginJson.getString( "token" );
                    userListerner.onLoginSuccess( u, loginCustomParams );

                    if( floatInteface != null ) {
                        floatInteface.show();
                    }
                } else {
                    // 登录失败
                    String error = loginJson.getString( "error" );
                    if( error != null && error.trim().length() > 0 && error.equals( "cancel_login" ) ) {
                        // 用如果用户在登陆界面选择退出登陆界面，应当在此重新调用进入登陆界面
                        Toast.makeText( currentActivity, "登录失败 : " + error, Toast.LENGTH_SHORT ).show();
                    } else if( error != null && error.trim().length() > 0 ) {
                        // 正常登陆失败的原因
                        Toast.makeText( currentActivity, "登录失败 : " + error, Toast.LENGTH_SHORT ).show();
                    }
                }
            }
        } catch( JSONException e ) {
            e.printStackTrace();
            Toast.makeText( currentActivity, "登录失败", Toast.LENGTH_SHORT ).show();
        }
    }

    /**
     * 注销接口回调
     */
    @Override
    public void onLoginOut( String loginOutCallBackStr ) {
        try {
            JSONObject json = new JSONObject( loginOutCallBackStr );
            String code = json.getString( "loginOutCode" );
            if( code.equals( "success" ) ) {
                userListerner.onLogout( null );
                if( floatInteface != null ) {
                    floatInteface.close();
                }
            } else {
                Toast.makeText( currentActivity, "注销失败", Toast.LENGTH_SHORT ).show();
            }
        } catch (JSONException e) {
            Toast.makeText( currentActivity, "注销失败", Toast.LENGTH_SHORT ).show();
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
            JSONObject roleInfo = new JSONObject(ext);

            String role_id = roleInfo.optString( "id" );
            String role_name = roleInfo.optString( "name" );
            String role_level = roleInfo.optString( "level" );
            String server_id = roleInfo.optString( "serverID" );
            String server_name = roleInfo.optString( "serverName" );

            //设置角色所在区服（注：如果初始化时不知道区服，等级的话可以暂时不设置，只需要在调用支付之前确定设置即可！）
            instance.setUserArea( server_name );
            //设置角色名
            instance.setUserName( role_name );
            //设置角色等级
            instance.setUserLevel( Integer.parseInt( role_level ) );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void openCommunity( Activity activity ) {
        userCenter.go2Ucenter();
    }

    public void finish(Activity activity) {
        if( floatInteface != null ) {
            floatInteface.close();
        }
        if( userCenter != null ) {
            userCenter.finish();
        }
        instance.exit();
    }

    @Override
    public void pay( Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
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

        userCenter.pay( activity, name, String.format( "%.02f", price ), ext );
    }

    /**
     * 支付接口回调
     */
    @Override
    public void onTradeFinish( String tradeType, int tradeCode, Intent intent ) {
        if( tradeCode == MMYInstance.PAY_RESULT_SUCCESS ) {
            // 在每次支付回调结束时候，调用此接口检查用户是否完善了资料
            userCenter.checkUserState( currentActivity );
            payCallBack.onSuccess(null);
            Toast.makeText( currentActivity, "支付成功", Toast.LENGTH_SHORT ).show();
        } else if (tradeCode == MMYInstance.PAY_RESULT_FAILED) {
            payCallBack.onFail(null);
            Toast.makeText( currentActivity, "支付失败", Toast.LENGTH_SHORT ).show();
        }
    }

    public void onResume(Activity activity) {
        //if( userCenter != null ) {
        //    if( !userCenter .checkLogin() ) {
        //        userListerner.onLogout( null );
        //        if( floatInteface != null ) {
        //            floatInteface.close();
        //        }
        //    }
        //}
    }
}
