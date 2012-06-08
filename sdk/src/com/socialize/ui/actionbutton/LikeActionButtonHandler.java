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
package com.socialize.ui.actionbutton;

import android.app.Activity;
import com.socialize.LikeUtils;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.api.action.like.LikeOptions;
import com.socialize.entity.Entity;
import com.socialize.entity.EntityStats;
import com.socialize.entity.Like;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.cache.EntityCache;

/**
 * @author Jason Polites
 *
 */
@Deprecated
public class LikeActionButtonHandler extends BaseActionButtonHandler<Like> {

	private boolean liked = false;
	private long likeId;
	
	private EntityCache entityCache;
	
	@Override
	protected void handleLoad(final Activity context, final Entity entity, final OnActionButtonEventListener<Like> listener) {
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
				
				handleLoadEvent(context, entity, like, listener);
			}
			
			@Override
			public void onError(SocializeException error) {
				if(error instanceof SocializeApiError) {
					if(((SocializeApiError)error).getResultCode() == 404) {
						// no like
						liked = false;
						
						handleLoadEvent(context, entity, null, listener);
						
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

	protected void handleLoadEvent(final Activity context, final Entity entity, Like like, final OnActionButtonEventListener<Like> listener) {
		if(listener != null) {
			if(like == null) {
				// Get entity
				getSocialize().getEntity(entity.getKey(), new EntityGetListener() {
					@Override
					public void onGet(Entity entity) {
						listener.onLoad(context, null, entity);
					}
					
					@Override
					public void onError(SocializeException error) {
						
						if(error instanceof SocializeApiError) {
							if(((SocializeApiError)error).getResultCode() == 404) {
								// no entity
								getSocialize().addEntity(context, entity, new EntityAddListener() {
									@Override
									public void onError(SocializeException error) {
										listener.onError(context, error);
									}
									@Override
									public void onCreate(Entity entity) {
										listener.onLoad(context, null, entity);
									}
								});
								
								// Don't log error
								return;
							}
						}						
						
						listener.onError(context, error);
					}
				});
			}
			else {
				listener.onLoad(context, like, like.getEntity());
			}
		}
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
	protected void handleAction(final Activity context, final Entity entity, ShareOptions shareOptions, final OnActionButtonEventListener<Like> listener, SocialNetwork...networks) {
		
		if(liked) {
			liked = false;
			getSocialize().unlike(likeId, new LikeDeleteListener() {
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onError(context, error);
					}
				}
				
				@Override
				public void onDelete() {
					entityCache.remove(entity.getKey());
					if(listener != null) {
						getSocialize().getEntity(entity.getKey(), new EntityGetListener() {
							@Override
							public void onGet(Entity entity) {
								listener.onAfterAction(context, null, entity);
							}
							
							@Override
							public void onError(SocializeException error) {
								listener.onError(context, error);
							}
						});						
					}
				}
			});	
		}
		else {
			
			LikeOptions likeOptions = LikeUtils.getUserLikeOptions(context);
			likeOptions.merge(shareOptions);
			
			LikeUtils.like(context, entity, likeOptions, new LikeAddListener() {
				@Override
				public void onError(SocializeException error) {
					if(listener != null) {
						listener.onError(context, error);
					}
				}
				
				@Override
				public void onCreate(Like like) {
					entityCache.remove(entity.getKey());
					liked = true;
					likeId = like.getId();
					if(listener != null) {
						listener.onAfterAction(context, like, like.getEntity());
					}
				}
			}, networks);
			
		}
	}

	public void setEntityCache(EntityCache entityCache) {
		this.entityCache = entityCache;
	}

	@Override
	public int getCountForAction(Entity entity) {
		if(entity != null) {
			EntityStats entityStats = entity.getEntityStats();
			if(entityStats != null) {
				return entityStats.getLikes();
			}
		}
		return 0;
	}

	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
}
