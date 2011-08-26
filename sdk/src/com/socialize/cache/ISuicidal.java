/**
 * 
 */
package com.socialize.cache;

/**
 * A suicidal object will destroy itself once a given condition is met.
 * <br/>
 * @author Jason
 */
public interface ISuicidal<K extends Comparable<K>> extends ICacheable<K> {

	/**
	 * Returns true when the object has died.
	 * @return
	 */
	public boolean isDead();
	
}
