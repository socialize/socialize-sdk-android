package com.socialize.ui.activity;

import android.content.Context;
import android.graphics.Color;

public class SocializeActivityContent extends SocializeActivityViewChild {
	
	private int height;
	
	public SocializeActivityContent(Context context, SocializeActivityView parent, int height) {
		super(context, parent);
		this.height = height;
	}
	
	public void init() {
		setBackgroundColor(Color.WHITE);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, height);
		params.setMargins(0,0,0,0);
		setLayoutParams(params);	
	}
}
