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
package com.socialize.demo.implementations.share;

import com.socialize.ShareUtils;
import com.socialize.demo.SDKDemoActivity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Share;
import com.socialize.error.SocializeException;
import com.socialize.listener.share.ShareGetListener;
import com.socialize.listener.share.ShareListListener;


/**
 * @author Jason Polites
 *
 */
public class GetSharesByIDActivity extends SDKDemoActivity {

	/* (non-Javadoc)
	 * @see com.socialize.demo.DemoActivity#executeDemo()
	 */
	@Override
	public void executeDemo(String text) {
		
		// We are going to list shares just so we can get the ID for a single share
		// Usually you would NOT do this as you would usually already have an ID (e.g. from a click on a list view)
		ShareUtils.getSharesByEntity(this, entity.getKey(), 0, 1, new ShareListListener() {
			
			@Override
			public void onList(ListResult<Share> shares) {
				
				// Use the id from the first share
				if(shares.getTotalCount() > 0) {
					ShareUtils.getShare(GetSharesByIDActivity.this, new ShareGetListener() {
						@Override
						public void onGet(Share share) {
							handleSocializeResult(share);
						}
						
						@Override
						public void onError(SocializeException error) {
							handleError(GetSharesByIDActivity.this, error);
						}
					}, shares.getItems().get(0).getId());
				}
			}
			
			@Override
			public void onError(SocializeException error) {
				handleError(GetSharesByIDActivity.this, error);
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
		return "Get Last Share by ID";
	}
}
