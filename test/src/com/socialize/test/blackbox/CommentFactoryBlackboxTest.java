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
package com.socialize.test.blackbox;

import com.socialize.entity.factory.CommentFactory;

/**
 * @author Jason Polites
 *
 */
public class CommentFactoryBlackboxTest extends AbstractFactoryBlackBoxTest {

	
	CommentFactory factory = null;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		factory = new CommentFactory();
	}

	/**
	 * [
		    { 
		        'entity': 'http://www.example.com/interesting-story/',
		        'text': 'this was a great story'
		    },
		    {
		        'entity': {
		            'key': 'http://www.example.com/another-story/',
		            'name': 'Another Interesting Story'
		        }
		    },
		    { 
		        'entity': 'http://www.example.com/interesting-story/',
		        'text': 'I did not think the story was that great'
		    },
		]
	 */
	public void testCreateRequest() {
		//Comment comment = new Comment();
	}
	
	public void testListByIdRequest() {
		
	}
	
	public void testListByEntityRequest(){
		
	}
	
	public void testListResponse(){
		
	}
	
	public void testGetResponse(){
		
	}
	
}
