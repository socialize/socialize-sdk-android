package com.socialize.ui.comment;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.socialize.ShareUtils;
import com.socialize.Socialize;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Comment;
import com.socialize.entity.Entity;
import com.socialize.entity.ListResult;
import com.socialize.entity.Subscription;
import com.socialize.entity.User;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.listener.subscription.SubscriptionResultListener;
import com.socialize.listener.subscription.SubscriptionGetListener;
import com.socialize.log.SocializeLogger;
import com.socialize.networks.ShareOptions;
import com.socialize.networks.SocialNetwork;
import com.socialize.notifications.NotificationType;
import com.socialize.ui.auth.AuthPanelView;
import com.socialize.ui.auth.ShareDialogListener;
import com.socialize.ui.dialog.AuthRequestDialogFactory;
import com.socialize.ui.dialog.DialogFactory;
import com.socialize.ui.header.SocializeHeader;
import com.socialize.ui.image.ImageLoader;
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
	private DialogFactory<ProgressDialog> progressDialogFactory;
	private DialogFactory<AlertDialog> alertDialogFactory;
	private Drawables drawables;
	private AppUtils appUtils;
	private DisplayUtils displayUtils;
	private ProgressDialog dialog = null;
	
	private IBeanFactory<SocializeHeader> commentHeaderFactory;
	private IBeanFactory<CommentEditField> commentEditFieldFactory;
	private IBeanFactory<LoadingListView> commentContentViewFactory;
	private IBeanFactory<CustomCheckbox> notificationEnabledOptionFactory;
	
	private View field;
	private SocializeHeader header;
	private LoadingListView content;
	private AuthRequestDialogFactory authRequestDialogFactory;
	
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
		field = commentEditFieldFactory.getBean();
		content = commentContentViewFactory.getBean();
		
		if(commentEntryFactory != null) {
			commentEntrySliderItem = commentEntryFactory.getBean(getCommentAddListener());
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
			
			User user = Socialize.getSocialize().getSession().getUser();
			
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
		
		sliderAnchor.addView(field);
		
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
	
	protected CommentAddButtonListener getCommentAddListener() {
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
					// No networks requested, ensure we are authed with at least one
					boolean showAuth = true;
					boolean authSupported = false;
					
					SocialNetwork[] all = SocialNetwork.values();
					
					for (SocialNetwork socialNetwork : all) {
						AuthProviderType type = AuthProviderType.valueOf(socialNetwork);
						if(getSocialize().isSupported(type)) {
							authSupported = true;
							if(getSocialize().isAuthenticated(type)) {
								showAuth = false;
								break;
							}
						}
					}
					
					if(showAuth && authSupported) {
						authRequestDialogFactory.show(CommentListView.this.getContext(), getCommentAuthListener(text, shareLocation, subscribe, networks), ShareUtils.FACEBOOK | ShareUtils.TWITTER);
					}
					else {
						// Post as anon
						doPostComment(text, shareLocation, subscribe);
					}
				}
				else {
					doPostComment(text, shareLocation, subscribe, networks);
				}
			}
		});
	}
	
	protected ShareDialogListener getCommentAuthListener(final String text, final boolean shareLocation, final boolean subscribe, final SocialNetwork...networks) {
		return new ShareDialogListener() {
			@Override
			public void onShow(Dialog dialog, AuthPanelView dialogView) {}
			
			@Override
			public void onContinue(Dialog dialog, SocialNetwork...network) {
				doPostComment(text, shareLocation, subscribe, networks);
			}

			@Override
			public void onCancel(Dialog dialog) {}
		};
	}
	
	public void doPostComment(String text, boolean shareLocation, final boolean subscribe, SocialNetwork...networks) {
				
		dialog = progressDialogFactory.show(getContext(), "Posting comment", "Please wait...");
		
		ShareOptions options = newShareOptions();
		
		options.setShareTo(networks);
		options.setShareLocation(shareLocation);
		
		Comment comment = newComment();
		comment.setText(text);
		comment.setNotificationsEnabled(subscribe);
		comment.setEntity(entity);
		
		getSocialize().addComment(getActivity(), comment, options, new CommentAddListener() {

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
		});
		
		// Won't persist.. but that's ok.
		SocializeSession session = getSocialize().getSession();
		
		if(session != null && session.getUser() != null) {
			// TODO: set options
//			session.getUser().setAutoPostCommentsFacebook(autoPostToFacebook);
			session.getUser().setShareLocation(shareLocation);
		}
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
			getSocialize().listCommentsByEntity(entity.getKey(), 
					startIndex,
					endIndex,
					new CommentListListener() {

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
		
		final ProgressDialog dialog = progressDialogFactory.show(getContext(), "Notifications", "Please wait...");
		
		if(notifyBox.isChecked()) {
			getSocialize().subscribe(getContext(), entity, NotificationType.NEW_COMMENTS, new SubscriptionResultListener() {
				@Override
				public void onError(SocializeException error) {
					if(dialog != null) dialog.dismiss();
					showError(getContext(), error);
					notifyBox.setChecked(false);
				}
				
				@Override
				public void onCreate(Subscription entity) {
					if(commentEntrySliderItem != null) {
						commentEntrySliderItem.getCommentEntryView().setNotifySubscribeState(true);
					}
					if(dialog != null) dialog.dismiss();
					alertDialogFactory.show(getContext(), "Subscribe Successful", "We will notify you when someone posts a comment to this discussion.");
				}
			});
		}
		else {
			getSocialize().unsubscribe(getContext(), entity, NotificationType.NEW_COMMENTS, new SubscriptionResultListener() {
				@Override
				public void onError(SocializeException error) {
					if(dialog != null) dialog.dismiss();
					showError(getContext(), error);
					notifyBox.setChecked(true);
				}
				
				@Override
				public void onCreate(Subscription entity) {
					if(commentEntrySliderItem != null) {
						commentEntrySliderItem.getCommentEntryView().setNotifySubscribeState(false);
					}
					if(dialog != null) dialog.dismiss();
					alertDialogFactory.show(getContext(), "Unsubscribe Successful", "You will no longer receive notifications for updates to this discussion.");
				}
			});
		}		
	}
	
	protected void doNotificationStatusLoad() {
		if(notifyBox != null) {
			notifyBox.showLoading();
			
			// Now load the subscription status for the user
			getSocialize().getSubscription(entity, new SubscriptionGetListener() {
				
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

		getSocialize().listCommentsByEntity(entity.getKey(), 
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
			
			field.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(commentEntrySlider != null) {
						commentEntrySlider.showSliderItem(commentEntrySliderItem);
					}
				}
			});		
		}
		else if(field != null) {
			field.setVisibility(GONE);
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

	public void setProgressDialogFactory(DialogFactory<ProgressDialog> progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

	public void setAlertDialogFactory(DialogFactory<AlertDialog> alertDialogFactory) {
		this.alertDialogFactory = alertDialogFactory;
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

	protected void setField(View field) {
		this.field = field;
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

	public void setAuthRequestDialogFactory(AuthRequestDialogFactory authRequestDialogFactory) {
		this.authRequestDialogFactory = authRequestDialogFactory;
	}
	
	/**
	 * Called when the current logged in user updates their profile.
	 */
	public void onProfileUpdate() {
		commentAdapter.notifyDataSetChanged();
		
		if(commentEntrySlider != null) {
			commentEntrySlider.updateContent();
		}
		
		User user = Socialize.getSocialize().getSession().getUser();
		
		if(notifyBox != null) {
			if(user.isNotificationsEnabled()) {
				notifyBox.setVisibility(View.VISIBLE);
			}
			else {
				notifyBox.setVisibility(View.GONE);
			}
		}
	}
	
	// So we can mock
	protected ShareOptions newShareOptions() {
		return new ShareOptions();
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
