package org.weilan;

import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import com.meizu.gamecenter.sdk.LoginResultCode;
import com.meizu.gamecenter.sdk.MzAccountInfo;
import com.meizu.gamecenter.sdk.MzBuyInfo;
import com.meizu.gamecenter.sdk.MzGameBarPlatform;
import com.meizu.gamecenter.sdk.MzGameCenterPlatform;
import com.meizu.gamecenter.sdk.MzLoginListener;
import com.meizu.gamecenter.sdk.MzPayListener;
import com.meizu.gamecenter.sdk.PayResultCode;


class ProductInfo {
    public String productName;
    public String productDesc;
    public String price;
    public String userName;
    public String goodsID;
    public String orderID;
    public String callBackInfo;

    public ProductInfo(String productName, String productDesc, String price, String userName, String goodsID, String orderID, String callBackInfo) {
        this.productName = productName;
        this.productDesc = productDesc;
        this.price = price;
        this.userName = userName;
        this.goodsID = goodsID;
        this.orderID = orderID;
        this.callBackInfo = callBackInfo;
    }
}


public class GameProxyImpl extends GameProxy{
    private static final String APP_ID  = "${APPID}";
    private static final String APP_KEY = "${APPKEY}";

    private Activity    curActivity;
    private ProductInfo productInfo;
    private PayCallBack  mPayCallback = null;
    private String mOrderInfo;

    private static final int START_PAY = 1;

    private Object mCustomParams;

