package com.socialize.ui.comment;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.config.SocializeConfig;
import com.socialize.entity.Comment;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.SocializeAuthListener;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.BaseView;
import com.socialize.ui.SocializeUI;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.KeyboardUtils;
import com.socialize.ui.view.ViewFactory;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

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
	
	private CommentHeaderFactory commentHeaderFactory;
	private CommentEditFieldFactory commentEditFieldFactory;
	private CommentContentViewFactory commentContentViewFactory;
	
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

	/**
	 * @deprecated Prefer parameterless constructor
	 * @param context
	 * @param provider
	 * @param deviceUtils
	 * @param drawables
	 * @param colors
	 * @param entityKey
	 */
	@Deprecated
	public CommentListView(
			final Context context, 
			final CommentAdapter commentAdapter,
			final DeviceUtils deviceUtils, 
			final Drawables drawables,
			final Colors colors,
			final String entityKey) {

		super(context);

		this.commentAdapter = commentAdapter;
		this.entityKey = entityKey;
		this.drawables = drawables;
		this.colors = colors;
		this.deviceUtils = deviceUtils;
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

		field.setButtonListener(new CommentAddButtonListener(getContext(), field, new CommentButtonCallback() {
			@Override
			public void onError(Context context, String message) {
				showError(getContext(), message);
			}
			
			@Override
			public void onComment(String text) {
				doPostComment(text);
			}
		}, keyboardUtils));
		
		content.setListAdapter(commentAdapter);
		content.setScrollListener(new CommentScrollListener(new CommentScrollCallback() {
			@Override
			public void onGetNextSet() {
				getNextSet();
			}
			
			@Override
			public boolean isLoading() {
				return loading;
			}
		}));

		addView(header);
		addView(field);
		addView(content);
	}

	public void doPostComment(String comment) {
		dialog = progressDialogFactory.show(getContext(), "Posting comment", "Please wait...");

		Socialize.getSocialize().addComment(entityKey, comment, new CommentAddListener() {

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

	public void doListComments(boolean update) {

		startIndex = 0;
		endIndex = defaultGrabLength;

		loading = true;

		if(update || commentAdapter.getComments() == null || commentAdapter.getComments().size() == 0) {
			Socialize.getSocialize().listCommentsByEntity(entityKey, 
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

	private void getNextSet() {
		
		loading = true; // Prevent continuous load

		startIndex+=defaultGrabLength;
		endIndex+=defaultGrabLength;

		if(endIndex > totalCount) {
			endIndex = totalCount;

			if(startIndex >= endIndex) {
				commentAdapter.setLast(true);
				commentAdapter.notifyDataSetChanged();
				return;
			}
		}

		Socialize.getSocialize().listCommentsByEntity(entityKey, 
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

		if(Socialize.getSocialize().isAuthenticated()) {
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

	public void setCommentHeaderFactory(CommentHeaderFactory commentHeaderFactory) {
		this.commentHeaderFactory = commentHeaderFactory;
	}

	public void setCommentEditFieldFactory(CommentEditFieldFactory commentEditFieldFactory) {
		this.commentEditFieldFactory = commentEditFieldFactory;
	}

	public void setCommentContentViewFactory(CommentContentViewFactory commentContentViewFactory) {
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
}
