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
package com.socialize.demo.implementations.auth;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.socialize.ShareUtils;
import com.socialize.api.SocializeSession;
import com.socialize.demo.DemoActivity;
import com.socialize.demo.DemoUtils;
import com.socialize.demo.R;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.facebook.FacebookUtils;
import com.socialize.networks.twitter.TwitterUtils;
import com.socialize.ui.auth.AuthDialogListener;
import com.socialize.ui.auth.AuthPanelView;


/**
 * @author Jason Polites
 *
 */
public class AuthButtonsActivity extends DemoActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth_buttons_activity);
		
		final Button btnFacebook = (Button) findViewById(R.id.btnFacebook);
		final Button btnTwitter = (Button) findViewById(R.id.btnTwitter);
		final Button btnAuth = (Button) findViewById(R.id.btnAuth);
		
		btnAuth.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ShareUtils.showLinkDialog(AuthButtonsActivity.this, new AuthDialogListener() {
					
					@Override
					public void onShow(Dialog dialog, AuthPanelView dialogView) {}
					
					@Override
					public void onCancel(Dialog dialog) {
						DemoUtils.showToast(AuthButtonsActivity.this, "Cancelled");
					}
					
					@Override
					public void onSkipAuth(Activity context, Dialog dialog) {
						DemoUtils.showToast(AuthButtonsActivity.this, "Skipped");
						dialog.dismiss();
					}
					
					@Override
					public void onError(Activity context, Dialog dialog, Exception error) {
						handleError(context, error);
					}
					
					@Override
					public void onAuthenticate(Activity context, Dialog dialog, SocialNetwork network) {
						DemoUtils.showToast(AuthButtonsActivity.this, "Authed with " + network.name());
					}
				});
			}
		});
		
		
		// Check if we are signed into Facebook
		if(FacebookUtils.isLinkedForRead(this)) {
			btnFacebook.setText("Sign OUT of Facebook");
		}
		else {
			btnFacebook.setText("Sign IN to Facebook");
		}
		
		if(TwitterUtils.isLinked(this)) {
			btnTwitter.setText("Sign OUT of Twitter");
		}
		else {
			btnTwitter.setText("Sign IN to Twitter");
		}
		
		btnFacebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(btnFacebook.getText().equals("Sign IN to Facebook")) {
					
					btnFacebook.setEnabled(false);
					
					FacebookUtils.linkForRead(AuthButtonsActivity.this, new SocializeAuthListener() {
						
						@Override
						public void onError(SocializeException error) {
							handleError(AuthButtonsActivity.this, error);
							btnFacebook.setEnabled(true);
						}
						
						@Override
						public void onCancel() {
							DemoUtils.showToast(AuthButtonsActivity.this, "Cancelled");
							btnFacebook.setEnabled(true);
						}
						
						@Override
						public void onAuthSuccess(SocializeSession session) {
							btnFacebook.setText("Sign OUT of Facebook");
							btnFacebook.setEnabled(true);
							DemoUtils.showToast(AuthButtonsActivity.this, "Login Successful");
						}
						
						@Override
						public void onAuthFail(SocializeException error) {
							handleError(AuthButtonsActivity.this, error);
							btnFacebook.setEnabled(true);
						}
					});
				}
				else {
					FacebookUtils.unlink(AuthButtonsActivity.this);
					btnFacebook.setText("Sign IN to Facebook");	
				}
			}
		});
		
		
		btnTwitter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(btnTwitter.getText().equals("Sign IN to Twitter")) {
					
					btnTwitter.setEnabled(false);
					
					TwitterUtils.link(AuthButtonsActivity.this, new SocializeAuthListener() {
						
						@Override
						public void onError(SocializeException error) {
							handleError(AuthButtonsActivity.this, error);
							btnTwitter.setEnabled(true);
						}
						
						@Override
						public void onCancel() {
							DemoUtils.showToast(AuthButtonsActivity.this, "Cancelled");
							btnTwitter.setEnabled(true);
						}
						
						@Override
						public void onAuthSuccess(SocializeSession session) {
							btnTwitter.setText("Sign OUT of Twitter");
							btnTwitter.setEnabled(true);
						}
						
						@Override
						public void onAuthFail(SocializeException error) {
							handleError(AuthButtonsActivity.this, error);
							btnTwitter.setEnabled(true);
						}
					});
					
				}
				else {
					TwitterUtils.unlink(AuthButtonsActivity.this);
					btnTwitter.setText("Sign IN to Twitter");	
				}
			}
		});		
	}
}
