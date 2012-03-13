package com.socialize.test.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ResultHolder {
	private List<Object> bucket;
	private AtomicInteger index;
	
	public void setUp() {
		bucket = new ArrayList<Object>();
		index = new AtomicInteger(0);
	}
	
	public int getNextIndex() {
		return index.getAndIncrement();
	}
	
	public void addResult(Object obj) {
		bucket.add(obj);
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
	}
}
