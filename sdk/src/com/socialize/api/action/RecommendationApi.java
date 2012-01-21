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
package com.socialize.api.action;

import com.socialize.api.SocializeApi;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Entity;
import com.socialize.listener.entity.EntityListener;
import com.socialize.provider.SocializeProvider;

/**
 * @author Jason Polites
 *
 */
@Deprecated
public class RecommendationApi extends SocializeApi<Entity, SocializeProvider<Entity>> {
	
	public static final String ENDPOINT = "/recommendation/";

	public RecommendationApi(SocializeProvider<Entity> provider) {
		super(provider);
	}
	
	public void listRecommendedEntityesForLike(SocializeSession session, EntityListener listener, long id) {
		
//		Entity e0 = new Entity();
//		e0.setName("test");
//		e0.setKey("http://www.foobar.com");
//		
//		Entity e1 = new Entity();
//		e1.setName("test");
//		e1.setKey("http://www.foobar.com");
//		
//		ListResult<Entity> result = new ListResult<Entity>();
//		result.add(e0);
//		result.add(e1);
//		
//		listener.onList(result);
		
		String[] ids = new String []{String.valueOf(id)};
		listAsync(session, ENDPOINT + "entity/", null, ids, "like_id", 0, SocializeConfig.MAX_LIST_RESULTS, listener);
	}
}
