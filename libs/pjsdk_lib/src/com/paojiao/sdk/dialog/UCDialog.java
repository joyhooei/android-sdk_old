/**
 * 
 */
package com.paojiao.sdk.dialog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.paojiao.sdk.PJApi;
import com.paojiao.sdk.activity.WebActivity;
import com.paojiao.sdk.adapter.NewsHotspotAdapter;
import com.paojiao.sdk.api.GetInfoApi;
import com.paojiao.sdk.bean.common.Hotspot;
import com.paojiao.sdk.bean.common.MenuNavs;
import com.paojiao.sdk.bean.common.UserCenterData.UCenterDaTa;
import com.paojiao.sdk.config.BridgeMediation;
import com.paojiao.sdk.config.ConfigurationInfo;
import com.paojiao.sdk.config.Route;
import com.paojiao.sdk.net.AsyncHttpResponseHandler;
import com.paojiao.sdk.res.StringGlobal;
import com.paojiao.sdk.utils.DialogAnimationUtils;
import com.paojiao.sdk.utils.DialogUtils;
import com.paojiao.sdk.utils.HomeListener;
import com.paojiao.sdk.utils.HomeListener.OnHomePressedListener;
import com.paojiao.sdk.utils.ImageUtils;
import com.paojiao.sdk.utils.JSONLoginInfo;
import com.paojiao.sdk.utils.Prints;
import com.paojiao.sdk.utils.ResourceUtil;
import com.paojiao.sdk.utils.RotateAnimation;
import com.paojiao.sdk.utils.RotateAnimation.InterpolatedTimeListener;
import com.paojiao.sdk.utils.Utils;
import com.paojiao.sdk.widget.FloatUtils;
import com.paojiao.sdk.widget.MyFloatView;

/**
 * 接口合成一个后使用
 * 
 * @author zhounan
 */
