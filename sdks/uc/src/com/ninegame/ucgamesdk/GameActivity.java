package com.ninegame.ucgamesdk;

import android.widget.Button;
import android.widget.RadioButton;

import org.json.JSONObject;

import ucgamesdk.example.uc.*;

import com.ninegame.game.GLog;
import com.ninegame.game.LAGameView;
import com.ninegame.game.jigsaw.JigsawScreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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

/**
 * 
 * 游戏主程序。包含了对UCGameSDK接口的调用。
 * 必接功能：开机启动页（闪屏）、UCID登录、提交游戏扩展数据、悬浮按钮、SDK充值（如未开启充值可不调用）、退出SDK<br>
 * 建议参考以下帖子进行接入，方便快捷、减少出错！<br>
 * 【SDK接入】接入注意事项汇总贴（必看）http://bbs.9game.cn/thread-3650324-1-1.html<br>
 * 【SDK接入】九游SDK必须接入的功能！务必查看！http://bbs.9game.cn/thread-3704011-1-1.html<br>
 * 
 * @author chenzh
 * 
 */
public class GameActivity extends Activity {

	protected static final String GameActivity = null;

	private LAGameView gameView;

	final GameActivity me = this;

	public void onCreate(Bundle b) {
		GLog.d("GameActivity", "----------onCreate---------");
		super.onCreate(b);
		this.setContentView(R.layout.splashscreen); // 设置启动画面
		checkNetwork();
	}

