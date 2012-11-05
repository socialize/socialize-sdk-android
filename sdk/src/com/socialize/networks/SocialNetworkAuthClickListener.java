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
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.error.SocializeException;
import com.socialize.i18n.I18NConstants;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.facebook.FacebookUtils;
import com.socialize.networks.twitter.TwitterUtils;
import com.socialize.ui.dialog.SimpleDialogFactory;

/**
 * @author Jason Polites
 *
 */
public abstract class SocialNetworkAuthClickListener implements OnClickListener {

	private SocializeAuthListener listener;
	private SimpleDialogFactory<ProgressDialog> dialogFactory;
	private ProgressDialog dialog; 

	@Override
	public void onClick(final View view) {
		
		view.setEnabled(false);
		
		SocialNetwork network = getSocialNetwork();
		SocializeAuthListener authListener = getAuthListener(view);
		
		if(!getSocialize().isAuthenticated(AuthProviderType.valueOf(network))) {
			dialog = dialogFactory.show(view.getContext(), I18NConstants.DLG_AUTH, I18NConstants.DLG_AUTH_MESSAGE);
			switch (network) {
				case FACEBOOK:
					FacebookUtils.link((Activity) view.getContext(), getAuthListener(view));
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
				if(dialog != null) {
					dialog.dismiss();
				}
				
				if(listener != null) {
					listener.onError(error);
				}
				view.setEnabled(true);
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				if(dialog != null) {
					dialog.dismiss();
				}
				if(listener != null) {
					listener.onAuthSuccess(session);
				}
				view.setEnabled(true);
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				if(dialog != null) {
					dialog.dismiss();
				}
				if(listener != null) {
					listener.onAuthFail(error);
				}
				view.setEnabled(true);
			}

			@Override
			public void onCancel() {
				if(dialog != null) {
					dialog.dismiss();
				}
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

	public void setDialogFactory(SimpleDialogFactory<ProgressDialog> dialogFactory) {
		this.dialogFactory = dialogFactory;
	}

	public void setListener(SocializeAuthListener listener) {
		this.listener = listener;
	}
}
