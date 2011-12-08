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
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.action.ActionType;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.util.DateUtils;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 */
public class UserActivityListAdapter extends BaseAdapter {
	
	// Injected
	private IBeanFactory<UserActivityListItem> userActivityListItemFactory;
	private Drawables drawables;
	private DateUtils dateUtils;
	private SocializeErrorHandler errorHandler;
	private Activity activity;
	private Date now = null;
	
	public UserActivityListAdapter(Activity activity) {
		super();
		now = new Date();
		this.activity = activity;
	}

	// Local
	private List<SocializeAction> actions;

	public void reset() {
		if(actions != null) {
			actions.clear();
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return (actions == null) ? 0 : actions.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		if(actions != null && position < actions.size()) {
			return actions.get(position);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getItemViewType(int position) {
		SocializeAction item = (SocializeAction) getItem(position);
		
		if(item.getActionType().equals(ActionType.LIKE)) {
			return 1;
		}
		else {
			return 0;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}	

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View oldView, ViewGroup parent) {
		UserActivityListItem view = null;
		
		final SocializeAction item = (SocializeAction) getItem(position);
		
		if(oldView instanceof UserActivityListItem) {
			view = (UserActivityListItem) oldView;
		}
		else {
			if(item.getActionType().equals(ActionType.LIKE)) {
				view = userActivityListItemFactory.getBean(true);
			}
			else {
				view = userActivityListItemFactory.getBean(false);
			}
		}
		
		if(item != null) {
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(SocializeUI.getInstance().getEntityLoader() != null) {
						try {
							SocializeUI.getInstance().getEntityLoader().loadEntity(activity, item.getEntity());
						} 
						catch (Exception e) {
							errorHandler.handleError(activity, e);
						}
					}
				}
			});						

			view.setText(item.getDisplayText());
			
			Long date = item.getDate();
			
			if(date != null && date.longValue() > 0) {
				long diff = (now.getTime() - date.longValue());
				view.setDate(dateUtils.getTimeString(diff) + " ");
			}
			else {
				view.setDate(" ");
			}			

			switch(item.getActionType()) {
				case COMMENT:
					view.setTitle("Commented on " + item.getEntityDisplayName());
					if (drawables != null) if (drawables != null) view.setIcon(drawables.getDrawable("icon_comment.png"));
					break;
				case LIKE:
					view.setTitle("Liked " + item.getEntityDisplayName());
					if (drawables != null) view.setIcon(drawables.getDrawable("icon_like_hi.png"));
					break;
				case SHARE:
					view.setTitle("Shared " + item.getEntityDisplayName());
					if (drawables != null) view.setIcon(drawables.getDrawable("icon_share.png"));
					break;
			}
		}

		return view;
	}

	public void setUserActivityListItemFactory(IBeanFactory<UserActivityListItem> userActivityListItemFactory) {
		this.userActivityListItemFactory = userActivityListItemFactory;
	}

	protected void setActions(List<SocializeAction> actions) {
		this.actions = actions;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setDateUtils(DateUtils dateUtils) {
		this.dateUtils = dateUtils;
	}
	
	
}
