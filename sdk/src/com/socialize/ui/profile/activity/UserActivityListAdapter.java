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

import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.SocializeAction;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 */
public class UserActivityListAdapter extends BaseAdapter {
	
	// Injected
	private IBeanFactory<UserActivityListItem> userActivityListItemFactory;
	private Drawables drawables;
	
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

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View oldView, ViewGroup parent) {
		UserActivityListItem view = null;
		
		if(oldView instanceof UserActivityListItem) {
			view = (UserActivityListItem) oldView;
		}
		else {
			view = userActivityListItemFactory.getBean();
		}
		
		final SocializeAction item = (SocializeAction) getItem(position);
		
		if(item != null) {
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO: add entity loader
				}
			});						

			TextView text = view.getText();
			ImageView icon = view.getIcon();

			if (text != null) {
				text.setText(item.getDisplayText());
			}

			if (icon != null && drawables != null) {
				
				switch(item.getActionType()) {
					case COMMENT:
						icon.setImageDrawable(drawables.getDrawable("icon_comment.png"));
						break;
					case LIKE:
						icon.setImageDrawable(drawables.getDrawable("icon_like.png"));
						break;
					case SHARE:
						icon.setImageDrawable(drawables.getDrawable("icon_share.png"));
						break;
				
				}
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
}
