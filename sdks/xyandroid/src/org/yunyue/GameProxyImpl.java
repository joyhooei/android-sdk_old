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

import com.kingnet.xyplatform.source.app.XYGameSDKStatusCode;
import com.kingnet.xyplatform.source.app.XYSDKGames;
import com.kingnet.xyplatform.source.interfaces.OnDialogCallBackListener;
import com.kingnet.xyplatform.source.interfaces.XYSDKCallBackListener;
import com.kingnet.xyplatform.source.interfaces.XYSDKCloseRechargeListener;
import com.kingnet.xyplatform.source.interfaces.XYSDKExitAccountListener;
import com.kingnet.xyplatform.source.interfaces.XYSDKExitAppListener;
import com.kingnet.xyplatform.source.interfaces.XYSDKExitPlatfromListener;
import com.kingnet.xyplatform.source.interfaces.XYSDKVisitorToUserListener;
import com.kingnet.xyplatform.source.bean.XYSDKPayBean;
import com.kingnet.xyplatform.source.interfaces.XYSDKCallBackListener;
import com.kingnet.xyplatform.source.interfaces.XYSDKExitAccountByGameListener;

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

    public boolean supportLogout() {
        return true;
    }

    public void applicationInit(Activity activity) {
		//初始化SDK
		XYSDKGames.initSDK(activity.getApplicationContext());
		//添加悬浮框
		XYSDKGames.addHoverImageView();
        //从XYSDK退出
        XYSDKGames.XYExitAccount(activity,new XYSDKExitAccountListener() {
            @Override
            // account: 退出的账号
            // uidString :退出账号的uid
            public void exit(String account,String uidString) {
                userListerner.onLogout(null);
            }
        });
    }

    public void login(final Activity activity, final Object customParams) {
        //登录回调<登录页点击左上角返回和注册页面点击右上角X也走该回调>（主动进入SDK）
        XYSDKGames.XYLogin(activity, new XYSDKCallBackListener() {
            @Override
            public void callback(int code, String msg) {
                switch (code) {
                    case XYGameSDKStatusCode.CONNECTION_PARAMS_ERROR:
                        userListerner.onLoginFailed("", customParams);
                        break;
                    case XYGameSDKStatusCode.SUCCESS:
                        User u = new User();
                        u.userID = XYSDKGames.getXYUid();
                        u.token = XYSDKGames.getXYUserToken();
                        userListerner.onLoginSuccess(u, customParams);
                        XYSDKGames.showMIUIDialog(activity, "${APPNAME}");
                        break;
                    default:
                        break;
                }
            }
        });

    }

    public void exit(Activity activity, ExitCallback callback) {
        XYSDKGames.XYExitApp(activity, new XYSDKExitAppListener() {
            @Override
            public void exitApp() {
                poem.quitApplication();
            }
        });
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        //如果该页面必定不需要显示悬浮框，例如启动页，加载页等，此处可调用下面的方法,其他需要显示悬浮框的页面的该生命周期方法中更改成XYSDKGames.showHoverImageView()，可参见demo的PayActivity;
        XYSDKGames.showHoverImageView();
    }

    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
        XYSDKGames.hideHoverImageView();
    }

    @Override
    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        //如果此处（退出游戏页面的销毁生命周期方法中）调用了该方法，表示彻底销毁了悬浮框，而不是隐藏，下次显示则需要重新构建，
        //那么需要在第一个页面的OnCreate()方法中添加XYSDKGames.addHoverImageView()，而不是在Application的OnCreate()方法中添加(可见本Activity的L39行)，因为Application的OnCreate()并不会每次游戏启动都会执行。
        XYSDKGames.removeHoverImageView();
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        XYSDKGames.XYPayResult(activity,new XYSDKPayBean("${APPNAME}",name, String.valueOf((int)price), "_" + callBackInfo + "_" + orderID, 0),new XYSDKCallBackListener() {
            @Override
            public void callback(int code, String msg) {
                switch (code) {
                    case XYGameSDKStatusCode.PAY_SUCCESS_CODE:
                        payCallBack.onSuccess("");
                        break;
                    case XYGameSDKStatusCode.PAY_FAIL_CODE:
                        payCallBack.onFail("");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void logout(Activity activity,Object customParams) {
        XYSDKGames.XYExitAccountByGame(activity, new XYSDKExitAccountByGameListener() {
            @Override
            public void exitAccount(String account, String uidString) {
                userListerner.onLogout(null);
            }
        }, false);
    }
}
