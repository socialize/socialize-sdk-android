/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.cache;

/**
 * @author Jason Polites
 *
 */
public class TTLObject<K extends Comparable<K>, E extends ICacheable<K>> {
	private E object;
	private K key;
	private boolean eternal = false;
	private long lifeExpectancy;
	private long ttl;
	
	public TTLObject() {
		super();
	}

	public TTLObject(E obj, K key, long ttl) {
		super();
		this.object = obj;
		this.key = key;
		this.ttl = ttl;
		extendLife(ttl);
	}
	
	public void extendLife(long ttl) {
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
	public long getLifeExpectancy() {
		return lifeExpectancy;
	}
	
	/**
	 * @return Returns the ttl.
	 */
	public long getTtl() {
		return ttl;
	}

	/**
	 * @return Returns the object.
	 */
	public E getObject() {
		return object;
	}

	/**
	 * @return the key
	 */
	public K getKey() {
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
	public boolean isEternal() {
		return eternal;
	}

	/**
	 * @param eternal the eternal to set
	 */
	public void setEternal(boolean eternal) {
		this.eternal = eternal;
	}

	public void setLifeExpectancy(long lifeExpectancy) {
		this.lifeExpectancy = lifeExpectancy;
	}

	public void setTtl(long ttl) {
		this.ttl = ttl;
	}

	public void setObject(E object) {
		this.object = object;
	}

	public void setKey(K key) {
		this.key = key;
	}
}
