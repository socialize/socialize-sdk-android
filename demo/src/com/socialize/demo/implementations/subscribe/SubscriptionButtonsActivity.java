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
package com.socialize.demo.implementations.subscribe;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.socialize.SubscriptionUtils;
import com.socialize.demo.DemoActivity;
import com.socialize.demo.R;
import com.socialize.entity.Subscription;
import com.socialize.error.SocializeException;
import com.socialize.listener.subscription.SubscriptionGetListener;
import com.socialize.listener.subscription.SubscriptionResultListener;
import com.socialize.notifications.NotificationType;
import com.socialize.ui.dialog.SafeProgressDialog;


/**
 * @author Jason Polites
 *
 */
public class SubscriptionButtonsActivity extends DemoActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscription_buttons_activity);
		
		final Button btnSubscription = (Button) findViewById(R.id.btnSubscription);
		
		final SafeProgressDialog progress = SafeProgressDialog.show(this);
		progress.setCancelable(false);
		
		// Check if we are liked
		SubscriptionUtils.isSubscribed(this, entity, NotificationType.NEW_COMMENTS, new SubscriptionGetListener() {
			
			@Override
			public void onGet(Subscription result) {
				if(result.isSubscribed()) {
					btnSubscription.setText("Unsubscribe");
				}
				else {
					btnSubscription.setText("Subscribe");
				}
				
				progress.dismiss();
			}
			
			@Override
			public void onError(SocializeException error) {
				progress.dismiss();
				handleError(error);
			}
		});
		
		btnSubscription.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final SafeProgressDialog progress = SafeProgressDialog.show(SubscriptionButtonsActivity.this);
				progress.setCancelable(false);
				
				if(btnSubscription.getText().equals("Subscribe")) {
					
					
					SubscriptionUtils.subscribe(SubscriptionButtonsActivity.this, entity, NotificationType.NEW_COMMENTS, new SubscriptionResultListener() {
						
						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							handleError(error);
						}
						
						@Override
						public void onCreate(Subscription result) {
							progress.dismiss();
							btnSubscription.setText("Unsubscribe");
						}
					});
					
				}
				else {
					
					
					SubscriptionUtils.unsubscribe(SubscriptionButtonsActivity.this, entity, NotificationType.NEW_COMMENTS, new SubscriptionResultListener() {
						
						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							handleError(error);
						}
						
						@Override
						public void onCreate(Subscription result) {
							progress.dismiss();
							btnSubscription.setText("Subscribe");
						}
					});					
				}
			}
		});
	}
}
