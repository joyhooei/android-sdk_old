package com.paojiao.sdk.config;

import java.util.ArrayList;

import com.paojiao.sdk.bean.common.MenuNavs;

/**
 * 用于 类与类 之间 桥接的关系 或 中介 关系
 * @author zgt
 *
 */
public class BridgeMediation {
	
	/** 页面导航菜单 */
	private static ArrayList<MenuNavs> fMenu = new ArrayList<MenuNavs>();

	/** 获取页面导航菜单 */
	public static ArrayList<MenuNavs> getfMenu() {
		return fMenu;
	}

	/** 设置页面导航菜单 */
	public static void setfMenu(ArrayList<MenuNavs> fMenu) {
		BridgeMediation.fMenu = fMenu;
	}

}
