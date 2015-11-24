package org.weilan;

import java.util.UUID;
import java.text.DecimalFormat;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import com.gionee.gamesdk.AccountInfo;
import com.gionee.gamesdk.OrderInfo;
import com.gionee.gamesdk.GamePayer;
import com.gionee.gamesdk.GamePlatform;
import com.gionee.gamesdk.GamePlatform.LoginListener;

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
    private Object mCustomParams;

    private GamePayer mGamePayer = null;

    private GamePayer.GamePayCallback mGamePayCallback;

    private Activity mCurActivity;

    private PayCallBack  mPayCallback = null;

    private static final int START_PAY = 1;

    private String mOrderInfo;

    private ProductInfo productInfo;

    private Handler mHandler = new Handler()
    {
        public void handleMessage( android.os.Message msg )
        {
            if (msg.what == START_PAY)
            {
                try {
                    JSONObject jOrder = new JSONObject(mOrderInfo);
                    String order_id    = jOrder.optString("order_id");
                    String submit_time = jOrder.optString("submit_time");
                    onSdkPay(order_id, submit_time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
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

    public void applicationInit(Activity activity) {
        // SDK初始化
        GamePlatform.init(activity, SdkConfig.APP_KEY);

        mGamePayer = new GamePayer(activity);

        mGamePayCallback = mGamePayer.new GamePayCallback() {

            //支付成功
            @Override
            public void onPaySuccess() {
                // 可以在这里处理自己的业务
                if( mPayCallback != null )
                    mPayCallback.onSuccess("支付成功");
            }
            
            //支付取消
            @Override
            public void onPayCancel() {
                // 可以在这里处理自己的业务
                if( mPayCallback != null )
                    mPayCallback.onFail("支付取消");
            }

            //支付失败，stateCode为支付失败状态码，详见接入指南
            @Override
            public void onPayFail(String stateCode) {
                // 可以在这里处理自己的业务
                if( mPayCallback != null )
                    mPayCallback.onFail("支付失败:code=" + stateCode);
            }
        };
    }

    public void login(Activity activity,Object customParams) {
        // 登录，customParams透传给回调
        mCustomParams = customParams;
        onSdkLogin(activity, true);
    }

    public void logout(Activity activity,Object customParams) {
        // 登出，customParams透传给回调
        mCustomParams = customParams;
        onSdkLogin(activity, false);
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
        if(mGamePayer != null)
            mGamePayer.onDestroy();
    }

    public void onResume(Activity activity) {
        if(mGamePayer != null)
            mGamePayer.onResume();
    }

    private void onSdkLogin(Activity activity, boolean autoLogin)
    {
        GamePlatform.loginAccount(activity, autoLogin, new LoginListener() {

            @Override
            public void onSuccess(AccountInfo accountInfo) {
                // 登录成功，处理自己的业务。

                // 获取playerId
                String playerId = accountInfo.mPlayerId;

                // 获取amigoToken
                String amigoToken = accountInfo.mToken;

                User usr = new User();
                usr.userID = playerId;
                usr.token  = amigoToken;

                userListerner.onLoginSuccess(usr, mCustomParams);
            }

            @Override
            public void onError(Exception e) {
                userListerner.onLoginFailed("登录失败"+ e, mCustomParams);
            }

            @Override
            public void onCancel() {
                userListerner.onLoginFailed("取消登录", mCustomParams);
            }

        });
    }

    private void onSdkPay(String outOrderNum, String submitTime)
    {
        try {
            Log.d("cocos2d", "outOrderNum = " + outOrderNum + ",submitTime = " + submitTime);
            //创建订单信息
            OrderInfo orderInfo = new OrderInfo();
            //开发者后台申请的Apikey
            orderInfo.setApiKey(SdkConfig.APP_KEY);
            //商户订单号，与创建支付订单中的"out_order_no"值相同
            orderInfo.setOutOrderNo(outOrderNum);
            //支付订单提交时间，与创建支付订单中的"submit_time"值相同
            orderInfo.setSubmitTime(submitTime);

            //调用启动收银台接口
            mGamePayer.pay(orderInfo, mGamePayCallback);
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    /** 支付前去服务端创建订单 */
    private void getOrderInfo(ProductInfo productInfo)
    {
        try
        {
            URL url = new URL(SdkConfig.ORDER_URL);
            HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type",
                    "application/x-www-form-urlencoded");
            connection.setDoOutput(true);// 是否输入参数
            StringBuffer params = new StringBuffer();
            //params.append("&returnJson=");
            //params.append(enCode("{\"channel\": \"vivo\", \"open_id\": \"\", \"user_name\": \"\", \"access_token\": \"\" }"));
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
            Log.d("MyView", "getOrderInfo: " + params.toString());
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
            Log.i("MyView", "getOrderInfo: " + mOrderInfo);
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

}
