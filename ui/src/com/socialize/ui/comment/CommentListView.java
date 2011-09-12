package com.socialize.ui.comment;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.LinearLayout;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.entity.Comment;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.BaseView;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.KeyboardUtils;
import com.socialize.ui.view.ViewFactory;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

//TODO: Remove this annotation
@SuppressWarnings("unused")
public class CommentListView extends BaseView {

	private int defaultGrabLength = 20;
	private CommentAdapter commentAdapter;
	private boolean loading = true; // Default to true
	
	private String entityKey;
	private int startIndex = 0;
	private int endIndex = defaultGrabLength;
	private int totalCount = 0;
	
	private SocializeLogger logger;
	private ProgressDialogFactory progressDialogFactory;
	private Drawables drawables;
	private ProgressDialog dialog = null;
	private Colors colors;
	private DeviceUtils deviceUtils;
	private KeyboardUtils keyboardUtils;
	
	private ViewFactory<CommentHeader> commentHeaderFactory;
	private ViewFactory<CommentEditField> commentEditFieldFactory;
	private ViewFactory<CommentContentView> commentContentViewFactory;
	
	private CommentEditField field;
	private CommentHeader header;
	private CommentContentView content;

	public CommentListView(Context context, String entityKey) {
		this(context);
		this.entityKey = entityKey;
	}
	
	public CommentListView(Context context) {
		super(context);
	}

	public void init() {

		int four = deviceUtils.getDIP(4);
		int eight = deviceUtils.getDIP(8);

		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);

		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		setBackgroundDrawable(drawables.getDrawable("crosshatch.png", true, true, true));
		setPadding(0, 0, 0, 0);
		setVerticalFadingEdgeEnabled(false);

		header = commentHeaderFactory.make(getContext());
		field = commentEditFieldFactory.make(getContext());
		content = commentContentViewFactory.make(getContext());

		field.setButtonListener(getCommentAddListener());
		
		content.setListAdapter(commentAdapter);
		
		content.setScrollListener(getCommentScrollListener());

		addView(header);
		addView(field);
		addView(content);
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
		return new CommentAddButtonListener(getContext(), field, new CommentButtonCallback() {
			@Override
			public void onError(Context context, String message) {
				showError(getContext(), message);
			}
			
			@Override
			public void onComment(String text) {
				doPostComment(text);
			}
		}, keyboardUtils);
	}

	public void doPostComment(String comment) {
		dialog = progressDialogFactory.show(getContext(), "Posting comment", "Please wait...");

		getSocialize().addComment(entityKey, comment, new CommentAddListener() {

			@Override
			public void onError(SocializeException error) {
				showError(getContext(), error.getMessage());
				if(dialog != null) {
					dialog.dismiss();
				}
			}

			@Override
			public void onCreate(Comment entity) {
				List<Comment> comments = commentAdapter.getComments();
				comments.add(0, entity);
				totalCount++;
				startIndex++;
				endIndex++;
				header.setText(totalCount + " Comments");
				field.clear();
				commentAdapter.notifyDataSetChanged();
				content.scrollToTop();
				if(dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	public void doListComments(boolean update) {

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
					showError(getContext(), error.getMessage());
					content.showList();
					
					if(dialog != null) {
						dialog.dismiss();
					}

					loading = false;
				}

				@Override
				public void onList(ListResult<Comment> entities) {
					totalCount = entities.getTotalCount();
					header.setText(totalCount + " Comments");
					commentAdapter.setComments(entities.getItems());

					if(totalCount <= endIndex) {
						commentAdapter.setLast(true);
					}

					commentAdapter.notifyDataSetChanged();
					
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

			commentAdapter.notifyDataSetChanged();
			if(dialog != null) {
				dialog.dismiss();
			}

			loading = false;
		}
	}

	protected void getNextSet() {
		
		loading = true; // Prevent continuous load

		startIndex+=defaultGrabLength;
		endIndex+=defaultGrabLength;

		if(endIndex > totalCount) {
			endIndex = totalCount;

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
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if(getSocialize().isAuthenticated()) {
			doListComments(false);
		}
		else {
			showError(getContext(), "Socialize not authenticated");
			content.showList();
		}
	}

	public void setCommentAdapter(CommentAdapter commentAdapter) {
		this.commentAdapter = commentAdapter;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setDefaultGrabLength(int defaultGrabLength) {
		this.defaultGrabLength = defaultGrabLength;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public void setCommentHeaderFactory(ViewFactory<CommentHeader> commentHeaderFactory) {
		this.commentHeaderFactory = commentHeaderFactory;
	}

	public void setCommentEditFieldFactory(ViewFactory<CommentEditField> commentEditFieldFactory) {
		this.commentEditFieldFactory = commentEditFieldFactory;
	}

	public void setCommentContentViewFactory(ViewFactory<CommentContentView> commentContentViewFactory) {
		this.commentContentViewFactory = commentContentViewFactory;
	}

	public void setKeyboardUtils(KeyboardUtils keyboardUtils) {
		this.keyboardUtils = keyboardUtils;
	}

	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}

	public void setDeviceUtils(DeviceUtils deviceUtils) {
		this.deviceUtils = deviceUtils;
	}

	public boolean isLoading() {
		return loading;
	}

	protected void setLoading(boolean loading) {
		this.loading = loading;
	}

	protected void setField(CommentEditField field) {
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

	protected void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public int getTotalCount() {
		return totalCount;
	}
}
