package com.dd.whateat.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dd.whateat.DdApplication;
import com.dd.whateat.R;
import com.dd.whateat.utils.Utils;

public class LoadingHeaderLayout extends PullLoadingLayout {
	private final TextView headerTime;
	private String refreshingTime = DdApplication.getContext().getString(
			R.string.pull_to_refresh_time_label, Utils.getRefreshTime());

	public LoadingHeaderLayout(Context context, final int mode, String releaseLabel,
			String pullLabel, String refreshingLabel) {
		super(context, mode, releaseLabel, pullLabel, refreshingLabel);
		headerTime = (TextView) getHeader().findViewById(R.id.pull_to_refresh_time);
		headerTime.setText(refreshingTime);
	}
	
	@Override
	public void setHeader(Context context){
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_header, this);
		super.setHeader(header);
	}

	@Override
	public void refreshing() {
		super.refreshing();
		headerTime.setText(refreshingTime);
		refreshingTime = DdApplication.getContext().getString(
				R.string.pull_to_refresh_time_label, Utils.getRefreshTime());
	}

	public void setRefreshingTime(String time) {
		this.refreshingTime = time;
	}
}
