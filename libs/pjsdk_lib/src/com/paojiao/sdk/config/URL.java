package com.paojiao.sdk.config;


/**
 * SDK接口配置文件,定义所有接口调用的URL地址
 * 
 * @author Van
 * @version 1.0
 */
public class URL {
	
	
	/** 全局SDK域名根目录 */
	public static String DOMAIN_URL = "http://ng.sdk.paojiao.cn/";
	/** 全局用户中心URL*/
	public static String INFOMATION_URL = "http://uc.paojiao.cn/";
	/** 登录接口地址 */
	public static String LOGIN_URL = DOMAIN_URL + "api/user/login.do";
	/** 注销接口地址 */
	public static String LOGOUT_URL = DOMAIN_URL + "api/user/logout.do";
	/** 注册接口地址 */
	public static String REGISTER_URL = DOMAIN_URL + "api/user/reg.do";
	/** token鉴权地址 */
	public static String INFO_URL = DOMAIN_URL + "api/user/token.do";
	/** 根据机器识别码获取登录的帐户列表 */
	public static String ACCOUNT_URL = DOMAIN_URL + "api/user/account.do";
	/** 快速游戏用户注册接口 */
	public static String QUICK_REG = DOMAIN_URL + "api/user/random.do";
	/** 启动统计接口,应用启动时调用 */
	public static String STAT_URL = DOMAIN_URL + "stat/boot.do";
	/** 退出统计接口,用于退出时调用 */
	public static String EXIT_STAT_URL = DOMAIN_URL + "stat/exit.do";
	/** 获取热点消息 */
	public static String ACCESS_HOST_NEWS = DOMAIN_URL+ "api/hotspots.do";
	/** 退出应用时加载对话框数据调用的接口 */
	public static String USER_EXIT_INFO = DOMAIN_URL + "api/exitAd.do?";
	/** 修改自动登录状态地址 */
	public static String CHANGE_LOGIN_STATUS = INFOMATION_URL+ "api/user/changeAutoLoginStatus.html";
	
	/** 从服务器获取自动登录状态 */
	public static String GET_LOGIN_STATUS = INFOMATION_URL+ "api/user/autoLoginStatus.html";
	
	/** 从服务器获取个人信息所有资料 */
	public static String GET_SIMPLE_INFO = INFOMATION_URL+ "api/user/simpleInfo.html";
	/** 从服务器获取签到天数 */
	public static String GET_SIGN_DAYS = INFOMATION_URL+ "api/user/signDays.html";
	/** 调用签到接口 */
	public static String USER_SIGN_IN = INFOMATION_URL+ "api/user/userSign.html";
	/** 获取用户中心信息接口 */
	public static String USER_INFO = INFOMATION_URL+ "api/user/sdkInfo.do?";
	/** 用户中心WAP首页地址 */
	public static String UCENTER_INDEX = INFOMATION_URL+ "wap/index.do";
	public static String DEFAULT = "http://bbs.paojiao.cn";
	/**打开支付界面URL*/
	public static String RECHARGE = DOMAIN_URL + "pay/payGame.do";
	/**用户中心首页URL*/
	public static String UCENTER = INFOMATION_URL + "wap/index.do";
	/**找回密码URL*/
	public static String RESET_PWD = INFOMATION_URL+ "wap/user/findPassword.do";
	/**绑定手机URL*/
	public static String BIND_MOBILE = INFOMATION_URL+ "wap/user/preBindMobile.do";

	
	/***
	 * 静态变量全部重新实例化；
	 */
	public static void init(){
		/** 登录接口地址 */
		LOGIN_URL = DOMAIN_URL + "api/user/login.do";
		/** 注销接口地址 */
		LOGOUT_URL = DOMAIN_URL + "api/user/logout.do";
		/** 注册接口地址 */
		REGISTER_URL = DOMAIN_URL + "api/user/reg.do";
		/** token鉴权地址 */
		INFO_URL = DOMAIN_URL + "api/user/token.do";
		/** 根据机器识别码获取登录的帐户列表 */
		ACCOUNT_URL = DOMAIN_URL + "api/user/account.do";
		/** 快速游戏用户注册接口 */
		QUICK_REG = DOMAIN_URL + "api/user/random.do";
		/** 启动统计接口,应用启动时调用 */
		STAT_URL = DOMAIN_URL + "stat/boot.do";
		/** 退出统计接口,用于退出时调用 */
		EXIT_STAT_URL = DOMAIN_URL + "stat/exit.do";
		/** 获取热点消息 */
		ACCESS_HOST_NEWS = DOMAIN_URL+ "api/hotspots.do";
		/** 退出应用时加载对话框数据调用的接口 */
		USER_EXIT_INFO = DOMAIN_URL + "api/exitAd.do?";
		/** 修改自动登录状态地址 */
		CHANGE_LOGIN_STATUS = INFOMATION_URL+ "api/user/changeAutoLoginStatus.html";
		/** 从服务器获取自动登录状态 */
		GET_LOGIN_STATUS = INFOMATION_URL+ "api/user/autoLoginStatus.html";
		/** 从服务器获取个人信息所有资料 */
		GET_SIMPLE_INFO = INFOMATION_URL+ "api/user/simpleInfo.html";
		/** 从服务器获取签到天数 */
		GET_SIGN_DAYS = INFOMATION_URL+ "api/user/signDays.html";
		/** 调用签到接口 */
		USER_SIGN_IN = INFOMATION_URL+ "api/user/userSign.html";
		/** 获取用户中心信息接口 */
		USER_INFO = INFOMATION_URL+ "api/user/sdkInfo.do?";
		/** 用户中心WAP首页地址 */
		UCENTER_INDEX = INFOMATION_URL+ "wap/index.do";
		DEFAULT = "http://bbs.paojiao.cn";
		/**打开支付界面URL*/
		RECHARGE = DOMAIN_URL + "pay/payGame.do";
		/**用户中心首页URL*/
		UCENTER = INFOMATION_URL + "wap/index.do";
		/**找回密码URL*/
		RESET_PWD = INFOMATION_URL+ "wap/user/findPassword.do";
		/**绑定手机URL*/
		BIND_MOBILE = INFOMATION_URL+ "wap/user/preBindMobile.do";
	}

}
