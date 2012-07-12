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
package com.socialize.api.action.like;

import android.app.Activity;
import android.content.Context;
import android.widget.CompoundButton;
import com.socialize.annotations.Synchronous;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.listener.like.LikeAddListener;
import com.socialize.listener.like.LikeDeleteListener;
import com.socialize.listener.like.LikeGetListener;
import com.socialize.listener.like.LikeListListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.actionbutton.LikeButtonListener;


/**
 * @author Jason Polites
 *
 */
public interface LikeUtilsProxy {
	
	@Synchronous
	public LikeOptions getUserLikeOptions(Context context);

	public void like (Activity context, Entity entity, LikeAddListener listener);
	
	public void like (Activity context, Entity entity, LikeOptions likeOptions, LikeAddListener listener, SocialNetwork...shareTo);
	
	public void unlike (Activity context, String entityKey, LikeDeleteListener listener);
 
	public void getLike (Activity context, long id, LikeGetListener listener);
	
	public void getLike (Activity context, String entityKey, LikeGetListener listener);
	
	public void getLikesByUser (Activity context, User user, int start, int end, LikeListListener listener);
	
	public void getLikesByEntity (Activity context, String entityKey, int start, int end, LikeListListener listener);
	
	public void getLikesByApplication (Activity context, int start, int end, LikeListListener listener);
	
	public void makeLikeButton(Activity context, CompoundButton button, Entity entity, LikeButtonListener listener);
}
