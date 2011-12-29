package com.socialize.sample.mocks;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.socialize.SocializeUI;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.User;
import com.socialize.ui.actionbar.ActionBarListener;
import com.socialize.ui.actionbar.ActionBarOptions;
import com.socialize.ui.comment.OnCommentViewActionListener;

public class MockSocializeUI implements SocializeUI {

	@Override
	public View showActionBar(Activity parent, View original, Entity entity) {
		return null;
	}

	@Override
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options) {
		return null;
	}

	@Override
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarListener listener) {
		return null;
	}

	@Override
	public View showActionBar(Activity parent, View original, Entity entity, ActionBarOptions options, ActionBarListener listener) {
		return null;
	}

	@Override
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options, ActionBarListener listener) {
		return null;
	}

	@Override
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarOptions options) {
		return null;
	}

	@Override
	public View showActionBar(Activity parent, int resId, Entity entity, ActionBarListener listener) {
		return null;
	}

	@Override
	public View showActionBar(Activity parent, int resId, Entity entity) {
		return null;
	}

	@Override
	public void showUserProfileViewForResult(Activity context, Long userId, int requestCode) {}

	@Override
	public void showUserProfileView(Activity context, Long userId) {}

	@Override
	public void showCommentView(Activity context, Entity entity, OnCommentViewActionListener listener) {}

	@Override
	public void showCommentView(Activity context, Entity entity) {}

	@Override
	public void showActionDetailViewForResult(Activity context, User user, SocializeAction action, int requestCode) {}

	@Override
	public Drawable getDrawable(String name, int density, boolean eternal) {
		return null;
	}

	@Override
	public Drawable getDrawable(String name, boolean eternal) {
		return null;
	}

}