    private Handler mHandler = new Handler()
    {
        public void handleMessage( android.os.Message msg )
        {
            if (msg.what == START_PAY)
            {
                try {
                    JSONObject object = new JSONObject(mOrderInfo);
                    object = object.getJSONObject("value");
                    String orderId = object.getString("cp_order_id");
                    String sign = object.getString("sign");
                    String signType = object.getString("sign_type");
                    int buyCount = object.getInt("buy_amount");
                    String cpUserInfo = object.has("user_info") ? object.getString("user_info") : "";
                    String amount = object.has("total_price") ? object.getString("total_price") : "0";
                    String productId = object.getString("product_id");
                    String productSubject = object.getString("product_subject");
                    String productBody = object.getString("product_body");
                    String productUnit = object.getString("product_unit");
                    String appid = object.getString("app_id");
                    String uid = object.getString("uid");
                    String perPrice = object.getString("product_per_price");
                    long createTime = object.getLong("create_time");
                    int payType = object.getInt("pay_type");
                    MzBuyInfo buyInfo = new MzBuyInfo()
                        .setBuyCount(buyCount)
                        .setCpUserInfo(cpUserInfo)
                        .setOrderAmount(amount)
                        .setOrderId(orderId)
                        .setPerPrice(perPrice)
                        .setProductBody(productBody)
                        .setProductId(productId)
                        .setProductSubject(productSubject)
                        .setProductUnit(productUnit)
                        .setSign(sign)
                        .setSignType(signType)
                        .setCreateTime(createTime)
                        .setAppid(appid)
                        .setUserUid(uid)
                        .setPayType(payType);
                    onSdkPay(buyInfo);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    };

    //gamebar操作实例,不需要悬浮窗的可以不用
    MzGameBarPlatform mzGameBarPlatform;

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
        curActivity = activity;
        MzGameCenterPlatform.init(activity, APP_ID, APP_KEY);

        mzGameBarPlatform = new MzGameBarPlatform(activity, MzGameBarPlatform.GRAVITY_LEFT_TOP);

        mzGameBarPlatform.onActivityCreate();
    }

    public void login(Activity activity,Object customParams) {
        // 登录，customParams透传给回调
        mCustomParams = customParams;
        MzGameCenterPlatform.login(activity, new MzLoginListener() {
            @Override
            public void onLoginResult(int code, MzAccountInfo accountInfo, String errorMsg) {
                //  登录结果回调，该回调跑在应用主线程
                switch(code){
                    case LoginResultCode.LOGIN_SUCCESS:
                        //  登录成功，拿到uid 和 session到自己的服务器去校验session合法性
                        String uid     = accountInfo.getUid();
                        String uname   = accountInfo.getName();
                        String session = accountInfo.getSession();
                        User usr = new User();
                        usr.username = uname;
                        usr.userID   = uid;
                        usr.token    = session;
                        userListerner.onLoginSuccess(usr, mCustomParams);
                        break;
                    case LoginResultCode.LOGIN_ERROR_CANCEL:
                        //  用户取消登陆操作
                        userListerner.onLoginFailed("用户取消登陆操作", mCustomParams);
                        break;
                    default:
                        //  登陆失败，包含错误码和错误消息。
                        //  注意，错误消息需要由游戏展示给用户，错误码可以打印，供调试使用
                        Toast.makeText(curActivity, "登录失败 : " + errorMsg, Toast.LENGTH_LONG).show();
                        userListerner.onLoginFailed("登录失败 : " + errorMsg, mCustomParams);
                        Log.i("cocos","登录失败 : " + errorMsg + " , code = " + code);
                        break;
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
        curActivity = activity;
        mPayCallback = payCallBack;

        DecimalFormat df = new DecimalFormat("0.00");
        productInfo = new ProductInfo(name, "钻石", df.format(price),
                "", ID, orderID, callBackInfo);

        new Thread(new Runnable()
            {
                @Override
                public void run( )
                {
                    getOrderInfo( productInfo );
                }
            }).start();
    }

    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        MzGameCenterPlatform.logout(activity);
        mzGameBarPlatform.onActivityDestroy();
    }

    public void onResume(Activity activity) {
        super.onResume(activity);
        mzGameBarPlatform.onActivityResume();
    }

    public void onPause(Activity activity) {
        super.onPause(activity);
        mzGameBarPlatform.onActivityPause();
    }

    /** 支付前去服务端创建订单 */
    private void getOrderInfo(ProductInfo productInfo)
    {
        try
        {
            URL url = new URL("${ORDER_URL}");
            HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type",
                    "application/x-www-form-urlencoded");
            connection.setDoOutput(true);// 是否输入参数
            StringBuffer params = new StringBuffer();
            params.append("&returnJson=");
            params.append(enCode("{\"channel\": \"mz\", \"open_id\": \"\", \"user_name\": \"\", \"access_token\": \"\" }"));
            params.append("&productName=");
            params.append(enCode(productInfo.productName));
            params.append("&description=");
            params.append(enCode(productInfo.productDesc));
            params.append("&amount=");
            params.append(enCode(productInfo.price));
            params.append("&orderid=");
            params.append(enCode(productInfo.orderID));
            params.append("&cpPrivateInfo=");
            params.append(enCode(productInfo.callBackInfo));
            Log.d("cocos", "getOrderInfo: " + params.toString());
            byte[] bytes = params.toString().getBytes();
            connection.connect();
            connection.getOutputStream().write(bytes);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer readbuff = new StringBuffer();
            String lstr = null;
            while ((lstr = reader.readLine()) != null)
            {
                readbuff.append(lstr);
            }
            connection.disconnect();
            reader.close();
            mOrderInfo = readbuff.toString();
            Log.i("cocos", "getOrderInfo: " + mOrderInfo);
            mHandler.sendEmptyMessage(START_PAY);

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String enCode( String value )
    {
        String enCodeValue = null;
        try
        {
            enCodeValue = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return enCodeValue;
    }

    private void onSdkPay(MzBuyInfo buyInfo){
        MzGameCenterPlatform.payOnline(curActivity, buyInfo, new MzPayListener() {
            @Override
            public void onPayResult(int code, MzBuyInfo info, String errorMsg) {
                // 支付结果回调，该回调跑在应用主线程
                switch(code){
                    case PayResultCode.PAY_SUCCESS:
                        // 如果成功，接下去需要到自己的服务器查询订单结果
                        mPayCallback.onSuccess("支付成功 : " + info.getOrderId());
                        break;
                    case PayResultCode.PAY_ERROR_CANCEL:
                        // 用户取消支付操作
                        mPayCallback.onFail("用户取消支付操作");
                        break;
                    default:
                        // 支付失败，包含错误码和错误消息。
                        // 注意，错误消息需要由游戏展示给用户，错误码可以打印，供调试使用
                        mPayCallback.onFail("支付失败 : " + errorMsg + " , code = " + code);
                        Toast.makeText(curActivity, "支付失败 : " + errorMsg, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }
}
