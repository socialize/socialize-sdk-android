package com.socialize.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ScrollView;

public class SocializeActivityContent extends SocializeActivityViewChild {
	
	private int height;
	private ScrollView scroll;
	
	public SocializeActivityContent(Context context, SocializeActivityView parent, int height) {
		super(context, parent);
		this.height = height;
	}
	
	public void init() {
		setBackgroundColor(Color.parseColor("#313131"));
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, height);
		
		params.setMargins(0,0,0,0);
		setLayoutParams(params);	
		setOrientation(VERTICAL);
		
		scroll = new ScrollView(getContext());
		LayoutParams scrollParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		scroll.setLayoutParams(scrollParams);
		addView(scroll);
	}

	@Override
	public void addView(View child) {
		scroll.addView(child);
	}
	
	
}
