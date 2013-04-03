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
package com.socialize.auth.facebook;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.error.SocializeException;
import com.socialize.facebook.DialogError;
import com.socialize.facebook.Facebook;
import com.socialize.facebook.Facebook.DialogListener;
import com.socialize.facebook.FacebookError;
import com.socialize.listener.AuthProviderListener;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * @author Jason Polites
 *
 */
@Deprecated
public abstract class FacebookDialogListener implements DialogListener {

	private FacebookSessionStore facebookSessionStore;
	private Facebook facebook;
	private WeakReference<Context> context;
	private AuthProviderListener listener;

	public FacebookDialogListener(Context context, Facebook facebook, FacebookSessionStore facebookSessionStore, AuthProviderListener listener) {
		super();
		this.context = new WeakReference<Context>(context);
		this.facebook = facebook;
		this.facebookSessionStore = facebookSessionStore;
		this.listener = listener;
	}

	@Override
	public void onComplete(final Bundle values) {
		facebookSessionStore.save(facebook, context.get());
		
		new AsyncTask<Void, Void, Void>() {
			
			String id;
			String token;
			Exception error;
			
			@Override
			protected Void doInBackground(Void... params) {
				try {
					String json = facebook.request("me");
					JSONObject obj = new JSONObject(json);
					id = obj.getString("id");
					token = values.getString("access_token");
				}
				catch (Exception e) {
					error = e;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if(error != null) {
					if(listener != null) {
						listener.onError(new SocializeException(error));
					}
					else {
						handleError(error);
					}					
				}
				else {
					if(listener != null) {
						AuthProviderResponse response = new AuthProviderResponse();
						response.setUserId(id);
						response.setToken(token);
						listener.onAuthSuccess(response);
					}
					else {
						// TODO: log error
					}
					
					onFinish();
				}
				
			}
		}.execute();
		
	}
	
	@Override
	public void onFacebookError(FacebookError e) {
		if(listener != null) {
			listener.onError(new SocializeException(e));
		}
		else {
			handleError(e);
		}
	}
	
	@Override
	public void onError(DialogError e) {
		if(listener != null) {
			listener.onError(new SocializeException(e));
		}
		else {
			handleError(e);
		}
	}

	@Override
	public void onCancel() {
		if(listener != null) {
			listener.onCancel();
		}
		else {
			Toast.makeText(context.get(), "Request cancelled", Toast.LENGTH_SHORT).show();
		}
		
		onFinish();
	}

	public abstract void onFinish();
	
	public abstract void handleError(Throwable error);
}
