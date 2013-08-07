package com.socialize.ui.comment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.socialize.entity.Comment;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.util.Colors;
import com.socialize.ui.util.CompatUtils;
import com.socialize.ui.view.CachedImageView;
import com.socialize.util.DisplayUtils;
import com.socialize.util.Drawables;

public class CommentListItem extends LinearLayout {
	
	private TextView commentText;
	private TextView time;
	private TextView author;
	private CachedImageView userIcon;
	private ImageView locationIcon;
	private DisplayUtils displayUtils;
	private Colors colors;
	private Drawables drawables;
	private SocializeLogger logger;
	private Comment commentObject;
	private boolean deleteOk = false;

	private LinearLayout contentLayout;
	private LinearLayout iconLayout;
	
	@SuppressWarnings("unused")
	private CommentListItemBackgroundFactory backgroundFactory;
	
	public CommentListItem(Context context) {
		super(context);
	}

	public void init() {
		
		final int eight = displayUtils.getDIP(8);
		final int four = displayUtils.getDIP(4);
		final int imagePadding = displayUtils.getDIP(2);
		final int textColor = colors.getColor(Colors.COMMENT_BODY);
		final int titleColor = colors.getColor(Colors.COMMENT_TITLE);
		final int iconSize = displayUtils.getDIP(64);
		
		ListView.LayoutParams layout = new ListView.LayoutParams(ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT);
		
		setBackgroundColor(colors.getColor(Colors.LIST_ITEM_BG));
		
		setOrientation(LinearLayout.HORIZONTAL);
		setLayoutParams(layout);
		setGravity(Gravity.TOP);
		setPadding(eight,eight,eight,eight);
		
		contentLayout = new LinearLayout(getContext());
		contentLayout.setOrientation(LinearLayout.VERTICAL);
		contentLayout.setGravity(Gravity.LEFT);
		contentLayout.setPadding(0, 0, 0, 0);
		
		LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		contentLayoutParams.setMargins(four, 0, 0, 0);
		contentLayout.setLayoutParams(contentLayoutParams);
		
		LinearLayout contentHeaderLayout = new LinearLayout(getContext());
		LinearLayout.LayoutParams contentHeaderLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		contentHeaderLayout.setLayoutParams(contentHeaderLayoutParams);
		contentHeaderLayout.setGravity(Gravity.LEFT);
		contentHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
		contentHeaderLayout.setPadding(0, 0, 0, 0);
		
		LinearLayout.LayoutParams authorLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		authorLayoutParams.weight = 1.0f;
		
		author = new TextView(getContext());
		author.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		author.setMaxLines(1);
		author.setTypeface(Typeface.DEFAULT_BOLD);
		author.setTextColor(titleColor);
		author.setLayoutParams(authorLayoutParams);
		author.setSingleLine();
		
		LinearLayout.LayoutParams commentLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		commentText = new TextView(getContext());
		commentText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		commentText.setTextColor(textColor);
		commentText.setLayoutParams(commentLayoutParams);

		LinearLayout.LayoutParams timeLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		time = new TextView(getContext());
		time.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		time.setMaxLines(1);
		time.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		time.setTextColor(titleColor);
		time.setLayoutParams(timeLayoutParams);
		time.setSingleLine();
		time.setGravity(Gravity.RIGHT);

		LinearLayout.LayoutParams locationIconParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		locationIconParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		
		locationIcon = new ImageView(getContext());
		locationIcon.setImageDrawable(drawables.getDrawable("icon_location_pin.png"));
		locationIcon.setLayoutParams(locationIconParams);
		
		LinearLayout.LayoutParams metaParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		metaParams.gravity = Gravity.RIGHT | Gravity.TOP;
		
		LinearLayout meta = new LinearLayout(getContext());
		
		meta.setOrientation(HORIZONTAL);
		meta.setLayoutParams(metaParams);
		meta.addView(time);
		meta.addView(locationIcon);
		
		contentHeaderLayout.addView(author);
		contentHeaderLayout.addView(meta);
		
		contentLayout.addView(contentHeaderLayout);
		contentLayout.addView(commentText);

		LinearLayout.LayoutParams iconLayoutParams = new LinearLayout.LayoutParams(iconSize, iconSize);
		iconLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;

		iconLayout = new LinearLayout(getContext());
		iconLayout.setLayoutParams(iconLayoutParams);
		iconLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		
		userIcon = new CachedImageView(getContext());
		userIcon.setDrawables(drawables);
		userIcon.setLogger(logger);
		userIcon.setDefaultImage();
		userIcon.setLayoutParams(iconLayoutParams);
		userIcon.setPadding(imagePadding, imagePadding, imagePadding, imagePadding);
		
		GradientDrawable imageBG = new GradientDrawable(Orientation.BOTTOM_TOP, new int[] {Color.WHITE, Color.WHITE});
		imageBG.setStroke(displayUtils.getDIP(1), Color.BLACK);

		CompatUtils.setBackgroundDrawable(userIcon, imageBG);

		iconLayout.addView(userIcon);
		
		addView(iconLayout);
		addView(contentLayout);
	}
	
	public TextView getCommentText() {
		return commentText;
	}

	public TextView getTime() {
		return time;
	}

	public TextView getAuthor() {
		return author;
	}

	public CachedImageView getUserIcon() {
		return userIcon;
	}
	
	public ImageView getLocationIcon() {
		return locationIcon;
	}

	public void setDisplayUtils(DisplayUtils deviceUtils) {
		this.displayUtils = deviceUtils;
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setBackgroundFactory(CommentListItemBackgroundFactory backgroundFactory) {
		this.backgroundFactory = backgroundFactory;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}
	
	public Comment getCommentObject() {
		return commentObject;
	}
	
	public void setCommentObject(Comment commentObject) {
		this.commentObject = commentObject;
	}
	
	public boolean isDeleteOk() {
		return deleteOk;
	}

	public void setDeleteOk(boolean deleteOk) {
		this.deleteOk = deleteOk;
	}

	public LinearLayout getContentLayout() {
		return contentLayout;
	}

	public LinearLayout getIconLayout() {
		return iconLayout;
	}
}
