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
import android.app.ProgressDialog;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import com.socialize.android.ioc.BeanCreationListener;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.facebook.FacebookSignInCell;
import com.socialize.networks.twitter.TwitterSignInCell;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.auth.AuthRequestListener;
import com.socialize.ui.util.Colors;
import com.socialize.util.DisplayUtils;

/**
 * @author Jason Polites
 *
 */
public class AuthRequestDialogFactory extends AuthDialogFactory  {
	
	private IBeanFactory<AuthPanelView> authPanelViewFactory;
	private DisplayUtils displayUtils;
	
	public Dialog create(View parent) {
		return create(parent, null);
	}
	
	public Dialog create(final View parent, final AuthRequestListener listener) {

		final Dialog dialog = newDialog(parent.getContext());
		final ProgressDialog progress = SafeProgressDialog.show(parent.getContext(), "", "Please wait...");
		
		authPanelViewFactory.getBeanAsync(new BeanCreationListener<AuthPanelView>() {
			
			@Override
			public void onError(String name, Exception e) {
				dialog.dismiss();
				progress.dismiss();
				e.printStackTrace();
			}
			
			@Override
			public void onCreate(AuthPanelView view) {
				LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
				
				GradientDrawable background = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { Colors.parseColor("#323a43"), Colors.parseColor("#1d2227") });
				
				background.setCornerRadius(displayUtils.getDIP(4));
				
				view.setBackgroundDrawable(background);				
				dialog.setContentView(view, params);
				
				FacebookSignInCell facebookSignInCell = view.getFacebookSignInCell();
				TwitterSignInCell twitterSignInCell = view.getTwitterSignInCell();
				
				if(facebookSignInCell != null) {
					facebookSignInCell.setAuthListener(getAuthClickListener(dialog, listener, SocialNetwork.FACEBOOK));
				}
				
				if(twitterSignInCell != null) {
					twitterSignInCell.setAuthListener(getAuthClickListener(dialog, listener, SocialNetwork.TWITTER));
				}	
				
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			    lp.copyFrom(dialog.getWindow().getAttributes());
			    lp.width = WindowManager.LayoutParams.FILL_PARENT;
			    lp.height = WindowManager.LayoutParams.FILL_PARENT;
			    
			    dialog.getWindow().setAttributes(lp);				
				
			    dialog.show();
			    
				progress.dismiss();
			}
		}, listener, dialog);
		
//		AuthPanelView view = authPanelViewFactory.getBean(listener, dialog);
		
		return dialog;
	}
	
	protected SocializeAuthListener getAuthClickListener(final Dialog alertDialog, final AuthRequestListener listener, final SocialNetwork network) {
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
					listener.onResult(alertDialog, network);
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

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}
}
