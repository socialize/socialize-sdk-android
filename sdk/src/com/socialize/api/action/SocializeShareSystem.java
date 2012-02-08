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
package com.socialize.api.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.location.Location;

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.listener.share.ShareListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkSharer;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 */
public class SocializeShareSystem extends SocializeApi<Share, SocializeProvider<Share>> implements ShareSystem {
	
	private Map<String, SocialNetworkSharer> sharers;
	private SocializeLogger logger;
	
	public SocializeShareSystem(SocializeProvider<Share> provider) {
		super(provider);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.ShareSystem#addShare(com.socialize.api.SocializeSession, com.socialize.entity.Entity, java.lang.String, com.socialize.api.action.ShareType, android.location.Location, com.socialize.listener.share.ShareListener)
	 */
	@Override
	public void addShare(SocializeSession session, Entity entity, String text, ShareType shareType, Location location, ShareListener listener) {
		Share c = new Share();
		c.setEntity(entity);
		c.setText(text);
		c.setMedium(shareType.getId());
		c.setMediumName(shareType.getName());
		
		setLocation(c, location);
		
		List<Share> list = new ArrayList<Share>(1);
		list.add(c);
		
		postAsync(session, ENDPOINT, list, listener);
	}
	
	@Deprecated
	public void addShare(SocializeSession session, String key, String text, ShareType shareType, Location location, ShareListener listener) {
		addShare(session, Entity.newInstance(key, null), text, shareType, location, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.ShareSystem#getSharesByEntity(com.socialize.api.SocializeSession, java.lang.String, int, int, com.socialize.listener.share.ShareListener)
	 */
	@Override
	public void getSharesByEntity(SocializeSession session, String key, int startIndex, int endIndex, ShareListener listener) {
		listAsync(session, ENDPOINT, key, null, startIndex, endIndex, listener);
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.api.action.ShareSystem#getSharesByUser(com.socialize.api.SocializeSession, long, com.socialize.listener.share.ShareListener)
	 */
	@Override
	public void getSharesByUser(SocializeSession session, long userId, ShareListener listener) {
		String endpoint = "/user/" + userId + ENDPOINT;
		listAsync(session, endpoint, listener);
	}

	@Override
	public void shareEntity(Activity context, Entity entity, String comment, Location location, SocialNetwork destination, boolean autoAuth, SocialNetworkListener listener) {
		SocialNetworkSharer sharer = getSharer(destination);
		if(sharer != null) {
			sharer.shareEntity(context, entity, comment, autoAuth, listener);
		}
	}
	
	@Override
	public void shareComment(Activity context, Entity entity, String comment, Location location, SocialNetwork destination, boolean autoAuth, SocialNetworkListener listener) {
		SocialNetworkSharer sharer = getSharer(destination);
		if(sharer != null) {
			sharer.shareComment(context, entity, comment, autoAuth, listener);
		}
	}

	@Override
	public void shareLike(Activity context, Entity entity, String comment, Location location, SocialNetwork destination, boolean autoAuth, SocialNetworkListener listener) {
		SocialNetworkSharer sharer = getSharer(destination);
		if(sharer != null) {
			sharer.shareLike(context, entity, comment, autoAuth, listener);
		}
	}

	protected SocialNetworkSharer getSharer(SocialNetwork destination) {
		SocialNetworkSharer sharer = null;
		
		if(sharers != null) {
			sharer = sharers.get(destination.name().toLowerCase());
		}
		
		if(sharer == null) {
			if(logger != null) {
				logger.warn("No sharer found for network type [" +
						destination.name() +
						"]");
			}
		}	
		
		return sharer;
		
	}

	public void setSharers(Map<String, SocialNetworkSharer> sharers) {
		this.sharers = sharers;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}	
}
