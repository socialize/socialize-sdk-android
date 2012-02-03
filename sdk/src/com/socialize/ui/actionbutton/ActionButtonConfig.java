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
package com.socialize.ui.actionbutton;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.socialize.networks.SocialNetwork;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class ActionButtonConfig {
	
	private static final String androidns="http://schemas.android.com/apk/res/android";
	private static final String socializens="http://getsocialize.com";
	
	private int imageResIdOn;
	private int imageResIdOff;
	private int imageResIdDisabled;
	private int backgroundResId;
	private int backgroundColor;
	
	private int textColor = Color.WHITE;
	private float textSize = -1;
	
	private Drawable imageOn;
	private Drawable imageOff;
	private Drawable imageDisabled;
	private Drawable background;
	
	private String textOn;
	private String textOff;
	private String textLoading = "...";
	
	private String actionType;
	private String entityKey;
	private String entityName;
	private String buttonId;
	private String shareTo;
	
	private boolean shareLocation;
	private boolean showCount = true;
	private boolean autoAuth;
	
	private SocialNetwork[] shareToNetworks;
	
	public ActionButtonConfig() {
		super();
	}
	
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	public String getEntityKey() {
		return entityKey;
	}
	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getButtonId() {
		return buttonId;
	}
	public void setButtonId(String buttonId) {
		this.buttonId = buttonId;
	}
	public String getShareTo() {
		return shareTo;
	}
	public void setShareTo(String shareTo) {
		this.shareTo = shareTo;
	}
	public boolean isShareLocation() {
		return shareLocation;
	}
	public void setShareLocation(boolean shareLocation) {
		this.shareLocation = shareLocation;
	}
	
	public SocialNetwork[] getShareToNetworks() {
		return shareToNetworks;
	}
	
	public void setShareToNetworks(SocialNetwork...shareToNetworks) {
		this.shareToNetworks = shareToNetworks;
	}
	
	public int getImageResIdOn() {
		return imageResIdOn;
	}
	public void setImageResIdOn(int imageResIdOn) {
		this.imageResIdOn = imageResIdOn;
	}
	public int getImageResIdOff() {
		return imageResIdOff;
	}
	public void setImageResIdOff(int imageResIdOff) {
		this.imageResIdOff = imageResIdOff;
	}
	public int getImageResIdDisabled() {
		return imageResIdDisabled;
	}
	public void setImageResIdDisabled(int imageResIdDisabled) {
		this.imageResIdDisabled = imageResIdDisabled;
	}
	
	public int getBackgroundResId() {
		return backgroundResId;
	}
	public void setBackgroundResId(int backgroundResId) {
		this.backgroundResId = backgroundResId;
	}
	public String getTextOn() {
		return textOn;
	}
	public void setTextOn(String textOn) {
		this.textOn = textOn;
	}
	public String getTextOff() {
		return textOff;
	}
	public void setTextOff(String textOff) {
		this.textOff = textOff;
	}
	
	public boolean isAutoAuth() {
		return autoAuth;
	}
	public void setAutoAuth(boolean autoAuth) {
		this.autoAuth = autoAuth;
	}
	
	public int getBackgroundColor() {
		return backgroundColor;
	}
	
	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public Drawable getImageOn() {
		return imageOn;
	}
	
	public void setImageOn(Drawable imageOn) {
		this.imageOn = imageOn;
	}
	
	public Drawable getImageOff() {
		return imageOff;
	}
	
	public void setImageOff(Drawable imageOff) {
		this.imageOff = imageOff;
	}
	
	public Drawable getImageDisabled() {
		return imageDisabled;
	}
	
	public void setImageDisabled(Drawable imageDisabled) {
		this.imageDisabled = imageDisabled;
	}
	
	public Drawable getBackground() {
		return background;
	}
	
	public void setBackground(Drawable background) {
		this.background = background;
	}
	
	public String getTextLoading() {
		return textLoading;
	}
	
	public void setTextLoading(String textLoading) {
		this.textLoading = textLoading;
	}
	
	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}
	
	public boolean isShowCount() {
		return showCount;
	}

	public void setShowCount(boolean showCount) {
		this.showCount = showCount;
	}

	public void build(Context context, AttributeSet attrs) {
		buttonId = attrs.getAttributeValue(androidns, "id");
		actionType = attrs.getAttributeValue(socializens, "action_type");
		entityKey = attrs.getAttributeValue(socializens, "entity_key");
		entityName = attrs.getAttributeValue(socializens, "entity_name");
		shareTo = attrs.getAttributeValue(socializens, "share_to");
		shareLocation = attrs.getAttributeBooleanValue(socializens, "share_location", true);
		autoAuth = attrs.getAttributeBooleanValue(socializens, "auto_auth", false);
		showCount = attrs.getAttributeBooleanValue(socializens, "show_count", true);
		
		String strTextSize = attrs.getAttributeValue(androidns, "textSize");
		
		if(!StringUtils.isEmpty(strTextSize)) {
			
			Resources r = context.getResources();
			DisplayMetrics displayMetrics = r.getDisplayMetrics();
			
			if(strTextSize.endsWith("sp")) {
				float val = Float.parseFloat(strTextSize.substring(0, strTextSize.length() - 2));
				textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, val, displayMetrics);
			}
			else if(strTextSize.endsWith("pt")) {
				float val = Float.parseFloat(strTextSize.substring(0, strTextSize.length() - 2));
				textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, val, displayMetrics);
			}
			else if(strTextSize.endsWith("px")) {
				float val = Float.parseFloat(strTextSize.substring(0, strTextSize.length() - 2));
				textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, val, displayMetrics);
			}
			else if(strTextSize.endsWith("dp")) {
				float val = Float.parseFloat(strTextSize.substring(0, strTextSize.length() - 2));
				textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, displayMetrics);
			}
			else if(strTextSize.endsWith("dip")) {
				float val = Float.parseFloat(strTextSize.substring(0, strTextSize.length() - 3));
				textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, displayMetrics);
			}			
		}
		
		imageResIdOn = attrs.getAttributeResourceValue(socializens, "src_active", 0);
		imageResIdOff = attrs.getAttributeResourceValue(socializens, "src_inactive", 0);
		imageResIdDisabled = attrs.getAttributeResourceValue(socializens, "src_disabled", 0);
		backgroundResId = attrs.getAttributeResourceValue(androidns, "background", 0);
		
		int textColorResId = attrs.getAttributeResourceValue(androidns, "textColor", 0);
		int textOnResId = attrs.getAttributeResourceValue(socializens, "text_active", 0);
		int textOffResId = attrs.getAttributeResourceValue(socializens, "text_active", 0);
		int textLoadingResId = attrs.getAttributeResourceValue(socializens, "text_loading", 0);
		
		if(textColorResId > 0) {
			try {
				textColor = context.getResources().getColor(textOnResId);
			} 
			catch (Exception e) {
				// No such resource
				e.printStackTrace();
			}
		}
		else {
			String strColor = attrs.getAttributeValue(androidns, "textColor");
			if(!StringUtils.isEmpty(strColor)) {
				textColor = Color.parseColor(strColor);
			}
		}
		
		if(textOnResId > 0) {
			try {
				textOn = context.getResources().getString(textOnResId);
			} 
			catch (Exception e) {
				// No such resource
				e.printStackTrace();
			}
		}
		
		if(textOffResId > 0) {
			try {
				textOff = context.getResources().getString(textOffResId);
			} 
			catch (Exception e) {
				// No such resource
				e.printStackTrace();
			}
		}
		
		if(textLoadingResId > 0) {
			try {
				textLoading = context.getResources().getString(textLoadingResId);
			} 
			catch (Exception e) {
				// No such resource
				e.printStackTrace();
			}
		}
		
		if(backgroundResId <= 0) {
			String color = attrs.getAttributeValue(androidns, "background");
			try {
				backgroundColor = Color.parseColor(color);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(textOn == null) {
			textOn = attrs.getAttributeValue(socializens, "text_active");
		}
		
		if(textOff == null) {
			textOff = attrs.getAttributeValue(socializens, "text_inactive");
		}
		
		if(shareTo != null) {
			String[] split = shareTo.split("\\s*,\\s*");
			ArrayList<SocialNetwork> networks = new ArrayList<SocialNetwork>(split.length);
			
			for (String network : split) {
				try {
					SocialNetwork sn = SocialNetwork.valueOf(network.trim().toUpperCase());
					networks.add(sn);
				} 
				catch (Exception ignore) {
					ignore.printStackTrace();
				}
			}
			
			if(!networks.isEmpty()) {
				shareToNetworks = networks.toArray(new SocialNetwork[networks.size()]);
			}
		}
	}
	
	public static ActionButtonConfig getDefault() {
		ActionButtonConfig config = new ActionButtonConfig();
		config.setActionType("like");
		config.setAutoAuth(false);
		config.setShareLocation(false);
		config.setTextOn("Unlike");
		config.setTextOff("Like");
		return config;
	}
}
