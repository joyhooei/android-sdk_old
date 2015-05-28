package com.paojiao.sdk.api;

import android.content.Context;

import com.paojiao.sdk.api.base.BaseApi;
import com.paojiao.sdk.config.URL;
import com.paojiao.sdk.net.AsyncHttpResponseHandler;
import com.paojiao.sdk.net.RequestParams;
import com.paojiao.sdk.utils.Prints;

/**
 * 个人中心对话框对应的一些接口
 * 
 * @author zhounan
 * @version 2014-5-23 上午9:19:05
 */
public class GetInfoApi extends BaseApi {
	private static GetInfoApi uSerInfoApi = null;
	private final static String GAME_ID = "gameId";
	private final static String PARAM_USER_ID = "userId";
	private final static String PARAM_TOKEN = "token";
	private final static String PARAM_STATUS = "status";
	private static String url;

	public static GetInfoApi getInstance() {
		if (uSerInfoApi == null) {
			uSerInfoApi = new GetInfoApi();
		}
		return uSerInfoApi;
	}

	private GetInfoApi() {
	}

	/**
	 * 获取热点信息
	 */
	public void hotsPotsPost(Context context, int game_id, String token,
			AsyncHttpResponseHandler handler) {
		url = URL.ACCESS_HOST_NEWS;
		RequestParams params = new RequestParams();
		params.put(GAME_ID, String.valueOf(game_id));
		params.put(PARAM_TOKEN, token);
		super.pjPost(context, url, params, handler);
	}

	/**
	 * 获个人信息所有信息
	 */
	public void pjPostGetAllInfo(Context context, String userId, String token,
			AsyncHttpResponseHandler handler) {
		url = URL.GET_SIMPLE_INFO;
		RequestParams params = new RequestParams();
		params.put(PARAM_USER_ID, userId);
		params.put(PARAM_TOKEN, token);
		super.pjPost(context, url, params, handler);
	}

	/**
	 * 更改登自动登录状态
	 * 
	 * @param userId
	 * @param token
	 * @param status
	 */
	public void pjPostChangeLogStatus(Context context, String userId,
			String token, String status, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		url = URL.CHANGE_LOGIN_STATUS;
		// 更改登自动登录状态
		params.put(PARAM_USER_ID, userId);
		params.put(PARAM_TOKEN, token);
		params.put(PARAM_STATUS, status);
		super.pjPost(context, url, params, handler);
	}

	/**
	 * 从服务器获取登录状态
	 * 
	 * @param userId
	 * @param token
	 * @param status
	 */
	public void pjPostGetLogStatus(Context context, String userId,
			String token, AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		url = URL.GET_LOGIN_STATUS;
		params.put(PARAM_USER_ID, userId);
		params.put(PARAM_TOKEN, token);
		super.pjPost(context, url, params, handler);
	}

	/**
	 * 获取连续签到天数
	 * 
	 * @author zhounan
	 * @param
	 */
	public void pjPostSignedDays(Context context, String userId, String token,
			AsyncHttpResponseHandler handler) {
		url = URL.GET_SIGN_DAYS;
		RequestParams params = new RequestParams();
		params.put(PARAM_USER_ID, userId);
		params.put(PARAM_TOKEN, token);
		super.pjPost(context, url, params, handler);

	}

	/**
	 * 签到接口
	 * 
	 * @author zhounan
	 * @param
	 */
	public void pjPostSign(Context context, String userId, String token,
			AsyncHttpResponseHandler handler) {
		url = URL.USER_SIGN_IN;
		RequestParams params = new RequestParams();
		params.put(PARAM_USER_ID, userId);
		params.put(PARAM_TOKEN, token);
		super.pjPost(context, url, params, handler);
	}

	/**
	 * 点击悬浮窗展示用户信息合为一个接口。
	 * 
	 * @author paojiao
	 * @param s
	 * @param s
	 * 
	 */
	public void pjPostGetLoginInfo(Context context, String userId,
			String token, AsyncHttpResponseHandler handler) {
		url = URL.USER_INFO;
		RequestParams params = new RequestParams();
		params.put(PARAM_USER_ID, userId);
		params.put(PARAM_TOKEN, token);
		Prints.i("x-f-c","--URL--"+url+"--params--"+params.getUrlParams());
		super.pjPost(context, url, params, handler);
	}

	/**
	 * 退出应用调用接口
	 * 
	 * @param context
	 * @param gameId
	 * @param handler
	 */
	public void pjPostExit(Context context, int gameId,
			AsyncHttpResponseHandler handler) {
		RequestParams params = new RequestParams();
		params.put("gameId", String.valueOf(gameId));
		super.pjPost(context, URL.USER_EXIT_INFO, params, handler);
	}
}
