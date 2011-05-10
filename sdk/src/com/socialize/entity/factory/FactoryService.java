package com.socialize.entity.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.socialize.config.SocializeConfig;
import com.socialize.entity.Application;
import com.socialize.entity.Comment;
import com.socialize.entity.SocializeObject;

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
					if(strKey.startsWith("factory.")) {
						// Get the class name
						String className = strKey.substring("factory.".length(), strKey.length());
						String factoryClass = props.getProperty(strKey);
						
						// Instantiate
						SocializeObjectFactory<?> factory = (SocializeObjectFactory<?>) Class.forName(factoryClass).newInstance();
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
