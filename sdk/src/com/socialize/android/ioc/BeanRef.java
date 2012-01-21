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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author Jason Polites
 *
 */
public class BeanRef {

	private String name;
	private String className;
	
	private MethodRef initMethod;
	private MethodRef destroyMethod;
	
	private List<Argument> properties;
	private List<Argument> constructorArgs;
	
	private String extendsBean = null;
	
	private boolean singleton = true;
	private boolean abstractBean = false;
	
	private boolean initCalled = false;
	private boolean lazyInit = false;
	
	private boolean resolved = false;
	
	private boolean contextSensitive = false;
	
	private boolean contextSensitiveConstructor = false;
	private boolean contextSensitiveInitMethod = false;
	
	public void addConstructorArgument(Argument arg) {
		if(constructorArgs == null) constructorArgs = new LinkedList<Argument>();
		
		if(!constructorArgs.contains(arg)) {
			constructorArgs.add(arg);
		}
	}
	
	public void addProperty(Argument arg) {
		if(properties == null) properties = new LinkedList<Argument>();
		
		if(!properties.contains(arg)) {
			properties.add(arg);
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String type) {
		this.className = type;
	}

	public List<Argument> getProperties() {
		return properties;
	}
	
	protected void setProperties(List<Argument> properties) {
		this.properties = properties;
	}

	protected void setConstructorArgs(List<Argument> constructorArgs) {
		this.constructorArgs = constructorArgs;
	}

	public List<Argument> getConstructorArgs() {
		return constructorArgs;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	public MethodRef getInitMethod() {
		return initMethod;
	}

	public void setInitMethod(MethodRef initMethod) {
		this.initMethod = initMethod;
	}

	public MethodRef getDestroyMethod() {
		return destroyMethod;
	}

	public void setDestroyMethod(MethodRef destroyMethod) {
		this.destroyMethod = destroyMethod;
	}

	public String getExtendsBean() {
		return extendsBean;
	}

	public void setExtendsBean(String extendsBean) {
		this.extendsBean = extendsBean;
	}
	
	public boolean isAbstractBean() {
		return abstractBean;
	}
	public void setAbstractBean(boolean abstractBean) {
		this.abstractBean = abstractBean;
	}
	
	protected boolean isInitCalled() {
		return initCalled;
	}

	protected void setInitCalled(boolean initCalled) {
		this.initCalled = initCalled;
	}
	
	protected boolean isResolved() {
		return resolved;
	}

	protected void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	public boolean isContextSensitive() {
		return contextSensitive;
	}

	public void setContextSensitive(boolean contextSensitive) {
		this.contextSensitive = contextSensitive;
	}

	protected boolean isContextSensitiveConstructor() {
		return contextSensitiveConstructor;
	}

	protected void setContextSensitiveConstructor(boolean contextSensitiveConstructor) {
		this.contextSensitiveConstructor = contextSensitiveConstructor;
	}

	public boolean isContextSensitiveInitMethod() {
		return contextSensitiveInitMethod;
	}

	protected void setContextSensitiveInitMethod(boolean contextSensitiveInitMethod) {
		this.contextSensitiveInitMethod = contextSensitiveInitMethod;
	}

	@Deprecated
	public boolean isLazyInit() {
		return lazyInit;
	}

	@Deprecated
	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}
	
	public Set<Argument> getAllArguments() {
		
		Set<Argument> all = new HashSet<Argument>();
		
		if(properties != null) all.addAll(properties);
		if(constructorArgs != null) all.addAll(constructorArgs);
		
		if(initMethod != null) {
			if(initMethod.getArguments() != null) {
				all.addAll(initMethod.getArguments());
			}
		}
		
		if(destroyMethod != null) {
			if(destroyMethod.getArguments() != null) {
				all.addAll(destroyMethod.getArguments());
			}
		}
		
		
		Set<Argument> allChildren = new HashSet<Argument>();
		
		for (Argument argument : all) {
			List<Argument> children = argument.getChildren();
			
			while (children != null && !children.isEmpty()) {
				allChildren.addAll(children);
				for (Argument child : children) {
					children = child.getChildren();
				}
			}
		}
		
		all.addAll(allChildren);
		
		return all;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BeanRef other = (BeanRef) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
