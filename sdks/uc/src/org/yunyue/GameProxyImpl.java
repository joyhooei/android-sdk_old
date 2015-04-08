package org.yunyue;

import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import cn.uc.gamesdk.GameUserLoginResult;
import cn.uc.gamesdk.IGameUserLogin;
import cn.uc.gamesdk.UCCallbackListener;
import cn.uc.gamesdk.UCCallbackListenerNullException;
import cn.uc.gamesdk.UCFloatButtonCreateException;
import cn.uc.gamesdk.UCGameSDK;
import cn.uc.gamesdk.UCGameSDKStatusCode;
import cn.uc.gamesdk.UCLogLevel;
import cn.uc.gamesdk.UCLoginFaceType;
import cn.uc.gamesdk.UCOrientation;
import cn.uc.gamesdk.info.FeatureSwitch;
import cn.uc.gamesdk.info.GameParamInfo;
import cn.uc.gamesdk.info.OrderInfo;
import cn.uc.gamesdk.info.PaymentInfo;

import com.ninegame.game.GLog;
import com.ninegame.ucgamesdk.UCSdkConfig;
import com.ninegame.ucgamesdk.APNUtil;

public class GameProxyImpl extends GameProxy{
    private Activity currentActivity;
    private PayCallBack payCallBack;

    @Override
    public void applicationInit(Activity activity) {
        Log.v("sdk", "applicationInit");
        currentActivity = activity;
        // 初始化
        checkNetwork(activity);
    }

    @Override
    public void onCreate(Activity activity) {
        Log.v("sdk", "onCreate");
        currentActivity = activity;
    }

    @Override
    public void login(Activity activity,Object customParams) {
        Log.v("sdk", "login");
        currentActivity = activity;
        ucSdkLogin(activity, customParams);
    }

    @Override
    public void logout(Activity activity,Object customParams) {
        Log.v("sdk", "logout");
        ucSdkLogout(customParams);
    }

    @Override
    public void pay(Activity activity, String ID, String name, String orderID, float price, String callBackInfo, JSONObject roleInfo, PayCallBack payCallBack) {
        Log.v("sdk", "pay");
        currentActivity = activity;
        this.payCallBack = payCallBack;
		PaymentInfo pInfo = new PaymentInfo(); // 创建Payment对象，用于传递充值信息

		// 设置充值自定义参数，此参数不作任何处理，
		// 在充值完成后，sdk服务器通知游戏服务器充值结果时原封不动传给游戏服务器传值，字段为服务端回调的callbackInfo字段
		pInfo.setCustomInfo(callBackInfo);

		// 非必选参数，可不设置，此参数已废弃,默认传入0即可。
		// 如无法支付，请在开放平台检查是否已经配置了对应环境的支付回调地址，如无请配置，如有但仍无法支付请联系UC技术接口人。
		pInfo.setServerId(0);
        pInfo.setTransactionNumCP(orderID);

        try {
            pInfo.setRoleId(roleInfo.getString("id")); // 设置用户的游戏角色的ID，此为必选参数，请根据实际业务数据传入真实数据
            pInfo.setRoleName(roleInfo.getString("name")); // 设置用户的游戏角色名字，此为必选参数，请根据实际业务数据传入真实数据
            pInfo.setGrade(roleInfo.getString("level")); // 设置用户的游戏角色等级，此为可选参数
        } catch (JSONException e) {
            Log.e("sdk", "pay roleInfo json error");
        }

		// 非必填参数，设置游戏在支付完成后的游戏接收订单结果回调地址，必须为带有http头的URL形式。
		//pInfo.setNotifyUrl("http://192.168.1.1/notifypage.do");

		// 当传入一个amount作为金额值进行调用支付功能时，SDK会根据此amount可用的支付方式显示充值渠道
		// 如你传入6元，则不显示充值卡选项，因为市面上暂时没有6元的充值卡，建议使用可以显示充值卡方式的金额
		pInfo.setAmount(price);// 设置充值金额，此为可选参数

		try {
			UCGameSDK.defaultSDK().pay(activity.getApplicationContext(), pInfo,
					payResultListener);
		} catch (UCCallbackListenerNullException e) {
			// 异常处理
		}

    }

