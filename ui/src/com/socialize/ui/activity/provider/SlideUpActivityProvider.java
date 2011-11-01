package com.socialize.ui.activity.provider;

import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.socialize.ads.SocializeActivityProvider;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.ui.activity.SocializeActivityView;

public class SlideUpActivityProvider implements SocializeActivityProvider {

	private IBeanFactory<SocializeActivityView> socializeActivityViewFactory;
	
	@Override
	public void wrap(View view) {
		SocializeActivityView socializeAdView = null;
		ViewParent parent = view.getParent();
		
		if(parent instanceof RelativeLayout) {
			
			RelativeLayout frame = (RelativeLayout) parent;
			
			if(socializeAdView == null) {
				socializeAdView = socializeActivityViewFactory.getBean();

				RelativeLayout.LayoutParams barParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				barParams.addRule(RelativeLayout.ABOVE, view.getId());
				
				socializeAdView.setLayoutParams(barParams);
				socializeAdView.setVisibility(View.GONE);
				
				// Position in front of original, but behind actionbar
				int childCount = frame.getChildCount();
				
				for (int i = 0; i < childCount; i++) {
					View child = frame.getChildAt(i);
					
					if(child == view) {
						frame.addView(socializeAdView, i);
						break;
					}
				}
				
				socializeAdView.slide();
			}
		}	
	}

	public void setSocializeActivityViewFactory(IBeanFactory<SocializeActivityView> socializeAdViewFactory) {
		this.socializeActivityViewFactory = socializeAdViewFactory;
	}
}
