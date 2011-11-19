package com.socialize.ui.comment;

import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.socialize.android.ioc.IBeanFactory;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
import com.socialize.entity.Comment;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.auth.AuthRequestDialogFactory;
import com.socialize.ui.auth.AuthRequestListener;
import com.socialize.ui.dialog.DialogFactory;
import com.socialize.ui.facebook.FacebookWallPoster;
import com.socialize.ui.slider.ActionBarSliderFactory;
import com.socialize.ui.slider.ActionBarSliderFactory.ZOrder;
import com.socialize.ui.slider.ActionBarSliderView;
import com.socialize.ui.view.ViewFactory;
import com.socialize.util.Drawables;
import com.socialize.view.BaseView;

public class CommentListView extends BaseView {

	private int defaultGrabLength = 20;
	private CommentAdapter commentAdapter;
	private boolean loading = true; // Default to true
	
	private String entityKey;
	private String entityName;
	private boolean useLink;
	private int startIndex = 0;
	private int endIndex = defaultGrabLength;
//	private int totalCount = 0;
	
	private SocializeLogger logger;
	private DialogFactory<ProgressDialog> progressDialogFactory;
	private Drawables drawables;
	private ProgressDialog dialog = null;
	
	private ViewFactory<CommentHeader> commentHeaderFactory;
	private ViewFactory<View> commentEditFieldFactory;
	private ViewFactory<CommentContentView> commentContentViewFactory;
	
	private View field;
	private CommentHeader header;
	private CommentContentView content;
	private IBeanFactory<AuthRequestDialogFactory> authRequestDialogFactory;
	
	private IBeanFactory<CommentEntrySliderItem> commentEntryFactory;
	
	private ActionBarSliderView slider;
	private ActionBarSliderFactory<ActionBarSliderView> sliderFactory;
	
	private FacebookWallPoster facebookWallPoster;
	
	private RelativeLayout layoutAnchor;
	private ViewGroup sliderAnchor;
	
	private CommentEntrySliderItem commentEntryPage;
	
	public CommentListView(Context context, String entityKey, String entityName, boolean useLink) {
		this(context);
		this.entityKey = entityKey;
		this.entityName = entityName;
		this.useLink = useLink;
	}
	
	public CommentListView(Context context,String entityKey) {
		this(context, entityKey, null, true);
	}	
	
	public CommentListView(Context context) {
		super(context);
	}

	public void init() {

		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);

		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		setBackgroundDrawable(drawables.getDrawable("crosshatch.png", true, true, true));
		setPadding(0, 0, 0, 0);
		
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


		header = commentHeaderFactory.make(getContext());
		field = commentEditFieldFactory.make(getContext());
		content = commentContentViewFactory.make(getContext());
		
