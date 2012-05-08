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
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import com.socialize.android.ioc.BeanCreationListener;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Entity;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.auth.ShareDialogListener;
import com.socialize.ui.util.Colors;
import com.socialize.util.DisplayUtils;

/**
 * @author Jason Polites
 *
 */
public class AuthRequestDialogFactory extends BaseAuthDialogFactory  {
	
	private IBeanFactory<AuthPanelView> authPanelViewFactory;
	private DisplayUtils displayUtils;
	private Colors colors;
	
	@Override
	public Dialog show(View parent, int displayOptions) {
		return show(parent, null, null, displayOptions);
	}
	
	@Override
	public Dialog show(final View parent, SocialNetworkListener socialNetworkListener, final ShareDialogListener listener, int displayOptions) {
		return show(parent.getContext(), listener, displayOptions);
	}
	
	@Override
	public Dialog show(final Context context, final ShareDialogListener listener, int displayOptions) {
		return show(context, null, null, listener, displayOptions);
	}

	@Override
	public Dialog show(final Context context, Entity entity, final SocialNetworkListener socialNetworkListener, final ShareDialogListener shareDialoglistener, int displayOptions) {

		final Dialog dialog = newDialog(context);
		final ProgressDialog progress = SafeProgressDialog.show(context, "", "Please wait...");
		
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
				
				GradientDrawable background = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] { colors.getColor(Colors.AUTH_REQUEST_DIALOG_BOTTOM), colors.getColor(Colors.AUTH_REQUEST_DIALOG_TOP)});
				
				background.setCornerRadius(displayUtils.getDIP(4));
				
				view.setBackgroundDrawable(background);				
				dialog.setContentView(view, params);
				
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			    lp.copyFrom(dialog.getWindow().getAttributes());
			    lp.width = WindowManager.LayoutParams.FILL_PARENT;
			    lp.height = WindowManager.LayoutParams.FILL_PARENT;
			    
			    dialog.getWindow().setAttributes(lp);				
				
				progress.dismiss();
				
				try {
					dialog.show();
					
					if(shareDialoglistener != null) {
						shareDialoglistener.onShow(dialog, view);
					}
				}
				catch (Exception e) {
					// TODO: log error
					e.printStackTrace();
				}
				
	
			}
		}, entity, socialNetworkListener, shareDialoglistener, dialog, displayOptions);
		
		return dialog;
	}
	

	public void setAuthPanelViewFactory(IBeanFactory<AuthPanelView> authPanelViewFactory) {
		this.authPanelViewFactory = authPanelViewFactory;
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}
	
	public void setColors(Colors colors) {
		this.colors = colors;
	}
}
