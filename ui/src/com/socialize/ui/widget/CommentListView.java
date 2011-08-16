package com.socialize.ui.widget;

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
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.socialize.Socialize;
import com.socialize.entity.Comment;
import com.socialize.entity.ListResult;
import com.socialize.error.SocializeException;
import com.socialize.listener.comment.CommentAddListener;
import com.socialize.listener.comment.CommentListListener;
import com.socialize.ui.BaseView;
import com.socialize.ui.SocializeUI;
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
	private ProgressDialog dialog;
	private InputMethodManager imm;
	private String entityKey;
	
	public CommentListView(
			final Context context, 
			final SocializeCommentProvider provider,
			final DeviceUtils deviceUtils, 
			final String entityKey) {
		
		super(context);
		
		this.provider = provider;
		this.entityKey = entityKey;
		
		int four = deviceUtils.getDIP(4);
		
		dialog = new ProgressDialog(context);
		
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
	    editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
	    editText.setBackgroundColor(Color.WHITE);
	    editText.setHint("Post a comment");
		editText.setLayoutParams(editTextLayoutParams);
		
		button = new SocializeButton(context, deviceUtils);
		button.setText("Post");
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = editText.getText().toString();
				if(!StringUtils.isEmpty(text)) {
					imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
					doPostComment(text);
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
					Toast.makeText(context, "Last", Toast.LENGTH_SHORT).show();
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
		
//		addView(logo);
		addView(editPanel);
		addView(flipper);
	}
	
	public void doPostComment(String comment) {
		
		dialog.setTitle("Posting comment");
		dialog.setMessage("Please wait...");
		dialog.show();
		
		Socialize.getSocialize().addComment("http://aaa.com", comment, new CommentAddListener() {
			
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
		Socialize.getSocialize().listCommentsByEntity(entityKey, new CommentListListener() {
			
			@Override
			public void onError(SocializeException error) {
				showError(getContext(), error.getMessage());
				flipper.setDisplayedChild(1);
				dialog.dismiss();
			}
			
			@Override
			public void onList(ListResult<Comment> entities) {
				provider.setComments(entities.getItems());
				provider.setTotalCount(entities.getTotalCount());
				provider.notifyDataSetChanged();
				flipper.setDisplayedChild(1);
				editText.setText("");
				loading = false;
				dialog.dismiss();
			}
		});
	}
	

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		if(Socialize.getSocialize().isInitialized()) {
			if(Socialize.getSocialize().isAuthenticated()) {
				doListComments();
			}
			else {
				showError(getContext(), "Socialize not authenticated");
				flipper.setDisplayedChild(1);
			}
		}
		else {
			showError(getContext(), "Socialize not initialized");
			flipper.setDisplayedChild(1);
		}
	}

	public SocializeCommentProvider getProvider() {
		return provider;
	}

	public void setProvider(SocializeCommentProvider provider) {
		this.provider = provider;
	}

}
