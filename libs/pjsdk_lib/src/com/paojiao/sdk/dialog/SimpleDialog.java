/**
 * 
 */
package com.paojiao.sdk.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.paojiao.sdk.CallbackListener;
import com.paojiao.sdk.PJApi;
import com.paojiao.sdk.dialog.base.BaseDialog;
import com.paojiao.sdk.utils.ColorState;
import com.paojiao.sdk.utils.ResourceUtil;

/**
 * @author liurenqiu520
 * 
 */
public class SimpleDialog extends BaseDialog {

	private TextView messageText, tipsText, titleText;
	private Button negativeButton, positiveButton;

	public SimpleDialog(Context context, CallbackListener callbackListener) {
		super(context, callbackListener);
		if (PJApi.SCREEN_WIDTH > 620)
			setContentView(ResourceUtil.getLayoutId(context,
					"pj_layout_simple_dialog"));
		else
			setContentView(ResourceUtil.getLayoutId(context,
					"pj_layout_simple_dialog_small"));
	}

	public void setTips(String message) {
		if (message != null) {
			tipsText.setVisibility(View.VISIBLE);
		}
		tipsText.setText(message);
	}

	public void setTips(int message) {
		tipsText.setText(message);
	}

	public void setTitle(String message) {
		titleText.setVisibility(View.VISIBLE);
		titleText.setText(message);
	}

	public void setTitle(int message) {
		titleText.setText(message);
	}

	public void setMessage(String message) {
		messageText.setText(message);
	}

	public void setMessage(int message) {
		messageText.setText(message);
	}

	@Override
	protected void initData() {
		messageText.setText("");
	}

	@Override
	protected void setListener() {
	}

	public void setPositiveButton(String text, View.OnClickListener listener) {
		positiveButton.setVisibility(View.VISIBLE);
		positiveButton.setText(text);
		positiveButton.setOnClickListener(listener);
	}

	public void setNegativeButton(CharSequence text,
			View.OnClickListener listener) {
		negativeButton.setVisibility(View.VISIBLE);
		negativeButton.setText(text);
		negativeButton.setOnClickListener(listener);
	}

	public void setNegativeButton(int text, View.OnClickListener listener) {
		negativeButton.setVisibility(View.VISIBLE);
		negativeButton.setText(text);
		negativeButton.setOnClickListener(listener);
	}

	@Override
	protected void findView() {
		messageText = (TextView) findViewById(ResourceUtil.getId(getContext(),
				"pj_simple_dialog_message_textView"));
		tipsText = (TextView) findViewById(ResourceUtil.getId(getContext(),
				"pj_simple_dialog_tips_textView"));
		titleText = (TextView) findViewById(ResourceUtil.getId(getContext(),
				"pj_simple_dialog_title_textView"));
		negativeButton = (Button) findViewById(ResourceUtil.getId(getContext(),
				"pj_simple_dialog_nagtive_button"));
		negativeButton.setTextColor(ColorState.SetTextColor(0XFFFFFFFF,
				0XFFAAAAAA));
		positiveButton = (Button) findViewById(ResourceUtil.getId(getContext(),
				"pj_simple_dialog_positive_button"));
		positiveButton.setTextColor(ColorState.SetTextColor(0XFFFFFFFF,
				0XFFE40019));
	}

}
