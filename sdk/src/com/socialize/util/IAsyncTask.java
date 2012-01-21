/**
 * 
 */
package com.socialize.util;

/**
 * Provides a wrapper for AsyncTasks to allow us to mock them.
 * @author jasonpolites
 *
 */
public interface IAsyncTask<Params, Progress, Result> {
	public void doExecute(Params...params);
}
