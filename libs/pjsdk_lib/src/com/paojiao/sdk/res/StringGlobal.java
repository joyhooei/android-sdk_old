/**
 * 
 */
package com.paojiao.sdk.res;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * @author zhjhzhou
 * 
 */
public class StringGlobal {
	public static String UN_LOGGED = "";
	public static String LOGIN_PROGRESS = "";
	public static String GET_ACCOUNT_PROGRESS = "";
	public static String LOGIN = "";
	public static String FORGOT_PWD = "";
	public static String USERNAME_HINT = "";
	public static String PWD_HINT = "";
	public static String QUICK_LOGIN = "";
	public static String REGISTER = "";
	public static String BACK_TO_GAME = "";
	public static String WARM_TIPS = "";
	public static String AUTO_LOGIN = "";
	public static String GET_INFO_FAILED = "";
	public static String BIND_RIGHT_NOW = "";
	public static String BIND_LATTER = "";
	public static String LOGIN_TIMEOUT = "";
	public static String ILLEGAL_USER_NAME = "";
	public static String ILLEGAL_SHORT_PWD = "";
	public static String ILLEGAL_LONG_PWD = "";
	public static String DIFFERENT_PWD = "";
	public static String INVALID_EMAIL = "";
	public static String INCORRECT_PWD = "";
	public static String USER_NOT_EXIST = "";
	public static String USER_EXIST = "";
	public static String CAN_NOT_NULL = "";
	public static String ERROR_NETWORK_DISSABLE = "";
	public static String NOTICE_BIND_MOBILE = "";
	public static String LOADING = "";
	public static String REGISTER_SUCCESS = "";
	public static String REGISTER_NAME_HINT = "";
	public static String REGISTER_PWD_HINT = "";
	public static String CONFIRM_PWD_HINT = "";
	public static String REGISTER_PROGRESS = "";
	public static String EXIT_GAME = "";
	public static String CONTINUE_TO_GAME = "";
	public static String BACK = "";
	// sdk 2.0新增加
	public static String TAB_BBS;
	public static String TAB_ACT;
	public static String TAB_LOTTERY;
	public static String TAB_PACKAGE;
	public static String TAB_RECHARGE;

	public static String TAB_AUTO_LOGIN = "";
	public static String TAB_SIGN_IN = "";
	public static String TAB_CUSTOMER = "";
	public static String SETTING = "";
	public static String ADD_BY_SIGNED = "";

	static class StringConfig {
		public Class<?> mClass;

		public StringConfig(Class<?> cls) {
			mClass = cls;
		}

		public String getString(String str) {
			String tmp = null;
			try {
				Field field = mClass.getDeclaredField(str);
				tmp = (String) field.get(mClass);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tmp;
		}
	}

	//
	public static void initLanguages() {
		StringConfig mGlobal;
		String s = Locale.getDefault().getLanguage();
		if (s.equals("zh")) {
			mGlobal = new StringConfig(CNSTRINGS.class);
		} else if (s.equals("en")) {
			mGlobal = new StringConfig(ENSTRINGS.class);
		} else {
			mGlobal = new StringConfig(ENSTRINGS.class);
		}
		UN_LOGGED = mGlobal.getString("UN_LOGGED");
		LOGIN_PROGRESS = mGlobal.getString("LOGIN_PROGRESS");
		GET_ACCOUNT_PROGRESS = mGlobal.getString("GET_ACCOUNT_PROGRESS");
		LOGIN = mGlobal.getString("LOGIN");
		FORGOT_PWD = mGlobal.getString("FORGOT_PWD");
		USERNAME_HINT = mGlobal.getString("USERNAME_HINT");
		PWD_HINT = mGlobal.getString("PWD_HINT");
		QUICK_LOGIN = mGlobal.getString("QUICK_LOGIN");
		REGISTER = mGlobal.getString("REGISTER");
		BACK_TO_GAME = mGlobal.getString("BACK_TO_GAME");
		WARM_TIPS = mGlobal.getString("WARM_TIPS");
		AUTO_LOGIN = mGlobal.getString("AUTO_LOGIN");
		GET_INFO_FAILED = mGlobal.getString("GET_INFO_FAILED");
		BIND_RIGHT_NOW = mGlobal.getString("BIND_RIGHT_NOW");
		BIND_LATTER = mGlobal.getString("BIND_LATTER");
		LOGIN_TIMEOUT = mGlobal.getString("LOGIN_TIMEOUT");
		ILLEGAL_USER_NAME = mGlobal.getString("ILLEGAL_USER_NAME");
		ILLEGAL_SHORT_PWD = mGlobal.getString("ILLEGAL_SHORT_PWD");
		ILLEGAL_LONG_PWD = mGlobal.getString("ILLEGAL_LONG_PWD");
		DIFFERENT_PWD = mGlobal.getString("DIFFERENT_PWD");
		INVALID_EMAIL = mGlobal.getString("INVALID_EMAIL");
		INCORRECT_PWD = mGlobal.getString("INCORRECT_PWD");
		USER_NOT_EXIST = mGlobal.getString("USER_NOT_EXIST");
		USER_EXIST = mGlobal.getString("USER_EXIST");
		CAN_NOT_NULL = mGlobal.getString("CAN_NOT_NULL");
		ERROR_NETWORK_DISSABLE = mGlobal.getString("ERROR_NETWORK_DISSABLE");
		NOTICE_BIND_MOBILE = mGlobal.getString("NOTICE_BIND_MOBILE");
		LOADING = mGlobal.getString("LOADING");
		REGISTER_SUCCESS = mGlobal.getString("REGISTER_SUCCESS");
		REGISTER_NAME_HINT = mGlobal.getString("REGISTER_NAME_HINT");
		REGISTER_PWD_HINT = mGlobal.getString("REGISTER_PWD_HINT");
		CONFIRM_PWD_HINT = mGlobal.getString("CONFIRM_PWD_HINT");
		REGISTER_PROGRESS = mGlobal.getString("REGISTER_PROGRESS");
		EXIT_GAME = mGlobal.getString("EXIT_GAME");
		CONTINUE_TO_GAME = mGlobal.getString("CONTINUE_TO_GAME");
		BACK = mGlobal.getString("BACK");

		TAB_BBS = mGlobal.getString("TAB_BBS");
		TAB_ACT = mGlobal.getString("TAB_ACT");
		TAB_LOTTERY = mGlobal.getString("TAB_LOTTERY");
		TAB_PACKAGE = mGlobal.getString("TAB_PACKAGE");
		TAB_RECHARGE = mGlobal.getString("TAB_RECHARGE");

		TAB_AUTO_LOGIN = mGlobal.getString("TAB_AUTO_LOGIN");
		TAB_SIGN_IN = mGlobal.getString("TAB_SIGN_IN");
		TAB_CUSTOMER = mGlobal.getString("TAB_CUSTOMER");
		SETTING = mGlobal.getString("SETTING");
		ADD_BY_SIGNED = mGlobal.getString("ADD_BY_SIGNED");

	}
}
