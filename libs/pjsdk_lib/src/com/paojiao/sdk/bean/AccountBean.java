package com.paojiao.sdk.bean;

import java.util.List;

/**
 * 用于 用户获取的实体类 与 AccountApi 对应
 * 
 * @author zgt
 */
public class AccountBean extends ResponData{
	
	private List<AccountData> data;
	
	public List<AccountData> getData() {
		return data;
	}



	public void setData(List<AccountData> data) {
		this.data = data;
	}



	public static class AccountData{

		private String activeTime; //活跃时间
		private boolean autoLogin; //是否自动登录
		private String id;  //用户id
		private String mobile; //手机
		private String niceName; //昵称
		private String payMoney;
		private String pjPoint;
		private String pjSilver;
		private String rechargeMoney;
		private String status;
		private String token; //登录标识
		private String userName; //用户名
		public String getActiveTime() {
			return activeTime;
		}
		public void setActiveTime(String activeTime) {
			this.activeTime = activeTime;
		}
		
		public boolean isAutoLogin() {
			return autoLogin;
		}
		public void setAutoLogin(boolean autoLogin) {
			this.autoLogin = autoLogin;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		public String getNiceName() {
			return niceName;
		}
		public void setNiceName(String niceName) {
			this.niceName = niceName;
		}
		public String getPayMoney() {
			return payMoney;
		}
		public void setPayMoney(String payMoney) {
			this.payMoney = payMoney;
		}
		public String getPjPoint() {
			return pjPoint;
		}
		public void setPjPoint(String pjPoint) {
			this.pjPoint = pjPoint;
		}
		public String getPjSilver() {
			return pjSilver;
		}
		public void setPjSilver(String pjSilver) {
			this.pjSilver = pjSilver;
		}
		public String getRechargeMoney() {
			return rechargeMoney;
		}
		public void setRechargeMoney(String rechargeMoney) {
			this.rechargeMoney = rechargeMoney;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
	}
	

}
