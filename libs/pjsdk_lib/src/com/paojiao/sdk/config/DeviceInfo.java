package com.paojiao.sdk.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.paojiao.sdk.net.RequestParams;
import com.paojiao.sdk.service.OpenUDID_manager;
import com.paojiao.sdk.utils.NetworkControl;
import com.paojiao.sdk.utils.Utils;
/**
 * 
 * 手机 设备信息
 *
 */
public class DeviceInfo {

	/** IMEI */
	public String IMEI;
	/** 联网方式 */
	public String netType;
	/** mac地址 */
	public String macAddress;
	/** 机型 */
	public String mode;
	/** 系统版本 */
	public String sdk;
	/** 游侠版本 */
	public String yxVerion;
	/**
	 * 游侠版本号
	 */
	public String productVersion = "1.0";
	/** 渠道号 */
	public String channel_id;
	/** 统计类型 */
	public int operation = 1;
	/** 产品名称 */
	public String product = "paojiao_sdk";

	
	
	public int resourceId;

	public DeviceInfo(Context context) {
		this.IMEI = getImei(context);
		this.netType = getNetType(context);
		this.mode = getMode();
		this.sdk = getSdk();
		this.yxVerion = getYxVersion(context);
		this.macAddress = getLocalMacAddress(context);
		this.channel_id = getChannelId(context);
//		this.productVersion = getYxVersionCode(context);
	}

