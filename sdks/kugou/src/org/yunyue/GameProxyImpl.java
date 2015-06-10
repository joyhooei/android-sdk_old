package org.yunyue;

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
import android.view.View;

import com.kugou.game.sdk.api.online.OnlineConfig;
import com.kugou.game.sdk.api.common.ActivityOrientation;
import com.kugou.game.sdk.api.common.IEventCode;
import com.kugou.game.sdk.api.common.IEventDataField;
import com.kugou.game.sdk.api.common.OnPlatformEventListener;
import com.kugou.game.sdk.api.common.User;
import com.kugou.game.sdk.api.online.KGPlatform;
import com.kugou.game.sdk.api.common.OnExitListener;
import com.kugou.game.sdk.api.online.KGPlatform;
import com.kugou.game.sdk.ui.widget.ToolBar;
import com.kugou.game.sdk.api.common.DynamicParamsProvider;

public class GameProxyImpl extends GameProxy implements OnPlatformEventListener, DynamicParamsProvider {
    private Activity currentActivity;
    private ToolBar toolBar;
    private int currentServerId;
    private String currentRoleName = "";
    private int currentRoleLevel = 0;
    private String currentOrderID;
    private String currentExtension;
    private PayCallBack payCallBack;

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

        OnlineConfig sdkConfig = new OnlineConfig();
        /** --------填写SDK的必选配置项，参数来自酷狗提供的配置文档------------- */
        // 对应配置文档参数--MerchantId
        sdkConfig.setMerchantId(${MERCHANTID});
        // 对应配置文档参数--AppId
        sdkConfig.setAppId(${APPID});
        // 对应配置文档参数--AppKey
        sdkConfig.setAppKey("${APPKEY}");
        // 对应配置文档参数--GameId
        sdkConfig.setGameId(${GAMEID});
        // 对应配置文档参数--code ( 注意！！code内容里不要有换行)
        sdkConfig.setCode("${CODE}");

        /** --------设置SDK的可选配置项，具体可选项定义参看使用文档------------- */
        // 设置SDK界面的横竖屏
        sdkConfig.setActivityOrientation(ActivityOrientation.PORTRAIT);

        /** --------初始化SDK------------- */
        // 初始化SDK(--必须先初始化SDK后，才能使用SDK的功能---)
        KGPlatform.init(this, sdkConfig, this, this);
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
        this.payCallBack = payCallBack;
        currentOrderID = orderID;
        currentExtension = callBackInfo;
        KGPlatform.enterRechargeCenter(activity, Integer.parseInt(price));
    }

    public void login(Activity activity,Object customParams) {
        KGPlatform.enterGame(activity);
    }

    public void exit(Activity activity, ExitCallback callback) {
        KGPlatform.exitGame(activity, currentRoleName, currentRoleLevel, new OnExitListener() {
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
    }

    public void setExtData(Context context, String ext) {
        try {
            JSONObject o = new JSONObject(ext);
            currentRoleName = o.getString("name");
            currentRoleLevel = Integer.parseInt(o.getString("level"));
            currentServerId = Integer.parseInt(o.getString("serverID"));
            KGPlatform.sendEnterGameStatics(currentRoleName, currentRoleLevel);
        }catch(JSONException e) {
            Log.v("sdk", "set ext data failed");
        }
    }

    /**
     * 区服ID。初始化时没有区服id，或者选服失败，都返回默认值1.
     */
    public int getServerId() {
        return currentServerId;
    }

    /**
     * 角色名称。没有则返回默认值空字符串"".
     */
    public String getRoleName() {
        return currentRoleName;
    }

    /**
     * 获取游戏的充值订单号。注意：1、SDK进行充值时会通过该方法获取游戏生成的订单号 2、订单号必须保证非空，且唯一
     */
    public String createNewOrderId() {
        // 这里为了测试，随机生成字符串
        return currentOrderID;
    }

    /**
     * 扩展参数1：1.用于游戏客户端与服务端通信 2.游戏客户端从这个接口传入的参数，SDK服务端会原样返回给游戏服务端
     * 3.如果不需要使用，直接返回null
     */
    public String getExtension1() {
        return currentExtension;
    }

    /**
     * 扩展参数2：1.用于游戏客户端与服务端通信 2.游戏客户端从这个接口传入的参数，SDK服务端会原样返回给游戏服务端
     * 3.如果不需要使用，直接返回null
     */
    public String getExtension2() {
        return null;
    }
}
