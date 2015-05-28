package com.paojiao.sdk;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;

import com.paojiao.sdk.activity.WebActivity;
import com.paojiao.sdk.api.InfoApi;
import com.paojiao.sdk.api.StatApi;
import com.paojiao.sdk.bean.RoleInfo;
import com.paojiao.sdk.bean.common.Base;
import com.paojiao.sdk.bean.common.Hotspot;
import com.paojiao.sdk.bean.common.UserData;
import com.paojiao.sdk.config.BridgeMediation;
import com.paojiao.sdk.config.ConfigurationInfo;
import com.paojiao.sdk.config.DeviceInfo;
import com.paojiao.sdk.config.Route;
import com.paojiao.sdk.config.URL;
import com.paojiao.sdk.dialog.ExitDialog;
import com.paojiao.sdk.dialog.LoginDailog;
import com.paojiao.sdk.dialog.SplashDialog;
import com.paojiao.sdk.net.AsyncHttpResponseHandler;
import com.paojiao.sdk.res.StringGlobal;
import com.paojiao.sdk.service.OpenUDID_manager;
import com.paojiao.sdk.service.OpenUDID_service;
import com.paojiao.sdk.utils.DialogUtils;
import com.paojiao.sdk.utils.DisplayUtils;
import com.paojiao.sdk.utils.HttpPost;
import com.paojiao.sdk.utils.HttpPost.HttpPostResponse;
import com.paojiao.sdk.utils.ImageUtils;
import com.paojiao.sdk.utils.JSONLoginInfo;
import com.paojiao.sdk.utils.Utils;
import com.paojiao.sdk.widget.FloatUtils;
import com.paojiao.sdk.widget.MyFloatView;

/**
 * SDK接口类
 */
public class PJApi {

	/**渠道  true 优先从META-INF/channel_ 文件中读取文件名作为渠道号  false 从AndroidManifest.xml 读取PJ_CHANNEL对应的渠道号*/
	public static boolean channel=true;
	
	public static String packageName;
	
	public static String USER_ID;
	/** 泡椒平台分配给合作方的privateKey,向对接人索取 */
	private static String appKey;
	/** 泡椒平台分配给合作方的gameId,向对接人索取 */
	private static int appId;
	/** 商品名称,如元宝钻石(*) */
	public static final String ORDER_SUBJECT = "subject";
	/** 商品价格,RMB(*) */
	public static final String ORDER_PRICE = "price";
	/** 充值玩家所在的服务器（区/服等） */
	public static final String USER_SEVER_NAME = "serverName";
	/** 充值玩家的当前角色名称 */
	public static final String USER_ROLE_NAME = "roleName";

	public static final String ROLE_NAME_STR = "roleName";
	public static final String ROLE_LEVER_STR = "roleLever";
	public static final String ROLE_SERVER_STR = "roleServer";
	public static final String ROLE_MONEY_STR = "roleMoney";
	public static final String ROLE_EXTRA_STR = "extra";
	public static int SCREEN_WIDTH;
	/** 创建订单时的备注信息 */
	public static final String REMARK = "remark";
	/** 扩展信息,一般为订单号,通知时将回传此信息 */
	public static final String ORDER_EXT = "ext";
	/** 标识玩家在泡椒网的登录状态 */
	public static final String TOKEN = "token";
	/** 游戏启动时的启动画面 */
	private SplashDialog splashDialog;

	private static PJApi pjApi;

	private InfoApi infoApi;
	private static Activity mContext;
	private boolean loading = false;
	public static boolean isFirstInit = false;
	public static boolean IS_BUNDEL_TEL = true;
	public static ArrayList<Hotspot> hotspots;
	public static boolean IS_MIUI = false;

	/**
	 * 当前SDK版本
	 */
	public static final String SDK_VERSION = "2.4.6";
	
	/**
	 * 创建SDK实例对象，默认使用此构造实例化即可
	 * @param mContext
	 * @param appId 分配给合作方的appId或gameId
	 * @param appKey 分配给合作方的appKey或privateKey 
	 * @param showSplash 启动游戏的时候，是否闪屏
	 * @param debug 是否debug模式
	 * @return
	 */
	public static PJApi newInstance(Activity mContext, int appId,
			String appKey, boolean showSplash, boolean debug) {
		useTestUrl(debug);
		PJApi.mContext = mContext;
		PJApi.appId = appId;
		PJApi.appKey = appKey;
		if (pjApi == null) {
			pjApi = new PJApi();
		}
		//显示闪屏
		if (showSplash) {
			pjApi.openSplash(null, 3000);
		}
		
		OpenUDID_manager.sync(mContext);
		JPushInterface.setDebugMode(false);
		JPushInterface.init(mContext);
		
		return pjApi;
	}
	
	
	
