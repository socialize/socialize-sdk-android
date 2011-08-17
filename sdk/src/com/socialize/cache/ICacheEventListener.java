/**
 * 
 */
package com.socialize.cache;


/**
 * Listens for cache events.
 * @author Jason
 */
public interface ICacheEventListener<K extends Comparable<K>, E extends ICacheable<K>> {

	/**
	 * Called when an object is put into cache.
	 * @param object
	 */
	public void onPut(E object);
	
	/**
	 * Called when an object is retrieved from cache.
	 * @param object
	 */
	public void onGet(E object);
	
	/**
	 * Called when the reaper thread starts.
	 */
	public void onReapStart();
	
	/**
	 * Called when the reaper thread completes.
	 * @param reaped The number of objects reaped.
	 */
	public void onReapEnd(int reaped);
	
}
