package com.paojiao.sdk.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
/**
 * 保存一些游戏中通用的全局变量
 * @author zhounan
 * @version 2014-7-29 下午5:37:57
 */
public class ConfigurationInfo {

	public static final String USER_INFO = "com.paojiao.sdk.pjsdk_users_info";

	public static final String ACCOUNT = "com.paojiao.sdk.ACCOUNT";
	/**
	 * 当前使用中的token
	 */
	public static final String KEY_C_TOKE = "com.paojiao.sdk.KEY_C_TOKE";
	public static final String KEY_C_UID = "com.paojiao.sdk.KEY_C_UID";
	public static final String KEY_C_NICK_NAME = "com.paojiao.sdk.KEY_C_NICKNAME";
	/**
	 * 
	 */
	public static final String KEY_C_USER_INFO_STR = "com.paojiao.sdk.KEY_C_USER_INFO_STR";
	public static final String KEY_C_AVATAR_URL_STR = "com.paojiao.sdk.KEY_C_AVATAR_URL_STR";
	
	public static final String KEY_C_MIUI_X = "com.paojiao.sdk.KEY_C_MIUI_X_STR";

	public static final String KEY_C_MIUI_Y = "com.paojiao.sdk.KEY_C_MIUI_Y_STR";

	public static final String KEY_C_ACCOUNT_URL = "com.paojiao.sdk.KEY_C_PACKS_URL";
	public static final String KEY_C_ACCOUNT_MSG = "com.paojiao.sdk.KEY_C_PACKS_MSG";
	public static final String KEY_C_PACKS_URL = "com.paojiao.sdk.KEY_C_PACKS_URL";
	public static final String KEY_C_PACKS_MSG = "com.paojiao.sdk.KEY_C_PACKS_MSG";

	public static final String KEY_C_GUIDES_URL = "com.paojiao.sdk.KEY_C_GUIDES_URL";
	public static final String KEY_C_GUIDES_MSG = "com.paojiao.sdk.KEY_C_GUIDES_MSG";

	public static final String KEY_C_BBS_URL = "com.paojiao.sdk.KEY_C_BBS_URL";
	public static final String KEY_C_BBS_MSG = "com.paojiao.sdk.KEY_C_BBS_MSG";

	public static final String KEY_C_QQ_GROUP = "com.paojiao.sdk.KEY_C_QQ_GROUP";
	/**
	 * 
	 */
	public static final String KEY_AUTO_LOGIN = "com.paojiao.sdk.KEY_AUTO_LOGIN";

	public static void putObject(Context context, String key, Object value, String db) {
		SharedPreferences info = context.getSharedPreferences(db, Context.MODE_PRIVATE);
		Editor edit = info.edit();
		if (value instanceof String) {
			edit.putString(key, value.toString());
		} else if (value instanceof Boolean) {
			edit.putBoolean(key, (Boolean) value);
		} else if (value instanceof Integer) {
			edit.putInt(key, (Integer) value);
		} else if (value instanceof Long) {
			edit.putLong(key, (Long) value);
		} else if (value instanceof Float) {
			edit.putFloat(key, (Float) value);
		}
		edit.commit();
	}

	public static boolean contains(Context context, String key, String db) {
		SharedPreferences info = context.getSharedPreferences(db, Context.MODE_PRIVATE);
		return info.contains(key);
	}

	public static void clearKey(Context context, String key, String db) {
		SharedPreferences info = context.getSharedPreferences(db, Context.MODE_PRIVATE);
		info.edit().remove(key).commit();
	}

	public static void clear(Context context, String db) {
		SharedPreferences info = context.getSharedPreferences(db, Context.MODE_PRIVATE);
		info.edit().clear().commit();
	}

	public static String getString(Context context, String key, String db) {
		SharedPreferences info = context.getSharedPreferences(db, Context.MODE_PRIVATE);
		if(info!=null){
			return info.getString(key, "unknown");
		}
		return "unknown";
	}

	public static String getString(Context context, String key, String defaultValue, String db) {
		SharedPreferences info = context.getSharedPreferences(db, Context.MODE_PRIVATE);
		return info.getString(key, defaultValue);
	}

	public static boolean getBoolean(Context context, String key, String db) {
		SharedPreferences info = context.getSharedPreferences(db, Context.MODE_PRIVATE);
		return info.getBoolean(key, false);
	}

