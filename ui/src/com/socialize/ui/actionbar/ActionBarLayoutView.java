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
//import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IBeanFactory;
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
import com.socialize.ui.BaseView;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.cache.CacheableEntity;
import com.socialize.ui.cache.EntityCache;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
public class ActionBarLayoutView extends BaseView {

	private String entityKey;
	private ActionBarButton commentButton;
	private ActionBarButton likeButton;
	private ActionBarButton viewButton;
	
	private Drawables drawables;
	private EntityCache entityCache;
	private SocializeLogger logger;
	
	private Drawable likeIcon;
	private Drawable likeIconHi;
	
	private IBeanFactory<ActionBarButton> buttonFactory;
	
	private ProgressDialogFactory progressDialogFactory;
	private CacheableEntity localEntity;
	private DeviceUtils deviceUtils;
	
	public ActionBarLayoutView(Activity context, String entityKey) {
		super(context);
		this.entityKey = entityKey;
	}
	
	public void init(final Activity context) {
		
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("init called on " + getClass().getSimpleName());
		}

		Drawable commentIcon = drawables.getDrawable("icon_comment.png");
		likeIcon = drawables.getDrawable("icon_like.png");
		likeIconHi = drawables.getDrawable("icon_like_hi.png");
		Drawable viewIcon = drawables.getDrawable("icon_view.png");
		Drawable commentBg = drawables.getDrawable("action_bar_button_hi.png", true, false, true);
		Drawable viewBg = drawables.getDrawable("action_bar_button.png", true, false, true);
		
		viewButton = buttonFactory.getBean();
		likeButton = buttonFactory.getBean();
		commentButton = buttonFactory.getBean();
		
		viewButton.setIcon(viewIcon);
		viewButton.setBackground(viewBg);
		
		commentButton.setIcon(commentIcon);
		commentButton.setBackground(commentBg);
		
		commentButton.setListener(new ActionBarButtonListener() {
			@Override
			public void onClick(ActionBarButton button) {
				SocializeUI.getInstance().showCommentView(context, entityKey);
			}
		});
		
		likeButton.setListener(new ActionBarButtonListener() {
			@Override
			public void onClick(ActionBarButton button) {
				postLike();
			}
		});
		
		likeButton.setIcon(likeIcon);
		likeButton.setBackground(commentBg);
		
		LayoutParams masterParams = new LayoutParams(LayoutParams.FILL_PARENT, deviceUtils.getDIP(ActionBarView.ACTION_BAR_HEIGHT));
		masterParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		setLayoutParams(masterParams);
		
		viewButton.init(context, LayoutParams.FILL_PARENT, 1.0f);
		likeButton.init(context, 90, 0.0f);
		commentButton.init(context, 90, 0.0f);
		
		viewButton.setText("--");
		likeButton.setText("--");
		commentButton.setText("--");
		
		addView(viewButton);
		addView(likeButton);
		addView(commentButton);
	}
	
	@Override
	protected void onViewLoad() {
		super.onViewLoad();
		
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("onViewLoad called on " + getClass().getSimpleName());
		}
		
		CacheableEntity entity = entityCache.get(entityKey);
		
		if(entity == null) {
			getSocialize().view(entityKey, new ViewAddListener() {
				@Override
				public void onError(SocializeException error) {
					error.printStackTrace();
					getEntityData();
				}
				
				@Override
				public void onCreate(View entity) {
					getEntityData();
				}
			});
		}
		else {
			entityCache.extendTTL(entityKey);
			setEntityData(entity);
		}
	}
	
	@Override
	protected void onViewUpdate() {
		if(logger != null && logger.isInfoEnabled()) {
			logger.info("onViewUpdate called on " + getClass().getSimpleName());
		}
		
		super.onViewUpdate();
		
		viewButton.setText("--");
		likeButton.setText("--");
		commentButton.setText("--");
		
		getEntityData();
	}

	protected void postLike() {
		
		if(localEntity != null) {
			
			if(localEntity.isLiked()) {
//				final ProgressDialog dialog = progressDialogFactory.show(getContext(), "Like", "Removing like...");
//				dialog.setCancelable(true);
				
				// Unlike
				getSocialize().unlike(localEntity.getLikeId(), new LikeDeleteListener() {
					
					@Override
					public void onError(SocializeException error) {
						logError("Error deleting like", error);
//						dialog.dismiss();
					}
					
					@Override
					public void onDelete() {
//						dialog.dismiss();
						localEntity.setLiked(false);
						localEntity.getEntity().setLikes(localEntity.getEntity().getLikes()-1);
						setEntityData(localEntity);
					}
				});
			}
			else {
//				final ProgressDialog dialog = progressDialogFactory.show(getContext(), "Like", "Posting like...");
				
				// Unlike
				getSocialize().like(entityKey, new LikeAddListener() {
					
					@Override
					public void onError(SocializeException error) {
						logError("Error posting like", error);
//						dialog.dismiss();
					}
					
					@Override
					public void onCreate(Like entity) {
//						dialog.dismiss();
						localEntity.getEntity().setLikes(localEntity.getEntity().getLikes()+1);
						localEntity.setLiked(true);
						localEntity.setLikeId(entity.getId());
						setEntityData(localEntity);
					}
				});
			}
		}
	}
	
	protected void getEntityData() {
		
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
		viewButton.setText(entity.getViews().toString());
		likeButton.setText(entity.getLikes().toString());
		commentButton.setText(entity.getComments().toString());
		
		if(ce.isLiked()) {
			likeButton.setIcon(likeIconHi);
		}
		else {
			likeButton.setIcon(likeIcon);
		}
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

	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}

	public void setCommentButton(ActionBarButton commentButton) {
		this.commentButton = commentButton;
	}

	public void setLikeButton(ActionBarButton likeButton) {
		this.likeButton = likeButton;
	}

	public void setViewButton(ActionBarButton viewButton) {
		this.viewButton = viewButton;
	}

	public String getEntityKey() {
		return entityKey;
	}

	public ActionBarButton getCommentButton() {
		return commentButton;
	}

	public ActionBarButton getLikeButton() {
		return likeButton;
	}

	public ActionBarButton getViewButton() {
		return viewButton;
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
}
