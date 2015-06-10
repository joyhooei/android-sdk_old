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

public class GameProxyImpl extends GameProxy{
    private static final int CLIENT_ID = ${CLIENTID};

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return false;
    }

    public boolean supportPay() {
        return false;
    }

    public void login(Activity activity, final Object customParams) {
        EgameUser.start(activity, CLIENT_ID, new CallBackListener() {
            public void onSuccess(String info) {
                User u = new User();
                u.token = info;
                userListerner.onLoginSuccess(u, customParams);
            }
            public void onFailed(int code) {
                Log.v("sdk", "login failed:"+code);
                userListerner.onLoginFailed("登录失败", customParams);
            }
            public void onCancel() {
                userListerner.onLoginFailed("取消登录", customParams);
            }
        });
    }
}
