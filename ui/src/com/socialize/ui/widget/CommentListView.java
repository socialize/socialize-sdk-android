package com.socialize.ui.widget;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import com.socialize.Socialize;
import com.socialize.api.SocializeSession;
import com.socialize.auth.AuthProviderType;
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
import com.socialize.ui.provider.SocializeCommentProvider;
import com.socialize.util.DeviceUtils;
import com.socialize.util.StringUtils;

public class CommentListView extends BaseView {
	
	private SocializeCommentProvider provider;
	private ViewFlipper flipper;
	private EditText editText;
	private SocializeButton button;
	private ListView listView;
	private boolean loading = true;
	
	private InputMethodManager imm;
	private String entityKey;
	private int startIndex = 0;
	private int grabLength = 20;
	private int totalCount = 0;
	private SocializeLogger logger;
	private ProgressDialogFactory progressDialogFactory;
	private ProgressDialog dialog = null;
	
	public CommentListView(
			final Context context, 
			final SocializeCommentProvider provider,
			final DeviceUtils deviceUtils, 
			final String entityKey) {
		
		super(context);
		
		this.provider = provider;
		this.entityKey = entityKey;
		
		int four = deviceUtils.getDIP(4);
		int eight = deviceUtils.getDIP(8);
		
		imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	
		LayoutParams fill = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(fill);
		setBackgroundColor(SocializeUI.STANDARD_BACKGROUND_COLOR);
		
		LinearLayout editPanel = new LinearLayout(context);
		LayoutParams editPanelLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		editPanelLayoutParams.setMargins(four, four, four, four);
		editPanel.setLayoutParams(editPanelLayoutParams);
		editPanel.setOrientation(LinearLayout.HORIZONTAL);
		editPanel.setPadding(eight, eight, eight, eight);
		
		LinearLayout.LayoutParams editTextLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT);
		editTextLayoutParams.gravity = Gravity.TOP;
		editTextLayoutParams.weight = 1.0f;
		editTextLayoutParams.setMargins(0, 0, four, 0);
		
		editText = new EditText(context);
	    editText.setImeOptions(EditorInfo.IME_ACTION_DONE);  
	    editText.setMinLines(1);  
	    editText.setMaxLines(5); 
	    editText.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
	    editText.setGravity(Gravity.TOP);
	    editText.setVerticalScrollBarEnabled(true);
	    editText.setVerticalFadingEdgeEnabled(true);
	    editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
	    editText.setBackgroundColor(Color.WHITE);
	    editText.setHint("Post a comment");
		editText.setLayoutParams(editTextLayoutParams);
		
		button = new SocializeButton(context, deviceUtils);
		button.setText("Post");
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String text = editText.getText().toString();
				if(!StringUtils.isEmpty(text)) {
					imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
					
					// TODO: add other providers
					if(!Socialize.getSocialize().isAuthenticated(AuthProviderType.FACEBOOK)) {
						Socialize.getSocialize().authenticate(
								Socialize.getSocialize().getConfig().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_KEY), 
								Socialize.getSocialize().getConfig().getProperty(SocializeConfig.SOCIALIZE_CONSUMER_SECRET),
								AuthProviderType.FACEBOOK, 
								Socialize.getSocialize().getConfig().getProperty(SocializeConfig.FACEBOOK_APP_ID),
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
		
		//Here is where the magic happens
		listView.setOnScrollListener(new OnScrollListener(){
			//useless here, skip!
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}
			//dumdumdum
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
				//what is the bottom iten that is visible
				int lastInScreen = firstVisibleItem + visibleItemCount;
				
				boolean last = (lastInScreen == totalItemCount);
				
				if(last && !loading) {
					loading = true;
					
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
		
		ProgressBar progress = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
		progress.setLayoutParams(progressLayoutParams);
		
		loadingScreen.addView(progress);
		
		flipper.addView(loadingScreen);
		flipper.addView(contentView);
		
		flipper.setDisplayedChild(0);
		
		addView(editPanel);
		addView(flipper);
	}
	
	public void doPostComment(String comment) {
		dialog = progressDialogFactory.show(getContext(), "Posting comment", "Please wait...");

		Socialize.getSocialize().addComment(entityKey, comment, new CommentAddListener() {
			
			@Override
			public void onError(SocializeException error) {
				showError(getContext(), error.getMessage());
				dialog.dismiss();
			}
			
			@Override
			public void onCreate(Comment entity) {
				doListComments();
			}
		});
		
	}
	
	public void doListComments() {
		
		startIndex = 0;
		grabLength = 20;
		
		Socialize.getSocialize().listCommentsByEntity(entityKey, 
			startIndex,
			grabLength,
			new CommentListListener() {
			
			@Override
			public void onError(SocializeException error) {
				showError(getContext(), error.getMessage());
				flipper.setDisplayedChild(1);
				if(dialog != null) {
					dialog.dismiss();
				}
			}
			
			@Override
			public void onList(ListResult<Comment> entities) {
				totalCount = entities.getTotalCount();
				provider.setComments(entities.getItems());
				
				if(totalCount <= grabLength) {
					provider.setLast(true);
				}
				
				provider.notifyDataSetChanged();
				flipper.setDisplayedChild(1);
				editText.setText("");
				loading = false;
				if(dialog != null) {
					dialog.dismiss();
				}
			}
		});
	}
	

	private void getNextSet() {
		
		startIndex+=grabLength;
		grabLength+=grabLength;
		
		if(grabLength > totalCount) {
			grabLength = totalCount;
			
			if(startIndex >= grabLength) {
				provider.setLast(true);
				provider.notifyDataSetChanged();
				loading = false;
				return;
			}
		}
		
		Socialize.getSocialize().listCommentsByEntity(entityKey, 
				startIndex,
				grabLength,
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
			doListComments();
		}
		else {
			showError(getContext(), "Socialize not authenticated");
			flipper.setDisplayedChild(1);
		}
	}

	public SocializeCommentProvider getProvider() {
		return provider;
	}

	public void setProvider(SocializeCommentProvider provider) {
		this.provider = provider;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getGrabLength() {
		return grabLength;
	}

	public void setGrabLength(int grabLength) {
		this.grabLength = grabLength;
	}

	public SocializeLogger getLogger() {
		return logger;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public ProgressDialogFactory getProgressDialogFactory() {
		return progressDialogFactory;
	}

	public void setProgressDialogFactory(ProgressDialogFactory progressDialogFactory) {
		this.progressDialogFactory = progressDialogFactory;
	}
	
}
