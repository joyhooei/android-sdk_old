/**
 * 
 */
package com.paojiao.sdk.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.paojiao.sdk.CallbackListener;
import com.paojiao.sdk.config.ConfigurationInfo;
import com.paojiao.sdk.config.Route;
import com.paojiao.sdk.utils.DialogUtils;
import com.paojiao.sdk.utils.ResourceUtil;
import com.paojiao.sdk.utils.DialogUtils.concreteIBack;

/**
 * @author liurenqiu520
 * 
 */
@SuppressLint("ViewConstructor")
public class MiuiFloatView extends PopupWindow implements OnTouchListener,
		AnimationListener, OnClickListener {
	private LocalBroadcastManager lbm;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;
	private boolean isDrag;
	private boolean isRight = false;
	private View icon;
	private Activity context;
	private ImageView mFloatImage;
	private ImageView mAnimationImg;
	private Animation rotaAnimation;
	private boolean beenClicked = false;
	public static int statusBarHeight = 0;
	private int wWidth;
	
	
	private static MiuiFloatView miuiFloatView;
	
	public static MiuiFloatView newInstance(Activity context){
		if(miuiFloatView==null){
			miuiFloatView = new MiuiFloatView(context);
		}
		return miuiFloatView;
	}
	
	private boolean second = true;
	public   MiuiFloatView(Activity context,boolean second){
		this.second = second;
		init(context);
	}

	public   MiuiFloatView(Activity context) {
		super(context);
		this.second = true;
		init(context);
		
		
	}
	
	private  void init(Activity context){
		this.context = context;
		View view = View.inflate(context,
				ResourceUtil.getLayoutId(this.context, "pj_layout_float_view"),
				null);
		findView(view);
		statusBarHeight = getStatusBarHeight(context);
		Display display = context.getWindowManager().getDefaultDisplay();
		wWidth = display.getWidth();
		setContentView(view);
		setBackgroundDrawable(null);
		setHeight(LayoutParams.WRAP_CONTENT);
		setWidth(LayoutParams.WRAP_CONTENT);
		setFocusable(false);
		setTouchable(true);
		setOutsideTouchable(false);
		lbm = LocalBroadcastManager.getInstance(context);
		this.update();
		timerForHide();
	}

	private void findView(View view) {
		icon = view.findViewById(ResourceUtil.getId(context, "pj_float_view"));
		mFloatImage = (ImageView) view.findViewById(ResourceUtil.getId(context,
				"pj_float_view_icon_imageView"));
		mAnimationImg = (ImageView) view.findViewById(ResourceUtil.getId(
				context, "pj_float_view_icon_notify"));
		icon.setOnTouchListener(this);
		rotaAnimation = AnimationUtils.loadAnimation(context,
				ResourceUtil.getAnimId(context, "pj_loading_anim"));
		rotaAnimation.setInterpolator(new LinearInterpolator());
		mAnimationImg.startAnimation(rotaAnimation);
		if(!second)mAnimationImg.setVisibility(View.GONE);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		removeTimerTask();
		if (view.equals(icon)) {
			x = event.getRawX();
			y = event.getRawY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mFloatImage.setImageResource(ResourceUtil.getDrawableId(
						context, "pj_image_float_logo"));
				isDrag = false;
				mTouchStartX = event.getX();
				mTouchStartY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				float mMoveStartX = event.getX();
				float mMoveStartY = event.getY();
				// 如果移动量大于3才移动
				if (Math.abs(mTouchStartX - mMoveStartX) > 3
						&& Math.abs(mTouchStartY - mMoveStartY) > 3) {
					isDrag = true;
					updateViewPosition();
					return false;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (x > wWidth / 2) {
					isRight = true;
				} else
					isRight = false;
				timerForHide();
				if (!beenClicked) {
					mAnimationImg.setVisibility(View.VISIBLE);
					mAnimationImg.startAnimation(rotaAnimation);
					if(!second)mAnimationImg.setVisibility(View.GONE);
				}
				mTouchStartX = mTouchStartY = 0;
				if (isDrag) {
					updateViewPosition0();
					return false;
				} else {
					if (!isDrag) {
						mAnimationImg.clearAnimation();
						mAnimationImg.setVisibility(View.GONE);
						beenClicked = true;
						iconOnClick(view);
					}
					int rawX = (int) event.getRawX();
					int rawY = (int) event.getRawY();
					int x = (int) event.getX();
					int y = (int) event.getY();
					concreteIBack s = new concreteIBack();
					s.getCordinates(x, y, rawX, rawY);
					//System.out.println("tx:" + rawX + "ty:" + rawY);
					Intent intent = new Intent(
							CallbackListener.ACTION_CARRYING_COORDINATE);
					Bundle bundle = new Bundle();
					ConfigurationInfo.setMiuiX(context, rawX);
					ConfigurationInfo.setMiuiY(context, rawY);
					bundle.putInt("rawX", rawX);
					bundle.putInt("rawY", rawY);
					bundle.putInt("x", x);
					bundle.putInt("y", y);
					intent.putExtra("coordinates", bundle);
					
					lbm.sendBroadcast(intent);
					
					
				}
				break;
			}
		}
		return true;
	}

	private void updateViewPosition0() {
		int tx = (int) (x - mTouchStartX);
		int ty = (int) (y - mTouchStartY);
		if (ty < statusBarHeight) {
			ty = statusBarHeight;
		}
		if (tx > wWidth / 2) {
			update(wWidth, ty, -1, -1);
		} else {
			update(0, ty, -1, -1);
		}
	}

	private void iconOnClick(View view) {
		// 结束旋转动画并隐藏
		if (mAnimationImg.isShown()) {
			View decorView = context.getWindow().getDecorView();
			Display display = context.getWindowManager().getDefaultDisplay();
			mAnimationImg.clearAnimation();
			mAnimationImg.setVisibility(View.GONE);
		}
		DialogUtils.showUserCenter(context);
		this.dismiss();
	}

	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		int tx = (int) (x - mTouchStartX);
		int ty = (int) (y - mTouchStartY);
		if (ty < statusBarHeight) {
			ty = statusBarHeight;
		}
		update(tx, ty, -1, -1);
	}

	public void onAnimationEnd(Animation animation) {
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onClick(View view) {
	}

	public void startActivity(Class<? extends Activity> _class, int show,
			String url, Bundle params) {
		Intent intent = new Intent(context, _class);
		intent.putExtra(Route.URL, url);
		intent.putExtra(Route.SHOW_TAB, show);
		intent.putExtra(Route.PARAMS, params);
		context.startActivity(intent);
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (isRight) {
				mFloatImage.setImageResource(ResourceUtil.getDrawableId(
						context, "pj_image_float_right"));
				mAnimationImg.clearAnimation();
				mAnimationImg.setVisibility(View.GONE);
			} else {
				mFloatImage.setImageResource(ResourceUtil.getDrawableId(
						context, "pj_image_float_left"));
				mAnimationImg.clearAnimation();
				mAnimationImg.setVisibility(View.GONE);
			}
			super.handleMessage(msg);
		}
	};
	Timer timer;
	TimerTask task;

	private void timerForHide() {
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};
		timer.schedule(task, 3000, 3000);

	}

	private void removeTimerTask() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	private int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}
}
