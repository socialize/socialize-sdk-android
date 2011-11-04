package com.socialize.ui.activity.provider;

import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.socialize.activity.SocializeActivityFactory;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.ui.activity.SocializeActivityView;

public class SlideUpActivityProvider implements SocializeActivityFactory<SocializeActivityView> {

	private IBeanFactory<SocializeActivityView> socializeActivityViewFactory;
	
	@Override
	public SocializeActivityView wrap(View view) {
		SocializeActivityView socializeActivityView = null;
		ViewParent parent = view.getParent();
		
		if(parent instanceof RelativeLayout) {
			
			RelativeLayout frame = (RelativeLayout) parent;
			
			if(socializeActivityView == null) {
				socializeActivityView = socializeActivityViewFactory.getBean();

				RelativeLayout.LayoutParams barParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				barParams.addRule(RelativeLayout.ABOVE, view.getId());
				
				socializeActivityView.setLayoutParams(barParams);
				socializeActivityView.setVisibility(View.GONE);
				
				// Position in front of original, but behind actionbar
				int childCount = frame.getChildCount();
				
				for (int i = 0; i < childCount; i++) {
					View child = frame.getChildAt(i);
					
					if(child == view) {
						frame.addView(socializeActivityView, i);
						break;
					}
				}
			}
		}	
		
		return socializeActivityView;
	}

	public void setSocializeActivityViewFactory(IBeanFactory<SocializeActivityView> socializeAdViewFactory) {
		this.socializeActivityViewFactory = socializeAdViewFactory;
	}
}
