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

import java.util.LinkedList;
import java.util.List;

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

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public boolean isContextSensitive() {
		return contextSensitive;
	}

	@Deprecated
	protected void setContextSensitive(boolean contextSensitive) {
		this.contextSensitive = contextSensitive;
	}

	public boolean isContextSensitiveConstructor() {
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
	
	
}
