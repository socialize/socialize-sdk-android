package com.socialize.ui.comment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import com.socialize.Socialize;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.entity.Entity;
import com.socialize.i18n.LocalizationService;
import com.socialize.listener.ListenerHolder;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.dialog.SafeProgressDialog;
import com.socialize.ui.slider.ActionBarSliderView;
import com.socialize.ui.view.EntityView;

public class CommentView extends EntityView {
	
	private Dialog progress;
	private CommentListView commentListView;
	private boolean headerDisplayed = true;
	private Entity entity;
	
	public static final String COMMENT_LISTENER = "socialize.comment.listener";
	
	public CommentView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CommentView(Context context) {
		super(context);
	}

	@Override
	protected View getView(Bundle bundle, Object...entityKey) {
		
		if (entityKey != null || entity != null) {
			
			if(commentListView == null) {
				
				if(entity == null) {
					entity = (Entity) entityKey[0];
				}
				
				commentListView = container.getBean("commentList");
				commentListView.setEntity(entity);
				commentListView.setHeaderDisplayed(headerDisplayed);
				ListenerHolder holder = container.getBean("listenerHolder");
				if(holder != null) {
					OnCommentViewActionListener onCommentViewActionListener = holder.pop(COMMENT_LISTENER);
					commentListView.setOnCommentViewActionListener(onCommentViewActionListener);
				}	
			}
			
			return commentListView;
		}
		else {
			SocializeLogger.e("No entity url specified for comment view");
			return null;
		}
	}
	
	@Override
	protected void onBeforeSocializeInit() {
		if(!Socialize.getSocialize().isInitialized(getContext()) || !Socialize.getSocialize().isAuthenticated()) {
			try {
				// Cannot use localization here.
				progress = SafeProgressDialog.show(getContext(), "Loading Comments", "Please wait...");
			}
			catch (Exception ignore) {}
		}
	}

	@Override
	public void onAfterAuthenticate(IOCContainer container) {
		if(progress != null) {
			try {
				progress.dismiss();
			}
			catch (Exception ignore) {}
		}
	}
	
	@Override
	protected String[] getBundleKeys() {
		return new String[]{Socialize.ENTITY_OBJECT};
	}
	
	/**
	 * Called when the current logged in user updates their profile.
	 */
	public void onProfileUpdate() {
		if(commentListView != null) {
			commentListView.onProfileUpdate();
		}
	}
	
	public void reload() {
		if(commentListView != null) {
			commentListView.reload();
		}
	}
	
	@Override
	public View getLoadingView() {
		return null;
	}
	
	public ActionBarSliderView getCommentEntryViewSlider() {
		return (commentListView == null) ? null : commentListView.getCommentEntryViewSlider();
	}
	
	public CommentEditField getCommentEntryField() {
		return (commentListView == null) ? null : commentListView.getCommentEntryField();
	}
	
	public boolean isHeaderDisplayed() {
		return headerDisplayed;
	}
	
	public void setHeaderDisplayed(boolean headerDisplayed) {
		this.headerDisplayed = headerDisplayed;
		
		if(commentListView != null) {
			commentListView.setHeaderDisplayed(headerDisplayed);
		}
	}
	
	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
	public Entity getEntity() {
		return entity;
	}
	
	public void setLocalizationService(LocalizationService localizationService) {
		this.localizationService = localizationService;
	}

	@Override
	protected void createOptionsMenuItem(Activity source, Menu menu) {
		super.createOptionsMenuItem(source, menu);
		
		MenuItem add2 = menu.add("Refresh");
		
		if(drawables != null) {
			add2.setIcon(drawables.getDrawable("ic_menu_refresh.png"));
		}
		
		add2.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				reload();
				return true;
			}
		});
		
	}
}
