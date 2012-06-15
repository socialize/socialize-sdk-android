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
package com.socialize.demo.snippets;

import com.socialize.ShareUtils;
import com.socialize.api.action.share.SocialNetworkDialogListener;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.auth.AuthDialogListener;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.share.DialogFlowController;
import com.socialize.ui.share.ShareDialogListener;
import com.socialize.ui.share.SharePanelView;
import android.app.Activity;
import android.app.Dialog;


/**
 * @author Jason Polites
 *
 */
public class ShareSnippets extends Activity {

	
public void showShareDialog() {
// begin-snippet-0
Entity entity = Entity.newInstance("http://myentity.com", "My Name");

// The "this" argument refers to the current Activity
ShareUtils.showShareDialog(this, entity);
//end-snippet-0	
}

public void showShareDialogWithInterrupt() {
// begin-snippet-1
Entity entity = Entity.newInstance("http://myentity.com", "My Name");	

// The "this" argument refers to the current Activity
ShareUtils.showShareDialog(this, entity, new ShareDialogListener() {
	
	@Override
	public void onShow(Dialog dialog, SharePanelView dialogView) {
		// The dialog was shown.
	}
	
	@Override
	public void onCancel(Dialog dialog) {
		// User cancelled.
	}
	
	@Override
	public void onFlowInterrupted(DialogFlowController controller) {
		// This will only be called if onContinue returns false
		
		// Obtain a comment from the user
		String text = "...";
		
		// Call continue when you want flow to resume
		controller.onContinue(text);
	}
	
	@Override
	public boolean onContinue(Dialog dialog, boolean remember, SocialNetwork... networks) {
		// Return true if you want to interrupt the flow
		return true;
	}
});
//end-snippet-1	
}

public void showShareDialogWithOptions() {
// begin-snippet-2
Entity entity = Entity.newInstance("http://myentity.com", "My Name");	

// Setup the options for display
int options = ShareUtils.FACEBOOK | ShareUtils.TWITTER;

// The "this" argument refers to the current Activity
ShareUtils.showShareDialog(this, entity, new SocialNetworkDialogListener() {

	@Override
	public void onCancel(Dialog dialog) {
		// User cancelled
	}

	@Override
	public void onError(SocializeException error) {
		// Handle error
	}

	@Override
	public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
		// Failed to post to given network
	}
	
}, options);
//end-snippet-2	
}


public void showShareDialogWithOverride() {
// begin-snippet-3
Entity entity = Entity.newInstance("http://myentity.com", "My Name");	

// Setup the options for display
int options = ShareUtils.SOCIAL; // This is just a shortcut for Twitter and Facebook.

// The "this" argument refers to the current Activity
ShareUtils.showShareDialog(this, entity, new SocialNetworkDialogListener() {

	@Override
	public void onCancel(Dialog dialog) {
		// User cancelled
	}

	@Override
	public void onError(SocializeException error) {
		// Handle error
	}

	@Override
	public void onNetworkError(Activity context, SocialNetwork network, Exception error) {
		// Failed to post to given network
	}

	@Override
	public void onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
		// Change data for facebook post
		if(socialNetwork != null && socialNetwork.equals(SocialNetwork.FACEBOOK)) {
			postData.getPostValues().put("foo", "bar");
		}
	}
	
}, options);
//end-snippet-3	
}

public void showLinkDialog() {
// begin-snippet-4
// The "this" argument refers to the current Activity
ShareUtils.showLinkDialog(this, new AuthDialogListener() {
	
	@Override
	public void onShow(Dialog dialog, AuthPanelView dialogView) {
		// Dialog was shown
	}
	
	@Override
	public void onCancel(Dialog dialog) {
		// User cancelled
	}
	
	@Override
	public void onSkipAuth(Activity context, Dialog dialog) {
		// User elected not to auth (only available if socialize.allow.anon is true)
	}
	
	@Override
	public void onError(Activity context, Dialog dialog, Exception error) {
		// Handle error
	}
	
	@Override
	public void onAuthenticate(Activity context, Dialog dialog, SocialNetwork network) {
		// User authenticated with the given network
	}
});
//end-snippet-4	
}
}