		if(commentEntryFactory != null) {
			commentEntryPage = commentEntryFactory.getBean(getCommentAddListener());
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
		});
	}
	
	protected CommentAddButtonListener getCommentAddListener() {
		return new CommentAddButtonListener(getContext(), new CommentButtonCallback() {
			
			@Override
			public void onError(Context context, Exception e) {
				showError(getContext(), e);
			}

			@Override
			public void onCancel() {
				if(slider != null) {
					slider.close();
				}
			}

			@Override
			public void onComment(String text, boolean autoPostToFacebook) {
				if(!getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
					// Check that FB is enabled for this installation
					if(getSocializeUI().isFacebookSupported()) {
						AuthRequestDialogFactory dialog = authRequestDialogFactory.getBean();
						dialog.show(getContext(), getCommentAuthListener(text, autoPostToFacebook));
					}
					else {
						// Just post as anon
						doPostComment(text, false);
					}
				}
				else {
					doPostComment(text, autoPostToFacebook);
				}
			}
		});
	}
	
	protected AuthRequestListener getCommentAuthListener(final String text, final boolean autoPostToFacebook) {
		return new AuthRequestListener() {
			@Override
			public void onResult(Dialog dialog) {
				doPostComment(text, autoPostToFacebook);
			}
		};
	}

	public void doPostComment(String comment, boolean autoPostToFacebook) {
		
		dialog = progressDialogFactory.show(getContext(), "Posting comment", "Please wait...");

		getSocialize().addComment(entityKey, comment, new CommentAddListener() {

			@Override
			public void onError(SocializeException error) {
				showError(getContext(), error);
				if(dialog != null) {
					dialog.dismiss();
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
				
				if(slider != null) {
					slider.clearContent();
					slider.close();
				}
				
				commentAdapter.setTotalCount(commentAdapter.getTotalCount() + 1);
				commentAdapter.notifyDataSetChanged();
				
				header.setText(commentAdapter.getTotalCount() + " Comments");
				
				content.scrollToTop();
				
				if(dialog != null) {
					dialog.dismiss();
				}
			}
		});
		
		// Won't persist.. but that's ok.
		SocializeSession session = getSocialize().getSession();
		
		if(session != null && session.getUser() != null) {
			session.getUser().setAutoPostToFacebook(autoPostToFacebook);
		}
		
		if(getSocialize().isAuthenticated(AuthProviderType.FACEBOOK) && autoPostToFacebook) {
			facebookWallPoster.postComment(getActivity(), entityKey, entityName, comment, useLink, null);
		}
		
	}
	
	public void reload() {
		content.showLoading();
		commentAdapter.reset();
		doListComments(true);
	}

	public void doListComments(final boolean update) {

		startIndex = 0;
		endIndex = defaultGrabLength;

		loading = true;
		
		List<Comment> comments = commentAdapter.getComments();

		if(update || comments == null || comments.size() == 0) {
			getSocialize().listCommentsByEntity(entityKey, 
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
				}

				@Override
				public void onList(ListResult<Comment> entities) {
					int totalCount = entities.getTotalCount();
					header.setText(totalCount + " Comments");
					commentAdapter.setComments(entities.getItems());
					commentAdapter.setTotalCount(totalCount);

					if(totalCount <= endIndex) {
						commentAdapter.setLast(true);
					}

					if(update) {
						commentAdapter.notifyDataSetChanged();
						content.scrollToTop();
					}
					
					content.showList();

					if(dialog != null) {
						dialog.dismiss();
					}

					loading = false;
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
		}
	}

	protected void getNextSet() {
		
		if(logger != null && logger.isDebugEnabled()) {
			logger.info("getNextSet called on CommentListView");
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

		getSocialize().listCommentsByEntity(entityKey, 
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
			}

			@Override
			public void onList(ListResult<Comment> entities) {
				List<Comment> comments = commentAdapter.getComments();
				comments.addAll(entities.getItems());
				commentAdapter.setComments(comments);
				commentAdapter.notifyDataSetChanged();
				loading = false;
			}
		});
	}
	
//	@Override
//	protected void onViewLoad() {
//		super.onViewLoad();
//
//	}
	
	@Override
	protected void onViewRendered(int width, int height) {
		if(sliderFactory != null && slider == null) {
			slider = sliderFactory.wrap(getSliderAnchor(), ZOrder.FRONT, height);
		}
		
		if(commentEntryPage != null) {
			slider.loadItem(commentEntryPage);
			
			field.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(slider != null) {
						slider.showSliderItem(commentEntryPage);
					}
				}
			});		
		}
		else if(field != null) {
			field.setVisibility(GONE);
		}

		if(getSocialize().isAuthenticated()) {
			doListComments(false);
		}
		else {
			showError(getContext(), new SocializeException("Socialize not authenticated"));
			content.showList();
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

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setDefaultGrabLength(int defaultGrabLength) {
		this.defaultGrabLength = defaultGrabLength;
	}

	public void setCommentHeaderFactory(ViewFactory<CommentHeader> commentHeaderFactory) {
		this.commentHeaderFactory = commentHeaderFactory;
	}

	public void setCommentEditFieldFactory(ViewFactory<View> commentEditFieldFactory) {
		this.commentEditFieldFactory = commentEditFieldFactory;
	}

	public void setCommentContentViewFactory(ViewFactory<CommentContentView> commentContentViewFactory) {
		this.commentContentViewFactory = commentContentViewFactory;
	}

	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
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

	protected void setHeader(CommentHeader header) {
		this.header = header;
	}

	protected void setContent(CommentContentView content) {
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

	public void setFacebookWallPoster(FacebookWallPoster facebookWallPoster) {
		this.facebookWallPoster = facebookWallPoster;
	}

	public int getTotalCount() {
		return commentAdapter.getTotalCount();
	}

	public void setAuthRequestDialogFactory(IBeanFactory<AuthRequestDialogFactory> authRequestDialogFactory) {
		this.authRequestDialogFactory = authRequestDialogFactory;
	}
	
	public void setUseLink(boolean useLink) {
		this.useLink = useLink;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * Called when the current logged in user updates their profile.
	 */
	public void onProfileUpdate() {
		commentAdapter.notifyDataSetChanged();
		
		if(slider != null) {
			slider.clearContent();
		}
	}
	
	protected SocializeUI getSocializeUI() {
		return SocializeUI.getInstance();
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
}
