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
 * @author Jason Polites
 */
public class Container {

	private Map<String, Object> beans;
	private BeanMapping mapping;
	private ContainerBuilder builder;
	
	// Parameterless constructor so it can be mocked.
	protected Container() {
		super();
		beans = new LinkedHashMap<String, Object>();
	}

	protected Container(BeanMapping mapping, ContainerBuilder builder) {
		this();
		this.mapping = mapping;
		this.builder = builder;
	}
	
	protected BeanRef getBeanRef(String name) {
		return this.mapping.getBeanRef(name);
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T getBean(String name) {
		Object bean = beans.get(name);
		if(bean == null) {
			BeanRef beanRef = mapping.getBeanRef(name);
			if(beanRef != null) {
				if(!beanRef.isSingleton()) {
					bean = builder.buildBean(this, beanRef);
					builder.setBeanProperties(this, beanRef, bean);
					builder.initBean(this, beanRef, bean);
				}
			}
			else {
				Logger.e(getClass().getSimpleName(), "No such bean with name " + name);
			}
			
		}
		return (T) bean;
	}
	
	public boolean containsBean(String name) {
		return beans.containsKey(name);
	}
	
	public int size() {
		return (beans == null) ? 0 : beans.size();
	}
	
	/**
	 * Destroys the container and calls destroy on any beans with a destroy method.
	 */
	public void destroy() {
		if(mapping != null) {
			Collection<BeanRef> beanRefs = mapping.getBeanRefs();

			if(beanRefs != null) {
				for (BeanRef beanRef : beanRefs) {
					Object bean = beans.get(beanRef.getName());
					if(bean != null) {
						builder.destroyBean(this, beanRef, bean);
					}
				}
				
				beanRefs.clear();
				beanRefs = null;
			}
		}
		
		if(beans != null) {
			beans.clear();
			beans = null;
		}
	}
	
	protected void putBean(String name, Object bean) {
		beans.put(name, bean);
	}
	
	protected Map<String, Object> getBeans() {
		return beans;
	}
	
	protected BeanMapping getBeanMapping() {
		return mapping;
	}
}
