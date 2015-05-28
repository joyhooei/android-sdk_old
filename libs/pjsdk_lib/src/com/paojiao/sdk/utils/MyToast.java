package com.paojiao.sdk.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {

	public static void show(Context context, String text) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(
				ResourceUtil.getLayoutId(context, "pj_layout_toast"), null);
		TextView title = (TextView) layout.findViewById(ResourceUtil.getId(
				context, "pj_login_welcome_textView"));
		title.setText(text);
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.TOP, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();

		// Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		// toast.setGravity(Gravity.TOP, 0, 0);
		// LinearLayout toastView = (LinearLayout) toast.getView();
		// toastView.setOrientation(LinearLayout.HORIZONTAL);
		// toastView.setGravity(Gravity.BOTTOM);
		// ImageView imageCodeProject = new ImageView(context);
		// imageCodeProject.setImageResource(R.drawable.logo);
		// toastView.addView(imageCodeProject, 0);
		// toast.show();
	}
}
