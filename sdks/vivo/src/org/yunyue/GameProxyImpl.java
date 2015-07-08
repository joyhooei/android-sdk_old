package org.yunyue;

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

import com.vivo.account.base.activity.LoginActivity;
import com.bbk.payment.PaymentActivity;

class ProductInfo {
    public String productName;
    public String productDesc;
    public String price;
    public String userName;
    public String goodsID;
    public String orderID;
    public String callBackInfo;

    public ProductInfo(String productName, String productDesc, String price, String userName, String goodsID, String orderID, String callBackInfo) {
        productName = productName;
        productDesc = productDesc;
        price = price;
        userName = userName;
        goodsID = goodsID;
        orderID = orderID;
        callBackInfo = callBackInfo;
    }
}

public class GameProxyImpl extends GameProxy {

    private static final int START_PAY = 1;
    private String mOrderInfo;

    Handler mHandler = new Handler()
    {
        public void handleMessage( android.os.Message msg )
        {
            if (msg.what == START_PAY)
            {
                Bundle localBundle = new Bundle();
                localBundle.putString("transNo", orderID);// 交易流水号，由订单推送接口返回
                localBundle.putString("accessKey", accessKey);// 由订单推送接口返回
                localBundle.putString("productName", "商品名称");//商品名称
                localBundle.putString("productDes", "商品描述");//商品描述
                localBundle.putLong("price", 1000);//价格,单位为分（1000即10.00元）
                localBundle.putString("appId", appid);// appid为vivo开发者平台中生成的App ID

                // 以下为可选参数，能收集到务必填写，如未填写，掉单、用户密码找回等问题可能无法解决。
                localBundle.putString("blance", "100元宝");
                localBundle.putString("vip", "vip2");
                localBundle.putInt("level", 35);
                localBundle.putString("party", "工会");
                localBundle.putString("roleId", "角色id");
                localBundle.putString("roleName", "角色名称");
                localBundle.putString("serverName", "区服信息");
                localBundle.putString("extInfo", "扩展参数");
                localBundle.putBoolean("logOnOff", false);// CP在接入过程请传true值,接入完成后在改为false, 传true会在支付SDK打印大量日志信息	 
                Intent target = new Intent(TestActivity.this, PaymentActivity.class);
                target.putExtra("payment_params", localBundle);
                startActivityForResult(target, 1);
            }
        };
    };

    public boolean supportLogin() {
        return true;
    }

    //public boolean supportCommunity() {
    //    return true;
    //}

    public boolean supportPay() {
        return true;
    }

    public void login(Activity activity, Object customParams) {
        Intent loginIntent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(loginIntent, REQUEST_CODE_LOGIN);
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {

        productInfo = new ProductInfo(name, "元宝", df.format(price), 1,
                userName, goodsID, orderID, callBackInfo);

        new Thread(new Runnable()
            {
                @ Override
                public void run( )
                {
                    getOrderInfo(productInfo);
                }
            }).start();
    }

    /** 支付前去服务端创建订单 */
    private void getOrderInfo( ProductInfo productInfo )
    {
        String sUrl = ((poem)currentActivity).getMetaData("create_order_url");
        if (mLoginJson != null)
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
                params.append(enCode(mLoginJson));
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
                mHandler.sendEmptyMessage(START_PAY);

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
}
