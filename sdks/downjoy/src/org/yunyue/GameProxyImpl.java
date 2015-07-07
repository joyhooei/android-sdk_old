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

import com.downjoy.CallbackListener;
import com.downjoy.CallbackStatus;
import com.downjoy.Downjoy;
import com.downjoy.InitListener;
import com.downjoy.LoginInfo;
import com.downjoy.LogoutListener;
import com.downjoy.UserInfo;
import com.downjoy.util.Util;

public class GameProxyImpl extends GameProxy{
    private Context mContext;
    private Downjoy downjoy; //当乐游戏中心实例
    // CP后台可以查询到前三个参数MERCHANT_ID、APP_ID和APP_KEY.
    // 不同服务器序列号可使用不同计费通知地址
    private static final String merchantId = "${MERCHANT_ID}"; // 当乐分配的MERCHANT_ID
    private static final String appId = "${APPID}"; // 当乐分配的APP_ID
    private static final String appKey = "${APPKEY}"; // 当乐分配的 APP_KEY
    private static final String serverSeqNum = "1"; // 此参数自定义，需登录CP后台配置支付通知回调，其中的服务器序号就是serverSeqNum

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return true;
    }

    public boolean supportPay() {
        return true;
    }

    public void applicationInit(Activity activity) {
        mContext = activity;
        initDownjoy();
    }

    private void initDownjoy() {
        downjoy = Downjoy.getInstance(mContext, merchantId, appId,
                serverSeqNum, appKey, new InitListener() {

                    @Override
                    public void onInitComplete() {
                        //downjoyLogin();
                    }
                });

        // 设置登录成功后属否显示当乐游戏中心的悬浮按钮
        // 注意：
        // 此处应在调用登录接口之前设置，默认值是true，即登录成功后自动显示当乐游戏中心的悬浮按钮。
        // 如果此处设置为false，登录成功后，不显示当乐游戏中心的悬浮按钮。
        // 正常使用悬浮按钮还需要实现两个函数onResume、onPause
        downjoy.showDownjoyIconAfterLogined(true);
        downjoy.setInitLocation(Downjoy.LOCATION_RIGHT_CENTER_VERTICAL);
        downjoy.setLogoutListener(new LogoutListener() {
            @Override
            public void onLogoutSuccess() {
                userListerner.onLogout(null);
            }

            @Override
            public void onLogoutError(String msg) {
                Log.e("sdk", "logout error:" + msg);
            }
        });
    }

    public void login(Activity activity, final Object customParams) {
        downjoy.openLoginDialog(activity,
                new CallbackListener<LoginInfo>() {
                    @Override
                    public void callback(int status, LoginInfo data) {
                        if (status == CallbackStatus.SUCCESS && data != null) {
                            User u = new User();
                            u.userID = data.getUmid();
                            u.token = data.getToken();
                            userListerner.onLoginSuccess(u, customParams);
                        } else if (status == CallbackStatus.FAIL && data != null) {
                            userListerner.onLoginFailed("", customParams);
                        } else if (status == CallbackStatus.CANCEL && data != null) {
                            userListerner.onLoginFailed("", customParams);
                        }
                    }
                });
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        if (downjoy != null) {
            downjoy.resume(activity);
        }
    }

    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
        if (downjoy != null) {
            downjoy.pause();
        }
    }

    @Override
    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        if (downjoy != null) {
            downjoy.destroy();
            downjoy = null;
        }
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        String serverName = "Serer001";
        String playerName = "Player001";
        try {
            playerName = roleInfo.getString("name");
            serverName = roleInfo.getString("serverID");
        } catch (JSONException e) {
            Log.e("sdk", "get role info failed");
            return;
        }
        // 打开支付界面,获得订单号
        downjoy.openPaymentDialog(activity, price, name, "",
                orderID, serverName, playerName, new CallbackListener<String>() {
                    @Override
                    public void callback(int status, String data) {
                        if (status == CallbackStatus.SUCCESS) {
                            payCallBack.onSuccess("");
                        } else if (status == CallbackStatus.FAIL) {
                            Log.e("sdk", "pay failed:" + data);
                            payCallBack.onFail("");
                        }
                    }
                });
    }

    public void exit(Activity activity, ExitCallback callback) {
        downjoy.openExitDialog(activity,
                new CallbackListener<String>() {
                    @Override
                    public void callback(int status, String data) {
                        if (CallbackStatus.SUCCESS == status) {
                            poem.quitApplication();
                        } else if (CallbackStatus.CANCEL == status) {
                            Log.e("sdk", "exit canceled");
                        }
                    }
                });
    }

    public void openCommunity(Activity activity) {
        downjoy.openMemberCenterDialog(activity);
    }
}
