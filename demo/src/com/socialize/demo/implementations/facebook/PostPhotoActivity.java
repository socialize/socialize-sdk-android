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
package com.socialize.demo.implementations.facebook;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import com.socialize.ShareUtils;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.auth.facebook.FacebookService;
import com.socialize.demo.DemoActivity;
import com.socialize.demo.DemoUtils;
import com.socialize.demo.R;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.facebook.FacebookUtils;
import com.socialize.ui.dialog.SafeProgressDialog;


/**
 * @author Jason Polites
 *
 */
public class PostPhotoActivity extends DemoActivity {

	Button btnPostPhoto;
	Bitmap mImageBitmap;
	ImageView mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.photo);
		Button btnPhoto = (Button) findViewById(R.id.btnPhoto);
		btnPostPhoto = (Button) findViewById(R.id.btnPostPhoto);
		mImageView = (ImageView) findViewById(R.id.imgPhoto);

		btnPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(takePictureIntent, 0);
			}
		});
		
		// This does the post to facebook
		btnPostPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mImageBitmap != null) {
					
					final ProgressDialog progress = SafeProgressDialog.show(PostPhotoActivity.this);
				
					// First create a Socialize share object so we get the correct URLs
					ShareOptions options = ShareUtils.getUserShareOptions(PostPhotoActivity.this);
					
					ShareUtils.registerShare(PostPhotoActivity.this, entity, options, new ShareAddListener() {
						
						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							handleError(PostPhotoActivity.this, error);
						}
						
						@Override
						public void onCreate(final Share result) {
							
							FacebookUtils.link(PostPhotoActivity.this, new SocializeAuthListener() {
								
								@Override
								public void onError(SocializeException error) {
									handleError(PostPhotoActivity.this, error);
								}
								
								@Override
								public void onCancel() {
									DemoUtils.showToast(PostPhotoActivity.this, "Cancelled");
								}
								
								@Override
								public void onAuthSuccess(SocializeSession session) {
									// We have the result, use the URLs to add to the post
									PropagationInfo propagationInfo = result.getPropagationInfoResponse().getPropagationInfo(ShareType.FACEBOOK);
									String link = propagationInfo.getEntityUrl();

									// Now post to Facebook.
									try {
										// Get the bytes for the image
										byte[] image = FacebookUtils.getImageForPost(PostPhotoActivity.this, mImageBitmap, CompressFormat.JPEG);
										
										Map<String, Object> params = new HashMap<String, Object>();
										String graphPath = getGraphPath();
										
										params.put("caption", "A test photo of something " + link);
										params.put("photo", image);
										
										FacebookUtils.post(PostPhotoActivity.this, graphPath, params, new SocialNetworkPostListener() {
											
											@Override
											public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
												progress.dismiss();
												handleError(PostPhotoActivity.this, error);
											}
											
											@Override
											public void onCancel() {
												progress.dismiss();
												DemoUtils.showToast(PostPhotoActivity.this, "Cancelled");
											}
											
											@Override
											public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
												progress.dismiss();
												DemoUtils.showToast(parent, "Photo Shared!");
											}
										});									
									}
									catch (IOException e) {
										handleError(PostPhotoActivity.this, e);
									}										
								}
								
								@Override
								public void onAuthFail(SocializeException error) {
									handleError(PostPhotoActivity.this, error);
								}
							}, getPermissions());
							
							
						}
					}, SocialNetwork.FACEBOOK);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		if(intent != null) {
			Bundle extras = intent.getExtras();
			mImageBitmap = (Bitmap) extras.get("data");
			
			if(mImageBitmap != null) {
				mImageView.setImageBitmap(mImageBitmap);
				btnPostPhoto.setVisibility(View.VISIBLE);
			}
			else {
				btnPostPhoto.setVisibility(View.GONE);
			}
		}
	}
	
	protected String getGraphPath() {
		return "me/photos";
	}

	protected String[] getPermissions() {
		return FacebookService.DEFAULT_PERMISSIONS;
	}
}
