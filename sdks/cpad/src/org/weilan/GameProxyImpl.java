package org.weilan;

import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;

import java.net.URLEncoder;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;
import android.content.pm.ActivityInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import com.coolcloud.uac.android.api.Coolcloud;
import com.coolcloud.uac.android.api.ErrInfo;
import com.coolcloud.uac.android.api.OnResultListener;
import com.coolcloud.uac.android.common.Constants;
import com.coolcloud.uac.android.gameassistplug.GameAssistApi;
import com.coolcloud.uac.android.gameassistplug.GameAssistApi.SwitchingAccount;

import com.iapppay.interfaces.authentactor.AccountBean;
import com.iapppay.interfaces.callback.IPayResultCallback;
import com.iapppay.sdk.main.CoolPadPay;
import com.iapppay.utils.RSAHelper;

public class GameProxyImpl extends GameProxy{
    public static final String APP_ID   = "${APPID}";
    public static final String APP_KEY  = "${APPKEY}";
    public static final String PRIVATE_KEY  = "${PRIVATEKEY}";
    public static final String PAY_URL  = "${PAY_URL}";
    private GameAssistApi mGameAssistApi;
    private Coolcloud coolcloud = null;
    private int screen = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private Object mCustomeParams   = null;
    private PayCallBack mPayCallBack = null;
    private String mAuthCode;
    private String mUserInfo;

    private Activity curActivity;

    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        CoolPadPay.mPayResultCallback = null;
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
        coolcloud = Coolcloud.get(activity, APP_ID);
        /**
         * pay SDK初始化，完成SDK的初始化
         */
        CoolPadPay.init(activity, screen, APP_ID);
    }

    public void login(Activity activity,Object customParams) {
        mCustomeParams = customParams;
        // 登录，customParams透传给回调
        Bundle mInput = new Bundle();
        // 设置屏幕方向
        mInput.putInt(Constants.KEY_SCREEN_ORIENTATION, screen);
        // 设置需要权限 一般都为get_basic_userinfo这个常量
        mInput.putString(Constants.KEY_SCOPE, "get_basic_userinfo");
        // 获取类型为AuthCode
        mInput.putString(Constants.KEY_RESPONSE_TYPE, Constants.RESPONSE_TYPE_CODE);
        coolcloud.login(activity, mInput, new Handler(), new OnResultListener() {

            @Override
            public void onResult(Bundle arg0) {
                // 登录成功 在返回的Bundle中获取AuthCode
                mAuthCode = arg0.getString(Constants.RESPONSE_TYPE_CODE);
                User usr  = new User();
                usr.token = mAuthCode;
                userListerner.onLoginSuccess(usr, mCustomeParams);
            }

        @Override
            public void onError(ErrInfo s) {
                // 登录失败
                userListerner.onLoginFailed("登录失败", mCustomeParams);
            }

        @Override
            public void onCancel() {
                // 登录被取消
                userListerner.onLoginFailed("登录被取消", mCustomeParams);
            }
        });
    }

    public void logout(Activity activity,Object customParams) {
        // 登出，customParams透传给回调
        coolcloud.logout(activity);
        userListerner.onLogout(customParams);
    }


    public void switchAccount(Activity activity,Object customParams) {
        mCustomeParams = customParams;
        // 切换帐号（目前没用到），customParams透传给回调
        Bundle mInput = new Bundle();
        // 设置屏幕方向
        mInput.putInt(Constants.KEY_SCREEN_ORIENTATION, screen);
        // 设置需要权限 一般都为get_basic_userinfo这个常量
        mInput.putString(Constants.KEY_SCOPE, "get_basic_userinfo");
        // 获取类型为AuthCode
        mInput.putString(Constants.KEY_RESPONSE_TYPE, Constants.RESPONSE_TYPE_CODE);
        coolcloud.login(activity, mInput, new Handler(), new OnResultListener() {

            @Override
            public void onResult(Bundle arg0) {
                // 登录成功 在返回的Bundle中获取AuthCode
                mAuthCode = arg0.getString(Constants.RESPONSE_TYPE_CODE);
                User usr  = new User();
                usr.token = mAuthCode;
                userListerner.onLoginSuccess(usr, mCustomeParams);
            }

        @Override
            public void onError(ErrInfo s) {
                // 登录失败
                userListerner.onLoginFailed("登录失败", mCustomeParams);
            }

        @Override
            public void onCancel() {
                // 登录被取消
                userListerner.onLoginFailed("登录被取消", mCustomeParams);
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
        mPayCallBack = payCallBack;
        curActivity  = activity;

        int waresid = 0;
        String coolPadAuthToken = null;
        String coolPadOpenID    = null;
        try {
            waresid = Integer.parseInt(ID);

            String extra = roleInfo.optString("extra");
            Log.i("cocos", "pay===>extra=" + extra);
            if( extra != null){
                JSONObject extraObj = new JSONObject(extra);
                coolPadAuthToken = extraObj.optString("access_token");
                coolPadOpenID    = extraObj.optString("open_id");
            }
            Log.i("cocos", "pay===>coolPadAuthToken=" + coolPadAuthToken + ";coolPadOpenID=" + coolPadOpenID);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        double p = Double.parseDouble(String.valueOf(price)) ;
        String genUrl = getGenUrl(waresid, orderID, p, callBackInfo, coolPadOpenID);
        AccountBean account = null;
        account = CoolPadPay.buildAccount(curActivity, coolPadAuthToken, APP_ID, coolPadOpenID);
        CoolPadPay.startPay(curActivity, genUrl, account, new IPayResultCallback() {

            @Override
            public void onPayResult(int resultCode, String signvalue, String resultInfo) {
                switch (resultCode) {
                    case CoolPadPay.PAY_SUCCESS:
                        mPayCallBack.onSuccess(signvalue);
                        break;

                    default:
                        mPayCallBack.onFail(resultInfo);
                        break;
                }
            }
        });

    }

    /*客户端下单模式
     *
     * 生成数据后需要对数据做签名，签名的算法是使用应用的私钥做RSA签名。
     * 应用的私钥可以在商户自服务获取
     *
     *   */
    private String getGenUrl(int waresid, String orderID, double price, String callBackInfo, String coolPadOpenID){
        String json = "";

        JSONObject obj = new JSONObject();
        try {
            obj.put("appid", APP_ID);
            obj.put("waresid", waresid );
            //obj.put("quantity", 1 );
            obj.put("cporderid", orderID);
            obj.put("price", price);
            obj.put("appuserid", coolPadOpenID);

            /*CP私有信息，选填*/
            obj.put("cpprivateinfo", callBackInfo);

            /*支付成功的通知地址。选填。如果客户端不设置本参数，则使用服务端配置的地址。*/
            if( PAY_URL.length() > 0 )
                obj.put("notifyurl", PAY_URL);

            json = obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sign = "";
        try {
            Log.e("cocos","json = " + json);
            sign = RSAHelper.signForPKCS1(json, PRIVATE_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "transdata=" + URLEncoder.encode(json) + "&sign=" + URLEncoder.encode(sign) + "&signtype=" + "RSA";
    }
}
