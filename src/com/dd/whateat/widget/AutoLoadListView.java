package com.dd.whateat.widget;

import com.dd.whateat.R;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AutoLoadListView extends
		PullToRefreshListView {
	
	public AutoLoadListView(Context context) {
		super(context);
		this.setDisableScrollingWhileRefreshing(false);
		initFooter(context);
	}
	
	public AutoLoadListView(Context context, int mode) {
		super(context, mode);
		this.setDisableScrollingWhileRefreshing(false);
		initFooter(context);
	}

	public AutoLoadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setDisableScrollingWhileRefreshing(false);
		initFooter(context);
	}
	
	public boolean hideFooterWhenNoMore = false;
	View footer;
	TextView tv;
	ProgressBar pb;
	public void initFooter(Context context){
		footer = View.inflate(context, R.layout.pull_to_refresh_footer, null);
		tv = (TextView) footer.findViewById(R.id.pull_to_refresh_text);
		tv.setText(R.string.pull_to_refresh_refreshing_footer_label);
		pb = (ProgressBar) footer.findViewById(R.id.pull_to_refresh_progress);
		pb.setVisibility(VISIBLE);
		ImageView iv = (ImageView) footer.findViewById(R.id.pull_to_refresh_image);
		iv.setVisibility(GONE);
		
		final ListView listView = getRefreshableView();
		listView.addFooterView(footer, null, false);
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollListener != null){
					scrollListener.onScrollStateChanged(view, scrollState);
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
//				MeilaLog.d("test", "onScroll, "+firstVisibleItem+", "+visibleItemCount+", "+totalItemCount+", "+listView.getChildCount());
				if(totalItemCount > 0 && firstVisibleItem+visibleItemCount >= totalItemCount-listView.getFooterViewsCount() -2 && mHasMore && !isAutoLoading){
					int headerCount = listView.getHeaderViewsCount();
					int footCount = listView.getFooterViewsCount();
					if(autoLoadListener != null && totalItemCount - headerCount - footCount > 0){
						isAutoLoading = true;
						autoLoadListener.onload();
					}
				}
				
				if(scrollListener != null){
					scrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
				}
			}
		});
	}
	
	String footerCompleteLabel;
	public void setFooterCompleteLabel(String footerLabel){
		this.footerCompleteLabel = footerLabel;
	}
	String getFooterCompleteLabel(){
		if(TextUtils.isEmpty(footerCompleteLabel)){
			return getContext().getResources().getString(R.string.load_all_complete);
		}else{
			return footerCompleteLabel;
		}
	}
	
	OnScrollListener scrollListener;
	public final void setOnScrollListener(OnScrollListener listener) {
		scrollListener = listener;
	}
	
	AutoLoadListener autoLoadListener;
	boolean mHasMore = true;
	boolean mLostMore = mHasMore;
	public void setAutoLoadListener(AutoLoadListener listener){
		autoLoadListener = listener;
	}
	public void resetLastMoreStatus() {
		onAutoLoadComplete(mLostMore);
		mLostMore = mHasMore;
	}
	
	public void onAutoLoadComplete(boolean hasMore){
		isAutoLoading = false;
		mLostMore = this.mHasMore;
		this.mHasMore = hasMore;
		tv.setText(hasMore?getContext().getResources().getString(R.string.pull_to_refresh_refreshing_footer_label):getFooterCompleteLabel());
		pb.setVisibility(hasMore?VISIBLE:GONE);
//		if(hasMore){
//			footerGifRelease.setVisibility(VISIBLE);
//		}else{
//			footerGifPull.setVisibility(GONE);
//			footerGifRelease.setVisibility(GONE);
//		}
		
		if(hideFooterWhenNoMore && !hasMore){
			footer.setVisibility(GONE);
//			MeilaLog.d("test", "hide footer, "+hideFooterWhenNoMore+", "+hasMore);
		}else{
			footer.setVisibility(VISIBLE);
//			MeilaLog.d("test", "not hide footer, "+hideFooterWhenNoMore+", "+hasMore);
		}
	}
	
	public void setFooterVisible(boolean isShow){
		if(isShow){
			footer.setVisibility(VISIBLE);
		} else {
			footer.setVisibility(GONE);
		}
	}
	boolean isAutoLoading = false;
	public boolean isAutoLoading(){
		return isAutoLoading;
	}
	public interface AutoLoadListener{
		public void onload();
	}
}
