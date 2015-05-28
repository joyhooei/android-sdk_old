package com.paojiao.sdk.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.paojiao.sdk.utils.DialogUtils;
import com.paojiao.sdk.utils.DisplayUtils;
import com.paojiao.sdk.utils.ResourceUtil;
import com.paojiao.sdk.utils.ImageUtils;

/**
 * 泡椒小悬浮窗
 * 
 * @author zhounan
 * @version 2014-8-5 上午11:56:34
 */
public class MyFloatView implements OnTouchListener {

	private WindowManager.LayoutParams wmParams;
	private WindowManager wm;
	private float x;
	private float y;
	private Context context;
	Animation rotaAnimation;
	private boolean isAddWindow = false;
	private boolean isRight = false;
	private boolean isShow = false;
	private boolean canHide = false;
	private boolean beenClicked = false;

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	private MyFloatView() {
	}

	private static MyFloatView myFloatView = null;

	public static MyFloatView newInstance() {
		if (myFloatView == null) {
			myFloatView = new MyFloatView();
		}
		return myFloatView;
	}

	public boolean isAddWindow() {
		return isAddWindow;
	}

	public void setAddWindow(boolean isAddWindow) {
		this.isAddWindow = isAddWindow;
	}

	public void showFloatView(Context mContext) {
		if (isAddWindow == false) {
			this.context = mContext;
			wm = (WindowManager) mContext.getSystemService("window");
			// 更新浮动窗口位置参数 靠边
			DisplayMetrics dm = new DisplayMetrics();
			// 获取屏幕信息
			wm.getDefaultDisplay().getMetrics(dm);
			screenWidth = dm.widthPixels;
			screenHigh = dm.heightPixels;
			System.out
					.println("width--" + screenWidth + "height:" + screenHigh);
			this.wmParams = new WindowManager.LayoutParams();
			// 设置window type
			wmParams.type = LayoutParams.TYPE_PHONE;
			// 设置图片格式，效果为背景透明
			wmParams.format = PixelFormat.RGBA_8888;
			// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
			wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
			// 调整悬浮窗显示的停靠位置为左侧置�?
			wmParams.gravity = Gravity.LEFT | Gravity.TOP;

			screenHigh = wm.getDefaultDisplay().getHeight();
			statusBarHeight = DisplayUtils.getStatusBarHeight(mContext);

			// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
			wmParams.x = 0;
			wmParams.y = screenHigh / 2;

			// 设置悬浮窗口长宽数据
			wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
			wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
			wm.addView(createView(mContext), wmParams);
			MyFloatView.newInstance().hide();
		}
		isAddWindow = true;
	}

	private ImageView mFloatImage;
	private ImageView mAnimationImg;
	private View view;

	int screenWidth;
	int screenHigh;
	int statusBarHeight;

	private View createView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		// 从布局文件获取浮动窗口视图
		view = inflater.inflate(
				ResourceUtil.getLayoutId(this.context, "pj_layout_float_view"),
				null);
		mFloatImage = (ImageView) view.findViewById(ResourceUtil.getId(context,
				"pj_float_view_icon_imageView"));
		mAnimationImg = (ImageView) view.findViewById(ResourceUtil.getId(
				context, "pj_float_view_icon_notify"));
		view.setOnTouchListener(this);
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!isDrag) {
					mAnimationImg.clearAnimation();
					mAnimationImg.setVisibility(View.GONE);
					openUserCenter();
					beenClicked = true;
				}
			}
		});
		view.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		rotaAnimation = AnimationUtils.loadAnimation(context,
				ResourceUtil.getAnimId(context, "pj_loading_anim"));
		rotaAnimation.setInterpolator(new LinearInterpolator());
		mAnimationImg.startAnimation(rotaAnimation);
		return view;
	}

	private float mTouchStartX;
	private float mTouchStartY;
	private boolean isDrag;

	public boolean onTouch(View v, MotionEvent event) {
		removeTimerTask();
		// 获取相对屏幕的坐标，即以屏幕左上角为原点
		x = event.getRawX();
		y = event.getRawY() - 25; // 25是系统状态栏的高度
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isDrag = false;
			mTouchStartX = event.getX();
			mTouchStartY = event.getY();
			mFloatImage.setImageResource(ResourceUtil.getDrawableId(
					this.context, "pj_image_float_logo"));
			break;
		case MotionEvent.ACTION_MOVE:
			float mMoveStartX = event.getX();
			float mMoveStartY = event.getY();
			// 如果移动量大于3才移动
			if (Math.abs(mTouchStartX - mMoveStartX) > 3
					&& Math.abs(mTouchStartY - mMoveStartY) > 3) {
				isDrag = true;// 移动了
				// 更新浮动窗口位置参数
				wmParams.x = (int) (x - mTouchStartX);
				wmParams.y = (int) (y - mTouchStartY);
				wm.updateViewLayout(view, wmParams);
				return false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (wmParams.x >= screenWidth / 2) {
				wmParams.x = screenWidth;
				isRight = true;
			} else if (wmParams.x < screenWidth / 2) {
				isRight = false;
				wmParams.x = 0;
			}
			mFloatImage.setImageResource(ResourceUtil.getDrawableId(
					this.context, "pj_image_float_logo"));
			if (!beenClicked) {
				mAnimationImg.setVisibility(View.VISIBLE);
				mAnimationImg.startAnimation(rotaAnimation);
			}
			timerForHide();
			wm.updateViewLayout(view, wmParams);
			// 初始化
			mTouchStartX = mTouchStartY = 0;

			break;
		}
		return false;
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

	private void openUserCenter() {
		MyFloatView.newInstance().hide();
		DialogUtils.showUserCenter(context);
	}

	public void removeFloatView(Context mContext) {
		WindowManager wm = (WindowManager) mContext.getSystemService("window");
		try {
			if (view != null && isAddWindow) {
				wm.removeView(view);
				isAddWindow = false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void hide() {
		if (view != null) {
			view.setVisibility(View.GONE);
			removeTimerTask();
			isShow = false;
		}
	}

	public void show() {
		if (view != null) {
			mFloatImage.setImageResource(ResourceUtil.getDrawableId(
					this.context, "pj_background_float_icon"));
			view.setVisibility(View.VISIBLE);
			isShow = true;
			timerForHide();
		}
	}

	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// 比如隐藏悬浮框
			if (canHide) {
				canHide = false;
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
			}
			super.handleMessage(msg);
		}
	};

	Timer timer;
	TimerTask task;

	private void timerForHide() {

		canHide = true;
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
		if (canHide) {
			timer.schedule(task, 3000, 3000);
		}

	}

	public void hideNoSetShow() {
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	public void showNoSetShow() {
		if (view != null) {
			view.setVisibility(View.VISIBLE);
		}
	}
}