	public static String getImei(Context context2) {
		String imei = null;
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context2
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId();
		} catch (Exception ex) {
		}
		if (imei == null || imei.length() == 0) {
			imei = "unknown";
		}
		return imei;
	}

	public static String getNetType(Context context2) {
		String netType = null;
		try {
			netType = NetworkControl.getNetTypeString(context2);
		} catch (Exception ex) {
		}
		if (netType == null || netType.length() == 0) {
			netType = "unknown";
		}
		return netType;
	}

	public static String getMode() {
		return android.os.Build.MODEL;
	}

	public static String getSdk() {
		return android.os.Build.VERSION.RELEASE;
	}

	public static String getYxVersion(Context context2) {
		String yxVerion = null;
		try {
			PackageManager pm = context2.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context2.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			yxVerion = pi.versionName;
			if (yxVerion == null || yxVerion.length() <= 0) {
				yxVerion = "unknown";
			}
		} catch (Exception e) {
			yxVerion = "unknown";
			e.printStackTrace();
		}
		return yxVerion;
	}

	public static int getYxVersionCode(Context context2) {
		int code = 0;
		try {
			PackageManager pm = context2.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context2.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			code = pi.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}

	public static String getLocalMacAddress(Context context2) {
		String macAddress = null;
		try {
			WifiManager wifi = (WifiManager) context2
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			macAddress = info.getMacAddress();
		} catch (Exception ex) {
			macAddress = "unkown";
		}
		return macAddress;
	}

	public static String getChannelId(Context context2) {
		String channel_id = Utils.getMetaValue(context2,"PJ_CHANNEL");//getCustemMetaDataValue(context2, "PJ_CHANNEL");
		if (channel_id == null || channel_id.length() == 0) {
			channel_id = "default";
		}
		return channel_id;
	}

	public String toJsonString(String key, String value) {
		try {
			JSONObject message = new JSONObject();
			JSONObject stat = new JSONObject();
			stat.put("imei", this.IMEI);
			stat.put("net_type", this.netType);
			stat.put("mode", this.mode);
			stat.put("sdk", this.sdk);
			stat.put("productVersion", this.productVersion);
			stat.put("mac_address", this.macAddress);
			stat.put("cid", this.channel_id);
			stat.put("product", product);
			stat.put("operation", operation);
			stat.put("resourceId", resourceId);
			message.put("stat", stat);
			message.put(key, value);
			return message.toString();
		} catch (Exception ex) {
		}
		return null;
	}

	public String toJsonString() {
		try {
			JSONObject stat = new JSONObject();
			stat.put("imei", this.IMEI);
			stat.put("net_type", this.netType);
			stat.put("mode", this.mode);
			stat.put("sdk", this.sdk);
			stat.put("productVersion", this.productVersion);
			stat.put("mac_address", this.macAddress);
			stat.put("cid", this.channel_id);
			stat.put("product", product);
			stat.put("operation", operation);
			stat.put("resourceId", resourceId);
			return stat.toString();
		} catch (Exception ex) {
		}
		return null;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("?");
		buffer.append("productCode=");
		buffer.append(this.product);

		buffer.append("&");
		buffer.append("productVersion=");
		buffer.append(this.productVersion);

		buffer.append("&");
		buffer.append("sdk=");
		buffer.append(this.sdk);

		buffer.append("&");
		buffer.append("net_type=");
		buffer.append(this.netType);

		buffer.append("&");
		buffer.append("cid=");
		buffer.append(this.channel_id);

		buffer.append("&");
		buffer.append("imei=");
		buffer.append(this.IMEI);

		buffer.append("&");
		buffer.append("model=");
		buffer.append(this.mode);

		buffer.append("&");
		buffer.append("mac_address=");
		buffer.append(this.macAddress);

		buffer.append("&");
		buffer.append("operation=");
		buffer.append(this.operation);

		return buffer.toString();
	}

	public HashMap<String, String> toMap() {
		HashMap<String, String> tmp = new HashMap<String, String>();
		tmp.put("imei", this.IMEI);
		tmp.put("net_type", this.netType);
		tmp.put("mode", this.mode);
		tmp.put("sdk", this.sdk);
		tmp.put("productVersion", this.productVersion + "");
		tmp.put("mac_address", this.macAddress);
		tmp.put("product", product);
		tmp.put("operation", operation + "");

		return tmp;
	}
	
	public RequestParams toRequestParams() {
		RequestParams tmp = new RequestParams();
		tmp.put("imei", this.IMEI);
		tmp.put("net_type", this.netType);
		tmp.put("mode", this.mode);
		tmp.put("sdk", this.sdk);
		tmp.put("productVersion", this.productVersion);
		tmp.put("mac", this.macAddress);
		tmp.put("product", product);
		tmp.put("cid", this.channel_id);

		return tmp;
	}

	public List<NameValuePair> toNameValuePair() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("imei", this.IMEI));
		params.add(new BasicNameValuePair("net_type", this.netType));
		params.add(new BasicNameValuePair("sdk", this.sdk));
		params.add(new BasicNameValuePair("productVersion", this.productVersion + ""));
		params.add(new BasicNameValuePair("mac_address", this.macAddress));
		params.add(new BasicNameValuePair("product", product));
		params.add(new BasicNameValuePair("operation", operation + ""));

		return params;
	}

	public HashMap<String, String> toMap(HashMap<String, String> tmp) {
		tmp.put("imei", this.IMEI);
		tmp.put("net_type", this.netType);
		tmp.put("mode", this.mode);
		tmp.put("sdk", this.sdk);
		tmp.put("version", this.productVersion + "");
		tmp.put("mac_address", this.macAddress);
		tmp.put("product", product);
		tmp.put("operation", operation + "");

		return tmp;
	}
	
	
	
	
	/**
	 * 获取UDID
	 * @return
	 */
	public static String getUDID() {
		return OpenUDID_manager.isInitialized() == false ? "UNKNOW_UDID"
				: OpenUDID_manager.getOpenUDID();
	}



	public static String getResolution(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);

		Display display = wm.getDefaultDisplay();

		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		return metrics.widthPixels + "x" + metrics.heightPixels;
	}

	public static String getCarrier(Context context) {
		try {
			TelephonyManager manager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			return manager.getNetworkOperatorName();
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			Log.e("Countly", "No carrier found");
		}
		return "";
	}



	public static String appVersion(Context context) {
		String result = "1.0";
		try {
			result = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}

		return result;
	}
	

	public static String sdkVersion() {
		String result = "2.4.6";
		return result;
	}



}
