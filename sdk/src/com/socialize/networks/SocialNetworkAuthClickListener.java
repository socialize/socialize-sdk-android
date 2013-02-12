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
package com.socialize.networks;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.facebook.FacebookUtils;
import com.socialize.networks.twitter.TwitterUtils;

/**
 * @author Jason Polites
 *
 */
public abstract class SocialNetworkAuthClickListener implements OnClickListener {

	private SocializeAuthListener listener;

	@Override
	public void onClick(final View view) {
		
		view.setEnabled(false);
		
		SocialNetwork network = getSocialNetwork();
		SocializeAuthListener authListener = getAuthListener(view);
		
		if(!getSocialize().isAuthenticatedForWrite(AuthProviderType.valueOf(network))) {
			switch (network) {
				case FACEBOOK:
					FacebookUtils.linkForRead((Activity) view.getContext(), getAuthListener(view));
					break;
				case TWITTER:
					TwitterUtils.link((Activity) view.getContext(), getAuthListener(view));
					break;
			}
		}
		else {
			authListener.onAuthSuccess(getSocialize().getSession());
		}
	}
	
	protected SocializeAuthListener getAuthListener(final View view) {
		return new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				if(listener != null) {
					listener.onError(error);
				}
				view.setEnabled(true);
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				if(listener != null) {
					listener.onAuthSuccess(session);
				}
				view.setEnabled(true);
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				if(listener != null) {
					listener.onAuthFail(error);
				}
				view.setEnabled(true);
			}

			@Override
			public void onCancel() {
				if(listener != null) {
					listener.onCancel();
				}
				view.setEnabled(true);
			}
		};
	}

	protected abstract SocialNetwork getSocialNetwork();
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	public void setListener(SocializeAuthListener listener) {
		this.listener = listener;
	}
}
