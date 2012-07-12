package com.socialize.networks;

import android.content.Context;
import android.view.View;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.view.CustomCheckbox;

public class SocialNetworkCheckbox extends CustomCheckbox {

	private SocialNetworkAuthClickListener socialNetworkAuthClickListener;
	private SocialNetworkSignOutClickListener socialNetworkSignOutClickListener;
	private IBeanFactory<SocialNetworkSignOutClickListener> socialNetworkSignOutClickListenerFactory;
	private OnClickListener localListener = null;
	private SocializeAuthListener localAuthListener = null;
	private SocialNetworkSignOutListener localSignOutListener = null;
	
	public SocialNetworkCheckbox(Context context) {
		super(context);
	}
	
	public void init() {
		super.init();
		socialNetworkSignOutClickListener = socialNetworkSignOutClickListenerFactory.getBean();
		socialNetworkSignOutClickListener.setListener(new SocialNetworkSignOutListener() {
			@Override
			public void onSignOut() {
				setChecked(false);
				if(localSignOutListener != null) {
					localSignOutListener.onSignOut();
				}
			}

			@Override
			public void onCancel() {
				setChecked(true);
				if(localSignOutListener != null) {
					localSignOutListener.onCancel();
				}
			}
		});
		
		socialNetworkAuthClickListener.setListener(new SocializeAuthListener() {
			@Override
			public void onError(SocializeException error) {
				setChecked(false);
				if(localAuthListener != null) {
					localAuthListener.onError(error);
				}
			}
			
			@Override
			public void onCancel() {
				setChecked(false);
				
				if(localAuthListener != null) {
					localAuthListener.onCancel();
				}
			}
			
			@Override
			public void onAuthSuccess(SocializeSession session) {
				setChecked(true);
				
				if(localAuthListener != null) {
					localAuthListener.onAuthSuccess(session);
				}
			}
			
			@Override
			public void onAuthFail(SocializeException error) {
				setChecked(false);
				
				if(localAuthListener != null) {
					localAuthListener.onAuthFail(error);
				}
			}
		});
		
		// Must be super
		super.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isChecked()) {
					socialNetworkAuthClickListener.onClick(v);
				}
				else {
					socialNetworkSignOutClickListener.onClick(v);
				}
				
				if(localListener != null) {
					localListener.onClick(v);
				}
			}
		});
	}
	
	public void setSignInListener(SocializeAuthListener listener) {
		this.localAuthListener = listener;
	}

	public void setSignOutListener(SocialNetworkSignOutListener localSignOutListener) {
		this.localSignOutListener = localSignOutListener;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.localListener = l;
	}

	public void setSocialNetworkAuthClickListener(SocialNetworkAuthClickListener socialNetworkAuthClickListener) {
		this.socialNetworkAuthClickListener = socialNetworkAuthClickListener;
	}

	public void setSocialNetworkSignOutClickListenerFactory(IBeanFactory<SocialNetworkSignOutClickListener> socialNetworkSignOutClickListenerFactory) {
		this.socialNetworkSignOutClickListenerFactory = socialNetworkSignOutClickListenerFactory;
	}
}
