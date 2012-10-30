package com.socialize.android.ioc;

import java.util.Map;
import java.util.TreeMap;

public class BeanMappingCache {

	private static final Map<String, BeanMapping> mappings = new TreeMap<String, BeanMapping>();
	
	public static BeanMapping get(String key) {
		return mappings.get(key);
	}
	
	static void put(String key, BeanMapping mapping) {
		mappings.put(key, mapping);
	}
	
	static void destroy() {
		mappings.clear();
	}
}
