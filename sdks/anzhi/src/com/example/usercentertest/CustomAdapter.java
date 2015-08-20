package com.example.usercentertest;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class CustomAdapter extends BaseAdapter {
	List<String> data = null;
	Activity activity;
	public CustomAdapter(List<String> data,Activity activity) {
		this.data = data;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView t = new TextView(activity);
		t.setText(data.get(position));
		t.setTextColor(Color.WHITE);
		t.setTextSize(20f);
		t.setGravity(Gravity.CENTER);
		return t;
	}

}
