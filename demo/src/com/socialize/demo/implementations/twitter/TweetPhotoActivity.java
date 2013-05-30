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
package com.socialize.demo.implementations.twitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import com.socialize.ShareUtils;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.demo.DemoActivity;
import com.socialize.demo.DemoUtils;
import com.socialize.demo.R;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.twitter.PhotoTweet;
import com.socialize.networks.twitter.TwitterUtils;
import com.socialize.ui.dialog.SafeProgressDialog;
import org.json.JSONObject;

import java.io.IOException;


/**
 * @author Jason Polites
 *
 */
public class TweetPhotoActivity extends DemoActivity {

	Button btnPostPhoto;
	Bitmap mImageBitmap;
	ImageView mImageView;

	@Override
	protected void onCreate() {

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
		
		// This does the post to twitter
		btnPostPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(mImageBitmap != null) {
					
					final ProgressDialog progress = SafeProgressDialog.show(TweetPhotoActivity.this);


					// First create a Socialize share object so we get the correct URLs
					ShareOptions options = ShareUtils.getUserShareOptions(TweetPhotoActivity.this);
					ShareUtils.registerShare(TweetPhotoActivity.this, entity, options, new ShareAddListener() {

						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							handleError(TweetPhotoActivity.this, error);
						}

						@Override
						public void onCreate(Share result) {

							// We have the result, use the URLs to add to the post
							PropagationInfo propagationInfo = result.getPropagationInfoResponse().getPropagationInfo(ShareType.TWITTER);
							String link = propagationInfo.getEntityUrl();

							// Now post to Twitter.
							try {
								// Get the bytes for the image
								byte[] image = TwitterUtils.getImageForPost(TweetPhotoActivity.this, mImageBitmap, CompressFormat.JPEG);

								PhotoTweet tweet = new PhotoTweet();
								tweet.setImageData(image);
								tweet.setText("Photo Tweet! " + link);

								TwitterUtils.tweetPhoto(TweetPhotoActivity.this, tweet, new SocialNetworkPostListener() {

									@Override
									public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
										progress.dismiss();
										handleError(TweetPhotoActivity.this, error);
									}

									@Override
									public void onCancel() {
										progress.dismiss();
										DemoUtils.showToast(TweetPhotoActivity.this, "Cancelled");
									}

									@Override
									public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
										progress.dismiss();
										DemoUtils.showToast(parent, "Photo Shared!");
									}
								});


							}
							catch (IOException e) {
								handleError(TweetPhotoActivity.this, e);
							}

						}
					}, SocialNetwork.TWITTER);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		if(intent != null) {
			Bundle extras = intent.getExtras();
			mImageBitmap = (Bitmap) extras.get("data");

			Log.e("Socialize", mImageBitmap.getWidth() + "," + mImageBitmap.getHeight());
			
			if(mImageBitmap != null) {
				mImageView.setImageBitmap(mImageBitmap);
				btnPostPhoto.setVisibility(View.VISIBLE);
			}
			else {
				btnPostPhoto.setVisibility(View.GONE);
			}
		}
		
	}

}
