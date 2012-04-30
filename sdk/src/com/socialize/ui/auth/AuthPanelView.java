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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.socialize.ShareUtils;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.facebook.FacebookSignInCell;
import com.socialize.networks.twitter.TwitterSignInCell;
import com.socialize.ui.util.Colors;
import com.socialize.ui.view.ClickableSectionCell;
import com.socialize.ui.view.SocializeButton;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

/**
 * @author Jason Polites
 */
public class AuthPanelView extends BaseView {

	private AuthRequestDialogListener listener;
	private Dialog dialog;
	private SocializeConfig config;
	private Colors colors;
	
	private SocializeButton continueButton;
	private SocializeButton cancelButton;
	
	private int displayOptions;
	
	public AuthPanelView(Context context, AuthRequestDialogListener listener, Dialog dialog, int displayOptions) {
		this(context);
		this.listener = listener;
		this.dialog = dialog;
		this.displayOptions = displayOptions;
	}

	public AuthPanelView(Context context) {
		super(context);
	}
	
	private IBeanFactory<FacebookSignInCell> facebookSignInCellFactory;
	private IBeanFactory<TwitterSignInCell> twitterSignInCellFactory;
	private IBeanFactory<AnonymousCell> anonCellFactory; 
	
	private Drawables drawables;
	private DisplayUtils displayUtils;
	
	private FacebookSignInCell facebookSignInCell;
	private TwitterSignInCell twitterSignInCell;

	public void init() {
		
		int padding = displayUtils.getDIP(12);
		
		LayoutParams masterParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		LayoutParams contentParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams buttonParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams anonParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams headerParams = new LayoutParams(LayoutParams.FILL_PARENT, displayUtils.getDIP(45));
		RelativeLayout.LayoutParams badgeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LayoutParams cellParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams badgeLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		badgeLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		masterParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		badgeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		setLayoutParams(masterParams);
		setOrientation(VERTICAL);
		
		contentParams.setMargins(padding, padding, padding, padding);
		contentParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		contentParams.weight = 1.0f;
		
		TextView header = new TextView(getContext());
	
		float headerRadius = displayUtils.getDIP(3);
		
		GradientDrawable headerBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{colors.getColor(Colors.AUTH_PANEL_BOTTOM), colors.getColor(Colors.AUTH_PANEL_TOP)});
		headerBG.setCornerRadii(new float[]{headerRadius, headerRadius, headerRadius, headerRadius, 0.0f, 0.0f, 0.0f, 0.0f});
		header.setBackgroundDrawable(headerBG);
		header.setText("Authenticate");
		header.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		header.setTextColor(Color.WHITE);
		header.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		header.setLayoutParams(headerParams);
		
		LinearLayout contentLayout = new LinearLayout(getContext());
		contentLayout.setPadding(padding, padding, padding, padding);
		contentLayout.setOrientation(VERTICAL);
		contentLayout.setLayoutParams(contentParams);
		
		LinearLayout buttonLayout = new LinearLayout(getContext());
		buttonLayout.setPadding(padding, padding, padding, padding);
		buttonLayout.setOrientation(HORIZONTAL);
		buttonLayout.setLayoutParams(buttonParams);
		buttonLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
		
		RelativeLayout badgeLayout = new RelativeLayout(getContext());
		badgeLayout.setLayoutParams(badgeLayoutParams);

		ImageView authBadge = new ImageView(getContext());
		authBadge.setImageDrawable(drawables.getDrawable("auth_badge.png"));
		authBadge.setLayoutParams(badgeParams);
		authBadge.setPadding(0, 0, 0, padding);
		
		boolean fbOK = getSocialize().isSupported(AuthProviderType.FACEBOOK) && ((displayOptions & ShareUtils.FACEBOOK) != 0);
		boolean twOK = getSocialize().isSupported(AuthProviderType.TWITTER) && ((displayOptions & ShareUtils.TWITTER) != 0);
		
		float radii = displayUtils.getDIP(8);
		
		if(fbOK) {
			facebookSignInCell = facebookSignInCellFactory.getBean();
			facebookSignInCell.setLayoutParams(cellParams);
			facebookSignInCell.setPadding(padding, padding, padding, padding);
			
			if(twOK) {
				twitterSignInCell = twitterSignInCellFactory.getBean();
				twitterSignInCell.setPadding(padding, padding, padding, padding);
				twitterSignInCell.setLayoutParams(cellParams);
				
				float[] fbRadii = new float[]{radii, radii, radii, radii, 0.0f, 0.0f, 0.0f, 0.0f};
				int[] fbStroke = new int[]{1, 1, 0, 1};
				
				float[] twRadii = new float[]{0.0f, 0.0f, 0.0f, 0.0f, radii, radii, radii, radii};
				int[] twStroke = new int[]{1, 1, 1, 1};
				
				facebookSignInCell.setBackgroundData(fbRadii, fbStroke, Color.BLACK);
				twitterSignInCell.setBackgroundData(twRadii, twStroke, Color.BLACK);
			}
		}
		else if(twOK) {
			twitterSignInCell = twitterSignInCellFactory.getBean();
			twitterSignInCell.setLayoutParams(cellParams);
			twitterSignInCell.setPadding(padding, padding, padding, padding);
		}
		
