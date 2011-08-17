package com.socialize.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import com.socialize.log.SocializeLogger;

import android.content.Context;

/**
 * Simple cache object backed by a synchronized map which allows a TTL (Time To Live) for objects in cache.
 * @author Jason Polites
 */
public class TTLCache<K extends Comparable<K>, E extends ICacheable<K>> {

	public static final int DEFAULT_CACHE_COUNT = 1000;
	
	private TreeMap<Key, TTLObject> objects;
	private Map<K, Key> keys;
	
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
	
	private class Reaper extends TimerTask {
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
		keys = new HashMap<K, Key>(initialCapacity);
		
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
	
	private synchronized void clear(boolean destroy) {
		keys.clear();
		
		Collection<TTLObject> values = objects.values();

		for (TTLObject ttlObject : values) {
			ttlObject.getObject().onRemove(context, destroy);
		}
		
		objects.clear();
		currentSizeInBytes = 0;
	}
	
	public void setReapCycle(long milliseconds) {
		reapCycle = milliseconds;
		startReaper();
	}
	
	private synchronized void startReaper() {
		stopReaper();
		
		if(reapCycle > 0) {
			reaper = new Reaper();
			
			if(reapTimer == null) {
				reapTimer = new Timer("CacheReaper", true); // Daemon so we auto-exit on shutdown
			}
			
			reapTimer.schedule(reaper, reapCycle, reapCycle);	
		}
	}
	
