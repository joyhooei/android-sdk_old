package com.paojiao.sdk.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paojiao.sdk.CallbackListener;
import com.paojiao.sdk.PJApi;
import com.paojiao.sdk.activity.base.BaseActivity;
import com.paojiao.sdk.config.ConfigurationInfo;
import com.paojiao.sdk.config.Route;
import com.paojiao.sdk.config.URL;
import com.paojiao.sdk.dialog.SimpleDialog;
import com.paojiao.sdk.dialog.UCDialog;
import com.paojiao.sdk.res.StringGlobal;
import com.paojiao.sdk.utils.MyWebViewDownLoadListener;
import com.paojiao.sdk.utils.Prints;
import com.paojiao.sdk.utils.ResourceUtil;
import com.paojiao.sdk.utils.Utils;
import com.paojiao.sdk.widget.MyFloatView;

/**
 * 
 * @author paojiao
 * 
 */
public class WebActivity extends BaseActivity implements OnClickListener {
	private ProgressBar progressBar, statusBar;
	private static final String NAME_SPASE = "pjsdk";

	private String url;
	private Bundle params;
	private WebView webView;
	private Button backButtn, back2GameButton;
	private TextView titleTextView;
	private boolean isPaid;
	private LocalBroadcastManager lbm;

	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(PJApi.isLogin())MyFloatView.newInstance().hide();
		this.url = getIntent().getStringExtra(Route.URL);
		this.params = getIntent().getBundleExtra(Route.PARAMS);
		this.isPaid = false;
		lbm = LocalBroadcastManager.getInstance(this);
		setContentView(ResourceUtil.getLayoutId(this, "pj_layout_act_web"));
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void startLoad() {
		StringBuffer query = new StringBuffer();
		if (params != null) {
			if (url.contains("?")) {
				query.append("&");
			} else {
				query.append("?");
			}
			for (String key : params.keySet()) {
				query.append(key);
				query.append("=");
				query.append(params.getString(key));
				query.append("&");
			}
		}
		query.deleteCharAt(query.length() - 1);

		String struel = url + query.toString();

		String md5 = Utils.signForUrl(struel);

		String lUrl = struel + "&sign=" + md5;

		loadurl(webView, lUrl);
	}

	@Override
	protected void initData() {
		if (url != null) {
			if (params == null) {
				params = Route.creatUrlParams(this);
			}
			myInit();
		}

	}

	private void changeProgress(int progress) {
		if (progressBar != null) {
			progressBar.setProgress(progress);
			if (progress == 100) {
				progressBar.setProgress(0);
			}
		}
		// if (statusBar != null) {
		// if (progress == 100) {
		// statusBar.setVisibility(View.GONE);
		// }
		// }
	}

	private void showProgress() {
		if (progressBar != null) {
			progressBar.setProgress(0);
		}
		if (statusBar != null) {
			statusBar.setVisibility(View.GONE);
		}
	}

	public void loadurl(final WebView view, final String url) {
		Prints.d("WebAct-url:", url);
		view.loadUrl(url);
	}

	@Override
	protected void setListener() {
		backButtn.setOnClickListener(this);
		back2GameButton.setOnClickListener(this);
	}

