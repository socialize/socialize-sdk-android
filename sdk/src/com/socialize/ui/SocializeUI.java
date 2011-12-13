package com.socialize.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeInitListener;
import com.socialize.listener.SocializeListener;
import com.socialize.ui.action.ActionDetailActivity;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.comment.CommentDetailActivity;
import com.socialize.ui.comment.CommentView;
import com.socialize.ui.comment.OnCommentViewActionListener;
import com.socialize.ui.profile.ProfileActivity;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

public class SocializeUI {

	private static final SocializeUI instance = new SocializeUI();
	
	public static final String LOG_KEY = "Socialize";
	
	public static final String USER_ID = "socialize.user.id";
	public static final String COMMENT_ID = "socialize.comment.id";
	public static final String ENTITY_KEY = "socialize.entity.key";
	public static final String ENTITY_NAME = "socialize.entity.name";
	public static final String ENTITY_URL_AS_LINK = "socialize.entity.entityKey.link";
	
	public static final String DEFAULT_USER_ICON = "default_user_icon.png";
	public static final String SOCIALIZE_LOGO = "socialize_logo.png";
	public static final String BG_ACCENT = "bg_accent.png";
	
	public static final Map<String, SocializeListener> STATIC_LISTENERS = new HashMap<String, SocializeListener>();
	
	private IOCContainer container;
	private Drawables drawables;
	private final Properties customProperties = new Properties();
	private final Set<String> toBeRemoved = new HashSet<String>();
	private String[] beanOverrides;
	
	private SocializeEntityLoader entityLoader;
	
	public static SocializeUI getInstance() {
		return instance;
	}
	
	public SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	public void initSocialize(Context context) {
		String[] config = getConfig();
		getSocialize().init(context,config);
		getSocialize().getConfig().merge(customProperties, getPropertiesToBeRemoved());
	}
	
	public void initSocializeAsync(Context context, final SocializeInitListener listener) {
		
		String[] config = getConfig();
		
		SocializeInitListener overrideListener = new SocializeInitListener() {
			
			@Override
			public void onError(SocializeException error) {
				listener.onError(error);
			}
			
			@Override
			public void onInit(Context context, IOCContainer container) {
				getSocialize().getConfig().merge(customProperties, getPropertiesToBeRemoved());
				listener.onInit(context, container);
			}
		};
		
		getSocialize().initAsync(context, overrideListener, config);
		
	}
	
	// So we can mock
	protected Set<String> getPropertiesToBeRemoved() {
		return toBeRemoved;
	}
	
	protected String[] getConfig() {
		String[] config = null;
		
		if(!StringUtils.isEmpty(beanOverrides)) {
			config = new String[beanOverrides.length + 2];
			config[0] = "socialize_beans.xml";
			config[1] = "socialize_ui_beans.xml";
			for (int i = 0; i < beanOverrides.length; i++) {
				config[i+2] = beanOverrides[i];
			}
		}
		else {
			config = new String[]{"socialize_beans.xml", "socialize_ui_beans.xml"};
		}
		
		return config;
	}

	public void initUI(IOCContainer container) {
		this.container = container;
		if(container != null) {
			drawables = container.getBean("drawables");
		}
	}
	
	public void setContainer(IOCContainer container) {
		this.container = container;
	}

	public void destroy(Context context) {
		destroy(context, false);
	}
	
	public void destroy(Context context, boolean force) {
		customProperties.clear();
		getSocialize().destroy(force);
	}
	
	public View getView(String name) {
		return (View) container.getBean(name);
	}
	
	public Drawable getDrawable(String name, int density, boolean eternal) {
		return drawables.getDrawable(name, density, eternal);
	}
	
	public Drawable getDrawable(String name, boolean eternal) {
		return drawables.getDrawable(name, eternal);
	}
	
