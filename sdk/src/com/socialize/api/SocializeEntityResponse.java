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
package com.socialize.api;

import com.socialize.entity.ListResult;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Jason Polites
 *
 * @param <T>
 */
public class SocializeEntityResponse<T> implements SocializeResponse {

	private ListResult<T> results;
	private boolean resultsExpected = true;
	
	public ListResult<T> getResults() {
		return results;
	}
	public void setResults(ListResult<T> results) {
		this.results = results;
	}
	
	public void addResult(T result) {
		
		if(results == null)  {
			synchronized (this) {
				if(results == null)  {
					results = new ListResult<T>();
				}
			}
		}
		
		List<T> list = results.getItems();
		
		if(list == null) {
			list = new LinkedList<T>();
			results.setItems(list);
		}
		
		list.add(result);
	}
	
	public T getFirstResult() {
		if(results != null) {
			List<T> list = results.getItems();
			if(list != null && list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}
	
	public boolean isResultsExpected() {
		return resultsExpected;
	}
	
	public void setResultsExpected(boolean resultsExpected) {
		this.resultsExpected = resultsExpected;
	}
}
