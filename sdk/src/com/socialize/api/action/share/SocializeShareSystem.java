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
package com.socialize.api.action.share;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import com.socialize.Socialize;
import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.share.ShareListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.provider.SocializeProvider;
import com.socialize.share.ShareHandler;
import com.socialize.share.ShareHandlerListener;
import com.socialize.share.ShareHandlers;
import com.socialize.util.StringUtils;

/**
 * @author Jason Polites
 */
public class SocializeShareSystem extends SocializeApi<Share, SocializeProvider<Share>> implements ShareSystem {
	
	private ShareHandlers shareHandlers;
	
	private SocializeLogger logger;
	
	public SocializeShareSystem(SocializeProvider<Share> provider) {
		super(provider);
	}
	
	@Override
	public boolean canShare(Context context, ShareType shareType) {
		ShareHandler shareHandler = shareHandlers.getShareHandler(shareType);
		if(shareHandler != null) {
			return shareHandler.isAvailableOnDevice(context);
		}
		return false;
	}

	@Override
	public void addShare(Context context, SocializeSession session, Entity entity, String text, SocialNetwork network, Location location, ShareListener listener) {
		addShare(context, session, entity, text, null, network, location, listener);
	}

	/*
	 * (non-Javadoc)
	 * @see com.socialize.api.action.ShareSystem#addShare(android.content.Context, com.socialize.api.SocializeSession, com.socialize.entity.Entity, java.lang.String, com.socialize.api.action.ShareType, android.location.Location, com.socialize.listener.share.ShareListener)
	 */
	@Override
	public void addShare(Context context, SocializeSession session, Entity entity, String text, ShareType shareType, Location location, ShareListener listener) {
		addShare(context, session, entity, text, shareType, null, location, listener);
	}
	
	protected void addShare(
			final Context context, 
			final SocializeSession session, 
			final Entity entity, 
			final String text, 
			ShareType shareType, 
			SocialNetwork network, 
			final Location location, 
			final ShareListener listener) {
		
		if(shareType == null) {
			if(network != null) {
				shareType = ShareType.valueOf(network.name().toUpperCase());
			}
			else {
				shareType = ShareType.OTHER;
			}
		}
		else if(network == null) {
			network = SocialNetwork.valueOf(shareType);
		}
		
		final SocialNetwork fnetwork = network;
		final ShareType fshareType = shareType; 
		
		if(network != null) {
			AuthProviderType authType = AuthProviderType.valueOf(network);
			if(Socialize.getSocialize().isAuthenticated(authType)) {
				doShare(session, entity, text, shareType, network, location, listener);
			}
			else {
				Socialize.getSocialize().authenticate(context, authType, new SocializeAuthListener() {
					@Override
					public void onError(SocializeException error) {
						if(listener != null) {
							listener.onError(error);
						}
					}
					
					@Override
					public void onCancel() {
						// no network
						doShare(session, entity, text, fshareType, null, location, listener);
					}
					
					@Override
					public void onAuthSuccess(SocializeSession session) {
						doShare(session, entity, text, fshareType, fnetwork, location, listener);
					}
					
					@Override
					public void onAuthFail(SocializeException error) {
						if(listener != null) {
							listener.onError(error);
						}
					}
				});
			}
		}
		else {
			// no network
			doShare(session, entity, text, fshareType, null, location, listener);	
		}
	}
	
	protected void doShare(SocializeSession session, Entity entity, String text, ShareType shareType, SocialNetwork network, Location location, ShareListener listener) {
		
		if(StringUtils.isEmpty(text)) {
			text = entity.getDisplayName();
		}
		
		Share c = new Share();
		c.setEntity(entity);
		c.setText(text);
		c.setShareType(shareType);
		
		if(network != null) {
			ShareOptions shareOptions = new ShareOptions();
			shareOptions.setShareTo(network);
			shareOptions.setShareLocation(true);
			setPropagationData(c, shareOptions);
		}
		else if(shareType != null) {
			// Set propagation data for non-network share types
			setPropagationData(c, shareType);
		}
	
		setLocation(c, location);
		
		List<Share> list = new ArrayList<Share>(1);
		list.add(c);
		
		postAsync(session, ENDPOINT, list, listener);
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
	public void share(Activity context, SocializeSession session, SocializeAction action, String comment, Location location, ShareType destination, boolean autoAuth, ShareHandlerListener listener) {
		ShareHandler sharer = getSharer(destination);
		if(sharer != null) {
			sharer.handle(context, action, location, comment, listener);
		}
		else {
			if(listener != null) {
				listener.onError(context, action, "Unable to share to [" +
						destination.getDisplayName() +
						"]", new SocializeException("No sharer defined for type"));
			}
			 
			if(logger != null) {
				logger.warn("Unable to share to [" +
						destination.getDisplayName() +
						"].  No sharer defined for this type.");
			}
		}
	}

	protected ShareHandler getSharer(ShareType destination) {
		ShareHandler sharer = null;
		
		if(shareHandlers != null) {
			sharer = shareHandlers.getShareHandler(destination);
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

	
	public void setShareHandlers(ShareHandlers shareHandlers) {
		this.shareHandlers = shareHandlers;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}	
}
