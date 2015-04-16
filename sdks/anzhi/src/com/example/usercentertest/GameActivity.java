/*

o * File Name: GameActivity.java 
 * History:
 * Created by Administrator on 2013-8-2
 */
package com.example.usercentertest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.anzhi.usercenter.sdk.AnzhiUserCenter;
import com.anzhi.usercenter.sdk.inter.AnzhiCallback;
import com.anzhi.usercenter.sdk.inter.InitSDKCallback;
import com.anzhi.usercenter.sdk.item.CPInfo;
import com.anzhi.usercenter.sdk.item.UserGameInfo;
import com.test.anzhi.newtest.a.R;

public class GameActivity extends Activity implements View.OnClickListener, com.anzhi.usercenter.sdk.inter.KeybackCall,
        InitSDKCallback, AnzhiCallback {
    /*
     * 用户中心接入流程如下
     */
    private static final String TAG = "anzhiTest";
    // 用户中心按键
    private Button mBtnViewUser;
    // 游戏支付按键
    private Button mBtnPay;
    // 切换账号按键
    private Button mBtnLogout;
    // 登录按键
    private Button mBtnLogin;
    // 显示悬浮窗口按钮
    private Button mButtonshowf;
    // 隐藏悬浮窗口按钮
    private Button mButtindisf;

    private String Appkey = "1378375366Az26xatNyDOD5EM6D2ys";// SDK 初始化参数
    private String AppSecret = "ug2KMdLi2JSr4naOE48XmL3h";

    private AnzhiUserCenter mAnzhiCenter;
    private LinearLayout mControlLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initInfo();// 初始化参数
        initView();// 初始化UI
    }

    // 初始化UI界面
    private void initView() {
        setContentView(R.layout.game);
        mControlLayout = (LinearLayout) findViewById(R.id.ll_control);
        mBtnViewUser = (Button) findViewById(R.id.btn_view_user);//
        mBtnViewUser.setOnClickListener(this);
        mBtnPay = (Button) findViewById(R.id.btn_charge);
        mBtnPay.setOnClickListener(this);
        mBtnLogout = (Button) findViewById(R.id.btn_logout);
        mBtnLogout.setOnClickListener(this);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);
        mButtonshowf = (Button) findViewById(R.id.btn_showf);
        mButtonshowf.setOnClickListener(this);
        mButtindisf = (Button) findViewById(R.id.btn_disf);
        mButtindisf.setOnClickListener(this);
    }

    /*
     * 初始化SDK信息
     */
    private void initInfo() {
        final CPInfo info = new CPInfo();
        // info.setOpenOfficialLogin(false);// 是否开启游戏官方账号登录，默认为关闭
        info.setAppKey(Appkey);
        info.setSecret(AppSecret);
        info.setChannel("AnZhi");
        info.setGameName(getResources().getString(R.string.app_name));
        mAnzhiCenter = AnzhiUserCenter.getInstance();
        mAnzhiCenter.azinitSDK(this, info, this);// 初始化方法
        mAnzhiCenter.setOpendTestLog(true);// 调试log，开关
        mAnzhiCenter.setKeybackCall(this);//设置返回游戏的接口
        mAnzhiCenter.setCallback(this);//设置登录、登出、支付通知
        mAnzhiCenter.setActivityOrientation(1);// 0横屏,1竖屏,4根据物理感应来选择方向
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
            case 1:
                mBtnLogin.setVisibility(View.GONE);
                mControlLayout.setVisibility(View.VISIBLE);
                break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_view_user:
            mAnzhiCenter.viewUserInfo(this);// 显示个人中心
            break;
        case R.id.btn_charge:
            showPopupWindow();// 显示充值弹窗
            break;
        case R.id.btn_login:
            mAnzhiCenter.login(this, true);// 用户中心登录的方法，
            break;
        case R.id.btn_logout:
            creatLogoutDialog();
            break;
        case R.id.btn_showf:
            mAnzhiCenter.showFloaticon();// 展示悬浮窗口
            break;
        case R.id.btn_disf:
            mAnzhiCenter.dismissFloaticon();// 隐藏客户端
        default:
            break;
        }
    }

    // 创建退出dialog
    private void creatLogoutDialog() {
        AlertDialog.Builder build = new AlertDialog.Builder(GameActivity.this);
        build.setMessage("是否退出或切换账户?");
        build.setNeutralButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAnzhiCenter.logout(GameActivity.this);// 用户中心退出方法
            }
        });
        build.setNegativeButton("取消", null);
        build.show();
    }

    public void showPopupWindow() {
        ListView listView = new ListView(this);
        List<String> data = new ArrayList<String>();
        data.add("0.02元");
        data.add("10元");
        data.add("不固定金额");
        data.add("匿名支付");
        CustomAdapter adapter = new CustomAdapter(data, this);
        listView.setAdapter(adapter);
        final PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_list_popup_above));
        popupWindow.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 4);
        popupWindow.setHeight(400);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setContentView(listView);
        popupWindow.showAsDropDown(mBtnPay, 0, 0);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                popupWindow.dismiss();
                switch (position) {
                case 0:
                    /*
                     * 用户中心定额支付方法， 第二个参数是渠道号暂时不用，直接传0 第二个参数是支付金额类型为folat,最低支付金额为0.1元（RMB）
                     * 如果金额为0会自动转为不定额支付
                     */
                    mAnzhiCenter.pay(GameActivity.this, 0, 0.1f, "游戏支付", getcode());
                    break;
                case 1:
                    mAnzhiCenter.pay(GameActivity.this, 0, 10f, "游戏支付", getcode());
                    break;
                case 2:
                    mAnzhiCenter.pay(GameActivity.this, 0, 0f, "游戏支付", getcode());
                    break;
                case 3:
                    /*
                     *  匿名支付方法，匿名支付没有定额支付，支付金额由玩家决定。
                     *  
                     */
                    mAnzhiCenter.pay(GameActivity.this, getcode());
                    break;
                default:
                    break;
                }
            }
        });
    }

    // 模拟产生厂商的订单号
    private String getcode() {
        SimpleDateFormat slp = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String st = slp.format(new Date(System.currentTimeMillis()));
        return "anzhi_" + st;
    }

    // 登录、登出、支付回调接口
    @Override
    public void onCallback(CPInfo cpInfo, String result) {
        Log.e(TAG, "result " + result);
        try {
            JSONObject json = new JSONObject(result);
            String key = json.optString("callback_key");
            if ("key_pay".equals(key)) {
                int code = json.optInt("code");
                String desc = json.optString("desc");
                String orderId = json.optString("order_id");
                String price = json.optString("price");
                String time = json.optString("time");
                if (code == 200 || code == 201) {
                    Toast.makeText(GameActivity.this,
                            "新银联测试" + "支付成功 \n订单号: " + orderId + "\n金额: " + price + "\n时间: " + time, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(GameActivity.this, desc, Toast.LENGTH_SHORT).show();
                }
            } else if ("key_logout".equals(key)) {
                Toast.makeText(GameActivity.this, "已退出账户 ", Toast.LENGTH_SHORT).show();
                mBtnLogin.setVisibility(View.VISIBLE);
                mControlLayout.setVisibility(View.GONE);
            } else if ("key_login".equals(key)) {
                int code = json.optInt("code");
                String sid = json.optString("sid");
                String uid = json.optString("uid");//uid为安置账号唯一标示
                if (code == 200) {
                    mHandler.sendEmptyMessage(1);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //
    @Override
    public void KeybackCall(String st) {
        Log.e(TAG, "st===========" + st);// 根据st来判断返回页面，st可能为空
    }

    // 初始化接口所需实现的方法，SDK初始化之后回调此方法，在此方法中可以调用登录方法，完成自动登录的流程；
    @Override
    public void ininSdkCallcack() {
        mAnzhiCenter.login(this, true);// 登录方法，第二个参数暂时不起任何作用，传true 即可
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAnzhiCenter.gameOver(this);// 销毁悬浮球，在退出需要调用
    }

}
