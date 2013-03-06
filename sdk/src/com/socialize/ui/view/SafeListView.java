package com.socialize.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
import com.socialize.log.SocializeLogger;


public class SafeListView extends ListView {

	public SafeListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SafeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SafeListView(Context context) {
		super(context);
	}

	@Override
	protected void layoutChildren() {
		try {
			super.layoutChildren();
		}
		catch (Exception e) {
			// Don't crash
			SocializeLogger.w(e.getMessage(), e);
		}
	}
}
