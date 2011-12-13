/*
 * Copyright (c) 2011 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.ui.action;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.location.Address;
import android.net.Uri;
import android.text.InputFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Comment;
import com.socialize.entity.User;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.html.HtmlFormatter;
import com.socialize.ui.profile.activity.UserActivityView;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.DateUtils;
import com.socialize.ui.util.GeoUtils;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 */
public class ActionDetailContentView extends BaseView {

	private ImageView profilePicture;
	private TextView displayName;
	private WebView actionView;
	private TextView actionTimestamp;
	private TextView actionLocation;
	private GeoUtils geoUtils;
	private DateUtils dateUtils;
	private DeviceUtils deviceUtils;
	private Colors colors;
	private Drawables drawables;
	private HtmlFormatter htmlFormatter;
	private LinearLayout actionLocationLine;
	
	private LinearLayout headerView;
	
	private SocializeLogger logger;
	
	private IBeanFactory<UserActivityView> userActivityViewFactory;
	private UserActivityView userActivityView;
	
	public ActionDetailContentView(Context context) {
		super(context);
	}
	
	public void init() {
		
		final int imagePadding = deviceUtils.getDIP(4);
		final int margin = deviceUtils.getDIP(8);
		final int actionMargin = deviceUtils.getDIP(4);
		final int imageSize = deviceUtils.getDIP(64);
		final int editTextStroke = deviceUtils.getDIP(2);
		final float editTextRadius = editTextStroke;
		final int titleColor = colors.getColor(Colors.TITLE);
		
		LayoutParams masterLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		
		this.setLayoutParams(masterLayout);
		this.setOrientation(LinearLayout.VERTICAL);
		this.setPadding(0, 0, 0, 0);
		this.setGravity(Gravity.TOP);
		
		headerView = new LinearLayout(getContext());
		LayoutParams masterLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		headerView.setLayoutParams(masterLayoutParams);
		headerView.setOrientation(LinearLayout.HORIZONTAL);
		headerView.setGravity(Gravity.TOP);
		
		LinearLayout nameLayout = new LinearLayout(getContext());
		LayoutParams nameLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		nameLayout.setLayoutParams(nameLayoutParams);
		nameLayout.setOrientation(LinearLayout.VERTICAL);
		nameLayout.setGravity(Gravity.TOP);
		
		LayoutParams imageLayout = new LinearLayout.LayoutParams(imageSize,imageSize);
		LayoutParams displayNameTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams actionTimestampLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams actionLocationLineLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams actionLocationLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams actionLocationPinLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
// Margins
		masterLayout.setMargins(0, 0, 0, 0);
		imageLayout.setMargins(margin, margin, margin, margin);
		actionTimestampLayout.setMargins(0, 0, margin, 0);
		displayNameTextLayoutParams.setMargins(0, margin, margin, margin);
		
		actionTimestampLayout.gravity = Gravity.RIGHT;
		actionLocationLayout.gravity = Gravity.LEFT;
		actionLocationPinLayout.gravity = Gravity.CENTER_VERTICAL;
		
		profilePicture = new ImageView(getContext());
		displayName = new TextView(getContext());
		actionView = new WebView(getContext());
		actionTimestamp = new TextView(getContext());
		actionLocation = new TextView(getContext());
		
		ImageView locationPin = new ImageView(getContext());
		locationPin.setImageDrawable(drawables.getDrawable("icon_location_pin.png"));
		
		actionLocationLine = new LinearLayout(getContext());
		actionLocationLine.setPadding(imagePadding, margin, imagePadding, 0);
		actionLocationLine.addView(locationPin);
		actionLocationLine.addView(actionLocation);
		
		actionLocationLine.setLayoutParams(actionLocationLineLayout);
		actionLocation.setLayoutParams(actionLocationLayout);
		locationPin.setLayoutParams(actionLocationPinLayout);

		actionTimestamp.setGravity(Gravity.RIGHT);
		actionTimestamp.setLayoutParams(actionTimestampLayout);
		actionTimestamp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		actionTimestamp.setTextColor(Color.WHITE);
		
		actionLocation.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		actionLocation.setTextColor(Color.WHITE);
		
		actionView.setVisibility(View.GONE);
		actionTimestamp.setVisibility(View.GONE);
		actionLocationLine.setVisibility(View.GONE);
		
		GradientDrawable actionBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { Color.WHITE, Color.WHITE});
		actionBG.setCornerRadius(4.0f);
		actionBG.setStroke(2, Color.DKGRAY);
		
		LayoutParams actionViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		LayoutParams actionWebViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		actionViewLayout.setMargins(margin, actionMargin, margin, actionMargin);
		LinearLayout actionLayoutView = new LinearLayout(getContext());
		
		actionLayoutView.setLayoutParams(actionViewLayout);
		actionLayoutView.setBackgroundDrawable(actionBG);
//		actionLayoutView.setPadding(margin, 0, margin, 0);
		
		actionView.setLayoutParams(actionWebViewLayout);
		actionView.setLongClickable(true);
		
		actionLayoutView.addView(actionView);
		
