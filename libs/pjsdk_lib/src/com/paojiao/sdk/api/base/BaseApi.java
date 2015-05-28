package com.paojiao.sdk.api.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;

import com.paojiao.sdk.PJApi;
import com.paojiao.sdk.config.DeviceInfo;
import com.paojiao.sdk.net.AsyncHttpClient;
import com.paojiao.sdk.net.AsyncHttpResponseHandler;
import com.paojiao.sdk.net.RequestParams;
import com.paojiao.sdk.utils.MD5Util;
import com.paojiao.sdk.utils.Utils;

/**
 * SDK基础类，用于处理数据提交，提交数据时会将一些公用数据一并提交
 * 
 * @author van
 * @version 1.0
 */
public class BaseApi<T> extends AsyncHttpClient {

	/** 渠道号 */
	public final static String PARAM_CHANNEL = "channel";
	/** gameId或appId */
	public final static String PARAM_CPID = "gameId";
	/** 设备唯一识别码 */
	public final static String PARAM_UDID = "udid";
	/** 签名字符串 */
	public final static String PARAM_SIGN = "sign";
	/** SDK版本号 */
	public final static String PARAM_SDK_VERSION = "sdkVersion";
	/** 接入游戏的版本号 */
	public final static String PARAM_APP_VERSION = "appVersion";

	@SuppressWarnings("unused")
	private Class<T> entityClass;

	/***
	 * 
	 * @param context
	 * @param class1
	 *            bean 对象的 class
	 */
	public BaseApi(Class<T> class1) {
		entityClass = class1;
	}

	public BaseApi() {

	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * 公用post数据方法
	 * 
	 * @param context
	 * @param url
	 * @param params
	 * @param responseHandler
	 */
	public void pjPost(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

		params.put(PARAM_CHANNEL, Utils.getMetaValue(context, "PJ_CHANNEL"));
		params.put(PARAM_CPID, PJApi.getAppId() + "");
		// 设备唯一识别码
		params.put(PARAM_UDID, DeviceInfo.getUDID());
		// SDK版本号
		params.put(PARAM_SDK_VERSION, PJApi.SDK_VERSION);
		// 接入游戏的版本号
		params.put(PARAM_APP_VERSION, DeviceInfo.appVersion(context));

		params.put("sign", sign(params.getUrlParams()));

		/** 调用方法，提交数据 */
		super.post(url, params, responseHandler);
	}

	private String sign(ConcurrentHashMap<String, String> map) {
		StringBuffer stringBuffer = new StringBuffer();
		Set<String> set = map.keySet();
		List<String> list = new ArrayList<String>();
		list.addAll(set);
		Collections.sort(list);
		for (int i = 0; i < list.size(); i++) {
			stringBuffer.append(list.get(i)+"="+map.get(list.get(i)));

		}
		stringBuffer.append("mduSfA5TgCe8xxpAqxsIFpPeWWRqCVH4");
		String md5 = MD5Util.Md5(stringBuffer.toString());
		return md5;

	}

	/**
	 * 公用post数据方法
	 * 
	 * @param context
	 * @param url
	 * @param params
	 * @param object
	 */
//	public void pjPost(Context context, String url, RequestParams params, final IServicObject<T> object) {
//		params.put(PARAM_CHANNEL, Utils.getMetaValue(context, "PJ_CHANNEL"));
//		params.put(PARAM_CPID, PJApi.getAppId() + "");
//		// 设备唯一识别码
//		params.put(PARAM_UDID, DeviceInfo.getUDID());
//		// SDK版本号
//		params.put(PARAM_SDK_VERSION, DeviceInfo.sdkVersion());
//		// 接入游戏的版本号
//		params.put(PARAM_APP_VERSION, DeviceInfo.appVersion(context));
//
//		params.put("sign", sign(params.getUrlParams()));
//		/** 调用方法，提交数据 */
//		super.post(url, params, new AsyncHttpResponseHandler() {
//
//			@Override
//			public void onStart() {
//				super.onStart();
//				object.onStart();
//			}
//
//			@Override
//			public void onFinish() {
//				super.onFinish();
//				object.onFinish();
//			}
//
//			@Override
//			protected void handleSuccessMessage(int statusCode, Header[] headers, String responseBody) {
//				super.handleSuccessMessage(statusCode, headers, responseBody);
//				T t = null;
//				try {
//					t = JSON.parseObject(responseBody, entityClass);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				object.succes(statusCode, headers, t);
//			}
//
//			@Override
//			protected void handleFailureMessage(Throwable e, String responseBody) {
//				super.handleFailureMessage(e, responseBody);
//				object.handleFailureMessage(e, responseBody);
//			}
//		});
//	}
}
