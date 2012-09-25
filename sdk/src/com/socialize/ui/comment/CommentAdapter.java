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
package com.socialize.ui.comment;

import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.socialize.Socialize;
import com.socialize.UserUtils;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Comment;
import com.socialize.entity.User;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.image.ImageLoader;
import com.socialize.ui.util.DateUtils;
import com.socialize.ui.view.CachedImageView;
import com.socialize.ui.view.ListItemLoadingView;
import com.socialize.util.CacheableDrawable;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import com.socialize.util.SafeBitmapDrawable;
import com.socialize.util.StringUtils;

/**
 * Provides comments to the comment view.
* @author Jason Polites
 */
public class CommentAdapter extends BaseAdapter {

	private IBeanFactory<CommentListItem> commentItemViewFactory;
	private IBeanFactory<ListItemLoadingView> listItemLoadingViewFactory;
	private List<Comment> comments;
	private SocializeLogger logger;
	private Drawables drawables;
	private View loadingView;
	private DisplayUtils displayUtils;
	private DateUtils dateUtils;
	private boolean last = false;
	private ImageLoader imageLoader;
	private Activity context;
	private Date now;

	private int totalCount = 0;
	
	private int iconSize = 64;
	
	private int count = 0;

	public void init(Activity context) {
		this.context = context;
		now = new Date();
	}
	
	public void reset() {
		if(comments != null) {
			comments.clear();
		}
		last = false;
		now = new Date();
	}

	@Override
	public int getCount() {
		return count;
	}

	public boolean isDisplayLoading() {
		return !last && (comments != null && comments.size() > 0);
	}

	@Override
	public Object getItem(int position) {
		if(comments != null && position < comments.size()) {
			return comments.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if(!isDisplayLoading() || position < comments.size()) {
			return 0;
		}
		else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		if(!isDisplayLoading()) {
			return 1;
		}
		else {
			return 2;
		}
	}

	@Override
	public View getView(final int position, View oldView, ViewGroup parent) {

		View returnView = null;
		CommentListItem view = null;
		
		if(oldView instanceof CommentListItem) {
			view = (CommentListItem) oldView;
		}
		
		User tmpUser = null;

		final Comment item = (Comment) getItem(position);

		if(item != null) {
			tmpUser = item.getUser();
		}

		User currentUser = Socialize.getSocialize().getSession().getUser();

		if(currentUser != null && tmpUser != null && currentUser.getId().equals(tmpUser.getId())) {
			// Use this user as we may have been updated
			tmpUser = currentUser;
		}

		final User user = tmpUser;

		if (view == null || !imageLoader.isEmpty()) {
			view = commentItemViewFactory.getBean();
		} 

		if(view != null) {
			returnView = view;
			
			if(position >= comments.size()) {
				// Last one, get loading view
				if(loadingView == null) {
					loadingView = listItemLoadingViewFactory.getBean();
					loadingView.setTag(null);
				}

				returnView = loadingView;
			}
			else {

				if(item != null) {
					
					String displayName = "";
					String imageUrl = null;
					
					if(user != null) {
						imageUrl = user.getSmallImageUri();
						displayName = user.getDisplayName();
						
						if(displayName == null) {
							// Use the item user
							displayName = item.getUser().getDisplayName();

							if(displayName == null) {
								displayName = "Anonymous";
							}
						}
						
						view.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								if(user != null && user.getId() != null) {
									UserUtils.showUserProfileWithAction(context, user, item);
								}
								else {
									if(logger != null) {
										logger.warn("No user for comment " + item.getId());
									}
								}
							}
						});						
					}
					else {
						displayName = "Anonymous";
					}

					TextView comment = view.getComment();
					TextView userName = view.getAuthor();
					TextView time = view.getTime();
					ImageView locationIcon = view.getLocationIcon();
					final CachedImageView userIcon = view.getUserIcon();

					if (comment != null) {
						comment.setText(item.getText());
					}

					if (userName != null) {
						userName.setText(displayName);
					}
					
					if(locationIcon != null) {
						if(item.hasLocation() && item.isLocationShared()) {
							locationIcon.setVisibility(View.VISIBLE);
						}
						else {
							locationIcon.setVisibility(View.GONE);
						}
					}

					if (time != null) {
						Long date = item.getDate();
						if(date != null && date > 0) {
							long diff = (now.getTime() - date.longValue());
							time.setText(dateUtils.getTimeString(diff) + " ");
						}
						else {
							time.setText(" ");
						}
					}

					if (userIcon != null && drawables != null) {

						int densitySize = displayUtils.getDIP(iconSize);

						if(user != null) {
							userIcon.getBackground().setAlpha(255);

							if(!StringUtils.isEmpty(imageUrl)) {
								
								try {
									// Check the cache
									CacheableDrawable cached = drawables.getCache().get(imageUrl);

									if(cached != null && !cached.isRecycled()) {
										if(logger != null && logger.isDebugEnabled()) {
											logger.debug("CommentAdpater setting image icon to cached image " + cached);
										}
										
										userIcon.setImageName(imageUrl);
									}
									else {
										userIcon.setDefaultImage();
										imageLoader.loadImageByUrl(imageUrl, densitySize, densitySize, userIcon);										
									}
								}
								catch (Exception e) {
									String errorMsg = "Not a valid image uri [" + imageUrl + "]";
									logError(errorMsg, e);
									userIcon.setDefaultImage();
								}
							}
							else if(!StringUtils.isEmpty(user.getProfilePicData())) {
								userIcon.setDefaultImage();
								imageLoader.loadImageByData("user_" + user.getId(), user.getProfilePicData(),  densitySize, densitySize, userIcon);		
							}
							else {
								userIcon.setDefaultImage();
							}
						}
					}
				}
			}
		}

		if(returnView == null) {
			returnView = oldView;
		}

		return returnView;
	}
	
	protected void setImageIcon(User user, int position, ImageView userIcon, SafeBitmapDrawable drawable, Drawable defaultImage) {
		
		try {
			if(logger != null && logger.isDebugEnabled()) {
				logger.debug("CommentAdapter setting image icon [" +
						userIcon +
						"] at row [" +
						position +
						"] for user [" +
						user.getId() +
						"] to " + drawable);
			}		
			
			userIcon.setImageDrawable(drawable);
			userIcon.getBackground().setAlpha(255);
		}
		catch (Exception e) {
			logError("Error setting user icon image", e);
			try {
				userIcon.setImageDrawable(defaultImage);
				userIcon.getBackground().setAlpha(255);
			}
			catch (Exception e2) {
				logError("Error setting default icon image", e2);
			}
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		
		int extra = 0;
		
		if(isDisplayLoading()) {
			extra = 1;
		}
		
		count = (comments == null) ? 0 : comments.size() + extra;		
	}

	public void setCommentItemViewFactory(IBeanFactory<CommentListItem> commentItemViewFactory) {
		this.commentItemViewFactory = commentItemViewFactory;
	}

	public void setListItemLoadingViewFactory(IBeanFactory<ListItemLoadingView> listItemLoadingViewFactory) {
		this.listItemLoadingViewFactory = listItemLoadingViewFactory;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setLoadingView(View loadingView) {
		this.loadingView = loadingView;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public void setDateUtils(DateUtils dateUtils) {
		this.dateUtils = dateUtils;
	}

	public void setIconSize(int iconSize) {
		this.iconSize = iconSize;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	protected void logError(String msg, Exception e) {
		if(logger != null) {
			logger.error(msg, e);
		}
		else {
			SocializeLogger.e(e.getMessage(), e);
		}
	}
}