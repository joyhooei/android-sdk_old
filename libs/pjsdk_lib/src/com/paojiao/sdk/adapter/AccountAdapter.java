/**
 * 
 */
package com.paojiao.sdk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paojiao.sdk.bean.AccountData;
import com.paojiao.sdk.utils.ColorState;
import com.paojiao.sdk.utils.ResourceUtil;

/**
 * @author 仁秋
 * 
 */
public class AccountAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<AccountData> accounts;
	private LayoutInflater inflater;

	public AccountAdapter(Context context, ArrayList<AccountData> accounts) {
		this.context = context;
		this.accounts = accounts;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return accounts.size();
	}

	@Override
	public AccountData getItem(int position) {
		return accounts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(
					ResourceUtil.getLayoutId(context, "pj_account_item"), null);
			holder.userName = (TextView) convertView.findViewById(ResourceUtil
					.getId(context, "pj_account_username_textView"));
			// holder.niceName = (TextView)
			// convertView.findViewById(ResourceUtil
			// .getId(context, "pj_account_nicename_textView"));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.userName.setTextColor(ColorState.SetTextColor(0xFFFF0000,
				0xFFAAAAAA));
		holder.userName.setText(accounts.get(position).getUserName());
		// holder.niceName.setText(accounts.get(position).getNiceName());
		return convertView;
	}

	static class ViewHolder {
		TextView userName;
		// TextView niceName;
	}
	// @Override
	// public void notifyDataSetChanged() {
	// accounts.clear();
	// accounts.addAll(Account.getUsers(context));
	// super.notifyDataSetChanged();
	// }

}
