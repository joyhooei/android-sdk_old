package com.paojiao.sdk;

import android.content.Context;
import android.widget.Toast;

import com.paojiao.sdk.bean.AccounBase;
import com.paojiao.sdk.utils.JSONLoginInfo;


public class PJError extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7175581490089564454L;

	public static final int CODE_NO_TOKEN = 0x100;
	public static final int CODE_LOADING = 0x101;
	public static final int CODE_NO_INIT = 0x102;

	public static final int CODE_CANCEL = 0x201;
	public static final int CODE_NO_RESPONSE = 0x202;
	public static final int CODE_JSON_ERROR = 0x203;
	public static final int CODE_NO_DATA = 0x204;

	public static final String CODE_FAILED = "-1";
	public static final String CODE_WRONG_PWD = "-2";
	public static final String CODE_USER_FORBIDDEN = "-3";
	public static final String CODE_WRONG_USER = "-4";
	public static final String CODE_TIMEOUT = "-5";
	public static final String CODE_EXISTS_USER = "101";

	private int mErrorCode;
	private String mErrorMessage;

	public PJError() {
	}

	public PJError(int mErrorCode, String mErrorMessage) {
		this.mErrorCode = mErrorCode;
		this.mErrorMessage = mErrorMessage;
	}

	public int getMErrorCode() {
		return this.mErrorCode;
	}

	public String getMErrorMessage() {
		return this.mErrorMessage;
	}

	public static AccounBase checkForErrorA(Context context, String responseBody) {
		if (responseBody == null) {
			Toast.makeText(context, "服务器无返回", Toast.LENGTH_SHORT).show();
			return null;
		}
		AccounBase base = null;
		try {
			base = JSONLoginInfo.decodeA(responseBody);
		} catch (Exception ex) {
			Toast.makeText(context, "解析失败", Toast.LENGTH_SHORT).show();
			return null;
		}
		return base;
	}


}
