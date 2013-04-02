/**
 * 
 */
package com.socialize.test.unit;

import com.socialize.cache.ICacheEventListener;

/**
 * @author Jason
 *
 */
public class CacheListenerTest implements ICacheEventListener<String, StringCacheable> {

	public int reapStarts = 0;
	public int reapEnds = 0;
	public int gets = 0;
	public int puts = 0;	
	
	@Override
	public void onReapStart() {
		reapStarts++;
	}
	
	@Override
	public void onReapEnd(int reaped) {
		reapEnds++;
	}
	
	@Override
	public void onPut(StringCacheable object) {
		puts++;
	}
	
	@Override
	public void onGet(StringCacheable object) {
		gets++;
	}
}
