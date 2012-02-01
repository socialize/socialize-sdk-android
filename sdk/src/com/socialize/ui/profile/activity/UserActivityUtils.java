package com.socialize.ui.profile.activity;

import android.content.Context;
import android.text.TextUtils;

import com.socialize.Socialize;
import com.socialize.api.action.ActionType;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.util.StringUtils;

public class UserActivityUtils {

	public String makeActionHtml(Context context, final SocializeAction action, int titleFontSize, int contentFontSize, String fontColorHex, String linkColorHex) {
		
		StringBuilder builder = new StringBuilder();
		
		final SocializeEntityLoader entityLoader = Socialize.getSocialize().getEntityLoader();
		
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

		builder.append("<span style='font:");
		builder.append(titleFontSize);
		builder.append("px helvetica,arial,sans-serif;color:");
		builder.append(fontColorHex);
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
		
		if(entityLoader != null && entityLoader.canLoad(context, action.getEntity())) {
			builder.append("<span style='font:");
			builder.append(titleFontSize);
			builder.append("px helvetica,arial,sans-serif;font-weight:bold;text-decoration:none;color:");
			builder.append(linkColorHex);
			builder.append("'>");
			builder.append(entityName);
			builder.append("</span>");
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
				builder.append(fontColorHex);
				builder.append("'>&quot;");
				builder.append(actionText);
				builder.append("&quot;</span>");
			}
			else {
				builder.append(actionText);
			}
		}
		
		builder.append("</span>");
		
		return builder.toString();
	}
}
