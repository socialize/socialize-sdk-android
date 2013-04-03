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
package com.socialize.networks.facebook.v2;

import android.app.Activity;
import android.net.Uri;
import com.socialize.entity.Entity;
import com.socialize.entity.PropagationInfo;
import com.socialize.entity.Share;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.SocialNetworkPostListener;

import java.util.Map;

/**
 * @author Jason Polites
 *
 */
@Deprecated
public interface FacebookWallPoster {

	public void postLike(Activity parent, Entity entity, PropagationInfo propInfo, SocialNetworkListener listener);

	public void postComment(Activity parent, Entity entity, String comment, PropagationInfo propInfo, SocialNetworkListener listener);	
		
	public void postPhoto(Activity parent, Share share, String comment, Uri photoUri, SocialNetworkListener listener);
	
	public void postPhoto(Activity parent, String link, String caption, Uri photoUri, SocialNetworkListener listener);
	
	public void postOG(Activity parent, Entity entity, String message, String action, PropagationInfo propInfo, SocialNetworkListener listener);
	
	public void post(Activity parent, Entity entity, String message, PropagationInfo propInfo, SocialNetworkListener listener);
	
	public void post(Activity parent, SocialNetworkListener listener, PostData postData);
	
	public void post(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener);

	public void get(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener);
	
	public void delete(Activity parent, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener);
	
	public void getCurrentPermissions(Activity parent, String token, FacebookPermissionCallback callback);
}