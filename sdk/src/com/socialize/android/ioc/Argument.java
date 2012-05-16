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

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Jason Polites
 *
 */
public final class Argument {

	public static enum RefType {
		UNDEFINED,
		NULL,
		BEAN, 
		CONTEXT,
		ACTIVITY,
		SHORT, 
		INTEGER, 
		LONG, 
		STRING, 
		CHAR, 
		BYTE,
		FLOAT,
		DOUBLE,
		BOOLEAN,
		LIST,
		MAP,
		MAPENTRY,
		SET};
			
	public static enum CollectionType {
		LINKEDLIST,
		ARRAYLIST,
		VECTOR,
		STACK,
		HASHMAP,
		TREEMAP,
		HASHSET,
		TREESET
	}
	
	private String key;
	private String value;
	private RefType type = RefType.UNDEFINED;
	private CollectionType collectionType;
	
	private List<Argument> children;
	
	public Argument() {
		super();
	}
	
	public Argument(String key,String value,RefType type) {
		super();
		this.key = key;
		this.value = value;
		this.type = type;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public RefType getType() {
		return type;
	}
	public void setType(RefType type) {
		this.type = type;
	}
	public CollectionType getCollectionType() {
		return collectionType;
	}
	public void setCollectionType(CollectionType collectionType) {
		this.collectionType = collectionType;
	}
	public List<Argument> getChildren() {
		return children;
	}
	public synchronized void addChild(Argument arg) {
		if(children == null) children = new LinkedList<Argument>();
		children.add(arg);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Argument other = (Argument) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
