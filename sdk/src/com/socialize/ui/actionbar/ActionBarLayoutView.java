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
package com.socialize.ui.actionbar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import com.socialize.CommentUtils;
import com.socialize.LikeUtils;
import com.socialize.ShareUtils;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Entity;
import com.socialize.entity.EntityStats;
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
import com.socialize.ui.actionbar.OnActionBarEventListener.ActionBarEvent;
import com.socialize.ui.cache.CacheableEntity;
import com.socialize.ui.cache.EntityCache;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;
import com.socialize.config.SocializeConfig;
import java.util.Properties;
import android.content.Context;
import android.util.Log;
import java.io.IOException;

/**
 * @author Jason Polites
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
//	private AuthDialogFactory authDialogFactory;
	
	private DisplayUtils displayUtils;
	
	private ActionBarView actionBarView;
	
	final String loadingText = "...";
	
	private OnActionBarEventListener onActionBarEventListener;

	private static final int ACTION_BAR_BUTTONS_COUNT = 4;

	private static Activity contextActivity;

	
	public ActionBarLayoutView(Activity context, ActionBarView actionBarView) {
		super(context);
		contextActivity = context;
		this.actionBarView = actionBarView;
	}
	
	public void init() {
		
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("init called on " + getClass().getSimpleName());
		}
		
		likeIcon = drawables.getDrawable("icon_like.png");
		likeIconHi = drawables.getDrawable("icon_like_hi.png");

		Drawable commentIcon = drawables.getDrawable("icon_comment.png");
		Drawable viewIcon = drawables.getDrawable("icon_view.png");
		Drawable shareIcon = drawables.getDrawable("icon_share.png");
		
		Drawable commentBg = drawables.getDrawable("action_bar_button_hi.png#comment", true, false, true);
		Drawable shareBg = drawables.getDrawable("action_bar_button_hi.png#share", true, false, true);
		Drawable likeBg = drawables.getDrawable("action_bar_button_hi.png#like", true, false, true);
		
		boolean showShareButton = true;
		boolean showLikeButton = true;
		boolean showCommentButton = true;
		boolean showAlreadyLikedButton = true;

		Properties props = new Properties();
		InputStream is = null;
		try {
			is = contextActivity.getAssets().open(SocializeConfig.SOCIALIZE_PROPERTIES_PATH);
			props.load(is);

			String propertyString = props.getProperty(SocializeConfig.SOCIALIZE_SHARING_ENABLED);
			showShareButton = (propertyString == null) ? true : Boolean.parseBoolean(propertyString);
			Log.v("Socialize: ActionBarLayoutView", "should show share button? " + ((showShareButton) ? "YES" : "NO"));

			propertyString = props.getProperty(SocializeConfig.SOCIALIZE_LIKE_ENABLED);
			showLikeButton = (propertyString == null) ? true : Boolean.parseBoolean(propertyString);
			Log.v("Socialize: ActionBarLayoutView", "should show like button? " + ((showLikeButton) ? "YES" : "NO"));

			propertyString = props.getProperty(SocializeConfig.SOCIALIZE_COMMENTS_ENABLED);
			showCommentButton = (propertyString == null) ? true : Boolean.parseBoolean(propertyString);
			Log.v("Socialize: ActionBarLayoutView", "should show comment button? " + ((showCommentButton) ? "YES" : "NO"));

			propertyString = props.getProperty(SocializeConfig.SOCIALIZE_ALREADY_LIKED_ENABLED);
			showAlreadyLikedButton = (propertyString == null) ? true : Boolean.parseBoolean(propertyString);
			Log.v("Socialize: ActionBarLayoutView", "should show already liked button? " + ((showAlreadyLikedButton) ? "YES" : "NO"));
		} catch (IOException ignored) {
			// ignored IOException
		} finally {
			props = null;
			contextActivity = null;

			if (is != null) {
				try {
					is.close();
				} catch (IOException ignored) {
					// ignored IOException
				}
			}
		}

		int enabledButtonsCount = showShareButton ? 1 : 0;
		enabledButtonsCount += showLikeButton ? 1 : 0;
		enabledButtonsCount += showCommentButton ? 1 : 0;
		enabledButtonsCount += showAlreadyLikedButton ? 1 : 0;

		int width = ActionBarView.ACTION_BAR_BUTTON_WIDTH * ACTION_BAR_BUTTONS_COUNT / enabledButtonsCount + 1;
		
		int likeWidth = width - 5;
		int commentWidth = width + 15;
		int shareWidth = width - 5;
		
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
		likeButton.setBackground(likeBg);
		
		shareButton.setIcon(shareIcon);
		shareButton.setBackground(shareBg);
		
		commentButton.setListener(new ActionBarButtonListener() {
			@Override
			public void onClick(ActionBarButton button) {
				boolean consumed = false;
				
				if(onActionBarEventListener != null) {
					consumed = onActionBarEventListener.onClick(actionBarView, ActionBarEvent.COMMENT);
				}
				
				if(!consumed) {
					CommentUtils.showCommentView(getActivity(), actionBarView.getEntity());
				}
			}
		});
		
		likeButton.setListener(new ActionBarButtonListener() {
			@Override
			public void onClick(ActionBarButton button) {
				
				boolean consumed = false;
				
				if(onActionBarEventListener != null) {
					consumed = onActionBarEventListener.onClick(actionBarView, ActionBarEvent.LIKE);
				}
				
				if(!consumed) {
					doLike(likeButton);
				}
			}
		});
		
		shareButton.setListener(new ActionBarButtonListener() {
			@Override
			public void onClick(ActionBarButton button) {
				
				boolean consumed = false;
				
				if(onActionBarEventListener != null) {
					consumed = onActionBarEventListener.onClick(actionBarView, ActionBarEvent.SHARE);
				}
				
				if(!consumed) {
					ShareUtils.showShareDialog(getActivity(), actionBarView.getEntity());
				}
			}
		});
		
		ticker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(android.view.View v) {
				
				boolean consumed = false;
				
				if(onActionBarEventListener != null) {
					consumed = onActionBarEventListener.onClick(actionBarView, ActionBarEvent.VIEW);
				}	
				
				if(!consumed) {
					ticker.skipToNext();
				}
			}
		});
		
		LayoutParams masterParams = new LayoutParams(LayoutParams.FILL_PARENT, displayUtils.getDIP(ActionBarView.ACTION_BAR_HEIGHT));
		masterParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		setLayoutParams(masterParams);
		
		viewsItem.init();
		commentsItem.init();
		likesItem.init();
		sharesItem.init();
		
		ticker.init(LayoutParams.FILL_PARENT, 1.0f);
		likeButton.init(likeWidth, 0.0f);
		commentButton.init(commentWidth, 0.0f);
		shareButton.init(shareWidth, 0.0f);
		
		viewsItem.setText(loadingText);
		commentsItem.setText(loadingText);
		likesItem.setText(loadingText);
		sharesItem.setText(loadingText);
		
		likeButton.setText(loadingText);
		shareButton.setText("Share");
		commentButton.setText("Comment");
		
		if (showAlreadyLikedButton) addView(ticker);
		if (showLikeButton) addView(likeButton);
		if (showShareButton) addView(shareButton);
		if (showCommentButton) addView(commentButton);
	}
	
	@Override
	public void onViewLoad() {
		super.onViewLoad();
		doLoadSequence(false);
	}
	
	@Override
	public void onViewUpdate() {
		super.onViewUpdate();
		doLoadSequence(true);
	}
	
	protected void doLoadSequence(boolean reload) {
		final Entity userProvidedEntity = actionBarView.getEntity();
		
		if(userProvidedEntity != null) {
			if(reload) {
				ticker.resetTicker();
				viewsItem.setText(loadingText);
				commentsItem.setText(loadingText);
				likesItem.setText(loadingText);
				sharesItem.setText(loadingText);
				likeButton.setText(loadingText);
				
				if(onActionBarEventListener != null) {
					onActionBarEventListener.onUpdate(actionBarView);
				}	
			}
			else {
				ticker.startTicker();
				
				if(onActionBarEventListener != null) {
					onActionBarEventListener.onLoad(actionBarView);
				}	
			}
			
			updateEntity(userProvidedEntity, reload);
		}
		else {
			if(logger != null) {
				logger.warn("No entity provided to ActionBar.  Load sequence aborted.");
			}
		}
	}
	
	protected void updateEntity(final Entity entity, boolean reload) {

		CacheableEntity localEntity = getLocalEntity();
		
		if(localEntity == null) {
			getSocialize().view(getActivity(), entity, new ViewAddListener() {
				@Override
				public void onError(SocializeException error) {
					error.printStackTrace();
					getLike(entity.getKey());
				}
				
				@Override
				public void onCreate(View view) {
					// Entity will be set in like
					getLike(view.getEntity().getKey());
				}
			});
		}
		else {
			if(reload) {
				if(localEntity.isLiked()) {
					getLike(entity.getKey());
				}
				else {
					getEntity(entity.getKey());
				}
			}
			else {
				// Just set everything from the cached version
				setEntityData(localEntity);
			}
		}
	}
	
	public void reload() {
		if(actionBarView.getEntity() != null) {
			entityCache.remove(actionBarView.getEntity().getKey());
		}
		doLoadSequence(true);
	}

	protected void doLike(final ActionBarButton button) {
		final CacheableEntity localEntity = getLocalEntity();
		
		if(localEntity != null && localEntity.isLiked()) {
			// Unlike
			doUnLike(button, localEntity);
			return;
		}
		
		button.showLoading();
		
		LikeUtils.like(getActivity(), actionBarView.getEntity(), new LikeAddListener() {
			
			@Override
			public void onCancel() {
				button.hideLoading();
			}

			@Override
			public void onError(SocializeException error) {
				logError("Error posting like", error);
				button.hideLoading();
			}
			
			@Override
			public void onCreate(Like entity) {
				CacheableEntity localEntity = setLocalEntity(entity.getEntity());
				localEntity.setLiked(true);
				localEntity.setLikeId(entity.getId());
				setEntityData(localEntity);
				
				button.hideLoading();
				
				if(onActionBarEventListener != null) {
					onActionBarEventListener.onPostLike(actionBarView, entity);
				}
			}
		});
	}
	
	protected void doUnLike(final ActionBarButton button, final CacheableEntity localEntity) {
		button.showLoading();
		getSocialize().unlike(localEntity.getLikeId(), new LikeDeleteListener() {
			@Override
			public void onError(SocializeException error) {
				logError("Error deleting like", error);
				
				if(localEntity != null) {
					localEntity.setLiked(false);
					setEntityData(localEntity);
				}

				button.hideLoading();
			}
			
			@Override
			public void onDelete() {
				if(localEntity != null) {
					localEntity.setLiked(false);
					setEntityData(localEntity);
				}

				button.hideLoading();
				if(onActionBarEventListener != null) {
					onActionBarEventListener.onPostUnlike(actionBarView);
				}
			}
		});
	}
	
//	protected void doLike(final ActionBarButton button, final CacheableEntity localEntity, SocialNetwork...networks) {
//		
//		button.showLoading();
//		
//		ShareOptions options = new ShareOptions();
//		
//		if(networks == null || networks.length == 0) {
//			if(getSocialize().getSession().getUser().isAutoPostToFacebook() && getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
//				if(getSocialize().getSession().getUser().isAutoPostToTwitter() && getSocialize().isAuthenticated(AuthProviderType.TWITTER)) {
//					options.setShareTo(SocialNetwork.FACEBOOK, SocialNetwork.TWITTER);
//				}
//				else {
//					options.setShareTo(SocialNetwork.FACEBOOK);
//				}
//
//			}
//			else if(getSocialize().getSession().getUser().isAutoPostToTwitter() && getSocialize().isAuthenticated(AuthProviderType.TWITTER)) {
//				options.setShareTo(SocialNetwork.TWITTER);
//			}
//		}
//		else {
//			options.setShareTo(networks);
//		}
//		
//		getSocialize().like(getActivity(), localEntity.getEntity(), options, new LikeAddListener() {
//			
//			@Override
//			public void onError(SocializeException error) {
//				logError("Error posting like", error);
//				button.hideLoading();
//			}
//			
//			@Override
//			public void onCreate(Like entity) {
//				localEntity.setLiked(true);
//				localEntity.setLikeId(entity.getId());
//				button.hideLoading();
//				setEntityData(localEntity);
//				
//				if(onActionBarEventListener != null) {
//					onActionBarEventListener.onPostLike(actionBarView, entity);
//				}
//			}
//		});
//	}
	
	protected CacheableEntity getLocalEntity() {
		return entityCache.get(actionBarView.getEntity().getKey());
	}
	
	protected CacheableEntity setLocalEntity(Entity entity) {
		return entityCache.putEntity(entity);
	}
	
	protected void getLike(final String entityKey) {
		
		// Get the like
		getSocialize().getLike(entityKey, new LikeGetListener() {
			
			@Override
			public void onGet(Like like) {
				if(like != null) {
					CacheableEntity putEntity = setLocalEntity(like.getEntity());
					putEntity.setLiked(true);
					putEntity.setLikeId(like.getId());
					setEntityData(putEntity);
					
					if(onActionBarEventListener != null) {
						onActionBarEventListener.onGetLike(actionBarView, like);
					}
					
					if(onActionBarEventListener != null) {
						onActionBarEventListener.onGetEntity(actionBarView, like.getEntity());
					}	
				}
				else {
					getEntity(entityKey);
				}
			}
			
			@Override
			public void onError(SocializeException error) {
				if(error instanceof SocializeApiError) {
					if(((SocializeApiError)error).getResultCode() == 404) {
						// no like
						getEntity(entityKey);
						// Don't log error
						return;
					}
				}
				
				logError("Error retrieving entity data", error);
			}
		});
	}
	
	protected void getEntity(String entityKey) {
		getSocialize().getEntity(entityKey, new EntityGetListener() {
			@Override
			public void onGet(Entity entity) {
				CacheableEntity putEntity = setLocalEntity(entity);
				setEntityData(putEntity);
				
				if(onActionBarEventListener != null) {
					onActionBarEventListener.onGetEntity(actionBarView, entity);
				}	
			}
			
			@Override
			public void onError(SocializeException error) {
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Error retrieving entity data.  This may be ok if the entity is new", error);
				}
			}
		});
	}
	
	protected void setEntityData(CacheableEntity ce) {
		Entity entity = ce.getEntity();
		
		actionBarView.setEntity(entity);
		
		EntityStats stats = entity.getEntityStats();
		
		if(stats != null) {
			viewsItem.setText(getCountText(stats.getViews()));
			commentsItem.setText(getCountText(stats.getComments()));
			likesItem.setText(getCountText(stats.getLikes() + ((ce.isLiked()) ? 1 : 0)));
			sharesItem.setText(getCountText(stats.getShares()));
		}
		
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
	
	public ActionBarButton getShareButton() {
		return shareButton;
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

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public void setTickerFactory(IBeanFactory<ActionBarTicker> tickerFactory) {
		this.tickerFactory = tickerFactory;
	}

	public void setItemFactory(IBeanFactory<ActionBarItem> itemFactory) {
		this.itemFactory = itemFactory;
	}

	public void stopTicker() {
		ticker.stopTicker();
	}

	public void startTicker() {
		ticker.startTicker();
	}

	public void setOnActionBarEventListener(OnActionBarEventListener onActionBarEventListener) {
		this.onActionBarEventListener = onActionBarEventListener;
	}
}
