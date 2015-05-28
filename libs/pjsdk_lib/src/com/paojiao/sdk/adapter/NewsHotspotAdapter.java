package com.paojiao.sdk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paojiao.imageLoad.ImageLoader;
import com.paojiao.sdk.bean.common.Hotspot;
import com.paojiao.sdk.utils.Prints;
import com.paojiao.sdk.utils.ResourceUtil;

/**
 * 热点信息适配,用于展示小窗口的热点列表;
 * 
 * @author zhounan
 * @modify Van
 * @version 2014-5-19 下午2:28:32
 */
public class NewsHotspotAdapter extends PJBaseAdapter<Hotspot> {
	private Context mContext;
	private int LIST_SIZE;

	public NewsHotspotAdapter(Context context, ArrayList<Hotspot> list) {
		super(context, list);
		LIST_SIZE = list.size();
		for (int i = 0; i < list.size(); i++) {
			Hotspot bean = list.get(i);
			if (bean.getCoverSize().equals("big")) {
				list.remove(i);
				list.add(0, bean);
			}
		}

		mContext = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		boolean urlFlag = list.get(position).getCoverSize().equals("big");
		if (convertView == null) {
			holder = new ViewHolder();
			if (urlFlag) {
				convertView = inflater.inflate(ResourceUtil.getLayoutId(
						context, "pj_layout_msg_special_item"), null);
			} else {
				convertView = inflater
						.inflate(ResourceUtil.getLayoutId(context,
								"pj_layout_msg_item"), null);
			}
			holder.msgDividerV = (View) convertView.findViewById(ResourceUtil
					.getId(context, "msg_divider"));
			holder.msgIcon = (ImageView) convertView.findViewById(ResourceUtil
					.getId(context, "msg_icon_iv"));
			holder.msgContentTv = (TextView) convertView
					.findViewById(ResourceUtil.getId(context, "msg_content_tv"));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == LIST_SIZE - 1 && holder.msgDividerV != null) {
			holder.msgDividerV.setVisibility(View.GONE);
		}
		String tmpStr = list.get(position).getTitle();
		int endNum = tmpStr.indexOf("]") + 1;
		String headStr = tmpStr.substring(0, endNum);
		String contentStr = tmpStr.substring(endNum, tmpStr.length());
		Prints.i("hotsMsg:","headStr:" + headStr + "\n" + "endStr:" + contentStr);
		String source = "<font color='red'>" + headStr + "</font>" + contentStr;
		Spanned spStr = Html.fromHtml(source);
		if (urlFlag)
			holder.msgContentTv.setText(contentStr);
		else
			holder.msgContentTv.setText(spStr);
		ImageLoader imageLoader = ImageLoader.getInstance(mContext);
		imageLoader.configureImageLoader(
				ResourceUtil.getDrawableId(mContext, "pj_default_avatar_icon"),
				1);
		imageLoader.displayImage(list.get(position).getCover(), holder.msgIcon);
		return convertView;
	}

	private class ViewHolder {
		TextView msgContentTv;
		View msgDividerV;
		ImageView msgIcon;
	}

}