	private synchronized void stopReaper() {
		if(reaper != null) {
			reaper.cancel();
			reaper = null;
		}
		
		if(reapTimer != null) {
			reapTimer.purge();
		}
		
		reaping = false;
	}
	
	
	public Set<K> ketSet() {
		
		// Force a reap here
		reap();
		
		if(keys != null) {
			return keys.keySet();
		}
		return null;
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
	private synchronized boolean put(K k, E object, long ttl, boolean eternal) {
		// Check the key map first
		Key key = keys.get(k);
		
		TTLObject t = new TTLObject(object, k, ttl);
		
		t.setEternal(eternal);
		
		long addedSize = object.getSizeInBytes(context);
		long newSize = currentSizeInBytes + addedSize;
		boolean oversize = false;
		
		oversize = (hardByteLimit && maxCapacityBytes > 0 && newSize > maxCapacityBytes);
		
		if(key != null) {
			// Remove the object if it exists
			TTLObject removed = objects.remove(key);
			
			if(removed != null) {
				currentSizeInBytes -= removed.getObject().getSizeInBytes(context);
				removed.getObject().onRemove(context, true);
				newSize = currentSizeInBytes + addedSize;
				oversize = (hardByteLimit && maxCapacityBytes > 0 && newSize > maxCapacityBytes);
			}
		}
		
		if(!oversize) {
			key = new Key(k, System.currentTimeMillis());
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
	
	private TTLObject getTTLObject(K strKey) {
		TTLObject obj = null;
		
		// Look for the key
		Key key = keys.get(strKey);
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
		TTLObject obj = getTTLObject(strKey);
		if(obj != null && !expired(obj)) {
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
		TTLObject obj = getTTLObject(key);
		if(obj != null && !expired(obj)) {
			
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
		Collection<TTLObject> ttls = objects.values();
		if(ttls != null) {
			values = new ArrayList<E>(ttls.size());
			for (TTLObject t : ttls) {
				if(!expired(t)) {
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
		Key key = keys.get(k);
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
		Key key = keys.get(strKey);
		TTLObject removed = null;
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
		TTLObject object = getTTLObject(strKey);
		if(object != null) {
			object.lifeExpectancy = System.currentTimeMillis() + object.ttl;
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
	
	private boolean expired(TTLObject object) {
		if(object.getObject() instanceof ISuicidal) {
			ISuicidal s = (ISuicidal) object.getObject();
			if(s.isDead()) {
				return true;
			}
		}
		return !object.isEternal() && object.getLifeExpectancy() <= System.currentTimeMillis();
	}
	
	public boolean doReap() {
		return reap();
	}

	private synchronized boolean reap() {
		
		if(!reaping) {
			
			int reaped = 0;
			
			try {
				reaping = true;
				
				if(eventListener != null) {
					eventListener.onReapStart();
				}
				
				int size = objects.size();		
				
				if(size > 0) {
					
					Set<Key> localKeys = objects.keySet();
					
					TreeMap<Key, TTLObject> newMap = makeMap();
					
					long time = System.currentTimeMillis();
					
					boolean ok = true;
					
					TTLObject object = null;

					for (Key key : localKeys) {
						object = objects.get(key);
						
						ok = true;
						
						if(object.getObject() instanceof ISuicidal) {
							ISuicidal s = (ISuicidal) object.getObject();
							
							
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
							keys.remove(key.key);
							
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

						Key key = null;
						
						while((maxCapacity > 0 && size > maxCapacity) || (maxCapacityBytes > 0 && currentSizeInBytes > maxCapacityBytes)) {
							key = newMap.firstKey();
							
							if(debug && logger != null) {
								String msg = "Removing item with key [" +
										key +
										"] from cache";
								
								logger.debug( msg);
							}					
							
							
							TTLObject removed = newMap.remove(key);
							
							keys.remove(key.key);
							
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
	

	private TreeMap<Key, TTLObject> makeMap() {
		return new TreeMap<Key, TTLObject>();
	}

	public int size() {
		return objects.size();
	}
	
	public long sizeInBytes() {
		return currentSizeInBytes;
	}
	
	private final class Key implements Comparable<Key> {

		private long time;
		private K key;
		
		private Key(K key, long time) {
			this.time = time;
			this.key = key;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Key o) {
			if(o.time > time) {
				return -1;
			}
			else if(o.time < time) {
				return 1;
			}
			else {
				return o.key.compareTo(key);
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			return true;
		}

		public String toString() {
			return key.toString();
		}

		private TTLCache<K, E> getOuterType() {
			return TTLCache.this;
		}
	}
	
	private final class TTLObject {
		
		private E object;
		private K key;
		private boolean eternal = false;
		private long lifeExpectancy;
		private long ttl;
		
		public TTLObject(E obj, K key, long ttl) {
			super();
			this.object = obj;
			this.key = key;
			this.ttl = ttl;
			
			if(ttl > 0) {
				long time = System.currentTimeMillis();
				
				if(ttl < (Long.MAX_VALUE - time)) {
					this.lifeExpectancy = time + ttl;
				}
				else {
					this.lifeExpectancy = ttl;
				}
			}
			else {
				this.eternal = true;
			}
		}

		/**
		 * @return Returns the expectancy.
		 */
		public final long getLifeExpectancy() {
			return lifeExpectancy;
		}
		
		/**
		 * @return Returns the ttl.
		 */
		public final long getTtl() {
			return ttl;
		}

		/**
		 * @return Returns the object.
		 */
		public final E getObject() {
			return object;
		}

		/**
		 * @return the key
		 */
		public final K getKey() {
			return key;
		}

		public boolean equals(Object obj) {
			return object.equals(obj);
		}

		public int hashCode() {
			return object.hashCode();
		}

		public String toString() {
			return object.toString();
		}

		/**
		 * @return the eternal
		 */
		public final boolean isEternal() {
			return eternal;
		}

		/**
		 * @param eternal the eternal to set
		 */
		public final void setEternal(boolean eternal) {
			this.eternal = eternal;
		}
	}
	
	public boolean isExtendOnGet() {
		return extendOnGet;
	}

	public void setExtendOnGet(boolean extendOnGet) {
		this.extendOnGet = extendOnGet;
	}

	public final long getMaxCapacityBytes() {
		return maxCapacityBytes;
	}

	public final void setMaxCapacityBytes(long maxCapacityBytes) {
		this.maxCapacityBytes = maxCapacityBytes;
	}

	public final ICacheEventListener<K, E> getEventListener() {
		return eventListener;
	}

	public final void setEventListener(ICacheEventListener<K, E> eventListener) {
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
