package com.nearme.gamecenter.open.api;

import android.app.Activity;
import android.content.Context;

import com.nearme.gamecenter.open.core.framework.GCInternal;
import com.nearme.oauth.model.NDouProductInfo;

public class GameCenterSDK {
	
	private volatile static GameCenterSDK sInstance;

	public static GameCenterSDK getInstance() {
		if (sInstance == null) {
			throw new RuntimeException(
					"GameCenterSDK must be init before call getInstance");
		}
		return sInstance;
	}

	public static void init(GameCenterSettings gameCenterSettings,
			Context context) {
		synchronized (GameCenterSDK.class) {
			if (sInstance == null) {
				sInstance = new GameCenterSDK();
			}
		}
		GCInternal.init(gameCenterSettings, context);
	}

	public static void setmCurrentContext(Context mCurrentContext) {
		GCInternal.setmCurrentContext(mCurrentContext);
	}
	
	private GameCenterSDK() {
		
	}

	/**
	 * 查询用户在游戏中心的虚拟货币的余额
	 * 
	 * @param callback
	 */
	public void doCheckBalance(final ApiCallback callback, final Activity activity) {
		GCInternal.getInstance().doNewGetBalance(callback, activity);
	}
	

	/**
	 * 查询用户N豆余额
	 * @param callback
	 */
	public void doGetUserNDou(final ApiCallback callback, final Activity activity) {
		GCInternal.getInstance().doGetNDou(callback, activity);
	}

	/**
	 * 获取用户信息
	 * 
	 * @param callback
	 */
	public void getCurrUserInfo4SDK(final ApiCallback callback, final Activity activity) {
		GCInternal.getInstance().doNewGetUesrInfo(callback, activity,false);
	}
	/**
	 * 获取用户信息 供CP使用
	 * 
	 * @param callback
	 */
	public void doGetUserInfo(final ApiCallback callback, final Activity activity) {
		GCInternal.getInstance().doNewGetUesrInfo(callback, activity,true);
	}
	
	/**
	 * 获取用户信息 供CP使用
	 * 
	 * @param callback
	 */
	public void doGetUserInfo4CPServer(final ApiCallback callback, final Activity activity) {
		GCInternal.getInstance().doNewGetUesrInfo(callback, activity,true);
	}

	/**
	 * 请求登录
	 * 
	 * @param callback
	 */
	public void doLogin(final ApiCallback callback, final Activity activity) {
		GCInternal.getInstance().doNewLogin(callback, activity);
	}
	
	public void doKebiPay(final ApiCallback callback, final PayInfo payInfo, final Activity activity) {
		GCInternal.getInstance().doKebiPay(callback, payInfo, activity);
	}
	
	public void doPaymentForNDou(final ApiCallback callback, final NDouProductInfo productInfo, final Activity activity) {
		GCInternal.getInstance().doPaymentForNDOU(callback, productInfo, activity);
	}
	
	/**
	 * 
	 * 提交角色信息
	 * 
	 * @param callback
	 * @param extendInfo
	 * @param activity
	 */
	public void doSubmitExtendInfo(final ApiCallback callback,final String extendInfo,final Activity activity){
		GCInternal.getInstance().doSubmitExtendInfo(callback, extendInfo, activity);
	}
	
	/**
	 * 获取可币券信息
	 *
	 * @param callback
	 * @param extendInfo
	 * @param activity
	 */
	public void doGetVouchersInfo(final ApiCallback callback,final String extendInfo,final Activity activity){
		GCInternal.getInstance().doGetVouchersInfo(callback, extendInfo, activity);
	}
	
	//获取消息条数
	public void doGetMsgCount(final ApiCallback callback, final Activity activity) {

		GCInternal.getInstance().doGetMsgCount(callback, activity);
	}
	
	public void doGetCurrRoleInfo(final ApiCallback callback, final Activity activity) {

		GCInternal.getInstance().doGetCurrRoleInfo(callback, activity);
	}
	
	
	public int doGetAccountCount() {
		return GCInternal.getInstance().doGetAccountCount();
	}

	public void doShowForum(final Activity activity) {
		GCInternal.getInstance().doShowForum(activity);
	}
	
	public void doShowGameCenter(final Activity activity) {
		GCInternal.getInstance().doShowGameCenter(activity);
	}

	
	public void doShowSprite(final Activity activity) {
		GCInternal.getInstance().doShowSprite(activity);
	}
	
	
	public void doDismissSprite(Activity activity)	 {
		GCInternal.getInstance().doDismissSprite(activity);
	}

	
	public void doShowProfileSetting(Activity ac) {
		GCInternal.getInstance().doShowProfileSetting(ac);
	}
	
	public String doGetAccessToken() {
		return GCInternal.getInstance().getAccessToken4CP();
	}
	
}
