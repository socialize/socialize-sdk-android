/**
 * 
 */
package com.socialize.test.unit;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.cache.*;
import com.socialize.test.SocializeUnitTest;

import java.util.Collection;
import java.util.TreeMap;

/**
 * @author Jason
 */
public class TTLCacheTest extends SocializeUnitTest {

	private TTLCache<String, StringCacheable> cache;
	private CacheListenerTest listener;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		cache = new TTLCache<String, StringCacheable>(1, 2);
		cache.setDebug(true);
		cache.setMaxCapacityBytes(20);
		cache.setHardByteLimit(false);
		
		listener = new CacheListenerTest();
		
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
	public void testOnRemoveNOTCalledOnReplace() {
		StringCacheable testObj = new StringCacheable("test");
		StringCacheable testObj2 = new StringCacheable("test2");
		cache.put("testKey", testObj);
		cache.put("testKey", testObj2);
		
		assertEquals(0, testObj.getOnRemoveCount());
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
			Thread.sleep(500);
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
	
	public void testConstructorUsesDefaultCacheCount() {
		TTLCache<String, StringCacheable> cache = new TTLCache<String, StringCacheable>(1);
		assertEquals(TTLCache.DEFAULT_CACHE_COUNT, cache.getMaxCapacity());
	}
	
	public void testPauseCallsStopOnReaper() {
		TTLCache<String, StringCacheable> cache = new TTLCache<String, StringCacheable>() {
			@Override
			protected synchronized void stopReaper() {
				addResult(true);
			}
		};
		
		cache.pause();
		
		Boolean result = getNextResult();
		assertNotNull(result);
		assertTrue(result);
	}
	
	public void testResumeCallsStartOnReaper() {
		TTLCache<String, StringCacheable> cache = new TTLCache<String, StringCacheable>() {
			@Override
			protected synchronized void startReaper() {
				addResult(true);
			}
		};
		
		cache.resume();
		
		Boolean result = getNextResult();
		assertNotNull(result);
		assertTrue(result);
	}
	
	public void testPutCallPutWithDefaultTTL() {
		TTLCache<String, StringCacheable> cache = new TTLCache<String, StringCacheable>() {

			@Override
			protected synchronized boolean put(String k, StringCacheable object, long ttl, boolean eternal) {
				addResult(ttl);
				return true;
			}
			
		};
		
		cache.put("foobar", null);
		
		Long result = getNextResult();
		assertNotNull(result);
		assertEquals(cache.getDefaultTTL(), result.longValue());
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({TTLObject.class})
	public void testGetRaw() {
		final TTLObject<String, StringCacheable> object = AndroidMock.createMock(TTLObject.class);
		final StringCacheable cacheable = new StringCacheable();
		
		AndroidMock.expect(object.getObject()).andReturn(cacheable);
		
		AndroidMock.replay(object);
		
		TTLCache<String, StringCacheable> cache = new TTLCache<String, StringCacheable>() {
			@Override
			protected TTLObject<String, StringCacheable> getTTLObject(String strKey) {
				return object;
			}

			@Override
			public boolean isExpired(TTLObject<String, StringCacheable> object) {
				return false;
			}
		};
		
		assertSame(cacheable, cache.getRaw("foobar"));
		
		AndroidMock.verify(object);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({TTLObject.class, ICacheableFactory.class})
	public void testCreateOnEmptyGet() {
		
		final String key = "foobar";
		final TTLObject<String, StringCacheable> object = AndroidMock.createNiceMock(TTLObject.class);
		final ICacheableFactory<String, StringCacheable> factory = AndroidMock.createMock(ICacheableFactory.class);
		final StringCacheable cacheable = new StringCacheable();
		
		AndroidMock.expect(object.getObject()).andReturn(null);
		AndroidMock.expect(factory.create(key)).andReturn(cacheable);
		
		TTLCache<String, StringCacheable> cache = new TTLCache<String, StringCacheable>() {
			@Override
			protected TTLObject<String, StringCacheable> getTTLObject(String strKey) {
				return object;
			}

			@Override
			protected synchronized boolean put(String k, StringCacheable object, long ttl, boolean eternal) {
				addResult(true);
				return true;
			}
		};
		
		cache.setObjectFactory(factory);
		
		AndroidMock.replay(object);
		AndroidMock.replay(factory);
		
		assertSame(cacheable, cache.get(key));
		
		AndroidMock.verify(object);
		AndroidMock.verify(factory);
		
		Boolean result = getNextResult();
		assertNotNull(result);
		assertTrue(result);
	}
	
	public void testValues() {
		
		final int expiredCount = 5;
		int totalCount = 10;
		
		TTLCache<String, StringCacheable> cache = new TTLCache<String, StringCacheable>() {
			@Override
			public boolean isExpired(TTLObject<String, StringCacheable> object) {
				Integer nextResult = getNextResult();
				
				if(nextResult == null) {
					addResult(1);
					return true;
				}
				else if(nextResult >= expiredCount) {
					addResult(++nextResult);
					return false;
				}
				else {
					addResult(++nextResult);
					return true;
				}
			}
		};
		
		for (int i = 0; i < totalCount; i++) {
			StringCacheable value = new StringCacheable(String.valueOf(i));
			cache.put(String.valueOf(i), value);
		}
		
		Collection<StringCacheable> values = cache.values();
		assertNotNull(values);
		assertEquals((totalCount - expiredCount), values.size());
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks (TTLObject.class)
	public void testNotExpired() {

		TTLObject<String, StringCacheable> obj = AndroidMock.createMock(TTLObject.class);
		AndroidMock.expect(obj.getObject()).andReturn(null);
		AndroidMock.expect(obj.isEternal()).andReturn(false);
		AndroidMock.expect(obj.getLifeExpectancy()).andReturn(Long.MAX_VALUE);
		
		AndroidMock.replay(obj);

		assertFalse(cache.isExpired(obj));
		
		AndroidMock.verify(obj);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks (TTLObject.class)
	public void testExpired() {

		TTLObject<String, StringCacheable> obj = AndroidMock.createMock(TTLObject.class);
		AndroidMock.expect(obj.getObject()).andReturn(null);
		AndroidMock.expect(obj.isEternal()).andReturn(false);
		AndroidMock.expect(obj.getLifeExpectancy()).andReturn(0L);
		
		AndroidMock.replay(obj);

		assertTrue(cache.isExpired(obj));
		
		AndroidMock.verify(obj);
	}
	
	@SuppressWarnings("unchecked")
	@UsesMocks ({TTLObject.class, ISuicidal.class})
	public void testExpiredSuicical() {
		
		TTLCache<String, ISuicidal<String>> cache = new TTLCache<String, ISuicidal<String>>();
		
		TTLObject<String, ISuicidal<String>> obj = AndroidMock.createMock(TTLObject.class);
		ISuicidal<String> s = AndroidMock.createMock(ISuicidal.class);
		AndroidMock.expect(obj.getObject()).andReturn(s);
		AndroidMock.expect( s.isDead() ).andReturn(true);
		
		AndroidMock.replay(obj);
		AndroidMock.replay(s);

		assertTrue(cache.isExpired(obj));
		
		AndroidMock.verify(obj);
		AndroidMock.verify(s);
	}
	
	public void testKey() {
		
		Key<String> key1 = new Key<String>("foobar1", 0);
		Key<String> key2 = new Key<String>("foobar1", 0);
		Key<String> key3 = new Key<String>("foobar2", 0);
		
		assertFalse(key1.equals(key3));
		assertTrue(key1.equals(key2));
	}
	
	public void testKeyCompare() {
		Key<String> key1 = new Key<String>("foobar1", 100);
		Key<String> key2 = new Key<String>("foobar1", 200);
		
		// key1 is oldest
		TreeMap<Key<String>, String> sorted = new TreeMap<Key<String>, String>();
		
		sorted.put(key1, "foo");
		sorted.put(key2, "bar");
		
		Key<String> firstKey = sorted.firstKey();
		
		assertSame(key1, firstKey);
		
		TreeMap<Key<String>, String> sorted2 = new TreeMap<Key<String>, String>();
		
		sorted2.put(key2, "bar");
		sorted2.put(key1, "foo");
		
		Key<String> firstKey2 = sorted.firstKey();
		
		assertSame(key1, firstKey2);
		
	}
}