	/**
	 * Sets the credentials for your Socialize App.
	 * @param consumerKey Your consumer key, obtained via registration at http://getsocialize.com
	 * @param consumerSecret Your consumer secret, obtained via registration at http://getsocialize.com
	 */
	public void setSocializeCredentials(String consumerKey, String consumerSecret) {
		setCustomProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY, consumerKey);
		setCustomProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET, consumerSecret);
	}
	
	/**
	 * Sets the FB credentials for the current user if available.
	 * @param userId
	 * @param token
	 */
	public void setFacebookUserCredentials(String userId, String token) {
		setCustomProperty(SocializeConfig.FACEBOOK_USER_ID, userId);
		setCustomProperty(SocializeConfig.FACEBOOK_USER_TOKEN, token);
	}
	
	public void setDebugMode(boolean debug) {
		setCustomProperty(SocializeConfig.SOCIALIZE_DEBUG_MODE, String.valueOf(debug));
	}
	
	/**
	 * Sets the Facebook ID for FB authentication.  
	 * @param appId Your Facebook App Id, obtained from https://developers.facebook.com/
	 * @see https://developers.facebook.com/
	 */
	public void setFacebookAppId(String appId) {
		setCustomProperty(SocializeConfig.FACEBOOK_APP_ID, appId);
	}
	
	protected void setCustomProperty(String key, String value) {
		if(!StringUtils.isEmpty(value)) {
			customProperties.put(key, value);
		}
		else {
			customProperties.remove(key);
			toBeRemoved.add(key);
		}
	}
	
	/**
	 * Enables/disables Single Sign On for Facebook.
	 * @param enabled True if enabled.  Default is true.
	 */
	public void setFacebookSingleSignOnEnabled(boolean enabled) {
		setCustomProperty(SocializeConfig.FACEBOOK_SSO_ENABLED, String.valueOf(enabled));
	}
	
	/**
	 * Returns true if a Facebook ID has been set.
	 * @return
	 */
	public boolean isFacebookSupported() {
		return !StringUtils.isEmpty(getCustomConfigValue(SocializeConfig.FACEBOOK_APP_ID));
	}
	
	public String getCustomConfigValue(String key) {
		
		SocializeService socialize = getSocialize();
		SocializeConfig config = socialize.getConfig();
		
		if(config != null) {
			return config.getProperty(key);
		}
		
		return null;
	}
	
	public void showCommentView(Activity context, String entityKey, String entityName, boolean entityKeyIsUrl, OnCommentViewActionListener listener) {
		STATIC_LISTENERS.put(CommentView.COMMENT_LISTENER, listener);
		showCommentView(context, entityKey, entityName, entityKeyIsUrl);
	}
	
	public void showCommentView(Activity context, String entityKey, OnCommentViewActionListener listener) {
		STATIC_LISTENERS.put(CommentView.COMMENT_LISTENER, listener);
		showCommentView(context, entityKey);
	}

	public void showCommentView(Activity context, String entityKey) {
		Intent i = newIntent(context, CommentActivity.class);
		i.putExtra(ENTITY_KEY, entityKey);
		try {
			context.startActivity(i);
		} 
		catch (ActivityNotFoundException e) {
			Log.e(LOG_KEY, "Could not find CommentActivity.  Make sure you have added this to your AndroidManifest.xml");
		}	
	}
	
	/**
	 * 
	 * @param context
	 * @param entityKey
	 * @param entityName
	 * @param entityKeyIsUrl
	 */
	public void showCommentView(Activity context, String entityKey, String entityName, boolean entityKeyIsUrl) {
		Intent i = newIntent(context, CommentActivity.class);
		i.putExtra(ENTITY_KEY, entityKey);
		i.putExtra(ENTITY_NAME, entityName);
		i.putExtra(ENTITY_URL_AS_LINK, entityKeyIsUrl);
		
		try {
			context.startActivity(i);
		} 
		catch (ActivityNotFoundException e) {
			Log.e(LOG_KEY, "Could not find CommentActivity.  Make sure you have added this to your AndroidManifest.xml");
		}
	}
	
	public void showUserProfileView(Activity context, String userId) {
		Intent i = newIntent(context, ProfileActivity.class);
		i.putExtra(USER_ID, userId);
		try {
			context.startActivity(i);
		} 
		catch (ActivityNotFoundException e) {
			Log.e(LOG_KEY, "Could not find ProfileActivity.  Make sure you have added this to your AndroidManifest.xml");
		}
	}
	
	public void showUserProfileViewForResult(Activity context, String userId, int requestCode) {
		Intent i = newIntent(context, ProfileActivity.class);
		i.putExtra(USER_ID, userId);
		
		try {
			context.startActivityForResult(i, requestCode);
		} 
		catch (ActivityNotFoundException e) {
			Log.e(LOG_KEY, "Could not find ProfileActivity.  Make sure you have added this to your AndroidManifest.xml");
		}	
	}
	
	/**
	 * Displays the detail view for a single Socialize action.
	 * @param context
	 * @param userId
	 * @param commentId
	 * @param requestCode
	 */
	public void showActionDetailViewForResult(Activity context, User user, SocializeAction action, int requestCode) {
		Intent i = newIntent(context, ActionDetailActivity.class);
		i.putExtra(USER_ID, user.getId().toString());
		i.putExtra(COMMENT_ID, action.getId().toString());
		
		try {
			context.startActivityForResult(i, requestCode);
		} 
		catch (ActivityNotFoundException e) {
			// Revert to legacy
			i.setClass(context, CommentDetailActivity.class);
			try {
				context.startActivityForResult(i, requestCode);
				Log.w(LOG_KEY, "Using legacy CommentDetailActivity.  Please update your AndroidManifest.xml to use ActionDetailActivity");
			} 
			catch (ActivityNotFoundException e2) {
				Log.e(LOG_KEY, "Could not find ActionDetailActivity.  Make sure you have added this to your AndroidManifest.xml");
			}
		}
	}	
	
	/**
	 * 
	 * @param context
	 * @param userId
	 * @param commentId
	 * @param requestCode
	 * @deprecated Use showActionDetailViewForResult
	 */
	@Deprecated
	public void showCommentDetailViewForResult(Activity context, String userId, String commentId, int requestCode) {
		Intent i = newIntent(context, ActionDetailActivity.class);
		i.putExtra(USER_ID, userId);
		i.putExtra(COMMENT_ID, commentId);
		
		try {
			context.startActivityForResult(i, requestCode);
		} 
		catch (ActivityNotFoundException e) {
			// Revert to legacy
			i.setClass(context, CommentDetailActivity.class);
			try {
				context.startActivityForResult(i, requestCode);
				Log.w(LOG_KEY, "Using legacy CommentDetailActivity.  Please update your AndroidManifest.xml to use ActionDetailActivity");
			} 
			catch (ActivityNotFoundException e2) {
				Log.e(LOG_KEY, "Could not find ActionDetailActivity.  Make sure you have added this to your AndroidManifest.xml");
			}
		}
	}
	
	protected Intent newIntent(Activity context, Class<?> cls) {
		return new Intent(context, cls);
	}
	
	protected RelativeLayout newRelativeLayout(Activity parent) {
		return new RelativeLayout(parent);
	}
	
	protected ActionBarView newActionBarView(Activity parent) {
		return new ActionBarView(parent);
	}
	
	protected LayoutParams newLayoutParams(int width, int height) {
		return new LayoutParams(width, height);
	}
	
	protected ScrollView newScrollView(Activity parent) {
		return new ScrollView(parent);
	}
	
	public void setEntityName(Activity context, String name) {
		Intent intent = context.getIntent();
		Bundle extras = getExtras(intent);
		extras.putString(ENTITY_NAME, name);
		intent.putExtras(extras);
	}
	
	public void setUseEntityUrlAsLink(Activity context, boolean asLink) {
		Intent intent = context.getIntent();
		Bundle extras = getExtras(intent);
		extras.putBoolean(ENTITY_URL_AS_LINK, asLink);
		intent.putExtras(extras);
	}

	/**
	 * 
	 * @param context
	 * @param intent
	 * @param entityKey
	 * @deprecated Use setEntityKey
	 */
	@Deprecated
	public void setEntityUrl(Activity context, Intent intent, String entityKey) {
		Bundle extras = getExtras(intent);
		extras.putString(ENTITY_KEY, entityKey);
		intent.putExtras(extras);
	}
	
	/**
	 * 
	 * @param context
	 * @param entityKey
	 * @deprecated Use setEntityKey
	 */
	@Deprecated
	public void setEntityUrl(Activity context, String entityKey) {
		Intent intent = context.getIntent();
		setEntityUrl(context, intent, entityKey);
	}
	
	public void setEntityKey(Activity context, Intent intent, String entityKey) {
		Bundle extras = getExtras(intent);
		extras.putString(ENTITY_KEY, entityKey);
		intent.putExtras(extras);
	}
	
	public void setEntityKey(Activity context, String entityKey) {
		Intent intent = context.getIntent();
		setEntityKey(context, intent, entityKey);
	}	
	
	public void setUserId(Activity context, String userId) {
		Intent intent = context.getIntent();
		Bundle extras = getExtras(intent);
		extras.putString(USER_ID, userId);
		intent.putExtras(extras);
	}
	
	public View showActionBar(Activity parent, View original, String entityKey) {
		return showActionBar(parent, original, entityKey, null, true, true, null);
	}
	
	public View showActionBar(Activity parent, View original, String entityKey, ActionBarListener listener) {
		return showActionBar(parent, original, entityKey, null, true, true, listener);
	}
	
	public View showActionBar(Activity parent, View original, String entityKey, ActionBarOptions options, ActionBarListener listener) {
		return showActionBar(parent, original, entityKey, options.getEntityName(), options.isEntityKeyUrl(), options.isAddScrollView(), listener);
	}
	
	public View showActionBar(Activity parent, int resId, String entityKey) {
		return showActionBar(parent, resId, entityKey, null, true, true, null);
	}
	
	public View showActionBar(Activity parent, int resId, String entityKey, ActionBarListener listener) {
		return showActionBar(parent, resId, entityKey, null, true, true, listener);
	}
	
	public View showActionBar(Activity parent, int resId, String entityKey, ActionBarOptions options) {
		return showActionBar(parent, resId, entityKey, options.getEntityName(), options.isEntityKeyUrl(), options.isAddScrollView(), null);
	}
		
	public View showActionBar(Activity parent, int resId, String entityKey, ActionBarOptions options, ActionBarListener listener) {
		return showActionBar(parent, resId, entityKey, options.getEntityName(), options.isEntityKeyUrl(), options.isAddScrollView(), listener);
	}
	
	protected View showActionBar(Activity parent, int resId, String entityKey, String entityName, boolean isEntityKeyUrl, boolean addScrollView, ActionBarListener listener) {
		View original = inflateView(parent, resId);
		return showActionBar(parent, original, entityKey, entityName, isEntityKeyUrl, addScrollView, listener);
	}
	
	protected View inflateView(Activity parent, int resId) {
		LayoutInflater layoutInflater = (LayoutInflater) parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		return layoutInflater.inflate(resId, null);
	}
	
	protected View showActionBar(Activity parent, View original, String entityKey, String entityName, boolean isEntityKeyUrl, boolean addScrollView, ActionBarListener listener) {
		RelativeLayout barLayout = newRelativeLayout(parent);
		RelativeLayout originalLayout = newRelativeLayout(parent);
		
		ActionBarView socializeActionBar = newActionBarView(parent);
		socializeActionBar.assignId(original);
		socializeActionBar.setEntityKey(entityKey);
		socializeActionBar.setEntityName(entityName);
		socializeActionBar.setEntityKeyIsUrl(isEntityKeyUrl);
		
		LayoutParams barParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		barParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		LayoutParams originalParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		originalParams.addRule(RelativeLayout.ABOVE, socializeActionBar.getId());
		
		socializeActionBar.setLayoutParams(barParams);
		originalLayout.setLayoutParams(originalParams);
		
		if(listener != null) {
			listener.onCreate(socializeActionBar);
		}
		
		if(addScrollView && !(original instanceof ScrollView) ) {
			LayoutParams scrollViewParams = newLayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			ScrollView scrollView = newScrollView(parent);
			scrollView.setFillViewport(true);
			scrollView.setLayoutParams(scrollViewParams);
			scrollView.addView(original);
			scrollView.setScrollContainer(false);
			originalLayout.addView(scrollView);
		}
		else {
			originalLayout.addView(original);
		}
		
		barLayout.addView(originalLayout);
		barLayout.addView(socializeActionBar);
		
		return barLayout;
	}
	
	protected Bundle getExtras(Intent intent) {
		Bundle extras = intent.getExtras();
		if(extras == null) {
			extras = new Bundle();
		}	
		return extras;
	}
	
	public Properties getCustomProperties() {
		return customProperties;
	}

	/**
	 * EXPERT ONLY (Not documented)
	 * @param beanOverride
	 */
	void setBeanOverrides(String...beanOverrides) {
		this.beanOverrides = beanOverrides;
	}
	
	/**
	 * EXPERT ONLY (Not documented)
	 * @return
	 */
	IOCContainer getContainer() {
		return container;
	}
	
	public SocializeEntityLoader getEntityLoader() {
		return entityLoader;
	}

	public void setEntityLoader(SocializeEntityLoader entityLoader) {
		this.entityLoader = entityLoader;
	}
}
