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

import com.socialize.api.SocializeSession;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityListener;


/**
 * @author Jason Polites
 *
 */
public interface EntitySystem {
	
	public static final String ENDPOINT = "/entity/";

	public void addEntity(SocializeSession session, Entity entity, EntityListener listener);
	
	/**
	 * Gets a socialize entity.  This call is SYNCHRONOUS!  Do not call from the main UI thread.
	 * @param session
	 * @param id
	 * @throws SocializeException
	 * @return
	 */
	public Entity getEntitySynchronous(SocializeSession session, long id) throws SocializeException;		
	
	public void getEntity(SocializeSession session, long id, EntityListener listener);

	
	public void getEntity(SocializeSession session, String entityKey, EntityListener listener);

	public void getEntities(SocializeSession session, int start, int end, EntityListener listener, String... entityKeys);
	
	public void getAllEntities(SocializeSession session, int start, int end, EntityListener listener);
	
	public void getEntities(SocializeSession session, EntityListener listener, String... entityKeys);

}