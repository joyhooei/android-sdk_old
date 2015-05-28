package com.paojiao.sdk.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

/**
 * 
 * @ClassName: PJBaseAdapter
 * @Description: TODO(Adapter 基类)
 * @author 张广涛
 * @param <T>
 */
public abstract class PJBaseAdapter<T> extends BaseAdapter {

	protected LayoutInflater inflater;
	protected ArrayList<T> list;
	protected Context context;

	public PJBaseAdapter(Context context, ArrayList<T> list) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
