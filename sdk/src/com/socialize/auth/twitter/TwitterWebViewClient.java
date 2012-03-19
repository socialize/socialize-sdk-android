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
package com.socialize.auth.twitter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 *
 */
public class TwitterWebViewClient extends WebViewClient {
	
	private OAuthRequestListener oAuthRequestListener;
	private boolean called = false;
	
	public TwitterWebViewClient() {
		super();
	}

	public void setOauthRequestListener(OAuthRequestListener oAuthRequestListener) {
		this.oAuthRequestListener = oAuthRequestListener;
	}
	
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		if(url.trim().toLowerCase().startsWith(TwitterOAuthProvider.OAUTH_CALLBACK_URL.toLowerCase())) {
			
			if(!called) {
				called = true;
				
				// This is the callback, get the token
				Uri uri = Uri.parse(url);
				
				String cancelToken = uri.getQueryParameter("denied");
				
				if(!StringUtils.isEmpty(cancelToken)) {
					if(oAuthRequestListener != null) {
						oAuthRequestListener.onCancel(cancelToken);
					}
				}
				else {
					String token = uri.getQueryParameter("oauth_token");
					String verifier = uri.getQueryParameter("oauth_verifier");

					if(oAuthRequestListener != null) {
						oAuthRequestListener.onRequestToken(token, verifier);
					}
				}
			}
			
			view.stopLoading();
		}
		else {
			super.onPageStarted(view, url, favicon);
		}
	}
}
