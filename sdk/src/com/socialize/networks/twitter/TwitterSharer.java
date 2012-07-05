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
import com.socialize.LocationUtils;
import com.socialize.UserUtils;
import com.socialize.api.action.ActionType;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.networks.AbstractSocialNetworkSharer;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.ui.profile.UserSettings;
import com.socialize.util.StringUtils;
import android.net.Uri;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import com.socialize.util.ImageUtils;


/**
 * @author Jason Polites
 *
 */
public class TwitterSharer extends AbstractSocialNetworkSharer {
	
	private static final String TWITTER_COMMENT_MESSAGE = "socialize.twitter.comment.message";
	private static final String TWITTER_PICTURE = "socialize.sharing.picture";
	private static final String SHARING_EFFECT_NAME = "socialize.sharing.effect.name";


	/* (non-Javadoc)
	 * @see com.socialize.networks.AbstractSocialNetworkSharer#getNetwork()
	 */
	@Override
	protected SocialNetwork getNetwork() {
		return SocialNetwork.TWITTER;
	}

	@Override
	public void share(Activity context, Entity entity, PropagationInfo urlSet, String comment, boolean autoAuth, ActionType type, SocialNetworkListener listener) {
		if(listener != null) {
			listener.onBeforePost(context, getNetwork(), null);
		}
		super.share(context, entity, urlSet, comment, autoAuth, type, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.networks.AbstractSocialNetworkSharer#doShare(android.app.Activity, com.socialize.entity.Entity, com.socialize.entity.PropagationUrlSet, java.lang.String, com.socialize.networks.SocialNetworkListener, com.socialize.api.action.ActionType)
	 */
	@Override
	protected void doShare(Activity context, Entity entity, PropagationInfo urlSet, String comment, SocialNetworkListener listener, ActionType type) {
		
		PhotoTweet tweet = new PhotoTweet();
		
		switch(type) {
		
			case SHARE:
				if(StringUtils.isEmpty(comment))  comment = "Shared " + entity.getDisplayName();
				break;
			case LIKE:
				comment = "\u2764 likes " + entity.getDisplayName();
				break;
			case VIEW:
				comment = "Viewed " + entity.getDisplayName();
				break;
		}

		String preloaded_text = "";
		String pictureURL = "";

		Properties prop = new Properties();
				 
	   	try {
			prop.load(context.getResources().getAssets().open("socialize.properties"));

			if (!StringUtils.isEmpty(comment)) {
	 			if (prop.getProperty(TWITTER_COMMENT_MESSAGE) != null) {
					preloaded_text = prop.getProperty(TWITTER_COMMENT_MESSAGE);
		    	}
		    }

			prop.load(context.openFileInput("socialize_sharing.properties"));
			if (prop.getProperty(TWITTER_PICTURE) != null) {
				pictureURL = prop.getProperty(TWITTER_PICTURE);

				if (pictureURL != null) {
					Bitmap imgBitmap = ImageUtils.getBitmapFromURL(pictureURL);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
					byte[] data = baos.toByteArray();

					tweet.setImageData(data);
				} else {
					Log.v("TwitterSharer", "pictureURL == null");
				}
			}

			String effect_name = prop.getProperty(SHARING_EFFECT_NAME);
			if (effect_name != null && preloaded_text != null) {
				preloaded_text = preloaded_text.replace("%EFFECT_NAME%", effect_name) + " ";
			}

			Log.v("TwitterSharer", "sharing effect name: " + effect_name);
			Log.v("TwitterSharer", "preloaded_text string: " + preloaded_text);

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			Log.v("TwitterSharer", "Unknown exception catched:");
			ex.printStackTrace();
		}
		
		StringBuilder status = new StringBuilder();

		status.append(preloaded_text);

		if(StringUtils.isEmpty(comment)) {
			status.append(entity.getDisplayName());
		} else {
			status.append(comment);
		}
		
		status.append(", ");
		status.append(urlSet.getEntityUrl());
		
		tweet.setText(status.toString());
		
		UserSettings settings = UserUtils.getUserSettings(context);
		
		if(settings != null && settings.isLocationEnabled()) {
			tweet.setLocation(LocationUtils.getLastKnownLocation(context));
			tweet.setShareLocation(true);
		}
		
		TwitterUtils.tweetPhoto(context, tweet, listener);
	}

}
