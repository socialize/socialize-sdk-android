package com.socialize.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.socialize.Socialize;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.util.Drawables;

public class SocializeUI {

	private static final SocializeUI instance = new SocializeUI();
	
	private static final String CONFIG_KEY = "SocializeConfig";
	public static final String ENTITY_KEY = "socialize.entity.key";
	public static final String DEFAULT_USER_ICON = "default_user_icon.png";
	public static final String SOCIALIZE_LOGO = "socialize_logo.png";
	public static final String BG_ACCENT = "bg_accent.png";
	
	private IOCContainer container;
	private Drawables drawables;
	
	public static final SocializeUI getInstance() {
		return instance;
	}
	
	void initSocialize(Context context) {
		Socialize.getSocialize().init(context, new String[]{"socialize_beans.xml", "socialize_ui_beans.xml"});
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
	 * @param context The current activity
	 * @param consumerKey Your consumer key, obtained via registration at http://getsocialize.com
	 * @param consumerSecret Your consumer secret, obtained via registration at http://getsocialize.com
	 */
	public void setAppCredentials(Context context, String consumerKey, String consumerSecret) {
		SharedPreferences prefs = context.getSharedPreferences(CONFIG_KEY, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(SocializeConfig.SOCIALIZE_CONSUMER_KEY, consumerKey);
		editor.putString(SocializeConfig.SOCIALIZE_CONSUMER_SECRET, consumerSecret);
		editor.commit();
	}
	
	/**
	 * Sets the FB credentials for the current user if available.
	 * @param context The current activity
	 * @param userId
	 * @param token
	 */
	public void setFacebookUserCredentials(Context context, String userId, String token) {
		SharedPreferences prefs = context.getSharedPreferences(CONFIG_KEY, Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(SocializeConfig.FACEBOOK_USER_ID, userId);
		editor.putString(SocializeConfig.FACEBOOK_USER_TOKEN, token);
		editor.commit();
	}
	
	public String getGlobalConfigValue(Context context, String key) {
		SharedPreferences prefs = context.getSharedPreferences(CONFIG_KEY, Context.MODE_PRIVATE);
		return prefs.getString(key, null);
	}
	
	/**
	 * Sets the Facebook ID for FB authentication.  
	 * This is optional.  If not specified the default Socialize FB app will be used.
	 * @param context
	 * @param appId Your Facebook App Id, obtained from https://developers.facebook.com/
	 * @see https://developers.facebook.com/
	 */
	public void setFacebookAppId(Context context, String appId) {
		assertSocializeInitialized(context);
		Socialize.getSocialize().getConfig().setProperty(SocializeConfig.FACEBOOK_APP_ID, appId);
	}
	
	public void setEntityUrl(Activity context, String url) {
		Bundle extras = context.getIntent().getExtras();
		if(extras == null) {
			extras = new Bundle();
		}
		extras.putString(ENTITY_KEY, url);
		context.getIntent().putExtras(extras);
	}
	
	private void assertSocializeInitialized(Context context) {
		SocializeConfig config = Socialize.getSocialize().getConfig();
		if(config == null) {
			initSocialize(context);
		}
	}
}
