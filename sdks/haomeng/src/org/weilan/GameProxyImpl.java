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

import com.weedong.gamesdkplatform.WdCommplatform;
import com.weedong.gamesdkplatform.WeeDongCallBackListener.OnLoginProcessListener;
import com.weedong.gamesdkplatform.base.WdAppInfo;
import com.weedong.gamesdkplatform.base.WdReturnCode;
import com.weedong.gamesdkplatform.dialog.FloatingService;
import com.weedong.gamesdkplatform.utils.ImageUtils;

public class GameProxyImpl extends GameProxy{

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return true;
    }

    public boolean supportPay() {
        return true;
    }

    @Override
    public void applicationInit(Activity activity) {
        Log.v("sdk", "applicationInit");

        WdCommplatform SDK = WdCommplatform.getInstance();
        WdAppInfo appInfo = new WdAppInfo();
        appInfo.setCtx( activity );
        try {
            String packagename = getPackageName();
            ApplicationInfo Info = mContext.getPackageManager()
                .getApplicationInfo(packagename,
                        PackageManager.GET_META_DATA);
            int msg = Info.metaData.getInt("WeeDong_APP_ID");
            Log.i("setAppId", "setAppId" + msg);
            appInfo.setAppId(String.valueOf(msg));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            appInfo.setAppId("1");
        }

        appInfo.setAppKey("PTcFqVY4jAEmPyKtpFtA4bp8b3rpvDUf");
        int i = SDK.wdInital(appInfo);
        Log.e("setAppId", "inital=======" + i);
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


}
