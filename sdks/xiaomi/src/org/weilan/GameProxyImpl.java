package org.weilan;

import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiAccountInfo;
import com.xiaomi.gamecenter.sdk.entry.ScreenOrientation;

import com.xiaomi.gamecenter.sdk.gam.MiResponseHandler;

import com.xiaomi.gamecenter.sdk.OnPayProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfo;
import com.xiaomi.gamecenter.sdk.GameInfoField;

public class GameProxyImpl extends GameProxy implements OnLoginProcessListener, OnPayProcessListener{
	private MiAccountInfo accountInfo;
    private PayCallBack payCallBack;
    private Activity currentActivity;
    private Object loginCustomParams;

	private Handler accountHandler = new Handler()
		{
			public void handleMessage( Message msg )
			{
				switch( msg.what )
				{
					case 30000:
                        User u = new User(); // fill User with accountInfo
                        u.userID = Long.toString(accountInfo.getUid());
                        u.token = accountInfo.getSessionId();
                        userListerner.onLoginSuccess(u, loginCustomParams);
						Toast.makeText( currentActivity, "登录成功", Toast.LENGTH_SHORT ).show();
                        loadGameInfo();
					break;
					case 40000:
                        userListerner.onLoginFailed("登录失败", loginCustomParams);
						Toast.makeText( currentActivity, "登录失败", Toast.LENGTH_SHORT ).show();
					break;
					case 70000:
						Toast.makeText( currentActivity, "正在执行，不要重复操作", Toast.LENGTH_SHORT ).show();
					break;
					default:
					break;
				}
			};
		};

	private Handler payHandler = new Handler()
		{
			public void handleMessage( Message msg )
			{
				switch( msg.what )
				{
					case MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS:
                        payCallBack.onSuccess(null);
						Toast.makeText( currentActivity, "支付成功", Toast.LENGTH_SHORT ).show();
					break;
					case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_CANCEL: 
					case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_CANCEL:
                        payCallBack.onFail(null);
						Toast.makeText( currentActivity, "支付取消", Toast.LENGTH_LONG ).show();
					break;
					case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_PAY_FAILURE:
                        payCallBack.onFail(null);
						Toast.makeText( currentActivity, "支付失败", Toast.LENGTH_LONG ).show();
					break;
					case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED:
                        payCallBack.onFail(null);
						Toast.makeText( currentActivity, "支付失败2", Toast.LENGTH_LONG ).show();
					break;
					case MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_LOGIN_FAIL:
                        payCallBack.onFail(null);
						Toast.makeText( currentActivity, "请先登录", Toast.LENGTH_LONG ).show();
					break;

				}
			};
		};


    @Override
    public void applicationInit(Activity activity) {
        Log.v("sdk", "applicationInit");
    }

    @Override
    public void onCreate(Activity activity) {
        Log.v("sdk", "onCreate");
    }

    @Override
    public void login(Activity activity,Object customParams) {
        Log.v("sdk", "login");
        loginCustomParams = customParams;

        // 调用SDK执行登陆操作
        currentActivity = activity;
        MiCommplatform.getInstance().miLogin( activity, this );
    }

    @Override
    public void logout(Activity activity,Object customParams) {
        Log.v("sdk", "logout");
        currentActivity = activity;
        userListerner.onLogout(customParams);
    }

    @Override
    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        Log.v("sdk", "pay:" + ID + "," + name + "," + price + "," + callBackInfo + "," + roleInfo.toString());
        currentActivity = activity;
        this.payCallBack = payCallBack;

        MiBuyInfo miBuyInfo = new MiBuyInfo();
        miBuyInfo.setCpOrderId(orderID);
        miBuyInfo.setCpUserInfo(callBackInfo);
        miBuyInfo.setAmount((int)price);

        //用户信息，网游必须设置、单机游戏或应用可选
        try {
            Bundle mBundle = new Bundle();
            mBundle.putString( GameInfoField.GAME_USER_BALANCE, "0" );   //用户余额
            mBundle.putString( GameInfoField.GAME_USER_GAMER_VIP, roleInfo.getString("vip") );  //vip等级
            mBundle.putString( GameInfoField.GAME_USER_LV, roleInfo.getString("level") );           //角色等级
            mBundle.putString( GameInfoField.GAME_USER_PARTY_NAME, roleInfo.getString("faction") );  //工会，帮派
            mBundle.putString( GameInfoField.GAME_USER_ROLE_NAME, roleInfo.getString("name") ); //角色名称
            mBundle.putString( GameInfoField.GAME_USER_ROLEID, roleInfo.getString("id") );    //角色id
            mBundle.putString( GameInfoField.GAME_USER_SERVER_NAME, roleInfo.getString("serverID") );  //所在服务器
            miBuyInfo.setExtraInfo( mBundle ); //设置用户信息
        } catch (JSONException e) {
            Log.e("sdk", "roleInfo parse failed, ignore");
        }

