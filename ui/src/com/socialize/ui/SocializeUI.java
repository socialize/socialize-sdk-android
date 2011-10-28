package com.socialize.ui;

import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.ui.actionbar.ActionBarView;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.ui.comment.CommentDetailActivity;
import com.socialize.ui.profile.ProfileActivity;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

public class SocializeUI {

	private static final SocializeUI instance = new SocializeUI();
	
	public static final String USER_ID = "socialize.user.id";
	public static final String COMMENT_ID = "socialize.comment.id";
	public static final String ENTITY_KEY = "socialize.entity.key";
	public static final String ENTITY_NAME = "socialize.entity.name";
	public static final String ENTITY_DESCRIPTION = "socialize.entity.desc";
	public static final String ENTITY_URL_AS_LINK = "socialize.entity.url.link";
	
	public static final String DEFAULT_USER_ICON = "default_user_icon.png";
	public static final String SOCIALIZE_LOGO = "socialize_logo.png";
	public static final String BG_ACCENT = "bg_accent.png";
	
	private IOCContainer container;
	private Drawables drawables;
	private final Properties customProperties = new Properties();
	private String beanOverride;
	
	public static final SocializeUI getInstance() {
		return instance;
	}
	
	public SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	public void initSocialize(Context context) {
		
		String[] config = null;
		
		if(!StringUtils.isEmpty(beanOverride)) {
			config = new String[]{"socialize_beans.xml", "socialize_ui_beans.xml", beanOverride};
		}
		else {
			config = new String[]{"socialize_beans.xml", "socialize_ui_beans.xml"};
		}
		
		getSocialize().init(context,config);
		getSocialize().getConfig().merge(customProperties);
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
		getSocialize().destroy();
	}
	
	public View getView(String name) {
		return (View) container.getBean(name);
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
		customProperties.put(SocializeConfig.SOCIALIZE_CONSUMER_KEY, consumerKey);
		customProperties.put(SocializeConfig.SOCIALIZE_CONSUMER_SECRET, consumerSecret);
	}
	
	/**
	 * Sets the FB credentials for the current user if available.
	 * @param userId
	 * @param token
	 */
	public void setFacebookUserCredentials(String userId, String token) {
		customProperties.put(SocializeConfig.FACEBOOK_USER_ID, userId);
		customProperties.put(SocializeConfig.FACEBOOK_USER_TOKEN, token);
	}
	
	public void setDebugMode(boolean debug) {
		customProperties.put(SocializeConfig.SOCIALIZE_DEBUG_MODE, String.valueOf(debug));
	}
	
	/**
	 * Sets the Facebook ID for FB authentication.  
	 * @param appId Your Facebook App Id, obtained from https://developers.facebook.com/
	 * @see https://developers.facebook.com/
	 */
	public void setFacebookAppId(String appId) {
		customProperties.put(SocializeConfig.FACEBOOK_APP_ID, appId);
	}
	
	/**
	 * Enables/disables Single Sign On for Facebook.
	 * @param enabled True if enabled.  Default is true.
	 */
	public void setFacebookSingleSignOnEnabled(boolean enabled) {
		customProperties.put(SocializeConfig.FACEBOOK_SSO_ENABLED, String.valueOf(enabled));
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
	
	public void showCommentView(Activity context, String url) {
		Intent i = new Intent(context, CommentActivity.class);
		i.putExtra(ENTITY_KEY, url);
		context.startActivity(i);
	}
	
	public void showUserProfileView(Activity context, String userId) {
		Intent i = new Intent(context, ProfileActivity.class);
		i.putExtra(USER_ID, userId);
		context.startActivity(i);
	}
	
	public void showUserProfileViewForResult(Activity context, String userId, int requestCode) {
		Intent i = new Intent(context, ProfileActivity.class);
		i.putExtra(USER_ID, userId);
		context.startActivityForResult(i, requestCode);
	}
	
	public void showCommentDetailViewForResult(Activity context, String userId, String commentId, int requestCode) {
		Intent i = new Intent(context, CommentDetailActivity.class);
		i.putExtra(USER_ID, userId);
		i.putExtra(COMMENT_ID, commentId);
		context.startActivityForResult(i, requestCode);
	}
	
	/**
	 * 
	 * @param context
	 * @param name
	 * @param description
	 */
	public void setEntityMetaData(Activity context, String name, String description) {
		Intent intent = context.getIntent();
		Bundle extras = getExtras(intent);
		extras.putString(ENTITY_NAME, name);
		extras.putString(ENTITY_DESCRIPTION, description);
		intent.putExtras(extras);
	}
	
	/**
	 * 
	 * @param context
	 * @param asLink
	 */
	public void setUseEntityUrlAsLink(Activity context, boolean asLink) {
		Intent intent = context.getIntent();
		Bundle extras = getExtras(intent);
		extras.putBoolean(ENTITY_URL_AS_LINK, asLink);
		intent.putExtras(extras);
	}
	
	/**
	 * 
	 * @param context
	 * @param url
	 */
	public void setEntityUrl(Activity context, String url) {
		Intent intent = context.getIntent();
		Bundle extras = getExtras(intent);
		extras.putString(ENTITY_KEY, url);
		intent.putExtras(extras);
	}
	
	public void setUserId(Activity context, String userId) {
		Intent intent = context.getIntent();
		Bundle extras = getExtras(intent);
		extras.putString(USER_ID, userId);
		intent.putExtras(extras);
	}
	
	public void setContentViewWithActionBar(Activity parent, int resId) {
		setContentViewWithActionBar(parent, resId, true);
	}
	
	public void setContentViewWithActionBar(Activity parent, int resId, boolean scroll) {
		LayoutInflater layoutInflater = (LayoutInflater) parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		View original = layoutInflater.inflate(resId, null);
		setContentViewWithActionBar(parent, original, scroll);
	}
	
	public void setContentViewWithActionBar(Activity parent, View original) {
		setContentViewWithActionBar(parent, original, true);
	}
	
	public void setContentViewWithActionBar(Activity parent, View original, boolean scroll) {
		
		RelativeLayout barLayout = new RelativeLayout(parent);
		RelativeLayout originalLayout = new RelativeLayout(parent);
		
		ActionBarView socializeActionBar = new ActionBarView(parent);
		socializeActionBar.assignId(original);
		socializeActionBar.setAdsEnabled(true);
		
		LayoutParams barParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		barParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		LayoutParams originalParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		originalParams.addRule(RelativeLayout.ABOVE, socializeActionBar.getId());
		
		socializeActionBar.setLayoutParams(barParams);
		originalLayout.setLayoutParams(originalParams);
		
		if(scroll && !(original instanceof ScrollView) ) {
			ScrollView scrollView = new ScrollView(parent);
			scrollView.setFillViewport(true);
			scrollView.addView(original);
			originalLayout.addView(scrollView);
		}
		else {
			originalLayout.addView(original);
		}
		
		barLayout.addView(originalLayout);
		barLayout.addView(socializeActionBar);
		parent.setContentView(barLayout);
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
	public void setBeanOverrides(String beanOverride) {
		this.beanOverride = beanOverride;
	}
}
