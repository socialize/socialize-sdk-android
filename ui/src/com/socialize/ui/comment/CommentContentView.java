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
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
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
	
	@Deprecated
	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listView.setOnItemClickListener(listener);
	}
	
	public void showList() {
		setDisplayedChild(1);
	}

	public void scrollToTop() {
		listView.setSelection(0); // scroll to top
	}
}
