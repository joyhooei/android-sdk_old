/*
 * 文 件 名: ChangeUserReceiver.java
 * 版 权: Huawei Technologies Co., Ltd. Copyright YYYY-YYYY, All rights reserved
 * 描 述: <描述>
 * 修 改 人: c00206870
 * 修改时间: 2014-6-28
 * 跟踪单号: <跟踪单号>
 * 修改单号: <修改单号>
 * 修改内容: <修改内容>
 */
package org.weilan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * 接收切换帐号的广播，这里提供的是静态注册的方式， 开发者可根据自己的实际需求是否能满足决定是否要把注册方式改为动态注册
 * 要求：要保证能接收到SDK发出的切换帐号的广播和跳转到游戏首页
 * 
 * @author c00206870
 * @version [版本号, 2014-6-28]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ChangeUserReceiver extends BroadcastReceiver
{
	private static final String CHANGE_USER_LOGIN_ACTION = "com.huawei.gamebox.changeUserLogin";
	private static final String GAMEBOX_EXTRA_DATA = "gamebox_extra_data";
	private static final String KEY_GAMEBOX_CHANGEUSERLOGIN = "gamebox_changeUserLogin";
	private static final int VALUE_CHANGE_USER = 1;

	@ Override
	public void onReceive( Context context, Intent intent )
	{
		String action = intent.getAction();

		if (CHANGE_USER_LOGIN_ACTION.equals(action))
		{
			Bundle bundle = intent.getBundleExtra(GAMEBOX_EXTRA_DATA);
			int value = bundle.getInt(KEY_GAMEBOX_CHANGEUSERLOGIN);
			if (VALUE_CHANGE_USER == value)
			{
                GameProxy.getInstance().userListerner.onLogout(null);
			}
		}
	}
}
