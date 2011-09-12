package com.socialize.ui.profile;

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
import com.socialize.ui.comment.CommentAdapter;
import com.socialize.ui.comment.CommentAddButtonListener;
import com.socialize.ui.comment.CommentButtonCallback;
import com.socialize.ui.comment.CommentContentView;
import com.socialize.ui.comment.CommentEditField;
import com.socialize.ui.comment.CommentHeader;
import com.socialize.ui.comment.CommentScrollCallback;
import com.socialize.ui.comment.CommentScrollListener;
import com.socialize.ui.dialog.ProgressDialogFactory;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.KeyboardUtils;
import com.socialize.ui.view.ViewFactory;
import com.socialize.util.DeviceUtils;
import com.socialize.util.Drawables;

public class ProfileEditView extends BaseView {

	private boolean loading = true; // Default to true
	
	private String userId;
	
	private SocializeLogger logger;
	private ProgressDialogFactory progressDialogFactory;
	private Drawables drawables;
	private ProgressDialog dialog = null;
	private Colors colors;
	private DeviceUtils deviceUtils;
	private KeyboardUtils keyboardUtils;
	
	private ViewFactory<ProfileHeader> profileHeaderFactory;
	private ViewFactory<ProfileEditField> profileEditFieldFactory;
//	private ViewFactory<ProfileImageView> commentEditFieldFactory;
	
	private ProfileEditField field;
	private ProfileHeader header;

	public ProfileEditView(Context context, String userId) {
		this(context);
		this.userId = userId;
	}
	
	public ProfileEditView(Context context) {
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

		header = profileHeaderFactory.make(getContext());
		field = profileEditFieldFactory.make(getContext());
//		field.setButtonListener(getCommentAddListener());

		addView(header);
		addView(field);
	}
	
	
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}


	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if(getSocialize().isAuthenticated()) {

		}
		else {
			showError(getContext(), "Socialize not authenticated");

		}
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

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public void setKeyboardUtils(KeyboardUtils keyboardUtils) {
		this.keyboardUtils = keyboardUtils;
	}

	public void setUserId(String entityKey) {
		this.userId = entityKey;
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
}
