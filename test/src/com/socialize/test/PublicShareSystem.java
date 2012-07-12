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
package com.socialize.test;

import android.content.Context;
import android.location.Location;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.SocializeShareSystem;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.listener.share.ShareListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.provider.SocializeProvider;
import com.socialize.share.ShareHandler;


/**
 * @author Jason Polites
 *
 */
public class PublicShareSystem extends SocializeShareSystem {

	public PublicShareSystem(SocializeProvider<Share> provider) {
		super(provider);
	}

	@Override
	public void addShare(Context context, SocializeSession session, Entity entity, String text, ShareType shareType, SocialNetwork network, Location location, ShareListener listener) {
		super.addShare(context, session, entity, text, shareType, network, location, listener);
	}

	@Override
	public ShareHandler getSharer(ShareType destination) {
		return super.getSharer(destination);
	}
}
