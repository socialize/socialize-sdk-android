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
import android.webkit.WebView;

import com.socialize.Socialize;
import com.socialize.entity.SocializeAction;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.ui.util.Colors;

/**
 * @author Jason Polites
 *
 */
public class UserActivityActionHtml extends WebView implements UserActivityAction {
	
	private Colors colors;
	private String fontColor;
	private String linkColor;
	private int contentFontSize = 12;
	private int titleFontSize = 11;
	private UserActivityUtils userActivityUtils;
	
	public UserActivityActionHtml(Context context) {
		super(context);
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.profile.activity.UserActivityAction#init()
	 */
	@Override
	public void init() {
		getSettings().setJavaScriptEnabled(true);
		setBackgroundColor(0x00000000);
		fontColor = colors.getHexColor(Colors.BODY);
		linkColor = colors.getHexColor(Colors.SOCIALIZE_BLUE);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.ui.profile.activity.UserActivityAction#setAction(com.socialize.entity.SocializeAction)
	 */
	@Override
	public void setAction(SocializeAction action) {
		String html = makeActionText(action);
		loadDataWithBaseURL("", html, "text/html", "utf-8", null);
	}
	
	protected String makeActionText(final SocializeAction action) {
		
		StringBuilder builder = new StringBuilder();
		
		final SocializeEntityLoader entityLoader = Socialize.getSocialize().getEntityLoader();
		
		builder.append("<html><head><style>body { -webkit-tap-highlight-color: rgba(255, 255, 255, 0); background:transparent; margin:0px; padding:0; font:helvetica,arial,sans-serif;}</style></head>");
		
		if(entityLoader != null) {
			addJavascriptInterface(new Object() {
				@SuppressWarnings("unused")
				public void loadEntity() {
					entityLoader.loadEntity((Activity)getContext(), action.getEntity());
				}
			}, "socialize");
			
			builder.append("<body onclick='window.socialize.loadEntity();'>");
		}
		else {
			builder.append("<body>");
		}

		builder.append(userActivityUtils.makeActionHtml(action, titleFontSize, contentFontSize, fontColor, linkColor));
		builder.append("</body></html>");
		
		return builder.toString();
	}
	

	/* (non-Javadoc)
	 * @see com.socialize.ui.profile.activity.UserActivityAction#setContentFontSize(int)
	 */
	@Override
	public void setContentFontSize(int contentFontSize) {
		this.contentFontSize = contentFontSize;
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.profile.activity.UserActivityAction#setTitleFontSize(int)
	 */
	@Override
	public void setTitleFontSize(int titleFontSize) {
		this.titleFontSize = titleFontSize;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public void setUserActivityUtils(UserActivityUtils userActivityUtils) {
		this.userActivityUtils = userActivityUtils;
	}
}
