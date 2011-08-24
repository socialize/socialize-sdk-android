package com.socialize.ui.comment;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
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
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;
import com.socialize.util.StringUtils;

//TODO: Remove this annotation
@SuppressWarnings("unused")
public class CommentListView extends BaseView {

	private int defaultGrabLength = 10;
	private CommentAdapter provider;
	private ViewFlipper flipper;
	private EditText editText;
	ImageButton button;
	private ListView listView;
	private boolean loading = true; // Default to true

	private InputMethodManager imm;
	private String entityKey;
	private int startIndex = 0;
	private int endIndex = defaultGrabLength;
	private int totalCount = 0;
	private SocializeLogger logger;
	private ProgressDialogFactory progressDialogFactory;
	private Drawables drawables;
	private ProgressDialog dialog = null;
	private Colors colors;
	private TextView titleText;

	public CommentListView(
			final Context context, 
			final CommentAdapter provider,
			final DeviceUtils deviceUtils, 
			final Drawables drawables,
			final Colors colors,
			final String entityKey) {

		super(context);

		this.provider = provider;
		this.entityKey = entityKey;
		this.drawables = drawables;
		this.colors = colors;

		int four = deviceUtils.getDIP(4);
		int eight = deviceUtils.getDIP(8);

		imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);

		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		setBackgroundDrawable(drawables.getDrawable("crosshatch.png", true, true, true));
		setPadding(0, 0, 0, 0);
		setVerticalFadingEdgeEnabled(false);

		LinearLayout titlePanel = new LinearLayout(context);
		LayoutParams titlePanelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		titlePanelLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		titlePanel.setLayoutParams(titlePanelLayoutParams);
		titlePanel.setOrientation(LinearLayout.HORIZONTAL);
		titlePanel.setPadding(four, four, four, four);
		titlePanel.setBackgroundDrawable(drawables.getDrawable("header.png", true, false, true));

		titleText = new TextView(context);
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		titleText.setTextColor(colors.getColor(Colors.HEADER));
		titleText.setText("Comments");
		titleText.setPadding(0, 0, 0, deviceUtils.getDIP(2));

		LayoutParams titleTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		titleTextLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		//		titleTextLayoutParams.setMargins(four, 0, four, 0);
		titleText.setLayoutParams(titleTextLayoutParams);

		ImageView titleImage = new ImageView(context);
		titleImage.setImageDrawable(drawables.getDrawable("socialize_icon_white.png", true));
		titleImage.setPadding(0, 0, 0, 0);

		LayoutParams titleImageLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		titleImageLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		titleImageLayoutParams.setMargins(four, 0, four, 0);
		titleImage.setLayoutParams(titleImageLayoutParams);

		titlePanel.addView(titleImage);
		titlePanel.addView(titleText);

		LinearLayout editPanel = new LinearLayout(context);
		LayoutParams editPanelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		editPanelLayoutParams.setMargins(eight, eight, eight, eight);
		editPanel.setLayoutParams(editPanelLayoutParams);
		editPanel.setOrientation(LinearLayout.HORIZONTAL);
		editPanel.setPadding(0, 0, 0, 0);

		LinearLayout.LayoutParams editTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
		editTextLayoutParams.gravity = Gravity.TOP;
		editTextLayoutParams.weight = 1.0f;
		editTextLayoutParams.setMargins(0, 0, four, 0);

		editText = new EditText(context);
		editText.setImeOptions(EditorInfo.IME_ACTION_DONE);  
		editText.setMinLines(1);  
		editText.setMaxLines(5); 
		editText.setMinHeight(deviceUtils.getDIP(42)); 
		editText.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		editText.setGravity(Gravity.TOP);
		editText.setVerticalScrollBarEnabled(true);
		editText.setVerticalFadingEdgeEnabled(true);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		editText.setBackgroundColor(colors.getColor(Colors.TEXT_BG));
		editText.setHint("Write a comment...");
		editText.setLayoutParams(editTextLayoutParams);

		LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(deviceUtils.getDIP(42),deviceUtils.getDIP(42));
		buttonLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;

		int bottom = colors.getColor(Colors.BUTTON_BOTTOM);
		int top = colors.getColor(Colors.BUTTON_TOP);

		button = new ImageButton(context);
		button.setImageDrawable(drawables.getDrawable("post_icon.png", true));

		GradientDrawable foreground = new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
				new int[] { bottom, top });

		button.setBackgroundDrawable(foreground);
		button.setLayoutParams(buttonLayoutParams);
		
		final String consumerKey = SocializeUI.getInstance().getCustomConfigValue(getContext(),SocializeConfig.SOCIALIZE_CONSUMER_KEY);
		final String consumerSecret = SocializeUI.getInstance().getCustomConfigValue(getContext(),SocializeConfig.SOCIALIZE_CONSUMER_SECRET);
