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
package com.socialize.api;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.util.AppUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class DefaultShareMessageBuilder implements ShareMessageBuilder {
	
	private AppUtils appUtils;
	private SocializeConfig config;

	public DefaultShareMessageBuilder() {
		super();
	}
	

	/* (non-Javadoc)
	 * @see com.socialize.api.ShareMessageBuilder#buildShareLink(com.socialize.entity.Entity)
	 */
	@Override
	public String buildShareLink(Entity entity) {
		Long id = entity.getId();
		if(id != null) {
			if(config != null) {
				String host = config.getProperty(SocializeConfig.REDIRECT_HOST);
				if(!StringUtils.isEmpty(host)) {
					return host + "/e/" + id;
				}
			}
			return "http://r.getsocialize.com/e/" + id;
		}
		else {
			return entity.getKey();
		}
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
	
	// So we can mock.
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	@Deprecated
	public String buildShareSubject(String entityKey, String entityName) {
		return buildShareSubject(Entity.newInstance(entityKey, entityName));
	}
	
	@Deprecated
	public String buildShareMessage(String entityKey, String entityName, String comment, boolean html, boolean includeSocialize) {
		return buildShareMessage(Entity.newInstance(entityKey, entityName), comment, html, includeSocialize);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ShareMessageBuilder#getEntityLink(com.socialize.entity.Entity, boolean)
	 */
	@Override
	public String getEntityLink(Entity entity, boolean html) {
		String entityKey = entity.getKey();
		String entityName = entity.getName();
		
		StringBuilder builder = new StringBuilder();
		
		if(!StringUtils.isEmpty(entityKey)) {
			if(html) {
				builder.append("<a href=\"");
				builder.append(buildShareLink(entity));
				builder.append("\">");
			}
			
			if(!StringUtils.isEmpty(entityName)) {
				builder.append(entityName);
			}
			
			if(!html) {
				builder.append(": ");
			}
			else {
				builder.append(" ");
			}
			
			builder.append(buildShareLink(entity));
			
			if(html) {
				builder.append("</a>");
			}
		}
		
		return builder.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.ShareMessageBuilder#buildShareMessage(com.socialize.entity.Entity, java.lang.String, boolean, boolean)
	 */
	@Override
	public String buildShareMessage(Entity entity, String comment, boolean html, boolean includeSocialize) {
		
		StringBuilder builder = new StringBuilder();
		
		if(!StringUtils.isEmpty(comment)) {
			builder.append(comment);
			builder.append(getNewLine(html));
			builder.append(getNewLine(html));
		}
		
		builder.append(getEntityLink(entity, html));
		
		if(includeSocialize) {
			builder.append(getNewLine(html));
			builder.append(getNewLine(html));
			builder.append("Shared from ");
			
			if(html) {
				builder.append("<a href=\"");
				builder.append(appUtils.getAppUrl());
				builder.append("\">");
				builder.append(appUtils.getAppName());
				builder.append("</a>");
			}
			else {
				builder.append(appUtils.getAppName());
			}
			
			if(config.isBrandingEnabled()) {
				builder.append(" using ");
				
				if(html) {
					builder.append("<a href=\"http://www.getsocialize.com\">Socialize for Android</a>.");
				}
				else {
					builder.append("Socialize for Android. http://www.getsocialize.com");
				}
			}
		}
		
		return builder.toString();
	}
	
	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
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
