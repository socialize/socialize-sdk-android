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
package com.socialize.ui.share;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.socialize.ShareUtils;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.share.SimpleShareListener;
import com.socialize.auth.AuthProviderType;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.i18n.I18NConstants;
import com.socialize.i18n.LocalizationService;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.PostData;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.FacebookShareCell;
import com.socialize.networks.twitter.TwitterShareCell;
import com.socialize.ui.dialog.DialogPanelView;
import com.socialize.ui.dialog.SafeProgressDialog;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.CompatUtils;
import com.socialize.ui.view.ClickableSectionCell;
import com.socialize.ui.view.ClickableSectionCell.OnToggleListener;
import com.socialize.ui.view.SocializeButton;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import org.json.JSONObject;

/**
 * @author Jason Polites
 */
public class SharePanelView extends DialogPanelView {

	private ShareDialogListener shareDialogListener;
	private SocialNetworkListener socialNetworkListener;
	private Colors colors;
	private SocializeConfig config;
	
	private SocializeButton continueButton;
	private SocializeButton cancelButton;
	private TextView otherOptions;
	private LinearLayout buttonLayout;
	
	private int displayOptions = ShareUtils.DEFAULT;
	
	private Entity entity;
	
	public SharePanelView(Context context) {
		super(context);
	}
	
	private IBeanFactory<FacebookShareCell> facebookShareCellFactory;
	private IBeanFactory<TwitterShareCell> twitterShareCellFactory;
	private IBeanFactory<GooglePlusCell> googlePlusCellFactory;
	private IBeanFactory<EmailCell> emailCellFactory;
	private IBeanFactory<SMSCell> smsCellFactory;
	private IBeanFactory<RememberCell> rememberCellFactory;
	
	private Drawables drawables;
	private DisplayUtils displayUtils;
	
	private FacebookShareCell facebookShareCell;
	private TwitterShareCell twitterShareCell;
	
	private EmailCell emailCell;
	private SMSCell smsCell;
	private RememberCell rememberCell;
	private GooglePlusCell googlePlusCell;
	private LocalizationService localizationService;
	
	float radii = 6;
	int padding = 8;
	int headerHeight = 45;
	float headerRadius = 3;
	
	private float[] fbRadii = new float[]{radii, radii, radii, radii, 0.0f, 0.0f, 0.0f, 0.0f};
	private int[] fbStroke = new int[]{1, 1, 0, 1};
	
	private float[] twRadii = new float[]{0.0f, 0.0f, 0.0f, 0.0f, radii, radii, radii, radii};
	private int[] twStroke = new int[]{1, 1, 1, 1};
	
