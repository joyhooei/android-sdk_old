package com.paojiao.sdk.bean;

import com.paojiao.sdk.bean.common.UserData;

/**
 * 用户登陆 成功 后的 信息
 * @author zgt
 *
 */
public class LoginUserInfoBean extends ResponData{
	
	private UserData data;

	public UserData getData() {
		return data;
	}

	public void setData(UserData data) {
		this.data = data;
	}
}
