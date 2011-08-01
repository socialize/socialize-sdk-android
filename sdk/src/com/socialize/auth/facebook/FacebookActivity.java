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
package com.socialize.auth.facebook;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.socialize.auth.AuthProviderResponse;
import com.socialize.error.SocializeException;
import com.socialize.listener.AuthProviderListener;
import com.socialize.listener.ListenerHolder;
import com.socialize.log.SocializeLogger;

/**
 * @author Jason Polites
 */
public class FacebookActivity extends Activity {
	
	private Facebook mFacebook;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = this.getIntent();
		
		if(intent != null) {
			Bundle extras = intent.getExtras();
			
			if(extras != null) {
				String appId = extras.getString("appId");
				
				mFacebook = new Facebook(appId);

				SessionStore.restore(mFacebook, this);
		       	
		        doAuth();
			}
			else {
				finish();
			}
		}
		else {
			finish();
		}
	}
	
	private void doAuth() {
		final AuthProviderListener listener = ListenerHolder.getInstance().get("auth");
		
		mFacebook.authorize(this, new String[]{}, new DialogListener() {
			
			@Override
			public void onComplete(Bundle values) {
				SessionStore.save(mFacebook, FacebookActivity.this);
				
				try {
					String json = mFacebook.request("me");
					
					JSONObject obj = new JSONObject(json);
					
					String id = obj.getString("id");
					String token = values.getString("access_token");
					
					if(listener != null) {
						AuthProviderResponse response = new AuthProviderResponse();
						response.setUserId(id);
						response.setToken(token);
						listener.onAuthSuccess(response);
					}
					else {
						// TODO: log error
					}
				}
				catch (MalformedURLException e) {
					if(listener != null) {
						listener.onError(new SocializeException(e));
					}
					else {
						e.printStackTrace();
					}
				}
				catch (IOException e) {
					if(listener != null) {
						listener.onError(new SocializeException(e));
					}
					else {
						e.printStackTrace();
					}
				}
				catch (JSONException e) {
					if(listener != null) {
						listener.onError(new SocializeException(e));
					}
					else {
						e.printStackTrace();
					}
				}
				
				finish();
			}
			
			@Override
			public void onFacebookError(FacebookError e) {
				if(listener != null) {
					listener.onError(new SocializeException(e));
				}
				else {
					doError(e);
				}
				
				finish();
			}
			
			@Override
			public void onError(DialogError e) {
				if(listener != null) {
					listener.onError(new SocializeException(e));
				}
				else {
					doError(e);
				}
				
				finish();
			}

			@Override
			public void onCancel() {
				if(listener != null) {
					// TODO: use error code.
					listener.onError(new SocializeException("User canceled request"));
				}
				
				finish();
			}
		});
	}
	
	private void doError(final Throwable e) {
		runOnUiThread(new Runnable() {
			public void run() {
				Log.e(SocializeLogger.LOG_TAG, "Facebook error", e);
				doErrorUI(e.getMessage());
			}
		});
	}
	
	private void doErrorUI(final String error) {
		
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(FacebookActivity.this);
			builder.setTitle("Oops!");
			builder.setMessage("Oops!\nSomething went wrong...");
			builder.setCancelable(false);
			builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					FacebookActivity.this.doAuth();
				}
			});	
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					FacebookActivity.this.finish();
				}
			});	
			builder.create().show();
		}
		catch (Exception e) {
			Log.e(SocializeLogger.LOG_TAG, "Facebook error", e);
		}
	}
	
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }
}