	public void init() {
		
		boolean landscape = false;
		
		if(displayUtils != null) {
			padding = displayUtils.getDIP(12);
			headerRadius = displayUtils.getDIP(3);
			headerHeight = displayUtils.getDIP(45);
			radii = displayUtils.getDIP(radii);
			landscape = displayUtils.isLandscape();
			fbRadii = new float[]{radii, radii, radii, radii, 0.0f, 0.0f, 0.0f, 0.0f};
			twRadii = new float[]{0.0f, 0.0f, 0.0f, 0.0f, radii, radii, radii, radii};
		}
		
		LayoutParams masterParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		masterParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		setLayoutParams(masterParams);
		
		setOrientation(VERTICAL);
		
		RelativeLayout container = new RelativeLayout(getContext());
		LayoutParams containerParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		containerParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		containerParams.weight = 1.0f;
		container.setLayoutParams(containerParams);
		
		makeShareButtons();
		
		View continueButtonLayout = makeButtons();
		View header = makeHeaderView(headerHeight, headerRadius);
		
		LinearLayout contentLayout = new LinearLayout(getContext());
		contentLayout.setPadding(padding, padding, padding, 0);
		
		contentLayout.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
		
		if(landscape) {
			LayoutParams contentParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			contentLayout.setLayoutParams(contentParams);
		}
		else {
			RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			contentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			contentLayout.setLayoutParams(contentParams);
		}		
		
		LayoutParams socialNetworkButtonParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutParams emailSMSButtonParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		emailSMSButtonParams.setMargins(0, padding, 0, 0);
		contentLayout.setOrientation(VERTICAL);
		
		LinearLayout socialNetworkButtonLayout = new LinearLayout(getContext());
		socialNetworkButtonLayout.setPadding(0, 0, 0, 0);
		socialNetworkButtonLayout.setOrientation(VERTICAL);
		socialNetworkButtonLayout.setLayoutParams(socialNetworkButtonParams);	

		LinearLayout emailSMSButtonLayout = new LinearLayout(getContext());
		emailSMSButtonLayout.setPadding(0, 0, 0, 0);
		emailSMSButtonLayout.setOrientation(VERTICAL);
		emailSMSButtonLayout.setLayoutParams(emailSMSButtonParams);	
		
		if(facebookShareCell != null || twitterShareCell != null) {
			
			OnToggleListener onToggleListener = new OnToggleListener() {
				
				@Override
				public void onToggle(boolean on) {
				
					boolean fbOK = facebookShareCell != null && facebookShareCell.isToggled();
					boolean twOK = twitterShareCell != null && twitterShareCell.isToggled();
					
					if(fbOK || twOK) {
						
						if(rememberCell != null) {
							rememberCell.setVisibility(View.VISIBLE);
						}
						
						continueButton.setEnabled(true);
						
					}
					else {
						
						if(rememberCell != null) {
							rememberCell.setVisibility(View.GONE);
						}
						
						// Show continue if we are told to
						continueButton.setEnabled(((displayOptions & ShareUtils.ALWAYS_CONTINUE) != 0));
					}
				}
			};
			
			if(facebookShareCell != null) {
				socialNetworkButtonLayout.addView(facebookShareCell);
				facebookShareCell.setOnToggleListener(onToggleListener);
			}
			if(twitterShareCell != null) {
				socialNetworkButtonLayout.addView(twitterShareCell);
				twitterShareCell.setOnToggleListener(onToggleListener);
			}
			contentLayout.addView(socialNetworkButtonLayout);
		}
		
		if(googlePlusCell != null) {
			LinearLayout googlePlusCellLayout = new LinearLayout(getContext());
			googlePlusCellLayout.setPadding(0, 0, 0, 0);
			googlePlusCellLayout.setLayoutParams(emailSMSButtonParams);				
			googlePlusCellLayout.addView(googlePlusCell);
			contentLayout.addView(googlePlusCellLayout);
		}		
		
		if(emailCell != null || smsCell != null) {
		
			if(emailCell != null) {
				emailSMSButtonLayout.addView(emailCell);
			}
			
			if(smsCell != null) {
				emailSMSButtonLayout.addView(smsCell);
			}
			
			contentLayout.addView(emailSMSButtonLayout);
		}		
		
		if(rememberCell != null) {
			contentLayout.addView(rememberCell);
		}
			
		LayoutParams skipAuthParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		skipAuthParams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
		skipAuthParams.weight = 1.0f;
		
		if(displayUtils != null) {
			skipAuthParams.setMargins(0, displayUtils.getDIP(10), 0, 0);
		}
		
		otherOptions = new TextView(getContext());
		if(localizationService != null) otherOptions.setText(localizationService.getString(I18NConstants.SHARE_MORE_OPTIONS));
		otherOptions.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		if(colors != null) otherOptions.setTextColor(colors.getColor(Colors.ANON_CELL_TITLE));
		otherOptions.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
		otherOptions.setPadding(0, 0, 0, padding);
		otherOptions.setLayoutParams(skipAuthParams);
		
		otherOptions.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final SafeProgressDialog progress = SafeProgressDialog.show(getContext());
				ShareUtils.shareViaOther(getActivity(), entity, new ShareAddListener() {
					
					@Override
					public void onError(SocializeException error) {
						progress.dismiss();
						showErrorToast(getContext(), error);
					}
					
					@Override
					public void onCreate(Share result) {
						progress.dismiss();
					}
				});
			}
		});
		
		contentLayout.addView(otherOptions);			
		
		if(landscape) {
			RelativeLayout.LayoutParams scrollParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			scrollParams.setMargins(padding, padding, padding, 0);
			scrollParams.addRule(RelativeLayout.CENTER_IN_PARENT);		
			
			ScrollView scroller = new ScrollView(getContext());
			scroller.setLayoutParams(scrollParams);
			
			scroller.addView(contentLayout);
			
			container.addView(scroller);
		}
		else {
			container.addView(contentLayout);
		}
		
		addView(header);
		addView(container);
		addView(continueButtonLayout);
	}
	
	public void applyDisplayOptions() {
		
		if(otherOptions != null) {
			if((displayOptions & ShareUtils.MORE_OPTIONS) != 0) {
				otherOptions.setVisibility(View.VISIBLE);
			}
			else {
				otherOptions.setVisibility(View.GONE);
			}
		}
		
		if(facebookShareCell != null) {
			if(((displayOptions & ShareUtils.FACEBOOK) != 0)) {
				facebookShareCell.setVisibility(View.VISIBLE);
			}
			else {
				facebookShareCell.setVisibility(View.GONE);
			}
		}
		
		if(twitterShareCell != null) {
			if(((displayOptions & ShareUtils.TWITTER) != 0)) {
				twitterShareCell.setVisibility(View.VISIBLE);
			}
			else {
				twitterShareCell.setVisibility(View.GONE);
			}
		}
		
		if(emailCell != null) {
			if(((displayOptions & ShareUtils.EMAIL) != 0)) {
				emailCell.setVisibility(View.VISIBLE);
			}
			else {
				emailCell.setVisibility(View.GONE);
			}
		}
		
		if(smsCell != null) {
			if(((displayOptions & ShareUtils.SMS) != 0)) {
				smsCell.setVisibility(View.VISIBLE);
			}
			else {
				smsCell.setVisibility(View.GONE);
			}
		}
		
		if(rememberCell != null) {
			if(((displayOptions & ShareUtils.SHOW_REMEMBER) != 0)) {
				rememberCell.setVisibility(View.VISIBLE);
			}
			else {
				rememberCell.setVisibility(View.GONE);
				rememberCell = null;
			}
		}	
		
		if(googlePlusCell != null) {
			if(((displayOptions & ShareUtils.GOOGLE_PLUS) != 0)) {
				googlePlusCell.setVisibility(View.VISIBLE);
			}
			else {
				googlePlusCell.setVisibility(View.GONE);
			}
		}	
		
		if(buttonLayout != null) {
			if((displayOptions & ShareUtils.MORE_OPTIONS) != 0) {
				buttonLayout.setPadding(padding, 0, padding, padding);
			}
			else {
				buttonLayout.setPadding(padding, padding, padding, padding);
			}
		}
	}
	

	protected void makeShareButtons() {
		LayoutParams cellParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        SocializeService socialize = getSocialize();
        Context context = getContext();

        boolean fbOK = socialize.isSupported(context, AuthProviderType.FACEBOOK) && facebookShareCellFactory != null;
		boolean twOK = socialize.isSupported(context, AuthProviderType.TWITTER) && twitterShareCellFactory != null;
		boolean emailOK = socialize.canShare(context, ShareType.EMAIL) && emailCellFactory != null;
		boolean smsOK = socialize.canShare(context, ShareType.SMS) && smsCellFactory != null;
		boolean rememberOk = rememberCellFactory != null;
		boolean googlePlusOK = config != null && config.isGooglePlusEnabled() && socialize.canShare(context, ShareType.GOOGLE_PLUS) && googlePlusCellFactory != null;
		
		if(fbOK) {
			facebookShareCell = facebookShareCellFactory.getBean();
			
			if(facebookShareCell != null) {
				facebookShareCell.setLayoutParams(cellParams);
				facebookShareCell.setPadding(padding, padding, padding, padding);
				
				if(twOK) {
					twitterShareCell = twitterShareCellFactory.getBean();
					
					if(twitterShareCell != null) {
						twitterShareCell.setPadding(padding, padding, padding, padding);
						twitterShareCell.setLayoutParams(cellParams);
						twitterShareCell.setBackgroundData(twRadii, twStroke, Color.BLACK);
					}
					
					facebookShareCell.setBackgroundData(fbRadii, fbStroke, Color.BLACK);
					
				}
			}
		}
		else if(twOK) {
			twitterShareCell = twitterShareCellFactory.getBean();
			
			if(twitterShareCell != null) {
				twitterShareCell.setLayoutParams(cellParams);
				twitterShareCell.setPadding(padding, padding, padding, padding);
			}
		}
		
		if(googlePlusOK) {
			googlePlusCell = googlePlusCellFactory.getBean();
			
			if(googlePlusCell != null) {
				googlePlusCell.setLayoutParams(cellParams);
			}
		}
		
		if(emailOK) {
			emailCell = emailCellFactory.getBean();
			
			if(emailCell != null) {
				emailCell.setLayoutParams(cellParams);
				emailCell.setPadding(padding, padding, padding, padding);
				
				if(smsOK) {
					smsCell = smsCellFactory.getBean();
					smsCell.setLayoutParams(cellParams);
					smsCell.setPadding(padding, padding, padding, padding);

					emailCell.setBackgroundData(fbRadii, fbStroke, Color.BLACK);
					smsCell.setBackgroundData(twRadii, twStroke, Color.BLACK);
				}
			}
		}
		else if(smsOK) {
			smsCell = smsCellFactory.getBean();
			
			if(smsCell != null) {
				smsCell.setLayoutParams(cellParams);
				smsCell.setPadding(padding, padding, padding, padding);
			}
		}		
		
		if(rememberOk) {
			rememberCell = rememberCellFactory.getBean();
			
			if(rememberCell != null) {
				LayoutParams rememberCellParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				rememberCellParams.setMargins(0, padding, 0, 0);
				rememberCell.setLayoutParams(rememberCellParams);
			}
		}
		
		if(facebookShareCell != null) {
			facebookShareCell.setAuthListener(getAuthClickListener(facebookShareCell, SocialNetwork.FACEBOOK));
		}
		
		if(twitterShareCell != null) {
			twitterShareCell.setAuthListener(getAuthClickListener(twitterShareCell, SocialNetwork.TWITTER));
		}
		
		if(emailCell != null) {
			emailCell.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					
					if(shareDialogListener != null) {
						shareDialogListener.onSimpleShare(ShareType.EMAIL);
					}
					
					final ProgressDialog progress = SafeProgressDialog.show(v.getContext());
					
					if(dialog != null) {
						dialog.dismiss();
					}
					
					ShareUtils.shareViaEmail(getActivity(), entity, new SimpleShareListener() {
						
						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							showErrorToast(v.getContext(), error);
						}
						
						@Override
						public void onCreate(Share entity) {
							progress.dismiss();
						}

						@Override
						public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
							return socialNetworkListener != null && socialNetworkListener.onBeforePost(parent, socialNetwork, postData);
						}

						@Override
						public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
							if(socialNetworkListener != null) {
								socialNetworkListener.onAfterPost(parent, socialNetwork, responseObject);
							}
						}
					});
				}
			});
		}
		
		if(smsCell != null) {
			smsCell.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					
					if(shareDialogListener != null) {
						shareDialogListener.onSimpleShare(ShareType.SMS);
					}
					
					if(dialog != null) {
						dialog.dismiss();
					}
					
					final ProgressDialog progress = SafeProgressDialog.show(v.getContext());
					ShareUtils.shareViaSMS(getActivity(), entity, new SimpleShareListener() {
						
						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							showErrorToast(v.getContext(), error);
						}
						
						@Override
						public void onCreate(Share entity) {
							progress.dismiss();
						}

						@Override
						public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
							return socialNetworkListener != null && socialNetworkListener.onBeforePost(parent, socialNetwork, postData);
						}

						@Override
						public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
							if(socialNetworkListener != null) {
								socialNetworkListener.onAfterPost(parent, socialNetwork, responseObject);
							}
						}
					});
				}
			});
		}	
		
		if(googlePlusCell != null) {
			googlePlusCell.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					
					if(shareDialogListener != null) {
						shareDialogListener.onSimpleShare(ShareType.GOOGLE_PLUS);
					}
					
					if(dialog != null) {
						dialog.dismiss();
					}
					
					final ProgressDialog progress = SafeProgressDialog.show(v.getContext());
					
					ShareUtils.shareViaGooglePlus(getActivity(), entity, new SimpleShareListener() {
						
						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							showErrorToast(v.getContext(), error);
						}
						
						@Override
						public void onCreate(Share entity) {
							progress.dismiss();
						}

						@Override
						public boolean onBeforePost(Activity parent, SocialNetwork socialNetwork, PostData postData) {
							return socialNetworkListener != null && socialNetworkListener.onBeforePost(parent, socialNetwork, postData);
						}

						@Override
						public void onAfterPost(Activity parent, SocialNetwork socialNetwork, JSONObject responseObject) {
							if(socialNetworkListener != null) {
								socialNetworkListener.onAfterPost(parent, socialNetwork, responseObject);
							}
						}
					});
				}
			});
		}			
	}	

	
	protected View makeButtons() {
		
		buttonLayout = new LinearLayout(getContext());
		
		if(continueButton != null) {

			LayoutParams buttonParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			
			buttonLayout.setPadding(padding, 0, padding, padding);
			buttonLayout.setOrientation(HORIZONTAL);
			buttonLayout.setLayoutParams(buttonParams);
			buttonLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);	
			
			continueButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// Disable to prevent multiple clicks
					continueButton.setEnabled(false);
					
					SocialNetwork[] networks = null;
					
					// Get the number of networks enabled
					if(facebookShareCell != null && facebookShareCell.isToggled()) {
						if(twitterShareCell != null && twitterShareCell.isToggled()) {
							networks = new SocialNetwork[]{SocialNetwork.FACEBOOK, SocialNetwork.TWITTER};
						}
						else {
							networks = new SocialNetwork[]{SocialNetwork.FACEBOOK};
						}
					}
					else if(twitterShareCell != null && twitterShareCell.isToggled()) {
						networks = new SocialNetwork[]{SocialNetwork.TWITTER};
					}
					
					boolean remember = false;
					
					if(rememberCell != null) {
						remember = rememberCell.isToggled();
					}

					shareDialogListener.onContinue(dialog, remember, networks);
				}
			});

			if(cancelButton != null) {
				cancelButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						shareDialogListener.onCancel(dialog);
					}
				});

				buttonLayout.addView(cancelButton);
			}

			buttonLayout.addView(continueButton);
		}
		
		return buttonLayout;
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
			authBadge.setImageDrawable(drawables.getDrawable("share_badge.png"));
			authBadge.setLayoutParams(badgeParams);
			authBadge.setPadding(0, 0, 0, padding);
			badgeLayout.addView(authBadge);
		}
		
		return badgeLayout;
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
			header.setText(localizationService.getString(I18NConstants.SHARE_HEADER));
		}

		header.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		header.setTextColor(Color.WHITE);
		header.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		header.setLayoutParams(headerParams);
		
		return header;
	}
	
	public void updateNetworkButtonState() {
		if(facebookShareCell != null) {
			facebookShareCell.setToggled(getSocialize().isAuthenticatedForRead(AuthProviderType.FACEBOOK));
		}
		if(twitterShareCell != null) {
			twitterShareCell.setToggled(getSocialize().isAuthenticatedForRead(AuthProviderType.TWITTER));
		}
	}
	
	public void checkSupportedNetworkButtonState() {
		if(facebookShareCell != null && facebookShareCell.isToggled()) {
			facebookShareCell.setToggled(getSocialize().isAuthenticatedForRead(AuthProviderType.FACEBOOK));
		}
		if(twitterShareCell != null && twitterShareCell.isToggled()) {
			twitterShareCell.setToggled(getSocialize().isAuthenticatedForRead(AuthProviderType.TWITTER));
		}
	}	

	public void setFacebookShareCellFactory(IBeanFactory<FacebookShareCell> facebookSignInCellFactory) {
		this.facebookShareCellFactory = facebookSignInCellFactory;
	}

	public void setTwitterShareCellFactory(IBeanFactory<TwitterShareCell> twitterSignInCellFactory) {
		this.twitterShareCellFactory = twitterSignInCellFactory;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public FacebookShareCell getFacebookShareCell() {
		return facebookShareCell;
	}

	public TwitterShareCell getTwitterShareCell() {
		return twitterShareCell;
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

	public void setShareDialogListener(ShareDialogListener shareDialogListener) {
		this.shareDialogListener = shareDialogListener;
	}

	public void setEmailCellFactory(IBeanFactory<EmailCell> emailCellFactory) {
		this.emailCellFactory = emailCellFactory;
	}
	
	public void setSmsCellFactory(IBeanFactory<SMSCell> smsCellFactory) {
		this.smsCellFactory = smsCellFactory;
	}
	
	public void setRememberCellFactory(IBeanFactory<RememberCell> rememberCellFactory) {
		this.rememberCellFactory = rememberCellFactory;
	}
	
	public void setGooglePlusCellFactory(IBeanFactory<GooglePlusCell> googlePlusCellFactory) {
		this.googlePlusCellFactory = googlePlusCellFactory;
	}

	public void setConfig(SocializeConfig config) {
		this.config = config;
	}

	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public void setSocialNetworkListener(SocialNetworkListener socialNetworkListener) {
		this.socialNetworkListener = socialNetworkListener;
	}
	
	public void setDisplayOptions(int displayOptions) {
		this.displayOptions = displayOptions;
	}
	
	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}
	
	public SocializeButton getContinueButton() {
		return continueButton;
	}
	
	public SocializeButton getCancelButton() {
		return cancelButton;
	}

	protected SocializeAuthListener getAuthClickListener(final ClickableSectionCell cell, final SocialNetwork network) {
		return new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				SocializeLogger.e( error.getMessage(),  error);
				
				showErrorToast(getContext(), error);
				
				if(socialNetworkListener != null) {
					socialNetworkListener.onNetworkError(getActivity(), network, error);
				}
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				cell.setToggled(true);
				checkSupportedNetworkButtonState();
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				SocializeLogger.e(error.getMessage(), error);
				
				showError(getContext(), error);
				
				if(socialNetworkListener != null) {
					socialNetworkListener.onNetworkError(getActivity(), network, error);
				}
			}

			@Override
			public void onCancel() {}
		};
	}	
}
