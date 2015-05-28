package com.paojiao.sdk.api.base;

import org.apache.http.Header;

/**
 * 
 * @author zhangguangtao
 *
 * @param <T>
 */
public abstract class IServicObject<T>{
	/**
	 * 获取数据成功
	 * @param t
	 */
	public abstract void succes(int statusCode, Header[] headers,T t);
	
	
	public void onStart(){
		
	}

	public void onFinish(){
		
	}

	protected void handleFailureMessage(Throwable e, String responseBody) {
	}
	
}