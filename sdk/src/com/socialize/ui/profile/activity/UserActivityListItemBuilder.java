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

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.SocializeAction;

/**
 * @author Jason Polites
 * @deprecated not really needed
 */
@Deprecated
public class UserActivityListItemBuilder {

	private IBeanFactory<UserActivityListItem> userActivityListItemFactory;
	
	public UserActivityListItem build(final Activity activity, final SocializeAction item, final Date now) {
		UserActivityListItem view = userActivityListItemFactory.getBean();
		view.setAction(item, now);
		return view;
	}
	
//	public UserActivityListItem build(final Activity activity, UserActivityListItem view, final SocializeAction item, final Date now) {
		
		
//		
//		view.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(SocializeUI.getInstance().getEntityLoader() != null) {
//					try {
//						SocializeUI.getInstance().getEntityLoader().loadEntity(activity, item.getEntity());
//					} 
//					catch (Exception e) {
//						if(logger != null) {
//							logger.error("Failed to load entity", e);
//						}
//					}
//				}
//			}
//		});						
//
//		view.setText(item.getDisplayText());
//		
//		Long date = item.getDate();
//		
//		if(date != null && date.longValue() > 0) {
//			long diff = (now.getTime() - date.longValue());
//			view.setDate(dateUtils.getTimeString(diff) + " ");
//		}
//		else {
//			view.setDate("");
//		}			
//
//		switch(item.getActionType()) {
//			case COMMENT:
//				view.setTitle("Commented on " + item.getEntityDisplayName());
//				if (drawables != null) if (drawables != null) view.setIcon(drawables.getDrawable("icon_comment.png"));
//				break;
//			case LIKE:
//				view.setTitle("Liked " + item.getEntityDisplayName());
//				if (drawables != null) view.setIcon(drawables.getDrawable("icon_like_hi.png"));
//				break;
//			case SHARE:
//				view.setTitle("Shared " + item.getEntityDisplayName());
//				if (drawables != null) view.setIcon(drawables.getDrawable("icon_share.png"));
//				break;
//		}		
		
//		return view;
//	}

	public void setUserActivityListItemFactory(IBeanFactory<UserActivityListItem> userActivityListItemFactory) {
		this.userActivityListItemFactory = userActivityListItemFactory;
	}
}
