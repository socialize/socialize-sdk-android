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
package com.socialize.ui.share;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import com.socialize.entity.Entity;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.ui.dialog.AsyncDialogFactory;
import com.socialize.ui.dialog.DialogFactory;
import com.socialize.ui.dialog.SocializeDialogListener;


/**
 * @author Jason Polites
 *
 */
public class ShareDialogFactory extends AsyncDialogFactory<SharePanelView> implements DialogFactory<SharePanelView> {

	@Override
	public Dialog show(Context context, Entity entity, SocialNetworkListener socialNetworkListener, SocializeDialogListener<SharePanelView> shareDialoglistener, int displayOptions) {
		return null;
		
	}

	@Override
	public Dialog show(Context context, SocializeDialogListener<SharePanelView> listener, int displayOptions) {
		return null;
		
	}

	@Override
	public Dialog show(View parent, SocialNetworkListener socialNetworkListener, SocializeDialogListener<SharePanelView> listener, int displayOptions) {
		return null;
		
	}

	@Override
	public Dialog show(View parent, int displayOptions) {
		return null;
		
	}

}
