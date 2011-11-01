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
package com.socialize.ui.actionbar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.View;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.cache.CacheableEntity;
import com.socialize.ui.cache.EntityCache;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.ui.facebook.FacebookWallPoster;
import com.socialize.ui.share.ShareDialogFactory;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 *
 */
public class ActionBarLayoutView extends BaseView {

	private ActionBarButton commentButton;
	private ActionBarButton likeButton;
	private ActionBarButton shareButton;
	private ActionBarTicker ticker;
	
	private ActionBarItem viewsItem;
	private ActionBarItem commentsItem;
	private ActionBarItem likesItem;
	private ActionBarItem sharesItem;
	
	private Drawables drawables;
	private EntityCache entityCache;
	private SocializeLogger logger;
	
	private Drawable likeIcon;
	private Drawable likeIconHi;
	
	private IBeanFactory<ActionBarButton> buttonFactory;
	private IBeanFactory<ActionBarTicker> tickerFactory;
	private IBeanFactory<ActionBarItem> itemFactory;
	
	private ProgressDialogFactory progressDialogFactory;
	private CacheableEntity localEntity;
	private DeviceUtils deviceUtils;
	
	private FacebookWallPoster facebookWallPoster;
	private ShareDialogFactory shareDialogFactory;
	private ActionBarView actionBarView;
	
	public ActionBarLayoutView(Activity context, ActionBarView actionBarView) {
		super(context);
		this.actionBarView = actionBarView;
	}
	
	public void init() {
		
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("init called on " + getClass().getSimpleName());
		}
		
		likeIcon = drawables.getDrawable("icon_like.png", true);
		likeIconHi = drawables.getDrawable("icon_like_hi.png", true);

		Drawable commentIcon = drawables.getDrawable("icon_comment.png", true);
		Drawable viewIcon = drawables.getDrawable("icon_view.png", true);
		Drawable shareIcon = drawables.getDrawable("icon_share.png", true);
		Drawable commentBg = drawables.getDrawable("action_bar_button_hi.png", true, false, true);
		
		ticker = tickerFactory.getBean();
		
		viewsItem = itemFactory.getBean();
		commentsItem = itemFactory.getBean();
		likesItem = itemFactory.getBean();
		sharesItem = itemFactory.getBean();
		
		viewsItem.setIcon(viewIcon);
		commentsItem.setIcon(commentIcon);
		likesItem.setIcon(likeIcon);
		sharesItem.setIcon(shareIcon);
		
		ticker.addTickerView(viewsItem);
		ticker.addTickerView(commentsItem);
		ticker.addTickerView(likesItem);
		ticker.addTickerView(sharesItem);
		
		likeButton = buttonFactory.getBean();
		commentButton = buttonFactory.getBean();
		shareButton = buttonFactory.getBean();
		
		commentButton.setIcon(commentIcon);
		commentButton.setBackground(commentBg);
		
		likeButton.setIcon(likeIcon);
		likeButton.setBackground(commentBg);
		
		shareButton.setIcon(shareIcon);
		shareButton.setBackground(commentBg);
		
		commentButton.setListener(new ActionBarButtonListener() {
			@Override
			public void onClick(ActionBarButton button) {
				SocializeUI.getInstance().showCommentView(getActivity(), actionBarView.getEntityKey(), actionBarView.getEntityName(), actionBarView.isEntityKeyUrl());
			}
		});
		
		likeButton.setListener(new ActionBarButtonListener() {
			@Override
			public void onClick(ActionBarButton button) {
				postLike(likeButton);
			}
		});
		
		shareButton.setListener(new ActionBarButtonListener() {
			@Override
			public void onClick(ActionBarButton button) {
				if(shareDialogFactory != null) {
					shareDialogFactory.show(getContext(), actionBarView);
				}
			}
		});
		
