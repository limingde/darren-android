package com.dd.whateat.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class WelcomViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<View> list;
	public  WelcomViewPagerAdapter(Context context,List<View> list){
		this.context=context;
		if(list==null){
			this.list=new ArrayList<View>();
		}else{
			this.list=list;
		}
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View v=list.get(position);
		container.addView(v);
		return v;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		View v=list.get(position);
		container.removeView(v);
	}
}
