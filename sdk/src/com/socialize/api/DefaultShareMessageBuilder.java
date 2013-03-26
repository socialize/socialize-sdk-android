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
package com.socialize.api;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.User;
import com.socialize.i18n.I18NConstants;
import com.socialize.i18n.LocalizationService;
import com.socialize.util.AppUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class DefaultShareMessageBuilder implements ShareMessageBuilder {
	
	private AppUtils appUtils;
	private LocalizationService localizationService;

	public DefaultShareMessageBuilder() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.ShareMessageBuilder#buildShareSubject(com.socialize.entity.Entity)
	 */
	@Override
	public String buildShareSubject(Entity entity) {
		
		String entityKey = entity.getKey();
		String entityName = entity.getName();

		StringBuilder builder = new StringBuilder();
		SocializeSession session = getSocialize().getSession();
		
		User currentUser = null;
		
		if(session != null) {
			currentUser = session.getUser();
		}

		String sharingText = localizationService.getString(I18NConstants.SHARE_TEXT_SHARING);
		String sharedText = localizationService.getString(I18NConstants.SHARE_TEXT_SHARED);
		
		if(currentUser != null) {
			String name = currentUser.getDisplayName();
			if(StringUtils.isEmpty(name)) {
				builder.append(sharingText);
				builder.append(" ");
			}
			else {
				builder.append(name);
				builder.append(" ");
				builder.append(sharedText);
				builder.append(" ");
			}
		}
		else {
			builder.append(sharingText);
			builder.append(" ");
		}
		
		if(!StringUtils.isEmpty(entityName)) {
			builder.append(entityName);
		}
		else {
			builder.append(entityKey);
		}
		
		return builder.toString();
	}
	
	// So we can mock.
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	@Override
	public String getEntityLink(Entity entity, PropagationInfo urlSet, boolean html) {
		String entityName = entity.getName();
		
		StringBuilder builder = new StringBuilder();
		
		if(html) {
			builder.append("<a href=\"");
			builder.append(urlSet.getEntityUrl());
			builder.append("\">");
		}
		
		if(!StringUtils.isEmpty(entityName)) {
			builder.append(entityName);
		}
		
		if(html) {
			builder.append("</a>");
		}
		else {
			builder.append(": ");
			builder.append(urlSet.getEntityUrl());
		}
		
		return builder.toString();
	}
	
	@Override
	public String buildShareMessage(Entity entity, PropagationInfo urlSet, String comment, boolean html, boolean includeAppLink) {
	
		StringBuilder builder = new StringBuilder();
		
		if(!StringUtils.isEmpty(comment)) {
			builder.append(comment);
			builder.append(getNewLine(html));
			builder.append(getNewLine(html));
		}
		else {
			builder.append(getNewLine(html));
			builder.append(getNewLine(html));
		}
		
		builder.append(getEntityLink(entity, urlSet, html));

		String sentFromText = localizationService.getString(I18NConstants.SHARE_TEXT_SENT_FROM);
		
		if(includeAppLink) {
			builder.append(getNewLine(html));
			builder.append(getNewLine(html));
			builder.append(sentFromText);
			builder.append(" ");
			
			if(html) {
				builder.append("<a href=\"");
				builder.append(urlSet.getAppUrl());
				builder.append("\">");
				builder.append(appUtils.getAppName());
				builder.append("</a>");
			}
			else {
				builder.append(appUtils.getAppName());
				builder.append(" ");
				builder.append(urlSet.getAppUrl());
			}
		}
		
		return builder.toString();
	}

	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}

	protected String getNewLine(boolean html) {
		if(html) {
			return "<br/>";
		}
		else {
			return "\n";
		}
	}

	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}
}
