/**
 * 
 */
package com.paojiao.sdk.listener;

import org.apache.http.Header;

import android.app.Activity;

import com.paojiao.sdk.CallbackListener;
import com.paojiao.sdk.PJError;
import com.paojiao.sdk.bean.common.Base;
import com.paojiao.sdk.net.AsyncHttpResponseHandler;
import com.paojiao.sdk.utils.JSONLoginInfo;

/**
 * @author 仁秋
 * 
 */
public class PJOpenAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

	// private Activity context;
	private CallbackListener callbackListener;

	public PJOpenAsyncHttpResponseHandler(Activity context,
			CallbackListener callbackListener) {
		// this.context = context;
		this.callbackListener = callbackListener;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onFinish() {
		super.onFinish();
	}

	@Override
	protected void handleSuccessMessage(int statusCode, Header[] headers,
			String responseBody) {
		super.handleSuccessMessage(statusCode, headers, responseBody);
		Base base = JSONLoginInfo.checkForError(responseBody, callbackListener);
		if (base.getCode() !=null && !base.getCode().equals(Base.STATUS_OK)) {
			final int code = Integer.parseInt(base.getCode());
			PJError error = new PJError(code, base.getMsg());
			callbackListener.onOpenError(error);
			return;
		}
		pjHandleSuccessMessage(statusCode, headers, responseBody);
	}

	protected void pjHandleSuccessMessage(int statusCode, Header[] headers,
			String responseBody) {
	}

	@Override
	protected void handleFailureMessage(Throwable e, String responseBody) {
		super.handleFailureMessage(e, responseBody);
		callbackListener.onError(e);
	}

}