	/**
	 * 进行网络检查
	 */
	public void checkNetwork() {
		// !!!在调用SDK初始化前进行网络检查
		// 当前没有拥有网络
		if (false == APNUtil.isNetworkAvailable(this)) {
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setMessage("网络未连接,请设置网络");
			ab.setPositiveButton("设置", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent("android.settings.SETTINGS");
					startActivityForResult(intent, 0);
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
			ucSdkInit();
		}
	}

	/**
	 * 必接功能<br>
	 * sdk初始化功能<br>
	 */
	private void ucSdkInit() {

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
								ucSdkInit();
							}
							// 未登录成功
							if (statuscode == UCGameSDKStatusCode.NO_LOGIN) {
								// 调用SDK登录接口
								ucSdkLogin();
							}
							// 退出账号成功
							if (statuscode == UCGameSDKStatusCode.SUCCESS) {
								// 执行销毁悬浮按钮接口
								ucSdkDestoryFloatButton();
								// 调用SDK登录接口
								ucSdkLogin();
							}
							// 退出账号失败
							if (statuscode == UCGameSDKStatusCode.FAIL) {
								// 调用SDK退出当前账号接口
								ucSdkLogout();
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

			UCGameSDK.defaultSDK().initSDK(me, UCLogLevel.DEBUG,
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
								dispalyGameLoginUI();
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

	/**
	 * 模拟游戏老账号认证，并返回sid
	 */
	private String verifyGameUser(String username, String password) {
		String sid = "";
		// 此处需要进行游戏服务端登录校验，并从游戏服务端进行ucid.bind.create接口，进行UC账号绑定，获取对应的ucid和sid
		if (username.equals(password)) {
			sid = "0c3b8357-2b24-4b2f-97d9-c14ff687bd6d113550";
		} else {
			sid = "";
		}
		return sid;
	}

	/**
	 * 必接功能<br>
	 * SDK登录功能<br>
	 * SDK客户端登录成功后，游戏客户端通过getsid()方法获取SDK客户端的sid，发送给游戏服务器，
	 * 游戏服务器使用此sid进行服务端接口调用，即可获取用户标示， 随后游戏服务器向游戏客户端发送用户标示即可。
	 * （注：游戏客户端无法直接从SDK客户端获取用户标示）
	 * 详细流程可见接入文档“02-技术文档-SDK总体机制\UC游戏_SDK_开发参考说明书_总体机制_vXXX.pdf”。
	 */
	private void ucSdkLogin() {

		this.runOnUiThread(new Runnable() {
			public void run() {
				try {
					boolean gameAccountEnable = false; // 游戏老账号是否启用，设置为false即可
					String gameAccountTitle = "松鼠大战"; // 游戏老账号标题。例如：“松鼠大战”，开发商可以自行设置。如已启用老账号登录功能，此参数不能为空。

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
								UCGameSDK.defaultSDK().getSid();

								me.paintGame();
								// 执行悬浮按钮创建方法
								ucSdkCreateFloatButton();
								// 执行悬浮按钮显示方法
								ucSdkShowFloatButton();
							}

							// 登录失败。应该先执行初始化成功后再进行登录调用。
							if (code == UCGameSDKStatusCode.NO_INIT) {
								// 没有初始化就进行登录调用，需要游戏调用SDK初始化方法
								ucSdkInit();
							}

							// 登录退出。该回调会在登录界面退出时执行。
							if (code == UCGameSDKStatusCode.LOGIN_EXIT) {
								// 登录界面关闭，游戏需判断此时是否已登录成功进行相应操作
							}
						}
					};

					// 启用游戏老账号登录
					if (gameAccountEnable) {
						// 游戏老账号登录钩子。游戏必须遵循该接口返回对应数据以实现登录。
						IGameUserLogin gameUserLoginHook = new IGameUserLogin() {
							@Override
							public GameUserLoginResult process(String userName,
									String passWord) {

								// !!! 这里只是模拟游戏老账号登录流程。
								GameUserLoginResult galr = new GameUserLoginResult();

								// 假设用户名与密码一致的时候游戏帐号认证通过。
								String sid = verifyGameUser(userName, passWord);
								if (sid != null && sid != ""
										&& sid.length() > 0) {
									galr.setLoginResult(UCGameSDKStatusCode.SUCCESS);
									galr.setSid(sid);

								} else {
									galr.setLoginResult(UCGameSDKStatusCode.LOGIN_GAME_USER_AUTH_FAIL);
									galr.setSid("");
								}
								return galr;
							}
						};
						UCGameSDK.defaultSDK().login(me, loginCallbackListener,
								gameUserLoginHook, gameAccountTitle);
					}
					// 未启用官方老账号登录
					else {
						UCGameSDK.defaultSDK().login(me, loginCallbackListener);
					}
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
	private void ucSdkExit() {
		UCGameSDK.defaultSDK().exitSDK(me, new UCCallbackListener<String>() {
			@Override
			public void callback(int code, String msg) {
				if (UCGameSDKStatusCode.SDK_EXIT_CONTINUE == code) {
					// 此加入继续游戏的代码

				} else if (UCGameSDKStatusCode.SDK_EXIT == code) {
					// 在此加入退出游戏的代码
					ucSdkDestoryFloatButton();
					System.exit(0);
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
	private void ucSdkLogout() {
		try {
			UCGameSDK.defaultSDK().logout();
		} catch (UCCallbackListenerNullException e) {
			// 未设置退出侦听器
		}
	}

	/**
	 * 必接功能<br>
	 * 悬浮按钮创建及显示<br>
	 * 悬浮按钮必须保证在SDK进行初始化成功之后再进行创建需要在UI线程中调用<br>
	 */
	private void ucSdkCreateFloatButton() {
		GameActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				try {
					// 创建悬浮按钮。该悬浮按钮将悬浮显示在GameActivity页面上，点击时将会展开悬浮菜单，菜单中含有
					// SDK 一些功能的操作入口。
					// 创建完成后，并不自动显示，需要调用showFloatButton(Activity,
					// double, double, boolean)方法进行显示或隐藏。
					UCGameSDK.defaultSDK().createFloatButton(me,
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
	private void ucSdkShowFloatButton() {
		GameActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				// 显示悬浮图标，游戏可在某些场景选择隐藏此图标，避免影响游戏体验
				try {
					UCGameSDK.defaultSDK().showFloatButton(me, 100, 50, true);
				} catch (UCCallbackListenerNullException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 必接功能<br>
	 * 悬浮按钮销毁<br>
	 * 悬浮按钮销毁需要在UI线程中调用<br>
	 */
	private void ucSdkDestoryFloatButton() {
		GameActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				// 悬浮按钮销毁功能
				UCGameSDK.defaultSDK().destoryFloatButton(me);
			}
		});
	}

	/**
	 * 选接功能<br>
	 * 九游社区功能<br>
	 * 如已在程序中调用悬浮按钮，则可不需再调用九游社区功能<br>
	 */
	private void ucSdkEnterUserCenter() {
		try {
			UCGameSDK.defaultSDK().enterUserCenter(getApplicationContext(),
					new UCCallbackListener<String>() {
						@Override
						public void callback(int statuscode, String data) {
							switch (statuscode) {
							case UCGameSDKStatusCode.SUCCESS:
								break;
							// !!! 未初始化成功。应该先调用初始化
							case UCGameSDKStatusCode.NO_INIT:
								ucSdkInit();
								break;
							// !!! 未登录。应该先调用登录功能进行登录
							case UCGameSDKStatusCode.NO_LOGIN:
								ucSdkLogin();
								break;
							default:
								break;
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 定义充值金额，默认为0
	private float amount = 0;

	/**
	 * 必接功能<br>
	 * SDK支付功能<br>
	 * 调用SDK支付功能 <br>
	 * 如你在调用支付页面时，没有显示正确的支付页面 <br>
	 * 请参考自助解决方案：http://bbs.9game.cn/thread-6074095-1-1.html <br>
	 * 在联调环境中进行支付，可使用无效卡进行支付，只需符合卡号卡密长度位数即可<br>
	 * 当卡号卡密全部输入为1时，是支付失败的订单，服务器将会收到订单状态为F的订单信息<br>
	 */
	private void ucSdkPay() {
		PaymentInfo pInfo = new PaymentInfo(); // 创建Payment对象，用于传递充值信息

		// 设置充值自定义参数，此参数不作任何处理，
		// 在充值完成后，sdk服务器通知游戏服务器充值结果时原封不动传给游戏服务器传值，字段为服务端回调的callbackInfo字段
		pInfo.setCustomInfo("callback");

		// 非必选参数，可不设置，此参数已废弃,默认传入0即可。
		// 如无法支付，请在开放平台检查是否已经配置了对应环境的支付回调地址，如无请配置，如有但仍无法支付请联系UC技术接口人。
		pInfo.setServerId(0);

		pInfo.setRoleId("102"); // 设置用户的游戏角色的ID，此为必选参数，请根据实际业务数据传入真实数据
		pInfo.setRoleName("游戏角色名"); // 设置用户的游戏角色名字，此为必选参数，请根据实际业务数据传入真实数据
		pInfo.setGrade("12"); // 设置用户的游戏角色等级，此为可选参数

		// 非必填参数，设置游戏在支付完成后的游戏接收订单结果回调地址，必须为带有http头的URL形式。
		pInfo.setNotifyUrl("http://192.168.1.1/notifypage.do");

		// 当传入一个amount作为金额值进行调用支付功能时，SDK会根据此amount可用的支付方式显示充值渠道
		// 如你传入6元，则不显示充值卡选项，因为市面上暂时没有6元的充值卡，建议使用可以显示充值卡方式的金额
		pInfo.setAmount(amount);// 设置充值金额，此为可选参数

		try {
			UCGameSDK.defaultSDK().pay(getApplicationContext(), pInfo,
					payResultListener);
		} catch (UCCallbackListenerNullException e) {
			// 异常处理
		}

	}

	private UCCallbackListener<OrderInfo> payResultListener = new UCCallbackListener<OrderInfo>() {
		@Override
		public void callback(int statudcode, OrderInfo orderInfo) {
			if (statudcode == UCGameSDKStatusCode.NO_INIT) {
				// 没有初始化就进行登录调用，需要游戏调用SDK初始化方法
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
			}
			if (statudcode == UCGameSDKStatusCode.PAY_USER_EXIT) {
				// 用户退出充值界面。
			}
		}

	};

	/**
	 * 选接功能<br>
	 * 通知SDK用户进入游戏分区功能，将玩家实际进入的游戏分区名称、角色ID、角色名称传递给SDK。
	 */
	private void ucSdkNotifyZone() {
		// !!!以下参数值为模拟值，接入时需获取客户端实际内容进行传递
		UCGameSDK.defaultSDK().notifyZone("龙吟虎啸", "111", "傲红尘");
		Log.e("UCGameSDK", "通知SDK用户进入游戏分区功能调用成功");
	}

	/**
	 * 必接功能<br>
	 * 提交游戏扩展数据功能，游戏SDK要求游戏在运行过程中，提交一些用于运营需要的扩展数据，这些数据通过扩展数据提交方法进行提交。
	 * 登录游戏角色成功后调用此段
	 */
	private void ucSdkSubmitExtendData() {
		try {
			JSONObject jsonExData = new JSONObject();
			jsonExData.put("roleId", "R0010");// 玩家角色ID
			jsonExData.put("roleName", "令狐一冲");// 玩家角色名
			jsonExData.put("roleLevel", "99");// 玩家角色等级
			jsonExData.put("zoneId", 192825);// 游戏区服ID
			jsonExData.put("zoneName", "游戏一区-逍遥谷");// 游戏区服名称
			UCGameSDK.defaultSDK()
					.submitExtendData("loginGameRole", jsonExData);
			Log.e("UCGameSDK", "提交游戏扩展数据功能调用成功");
		} catch (Exception e) {
			// 处理异常
		}
	}

	/**
	 * 调用demo游戏登录页面
	 */
	private void dispalyGameLoginUI() {
		this.setContentView(R.layout.login);
		// 执行SDK登录功能
		ucSdkLogin();
		Button gameLoginBtn = (Button) this.findViewById(R.id.gameLoginBtn);
		gameLoginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 调用SDK登录功能
				ucSdkLogin();
			}
		});
	}

	/**
	 * 启动游戏主界面
	 */
	private void paintGame() {
		this.setContentView(R.layout.game);
		gameView = new LAGameView(this);
		gameView.setScreen(new JigsawScreen(gameView, "jinian.jpg", "over.png",
				4, 7));
		gameView.setShowFPS(true);
		LayoutParams gameViewLLP = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		LinearLayout llGameContainer = (LinearLayout) this
				.findViewById(R.id.layoutGameContainer);
		llGameContainer.addView(gameView, gameViewLLP);
		gameView.startPaint();

		// 调用通知SDK用户进入游戏分区功能
		ucSdkNotifyZone();

		// 调用提交游戏扩展数据功能
		ucSdkSubmitExtendData();

		// 进入普通支付
		Button btnEnterPay = (Button) this.findViewById(R.id.btnEnterPay);
		btnEnterPay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 调用SDK支付功能
				ucSdkPay();
			}
		});

		// 进入定额支付
		Button btnwaEnterPay = (Button) this.findViewById(R.id.btnwaEnterPay);
		btnwaEnterPay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onPause();

				// 跳转至游戏定额支付页面
				setContentView(R.layout.paywithamount);

				final RadioButton rbAmount6 = (RadioButton) findViewById(R.id.radio6);// 6元单选按钮
				final RadioButton rbAmount10 = (RadioButton) findViewById(R.id.radio10);// 10元单选按钮
				final RadioButton rbAmount20 = (RadioButton) findViewById(R.id.radio20);// 20元单选按钮
				final RadioButton rbAmount30 = (RadioButton) findViewById(R.id.radio30);// 30元单选按钮
				final RadioButton rbAmount50 = (RadioButton) findViewById(R.id.radio50);// 50元单选按钮
				final RadioButton rbAmount100 = (RadioButton) findViewById(R.id.radio100);// 100元单选按钮

				Button payWA = (Button) findViewById(R.id.pwa);
				payWA.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						if (rbAmount6.isChecked()) {
							amount = 6;
						} else if (rbAmount10.isChecked()) {
							amount = 10;
						} else if (rbAmount20.isChecked()) {
							amount = 20;
						} else if (rbAmount30.isChecked()) {
							amount = 30;
						} else if (rbAmount50.isChecked()) {
							amount = 50;
						} else if (rbAmount100.isChecked()) {
							amount = 100;
						}
						// 调用SDK支付方法
						ucSdkPay();
						amount = 0;
					}

				});

				// 返回游戏主界面
				Button back = (Button) findViewById(R.id.back);
				back.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						paintGame();
					}

				});
			}
		});

		// 进入个人中心
		Button btnEnterUserCenter = (Button) this
				.findViewById(R.id.btnEnterUserCenter);
		btnEnterUserCenter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 调用SDK个人中心功能
				ucSdkEnterUserCenter();
			}
		});

		// 退出当前账号
		Button btnLogout = (Button) this.findViewById(R.id.btnLogout);
		btnLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 调用SDK退出当前账号功能
				ucSdkLogout();
			}
		});

		// 退出SDK
		Button btnExitSDK = (Button) this.findViewById(R.id.btnExitSDK);
		btnExitSDK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 调用返回键方法
				onBackPressed();
			}
		});

		// 关于demo
		Button btnAbout = (Button) this.findViewById(R.id.btnAbout);
		btnAbout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转至关于demo页面
				setContentView(R.layout.aboutdemo);
				// 返回游戏主界面
				Button backgame = (Button) findViewById(R.id.backgame);
				backgame.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						paintGame();
					}

				});
			}
		});

	}

	/*
	 * 返回键方法
	 */
	public void onBackPressed() {
		ucSdkExit();
	}

	@Override
	protected void onStart() {
		GLog.d("GameActivity", "----------onStart---------");
		if (gameView != null) {
			gameView.setRunning(true);
		}
		super.onStart();
	}

	@Override
	protected void onRestart() {
		GLog.d("GameActivity", "----------onRestart---------");
		if (gameView != null) {
			gameView.setRunning(true);
		}
		super.onRestart();
	}

	@Override
	protected void onResume() {
		GLog.d("GameActivity", "----------onResume---------");
		if (gameView != null) {
			gameView.setRunning(true);
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		GLog.d("GameActivity", "----------onPause---------");
		if (gameView != null) {
			gameView.setRunning(false);
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		GLog.d("GameActivity", "----------onStop---------");
		if (gameView != null) {
			gameView.setRunning(false);
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		GLog.d("GameActivity", "----------onDestroy---------");
		try {
			if (gameView != null) {
				gameView.setRunning(false);
				Thread.sleep(16);
			}
			ucSdkDestoryFloatButton();
			super.onDestroy();
		} catch (Exception e) {
		}
	}

}