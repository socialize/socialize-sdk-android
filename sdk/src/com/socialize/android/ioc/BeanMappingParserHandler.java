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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.socialize.android.ioc.Argument.CollectionType;
import com.socialize.android.ioc.Argument.RefType;

/**
 * @author Jason Polites
 */
public class BeanMappingParserHandler extends DefaultHandler {
	
	public static final String BEANS = "beans";
	public static final String BEAN = "bean";
	
	public static final String PROPERTY = "property";
	public static final String FACTORY = "factory";
	public static final String PROXY = "PROXY";
	
	public static final String INIT_METHOD = "init-method";
	public static final String DESTROY_METHOD = "destroy-method";
	public static final String ARG = "arg";
	public static final String REF = "ref";
	public static final String CONSTRUCTOR_ARG = "constructor-arg";
	
	public static final String LIST = "list";
	public static final String MAP = "map";
	public static final String SET = "set";
	
	public static final String MAP_KEY = "key";
	public static final String MAP_VALUE = "value";
	public static final String MAP_ENTRY = "entry";
	
	
	private BeanMapping beanMapping;
	
	private BeanRef currentBean = null;
	private Argument currentArg = null;
	private Argument currentMapEntry = null;
	
	private boolean inInitMethod = false;
	private boolean inDestroyMethod = false;
	
