package com.socialize.ui.comment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;

import com.socialize.android.ioc.IOCContainer;
import com.socialize.listener.ListenerHolder;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.view.EntityView;

public class CommentView extends EntityView {
	
	private Dialog progress;
	private CommentListView commentListView;
	
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
			
			// TODO: always create?
			if(commentListView == null) {
				commentListView = container.getBean("commentList", entityKey[0]);
				
				ListenerHolder holder  = container.getBean("listenerHolder");
				
				if(holder != null) {
					OnCommentViewActionListener onCommentViewActionListener = holder.get(COMMENT_LISTENER);
					commentListView.setOnCommentViewActionListener(onCommentViewActionListener);
				}				
			}
			
			return commentListView;
		}
		else {
			Log.e("Socialize", "No entity url specified for comment view");
			return null;
		}
	}
	
	@Override
	protected void onBeforeSocializeInit() {
		try {
			progress = ProgressDialog.show(getContext(), "Loading Socialize", "Please wait...");
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
	protected String[] getEntityKeys() {
		return new String[]{SocializeUI.ENTITY_KEY};
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
	
	public boolean onCreateOptionsMenu(final Activity source, Menu menu) {
		createOptionsMenuItem(source, menu);

		MenuItem add2 = menu.add("Refresh");
		
		add2.setIcon(SocializeUI.getInstance().getDrawable("ic_menu_refresh.png", DisplayMetrics.DENSITY_DEFAULT, true));
		
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
