/**
 * 
 */
package com.paojiao.sdk.api;

import android.content.Context;

import com.paojiao.sdk.api.base.BaseApi;
import com.paojiao.sdk.config.DeviceInfo;
import com.paojiao.sdk.config.URL;
import com.paojiao.sdk.net.AsyncHttpResponseHandler;
import com.paojiao.sdk.net.RequestParams;
import com.paojiao.sdk.utils.Prints;

/**
 * 发送统计接口
 * @author 仁秋
 * 
 */
public class StatApi extends BaseApi<Object> {

	private String url = URL.STAT_URL;
	private String exit_url = URL.EXIT_STAT_URL;

	public void pjPost(Context context, AsyncHttpResponseHandler responseHandler) {
		DeviceInfo bean = new DeviceInfo(context);
		//只管发送，不管返回了。
		super.pjPost(context, url, bean.toRequestParams(), responseHandler);
	}
	
	public void pjPost(Context context, RequestParams params, AsyncHttpResponseHandler responseHandler){
		super.pjPost(context, exit_url, params, responseHandler);
		Prints.i("++++++++++++发送统计数据：",exit_url+"?"+params.toString());
	}
}
