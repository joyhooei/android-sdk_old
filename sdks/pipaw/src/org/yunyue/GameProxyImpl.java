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
import java.text.DecimalFormat;

import com.pipaw.pipawpay.PipawLoginListener;
import com.pipaw.pipawpay.PipawSDK;

public class GameProxyImpl extends GameProxy{
    private String merchantId = "${MERCHANT_ID}";
    private String merchantAppId = "${MERCHANT_APPID}";
    private String appId = "${APPID}";
    private String privateKey = "${PRIVATE_KEY}";

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    public void onResume(Activity activity) {
        super.onResume(activity);
        PipawSDK.getInstance().onResume(activity);
    }

    public void onPause(Activity activity) {
        super.onPause(activity);
        PipawSDK.getInstance().onPause(activity);
    }

    public void applicationDestroy(Activity activity) {
        super.onDestroy(activity);
        PipawSDK.getInstance().onDestroy(activity);
    }

    public void exit(Activity activity, ExitCallback callback) {
        PipawSDK.getInstance().exitSDK(activity, new Handler(),
                new PipawExitListener() {
                    @Override
                    public void callback(int resultCode, String data) {
                        if (resultCode == PipawSDK.EXIT_OK) {
                            /**
                             * 用户退出游戏
                             */
                            //Toast.makeText(MainActivity.this, "退出游戏",
                            //    Toast.LENGTH_SHORT).show();
                            quitApplication();
                        } else if (resultCode == PipawSDK.EXIT_CANCEL) {
                            /**
                             * 用户继续游戏
                             */
                            //Toast.makeText(MainActivity.this, "继续游戏",
                            //    Toast.LENGTH_SHORT).show();
                        }
                    }
        });
    }

    public void pay(final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        PipawPayRequest pipawPayRequest = new PipawPayRequest();
        pipawPayRequest.setMerchantId(merchantId);
        pipawPayRequest.setMerchantAppId(merchantAppId);
        pipawPayRequest.setAppId(appId);

        String payerId;
        try {
            payerId = roleInfo.getString("id");
        } catch (JSONException e) {
            return;
        }
        pipawPayRequest.setPayerId(payerId);

        pipawPayRequest.setExOrderNo(orderID);
        pipawPayRequest.setSubject(name);

        DecimalFormat df = new DecimalFormat("0.0");
        String sprice = df.format(price);
        pipawPayRequest.setPrice(sprice);
        pipawPayRequest.setExtraParam(callBackInfo);
        /**
         * 将merchantId，merchantAppId，appId，payerId，orderID，subject，price，
         * extraParam，privateKey直接连接起来获取md5签名值。
         * 建议:签名在商户服务器端进行，同时商户私钥也应存储在服务器端，避免可能的安全隐患。
         */
        StringBuilder content = new StringBuilder();
        content.append(merchantId).append(merchantAppId).append(appId)
            .append(payerId).append(orderID).append(name)
            .append(sprice).append(callBackInfo).append(privateKey);
        Log.d(TAG, "content " + content);
        String merchantSign = Md5Util.getMd5(content.toString());
        Log.d(TAG, "merchantSign " + merchantSign);
        /**
         * merchantSign 交易签名
         */
        pipawPayRequest.setMerchantSign(merchantSign);
        PipawSDK.getInstance().pay(this, pipawPayRequest,
                new PipawPayListener() {

                    /**
                     * 客户端同步通知 可选操作，请以服务端异步通知为准。
                     */
                    @Override
                    public void callback(int resultCode, String data) {
                        if (resultCode == PipawSDK.PAY_CANCEL) {
                            /**
                             * 用户取消支付
                             */
                            //Toast.makeText(activity, "取消支付",
                            //    Toast.LENGTH_SHORT).show();
                            payCallBack.onFail("取消支付");
                        } else if (resultCode == PipawSDK.PAY_SUCCESS) {
                            /**
                             * 支付成功
                             */
                            //Toast.makeText(activity, "支付成功",
                            //    Toast.LENGTH_SHORT).show();
                            payCallBack.onSuccess("支付成功");
                        } else if (resultCode == PipawSDK.PAY_FAIL) {
                            /**
                             * 支付失败
                             */
                            //Toast.makeText(activity, "支付失败",
                            //        Toast.LENGTH_SHORT).show();
                            //if (data != null) {
                            //    Toast.makeText(activity, data,
                            //            Toast.LENGTH_SHORT).show();
                            //}
                            payCallBack.onFail(data);
                        }
                    }
                });
    }

    public void login(Activity activity, final Object customParams) {
        PipawSDK.getInstance().login(activity, merchantId, merchantAppId, appId,
                new PipawLoginListener() {

                    @Override
                    public void callback(int resultCode, String data) {
                        if (resultCode == PipawSDK.LOGIN_EXIT) {
                            /**
                             * 退出登录
                             */
                            //Toast.makeText(UserActivity.this, "退出登录",
                            //    Toast.LENGTH_SHORT).show();
                            userListerner.onLogout(null);
                        } else if (resultCode == PipawSDK.LOGIN_SUCCESS) {
                            /**
                             * 登录成功
                             */
                            //Toast.makeText(UserActivity.this, "登录成功",
                            //    Toast.LENGTH_SHORT).show();
                            //    /**
                            //     * 返回包含username，sid，time的json对象。
                            //     * 游戏服务端可通过merchantId，merchantAppId
                            //     * ，appId，username，sid，time向 支付SDK服务端请求验证sid。
                            //     * 注：sid的有效时间为1小时，游戏服务端须在1小时内完成sid验证。
                            //     */
                            //    Toast.makeText(UserActivity.this, data,
                            //            Toast.LENGTH_SHORT).show();
                            User u = new User();
                            u.token = data;
                            userListerner.onLoginSuccess(u, customParams);
                        } else if (resultCode == PipawSDK.LOGIN_FAIL) {
                            /**
                             * 登录失败
                             */
                            //Toast.makeText(UserActivity.this, "登录失败",
                            //        Toast.LENGTH_SHORT).show();
                            //if (data != null) {
                            //    Toast.makeText(UserActivity.this, data,
                            //            Toast.LENGTH_SHORT).show();
                            //}
                            userListerner.onLoginFailed(data, customParams);
                        }
                    }
                });
    }
};
