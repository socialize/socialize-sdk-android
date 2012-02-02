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
import android.graphics.Color;
import android.util.AttributeSet;

import com.socialize.networks.SocialNetwork;

/**
 * @author Jason Polites
 *
 */
public class ActionButtonConfig {
	
	public static final String androidns="http://schemas.android.com/apk/res/android";
	public static final String socializens="http://getsocialize.com";
	
	private int imageResIdOn;
	private int imageResIdOff;
	private int imageResIdDisabled;
	private int backgroundResId;
	private int backgroundColor;
	
	private String textOn;
	private String textOff;
	
	private String actionType;
	private String entityKey;
	private String entityName;
	private String buttonId;
	private String shareTo;
	
	private boolean shareLocation;
	private boolean autoAuth;
	
	private SocialNetwork[] shareToNetworks;
	
//	public int getLayoutWidth() {
//		return layoutWidth;
//	}
//	public void setLayoutWidth(int layoutWidth) {
//		this.layoutWidth = layoutWidth;
//	}
	
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
	
	public void build(Context context, AttributeSet attrs) {
		buttonId = attrs.getAttributeValue(androidns, "id");
		actionType = attrs.getAttributeValue(socializens, "action_type");
		entityKey = attrs.getAttributeValue(socializens, "entity_key");
		entityName = attrs.getAttributeValue(socializens, "entity_name");
		shareTo = attrs.getAttributeValue(socializens, "share_to");
		shareLocation = attrs.getAttributeBooleanValue(socializens, "share_location", true);
		autoAuth = attrs.getAttributeBooleanValue(socializens, "auto_auth", false);
		
		imageResIdOn = attrs.getAttributeResourceValue(socializens, "src_active", 0);
		imageResIdOff = attrs.getAttributeResourceValue(socializens, "src_inactive", 0);
		imageResIdDisabled = attrs.getAttributeResourceValue(socializens, "src_disabled", 0);
		backgroundResId = attrs.getAttributeResourceValue(androidns, "background", 0);
		
		int textOnResId = attrs.getAttributeResourceValue(socializens, "text_active", 0);
		int textOffResId = attrs.getAttributeResourceValue(socializens, "text_active", 0);
		
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
}
