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
package com.socialize.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.facebook.FacebookSignInCell;
import com.socialize.networks.twitter.TwitterSignInCell;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.auth.AuthRequestListener;
import com.socialize.util.DeviceUtils;

/**
 * @author Jason Polites
 *
 */
public class AuthRequestDialogFactory extends AuthDialogFactory  {
	
	private IBeanFactory<AuthPanelView> authPanelViewFactory;
	private DeviceUtils deviceUtils;
	
	public Dialog create(View parent) {
		return create(parent, null);
	}
	
	public Dialog create(final View parent, final AuthRequestListener listener) {

		Dialog dialog = newDialog(parent.getContext());
		
		AuthPanelView view = authPanelViewFactory.getBean();
		
		GradientDrawable background = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { Color.parseColor("#323a43"), Color.parseColor("#1d2227") });
		
		view.setBackgroundDrawable(background);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		
		dialog.setContentView(view, params);
		
		SocializeAuthListener authListener = getAuthClickListener(dialog, listener);
		
		FacebookSignInCell facebookSignInCell = view.getFacebookSignInCell();
		TwitterSignInCell twitterSignInCell = view.getTwitterSignInCell();
		
		if(facebookSignInCell != null) {
			facebookSignInCell.setAuthListener(authListener);
		}
		
		if(twitterSignInCell != null) {
			twitterSignInCell.setAuthListener(authListener);
		}
		
//		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//	    lp.copyFrom(dialog.getWindow().getAttributes());
//	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
//	    lp.height = WindowManager.LayoutParams.FILL_PARENT;
//	    
//	    dialog.getWindow().setAttributes(lp);
		
		return dialog;
	}
	
	protected SocializeAuthListener getAuthClickListener(final Dialog alertDialog, final AuthRequestListener listener) {
		return new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				handleError("Error during auth", error);
				alertDialog.dismiss();
				if(listener != null) {
					listener.onResult(alertDialog);
				}
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				alertDialog.dismiss();
				if(listener != null) {
					listener.onResult(alertDialog);
				}
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				handleError("Error during auth", error);
				alertDialog.dismiss();
				if(listener != null) {
					listener.onResult(alertDialog);
				}
			}

			@Override
			public void onCancel() {
				// Do nothing
			}
		};
	}

	public void setAuthPanelViewFactory(IBeanFactory<AuthPanelView> authPanelViewFactory) {
		this.authPanelViewFactory = authPanelViewFactory;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}
}
