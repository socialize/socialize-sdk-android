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
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socialize.android.ioc.IBeanFactory;

/**
 * @author Jason Polites
 *
 */
public abstract class BaseLoadingView<V extends View> extends SafeViewFlipper implements LoadingView<V> {

	private V mainView;
	private TextView emptyTextView;
	private IBeanFactory<BasicLoadingView> loadingViewFactory;
	private String emptyText = "No data";
	
	public BaseLoadingView(Context context) {
		super(context);
	}
	
	public void init() {
		LinearLayout contentView = new LinearLayout(getContext());

		LinearLayout.LayoutParams contentViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		contentView.setLayoutParams(contentViewLayoutParams);
		contentView.setOrientation(LinearLayout.VERTICAL);

		LinearLayout.LayoutParams emptyContentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		
		emptyTextView = new TextView(getContext());
		emptyTextView.setLayoutParams(emptyContentLayoutParams);
		emptyTextView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		emptyTextView.setText(emptyText);
		
		mainView = createMainView();

		contentView.addView(mainView);

		LinearLayout.LayoutParams flipperLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		flipperLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		this.setLayoutParams(flipperLayoutParams);

		// create a loading screen
		BasicLoadingView loadingScreen = loadingViewFactory.getBean();
		
		if(loadingScreen != null) {
			this.addView(loadingScreen);
		}
		
		this.addView(contentView);
		this.addView(emptyTextView);
		this.showLoading();
	}	

	/* (non-Javadoc)
	 * @see com.socialize.ui.view.LoadingView#showLoading()
	 */
	@Override
	public void showLoading() {
		setDisplayedChild(0);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.ui.view.LoadingView#showList()
	 */
	@Override
	public void showList() {
		setDisplayedChild(1);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.ui.view.LoadingView#showEmptyText()
	 */
	@Override
	public void showEmptyText() {
		setDisplayedChild(2);
	}
	
	public void setLoadingViewFactory(IBeanFactory<BasicLoadingView> loadingViewFactory) {
		this.loadingViewFactory = loadingViewFactory;
	}

	@Override
	public String getEmptyText() {
		return emptyText;
	}

	@Override
	public void setEmptyText(String emptyText) {
		this.emptyText = emptyText;
		
		if(emptyTextView != null) {
			emptyTextView.setText(emptyText);
		}
	}
	
	@Override
	public V getMainView() {
		return mainView;
	}

	protected abstract V createMainView();
}
