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
 * 该接口用于获取部分个人信息。
 * 
 * @author 仁秋
 * 
 */
public class InfoApi extends BaseApi {

	private final static String PARAM_TOKEN = "token";
	private String url = URL.INFO_URL;

	public void pjPost(Context context, String token, AsyncHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		params.put(PARAM_TOKEN, token);
		super.pjPost(context, url, params, responseHandler);
	}
}
