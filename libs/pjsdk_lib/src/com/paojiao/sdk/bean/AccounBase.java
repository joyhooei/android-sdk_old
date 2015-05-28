package com.paojiao.sdk.bean;

import java.util.ArrayList;


public class AccounBase {
	
	public static final String STATUS_OK = "1";

	private String msg;
	private String code;
	

	private ArrayList<AccountData> aData = new ArrayList<AccountData>();

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

	public ArrayList<AccountData> getaData() {
		return aData;
	}

	public void setaData(ArrayList<AccountData> aData) {
		this.aData = aData;
	}
}
