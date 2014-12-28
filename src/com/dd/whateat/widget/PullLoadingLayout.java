package com.dd.whateat.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dd.whateat.R;

public class PullLoadingLayout extends FrameLayout {
	static final int DEFAULT_ROTATION_ANIMATION_DURATION = 150;

	private ImageView headerImage;
	private ProgressBar headerProgress;
//	private ProgressBar headerProgressPull, headerProgressLoading;
	private ProgressBar gifview_pull;
	private ProgressBar gifview_loading;
	private TextView headerText;

	private String pullLabel;
	private String refreshingLabel;
	private String releaseLabel;
	private ViewGroup header;
	private final Animation rotateAnimation, resetRotateAnimation;

	public PullLoadingLayout(Context context, final int mode, String releaseLabel,
			String pullLabel, String refreshingLabel) {
		super(context);
		
		final Interpolator interpolator = new LinearInterpolator();
		rotateAnimation = new RotateAnimation(0, -180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setInterpolator(interpolator);
		rotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		rotateAnimation.setFillAfter(true);

		resetRotateAnimation = new RotateAnimation(-180, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		resetRotateAnimation.setInterpolator(interpolator);
		resetRotateAnimation.setDuration(DEFAULT_ROTATION_ANIMATION_DURATION);
		resetRotateAnimation.setFillAfter(true);

		
		this.releaseLabel = releaseLabel;
		this.pullLabel = pullLabel;
		this.refreshingLabel = refreshingLabel;

		initLayout(context, mode);
	}

	public void initLayout(Context context, final int mode) {
		setHeader(context);
		headerText = (TextView) header.findViewById(R.id.pull_to_refresh_text);
		headerImage = (ImageView) header.findViewById(R.id.pull_to_refresh_image);
		headerProgress = (ProgressBar) header.findViewById(R.id.pull_to_refresh_progress);
//		headerProgressPull = (ProgressBar) header.findViewById(R.id.pull_to_refresh_pull);
//		headerProgressLoading = (ProgressBar) header.findViewById(R.id.pull_to_refresh_loading);
//		headerGifPull = (GifView) header.findViewById(R.id.pull_to_refresh_gif_pull);
//		headerGifRelease = (GifView) header.findViewById(R.id.pull_to_refresh_gif_release);
		gifview_pull = (ProgressBar) header.findViewById(R.id.pull_to_refresh_gifview_pull);
		gifview_loading = (ProgressBar) header.findViewById(R.id.pull_to_refresh_gifview_loading);

		headerImage.setVisibility(GONE);
		headerProgress.setVisibility(GONE);
		gifview_pull.setVisibility(VISIBLE);
		gifview_loading.setVisibility(GONE);
//		headerGifPull.setShowDimension(Utils.dip2px(context, 67), Utils.dip2px(context, 40));
//		headerGifRelease.setShowDimension(Utils.dip2px(context, 67), Utils.dip2px(context, 40));
//		headerGifPull.setGifImage(R.drawable.pull_pull);
//		headerGifRelease.setGifImage(R.drawable.pull_release);
		
		switch (mode) {
		case PullToRefreshBase.MODE_PULL_UP_TO_REFRESH:
			headerImage.setImageResource(R.drawable.pulltorefresh_up_arrow);
//			headerGifPull.setVisibility(VISIBLE);
//			headerGifRelease.setVisibility(GONE);
			setProgressAnim(true);
			break;
		case PullToRefreshBase.MODE_PULL_DOWN_TO_REFRESH:
		default:
			headerImage.setImageResource(R.drawable.pulltorefresh_down_arrow);
//			headerGifPull.setVisibility(VISIBLE);
//			headerGifRelease.setVisibility(GONE);
			setProgressAnim(true);
			break;
		}
	}
	
	public ViewGroup getHeader(){
		return header;
	}

	public void setHeader(ViewGroup header){
		this.header =  header;
	}
	
	public void setHeader(Context context){
		ViewGroup header = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_header, this);
		this.header =  header;
	}
	
	public void reset() {
		headerText.setText(pullLabel);
//		headerImage.setVisibility(View.VISIBLE);
//		headerProgress.setVisibility(View.GONE);
//		headerGifPull.setVisibility(VISIBLE);
//		headerGifRelease.setVisibility(GONE);
		setProgressAnim(true);
	}

	public void releaseToRefresh() {
		headerText.setText(releaseLabel);
//		headerImage.clearAnimation();
//		headerImage.startAnimation(rotateAnimation);
	}

	public void setPullLabel(String pullLabel) {
		this.pullLabel = pullLabel;
	}

	public void refreshing() {
		headerText.setText(refreshingLabel);
		headerImage.clearAnimation();
//		headerImage.setVisibility(View.INVISIBLE);
//		headerProgress.setVisibility(View.VISIBLE);
//		headerGifPull.setVisibility(GONE);
//		headerGifRelease.setVisibility(VISIBLE);
		setProgressAnim(false);
	}

	public void setRefreshingLabel(String refreshingLabel) {
		this.refreshingLabel = refreshingLabel;
	}

	public void setReleaseLabel(String releaseLabel) {
		this.releaseLabel = releaseLabel;
	}

	public void pullToRefresh() {
		headerText.setText(pullLabel);
//		headerImage.clearAnimation();
//		headerImage.startAnimation(resetRotateAnimation);
	}

	public void setTextColor(int color) {
		headerText.setTextColor(color);
	}
	
	void setProgressAnim(boolean pull){
//		headerProgressPull.setVisibility(pull?VISIBLE:GONE);
//		headerProgressLoading.setVisibility(pull?GONE:VISIBLE);
//		
//		//调整progress大小
//		try{
//			ViewGroup.LayoutParams lp = headerProgressPull.getLayoutParams();
//			if(lp != null){
//				Drawable d = getResources().getDrawable(R.drawable.pull_1);
//				lp.width = d.getIntrinsicWidth();
//				lp.height = d.getIntrinsicHeight();
//				headerProgressPull.setLayoutParams(lp);
//			}
//		}catch (Exception e) {
//		}
//		try{
//			ViewGroup.LayoutParams lp = headerProgressLoading.getLayoutParams();
//			if(lp != null){
//				Drawable d = getResources().getDrawable(R.drawable.loading_1);
//				lp.width = d.getIntrinsicWidth();
//				lp.height = d.getIntrinsicHeight();
//				headerProgressLoading.setLayoutParams(lp);
//			}
//		}catch (Exception e) {
//		}
		
		if(pull){
			gifview_loading.setVisibility(GONE);
			gifview_pull.setVisibility(VISIBLE);
//			gifview.setImages(100L, R.drawable.pull_1, R.drawable.pull_2);
		}else{
			gifview_pull.setVisibility(GONE);
			gifview_loading.setVisibility(VISIBLE);
//			gifview.setImages(100L, R.drawable.loading_1, R.drawable.loading_2, R.drawable.loading_3, R.drawable.loading_4);
		}
		
	}
}
