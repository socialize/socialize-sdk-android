/**
 * 
 */
package com.socialize.cache;

import android.content.Context;

/**
 * Represents an object that can be cached.
 * @author Jason
 */
public interface ICacheable<K extends Comparable<K>> {

	/**
	 * Returns the size in bytes of the object.
	 * @param context 
	 * @return
	 */
	public long getSizeInBytes(Context context);

	/**
	 * Called when the object is removed from cache.
	 * @param context 
	 * @param destroy If true the object should be completely destroyed.
	 */
	public boolean onRemove(Context context, boolean destroy);
	
	/**
	 * Called when the object is added to cache.
	 * @param context 
	 * @param key
	 */
	public boolean onPut(Context context, K key);
	
	
	/**
	 * Called when the object is retrieved to cache.
	 * @param context 
	 * @param key
	 */
	public boolean onGet(Context context);
	
	/**
	 * Returns the key for this item in cache.
	 * @return
	 */
	public K getKey();
	
}
