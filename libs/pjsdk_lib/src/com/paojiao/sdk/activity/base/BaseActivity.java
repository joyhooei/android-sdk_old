/**
 * 
 */
package com.paojiao.sdk.activity.base;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * @author liurenqiu520
 *项目中activity的父类
 */
public abstract class BaseActivity extends FragmentActivity {

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		findView();
		setListener();
		initData();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);	
		findView();
		setListener();
		initData();
	}
	/**
	 * 第三步初始化界面数据
	 * @author zhounan
	 * @param
	 */
	protected abstract void findView();
	/**
	 * 第二步初绑定控件事件
	 * @author zhounan
	 * @param
	 */
	protected abstract void setListener();
	/**
	 * 第一步初始化控件
	 * @author zhounan
	 * @param
	 */
	protected abstract void initData();
}
