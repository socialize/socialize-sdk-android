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

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.auth.AuthProviderType;
import com.socialize.networks.facebook.FacebookSignInCell;
import com.socialize.networks.twitter.TwitterSignInCell;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 *
 */
public class AuthPanelView extends BaseView {

	public AuthPanelView(Context context) {
		super(context);
	}
	
	private IBeanFactory<FacebookSignInCell> facebookSignInCellFactory;
	private IBeanFactory<TwitterSignInCell> twitterSignInCellFactory;
	private Drawables drawables;
	private DeviceUtils deviceUtils;
	
	private FacebookSignInCell facebookSignInCell;
	private TwitterSignInCell twitterSignInCell;

	public void init() {
		
		int padding = deviceUtils.getDIP(12);
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		LayoutParams wrapParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LayoutParams fillParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams fillParamsCenter = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		setLayoutParams(params);
		setPadding(padding, padding, padding, padding);
		
		LinearLayout layout = new LinearLayout(getContext());
		LinearLayout badgeLayout = new LinearLayout(getContext());
		
		badgeLayout.setLayoutParams(fillParamsCenter);
		layout.setOrientation(VERTICAL);
	
		params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		fillParamsCenter.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		
		layout.setLayoutParams(params);
		
		ImageView authBadge = new ImageView(getContext());
		authBadge.setImageDrawable(drawables.getDrawable("auth_badge.png"));
		authBadge.setLayoutParams(wrapParams);
		
		boolean fbOK = getSocialize().isSupported(AuthProviderType.FACEBOOK);
		boolean twOK = getSocialize().isSupported(AuthProviderType.TWITTER);
		
		float radii = deviceUtils.getDIP(8);
		
		if(fbOK) {
			facebookSignInCell = facebookSignInCellFactory.getBean();
			facebookSignInCell.setLayoutParams(fillParams);
			facebookSignInCell.setPadding(padding, padding, padding, padding);
			
			if(twOK) {
				twitterSignInCell = twitterSignInCellFactory.getBean();
				twitterSignInCell.setPadding(padding, padding, padding, padding);
				twitterSignInCell.setLayoutParams(fillParams);
				facebookSignInCell.setBackgroundRadii(new float[]{radii, radii, radii, radii, 0.0f, 0.0f, 0.0f, 0.0f});
				twitterSignInCell.setBackgroundRadii(new float[]{0.0f, 0.0f, 0.0f, 0.0f, radii, radii, radii, radii});
			}
		}
		else if(twOK) {
			twitterSignInCell = twitterSignInCellFactory.getBean();
			twitterSignInCell.setLayoutParams(fillParams);
			twitterSignInCell.setPadding(padding, padding, padding, padding);
		}
		
	
		badgeLayout.addView(authBadge);
		
		layout.addView(badgeLayout);
		
		if(fbOK) layout.addView(facebookSignInCell);
		if(twOK) layout.addView(twitterSignInCell);
		
		addView(layout);
	}

	public void setFacebookSignInCellFactory(IBeanFactory<FacebookSignInCell> facebookSignInCellFactory) {
		this.facebookSignInCellFactory = facebookSignInCellFactory;
	}

	public void setTwitterSignInCellFactory(IBeanFactory<TwitterSignInCell> twitterSignInCellFactory) {
		this.twitterSignInCellFactory = twitterSignInCellFactory;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public FacebookSignInCell getFacebookSignInCell() {
		return facebookSignInCell;
	}

	public TwitterSignInCell getTwitterSignInCell() {
		return twitterSignInCell;
	}
}
