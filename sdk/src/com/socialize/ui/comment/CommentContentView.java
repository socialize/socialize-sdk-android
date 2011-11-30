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
package com.socialize.ui.comment;

import android.content.Context;
import android.view.Gravity;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

/**
 * @author Jason Polites
 *
 */
public class CommentContentView extends ViewFlipper {

	private ListView listView;
	
	public CommentContentView(Context context) {
		super(context);
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}
	
	public void setListAdapter(ListAdapter adapter) {
		this.listView.setAdapter(adapter);
	}
	
	public void setScrollListener(OnScrollListener listener) {
		this.listView.setOnScrollListener(listener);
	}
	
	public void showLoading() {
		setDisplayedChild(0);
	}
	
	public void showList() {
		setDisplayedChild(1);
	}

	public void scrollToTop() {
		listView.setSelection(0); // scroll to top
	}
	
	public void init() {
		LinearLayout contentView = new LinearLayout(getContext());

		LinearLayout.LayoutParams contentViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		contentView.setLayoutParams(contentViewLayoutParams);
		contentView.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams listViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

		ListView listView = new ListView(getContext());
		listView.setId(CommentActivity.LIST_VIEW_ID);
		listView.setLayoutParams(listViewLayoutParams);
		listView.setDrawingCacheEnabled(true);
		listView.setCacheColorHint(0);
		listView.setDividerHeight(2);
		listView.setSmoothScrollbarEnabled(true);

		contentView.addView(listView);

		LinearLayout.LayoutParams flipperLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		flipperLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		this.setLayoutParams(flipperLayoutParams);

		// create a loading screen
		FrameLayout loadingScreen = new FrameLayout(getContext());
		FrameLayout.LayoutParams loadingScreenLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT);
		FrameLayout.LayoutParams progressLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);

		loadingScreenLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		progressLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;

		loadingScreen.setLayoutParams(loadingScreenLayoutParams);

		ProgressBar progress = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
		progress.setLayoutParams(progressLayoutParams);

		loadingScreen.addView(progress);

		this.addView(loadingScreen);
		this.addView(contentView);
		this.setDisplayedChild(0);
		this.setListView(listView);		
	}
}
