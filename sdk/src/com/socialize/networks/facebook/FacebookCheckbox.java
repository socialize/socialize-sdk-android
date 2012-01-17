package com.socialize.networks.facebook;

import android.content.Context;
import android.view.View;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.ui.view.CustomCheckbox;

public class FacebookCheckbox extends CustomCheckbox {

	private FacebookAuthClickListener facebookAuthClickListener;
	private FacebookSignOutClickListener facebookSignOutClickListener;
	private IBeanFactory<FacebookSignOutClickListener> facebookSignOutClickListenerFactory;
	
	private OnClickListener localListener = null;
	
	
	private SocializeAuthListener localAuthListener = null;
	private FacebookSignOutListener localSignOutListener = null;
	
	public FacebookCheckbox(Context context) {
		super(context);
	}
	
	public void init() {
		super.init();
		facebookSignOutClickListener = facebookSignOutClickListenerFactory.getBean();
		facebookSignOutClickListener.setListener(new FacebookSignOutListener() {
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
		
		facebookAuthClickListener.setListener(new SocializeAuthListener() {
			
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
					facebookAuthClickListener.onClick(v);
				}
				else {
					facebookSignOutClickListener.onClick(v);
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

	public void setSignOutListener(FacebookSignOutListener localSignOutListener) {
		this.localSignOutListener = localSignOutListener;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.localListener = l;
	}

	public void setFacebookAuthClickListener(FacebookAuthClickListener facebookAuthClickListener) {
		this.facebookAuthClickListener = facebookAuthClickListener;
	}

	public void setFacebookSignOutClickListenerFactory(IBeanFactory<FacebookSignOutClickListener> facebookSignOutClickListenerFactory) {
		this.facebookSignOutClickListenerFactory = facebookSignOutClickListenerFactory;
	}
}
