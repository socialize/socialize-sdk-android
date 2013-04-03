package com.socialize.test;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import com.socialize.SocializeService;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.ActionOptions;
import com.socialize.api.action.ShareType;
import com.socialize.api.action.ShareableActionOptions;
import com.socialize.api.action.share.ShareOptions;
import com.socialize.api.action.share.SocialNetworkShareListener;
import com.socialize.api.action.share.SocializeShareUtils;
import com.socialize.entity.Entity;
import com.socialize.entity.Share;
import com.socialize.entity.SocializeAction;
import com.socialize.listener.share.ShareAddListener;
import com.socialize.networks.SocialNetwork;
import com.socialize.networks.SocialNetworkListener;

public class PublicShareUtils extends SocializeShareUtils {
	@Override
	public void doShare(Activity context, Entity entity, ShareType shareType, ShareAddListener shareAddListener) {
		super.doShare(context, entity, shareType, shareAddListener);
	}

	@Override
	public void doShare(Activity context, Entity entity, SocialNetworkShareListener socialNetworkListener, ShareOptions shareOptions, SocialNetwork... networks) {
		super.doShare(context, entity, socialNetworkListener, shareOptions, networks);
	}

	@Override
	public void doShare(Dialog dialog, Activity context, Entity entity, SocialNetworkShareListener socialNetworkListener, SocialNetwork... networks) {
		super.doShare(dialog, context, entity, socialNetworkListener, networks);
	}

	@Override
	public void doShare(Dialog dialog, Activity context, Entity entity, SocialNetworkShareListener socialNetworkListener, ShareOptions shareOptions, SocialNetwork... networks) {
		super.doShare(dialog, context, entity, socialNetworkListener, shareOptions, networks);
	}

	@Override
	public void handleNonNetworkShare(Activity activity, SocializeSession session, ShareType shareType, Share share, String shareText, Location location, ShareAddListener shareAddListener) {
		super.handleNonNetworkShare(activity, session, shareType, share, shareText, location, shareAddListener);
	}

	@Override
	public void populateActionOptions(Context context, ActionOptions options) {
		super.populateActionOptions(context, options);
	}

	@Override
	public boolean isDisplayShareDialog(Context context, ShareableActionOptions options) {
		return super.isDisplayShareDialog(context, options);
	}

	@Override
	public boolean isDisplayAuthDialog(Context context, SocializeSession session, ActionOptions options, SocialNetwork... networks) {
		return super.isDisplayAuthDialog(context, session, options, networks);
	}

	@Override
	public SocializeService getSocialize() {
		return super.getSocialize();
	}

	@Override
	public void doActionShare(Activity context, SocializeAction action, String text, SocialNetworkListener listener, SocialNetwork... networks) {
		super.doActionShare(context, action, text, listener, networks);
	}
}