	/**
	 * 创建SDK实例对象 
	 * @param mContext
	 * @param appId 分配给合作方的appId或gameId
	 * @param appKey 分配给合作方的appKey或privateKey 
	 * @param showSplash 启动游戏的时候，是否闪屏
	 * @param debug 是否debug模式
	 * @param mChnanel 渠道  true 优先从META-INF/channel_ 文件中读取文件名作为渠道号  false 从AndroidManifest.xml 读取PJ_CHANNEL对应的渠道号
	 * @return
	 */
	public static PJApi newInstance(Activity mContext, int appId,
			String appKey, boolean showSplash, boolean debug,boolean mChnanel) {
		useTestUrl(debug);
		PJApi.mContext = mContext;
		PJApi.appId = appId;
		PJApi.appKey = appKey;
		PJApi.channel=mChnanel;
		if (pjApi == null) {
			pjApi = new PJApi();
		}
		//显示闪屏
		if (showSplash) {
			pjApi.openSplash(null, 3000);
		}
		
		OpenUDID_manager.sync(mContext);
		JPushInterface.setDebugMode(false);
		JPushInterface.init(mContext);
		
		return pjApi;
	}



	private PJApi() {
		infoApi = new InfoApi();
		ImageUtils.initImageLoader(mContext);
		StringGlobal.initLanguages();
		SCREEN_WIDTH = DisplayUtils.getShortSide(mContext);
		//System.out.println("SHORT-WIDTH:" + SCREEN_WIDTH);
		Intent service = new Intent(mContext, OpenUDID_service.class);
		mContext.startService(service);
		splashDialog = new SplashDialog(mContext);
		// 初始化的时候发送统计.延迟1秒执行，因为要初始化获取UDID
		// 先这样，后期再看

		new Handler().postDelayed(new Runnable() {
			public void run() {
				StatApi statApi = new StatApi();
				statApi.pjPost(mContext, null);
			}
		}, 1000);
	}
	
	/**测试获取渠道号*//*
	public static void  testChannel(){
		Toast.makeText(mContext, ZipUtils.getChannel2(mContext, "PJ_CHANNEL"), 1).show();
	}*/
	

	/**
	 * 显示悬浮窗口
	 * 
	 * @param con
	 */
	public static void showFloat(Activity con) {
		 DialogUtils.showFloat(con);
		
	}

	/**
	 * 显示悬浮窗口
	 * 
	 * @param con
	 */
	public static void showFloatSecondActivity(Activity con,boolean xiaomi) {
		if(isLogin()){
			 DialogUtils.showFloat(con,xiaomi);
		}
	}
	
	/**
	 * 移除 悬浮框
	 * @param activity
	 */
	public static void removeFloatSecondActivity(Activity activity){
		FloatUtils.getnewInstall().removeFloatView(activity);
	}
	

	/**
	 * 隐藏悬浮窗口
	 */
	public static void hideFloat() {
		MyFloatView.newInstance().hide();
	}

	/**
	 * 登录成功后弹出提示绑定电话对话框
	 * 
	 * @author zhounan
	 * @param @param mContext
	 * @param @param niceName
	 */
	public static void showSimpleDialog(final Activity mContext) {
		DialogUtils.showSimpleDialog(mContext);
	}

	private void openSplash(CallbackListener callbackListener, long delay) {

		if (splashDialog == null) {
			splashDialog = new SplashDialog(mContext);
		}
		if (!mContext.isFinishing()) {
			splashDialog.show(callbackListener, delay);
		}

	}

