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
package com.socialize.ui.notifications;

import android.content.Context;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.socialize.log.SocializeLogger;


/**
 * @author Jason Polites
 *
 */
public class DirectUrlWebView extends WebView {
	
	private SocializeLogger logger;
	private DirectUrlListener listener;
	
	public DirectUrlWebView(Context context) {
		super(context);
	}

	public void init() {
		FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
		
		getSettings().setJavaScriptEnabled(true);
		getSettings().setLoadWithOverviewMode(true);
        getSettings().setUseWideViewPort(true);
        getSettings().setSupportZoom(true);
        getSettings().setBuiltInZoomControls(true);
        
		setLayoutParams(FILL);
		setWebChromeClient(newWebChromeClient());
		setWebViewClient(newWebViewClient());
	}
	
	public void destroy() {
		clearCache(false);
		destroyDrawingCache();
	}
	
	protected WebChromeClient newWebChromeClient() {
		WebChromeClient client = new WebChromeClient() {

			@Override
			public void onConsoleMessage(String message, int lineNumber, String sourceID) {
				if(logger != null && logger.isInfoEnabled()) {
					logger.info(message + " (" + sourceID + ": " + lineNumber + ")");
				}
				
				super.onConsoleMessage(message, lineNumber, sourceID);
			}
		};
		
		return client;
	}
	
	protected WebViewClient newWebViewClient() {
		WebViewClient client = new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				if(logger != null && logger.isInfoEnabled()) {
					logger.info("Direct URL loaded page: " + url);
				}				
				
				
				if(listener != null) {
					listener.onAfterPageLoaded(DirectUrlWebView.this, url);
				}
				
				super.onPageFinished(view, url);
			}
		};
		
		return client;
	}
	
	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public void setListener(DirectUrlListener listener) {
		this.listener = listener;
	}
}
