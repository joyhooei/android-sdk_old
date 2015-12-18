package org.weilan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.widget.Toast;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.tencent.msdk.api.LoginRet;
import com.tencent.msdk.api.ShareRet;
import com.tencent.msdk.api.TokenRet;
import com.tencent.msdk.api.WGPlatformObserver;
import com.tencent.msdk.api.WakeupRet;
import com.tencent.msdk.consts.CallbackFlag;
import com.tencent.msdk.consts.TokenType;
import com.tencent.msdk.remote.api.RelationRet;
import com.tencent.msdk.tools.Logger;

/**
 * TODO GAME 游戏需要根据自己的逻辑实现自己的MsdkCallback对象。
 * MSDK通过WGPlatformObserver抽象类中的方法将授权、分享或查询结果回调给游戏。
 * 游戏根据回调结果调整UI等。只有设置回调，游戏才能收到MSDK的响应。
 * 这里是Java层回调(设置了Java层回调会优先调用Java层回调, 如果要使用C++层回调则不能设置Java层回调)
 */
public class MsdkCallback implements WGPlatformObserver {
    public static GameProxyImpl proxy;
    private static LocalBroadcastManager lbm;
    public static final String LOCAL_ACTION = "com.weilan.nzgl";

    MsdkCallback(GameProxyImpl proxy) {
        this.proxy = proxy;
        lbm = LocalBroadcastManager.getInstance(proxy.currentActivity.getApplicationContext());
    }

    /*
    // 发送结果到结果展示界面
    public static void sendResult(String result) {
        if(lbm != null) {
            Intent sendResult = new Intent(LOCAL_ACTION);
            sendResult.putExtra("Result", result);
            Logger.d("send: "+ result);
            lbm.sendBroadcast(sendResult);
        }
    }
    */

    public void OnLoginNotify(LoginRet ret) {
        Logger.d("cocos >>>>>>>>>>>> OnLoginNotify");
        Logger.d("cocos =========" + ret.flag);
        switch (ret.flag) {
            case CallbackFlag.eFlag_Succ:
                // 登陆成功, 读取各种票据
                String openId = ret.open_id;
                String pf = ret.pf;
                String pfKey = ret.pf_key;
                String wxAccessToken = "";
                long wxAccessTokenExpire = 0;
                String wxRefreshToken = "";
                long wxRefreshTokenExpire = 0;
                for (TokenRet tr : ret.token) {
                    switch (tr.type) {
                        case TokenType.eToken_WX_Access:
                            wxAccessToken = tr.value;
                            wxAccessTokenExpire = tr.expiration;
                            break;
                        case TokenType.eToken_WX_Refresh:
                            wxRefreshToken = tr.value;
                            wxRefreshTokenExpire = tr.expiration;
                            break;
                        default:
                            break;
                    }
                }
                proxy.letUserLogin();
                break;
            // 游戏逻辑，对登陆失败情况分别进行处理
            case CallbackFlag.eFlag_WX_UserCancel:
            case CallbackFlag.eFlag_WX_NotInstall:
            case CallbackFlag.eFlag_WX_NotSupportApi:
            case CallbackFlag.eFlag_WX_LoginFail:
            case CallbackFlag.eFlag_Local_Invalid:
            	Logger.d(ret.desc);
            default:
                // 显示登陆界面
                proxy.letUserLogout();
                break;
        }
    }

    public void OnShareNotify(ShareRet ret) {
        /*
        Logger.d("called");
        String result = "";
        switch (ret.flag) {
            case CallbackFlag.eFlag_Succ:
                // 分享成功
                result = "Share success\n" + ret.toString();
                break;
            case CallbackFlag.eFlag_QQ_UserCancel:
            case CallbackFlag.eFlag_QQ_NetworkErr:
            case CallbackFlag.eFlag_WX_UserCancel:
            case CallbackFlag.eFlag_WX_NotInstall:
            case CallbackFlag.eFlag_WX_NotSupportApi:

            default:
                // 分享失败处理
                Logger.d(ret.desc);
                result = "Share faild: \n" + ret.toString();
                break;
        }
        // 发送结果到结果展示界面
        sendResult(result);
        */
    }

    public void OnWakeupNotify(WakeupRet ret) {
        Logger.d("called");
        Logger.d(ret.toString() + ":flag:" + ret.flag);
        Logger.d(ret.toString() + "desc:" + ret.desc);
        Logger.d(ret.toString() + "platform:" + ret.platform);
        // TODO GAME 游戏需要在这里增加处理异账号的逻辑
        if (CallbackFlag.eFlag_Succ == ret.flag
                || CallbackFlag.eFlag_AccountRefresh == ret.flag) {
            //eFlag_AccountRefresh代表 刷新微信票据成功
            Logger.d("login success flag:" + ret.flag);
            proxy.letUserLogin();
        } else if (CallbackFlag.eFlag_UrlLogin == ret.flag) {
            // 用拉起的账号登录，登录结果在OnLoginNotify()中回调
        } else if (ret.flag == CallbackFlag.eFlag_NeedSelectAccount) {
            // 异账号时，游戏需要弹出提示框让用户选择需要登陆的账号
            Logger.d("diff account");
            proxy.showDiffLogin();
        } else if (ret.flag == CallbackFlag.eFlag_NeedLogin) {
            // 没有有效的票据，登出游戏让用户重新登录
            Logger.d("need login");
            proxy.letUserLogout();
        } else {
            Logger.d("logout");
            proxy.letUserLogout();
        }
    }

    @Override
    public void OnRelationNotify(RelationRet relationRet) {
    	String result = relationRet.toString();
        Logger.d("OnRelationNotify" + result);
        // 发送结果到结果展示界面
        //sendResult(result);
    }


    @Override
    public String OnCrashExtMessageNotify() {
        // 此处游戏补充crash时上报的额外信息
        /*
        Logger.d(String.format(Locale.CHINA, "OnCrashExtMessageNotify called"));
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return "new Upload extra crashing message for bugly on " + time.format(nowTime);
        */
        return "";
    }

}
