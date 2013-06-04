package com.socialize.ui.comment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.socialize.CommentUtils;
import com.socialize.Socialize;
import com.socialize.android.ioc.IOCContainer;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.i18n.LocalizationService;
import com.socialize.listener.ListenerHolder;
import com.socialize.listener.comment.CommentDeleteListener;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.dialog.SafeProgressDialog;
import com.socialize.ui.slider.ActionBarSliderView;
import com.socialize.ui.view.EntityView;

public class CommentView extends EntityView {
	
	private Dialog progress;
	private CommentListView commentListView;
	private boolean headerDisplayed = true;
	private Entity entity;
	private SocializeConfig config;
	private OnCommentViewActionListener onCommentViewActionListener;
	
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
				config = container.getBean("config");
				commentListView.setEntity(entity);
				commentListView.setHeaderDisplayed(headerDisplayed);
				ListenerHolder holder = container.getBean("listenerHolder");
				if(holder != null) {
					onCommentViewActionListener = holder.pop(COMMENT_LISTENER);
					commentListView.setOnCommentViewActionListener(onCommentViewActionListener);
				}	
				
				// Register for the delete comment menu
				((Activity)getContext()).registerForContextMenu(commentListView.getContent().getMainView());
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
	protected boolean onSettingsMenuItemClick(MenuItem item) {
		return onCommentViewActionListener != null && onCommentViewActionListener.onSettingsMenuItemClick(item);
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
				if(onCommentViewActionListener == null || !onCommentViewActionListener.onRefreshMenuItemClick(item)) {
					reload();
				}
				return true;
			}
		});
	}

	/**
	 * @param commentActivity
	 * @param menu
	 * @param v
	 * @param menuInfo
	 * @return
	 */
	public void onCreateContextMenu(CommentActivity commentActivity, ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(config.isAllowDeleteComment()) {
			AdapterView.AdapterContextMenuInfo info; 
	        try { 
	             info = (AdapterView.AdapterContextMenuInfo) menuInfo; 
	             CommentListItem item = (CommentListItem) info.targetView;
	             if(item != null) {
	            	 if(item.isDeleteOk()) {
	            		  menu.setHeaderTitle("Delete");
	            		  menu.add(Menu.NONE, 0, 0, "Delete this comment");
	            	 }
	             }
	        } 
	        catch (ClassCastException e) {} 
		}
	}
	
	public CommentListView getCommentListView() {
		return commentListView;
	}

	/**
	 * @param commentActivity
	 * @param item
	 * @return
	 */
	public boolean onContextItemSelected(final CommentActivity commentActivity, MenuItem item) {
		if(config.isAllowDeleteComment()) {
			AdapterView.AdapterContextMenuInfo info; 
			try { 
				info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo(); 
				CommentListItem cli = (CommentListItem) info.targetView;
				if(cli != null) {
					if(cli.isDeleteOk()) {

						final SafeProgressDialog progress = SafeProgressDialog.show(commentActivity);

						CommentUtils.deleteComment(commentActivity, cli.getCommentObject().getId(), new CommentDeleteListener() {

							@Override
							public void onError(SocializeException error) {
								progress.dismiss();
								Toast.makeText(commentActivity, "Failed to delete comment", Toast.LENGTH_SHORT).show();
								Log.e("Socialize", "Failed to delete comment", error);
							}

							@Override
							public void onDelete() {
								progress.dismiss();
								Toast.makeText(commentActivity, "Comment deleted", Toast.LENGTH_SHORT).show();
								reload();
							}
						});
					}
				}
			} 
			catch (ClassCastException e) {} 		
		}
		
		return false;
	}
}