	@Override
	protected void findView() {
		webView = (WebView) findViewById(ResourceUtil.getId(this,
				"pj_web_main_webView"));
		titleTextView = (TextView) findViewById(ResourceUtil.getId(this,
				"pj_web_title_textView"));
		backButtn = (Button) findViewById(ResourceUtil.getId(this,
				"pj_web_back_button"));
		back2GameButton = (Button) findViewById(ResourceUtil.getId(this,
				"pj_web_to_game_button"));
		back2GameButton.setText(StringGlobal.BACK_TO_GAME);
		progressBar = (ProgressBar) findViewById(ResourceUtil.getId(this,
				"pj_web_load_progressBar"));
		statusBar = (ProgressBar) findViewById(ResourceUtil.getId(this,
				"pj_web_load_status_progressBar"));
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backNavi();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void myFinish() {
		/**
		 * 发送广播告知重新获取对话框个人中心界面的一些信息
		 */
		if (PJApi.isLogin()) {
			Prints.d("logtag", "isreceiver-send");
			Intent intent = new Intent(CallbackListener.ACTION_BACK_FROM_WEB);
			lbm.sendBroadcast(intent);
		}
		if(PJApi.isLogin())MyFloatView.newInstance().show();
		this.finish();
	}

	public void onClick(View view) {
		if (view.getId() == ResourceUtil.getId(this, "pj_web_back_button")) {
			backNavi();
		}
		if (view.getId() == ResourceUtil.getId(this, "pj_web_to_game_button")) {
			checkPayMemt();
			myFinish();
		}
	}

	// 检查是不是充值页面，这里需要加一个充值取消的回调
	private void checkPayMemt() {
		if (!isPaid && this.url.contains(URL.RECHARGE)) {
			Intent intent = new Intent(CallbackListener.ACTION_PAY_CACEL);
			intent.putExtra("info", this.params);
			lbm.sendBroadcast(intent);
		}
	}

	/**
	 * 顶部栏返回按钮点击事件
	 * 
	 * @author zhounan
	 * @param
	 */
	private void backNavi() {
		if (webView != null) {
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				checkPayMemt();
				myFinish();
			}
		} else {
			checkPayMemt();
			myFinish();
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void myInit() {
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new JavascriptInterface(this),
				NAME_SPASE);

		
		
		webView.setWebViewClient(new WebViewClient() {

			public boolean shouldOverrideUrlLoading(final WebView view,
					final String url) {
				if (url.startsWith("tel:")) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					startActivity(intent);
					return true;
				}
				loadurl(view, url);
				return true;
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				// if (statusBar != null) {
				// statusBar.setVisibility(View.VISIBLE);
				// }
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				showProgress();
				// CookieManager cookieManager = CookieManager.getInstance();
				// String CookieStr = cookieManager.getCookie(url);

			}
		});
		webView.setWebChromeClient(new WebChromeClient() {

			public void onProgressChanged(WebView view, int progress) {
				super.onProgressChanged(view, progress);
				changeProgress(progress);
			}

			// 关键代码，以下函数是没有API文档的，所以在Eclipse中会报错，如果添加了@Override关键字在这里的话。
			// For Android 3.0+
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {

				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				WebActivity.this.startActivityForResult(
						Intent.createChooser(i, "File Chooser"),
						FILECHOOSER_RESULTCODE);

			}

			// For Android 3.0+
			public void openFileChooser(ValueCallback uploadMsg,
					String acceptType) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				WebActivity.this.startActivityForResult(
						Intent.createChooser(i, "File Browser"),
						FILECHOOSER_RESULTCODE);
			}

			// For Android 4.1
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType, String capture) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				WebActivity.this.startActivityForResult(
						Intent.createChooser(i, "File Chooser"),
						WebActivity.FILECHOOSER_RESULTCODE);

			}

		});
		
		webView.setDownloadListener(new MyWebViewDownLoadListener(this));
		
		// this.tabChangeListener = ((WebActivity) getActivity());
		startLoad();

	}

	// js通信接口
	public class JavascriptInterface {

		private Activity context;

		public JavascriptInterface(Activity context) {
			this.context = context;
		}

		@android.webkit.JavascriptInterface
		public void back2Game() {
			if (context == null) {
				return;
			}
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					myFinish();
				}
			});

		}

		/**
		 * 显示标题
		 * 
		 * @param visible
		 *            是否显示
		 * @param title
		 *            标题内容
		 */
		@android.webkit.JavascriptInterface
		public void setTitle(final boolean visible, final String title) {
			if (context == null) {
				return;
			}
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					titleTextView.setVisibility(visible ? View.VISIBLE
							: View.GONE);
					titleTextView.setText(title);
				}
			});
		}

		/**
		 * 是否显示返回上一页的按钮
		 * 
		 * @param visible
		 */
		@android.webkit.JavascriptInterface
		public void showBackNavi(final boolean visible) {
			if (context == null) {
				return;
			}
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					backButtn.setVisibility(visible ? View.VISIBLE : View.GONE);
				}
			});

		}

		/**
		 * 是否显示回到游戏的按钮
		 * 
		 * @param visible
		 */
		@android.webkit.JavascriptInterface
		public void showBack2Game(final boolean visible) {
			if (context == null) {
				return;
			}
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					back2GameButton.setVisibility(visible ? View.VISIBLE
							: View.GONE);
				}
			});

		}

		/**
		 * 注销登录
		 */
		@android.webkit.JavascriptInterface
		public void logout(final String uid, final String username) {
			if (context == null) {
				return;
			}
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ConfigurationInfo.deleteInfo(context, username);
					Intent intent = new Intent(CallbackListener.ACTION_LOGOUT);
					intent.putExtra("uid", uid);
					intent.putExtra("username", username);
					lbm.sendBroadcast(intent);
					context.finish();
				}
			});
		}

		/**
		 * 支付成功
		 * 
		 * @param orderNo
		 */
		@android.webkit.JavascriptInterface
		public void onPaymentSuccess(final String orderNo) {
			if (context == null) {
				return;
			}
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// 标记已支付
					isPaid = true;
					Intent intent = new Intent(
							CallbackListener.ACTION_PAY_SUCCESS);
					intent.putExtra("info", orderNo);
					Prints.d("PAY_SUCCESS", "PAY_SUCCESS--SEND");
					lbm.sendBroadcast(intent);
				}
			});
		};

		/**
		 * 支付失败
		 * 
		 * @param error
		 * @param orderNo
		 */
		@android.webkit.JavascriptInterface
		public void onPaymentError(final int code, final String message,
				final String orderNo) {
			if (context == null) {
				return;
			}
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(
							CallbackListener.ACTION_PAY_ERROR);
					intent.putExtra("info", orderNo);
					intent.putExtra("code", code);
					intent.putExtra("message", message);
					lbm.sendBroadcast(intent);
				}
			});
		};

		@android.webkit.JavascriptInterface
		public void showDialog(final String message) {
			if (context == null) {
				return;
			}
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final SimpleDialog simpleDialog = new SimpleDialog(context,
							null);
					simpleDialog.setMessage(message);
					simpleDialog.setPositiveButton("知道了",
							new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// 回调web页面的方法。
									try {
										// javascript:pjskd_DialogConfirm()
										webView.loadUrl("javascript:pjskd_DialogConfirm()");
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									simpleDialog.dismiss();
								}
							});
					simpleDialog.show();
				}
			});

		}

		/**
		 * 显示弹窗
		 * 
		 * @param title
		 *            弹窗的标题栏标题
		 * @param message
		 *            弹窗的提示信息，比如礼包号，或者其他提示
		 * @param listener
		 *            弹窗按钮回调监听
		 * @param extraContent
		 *            弹窗温馨提示
		 * @param buttonText
		 *            弹窗按钮文字
		 */
		@android.webkit.JavascriptInterface
		public void showDialog(final String title, final String message,
				final String listener, final String extraContent,
				final String buttonText) {
			if (context == null) {
				return;
			}
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					final SimpleDialog simpleDialog = new SimpleDialog(context,
							null);
					simpleDialog.setTitle(title);
					simpleDialog.setTips(extraContent);
					simpleDialog.setMessage(message);
					simpleDialog.setNegativeButton(buttonText,
							new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									if (buttonText.contains("复制")) {
										ClipboardManager cm = (ClipboardManager) context
												.getSystemService(Context.CLIPBOARD_SERVICE);
										// 将文本数据复制到剪贴板
										String code = message.replace("激活码：",
												"");
										cm.setText(code);
									}
									// 回调web页面的方法。
									try {
										// javascript:pjskd_DialogConfirm()
										if (listener != null) {
											webView.loadUrl("javascript:"
													+ listener);
										}
									} catch (Exception ex) {
										ex.printStackTrace();
									}
									simpleDialog.dismiss();
								}
							});
					simpleDialog.show();
				}
			});
		}

		/**
		 * 网页中用户的登录状态发生了改变，调用些方法
		 * 
		 * @param status
		 */
		@android.webkit.JavascriptInterface
		public void onLoginStatuChange(final boolean status) {
			if (context == null) {
				return;
			}
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					UCDialog.isAutoLogin = status;
					// 标记已支付
				}
			});
		};

		/**
		 * 用于调用掌上支付功能
		 * 
		 * @param params
		 */
		@android.webkit.JavascriptInterface
		public void palmPay(final String orderSubjectStr,
				final String orderNumberStr, final String orderAmountStr) {
			if (context == null) {
				return;
			}
			context.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// 标记已支付
					// PJApi.openPalmPay(orderSubjectStr, orderNumberStr,
					// orderAmountStr);
				}
			});
		};
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != RESULT_OK ? null
					: intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
	}

}
