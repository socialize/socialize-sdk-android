package com.socialize.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import android.content.Context;

import com.socialize.log.SocializeLogger;

/**
 * Simple cache object backed by a synchronized map which allows a TTL (Time To Live) for objects in cache.
 * @author Jason Polites
 */
public class TTLCache<K extends Comparable<K>, E extends ICacheable<K>> {

	public static int DEFAULT_CACHE_COUNT = 1000;
	
	private TreeMap<Key<K>, TTLObject<K, E>> objects;
	private Map<K, Key<K>> keys;
	
	private SocializeLogger logger;

	private boolean debug = false;
	private boolean extendOnGet = true;
	private long reapCycle = 60000L; // 1 minute
	private long defaultTTL = 60 * 60 * 1000; // 1 hour
	
	private int maxCapacity = -1;	
	private long maxCapacityBytes = -1;
	
	private long currentSizeInBytes = 0;
	
	/**
	 * If true the cache will not accept new additions if the max bytes would be exceeded.
	 * If false, over sized objects are removed during reap.
	 */
	private boolean hardByteLimit = false;
	
	private ICacheEventListener<K, E> eventListener;
	protected ICacheableFactory<K, E> objectFactory;

	private static Timer reapTimer;
	private Reaper reaper;
	
	private boolean reaping = false;
	
	protected class Reaper extends TimerTask {
		public void run() {
			reap();
		}
	}
	
	protected Context context;

	public TTLCache(Context context) {
		this(context, 10,DEFAULT_CACHE_COUNT);
	}
	
	public TTLCache(Context context, int initialCapacity) {
		this(context, initialCapacity,DEFAULT_CACHE_COUNT);
	}
	
	public TTLCache(Context context, int initialCapacity, int maxCapacity) {
		super();
		this.context = context;
		this.maxCapacity = maxCapacity;	
		objects = makeMap();
		keys = new HashMap<K, Key<K>>(initialCapacity);
		
		// Start reaper
		startReaper();
	}
	
	/**
	 * Empties the cache and destroys all persistent states.
	 */
	public void destroy(Context context) {
		stopReaper();
		clear(true);
	}
	
	/**
	 * Empties the case and should remove any persistent states.
	 */
	public void clear() {
		clear(false);
	}
	
	public void pause() {
		stopReaper();
	}
	
	public void resume() {
		startReaper();
	}
	
	protected synchronized void clear(boolean destroy) {
		keys.clear();
		
		Collection<TTLObject<K, E>> values = objects.values();

		for (TTLObject<K, E> ttlObject : values) {
			ttlObject.getObject().onRemove(context, destroy);
		}
		
		objects.clear();
		currentSizeInBytes = 0;
	}
	
	public void setReapCycle(long milliseconds) {
		reapCycle = milliseconds;
		startReaper();
	}
	
	protected synchronized void startReaper() {
		stopReaper();
		
		if(reapCycle > 0) {
			reaper = new Reaper();
			
			if(reapTimer == null) {
				reapTimer = new Timer("CacheReaper", true); // Daemon so we auto-exit on shutdown
			}
			
			reapTimer.schedule(reaper, reapCycle, reapCycle);	
		}
	}
	
	protected synchronized void stopReaper() {
		if(reaper != null) {
			reaper.cancel();
			reaper = null;
		}
		
		if(reapTimer != null) {
			reapTimer.purge();
		}
		
		reaping = false;
	}
	
	/**
	 * Adds an object to cache with the given time-to-live
	 * @param strKey
	 * @param object
	 * @param ttl
	 */
	public boolean put(K strKey, E object, long ttl) {
		return put(strKey, object, ttl, false);
	}
	
	/**
	 * Adds an object to cache that optionally lives forever.
	 * @param strKey
	 * @param object
	 * @param eternal
	 * @return
	 */
	public boolean put(K strKey, E object, boolean eternal) {
		return put(strKey, object, defaultTTL, eternal);
	}
	
	
	/**
	 * Adds an eternal object to cache.  Eternal objects will never expire, unless they commit suicide.
	 * @param strKey
	 * @param object
	 */
	public boolean put(K strKey, E object) {
		return put(strKey, object, defaultTTL, (defaultTTL <= 0));
	}
	
