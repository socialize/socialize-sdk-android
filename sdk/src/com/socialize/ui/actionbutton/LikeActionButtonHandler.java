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
package com.socialize.ui.actionbutton;

import android.app.Activity;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.networks.ShareOptions;

/**
 * @author Jason Polites
 *
 */
public class LikeActionButtonHandler extends BaseActionButtonHandler<Like> {

	private boolean liked = false;
	private long likeId;
	
	@Override
	protected void handleLoad(final Activity context, Entity entity, final OnActionButtonEventListener<Like> listener) {
		getSocialize().getLike(entity.getKey(), new LikeGetListener() {
			
			@Override
			public void onGet(Like like) {
				if(like != null) {
					liked = true;
					likeId = like.getId();
				}
				else {
					liked = false;
				}
				
				if(listener != null) {
					listener.onLoad(context, like);
				}
			}
			
			@Override
			public void onError(SocializeException error) {
				if(error instanceof SocializeApiError) {
					if(((SocializeApiError)error).getResultCode() == 404) {
						// no like
						liked = false;
						
						if(listener != null) {
							listener.onLoad(context, null);
						}
						
						// Don't log error
						return;
					}
				}
				
				if(listener != null) {
					listener.onError(context, error);
				}
			}
		});
	}

	@Override
	protected void handleAfterLoad(Activity context, ActionButton<Like> button, Like action) {
		if(liked) {
			button.setState(ActionButtonState.ACTIVE);
		}
		else {
			button.setState(ActionButtonState.INACTIVE);
		}
	}

	@Override
	protected void handleAfterAction(Activity context, ActionButton<Like> button, Like action) {
		if(liked) {
			button.setState(ActionButtonState.ACTIVE);
		}
		else {
			button.setState(ActionButtonState.INACTIVE);
		}
	}

	@Override
	protected void handleAction(final Activity context, Entity entity, ShareOptions shareOptions, final OnActionButtonEventListener<Like> listener) {
		
		if(liked) {
			getSocialize().unlike(likeId, new LikeDeleteListener() {
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onError(context, error);
					}
				}
				
				@Override
				public void onDelete() {
					liked = false;
					if(listener != null) {
						listener.onAfterAction(context, null);
					}					
				}
			});	
		}
		else {
			getSocialize().like(context, entity, shareOptions, new LikeAddListener() {
				
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onError(context, error);
					}
				}
				
				@Override
				public void onCreate(Like like) {
					liked = true;
					likeId = like.getId();
					if(listener != null) {
						listener.onAfterAction(context, like);
					}
				}
			});	
		}
	}

	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
}