		ticker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(android.view.View v) {
				ticker.skipToNext();
			}
		});
		
		LayoutParams masterParams = new LayoutParams(LayoutParams.FILL_PARENT, deviceUtils.getDIP(ActionBarView.ACTION_BAR_HEIGHT));
		masterParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		setLayoutParams(masterParams);
		
		int width = ActionBarView.ACTION_BAR_BUTTON_WIDTH;
		
		int likeWidth = width - 5;
		int commentWidth = width + 15;
		int shareWidth = width- 5;
		
		viewsItem.init();
		commentsItem.init();
		likesItem.init();
		sharesItem.init();
		
		ticker.init(LayoutParams.FILL_PARENT, 1.0f);
		likeButton.init(likeWidth, 0.0f);
		commentButton.init(commentWidth, 0.0f);
		shareButton.init(shareWidth, 0.0f);
		
		viewsItem.setText("--");
		commentsItem.setText("--");
		likesItem.setText("--");
		sharesItem.setText("--");
		
		likeButton.setText("--");
		shareButton.setText("Share");
		commentButton.setText("Comment");
		
		addView(ticker);
		addView(likeButton);
		addView(shareButton);
		addView(commentButton);
		
	}
	
	@Override
	protected void onViewLoad() {
		super.onViewLoad();
		
		final String entityKey = actionBarView.getEntityKey();
		
		ticker.startTicker();
		
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("onViewLoad called on " + getClass().getSimpleName());
		}
		
		CacheableEntity entity = entityCache.get(entityKey);
		
		if(entity == null) {
			getSocialize().view(entityKey, new ViewAddListener() {
				@Override
				public void onError(SocializeException error) {
					error.printStackTrace();
					getEntityData(entityKey);
				}
				
				@Override
				public void onCreate(View entity) {
					getEntityData(entityKey);
				}
			});
		}
		else {
//			entityCache.extendTTL(entityKey);
//			setEntityData(entity);
			getEntityData(entityKey);
		}
	}
	
	public void reload() {
		final String entityKey = actionBarView.getEntityKey();
		
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("onViewUpdate called on " + getClass().getSimpleName());
		}
		
		ticker.resetTicker();
		
		viewsItem.setText("--");
		commentsItem.setText("--");
		likesItem.setText("--");
		sharesItem.setText("--");
		likeButton.setText("--");
		
		getEntityData(entityKey);
	}
	
	@Override
	protected void onViewUpdate() {
		super.onViewUpdate();
		reload();
	}

	protected void postLike(final ActionBarButton button) {
		
		if(localEntity != null) {
			String entityKey = actionBarView.getEntityKey();
			
			button.showLoading();
			
			if(localEntity.isLiked()) {
				// Unlike
				getSocialize().unlike(localEntity.getLikeId(), new LikeDeleteListener() {
					
					@Override
					public void onError(SocializeException error) {
						logError("Error deleting like", error);
						button.hideLoading();
					}
					
					@Override
					public void onDelete() {
						localEntity.setLiked(false);
						localEntity.getEntity().setLikes(localEntity.getEntity().getLikes()-1);
						setEntityData(localEntity);
						button.hideLoading();
					}
				});
			}
			else {
				// Unlike
				getSocialize().like(entityKey, new LikeAddListener() {
					
					@Override
					public void onError(SocializeException error) {
						logError("Error posting like", error);
						button.hideLoading();
					}
					
					@Override
					public void onCreate(Like entity) {
						localEntity.getEntity().setLikes(localEntity.getEntity().getLikes()+1);
						localEntity.setLiked(true);
						localEntity.setLikeId(entity.getId());
						setEntityData(localEntity);
						button.hideLoading();
					}
				});
				
				// TODO: Inspect user prefs
				if(getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
					facebookWallPoster.postLike(getActivity(), actionBarView.getEntityKey(), actionBarView.getEntityName(), null, actionBarView.isEntityKeyUrl(), null);
				}
			}
		}
	}
	
	protected void getEntityData(final String entityKey) {
		
		// Get the like
		getSocialize().getLike(entityKey, new LikeGetListener() {
			
			@Override
			public void onGet(Like like) {
				CacheableEntity putEntity = entityCache.putEntity(like.getEntity());
				putEntity.setLiked(true);
				putEntity.setLikeId(like.getId());
				setEntityData(putEntity);
			}
			
			@Override
			public void onError(SocializeException error) {
				if(error instanceof SocializeApiError) {
					if(((SocializeApiError)error).getResultCode() == 404) {
						// no like
						getSocialize().getEntity(entityKey, new EntityGetListener() {
							@Override
							public void onGet(Entity entity) {
								CacheableEntity putEntity = entityCache.putEntity(entity);
								setEntityData(putEntity);
							}
							
							@Override
							public void onError(SocializeException error) {
								logError("Error retrieving entity data", error);
							}
						});
						
						// Don't log error
						return;
					}
				}
				
				logError("Error retrieving entity data", error);
			}
		});
	}
	
	protected void setEntityData(CacheableEntity ce) {
		this.localEntity = ce;
		
		Entity entity = ce.getEntity();
		
		viewsItem.setText(getCountText(entity.getViews()));
		commentsItem.setText(getCountText(entity.getComments()));
		likesItem.setText(getCountText(entity.getLikes()));
		sharesItem.setText(getCountText(entity.getShares()));
		
		if(ce.isLiked()) {
			likeButton.setText("Unlike");
			likeButton.setIcon(likeIconHi);
		}
		else {
			likeButton.setText("Like");
			likeButton.setIcon(likeIcon);
		}
	}
	
	protected String getCountText(Integer value) {
		String viewText = "";
		int iVal = value.intValue();
		if(iVal > 999) {
			viewText = "999+";
		}
		else {
			viewText = value.toString();
		}
		return viewText;
	}
	
	protected void logError(String msg, Exception error) {
		if(logger != null) {
			logger.error(msg, error);
		}
		else {
			error.printStackTrace();	
		}
	}
	
	// So we can mock for tests
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	public void setCommentButton(ActionBarButton commentButton) {
		this.commentButton = commentButton;
	}

	public void setLikeButton(ActionBarButton likeButton) {
		this.likeButton = likeButton;
	}

	public ActionBarButton getCommentButton() {
		return commentButton;
	}

	public ActionBarButton getLikeButton() {
		return likeButton;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setButtonFactory(IBeanFactory<ActionBarButton> buttonFactory) {
		this.buttonFactory = buttonFactory;
	}

	public void setEntityCache(EntityCache entityCache) {
		this.entityCache = entityCache;
	}

	public ProgressDialogFactory getProgressDialogFactory() {
		return progressDialogFactory;
	}

	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setTickerFactory(IBeanFactory<ActionBarTicker> tickerFactory) {
		this.tickerFactory = tickerFactory;
	}

	public void setItemFactory(IBeanFactory<ActionBarItem> itemFactory) {
		this.itemFactory = itemFactory;
	}

	public void setShareDialogFactory(ShareDialogFactory shareDialogFactory) {
		this.shareDialogFactory = shareDialogFactory;
	}

	public void setFacebookWallPoster(FacebookWallPoster facebookWallPoster) {
		this.facebookWallPoster = facebookWallPoster;
	}
}
