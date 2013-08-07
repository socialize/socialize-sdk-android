/*
 * Copyright (c) 2012 Socialize Inc.
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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.location.Address;
import android.net.Uri;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.profile.activity.UserActivityListItem;
import com.socialize.ui.profile.activity.UserActivityView;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.CompatUtils;
import com.socialize.ui.util.DateUtils;
import com.socialize.ui.util.GeoUtils;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

import java.util.Date;

/**
 * @author Jason Polites
 */
public class ActionDetailContentView extends BaseView {

	private ImageView profilePicture;
	private TextView displayName;
	private UserActivityListItem actionView;
	private TextView actionLocation;
	private GeoUtils geoUtils;
	private DateUtils dateUtils;
	private DisplayUtils displayUtils;
	private Colors colors;
	private Drawables drawables;
	private LinearLayout actionLocationLine;
	private TextView divider;
	
	private IBeanFactory<UserActivityListItem> userActivityListItemFactory;
	
	private LinearLayout headerView;
	
	private SocializeLogger logger;
	
	private IBeanFactory<UserActivityView> userActivityViewFactory;
	private UserActivityView userActivityView;
	
	public ActionDetailContentView(Context context) {
		super(context);
	}
	
	public void init() {
		
		final int imagePadding = displayUtils.getDIP(4);
		final int margin = displayUtils.getDIP(8);
		final int actionMargin = displayUtils.getDIP(4);
		final int imageSize = displayUtils.getDIP(64);
		final int editTextStroke = displayUtils.getDIP(2);
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
		LayoutParams actionLocationLineLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams actionLocationLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		LayoutParams actionLocationPinLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		masterLayout.setMargins(0, 0, 0, 0);
		imageLayout.setMargins(margin, margin, margin, margin);
		displayNameTextLayoutParams.setMargins(0, margin, margin, margin);
		
		displayNameTextLayoutParams.weight = 1.0f;
		
		actionLocationLayout.gravity = Gravity.LEFT;
		actionLocationPinLayout.gravity = Gravity.CENTER_VERTICAL;
		
		profilePicture = new ImageView(getContext());
		displayName = new TextView(getContext());
		actionView = userActivityListItemFactory.getBean();
		actionLocation = new TextView(getContext());
		
		ImageView locationPin = new ImageView(getContext());
		locationPin.setImageDrawable(drawables.getDrawable("icon_location_pin.png"));
		
		actionLocationLine = new LinearLayout(getContext());
		actionLocationLine.addView(locationPin);
		actionLocationLine.addView(actionLocation);
		
		actionLocationLine.setLayoutParams(actionLocationLineLayout);
		actionLocation.setLayoutParams(actionLocationLayout);
		locationPin.setLayoutParams(actionLocationPinLayout);

		actionLocation.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		actionLocation.setTextColor(Color.WHITE);
		
		
		ColorDrawable actionbg = new ColorDrawable(Color.WHITE);
		actionbg.setAlpha(32);
		
		actionView.setBackground(actionbg);
		actionView.setVisibility(View.GONE);
		actionLocationLine.setVisibility(View.INVISIBLE);
		
		LayoutParams actionWebViewLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		actionWebViewLayout.setMargins(margin, actionMargin, margin, actionMargin);
		actionView.setLayoutParams(actionWebViewLayout);
		
		GradientDrawable imageBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] {Color.WHITE, Color.WHITE});
		imageBG.setStroke(2, Color.BLACK);
		imageBG.setAlpha(64);
		
		profilePicture.setLayoutParams(imageLayout);
		profilePicture.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);

		CompatUtils.setBackgroundDrawable(profilePicture, imageBG);

		profilePicture.setScaleType(ScaleType.CENTER_CROP);
	
		displayName.setTextColor(titleColor);
		displayName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
		displayName.setMaxLines(1);
		displayName.setTypeface(Typeface.DEFAULT);
		displayName.setSingleLine();
		displayName.setLayoutParams(displayNameTextLayoutParams);
		
		GradientDrawable textBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int [] {colors.getColor(Colors.TEXT_BG), colors.getColor(Colors.TEXT_BG)});
		
		textBG.setStroke(editTextStroke, colors.getColor(Colors.TEXT_STROKE));
		textBG.setCornerRadius(editTextRadius);
		
		InputFilter[] maxLength = new InputFilter[1]; 
		maxLength[0] = new InputFilter.LengthFilter(128); 
		
		nameLayout.addView(displayName);
		nameLayout.addView(actionLocationLine);
		
		headerView.addView(profilePicture);
		headerView.addView(nameLayout);
		
		addView(headerView);
		addView(actionView);

		if(userActivityViewFactory != null) {
			
			divider = new TextView(getContext());

			CompatUtils.setBackgroundDrawable(divider, drawables.getDrawable("divider.png", true, false, true));

			LayoutParams dividerLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, displayUtils.getDIP(30));
			
			dividerLayout.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			dividerLayout.setMargins(0, margin, 0, 0);
			
			divider.setLayoutParams(dividerLayout);
			divider.setTextColor(Color.WHITE);
			divider.setText("Recent Activity");
			divider.setPadding(margin, 0, 0, 0);
			divider.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
			divider.setTypeface(Typeface.DEFAULT_BOLD);
			divider.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			
			addView(divider);
			
			LinearLayout activityHolder = new LinearLayout(getContext());
			
			LayoutParams userActivityLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
			LayoutParams activityHolderLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
			
			activityHolderLayout.setMargins(0, 0, 0, 0);
			
			userActivityLayout.setMargins(0, 0, 0, 0);
			
			activityHolder.setLayoutParams(activityHolderLayout);
			activityHolder.setPadding(margin, margin, margin, margin);
			
			int activityBg = colors.getColor(Colors.ACTIVITY_BG);
			GradientDrawable bg = makeGradient(activityBg, activityBg);
			bg.setAlpha(144);

			CompatUtils.setBackgroundDrawable(activityHolder, bg);

			activityHolderLayout.weight = 1.0f;
			
			userActivityView = userActivityViewFactory.getBean();
			userActivityView.setLayoutParams(userActivityLayout);
			
			activityHolder.addView(userActivityView);
			
			addView(activityHolder);
		}			
	}

	public ImageView getProfilePicture() {
		return profilePicture;
	}

	public TextView getDisplayName() {
		return displayName;
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
	
	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setUserActivityListItemFactory(IBeanFactory<UserActivityListItem> userActivityListItemFactory) {
		this.userActivityListItemFactory = userActivityListItemFactory;
	}

	public void setUserActivityViewFactory(IBeanFactory<UserActivityView> userActivityViewFactory) {
		this.userActivityViewFactory = userActivityViewFactory;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void loadUserActivity(User user, SocializeAction current) {
		if(userActivityView != null) {
			userActivityView.loadUserActivity(user.getId(), current);
		}
	}
	
	protected GradientDrawable makeGradient(int bottom, int top) {
		return new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
				new int[] { bottom, top });
	}

	public UserActivityView getUserActivityView() {
		return userActivityView;
	}

	public void setAction(final SocializeAction action) {
		if(actionView != null) {
			actionView.setAction(getActivity(), action, new Date());
			actionView.setVisibility(VISIBLE);
		}
		
		User user = action.getUser();
		
		if(user != null) {
			divider.setText("Recent Activity for " + user.getDisplayName());
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
									SocializeLogger.w("Failed to load map view", e);
								}
							}
						}
					});
				}
			}
		}
	}
}
