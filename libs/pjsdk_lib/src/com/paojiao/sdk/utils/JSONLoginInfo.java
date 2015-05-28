package com.paojiao.sdk.utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.paojiao.sdk.CallbackListener;
import com.paojiao.sdk.PJApi;
import com.paojiao.sdk.PJError;
import com.paojiao.sdk.bean.AccounBase;
import com.paojiao.sdk.bean.AccountData;
import com.paojiao.sdk.bean.common.Base;
import com.paojiao.sdk.bean.common.Hotspot;
import com.paojiao.sdk.bean.common.MenuNavs;
import com.paojiao.sdk.bean.common.UserCenterData;
import com.paojiao.sdk.bean.common.UserCenterData.UCenterDaTa;
import com.paojiao.sdk.bean.common.UserData;
import com.paojiao.sdk.config.BridgeMediation;

public class JSONLoginInfo {

	public static Base decode(String string) throws Exception {
		Base result = null;
		result = new Base();
		JSONObject job = new JSONObject(string);
		result.setCode(job.optString("code"));
		result.setMsg(job.optString("msg"));
		if (job.has("data")) {
			JSONObject data = job.getJSONObject("data");
			UserData iData = new UserData();
			iData.setActiveTime(data.optString("activeTime"));
			iData.setCreatedTime(data.optString("createdTime"));
			iData.setNiceName(data.optString("niceName"));
			iData.setEmail(data.optString("email"));
			iData.setId(data.optString("id"));
			iData.setPassword(data.optString("password"));
			iData.setPjSilver(data.optString("pjSilver"));
			iData.setSalt(data.optString("salt"));
			iData.setStatus(data.optString("status"));
			iData.setTableSuffix(data.optString("tableSuffix"));
			iData.setUid(data.optString("uid"));
			iData.setTimeout(data.optBoolean("timeout"));
			iData.setToken(data.optString("token"));
			iData.setUserName(data.optString("userName"));
			iData.setMobile(data.optString("mobile"));
			iData.setGameId(data.optString("gameId"));

			/**
			 * 以下内容由于客户端调整，暂时不使用json返回的结果，直接由客户端定义内容
			 ********************************************************************************* 
			 */
			JSONArray naviArray = data.optJSONArray("navs");
			if (naviArray != null && naviArray.length() != 0) {
				for (int i = 0; i < naviArray.length(); i++) {
					MenuNavs fMenu = new MenuNavs();
					JSONObject naviObject = naviArray.getJSONObject(i);
					fMenu.setNavName(naviObject.optString("navName"));
					fMenu.setNavUrl(naviObject.optString("navUrl"));
					fMenu.setSort(naviObject.optInt("sort"));
					iData.getNavs().add(fMenu);
				}
				// 设置全局变量
				BridgeMediation.setfMenu(iData.getNavs());
			} else {
				/**
				 * gameId未配置好，服务器没返回相应url,则显示默认
				 */
				setNavMenus(iData);
			}

			/**
			 ********************************************************************************* 
			 */
			JSONArray hotsArray = data.optJSONArray("hotspots");
			PJApi.hotspots = new ArrayList<Hotspot>();
			if (hotsArray != null && hotsArray.length() != 0) {
				Prints.i("hotsArray","----hotsArray:" + hotsArray);
				for (int i = 0; i < hotsArray.length(); i++) {
					Hotspot hotspot = new Hotspot();
					JSONObject hotObject = hotsArray.getJSONObject(i);
					hotspot.sethType(hotObject.optInt("hType"));
					hotspot.setTitle(hotObject.optString("title"));
					hotspot.setUrl(hotObject.optString("url"));
					hotspot.setCover(hotObject.optString("cover"));
					hotspot.setCoverSize(hotObject.optString("coverSize"));
					PJApi.hotspots.add(hotspot);
				}
			} else {
				for (int i = 0; i < 4; i++) {
					Hotspot hotspot = new Hotspot();
					hotspot.sethType(1);
					if (i == 1)
						hotspot.setCoverSize("big");
					else
						hotspot.setCoverSize("small");
					hotspot.setCover("http://anzhuo.webdown.paojiao.cn//game//slider//201409//25//1411639841702_.jpg");
					hotspot.setTitle("泡椒网，和您一起寻找好玩安卓游戏");
					hotspot.setUrl("http://paojiao.com");
					PJApi.hotspots.add(hotspot);
				}

			}
			result.setData(iData);
		}
		return result;
	}

