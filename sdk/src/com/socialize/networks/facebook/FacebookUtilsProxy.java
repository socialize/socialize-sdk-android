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
package com.socialize.networks.facebook;

import java.io.IOException;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import com.socialize.annotations.Synchronous;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.entity.Entity;
import com.socialize.facebook.Facebook;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetworkPostListener;
import com.socialize.networks.SocializeDeAuthListener;

/**
 * @author Jason Polites
 */
public interface FacebookUtilsProxy {
	public void link (Activity context, SocializeAuthListener listener);
	public void link (Activity context, String token, boolean verifyPermissions, SocializeAuthListener listener);
	public void link(Activity context, SocializeAuthListener listener, String...permissions);
	
	public void unlink (Context context, SocializeDeAuthListener listener);
	
	@Synchronous
	public boolean isLinked(Context context);
	
	@Synchronous
	public boolean isAvailable(Context context);
	
	@Synchronous
	public void setAppId(Context context, String appId);
	
	@Synchronous
	public String getAccessToken(Context context);
	
	public void extendAccessToken(Activity context, SocializeAuthListener listener);
	
	public void postEntity(Activity context, Entity entity, String text, SocialNetworkShareListener listener);
	
	public void post(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener);
	
	public void get(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener);
	
	public void delete(Activity context, String graphPath, Map<String, Object> postData, SocialNetworkPostListener listener);
	
	public void getCurrentPermissions(Activity context, String token, FacebookPermissionCallback callback);

	public byte[] getImageForPost(Activity context, Uri imagePath) throws IOException;
	
	public byte[] getImageForPost(Activity context, Bitmap image, CompressFormat format) throws IOException;
	
	@Synchronous
	public Facebook getFacebook(Context context);
	
}
