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
package com.socialize.ui.auth;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.api.SocializeSessionPersister;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.error.SocializeException;
import com.socialize.i18n.I18NConstants;
import com.socialize.i18n.LocalizationService;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.facebook.FacebookSignInCell;
import com.socialize.networks.twitter.TwitterSignInCell;
import com.socialize.ui.dialog.DialogPanelView;
import com.socialize.ui.profile.UserSettings;
import com.socialize.ui.share.RememberCell;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.CompatUtils;
import com.socialize.ui.view.ClickableSectionCell;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 */
public class AuthPanelView extends DialogPanelView {

	private Colors colors;
	private Drawables drawables;
	private DisplayUtils displayUtils;
	private IBeanFactory<FacebookSignInCell> facebookSignInCellFactory;
	private IBeanFactory<TwitterSignInCell> twitterSignInCellFactory;
	private IBeanFactory<AnonymousCell> anonCellFactory;
	private IBeanFactory<RememberCell> rememberAuthCellFactory;
	private SocializeConfig config;
	private SocializeSessionPersister sessionPersister;
	private LocalizationService localizationService;
	
	public AuthPanelView(Context context) {
		super(context);
	}

	public AuthPanelView(Context context, AuthDialogListener listener) {
		super(context);
		this.authDialogListener = listener;
	}
	
	private AuthDialogListener authDialogListener;
	private FacebookSignInCell facebookSignInCell;
	private TwitterSignInCell twitterSignInCell;
	private AnonymousCell anonymousCell;
	private RememberCell rememberCell;
	private TextView skipAuth;
	
	float radii = 6;
	int padding = 8;
	int headerHeight = 45;
	float headerRadius = 3;
	
	private final float[] fbRadii = new float[]{radii, radii, radii, radii, 0.0f, 0.0f, 0.0f, 0.0f};
	private final int[] fbStroke = new int[]{1, 1, 0, 1};
	
	private final float[] twRadii = new float[]{0.0f, 0.0f, 0.0f, 0.0f, radii, radii, radii, radii};
	private final int[] twStroke = new int[]{1, 1, 1, 1};
	
	public void init() {
		
		boolean landscape = false;
		boolean lowRes = false;
		
		if(displayUtils != null) {
			padding = displayUtils.getDIP(12);
			headerRadius = displayUtils.getDIP(3);
			headerHeight = displayUtils.getDIP(45);
			radii = displayUtils.getDIP(8);
			landscape = displayUtils.isLandscape();
			lowRes = displayUtils.isLowRes();
		}
		
		LayoutParams masterParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		masterParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		
		setLayoutParams(masterParams);
		setOrientation(VERTICAL);
		
		RelativeLayout container = new RelativeLayout(getContext());
		LayoutParams containerParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		containerParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		containerParams.weight = 1.0f;
		container.setLayoutParams(containerParams);
		
		makeAuthButtons();
		
		View header = makeHeaderView(headerHeight, headerRadius);
		
		RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		contentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		LinearLayout contentLayout = new LinearLayout(getContext());
		contentLayout.setPadding(padding, padding, padding, 0);
		contentLayout.setLayoutParams(contentParams);
		contentLayout.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
		
		LayoutParams socialNetworkButtonParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		contentLayout.setOrientation(VERTICAL);
		
		LinearLayout socialNetworkButtonLayout = new LinearLayout(getContext());
		socialNetworkButtonLayout.setPadding(0, 0, 0, 0);
		socialNetworkButtonLayout.setOrientation(VERTICAL);
		socialNetworkButtonLayout.setLayoutParams(socialNetworkButtonParams);	

		if(!landscape && !lowRes) {
			View shareBadge = makeShareBadge();
			contentLayout.addView(shareBadge);
		}
		
		if(facebookSignInCell != null || twitterSignInCell != null) {
			if(facebookSignInCell != null) {
				socialNetworkButtonLayout.addView(facebookSignInCell);
			}
			if(twitterSignInCell != null) {
				socialNetworkButtonLayout.addView(twitterSignInCell);
			}
			contentLayout.addView(socialNetworkButtonLayout);
		}
		
		if(anonymousCell != null) {
			contentLayout.addView(anonymousCell);
		}
		
		LayoutParams skipAuthParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		skipAuthParams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
		skipAuthParams.weight = 1.0f;
		
		if(displayUtils != null) {
			skipAuthParams.setMargins(0, displayUtils.getDIP(30), 0, 0);
		}
		
		skipAuth = new TextView(getContext());
		skipAuth.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		if(colors != null) skipAuth.setTextColor(colors.getColor(Colors.ANON_CELL_TITLE));
		skipAuth.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
		skipAuth.setPadding(0, 0, 0, padding);
		skipAuth.setLayoutParams(skipAuthParams);
		
		if(rememberCell != null) {
			contentLayout.addView(rememberCell);
		}
		
		contentLayout.addView(skipAuth);
		
		container.addView(contentLayout);
		
		addView(header);
		addView(container);
	}
	
