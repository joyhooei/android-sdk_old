package com.paojiao.sdk.bean.common;

/**
 * 热点消息实体类
 * 
 * @author zhounan
 * @version 2014-8-5 下午2:34:00
 */
public class Hotspot {
	// hType为3时，显示群号、无有效链接
	private int hType;
	private String url;
	private String title;
	private String cover;
	private String coverSize;

	public String getCoverSize() {
		return coverSize;
	}

	public void setCoverSize(String coverSize) {
		this.coverSize = coverSize;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public Hotspot() {

	}

	public Hotspot(int hType, String url, String title) {
		super();
		this.hType = hType;
		this.url = url;
		this.title = title;
	}

	public int gethType() {
		return hType;
	}

	public void sethType(int hType) {
		this.hType = hType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}