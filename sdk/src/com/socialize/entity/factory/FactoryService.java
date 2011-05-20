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
package com.socialize.entity.factory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.socialize.config.SocializeConfig;
import com.socialize.entity.Application;
import com.socialize.entity.Comment;
import com.socialize.entity.SocializeObject;

/**
 * @author Jason Polites
 *
 */
public class FactoryService {

	private final Map<String, SocializeObjectFactory<?>> factories = new HashMap<String, SocializeObjectFactory<?>>();
	
	public FactoryService(SocializeConfig config) {
		super();
		
		Properties props = config.getProperties();
		
		try {
			if(props != null) {
				Set<Object> keySet = props.keySet();
				
				for (Object key : keySet) {
					String strKey = key.toString();
					if(strKey.startsWith(SocializeConfig.FACTORY_PREFIX)) {
						// Get the class name
						String className = strKey.substring(SocializeConfig.FACTORY_PREFIX.length(), strKey.length());
						String factoryClass = props.getProperty(strKey);
						
						// Instantiate
						Class<?> clsFactory = Class.forName(factoryClass);
						Constructor<?> constructor = clsFactory.getDeclaredConstructor(FactoryService.class);
						SocializeObjectFactory<?> factory = (SocializeObjectFactory<?>) constructor.newInstance(this);
						factory.factoryService = this;
						
						factories.put(className, factory);
					}
				}
			}
			else {
				// TODO: Log warn
				// Use predefined settings
				initDefaultFactories();
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends SocializeObject, F extends SocializeObjectFactory<T>> F getFactoryFor(Class<T> clazz) {
		F factory = (F) factories.get(clazz.getName());
		return factory;
	}
	
	void initDefaultFactories() {
		// TODO: Add remaining factories
		factories.put(Application.class.getName(), new ApplicationFactory(this));
		factories.put(Comment.class.getName(), new CommentFactory(this));
	}

	public Map<String, SocializeObjectFactory<?>> getFactories() {
		return factories;
	}
	
}
