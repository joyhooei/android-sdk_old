package com.paojiao.sdk.jpush;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
				+ ", extras: " + printBundle(bundle));
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
			// send the Registration Id to your server...
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			String msgReceiver = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + msgReceiver);
			processCustomMessage(context, bundle);

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
			int notifactionId = bundle
					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
			processMyMessage(context, bundle);
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			Log.d(TAG,
					"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..
		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Log.e(TAG, "[MyReceiver]" + intent.getAction()
					+ " connected state change to " + connected);
		} else {
			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}

	/**
	 * @author zhounan
	 * @param
	 */
	private void processCustomMessage(Context context, Bundle bundle) {
		// TODO Auto-generated method stub

	}

	/**
	 * 处理点击通知事件,主要针对自定义通知。
	 * 
	 * @author zhounan
	 * @param
	 * 
	 */
	private void processMyMessage(Context context, Bundle bundle) {
		// 推送的msg内容
		String url = null;
		int gameId = 0;
		// 自定义通知字段的json字符串
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		if (!ExampleUtil.isEmpty(extras)) {
			try {
				JSONObject extraJson = new JSONObject(extras);
				if (null != extraJson && extraJson.length() > 0) {
					boolean isUrl = (extraJson.has("url"));
					boolean isGid = (extraJson.has("gameId"));
					url = (extraJson.has("url")) ? extraJson.getString("url")
							: null;
					gameId = (extraJson.has("gameId")) ? extraJson
							.getInt("gameId") : 0;
				}
			} catch (JSONException e) {

			}

		}

		// --------------------跳转打开某一个网页
		if (url != null) {
			Intent intent = new Intent();
			String msgContent = bundle.getString(JPushInterface.EXTRA_ALERT);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse("http://" + url); // URL要JSON加一条
			intent.setData(content_url);
			context.startActivity(intent);
			// --------------------跳转到某一个应用
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

}
