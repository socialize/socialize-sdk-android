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

import android.content.Context;

import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.ListResult;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.listener.activity.UserActivityListListener;
import com.socialize.ui.view.LoadingListView;
import com.socialize.view.BaseView;

/**
 * Renders a list of recent user activity.
 * @author Jason Polites
 *
 */
public class UserActivityView extends BaseView {

	// Local
	private UserActivityListAdapter adapter;
	private LoadingListView listView;
	
	// Injected
	private int numItems = 5;
	
	private IBeanFactory<UserActivityListAdapter> userActivityListAdapterFactory;
	private IBeanFactory<LoadingListView> loadingListViewFactory;
	
	public UserActivityView(Context context) {
		super(context);
	}

	public void init() {
		listView = loadingListViewFactory.getBean();
		adapter = userActivityListAdapterFactory.getBean();
		listView.setListAdapter(adapter);
		addView(listView);
	}
	
	public void loadUserActivity(long userId) {
		listView.showLoading();
		Socialize.getSocialize().listActivityByUser(userId, 0, numItems, new UserActivityListListener() {
			@Override
			public void onList(ListResult<SocializeAction> entities) {
				adapter.setActions(entities.getItems());
				adapter.notifyDataSetChanged();
				listView.showList();
			}
			@Override
			public void onError(SocializeException error) {
				adapter.reset();
				adapter.notifyDataSetChanged();
				listView.showList();
			}
		});
	}
	
	public void setNumItems(int numItems) {
		this.numItems = numItems;
	}

	public void setUserActivityListAdapterFactory(IBeanFactory<UserActivityListAdapter> userActivityListAdapterFactory) {
		this.userActivityListAdapterFactory = userActivityListAdapterFactory;
	}

	public void setLoadingListViewFactory(IBeanFactory<LoadingListView> loadingListViewFactory) {
		this.loadingListViewFactory = loadingListViewFactory;
	}
}
