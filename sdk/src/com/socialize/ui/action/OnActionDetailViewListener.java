/**
 * 
 */
package com.socialize.ui.action;

import com.socialize.listener.SocializeListener;

/**
 * Provides a callback for action detail UI view events.
 * @author jasonpolites
 */
public interface OnActionDetailViewListener extends SocializeListener {

	public void onCreate(ActionDetailLayoutView view);
	
	public void onRender(ActionDetailLayoutView view);
	
}
