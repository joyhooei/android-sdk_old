/**
 * 
 */
package com.paojiao.sdk.api;

import android.content.Context;

import com.paojiao.sdk.api.base.BaseApi;
import com.paojiao.sdk.api.base.IServicObject;
import com.paojiao.sdk.bean.AccountBean;
import com.paojiao.sdk.config.URL;
import com.paojiao.sdk.net.AsyncHttpResponseHandler;
import com.paojiao.sdk.net.RequestParams;

/**
 * 获取用户帐号列表接口
 * @author 仁秋
 * 
 */
public class AccountApi extends BaseApi<AccountBean> {

	private String url = URL.ACCOUNT_URL;
	
	public AccountApi(){
		setEntityClass(AccountBean.class);
	}

	public void pjPost(Context context, AsyncHttpResponseHandler responseHandler) {
		RequestParams params = new RequestParams();
		
		//System.out.println("+++++++++++++++获取帐户："+url);
		
		super.pjPost(context, url, params, responseHandler);
	}
	
//	public void pjPost(Context context,IServicObject<AccountBean> object) {
//		RequestParams params = new RequestParams();
//		super.pjPost(context, url, params, object);
//	}

}
