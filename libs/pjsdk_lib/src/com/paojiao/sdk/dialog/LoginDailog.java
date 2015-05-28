/**
 * 
 */
package com.paojiao.sdk.dialog;

import java.util.ArrayList;

import org.apache.http.Header;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.paojiao.sdk.CallbackListener;
import com.paojiao.sdk.PJApi;
import com.paojiao.sdk.PJError;
import com.paojiao.sdk.PJUser;
import com.paojiao.sdk.R;
import com.paojiao.sdk.activity.WebActivity;
import com.paojiao.sdk.adapter.AccountAdapter;
import com.paojiao.sdk.api.AccountApi;
import com.paojiao.sdk.api.LoginApi;
import com.paojiao.sdk.api.base.BaseApi;
import com.paojiao.sdk.bean.AccounBase;
import com.paojiao.sdk.bean.AccountData;
import com.paojiao.sdk.bean.common.Base;
import com.paojiao.sdk.config.ConfigurationInfo;
import com.paojiao.sdk.config.Route;
import com.paojiao.sdk.config.URL;
import com.paojiao.sdk.dialog.base.BaseDialog;
import com.paojiao.sdk.net.AsyncHttpResponseHandler;
import com.paojiao.sdk.net.RequestParams;
import com.paojiao.sdk.res.StringGlobal;
import com.paojiao.sdk.utils.ColorState;
import com.paojiao.sdk.utils.JSONLoginInfo;
import com.paojiao.sdk.utils.MyToast;
import com.paojiao.sdk.utils.ResourceUtil;

/**
 * @author 仁秋
 * 
 */
