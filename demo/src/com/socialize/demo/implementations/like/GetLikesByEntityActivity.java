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
package com.socialize.demo.implementations.like;

import java.util.List;
import com.socialize.LikeUtils;
import com.socialize.demo.SDKDemoActivity;
import com.socialize.entity.Like;
import com.socialize.error.SocializeException;
import com.socialize.listener.like.LikeListListener;


/**
 * @author Jason Polites
 *
 */
public class GetLikesByEntityActivity extends SDKDemoActivity {

	/* (non-Javadoc)
	 * @see com.socialize.demo.DemoActivity#executeDemo()
	 */
	@Override
	public void executeDemo(String text) {
		LikeUtils.getLikesByEntity(this, entity.getKey(), 0, 50, new LikeListListener() {
			@Override
			public void onList(List<Like> items, int totalSize) {
				handleSocializeResult(items);
			}
			
			@Override
			public void onError(SocializeException error) {
				handleError(GetLikesByEntityActivity.this, error);
			}
		});
	}
	
	@Override
	public boolean isTextEntryRequired() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.socialize.demo.DemoActivity#getButtonText()
	 */
	@Override
	public String getButtonText() {
		return "List 50 Likes for Current Entity";
	}
}
