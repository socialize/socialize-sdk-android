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
package com.socialize.test.unit;

import java.util.List;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.entity.Stats;
import com.socialize.entity.User;
import com.socialize.entity.UserAuthData;
import com.socialize.test.SocializeUnitTest;

/**
 * @author Jason Polites
 * 
 */
public class UserTest extends SocializeUnitTest {

	public void testUserDisplayName() {

		String fname = "foo";
		String lname = "bar";
		String uname = "snafu";

		User user0 = new User();
		User user1 = new User();
		User user2 = new User();
		User user3 = new User();

		user0.setFirstName(fname);

		user1.setFirstName(fname);
		user1.setLastName(lname);

		user2.setLastName(lname);

		user3.setUsername(uname);

		assertEquals(fname, user0.getDisplayName());
		assertEquals(fname + " " + lname, user1.getDisplayName());
		assertEquals(lname, user2.getDisplayName());
		assertEquals(uname, user3.getDisplayName());

	}

	@SuppressWarnings("unchecked")
	@UsesMocks({ User.class, Stats.class, List.class })
	public void testMerge() {

		User mockUser = AndroidMock.createMock(User.class);
		User realUser = new User();
		Stats stats = AndroidMock.createMock(Stats.class);
		List<UserAuthData> authData = AndroidMock.createMock(List.class);

		final String meta = "foobar_meta";
		final String firstName = "foobar_firstName";
		final String lastName = "foobar_lastName";
		final String userName = "foobar_userName";
		final String location = "foobar_location";
		final String small_url = "foobar_small_url";
		final String medium_url = "foobar_medium_url";
		final String large_url = "foobar_large_url";
		final String picture = "foobar_picture";

		AndroidMock.expect(mockUser.getMetaData()).andReturn(meta);
		AndroidMock.expect(mockUser.getFirstName()).andReturn(firstName);
		AndroidMock.expect(mockUser.getLastName()).andReturn(lastName);
		AndroidMock.expect(mockUser.getUsername()).andReturn(userName);
		AndroidMock.expect(mockUser.getLocation()).andReturn(location);
		AndroidMock.expect(mockUser.getSmallImageUri()).andReturn(small_url);
		AndroidMock.expect(mockUser.getMediumImageUri()).andReturn(medium_url);
		AndroidMock.expect(mockUser.getLargeImageUri()).andReturn(large_url);
		AndroidMock.expect(mockUser.getProfilePicData()).andReturn(picture);

		AndroidMock.expect(mockUser.getStats()).andReturn(stats);
		AndroidMock.expect(mockUser.getAuthData()).andReturn(authData);

		AndroidMock.replay(mockUser);

		realUser.update(mockUser);

		AndroidMock.verify(mockUser);

		assertEquals(meta, realUser.getMetaData());
		assertEquals(firstName, realUser.getFirstName());
		assertEquals(lastName, realUser.getLastName());
		assertEquals(userName, realUser.getUsername());
		assertEquals(location, realUser.getLocation());
		assertEquals(small_url, realUser.getSmallImageUri());
		assertEquals(medium_url, realUser.getMediumImageUri());
		assertEquals(large_url, realUser.getLargeImageUri());
		assertEquals(picture, realUser.getProfilePicData());

		assertSame(stats, realUser.getStats());
		assertSame(authData, realUser.getAuthData());
	}

}
