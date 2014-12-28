package com.dd.whateat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.dd.whateat.R;

public class PullToRefreshListView extends
		PullToRefreshAdapterViewBase<ListView> {

	private LoadingHeaderLayout headerLoadingView;
	private LoadingFooterLayout footerLoadingView;

	class InternalListView extends ListView implements EmptyViewMethodAccessor {
		// private GestureDetector mGestureDetector;

		public InternalListView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		public void setEmptyView(View emptyView) {
			PullToRefreshListView.this.setEmptyView(emptyView);
		}

		@Override
		public void setEmptyViewInternal(View emptyView) {
			super.setEmptyView(emptyView);
		}

		public ContextMenuInfo getContextMenuInfo() {
			return super.getContextMenuInfo();
		}

		@Override
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			return super.onInterceptTouchEvent(ev);
		}

	}

	public PullToRefreshListView(Context context) {
		super(context);
		this.setDisableScrollingWhileRefreshing(false);
	}

	public PullToRefreshListView(Context context, int mode) {
		super(context, mode);
		this.setDisableScrollingWhileRefreshing(false);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setDisableScrollingWhileRefreshing(false);
	}

	@Override
	public ContextMenuInfo getContextMenuInfo() {
		return ((InternalListView) getRefreshableView()).getContextMenuInfo();
	}

	public void setReleaseLabel(String releaseLabel) {
		super.setReleaseLabel(releaseLabel);
		if (null != headerLoadingView) {
			headerLoadingView.setReleaseLabel(releaseLabel);
		}
		if (null != footerLoadingView) {
			footerLoadingView.setReleaseLabel(releaseLabel);
		}
	}

	public void setPullLabel(String pullLabel) {
		super.setPullLabel(pullLabel);

		if (null != headerLoadingView) {
			headerLoadingView.setPullLabel(pullLabel);
		}
		if (null != footerLoadingView) {
			footerLoadingView.setPullLabel(pullLabel);
		}
	}

	public void setRefreshingLabel(String refreshingLabel) {
		super.setRefreshingLabel(refreshingLabel);

		if (null != headerLoadingView) {
			headerLoadingView.setRefreshingLabel(refreshingLabel);
		}
		if (null != footerLoadingView) {
			footerLoadingView.setRefreshingLabel(refreshingLabel);
		}
	}

	public void setRefreshingTime(String time) {
		if (null != headerLoadingView) {
			headerLoadingView.setRefreshingTime(time);
		}
	}

	@Override
	protected final ListView createRefreshableView(Context context,
			AttributeSet attrs) {
		ListView lv = new InternalListView(context, attrs);
		final int mode = this.getMode();

		// Add Loading Views
		if (mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) {
			// // Loading View Strings
			String pullLabel = context
					.getString(R.string.pull_to_refresh_pull_label_meila);
			String refreshingLabel = context
					.getString(R.string.pull_to_refresh_refreshing_label_meila);
			String releaseLabel = context
					.getString(R.string.pull_to_refresh_release_label_meila);

			FrameLayout frame = new FrameLayout(context);
			headerLoadingView = new LoadingHeaderLayout(context,
					MODE_PULL_DOWN_TO_REFRESH, releaseLabel, pullLabel,
					refreshingLabel);
			frame.addView(headerLoadingView,
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			headerLoadingView.setVisibility(View.GONE);
			lv.addHeaderView(frame);
		}
		if (mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) {
			// // Loading View Strings
			String pullLabel = context
					.getString(R.string.pull_to_refresh_pull_up_label);
			String refreshingLabel = context
					.getString(R.string.pull_to_refresh_refreshing_footer_label);
			String releaseLabel = context
					.getString(R.string.pull_to_refresh_release_footer_label);

			FrameLayout frame = new FrameLayout(context);
			footerLoadingView = new LoadingFooterLayout(context,
					MODE_PULL_UP_TO_REFRESH, releaseLabel, pullLabel,
					refreshingLabel);
			frame.addView(footerLoadingView,
					FrameLayout.LayoutParams.FILL_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			footerLoadingView.setVisibility(View.GONE);
			lv.addFooterView(frame);
		}

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		return lv;
	}

	@Override
	protected void setRefreshingInternal(boolean doScroll) {
		super.setRefreshingInternal(false);

		final PullLoadingLayout headerOriginalLoadingLayout, headerListViewLoadingLayout;
		final PullLoadingLayout footerOriginalLoadingLayout, footerListViewLoadingLayout;
		final int selection, scrollToY;

		switch (getCurrentMode()) {
		case MODE_PULL_UP_TO_REFRESH:
			footerOriginalLoadingLayout = this.getFooterLayout();
			footerListViewLoadingLayout = this.footerLoadingView;
			selection = refreshableView.getCount() - 1;
			scrollToY = getScrollY() - getHeaderHeight();
			Log.d("PullToRefreshListView", "MODE_PULL_UP_TO_REFRESH");
			
			// Hide our original Loading View
			footerOriginalLoadingLayout.setVisibility(View.INVISIBLE);

						// Show the ListView Loading View and set it to refresh
			footerListViewLoadingLayout.setVisibility(View.VISIBLE);
			footerListViewLoadingLayout.refreshing();
			break;
		case MODE_PULL_DOWN_TO_REFRESH:
		default:
			headerOriginalLoadingLayout = this.getHeaderLayout();
			headerListViewLoadingLayout = this.headerLoadingView;
			selection = 0;
			scrollToY = getScrollY() + getHeaderHeight();
			Log.d("PullToRefreshListView", "MODE_PULL_DOWN_TO_REFRESH");
			
			// Hide our original Loading View
			headerOriginalLoadingLayout.setVisibility(View.INVISIBLE);

			// Show the ListView Loading View and set it to refresh
			headerListViewLoadingLayout.setVisibility(View.VISIBLE);
			headerListViewLoadingLayout.refreshing();
			break;
		}

		if (doScroll) {
			// We scroll slightly so that the ListView's header/footer is at the
			// same Y position as our normal header/footer
			this.setHeaderScroll(scrollToY);
		}

		if (doScroll) {
			// Make sure the ListView is scrolled to show the loading
			// header/footer
			refreshableView.setSelection(selection);

			// Smooth scroll as normal
			smoothScrollTo(0);
		}
	}

	@Override
	protected void resetHeader() {
		Log.d("PullToRefreshListView", "resetHeader");
		final PullLoadingLayout headerOriginalLoadingLayout, headerListViewLoadingLayout;
		final PullLoadingLayout footerOriginalLoadingLayout, footerListViewLoadingLayout;
		
		int scrollToHeight = getHeaderHeight();
		final boolean doScroll;

		switch (getCurrentMode()) {
		case MODE_PULL_UP_TO_REFRESH:
			footerOriginalLoadingLayout = this.getFooterLayout();
			footerListViewLoadingLayout = footerLoadingView;
			doScroll = this.isReadyForPullUp();
			
			// Set our Original View to Visible
			footerOriginalLoadingLayout.setVisibility(View.VISIBLE);
			// Hide the ListView Header/Footer
			footerListViewLoadingLayout.setVisibility(View.GONE);
			break;
		case MODE_PULL_DOWN_TO_REFRESH:
		default:
			headerOriginalLoadingLayout = this.getHeaderLayout();
			headerListViewLoadingLayout = headerLoadingView;
			scrollToHeight *= -1;
			doScroll = this.isReadyForPullDown();
			
			// Set our Original View to Visible
			headerOriginalLoadingLayout.setVisibility(View.VISIBLE);
			// Hide the ListView Header/Footer
			headerListViewLoadingLayout.setVisibility(View.GONE);
			break;
		}

		// Scroll so our View is at the same Y as the ListView header/footer,
		// but only scroll if the ListView is at the top/bottom
		if (doScroll) {
			this.setHeaderScroll(scrollToHeight);
		}
		super.resetHeader();
	}

	public int getPullDownMode() {
		return MODE_PULL_DOWN_TO_REFRESH;
	}

	public int getPullUpMode() {
		return MODE_PULL_UP_TO_REFRESH;
	}

}
