package com.socialize.ui.comment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;

import com.socialize.Socialize;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.entity.Entity;
import com.socialize.listener.ListenerHolder;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.dialog.SafeProgressDialog;
import com.socialize.ui.slider.ActionBarSliderView;
import com.socialize.ui.view.EntityView;
import com.socialize.util.Drawables;

public class CommentView extends EntityView {
	
	private Dialog progress;
	private CommentListView commentListView;
	private Drawables drawables;
	
	public static final String COMMENT_LISTENER = "socialize.comment.listener";
	
	public CommentView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CommentView(Context context) {
		super(context);
	}

	@Override
	protected View getView(Bundle bundle, Object...entityKey) {
		if (entityKey != null) {
			
			if(drawables == null) {
				drawables = container.getBean("drawables");
			}
			
			// TODO: always create?
			if(commentListView == null) {
				Entity entity = (Entity) entityKey[0];
				commentListView = container.getBean("commentList", entity);
				ListenerHolder holder = container.getBean("listenerHolder");
				if(holder != null) {
					OnCommentViewActionListener onCommentViewActionListener = holder.pop(COMMENT_LISTENER);
					commentListView.setOnCommentViewActionListener(onCommentViewActionListener);
				}	
			}
			
			return commentListView;
		}
		else {
			Log.e(SocializeLogger.LOG_TAG, "No entity url specified for comment view");
			return null;
		}
	}
	
	@Override
	protected void onBeforeSocializeInit() {
		try {
			progress = SafeProgressDialog.show(getContext(), "Loading Socialize", "Please wait...");
		}
		catch (Exception ignore) {}
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
	
	public boolean onCreateOptionsMenu(final Activity source, Menu menu) {
		createOptionsMenuItem(source, menu);

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
		
		return true;
	}	
}
