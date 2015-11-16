package org.weilan;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;
import java.text.DecimalFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.huawei.gamebox.buoy.sdk.inter.UserInfo;
import com.android.huawei.pay.plugin.IPayHandler;
import com.android.huawei.pay.plugin.PayParameters;
import com.android.huawei.pay.util.HuaweiPayUtil;
import com.android.huawei.pay.util.Rsa;
import com.huawei.gamebox.buoy.sdk.impl.BuoyOpenSDK;
import com.huawei.gamebox.buoy.sdk.util.DebugConfig;
import com.huawei.gamebox.buoy.sdk.util.RoleInfo;
import com.huawei.hwid.openapi.OpenHwID;
import com.huawei.opensdk.OpenSDK;
import com.huawei.opensdk.RetCode;

public class GameProxyImpl extends GameProxy {
    String privateKey = "${PRIVATEKEY}";
    String VALID_TOKEN_ADDR = "${VALID_TOKEN_ADDR}";
 
    private Activity currentActivity;
    private Object loginCustomParams;
    private PayCallBack payCallBack;
    private int nInitRetCode = -1;

    /**
     * 支付回调handler
     */
    private IPayHandler payHandler = new IPayHandler() {
        @Override
        public void onFinish( Map<String, String> payResp ) {
            Log.v( "sdk", "支付结束：payResp= " + payResp );
            // 处理支付结果
            String pay = "支付未成功！";
            Log.v( "sdk", "支付结束，返回码： returnCode=" + payResp.get( PayParameters.returnCode ) );

            // 支付成功，进行验签
            if( "0".equals( payResp.get( PayParameters.returnCode ) ) ) {
                if( "success".equals( payResp.get( PayParameters.errMsg ) ) ) {
                    // 支付成功，验证信息的安全性；待验签字符串中如果有isCheckReturnCode参数且为yes，则去除isCheckReturnCode参数
                    if( payResp.containsKey( "isCheckReturnCode" ) && "yes".equals( payResp.get( "isCheckReturnCode" ) ) ) {
                        payResp.remove( "isCheckReturnCode" );
                    } else { // 支付成功，验证信息的安全性；待验签字符串中如果没有isCheckReturnCode参数活着不为yes，则去除isCheckReturnCode和returnCode参数
                        payResp.remove( "isCheckReturnCode" );
                        payResp.remove( PayParameters.returnCode );
                    }

                    // 支付成功，验证信息的安全性；待验签字符串需要去除sign参数
                    String sign = payResp.remove( PayParameters.sign );
                    String noSigna = HuaweiPayUtil.getSignData( payResp );

                    // 使用公钥进行验签
                    boolean s = Rsa.doCheck( noSigna, sign, GlobalParam.PAY_RSA_PUBLIC );

                    if( s ) {
                        pay = "支付成功！";
                        payCallBack.onSuccess(null);
                    } else {
                        payCallBack.onFail(null);
                        pay = "支付成功，但验签失败！";
                    }
                    Log.v( "sdk", "支付结束：sign= " + sign + "，待验证字段：" + noSigna + "，Rsa.doChec = " + s );
                } else {
                    payCallBack.onFail(null);
                    Log.v( "sdk", "支付失败 errMsg= " + payResp.get( PayParameters.errMsg ) );
                }
            } else if ( "30002".equals( payResp.get( PayParameters.returnCode ) ) ) {
                payCallBack.onFail(null);
                pay = "支付结果查询超时！";
            }

            Log.v( "sdk", "支付结果 result = " + pay );
        }
    };

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    private String df_price;
    private String productName;
    private String productDesc;
    private String requestId;

    private static final int START_INIT_SDK = 1;
    private static final int START_PAY = 2;
    Handler mHandler = new Handler() {
        public void handleMessage( android.os.Message msg ) {
            if( msg.what == START_INIT_SDK ) {
                // 浮标初始化
                nInitRetCode = OpenSDK.init( currentActivity, GlobalParam.APP_ID, GlobalParam.PAY_ID, GlobalParam.BUO_SECRET, new UserInfo() {
                    @Override
                    public void dealUserInfo( HashMap<String, String> userInfo ) {
                        // 用户信息为空，登录失败
                        if( null == userInfo ) {
                            userListerner.onLoginFailed( "登录失败", loginCustomParams );

                            Toast.makeText( currentActivity, "用户信息为null", Toast.LENGTH_LONG ).show();
                            Log.v( "sdk", "用户信息为null" );
                        } else if ( "1".equals( (String)userInfo.get( "loginStatus" ) ) ) { // 使用华为账号登录且成功，进行accessToken验证
                            User u = new User(); // fill User with accountInfo
                            u.userID = (String)userInfo.get( "userID" );
                            u.token = (String)userInfo.get( "accesstoken" );
                            //u.username = (String)userInfo.get( "userName" );
                            userListerner.onLoginSuccess( u, loginCustomParams );

                            Log.v( "sdk", "使用华为账号登录，进行accessToken校验" );
                        } else {
                            userListerner.onLoginFailed( "登录失败", loginCustomParams );

                            Log.v( "sdk", "验证 accessToken 失败" );
                            Toast.makeText( currentActivity, "验证 accessToken 失败", Toast.LENGTH_LONG ).show();
                        }
                    }
                });

                Log.v( "sdk", "OpenSDK init nInitRetCode is:" + nInitRetCode );
            }

            if( msg.what == START_PAY ) {
                GameBoxUtil.startPay( currentActivity, df_price, productName, productDesc, requestId, payHandler );
            }
        };
    };

