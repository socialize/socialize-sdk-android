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

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebView;

import com.socialize.api.action.ActionType;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.util.Colors;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class UserActivityActionText extends WebView {
	
	private Colors colors;
	private String fontColor;
	private int contentFontSize = 12;
	private int titleFontSize = 11;
	
	public UserActivityActionText(Context context) {
		super(context);
	}

	public void init() {
		getSettings().setJavaScriptEnabled(true);
		setBackgroundColor(0x00000000);
		fontColor = colors.getHexColor(Colors.BODY);
	}
	
	public void setAction(SocializeAction action) {
		String html = makeActionText(action);
		loadDataWithBaseURL("", html, "text/html", "utf-8", null);
	}
	
	protected String makeActionText(final SocializeAction action) {
		
		StringBuilder builder = new StringBuilder();
		
		final SocializeEntityLoader entityLoader = SocializeUI.getInstance().getEntityLoader();
		
		String name = "";
		
		User user = action.getUser();
		
		if(user != null) {
			name = user.getFirstName();
			
			if(StringUtils.isEmpty(name)) {
				name = user.getDisplayName();
			}
			if(name == null) {
				name = "";
			}
		}		
		
		builder.append("<html><head><style>body { -webkit-tap-highlight-color: rgba(255, 255, 255, 0); background:transparent; margin:0px; padding:0; font:helvetica,arial,sans-serif;}</style></head><body>");
		builder.append("<span style='font:");
		builder.append(titleFontSize);
		builder.append("px helvetica,arial,sans-serif;color:");
		builder.append(fontColor);
		builder.append("'>");
		builder.append(name);
		builder.append(" ");
		
		String actionText = null;
		
		// Do the intro text
		switch(action.getActionType()) {
			case COMMENT:
				builder.append("commented on ");
				
				actionText = StringUtils.replaceNewLines(action.getDisplayText(), 3, 2);
				String html = actionText;
				
				html = TextUtils.htmlEncode(html);
				html = actionText.replaceAll("\n", "<br/>");				
				
				actionText = html;
				
				break;
			case LIKE:
				builder.append("liked ");
				break;
			case SHARE:
				actionText = action.getDisplayText();
				builder.append("shared ");
				break;
		}	
		
		String entityName = StringUtils.ellipsis(action.getEntityDisplayName(), 30);
		
		if(entityLoader != null) {
			builder.append("<a style='font:");
			builder.append(titleFontSize);
			builder.append("px helvetica,arial,sans-serif;font-weight:bold;text-decoration:none;color:");
			builder.append(colors.getHexColor(Colors.SOCIALIZE_BLUE));
			builder.append("' ");
			builder.append("href='javascript:void(window.socialize.loadEntity())'>");
			builder.append(entityName);
			builder.append("</a>");
			
			addJavascriptInterface(new Object() {
				@SuppressWarnings("unused")
				public void loadEntity() {
					entityLoader.loadEntity((Activity)getContext(), action.getEntity());
				}
			}, "socialize");
		}
		else {
			builder.append(entityName);
		}
		
		if(actionText != null) {
			
			builder.append("<br/><br/>");
			
			if(action.getActionType().equals(ActionType.COMMENT)) {
				builder.append("<span style='font:");
				builder.append(contentFontSize);
				builder.append("px times new roman,helvetica,arial;font-weight:normal;font-style:italic;color:");
				builder.append(fontColor);
				builder.append("'>&quot;");
				builder.append(actionText);
				builder.append("&quot;</span>");
			}
			else {
				builder.append(actionText);
			}
		}
		
		builder.append("</span>");
		builder.append("</body></html>");
		
		return builder.toString();
	}
	
	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}

	public void setContentFontSize(int contentFontSize) {
		this.contentFontSize = contentFontSize;
	}

	public void setTitleFontSize(int titleFontSize) {
		this.titleFontSize = titleFontSize;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}
}