	private static void setNavMenus(UserData iData) {
		iData.getNavs().clear();
		MenuNavs fMenu = new MenuNavs();
		fMenu.setNavName("论坛");
		fMenu.setNavUrl("http://bbs.paojiao.com");
		fMenu.setSort(0);
		iData.getNavs().add(fMenu);

		MenuNavs fMenu1 = new MenuNavs();
		fMenu1.setNavName("公会");
		fMenu1.setNavUrl("http://gh.paojiao.com");
		fMenu1.setSort(1);
		iData.getNavs().add(fMenu1);

		MenuNavs fMenu2 = new MenuNavs();
		fMenu2.setNavName("抽奖");
		fMenu2.setNavUrl("http://paojiao.com");
		fMenu2.setSort(2);
		iData.getNavs().add(fMenu2);

		MenuNavs fMenu3 = new MenuNavs();
		fMenu3.setNavName("礼包");
		fMenu3.setNavUrl("http://paojiao.com");
		fMenu3.setSort(3);
		iData.getNavs().add(fMenu3);

		MenuNavs fMenu4 = new MenuNavs();
		fMenu4.setNavName("充值");
		fMenu4.setNavUrl("http://ng.sdk.paojiao.cn/user/incharge.do");
		fMenu4.setSort(4);
		iData.getNavs().add(fMenu4);

		List<MenuNavs> fMenuss = new ArrayList<MenuNavs>();
		fMenuss.addAll(iData.getNavs());
		BridgeMediation.setfMenu(iData.getNavs());
	}

	public static AccounBase decodeA(String responseBody) throws Exception {
		AccounBase result = null;
		result = new AccounBase();
		JSONObject job = new JSONObject(responseBody);
		result.setCode(job.optString("code"));
		result.setMsg(job.optString("msg"));
		if (job.has("data")) {
			JSONArray data = job.getJSONArray("data");
			for (int i = 0; i < data.length(); i++) {
				AccountData iData = new AccountData();
				JSONObject account = data.getJSONObject(i);
				iData.setActiveTime(account.optString("activeTime"));
				iData.setNiceName(account.optString("niceName"));
				iData.setUid(account.optString("uid"));
				iData.setAutoLogin(account.optBoolean("autoLogin"));
				iData.setUserName(account.optString("userName"));
				iData.setMobile(account.optString("mobile"));
				iData.setToken(account.optString("token"));
				result.getaData().add(iData);
			}
		}
		return result;
	}

	public static Base checkForError(Context context, String responseBody) {
		if (responseBody == null) {
			Toast.makeText(context, "服务器无返回", Toast.LENGTH_SHORT).show();
			return null;
		}
		Base base = null;
		try {
			base = JSONLoginInfo.decode(responseBody);
		} catch (Exception ex) {
			if (ex != null)
				Prints.i("PJError", "ParseError:" + ex.toString());
			return null;
		}
		return base;
	}

	public static Base checkForError(String responseBody,
			CallbackListener callbackListener) {
		if (responseBody == null) {
			PJError error = new PJError(PJError.CODE_NO_RESPONSE, "服务器无返回");
			callbackListener.onInfoError(error);
			return null;
		}
		Base base = null;
		try {
			base = JSONLoginInfo.decode(responseBody);
		} catch (Exception ex) {
			callbackListener.onError(ex);
			return null;
		}
		return base;
	}

	public static UCenterDaTa getUserCenterInfo(String string) {
		UserCenterData result = new UserCenterData();
		UCenterDaTa uda = null;
		/**
		 * "msg":"信息返回成功。","code":"1","data":{"autoLogin":false,"niceName":
		 * "zhounanpj"
		 * ,"pjPoint":233,"pjSilver":967,"signTimes":1,"todaySign":0,"userId"
		 * :286268,"vipLevel":"0"}
		 */
		try {
			JSONObject job = new JSONObject(string);
			result.setCode(job.optInt("code"));
			result.setMsg(job.optString("msg"));
			if (job.has("data")) {
				uda = new UCenterDaTa();
				JSONObject data = job.getJSONObject("data");
				String MSG_TEMPLATE = "-0-={0}\n-1-={1}\n-2-={2}\n-3-={3}\n-4-={4}\n-5-={5}-6-={6}\n-7-={7}\n-8-={8}";
				String str0 = data.optBoolean("autoLogin") + "";
				String str1 = data.optString("avatar") + "";
				String str2 = data.optString("niceName") + "";
				String str3 = data.optInt("pjPoint") + "";
				String str4 = data.optInt("pjSilver") + "";
				String str5 = data.optInt("signTimes") + "";
				String str6 = data.optBoolean("todaySign") + "";
				String str7 = data.optInt("userId") + "";
				String str8 = data.optInt("vipLevel") + "";
				String endStr = MessageFormat.format(MSG_TEMPLATE, str0, str1,
						str2, str3, str4, str5, str6, str7, str8);
				Prints.d("JSONRsp", endStr);
				uda.setPjPoint(data.optInt("pjPoint"));
				uda.setPjSilver(data.optInt("pjSilver"));
				uda.setSignTimes(data.optInt("signTimes"));
				uda.setUserId(data.optInt("userId"));
				uda.setTodaySign(data.optInt("todaySign"));
				uda.setAutoLogin(data.optBoolean("autoLogin"));
				if (data.has("avatar"))
					uda.setAvatar(data.optString("avatar"));
				uda.setVipLevel(data.optInt("vipLevel"));
				if (data.has("niceName"))
					uda.setNiceName(data.optString("niceName"));
			}
		} catch (JSONException e) {
			if (e != null) {
				Prints.i("JSONException", e + "---eString" + e.toString());
			}
			e.printStackTrace();
		}
		return uda;
	}
}
