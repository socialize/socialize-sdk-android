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
package com.socialize.networks;

import android.app.Activity;
import com.socialize.api.action.ActionType;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;

/**
 * Shares entities to a social network.
 * @author Jason Polites
 *
 */
public interface SocialNetworkSharer {
	
	/**
	 * Shares the given entity to the given social network.
	 * @param context The current context.
	 * @param entity The entity to be shared.
	 * @param urlSet The set of urls to be used in the share.
	 * @param comment The comment provided by the user.
	 * @param autoAuth If true authentication will be attempted automatically.
	 * @param listener A listener to handle callbacks from the post.
	 */
	public void share(Activity context, Entity entity, PropagationInfo urlSet, String comment, boolean autoAuth, ActionType type, SocialNetworkListener listener);
	
}
