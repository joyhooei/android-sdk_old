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
 * 登出/注销账号接口
 * @author 仁秋
 * 
 */
public class LogoutApi extends BaseApi {

	private final static String TOKEN = "token";
	private String url = URL.LOGOUT_URL;

	public void pjPost(Context context, String token,
			AsyncHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put(TOKEN, token);
		super.pjPost(context, url, params, responseHandler);
	}

}