        MiCommplatform.getInstance().miUniPay( activity, miBuyInfo, this );
    }

    @Override
    public void exit(Activity activity, ExitCallback callback) {
        Log.v("sdk", "exit");
        callback.onNo3rdExiterProvide();
    }

    @Override
    public void applicationDestroy(Activity activity) {
        Log.v("sdk", "applicationDestroy");
    }

    @Override
    public void setExtData(Context context, String ext) {
        Log.v("sdk", "setExtData:" + ext);
    }

	@Override
	public void finishLoginProcess( int arg0, MiAccountInfo arg1 )
	{
		if ( MiErrorCode.MI_XIAOMI_PAYMENT_SUCCESS == arg0 )
		{
			accountInfo = arg1;
			accountHandler.sendEmptyMessage( 30000 );
		}
		else if ( MiErrorCode.MI_XIAOMI_PAYMENT_ERROR_ACTION_EXECUTED == arg0 )
		{
			accountHandler.sendEmptyMessage( 70000 );
		}
		else
		{
			accountHandler.sendEmptyMessage( 40000 );
		}
	}

	@Override
	public void finishPayProcess( int arg0 )
	{
		payHandler.sendEmptyMessage( arg0 );
	}

	private void loadGameInfo()
	{
		MiCommplatform.getInstance().loadGameInfo( currentActivity, new MiResponseHandler()
			{
				@Override
				protected void onError( int sdkStatus, JSONObject result )
				{
                    /*
					closeProgerssDialog();
					alert( currentActivity, "onError", "\n sdkstatus:" + sdkStatus );
                    */
				}

				@Override
				protected void onComplete( int arg1, JSONObject result )
				{
                    /*
					closeProgerssDialog();
					alert( GamActivity.this, "onComplete", "\n sdkstatus:" + arg1 + "\n result:" + result );
					Log.i( "Game Info", "rechargeable_heart: " + result.optString( "rechargeable_heart" ) );
					Log.i( "Game Info", "game_message_interval: " + result.optString( "game_message_interval" ) );
					Log.i( "Game Info", "heart_regen_interval: " + result.optString( "heart_regen_interval" ) );
					Log.i( "Game Info", "invitation_interval: " + result.optString( "invitation_interval" ) );
					Log.i( "Game Info", "name: " + result.optString( "name" ) );
					Log.i( "Game Info", "max_heart: " + result.optString( "max_heart" ) );
					Log.i( "Game Info", "score_reset_wday: " + result.optString( "score_reset_wday" ) );
					Log.i( "Game Info", "score_reset_hour: " + result.optString( "score_reset_hour" ) );
					Log.i( "Game Info", "next_score_reset_time: " + result.optString( "next_score_reset_time" ) );
					Log.i( "Game Info", "min_version_for_ios: " + result.optString( "min_version_for_ios" ) );
					Log.i( "Game Info", "min_version_for_android: " + result.optString( "min_version_for_android" ) );
					Log.i( "Game Info", "current_version_for_ios:" + result.optString( "current_version_for_ios" ) );
					Log.i( "Game Info", "current_version_for_android: " + result.optString( "current_version_for_android" ) );
					Log.i( "Game Info", "notice: " + result.optString( "notice" ) );

					JSONArray leaderboards = result.optJSONArray( "leaderboards" );

					if ( leaderboardKeys != null )
						leaderboardKeys = new String[0];

					leaderboardKeys = new String[leaderboards.length()];

					try
					{
						Log.i( "", "-------- Game leaderboards" );
						for ( int i = 0; i < leaderboards.length(); i++ )
						{
							JSONObject leaderboard = leaderboards.getJSONObject( i );
							Log.i( "", "name: " + leaderboard.optString( "name" ) );
							Log.i( "", "key: " + leaderboard.optString( "key" ) );
							leaderboardKeys[i] = leaderboard.optString( "key" );
						}
					}
					catch ( JSONException e )
					{
						e.printStackTrace();
					}
                    */
				}
			} );
	}

    @Override
    public boolean supportCommunity() {
        return false;
    }

}