	private boolean inList = false;
	private boolean inSet = false;
	private boolean inMap = false;
	private boolean inMapEntry = false;
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(localName.equalsIgnoreCase(BEANS)) {
			beanMapping = new BeanMapping();
		}
		else if(localName.equalsIgnoreCase(BEAN)) {
			currentBean = new BeanRef();
			currentBean.setClassName(attributes.getValue("class"));
			currentBean.setName(attributes.getValue("id"));
			currentBean.setExtendsBean(attributes.getValue("extends"));
			
			String initMethod = attributes.getValue("initMethod");
			if(initMethod != null && initMethod.trim().length() > 0) {
				MethodRef initM = new MethodRef();
				initM.setName(initMethod);
				currentBean.setInitMethod(initM);
			}
			
			String singleton = attributes.getValue("singleton");
			
			if(singleton != null && singleton.trim().length() > 0) {
				currentBean.setSingleton(Boolean.parseBoolean(singleton));
			}
			else {
				currentBean.setSingleton(true);
			}
			
			String abstractBean = attributes.getValue("abstract");
			
			if(abstractBean != null && abstractBean.trim().length() > 0) {
				currentBean.setAbstractBean(Boolean.parseBoolean(abstractBean));
			}
			else {
				currentBean.setAbstractBean(false);
			}
			
			String lazy = attributes.getValue("lazy-init");
			
			if(lazy != null && lazy.trim().length() > 0) {
				currentBean.setLazyInit(Boolean.parseBoolean(lazy));
			}
			else {
				currentBean.setLazyInit(false);
			}
			
			beanMapping.addBeanRef(currentBean);
		}
		else if(localName.equalsIgnoreCase(CONSTRUCTOR_ARG)) {
			currentArg = getProperty(attributes);
			currentBean.addConstructorArgument(currentArg);
		}
		else if(localName.equalsIgnoreCase(ARG)) {
			if(inInitMethod) {
				currentArg = getProperty(attributes);
				if(currentBean.getInitMethod() != null) {
					currentBean.getInitMethod().addArgument(currentArg);
				}
			}
			else if(inDestroyMethod) {
				currentArg = getProperty(attributes);
				if(currentBean.getDestroyMethod() != null) {
					currentBean.getDestroyMethod().addArgument(currentArg);
				}
			}
			else {
				Logger.w(getClass().getSimpleName(), "Orphaned arg [" +
						attributes.getValue("name") +
						"].. ignoring");
			}
		}
		else if(localName.equalsIgnoreCase(PROPERTY)) {
			
			if(inList || inSet) {
				if(currentArg != null) {
					currentArg.addChild(getProperty(attributes));
				}
				else {
					Logger.w(getClass().getSimpleName(), "No current property for list or set property.  List/Set types can only be can only be declared within a <property>, <arg> or <constructor-arg> element");
				}
			}
			else {
				currentArg = getProperty(attributes);
				currentBean.addProperty(currentArg);
			}
		}
		else if(localName.equalsIgnoreCase(REF)) {
			
			if(inList || inSet) {
				if(currentArg != null) {
					currentArg.addChild(getProperty(attributes));
				}
				else {
					Logger.e(getClass().getSimpleName(), "No current argument found upon encountering a bean reference in a list. ");
				}
			}
			else {
				Logger.w(getClass().getSimpleName(), "No current list or set.  Ref types can only be can only be declared within a <list>, <set> element");
			}
		}
		else if(localName.equalsIgnoreCase(INIT_METHOD)) {
			inInitMethod = true;
			currentBean.setInitMethod(createMethod(attributes));
		}
		else if(localName.equalsIgnoreCase(DESTROY_METHOD)) {
			inDestroyMethod = true;
			currentBean.setDestroyMethod(createMethod(attributes));
		}
		else if(localName.equalsIgnoreCase(LIST)) {
			if(currentArg != null) {
				inList = true;
				currentArg.setType(RefType.LIST);
				setCollectionType(currentArg, attributes);
			}
			else {
				Logger.w(getClass().getSimpleName(), "No current argument for list element.  List types can only be can only be declared within a <property>, <arg> or <constructor-arg> element");
			}
		}
		else if(localName.equalsIgnoreCase(SET)) {
			if(currentArg != null) {
				inSet = true;
				currentArg.setType(RefType.SET);
				setCollectionType(currentArg, attributes);
			}
			else {
				Logger.w(getClass().getSimpleName(), "No current argument for set element.  Set types can only be can only be declared within a <property>, <arg> or <constructor-arg> element");
			}
		}
		else if(localName.equalsIgnoreCase(MAP)) {
			if(currentArg != null) {
				inMap = true;
				currentArg.setType(RefType.MAP);
				setCollectionType(currentArg, attributes);
			}
			else {
				Logger.w(getClass().getSimpleName(), "No current argument for map element.  Map types can only be can only be declared within a <property>, <arg> or <constructor-arg> element");
			}
		}
		else if(localName.equalsIgnoreCase(MAP_ENTRY)) {
			if(inMap) {
				if(currentArg != null && currentArg.getType().equals(RefType.MAP)) {
					inMapEntry = true;
					currentMapEntry = new Argument();
					currentMapEntry.setType(RefType.MAPENTRY);
					currentArg.addChild(currentMapEntry);
				}
				else {
					Logger.w(getClass().getSimpleName(), "No current argument for map property.  Map types can only be declared within a <property>, <arg> or <constructor-arg> element");
				}
			}
			else {
				Logger.w(getClass().getSimpleName(), "No current map element for map entry.  Map entry types can only be declared within a <map> element");
			}
		}
		else if(localName.equalsIgnoreCase(MAP_KEY)) {
			if(inMapEntry) {
				if(currentMapEntry != null) {
					Argument key = getProperty(attributes);
					key.setKey(MAP_KEY);
					currentMapEntry.addChild(key);
				}
				else {
					Logger.w(getClass().getSimpleName(), "No current map entry for key field.  Key types can only be declared within an <entry> element");
				}
			}
			else {
				Logger.w(getClass().getSimpleName(), "No current map element for map entry.  Map entry types can only be declared within a <map> element");
			}
		}
		else if(localName.equalsIgnoreCase(MAP_VALUE)) {
			if(inMapEntry) {
				if(currentMapEntry != null) {
					Argument value = getProperty(attributes);
					value.setKey(MAP_VALUE);
					currentMapEntry.addChild(value);
				}
				else {
					Logger.w(getClass().getSimpleName(), "No current map entry for key field.  Key types can only be declared within an <entry> element");
				}
			}
			else {
				Logger.w(getClass().getSimpleName(), "No current map element for map entry.  Map entry types can only be declared within a <map> element");
			}
		}
		else if(localName.equalsIgnoreCase(FACTORY)) {
			// We have a bean factory...
			// We expect an ID and beanName
			FactoryRef ref = new FactoryRef();
			ref.setMakes(attributes.getValue("makes"));
			ref.setName(attributes.getValue("id"));
			beanMapping.addFactoryRef(ref);
		}
		else if(localName.equalsIgnoreCase(PROXY)) {
			// We have a bean proxy...
			// We expect a bean ref
			beanMapping.addProxyRef(attributes.getValue(REF));
		}		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if(localName.equalsIgnoreCase(INIT_METHOD)) {
			inInitMethod = false;
		}
		else if(localName.equalsIgnoreCase(DESTROY_METHOD)) {
			inDestroyMethod = false;
		}
		else if(localName.equalsIgnoreCase(LIST)) {
			inList = false;
		}
		else if(localName.equalsIgnoreCase(SET)) {
			inSet = false;
		}
		else if(localName.equalsIgnoreCase(MAP)) {
			inMap = false;
		}
		else if(localName.equalsIgnoreCase(MAP_ENTRY)) {
			inMapEntry = false;
			currentMapEntry = null;
		}
	}

	private MethodRef createMethod(Attributes attributes) {
		String methodName = attributes.getValue("name");
		MethodRef method = null;
		if(methodName != null && methodName.trim().length() > 0) {
			method = new MethodRef();
			method.setName(methodName);
		}
		return method;
	}
	
	private void setCollectionType(Argument arg, Attributes attributes) {
		String type = attributes.getValue("type");
		
		if(type != null && type.trim().length() > 0) {
			try {
				CollectionType cType = CollectionType.valueOf(type.replaceAll("-", "").toUpperCase());
				
				// Verify
				if(isCollectionTypeValid(arg, attributes, cType)) {
					arg.setCollectionType(cType);
				}
				else {
					Logger.w(getClass().getSimpleName(), "Invalid collection type [" +
							type +
							"] for the argument of type [" +
							arg.getType() +
							"]");
					
					setDefaultCollectionType(arg, attributes);
				}
				
			}
			catch (Exception e) {
				Logger.w(getClass().getSimpleName(), "Invalid collection type [" +
						type +
						"]", e);
				
				
				setDefaultCollectionType(arg, attributes);
			}
		}
		else {
			setDefaultCollectionType(arg, attributes);
		}
	}
	
	private void setDefaultCollectionType(Argument arg, Attributes attributes) {
		switch (arg.getType()) {
			case LIST:
				arg.setCollectionType(CollectionType.LINKEDLIST);
				break;
				
			case SET:
				arg.setCollectionType(CollectionType.HASHSET);
				break;
	
			case MAP:
				arg.setCollectionType(CollectionType.HASHMAP);
				break;
				
			default:
				
				Logger.e(getClass().getSimpleName(), "Current argument [" +
						arg.getType() +
						"] is not a collection");
				
				break;
		}
	}
	
	private boolean isCollectionTypeValid(Argument arg, Attributes attributes, CollectionType type) {
		boolean valid = false;
		switch (arg.getType()) {
			case LIST:
				valid = (type.equals(CollectionType.LINKEDLIST) || type.equals(CollectionType.ARRAYLIST) || type.equals(CollectionType.STACK));
				break;
				
			case SET:
				valid = (type.equals(CollectionType.HASHSET) || type.equals(CollectionType.TREESET));
				break;
	
			case MAP:
				valid = (type.equals(CollectionType.HASHMAP) || type.equals(CollectionType.TREEMAP));
				break;

		}
		
		return valid;
	}
	
	private Argument getProperty(Attributes attributes) {
		Argument prop = new Argument();
		prop.setKey(attributes.getValue("name"));
		
		String value = attributes.getValue("value");
		String ref = attributes.getValue("ref");
		String id = attributes.getValue("id");
		
		if(ref != null && ref.trim().length() > 0) {
			prop.setValue(ref);
			prop.setType(RefType.BEAN);
		}
		else if(id != null && id.trim().length() > 0) {
			prop.setValue(id);
			prop.setType(RefType.BEAN);
		}
		else {
			prop.setValue(value);
			String type = attributes.getValue("type");
			
			if(type != null && type.trim().length() > 0) {
				type = type.trim().toUpperCase();
				prop.setType(RefType.valueOf(type));
			}
			else {
				prop.setType(RefType.STRING);
			}
		}
		return prop;
	}


	public BeanMapping getBeanMapping() {
		return beanMapping;
	}
}
