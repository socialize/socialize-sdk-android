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

import android.app.Dialog;
import com.socialize.api.action.ShareType;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.networks.SocialNetwork;
import com.socialize.ui.share.DialogFlowController;
import com.socialize.ui.share.ShareDialogListener;
import com.socialize.ui.share.SharePanelView;


/**
 * @author Jason Polites
 */
public abstract class SocialNetworkDialogListener extends SocialNetworkShareListener implements ShareDialogListener {

	/* (non-Javadoc)
	 * @see com.socialize.ui.auth.ShareDialogListener#onShow(android.app.Dialog, com.socialize.ui.auth.AuthPanelView)
	 */
	@Override
	public void onShow(Dialog dialog, SharePanelView dialogView) {}
	
	@Override
	public void onSimpleShare(ShareType type) {}	

	/* (non-Javadoc)
	 * @see com.socialize.ui.auth.ShareDialogListener#onContinue(android.app.Dialog, com.socialize.networks.SocialNetwork[])
	 */
	@Override
	public boolean onContinue(Dialog dialog, boolean remember, SocialNetwork... networks) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.socialize.ui.auth.ShareDialogListener#onFlowInterrupted(com.socialize.ui.auth.ShareDialogFlowController)
	 */
	@Override
	public void onFlowInterrupted(DialogFlowController controller) {}

	/* (non-Javadoc)
	 * @see com.socialize.ui.auth.ShareDialogListener#onCancel(android.app.Dialog)
	 */
	@Override
	public void onCancel(Dialog dialog) {}

	/* (non-Javadoc)
	 * @see com.socialize.listener.AbstractSocializeListener#onCreate(com.socialize.entity.SocializeObject)
	 */
	@Override
	public void onCreate(Share entity) {}

	/* (non-Javadoc)
	 * @see com.socialize.listener.AbstractSocializeListener#onError(com.socialize.error.SocializeException)
	 */
	@Override
	public void onError(SocializeException error) {}

}
