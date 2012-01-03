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
package com.socialize.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import android.content.Context;

import com.socialize.log.SocializeLogger;
import com.socialize.util.ResourceLocator;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeConfig {
	
	public static final String SOCIALIZE_PROPERTIES_PATH = "socialize.properties";

	public static final String SOCIALIZE_CORE_BEANS_PATH = "socialize_core_beans.xml";
//	public static final String SOCIALIZE_SYSTEM_BEANS_PATH = "socialize_system_beans.xml";
	public static final String SOCIALIZE_UI_BEANS_PATH = "socialize_ui_beans.xml";
	public static final String SOCIALIZE_NOTIFICATION_BEANS_PATH = "socialize_notification_beans.xml";
	public static final String SOCIALIZE_ERRORS_PATH = "socialize.errors.properties";
	
	public static final String SOCIALIZE_CONSUMER_KEY = "socialize.consumer.key";
	public static final String SOCIALIZE_CONSUMER_SECRET = "socialize.consumer.secret";
	
	public static final String SOCIALIZE_REGISTER_NOTIFICATION_ON_STARTUP = "socialize.register.notification.on.startup";
	
	public static final String SOCIALIZE_C2DM_SENDER_ID = "socialize.c2dm.sender.id";
	
	@Deprecated
	public static final String SOCIALIZE_BEANS_PATH = SOCIALIZE_CORE_BEANS_PATH;
	
	@Deprecated
	public static final String SOCIALIZE_DEBUG_MODE = "socialize.debug.mode";
	
	/**
	 * true if Single Sign On is enabled.  Default is true.
	 */
	public static final String FACEBOOK_SSO_ENABLED = "facebook.sso.enabled";
	public static final String FACEBOOK_APP_ID = "facebook.app.id";
	public static final String FACEBOOK_USER_ID = "facebook.user.id";
	public static final String FACEBOOK_USER_TOKEN = "facebook.user.token";
	
	private Properties properties;
	private SocializeLogger logger;
	private ResourceLocator resourceLocator;
	
	private String propertiesFileName = SOCIALIZE_PROPERTIES_PATH;
	
	public static final String LOG_LEVEL = "log.level";
	public static final String LOG_TAG = "log.tag";
	public static final String LOG_MSG = "log.msg.";
	public static final String API_HOST = "api.host";
	public static final String REDIRECT_HOST = "redirect.host";
	
	
	public static final String HTTP_CONNECTION_TIMEOUT = "http.connection.timeout";
	public static final String HTTP_SOCKET_TIMEOUT = "http.socket.timeout";
	
	public static final int MAX_LIST_RESULTS = 100;
	
//	private final Properties customProperties = new Properties();
//	private final Set<String> toBeRemoved = new HashSet<String>();
	
	public SocializeConfig() {
		super();
	}

	public SocializeConfig(String propertiesFileName) {
		super();
		this.propertiesFileName = propertiesFileName;
	}
	
	public void init(Context context) {
		init(context, true);
	}
	
	/**
	 * 
	 * @param context
	 * @param override
	 */
	public void init(Context context, boolean override) {
		InputStream in = null;
		try {
			if(resourceLocator != null) {
				try {
					in = resourceLocator.locateInClassPath(context, propertiesFileName);
					
					if(in != null) {
						Properties old = null;
						
						if(properties != null) {
							old = properties;
						}
						
						properties = createProperties();
						properties.load(in);
						
						if(old != null) {
							merge(old, null);
						}
					}
				}
				catch (IOException ignore) {
					// No config.. eek!
				}
				finally {
					if(in != null) {
						in.close();
					}
				}
				
				if(override) {
					// Look for override
					try {
						in = resourceLocator.locateInAssets(context, propertiesFileName);
						
						if(in != null) {
							Properties overrideProps = createProperties();
							overrideProps.load(in);
							merge(overrideProps, null);
						}
					}
					catch (IOException ignore) {
						// ignore
					}
					finally {
						if(in != null) {
							in.close();
						}
					}
				}
			}
			
			if(properties == null) {
				if(logger != null) {
					logger.error(SocializeLogger.NO_CONFIG);
				}
				properties = createProperties();
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	

	/**
	 * Sets a custom property.  This will override settings in socialize.properties.
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value) {
		
		if(properties == null) {
			properties = createProperties();
		}
		
		properties.put(key, value);
		
//		if(!StringUtils.isEmpty(value)) {
//			customProperties.put(key, value);
//		}
//		else {
//			customProperties.remove(key);
//			toBeRemoved.add(key);
//		}
	}
	
	/**
	 * Returns a property from socialize.properties.
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return (properties == null) ? null : properties.getProperty(key);
//		String property = customProperties.getProperty(key);
//		if(StringUtils.isEmpty(property)) {
//			property = getLocalProperty(key);
//		}
//		return property;
	}	
	
	public int getIntProperty(String key, int defaultValue) {
		String val = getProperty(key);
		if(!StringUtils.isEmpty(val)) {
			return Integer.parseInt(val);
		}
		return defaultValue;
	}
	
	public boolean getBooleanProperty(String key, boolean defaultValue) {
		String val = getProperty(key);
		if(!StringUtils.isEmpty(val)) {
			return Boolean.parseBoolean(val);
		}
		return defaultValue;
	}
	
	/**
	 * Sets the Facebook ID for FB authentication.  
	 * @param appId Your Facebook App Id, obtained from https://developers.facebook.com/
	 * @see https://developers.facebook.com/
	 */
	public void setFacebookAppId(String appId) {
		setProperty(SocializeConfig.FACEBOOK_APP_ID, appId);
	}

	/**
	 * Enables/disables Single Sign On for Facebook.
	 * @param enabled True if enabled.  Default is true.
	 */
	public void setFacebookSingleSignOnEnabled(boolean enabled) {
		setProperty(SocializeConfig.FACEBOOK_SSO_ENABLED, String.valueOf(enabled));
	}

	/**
	 * Sets the FB credentials for the current user if available.
	 * @param userId
	 * @param token
	 */
	public void setFacebookUserCredentials(String userId, String token) {
		setProperty(SocializeConfig.FACEBOOK_USER_ID, userId);
		setProperty(SocializeConfig.FACEBOOK_USER_TOKEN, token);
	}
	
	/**
	 * Sets the Socialize credentials for your App.
	 * @param consumerKey Your consumer key, obtained via registration at http://getsocialize.com
	 * @param consumerSecret Your consumer secret, obtained via registration at http://getsocialize.com
	 */
	public void setSocializeCredentials(String consumerKey, String consumerSecret) {
		setProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY, consumerKey);
		setProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET, consumerSecret);
	}	
	
	protected String getLocalProperty(String key) {
		return getLocalProperty(key, null);
	}
	
	protected String getLocalProperty(String key, String defaultValue) {
		String val = properties.getProperty(key);
		if(!StringUtils.isEmpty(val)) {
			return val;
		}
		return defaultValue;
	}
	
	// So we can mock
//	protected Set<String> getPropertiesToBeRemoved() {
//		return toBeRemoved;
//	}
	
	public void merge(SocializeConfig config) {
		merge(config.getProperties(), null);
	}
	
//	public void merge() {
//		doMerge(customProperties, toBeRemoved);
//	}
	
	/**
	 * Merge properties into the config.
	 * @param other
	 */
	public void merge(Properties other, Set<String> toBeRemoved) {
//		merge();
		doMerge(other, toBeRemoved);
	}

	protected void doMerge(Properties other, Set<String> toBeRemoved) {

		if(properties == null) {
			properties = createProperties();
		}
		
		if(other != null && other.size() > 0) {
			Set<Entry<Object, Object>> entrySet = other.entrySet();
			for (Entry<Object, Object> entry : entrySet) {
				properties.put(entry.getKey(), entry.getValue());
			}
		}
		
		if(toBeRemoved != null && toBeRemoved.size() > 0) {
			for (String key : toBeRemoved) {
				properties.remove(key);
			}
			toBeRemoved.clear();
		}
	}

	public String getDefaultPropertiesFileName() {
		return SOCIALIZE_PROPERTIES_PATH;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setResourceLocator(ResourceLocator resourceLocator) {
		this.resourceLocator = resourceLocator;
	}

	/**
	 * @deprecated Filename does not need to be changed.  Just use assets to override with socialize.properties.
	 * @param propertiesFileName
	 */
	@Deprecated
	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}
	
	protected Properties createProperties() {
		return new Properties();
	}
}
