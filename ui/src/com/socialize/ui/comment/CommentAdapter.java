/**
 * 
 */
package com.socialize.ui.comment;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Comment;
import com.socialize.entity.User;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.user.UserService;
import com.socialize.ui.util.TimeUtils;
import com.socialize.ui.view.ListItemLoadingView;
import com.socialize.ui.view.ViewHolder;
import com.socialize.util.Base64Utils;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

/**
 * Provides comments to the comment view.
 * @author jasonpolites
 */

// TODO: remove this annotation
@SuppressWarnings("unused")
public class CommentAdapter extends BaseAdapter {

	private IBeanFactory<CommentListItem> commentItemViewFactory;
	private IBeanFactory<ListItemLoadingView> listItemLoadingViewFactory;
	private List<Comment> comments;
	private SocializeLogger logger;
	private Drawables drawables;
	private View loadingView;
	private DeviceUtils deviceUtils;
	private TimeUtils timeUtils;
	private UserService userService;
	private Base64Utils base64Utils;
	private boolean last = false;
	
	private final int iconSize = 64;
	
	public CommentAdapter(Context context) {
		super();
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
		Comment item = (Comment) getItem(position);
		return (item == null) ? -1 : item.getId();
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
	
	protected ViewHolder createViewHolder() {
		return new ViewHolder();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
        ViewHolder holder;
   
        if (view == null) {
        	
        	CommentListItem v = commentItemViewFactory.getBean();
        	
            holder = createViewHolder();
            
            holder.setTime(v.getTime());
            holder.setUserName(v.getAuthor());
            holder.setComment(v.getComment());
//    		userIcon =  v.getUserIcon();
    		holder.setNow(new Date());

            v.setTag(holder);
            
            view = v;
        } 
        else {
            holder = (ViewHolder) view.getTag();
        }
        
        if(position >= comments.size()) {
        	// Last one, get loading view
        	if(loadingView == null) {
        		loadingView = listItemLoadingViewFactory.getBean();
        		loadingView.setTag(holder);
        	}
        	
        	view = loadingView;
        }
        else {
        	Comment item = (Comment) getItem(position);
    		
    		if(item != null) {
    			User currentUser = userService.getCurrentUser();
    			User user = item.getUser();
    			String displayName = null;
    			
    			if(currentUser != null && user != null && currentUser.getId().equals(user.getId())) {
    				user = currentUser;
    				displayName = "You";
    			}
    			
    			if(user != null && displayName == null) {
    				displayName = user.getDisplayName();
    				
    				if(displayName == null) {
    					displayName = "Anonymous";
    				}
    			}
    			
    			TextView comment = holder.getComment();
    			TextView userName = holder.getUserName();
    			TextView time = holder.getTime();
    			ImageView userIcon = holder.getUserIcon();
    			
    			if (comment != null) {
    				comment.setText(item.getText());
    			}
    			
    			if (userName != null) {
    				if(user != null) {
    					userName.setText(displayName);
    				}
    			}
    			
    			if (time != null) {
    				Long date = item.getDate();
    				if(date != null && date > 0) {
    					long diff = (holder.getNow().getTime() - date.longValue());
    					time.setText(timeUtils.getTimeString(diff) + " ");
    				}
    				else {
    					time.setText(" ");
    				}
    			}
    			
    			// TODO: uncomment when we do profile screen
    			
//    			if (userIcon != null) {
//    			    Drawable defaultImage = drawables.getDrawable(SocializeUI.DEFAULT_USER_ICON, deviceUtils.getDIP(iconSize), deviceUtils.getDIP(iconSize), true);
//    					
//    				if(user != null) {
//    					if(!StringUtils.isEmpty(user.getSmallImageUri())) {
//    						try {
//    							Uri uri = Uri.parse(user.getSmallImageUri());
//    							userIcon.setImageURI(uri);
//    						}
//    						catch (Exception e) {
//    							String errorMsg = "Not a valid image uri [" + user.getSmallImageUri() + "]";
//    							if(logger != null) {
//    								logger.error(errorMsg, e);
//    							}
//    							else {
//    								System.err.println(errorMsg);
//    							}
//    							
//    							userIcon.setImageDrawable(defaultImage);
//    						}
//    					}
//    					else if(drawables != null && !StringUtils.isEmpty(user.getProfilePicData())) {
//    						try {
//								Drawable drawable = drawables.getDrawable(user.getId().toString(), base64Utils.decode(user.getProfilePicData()), deviceUtils.getDIP(iconSize), deviceUtils.getDIP(iconSize));
//								userIcon.setImageDrawable(drawable);
//							}
//							catch (Base64DecoderException e) {
//								if(logger != null) {
//									logger.error("Invalid image data", e);
//								}
//								else {
//									e.printStackTrace();
//								}
//								
//								userIcon.setImageDrawable(defaultImage);
//							}
//    					}
//    					else {
//    						userIcon.setImageDrawable(defaultImage);
//    					}
//    				}
//    				else {
//    					userIcon.setImageDrawable(defaultImage);
//    				}
//    			}
    		}
        }
        
		return view;
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

	public void setTimeUtils(TimeUtils timeUtils) {
		this.timeUtils = timeUtils;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setBase64Utils(Base64Utils base64Utils) {
		this.base64Utils = base64Utils;
	}
}