	public void setAuthRequired(final boolean required) {
		if(!required) {
			
			if(rememberCell != null) {
				rememberCell.setVisibility(View.VISIBLE);
			}
			
			String mystring = localizationService.getString(I18NConstants.AUTH_RATHER_NOT);
			SpannableString content = new SpannableString(mystring);
			content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
			skipAuth.setText(content);
			skipAuth.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(rememberCell != null && rememberCell.isToggled()) {
						// Never auth again
						UserSettings settings = getSocialize().getSession().getUserSettings();
						settings.setShowAuthDialog(false);
						sessionPersister.saveUserSettingsAsync(getContext(), settings);
					}
					
					if(authDialogListener != null) {
						authDialogListener.onSkipAuth(getActivity(), dialog);
					}
				}
			});
		}
		else {
			if(rememberCell != null) {
				rememberCell.setVisibility(View.GONE);
			}
			
			skipAuth.setText(localizationService.getString(I18NConstants.AUTH_REQUIRED));
		}
	}
	
	protected View makeShareBadge() {
		RelativeLayout.LayoutParams badgeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		LayoutParams badgeLayoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		badgeLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		
		badgeParams.addRule(RelativeLayout.CENTER_IN_PARENT);		
		
		RelativeLayout badgeLayout = new RelativeLayout(getContext());
		badgeLayout.setLayoutParams(badgeLayoutParams);
		
		if(drawables != null) {
			ImageView authBadge = new ImageView(getContext());
			authBadge.setImageDrawable(drawables.getDrawable("auth_badge.png"));
			authBadge.setLayoutParams(badgeParams);
			authBadge.setPadding(0, 0, 0, padding);
			badgeLayout.addView(authBadge);
		}
		
		return badgeLayout;
	}
	
	protected void makeAuthButtons() {
		
		LayoutParams cellParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		boolean fbOK = getSocialize().isSupported(getContext(), AuthProviderType.FACEBOOK) && facebookSignInCellFactory != null;
		boolean twOK = getSocialize().isSupported(getContext(), AuthProviderType.TWITTER) && twitterSignInCellFactory != null;
		boolean rememberOk = rememberAuthCellFactory != null && config.isAllowNeverAuth();
		
		if(fbOK) {
			facebookSignInCell = facebookSignInCellFactory.getBean();
			
			if(facebookSignInCell != null) {
				facebookSignInCell.setLayoutParams(cellParams);
				facebookSignInCell.setPadding(padding, padding, padding, padding);
				
				if(twOK) {
					twitterSignInCell = twitterSignInCellFactory.getBean();
					twitterSignInCell.setPadding(padding, padding, padding, padding);
					twitterSignInCell.setLayoutParams(cellParams);
					
					facebookSignInCell.setBackgroundData(fbRadii, fbStroke, Color.BLACK);
					twitterSignInCell.setBackgroundData(twRadii, twStroke, Color.BLACK);
				}
			}
		}
		else if(twOK) {
			twitterSignInCell = twitterSignInCellFactory.getBean();
			
			if(twitterSignInCell != null) {
				twitterSignInCell.setLayoutParams(cellParams);
				twitterSignInCell.setPadding(padding, padding, padding, padding);
			}
		}
		
		if(facebookSignInCell != null) {
			facebookSignInCell.setAuthListener(getAuthClickListener(facebookSignInCell, SocialNetwork.FACEBOOK));
		}
		
		if(twitterSignInCell != null) {
			twitterSignInCell.setAuthListener(getAuthClickListener(twitterSignInCell, SocialNetwork.TWITTER));
		}
		
		if(rememberOk) {
			rememberCell = rememberAuthCellFactory.getBean();
			
			if(rememberCell != null) {
				LayoutParams rememberCellParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				rememberCellParams.setMargins(0, padding, 0, 0);
				rememberCell.setLayoutParams(rememberCellParams);
			}
		}
				
		if(anonCellFactory != null) {
			anonymousCell = anonCellFactory.getBean();
			
			if(anonymousCell != null) {
				LayoutParams anonCellParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				anonCellParams.setMargins(0, padding, 0, 0);
				anonymousCell.setLayoutParams(anonCellParams);
			}
		}
	}
	
	protected View makeHeaderView(int headerHeight, float headerRadius) {
		LayoutParams headerParams = new LayoutParams(LayoutParams.FILL_PARENT, headerHeight);
		
		TextView header = new TextView(getContext());
		
		if(colors != null) {
			GradientDrawable headerBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{colors.getColor(Colors.AUTH_PANEL_BOTTOM), colors.getColor(Colors.AUTH_PANEL_TOP)});
			headerBG.setCornerRadii(new float[]{headerRadius, headerRadius, headerRadius, headerRadius, 0.0f, 0.0f, 0.0f, 0.0f});

			CompatUtils.setBackgroundDrawable(header, headerBG);
		}

        if(localizationService != null) {
            header.setText(localizationService.getString(I18NConstants.AUTH_HEADER));
        }

		header.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		header.setTextColor(Color.WHITE);
		header.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		header.setLayoutParams(headerParams);
		
		return header;
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
	
	public void setAuthDialogListener(AuthDialogListener authDialogListener) {
		this.authDialogListener = authDialogListener;
	}

	public void setAnonCellFactory(IBeanFactory<AnonymousCell> anonCellFactory) {
		this.anonCellFactory = anonCellFactory;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}
	
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public void setRememberAuthCellFactory(IBeanFactory<RememberCell> rememberAuthCellFactory) {
		this.rememberAuthCellFactory = rememberAuthCellFactory;
	}
	
	public void setSessionPersister(SocializeSessionPersister sessionPersister) {
		this.sessionPersister = sessionPersister;
	}
	
	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}

	protected SocializeAuthListener getAuthClickListener(final ClickableSectionCell cell, final SocialNetwork network) {
		return new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				SocializeLogger.e(error.getMessage(), error);
				showErrorToast(getContext(), error);
				if(authDialogListener != null) {
					authDialogListener.onError(getActivity(), dialog, error);
				}
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				if(authDialogListener != null) {
					authDialogListener.onAuthenticate(getActivity(), dialog, network);
				}
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				SocializeLogger.e(error.getMessage(), error);
				showErrorToast(getContext(), error);
				if(authDialogListener != null) {
					authDialogListener.onError(getActivity(), dialog, error);
				}
			}

			@Override
			public void onCancel() {
				if(authDialogListener != null) {
					authDialogListener.onCancel(dialog);
				}				
			}
		};
	}	
}
