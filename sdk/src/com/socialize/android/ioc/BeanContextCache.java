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
package com.socialize.android.ioc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


/**
 * @author Jason Polites
 */
public class BeanContextCache {
	
	
	private static final Map<Context, Map<String, List<CacheEntry>>> cache = new HashMap<Context, Map<String,List<CacheEntry>>>();
	
	private Context context;
	
	public BeanContextCache() {
		super();
	}

	protected Object matchEntry(List<CacheEntry> entries, Object...args) {
		for (CacheEntry entry : entries) {
			if(Arrays.equals(entry.args, args)) {
				return entry.object;
			}
		}
		return null;
	}
	
	public Object get(Container container, String name, Object...args) {
		
		Object object = null;
		BeanRef beanRef = container.getBeanRef(name);
		
		if(beanRef != null && (beanRef.isSingleton() || !beanRef.isCached())) {
			object = container.getBeanLocal(name, args);
		}
		else {
			List<CacheEntry> entries = getBeanCache().get(name);
			
			if(entries != null && entries.size() > 0) {
				object = matchEntry(entries, args);
			}
			
			if(object == null) {
				object = container.getBeanLocal(name, args);
				
				if(object != null) {
					
					if(beanRef != null && beanRef.isContextSensitive() && beanRef.isCached()) {
						// Cache
						CacheEntry entry = new CacheEntry();
						entry.object = object;
						entry.name = name;
						entry.args = args;
						
						if(entries == null) {
							entries = new ArrayList<BeanContextCache.CacheEntry>();
						}
						
						entries.add(entry);
						
						getBeanCache().put(name, entries);
					}
				}
			}
			else {
				if(object instanceof View) {
					Object parent = ((View)object).getParent();
					if(parent instanceof ViewGroup) {
						((ViewGroup)parent).removeView((View)object);
					}
				}
			}
		}
		
		
		return object;
	}
	
	class CacheEntry {
		String name;
		Object object;
		Object[] args;
	}

	private Map<String, List<CacheEntry>> getBeanCache() {
		Map<String, List<CacheEntry>> map = cache.get(context);
		if(map == null) {
			map = new TreeMap<String, List<CacheEntry>>();
			cache.put(context, map);
			return map;
		}
		
		return cache.get(context);
	}
	
	public void setContext(Context context) {
		this.context = context;
		getBeanCache();
	}

	public void onContextDestroyed(Context context) {
		Map<String, List<CacheEntry>> map = cache.get(context);
		if(map != null) {
			map.clear();
		}
	}
}
