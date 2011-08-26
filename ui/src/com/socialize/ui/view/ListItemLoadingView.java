package com.socialize.ui.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.socialize.ui.BaseView;
import com.socialize.ui.util.Colors;
import com.socialize.util.DeviceUtils;

/**
 * Renders a simple loading spinner.
 * @author jasonpolites
 *
 */
public class ListItemLoadingView extends BaseView {

	private DeviceUtils deviceUtils;
	private Colors colors;
	
	public ListItemLoadingView(Context context) {
		super(context);
	}
	
	public void init() {
		final int eight = deviceUtils.getDIP(8);
		
		ProgressBar progress = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
		TextView text = new TextView(getContext());
		
		progress.setPadding(eight, eight, 0, eight);
		
		text.setTextColor(colors.getColor(Colors.BODY));
		text.setText("Loading...");
		text.setPadding(eight, eight, eight, eight);
		
		ListView.LayoutParams layout = new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT);
		
		setBackgroundColor(colors.getColor(Colors.LOADING_ITEM_BG));
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

	public Colors getColors() {
		return colors;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}
}
