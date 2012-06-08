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
package com.socialize.api.action.entity;

import android.app.Activity;
import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.listener.entity.EntityAddListener;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.entity.EntityListListener;


/**
 * @author Jason Polites
 *
 */
public class SocializeEntityUtils implements EntityUtilsProxy {
	
	private EntitySystem entitySystem;
	
	@Override
	public void getEntity(Activity context, long id, EntityGetListener listener) {
		entitySystem.getEntity(Socialize.getSocialize().getSession(), id, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.entity.EntityUtilsProxy#addEntity(android.app.Activity, com.socialize.entity.Entity, com.socialize.listener.entity.EntityAddListener)
	 */
	@Override
	public void addEntity(Activity context, Entity e, EntityAddListener listener) {
		entitySystem.addEntity(Socialize.getSocialize().getSession(), e, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.entity.EntityUtilsProxy#getEntity(android.app.Activity, java.lang.String, com.socialize.listener.entity.EntityGetListener)
	 */
	@Override
	public void getEntity(Activity context, String key, EntityGetListener listener) {
		entitySystem.getEntity(Socialize.getSocialize().getSession(), key, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.entity.EntityUtilsProxy#getEntities(android.app.Activity, int, int, com.socialize.listener.entity.EntityListListener)
	 */
	@Override
	public void getEntities(Activity context, int start, int end, EntityListListener listener) {
		entitySystem.getAllEntities(Socialize.getSocialize().getSession(), start, end, listener);
	}

	/* (non-Javadoc)
	 * @see com.socialize.api.action.entity.EntityUtilsProxy#getEntities(android.app.Activity, int, int, com.socialize.listener.entity.EntityListListener, java.lang.String[])
	 */
	@Override
	public void getEntities(Activity context, EntityListListener listener, String... keys) {
		entitySystem.getEntities(Socialize.getSocialize().getSession(), listener, keys);
	}

	public void setEntitySystem(EntitySystem entitySystem) {
		this.entitySystem = entitySystem;
	}
}
