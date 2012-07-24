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
import com.socialize.listener.subscription.SubscriptionCheckListener;
import com.socialize.listener.subscription.SubscriptionResultListener;
import com.socialize.notifications.SubscriptionType;
import com.socialize.ui.dialog.SafeProgressDialog;


/**
 * @author Jason Polites
 *
 */
public class SubscriptionButtonsActivity extends DemoActivity {

	protected void setupButton(final Button btnSubscription, final String subscribe, final String unsubscribe, final SubscriptionType type) {

		// Check if we are subscribed
		SubscriptionUtils.isSubscribed(this, entity, type, new SubscriptionCheckListener() {
			
			@Override
			public void onSubscribed(Subscription subscription) {
				btnSubscription.setText(unsubscribe);
				btnSubscription.setEnabled(true);
			}

			@Override
			public void onNotSubscribed() {
				btnSubscription.setText(subscribe);
				btnSubscription.setEnabled(true);
			}

			@Override
			public void onError(SocializeException error) {
				handleError(SubscriptionButtonsActivity.this, error);
			}
		});
		
		btnSubscription.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final SafeProgressDialog progress = SafeProgressDialog.show(SubscriptionButtonsActivity.this);
				progress.setCancelable(false);
				
				if(btnSubscription.getText().equals(subscribe)) {
					
					SubscriptionUtils.subscribe(SubscriptionButtonsActivity.this, entity, type, new SubscriptionResultListener() {
						
						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							handleError(SubscriptionButtonsActivity.this, error);
						}
						
						@Override
						public void onCreate(Subscription result) {
							progress.dismiss();
							btnSubscription.setText(unsubscribe);
						}
					});
					
				}
				else {
					SubscriptionUtils.unsubscribe(SubscriptionButtonsActivity.this, entity, type, new SubscriptionResultListener() {
						
						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							handleError(SubscriptionButtonsActivity.this, error);
						}
						
						@Override
						public void onCreate(Subscription result) {
							progress.dismiss();
							btnSubscription.setText(subscribe);
						}
					});					
				}
			}
		});		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscription_buttons_activity);
		
		final Button btnSubscription = (Button) findViewById(R.id.btnSubscription);
		final Button btnSubscriptionEntity = (Button) findViewById(R.id.btnSubscriptionEntity);
		
		btnSubscription.setText("Checking...");
		btnSubscriptionEntity.setText("Checking...");
		
		btnSubscription.setEnabled(false);
		btnSubscriptionEntity.setEnabled(false);
		
		setupButton(btnSubscription, "Subscribe NEW_COMMENTS", "Unsubscribe NEW_COMMENTS", SubscriptionType.NEW_COMMENTS);
		setupButton(btnSubscriptionEntity, "Subscribe ENTITY_NOTIFICATION", "Unsubscribe ENTITY_NOTIFICATION", SubscriptionType.ENTITY_NOTIFICATION);
		
	}
}
