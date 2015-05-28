/**
 * 
 */
package com.paojiao.sdk.api;

import android.content.Context;

import com.paojiao.sdk.api.base.BaseApi;
import com.paojiao.sdk.config.URL;
import com.paojiao.sdk.net.AsyncHttpResponseHandler;
import com.paojiao.sdk.net.RequestParams;

/**
 * 注册账号接口
 * @author 仁秋
 * 
 */
public class RegisterApi extends BaseApi {

	private final static String PARAM_NAME = "userName";
	private final static String PARAM_PWD = "password";
	// private final static String PARAM_NICKNAME = "niceName";
	// private final static String PARAM_EMAIL = "email";
	private String url = URL.REGISTER_URL;

	public void pjPost(Context context, String userName, String password, String email, String nickname, AsyncHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put(PARAM_NAME, userName);
		params.put(PARAM_PWD, password);
		// params.put(PARAM_EMAIL, email);
		// params.put(PARAM_NICKNAME, nickname);
		super.pjPost(context, url, params, responseHandler);
	}

}
