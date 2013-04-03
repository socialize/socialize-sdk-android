/**
 * 
 */
package com.socialize.cache;

/**
 * Represents an object that can be cached.
 * @author Jason
 */
public interface ICacheable<K extends Comparable<K>> {

	/**
	 * Returns the size in bytes of the object.
	 * @return
	 */
	public long getSizeInBytes();

	/**
	 * Called when the object is removed from cache.
	 * @param destroy If true the object should be completely destroyed.
	 */
	public boolean onRemove(boolean destroy);
	
	/**
	 * Called when the object is added to cache.
	 * @param key
	 */
	public boolean onPut(K key);
	
	
	/**
	 * Called when the object is retrieved to cache.
	 */
	public boolean onGet();
	
	/**
	 * Returns the key for this item in cache.
	 * @return
	 */
	public K getKey();
	
}
