package com.paojiao.sdk.utils;

import com.paojiao.sdk.dialog.UCDialog;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author zhjh
 * 
 */
public class DialogAnimationUtils {
	/**
	 * 切换两个View显示状态，显示一个隐藏一个
	 * 
	 * @param ll1
	 * @param ll2
	 * @param args
	 *            通过本参数来确定显示哪个view
	 */
	public static void setHint(RelativeLayout ll1, RelativeLayout ll2, int args) {
		ll1.setVisibility(View.GONE);
		ll2.setVisibility(View.GONE);
		if (args == 1)
			ll1.setVisibility(View.VISIBLE);
		else
			ll2.setVisibility(View.VISIBLE);
	}

	/**
	 */
	public static void startAct(RotateAnimation rotateAnim, UCDialog mDialog,
			RelativeLayout mLayout) {

		if (rotateAnim != null) {
			rotateAnim.setInterpolatedTimeListener(mDialog);
			rotateAnim.setFillAfter(true);
			mLayout.startAnimation(rotateAnim);
		}
	}

	public static void signAnimation(final TextView v) {
		v.setVisibility(View.VISIBLE);
		Animation alpha1 = new AlphaAnimation(0.1f, 1.0f);
		alpha1.setDuration(500);
		// 设置动画时间
		v.startAnimation(alpha1);
		final Animation alpha2 = new AlphaAnimation(1f, 0.1f);
		alpha2.setDuration(800);
		alpha1.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				//System.out.println("animation:onAnimationStart");
			}

			public void onAnimationRepeat(Animation animation) {
				//System.out.println("animation:onAnimationRepeat");

			}

			public void onAnimationEnd(Animation animation) {
				//System.out.println("animation:onAnimationEnd");
				v.setAnimation(alpha2);
			}
		});
		alpha2.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.GONE);
			}
		});

	}
}
