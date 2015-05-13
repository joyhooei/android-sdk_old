package org.yunyue;

import android.app.Application;

import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.entry.MiAppInfo;
import com.xiaomi.gamecenter.sdk.entry.ScreenOrientation;

public class MiAppApplication extends app
{
	public static MiAppInfo appInfo;

	@Override
	public void onCreate()
	{
		super.onCreate();

		/** SDK初始化 */
		appInfo = new MiAppInfo();
		appInfo.setAppId( "${APPID}" );
		appInfo.setAppKey( "${APPKEY}" );
		appInfo.setOrientation( ScreenOrientation.vertical ); // 横竖屏
		MiCommplatform.Init( this, appInfo );
}
}
