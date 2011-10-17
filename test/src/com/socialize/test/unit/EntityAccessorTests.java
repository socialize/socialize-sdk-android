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
package com.socialize.test.unit;

import com.socialize.api.action.ActionType;
import com.socialize.entity.Application;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.SocializeAction;
import com.socialize.entity.Stats;
import com.socialize.entity.User;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 *
 */
public class EntityAccessorTests extends SocializeUnitTest {

	public void testStatsEntityAccessors() {
		
		Stats stats = new Stats();
		
		Integer comments = 1;
		Integer id = 2;
		Integer likes = 3;
		Integer shares = 4;
		Integer views = 5;
		
		stats.setComments(comments);
		stats.setId(id);
		stats.setLikes(likes);
		stats.setShares(shares);
		stats.setViews(views);
		
		assertEquals(comments, stats.getComments());
		assertEquals(id, stats.getId());
		assertEquals(likes, stats.getLikes());
		assertEquals(shares, stats.getShares());
		assertEquals(views, stats.getViews());
	}
	
	public void testUserEntityAccessors() {
		
		User user = new User();
		Stats stats = new Stats();
		
		String description = "description";
		String firstName = "firstName";
		String largeImageUri = "largeImageUri";
		String location = "location";
		String mediumImageUri = "mediumImageUri";
		String smallImageUri = "smallImageUri";
		String username = "username";
		
		user.setDescription(description );
		user.setFirstName(firstName);
		user.setLargeImageUri(largeImageUri);
		user.setLocation(location);
		user.setMediumImageUri(mediumImageUri);
		user.setSmallImageUri(smallImageUri);
		user.setStats(stats);
		user.setUsername(username);
		
		assertEquals(description, user.getDescription());
		assertEquals(firstName, user.getFirstName());
		assertEquals(largeImageUri, user.getLargeImageUri());
		assertEquals(location, user.getLocation());
		assertEquals(mediumImageUri, user.getMediumImageUri());
		assertEquals(smallImageUri, user.getSmallImageUri());
		assertEquals(username, user.getUsername());
		assertEquals(stats, user.getStats());
	}
	
	public void testApplicationEntityAccessors() {
		Application application = new Application();
		String name = "name";
		application.setName(name );
		assertEquals(name, application.getName());
	}
	
	public void testCommentEntityAccessors() {
		Comment comment = new Comment();
		String text = "text";
		comment.setText(text );
		assertEquals(text, comment.getText());
	}
	
	public void testSocializeActionAccessors() {
		SocializeAction action = new SocializeAction() {

			@Override
			public ActionType getActionType() {
				return null;
			}

			@Override
			public String getDisplayText() {
				return null;
			}
		};
		
		Application app = new Application();
		Entity entity = new Entity();
		User user = new User();
		
		Long date = 123456l;
		Integer id = 69;
		Double lat = 1.2d, lon = 3.4d;
		
		action.setApplication(app);
		action.setDate(date);
		action.setEntity(entity);
		action.setId(id);
		action.setLat(lat);
		action.setLon(lon);
		action.setUser(user);
		
		assertSame(app, action.getApplication());
		assertSame(entity, action.getEntity());
		assertSame(user, action.getUser());
		assertEquals(date, action.getDate());
		assertEquals(id, action.getId());
		assertEquals(lat, action.getLat());
		assertEquals(lon, action.getLon());
		
	}
	
	public void testEntityAccessors() {
		
		Entity entity = new Entity();
		
		String name = "foo";
		String key = "bar";
		
		Integer comments = 1;
		Integer likes = 2;
//		Integer id = 3;
		Integer shares = 4;
		Integer views = 5;
		
		entity.setComments(comments );
//		entity.setId(id);
		entity.setKey(key);
		entity.setLikes(likes);
		entity.setName(name);
		entity.setShares(shares);
		entity.setViews(views);
		
		assertEquals(comments, entity.getComments());
		assertEquals(likes, entity.getLikes());
//		assertEquals(id, entity.getId());
		assertEquals(shares, entity.getShares());
		assertEquals(views, entity.getViews());
		assertEquals(name, entity.getName());
		assertEquals(key, entity.getKey());
	}
}
