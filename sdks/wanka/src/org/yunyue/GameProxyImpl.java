package org.yunyue;

import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;

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

import com.huanju.wanka.paysdk.ILoginCallbackListener;
import com.huanju.wanka.paysdk.IPayCallbackListener;
import com.huanju.wanka.paysdk.PaySdkProxy;
import com.huanju.wanka.paysdk.ProductInfo;
import com.huanju.wanka.paysdk.UserInfo;


public class GameProxyImpl extends GameProxy {
    private static final int STARY_PAY = 1;

    private Object loginCustomParams;
    private Activity currentActivity;
    private ProductInfo productInfo;
    private PayCallBack payCallBack;
    private String mOrderInfo;
    private static final String CHANNEL = "channel";// 厂商名称
    private static final String AUTH_INFO = "auth_info";// 创建订单是需要传给Server的字段名
    private JSONObject mJsonObject;// 登陆返回的信息
    private JSONObject mLoginJsonObject;// 验证token返回的信息

    Handler mHandler = new Handler()
    {
        public void handleMessage( android.os.Message msg )
        {
            if (msg.what == STARY_PAY)
            {
                PaySdkProxy.getInstance().pay(currentActivity, productInfo,
                        mOrderInfo, new IPayCallbackListener()
                        {

                            @ Override
                            public void onSuccess( ProductInfo lastProductInfo )
                            {
                                Toast.makeText(currentActivity, "当前支付成功",
                                    Toast.LENGTH_LONG).show();
                                payCallBack.onSuccess("支付成功");
                            }

                            @ Override
                            public void onFail( ProductInfo lastProductInfo,
                                String paramString )
                            {
                                Toast.makeText(currentActivity,
                                    "当前支付失败： " + paramString,
                                    Toast.LENGTH_LONG).show();
                                payCallBack.onFail("支付失败：" + paramString);
                            }
                        });
            }
        };
    };

    @Override
    public void applicationInit(Activity activity) {
        Log.v("sdk", "applicationInit");

        currentActivity = activity;
        PaySdkProxy.getInstance().applicationInit(activity);
        PaySdkProxy.getInstance().onCreate(activity);
        PaySdkProxy.getInstance().setUserListener(activity,
                new ILoginCallbackListener()
                {
                    @ Override
                    public void onLogout( Object paramObject )
                    {
                        userListerner.onLogout(null);
                    }

                    @ Override
                    public void onLoginSuccess( final UserInfo paramXMUser,
                            String loginResult )
                    {
                        Log.i("sdk", "login success:" + loginResult);
                        User u = new User();
                        u.token = loginResult;
                        userListerner.onLoginSuccess(u, loginCustomParams);
                        try
                        {
                            mJsonObject = new JSONObject(loginResult);
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @ Override
                    public void onLoginFailed( String paramString,
                            Object paramObject )
                    {
                        Log.i("MyView", "onLoginFailed");
                        userListerner.onLoginFailed(paramString, loginCustomParams);
                    }
                });
    }

    @Override
    public void onCreate(Activity activity) {
        Log.v("sdk", "onCreate");
    }

    @Override
    public void login(Activity activity,Object customParams) {
        Log.v("sdk", "login");
        loginCustomParams = customParams;

        // 调用SDK执行登陆操作
        PaySdkProxy.getInstance().login(activity, null);
    }

    @Override
    public void logout(Activity activity,Object customParams) {
        Log.v("sdk", "logout");
        userListerner.onLogout(customParams);
        PaySdkProxy.getInstance().loginSwitch(activity, null);
    }

    @Override
    public void pay(final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        Log.v("sdk", "pay:" + ID + "," + name + "," + orderID + "," + price + "," + callBackInfo + "," + roleInfo.toString());
        this.payCallBack = payCallBack;

        String roleName = "";
        try {
            roleName = roleInfo.getString("name");
            mLoginJsonObject = new JSONObject(roleInfo.getString("extra"));
        } catch (JSONException e) {
            ;
        }
        productInfo = new ProductInfo(name, "元宝", Float.toString(price), 1,
                roleName, ID, callBackInfo + "_" + orderID, callBackInfo);

        new Thread(new Runnable()
            {
                @ Override
                public void run( )
                {
                    getOrderInfo(productInfo);
                }
            }).start();
    }

    @Override
    public void applicationDestroy(Activity activity) {
        Log.v("sdk", "applicationDestroy");
        PaySdkProxy.getInstance().applicationDestroy(activity);
    }

    @Override
    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        PaySdkProxy.getInstance().onDestroy(activity);
    }

    @Override
    public void setExtData(Context context, String ext) {
        Log.v("sdk", "setExtData:" + ext);
    }

    // 为了整合所有支付流程请注意该生命周期必须实现
    @Override
    public void onActivityResult( Activity activity, int requestCode, int resultCode,
            Intent data )
    {
        super.onActivityResult(activity, requestCode, resultCode, data);
        PaySdkProxy.getInstance().onActivityResult(activity,
                requestCode, resultCode, data);
    }

    @Override
    public void onResume(Activity activity )
    {
        super.onResume(activity);
        PaySdkProxy.getInstance().onResume(activity);
    }

    @Override
    public void onPause(Activity activity)
    {
        PaySdkProxy.getInstance().onPause(activity);
        super.onPause(activity);
    }

    /** 支付前去服务端创建订单 */
    private void getOrderInfo( ProductInfo productInfo )
    {
        String sUrl = ((poem)currentActivity).getMetaData("create_order_url");
        if (mLoginJsonObject != null && mLoginJsonObject.length() != 0)
        {
            try
            {
                URL url = new URL(sUrl);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-type",
                        "application/x-www-form-urlencoded");
                Log.d("MyView", mJsonObject.toString());
                connection.setDoOutput(true);// 是否输入参数
                StringBuffer params = new StringBuffer();
                params.append("channel=");
                params.append(enCode(mJsonObject.getString(CHANNEL)));
                params.append("&returnJson=");
                params.append(enCode(mLoginJsonObject.getString(AUTH_INFO)));
                params.append("&productName=");
                params.append(enCode(productInfo.getProductName()));
                params.append("&description=");
                params.append(enCode(productInfo.getDescription()));
                params.append("&amount=");
                params.append(enCode(productInfo.getAmount()));
                params.append("&number=");
                params.append(productInfo.getNumber());
                params.append("&manufacturerName=");
                params.append(enCode(productInfo.getManufacturerName()));
                params.append("&id=");
                params.append(enCode(productInfo.getId()));
                params.append("&orderid=");
                params.append(enCode(productInfo.getOrderid()));
                params.append("&cpPrivateInfo=");
                if (productInfo.getCpPrivateInfo() != null)
                {
                    params.append(enCode(productInfo.getCpPrivateInfo()));
                }
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
                Log.i("MyView", "getOrderInfo: " + readbuff.toString());
                connection.disconnect();
                reader.close();
                mOrderInfo = readbuff.toString();
                Log.i("MyView", "getOrderInfo: " + mOrderInfo);
                mHandler.sendEmptyMessage(STARY_PAY);

            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (JSONException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
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