		if(facebookSignInCell != null) {
			facebookSignInCell.setToggled(getSocialize().isAuthenticated(AuthProviderType.FACEBOOK));
			facebookSignInCell.setAuthListener(getAuthClickListener(facebookSignInCell, SocialNetwork.FACEBOOK));
		}
		
		if(twitterSignInCell != null) {
			twitterSignInCell.setToggled(getSocialize().isAuthenticated(AuthProviderType.TWITTER));
			twitterSignInCell.setAuthListener(getAuthClickListener(twitterSignInCell, SocialNetwork.TWITTER));
		}
		
		badgeLayout.addView(authBadge);
		contentLayout.addView(badgeLayout);
		
		if(fbOK) contentLayout.addView(facebookSignInCell);
		if(twOK) contentLayout.addView(twitterSignInCell);
		
		LinearLayout anonLayout = new LinearLayout(getContext());
		anonLayout.setPadding(0, padding, 0, padding);
		anonLayout.setOrientation(VERTICAL);
		anonLayout.setLayoutParams(anonParams);		
		
		AnonymousCell anonCell = anonCellFactory.getBean();
		
		anonLayout.addView(anonCell);
		
		contentLayout.addView(anonLayout);

		
//		if(config.isAllowAnonymousUser()) {
		
//			LayoutParams cancelParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
//			cancelParams.setMargins(0, padding * 2, 0, 0);		
//			RelativeLayout cancelLayout = new RelativeLayout(getContext());
//			cancelLayout.setLayoutParams(cancelParams);	
//			TextView cancelText = new TextView(getContext());
//			cancelText.setTextColor(colors.getColor(Colors.AUTH_PANEL_CANCEL_TEXT));
//			cancelText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
//			cancelText.setText("I'd rather remain anonymous...");
//			
//			RelativeLayout.LayoutParams cancelTextParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			cancelTextParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//			
//			cancelText.setLayoutParams(cancelTextParams);
//			
//			cancelLayout.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					dialog.dismiss();
//					if(listener != null) {
//						listener.onCancel(dialog);
//					}
//				}
//			});
//			
//			cancelLayout.addView(cancelText);
//			contentLayout.addView(cancelLayout);
//		}
		
		if(cancelButton != null) {
			
			cancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(listener != null) {
						dialog.dismiss();
						listener.onCancel(dialog);
					}
				}
			});
			
			buttonLayout.addView(cancelButton);
		}
		
		if(continueButton != null) {
			continueButton.setEnabled(false);
			continueButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					SocialNetwork[] networks = null;
					
					// Get the number of networks enabled
					if(facebookSignInCell.isToggled()) {
						if(twitterSignInCell.isToggled()) {
							networks = new SocialNetwork[]{SocialNetwork.FACEBOOK, SocialNetwork.TWITTER};
						}
						else {
							networks = new SocialNetwork[]{SocialNetwork.FACEBOOK};
						}
					}
					else if(twitterSignInCell.isToggled()) {
						networks = new SocialNetwork[]{SocialNetwork.TWITTER};
					}
					
					listener.onContinue(dialog, networks);
				}
			});
			
			buttonLayout.addView(continueButton);
		}
		
		addView(header);
		addView(contentLayout);
		addView(buttonLayout);
		
		toggleContinueButton();
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

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public FacebookSignInCell getFacebookSignInCell() {
		return facebookSignInCell;
	}

	public TwitterSignInCell getTwitterSignInCell() {
		return twitterSignInCell;
	}

	public void setAnonCellFactory(IBeanFactory<AnonymousCell> anonCellFactory) {
		this.anonCellFactory = anonCellFactory;
	}
	
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}
	
	public void setContinueButton(SocializeButton continueButton) {
		this.continueButton = continueButton;
	}
	
	public void setCancelButton(SocializeButton cancelButton) {
		this.cancelButton = cancelButton;
	}
	
	protected void toggleContinueButton() {
		if(config.isAllowAnonymousUser()) {
			continueButton.setEnabled(true);
		}
		else {
			continueButton.setEnabled(twitterSignInCell.isToggled() || facebookSignInCell.isToggled());
		}
	}

	protected SocializeAuthListener getAuthClickListener(final ClickableSectionCell cell, final SocialNetwork network) {
		return new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				cell.setEnabled(false);
				if(listener != null) {
					listener.onAuthFail(dialog, network, error);
				}
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				cell.setToggled(!cell.isToggled());
				toggleContinueButton();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				cell.setEnabled(false);
				if(listener != null) {
					listener.onAuthFail(dialog, network, error);
				}
			}

			@Override
			public void onCancel() {}
		};
	}	
}
