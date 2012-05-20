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

import android.app.ProgressDialog;
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
import com.socialize.api.action.ShareType;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;
import com.socialize.networks.facebook.FacebookShareCell;
import com.socialize.networks.twitter.TwitterShareCell;
import com.socialize.ui.dialog.DialogPanelView;
import com.socialize.ui.dialog.SafeProgressDialog;
import com.socialize.ui.util.Colors;
import com.socialize.ui.view.ClickableSectionCell;
import com.socialize.ui.view.ClickableSectionCell.OnToggleListener;
import com.socialize.ui.view.SocializeButton;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 */
public class SharePanelView extends DialogPanelView {

	private ShareDialogListener shareDialogListener;
	private SocialNetworkListener socialNetworkListener;
	private Colors colors;
	
	private SocializeButton continueButton;
	private SocializeButton cancelButton;
	private TextView otherOptions;
	
	private int displayOptions;
	
	private Entity entity;
	
	public SharePanelView(Context context, Entity entity, int displayOptions) {
		this(context, entity, null, null, displayOptions);
	}
	
	public SharePanelView(Context context, Entity entity, ShareDialogListener listener, int displayOptions) {
		this(context, entity, null, listener, displayOptions);
	}
	
	public SharePanelView(Context context, Entity entity, SocialNetworkListener socialNetworkListener, ShareDialogListener listener, int displayOptions) {
		super(context);
		this.shareDialogListener = listener;
		this.socialNetworkListener = socialNetworkListener;
		this.displayOptions = displayOptions;
		this.entity = entity;
	}
	
	public SharePanelView(Context context) {
		this(context, null, null, null, ShareUtils.COMMENT_AND_LIKE);
	}
	
	private IBeanFactory<FacebookShareCell> facebookShareCellFactory;
	private IBeanFactory<TwitterShareCell> twitterShareCellFactory;
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
	
	float radii = 6;
	int padding = 8;
	int headerHeight = 45;
	float headerRadius = 3;
	int landscapeButtonWidth = 190;
	
	private float[] fbRadii = new float[]{radii, radii, radii, radii, 0.0f, 0.0f, 0.0f, 0.0f};
	private int[] fbStroke = new int[]{1, 1, 0, 1};
	
	private float[] twRadii = new float[]{0.0f, 0.0f, 0.0f, 0.0f, radii, radii, radii, radii};
	private int[] twStroke = new int[]{1, 1, 1, 1};
	
