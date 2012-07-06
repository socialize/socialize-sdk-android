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
package com.socialize.test.blackbox;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.api.DefaultSocializeRequestFactory;
import com.socialize.api.SocializeSession;
import com.socialize.entity.ApplicationFactory;
import com.socialize.entity.Comment;
import com.socialize.entity.CommentFactory;
import com.socialize.entity.Entity;
import com.socialize.entity.EntityFactory;
import com.socialize.entity.UserFactory;
import com.socialize.error.SocializeException;
import com.socialize.oauth.OAuthRequestSigner;
import com.socialize.oauth.OAuthSignListener;
import com.socialize.test.util.JsonAssert;

/**
 * @author Jason Polites
 *
 */
public class CommentFactoryBlackboxTest extends AbstractFactoryBlackBoxTest {

	
	CommentFactory commentFactory = null;
	DefaultSocializeRequestFactory<Comment> requestFactory;
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		commentFactory = new CommentFactory();
		commentFactory.setApplicationFactory(new ApplicationFactory());
		commentFactory.setEntityFactory(new EntityFactory());
		commentFactory.setUserFactory(new UserFactory());
		
		requestFactory = new DefaultSocializeRequestFactory<Comment>(new OAuthRequestSigner() {
			
			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request) throws SocializeException {
				return request;
			}

			@Override
			public <R extends HttpUriRequest> R sign(SocializeSession session, R request, OAuthSignListener listener) throws SocializeException {
				return request;
			}

			@Override
			public <R extends HttpUriRequest> R sign(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret, R request, OAuthSignListener listener) throws SocializeException {
				return request;
			}
			
		}, commentFactory);
	}
	
	@UsesMocks ({SocializeSession.class})
	public void testCreateRequest() throws Throwable {
		
		SocializeSession session = AndroidMock.createMock(SocializeSession.class);
		final String endPoint = "foobar";
		
		Comment comment0 = new Comment();
		Comment comment1 = new Comment();
		Comment comment2 = new Comment();
		
		comment0.setEntityKey("http://www.example.com/interesting-story/");
		comment0.setText("this was a great story");
		
		
		Entity entity0 = new Entity();
		entity0.setKey("http://www.example.com/another-story/");
		entity0.setName("Another Interesting Story");
		
		comment1.setEntity(entity0);
		comment1.setText("Another comment");
		
		comment2.setEntityKey("http://www.example.com/interesting-story/");
		comment2.setText("I did not think the story was that great");
		
		List<Comment> objects = new ArrayList<Comment>();
		objects.add(comment0);
		objects.add(comment1);
		objects.add(comment2);
		
		HttpUriRequest request = requestFactory.getPostRequest(session, endPoint, objects);
		
		assertTrue(request instanceof HttpEntityEnclosingRequest);
		
		HttpEntityEnclosingRequest eReq = (HttpEntityEnclosingRequest) request;
		
		HttpEntity entity = eReq.getEntity();
		
		assertNotNull(entity);
		
		assertTrue(entity instanceof UrlEncodedFormEntity);
		
		List<NameValuePair> parsed = URLEncodedUtils.parse(entity);
		
		assertEquals(1, parsed.size());
		
		NameValuePair nvp = parsed.get(0);
		
		assertEquals("payload", nvp.getName());
		
		String strActual = nvp.getValue();
		String strExpected = getSampleJSON(JSON_REQUEST_COMMENT_CREATE);
		
		JSONArray actual = new JSONArray(strActual);
		JSONArray expected = new JSONArray(strExpected);
		
		JsonAssert.assertJsonArrayEquals(expected, actual);
	}
}
