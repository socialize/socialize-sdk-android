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

import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.socialize.CommentUtils;
import com.socialize.EntityUtils;
import com.socialize.LikeUtils;
import com.socialize.ShareUtils;
import com.socialize.api.action.share.SocialNetworkDialogListener;
import com.socialize.demo.DemoActivity;
import com.socialize.demo.R;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.ListResult;
import com.socialize.entity.UserEntityStats;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityListListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.dialog.SafeProgressDialog;


/**
 * @author Jason Polites
 *
 */
public class MultiActionBarActivity extends DemoActivity {

	String[] entityKeys = {
			"https://lh5.googleusercontent.com/-oPgrWjOC-6w/T9UWPfl2qoI/AAAAAAAABMQ/KtRKcWwRsJ0/s932/P1010444.JPG",	
			"https://lh6.googleusercontent.com/-Un85-Xa_06o/T9UWOZYzicI/AAAAAAAABMA/tJLz6FhsVe8/s932/P1010252.JPG",
			"https://lh5.googleusercontent.com/-ELoXZoz5xvE/T9UWO8W0Y3I/AAAAAAAABMI/UtqkHFBtEOg/s932/P1010206.JPG"
	};
	
	// Hold local like state
	boolean [] liked = new boolean[]{false, false, false};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.actionbar_multi);
		
		// This page has 3 entities
		final Entity entity0 = Entity.newInstance(entityKeys[0], "Machu Picchu");
		final Entity entity1 = Entity.newInstance(entityKeys[1], "Hawaii");
		final Entity entity2 = Entity.newInstance(entityKeys[2], "Waterfall");
		
		final ProgressDialog progress = SafeProgressDialog.show(this);
		
		// Next get the status of all three...
		EntityUtils.getEntities(this, new EntityListListener() {
			
			@Override
			public void onList(ListResult<Entity> result) {
				// We have the results.. iterate and update the UI
				List<Entity> items = result.getItems();
				
				for (Entity entity : items) {
					// locate the UI control corresponding to this entity
					if(entity.getKey().equals(entityKeys[0])) {
						checkLiked(R.id.btnLike0, 0, entity);
					}
					else if(entity.getKey().equals(entityKeys[1])) {
						checkLiked(R.id.btnLike1, 1, entity);
					}
					else if(entity.getKey().equals(entityKeys[2])) {
						checkLiked(R.id.btnLike2, 2, entity);
					}
				}
				
				// Set up the click listeners
				setLikeListener(R.id.btnLike0, 0, entity0);
				setLikeListener(R.id.btnLike1, 1, entity1);
				setLikeListener(R.id.btnLike2, 2, entity2);				
				
				// Setup comment listeners
				setCommentListener(R.id.btnComment0, entity0);
				setCommentListener(R.id.btnComment1, entity1);
				setCommentListener(R.id.btnComment1, entity1);
				
				// Setup share listeners
				setShareListener(R.id.btnShare0, 0, entity0);
				setShareListener(R.id.btnShare1, 1,entity1);
				setShareListener(R.id.btnShare2, 2,entity2);
				
				progress.dismiss();
			}
			
			@Override
			public void onError(SocializeException error) {
				// It's ok if an entity could not be found, it may just not exist yet.
				if(!isNotFoundError(error)) {
					progress.dismiss();
					handleError(MultiActionBarActivity.this, error);
				}
			}
			
		}, entityKeys);
	}
	
	protected void setLikeListener(int viewId, final int index, final Entity entity) {
		final ImageView view = (ImageView) findViewById(viewId);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				if(!liked[index]) {
					
					LikeUtils.like((Activity)v.getContext(), entity, new LikeAddListener() {
						
						@Override
						public void onError(SocializeException error) {
							handleError((Activity)v.getContext(), error);
						}
						
						@Override
						public void onCreate(Like result) {
							view.setImageResource(R.drawable.icon_like_hi);
							liked[index] = true;
						}
					});
				}
				else {
					view.setImageResource(R.drawable.icon_like);
					
					LikeUtils.unlike((Activity)v.getContext(), entity.getKey(), new LikeDeleteListener() {
						
						@Override
						public void onError(SocializeException error) {
							view.setImageResource(R.drawable.icon_like_hi);
							handleError((Activity)v.getContext(), error);
						}
						
						@Override
						public void onDelete() {
							liked[index] = false;
						}
					});
				}
			}
		});
	}
	
	protected void setCommentListener(int viewId, final Entity entity) {
		final ImageView view = (ImageView) findViewById(viewId);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CommentUtils.showCommentView((Activity) v.getContext(), entity);
			}
		});
	}

	protected void setShareListener(int viewId, final int index, final Entity entity) {
		final ImageView view = (ImageView) findViewById(viewId);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShareUtils.showShareDialog((Activity) v.getContext(), entity, new SocialNetworkDialogListener() {
					@Override
					public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
						postData.getPostValues().put("type", "photo");
						return false;
					}
				}, ShareUtils.DEFAULT);
			}
		});
	}
	
	protected void checkLiked(int viewId, int index, Entity entity) {
		ImageView view = (ImageView) findViewById(viewId);
		// Get the user data from the entity
		UserEntityStats userEntityStats = entity.getUserEntityStats();
		if(userEntityStats != null) {
			
			liked[index] = userEntityStats.isLiked();
			
			if(userEntityStats.isLiked()) {
				view.setImageResource(R.drawable.icon_like_hi);
			}
		}
	}
}
