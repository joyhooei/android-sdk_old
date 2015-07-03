package org.yunyue;

import java.util.UUID;
import java.util.Random;

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
import android.text.TextUtils;


import com.nearme.gamecenter.open.api.ApiCallback;
import com.nearme.gamecenter.open.api.FixedPayInfo;
import com.nearme.gamecenter.open.api.GameCenterSDK;
import com.nearme.gamecenter.open.api.GameCenterSettings;
import com.nearme.gamecenter.open.api.PayInfo;
import com.nearme.gamecenter.open.api.RatePayInfo;
import com.nearme.gamecenter.open.core.util.ImageLoader;
import com.nearme.gamecenter.open.core.util.Util;
import com.nearme.oauth.log.LogUtil;
import com.nearme.oauth.model.NDouProductInfo;
import com.nearme.oauth.model.UserInfo;

public class GameProxyImpl extends GameProxy {
    private Activity currentActivity;
    private Object switchCustomParams;
    private PayCallBack payCallBack;

    @Override
    public void applicationInit(Activity activity) {
        Log.v("sdk", "applicationInit");
		GameCenterSettings gameCenterSettings = new GameCenterSettings(
				"${APPKEY}", "${APPSECRET}") {

			@Override
			public void onForceReLogin() {
				// sdk由于某些原因登出,此方法通知cp,cp需要在此处清理当前的登录状态并重新请求登录.
				// 可以发广播通知页面重新登录
                userListerner.onLogout(null);
			}
			
			@Override 
			public void onForceUpgradeCancel() {
				// 游戏自升级，后台有设置为强制升级，用户点击取消时的回调函数。
				// 若开启强制升级模式 ，  一般要求不更新则强制退出游戏并杀掉进程。
				// System.exit(0) or kill this process
			}
		};
		// TODO for test old
//		AccountAgent.useNewApi = true;
		GameCenterSettings.isDebugModel = false;// 测试log开关
		GameCenterSettings.isOritationPort = true;// 控制SDK activity的横竖屏 true为竖屏
		GameCenterSettings.proInnerSwitcher = false;//是否支持游戏内切换账号
		GameCenterSDK.init(gameCenterSettings, activity);
    }

	@Override
	public void onResume(Activity activity) {
		super.onResume(activity);
        GameCenterSDK.setmCurrentContext(activity);

        GameCenterSDK.getInstance().doShowSprite(new ApiCallback() {
            @Override
            public void onSuccess(String content, int code) {
                //makeToast("切换账号成功:" + content + "#" + code);
            }
            @Override
            public void onFailure(String content, int code) {
                //makeToast("切换账号失败:" + content + "#" + code);
            }
        }, activity);//显示浮标
    }

	@Override
	public void onPause(Activity activity) {
		super.onPause(activity);
        GameCenterSDK.getInstance().doDismissSprite(activity);
    }

    @Override
    public void onCreate(Activity activity) {
        Log.v("sdk", "onCreate");
    }

    @Override
    public void login(final Activity activity, final Object customParams) {
        // 调用SDK执行登陆操作
        Log.v("sdk", "login");

		GameCenterSDK.getInstance().doLogin(new ApiCallback() {

			@Override
			public void onSuccess(String content, int code) {
                User u = new User();
                u.token = GameCenterSDK.getInstance().doGetAccessToken();
                userListerner.onLoginSuccess(u, customParams);
			}

			@Override
			public void onFailure(String content, int code) {
                Toast.makeText(activity, content, Toast.LENGTH_LONG).show();
                userListerner.onLoginFailed(content, customParams);
			}
		}, activity);
    }

    @Override
    public void logout(Activity activity, Object customParams) {
        Log.v("sdk", "logout");
        userListerner.onLogout(customParams);
    }

    @Override
    public void switchAccount(Activity activity, Object customParams) {
        switchCustomParams = customParams;
    }

	private ApiCallback kebiPayment = new ApiCallback() {
		@Override
		public void onSuccess(String content, int code) {
            payCallBack.onSuccess("支付成功");
		}

		@Override
		public void onFailure(String content, int code) {
            payCallBack.onFail(content + "," + code);
            Toast.makeText(currentActivity, "消耗可币失败:" + content, Toast.LENGTH_LONG).show();
		}
	};

    @Override
    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        Log.v("sdk", "pay:" + ID + "," + name + "," + price + "," + callBackInfo + "," + roleInfo.toString());
        currentActivity = activity;
        this.payCallBack = payCallBack;

		final FixedPayInfo payInfo = new FixedPayInfo(orderID, callBackInfo, (int)price * 100);
		payInfo.setProductDesc(name);
		payInfo.setProductName("元宝");
		payInfo.setCallbackUrl("${PAY_URL}");
		payInfo.setGoodsCount((int)price*10);
		GameCenterSDK.getInstance().doFixedKebiPayment(kebiPayment, payInfo, activity);
    }

    @Override
    public void exit(Activity activity, ExitCallback callback) {
        Log.v("sdk", "exit");
        callback.onNo3rdExiterProvide();
    }

    private ApiCallback mExtInfoCallBack = new ApiCallback() {
            @Override
            public void onSuccess(String content, int code) {
                // TODO Auto-generated method stub
                //Util.makeToast("提交角色信息成功：" + content + "#" + code,
                //        OpenSDKDemoNewActivity.this);
            }
        
            @Override
            public void onFailure(String content, int code) {
                // TODO Auto-generated method stub
                //Util.makeToast("提交角色信息失败：" + content, OpenSDKDemoNewActivity.this);
            }
    };

    @Override
    public void setExtData(Context context, String ext) {
        Log.v("sdk", "setExtData:" + ext);
        try {
            JSONObject src = new JSONObject(ext);
            String extendInfo = new StringBuilder()
                .append("gameId=").append(((poem)currentActivity).getMetaData("appid"))
                .append("&service=").append(0)
                .append("&role=").append(src.getString("name"))
                .append("&grade=").append(src.getString("level")).toString();

            GameCenterSDK.getInstance().doSubmitExtendInfo(mExtInfoCallBack,
                    extendInfo, currentActivity);
        } catch (Exception e) {
            // 处理异常
            Log.e("sdk", "set Ext Data exception");
            e.printStackTrace();
        }
    }

    @Override
    public void openCommunity(Activity activity) {
        GameCenterSDK.getInstance().doShowForum(activity);
    }
}
