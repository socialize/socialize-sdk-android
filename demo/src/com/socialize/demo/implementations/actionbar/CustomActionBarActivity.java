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

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.socialize.CommentUtils;
import com.socialize.LikeUtils;
import com.socialize.ShareUtils;
import com.socialize.demo.DemoActivity;
import com.socialize.demo.R;
import com.socialize.entity.Like;
import com.socialize.error.SocializeException;
import com.socialize.listener.like.IsLikedListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.ui.dialog.SafeProgressDialog;


/**
 * @author Jason Polites
 *
 */
public class CustomActionBarActivity extends DemoActivity {
	
	// Normally you wouldn't use a local field like this because
	// the like is entity-specific whereas the Activity is general
	Boolean userLiked = null;

	@Override
	protected void onCreate() {
		setContentView(R.layout.custom_actionbar);
		
		final ImageView likeButton = (ImageView) findViewById(R.id.btnLike);
		final ImageView commentButton = (ImageView) findViewById(R.id.btnComment);
		final ImageView shareButton = (ImageView) findViewById(R.id.btnShare);
		
		final SafeProgressDialog progress = SafeProgressDialog.show(this);
		progress.setCancelable(false);
		
		// Setup like
		
		if(userLiked == null) {
			// Get the current like state
			LikeUtils.isLiked(this, entity.getKey(), new IsLikedListener() {
				@Override
				public void onNotLiked() {
					likeButton.setImageResource(R.drawable.icon_like);
					userLiked = false;
					progress.dismiss();
				}
				
				@Override
				public void onLiked(Like like) {
					likeButton.setImageResource(R.drawable.icon_like_hi);
					userLiked = true;
					progress.dismiss();
				}
			});
		}
		
		likeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(userLiked != null && userLiked) {
					LikeUtils.unlike(CustomActionBarActivity.this, entity.getKey(), new LikeDeleteListener() {
						@Override
						public void onError(SocializeException error) {
							handleError(CustomActionBarActivity.this, error);
						}
						
						@Override
						public void onDelete() {
							likeButton.setImageResource(R.drawable.icon_like);
							userLiked = false;
						}
					});
				}
				else {
					LikeUtils.like(CustomActionBarActivity.this, entity, new LikeAddListener() {
						
						@Override
						public void onError(SocializeException error) {
							handleError(CustomActionBarActivity.this, error);
						}
						
						@Override
						public void onCreate(Like result) {
							likeButton.setImageResource(R.drawable.icon_like_hi);
							userLiked = true;
						}
					});
				}
			}
		});
		
		
		// Setup comment
		commentButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CommentUtils.showCommentView(CustomActionBarActivity.this, entity);
			}
		});
		
		// Setup share
		shareButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShareUtils.showShareDialog(CustomActionBarActivity.this, entity);
			}
		});
	}
}
