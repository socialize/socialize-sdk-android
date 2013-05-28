package com.socialize.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.error.SocializeErrorHandler;
import com.socialize.log.SocializeLogger;

public abstract class BaseView extends LinearLayout implements SocializeView {
	
	private SocializeErrorHandler errorHandler;
	
	private int loadCount = 0;
	private int lastId = 0;
	
	private boolean rendered = false;
	
	private Toast toast;
	
	public BaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BaseView(Context context) {
		super(context);
	}
	
	public void showErrorToast(Context context, Exception e) {
		if(toast != null) toast.cancel();
		toast = Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG);
		toast.show();
	}
	
	public void showError(Context context, Exception e) {
		if(errorHandler != null) {
			errorHandler.handleError(context, e);
		}
		else {
			SocializeLogger.e("", e);
		}
	}

	public SocializeErrorHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(SocializeErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}
	
	/* (non-Javadoc)
	 * @see com.socialize.view.SocializeView#getActivity()
	 */
	@Override
	public Activity getActivity() {
		Context context = getContext();
		if(context instanceof Activity) {
			return (Activity) context;
		}
		return null;
	}
	
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		
		if(!isInEditMode()) {
			if(visibility == VISIBLE) {
				try {
					if(!checkLoaded()) {
						onViewLoad();
					}
					else {
						onViewUpdate();
					}
				}
				catch (Exception e) {
					onViewError(e);
				}
			}
			else {
				rendered = false;
			}
		}
		else {
			// Add the default Socialize View for display
			View editView = getEditModeView();
			if(editView != null) {
				addView(editView);
			}
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		onRender(w, h);
	}

	protected void onRender(int w, int h) {
		if(!rendered) {
			rendered = true;
			try {
				onViewRendered(w, h);
			}
			catch (Exception e) {
				onViewError(e);
			}
		}
	}

	protected void incrementLoaded() {
		loadCount++;
	}
	
	protected boolean checkLoaded() {
		boolean loaded = (loadCount > 0);
		incrementLoaded();
		return loaded;
	}
	
	// Subclasses override
	protected View getEditModeView() {
		return null;
	}
	
	public void assignId(View parent) {
		setId(getNextViewId(parent));
	}

	/* (non-Javadoc)
	 * @see com.socialize.view.SocializeView#onViewUpdate()
	 */
	@Override
	public void onViewUpdate() {}

	/* (non-Javadoc)
	 * @see com.socialize.view.SocializeView#onViewLoad()
	 */
	@Override
	public void onViewLoad() {}
	
	@Override
	public void onViewError(Exception e) {
		setVisibility(View.GONE);
	}

	/* (non-Javadoc)
	 * @see com.socialize.view.SocializeView#onViewRendered(int, int)
	 */
	@Override
	public void onViewRendered(int width, int height) {}
	
	protected View getParentView() {
		ViewParent parent = getParent();
		if(parent instanceof View) {
			return (View) parent;
		}
		return this;
	}
	
	protected int getNextViewId(View parent) {
		if(parent instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) parent;
			int childCount = group.getChildCount();
			
			if(childCount > 0) {
				int id = 0;
				
				for (int i = 0; i < childCount; i++) {
					View child = group.getChildAt(i);
					int childId = child.getId();
					if(childId > id) {
						id = childId;
					}
				}
				
				while(id < lastId) {
					id++;
				}
				
				lastId = id+1;
				return lastId;
			}
		}
		
		return ++lastId;
	}
}
