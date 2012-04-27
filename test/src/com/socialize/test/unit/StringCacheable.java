/**
 * 
 */
package com.socialize.test.unit;

import android.content.Context;
import com.socialize.cache.ISuicidal;

/**
 * @author Jason
 * 
 */
public class StringCacheable implements ISuicidal<String> {

	protected String value;
	protected String key;
	int onRemoveCount = 0;

	int onPutCount = 0;
	int onGetCount = 0;

	public StringCacheable() {
		super();
	}

	public StringCacheable(String value) {
		super();
		this.value = value;
	}

	@Override
	public String getKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.appmakr.android.cache.ICacheable#getSizeInBytes()
	 */
	@Override
	public long getSizeInBytes(Context context) {
		return this.value.length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.appmakr.android.cache.ICacheable#onRemove()
	 */
	@Override
	public boolean onRemove(Context context, boolean expired) {
		onRemoveCount++;
		return true;
	}

	public final int getOnRemoveCount() {
		return onRemoveCount;
	}

	public final String getValue() {
		return value;
	}

	@Override
	public boolean onPut(Context context, String key) {
		this.key = key;
		onPutCount++;
		return true;
	}

	@Override
	public boolean onGet(Context context) {
		onGetCount++;
		return true;
	}

	public final int getOnPutCount() {
		return onPutCount;
	}

	public final int getOnGetCount() {
		return onGetCount;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean isDead() {
		return false;
	}
}
