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

import java.io.IOException;
import java.io.InputStream;
import com.socialize.test.SocializeActivityTest;
import com.socialize.test.ui.util.TestUtils;
import com.socialize.util.IOUtils;

/**
 * @author Jason Polites
 *
 */
public abstract class AbstractFactoryBlackBoxTest extends SocializeActivityTest {

	protected static final String JSON_REQUEST = "json/requests/";
	protected static final String JSON_RESPONSE = "json/responses/";
	
	protected static final String JSON_REQUEST_COMMENT_CREATE = JSON_REQUEST + "comment_create.json";
	protected static final String JSON_REQUEST_COMMENT_LIST_BY_ID = JSON_REQUEST + "comment_retrieve_list_by_ids.json";
	protected static final String JSON_REQUEST_COMMENT_LIST_BY_ENTITY = JSON_REQUEST + "comment_retrieve_list.json";
	protected static final String JSON_RESPONSE_LIST = JSON_RESPONSE + "comment_list_response.json";
	protected static final String JSON_RESPONSE_GET = JSON_RESPONSE + "comment_single_response.json";
	
	/**
	 * Gets the JSON string from the set of sample data based on filename.
	 * @param filename
	 * @return
	 * @throws IOException 
	 */
	protected String getSampleJSON(String filename) throws Throwable {
		
		
		String json = null;
		
		InputStream in = null;
		IOUtils utils = new IOUtils();
		try {
			in = TestUtils.getActivity(this).getAssets().open(filename);
			json = utils.read(in);
		}
		finally {
			if(in != null) in.close();
		}
		return json;
	}
}
