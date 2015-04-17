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
import android.support.v4.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.DialogInterface;
import android.app.AlertDialog;

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

public class GameProxyImpl extends GameProxy {
    public Object loginCustomParams;
    public Activity currentActivity;
    public static final String LOCAL_ACTION = "com.yunyue.nzgl";

	public LocalBroadcastManager lbm;
	public BroadcastReceiver mReceiver;

    private long pauseTime = 0;

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
         *      baseInfo值游戏填写错误将导致 QQ、微信的分享，登录失败 ，切记 ！！！
         *         只接单一平台的游戏请勿随意填写其余平台的信息，否则会导致公告获取失败
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
        WGPlatform.WGSetObserver(new MsdkCallback(this));

        //WGPlatform.WGLoginWithLocalInfo();

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


        // 设置局部广播，处理回调信息
        lbm = LocalBroadcastManager.getInstance(activity.getApplicationContext());
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String result = intent.getExtras().getString("Result");
                Logger.d(result);
                //displayResult(result);
            }

        };
        lbm.registerReceiver(mReceiver,  new IntentFilter(MsdkCallback.LOCAL_ACTION));

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
        if (pauseTime != 0 && System.currentTimeMillis() / 1000 - pauseTime > 1800) {
            Logger.d("MsdkStart", "start auto login");
            // 模拟游戏自动登录，这里需要游戏添加加载动画
            // WGLoginWithLocalInfo是一个异步接口, 会到后台验证上次登录的票据是否有效
            WGPlatform.WGLoginWithLocalInfo();
            // 模拟游戏自动登录 END
        } else {
            Logger.d("MsdkStart", "do not start auto login");
        }
    }

    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
        WGPlatform.onPause();
        this.pauseTime = System.currentTimeMillis() / 1000;
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
        if(lbm != null) {
        	lbm.unregisterReceiver(mReceiver);
        }
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
        String param = (String)customParams;
        loginCustomParams = customParams;
        Log.v("sdk", param);
        if (param.compareToIgnoreCase("qq") == 0) {
            Log.v("sdk", "login qq");
            WGPlatform.WGLogin(EPlatform.ePlatform_QQ);
        }
        else { // weixin
            WGPlatform.WGLogin(EPlatform.ePlatform_Weixin);
        }
    }

    // 在异账号时，模拟游戏弹框提示用户选择登陆账号
    public void showDiffLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setTitle("异账号提示");
        builder.setMessage("你当前拉起的账号与你本地的账号不一致，请选择使用哪个账号登陆：");
        builder.setPositiveButton("本地账号",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                            int whichButton) {
                        Toast.makeText(currentActivity, "选择使用本地账号", Toast.LENGTH_LONG).show();
                        if(!WGPlatform.WGSwitchUser(false)){
                            letUserLogout();
                        }
                    }
                });
        builder.setNeutralButton("拉起账号",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                            int whichButton) {
                        Toast.makeText(currentActivity, "选择使用拉起账号", Toast.LENGTH_LONG).show();
                        if(!WGPlatform.WGSwitchUser(true)){
                            letUserLogout();
                        }
                    }
                });
        builder.show();
    }

	// 登出后, 更新view. 由游戏自己实现登出的逻辑
	public void letUserLogout() {
		WGPlatform.WGLogout();
        userListerner.onLogout(null);
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
        u.userID = ret.open_id;
        if (ret.platform == WeGame.QQPLATID) {
            u.token = "{\"type\": \"qq\", \"token\": \"" + ret.pf_key + "\"}";
        }
        else if (ret.platform == WeGame.WXPLATID) {
            u.token = "{\"type\": \"weixin\", \"token\": \"" + ret.pf_key + "\"}";
        }
        userListerner.onLoginSuccess(u, loginCustomParams);
	}

}
