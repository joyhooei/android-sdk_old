package com.paojiao.sdk.bean.common;

import java.util.ArrayList;

/**
 * 用户的信息  当用户登陆成功 或 退出时 
 * @author zgt
 *
 */
public class UserData {
	
	
	
	

	private String createdTime;
	private String email;
	private String id;
	private String niceName;
	private String password;
	private String pjSilver;
	private String salt;
	private String status;
	private String tableSuffix;
	private String userName;
	private String gameId;
	private Hotspot hotspot;
	private ArrayList<MenuNavs> navs = new ArrayList<MenuNavs>();

	private String activeTime;
	private boolean timeout;
	private String token;
	private String uid;
	private String mobile;
	
	
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNiceName() {
		return niceName;
	}
	public void setNiceName(String niceName) {
		this.niceName = niceName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPjSilver() {
		return pjSilver;
	}
	public void setPjSilver(String pjSilver) {
		this.pjSilver = pjSilver;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTableSuffix() {
		return tableSuffix;
	}
	public void setTableSuffix(String tableSuffix) {
		this.tableSuffix = tableSuffix;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getActiveTime() {
		return activeTime;
	}
	public void setActiveTime(String activeTime) {
		this.activeTime = activeTime;
	}
	
	public boolean isTimeout() {
		return timeout;
	}
	public void setTimeout(boolean timeout) {
		this.timeout = timeout;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	public ArrayList<MenuNavs> getNavs() {
		return navs;
	}
	public void setNavs(ArrayList<MenuNavs> navs) {
		this.navs = navs;
	}
}
