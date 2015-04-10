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

import com.huanju.wanka.paysdk.ILoginCallbackListener;
import com.huanju.wanka.paysdk.IPayCallbackListener;
import com.huanju.wanka.paysdk.PaySdkProxy;
import com.huanju.wanka.paysdk.ProductInfo;
import com.huanju.wanka.paysdk.UserInfo;


public class GameProxyImpl extends GameProxy {
    private Object loginCustomParams;

    @Override
    public void applicationInit(Activity activity) {
        Log.v("sdk", "applicationInit");

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
        Log.v("sdk", "pay:" + ID + "," + name + "," + price + "," + callBackInfo + "," + roleInfo.toString());

        String roleName = "";
        try {
            roleName = roleInfo.getString("name");
        } catch (JSONException e) {
            ;
        }
        ProductInfo productInfo = new ProductInfo(name, "元宝", Float.toString(price), 1,
                roleName, ID, orderID, callBackInfo);

        PaySdkProxy.getInstance().pay(activity, productInfo,
                        "", new IPayCallbackListener()
                        {

                            @ Override
                            public void onSuccess( ProductInfo lastProductInfo )
                            {
                                Toast.makeText(activity, "当前支付成功",
                                        Toast.LENGTH_LONG).show();
                                payCallBack.onSuccess("支付成功");
                            }

                            @ Override
                            public void onFail( ProductInfo lastProductInfo,
                                    String paramString )
                            {
                                Toast.makeText(activity,
                                        "当前支付失败： " + paramString,
                                        Toast.LENGTH_LONG).show();
                                payCallBack.onFail("支付失败：" + paramString);
                            }
                        });
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

}
