/**
 * 
 */
package com.socialize.cache;

/**
 * A suicidal object will destroy itself once a given condition is met.
 * <br/>
 * @author Jason
 */
public interface ISuicidal {

	/**
	 * Returns true when the object has died.
	 * @return
	 */
	public boolean isDead();
	
}