	public static int getInt(Context context, String key, String db) {
		SharedPreferences info = context.getSharedPreferences(db, Context.MODE_PRIVATE);
		return info.getInt(key, 0);
	}

	public static long getLong(Context context, String key, String db) {
		SharedPreferences info = context.getSharedPreferences(db, Context.MODE_PRIVATE);
		return info.getLong(key, 0);
	}

	public static boolean hasValue(Context context, String key, String db) {
		SharedPreferences info = context.getSharedPreferences(db, Context.MODE_PRIVATE);
		if (!info.contains(key) || info.getString(key, "").length() == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 保存与获取登录后返回的uid
	 * 
	 * @author zhounan
	 * @param context
	 * @param uid
	 */
	public static void setUid(Context context, String uid) {
		putObject(context, KEY_C_UID, uid, USER_INFO);
	}

	public static String getUid(Context context) {
		return getString(context, KEY_C_UID, USER_INFO);
	}
	/**
	 * 设置登录用户昵称
	 * 
	 * @param context
	 * @param nickName
	 */
	public static void setNickName(Context context, String nickName) {
		putObject(context, KEY_C_NICK_NAME, nickName, USER_INFO);
	}

	public static String getNickName(Context context) {
		return getString(context, KEY_C_NICK_NAME, USER_INFO);
	}
	/**
	 * 获取本地存储的关于用户中心对话框里的json信息
	 * @param context
	 * @return
	 */
	public static String getInfoStr(Context context) {
		return getString(context, KEY_C_USER_INFO_STR, USER_INFO);
	}
	/**
	 * 网络获取的json信息保存到本地
	 * @param context
	 * @param infoStr
	 */
	public static void setInfoStr(Context context, String infoStr) {
		putObject(context, KEY_C_USER_INFO_STR, infoStr, USER_INFO);
	}
	/**
	 * 获取本地存储的关于用户中心对话框里的json信息
	 * @param context
	 * @return
	 */
	public static String getAvatarUrl(Context context) {
		return getString(context, KEY_C_AVATAR_URL_STR, USER_INFO);
	}
	/**
	 * 网络获取的json信息保存到本地
	 * @param context
	 * @param avatarName
	 */
	public static void setAvatarUrl(Context context, String avatarName) {
		putObject(context, KEY_C_AVATAR_URL_STR, avatarName, USER_INFO);
	}
	/**
	 * 获取悬浮窗x坐标
	 * @param context
	 * @return
	 */
	public static int getMiuiX(Context context) {
		return getInt(context, KEY_C_MIUI_X, USER_INFO);
	}
	/**
	 * 
	 * @param context
	 * @param coorX 保存悬浮窗x坐标
	 */
	public static void setMiuiX(Context context, int coorX) {
		putObject(context, KEY_C_MIUI_X, coorX, USER_INFO);
	}
	/**
	 * 获取悬浮窗Y坐标
	 * @param context
	 * @return
	 */
	public static int getMiuiY(Context context) {
		return getInt(context, KEY_C_MIUI_Y, USER_INFO);
	}
	/**
	 * 
	 * @param context
	 * @param coorY 保存悬浮窗Y坐标
	 */
	public static void setMiuiY(Context context, int coorY) {
		putObject(context, KEY_C_MIUI_Y, coorY, USER_INFO);
	}

	public static void addInfo(Context context, String username, String token) {
		putObject(context, username, token, ACCOUNT);
		putObject(context, KEY_C_TOKE, token, USER_INFO);
	}

	/**
	 * 通过用户名来删除对应的token 并删除当前的token
	 * 
	 * @param context
	 * @param username
	 */
	public static void deleteInfo(Context context, String username) {
		clearKey(context, username, ACCOUNT);
		clearKey(context, KEY_C_TOKE, USER_INFO);
	}

	/**
	 * 删除当前的token，并找出该对应的用户，删除用户信息
	 * 
	 * @param context
	 */
	public static void deleteCurrentInfo(Context context) {
		SharedPreferences info = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE);
		final String token = getCurrentToken(context);
		for (String tk : info.getAll().keySet()) {
			if (getString(context, tk, ACCOUNT).equals(token)) {

				clearKey(context, tk, ACCOUNT);
				break;
			}
		}
		clearKey(context, KEY_C_TOKE, USER_INFO);
	}

	public static String getToken(Context context, String username) {
		return getString(context, username, ACCOUNT);
	}

	public static String getCurrentToken(Context context) {
		return getString(context, KEY_C_TOKE, USER_INFO);
	}


}
