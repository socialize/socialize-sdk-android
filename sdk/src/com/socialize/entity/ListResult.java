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
package com.socialize.entity;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents the result of a list operation
 * @author Jason Polites
 */
public class ListResult<T> {

	private List<T> items;
	private List<ActionError> errors;
	private int totalCount;
	private T singleObject;
	
	public ListResult() {
		super();
	}
	
	public ListResult(List<T> results) {
		this();
		this.items = results;
	}
	
	public List<T> getItems() {
		return items;
	}
	public void setItems(List<T> results) {
		this.items = results;
	}
	public List<ActionError> getErrors() {
		return errors;
	}
	public void setErrors(List<ActionError> errors) {
		this.errors = errors;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public T getSingleObject() {
		return singleObject;
	}
	public void setSingleObject(T singleObject) {
		this.singleObject = singleObject;
	}

	public int size() {
		if(items != null) {
			return items.size();
		}
		else if(singleObject != null) {
			return 1;
		}
		return 0;
	}

	public ListResult<T> add(T item) {
		if(items == null) items = new LinkedList<T>();
		items.add(item);
		return this;
	}
}
