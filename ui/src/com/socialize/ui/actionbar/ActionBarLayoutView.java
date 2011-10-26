/*
 * Copyright (c) 2011 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.ui.actionbar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Gravity;

import com.socialize.Socialize;
import com.socialize.SocializeService;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.entity.Entity;
import com.socialize.entity.View;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.listener.view.ViewAddListener;
import com.socialize.log.SocializeLogger;
import com.socialize.ui.BaseView;
import com.socialize.ui.SocializeUI;
import com.socialize.util.Drawables;

/**
 * @author Jason Polites
 *
 */
public class ActionBarLayoutView extends BaseView {

	private String entityKey;
	private ActionBarButton commentButton;
	private ActionBarButton likeButton;
	private ActionBarButton viewButton;
	
	private Drawables drawables;
	private SocializeLogger logger;
	
	private IBeanFactory<ActionBarButton> buttonFactory;
	
	public ActionBarLayoutView(Activity context, String entityKey) {
		super(context);
		this.entityKey = entityKey;
	}
	
	public void init(final Activity context) {
		Drawable commentIcon = drawables.getDrawable("icon_comment.png");
		Drawable likeIcon = drawables.getDrawable("icon_like.png");
		Drawable viewIcon = drawables.getDrawable("icon_view.png");
		Drawable commentBg = drawables.getDrawable("action_bar_button_hi.png", true, false, true);
		Drawable viewBg = drawables.getDrawable("action_bar_button.png", true, false, true);
		
		viewButton = buttonFactory.getBean();
		likeButton = buttonFactory.getBean();
		commentButton = buttonFactory.getBean();
		
		viewButton.setIcon(viewIcon);
		viewButton.setBackground(viewBg);
		
		commentButton.setIcon(commentIcon);
		commentButton.setBackground(commentBg);
		
		commentButton.setListener(new ActionBarButtonListener() {
			@Override
			public void onClick(ActionBarButton button) {
				SocializeUI.getInstance().showCommentView(context, entityKey);
			}
		});
		
		likeButton.setIcon(likeIcon);
		likeButton.setBackground(commentBg);
		
		LayoutParams masterParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		masterParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		setLayoutParams(masterParams);
		
		viewButton.init(context, LayoutParams.FILL_PARENT, 1.0f);
		likeButton.init(context, 100, 0.0f);
		commentButton.init(context, 100, 0.0f);
		
		viewButton.setText("--");
		likeButton.setText("--");
		commentButton.setText("--");

		
		addView(viewButton);
		addView(likeButton);
		addView(commentButton);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getSocialize().view(entityKey, new ViewAddListener() {
			@Override
			public void onError(SocializeException error) {
				error.printStackTrace();
				getEntityData();
			}
			
			@Override
			public void onCreate(View entity) {
				getEntityData();
			}
		});
	}
	
	protected void getEntityData() {
		getSocialize().getEntity(entityKey, new EntityGetListener() {
			
			@Override
			public void onGet(Entity entity) {
				viewButton.setText(entity.getViews().toString());
				likeButton.setText(entity.getLikes().toString());
				commentButton.setText(entity.getComments().toString());
			}
			
			@Override
			public void onError(SocializeException error) {
				if(logger != null) {
					logger.error("Error retrieving entity data", error);
				}
				else {
					error.printStackTrace();	
				}
			}
		});
	}
	
	// So we can mock for tests
	protected SocializeService getSocialize() {
		return Socialize.getSocialize();
	}

	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}

	public void setCommentButton(ActionBarButton commentButton) {
		this.commentButton = commentButton;
	}

	public void setLikeButton(ActionBarButton likeButton) {
		this.likeButton = likeButton;
	}

	public void setViewButton(ActionBarButton viewButton) {
		this.viewButton = viewButton;
	}

	public String getEntityKey() {
		return entityKey;
	}

	public ActionBarButton getCommentButton() {
		return commentButton;
	}

	public ActionBarButton getLikeButton() {
		return likeButton;
	}

	public ActionBarButton getViewButton() {
		return viewButton;
	}

	public void setDrawables(Drawables drawables) {
		this.drawables = drawables;
	}

	public void setLogger(SocializeLogger logger) {
		this.logger = logger;
	}

	public void setButtonFactory(IBeanFactory<ActionBarButton> buttonFactory) {
		this.buttonFactory = buttonFactory;
	}
	
	
}
