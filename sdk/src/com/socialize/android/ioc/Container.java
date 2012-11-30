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

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.content.Context;

/**
 * @author Jason Polites
 */
public class Container {

	protected static Map<String, Object> staticProxies = new HashMap<String, Object>();
	protected static Map<String, Object> staticStubs = new HashMap<String, Object>();
	protected static Set<String> staticProxiesRemoved = new HashSet<String>();
	protected static BeanContextCache beanContextCache = new BeanContextCache();
	
	private Map<String, Object> beans;
	private Map<String, ProxyObject<?>> proxies;
	private BeanMapping mapping;
	private ContainerBuilder builder;
	
	private Context context; // Used only to track the current context.
	
	private boolean destroyed = false;
	
	// Parameterless constructor so it can be mocked.
	protected Container() {
		super();
		beans = new LinkedHashMap<String, Object>();
		proxies = new LinkedHashMap<String, ProxyObject<?>>();
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
	
	protected static void registerProxy(String name, Object proxy) {
		staticProxies.put(name, proxy);
	}
	
	protected static void unregisterProxy(String name) {
		staticProxies.remove(name);
		staticProxiesRemoved.add(name);
	}	
	
	protected static void clearStubs() {
		staticStubs.clear();
	}
	
	protected static void registerStub(String name, Object stub) {
		staticStubs.put(name, stub);
	}
	
	protected static void unregisterStub(String name) {
		staticStubs.remove(name);
	}		
	
	public <T extends Object> void setStaticRuntimeProxy(String name, T bean) {
		setRuntimeProxyInternal(name, bean, true);
	}
	
	public <T extends Object> void setRuntimeProxy(String name, T bean) {
		setRuntimeProxyInternal(name, bean, false);
	}
	
	protected <T extends Object> void setRuntimeProxyInternal(String name, T bean, boolean isStatic) {
		
		BeanRef beanRef = getBeanRef(name);
		
		if(beanRef != null) {
			
			if(!beanRef.isSingleton() || isStatic) {
				if(this.mapping.hasProxy(name)) {
					Logger.w(getClass().getSimpleName(), "Proxy already defined for bean [" +
							name +
							"].  It will be replaced");
				}
				
				mapping.addProxyRef(name);
				
				ProxyObject<T> proxy = new ProxyObject<T>();
				proxy.setDelegate(bean);
				proxy.setStaticProxy(isStatic);
				
				proxies.put(name, proxy);
			}
			else {
				Logger.w(getClass().getSimpleName(), "Bean [" +
						name +
						"] is a singleton bean and cannot be proxied at runtime");
			}
		}
		else {
			Logger.e(getClass().getSimpleName(), "No bean defined with name [" +
					name +
					"].  Proxy cannot be created");
		}
	}
	
	public <T extends Object> ProxyObject<T> getProxy(String name) {
		return getProxy(name,  (Object[]) null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> ProxyObject<T> getProxy(String name, Object...args) {
		
		if(this.mapping.hasProxy(name)) {
			
			BeanRef beanRef = mapping.getBeanRef(name);
			
			if(beanRef != null) {
				if(beanRef.isAbstractBean()) {
					Logger.w(getClass().getSimpleName(), "Cannot proxy abstract bean [" +
							name +
							"]");
				}
				else {
					T bean = getBeanInternal(name, args);
					
					if(bean != null) {
						
						ProxyObject<T> proxy = null;
						
						proxy = (ProxyObject<T>) proxies.get(name);
						
						if(proxy == null) {
							proxy = new ProxyObject<T>();
							proxy.setDelegate(bean);
							
//							if(beanRef.isSingleton()) {
								proxies.put(name, proxy);
//							}					
						}
						else if(!beanRef.isSingleton() && !proxy.isStaticProxy()) {
							proxy.setDelegate(bean);
						}
						
						return proxy;				
					}
					else {
						Logger.w(getClass().getSimpleName(), "No bean with name [" +
								name +
								"] found when attempting to proxy.");
					}
				}
			}
			else {
				Logger.w(getClass().getSimpleName(), "Bean [" +
						name +
						"] does not exist and therefore cannot be proxied.  Make sure <proxy> elements exist AFTER the definition of this bean.");
			}
		}
		else {
			Logger.w(getClass().getSimpleName(), "Bean [" +
					name +
					"] does not define a proxy.  A <proxy> element must exist in config for this bean to be proxied");
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> void getBeanAsync(String name, BeanCreationListener<T> listener, Object... args) {
		AsyncBeanRequest<T> request = new AsyncBeanRequest<T>();
		request.setName(name);
		request.setArgs(args);
		request.setBeanCreationListener(listener);
		
		AsyncBeanRetriever<T> retriever = new AsyncBeanRetriever<T>(this);
		retriever.execute(request);
	}
	
	public <T> void getBeanAsync(String name, BeanCreationListener<T> listener) {
		getBeanAsync(name, listener, (Object[]) null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T getBean(String name) {
		return (T) getBean(name, (Object[]) null);
	}
	
	@SuppressWarnings("unchecked")
	public<T extends Object> T getBean(String name, Object...args) {
		return (T) beanContextCache.get(this, name, args);
	}
	
	@SuppressWarnings("unchecked")
	<T extends Object> T getBeanLocal(String name, Object...args) {
		
		if(staticStubs.containsKey(name)) {
			return (T) staticStubs.get(name);
		}
		
		if(mapping.hasProxy(name)) {
			if(staticProxiesRemoved.contains(name)) {
				mapping.removeProxyRef(name);
				staticProxiesRemoved.remove(name);
				return getBeanInternal(name, args);
			}
			else {
				return getBeanProxy(name, args);
			}
		}
		else {
			return getBeanInternal(name, args);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Object> T getBeanProxy (String name, Object...args) {
		ProxyObject<T> proxy = getProxy(name, args);
		BeanRef beanRef = mapping.getBeanRef(name);

		try {
			if(beanRef != null) {
				Class<T> beanClass = (Class<T>) Class.forName(beanRef.getClassName());
				
				Class<?>[] interfaces = getAllInterfacesAsArray(beanClass);
				
				if(interfaces == null || interfaces.length == 0) {
					Logger.w(getClass().getSimpleName(), "Bean [" +
							name +
							"] does not declare an interface.  Only beans with interfaces can be proxied");
				}
				else {
					return (T) Proxy.newProxyInstance(
							beanClass.getClassLoader(),
							interfaces,
							proxy);		
				}
			}
			else {
				Logger.w(getClass().getSimpleName(), "No bean with name [" +
						name +
						"] found when attempting to proxy.");
			}
		}
		catch (Exception e) {
			Logger.e(getClass().getSimpleName(), "Failed to create proxy for bean class [" +
					beanRef.getClassName() +
					"]", e);
		}
		
		return getBeanInternal(name, args);
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Object> T getBeanInternal(String name, Object...args) {
		args = cleanNulls(name, args);
		
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
			}
			else if(bean == null) { // might be a factory
				Logger.w(getClass().getSimpleName(), "No such bean with name " + name);
			}

			return (T) bean;
		}
		
		return null;
	}
	
	protected Object[] cleanNulls(String beanName, Object[] args) {
		
		if(args != null) {
			Object[] nonNull = null;
			int nullCount = 0;
			
			for (Object object : args) {
				if(object == null) {
					nullCount++;
				}
			}
			
			if(nullCount > 0) {
				
				Logger.i(getClass().getSimpleName(), "Some arguments passed to getBean were null for bean [" +
						beanName +
						"].  Stripping nulls from argument list");				
				
				if(nullCount < args.length) {
					nonNull = new Object[args.length - nullCount];
					int i = 0;
					
					for (Object object : args) {
						if(object != null) {
							nonNull[i] = object;
							i++;
						}
					}
					
					return nonNull;
				}
				else {
					return null;
				}
			}
		}	
		
		return args;
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

			try {
				if(beanRefs != null) {
					for (BeanRef beanRef : beanRefs) {
						Object bean = beans.get(beanRef.getName());
						if(bean != null) {
							builder.destroyBean(this, beanRef, bean);
						}
					}
				}
			}
			finally {
				mapping.clear();
			}
		}
		
		if(beans != null) {
			beans.clear();
			beans = null;
		}
		
		if(proxies != null) {
			proxies.clear();
			proxies = null;
		}	
		
		staticProxies.clear();
		staticStubs.clear();
		staticProxiesRemoved.clear();
		BeanMappingCache.destroy();
		
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
	
	public void onContextDestroyed(Context context) {
		if(beanContextCache != null) {
			beanContextCache.onContextDestroyed(context);
		}
	}

	public void setContext(Context context) {
		
		beanContextCache.setContext(context);
		
		if(!destroyed) {
			
			if(this.context != null && this.context != context) {
				
				// Clear context cache
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
							
//							if(!ref.isLazyInit()) {
								builder.initBean(this, ref, bean);
//							}
							
							putBean(ref.getName(), bean);
							
							// We need to check for any other objects who are referencing this object as a property or as a contructor argument.
							replaced.add(ref);
						}
						else if(ref.isContextSensitiveInitMethod()) {
							
//							if(ref.isLazyInit()) {
//								// Mark for re-init
//								ref.setInitCalled(false);
//							}
//							else {
								// Re-call init
								Object bean = getBean(ref.getName());
								builder.initBean(this, ref, bean);
//							}
						}
					}
				}
			}
			
			this.context = context;
		}
	}
	
	public Context getContext() {
		return context;
	}

	protected Class<?>[] getAllInterfacesAsArray(Class<?> cls) {
		List<Class<?>> interfaces = getAllInterfaces(cls);
		if(interfaces != null) {
			return interfaces.toArray(new Class<?>[interfaces.size()]);
		}
		return null;
	}
	

	protected List<Class<?>> getAllInterfaces(Class<?> cls) {
		if (cls == null) {
			return null;
		}

		LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<Class<?>>();
		getAllInterfaces(cls, interfacesFound);

		return new ArrayList<Class<?>>(interfacesFound);
	}

	private void getAllInterfaces(Class<?> cls, HashSet<Class<?>> interfacesFound) {
		while (cls != null) {
			Class<?>[] interfaces = cls.getInterfaces();

			for (Class<?> i : interfaces) {
				if (interfacesFound.add(i)) {
					getAllInterfaces(i, interfacesFound);
				}
			}

			cls = cls.getSuperclass();
		}
	}	
}
