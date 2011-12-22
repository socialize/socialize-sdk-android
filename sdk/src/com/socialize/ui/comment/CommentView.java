package com.socialize.ui.comment;

import org.json.JSONException;
import org.json.JSONObject;

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

import com.socialize.Socialize;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.entity.Entity;
import com.socialize.entity.EntityFactory;
import com.socialize.listener.ListenerHolder;
import com.socialize.log.SocializeLogger;
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
				try {
					EntityFactory entityFactory = container.getBean("entityFactory");
					Entity entity = entityFactory.fromJSON(new JSONObject(entityKey[0].toString()));
					
					commentListView = container.getBean("commentList", entity);
					
					ListenerHolder holder  = container.getBean("listenerHolder");
					
					if(holder != null) {
						OnCommentViewActionListener onCommentViewActionListener = holder.get(COMMENT_LISTENER);
						commentListView.setOnCommentViewActionListener(onCommentViewActionListener);
					}	
				} 
				catch (JSONException e) {
					Log.e(SocializeLogger.LOG_TAG, "Invalid entity object", e);
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
	
	public boolean onCreateOptionsMenu(final Activity source, Menu menu) {
		createOptionsMenuItem(source, menu);

		MenuItem add2 = menu.add("Refresh");
		
		add2.setIcon(Socialize.getSocializeUI().getDrawable("ic_menu_refresh.png", DisplayMetrics.DENSITY_DEFAULT, true));
		
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
