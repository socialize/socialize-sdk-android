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

import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.User;
import com.socialize.error.SocializeApiError;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeLikeButton extends Button {
	
	private static final String socializens="http://getsocialize.com";
	
	public static enum State {ON,OFF,DISABLED,LOADING}
	
	private Drawable imageOn;
	private Drawable imageOff;
	private Drawable imageDisabled;
	private Drawable imageLoading;
	private String textOn;
	private String textOff;
	private String textDisabled = "---";
	private String textLoading = "...";
	private Entity entity;
	private Like like;
	
	private boolean showCount = true;
	
	private OnClickListener onClickListener;
	
	private State state = State.LOADING;
	
	private boolean loaded = false;
	private int resOn;
	private int resOff;
	private int resLoading;
	private int resDisabled;
	
	public SocializeLikeButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public SocializeLikeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public SocializeLikeButton(Context context) {
		super(context);
	}
	
	protected void init(AttributeSet attrs) {
		if(textOn == null)  textOn = attrs.getAttributeValue(socializens, "text_active");
		if(textOff == null)  textOff = attrs.getAttributeValue(socializens, "text_inactive");
		if(textLoading == null)  textLoading = attrs.getAttributeValue(socializens, "text_loading");
		if(textDisabled == null)  textDisabled = attrs.getAttributeValue(socializens, "text_disabled");
		
		if(StringUtils.isEmpty(textOn)) textOn = "Unlike";
		if(StringUtils.isEmpty(textOff)) textOff = "Like";
		if(StringUtils.isEmpty(textLoading)) textLoading = "...";
		if(StringUtils.isEmpty(textDisabled)) textDisabled = "---";
		
		showCount = attrs.getAttributeBooleanValue(socializens, "show_count", true);
		
		resOn = attrs.getAttributeResourceValue(socializens, "src_active", 0);
		resOff = attrs.getAttributeResourceValue(socializens, "src_inactive", 0);
		resLoading = attrs.getAttributeResourceValue(socializens, "src_loading", 0);
		resDisabled = attrs.getAttributeResourceValue(socializens, "src_disabled", 0);
		
		setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		
		super.setOnClickListener(getOnClickListener());
	}
		
	protected void setupDrawables() {

		if(imageOn == null && resOn > 0) {
			try {
				imageOn = getResources().getDrawable(resOn);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(imageOff == null && resOff > 0) {
			try {
				imageOff = getResources().getDrawable(resOff);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(imageDisabled == null && resDisabled > 0) {
			try {
				imageDisabled = getResources().getDrawable(resDisabled);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(imageLoading == null) {
			if(resLoading <= 0) {
				resLoading = R.attr.indeterminateDrawable;
			}
			
			try {
				imageLoading = getResources().getDrawable(resLoading);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}		
		
		if(imageOn == null) imageOn = getSocialize().getDrawable("icon_like_hi.png");
		if(imageOff == null) imageOff = getSocialize().getDrawable("icon_like.png");
		if(imageDisabled == null) imageDisabled = getSocialize().getDrawable("icon_like.png");
	}
	
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		
		if(!isInEditMode()) {
			if(visibility == VISIBLE) {
				if(!loaded) {
					load();
				}
				else {
					setState();
				}
			}
		}
	}
	
	protected void load() {
		loaded = true;
		
		setState(State.LOADING);
		
		if(!getSocialize().isInitialized()) {
			getSocialize().initAsync(getContext(), new SocializeInitListener() {
				
				@Override
				public void onError(SocializeException error) {
					logError("Error initializing Socialize", error);
					setState(State.DISABLED);
				}
				
				@Override
				public void onInit(Context context, IOCContainer container) {
					setupDrawables();
					doAuth();
				}
			});
		}
		else {
			setupDrawables();
			doAuth();
		}
	}
	
	public void reload() {
		loaded = false;
		load();
	}
	
	protected void doAuth() {
		if(!getSocialize().isAuthenticated()) {
			getSocialize().authenticate(getContext(), new SocializeAuthListener() {
				
				@Override
				public void onError(SocializeException error) {
					logError("Error authenticating with Socialize", error);
				}
				
				@Override
				public void onCancel() {
					setState(State.DISABLED);
				}
				
				@Override
				public void onAuthSuccess(SocializeSession session) {
					loadLike();
				}
				
				@Override
				public void onAuthFail(SocializeException error) {
					logError("Error authenticating with Socialize", error);
				}
			});
		}
		else {
			loadLike();
		}
	}
	
	protected void loadLike() {
		
		getSocialize().getLike(entity.getKey(), new LikeGetListener() {
			
			@Override
			public void onGet(Like like) {
				if(like != null) {
					SocializeLikeButton.this.like = like;
					SocializeLikeButton.this.entity = like.getEntity();
					setState(State.ON);
				}
				else {
					loadEntity();
				}
			}
			
			@Override
			public void onError(SocializeException error) {
				if(error instanceof SocializeApiError) {
					if(((SocializeApiError)error).getResultCode() == 404) {
						// no like
						loadEntity();
						// Don't log error
						return;
					}
				}	
				
				setState(State.DISABLED);
				
				logError("Error retrieving entity data", error);
			}
		});
	}
	
	protected void loadEntity() {
		getSocialize().getEntity(entity.getKey(), new EntityGetListener() {
			@Override
			public void onGet(Entity entity) {
				SocializeLikeButton.this.entity = entity;
				setState(State.OFF);
			}
			
			@Override
			public void onError(SocializeException error) {
				SocializeLogger logger = getSocialize().getLogger();
				if(logger != null && logger.isDebugEnabled()) {
					logger.debug("Error retrieving entity data.  This may be ok if the entity is new", error);
				}
				
				setState(State.OFF);
			}
		});
	}
	
	protected void logError(String msg, Exception error) {
		SocializeLogger logger = getSocialize().getLogger();
		if(logger != null) {
			logger.error(msg, error);
		}
		else {
			error.printStackTrace();	
		}
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	protected String getDisplayText() {
		String displayText = textDisabled;
		switch (state) {
			case ON:
				displayText = textOn;
				break;
			case OFF:
				displayText = textOff;
				break;
			case DISABLED:
				return textDisabled;
			case LOADING:
				return textLoading;
		}
		
		if(entity != null && entity.getEntityStats() != null) {
			if(showCount) {
				displayText += " (" + entity.getEntityStats().getLikes() + ")";
			}
		}
		
		return displayText;
	}
	
	protected OnClickListener getOnClickListener() {
		
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Activity activity = getActivity();
				
				if(activity != null) {
					if(state.equals(State.ON)) {
						if(like != null) {
							setState(State.LOADING);
							getSocialize().unlike(like.getId(), new LikeDeleteListener() {
								
								@Override
								public void onError(SocializeException error) {
									logError("Error removing like", error);
									setState(State.OFF);
								}
								
								@Override
								public void onDelete() {
									SocializeLikeButton.this.like = null;
									setState(State.OFF);
								}
							});
						}
					}
					else if(state.equals(State.OFF)) {
						
						SocializeSession session = getSocialize().getSession();
						
						if(session != null) {
							User user = session.getUser();
							
							if(user != null) {
								ShareOptions options = new ShareOptions();
								
								if(user.isAutoPostToFacebook() && getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
									if(user.isAutoPostToTwitter() && getSocialize().isAuthenticated(AuthProviderType.TWITTER)) {
										options.setShareTo(SocialNetwork.FACEBOOK, SocialNetwork.TWITTER);
									}
									else {
										options.setShareTo(SocialNetwork.FACEBOOK);
									}
									
								}
								else if(user.isAutoPostToTwitter() && getSocialize().isAuthenticated(AuthProviderType.TWITTER)) {
									options.setShareTo(SocialNetwork.TWITTER);
								}		
								
								setState(State.LOADING);
								
								getSocialize().like(activity, entity, options, new LikeAddListener() {
									
									@Override
									public void onError(SocializeException error) {
										logError("Error creating like", error);
										setState(State.OFF);
									}
									
									@Override
									public void onCreate(Like like) {
										SocializeLikeButton.this.like = like;
										setState(State.ON);
									}
								});
							}
						}
					}
				}
				
				if(onClickListener != null) {
					onClickListener.onClick(v);
				}
			}
		};
	}
	
	protected Activity getActivity() {
		Context ctx = getContext();
		if(ctx instanceof Activity) {
			return (Activity) ctx;
		}
		return null;
	}
	
	protected void setState() {
		if(like != null) {
			setState(State.ON);
		}
		else {
			setState(State.OFF);
		}
	}
	
	public void setState(State state) {
		this.state = state;
		
		switch (state) {
			case ON:
				setCompoundDrawablesWithIntrinsicBounds(imageOn, null, null, null);
				setText(getDisplayText());
				break;
			case OFF:
				setCompoundDrawablesWithIntrinsicBounds(imageOff, null, null, null);
				setText(getDisplayText());
				break;
			case DISABLED:
				setCompoundDrawablesWithIntrinsicBounds(imageDisabled, null, null, null);
				setText(textDisabled);
				break;	
			case LOADING:
				setCompoundDrawablesWithIntrinsicBounds(imageLoading, null, null, null);
				setText(textLoading);
				break;				
		}
	}
	
	public Drawable getImageOn() {
		return imageOn;
	}

	public void setImageOn(Drawable imageOn) {
		this.imageOn = imageOn;
	}
	
	public Drawable getImageOff() {
		return imageOff;
	}
	
	public void setImageOff(Drawable imageOff) {
		this.imageOff = imageOff;
	}

	public String getTextOn() {
		return textOn;
	}
	
	public void setTextOn(String textOn) {
		this.textOn = textOn;
	}
	
	public String getTextOff() {
		return textOff;
	}
	
	public void setTextOff(String textOff) {
		this.textOff = textOff;
	}
	
	public String getTextDisabled() {
		return textDisabled;
	}
	
	public void setTextDisabled(String textDisabled) {
		this.textDisabled = textDisabled;
	}

	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public String getTextLoading() {
		return textLoading;
	}
	
	public void setTextLoading(String textLoading) {
		this.textLoading = textLoading;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.onClickListener = l;
	}

	/**
	 * @return
	 * @deprecated
	 */
	public ActionButtonConfig getConfig() {
		return new ActionButtonConfig();
	}
}