	/**
	 * Adds an object to cache with the given Time To Live in milliseconds
	 * @param key
	 * @param object
	 * @param ttl milliseconds
	 */
	protected synchronized boolean put(K k, E object, long ttl, boolean eternal) {
		// Check the key map first
		Key<K> key = keys.get(k);
		
		TTLObject<K, E> t = new TTLObject<K, E>(object, k, ttl);
		
		t.setEternal(eternal);
		
		long addedSize = object.getSizeInBytes(context);
		long newSize = currentSizeInBytes + addedSize;
		boolean oversize = false;
		
		oversize = (hardByteLimit && maxCapacityBytes > 0 && newSize > maxCapacityBytes);
		
//		if(key != null) {
//			// Remove the object if it exists
//			TTLObject<K, E> removed = objects.remove(key);
//			
//			if(removed != null) {
//				currentSizeInBytes -= removed.getObject().getSizeInBytes(context);
//				removed.getObject().onRemove(context, true);
//				newSize = currentSizeInBytes + addedSize;
//				oversize = (hardByteLimit && maxCapacityBytes > 0 && newSize > maxCapacityBytes);
//			}
//		}
		
		if(!oversize) {
			key = new Key<K>(k, System.currentTimeMillis());
			keys.put(k, key);	
			objects.put(key, t);
			
			t.getObject().onPut(context, k);
			
			// Increment size
			currentSizeInBytes = newSize;
			
			if(eventListener != null) {
				eventListener.onPut(object);
			}		
			
			return true;
		}

		return false;
	}
	
	protected TTLObject<K, E> getTTLObject(K strKey) {
		TTLObject<K, E> obj = null;
		
		// Look for the key
		Key<K> key = keys.get(strKey);
		if(key != null) {
			obj = objects.get(key);
		}
		
		return obj;
	}
	
	/**
	 * Ignores proxy and always returns raw object
	 * @param strKey
	 * @return
	 */
	public synchronized E getRaw(K strKey) {
		TTLObject<K, E> obj = getTTLObject(strKey);
		if(obj != null && !isExpired(obj)) {
			return obj.getObject();
		}
		return null;
	}
	
