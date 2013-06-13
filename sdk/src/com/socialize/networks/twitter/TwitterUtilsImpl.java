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
package com.socialize.networks.twitter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import com.socialize.ConfigUtils;
import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.apache.http.entity.mime.HttpMultipartMode;
import com.socialize.apache.http.entity.mime.MultipartEntity;
import com.socialize.apache.http.entity.mime.content.ByteArrayBody;
import com.socialize.apache.http.entity.mime.content.StringBody;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.api.action.user.UserSystem;
import com.socialize.auth.AuthProviderInfoBuilder;
import com.socialize.auth.AuthProviderType;
import com.socialize.auth.DefaultUserProviderCredentials;
import com.socialize.auth.UserProviderCredentials;
import com.socialize.auth.twitter.TwitterAuthProviderInfo;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.net.HttpRequestListener;
import com.socialize.net.HttpRequestProvider;
import com.socialize.networks.DefaultPostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.oauth.OAuthRequestSigner;
import com.socialize.util.ImageUtils;
import com.socialize.util.JSONParser;
import com.socialize.util.StringUtils;
import com.socialize.util.UrlBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;


/**
 * @author Jason Polites
 *
 */
public class TwitterUtilsImpl implements TwitterUtilsProxy {
	
	private UserSystem userSystem;
	private SocializeConfig config;
	private OAuthRequestSigner requestSigner;
	private HttpRequestProvider httpRequestProvider;
	private ImageUtils imageUtils;
	private AuthProviderInfoBuilder authProviderInfoBuilder;
	private JSONParser jsonParser;
	private SocializeConfig socializeConfig;
	
	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#link(android.app.Activity, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, SocializeAuthListener listener) {
		getSocialize().authenticateForWrite(context, AuthProviderType.TWITTER, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#link(android.app.Activity, java.lang.String, java.lang.String, com.socialize.listener.SocializeAuthListener)
	 */
	@Override
	public void link(Activity context, String token, String secret, SocializeAuthListener listener) {
		
		TwitterAuthProviderInfo twInfo = new TwitterAuthProviderInfo();
		twInfo.setConsumerKey(config.getProperty(SocializeConfig.TWITTER_CONSUMER_KEY));
		twInfo.setConsumerSecret(config.getProperty(SocializeConfig.TWITTER_CONSUMER_SECRET));
		
		DefaultUserProviderCredentials credentials = new DefaultUserProviderCredentials();
		credentials.setAuthProviderInfo(twInfo);
		credentials.setAccessToken(token);
		credentials.setTokenSecret(secret);
		
		getSocialize().authenticateKnownUser(
				context, 
				credentials, 
				listener);		
		
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#unlink(android.app.Activity)
	 */
	@Override
	public void unlink(Context context) {
		SocializeSession session = getSocialize().getSession();
		session.clear(AuthProviderType.TWITTER);
		session.getUserSettings().setAutoPostTwitter(false);
		userSystem.saveSession(context, session);		
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#isLinked(android.content.Context)
	 */
	@Override
	public boolean isLinked(Context context) {
		return getSocialize().isAuthenticatedForWrite(AuthProviderType.TWITTER);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#isAvailable(android.content.Context)
	 */
	@Override
	public boolean isAvailable(Context context) {
		return authProviderInfoBuilder.isSupported(AuthProviderType.TWITTER);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#setCredentials(android.content.Context, java.lang.String, java.lang.String)
	 */
	@Override
	public void setCredentials(Context context, String consumerKey, String consumerSecret) {
		config.setTwitterKeySecret(consumerKey, consumerSecret);
	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#getAccessToken()
	 */
	@Override
	public String getAccessToken(Context context) {
		UserProviderCredentials creds = getSocialize().getSession().getUserProviderCredentials(AuthProviderType.TWITTER);
		if(creds != null) {
			return creds.getAccessToken();
		}
		return null;

	}

	/* (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#getTokenSecret()
	 */
	@Override
	public String getTokenSecret(Context context) {
		UserProviderCredentials creds = getSocialize().getSession().getUserProviderCredentials(AuthProviderType.TWITTER);
		if(creds != null) {
			return creds.getTokenSecret();
		}
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.networks.twitter.TwitterUtilsProxy#tweet(android.app.Activity, com.socialize.entity.Entity, java.lang.String, com.socialize.networks.SocialNetworkListener)
	 */
	@Override
	public void tweetEntity(final Activity context, final Entity entity, final String text, final SocialNetworkShareListener listener) {
		ShareOptions options = ShareUtils.getUserShareOptions(context);
		options.setText(text);
		options.setShowAuthDialog(false);
		ShareUtils.shareViaSocialNetworks(context, entity, options, listener, SocialNetwork.TWITTER);
	}
	
	@Override
	public void tweetPhoto(Activity context, PhotoTweet tweet, SocialNetworkPostListener listener) {
		try {
			MultipartEntity multipart = newMultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			
			ByteArrayBody body = new ByteArrayBody(tweet.getImageData(), "media");
			StringBody status = new StringBody(tweet.getText());
			StringBody possiblySensitive = new StringBody(String.valueOf(tweet.isPossiblySensitive()));
			
			multipart.addPart("media", body);
			multipart.addPart("status", status);
			multipart.addPart("possibly_sensitive", possiblySensitive);

			String photoEndpoint = config.getProperty("twitter.upload.endpoint");
			post(context, photoEndpoint + "statuses/update_with_media.json", multipart, listener);
		}
		catch (Exception e) {
			if(listener != null) {
				listener.onNetworkError(context, SocialNetwork.TWITTER, e);
			}
		}
	}
	
	// Mockable
	protected MultipartEntity newMultipartEntity(HttpMultipartMode mode) {
		return new MultipartEntity(mode);
	}
	
	@Override
	public void tweet(final Activity context, Tweet tweet, final SocialNetworkListener listener) {
		tweet(context, null, tweet, listener);
	}

	@Override
	public void tweet(final Activity context, Entity entity, Tweet tweet, final SocialNetworkListener listener) {
		
		DefaultPostData postData = new DefaultPostData();
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("status", tweet.getText());
		
		// Was failing: https://dev.twitter.com/discussions/8685
		if(tweet.isShareLocation() && tweet.getLocation() != null) {
			map.put("lat", String.valueOf(tweet.getLocation().getLatitude()));
			map.put("long", String.valueOf(tweet.getLocation().getLongitude()));
			map.put("display_coordinates", "true");
		}
		
		postData.setPostValues(map);
		postData.setEntity(entity);
		
		boolean okToGo = true;
		
		if(listener != null) {
			okToGo = !listener.onBeforePost(context, SocialNetwork.TWITTER, postData);
		}
		
		if(okToGo) {
			String path = postData.getPath();
			
			if(StringUtils.isEmpty(path)) {
				path = "statuses/update.json";
			}
			
			post(context, path, postData.getPostValues(), listener);
		}
	}	
	
	@Override
	public void post(final Activity context, String resource, Map<String, Object> postData, final SocialNetworkPostListener listener) {

		try {
			Set<Entry<String, Object>> entries = postData.entrySet();
			
			List<NameValuePair> data = new ArrayList<NameValuePair>();
			
			for (Entry<String, Object> entry : entries) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if(key != null && value != null) {
					data.add(new BasicNameValuePair(key, value.toString()));
				}
			}

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data, "UTF-8");
			String apiEndpoint = config.getProperty("twitter.api.endpoint");
			post(context, apiEndpoint + resource, entity, listener);
		}
		catch (Exception e) {
			if(listener != null) {
				listener.onNetworkError(context, SocialNetwork.TWITTER, e);
			}
		}		
	}
	
	protected void post(final Activity context, String resource, HttpEntity entity, final SocialNetworkPostListener listener) {
		try {
			resource = resource.trim();
			
			if(!resource.endsWith(".json")) {
				resource += ".json";
			}
			
			HttpPost post = new HttpPost(resource);
			post.setEntity(entity);		
			
			SocializeSession session = getSocialize().getSession();
			
			UserProviderCredentials creds = session.getUserProviderCredentials(AuthProviderType.TWITTER);
			
			String consumerKey = ConfigUtils.getConfig(context).getProperty(SocializeConfig.TWITTER_CONSUMER_KEY);
			String consumerSecret = ConfigUtils.getConfig(context).getProperty(SocializeConfig.TWITTER_CONSUMER_SECRET);			
			
			HttpPost signedRequest = requestSigner.sign(consumerKey, consumerSecret, creds.getAccessToken(), creds.getTokenSecret(), post, null);
			
			httpRequestProvider.post(signedRequest, new HttpRequestListener() {
				
				@Override
				public void onSuccess(HttpResponse response, String responseData) {
					
					try {
						JSONObject responseObject = jsonParser.parseObject(responseData);
						if(listener != null) {
							listener.onAfterPost(context, SocialNetwork.TWITTER, responseObject);
						}
					}
					catch (JSONException e) {
						if(listener != null) {
							listener.onNetworkError(context, SocialNetwork.TWITTER, e);
						}
					}
				}
				
				@Override
				public void onError(Exception error, HttpResponse response, int errorCode, String responseData) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.TWITTER, error);
					}
				}
			});
		}
		catch (Exception e) {
			if(listener != null) {
				listener.onNetworkError(context, SocialNetwork.TWITTER, e);
			}
		}
	}

	@Override
	public void get(final Activity context, String resource, Map<String, Object> params, final SocialNetworkPostListener listener) {

		try {
			resource = resource.trim();
			
			if(!resource.contains(".json")) {
				if(resource.contains("?")) {
					String[] split = resource.split("\\?");
					split[0] += ".json";
					resource = split[0] + split[1];
				}
				else {
					resource += ".json";
				}
			}

			String apiEndpoint = config.getProperty("twitter.api.endpoint");

			UrlBuilder builder = new UrlBuilder();
			builder.start(apiEndpoint + resource);
			
			if(params != null) {
				
				Set<Entry<String, Object>> entries = params.entrySet();
				
				for (Entry<String, Object> entry : entries) {
					String key = entry.getKey();
					Object value = entry.getValue();
					if(key != null && entry != null) {
						builder.addParam(key, value.toString());
					}
				}
			}
			
			HttpGet get = new HttpGet(builder.toString());

			SocializeSession session = getSocialize().getSession();
			
			UserProviderCredentials creds = session.getUserProviderCredentials(AuthProviderType.TWITTER);
			
			String consumerKey = ConfigUtils.getConfig(context).getProperty(SocializeConfig.TWITTER_CONSUMER_KEY);
			String consumerSecret = ConfigUtils.getConfig(context).getProperty(SocializeConfig.TWITTER_CONSUMER_SECRET);			
			
			HttpGet signedRequest = requestSigner.sign(consumerKey, consumerSecret, creds.getAccessToken(), creds.getTokenSecret(), get, null);
			
			httpRequestProvider.get(signedRequest, new HttpRequestListener() {
				
				@Override
				public void onSuccess(HttpResponse response, String responseData) {
					
					try {
						JSONObject responseObject = jsonParser.parseObject(responseData);
						if(listener != null) {
							listener.onAfterPost(context, SocialNetwork.TWITTER, responseObject);
						}
					}
					catch (JSONException e) {
						if(listener != null) {
							listener.onNetworkError(context, SocialNetwork.TWITTER, e);
						}
					}
				}
				
				@Override
				public void onError(Exception error, HttpResponse response, int errorCode, String responseData) {
					if(listener != null) {
						listener.onNetworkError(context, SocialNetwork.TWITTER, error);
					}
				}
			});
		}
		catch (Exception e) {
			if(listener != null) {
				listener.onNetworkError(context, SocialNetwork.TWITTER, e);
			}
		}		
	}
	
	@Override
	public byte[] getImageForPost(Activity context, Uri imagePath) throws IOException {
		return imageUtils.scaleImage(context, imagePath);
	}
	
	@Override
	public byte[] getImageForPost(Activity context, Bitmap image, CompressFormat format) throws IOException {
		return imageUtils.scaleImage(context, image, format);
	}

	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	public void setUserSystem(UserSystem userSystem) {
		this.userSystem = userSystem;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
	
	public void setRequestSigner(OAuthRequestSigner requestSigner) {
		this.requestSigner = requestSigner;
	}

	public void setHttpRequestProvider(HttpRequestProvider httpRequestProvider) {
		this.httpRequestProvider = httpRequestProvider;
	}

	public void setImageUtils(ImageUtils imageUtils) {
		this.imageUtils = imageUtils;
	}

	public void setAuthProviderInfoBuilder(AuthProviderInfoBuilder authProviderInfoBuilder) {
		this.authProviderInfoBuilder = authProviderInfoBuilder;
	}

	public void setJsonParser(JSONParser jsonParser) {
		this.jsonParser = jsonParser;
	}
}
