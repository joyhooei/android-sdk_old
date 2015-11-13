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
import android.text.TextUtils;

import com.qihoo.gamecenter.sdk.matrix.Matrix;
import com.qihoo.gamecenter.sdk.protocols.ProtocolConfigs;
import com.qihoo.gamecenter.sdk.protocols.ProtocolKeys;
import com.qihoo.gamecenter.sdk.activity.ContainerActivity;
import com.qihoo.gamecenter.sdk.common.IDispatcherCallback;

import com.qihoo.gamecenter.sdk.demosp.payment.QihooPayInfo;
import com.qihoo.gamecenter.sdk.demosp.utils.ProgressUtil;
import com.qihoo.gamecenter.sdk.demosp.utils.QihooUserInfo;
import com.qihoo.gamecenter.sdk.demosp.utils.QihooUserInfoListener;
import com.qihoo.gamecenter.sdk.demosp.utils.QihooUserInfoTask;
import com.qihoo.gamecenter.sdk.demosp.utils.Utils;

import ${PACKAGE_NAME}.R;

public class GameProxyImpl extends GameProxy {
    private Activity currentActivity;
    protected String mAccessToken = null;
    private Object loginCustomParams;
    private Object switchCustomParams;
    private PayCallBack payCallBack;

    @Override
    public void applicationInit(Activity activity) {
        Log.v("sdk", "applicationInit");
        Matrix.init(activity);
    }

    @Override
    public void login(Activity activity,Object customParams) {
        // 调用SDK执行登陆操作
        Log.v("sdk", "login");
        currentActivity = activity;
        loginCustomParams = customParams;

        Matrix.execute(activity, getLoginIntent(false), mLoginCallback);
    }

    @Override
    public void logout(Activity activity, Object customParams) {
        Log.v("sdk", "logout");
        currentActivity = activity;
        userListerner.onLogout(customParams);
    }

    @Override
    public void switchAccount(Activity activity, Object customParams) {
        switchCustomParams = customParams;
        Matrix.invokeActivity(activity, getSwitchAccountIntent(false), mAccountSwitchCallback);
    }

    @Override
    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        Log.v("sdk", "pay:" + ID + "," + name + "," + price + "," + callBackInfo + "," + roleInfo.toString());
        currentActivity = activity;
        this.payCallBack = payCallBack;
        Log.v("sdk", "set payCallBack: " + payCallBack);

        // 创建QihooPay
        QihooPayInfo qihooPay = new QihooPayInfo();

        try {
            qihooPay.setAppUserName(roleInfo.getString("name"));
            qihooPay.setAppUserId(roleInfo.getString("id"));
            qihooPay.setQihooUserId(roleInfo.getString("raw_username").substring(8));
        } catch (Exception e) {
            Log.e("sdk", "get role info error.");
        }

        qihooPay.setMoneyAmount(Integer.toString((int)(price * 100)));
        qihooPay.setExchangeRate("10");

        qihooPay.setProductName(name);
        qihooPay.setProductId(ID);

        qihooPay.setNotifyUri("${PAY_URL}");
        qihooPay.setAppName(activity.getString(R.string.app_name));

        // 可选参数
        qihooPay.setAppExt1(callBackInfo);
        //qihooPay.setAppExt2(getString(R.string.demo_pay_app_ext2));
        qihooPay.setAppOrderId(orderID);

        Intent intent = getPayIntent(false, qihooPay);

        //Bundle bundle = new Bundle();
        // 可选参数，默认支付类型
        //bundle.putString(ProtocolKeys.DEFAULT_PAY_TYPE, "");
        //intent.putExtras(bundle);

