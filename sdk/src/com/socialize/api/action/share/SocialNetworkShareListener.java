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
package com.socialize.api.action.share;

import android.app.Activity;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import org.json.JSONObject;


/**
 * @author Jason Polites
 */
public abstract class SocialNetworkShareListener extends ShareAddListener implements SocialNetworkListener {
	
	@Override
	public void onCreate(Share result) {}

	@Override
	public void onError(SocializeException error) {}

	public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
		onError(SocializeException.wrap(error));
	}

	public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
		return false;
	}

	public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {}

}
