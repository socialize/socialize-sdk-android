/**
 * 
 */
package com.socialize.cache;



/**
 * Used by the cache when an object is not found.
 * @author jasonpolites
 */
public interface ICacheableFactory<K extends Comparable<K>, E extends ICacheable<K>> {

	public E create(K key);
	
}
