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
package com.socialize.listener.like;

import com.socialize.entity.Like;
import com.socialize.entity.ListResult;

import java.util.List;


/**
 * @author Jason Polites
 *
 */
public abstract class LikeListListener extends LikeListener {
 
	@Override
	public final void onCreate(Like entity) {}

	@Override
	public final void onGet(Like entity) {}

	@Override
	public final void onUpdate(Like entity) {}
	
	@Override
	public final void onDelete() {}
	
	@Override
	public final void onList(ListResult<Like> entities) {
		if(entities != null) {
			onList(entities.getItems(), entities.getTotalCount());
		}
		else {
			onList(null, 0);
		}
	}

	public abstract void onList(List<Like> items, int totalSize);

}
