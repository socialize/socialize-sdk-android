package com.socialize.ui.comment;

import java.util.List;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.socialize.CommentUtils;
import com.socialize.Socialize;
import com.socialize.SubscriptionUtils;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.comment.CommentOptions;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Subscription;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.subscription.SubscriptionGetListener;
import com.socialize.listener.subscription.SubscriptionResultListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.SocialNetwork;
import com.socialize.notifications.NotificationType;
import com.socialize.ui.dialog.SimpleDialogFactory;
import com.socialize.ui.header.SocializeHeader;
import com.socialize.ui.image.ImageLoader;
import com.socialize.ui.profile.UserSettings;
import com.socialize.ui.slider.ActionBarSliderFactory;
import com.socialize.ui.slider.ActionBarSliderFactory.ZOrder;
import com.socialize.ui.slider.ActionBarSliderView;
import com.socialize.ui.view.CustomCheckbox;
import com.socialize.ui.view.LoadingListView;
import com.socialize.util.AppUtils;
import com.socialize.util.CacheableDrawable;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;
import com.socialize.view.BaseView;

public class CommentListView extends BaseView {

	private int defaultGrabLength = 30;
	// TODO: config this
	private int iconSize = 100;
	private CommentAdapter commentAdapter;
	private boolean loading = true; // Default to true
	
	private Entity entity;
	
	private int startIndex = 0;
	private int endIndex = defaultGrabLength;
	
	private SocializeLogger logger;
	private SimpleDialogFactory<ProgressDialog> progressDialogFactory;
//	private SimpleDialogFactory<AlertDialog> alertDialogFactory;
	private Drawables drawables;
	private AppUtils appUtils;
	private DisplayUtils displayUtils;
	private ProgressDialog dialog = null;
	
	private IBeanFactory<SocializeHeader> commentHeaderFactory;
	private IBeanFactory<CommentEditField> commentEditFieldFactory;
	private IBeanFactory<LoadingListView> commentContentViewFactory;
	private IBeanFactory<CustomCheckbox> notificationEnabledOptionFactory;
	
	private View commentEntryField;
	private SocializeHeader header;
	private LoadingListView content;
	
	private IBeanFactory<CommentEntrySliderItem> commentEntryFactory;
	
	private ActionBarSliderView commentEntrySlider;
	
	private ActionBarSliderFactory<ActionBarSliderView> sliderFactory;
	
	private RelativeLayout layoutAnchor;
	private ViewGroup sliderAnchor;
	private CustomCheckbox notifyBox;
	private ImageLoader imageLoader;
	
	private CommentEntrySliderItem commentEntrySliderItem;
	
	private OnCommentViewActionListener onCommentViewActionListener;
	
	public CommentListView(Context context, Entity entity) {
		super(context);
		this.entity = entity;
	}
	
	public void init() {

		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);

		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		setBackgroundDrawable(drawables.getDrawable("crosshatch.png", true, true, true));
		setPadding(0, 0, 0, 0);
		
		iconSize = displayUtils.getDIP(iconSize);
		
		layoutAnchor = new RelativeLayout(getContext());
		
		LayoutParams innerParams = new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		
		layoutAnchor.setLayoutParams(innerParams);
		
		LinearLayout top = new LinearLayout(getContext());
		LinearLayout middle = new LinearLayout(getContext());
		sliderAnchor = new LinearLayout(getContext());
		
		int topId = getNextViewId(getParentView());
		int middleId = getNextViewId(getParentView());
		int bottomId = getNextViewId(getParentView());
		
		top.setPadding(0, 0, 0, 0);
		middle.setPadding(0, 0, 0, 0);
		sliderAnchor.setPadding(0, 0, 0, 0);
		
		top.setId(topId);
		middle.setId(middleId);
		sliderAnchor.setId(bottomId);
		
		RelativeLayout.LayoutParams topParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams middleParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		topParams.setMargins(0, 0, 0, 0);
		middleParams.setMargins(0, 0, 0, 0);
		bottomParams.setMargins(0, 0, 0, 0);	
		
		topParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		
		middleParams.addRule(RelativeLayout.BELOW, topId);
		middleParams.addRule(RelativeLayout.ABOVE, bottomId);		
		
		top.setLayoutParams(topParams);
		middle.setLayoutParams(middleParams);
		sliderAnchor.setLayoutParams(bottomParams);

