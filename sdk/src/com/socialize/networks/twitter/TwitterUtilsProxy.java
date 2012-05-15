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
package com.socialize.networks.twitter;

import android.app.Activity;
import android.content.Context;
import com.socialize.annotations.Synchronous;
import com.socialize.entity.Entity;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetworkListener;


/**
 * @author Jason Polites
 *
 */
public interface TwitterUtilsProxy {
	
	public void link (Activity context, SocializeAuthListener listener);
	
	public void link (Activity context, String token, String secret, SocializeAuthListener listener);
	
	public void unlink (Context context);
	
	@Synchronous
	public boolean isLinked(Context context);
	
	@Synchronous
	public boolean isAvailable(Context context);
	
	@Synchronous
	public void setCredentials(Context context, String consumerKey, String consumerSecret);
	
	@Synchronous
	public String getAccessToken(Context context);
	
	@Synchronous
	public String getTokenSecret(Context context);
	
	public void tweet(final Activity context, final Entity entity, final String text, final SocialNetworkListener listener);
}
