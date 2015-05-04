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

import com.baidu.gamesdk.BDGameSDK;
import com.baidu.gamesdk.BDGameSDKSetting;
import com.baidu.gamesdk.BDGameSDKSetting.Domain;
import com.baidu.gamesdk.BDGameSDKSetting.Orientation;

import com.baidu.gamesdk.IResponse;
import com.baidu.gamesdk.ResultCode;
import com.baidu.platformsdk.PayOrderInfo;

import com.baidu.gamesdk.ActivityAdPage;
import com.baidu.gamesdk.ActivityAdPage.Listener;
import com.baidu.gamesdk.ActivityAnalytics;

public class GameProxyImpl extends GameProxy {
    private Activity currentActivity;
    private PayCallBack payCallBack;

	private ActivityAdPage mActivityAdPage;
	private ActivityAnalytics mActivityAnalytics;

    @Override
    public void applicationInit(final Activity activity) {
        Log.v("sdk", "applicationInit");
        currentActivity = activity;

        BDGameSDKSetting mBDGameSDKSetting = new BDGameSDKSetting();
        mBDGameSDKSetting.setAppID(5584487);//APPID设置
        mBDGameSDKSetting.setAppKey("fuZz2kKfl7hdzilGuaxcK4BY");//APPKEY设置
        mBDGameSDKSetting.setDomain(Domain.RELEASE);//设置为正式模式
        mBDGameSDKSetting.setOrientation(Orientation.PORTRAIT);//设置为横屏
		BDGameSDK.init(activity, mBDGameSDKSetting, new IResponse<Void>(){
			@Override
			public void onResponse(int resultCode, String resultDesc,
					Void extraData) {
				switch(resultCode){
				case ResultCode.INIT_SUCCESS:
					break;
				case ResultCode.INIT_FAIL:
				default:
					Toast.makeText(activity, "启动失败", Toast.LENGTH_LONG).show();
					activity.finish();
					//初始化失败
				}
			}
		}); 
        init(activity);
    }

    private void init(Activity activity){
		mActivityAnalytics = new ActivityAnalytics(activity);
		
		mActivityAdPage = new ActivityAdPage(activity, new Listener(){
			@Override
			public void onClose() {
				// TODO 关闭暂停页, CP可以让玩家继续游戏
				//Toast.makeText(getApplicationContext(), "继续游戏", Toast.LENGTH_LONG).show();
			}
			
		}); 
	}

    @Override
    public void onCreate(Activity activity) {
        Log.v("sdk", "onCreate");
        setSuspendWindowChangeAccountListener();
        setSessionInvalidListener();
    }

    @Override
    public void login(final Activity activity, final Object customParams) {
        Log.v("sdk", "login");
		BDGameSDK.login(new IResponse<Void>() {
			@Override
			public void onResponse(int resultCode, String resultDesc, Void extraData) { 
				Log.d("sdk", "login resultCode is " + resultCode);
				String hint = "";
				switch(resultCode){
				case ResultCode.LOGIN_SUCCESS:
					hint = "登录成功";

                    User u = new User();
                    u.userID = BDGameSDK.getLoginUid();
                    u.token = BDGameSDK.getLoginAccessToken();
                    userListerner.onLoginSuccess(u, customParams);
					break; 
				case ResultCode.LOGIN_CANCEL:
					hint = "取消登录";
                    userListerner.onLoginFailed("取消登录", customParams);
					break;
				case ResultCode.LOGIN_FAIL:
				default:	
					hint = "登录失败";	 
                    userListerner.onLoginFailed("登录失败", customParams);
				} 
				Toast.makeText(activity.getApplicationContext(), hint, Toast.LENGTH_LONG).show(); 
			}
		});
    }

    @Override
    public void logout(Activity activity,Object customParams) {
        Log.v("sdk", "logout");
        BDGameSDK.logout();
    }

