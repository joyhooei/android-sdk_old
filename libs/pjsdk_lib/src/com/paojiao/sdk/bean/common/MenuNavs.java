package com.paojiao.sdk.bean.common;
/**
 * 页面导航菜单 
 *
 */
public class MenuNavs {
	
	private String navName;
	private String navUrl;
	private int sort;
	
	public MenuNavs(){
		super();
	}
	public MenuNavs(String name, String url){
		navName = name;
		navUrl = url;
	}
	public String getNavName() {
		return navName;
	}

	public void setNavName(String navName) {
		this.navName = navName;
	}

	public String getNavUrl() {
		return navUrl;
	}

	public void setNavUrl(String navUrl) {
		this.navUrl = navUrl;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

}
