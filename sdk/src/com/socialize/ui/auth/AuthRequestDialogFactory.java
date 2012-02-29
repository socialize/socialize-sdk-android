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
package com.socialize.ui.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.view.WindowManager;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.facebook.FacebookSignInCell;
import com.socialize.networks.twitter.TwitterSignInCell;

/**
 * @author Jason Polites
 *
 */
public class AuthRequestDialogFactory extends AuthDialogFactory  {
	
	private IBeanFactory<AuthPanelView> authPanelViewFactory;
//	private IBeanFactory<AuthConfirmDialogFactory> authConfirmDialogFactory;
	
	public AlertDialog create(final Context context) {
		return create(context, null);
	}
	
	public AlertDialog create(final Context context, final AuthRequestListener listener) {

		AlertDialog.Builder builder = newBuilder(context);
		
		AuthPanelView view = authPanelViewFactory.getBean();
		
		builder.setView(view);
		
		final AlertDialog alertDialog = builder.create();
		
		alertDialog.setIcon(drawables.getDrawable("socialize_icon_white.png"));
		alertDialog.setTitle("Authentication");
		
		SocializeAuthListener authListener = getAuthClickListener(alertDialog, listener);
		
		FacebookSignInCell facebookSignInCell = view.getFacebookSignInCell();
		TwitterSignInCell twitterSignInCell = view.getTwitterSignInCell();
		
		if(facebookSignInCell != null) {
			facebookSignInCell.setAuthListener(authListener);
		}
		
		if(twitterSignInCell != null) {
			twitterSignInCell.setAuthListener(authListener);
		}
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(alertDialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    lp.height = WindowManager.LayoutParams.FILL_PARENT;		
		
//		alertDialog.setOnCancelListener(new OnCancelListener() {
//			@Override
//			public void onCancel(DialogInterface dialog) {
//				// Render the confirm dialog
//				AuthConfirmDialogFactory dialogFactory = authConfirmDialogFactory.getBean();
//				dialogFactory.show(context, listener);
//			}
//		});
//		
		
		return alertDialog;
	}
	
	protected SocializeAuthListener getAuthClickListener(final AlertDialog alertDialog, final AuthRequestListener listener) {
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
}