        // 必需参数，使用360SDK的支付模块。
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_PAY);

        Matrix.invokeActivity(activity, intent, mPayCallback);
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

    /**
     * 生成调用360SDK登录接口的Intent
     * @param isLandScape 是否横屏
     * @return intent
     */
    private Intent getLoginIntent(boolean isLandScape) {
        Intent intent = new Intent(currentActivity, ContainerActivity.class);

        // 界面相关参数，360SDK界面是否以横屏显示。
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

        // 必需参数，使用360SDK的登录模块。
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_LOGIN);

        //是否显示关闭按钮
        intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, true);

        // 可选参数，是否支持离线模式，默认值为false
        //intent.putExtra(ProtocolKeys.IS_SUPPORT_OFFLINE, false);

        // 可选参数，是否在自动登录的过程中显示切换账号按钮
        //intent.putExtra(ProtocolKeys.IS_SHOW_AUTOLOGIN_SWITCH, false);

        // 可选参数，是否隐藏欢迎界面
        //intent.putExtra(ProtocolKeys.IS_HIDE_WELLCOME, true);

        // 可选参数，登录界面的背景图片路径，必须是本地图片路径
        //intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTRUE, getUiBackgroundPicPath());
        // 可选参数，指定assets中的图片路径，作为背景图
        //intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTURE_IN_ASSERTS, getUiBackgroundPathInAssets());

        //-- 以下参数仅仅针对自动登录过程的控制
        // 可选参数，自动登录过程中是否不展示任何UI，默认展示。
        //intent.putExtra(ProtocolKeys.IS_AUTOLOGIN_NOUI, getCheckBoxBoolean(R.id.isAutoLoginHideUI));

        // 可选参数，静默自动登录失败后是否显示登录窗口，默认不显示
        intent.putExtra(ProtocolKeys.IS_SHOW_LOGINDLG_ONFAILED_AUTOLOGIN, false);
        // 测试参数，发布时要去掉
        //intent.putExtra(ProtocolKeys.IS_SOCIAL_SHARE_DEBUG, true);

        return intent;
    }

    /***
     * 生成调用360SDK切换账号接口的Intent
     *
     * @param isLandScape 是否横屏
     * @return Intent
     */
    private Intent getSwitchAccountIntent(boolean isLandScape) {

        Intent intent = new Intent(currentActivity, ContainerActivity.class);

        // 必须参数，360SDK界面是否以横屏显示。
        intent.putExtra(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

        // 必需参数，使用360SDK的切换账号模块。
        intent.putExtra(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_SWITCH_ACCOUNT);

        //是否显示关闭按钮
        intent.putExtra(ProtocolKeys.IS_LOGIN_SHOW_CLOSE_ICON, true);

        // 可选参数，是否支持离线模式，默认值为false
        //intent.putExtra(ProtocolKeys.IS_SUPPORT_OFFLINE, getCheckBoxBoolean(R.id.isSupportOffline));

        // 可选参数，是否隐藏欢迎界面
        //intent.putExtra(ProtocolKeys.IS_HIDE_WELLCOME, getCheckBoxBoolean(R.id.isHideWellcome));

        /*
         * 指定界面背景（可选参数）：
         *  1.ProtocolKeys.UI_BACKGROUND_PICTRUE 使用的系统路径，如/sdcard/1.png
         *  2.ProtocolKeys.UI_BACKGROUND_PICTURE_IN_ASSERTS 使用的assest中的图片资源，
         *    如传入bg.png字符串，就会在assets目录下加载这个指定的文件
         *  3.图片大小不要超过5M，尺寸不要超过1280x720
         */
        // 可选参数，登录界面的背景图片路径，必须是本地图片路径
        // 可选参数，登录界面的背景图片路径，必须是本地图片路径
        //intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTRUE, getUiBackgroundPicPath());
        // 可选参数，指定assets中的图片路径，作为背景图
        //intent.putExtra(ProtocolKeys.UI_BACKGROUND_PICTURE_IN_ASSERTS, getUiBackgroundPathInAssets());

        // 测试参数，发布时要去掉
        intent.putExtra(ProtocolKeys.IS_SOCIAL_SHARE_DEBUG, false);

        return intent;
    }

    private QihooUserInfo parseUserInfoFromLoginResult(String loginRes) {
        try {
            JSONObject joRes = new JSONObject(loginRes);
            JSONObject joData = joRes.getJSONObject("data");
            JSONObject joUserLogin = joData.getJSONObject("user_login_res");
            JSONObject joUserLoginData = joUserLogin.getJSONObject("data");
            JSONObject joAccessInfo = joUserLoginData.getJSONObject("accessinfo");
            JSONObject joUserMe = joAccessInfo.getJSONObject("user_me");
            return QihooUserInfo.parseUserInfo(joUserMe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isCancelLogin(String data) {
        try {
            JSONObject joData = new JSONObject(data);
            int errno = joData.optInt("errno", -1);
            if (-1 == errno) {
                //Toast.makeText(currentActivity, data, Toast.LENGTH_LONG).show();
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    // ---------------------------------360SDK接口的回调-----------------------------------

    // 登录、注册的回调
    private IDispatcherCallback mLoginCallback = new IDispatcherCallback() {

        @Override
        public void onFinished(String data) {
            // press back
            if (isCancelLogin(data)) {
                return;
            }
            // 显示一下登录结果
            //Toast.makeText(currentActivity, data, Toast.LENGTH_LONG).show();
            // 解析access_token
            mAccessToken = parseAccessTokenFromLoginResult(data);

            if (!TextUtils.isEmpty(mAccessToken)) {
                // 登录结果直接返回的userinfo中没有qid，需要去应用的服务器获取用access_token获取一下带qid的用户信息
                User u = new User();
                u.token = mAccessToken;
                userListerner.onLoginSuccess(u, loginCustomParams);
            } else {
                //Toast.makeText(currentActivity, "get access_token failed!", Toast.LENGTH_LONG).show();
                userListerner.onLoginFailed("get access_token failed!", loginCustomParams);
            }
        }
    };

    // 切换账号的回调
    private IDispatcherCallback mAccountSwitchCallback = new IDispatcherCallback() {

        @Override
        public void onFinished(String data) {
            // press back
            if (isCancelLogin(data)) {
                return;
            }

            // 显示一下登录结果
            //Toast.makeText(currentActivity, data, Toast.LENGTH_LONG).show();

            // 解析access_token
            mAccessToken = parseAccessTokenFromLoginResult(data);

            if (!TextUtils.isEmpty(mAccessToken)) {
                // 登录结果直接返回的userinfo中没有qid，需要去应用的服务器获取用access_token获取一下带qid的用户信息
                User u = new User();
                u.token = mAccessToken;
                userListerner.onLoginSuccess(u, switchCustomParams);
            } else {
                //Toast.makeText(currentActivity, "get access_token failed!", Toast.LENGTH_LONG).show();
            }
        }
    };

    // 支付的回调
    protected IDispatcherCallback mPayCallback = new IDispatcherCallback() {

        @Override
        public void onFinished(String data) {
            Log.d("sdk", "mPayCallback, data is " + data);
            if(TextUtils.isEmpty(data)) {
                payCallBack.onFail("支付失败，数据为空");
                return;
            }

            JSONObject jsonRes;
            try {
                jsonRes = new JSONObject(data);
                // error_code 状态码： 0 支付成功， -1 支付取消， 1 支付失败， -2 支付进行中, 4010201和4009911 登录状态已失效，引导用户重新登录
                // error_msg 状态描述
                int errorCode = jsonRes.optInt("error_code");
                switch (errorCode) {
                    case 0:
                        //Toast.makeText(currentActivity, "支付成功", Toast.LENGTH_SHORT).show();
                        payCallBack.onSuccess("支付成功");
                        Log.v("sdk", "payCallBack.onSuccess");
                        break;
                    case 1:
                    case -1:
                    case -2:
                        String errorMsg = jsonRes.optString("error_msg");
                        //String text = "支付失败: " + errorCode + "," + errorMsg;
                        //Toast.makeText(currentActivity, errorMsg, Toast.LENGTH_SHORT).show();
                        payCallBack.onFail(errorMsg);
                        break;
                    case 4010201:
                        //acess_token失效
                        //Toast.makeText(currentActivity, "acess_token失效", Toast.LENGTH_SHORT).show();
                        payCallBack.onFail("acess_token失效");
                        break;
                    case 4009911:
                        //QT失效
                        //Toast.makeText(currentActivity, "qt失效", Toast.LENGTH_SHORT).show();
                        payCallBack.onFail("qt失效");
                        break;
                    default:
                        payCallBack.onFail("支付失败");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(currentActivity, "支付数据异常", Toast.LENGTH_LONG).show();
                payCallBack.onFail("支付数据异常");
            }
        }
    };

    private String parseAccessTokenFromLoginResult(String loginRes) {
        try {
            JSONObject joRes = new JSONObject(loginRes);
            JSONObject joData = joRes.getJSONObject("data");
            return joData.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 生成调用360SDK支付接口的Intent
     *
     * @param isLandScape
     * @param pay
     * @return Intent
     */
    protected Intent getPayIntent(boolean isLandScape, QihooPayInfo pay) {
        Bundle bundle = new Bundle();

        // 界面相关参数，360SDK界面是否以横屏显示。
        bundle.putBoolean(ProtocolKeys.IS_SCREEN_ORIENTATION_LANDSCAPE, isLandScape);

        // *** 以下非界面相关参数 ***

        // 设置QihooPay中的参数。

        // 必需参数，360账号id，整数。
        bundle.putString(ProtocolKeys.QIHOO_USER_ID, pay.getQihooUserId());

        // 必需参数，所购买商品金额, 以分为单位。金额大于等于100分，360SDK运行定额支付流程； 金额数为0，360SDK运行不定额支付流程。
        bundle.putString(ProtocolKeys.AMOUNT, pay.getMoneyAmount());

        // 必需参数，人民币与游戏充值币的默认比例，例如2，代表1元人民币可以兑换2个游戏币，整数。
        bundle.putString(ProtocolKeys.RATE, pay.getExchangeRate());

        // 必需参数，所购买商品名称，应用指定，建议中文，最大10个中文字。
        bundle.putString(ProtocolKeys.PRODUCT_NAME, pay.getProductName());

        // 必需参数，购买商品的商品id，应用指定，最大16字符。
        bundle.putString(ProtocolKeys.PRODUCT_ID, pay.getProductId());

        // 必需参数，应用方提供的支付结果通知uri，最大255字符。360服务器将把支付接口回调给该uri，具体协议请查看文档中，支付结果通知接口–应用服务器提供接口。
        bundle.putString(ProtocolKeys.NOTIFY_URI, pay.getNotifyUri());

        // 必需参数，游戏或应用名称，最大16中文字。
        bundle.putString(ProtocolKeys.APP_NAME, pay.getAppName());

        // 必需参数，应用内的用户名，如游戏角色名。 若应用内绑定360账号和应用账号，则可用360用户名，最大16中文字。（充值不分区服，
        // 充到统一的用户账户，各区服角色均可使用）。
        bundle.putString(ProtocolKeys.APP_USER_NAME, pay.getAppUserName());

        // 必需参数，应用内的用户id。
        // 若应用内绑定360账号和应用账号，充值不分区服，充到统一的用户账户，各区服角色均可使用，则可用360用户ID最大32字符。
        bundle.putString(ProtocolKeys.APP_USER_ID, pay.getAppUserId());

        // 可选参数，应用扩展信息1，原样返回，最大255字符。
        bundle.putString(ProtocolKeys.APP_EXT_1, pay.getAppExt1());

        // 可选参数，应用扩展信息2，原样返回，最大255字符。
        bundle.putString(ProtocolKeys.APP_EXT_2, pay.getAppExt2());

        // 可选参数，应用订单号，应用内必须唯一，最大32字符。
        bundle.putString(ProtocolKeys.APP_ORDER_ID, pay.getAppOrderId());

        // 必需参数，使用360SDK的支付模块。
        bundle.putInt(ProtocolKeys.FUNCTION_CODE, ProtocolConfigs.FUNC_CODE_PAY);

        Intent intent = new Intent(currentActivity, ContainerActivity.class);
        intent.putExtras(bundle);

        return intent;
    }

    public boolean supportCommunity() {
        return false;
    }

}