    @Override
    public void exit(Activity activity, ExitCallback callback) {
        Log.v("sdk", "exit");
        ucSdkExit(activity, callback);
    }

    @Override
    public void applicationDestroy(Context context) {
        Log.v("sdk", "applicationDestroy");
    }

	/**
	 * 进行网络检查
	 */
	public void checkNetwork(final Activity activity) {
		// !!!在调用SDK初始化前进行网络检查
		// 当前没有拥有网络
		if (false == APNUtil.isNetworkAvailable(activity)) {
			AlertDialog.Builder ab = new AlertDialog.Builder(activity);
			ab.setMessage("网络未连接,请设置网络");
			ab.setPositiveButton("设置", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent("android.settings.SETTINGS");
					activity.startActivityForResult(intent, 0);
				}
			});
			ab.setNegativeButton("退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.exit(0);
				}
			});
			ab.show();
		} else {
			ucSdkInit(activity);
		}
	}

	/**
	 * 必接功能<br>
	 * sdk初始化功能<br>
	 */
	private void ucSdkInit(final Activity activity) {
		// 监听用户注销登录消息
		// 九游社区-退出当前账号功能执行时会触发此监听
		try {
			UCGameSDK.defaultSDK().setLogoutNotifyListener(
					new UCCallbackListener<String>() {
						@Override
						public void callback(int statuscode, String msg) {
							// TODO 此处需要游戏客户端注销当前已经登录的游戏角色信息
							String s = "游戏接收到用户退出通知。" + msg + statuscode;
							Log.e("UCGameSDK", s);
							// 未成功初始化
							if (statuscode == UCGameSDKStatusCode.NO_INIT) {
								// 调用SDK初始化接口
								ucSdkInit(activity);
							}
							// 未登录成功
							if (statuscode == UCGameSDKStatusCode.NO_LOGIN) {
								// 调用SDK登录接口
								ucSdkLogin(activity, null);
							}
							// 退出账号成功
							if (statuscode == UCGameSDKStatusCode.SUCCESS) {
								// 执行销毁悬浮按钮接口
								ucSdkDestoryFloatButton(activity);
								// 调用SDK登录接口
								ucSdkLogin(activity, null);
							}
							// 退出账号失败
							if (statuscode == UCGameSDKStatusCode.FAIL) {
								// 调用SDK退出当前账号接口
								ucSdkLogout(null);
							}
						}
					});
		} catch (UCCallbackListenerNullException e) {
			// 处理异常
		}

		try {
			GameParamInfo gpi = new GameParamInfo();// 下面的值仅供参考
			gpi.setCpId(UCSdkConfig.cpId);
			gpi.setGameId(UCSdkConfig.gameId);
			gpi.setServerId(UCSdkConfig.serverId); // 服务器ID可根据游戏自身定义设置，或传入0
			// gpi.setChannelId(2); // 渠道号统一处理，已不需设置，此参数已废弃，服务端此参数请设置值为2

			// 在九游社区设置显示查询充值历史和显示切换账号按钮，
			// 在不设置的情况下，默认情况情况下，生产环境显示查询充值历史记录按钮，不显示切换账户按钮
			// 测试环境设置无效
			gpi.setFeatureSwitch(new FeatureSwitch(true, false));

			// 设置SDK登录界面为横屏，个人中心及充值页面默认为强制竖屏，无法修改
			// UCGameSDK.defaultSDK().setOrientation(UCOrientation.LANDSCAPE);

			// 设置SDK登录界面为竖屏
			UCGameSDK.defaultSDK().setOrientation(UCOrientation.PORTRAIT);

			// 设置登录界面：
			// USE_WIDGET - 简版登录界面
			// USE_STANDARD - 标准版登录界面
			UCGameSDK.defaultSDK().setLoginUISwitch(UCLoginFaceType.USE_WIDGET);

			// setUIStyle已过时，不需调用。
			// UCGameSDK.defaultSDK().setUIStyle(UCUIStyle.STANDARD);

			UCGameSDK.defaultSDK().initSDK(activity, UCLogLevel.DEBUG,
					UCSdkConfig.debugMode, gpi,
					new UCCallbackListener<String>() {
						@Override
						public void callback(int code, String msg) {
							Log.e("UCGameSDK", "UCGameSDK初始化接口返回数据 msg:" + msg
									+ ",code:" + code + ",debug:"
									+ UCSdkConfig.debugMode + "\n");
							switch (code) {
							// 初始化成功,可以执行后续的登录充值操作
							case UCGameSDKStatusCode.SUCCESS:
                                break;
							default:
								break;
							}
						}
					});
		} catch (UCCallbackListenerNullException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy(Activity activity) {
		GLog.d("sdk", "----------onDestroy---------");
		ucSdkDestoryFloatButton(activity);
        super.onDestroy(activity);
	}

	/**
	 * 必接功能<br>
	 * 悬浮按钮销毁<br>
	 * 悬浮按钮销毁需要在UI线程中调用<br>
	 */
	private void ucSdkDestoryFloatButton(final Activity activity) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				// 悬浮按钮销毁功能
				UCGameSDK.defaultSDK().destoryFloatButton(activity);
			}
		});
	}

	/**
	 * 必接功能<br>
	 * SDK登录功能<br>
	 * SDK客户端登录成功后，游戏客户端通过getsid()方法获取SDK客户端的sid，发送给游戏服务器，
	 * 游戏服务器使用此sid进行服务端接口调用，即可获取用户标示， 随后游戏服务器向游戏客户端发送用户标示即可。
	 * （注：游戏客户端无法直接从SDK客户端获取用户标示）
	 * 详细流程可见接入文档“02-技术文档-SDK总体机制\UC游戏_SDK_开发参考说明书_总体机制_vXXX.pdf”。
	 */
	private void ucSdkLogin(final Activity activity, final Object customParams) {

		activity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					// 登录接口回调。从这里可以获取登录结果。
					UCCallbackListener<String> loginCallbackListener = new UCCallbackListener<String>() {
						@Override
						public void callback(int code, String msg) {
							Log.e("UCGameSDK", "UCGameSdk登录接口返回数据:code=" + code
									+ ",msg=" + msg);

							// 登录成功。此时可以获取sid。并使用sid进行游戏的登录逻辑。
							// 客户端无法直接获取UCID
							if (code == UCGameSDKStatusCode.SUCCESS) {

								// 获取sid。（注：ucid需要使用sid作为身份标识去SDK的服务器获取）
                                User u = new User();
								u.token = UCGameSDK.defaultSDK().getSid();
                                userListerner.onLoginSuccess(u, customParams);

								// 执行悬浮按钮创建方法
								ucSdkCreateFloatButton(activity);
								// 执行悬浮按钮显示方法
								ucSdkShowFloatButton(activity);
							}

							// 登录失败。应该先执行初始化成功后再进行登录调用。
							if (code == UCGameSDKStatusCode.NO_INIT) {
								// 没有初始化就进行登录调用，需要游戏调用SDK初始化方法
								ucSdkInit(activity);
							}

							// 登录退出。该回调会在登录界面退出时执行。
							if (code == UCGameSDKStatusCode.LOGIN_EXIT) {
								// 登录界面关闭，游戏需判断此时是否已登录成功进行相应操作
							}
						}
					};

					// 未启用官方老账号登录
                    UCGameSDK.defaultSDK().login(activity, loginCallbackListener);
				} catch (UCCallbackListenerNullException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 必接功能<br>
	 * 当游戏退出前必须调用该方法，进行清理工作。建议在游戏退出事件中进行调用，必须在游戏退出前执行<br>
	 * 如果游戏直接退出，而不调用该方法，可能会出现未知错误，导致程序崩溃<br>
	 */
	private void ucSdkExit(final Activity activity, final ExitCallback callback) {
		UCGameSDK.defaultSDK().exitSDK(activity, new UCCallbackListener<String>() {
			@Override
			public void callback(int code, String msg) {
				if (UCGameSDKStatusCode.SDK_EXIT_CONTINUE == code) {
					// 此加入继续游戏的代码

				} else if (UCGameSDKStatusCode.SDK_EXIT == code) {
					// 在此加入退出游戏的代码
					ucSdkDestoryFloatButton(activity);
                    callback.onExit();
				}
			}
		});
	}

	/**
	 * 选接功能<br>
	 * 游戏可通过调用下面方法，退出当前登录的账号<br>
	 * 通过退出账号侦听器（此侦听器在初始化前经由setLogoutNotifyListener 方法设置）<br>
	 * 把退出消息返回给游戏，游戏可根据状态码进行相应的处理。<br>
	 */
	private void ucSdkLogout(Object customParams) {
		try {
			UCGameSDK.defaultSDK().logout();
		} catch (UCCallbackListenerNullException e) {
			// 未设置退出侦听器
		}
        userListerner.onLogout(customParams);
	}

	/**
	 * 必接功能<br>
	 * 悬浮按钮创建及显示<br>
	 * 悬浮按钮必须保证在SDK进行初始化成功之后再进行创建需要在UI线程中调用<br>
	 */
	private void ucSdkCreateFloatButton(final Activity activity) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				try {
					// 创建悬浮按钮。该悬浮按钮将悬浮显示在GameActivity页面上，点击时将会展开悬浮菜单，菜单中含有
					// SDK 一些功能的操作入口。
					// 创建完成后，并不自动显示，需要调用showFloatButton(Activity,
					// double, double, boolean)方法进行显示或隐藏。
					UCGameSDK.defaultSDK().createFloatButton(activity,
							new UCCallbackListener<String>() {

								@Override
								public void callback(int statuscode, String data) {
									Log.d("SelectServerActivity`floatButton Callback",
											"statusCode == " + statuscode
													+ "  data == " + data);
								}
							});

				} catch (UCCallbackListenerNullException e) {
					e.printStackTrace();
				} catch (UCFloatButtonCreateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 必接功能<br>
	 * 悬浮按钮显示<br>
	 * 悬浮按钮显示需要在UI线程中调用<br>
	 */
	private void ucSdkShowFloatButton(final Activity activity) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				// 显示悬浮图标，游戏可在某些场景选择隐藏此图标，避免影响游戏体验
				try {
					UCGameSDK.defaultSDK().showFloatButton(activity, 100, 50, true);
				} catch (UCCallbackListenerNullException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private UCCallbackListener<OrderInfo> payResultListener = new UCCallbackListener<OrderInfo>() {
		@Override
		public void callback(int statudcode, OrderInfo orderInfo) {
			if (statudcode == UCGameSDKStatusCode.NO_INIT) {
				// 没有初始化就进行登录调用，需要游戏调用SDK初始化方法
                ucSdkInit(currentActivity);
                payCallBack.onFail("未初始化");
                return;
			}
			if (statudcode == UCGameSDKStatusCode.SUCCESS) {
				// 成功充值
				if (orderInfo != null) {
					String ordereId = orderInfo.getOrderId();// 获取订单号
					float orderAmount = orderInfo.getOrderAmount();// 获取订单金额
					int payWay = orderInfo.getPayWay();
					String payWayName = orderInfo.getPayWayName();
					System.out.print(ordereId + "," + orderAmount + ","
							+ payWay + "," + payWayName);
				}
                payCallBack.onSuccess("充值成功");
			}
			if (statudcode == UCGameSDKStatusCode.PAY_USER_EXIT) {
				// 用户退出充值界面。
                payCallBack.onFail("用户取消");
			}
		}

	};

    /**
	 * 必接功能<br>
	 * 提交游戏扩展数据功能，游戏SDK要求游戏在运行过程中，提交一些用于运营需要的扩展数据，这些数据通过扩展数据提交方法进行提交。
	 * 登录游戏角色成功后调用此段
	 */
    @Override
    public void setExtData(Context context, String ext) {
		try {
            JSONObject src = new JSONObject(ext);
			JSONObject jsonExData = new JSONObject();
			jsonExData.put("roleId", src.getString("id"));// 玩家角色ID
			jsonExData.put("roleName", src.getString("name"));// 玩家角色名
			jsonExData.put("roleLevel", src.getString("level"));// 玩家角色等级
			jsonExData.put("zoneId", src.getString("serverID"));// 游戏区服ID
			jsonExData.put("zoneName", src.getString("serverName"));// 游戏区服名称
			UCGameSDK.defaultSDK().submitExtendData("loginGameRole", jsonExData);
		} catch (Exception e) {
			// 处理异常
            Log.e("sdk", "set Ext Data exception");
			e.printStackTrace();
		}
	}

    @Override
    public boolean supportCommunity() {
        return false;
    }
}
