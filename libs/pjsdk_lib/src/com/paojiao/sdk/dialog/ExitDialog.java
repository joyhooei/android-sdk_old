/**
 * 
 */
package com.paojiao.sdk.dialog;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.paojiao.imageLoad.ImageLoader;
import com.paojiao.sdk.CallbackListener;
import com.paojiao.sdk.ExitInterface;
import com.paojiao.sdk.PJApi;
import com.paojiao.sdk.activity.WebActivity;
import com.paojiao.sdk.api.GetInfoApi;
import com.paojiao.sdk.bean.RoleInfo;
import com.paojiao.sdk.dialog.base.BaseDialog;
import com.paojiao.sdk.net.AsyncHttpResponseHandler;
import com.paojiao.sdk.res.StringGlobal;
import com.paojiao.sdk.utils.Prints;
import com.paojiao.sdk.utils.ResourceUtil;
import com.paojiao.sdk.utils.Utils;
import com.paojiao.sdk.utils.HttpPost.HttpPostResponse;
import com.paojiao.sdk.widget.MyFloatView;

/**
 * @author paojiao
 * 
 */
public class ExitDialog extends BaseDialog {
	private Activity mContext;
	PJApi mPjApi;
	GetInfoApi getApi;
	Handler mLoadDataHandler;
	String activityUrl = "";
	String giftUrl = "";
	String bannerUrl = "";
	String imgUrl = "";
	private ImageView mBannerIv;
	private Bitmap mBannerBitmap;
	private ExitInterface mExitInterface;

	/**
	 * 
	 * @param activity
	 * @param bundle
	 *            带有玩家一些信息，方便上传至服务器
	 * @param pjApi
	 * @param callbackListener
	 *            可为空
	 * @param exitInterface添加了个回调
	 *            ，方便cp商处理自己事物
	 */

	public ExitDialog(Activity activity, PJApi pjApi,
			CallbackListener callbackListener, ExitInterface exitInterface) {
		super(activity, callbackListener);
		this.mExitInterface = exitInterface;
		this.mContext = activity;
		
		mPjApi = pjApi;
		if (PJApi.SCREEN_WIDTH > 620)
			setContentView(ResourceUtil.getLayoutId(activity,
					"pj_layout_exit_dialog"));
		else
			setContentView(ResourceUtil.getLayoutId(activity,
					"pj_layout_exit_dialog_small"));
	}
	
	
	
	private RoleInfo roleInfo;
	public ExitDialog(RoleInfo roleInfo,Activity activity, PJApi pjApi,
			CallbackListener callbackListener, ExitInterface exitInterface) {
		super(activity, callbackListener);
		this.mExitInterface = exitInterface;
		this.mContext = activity;
		this.roleInfo=roleInfo;
		mPjApi = pjApi;
		if (PJApi.SCREEN_WIDTH > 620)
			setContentView(ResourceUtil.getLayoutId(activity,"pj_layout_exit_dialog"));
		else
			setContentView(ResourceUtil.getLayoutId(activity,"pj_layout_exit_dialog_small"));
	}
	

	/**
	 * 
	 * @param activity
	 * @param bundle
	 *            带有玩家一些信息，方便上传至服务器
	 * @param pjApi
	 * @param callbackListener
	 *            可为空
	 */

	public ExitDialog(Activity activity, PJApi pjApi,
			CallbackListener callbackListener) {
		super(activity, callbackListener);
		this.mContext = activity;
		
		mPjApi = pjApi;
		setContentView(ResourceUtil.getLayoutId(activity,
				"pj_layout_exit_dialog"));
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		MyFloatView.newInstance().hide();
	}

	protected void initData() {
		getApi = GetInfoApi.getInstance();
		getApi.pjPostExit(mContext, PJApi.getAppId(),
				new AsyncHttpResponseHandler() {
					public void onSuccess(int statusCode, String content) {
						super.onSuccess(statusCode, content);
						JSONObject job;
						try {
							job = new JSONObject(content);
							int code = job.optInt("code");
							if (code == 1) {
								if (job.has("data")) {
									JSONObject data = job.getJSONObject("data");
									activityUrl = data.optString("activeUrl");
									imgUrl = data.optString("bannerImg");
									bannerUrl = data.optString("bannerUrl");
									giftUrl = data.optString("giftUrl");
									Prints.i("EXIT_URL", "1--" + activityUrl
											+ "2--" + imgUrl + "3--"
											+ bannerUrl + "4--" + giftUrl);
									BitmapFactory.Options opt = new BitmapFactory.Options();
									opt.inPreferredConfig = Bitmap.Config.RGB_565;
									opt.inPurgeable = true;
									opt.inInputShareable = true;
									// LoadAsyncTask task = new LoadAsyncTask(
									// imgUrl);
									// task.execute(imgUrl);
									ImageLoader imageLoader = ImageLoader
											.getInstance(mContext);
									imageLoader.displayImage(imgUrl, mBannerIv,
											ResourceUtil.getDrawableId(
													getContext(),
													"pj_exit_default_banner"),
											0, 0);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
		mLoadDataHandler = new Handler() {
		};
	}

	/**
	 * 为imageView添加网络图片
	 * 
	 * @author paojiao
	 * 
	 */
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
					mBannerBitmap = BitmapFactory.decodeStream(
							picUrl.openStream(), null, opt);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return null;
		}

		@SuppressLint("NewApi")
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			mBannerIv.setCropToPadding(true);
			Bitmap finalBitmap = Utils.getRoundedCornerBitmap(mBannerBitmap);
			mBannerIv.setImageBitmap(finalBitmap);
		}
	}

	protected void setListener() {
		// banner宣传页，点击跳转到广告宣传页面。
		findViewById(ResourceUtil.getId(getContext(), "pj_banner_iv"))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PJApi.startActivity(WebActivity.class, bannerUrl, null);
					}
				});
		// 活动，点击跳转到webView页面
		findViewById(ResourceUtil.getId(getContext(), "pj_act_iv"))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PJApi.startActivity(WebActivity.class, activityUrl,
								null);
					}
				});
		// 礼包，点击跳转到webView页面
		findViewById(ResourceUtil.getId(getContext(), "pj_gift_iv"))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						PJApi.startActivity(WebActivity.class, giftUrl, null);
					}
				});
		// 继续游戏，点击关闭本页面
		Button btContinue = (Button) findViewById(ResourceUtil.getId(getContext(), "pj_exit_continute"));
		btContinue.setTypeface(Typeface.MONOSPACE);
		btContinue.setText(StringGlobal.CONTINUE_TO_GAME);
		findViewById(ResourceUtil.getId(getContext(), "pj_exit_continute"))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						cancel();
					}
				});
		// 退出按钮，点击退出应用
		Button bt = (Button) findViewById(ResourceUtil.getId(getContext(),"pj_exit_iv"));
		bt.setTypeface(Typeface.MONOSPACE);
		bt.setText(StringGlobal.EXIT_GAME);
		findViewById(ResourceUtil.getId(getContext(), "pj_exit_iv"))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						if(roleInfo==null){
							mPjApi.exitNormal(mExitInterface);
							mPjApi.distoryApi();
							return;
						}
						
					
						mPjApi.uploadPlayerInfo(roleInfo,new HttpPostResponse() {
							@Override
							public void response(String msg) {
								mPjApi.exitNormal(mExitInterface);
								mPjApi.distoryApi();
							}
						});
						
						dismiss();
					}
				});
	}

	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		MyFloatView.newInstance().show();
	}

	protected void findView() {
		mBannerIv = (ImageView) findViewById(ResourceUtil.getId(getContext(),
				"pj_banner_iv"));
	}
}
