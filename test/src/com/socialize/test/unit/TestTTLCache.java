/**
 * 
 */
package com.socialize.test.unit;

import com.socialize.cache.TTLCache;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason
 */
public class TestTTLCache extends SocializeUnitTest {

	private TTLCache<String, StringCacheable> cache;
	private TestCacheListener listener;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		cache = new TTLCache<String, StringCacheable>(getContext(), 1, 2);
		cache.setDebug(true);
		cache.setMaxCapacityBytes(20);
		cache.setHardByteLimit(false);
		
		listener = new TestCacheListener();
		
		cache.setEventListener(listener);
	}
	
	

	@Override
	protected void tearDown() throws Exception {
		cache.clear();
		super.tearDown();
	}

	/**
	 * Tests that an object put into cache can be retrieved.
	 */
	public void testSimpleCachePut() {
		StringCacheable testObj = new StringCacheable("test");
		cache.put("testKey", testObj);
		
		StringCacheable fromCache = cache.get("testKey");
		
		assertNotNull(fromCache);
		assertEquals("test", fromCache.getValue());
	}
	
	
	public void testOnPutCalledOnPut() {
		StringCacheable testObj = new StringCacheable("test");
		cache.put("testKey", testObj);
		assertEquals(1, testObj.getOnPutCount());
	}
	
	public void testOnGetCalledOnGet() {
		StringCacheable testObj = new StringCacheable("test");
		cache.put("testKey", testObj);
		cache.get("testKey");
		assertEquals(1, testObj.getOnGetCount());
	}
		
	
	/**
	 * Tests that the reaper was executed when expected.
	 */
	public void testManualCacheReap() {
		assertTrue(cache.doReap());
		assertEquals(1, listener.reapStarts);
		assertEquals(1, listener.reapEnds);
	}	
	
	public void testScheduledCacheReap() {
		
		assertEquals(0, listener.reapStarts);
		assertEquals(0, listener.reapEnds);		
		
		cache.setReapCycle(2000);
		
		try {
			Thread.sleep(2200);
		} catch (InterruptedException ignore) {
			ignore.printStackTrace();
		}
		
		assertEquals(1, listener.reapStarts);
		assertEquals(1, listener.reapEnds);
	}
	
	/**
	 * Tests that the onRemove method is called when an object is replaced.
	 */
	public void testOnRemoveCalledOnReplace() {
		StringCacheable testObj = new StringCacheable("test");
		StringCacheable testObj2 = new StringCacheable("test2");
		cache.put("testKey", testObj);
		cache.put("testKey", testObj2);
		
		assertEquals(1, testObj.getOnRemoveCount());
	}
	
	/**
	 * Tests that an object expires after its ttl.
	 */
	public void testObjectExpiry() {
		StringCacheable testObj = new StringCacheable("test");
		cache.put("testKey", testObj, 100);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignore) {
			ignore.printStackTrace();
		}
				
		assertTrue(cache.doReap());
		assertFalse(cache.exists("testKey"));
		assertNull(cache.get("testKey"));
	}	
	
	/**
	 * Tests that the onRemove method is called when an object is reaped.
	 */
	public void testOnRemoveCalledOnReap() {
		StringCacheable testObj = new StringCacheable("test");
		cache.put("testKey", testObj, 100);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignore) {
			ignore.printStackTrace();
		}
				
		assertTrue(cache.doReap());
		assertEquals(1, testObj.getOnRemoveCount());
	}
	
	/**
	 * Tests that the onRemove method is called when an object is manually removed.
	 */
	public void testOnRemoveCalledOnRemove() {
		StringCacheable testObj = new StringCacheable("test");
		cache.put("testKey", testObj);
		
		assertTrue(cache.exists("testKey"));
		
		cache.remove("testKey");
		
		assertFalse(cache.exists("testKey"));
		assertEquals(1, testObj.getOnRemoveCount());
	}
	
	/**
	 * Tests that the onRemove method is called when an object is manually removed.
	 */
	public void testOnRemoveCalledOnClear() {
		StringCacheable testObj = new StringCacheable("test");
		StringCacheable testObj2 = new StringCacheable("test2");
		cache.put("testKey", testObj);
		cache.put("testKey2", testObj2);
		
		assertEquals(2, cache.size());
		
		cache.clear();
		
		assertEquals(1, testObj.getOnRemoveCount());
		assertEquals(1, testObj2.getOnRemoveCount());
		assertEquals(0, cache.size());
	}	
	
	public void testSizeInBytesIncreasedOnPut() {
		StringCacheable testObj = new StringCacheable("test1");
		StringCacheable testObj2 = new StringCacheable("test2");
		cache.put("testKey", testObj);
		cache.put("testKey2", testObj2);
		assertEquals(10, cache.sizeInBytes());
	}
	
	public void testSizeInBytesCorrectOnReplace() {
		StringCacheable testObj = new StringCacheable("test1");
		StringCacheable testObj2 = new StringCacheable("test2");
		cache.put("testKey", testObj);
		cache.put("testKey", testObj2);
		assertEquals(5, cache.sizeInBytes());
	}
	
	public void testSizeInBytesCorrectOnClear() {
		StringCacheable testObj = new StringCacheable("test1");
		StringCacheable testObj2 = new StringCacheable("test2");
		cache.put("testKey", testObj);
		cache.put("testKey2", testObj2);
		
		cache.clear();
		
		assertEquals(0, cache.sizeInBytes());
	}	
	
	public void testSizeInBytesCorrectOnReap() {
		StringCacheable testObj = new StringCacheable("test1");
		StringCacheable testObj2 = new StringCacheable("test2");
		cache.put("testKey", testObj);
		cache.put("testKey2", testObj2, 100);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException ignore) {
			ignore.printStackTrace();
		}
		assertTrue(cache.doReap());
		assertEquals(0, testObj.getOnRemoveCount());
		assertEquals(1, testObj2.getOnRemoveCount());		
		
		assertEquals(5, cache.sizeInBytes());
	}	
	
	public void testSizeInBytesCorrectOnRemove() {
		StringCacheable testObj = new StringCacheable("test1");
		StringCacheable testObj2 = new StringCacheable("test2");
		cache.put("testKey", testObj);
		cache.put("testKey2", testObj2);
		
		cache.remove("testKey");
		
		assertEquals(5, cache.sizeInBytes());
	}		
	
	public void testTrimOnCountExceededAfterReap() {
		StringCacheable testObj = new StringCacheable("test1");
		StringCacheable testObj2 = new StringCacheable("test2");
		StringCacheable testObj3 = new StringCacheable("test3");
		
		cache.put("testKey", testObj);
		cache.put("testKey2", testObj2);
		cache.put("testKey3", testObj3);	
		
		assertEquals(3, cache.size());
		
		assertTrue(cache.doReap());
		
		assertEquals(2, cache.size());
	}
	
	public void testTrimOnBytesExceededAfterReap() {
		StringCacheable testObj = new StringCacheable("0000000000");
		StringCacheable testObj2 = new StringCacheable("00000000000");
		
		cache.put("testKey", testObj);
		cache.put("testKey2", testObj2);
		
		assertEquals(2, cache.size());
		
		assertTrue(cache.doReap());
		
		assertEquals(1, cache.size());
	}	
	
	public void testPutFailOnBytesExceededWithHardLimit() {
		StringCacheable testObj = new StringCacheable("0000000000");
		StringCacheable testObj2 = new StringCacheable("00000000000");

		cache.setHardByteLimit(true);
		cache.put("testKey", testObj);
		assertFalse(cache.put("testKey2", testObj2));
		assertEquals(1, cache.size());
		
	}	
	
	public void testOldestRemoveOnReap() {
		StringCacheable testObj = new StringCacheable("test1");
		StringCacheable testObj2 = new StringCacheable("test2");
		StringCacheable testObj3 = new StringCacheable("test3");

		cache.put("testKey", testObj);
		cache.put("testKey2", testObj2);

		try {
			Thread.sleep(100);
		} catch (InterruptedException ignore) {
			ignore.printStackTrace();
		}	

		cache.put("testKey", testObj); // replace so we know it's not just first
		cache.put("testKey3", testObj3);
		
		assertEquals(3, cache.size());
		
		assertTrue(cache.doReap());
		assertEquals(2, cache.size());
		
		assertTrue(cache.exists("testKey"));
		assertFalse(cache.exists("testKey2"));
		assertTrue(cache.exists("testKey3"));
	}
}