	/**
	 * Gets an object from cache.  Returns null if the object does not exist, or has expired.
	 * @param key
	 * @return
	 */
	public synchronized E get(K key) {
		TTLObject<K, E> obj = getTTLObject(key);
		if(obj != null && !isExpired(obj)) {
			
			if(extendOnGet) {
				extendTTL(key);
			}

			if(eventListener != null) {
				eventListener.onGet(obj.getObject());
			}			
			
			obj.getObject().onGet(context);
			
			return obj.getObject();
	
		}
		else if(obj != null) {
			// Expired
			destroy(obj.getKey());
			obj = null;
		}
		
		if (obj == null) {
			if(objectFactory != null) {
				E object = objectFactory.create(key);
				
				if(object != null) {
					if(!put(key, object) && logger != null) {
						// We couldn't put this record.. just log a warning
						logger.warn("Failed to put object into cache. Cache size exceeded");
					}
				}
				
				return object;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the internal values of the cache. 
	 * <br/>
	 * Proxy objects are returned if gets are proxied within the cache.
	 * @return
	 */
	public Collection<E> values() {
		Collection<E> values = null;
		Collection<TTLObject<K, E>> ttls = objects.values();
		if(ttls != null) {
			values = new ArrayList<E>(ttls.size());
			for (TTLObject<K, E> t : ttls) {
				if(!isExpired(t)) {
					values.add(t.getObject());
				}
			}
		}
		return values;
	}
	
	/**
	 * Returns true if the object with the given key resides in the cache.
	 * @param key
	 * @return
	 */
	public boolean exists(K k) {
		Key<K> key = keys.get(k);
		if(key != null) {
			return objects.get(key) != null;
		}
		return false;
	}
	
	public boolean keyExists(K k) {
		return keys.get(k) != null;
	}
	
	/**
	 * Removes an object from cache.  If the object maintains a persistent state.
	 * @param key
	 */
	public E remove(K key) {
		return remove(key, false);
	}
	
	/**
	 * Destroys an object in cache.  This differs from remove() such that any persistent state associated with the object should also be removed.
	 * @param key
	 */
	public E destroy(K key) {
		return remove(key, true);
	}
	
	public synchronized E remove(K strKey, boolean destroy) {
		Key<K> key = keys.get(strKey);
		TTLObject<K, E> removed = null;
		if(key != null) {
			removed = objects.remove(key);
			
			if(removed != null) {
				currentSizeInBytes -= removed.getObject().getSizeInBytes(context);
				removed.getObject().onRemove(context, destroy);
			}
			
			keys.remove(strKey);
		}
		
		if(removed != null) {
			return removed.getObject();
		}
		
		return null;
	}
	
	/**
	 * Extends the ttl of the object with the given key with the current system time.
	 * @param key
	 */
	public synchronized void extendTTL(K strKey) {
		TTLObject<K, E> object = getTTLObject(strKey);
		if(object != null) {
			object.setLifeExpectancy(System.currentTimeMillis() + object.getTtl());
		}
	}

	/**
	 * @return Returns the debug.
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * @param debug The debug to set.
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public boolean isExpired(TTLObject<K, E> object) {
		E o = object.getObject();
		
		if(o instanceof ISuicidal) {
			ISuicidal<K> s = (ISuicidal<K>) o;
			if(s.isDead()) {
				return true;
			}
		}
		return !object.isEternal() && object.getLifeExpectancy() <= System.currentTimeMillis();
	}
	
	public boolean doReap() {
		return reap();
	}

	protected synchronized boolean reap() {
		
		if(!reaping) {
			
			int reaped = 0;
			
			try {
				reaping = true;
				
				if(eventListener != null) {
					eventListener.onReapStart();
				}
				
				int size = objects.size();		
				
				if(size > 0) {
					
					Set<Key<K>> localKeys = objects.keySet();
					
					TreeMap<Key<K>, TTLObject<K, E>> newMap = makeMap();
					
					long time = System.currentTimeMillis();
					
					boolean ok = true;
					
					TTLObject<K, E> object = null;

					for (Key<K> key : localKeys) {
						object = objects.get(key);
						
						ok = true;
						
						if(object.getObject() instanceof ISuicidal) {
							ISuicidal<K> s = (ISuicidal<K>) object.getObject();
							
							
							if(s.isDead()) {
								
								size--;
								
								if(debug && logger != null) {
									
									String msg = "Object [" +
											object.getObject().toString() +
											"] has comitted suicide and will be purged from cache [" +
											size +
											"] objects remain";
									
									logger.debug(msg);
								}	
								
								ok = false;
							}
						}
						
						if(ok) {
							if(object.isEternal() || object.getLifeExpectancy() >= time) {
								// Save
								newMap.put(key, object);
							}
							else {
								
								ok = false;
								
								size--;
								
								if(debug && logger != null) {
									
									String msg = "Object [" +
											object.getObject().toString() +
											"] with ttl of [" +
											object.getTtl() +
											"] has expired and will be purged from cache [" +
											size +
											"] objects remain";
									
									logger.debug(msg);
								}
							}
						}
						
						if(!ok) {
							keys.remove(key.getKey());
							
							currentSizeInBytes -= object.getObject().getSizeInBytes(context);
							reaped++;
							object.getObject().onRemove(context, true);
						}
					}
					
					// Now check for over size
					size = newMap.size();
					
					if((maxCapacity > 0 && size > maxCapacity) || (maxCapacityBytes > 0 && currentSizeInBytes > maxCapacityBytes)) {
						// To many objects.. start trimming
						if(debug && logger != null) {
							String msg = "TTLCache has count of [" +
									size +
									"], size of [" +
									currentSizeInBytes +
									"] bytes  which exceeds maximum of [" +
									maxCapacity +
									"], [" +
									maxCapacityBytes +
									"] bytes.  Excess items will be trimmed";
							
							logger.debug(msg);
					
						}

						Key<K> key = null;
						
						while((maxCapacity > 0 && size > maxCapacity) || (maxCapacityBytes > 0 && currentSizeInBytes > maxCapacityBytes)) {
							key = newMap.firstKey();
							
							if(debug && logger != null) {
								String msg = "Removing item with key [" +
										key +
										"] from cache";
								
								logger.debug( msg);
							}					
							
							
							TTLObject<K, E> removed = newMap.remove(key);
							
							keys.remove(key.getKey());
							
							size = newMap.size();
							
							currentSizeInBytes -= removed.getObject().getSizeInBytes(context);
							
							removed.getObject().onRemove(context, true);
							
							reaped++;
							
							if(debug && logger != null) {
								String msg = "[" +
										size +
										"] objects remain with size of [" +
										currentSizeInBytes +
										"]";
								
								logger.debug(msg);
							}					
						}
					}
					
					// Swap
					objects = newMap;
				}
			}
			finally {
				
				if(eventListener != null) {
					eventListener.onReapEnd(reaped);
				}				
				
				reaping = false;
			}
			
			return true;
		}
		
		return false;
	}
	

	protected TreeMap<Key<K>, TTLObject<K, E>> makeMap() {
		return new TreeMap<Key<K>, TTLObject<K, E>>();
	}

	public int size() {
		return objects.size();
	}
	
	public long sizeInBytes() {
		return currentSizeInBytes;
	}
	
//	protected class Key implements Comparable<Key> {
//
//		private long time;
//		private K key;
//		
//		private Key(K key, long time) {
//			this.time = time;
//			this.key = key;
//		}
//		
//		/* (non-Javadoc)
//		 * @see java.lang.Comparable#compareTo(java.lang.Object)
//		 */
//		public int compareTo(Key o) {
//			if(o.time > time) {
//				return -1;
//			}
//			else if(o.time < time) {
//				return 1;
//			}
//			else {
//				return o.key.compareTo(key);
//			}
//		}
//
//		@Override
//		public int hashCode() {
//			int prime = 31;
//			int result = 1;
//			result = prime * result + getOuterType().hashCode();
//			result = prime * result + ((key == null) ? 0 : key.hashCode());
//			return result;
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (getClass() != obj.getClass())
//				return false;
//			Key other = (Key) obj;
//			if (!getOuterType().equals(other.getOuterType()))
//				return false;
//			if (key == null) {
//				if (other.key != null)
//					return false;
//			} else if (!key.equals(other.key))
//				return false;
//			return true;
//		}
//
//		public String toString() {
//			return key.toString();
//		}
//
//		private TTLCache<K, E> getOuterType() {
//			return TTLCache.this;
//		}
//	}
	
//	protected class TTLObject {
//		
//		private E object;
//		private K key;
//		private boolean eternal = false;
//		private long lifeExpectancy;
//		private long ttl;
//		
//		public TTLObject(E obj, K key, long ttl) {
//			super();
//			this.object = obj;
//			this.key = key;
//			this.ttl = ttl;
//			
//			if(ttl > 0) {
//				long time = System.currentTimeMillis();
//				
//				if(ttl < (Long.MAX_VALUE - time)) {
//					this.lifeExpectancy = time + ttl;
//				}
//				else {
//					this.lifeExpectancy = ttl;
//				}
//			}
//			else {
//				this.eternal = true;
//			}
//		}
//
//		/**
//		 * @return Returns the expectancy.
//		 */
//		public long getLifeExpectancy() {
//			return lifeExpectancy;
//		}
//		
//		/**
//		 * @return Returns the ttl.
//		 */
//		public long getTtl() {
//			return ttl;
//		}
//
//		/**
//		 * @return Returns the object.
//		 */
//		public E getObject() {
//			return object;
//		}
//
//		/**
//		 * @return the key
//		 */
//		public K getKey() {
//			return key;
//		}
//
//		public boolean equals(Object obj) {
//			return object.equals(obj);
//		}
//
//		public int hashCode() {
//			return object.hashCode();
//		}
//
//		public String toString() {
//			return object.toString();
//		}
//
//		/**
//		 * @return the eternal
//		 */
//		public boolean isEternal() {
//			return eternal;
//		}
//
//		/**
//		 * @param eternal the eternal to set
//		 */
//		public void setEternal(boolean eternal) {
//			this.eternal = eternal;
//		}
//	}
	
	public boolean isExtendOnGet() {
		return extendOnGet;
	}

	public void setExtendOnGet(boolean extendOnGet) {
		this.extendOnGet = extendOnGet;
	}

	public long getMaxCapacityBytes() {
		return maxCapacityBytes;
	}

	public void setMaxCapacityBytes(long maxCapacityBytes) {
		this.maxCapacityBytes = maxCapacityBytes;
	}

	public ICacheEventListener<K, E> getEventListener() {
		return eventListener;
	}

	public void setEventListener(ICacheEventListener<K, E> eventListener) {
		this.eventListener = eventListener;
	}
	
	/**
	 * Extends the maximum capacity.
	 * @param extension
	 */
	public void extendMax(int extension) {
		maxCapacity += extension;
	}
	
	/**
	 * @return the maxCapacity
	 */
	public int getMaxCapacity() {
		return maxCapacity;
	}

	public ICacheableFactory<K, E> getObjectFactory() {
		return objectFactory;
	}

	public void setObjectFactory(ICacheableFactory<K, E> constructor) {
		this.objectFactory = constructor;
	}

	public long getDefaultTTL() {
		return defaultTTL;
	}

	public void setDefaultTTL(long defaultTTL) {
		this.defaultTTL = defaultTTL;
	}

	public boolean isHardByteLimit() {
		return hardByteLimit;
	}

	public void setHardByteLimit(boolean hardByteLimit) {
		this.hardByteLimit = hardByteLimit;
	}
	
	public SocializeLogger getLogger() {
		return logger;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	/**
	 * General init method.  Subclasses override.
	 * @param context
	 */
	public void init(Context context) {}
}
