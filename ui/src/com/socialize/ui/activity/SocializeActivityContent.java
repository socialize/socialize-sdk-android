package com.socialize.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.ScrollView;

@SuppressWarnings("unused")
public class SocializeActivityContent extends SocializeActivityViewChild {
	
	private int height;
	private ScrollView scroll;
	private LinearLayout content;
	
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
		
//		scroll = new ScrollView(getContext());
//		LayoutParams scrollParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//		
//		scroll.setLayoutParams(scrollParams);
//		
//		content = new LinearLayout(getContext());
//		LayoutParams contentParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//		
//		content.setLayoutParams(contentParams);
//		content.setOrientation(VERTICAL);
//		
//		scroll.addView(content);
//		
//		addView(scroll);
	}

//	@Override
//	public void addView(View child) {
//		addView(child);
//	}
}
