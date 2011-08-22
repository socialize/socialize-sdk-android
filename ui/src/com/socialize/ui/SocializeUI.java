package com.socialize.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.socialize.Socialize;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.util.Drawables;

public class SocializeUI {

	private static final SocializeUI instance = new SocializeUI();
	
	public static final String ENTITY_KEY = "socialize.entity.key";
	public static final String DEFAULT_USER_ICON = "default_user_icon.png";
	public static final String SOCIALIZE_LOGO = "socialize_logo.png";
	public static final String BG_ACCENT = "bg_accent.png";
	public static int STANDARD_BACKGROUND_COLOR = Color.BLACK;
	
	private IOCContainer container;
	private Drawables drawables;
	
	static {
		STANDARD_BACKGROUND_COLOR = Color.parseColor("#38444f");
	}
	
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
		assertSocializeInitialized(context);
		Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY, consumerKey);
		Socialize.getSocialize().getConfig().setProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET, consumerSecret);
	}
	
	/**
	 * Sets the FB credentials for the current user if available.
	 * @param context The current activity
	 * @param userId
	 * @param token
	 */
	public void setFacebookUserCredentials(Activity context, String userId, String token) {
		assertSocializeInitialized(context);
		Socialize.getSocialize().getConfig().setProperty(SocializeConfig.FACEBOOK_USER_ID, userId);
		Socialize.getSocialize().getConfig().setProperty(SocializeConfig.FACEBOOK_USER_TOKEN, token);
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
