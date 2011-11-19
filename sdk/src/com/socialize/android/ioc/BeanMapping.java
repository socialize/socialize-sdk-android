/*
 * Copyright (c) 2011 Socialize Inc. 
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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author Jason Polites
 *
 */
public class BeanMapping {

	private Map<String, BeanRef> beanRefs;
	private Map<String, FactoryRef> factoryRefs;
	
	public BeanMapping() {
		super();
		this.factoryRefs = new LinkedHashMap<String, FactoryRef>();
		this.beanRefs = new LinkedHashMap<String, BeanRef>();
	}

	public Collection<BeanRef> getBeanRefs() {
		return beanRefs.values();
	}
	
	public Collection<FactoryRef> getFactoryRefs() {
		return factoryRefs.values();
	}

	public void addFactoryRef(FactoryRef ref) {
		factoryRefs.put(ref.getName(), ref);
	}

	public void addBeanRef(BeanRef ref) {
		beanRefs.put(ref.getName(), ref);
	}

	public BeanRef getBeanRef(String name) {
		return beanRefs.get(name);
	}
	
	public FactoryRef getFactoryRef(String name) {
		return factoryRefs.get(name);
	}
	
	/**
	 * Replaces any beans matching those in the provided map
	 * @param mapping
	 */
	public void merge(BeanMapping mapping) {
		Collection<BeanRef> other = mapping.getBeanRefs();
		
		if(other != null) {
			for (BeanRef beanRef : other) {
				addBeanRef(beanRef);
			}
		}

		Collection<FactoryRef> otherFs = mapping.getFactoryRefs();
		
		if(otherFs != null) {
			for (FactoryRef factoryRef : otherFs) {
				addFactoryRef(factoryRef);
			}
		}
	}
}
