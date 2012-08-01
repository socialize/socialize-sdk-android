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
import android.content.Context;
import com.socialize.annotations.Synchronous;
import com.socialize.entity.Entity;
import com.socialize.entity.User;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.listener.share.ShareGetListener;
import com.socialize.listener.share.ShareListListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.auth.AuthDialogListener;
import com.socialize.ui.share.ShareDialogListener;


/**
 * @author Jason Polites
 *
 */
public interface ShareUtilsProxy {
	
	@Synchronous
	public ShareOptions getUserShareOptions(Context context);
	
	public void showLinkDialog (Activity context, AuthDialogListener listener);
	
	public void preloadLinkDialog (Activity context);
	
	public void preloadShareDialog (Activity context);
	
	public void showShareDialog (Activity context, Entity entity, int options, SocialNetworkShareListener listener, ShareDialogListener dialogListener);
	
	public void shareViaEmail(Activity context, Entity entity, ShareAddListener listener);
	
	public void shareViaOther(Activity context, Entity entity, ShareAddListener listener);
	
	public void shareViaSMS(Activity context, Entity entity, ShareAddListener listener);
	
	public void shareViaGooglePlus(Activity context, Entity entity, ShareAddListener listener);
	
	public void shareViaSocialNetworks(Activity context, Entity entity, ShareOptions shareOptions, SocialNetworkShareListener listener, SocialNetwork...networks);
	
	public void getShare (Activity context, ShareGetListener listener, long id);
	
	public void getShares (Activity context, ShareListListener listener, long...ids);
	
	public void getSharesByUser (Activity context, User user, int start, int end, ShareListListener listener);
	
	public void getSharesByEntity (Activity context, String entityKey, int start, int end, ShareListListener listener);
	
	public void getSharesByApplication (Activity context, int start, int end, ShareListListener listener);
	
	public void registerShare(Activity context, Entity entity, ShareOptions shareOptions, ShareAddListener listener, SocialNetwork...networks);
	
	@Synchronous
	public boolean canShareViaEmail(Activity context);
	
	@Synchronous
	public boolean canShareViaSMS(Activity context);
}
