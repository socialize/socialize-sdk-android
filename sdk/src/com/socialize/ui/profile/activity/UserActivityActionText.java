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
package com.socialize.ui.profile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import com.socialize.Socialize;
import com.socialize.api.action.ActionType;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.SocializeEntityLoader;
import com.socialize.ui.util.Colors;
import com.socialize.util.DisplayUtils;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class UserActivityActionText extends TextView implements UserActivityAction {
	
	private Colors colors;
	private int contentFontSize = 12;
	private int titleFontSize = 11;
	private DisplayUtils displayUtils;
	private SocializeLogger logger;
	
	public UserActivityActionText(Context context) {
		super(context);
	}

	/* (non-Javadoc)
	 * @see com.socialize.ui.profile.activity.UserActivityAction#init()
	 */
	@Override
	public void init() {
		setBackgroundColor(0x00000000);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.ui.profile.activity.UserActivityAction#setAction(com.socialize.entity.SocializeAction)
	 */
	@Override
	public void setAction(Context context, final SocializeAction action) {
		try {
			doSetAction(action);
		} 
		catch (Exception e) {
			setText("Error!");
			
			if(logger != null) {
				logger.error("Error rendering action text", e);
			}
			else {
				SocializeLogger.e(e.getMessage(), e);
			}
		}
	}
	
	protected void doSetAction(final SocializeAction action) {

		int contentFontSizeDip = displayUtils.getDIP(contentFontSize);
		
		StringBuilder builder = new StringBuilder();
		
		boolean canLoad = false;
	
		final SocializeEntityLoader entityLoader = Socialize.getSocialize().getEntityLoader();
		if(entityLoader != null) {
			
			canLoad = entityLoader.canLoad(getContext(), action.getEntity());
			
			if(canLoad) {
				setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						entityLoader.loadEntity((Activity)getContext(), action.getEntity());
					}
				});
			}
		}
		
		setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleFontSize);
		
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
		
		builder.append(name);
		
		String actionText = null;
		
		// Do the intro text
		switch(action.getActionType()) {
			case COMMENT:
				builder.append(" commented on ");
				actionText = StringUtils.replaceNewLines(action.getDisplayText(), 3, 2);
				break;
			case LIKE:
				builder.append(" liked ");
				break;
			case SHARE:
				actionText = action.getDisplayText();
				builder.append(" shared ");
				break;
		}
		
		String textSoFar = builder.toString();
		
		String entityName = StringUtils.ellipsis(action.getEntityDisplayName(), 30);

		if(!StringUtils.isEmpty(entityName)) {
			entityName = StringUtils.encodeUtf8(entityName);
		}
		else {
			entityName = "";
		}

		int entityNameStartIndex = textSoFar.length();
		int entityNameEndIndex = entityNameStartIndex + entityName.length();
		int actionTextStartIndex = 0;
		int actionTextEndIndex = 0;
		
		builder.append(entityName);

		if(actionText != null) {
			
			actionText = StringUtils.encodeUtf8(actionText);
			
			builder.append("\n\n");
			
			actionTextStartIndex = entityNameEndIndex + 2; // Plus 2 for the new lines and the end of the entity name.
			
			if(action.getActionType().equals(ActionType.COMMENT)) {
				builder.append("\"");
				builder.append(actionText);
				builder.append("\"");
				
				actionTextEndIndex = actionTextStartIndex + actionText.length() + 2;  // Plus 2 for the quotes;
			}
			else {
				builder.append(actionText);
				actionTextEndIndex = actionTextStartIndex + actionText.length();
			}
		}

		Spannable spannable = new SpannableString(builder.toString()); 

		if(canLoad) {
			
			ColorStateList linkColor = new ColorStateList(
		            new int[][] {
		                new int[] { android.R.attr.state_pressed },
		                new int[0],
		            }, new int[] {
		                Color.DKGRAY,
		                colors.getColor(Colors.SOCIALIZE_BLUE)
		            });

			TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan("sans", Typeface.BOLD, contentFontSizeDip, linkColor, linkColor);
			spannable.setSpan(textAppearanceSpan, entityNameStartIndex, entityNameEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	
		if(actionText != null) {

			ColorStateList bodyColor = new ColorStateList(
		            new int[][] {
			                new int[] { android.R.attr.state_pressed },
			                new int[0],
			            }, new int[] {
			                Color.DKGRAY,
			                colors.getColor(Colors.BODY)
			            });	
			
			TextAppearanceSpan textAppearanceSpan = new TextAppearanceSpan("serif", Typeface.ITALIC, contentFontSizeDip, bodyColor, bodyColor);
			spannable.setSpan(textAppearanceSpan, actionTextStartIndex, actionTextEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		setText(spannable);		
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

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
}
