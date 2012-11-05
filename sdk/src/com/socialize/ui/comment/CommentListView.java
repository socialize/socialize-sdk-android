package com.socialize.ui.comment;

import java.util.List;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.socialize.ConfigUtils;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.api.action.comment.CommentOptions;
import com.socialize.api.action.comment.CommentUtilsProxy;
import com.socialize.api.action.comment.SubscriptionUtilsProxy;
import com.socialize.api.action.user.UserUtilsProxy;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Subscription;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.i18n.I18NConstants;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.subscription.SubscriptionCheckListener;
import com.socialize.listener.subscription.SubscriptionResultListener;
import com.socialize.log.SocializeLogger;
import com.socialize.notifications.SubscriptionType;
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
	
	private boolean headerDisplayed = true;

	private Entity entity;
	
	private int startIndex = 0;
	private int endIndex = defaultGrabLength;
	
	private SocializeLogger logger;
	private SimpleDialogFactory<ProgressDialog> progressDialogFactory;
	private Drawables drawables;
	private AppUtils appUtils;
	private DisplayUtils displayUtils;
	private CommentUtilsProxy commentUtils;
	private SubscriptionUtilsProxy subscriptionUtils;
	private UserUtilsProxy userUtils;
	private ProgressDialog dialog = null;
	private SocializeConfig config;
	
	private IBeanFactory<SocializeHeader> commentHeaderFactory;
	private IBeanFactory<CommentEditField> commentEditFieldFactory;
	private IBeanFactory<LoadingListView> commentContentViewFactory;
	private IBeanFactory<CustomCheckbox> notificationEnabledOptionFactory;
	
	private CommentEditField commentEntryField;
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
	
	public CommentListView(Context context) {
		super(context);
	}
	
	public void init() {

		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);

		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		setBackgroundDrawable(drawables.getDrawable("crosshatch.png", true, true, true));
		setPadding(0, 0, 0, 0);
		
		boolean landscape = displayUtils.isLandscape();
		
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

		if(!landscape && config.isShowCommentHeader() && headerDisplayed) {
			header = commentHeaderFactory.getBean();
		}
		
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
			
			UserSettings user = userUtils.getUserSettings(getContext());
			
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

		if(header != null) {
			top.addView(header);
		}
		
		middle.addView(content);
		
		sliderAnchor.addView(commentEntryField);
		
		layoutAnchor.addView(top);
		layoutAnchor.addView(middle);
		layoutAnchor.addView(sliderAnchor);
		
		addView(layoutAnchor);
		
		setDefaultGrabLength(ConfigUtils.getConfig(getContext()).getIntProperty("comment.page.size", 20));
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
		return new CommentAddButtonListener(new CommentButtonCallback() {
			
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
			public void onComment(String text, boolean shareLocation, boolean subscribe) {
				text = StringUtils.replaceNewLines(text, 3, 2);
				
				if(progressDialogFactory != null) {
					dialog = progressDialogFactory.show(getContext(), I18NConstants.DLG_COMMENT, I18NConstants.PLEASE_WAIT);
				}
				
				CommentOptions options = newShareOptions();
				options.setSubscribeToUpdates(subscribe);
				options.setShowAuthDialog(true);
				options.setShowShareDialog(true);
				
				commentUtils.addComment(CommentListView.this.getActivity(), entity, text, options, getCommentAddListener(subscribe));
				
				// Won't persist.. but that's ok.
				SocializeSession session = getSocialize().getSession();
				
				if(session != null && session.getUserSettings() != null) {
					session.getUserSettings().setLocationEnabled(shareLocation);
				}
			}
		});
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
				
				setHeaderText();
				
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

			@Override
			public void onCancel() {
				if(dialog != null) {
					dialog.dismiss();
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
			
			commentUtils.getCommentsByEntity(getActivity(), entity.getKey(), 
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
						
						List<Comment> items = entities.getItems();
						preLoadImages(items);
						commentAdapter.setComments(items);
						commentAdapter.setTotalCount(totalCount);
						
						setHeaderText();

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
			setHeaderText();
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
	
	protected void setHeaderText() {
		if(header != null) {
			String name = entity.getName();
			if(StringUtils.isEmpty(name)) {
				header.setText(commentAdapter.getTotalCount() + " Comments");
			}
			else {
				header.setText(name + " (" + commentAdapter.getTotalCount() + ")");
			}
		}	
	}

	protected void doNotificationStatusSave() {
		
		
		notifyBox.showLoading();
		
		if(notifyBox.isChecked()) {
			
			subscriptionUtils.subscribe(getActivity(), entity, SubscriptionType.NEW_COMMENTS, new SubscriptionResultListener() {
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
			subscriptionUtils.unsubscribe(getActivity(), entity, SubscriptionType.NEW_COMMENTS, new SubscriptionResultListener() {
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
			subscriptionUtils.isSubscribed(getActivity(), entity, SubscriptionType.NEW_COMMENTS, new SubscriptionCheckListener() {
				
				@Override
				public void onSubscribed(Subscription subscription) {
					notifyBox.setChecked(subscription.isSubscribed());
					
					if(commentEntrySliderItem != null) {
						commentEntrySliderItem.getCommentEntryView().setNotifySubscribeState(subscription.isSubscribed());
					}
					
					notifyBox.hideLoading();
				}

				@Override
				public void onNotSubscribed() {
					notifyBox.setChecked(false);
					
					if(commentEntrySliderItem != null) {
						commentEntrySliderItem.getCommentEntryView().setNotifySubscribeState(true); // Default to true
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
						SocializeLogger.e(error.getMessage(), error);
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
		
		commentUtils.getCommentsByEntity(getActivity(), entity.getKey(), 
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
					SocializeLogger.e(error.getMessage(), error);
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
		
		if(commentAdapter != null && onCommentViewActionListener != null) {
			commentAdapter.setOnCommentViewActionListener(onCommentViewActionListener);
		}
	}

	@Override
	public void onViewRendered(int width, int height) {
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

	protected void setCommentEntryField(CommentEditField field) {
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
		
		UserSettings user = userUtils.getUserSettings(getContext());
		
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
		return commentUtils.getUserCommentOptions(getContext());
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
		
		if(commentAdapter != null) {
			commentAdapter.setOnCommentViewActionListener(onCommentViewActionListener);
		}
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

	public void setCommentUtils(CommentUtilsProxy commentUtils) {
		this.commentUtils = commentUtils;
	}
	
	public void setSubscriptionUtils(SubscriptionUtilsProxy subscriptionUtils) {
		this.subscriptionUtils = subscriptionUtils;
	}
	
	public void setUserUtils(UserUtilsProxy userUtils) {
		this.userUtils = userUtils;
	}
	
	public void setConfig(SocializeConfig config) {
		this.config = config;
	}
	
	public CommentEditField getCommentEntryField() {
		return commentEntryField;
	}

	public void setHeaderDisplayed(boolean showHeader) {
		this.headerDisplayed = showHeader;
		if(header != null) {
			if(headerDisplayed) {
				header.setVisibility(View.VISIBLE);
			}
			else {
				header.setVisibility(View.GONE);
			}
		}
	}
}
