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

import com.pptv.vassdk.agent.PptvVasAgent;
import com.pptv.vassdk.agent.listener.ExitDialogListener;
import com.pptv.vassdk.agent.listener.LoginListener;
import com.pptv.vassdk.agent.listener.PayListener;
import com.pptv.vassdk.agent.model.LoginResult;
import com.pptv.vassdk.agent.model.PayResult;
import com.pptv.vassdk.agent.utils.CfgUtil;
import com.pptv.vassdk.cfg.Debug;
import com.pptv.vassdk.utils.PayUtil;

public class GameProxyImpl extends GameProxy{

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return true;
    }

    @Override
    public void onDestroy(Activity activity)
    {
        PptvVasAgent.releseFloatViewWindow();
        super.onDestroy(activity);
    }

    public void applicationInit(Activity activity) {
        PptvVasAgent.showFloatingView(activity);
    }

    public void exit(Activity activity, ExitCallback callback) {
        PptvVasAgent.onExit(activity, new ExitDialogListener()
            {
                @Override
                public void onExit()
                {
                    // TODO Auto-generated method stub
                    poem.quitApplication();
                }

                @Override
                public void onContinue()
                {
                    // TODO Auto-generated method stub
                    //Toast.makeText(PptvVasSDKExampleActivity.this, "continue", Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        PptvVasAgent.startPayActivity(activity, Debug.SID, Debug.RID, Debug.EXTRA,
                Debug.PayNotifyUrlVer, price, name, new PayListener() {
                    @Override
                    public void onPayFinish()
                    {
                        //Toast.makeText(PptvVasSDKExampleActivity.this, "[demo]玩家支付结束", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPaySuccess(PayResult payResult)
                    {
                        payCallBack.onSuccess("");
                        //Toast.makeText(PptvVasSDKExampleActivity.this, "[demo]玩家支付成功:" + payResult.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPayFail(PayResult payResult)
                    {
                        payCallBack.onFail("");
                        //Toast.makeText(PptvVasSDKExampleActivity.this, "[demo]玩家支付失败:" + payResult, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPayWait(PayResult arg0)
                    {
                        // TODO Auto-generated method stub

                    }
        });
    }

    public void login(Activity activity,Object customParams) {
        PptvVasAgent.startLoginActivity(activity, new LoginListener() {

            @Override
            public void onLoginCancel()
            {
                userListerner.onLoginFailed("", null);
                //Toast.makeText(PptvVasSDKExampleActivity.this, "[demo]玩家取消登录", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoginSuccess(LoginResult loginResult)
            {
                //sout(loginResult.toJsonString());
                //resulttv.setText("登陆返回数据:\n" + loginResult.toJsonString());
                User u = new User();
                // TODO
                userListerner.onLoginSuccess(u, null);
            }

        });
    }
}
