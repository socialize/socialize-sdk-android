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
package com.socialize.demo.implementations.actionbar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.json.JSONObject;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import com.socialize.ActionBarUtils;
import com.socialize.ShareUtils;
import com.socialize.api.action.share.SocialNetworkDialogListener;
import com.socialize.demo.DemoActivity;
import com.socialize.demo.R;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.Share;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.FacebookUtils;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.actionbar.OnActionBarEventListener;


/**
 * @author Jason Polites
 *
 */
public class PhotoActionBarActivity extends DemoActivity {
	
	private static final int IO_BUFFER_SIZE = 4 * 1024; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBarOptions options = new ActionBarOptions();
		
		View actionBarWrapped = ActionBarUtils.showActionBar(this, R.layout.photo_actionbar, entity, options, new ActionBarListener() {
			
			@Override
			public void onCreate(ActionBarView actionBar) {
				
				actionBar.setOnActionBarEventListener(new OnActionBarEventListener() {

					@Override
					public void onUpdate(ActionBarView actionBar) {
						// Called when the action bar has its data updated
					}

					@Override
					public void onPostUnlike(ActionBarView actionBar) {
						// Called AFTER a user has removed a like
					}

					@Override
					public void onPostShare(ActionBarView actionBar, Share share) {
						// Called AFTER a user has posted a share
					}

					@Override
					public void onPostLike(ActionBarView actionBar, Like like) {
						// Called AFTER a user has posted a like
					}

					@Override
					public void onLoad(ActionBarView actionBar) {
						// Called when the action bar is loaded
					}

					@Override
					public void onGetLike(ActionBarView actionBar, Like like) {
						// Called when the action bar retrieves the like for the
						// current user
					}

					@Override
					public void onGetEntity(ActionBarView actionBar, Entity entity) {
						// Called when the action bar retrieves the entity data
					}

					@Override
					public boolean onClick(ActionBarView actionBar, ActionBarEvent evt) {
						if(evt.equals(ActionBarEvent.SHARE)) {
							ShareUtils.showShareDialog(PhotoActionBarActivity.this, entity, new SocialNetworkDialogListener() {

								@Override
								public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, final PostData postData) {
									if(socialNetwork.equals(SocialNetwork.FACEBOOK)) {
										
										// Because we are accessing a network address we can't be on the main UI thread.
										// If you are loading an image from the local device you probably won't need this.
										new Thread() {

											@Override
											public void run() {
												InputStream in = null;
												Bitmap image = null;
												
												try {
												   
													// In this example we are going to manually load an image from an HTTP URL
													// however you could just as easily use an image file already on the device.
											        in = new BufferedInputStream(new URL("https://lh5.googleusercontent.com/-oPgrWjOC-6w/T9UWPfl2qoI/AAAAAAAABMQ/KtRKcWwRsJ0/s932/P1010444.JPG").openStream(), IO_BUFFER_SIZE);
											        image = BitmapFactory.decodeStream(in);
											        
											        in.close();
													
													// Format the picture for Facebook
													byte[] imageData = FacebookUtils.getImageForPost(PhotoActionBarActivity.this, image, CompressFormat.JPEG);
													
													// Add the photo to the post
													postData.getPostValues().put("photo", imageData);
													
													// Add the link returned from Socialize to use SmartDownloads
													postData.getPostValues().put("caption", "A test photo of something " + postData.getPropagationInfo().getEntityUrl());
													
													// Add other fields to postData as necessary
													
													// Post to me/photos
													FacebookUtils.post(PhotoActionBarActivity.this, "me/photos", postData.getPostValues(), new SocialNetworkPostListener() {
														
														@Override
														public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
															// Handle error
														}
														
														@Override
														public void onCancel() {
															// The user cancelled the auth process
														}
														
														@Override
														public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
															// The post was successful
														}
													});			
												}
												catch (IOException e) {
													// Handle error
												}										
												finally {
													if(image != null) {
														image.recycle();
													}
												}
											}
										}.start();
										
										// Don't let Socialize do the share
										return true;
									}
									
									return false;
								}
							});
							
							return true;
						}
						return false;
					}
				});				
				
			}
		});		
		
		setContentView(actionBarWrapped);
	}
}