public class UCDialog extends Dialog implements
		android.view.View.OnClickListener, InterpolatedTimeListener {
	private String TAG = "rsp-uCenter";
	private ImageView pjAvatarIv;
	// 显示用户昵称
	private TextView pjNickNameTv;
	// 显示热点消息
	private ListView lv;
	private Context mContext;
	// 设置界面各模块数组
	List<MenuNavs> MenuNavsList = null;
	// 热点数组
	private ArrayList<Hotspot> hotsPotList;
	GetInfoApi userInfoApi;
	// 标记是否自动登录
	public static boolean isAutoLogin;
	private String token;
	String userId;
	// 用户头像bitmap
	private static Bitmap avatarBitmap;
	// 默认用户头像bitmap
	private Bitmap defBitmap;
	// 用户中心实体类，用以保存界面上各项数据
	UCenterDaTa userCenterData;
	private int todaySign;
	private ToggleButton toggle_AutoLogin;
	static String avatarUrl;
	private RelativeLayout tb1, tb2, tb3, tb4, tb5;
	// 各个版块的显示名称
	private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tvSetting;
	Handler handler;
	View view;
	HomeListener homeListener;
	private ImageView backToUcIv;
	private ImageView toSettingIv;
	public static UCDialog mUserCenter = null;
	// 点击签到后显示在侧边的+1效果
	private TextView showSignTv;
	private ImageView signIv;
	// 翻转动画一些功能
	RelativeLayout firstRelativeLayout, secondRelativeLayout,
			mFramRelativiLayout;
	RotateAnimation rotateAnim = null;
	float cX;
	float cY;
	private boolean enableRefresh = false;
	// 确定显示对话框的哪一面
	private static int HID_LAYOUT_FLAG;

	public static UCDialog newInstance(Context context) {
		if (mUserCenter == null) {
			mUserCenter = new UCDialog(context, ResourceUtil.getStyleId(
					context, "PJDialog"));
		}
		return mUserCenter;
	}

	public  UCDialog(Context context, int theme) {
		super(context, theme);
		// 无title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 背景色透明
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mContext = context;
		setCanceledOnTouchOutside(true);
		view = View.inflate(mContext,
				ResourceUtil.getLayoutId(mContext, "pj_dialog_uc"), null);
		if (PJApi.SCREEN_WIDTH < 620) {
			view = View.inflate(mContext,
					ResourceUtil.getLayoutId(mContext, "pj_dialog_uc_small"),
					null);
		}
		userInfoApi = GetInfoApi.getInstance();
		token = ConfigurationInfo.getCurrentToken(mContext);
		userId = ConfigurationInfo.getUid(mContext);
		if (!Utils.isMiuiV5(context)) {
			homeListener = new HomeListener(mContext);
			homeListener.setOnHomePressedListener(new OnHomePressedListener() {
				public void onHomePressed() {
					dismiss();
				}

				public void onHomeLongPressed() {
					dismiss();
				}
			});
			homeListener.startWatch();
		}
		findView(view);
		initUserData();
	}

	/**
	 * 获取网络数据
	 */
	private void GetInfoFromWeb() {
		GetInfoApi getInfoApi = GetInfoApi.getInstance();
		getInfoApi.pjPostGetLoginInfo(mContext, userId, token,
				new AsyncHttpResponseHandler() {

					public void onSuccess(int statusCode, String content) {
						super.onSuccess(statusCode, content);
						ConfigurationInfo.setInfoStr(mContext, content);
						userCenterData = JSONLoginInfo
								.getUserCenterInfo(content);
						Prints.i(
								"rsp-uCenter",
								"suc--"
										+ ConfigurationInfo
												.getInfoStr(mContext));
						setDataForView(false);
					}

					public void onFailure(Throwable error, String content) {
						super.onFailure(error, content);
						Prints.i("rsp-uCenter", "fai--" + content);
					}
				});
	}

	/**
	 * 将最终获取到的数据保存展示到view
	 * 
	 * @param first
	 *            为true时为第一次加载，false为从接口获得真实数据。
	 */
	private void setDataForView(boolean first) {
		if (userCenterData == null)
			return;
		toggle_AutoLogin.setChecked(userCenterData.isAutoLogin());
		pjNickNameTv.setText(userCenterData.getNiceName());
		todaySign = userCenterData.getTodaySign();
		if (todaySign == 0) {
			signIv.setImageDrawable(mContext.getResources().getDrawable(
					ResourceUtil.getDrawableId(mContext, "pj_icon_signed")));
			signIv.setClickable(false);
		}
		String avatal = userCenterData.getAvatar();
		if (!first) {
			if (avatal != null && !avatal.equals("")) {
				loadImage(userCenterData.getAvatar());
			} else {
				defBitmap = BitmapFactory.decodeResource(mContext
						.getResources(), ResourceUtil.getDrawableId(mContext,
						"pj_default_avatar_icon"));
				handler.sendEmptyMessage(2);
			}
		}
	}

	/**
	 * 获取一些本地已存信息
	 */
	private void getDefaultInfo() {
		setAvatarBitmap();
		String contentStr = ConfigurationInfo.getInfoStr(mContext);
		if (contentStr != null && !contentStr.equals("")) {
			userCenterData = JSONLoginInfo.getUserCenterInfo(contentStr);
		}
		setDataForView(true);
	}

	/**
	 * 设置头像
	 * 
	 * @param local
	 */
	private void setAvatarBitmap() {

		// 从本地文件中加载头像图片
		if (ConfigurationInfo.getAvatarUrl(mContext).equals("unknow")) {
			defBitmap = BitmapFactory.decodeResource(mContext.getResources(),
					ResourceUtil.getDrawableId(mContext,
							"pj_default_avatar_icon"));
			handler.sendEmptyMessage(2);
		} else {
			new Thread(new Runnable() {
				public void run() {
					defBitmap = BitmapFactory
							.decodeFile("/sdcard/pjsdk/myAva.png");
					handler.sendEmptyMessage(2);
				}
			}).start();
		}
	}

	private void loadImage(String url) {
		LoadAsyncTask imageTask = new LoadAsyncTask(url);
		imageTask.execute(url);
	}

	class LoadAsyncTask extends AsyncTask<String, Integer, Bitmap> {
		/**
		 * @param avatarUrl
		 */
		public LoadAsyncTask(String avatarUrl) {
		}

		protected Bitmap doInBackground(String... params) {
			final URL picUrl;
			try {
				picUrl = new URL(params[0]);
				try {
					BitmapFactory.Options opt = new BitmapFactory.Options();
					opt.inPreferredConfig = Bitmap.Config.RGB_565;
					opt.inPurgeable = true;
					opt.inInputShareable = true;
					avatarBitmap = BitmapFactory.decodeStream(
							picUrl.openStream(), null, opt);
					if(avatarBitmap!=null){
						ImageUtils.savaPicToLocal(mContext, avatarUrl);
						ImageUtils.writeFileToSD(avatarBitmap);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (MalformedURLException e) {

				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			pjAvatarIv.setImageBitmap(ImageUtils.getRoundBitmap(avatarBitmap,
					mContext));
		}
	}

	/**
	 * 初始化数据并逐个调用接口获取服务器数据
	 */
	private void initUserData() {
		initData();
		initHandler();
		initClick();
		getDefaultInfo();
		GetInfoFromWeb();
	}

	/**
	 * 监听自动登录状态接口,和设置默认头像
	 */
	private void initClick() {
		toggle_AutoLogin
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						String status = String.valueOf(isChecked);
						if (!ConfigurationInfo.getUid(mContext).equals(
								"unknown")
								&& token != null) {
							isAutoLogin = isChecked;
							userInfoApi.pjPostChangeLogStatus(mContext,
									ConfigurationInfo.getUid(mContext), token,
									status, new AsyncHttpResponseHandler() {
										protected void handleFailureMessage(
												Throwable e, String responseBody) {
											super.handleFailureMessage(e,
													responseBody);
											if (e != null)
												Prints.e(TAG,
														"ChangeLogStatus--fail"
																+ e.toString());
										}

										protected void handleSuccessMessage(
												int statusCode,
												Header[] headers,
												String responseBody) {
											super.handleSuccessMessage(
													statusCode, headers,
													responseBody);
											Prints.i(
													TAG,
													"ChangeLogStatus--suc"
															+ responseBody
																	.toString());
										}
									});
						}
					}
				});
	}

	/**
	 * 初始化一些控件
	 * 
	 * @param view
	 */
	private void findView(View view) {
		setContentView(view);
		// 个人资料数据
		pjAvatarIv = (ImageView) view.findViewById(ResourceUtil.getId(mContext,
				"pj_avatar_icon_iv"));
		pjNickNameTv = (TextView) view.findViewById(ResourceUtil.getId(
				mContext, "pj_nick_tv"));
		backToUcIv = (ImageView) view.findViewById(ResourceUtil.getId(mContext,
				"pj_back_to_t_uc"));
		toSettingIv = (ImageView) view.findViewById(ResourceUtil.getId(
				mContext, "pj_setting_iv_"));
		showSignTv = (TextView) findViewById(ResourceUtil.getId(mContext,
				"pj_sign_in_tips_tv"));

		signIv = (ImageView) findViewById(ResourceUtil.getId(mContext,
				"pj_sign_in_iv"));
		firstRelativeLayout = (RelativeLayout) view.findViewById(ResourceUtil
				.getId(mContext, "pj_uc_first_rl"));
		secondRelativeLayout = (RelativeLayout) view.findViewById(ResourceUtil
				.getId(mContext, "pj_uc_second_rl"));
		mFramRelativiLayout = (RelativeLayout) view.findViewById(ResourceUtil
				.getId(mContext, "pj_dialog_fram_rl_"));
		// 跳转页面
		tb1 = (RelativeLayout) view.findViewById(ResourceUtil.getId(mContext,
				"pj_tab1"));
		tb2 = (RelativeLayout) view.findViewById(ResourceUtil.getId(mContext,
				"pj_tab2"));
		tb3 = (RelativeLayout) view.findViewById(ResourceUtil.getId(mContext,
				"pj_tab3"));
		tb4 = (RelativeLayout) view.findViewById(ResourceUtil.getId(mContext,
				"pj_tab4"));
		tb5 = (RelativeLayout) view.findViewById(ResourceUtil.getId(mContext,
				"pj_tab5"));
		tv1 = (TextView) view.findViewById(ResourceUtil.getId(mContext,
				"pj_discus_tv"));
		tv2 = (TextView) view.findViewById(ResourceUtil.getId(mContext,
				"pj_group_tv"));
		tv3 = (TextView) view.findViewById(ResourceUtil.getId(mContext,
				"pj_lottery_tv"));
		tv4 = (TextView) view.findViewById(ResourceUtil.getId(mContext,
				"pj_gift_tv"));
		tv5 = (TextView) view.findViewById(ResourceUtil.getId(mContext,
				"pj_pay_tv"));
		tv6 = (TextView) view.findViewById(ResourceUtil.getId(mContext,
				"pj_auto_tv"));
		tv7 = (TextView) view.findViewById(ResourceUtil.getId(mContext,
				"pj_sign_tv"));
		tv8 = (TextView) view.findViewById(ResourceUtil.getId(mContext,
				"pj_customer_tv"));
		tvSetting = (TextView) view.findViewById(ResourceUtil.getId(mContext,
				"pj_setting_tv"));
		TextView[] tvs = new TextView[] { tv1, tv2, tv3, tv4, tv5 };
		RelativeLayout[] tbs = new RelativeLayout[] { tb1, tb2, tb3, tb4, tb5 };
		ImageView[] ivs = new ImageView[] { pjAvatarIv, backToUcIv,
				toSettingIv, signIv };

		// 自动登录
		toggle_AutoLogin = (ToggleButton) view.findViewById(ResourceUtil.getId(
				mContext, "pj_toggle_login"));
		lv = (ListView) findViewById(ResourceUtil.getId(mContext,
				"pj_msg_listview"));
		setNavsName(tvs, tbs, ivs);
	}

	/**
	 * 添加监听事件以及初始化一些设置
	 * 
	 * @param tvs
	 * @param tbs
	 * @param ivs
	 */
	private void setNavsName(TextView[] tvs, RelativeLayout[] tbs,
			ImageView[] ivs) {
		MenuNavsList = BridgeMediation.getfMenu();
		if(MenuNavsList!=null&&MenuNavsList.size()>0){
		for (int i = 0; i < MenuNavsList.size(); i++) {
			tvs[i].setOnClickListener(this);
			tbs[i].setOnClickListener(this);
			tvs[i].setText(MenuNavsList.get(i).getNavName());
		}
		for (int i = 0; i < ivs.length; i++) {
			ivs[i].setOnClickListener(this);
		}
		}
		showSignTv.setText(StringGlobal.ADD_BY_SIGNED);
		tv6.setText(StringGlobal.AUTO_LOGIN);
		tv7.setText(StringGlobal.TAB_SIGN_IN);
		tv8.setText(StringGlobal.TAB_CUSTOMER);
		tvSetting.setText(StringGlobal.SETTING);
		
	}

	/**
	 * 
	 */
	private void initHandler() {
		handler = new Handler() {
			public void handleMessage(Message msg) {
				//
				super.handleMessage(msg);
				pjAvatarIv.setImageBitmap(ImageUtils.getRoundBitmap(defBitmap,
						mContext));
			}
		};
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	/**
	 * 初始化热点消息列表和几个版块的入口的显示数据等一些初始数据
	 */
	private void initData() {
		hotsPotList = new ArrayList<Hotspot>();
		hotsPotList.clear();
		if(PJApi.hotspots!=null)hotsPotList.addAll(PJApi.hotspots);
		NewsHotspotAdapter adapter = new NewsHotspotAdapter(mContext,
				hotsPotList);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (hotsPotList.get(position).gethType() == 3)
					return;
				else if (hotsPotList.get(position).getUrl().equals("#")) {
					return;
				} else
					PJApi.startActivity(WebActivity.class,
							hotsPotList.get(position).getUrl(), null);
				dismiss();
			}
		});
	}

	public void onClick(View view) {
		Bundle params = Route.creatUrlParams(mContext);
		MenuNavs fMenu = null;

		if (view instanceof RelativeLayout) {
			if (MenuNavsList != null && MenuNavsList.size() > 0)
				if (view == tb1) {
					fMenu = MenuNavsList.get(0);
				} else if (view == tb2) {
					fMenu = MenuNavsList.get(1);
				} else if (view == tb3) {
					fMenu = MenuNavsList.get(2);
				} else if (view == tb4) {
					fMenu = MenuNavsList.get(3);
				} else if (view == tb5) {
					fMenu = MenuNavsList.get(4);
				} else
					return;
			else
				return;
			// 新sdk改成去除fragmentTab
			PJApi.startActivity(WebActivity.class, fMenu.getNavUrl(), params);
			dismiss();
		} else if (view instanceof ImageView) {
			cX = mFramRelativiLayout.getWidth() / 2.0f;
			cY = mFramRelativiLayout.getHeight() / 2.0f;
			if (view == pjAvatarIv) {
				PJApi.startActivity(WebActivity.class,
						com.paojiao.sdk.config.URL.UCENTER_INDEX + "?" + token,
						null);
				dismiss();
			} else if (view == backToUcIv) {
				HID_LAYOUT_FLAG = 1;
				// TODO 翻转动画
				enableRefresh = true;
				rotateAnim = new RotateAnimation(cX, cY,
						RotateAnimation.ROTATE_DECREASE);
				DialogAnimationUtils.startAct(rotateAnim, this,
						mFramRelativiLayout);
			} else if (view == toSettingIv) {
				HID_LAYOUT_FLAG = 2;
				enableRefresh = true;
				rotateAnim = new RotateAnimation(cX, cY,
						RotateAnimation.ROTATE_INCREASE);
				DialogAnimationUtils.startAct(rotateAnim, this,
						mFramRelativiLayout);
			} else if (view == signIv) {
				userInfoApi.pjPostSign(mContext, userId, token,
						new AsyncHttpResponseHandler() {
							protected void handleSuccessMessage(int statusCode,
									Header[] headers, String responseBody) {
								super.handleSuccessMessage(statusCode, headers,
										responseBody);
								Prints.i(TAG, "Sign--suc:" + responseBody);
								GetInfoFromWeb();
								DialogAnimationUtils.signAnimation(showSignTv);
							}

							protected void handleFailureMessage(Throwable e,
									String responseBody) {
								super.handleFailureMessage(e, responseBody);
								if (e != null)
									Prints.e(TAG, "Sign--fai:" + e.toString());
							}
						});
			}
		}
	}

	public void removeUserCenter(Context mContext) {
		try {
			if (mUserCenter != null) {
				if (!Utils.isMiuiV5(mContext))homeListener.stopWatch();
				mUserCenter = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (!Utils.isMiuiV5(mContext)) {
			MyFloatView.newInstance().show();
		} else {
			DialogUtils.tempShowFloat(mContext);
			if (mContext instanceof Activity) {
				FloatUtils.getnewInstall().showMiuiFloat((Activity)mContext);
			}
		}
	}

	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		MyFloatView.newInstance().hide();
	}

	protected void onStart() {
		super.onStart();
	}

	protected void onStop() {
		super.onStop();

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			dismiss();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void interpolatedTime(float interpolatedTime) {
		if (enableRefresh && interpolatedTime > 0.5f) {
			DialogAnimationUtils.setHint(firstRelativeLayout,
					secondRelativeLayout, HID_LAYOUT_FLAG);
			enableRefresh = false;
		}
	}

}
