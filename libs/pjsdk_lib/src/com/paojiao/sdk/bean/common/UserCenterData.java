package com.paojiao.sdk.bean.common;

/**
 * 实体类对象应用于存储点击悬浮窗显示个人中心需要的一些json解析返回数据
 * 
 * @author zhounan
 */
public class UserCenterData {
	private String msg;
	private int code;
	private UCenterDaTa data;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public UCenterDaTa getData() {
		return data;
	}

	public void setData(UCenterDaTa data) {
		this.data = data;
	}

	public static class UCenterDaTa {
		private boolean autoLogin;
		private String avatar;
		private String niceName;
		private int pjPoint;
		private int pjSilver;
		private int signTimes;
		private int todaySign;
		private int userId;
		private int vipLevel;
		public boolean isAutoLogin() {
			return autoLogin;
		}
		public void setAutoLogin(boolean autoLogin) {
			this.autoLogin = autoLogin;
		}
		public String getAvatar() {
			return avatar;
		}
		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}
		public String getNiceName() {
			return niceName;
		}
		public void setNiceName(String niceName) {
			this.niceName = niceName;
		}
		public int getPjPoint() {
			return pjPoint;
		}
		public void setPjPoint(int pjPoint) {
			this.pjPoint = pjPoint;
		}
		public int getPjSilver() {
			return pjSilver;
		}
		public void setPjSilver(int pjSilver) {
			this.pjSilver = pjSilver;
		}
		public int getSignTimes() {
			return signTimes;
		}
		public void setSignTimes(int signTimes) {
			this.signTimes = signTimes;
		}
	
		public int getTodaySign() {
			return todaySign;
		}
		public void setTodaySign(int todaySign) {
			this.todaySign = todaySign;
		}
		public int getUserId() {
			return userId;
		}
		public void setUserId(int userId) {
			this.userId = userId;
		}
		public int getVipLevel() {
			return vipLevel;
		}
		public void setVipLevel(int vipLevel) {
			this.vipLevel = vipLevel;
		}
	}
}
