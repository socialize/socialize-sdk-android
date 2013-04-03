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

import android.content.Context;
import com.socialize.ActionUtils;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.listener.activity.ActionListListener;
import com.socialize.ui.view.LoadingItemView;
import com.socialize.view.BaseView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Renders a list of recent user activity.
 * @author Jason Polites
 *
 */
public class UserActivityView extends BaseView {

	// Local
	private LoadingItemView<UserActivityListItem> itemView;
	
	// Injected
	private int numItems = 10;
	private IBeanFactory<UserActivityListItem> userActivityListItemFactory;
	
	private IBeanFactory<LoadingItemView<UserActivityListItem>> loadingItemViewFactory;
	
	public UserActivityView(Context context) {
		super(context);
	}

	public void init() {
		itemView = loadingItemViewFactory.getBean();
		itemView.setEmptyText("No other recent activity");
		addView(itemView);
	}
	
	public void clearUserActivity() {
		itemView.clear();
		itemView.showEmptyText();
	}
	
	public void loadUserActivity(long userId, final SocializeAction current) {
		itemView.showLoading();
		
		ActionUtils.getActionsByUser(getActivity(), userId, 0, numItems, new ActionListListener() {
			@Override
			public void onList(ListResult<SocializeAction> entities) {
				if(entities != null) {
					
					Date now = new Date();
					
					List<SocializeAction> items = entities.getItems();
					
					if(items != null) {
						if(current != null) {
							items.remove(current);
						}
					}
					
					if(items != null && items.size() > 0) {
						ArrayList<UserActivityListItem> views = new ArrayList<UserActivityListItem>(items.size());
						
						LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
						params.setMargins(0, 8, 0, 0);
						
						for (SocializeAction item : items) {
							UserActivityListItem view = userActivityListItemFactory.getBean();
							view.setAction(getContext(), item, now);
							view.setLayoutParams(params);
							views.add(view);
						}
						
						itemView.setItems(views);
						itemView.showList();
					}
					else {
						itemView.clear();
						itemView.showEmptyText();
					}
				}
				else {
					itemView.clear();
					itemView.showEmptyText();
				}
			}
			@Override
			public void onError(SocializeException error) {
				itemView.clear();
				itemView.showEmptyText();
			}
		});
	}

	public LoadingItemView<UserActivityListItem> getItemView() {
		return itemView;
	}

	public void setNumItems(int numItems) {
		this.numItems = numItems;
	}

	public void setLoadingItemViewFactory(IBeanFactory<LoadingItemView<UserActivityListItem>> loadingListViewFactory) {
		this.loadingItemViewFactory = loadingListViewFactory;
	}

	public void setUserActivityListItemFactory(IBeanFactory<UserActivityListItem> userActivityListItemFactory) {
		this.userActivityListItemFactory = userActivityListItemFactory;
	}
}
