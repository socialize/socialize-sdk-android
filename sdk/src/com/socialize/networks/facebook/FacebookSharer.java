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
package com.socialize.networks.facebook;

import android.app.Activity;
import com.socialize.api.ShareMessageBuilder;
import com.socialize.api.action.ActionType;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.networks.AbstractSocialNetworkSharer;
import com.socialize.networks.SocialNetwork;

/**
 * @author Jason Polites
 */
public class FacebookSharer extends AbstractSocialNetworkSharer {
	
	private ShareMessageBuilder shareMessageBuilder;
	private FacebookWallPoster facebookWallPoster;
	
	@Override
	protected SocialNetwork getNetwork() {
		return SocialNetwork.FACEBOOK;
	}
	
	@Override
	protected void doShare(Activity context, Entity entity, PropagationInfo urlSet, String comment, SocialNetworkShareListener listener, ActionType type) {
		switch (type) {
			case COMMENT:
				facebookWallPoster.postComment(context, entity, comment, urlSet, listener);
				break;
				
			case SHARE:
				String body = shareMessageBuilder.buildShareMessage( entity, urlSet, comment, false, false);
				facebookWallPoster.post(context, entity, body, urlSet, listener);
				break;
				
			case LIKE:
				facebookWallPoster.postLike(context, entity, urlSet, listener);
				break;			
		}		
	}

	public void setShareMessageBuilder(ShareMessageBuilder shareMessageBuilder) {
		this.shareMessageBuilder = shareMessageBuilder;
	}

	public void setFacebookWallPoster(FacebookWallPoster facebookWallPoster) {
		this.facebookWallPoster = facebookWallPoster;
	}
}
