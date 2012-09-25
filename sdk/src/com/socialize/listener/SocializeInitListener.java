/**
 * 
 */
package com.socialize.listener;

import android.content.Context;
import com.socialize.android.ioc.IOCContainer;

/**
 * Provides a callback for asynchronous initialization.
 * @author jasonpolites
 */
public interface SocializeInitListener extends SocializeListener {

	/**
	 * Called after the socialize instance is successfully initialized.
	 * @param context
	 * @param container
	 */
	public void onInit(Context context, IOCContainer container); 
}
