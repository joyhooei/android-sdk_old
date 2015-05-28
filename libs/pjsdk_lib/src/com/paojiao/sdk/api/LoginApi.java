/**
 * 
 */
package com.paojiao.sdk.api;

import android.content.Context;

import com.paojiao.sdk.api.base.BaseApi;
import com.paojiao.sdk.bean.LoginUserInfoBean;
import com.paojiao.sdk.config.URL;
import com.paojiao.sdk.net.AsyncHttpResponseHandler;
import com.paojiao.sdk.net.RequestParams;

/**
 * 登录接口
 * @author 仁秋
 * 
 */
public class LoginApi extends BaseApi<LoginUserInfoBean> {

	private final static String PARAM_TOKEN = "token";
  
	private final static String PARAM_NAME = "userName";
	private final static String PARAM_PWD = "password";
	private String url = URL.LOGIN_URL;

	public void pjPost(Context context, String userName, String password, AsyncHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put(PARAM_NAME, userName);
		params.put(PARAM_PWD, password);
		super.pjPost(context, url, params, responseHandler);
	}

	public void pjPost(Context context, String token, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.put(PARAM_TOKEN, token);
		super.pjPost(context, URL.INFO_URL, params, handler);
	}
}
