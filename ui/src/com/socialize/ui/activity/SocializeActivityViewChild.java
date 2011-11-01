package com.socialize.ui.activity;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.widget.LinearLayout;

public abstract class SocializeActivityViewChild extends LinearLayout {
	
	private SocializeActivityView parentAdView;
	
	private int yOffset = 0;
	
	public SocializeActivityViewChild(Context context, SocializeActivityView parent) {
		super(context);
		this.parentAdView = parent;
	}
	
	protected SocializeActivityView getParentAdView() {
		return parentAdView;
	}
	
	public void notifyMove(int offset) {
		this.yOffset = offset;
	}
	
	protected void adjustHitRect(Rect rect) {
		rect.bottom += yOffset;
		rect.top += yOffset;
	}

	@Override
	public void getHitRect(Rect outRect) {
	    super.getHitRect(outRect);
	    adjustHitRect(outRect);
	    
	    Log.e("DEBUG", "Rect is top/botton [" +
	    		outRect.top +
	    		"/" +
	    		outRect.bottom +
	    		"]");
	}
}