	/**
	 * 发起支付
	 * @param subject
	 * @param price
	 * @param remark
	 * @param ext
	 * @param callbackListener
	 */
	public void openPayActivity(String subject,float price,String remark,String ext,final CallbackListener callbackListener) {
		
		
		if (!isLogin()) {
			PJError error = new PJError(PJError.CODE_NO_TOKEN,StringGlobal.UN_LOGGED);
			callbackListener.onOpenError(error);
			return;
		}

		if ( !TextUtils.isEmpty(remark)) {
			remark = remark.replace("#", ",");
		}
		
		//拼接支付参数
		String query = MessageFormat.format("subject={0}&price={1}&ext={2}&remark={3}",
				subject, 
				price,
				ext, 
				remark);

		try {
			startActivity(WebActivity.class, URL.RECHARGE + "?" + query, null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 打开登陆窗口
	 * 
	 * @param callbackListener
	 *            主要回调了 onLoginError,onLoginSuccess
	 */
	public void openLoginDialog(CallbackListener callbackListener) {
		LoginDailog dialog = new LoginDailog(mContext, callbackListener);
		dialog.show();
	}

	/**
	 * 打开用户中心
	 * 
	 * @param callbackListener
	 *            主要回调了 onOpenError 方法
	 */
	public void openUcenterActivity(final CallbackListener callbackListener) {
		if (!isLogin()) {

			PJError error = new PJError(PJError.CODE_NO_TOKEN,
					StringGlobal.UN_LOGGED);
			callbackListener.onOpenError(error);
			return;
		}
		try {
			startActivity(WebActivity.class, URL.UCENTER_INDEX + "?"
					+ ConfigurationInfo.getCurrentToken(mContext), null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void startActivity(Class<? extends Activity> _class,
			String url, Bundle params) {
		Intent intent = new Intent(mContext, _class);
		intent.putExtra(Route.URL, url);
		intent.putExtra(Route.PARAMS, params);
		intent.putExtra(Route.SHOW_TAB, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}


	/**
	 * 获取 用户在泡椒平台信息
	 * 
	 * @param callbackListener
	 *            主要回调了 onError ,onInfoSuccess,onInfoError
	 */
	public void getInfo(final CallbackListener callbackListener) {
		if (!isLogin()) {
			PJError error = new PJError(PJError.CODE_NO_TOKEN,
					StringGlobal.UN_LOGGED);
			callbackListener.onInfoError(error);
			return;
		}
		if (loading) {
			PJError error = new PJError(PJError.CODE_LOADING,
					StringGlobal.LOADING);

			callbackListener.onInfoError(error);
			return;
		}
		infoApi.pjPost(mContext, ConfigurationInfo.getCurrentToken(mContext),
				new AsyncHttpResponseHandler() {

					public void onStart() {
						super.onStart();
						loading = true;
					}

					public void onFinish() {
						super.onFinish();
						loading = false;
					}

					protected void handleSuccessMessage(int statusCode,
							Header[] headers, String responseBody) {
						super.handleSuccessMessage(statusCode, headers,
								responseBody);
						Base base = JSONLoginInfo.checkForError(responseBody,
								callbackListener);
						if (!base.getCode().equals(Base.STATUS_OK)) {
							final int code = Integer.parseInt(base.getCode());
							PJError error = new PJError(code, base.getMsg());
							callbackListener.onInfoError(error);
							return;
						}
						if (base.getData() == null) {
							PJError error = new PJError(PJError.CODE_NO_DATA,
									StringGlobal.GET_INFO_FAILED);
							callbackListener.onInfoError(error);
							return;
						}
						UserData iData = base.getData();
						if (iData != null) {
							Bundle bundle = new Bundle();
							bundle.putString(PJUser.ACTIVETIME,
									iData.getActiveTime());
							bundle.putString(PJUser.CREATEDTIME,
									iData.getCreatedTime());
							bundle.putString(PJUser.EMAIL, iData.getEmail());
							bundle.putString(PJUser.NICENAME,
									iData.getNiceName());
							bundle.putString(PJUser.USERNAME,
									iData.getUserName());
							bundle.putString(PJUser.UID, iData.getUid());
							bundle.putString(PJUser.TOKEN, iData.getToken());
							callbackListener.onInfoSuccess(bundle);
						}
					}

					protected void handleFailureMessage(Throwable e,
							String responseBody) {
						super.handleFailureMessage(e, responseBody);
						callbackListener.onError(e);
					}
				});
	}

	/**
	 * 检查是否登陆
	 * 
	 * @return
	 */
	public static boolean isLogin() {
		if (!ConfigurationInfo.getCurrentToken(mContext).equals("unknown")
				&& BridgeMediation.getfMenu() != null
				&& BridgeMediation.getfMenu().size() != 0) {
			return true;
		}
		return false;
	}

	
	
	/**
	 * 构造一些公共参数，在提交数据时，公共参数会随业务数据一同提交；
	 * @return
	 */
	private Map<String,String> generateCommonParams(){
		Map<String,String> params = new HashMap<String,String>();
		params.put(PJApi.TOKEN, ConfigurationInfo.getCurrentToken(mContext));
		params.put("channel", Utils.getMetaValue(mContext, "PJ_CHANNEL"));
		params.put("gameId", PJApi.getAppId() + "");
		params.put("udid", DeviceInfo.getUDID());
		params.put("sdkVersion", SDK_VERSION);
		params.put("appVersion", DeviceInfo.appVersion(mContext));
		return params;
	}
	
	
	
	/**
	 * 上报玩家信息
	 * @param roleInfo
	 */
	public void uploadPlayerInfo(RoleInfo roleInfo,HttpPostResponse response){
		

		//基础数据缺失，不予上报
		if (roleInfo==null || roleInfo.isEmpty()) {
			return;
		}
		
		// 构造参数并提交
		Map<String,String> params = new HashMap<String,String>();
		params.put(ROLE_NAME_STR, roleInfo.getRoleName());
		params.put(ROLE_LEVER_STR, String.valueOf(roleInfo.getRoleLevel()));
		params.put(ROLE_SERVER_STR, roleInfo.getRoleServer());
		params.put(ROLE_MONEY_STR, String.valueOf(roleInfo.getRoleMoney()));
		params.put(ROLE_EXTRA_STR, "");
		params.putAll(this.generateCommonParams());
		
		//发送数据到服务器端
		new HttpPost().postWithoutResult(URL.EXIT_STAT_URL, params,response);
	}

	
	/**
	 * 
	 */
	public  void distoryApi() {
		appKey = null;
		mContext = null;
		splashDialog = null;
		pjApi = null;
	}


	
	/**
	 * 退出游戏时调用的方法，在退出游戏时，将角色信息提交给泡椒网，便于分析推广效果
	 * @param roleInfo 角色信息实体
	 * @param showExitDialog 是否展示泡椒的弹出窗口
	 * @param exitInterface 在退出时处理的回调，游戏产商定义
	 */
	public void exitGame(RoleInfo roleInfo,boolean showExitDialog,final ExitInterface exitInterface){
		
		if(!isLogin()){
			
			return;
		}
		//展示退出窗口
		if(showExitDialog){
			ExitDialog exitDialog = new ExitDialog(roleInfo,mContext, this,null, exitInterface);
			exitDialog.show();
			MyFloatView.newInstance().hide();
		}else{
			
			//角色为空时，不上传
			if(roleInfo==null){
				exitNormal(exitInterface);
				distoryApi();
				return;
			}
			
			//角色信息不为空则开启线程上传玩家信息，不管后面用户有没有退出游戏
			this.uploadPlayerInfo(roleInfo,new HttpPostResponse() {
				
				@Override
				public void response(String msg) {
					exitNormal(exitInterface);
					distoryApi();
				}
			});
			
		}
	}


	/**
	 * 普通的方式退出游戏。
	 * @param exitInterface
	 */
	public void exitNormal(ExitInterface exitInterface) {
		mContext.stopService(new Intent(mContext, OpenUDID_service.class));
		unRegisterReceiver();
		BridgeMediation.setfMenu(null);
		distoryApi();
		exitInterface.onExit();
	}

	private void unRegisterReceiver() {
		Intent intent = new Intent();
		intent.setAction(CallbackListener.ACTION_UN_REGISTER_SELF);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	}

	public static String getAppKey() {
		return appKey;
	}

	public static void setAppKey(String appKey) {
		PJApi.appKey = appKey;
	}

	public static int getAppId() {
		return appId;
	}

	public static void setAppId(int appId) {
		PJApi.appId = appId;
	}

	/**
	 * 对接时用的测试url
	 * 
	 * @param useTest
	 */
	private static void useTestUrl(boolean debug) {
		if (debug) {
			URL.DOMAIN_URL = "http://test.sdk.paojiao.cn/";
			URL.INFOMATION_URL = "http://test.uc.paojiao.cn/";
			// 全部重新实例化静态变量
			URL.init();
		}
	}
}
