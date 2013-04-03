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
package com.socialize.listener.comment;


import android.app.Activity;
import com.socialize.entity.Comment;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import org.json.JSONObject;


/**
 * @author Jason Polites
 *
 */
public abstract class CommentDeleteListener extends CommentListener implements SocialNetworkListener {

	@Override
	public void onCreate(Comment result) {}

	@Override
	public final void onGet(Comment comment) {}

	@Override
	public final void onList(ListResult<Comment> comments) {}

	@Override
	public final void onUpdate(Comment comment) {}

	@Override
	public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
		onError(SocializeException.wrap(error));
	}

	@Override
	public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
		return false;
	}

	@Override
	public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {}
}