	public void init() {
		
		boolean landscape = false;
		boolean lowRes = false;
		
		if(displayUtils != null) {
			padding = displayUtils.getDIP(12);
			headerRadius = displayUtils.getDIP(3);
			headerHeight = displayUtils.getDIP(45);
			radii = displayUtils.getDIP(radii);
			landscape = displayUtils.isLandscape();
			lowRes = displayUtils.isLowRes();
			landscapeButtonWidth = displayUtils.getDIP(landscapeButtonWidth);
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
		
		View continueButtonLayout = makeContinueButton();
		View header = makeHeaderView(headerHeight, headerRadius);
		
		RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		contentParams.setMargins(padding, padding, padding, 0);
		contentParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		
		LinearLayout contentLayout = new LinearLayout(getContext());
		contentLayout.setPadding(padding, padding, padding, 0);
		contentLayout.setLayoutParams(contentParams);
		contentLayout.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
		
		LayoutParams socialNetworkButtonParams = null;
		LayoutParams emailSMSButtonParams = null;
		
		if(landscape) {
			socialNetworkButtonParams = new LayoutParams(landscapeButtonWidth, LayoutParams.WRAP_CONTENT);
			emailSMSButtonParams = new LayoutParams(landscapeButtonWidth, LayoutParams.WRAP_CONTENT);
			contentLayout.setOrientation(HORIZONTAL);
			
			socialNetworkButtonParams.setMargins(0, 0, padding/2, 0);
			emailSMSButtonParams.setMargins(padding/2, 0, 0, 0);
		}
		else {
			socialNetworkButtonParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			emailSMSButtonParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			emailSMSButtonParams.setMargins(0, padding, 0, 0);
			contentLayout.setOrientation(VERTICAL);
		}
		
		LinearLayout socialNetworkButtonLayout = new LinearLayout(getContext());
		socialNetworkButtonLayout.setPadding(0, 0, 0, 0);
		socialNetworkButtonLayout.setOrientation(VERTICAL);
		socialNetworkButtonLayout.setLayoutParams(socialNetworkButtonParams);	

		LinearLayout emailSMSButtonLayout = new LinearLayout(getContext());
		emailSMSButtonLayout.setPadding(0, 0, 0, 0);
		emailSMSButtonLayout.setOrientation(VERTICAL);
		emailSMSButtonLayout.setLayoutParams(emailSMSButtonParams);	
		
		if(!landscape && !lowRes) {
			View shareBadge = makeShareBadge();
			contentLayout.addView(shareBadge);
		}
		
		if(facebookShareCell != null || twitterShareCell != null) {
			
			OnToggleListener onToggleListener = new OnToggleListener() {
				
				@Override
				public void onToggle(boolean on) {
				
					boolean fbOK = facebookShareCell == null || facebookShareCell.isToggled();
					boolean twOK = twitterShareCell == null || twitterShareCell.isToggled();
					if(fbOK || twOK) {
						if(rememberCell != null) {
							rememberCell.setVisibility(View.VISIBLE);
						}
						
						if((displayOptions & ShareUtils.ALLOW_NONE) == 0) {
							continueButton.setEnabled(true);
						}
						
					}
					else {
						
						if(rememberCell != null) {
							rememberCell.setVisibility(View.GONE);
						}
						
						if((displayOptions & ShareUtils.ALLOW_NONE) == 0) {
							continueButton.setEnabled(false);
						}
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
		
		if((displayOptions & ShareUtils.MORE_OPTIONS) != 0) {
			otherOptions = new TextView(getContext());
			otherOptions.setText("More options...");
			otherOptions.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
			
			if(colors != null) {
				otherOptions.setTextColor(colors.getColor(Colors.ANON_CELL_TITLE));
			}

			otherOptions.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
			otherOptions.setPadding(0, 0, 0, padding);
			
			LayoutParams skipAuthParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			skipAuthParams.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
			skipAuthParams.weight = 1.0f;
			
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
		}
		
		container.addView(contentLayout);
		
		addView(header);
		addView(container);
		addView(continueButtonLayout);
		
		updateNetworkButtonState();
	}
	
	protected View makeContinueButton() {
		
		LinearLayout buttonLayout = new LinearLayout(getContext());
		
		if(continueButton != null) {

			LayoutParams buttonParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			
			buttonLayout.setPadding(padding, 0, padding, padding);
			buttonLayout.setOrientation(HORIZONTAL);
			buttonLayout.setLayoutParams(buttonParams);
			buttonLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);		
			
			continueButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
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
			
			cancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					shareDialogListener.onCancel(dialog);
				}
			});
			
			buttonLayout.addView(cancelButton);
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
	
	protected void makeShareButtons() {
		LayoutParams cellParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		boolean fbOK = getSocialize().isSupported(AuthProviderType.FACEBOOK) && ((displayOptions & ShareUtils.FACEBOOK) != 0) && facebookShareCellFactory != null;
		boolean twOK = getSocialize().isSupported(AuthProviderType.TWITTER) && ((displayOptions & ShareUtils.TWITTER) != 0) && twitterShareCellFactory != null;
		boolean emailOK = (entity != null && (displayOptions & ShareUtils.EMAIL) != 0) && getSocialize().canShare(getContext(), ShareType.EMAIL) && emailCellFactory != null;
		boolean smsOK = (entity != null && (displayOptions & ShareUtils.SMS) != 0) && getSocialize().canShare(getContext(), ShareType.SMS) && smsCellFactory != null;
		boolean rememberOk = ((displayOptions & ShareUtils.SHOW_REMEMBER) != 0) && rememberCellFactory != null;
		
		if(fbOK) {
			facebookShareCell = facebookShareCellFactory.getBean();
			
			if(facebookShareCell != null) {
				facebookShareCell.setLayoutParams(cellParams);
				facebookShareCell.setPadding(padding, padding, padding, padding);
				
				if(twOK) {
					twitterShareCell = twitterShareCellFactory.getBean();
					twitterShareCell.setPadding(padding, padding, padding, padding);
					twitterShareCell.setLayoutParams(cellParams);
					
					facebookShareCell.setBackgroundData(fbRadii, fbStroke, Color.BLACK);
					twitterShareCell.setBackgroundData(twRadii, twStroke, Color.BLACK);
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
			LayoutParams rememberCellParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			rememberCellParams.setMargins(0, padding, 0, 0);
			rememberCell.setLayoutParams(rememberCellParams);
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
					final ProgressDialog progress = SafeProgressDialog.show(v.getContext());
					ShareUtils.shareViaEmail(getActivity(), entity, new ShareAddListener() {
						
						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							showError(v.getContext(), error);
						}
						
						@Override
						public void onCreate(Share entity) {
							progress.dismiss();
						}
					});
				}
			});
		}
		
		if(smsCell != null) {
			smsCell.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					final ProgressDialog progress = SafeProgressDialog.show(v.getContext());
					ShareUtils.shareViaSMS(getActivity(), entity, new ShareAddListener() {
						
						@Override
						public void onError(SocializeException error) {
							progress.dismiss();
							showError(v.getContext(), error);
						}
						
						@Override
						public void onCreate(Share entity) {
							progress.dismiss();
						}
					});
				}
			});
		}		
	}
	
	protected View makeHeaderView(int headerHeight, float headerRadius) {
		LayoutParams headerParams = new LayoutParams(LayoutParams.FILL_PARENT, headerHeight);
		
		TextView header = new TextView(getContext());
		
		if(colors != null) {
			GradientDrawable headerBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{colors.getColor(Colors.AUTH_PANEL_BOTTOM), colors.getColor(Colors.AUTH_PANEL_TOP)});
			headerBG.setCornerRadii(new float[]{headerRadius, headerRadius, headerRadius, headerRadius, 0.0f, 0.0f, 0.0f, 0.0f});
			header.setBackgroundDrawable(headerBG);
		}

		header.setText("Share To...");
		header.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		header.setTextColor(Color.WHITE);
		header.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		header.setLayoutParams(headerParams);
		
		return header;
	}
	
	public void updateNetworkButtonState() {
		if(facebookShareCell != null) {
			facebookShareCell.setToggled(getSocialize().isAuthenticated(AuthProviderType.FACEBOOK));
		}
		if(twitterShareCell != null) {
			twitterShareCell.setToggled(getSocialize().isAuthenticated(AuthProviderType.TWITTER));
		}
	}
	
	public void checkSupportedNetworkButtonState() {
		if(facebookShareCell != null && facebookShareCell.isToggled()) {
			facebookShareCell.setToggled(getSocialize().isAuthenticated(AuthProviderType.FACEBOOK));
		}
		if(twitterShareCell != null && twitterShareCell.isToggled()) {
			twitterShareCell.setToggled(getSocialize().isAuthenticated(AuthProviderType.TWITTER));
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
	
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	protected SocializeAuthListener getAuthClickListener(final ClickableSectionCell cell, final SocialNetwork network) {
		return new SocializeAuthListener() {
			
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				
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
				error.printStackTrace();
				
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
