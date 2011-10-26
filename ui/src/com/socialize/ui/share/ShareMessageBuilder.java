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
package com.socialize.ui.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.socialize.entity.User;
import com.socialize.ui.SocializeUI;
import com.socialize.util.DeviceUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class ShareMessageBuilder {
	
	private DeviceUtils deviceUtils;

	public ShareMessageBuilder() {
		super();
	}

	public String buildShareSubject(Activity context) {
		
		StringBuilder builder = new StringBuilder();
		
		User currentUser = getSocializeUI().getSocialize().getSession().getUser();
		
		Intent intent = context.getIntent();
		Bundle extras = intent.getExtras();
		
		String entityKey = null;
		String entityName = null;
		
		if(extras != null) {
			entityKey = extras.getString(SocializeUI.ENTITY_KEY);
			entityName = extras.getString(SocializeUI.ENTITY_NAME);
		}
		
		if(currentUser != null) {
			String name = currentUser.getDisplayName();
			if(StringUtils.isEmpty(name)) {
				builder.append("Sharing ");
			}
			else {
				builder.append(name);
				builder.append(" shared ");
			}
		}
		else {
			builder.append("Sharing ");
		}
		
		if(!StringUtils.isEmpty(entityName)) {
			builder.append(entityName);
		}
		else {
			builder.append(entityKey);
		}
		
		return builder.toString();
		
	}
	
	public String buildShareMessage(Activity context, String comment, boolean html, boolean includeSocialize) {
		
		StringBuilder builder = new StringBuilder();
		
		Intent intent = context.getIntent();
		Bundle extras = intent.getExtras();
		String entityKey = null;
		String entityName = null;
		String entityDescription = null;
		boolean isUseLink = false;
		
		if(extras != null) {
			entityKey = extras.getString(SocializeUI.ENTITY_KEY);
			entityName = extras.getString(SocializeUI.ENTITY_NAME);
			entityDescription = extras.getString(SocializeUI.ENTITY_DESCRIPTION);
			isUseLink = extras.getBoolean(SocializeUI.ENTITY_URL_AS_LINK);
		}
		
		if(StringUtils.isEmpty(comment)) {
			comment = "Check this out:";
		}
		
		builder.append(comment);

		builder.append(getNewLine(html));
		
		if(!StringUtils.isEmpty(entityKey)) {
			if(isUseLink && html) {
				builder.append("<a href=\"");
				builder.append(entityKey);
				builder.append("\">");
			}
			
			if(!StringUtils.isEmpty(entityName)) {
				builder.append(entityName);
			}
			else {
				builder.append(entityKey);
			}
			
			if(isUseLink && html) {
				builder.append("</a>");
			}
		}
		
		if(!StringUtils.isEmpty(entityDescription)) {
			builder.append(getNewLine(html));
			builder.append(entityDescription);
		}
		
		if(includeSocialize) {
			builder.append(getNewLine(html));
			builder.append(getNewLine(html));
			builder.append("Shared from ");
			
			if(html) {
				builder.append("<a href=\"");
				builder.append(deviceUtils.getMarketUrl(html));
				builder.append("\">");
				builder.append(deviceUtils.getAppName());
				builder.append("</a>");
			}
			else {
				builder.append(deviceUtils.getAppName());
			}
			
			builder.append(" using ");
			
			if(html) {
				builder.append("<a href=\"http://www.getsocialize.com\">Socialize for Android</a>.  ");
			}
			else {
				builder.append("Socialize for Android. http://www.getsocialize.com");
			}
		}
		
		return builder.toString();
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}
	
	protected SocializeUI getSocializeUI() {
		return SocializeUI.getInstance();
	}
	
	protected String getNewLine(boolean html) {
		if(html) {
			return "<br/>";
		}
		else {
			return "\n";
		}
	}

}
