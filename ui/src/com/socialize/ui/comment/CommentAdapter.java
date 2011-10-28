/**
 * 
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

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Comment;
import com.socialize.entity.User;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.image.ImageLoadListener;
import com.socialize.ui.image.ImageLoadRequest;
import com.socialize.ui.image.ImageLoader;
import com.socialize.ui.util.DateUtils;
import com.socialize.ui.view.ListItemLoadingView;
import com.socialize.util.Base64DecoderException;
import com.socialize.util.Base64Utils;
import com.socialize.util.CacheableDrawable;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.util.SafeBitmapDrawable;
import com.socialize.util.StringUtils;

/**
 * Provides comments to the comment view.
 * @author jasonpolites
 */
public class CommentAdapter extends BaseAdapter {

	private IBeanFactory<CommentListItem> commentItemViewFactory;
	private IBeanFactory<ListItemLoadingView> listItemLoadingViewFactory;
	private List<Comment> comments;
	private SocializeLogger logger;
	private Drawables drawables;
	private View loadingView;
	private DeviceUtils deviceUtils;
	private DateUtils dateUtils;
	private boolean last = false;
	private Base64Utils base64Utils;
	private ImageLoader imageLoader;
	private Activity context;
	private Date now;

	private int iconSize = 100;

	public CommentAdapter(Activity context) {
		super();
		this.context = context;
		now = new Date();
	}

	@Override
	public int getCount() {
		int extra = 1;
		if(!isDisplayLoading()) {
			extra = 0;
		}
		return (comments == null) ? 0 : comments.size() + extra;
	}

	public boolean isDisplayLoading() {
		return !(last || (comments != null && comments.size() == 0));
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
		//		Comment item = (Comment) getItem(position);
		//		return (item == null) ? -1 : item.getId();
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

		User currentUser = getSocializeUI().getSocialize().getSession().getUser();

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
				final String imageUrl = user.getSmallImageUri();

				if(item != null) {

					String displayName = "";

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
								getSocializeUI().showCommentDetailViewForResult(context, user.getId().toString(), item.getId().toString(), CommentActivity.PROFILE_UPDATE);
							}
							else {
								if(logger != null) {
									logger.warn("No user for comment " + item.getId());
								}
							}
						}
					});

					TextView comment = view.getComment();
					TextView userName = view.getAuthor();
					TextView time = view.getTime();
					final ImageView userIcon = view.getUserIcon();

					if (comment != null) {
						comment.setText(item.getText());
					}

					if (userName != null) {
						userName.setText(displayName);
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

						int densitySize = deviceUtils.getDIP(iconSize);

						final Drawable defaultImage = drawables.getDrawable(SocializeUI.DEFAULT_USER_ICON, true);

						if(user != null) {
							userIcon.getBackground().setAlpha(255);

							if(!StringUtils.isEmpty(imageUrl)) {
								try {
									// Check the cache
									CacheableDrawable cached = drawables.getCache().get(imageUrl);

									if(cached != null && !cached.isRecycled()) {
										if(logger != null && logger.isInfoEnabled()) {
											logger.info("CommentAdpater setting image icon to cached image " + cached);
										}
										userIcon.setImageDrawable(cached);
										userIcon.getBackground().setAlpha(255);
									}
									else {
										userIcon.setImageDrawable(null);
										userIcon.getBackground().setAlpha(64);

										imageLoader.loadImage(position, imageUrl, new ImageLoadListener() {
											@Override
											public void onImageLoadFail(final ImageLoadRequest request, Exception error) {
												logError("Error loading image", error);
												userIcon.post(new Runnable() {
													public void run() {
														if(logger != null && logger.isInfoEnabled()) {
															logger.info("CommentAdpater setting image icon to default image");
														}
														userIcon.setImageDrawable(defaultImage);
														userIcon.getBackground().setAlpha(255);
													}
												});
											}

											@Override
											public void onImageLoad(final ImageLoadRequest request, final SafeBitmapDrawable drawable, boolean async) {
												if(request == null || !request.isCanceled()) {
													if(async) {
														// Must be run on UI thread
														userIcon.post(new Runnable() {
															public void run() {
																setImageIcon(user, position, userIcon, drawable, defaultImage);
															}
														});
													}
													else {
														setImageIcon(user, position, userIcon, drawable, defaultImage);
													}
												}
											}
										});
									}
								}
								catch (Exception e) {
									String errorMsg = "Not a valid image uri [" + imageUrl + "]";
									logError(errorMsg, e);

									userIcon.setImageDrawable(defaultImage);
								}
							}
							else if(!StringUtils.isEmpty(user.getProfilePicData())) {
								try {
									Drawable drawable = drawables.getDrawable(user.getId().toString(), base64Utils.decode(user.getProfilePicData()), densitySize, densitySize);
									userIcon.setImageDrawable(drawable);
								}
								catch (Base64DecoderException e) {

									logError("Invalid image data", e);

									userIcon.setImageDrawable(defaultImage);
								}
							}
							else {
								userIcon.setImageDrawable(defaultImage);
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

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public void setDateUtils(DateUtils dateUtils) {
		this.dateUtils = dateUtils;
	}

	public void setBase64Utils(Base64Utils base64Utils) {
		this.base64Utils = base64Utils;
	}

	public void setIconSize(int iconSize) {
		this.iconSize = iconSize;
	}

	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	protected SocializeUI getSocializeUI() {
		return SocializeUI.getInstance();
	}

	protected void logError(String msg, Exception e) {
		if(logger != null) {
			logger.error(msg, e);
		}
		else {
			e.printStackTrace();
		}
	}
}