		GradientDrawable imageBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] {Color.WHITE, Color.WHITE});
		imageBG.setStroke(2, Color.BLACK);
		imageBG.setAlpha(64);
		
		profilePicture.setLayoutParams(imageLayout);
		profilePicture.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
		profilePicture.setBackgroundDrawable(imageBG);
		profilePicture.setScaleType(ScaleType.CENTER_CROP);
	
		displayName.setTextColor(titleColor);
		displayName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		displayName.setMaxLines(1);
		displayName.setTypeface(Typeface.DEFAULT);
		displayName.setTextColor(titleColor);
		displayName.setSingleLine();
		displayName.setLayoutParams(displayNameTextLayoutParams);
		
		GradientDrawable textBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int [] {colors.getColor(Colors.TEXT_BG), colors.getColor(Colors.TEXT_BG)});
		
		textBG.setStroke(editTextStroke, colors.getColor(Colors.TEXT_STROKE));
		textBG.setCornerRadius(editTextRadius);
		
		InputFilter[] maxLength = new InputFilter[1]; 
		maxLength[0] = new InputFilter.LengthFilter(128); 
		
		nameLayout.addView(displayName);
		
		headerView.addView(profilePicture);
		headerView.addView(nameLayout);
		
		this.addView(headerView);
		this.addView(actionLocationLine);
		this.addView(actionLayoutView);
		this.addView(actionTimestamp);
		
		if(userActivityViewFactory != null) {
			
			TextView divider = new TextView(getContext());
			divider.setBackgroundDrawable(drawables.getDrawable("divider.png", true, false, true));
			
			LayoutParams dividerLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, deviceUtils.getDIP(30));
			
			dividerLayout.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			dividerLayout.setMargins(0, margin, 0, 0);
			
			divider.setLayoutParams(dividerLayout);
			divider.setTextColor(Color.WHITE);
			divider.setText("Recent Activity");
			divider.setPadding(margin, 0, 0, 0);
			divider.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
			divider.setTypeface(Typeface.DEFAULT_BOLD);
			divider.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			
			this.addView(divider);
			
			LinearLayout activityHolder = new LinearLayout(getContext());
			
			LayoutParams userActivityLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
			LayoutParams activityHolderLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
			activityHolderLayout.weight = 1.0f;
			
			activityHolder.setLayoutParams(userActivityLayout);
			activityHolder.setBackgroundDrawable(drawables.getDrawable("crosshatch.png", true, true, true));	
			
			userActivityView = userActivityViewFactory.getBean();
			userActivityView.setLayoutParams(userActivityLayout);
			
			activityHolder.addView(userActivityView);
			
			this.addView(activityHolder);
		}			
	}

	public ImageView getProfilePicture() {
		return profilePicture;
	}

	public TextView getDisplayName() {
		return displayName;
	}

	public WebView getCommentView() {
		return actionView;
	}

	public TextView getCommentTimestamp() {
		return actionTimestamp;
	}

	public GeoUtils getGeoUtils() {
		return geoUtils;
	}

	public void setGeoUtils(GeoUtils geoUtils) {
		this.geoUtils = geoUtils;
	}

	public DateUtils getDateUtils() {
		return dateUtils;
	}

	public void setDateUtils(DateUtils dateUtils) {
		this.dateUtils = dateUtils;
	}
	
	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public View getHeaderView() {
		return headerView;
	}

	public void setHtmlFormatter(HtmlFormatter htmlFormatter) {
		this.htmlFormatter = htmlFormatter;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setUserActivityViewFactory(IBeanFactory<UserActivityView> userActivityViewFactory) {
		this.userActivityViewFactory = userActivityViewFactory;
	}
	
	
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void loadUserActivity(User user) {
		if(userActivityView != null) {
			userActivityView.loadUserActivity(user.getId());
		}
	}

	public void setComment(final Comment action) {
		if(actionView != null) {
			actionView.loadDataWithBaseURL("", htmlFormatter.format(action.getText()), "text/html", "utf-8", "");
			actionView.setVisibility(View.VISIBLE);
			actionView.setBackgroundColor(0x00000000);
		}
		
		if(actionTimestamp != null) {
			actionTimestamp.setVisibility(View.VISIBLE);
			
			String meta = "";
			if(action.getDate() != null) {
				Date actionDate = new Date(action.getDate());
				meta = dateUtils.getSimpleDateString(actionDate);
			}
			
			actionTimestamp.setText(meta);
		}
		
		if(actionLocation != null) {
			if(action.isLocationShared() && action.getLat() != null && action.getLon() != null) {
				Address address = geoUtils.geoCode(action.getLat(), action.getLon());
				if(address != null) {
					actionLocationLine.setVisibility(View.VISIBLE);
					actionLocation.setText(geoUtils.getSimpleLocation(address));
					
					actionLocation.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							try {
								String uri = "http://maps.google.com/?q=" + action.getLat() + "," + action.getLon() + "&z=17";
								Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
								Uri.parse(uri));
								getActivity().startActivity(intent);
							}
							catch (Exception e) {
								if(logger != null) {
									logger.warn("Failed to load map view", e);
								}
								else {
									Log.w("Socialize", "Failed to load map view", e);
								}
							}
						}
					});
				}
			}
		}
	}
}
