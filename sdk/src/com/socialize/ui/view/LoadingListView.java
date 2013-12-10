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
package com.socialize.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * @author Jason Polites
 *
 */
public class LoadingListView extends BaseLoadingView<ListView> {
	
	/**
	 * Used to locate the list view (to aid in testing)
	 */
	public static final int LIST_VIEW_ID = 10001;

	public LoadingListView(Context context) {
		super(context);
	}
	
	public void setListAdapter(ListAdapter adapter) {
		getMainView().setAdapter(adapter);
	}
	
	public void setScrollListener(OnScrollListener listener) {
		getMainView().setOnScrollListener(listener);
	}

	public void scrollToTop() {
		getMainView().requestFocusFromTouch();
		getMainView().setSelection(0); // scroll to top
	}

	@Override
	protected ListView createMainView() {
		LinearLayout.LayoutParams listViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		final ListView listView = new SafeListView(getContext());
		listView.setId(LIST_VIEW_ID);
		listView.setLayoutParams(listViewLayoutParams);
		listView.setDrawingCacheEnabled(true);
		listView.setCacheColorHint(Color.TRANSPARENT);
		listView.setScrollingCacheEnabled(false);
		listView.setDividerHeight(1);
		listView.setSmoothScrollbarEnabled(true);
		listView.setVerticalFadingEdgeEnabled(false);
		listView.setItemsCanFocus(true);
		listView.setClickable(true);
		listView.setFocusable(true);
		listView.setFocusableInTouchMode(true);
		
		return listView;
	}
}
