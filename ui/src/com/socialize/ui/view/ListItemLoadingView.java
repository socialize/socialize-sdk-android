package com.socialize.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socialize.ui.BaseView;
import com.socialize.util.DeviceUtils;

/**
 * Renders a simple loading spinner.
 * @author jasonpolites
 *
 */
public class ListItemLoadingView extends BaseView {

	private DeviceUtils deviceUtils;
	
	public ListItemLoadingView(Context context) {
		super(context);
	}
	
	public void init() {
		final int eight = deviceUtils.getDIP(8);
		
		ProgressBar progress = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmallInverse);
		TextView text = new TextView(getContext());
		
		text.setTextColor(Color.BLACK);
		text.setText("Loading...");
		
		ListView.LayoutParams layout = new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, deviceUtils.getDIP(80));
		
		setBackgroundColor(Color.WHITE);
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(layout);
		setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		setPadding(eight,eight,eight,eight);
		
		addView(progress);
		addView(text);
	}

	public DeviceUtils getDeviceUtils() {
		return deviceUtils;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

}
