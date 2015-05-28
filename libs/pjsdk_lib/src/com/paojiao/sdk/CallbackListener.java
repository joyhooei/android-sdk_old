package com.paojiao.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.paojiao.sdk.utils.Prints;

/**
 * 
 *  回调类
 *
 */


public abstract class CallbackListener {

	/** 支付成功的 BroadcastReceiver action */
	public static final String ACTION_PAY_SUCCESS = "com.paojiao.sdk.ACTION_PAY_SUCCESS";

	/** 支付失败的 BroadcastReceiver action */
	public static final String ACTION_PAY_ERROR = "com.paojiao.sdk.ACTION_PAY_ERROR";

	/** 支付取消的 BroadcastReceiver action */
	public static final String ACTION_PAY_CACEL = "com.paojiao.sdk.ACTION_PAY_CACEL";

	/** 用户登出的 BroadcastReceiver action */
	public static final String ACTION_LOGOUT = "com.paojiao.sdk.ACTION_PAY_ERROR";

	/** 进入用户中心的 BroadcastReceiver action */
	public static final String ACTION_BACK_FROM_WEB = "com.paojiao.sdk.ACTION_BACK_FROM_WEB";

	/** 移除BroadcastReceiver所用 action 退出时使用 */
	public static final String ACTION_UN_REGISTER_SELF = "com.paojiao.sdk.ACTION_UN_REGISTER_SELF";
	/** 移除BroadcastReceiver所用 action 退出时使用 */
	public static final String ACTION_CARRYING_COORDINATE = "com.paojiao.sdk.ACTION_CARRYING_COORDINATE";

	public CallbackListener(Context mContext) {

		instanceRegisterBroadcast(mContext);

	}

	/**
	 * 注册广播
	 * 
	 * @author zhounan
	 * @param
	 */
	private void instanceRegisterBroadcast(final Context mContext) {
		final LocalBroadcastManager lbm = LocalBroadcastManager
				.getInstance(mContext);

		final IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_LOGOUT);
		filter.addAction(ACTION_PAY_ERROR);
		filter.addAction(ACTION_PAY_SUCCESS);
		filter.addAction(ACTION_BACK_FROM_WEB);
		filter.addAction(ACTION_PAY_CACEL);
		filter.addAction(ACTION_UN_REGISTER_SELF);
		filter.addAction(ACTION_CARRYING_COORDINATE);

		lbm.registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {

				String action = intent.getAction();
				if (action == null) {
					return;
				}
				if (action.equals(ACTION_LOGOUT)) {
					String uid = intent.getStringExtra("uid");
					String username = intent.getStringExtra("username");
					onLogout(uid, username);
				} else if (action.equals(ACTION_PAY_ERROR)) {
					int code = intent.getIntExtra("code", 0);
					String message = intent.getStringExtra("message");
					PJError pjError = new PJError(code, message);
					String info = intent.getStringExtra("username");
					onPaymentError(pjError, info);
				} else if (action.equals(ACTION_PAY_SUCCESS)) {
					String info = intent.getStringExtra("username");
					onPaymentSuccess(info);
				} else if (action.equals(ACTION_PAY_CACEL)) {
					Bundle info = intent.getBundleExtra("info");
					onPaymentCancel(info);
				} else if (action.equals(ACTION_BACK_FROM_WEB)) {
				} else if (action.equals(ACTION_UN_REGISTER_SELF)) {
					lbm.unregisterReceiver(this);
				}else if (action.equals(ACTION_CARRYING_COORDINATE)){
					Bundle info = intent.getBundleExtra("coordinates");
					onGetCoordinate(info);
				}
				lbm.unregisterReceiver(this);
			}
		}, filter);
	}

	public void onSplashComplete() {

	}

	/**
	 * 错误回调
	 * 
	 * @param error
	 */
	public void onError(Throwable error) {
	};

	/**
	 * 用户登陆成功后的回调
	 * 
	 * @param bundle
	 */
	public void onLoginSuccess(Bundle bundle) {
		// PJApi.showFloat();
	};

	/**
	 * 登陆出错的回调
	 * 
	 * @param error
	 */
	public void onLoginError(PJError error) {
	};

	/**
	 * 退出登录，注销 回调的方法
	 */
	public void onLogoutSuccess() {
		PJApi.hideFloat();
	};

	/**
	 * 错误的退出的回调
	 * 
	 * @param error
	 */
	public void onLogoutError(PJError error) {
	};

	/**
	 * 显示用户信息的回调
	 * 
	 * @param bundle
	 *            (bundle的key分别为:
	 *            PJUser.TOKEN(用户在泡椒网的登录标识),PJUser.UID(用户id),PJUser
	 *            .USERNAME(用户名)
	 *            ,PJUser.NICENAME(昵称),PJUser.CREATEDTIME(用户会话创建时间)
	 *            ,PJUser.ACTIVETIME(用户最后活跃时间) )
	 */
	public void onInfoSuccess(Bundle bundle) {
	};

	/**
	 * 获取用户信息错误
	 * 
	 * @param error
	 */
	public void onInfoError(PJError error) {
	}

	/**
	 * 打开用户中心 错误时的回调
	 * 
	 * @param error
	 */
	public void onOpenError(PJError error) {
	};

	/**
	 * 支付成功
	 * 
	 * @param orderNo
	 */
	public void onPaymentSuccess(String info) {
		Prints.d("PAY_SUCCESS", "PAY_SUCCESS--RECEIVER");
	};

	/**
	 * 
	 * @param info
	 */
	public void onGetCoordinate(Bundle info) {
		
	};
	/**
	 * 支付取消
	 * 
	 * @param info
	 */
	public void onPaymentCancel(Bundle info) {

	};

	/**
	 * 支付失败
	 * 
	 * @param error
	 * @param orderNo
	 */
	public void onPaymentError(PJError pjError, String info) {

	};

	/**
	 * 用户登出
	 * 
	 * @param username
	 */
	public void onLogout(String uid, String username) {
		PJApi.hideFloat();
	};
}
