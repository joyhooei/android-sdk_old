package com.paojiao.sdk.bean.common;

/**
 * 实体类对象应用于存储一些json解析返回数据
 * @author zhounan
 * @version 2014-8-5 下午2:27:53
 */
public class Base {
	public static final String STATUS_OK = "1";

	private String msg;
	private String code;

	private UserData iData;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public UserData getData() {
		return iData;
	}

	public void setData(UserData iData) {
		this.iData = iData;
	}
	
}
