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
import java.util.LinkedList;
import java.util.List;

public class ParserUtils {

	public void merge(BeanRef source, BeanRef destination) {
		if(source.getProperties() != null) {
			if(destination.getProperties() == null) {
				List<Argument> destList = new ArrayList<Argument>(source.getProperties());
				destination.setProperties(destList);
			}
			else {
				merge(source.getProperties(), destination.getProperties());
			}
		}
		
		if(destination.getClassName() == null) {
			destination.setClassName(source.getClassName());
		}
		
		if(destination.getInitMethod() == null) {
			destination.setInitMethod(source.getInitMethod());
		}
		
		if(destination.getDestroyMethod() == null) {
			destination.setDestroyMethod(source.getDestroyMethod());
		}
	}
	
	public void merge(List<Argument> source, List<Argument> desination) {
		List<Argument> result = new LinkedList<Argument>();
		// Dupe dest
		List<Argument> dupe = new LinkedList<Argument>(desination);
		for (Argument src : source) {
			
			boolean found = false;
			
			for (Argument dest : dupe) {
				if(src.getKey() != null && dest.getKey() != null && src.getKey().equals(dest.getKey())) {
					result.add(dest);
					dupe.remove(dest);
					found = true;
					break;
				}
			}
			
			if(!found) {
				result.add(src);
			}
		}
		
		desination.clear();
		desination.addAll(result);
		desination.addAll(dupe);
	}
}
