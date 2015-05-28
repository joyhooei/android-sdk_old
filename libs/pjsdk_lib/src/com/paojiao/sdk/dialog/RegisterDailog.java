/**
 * 
 */
package com.paojiao.sdk.dialog;

import org.apache.http.Header;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.paojiao.sdk.CallbackListener;
import com.paojiao.sdk.PJApi;
import com.paojiao.sdk.PJError;
import com.paojiao.sdk.api.RegisterApi;
import com.paojiao.sdk.bean.common.Base;
import com.paojiao.sdk.dialog.base.BaseDialog;
import com.paojiao.sdk.net.AsyncHttpResponseHandler;
import com.paojiao.sdk.res.StringGlobal;
import com.paojiao.sdk.utils.ColorState;
import com.paojiao.sdk.utils.JSONLoginInfo;
import com.paojiao.sdk.utils.ResourceUtil;

/**
 * @author 仁秋
 * 
 */
public class RegisterDailog extends BaseDialog {

	private String mUserName;
	private String mPassword;
	private String mPassword2;
	private String mEmail;
	private String mNickName;

	private RegisterApi registerApi;

	private EditText mUserNameView;
	private EditText mPasswordView;
	private EditText mPasswordView2;
	private EditText mEmailView;
	private EditText mNickNameView;
	private TextView mTvRegStatus;
	private View mLoginFormView;
	private View mLoginStatusView;

	protected RegisterDailog(Activity activity,
			final CallbackListener callbackListener, String userName) {
		super(activity, callbackListener);
		mUserName = userName;
		setContentView(ResourceUtil.getLayoutId(activity,
				"pj_layout_act_register"));
		if(PJApi.SCREEN_WIDTH<=620)
			setContentView(ResourceUtil.getLayoutId(activity,
					"pj_layout_act_register_small"));
		setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				try {
					registerApi.getHttpClient().getConnectionManager()
							.shutdown();
				} catch (Exception ex) {
				}
				PJError error = new PJError(PJError.CODE_CANCEL, "取消注册");
				callbackListener.onLoginError(error);
			}
		});
	}

	@Override
	protected void initData() {
		// mUserNameView.setText(mUserName);
		registerApi = new RegisterApi();
	}

	@Override
	protected void setListener() {
		Button btRegister = (Button) findViewById(ResourceUtil.getId(activity,
				"pj_register_do_button"));
		btRegister
				.setTextColor(ColorState.SetTextColor(0xFFFFFFFF, 0xFFE40019));
		btRegister.setText(StringGlobal.REGISTER);
		btRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		Button btRegisterLog = (Button) findViewById(ResourceUtil.getId(
				activity, "pj_register_back"));
		btRegisterLog.setText(StringGlobal.LOGIN);
		btRegisterLog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginDailog dialog = new LoginDailog(activity, callbackListener);
				dialog.show();
				RegisterDailog.this.dismiss();
			}
		});
	}

	@Override
	protected void findView() {
		mPasswordView = (EditText) findViewById(ResourceUtil.getId(activity,
				"pj_register_password_editText"));
		mPasswordView.setHint(StringGlobal.REGISTER_PWD_HINT);
		mPasswordView2 = (EditText) findViewById(ResourceUtil.getId(activity,
				"pj_register_password2_editText"));
		mPasswordView2.setHint(StringGlobal.CONFIRM_PWD_HINT);
		mUserNameView = (EditText) findViewById(ResourceUtil.getId(activity,
				"pj_register_username_editText"));
		mUserNameView.setHint(StringGlobal.REGISTER_NAME_HINT);
		mTvRegStatus = (TextView) findViewById(ResourceUtil.getId(activity,
				"pj_register_status_message"));
		mTvRegStatus.setText(StringGlobal.REGISTER_PROGRESS);
		mEmailView = (EditText) findViewById(ResourceUtil.getId(activity,
				"pj_register_email_editText"));
		mNickNameView = (EditText) findViewById(ResourceUtil.getId(activity,
				"pj_register_nickname_editText"));
		mLoginFormView = findViewById(ResourceUtil.getId(activity,
				"pj_register_form"));
		mLoginStatusView = findViewById(ResourceUtil.getId(activity,
				"pj_register_status"));
		mPasswordView2.setOnEditorActionListener(new OnEditorActionListener() {

			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				attemptLogin();
				return false;
			}
		});
	}

	public void attemptLogin() {
		mUserNameView.setError(null);
		mPasswordView.setError(null);
		mPasswordView2.setError(null);
		mEmailView.setError(null);
		mNickNameView.setError(null);

		mUserName = mUserNameView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mPassword2 = mPasswordView2.getText().toString();
		mEmail = mEmailView.getText().toString();
		mNickName = mNickNameView.getText().toString();

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

		if (TextUtils.isEmpty(mPassword2)) {
			mPasswordView2.setError(StringGlobal.CAN_NOT_NULL);
			focusView = mPasswordView2;
			cancel = true;
		} else if (!mPassword2.equals(mPassword)) {
			mPasswordView2.setError(StringGlobal.DIFFERENT_PWD);
			focusView = mPasswordView2;
			cancel = true;
		}
		if (TextUtils.isEmpty(mUserName)) {
			mUserNameView.setError(StringGlobal.CAN_NOT_NULL);
			focusView = mUserNameView;
			cancel = true;
		} else if (mUserNameView.length() > 16 || mUserNameView.length() < 6) {
			mUserNameView.setError(StringGlobal.ILLEGAL_USER_NAME);
			focusView = mUserNameView;
			cancel = true;
		}
		if (!TextUtils.isEmpty(mEmail) && !mEmail.contains("@")) {
			mEmailView.setError(StringGlobal.INVALID_EMAIL);
			focusView = mEmailView;
			cancel = true;
		}
		if (cancel) {
			focusView.setFocusable(true);
			focusView.setFocusableInTouchMode(true);
			focusView.requestFocus();
		} else {
			showProgress(true);
			doRegister(mUserName, mPassword, mEmail, mNickName);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
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
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public void doRegister(String name, String pwd, String email,
			String mNickName) {
		registerApi.pjPost(getContext(), name, pwd, email, mNickName,
				new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						super.onStart();
						showProgress(true);
					}

					@Override
					public void onFinish() {
						super.onFinish();
						showProgress(false);
					}

					@Override
					protected void handleFailureMessage(Throwable e,
							String responseBody) {
						super.handleFailureMessage(e, responseBody);
						Toast.makeText(getContext(),
								StringGlobal.ERROR_NETWORK_DISSABLE,
								Toast.LENGTH_LONG).show();
					}

					@Override
					protected void handleSuccessMessage(int statusCode,
							Header[] headers, String responseBody) {
						super.handleSuccessMessage(statusCode, headers,
								responseBody);

						Base base = JSONLoginInfo.checkForError(getContext(),
								responseBody);
						if (base != null) {
							if (base.getCode().equals(PJError.CODE_EXISTS_USER)) {
								mUserNameView.setError(StringGlobal.USER_EXIST);
								mUserNameView.setFocusable(true);
								mUserNameView.setFocusableInTouchMode(true);
								mUserNameView.requestFocus();
								return;
							}
							Toast.makeText(getContext(),
									StringGlobal.REGISTER_SUCCESS,
									Toast.LENGTH_LONG).show();
							LoginDailog dialog = new LoginDailog(activity,
									callbackListener, mUserName, mPassword);
							dialog.show();
							RegisterDailog.this.dismiss();
						}
					}
				});
	}

}
