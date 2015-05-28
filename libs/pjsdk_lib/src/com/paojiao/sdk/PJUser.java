package com.paojiao.sdk;


/**
 * 用户信息
 * 
 * @author Van
 * 
 */
public class PJUser {

	/**用户名*/
	public static final String USERNAME = "com.paojiao.sdk.userName";
	/**昵称*/
	public static final String NICENAME = "com.paojiao.sdk.niceName";
	/**邮箱*/
	public static final String EMAIL = "com.paojiao.sdk.email";
	/**注册时间*/
	public static final String CREATEDTIME = "com.paojiao.sdk.createdTime";
	/**最后活跃时间*/
	public static final String ACTIVETIME = "com.paojiao.sdk.activeTime";
	/**头像*/
	public static final String AVATAR = "com.paojiao.sdk.avatar";
	/**TOKEN*/
	public static final String TOKEN = "com.paojiao.sdk.TOKEN";
	/**UID*/
	public static final String UID = "com.paojiao.sdk.UID";
	
	private int id;
	private String userName;// 用户名，登陆账号
	private String niceName;// 昵称
	private String password;// 密码
	private String email;// 邮箱
	private String createdTime;// 注册时间
	private String avatar;// 头像

	public PJUser() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}


	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
}
