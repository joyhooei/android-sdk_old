package com.paojiao.sdk.config;

import android.content.Context;
import android.os.Bundle;

import com.paojiao.sdk.PJApi;
import com.paojiao.sdk.api.base.BaseApi;
import com.paojiao.sdk.utils.Utils;

public class Route {

	
	public static final String URL = "pj_sdk_web_url.URL";
	public static final String SHOW_TAB = "pj_sdk_web_url.SHOW_TAB";
	public static final String PARAMS = "pj_sdk_web_url.PARAMS";
	
	public static Bundle creatUrlParams(Context mContext){
		Bundle params = new Bundle();
		params.putString(PJApi.TOKEN, ConfigurationInfo.getCurrentToken(mContext));
		params.putString(BaseApi.PARAM_UDID, DeviceInfo.getUDID());
		params.putString(BaseApi.PARAM_CPID, PJApi.getAppId() + "");
		//两个版本号，一个是sdk本身的，一个是接入应用的
		params.putString(BaseApi.PARAM_SDK_VERSION, PJApi.SDK_VERSION);
		params.putString(BaseApi.PARAM_APP_VERSION, DeviceInfo.appVersion(mContext));
		params.putString(BaseApi.PARAM_CHANNEL,
				Utils.getMetaValue(mContext, "PJ_CHANNEL"));
		return params;
	}

}
