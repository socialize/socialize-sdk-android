package com.socialize.test.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultHolder {
	private List<Object> bucket;
	private AtomicInteger index;
	private Map<String, Integer> counts;
	
	public void setUp() {
		bucket = new ArrayList<Object>();
		index = new AtomicInteger(0);
		counts = new HashMap<String, Integer>();
	}
	

	public void incrementCount(String key) {
		Integer count = counts.get(key);
		if(count == null) {
			counts.put(key, 1);
		}
		else {
			counts.put(key, count.intValue() + 1);
		}
		
	}
	
	public int getCount(String key) {
		Integer count = counts.get(key);
		if(count == null) {
			return 0;
		}
		return count.intValue();
	}

	public int getNextIndex() {
		return index.getAndIncrement();
	}
	
	public void addResult(Object obj) {
		if(obj != null)
			bucket.add(obj);
	}
	
	public List<Object> getAllResults() {
		return bucket;
	}
	
	public void addResult(int index, Object obj) {
		int size = bucket.size();
		if(size <= index) {
			for (int i = size; i <= index; i++) {
				bucket.add(i, null);
			}
		}
		bucket.set(index, obj);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getResult(int index) {
		if(!bucket.isEmpty()) {
			if(index < bucket.size()) {
				return (T) bucket.get(index);
			}
		}
		return (T) null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getNextResult() {
		if(!bucket.isEmpty()) {
			return (T) bucket.remove(0);
		}
		return (T) null;
	}
	
	public void clear() {
		if(bucket != null) bucket.clear();
		if(counts != null) counts.clear();
	}
}
