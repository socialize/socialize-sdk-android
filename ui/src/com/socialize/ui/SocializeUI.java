package com.socialize.ui;

import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.socialize.Socialize;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.ui.comment.CommentActivity;
import com.socialize.util.Drawables;

public class SocializeUI {

	private static final SocializeUI instance = new SocializeUI();
	
	public static final String ENTITY_KEY = "socialize.entity.key";
	public static final String DEFAULT_USER_ICON = "default_user_icon.png";
	public static final String SOCIALIZE_LOGO = "socialize_logo.png";
	public static final String BG_ACCENT = "bg_accent.png";
	
	private IOCContainer container;
	private Drawables drawables;
	private final Properties customProperties = new Properties();
	
	public static final SocializeUI getInstance() {
		return instance;
	}
	
	void initSocialize(Context context) {
		Socialize.getSocialize().init(context, new String[]{"socialize_beans.xml", "socialize_ui_beans.xml"});
		Socialize.getSocialize().getConfig().merge(customProperties);
	}
	
	void initUI(IOCContainer container) {
		this.container = container;
		if(container != null) {
			drawables = container.getBean("drawables");
		}
	}
	
	void destroy(Context context) {
		Socialize.getSocialize().destroy();
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
	
	/**
	 * 
	 * @param debug
	 */
	public void setDebugMode(boolean debug) {
		customProperties.put(SocializeConfig.SOCIALIZE_DEBUG_MODE, String.valueOf(debug));
	}
	
	/**
	 * Sets the Facebook ID for FB authentication.  
	 * This is optional.  If not specified the default Socialize FB app will be used.
	 * @param appId Your Facebook App Id, obtained from https://developers.facebook.com/
	 * @see https://developers.facebook.com/
	 */
	public void setFacebookAppId(String appId) {
		customProperties.put(SocializeConfig.FACEBOOK_APP_ID, appId);
	}
	
	public String getCustomConfigValue(Context context, String key) {
		return Socialize.getSocialize().getConfig().getProperty(key);
	}
	
	public void showCommentView(Activity context, String url) {
		Intent i = new Intent(context, CommentActivity.class);
		i.putExtra(ENTITY_KEY, url);
		context.startActivity(i);
	}
	
	public void setEntityUrl(Activity context, String url) {
		Bundle extras = context.getIntent().getExtras();
		if(extras == null) {
			extras = new Bundle();
		}
		extras.putString(ENTITY_KEY, url);
		context.getIntent().putExtras(extras);
	}
	
	public Properties getCustomProperties() {
		return customProperties;
	}
}
