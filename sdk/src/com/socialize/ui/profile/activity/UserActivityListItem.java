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
package com.socialize.ui.profile.activity;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.SocializeAction;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.DateUtils;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 */
public class UserActivityListItem extends TableLayout {
	
	private TextView date;
	private ImageView icon;
	private ImageView locationIcon;
	private UserActivityAction actionText;
	private Drawable background;
	
	private DeviceUtils deviceUtils;
	private Colors colors;	
	private Drawables drawables;
	private DateUtils dateUtils;
	
	private int padding;
	private int bgColor;
	private int topColor;
	private int bottomColor;
	
	private int contentFontSize = 12;
	private int titleFontSize = 11;
	
	private IBeanFactory<UserActivityAction> userActivityActionHtmlFactory;
	private IBeanFactory<UserActivityAction> userActivityActionTextFactory;
	
	public UserActivityListItem(Context context) {
		super(context);
	}

	public UserActivityListItem(Context context, boolean singleLine) {
		super(context);
	}
	
	public void init() {
		padding = deviceUtils.getDIP(4);
		bgColor = colors.getColor(Colors.LIST_ITEM_BG);
		topColor = colors.getColor(Colors.LIST_ITEM_TOP);
		bottomColor = colors.getColor(Colors.LIST_ITEM_BOTTOM);
		
		int contentMargin = deviceUtils.getDIP(4);
		
		if(Socialize.getSocialize().getConfig().getBooleanProperty(SocializeConfig.SOCIALIZE_USE_ACTION_WEBVIEW, false)) {
			contentMargin = deviceUtils.getDIP(10);
		}
		
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.TOP);
		setPadding(padding,padding,padding,padding);
		
		// Make the middle column fill the width
		setColumnStretchable(2, true);
		
		// Make sure we don't overflow
		setColumnShrinkable(2, true);
		
		TableRow firstRow = new TableRow(getContext());
		TableRow secondRow = new TableRow(getContext());
		
		View location = createLocation();
		View icon = createIcon();
		View content = createTitle();
		View date = createDate();
		
		icon.setPadding(padding, padding, padding, padding);
		content.setPadding(padding, padding, padding, padding);
		date.setPadding(padding, padding, padding, padding);
		location.setPadding(padding, padding, padding, padding);
		
		TableRow.LayoutParams iconParams = new TableRow.LayoutParams();
		TableRow.LayoutParams locationIconParams = new TableRow.LayoutParams();
		TableRow.LayoutParams contentParams = new TableRow.LayoutParams();
		TableRow.LayoutParams dateParams = new TableRow.LayoutParams();
		
		iconParams.column = 1;
		iconParams.gravity = Gravity.TOP | Gravity.LEFT;
		
		contentParams.column = 2;
		contentParams.gravity = Gravity.TOP | Gravity.LEFT;
		contentParams.setMargins(0, contentMargin, 0, 0);
		
		locationIconParams.column = 3; 
		locationIconParams.gravity = Gravity.TOP | Gravity.RIGHT;
		
		dateParams.width = TableRow.LayoutParams.FILL_PARENT;
		dateParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		dateParams.span = 4;
		
		location.setLayoutParams(locationIconParams);
		icon.setLayoutParams(iconParams);
		content.setLayoutParams(contentParams);
		date.setLayoutParams(dateParams);
		
		firstRow.addView(icon);
		firstRow.addView(content);
		firstRow.addView(location);
		secondRow.addView(date);
		
		addView(firstRow);
		addView(secondRow);
	}
	
	protected View createTitle() {
		
		if(Socialize.getSocialize().getConfig().getBooleanProperty(SocializeConfig.SOCIALIZE_USE_ACTION_WEBVIEW, false)) {
			actionText = userActivityActionHtmlFactory.getBean();
		}
		else {
			actionText = userActivityActionTextFactory.getBean();
		}
		
		return (View) actionText;
	}
	
	protected View createIcon() {
		icon = new ImageView(getContext());
		return icon;
	}
	
	protected View createLocation() {
		locationIcon = new ImageView(getContext());
		locationIcon.setImageDrawable(drawables.getDrawable("icon_location_pin.png"));
		return locationIcon;
	}
	
	protected View createDate() {
		date = new TextView(getContext());
		date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		date.setTextColor(Color.LTGRAY);
		date.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
		return date;
	}
	
	public void setAction(Context context, final SocializeAction action, Date now) {
		
		if(background == null) {
			background = makeDefaultBackground();
		}
		
		setBackgroundDrawable(background);		
		
		actionText.setTitleFontSize(titleFontSize);
		actionText.setContentFontSize(contentFontSize);
		actionText.setAction(context, action);
		
		Long actionDate = action.getDate();
		
		if(actionDate != null && actionDate.longValue() > 0) {
			long diff = (now.getTime() - actionDate.longValue());
			date.setText(dateUtils.getTimeString(diff) + " ");
		}
		else {
			date.setText("");
		}	
		
		if(!action.isLocationShared()) {
			locationIcon.setVisibility(GONE);
		}
		
		switch(action.getActionType()) {
			case COMMENT:
				icon.setImageDrawable(drawables.getDrawable("icon_comment.png"));
				break;
			case LIKE:
				icon.setImageDrawable(drawables.getDrawable("icon_like_hi.png"));
				break;
			case SHARE:
				icon.setImageDrawable(drawables.getDrawable("icon_share.png"));
				break;
		}	
		
		final SocializeEntityLoader entityLoader = Socialize.getSocialize().getEntityLoader();
		
		if(entityLoader != null && entityLoader.canLoad(getContext(), action.getEntity())) {
			setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					entityLoader.loadEntity((Activity) getContext(), action.getEntity());
				}
			});
		}
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}
	
	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}
	
	public void setDateUtils(DateUtils dateUtils) {
		this.dateUtils = dateUtils;
	}

	public void setUserActivityActionHtmlFactory(IBeanFactory<UserActivityAction> userActivityActionHtmlFactory) {
		this.userActivityActionHtmlFactory = userActivityActionHtmlFactory;
	}

	public void setUserActivityActionTextFactory(IBeanFactory<UserActivityAction> userActivityActionTextFactory) {
		this.userActivityActionTextFactory = userActivityActionTextFactory;
	}

	public void setBackground(Drawable background) {
		this.background = background;
	}
	
	protected Drawable makeDefaultBackground() {
		// TODO: Make a singleton
		ColorDrawable shadow = makeColorDrawable(bottomColor);
		ColorDrawable highlight = makeColorDrawable(topColor);
		ColorDrawable surface = makeColorDrawable(bgColor);
		LayerDrawable layers = new LayerDrawable(new Drawable[] {shadow, highlight, surface});
		
		layers.setLayerInset(0, 0, 0, 0, 0);
		layers.setLayerInset(1, 1, 0, 0, 1);
		layers.setLayerInset(2, 1, 1, 1, 1);
		
		return layers;
	}
	
	public void setContentFontSize(int contentFontSize) {
		this.contentFontSize = contentFontSize;
	}

	public void setTitleFontSize(int titleFontSize) {
		this.titleFontSize = titleFontSize;
	}
	
	protected ColorDrawable makeColorDrawable(int color) {
		return new ColorDrawable(color);
	}	
}