    @Override
    public void pay(final Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, final PayCallBack payCallBack) {
        Log.v("sdk", "pay:" + ID + "," + name + "," + price + "," + callBackInfo + "," + roleInfo.toString());

		PayOrderInfo payOrderInfo = new PayOrderInfo();
		payOrderInfo.setCooperatorOrderSerial(orderID);
		payOrderInfo.setProductName(name); 
		payOrderInfo.setTotalPriceCent((int)(price*100));//以分为单位
		payOrderInfo.setRatio(1);
		payOrderInfo.setExtInfo(callBackInfo);//该字段将会在支付成功后原样返回给CP(不超过500个字符)

		BDGameSDK.pay(payOrderInfo, null, 
				new IResponse<PayOrderInfo>(){

					@Override
					public void onResponse(int resultCode, String resultDesc,
							PayOrderInfo extraData) {
						String resultStr = "";
						switch(resultCode){
						case ResultCode.PAY_SUCCESS://支付成功
							resultStr = "支付成功:" + resultDesc;
                            payCallBack.onSuccess(null);
							break;
						case ResultCode.PAY_CANCEL://订单支付取消
							resultStr = "取消支付";
                            payCallBack.onFail(null);
							break;	
						case ResultCode.PAY_FAIL://订单支付失败
							resultStr = "支付失败：" + resultDesc;
                            payCallBack.onFail(null);
							break;	
						case ResultCode.PAY_SUBMIT_ORDER://订单已经提交，支付结果未知（比如：已经请求了，但是查询超时）
							resultStr = "订单已经提交，支付结果未知";
							break;	
						}
						Toast.makeText(activity.getApplicationContext(), resultStr, Toast.LENGTH_LONG).show();
					}
		});
    }

    @Override
    public void applicationDestroy(Activity activity) {
        Log.v("sdk", "applicationDestroy");
        mActivityAdPage.onDestroy();
		BDGameSDK.destroy();
    }

    @Override
    public void setExtData(Context context, String ext) {
        Log.v("sdk", "setExtData:" + ext);
    }

    @Override
    public boolean supportCommunity() {
        return false;
    }

    private void setSuspendWindowChangeAccountListener(){//设置切换账号事件监听（个人中心界面切换账号）
		BDGameSDK.setSuspendWindowChangeAccountListener(new IResponse<Void>(){
			@Override
			public void onResponse(int resultCode, String resultDesc, Void extraData) {
				 switch(resultCode){
				 case ResultCode.LOGIN_SUCCESS:
					 //TODO 登录成功，不管之前是什么登录状态，游戏内部都要切换成新的用户
					 Toast.makeText(currentActivity.getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();

                     User u = new User();
                     u.userID = BDGameSDK.getLoginUid();
                     u.token = BDGameSDK.getLoginAccessToken();
                     userListerner.onLogout(null);
                     userListerner.onLoginSuccess(u, null);
					 break;
				 case ResultCode.LOGIN_FAIL:
					 //TODO 登录失败，游戏内部之前如果是已经登录的，要清楚自己记录的登录状态，设置成未登录。如果之前未登录，不用处理。
					 Toast.makeText(currentActivity.getApplicationContext(), "登录失败", Toast.LENGTH_LONG).show();
                     userListerner.onLogout(null);
					 break;
				 case ResultCode.LOGIN_CANCEL:					 
					 break; 
				 } 
			}
			
		});
	}

    private void setSessionInvalidListener(){
		BDGameSDK.setSessionInvalidListener(new IResponse<Void>(){
			@Override
			public void onResponse(int resultCode, String resultDesc, Void extraData) {
				if(resultCode == ResultCode.SESSION_INVALID){
					//会话失效，开发者需要重新登录或者重启游戏
                    userListerner.onLogout(null);
				}
			}
		});
	}

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        mActivityAdPage.onResume();
		mActivityAnalytics.onResume();
    }

    @Override
	public void onStop(Activity activity) {
		super.onStop(activity);
		mActivityAdPage.onStop();
	}

    @Override
	public void onPause(Activity activity) {
		super.onPause(activity);
        mActivityAdPage.onPause();
        mActivityAnalytics.onPause();
	}

}
