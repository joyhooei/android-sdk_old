package org.yunyue;

import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.app.View;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.kugou.game.sdk.api.common.IEventCode;
import com.kugou.game.sdk.api.common.IEventDataField;
import com.kugou.game.sdk.api.common.OnPlatformEventListener;
import com.kugou.game.sdk.api.common.User;
import com.kugou.game.sdk.api.online.KGPlatform;
import com.kugou.game.sdk.api.common.OnExitListener;
import com.kugou.game.sdk.api.online.KGPlatform;
import com.kugou.game.sdk.ui.widget.ToolBar;
import com.kugou.game.sdk.api.common.DynamicParamsProvider;

public class GameProxyImpl extends GameProxy implements OnPlatformEventListener{
    private Activity currentActivity;
    private ToolBar toolBar;

    public boolean supportLogin() {
        return true;
    }

    public boolean supportCommunity() {
        return true;
    }

    public boolean supportPay() {
        return true;
    }

    public void applicationInit(Activity activity) {
        currentActivity = activity;
        /** --------初始化SDK------------- */
        // SDK事件回调接口
        DynamicParamsProvider dynamicParamsProvider = new GameParamsProvider();
        // 初始化SDK(--必须先初始化SDK后，才能使用SDK的功能---)
        KGPlatform.init(this, sdkConfig, this, dynamicParamsProvider);
    }

    @Override
    public void onEventOccur(int eventCode, Bundle data) {
        switch (eventCode) {
            case IEventCode.ENTER_GAME_SUCCESS:
                // 获取登录成功后的用户信息
                User user = (User) data.getSerializable(IEventDataField.EXTRA_USER);
                org.yunyue.User u = new org.yunyue.User();
                u.userID = user.getUserName();
                u.token = user.getToken();
                userListerner.onLoginSuccess(u, null);
                doActionAfterLoginSuccess();
                break;
            case IEventCode.ENTER_GAME_FAILED:
                userListerner.onLoginFailed("", null);
                break;
            case IEventCode.ACCOUNT_CHANGED_SUCCESS:
                Log.d("sdk", "切换账号成功");
                break;
            case IEventCode.REGISTER_SUCCESS:
                //Toast.makeText(mContext, "注册成功", Toast.LENGTH_SHORT).show();
                break;
            case IEventCode.RECHARGE_SUCCESS:
                payCallBack.onSuccess("");
                break;
            case IEventCode.INTENT_TO_REBOOT_APP:
                Log.d("sdk", "即将重启游戏");
                break;
            case IEventCode.GO_BACK_TO_GAME:
                User info = KGPlatform.getCurrentUser();
                if (info != null) {// 已登录SDK
                    //Toast.makeText(mContext, "退出sdk回到游戏", Toast.LENGTH_SHORT).show();
                } else {
                    userListerner.onLogout(null);
                    //Toast.makeText(mContext, "退出sdk回到游戏(还未登录)", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void openCommunity(Activity activity) {
        KGPlatform.enterUserCenter(activity);
    }

    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        KGPlatform.enterRechargeCenter(activity, price);
    }

    public void login(Activity activity,Object customParams) {
        KGPlatform.enterGame(activity);
    }

    public void exit(Activity activity, ExitCallback callback) {
        KGPlatform.exitGame(activity, roleName, roleLevel, new OnExitListener() {
            @Override
            public void exitGame() {
                // 记得调用SDK的退出函数，释放资源
                KGPlatform.release(true);
                poem.quitApplication();
            }
        });
    }



    /**
     * SDK登录成功后的必选操作
     */
    private void doActionAfterLoginSuccess() {
        // 显示欢迎消息
        KGPlatform.showWelcomeDialog(currentActivity);

        // 创建浮动工具栏
        initToolBar();
    }

    private void initToolBar() {
        // 注意：1、悬浮窗必须在**登录成功后**进行创建；2、ToolBar不是单例模式，不要重复创建，不然会生成多个悬浮窗。
        toolBar = new ToolBar(currentActivity, ToolBar.LEFT_MID);
        toolBar.show();
        // 设置悬浮窗收拢
        toolBar.setCustomViewVisibility(View.GONE);
    }

    @Override
    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        // 销毁悬浮窗。注意：在界面被销毁前，必须执行该方法，建议在onDestroy()里执行
        if (toolBar != null) {
            toolBar.recycle();
            toolBar = null;
        }

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    public void setExtData(Context context, String ext) {
        try {
            JSONObject o = new JSONObject(ext);
            KGPlatform.sendEnterGameStatics(o.getString("name"), o.getInteger("level"));
        }catch(JSONException e) {
            Log.v("sdk", "set ext data failed");
        }
    }
}