		header = commentHeaderFactory.getBean();
		commentEntryField = commentEditFieldFactory.getBean();
		content = commentContentViewFactory.getBean();
		
		if(commentEntryFactory != null) {
			commentEntrySliderItem = commentEntryFactory.getBean(getCommentAddButtonListener());
		}
		
		boolean notificationsAvailable = appUtils.isNotificationsAvailable(getContext());		
		
		if(notificationsAvailable) {
			notifyBox = notificationEnabledOptionFactory.getBean();
			notifyBox.setEnabled(false); // Start disabled
			notifyBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					doNotificationStatusSave();
				}
			});
			
			UserSettings user = Socialize.getSocialize().getSession().getUserSettings();
			
			if(user.isNotificationsEnabled()) {
				notifyBox.setVisibility(View.VISIBLE);
			}
			else {
				notifyBox.setVisibility(View.GONE);
			}
			
			sliderAnchor.addView(notifyBox);
		}		
		
		content.setListAdapter(commentAdapter);
		content.setScrollListener(getCommentScrollListener());

		top.addView(header);
		middle.addView(content);
		
		sliderAnchor.addView(commentEntryField);
		
		layoutAnchor.addView(top);
		layoutAnchor.addView(middle);
		layoutAnchor.addView(sliderAnchor);
		
		addView(layoutAnchor);
		
		setDefaultGrabLength(Socialize.getSocialize().getConfig().getIntProperty("comment.page.size", 20));
	}
	
	protected CommentScrollListener getCommentScrollListener() {
		return new CommentScrollListener(new CommentScrollCallback() {
			@Override
			public void onGetNextSet() {
				getNextSet();
			}
			
			@Override
			public boolean isLoading() {
				return loading;
			}

			@Override
			public boolean hasMoreItems() {
				return !commentAdapter.isLast();
			}
		});
	}
	
	protected CommentAddButtonListener getCommentAddButtonListener() {
		return new CommentAddButtonListener(getContext(), new CommentButtonCallback() {
			
			@Override
			public void onError(Context context, SocializeException e) {
				showError(getContext(), e);
				if(onCommentViewActionListener != null) {
					onCommentViewActionListener.onError(e);
				}				
			}

			@Override
			public void onCancel() {
				if(commentEntrySlider != null) {
					commentEntrySlider.close();
				}
			}

			@Override
			public void onComment(String text, boolean shareLocation, boolean subscribe, SocialNetwork... networks) {
				text = StringUtils.replaceNewLines(text, 3, 2);
				if(networks == null || networks.length == 0) {
					CommentUtils.addComment(CommentListView.this.getActivity(), entity, text, getCommentAddListener(subscribe));
				}
				else {
					doPostComment(text, shareLocation, subscribe, networks);
				}
			}
		});
	}
	
	public void doPostComment(String text, boolean shareLocation, final boolean subscribe, SocialNetwork...networks) {
		dialog = progressDialogFactory.show(getContext(), "Posting comment", "Please wait...");
		
		CommentOptions options = newShareOptions();
		
//		options.setShareTo(networks);
		options.setShareLocation(shareLocation);
		options.setSubscribeToUpdates(subscribe);
		
		Comment comment = newComment();
		comment.setText(text);
//		comment.setNotificationsEnabled(subscribe);
		comment.setEntitySafe(entity);
		
		CommentUtils.addComment(getActivity(), entity, text, options, getCommentAddListener(subscribe), networks);
		
//		getSocialize().addComment(getActivity(), comment, options, getCommentAddListener(subscribe), networks);
		
		// Won't persist.. but that's ok.
		SocializeSession session = getSocialize().getSession();
		
		if(session != null && session.getUserSettings() != null) {
			// TODO: set options
//			session.getUser().setAutoPostCommentsFacebook(autoPostToFacebook);
			session.getUserSettings().setLocationEnabled(shareLocation);
		}
	}
	
	protected CommentAddListener getCommentAddListener(final boolean subscribe) {
		return new CommentAddListener() {

			@Override
			public void onError(SocializeException error) {
				showError(getContext(), error);
				if(dialog != null) {
					dialog.dismiss();
				}
				
				if(onCommentViewActionListener != null) {
					onCommentViewActionListener.onError(error);
				}						
			}

			@Override
			public void onCreate(Comment entity) {
				List<Comment> comments = commentAdapter.getComments();
				if(comments != null) {
					comments.add(0, entity);
				}
				else {
					// TODO: handle error!
				}
				
				startIndex++;
				endIndex++;
				
				if(commentEntrySlider != null) {
					commentEntrySlider.clearContent();
					commentEntrySlider.close();
				}
				
				commentAdapter.setTotalCount(commentAdapter.getTotalCount() + 1);
				commentAdapter.notifyDataSetChanged();
				
				header.setText(commentAdapter.getTotalCount() + " Comments");
				
				content.scrollToTop();
				
				if(dialog != null) {
					dialog.dismiss();
				}
				
				if(notifyBox != null) {
					notifyBox.setChecked(subscribe);
				}
				
				if(onCommentViewActionListener != null) {
					onCommentViewActionListener.onPostComment(entity);
				}
			}
		};
	}
	
	public void reload() {
		content.showLoading();
		commentAdapter.reset();
		
		if(onCommentViewActionListener != null) {
			onCommentViewActionListener.onReload(this);
		}
		
		doListComments(true);
	}
	
	public CommentAdapter getCommentAdapter() {
		return commentAdapter;
	}

	public void doListComments(final boolean update) {

		startIndex = 0;
		endIndex = defaultGrabLength;

		loading = true;
		
		final List<Comment> comments = commentAdapter.getComments();

		if(update || comments == null || comments.size() == 0) {
			
			CommentUtils.getCommentsByEntity(getActivity(), entity.getKey(), 
					startIndex,
					endIndex, new CommentListListener() {

				@Override
				public void onError(SocializeException error) {
					showError(getContext(), error);
					content.showList();
					
					if(dialog != null) {
						dialog.dismiss();
					}

					loading = false;
					
					if(onCommentViewActionListener != null) {
						onCommentViewActionListener.onError(error);
					}					
				}

				@Override
				public void onList(ListResult<Comment> entities) {
					
					if(entities != null) {
						int totalCount = entities.getTotalCount();
						header.setText(totalCount + " Comments");
						List<Comment> items = entities.getItems();
						preLoadImages(items);
						commentAdapter.setComments(items);
						commentAdapter.setTotalCount(totalCount);

						if(totalCount <= endIndex) {
							commentAdapter.setLast(true);
						}

						if(update || comments == null) {
							commentAdapter.notifyDataSetChanged();
							content.scrollToTop();
						}
					}
					
					content.showList();

					loading = false;
					
					if(onCommentViewActionListener != null && entities != null) {
						onCommentViewActionListener.onCommentList(CommentListView.this, entities.getItems(), startIndex, endIndex);
					}
					
					if(dialog != null)  dialog.dismiss();
				}
			});
		}
		else {
			content.showList();
			header.setText(commentAdapter.getTotalCount() + " Comments");
			commentAdapter.notifyDataSetChanged();
			if(dialog != null) {
				dialog.dismiss();
			}

			loading = false;
			
			if(onCommentViewActionListener != null) {
				onCommentViewActionListener.onCommentList(CommentListView.this, comments, startIndex, endIndex);
			}			
		}
	}
	
	// SO we can mock
	protected Comment newComment() {
		return new Comment();
	}

	protected void doNotificationStatusSave() {
		
		
		notifyBox.showLoading();
		
		if(notifyBox.isChecked()) {
			
			SubscriptionUtils.subscribe(getActivity(), entity, NotificationType.NEW_COMMENTS, new SubscriptionResultListener() {
				@Override
				public void onError(SocializeException error) {
					showError(getContext(), error);
					notifyBox.setChecked(false);
					notifyBox.hideLoading();
				}
				
				@Override
				public void onCreate(Subscription entity) {
					if(commentEntrySliderItem != null) {
						commentEntrySliderItem.getCommentEntryView().setNotifySubscribeState(true);
					}
					notifyBox.hideLoading();
				}
			});
		}
		else {
			SubscriptionUtils.unsubscribe(getActivity(), entity, NotificationType.NEW_COMMENTS, new SubscriptionResultListener() {
				@Override
				public void onError(SocializeException error) {
					showError(getContext(), error);
					notifyBox.setChecked(true);
					notifyBox.hideLoading();
				}
				
				@Override
				public void onCreate(Subscription entity) {
					if(commentEntrySliderItem != null) {
						commentEntrySliderItem.getCommentEntryView().setNotifySubscribeState(false);
					}
					notifyBox.hideLoading();
				}
			});
		}		
	}
	
	protected void doNotificationStatusLoad() {
		if(notifyBox != null) {
			notifyBox.showLoading();
			
			// Now load the subscription status for the user
			SubscriptionUtils.isSubscribed(getActivity(), entity, NotificationType.NEW_COMMENTS, new SubscriptionGetListener() {
				
				@Override
				public void onGet(Subscription subscription) {
					
					if(subscription == null) {
						notifyBox.setChecked(false);
						
						if(commentEntrySliderItem != null) {
							commentEntrySliderItem.getCommentEntryView().setNotifySubscribeState(true); // Default to true
						}
					}
					else {
						notifyBox.setChecked(subscription.isSubscribed());
						
						if(commentEntrySliderItem != null) {
							commentEntrySliderItem.getCommentEntryView().setNotifySubscribeState(subscription.isSubscribed());
						}
					}
					
					notifyBox.hideLoading();
				}
				
				@Override
				public void onError(SocializeException error) {
					notifyBox.setChecked(false);
					
					if(logger != null) {
						logger.error("Error retrieving subscription info", error);
					}
					else {
						error.printStackTrace();
					}
					
					if(commentEntrySliderItem != null) {
						commentEntrySliderItem.getCommentEntryView().setNotifySubscribeState(true); // Default to true
					}
					
					notifyBox.hideLoading();
				}
			});
		}		
	}
	
	protected void getNextSet() {
		
		if(logger != null && logger.isDebugEnabled()) {
			logger.debug("getNextSet called on CommentListView");
		}
		
		loading = true; // Prevent continuous load

		startIndex+=defaultGrabLength;
		endIndex+=defaultGrabLength;

		if(endIndex > commentAdapter.getTotalCount()) {
			endIndex = commentAdapter.getTotalCount();

			if(startIndex >= endIndex) {
				commentAdapter.setLast(true);
				commentAdapter.notifyDataSetChanged();
				loading = false;
				return;
			}
		}
		
		CommentUtils.getCommentsByEntity(getActivity(), entity.getKey(), 
				startIndex,
				endIndex, 
				new CommentListListener() {

			@Override
			public void onError(SocializeException error) {

				// Don't show loading anymore
				if(logger != null) {
					logger.error("Error retrieving comments", error);
				}
				else {
					error.printStackTrace();
				}

				loading = false;
				
				if(onCommentViewActionListener != null) {
					onCommentViewActionListener.onError(error);
				}				
			}

			@Override
			public void onList(ListResult<Comment> entities) {
				List<Comment> comments = commentAdapter.getComments();
				comments.addAll(entities.getItems());
				preLoadImages(comments);
				commentAdapter.setComments(comments);
				commentAdapter.notifyDataSetChanged();
				loading = false;
			}
		});
	}
	
	protected void preLoadImages(List<Comment> comments) {
		if(comments != null) {
			for (Comment comment : comments) {
				User user = comment.getUser();
				if(user != null) {
					String imageUrl = user.getSmallImageUri();
					if(!StringUtils.isEmpty(imageUrl)) {
						CacheableDrawable cached = drawables.getCache().get(imageUrl);
						if(cached == null || cached.isRecycled()) {
							imageLoader.loadImageByUrl(imageUrl, iconSize, iconSize, null);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onViewLoad() {
		super.onViewLoad();
		if(onCommentViewActionListener != null) {
			onCommentViewActionListener.onCreate(this);
		}		
	}

	@Override
	public void onViewRendered(int width, int height) {
		
		// TODO: loading progress dialog?
		
		if(sliderFactory != null) {
			if(commentEntrySlider == null) {
				commentEntrySlider = sliderFactory.wrap(getSliderAnchor(), ZOrder.FRONT, height);
			}
		}
		
		if(commentEntrySliderItem != null) {
			
			commentEntrySlider.loadItem(commentEntrySliderItem);
			
			commentEntryField.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(commentEntrySlider != null) {
						commentEntrySlider.showSliderItem(commentEntrySliderItem);
					}
				}
			});		
		}
		else if(commentEntryField != null) {
			commentEntryField.setVisibility(GONE);
		}
		
		if(getSocialize().isAuthenticated()) {
			doListComments(false);
			doNotificationStatusLoad();
		}
		else {
			SocializeException e = new SocializeException("Socialize not authenticated");
			
			showError(getContext(),e);
			
			if(onCommentViewActionListener != null) {
				onCommentViewActionListener.onError(e);
			}
			
			if(content != null) {
				content.showList();
			}
		}	
		
		if(onCommentViewActionListener != null) {
			onCommentViewActionListener.onRender(this);
		}		
	}

	public void setCommentAdapter(CommentAdapter commentAdapter) {
		this.commentAdapter = commentAdapter;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setProgressDialogFactory(SimpleDialogFactory<ProgressDialog> progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

//	public void setAlertDialogFactory(SimpleDialogFactory<AlertDialog> alertDialogFactory) {
//		this.alertDialogFactory = alertDialogFactory;
//	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setDefaultGrabLength(int defaultGrabLength) {
		this.defaultGrabLength = defaultGrabLength;
	}

	public void setCommentHeaderFactory(IBeanFactory<SocializeHeader> commentHeaderFactory) {
		this.commentHeaderFactory = commentHeaderFactory;
	}

	public void setCommentEditFieldFactory(IBeanFactory<CommentEditField> commentEditFieldFactory) {
		this.commentEditFieldFactory = commentEditFieldFactory;
	}

	public void setCommentContentViewFactory(IBeanFactory<LoadingListView> commentContentViewFactory) {
		this.commentContentViewFactory = commentContentViewFactory;
	}

	public void setAppUtils(AppUtils appUtils) {
		this.appUtils = appUtils;
	}

	public void setNotificationEnabledOptionFactory(IBeanFactory<CustomCheckbox> notificationEnabledOptionFactory) {
		this.notificationEnabledOptionFactory = notificationEnabledOptionFactory;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public boolean isLoading() {
		return loading;
	}
	
	protected void setLoading(boolean loading) {
		this.loading = loading;
	}

	protected void setCommentEntryField(View field) {
		this.commentEntryField = field;
	}

	protected void setHeader(SocializeHeader header) {
		this.header = header;
	}

	protected void setContent(LoadingListView content) {
		this.content = content;
	}

	protected void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	protected void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}
	
	public void setCommentEntryFactory(IBeanFactory<CommentEntrySliderItem> commentEntryFactory) {
		this.commentEntryFactory = commentEntryFactory;
	}
	
	public int getTotalCount() {
		return commentAdapter.getTotalCount();
	}

//	public void setAuthDialogFactory(AuthDialogFactory authDialogFactory) {
//		this.authDialogFactory = authDialogFactory;
//	}
	
	/**
	 * Called when the current logged in user updates their profile.
	 */
	public void onProfileUpdate() {
		commentAdapter.notifyDataSetChanged();
		
		if(commentEntrySlider != null) {
			commentEntrySlider.updateContent();
		}
		
		UserSettings user = Socialize.getSocialize().getSession().getUserSettings();
		
		if(notifyBox != null && user != null) {
			if(user.isNotificationsEnabled()) {
				notifyBox.setVisibility(View.VISIBLE);
			}
			else {
				notifyBox.setVisibility(View.GONE);
			}
		}
	}
	
	// So we can mock
	protected CommentOptions newShareOptions() {
		return new CommentOptions();
	}
	
	protected RelativeLayout getLayoutAnchor() {
		return layoutAnchor;
	}

	protected ViewGroup getSliderAnchor() {
		return sliderAnchor;
	}

	public void setSliderFactory(ActionBarSliderFactory<ActionBarSliderView> sliderFactory) {
		this.sliderFactory = sliderFactory;
	}

	public void setOnCommentViewActionListener(OnCommentViewActionListener onCommentViewActionListener) {
		this.onCommentViewActionListener = onCommentViewActionListener;
	}
	
	public ActionBarSliderView getCommentEntryViewSlider() {
		return commentEntrySlider;
	}
	public void setImageLoader(ImageLoader imageLoader) {
		this.imageLoader = imageLoader;
	}

	public void setDisplayUtils(DisplayUtils displayUtils) {
		this.displayUtils = displayUtils;
	}
}
