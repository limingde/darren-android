package com.dd.whateat.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dd.whateat.R;

public class LoadingFooterLayout extends PullLoadingLayout {

	public LoadingFooterLayout(Context context, final int mode, String releaseLabel,
			String pullLabel, String refreshingLabel) {
		super(context, mode, releaseLabel, pullLabel, refreshingLabel);

	}
	
	@Override
	public void setHeader(Context context){
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_footer, this);
		super.setHeader(header);
	}
}