    Runnable getBuoyTask = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL( GlobalParam.GET_BUOY_PRIVATE_KEY );
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput( false ); // 是否输入参数
                connection.connect();

                BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
                StringBuffer readbuff = new StringBuffer();
                String lstr = null;
                while( ( lstr = reader.readLine() ) != null ) {
                    readbuff.append( lstr );
                }
                connection.disconnect();
                reader.close();

                GlobalParam.BUO_SECRET = readbuff.toString();
                Log.v( "sdk", "GlobalParam.BUO_SECRET : " + GlobalParam.BUO_SECRET );

                mHandler.sendEmptyMessage( START_INIT_SDK );
            } catch( MalformedURLException e ) {
                e.printStackTrace();
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void applicationInit( Activity activity ) {
        Log.v( "sdk", "applicationInit" );

        currentActivity = activity;

        new Thread( getBuoyTask ).start();
    }

    @Override
    public void login( Activity activity,Object customParams ) {
        Log.v("sdk", "login");
        loginCustomParams = customParams;

        currentActivity = activity;

        // 初始化成功，进行登录调用
        if( RetCode.SUCCESS == nInitRetCode ) {
            OpenSDK.start();
        } else {
            Toast.makeText( activity, "OpenSDK init 失败", Toast.LENGTH_LONG ).show();
        }
    }

    @Override
    public void logout(Activity activity,Object customParams) {
        Log.v("sdk", "logout");
        currentActivity = activity;
        userListerner.onLogout(customParams);
    }

    Runnable getPayTask = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL( GlobalParam.GET_PAY_PRIVATE_KEY );
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput( false ); // 是否输入参数
                connection.connect();

                BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
                StringBuffer readbuff = new StringBuffer();
                String lstr = null;
                while( ( lstr = reader.readLine() ) != null ) {
                    readbuff.append( lstr );
                }
                connection.disconnect();
                reader.close();

                GlobalParam.PAY_RSA_PRIVATE = readbuff.toString();
                Log.v( "sdk", "GlobalParam.PAY_RSA_PRIVATE : " + GlobalParam.PAY_RSA_PRIVATE );

                mHandler.sendEmptyMessage( START_PAY );
            } catch( MalformedURLException e ) {
                e.printStackTrace();
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        Log.v( "sdk", "pay:" + ID + "," + name + "," + price + "," + callBackInfo + "," + roleInfo.toString() );

        currentActivity = activity;
        this.payCallBack = payCallBack;

        DecimalFormat df = new DecimalFormat( "0.00" );
        if( "".equals( GlobalParam.PAY_RSA_PRIVATE ) ) {
            df_price = df.format( price );
            productName = name;
            productDesc = "钻石";
            requestId = orderID;

            new Thread( getPayTask ).start();
        } else {
            GameBoxUtil.startPay( activity, df.format( price ), name, "钻石", orderID, payHandler );
        }
    }

    @Override
    public void exit(Activity activity, ExitCallback callback) {
        Log.v("sdk", "exit");
        callback.onNo3rdExiterProvide();
    }

    public void onResume( Activity activity ) {
        super.onResume( activity );

        // 在界面恢复的时候又显示浮标，和onPause配合使用
        if( RetCode.SUCCESS == nInitRetCode ) {
            BuoyOpenSDK.getIntance().showSmallWindow( activity );
        }
    }

    public void onPause( Activity activity ) {
        super.onPause( activity );

        // 在界面暂停的时候，隐藏浮标，和onResume配合使用
        if( RetCode.SUCCESS == nInitRetCode ) {
            BuoyOpenSDK.getIntance().hideSmallWindow( activity );
            BuoyOpenSDK.getIntance().hideBigWindow( activity );
        }
    }
}