//		final String facebookAppId = SocializeUI.getInstance().getCustomConfigValue(getContext(),SocializeConfig.FACEBOOK_APP_ID);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String text = editText.getText().toString();
				if(!StringUtils.isEmpty(text)) {
					imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

					// TODO: add other providers
					// TODO: enable FB auth
//					if(!Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
					if(!Socialize.getSocialize().isAuthenticated()) {
						Socialize.getSocialize().authenticate(
								consumerKey, 
								consumerSecret,
//								AuthProviderType.FACEBOOK, 
//								facebookAppId,
								new SocializeAuthListener() {

									@Override
									public void onError(SocializeException error) {
										showError(context, error.getMessage());
									}

									@Override
									public void onAuthSuccess(SocializeSession session) {
										doPostComment(text);
									}

									@Override
									public void onAuthFail(SocializeException error) {
										showError(context, error.getMessage());
									}
								});
					}
					else {
						doPostComment(text);
					}
				}
			}
		});

		editPanel.addView(editText);
		editPanel.addView(button);

		LinearLayout contentView = new LinearLayout(context);
		LayoutParams contentViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		contentViewLayoutParams.weight = 1.0f;
		contentView.setLayoutParams(contentViewLayoutParams);
		contentView.setOrientation(LinearLayout.VERTICAL);

		LayoutParams listViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		listViewLayoutParams.weight = 1.0f;

		listView = new ListView(context);
		listView.setAdapter(provider);
		listView.setLayoutParams(listViewLayoutParams);
		listView.setDrawingCacheEnabled(true);
		listView.setCacheColorHint(0);
		listView.setDividerHeight(2);
		listView.setSmoothScrollbarEnabled(true);

		listView.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//what is the bottom item that is visible
				int lastInScreen = firstVisibleItem + visibleItemCount;

				boolean last = (lastInScreen == totalItemCount);

				if(last && !loading) {
					// Get next set...
					getNextSet();
				}
			}
		});

		listView.requestFocus();

		contentView.addView(listView);

		// Create a view flipper to show a loading screen...
		flipper = new ViewFlipper(context);

		LayoutParams flipperLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		flipperLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		flipper.setLayoutParams(flipperLayoutParams);

		// create a loading screen
		FrameLayout loadingScreen = new FrameLayout(context);
		FrameLayout.LayoutParams loadingScreenLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT);
		FrameLayout.LayoutParams progressLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);

		loadingScreenLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
		progressLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;

		loadingScreen.setLayoutParams(loadingScreenLayoutParams);

		ProgressBar progress = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
		progress.setLayoutParams(progressLayoutParams);

		loadingScreen.addView(progress);

		flipper.addView(loadingScreen);
		flipper.addView(contentView);

		flipper.setDisplayedChild(0);

		addView(titlePanel);
		addView(editPanel);
		addView(flipper);
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
				List<Comment> comments = provider.getComments();
				comments.add(0, entity);
				totalCount++;
				startIndex++;
				endIndex++;
				titleText.setText(totalCount + " Comments");
				editText.setText("");
				provider.notifyDataSetChanged();
				listView.setSelection(0); // scroll to top
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

		if(update || provider.getComments() == null || provider.getComments().size() == 0) {
			Socialize.getSocialize().listCommentsByEntity(entityKey, 
					startIndex,
					endIndex,
					new CommentListListener() {

				@Override
				public void onError(SocializeException error) {
					showError(getContext(), error.getMessage());
					flipper.setDisplayedChild(1);
					if(dialog != null) {
						dialog.dismiss();
					}

					loading = false;
				}

				@Override
				public void onList(ListResult<Comment> entities) {
					totalCount = entities.getTotalCount();
					titleText.setText(totalCount + " Comments");
					provider.setComments(entities.getItems());

					if(totalCount <= endIndex) {
						provider.setLast(true);
					}

					provider.notifyDataSetChanged();
					flipper.setDisplayedChild(1);

					if(dialog != null) {
						dialog.dismiss();
					}

					loading = false;
				}
			});
		}
		else {
			flipper.setDisplayedChild(1);

			provider.notifyDataSetChanged();
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
				provider.setLast(true);
				provider.notifyDataSetChanged();
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
				List<Comment> comments = provider.getComments();
				comments.addAll(entities.getItems());
				provider.setComments(comments);
				provider.notifyDataSetChanged();
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
			flipper.setDisplayedChild(1);
		}
	}

	public void setProvider(CommentAdapter provider) {
		this.provider = provider;
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
}