public class LoginDailog extends BaseDialog implements
		android.view.View.OnClickListener, OnCheckedChangeListener {

	private static final String FEAK_PASSWORD = "pj_feakpassword";
	private String mUserName;
	private String mPassword;

	private EditText mUserNameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private ListView accountListView;
	private AccountAdapter adapter;
	private ArrayList<AccountData> accounts;
	// private ArrayList<AccountBean.AccountData> accounts;

	private LoginApi loginApi;
	private BaseApi simpleApi;
	private AccountApi accountApi;

	/**
	 * 老用户打开游戏调用此构造器。
	 * 
	 * @param activity
	 * @param cancelListener
	 */
	public LoginDailog(Activity activity, CallbackListener cancelListener) {
		super(activity, cancelListener);
		initApi(cancelListener);
	}

	private void initApi(final CallbackListener cancelListener) {
		loginApi = new LoginApi();
		simpleApi = new BaseApi();
		accountApi = new AccountApi();
		accounts = new ArrayList<AccountData>();
		setContentView(ResourceUtil
				.getLayoutId(activity, "pj_layout_act_login"));
		if(PJApi.SCREEN_WIDTH<=620){
			setContentView(ResourceUtil
					.getLayoutId(activity, "pj_layout_act_login_small"));
		}
		setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				try {
					loginApi.getHttpClient().getConnectionManager().shutdown();
				} catch (Exception ex) {
				}
				try {
					simpleApi.getHttpClient().getConnectionManager().shutdown();
				} catch (Exception ex) {
				}
				PJError error = new PJError(PJError.CODE_CANCEL, "取消登陆");
				cancelListener.onLoginError(error);
			}
		});
	}

	/**
	 * 新用户注册完自动登录调用此构造器。
	 * 
	 * @param activity
	 * @param cancelListener
	 * @param userName
	 * @param password
	 */
	public LoginDailog(Activity activity,
			final CallbackListener cancelListener, String userName,
			String password) {
		super(activity, cancelListener);
		mUserName = userName;
		mPassword = password;
		initApi(cancelListener);
		// accountApi = new AccountApi();

		if (mUserName != null && mPassword != null) {
			mUserNameView.setText(mUserName);
			mPasswordView.setText(mPassword);
			if (attemptLogin()) {
				doLogin();
			}
		}
	}

	@Override
	protected void initData() {
		// 如果不需要加载用户列表的话，则返回
		if (accountApi == null) {
			return;
		}
		accountApi.pjPost(getContext(), new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				mLoginStatusMessageView
						.setText(StringGlobal.GET_ACCOUNT_PROGRESS);
				showProgress(true);
			}

			@Override
			public void onFinish() {
				super.onFinish();
				showProgress(false);
			}

			@Override
			protected void handleSuccessMessage(int statusCode,
					Header[] headers, String responseBody) {
				super.handleSuccessMessage(statusCode, headers, responseBody);
				AccounBase base = PJError.checkForErrorA(getContext(),
						responseBody);
				if (base != null) {
					if (!base.getCode().equals(Base.STATUS_OK)) {
						Toast.makeText(getContext(), base.getMsg(),
								Toast.LENGTH_SHORT).show();
						return;
					}
					if (base.getaData() == null) {
						Toast.makeText(getContext(),
								StringGlobal.GET_INFO_FAILED,
								Toast.LENGTH_SHORT).show();
						return;
					}
					accounts.clear();
					accounts.addAll(base.getaData());
					setListAdapter();
					mUserNameView.setError(null);
					mPasswordView.setError(null);
					if (base.getaData().size() > 0) {
						findViewById(
								ResourceUtil.getId(activity,
										"pj_login_quick_reg_button"))
								.setVisibility(View.GONE);
						findViewById(
								ResourceUtil.getId(activity, "pj_padding_view"))
								.setVisibility(View.GONE);
						Button btLogin = (Button) findViewById(ResourceUtil
								.getId(activity, "pj_login_do_button"));
						btLogin.setTextColor(ColorState.SetTextColor(
								0xFFFFFFFF, 0xFFE40019));
						btLogin.setBackgroundResource(ResourceUtil
								.getDrawableId(activity, "pj_button_bg_red"));
						btLogin.setText(StringGlobal.LOGIN);

						final String username = base.getaData().get(0)
								.getUserName();
						mUserNameView.setText(username);
						String token = base.getaData().get(0).getToken();
						if (token != null && !TextUtils.isEmpty(token)) {
							// 采用云端token
							ConfigurationInfo.addInfo(getContext(), username,
									token);
						}
						if (ConfigurationInfo.contains(getContext(), username,
								ConfigurationInfo.ACCOUNT)
								&& !ConfigurationInfo.getToken(getContext(),
										username).equals("unknown")) {
							mPasswordView.setText(FEAK_PASSWORD);
						} else {
							mPasswordView.setText("");
						}

						// 自动登录控制
						if (base.getaData().get(0).getAutoLogin()) {
							if (attemptLogin()) {
								doLogin();
							}
						}
					}
					if (base.getaData().size() > 1) {
						findViewById(
								ResourceUtil.getId(activity,
										"pj_login_choice_button"))
								.setVisibility(View.VISIBLE);
					} else {
						findViewById(
								ResourceUtil.getId(activity,
										"pj_login_choice_button"))
								.setVisibility(View.GONE);
					}
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			protected void handleFailureMessage(Throwable e, String responseBody) {
				super.handleFailureMessage(e, responseBody);
			}

		});
	}

	private void setListAdapter() {
		adapter = new AccountAdapter(getContext(), accounts);
		accountListView.setAdapter(adapter);
		accountListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int position, long arg3) {
						final String username = adapter.getItem(position)
								.getUserName();
						mUserNameView.setText(username);
						String token = adapter.getItem(position).getToken();
						if (token != null && !TextUtils.isEmpty(token)) {
							// 采用云端token
							ConfigurationInfo.addInfo(getContext(), username,
									token);
						}
						if (ConfigurationInfo.contains(getContext(), username,
								ConfigurationInfo.ACCOUNT)
								&& !ConfigurationInfo.getToken(getContext(),
										username).equals("unknown")) {
							mPasswordView.setText(FEAK_PASSWORD);
						} else {
							mPasswordView.setText("");
						}
						((ToggleButton) findViewById(ResourceUtil.getId(
								activity, "pj_login_choice_button")))
								.setChecked(false);
					}
				});
	}

	@Override
	protected void setListener() {
		mPasswordView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == ResourceUtil.getId(activity, "pj_strings_ime_login")
						|| id == EditorInfo.IME_NULL) {
					if (attemptLogin()) {
						doLogin();
					}
					return true;
				}
				return false;
			}
		});
		Button btLogin = (Button) findViewById(ResourceUtil.getId(activity,
				"pj_login_do_button"));

		btLogin.setTextColor(ColorState.SetTextColor(0xFFFFFFFF, 0xFFAAAAAA));
		btLogin.setText(StringGlobal.LOGIN);
		findViewById(ResourceUtil.getId(activity, "pj_login_do_button"))
				.setOnClickListener(this);
		Button btLoginQuick = (Button) findViewById(ResourceUtil.getId(
				activity, "pj_login_quick_reg_button"));
		btLoginQuick.setTextColor(ColorState.SetTextColor(0XFFFFFFFF,
				0XFFE40019));
		btLoginQuick.setText(StringGlobal.QUICK_LOGIN);
		findViewById(ResourceUtil.getId(activity, "pj_login_quick_reg_button"))
				.setOnClickListener(this);
		Button btLoginReg = (Button) findViewById(ResourceUtil.getId(activity,
				"pj_login_to_reg_button"));
		btLoginReg.setText(StringGlobal.REGISTER);
		btLoginReg.setOnClickListener(this);
		((ToggleButton) findViewById(ResourceUtil.getId(activity,
				"pj_login_choice_button"))).setOnCheckedChangeListener(this);
		TextView tvForget = (TextView) findViewById(ResourceUtil.getId(
				activity, "pj_login_forget_pwd_textView"));
		tvForget.setTextColor(ColorState.SetTextColor(0XFFFF0000, 0XFFAAAAAA));
		tvForget.setText(StringGlobal.FORGOT_PWD);
		findViewById(
				ResourceUtil.getId(activity, "pj_login_forget_pwd_textView"))
				.setOnClickListener(this);
	}

	@Override
	protected void findView() {
		mUserNameView = (EditText) findViewById(ResourceUtil.getId(activity,
				"pj_login_username_textview"));
//		setDrawableLeft(mUserNameView, activity, "pj_image_icon_username");
		mUserNameView.setHint(StringGlobal.USERNAME_HINT);
		mPasswordView = (EditText) findViewById(ResourceUtil.getId(activity,
				"pj_login_password_editText"));
//		setDrawableLeft(mPasswordView, activity, "pj_image_icon_password");
		mPasswordView.setHint(StringGlobal.PWD_HINT);
		mPasswordView.setImeActionLabel(StringGlobal.LOGIN,
				ResourceUtil.getId(activity, "pj_strings_ime_login"));
		mLoginFormView = findViewById(ResourceUtil.getId(activity,
				"pj_login_form"));
		mLoginStatusView = findViewById(ResourceUtil.getId(activity,
				"pj_login_status"));
		mLoginStatusMessageView = (TextView) findViewById(ResourceUtil.getId(
				activity, "pj_login_status_message"));
		accountListView = (ListView) findViewById(ResourceUtil.getId(activity,
				"pj_login_account_listView"));
	}

	private void setDrawableLeft(EditText editText, Context mContext,
			String drawableId) {
		Drawable drawable = activity.getResources().getDrawable(
				ResourceUtil.getDrawableId(mContext, drawableId));
		// / 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth() - 30,
				drawable.getMinimumHeight());
		editText.setCompoundDrawables(drawable, null, null, null);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == ResourceUtil.getId(activity, "pj_login_do_button")) {
			if (attemptLogin()) {
				doLogin();
			}
		} else if (v.getId() == ResourceUtil.getId(activity,
				"pj_login_quick_reg_button")) {
			quickLogin();
		} else if (v.getId() == ResourceUtil.getId(activity,
				"pj_login_to_reg_button")) {
			mUserNameView.clearFocus();
			mUserNameView.setError(null);
			RegisterDailog dialog = new RegisterDailog(activity,
					callbackListener, mUserNameView.getText().toString());
			dialog.show();
			this.dismiss();
		} else if (v.getId() == ResourceUtil.getId(activity,
				"pj_login_forget_pwd_textView")) {
			final String url = URL.RESET_PWD;
			Bundle params = Route.creatUrlParams(getContext());
			PJApi.startActivity(WebActivity.class, url, params);
			// this.dismiss();
		}
	}

	public boolean attemptLogin() {

		mUserNameView.setError(null);
		mPasswordView.setError(null);

		mUserName = mUserNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;

		View focusView = null;
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(StringGlobal.CAN_NOT_NULL);
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 6) {
			mPasswordView.setError(StringGlobal.ILLEGAL_SHORT_PWD);
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() > 30) {
			mPasswordView.setError(StringGlobal.ILLEGAL_LONG_PWD);
			focusView = mPasswordView;
			cancel = true;
		}

		if (TextUtils.isEmpty(mUserName)) {
			mUserNameView.setError(StringGlobal.CAN_NOT_NULL);
			focusView = mUserNameView;
			cancel = true;
		}

		if (cancel) {
			focusView.setFocusable(true);
			focusView.setFocusableInTouchMode(true);
			focusView.requestFocus();
			return false;
		}
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getContext().getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.INVISIBLE
									: View.VISIBLE);
						}
					});
		} else {
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	protected void quickLogin() {
		mUserNameView.setError(null);
		mPasswordView.setError(null);
		mUserName = mUserNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		simpleApi.pjPost(getContext(), URL.QUICK_REG, new RequestParams(),
				new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();
						mLoginStatusMessageView
								.setText(StringGlobal.REGISTER_PROGRESS);
						showProgress(true);
					}

					@Override
					public void onFinish() {
						super.onFinish();
						showProgress(false);
					}

					@Override
					protected void handleSuccessMessage(int statusCode,
							Header[] headers, String responseBody) {
						super.handleSuccessMessage(statusCode, headers,
								responseBody);
						Base base = JSONLoginInfo.checkForError(getContext(),
								responseBody);

						if (base != null) {
							if (!base.getCode().equals(Base.STATUS_OK)) {
								Toast.makeText(getContext(), base.getMsg(),
										Toast.LENGTH_SHORT).show();
								return;
							}
							Toast.makeText(getContext(),
									StringGlobal.REGISTER_SUCCESS,
									Toast.LENGTH_LONG).show();
							if (base.getData() == null) {
								Toast.makeText(getContext(),
										StringGlobal.GET_INFO_FAILED,
										Toast.LENGTH_SHORT).show();
								return;
							}
							mUserNameView.setError(null);
							mPasswordView.setError(null);
							mUserNameView.setText(base.getData().getUserName());
							mPasswordView.setText(FEAK_PASSWORD);

							ConfigurationInfo.addInfo(getContext(), base
									.getData().getUserName(), base.getData()
									.getToken());

							Bundle bundle = new Bundle();
							bundle.putString(PJUser.TOKEN, base.getData()
									.getToken());
							bundle.putString(PJUser.USERNAME, base.getData()
									.getUserName());
							bundle.putString(PJUser.NICENAME, base.getData()
									.getNiceName());
							bundle.putString(PJUser.CREATEDTIME, base.getData()
									.getCreatedTime());
							bundle.putString(PJUser.ACTIVETIME, base.getData()
									.getActiveTime());
							bundle.putString(PJUser.UID, base.getData()
									.getUid());
							PJApi.USER_ID = base.getData().getUid();
							ConfigurationInfo.setUid(getContext(), base
									.getData().getUid());
							ConfigurationInfo.setNickName(getContext(), base
									.getData().getNiceName());
							// 保存一些状态消息，导航地址和未读消息数

							MyToast.show(getContext(), base.getData()
									.getNiceName() + " 欢迎回来");
							LoginDailog.this.dismiss();
							if (base.getData().getMobile().length() == 0) {
								PJApi.IS_BUNDEL_TEL = false;
							}
							callbackListener.onLoginSuccess(bundle);
						}
					}

					@Override
					protected void handleFailureMessage(Throwable e,
							String responseBody) {
						super.handleFailureMessage(e, responseBody);
					}

				});
	}

	public void doLogin() {
		AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				super.onStart();
				mLoginStatusMessageView.setText(StringGlobal.LOGIN_PROGRESS);
				showProgress(true);
			}

			@Override
			public void onFinish() {
				super.onFinish();
				showProgress(false);
			}

			@Override
			protected void handleFailureMessage(Throwable e, String responseBody) {
				super.handleFailureMessage(e, responseBody);
				Toast.makeText(getContext(),
						StringGlobal.ERROR_NETWORK_DISSABLE, Toast.LENGTH_LONG)
						.show();
			}

			@Override
			protected void handleSuccessMessage(int statusCode,
					Header[] headers, String responseBody) {
				super.handleSuccessMessage(statusCode, headers, responseBody);
				Base base = JSONLoginInfo.checkForError(getContext(),
						responseBody);

				if (base != null) {
					if (base.getCode().equals(PJError.CODE_WRONG_PWD)) {
						mPasswordView.setFocusable(true);
						mPasswordView.setFocusableInTouchMode(true);
						mPasswordView.requestFocus();
						mPasswordView.setError(StringGlobal.INCORRECT_PWD);
					} else if (base.getCode().equals(PJError.CODE_WRONG_USER)) {
						mUserNameView.setFocusable(true);
						mUserNameView.setFocusableInTouchMode(true);
						mUserNameView.requestFocus();
						mUserNameView.setError(StringGlobal.USER_NOT_EXIST);
					} else if (base.getCode().equals(PJError.CODE_TIMEOUT)) {
						mUserNameView.setText(mUserName);
						mPasswordView.setText("");

						// 清除该账户的token和当前的token
						ConfigurationInfo.deleteInfo(getContext(), mUserName);

						adapter.notifyDataSetChanged();
						Toast.makeText(getContext(),
								StringGlobal.LOGIN_TIMEOUT, Toast.LENGTH_SHORT)
								.show();
					} else {
						if (base.getData() == null) {
							Toast.makeText(getContext(),
									StringGlobal.GET_INFO_FAILED,
									Toast.LENGTH_SHORT).show();
							return;
						}

						ConfigurationInfo.addInfo(getContext(), base.getData()
								.getUserName(), base.getData().getToken());

						Bundle bundle = new Bundle();
						bundle.putString(PJUser.TOKEN, base.getData()
								.getToken());
						bundle.putString(PJUser.USERNAME, base.getData()
								.getUserName());
						bundle.putString(PJUser.NICENAME, base.getData()
								.getNiceName());
						bundle.putString(PJUser.CREATEDTIME, base.getData()
								.getCreatedTime());
						bundle.putString(PJUser.ACTIVETIME, base.getData()
								.getActiveTime());
						bundle.putString(PJUser.UID, base.getData().getUid());
						PJApi.USER_ID = base.getData().getUid();
						ConfigurationInfo.setUid(getContext(), base.getData()
								.getUid());
						ConfigurationInfo.setNickName(getContext(), base
								.getData().getNiceName());
						PJApi.isFirstInit = true;
						// 保存一些状态消息，导航地址和未读消息数
						MyToast.show(getContext(), base.getData().getNiceName()
								+ " 欢迎回来");
						LoginDailog.this.dismiss();
						if (base.getData().getMobile().length() == 0) {
							PJApi.IS_BUNDEL_TEL = false;
						} else
							PJApi.IS_BUNDEL_TEL = true;
						callbackListener.onLoginSuccess(bundle);
					}
				}
			}

		};
		if (!ConfigurationInfo.getToken(activity, mUserName).equals("unknonw")
				&& mPassword.equals(FEAK_PASSWORD)) {
			loginApi.pjPost(getContext(),
					ConfigurationInfo.getToken(activity, mUserName), handler);
		} else {
			loginApi.pjPost(getContext(), mUserName, mPassword, handler);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == ResourceUtil.getId(activity,
				"pj_login_choice_button")) {
			accountListView.setVisibility(accountListView.isShown() ? View.GONE
					: View.VISIBLE);
		}
	}
}
