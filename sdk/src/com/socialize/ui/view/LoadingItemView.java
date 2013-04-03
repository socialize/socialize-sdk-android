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
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

/**
 * @author Jason Polites
 */
public class LoadingItemView<V extends View> extends BaseLoadingView<StaticItemList<V>> {

	private List<V> items;

	public LoadingItemView(Context context) {
		super(context);
	}

	@Override
	protected StaticItemList<V> createMainView() {
		// TODO: Use factory
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		StaticItemList<V> staticItemList = new StaticItemList<V>(getContext());
		staticItemList.setLayoutParams(params);
		staticItemList.setOrientation(LinearLayout.VERTICAL);
		return staticItemList;
	}
	
	public void clear() {
		getMainView().setItems(null);
		getMainView().notifyDataSetChanged();
	}
	
	public void setItems(List<V> items) {
		this.items = items;
		getMainView().setItems(items);
		getMainView().notifyDataSetChanged();
	}

	public List<V> getItems() {
		return items;
	}
}
