package com.dd.whateat.widget;

import com.dd.whateat.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class LoadingArticleHeaderLayout extends PullLoadingLayout {

	private final TextView headerPreTitle;
	private String refreshingPreTitle;

	public LoadingArticleHeaderLayout(Context context, final int mode,
			String releaseLabel, String pullLabel, String refreshingLabel) {
		super(context, mode, releaseLabel, pullLabel, refreshingLabel);
		headerPreTitle = (TextView) getHeader().findViewById(
				R.id.pull_to_refresh_next_title);

	}

	
	@Override
	public void setHeader(Context context){
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_article_header, this);
		super.setHeader(header);
	}
	
	@Override
	public void refreshing() {
		super.refreshing();
		//headerNextTitle.setText(refreshingNextTitle);
	}

	public void setRefreshingNextTitle(String nextTitle) {
		this.refreshingPreTitle = nextTitle;
		headerPreTitle.setText(nextTitle);
	}
}
