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

import com.tencent.msdk.WeGame;
import com.tencent.msdk.api.LoginRet;
import com.tencent.msdk.api.MsdkBaseInfo;
import com.tencent.msdk.api.WGPlatform;
import com.tencent.msdk.api.WGQZonePermissions;
import com.tencent.msdk.consts.CallbackFlag;
import com.tencent.msdk.consts.EPlatform;
import com.tencent.msdk.consts.TokenType;
import com.tencent.msdk.tools.Logger;

import com.tencent.msdk.api.ShareRet;
import com.tencent.msdk.api.TokenRet;
import com.tencent.msdk.api.WGPlatformObserver;
import com.tencent.msdk.api.WakeupRet;

public class GameProxyImpl extends GameProxy implements WGPlatformObserver{
    private Object loginCustomParams;
    private Activity currentActivity;

	@Override
    public void onCreate(Activity activity) {
        currentActivity = activity;
        super.onCreate(activity);

        // TODO GAME 游戏需自行检测自身是否重复, 检测到吃重复的Activity则要把自己finish掉
        // 注意：游戏需要加上去重判断finish重复的实例，否则可能发生重复拉起游戏的问题。游戏可自行决定重复的判定。
        if (WGPlatform.IsDifferentActivity(activity)) {
            Logger.d("Warning!Reduplicate game activity was detected.Activity will finish immediately.");
            activity.finish();
            return;
        }

        /***********************************************************
         *  TODO GAME 接入必须要看， baseInfo值因游戏而异，填写请注意以下说明:
         *  	baseInfo值游戏填写错误将导致 QQ、微信的分享，登录失败 ，切记 ！！！
         * 		只接单一平台的游戏请勿随意填写其余平台的信息，否则会导致公告获取失败
         *      offerId 为必填，一般为手QAppId
         ***********************************************************/
        MsdkBaseInfo baseInfo = new MsdkBaseInfo();
        baseInfo.qqAppId = "1104480701";
        baseInfo.qqAppKey = "R8U6PCBOw3sX64H0";
        baseInfo.wxAppId = "wx2ab37fb74e206f3d";
        baseInfo.wxAppKey = "c032d3e153a0e477fae08328153e35cf";
        //订阅型测试用offerId
        baseInfo.offerId = "1104480701";

        // 注意：传入Initialized的activity即this，在游戏运行期间不能被销毁，否则会产生Crash
        WGPlatform.Initialized(activity, baseInfo);
        // 设置拉起QQ时候需要用户授权的项
		WGPlatform.WGSetPermission(WGQZonePermissions.eOPEN_ALL);

        // 全局回调类，游戏自行实现
        WGPlatform.WGSetObserver(this);

        WGPlatform.WGLoginWithLocalInfo();

        // launchActivity的onCreat()和onNewIntent()中必须调用
        // WGPlatform.handleCallback()。否则会造成微信登录无回调
        if (WGPlatform.wakeUpFromHall(activity.getIntent())) {
        	// 拉起平台为大厅
        	Logger.d("LoginPlatform is Hall");
            Logger.d(activity.getIntent());
            WGPlatform.handleCallback(activity.getIntent());
        } else {
        	// 拉起平台不是大厅
            Logger.d("LoginPlatform is not Hall");
            Logger.d(activity.getIntent());
            WGPlatform.handleCallback(activity.getIntent());
        }

    }

    @Override
    public void onRestart(Activity activity) {
        super.onRestart(activity);
        WGPlatform.onRestart();
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        WGPlatform.onResume();
    }

    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
        WGPlatform.onPause();
    }

    // TODO GAME 游戏需要集成此方法并调用WGPlatform.onStop()
    @Override
    public void onStop(Activity activity) {
        super.onStop(activity);
        WGPlatform.onStop();
    }

    @Override
    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        WGPlatform.onDestory(activity);
    }

    @Override
	public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		super.onActivityResult(activity, requestCode, resultCode, data);
		WGPlatform.onActivityResult(requestCode, resultCode, data);
	}

    @Override
    public void onNewIntent(Activity activity, Intent intent) {
        super.onNewIntent(activity, intent);

        // TODO GAME 处理游戏被拉起的情况
        // launchActivity的onCreat()和onNewIntent()中必须调用
        // WGPlatform.handleCallback()。否则会造成微信登录无回调
        if (WGPlatform.wakeUpFromHall(intent)) {
            Logger.d("LoginPlatform is Hall");
            Logger.d(intent);
            WGPlatform.handleCallback(intent);
        } else {
            Logger.d("LoginPlatform is not Hall");
            Logger.d(intent);
            WGPlatform.handleCallback(intent);
        }
    }

    public void login(Activity activity, Object customParams) {
        loginCustomParams = customParams;
        if ((String)customParams == "qq") {
            WGPlatform.WGLogin(EPlatform.ePlatform_QQ);
        }
        else { // weixin
            WGPlatform.WGLogin(EPlatform.ePlatform_Weixin);
        }
    }

    // 平台授权成功,让用户进入游戏. 由游戏自己实现登录的逻辑
	public void letUserLogin() {
		LoginRet ret = new LoginRet();
        WGPlatform.WGGetLoginRecord(ret);
        Logger.d("flag: " + ret.flag);
        Logger.d("platform: " + ret.platform);
        if(ret.flag != CallbackFlag.eFlag_Succ){
    		Toast.makeText(currentActivity, "UserLogin error!!!",
                    Toast.LENGTH_LONG).show();
    		Logger.d("UserLogin error!!!");
            letUserLogout();
            return;
    	}
        User u = new User();
        userListerner.onLoginSuccess(u, loginCustomParams);
	}

	// 登出后, 更新view. 由游戏自己实现登出的逻辑
	public void letUserLogout() {
		WGPlatform.WGLogout();
        userListerner.onLogout(null);
	}

    // 回调

    // 发送结果到结果展示界面
    public static void sendResult(String result) {
        /*
        Intent sendResult = new Intent(LOCAL_ACTION);
        sendResult.putExtra("Result", result);
        Logger.d("send: "+ result);
        */
    }

    public void OnLoginNotify(LoginRet ret) {
        Logger.d("called");
        Logger.d("ret.flag" + ret.flag);
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
                letUserLogin();
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
                letUserLogout();
                break;
        }
    }

    public void OnShareNotify(ShareRet ret) {
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
                userListerner.onLoginFailed("登录失败", loginCustomParams);
                break;

            default:
            	// 分享失败处理
                Logger.d(ret.desc);
                result = "Share faild: \n" + ret.toString();
                break;
        }
        // 发送结果到结果展示界面
        sendResult(result);
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
            letUserLogin();
        } else if (CallbackFlag.eFlag_UrlLogin == ret.flag) {
            // 用拉起的账号登录，登录结果在OnLoginNotify()中回调
        } else if (ret.flag == CallbackFlag.eFlag_NeedSelectAccount) {
            // 异账号时，游戏需要弹出提示框让用户选择需要登陆的账号
            Logger.d("diff account");
            //showDiffLogin();
        } else if (ret.flag == CallbackFlag.eFlag_NeedLogin) {
            // 没有有效的票据，登出游戏让用户重新登录
            Logger.d("need login");
            letUserLogout();
        } else {
            Logger.d("logout");
            letUserLogout();
        }
    }
}
