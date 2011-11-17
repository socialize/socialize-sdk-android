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
package com.socialize.ui.recommendation;

import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeApiHost;
import com.socialize.entity.Entity;
import com.socialize.entity.Like;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityListListener;
import com.socialize.ui.activity.SocializeActivityEntityView;

/**
 * @author Jason Polites
 */
public class LikeRecommendationProvider implements RecommendationProvider<Like, Entity> {
	
	private SocializeApiHost socializeApiHost;
	private IBeanFactory<SocializeActivityEntityView> socializeActivityEntityViewFactory;
	
	@Override
	public void loadActivity(Like item, RecommendationConsumer<Entity> consumer) {
		provide(item, consumer);
	}

	@Override
	public void provide(Like like, final RecommendationConsumer<Entity> consumer) {
		socializeApiHost.listRecommendedEntitiesByLike(Socialize.getSocialize().getSession(), new EntityListListener() {
			@Override
			public void onList(ListResult<Entity> entities) {
				if(entities != null) {
					int size = entities.size();
					if(size > 0 ) {
//						consumer.consume(entities.getItems());
					}
				}
			}
			
			@Override
			public void onError(SocializeException error) {
				// Do nothing
				error.printStackTrace();
				
			}
		}, like.getId());
	}

	public void setSocializeApiHost(SocializeApiHost socializeApiHost) {
		this.socializeApiHost = socializeApiHost;
	}
}
