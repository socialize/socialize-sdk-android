/*
 * Copyright (c) 2011 Socialize Inc* Permission is hereby granted, free of charge, to any person obtaining a copy
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
package com.socialize.api.action;

import java.util.ArrayList;
import java.util.List;

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.entity.Entity;
import com.socialize.listener.entity.EntityListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 *
 */
public class EntityApi extends SocializeApi<Entity, SocializeProvider<Entity>> {
	
	public static final String ENDPOINT = "/entity/";

	public EntityApi(SocializeProvider<Entity> provider) {
		super(provider);
	}
	
	public void createEntity(SocializeSession session, String key, String name, EntityListener listener) {
		Entity c = new Entity();
		c.setKey(key);
		c.setName(name);
		
		List<Entity> list = new ArrayList<Entity>(1);
		list.add(c);
		
		postAsync(session, ENDPOINT, list, listener);
	}
	
	public void getEntity(SocializeSession session, String key, EntityListener listener) {
		getAsync(session, ENDPOINT, key, listener);
	}

	public void listEntities(SocializeSession session, EntityListener listener, String...keys) {
		listAsync(session, ENDPOINT, null, keys, listener);
	}
	
}
