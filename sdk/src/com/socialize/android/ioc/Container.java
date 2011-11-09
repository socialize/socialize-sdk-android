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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;

/**
 * @author Jason Polites
 */
public class Container {

	private Map<String, Object> beans;
	private BeanMapping mapping;
	private ContainerBuilder builder;
	
	private Context context; // Used only to track the current context.
	
	private boolean destroyed = false;
	
	// Parameterless constructor so it can be mocked.
	protected Container() {
		super();
		beans = new LinkedHashMap<String, Object>();
	}

	protected Container(BeanMapping mapping, ContainerBuilder builder) {
		this();
		this.mapping = mapping;
		this.builder = builder;
		this.context = builder.getContext();
	}
	
	protected BeanRef getBeanRef(String name) {
		return this.mapping.getBeanRef(name);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getBean(String name) {
		return (T) getBean(name, (Object[]) null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getBean(String name, Object...args) {
		if(beans != null) {
			Object bean = beans.get(name);
			BeanRef beanRef = mapping.getBeanRef(name);
			
			if(beanRef != null) {
				if(bean == null) {
					if(!beanRef.isSingleton()) {
						bean = builder.buildBean(this, beanRef, args);
						
						if(bean == null) {
							Logger.e(getClass().getSimpleName(), "Failed to instantiate non-singleton bean with name " + name);
						}
						else {
							builder.setBeanProperties(this, beanRef, bean);
							builder.initBean(this, beanRef, bean);
						}
					}
				}
				else if(beanRef.isLazyInit() && !beanRef.isInitCalled()) {
					builder.initBean(this, beanRef, bean);
				}	
			}
			else if(bean == null) { // might be a factory
				Logger.w(getClass().getSimpleName(), "No such bean with name " + name);
			}

			return (T) bean;
		}
		
		return null;
	}
	
	public boolean containsBean(String name) {
		return (beans == null) ? false : beans.containsKey(name);
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
		
		destroyed = true;
	}
	
	public boolean isDestroyed() {
		return destroyed;
	}
	
	protected void putBean(String name, Object bean) {
		
		Object old = beans.get(name);
		
		if(old != null) {
			Logger.i(getClass().getSimpleName(), "Replacing existing bean instance [" +
					name +
					"]");
			
			if(old instanceof ContainerAware) {
				((ContainerAware)old).onDestroy(this);
			}
		}
		
		beans.put(name, bean);
	}
	
	protected Map<String, Object> getBeans() {
		return beans;
	}
	
	protected BeanMapping getBeanMapping() {
		return mapping;
	}

	public void setContext(Context context) {
		
		if(!destroyed) {
			if(this.context != null && this.context != context) {
				// Set for any new beans
				builder.setContext(context);
				
				// Now look for existing singletons
				Collection<BeanRef> beanRefs = this.mapping.getBeanRefs();
				
				List<BeanRef> replaced = new LinkedList<BeanRef>();
				
				for (BeanRef ref : beanRefs) {
					
					if(ref.isSingleton() ) {
						if(ref.isContextSensitiveConstructor()) {
							// We have a new context, so we need to rebuild this bean
							Object bean = builder.buildBean(this, ref);
							builder.setBeanProperties(this, ref, bean);
							
							if(!ref.isLazyInit()) {
								builder.initBean(this, ref, bean);
							}
							
							putBean(ref.getName(), bean);
							
							// We need to check for any other objects who are referencing this object as a property or as a contructor argument.
							replaced.add(ref);
						}
						else if(ref.isContextSensitiveInitMethod()) {
							
							if(ref.isLazyInit()) {
								// Mark for re-init
								ref.setInitCalled(false);
							}
							else {
								// Re-call init
								Object bean = getBean(ref.getName());
								builder.initBean(this, ref, bean);
							}
						}
					}
				}
			}
			
			this.context = context;
		}
	}
}
