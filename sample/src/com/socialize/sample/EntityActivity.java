package com.socialize.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.socialize.Socialize;
import com.socialize.entity.Entity;
import com.socialize.error.SocializeException;
import com.socialize.listener.entity.EntityGetListener;
import com.socialize.sample.util.ErrorHandler;

public class EntityActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entity);
		
		Socialize.init(this);
		
		final EditText txtKey = (EditText) findViewById(R.id.txtKey);
//		final EditText txtName = (EditText) findViewById(R.id.txtName);
		final TextView txtEntityCreateResult = (TextView) findViewById(R.id.txtEntityCreateResult);
		
//		final Button btnEntityCreate = (Button) findViewById(R.id.btnEntityCreate);
		final Button btnEntityGet = (Button) findViewById(R.id.btnEntityGet);
		
		
		if(Socialize.getSocialize().isAuthenticated()) {
			
//			btnEntityCreate.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					
//					txtEntityCreateResult.setText("");
//					btnEntityCreate.setEnabled(false);
//					
//					String key = txtKey.getText().toString();
//					String name = txtName.getText().toString();
//					
//					Socialize.getSocialize().createEntity(key, name, new EntityCreateListener() {
//						
//						@Override
//						public void onError(SocializeException error) {
//							txtEntityCreateResult.setText("FAIL: " + ErrorHandler.handleApiError(EntityActivity.this, error));
//							btnEntityCreate.setEnabled(true);
//						}
//						
//						@Override
//						public void onCreate(Entity entity) {
//							btnEntityCreate.setEnabled(true);
//							txtEntityCreateResult.setText("SUCCESS");
//							populateEntityData(entity);
//						}
//					});
//				}
//			});
			
			
			btnEntityGet.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					txtEntityCreateResult.setText("");
					btnEntityGet.setEnabled(false);
					String key = txtKey.getText().toString();
					
					Socialize.getSocialize().getEntity(key, new EntityGetListener() {
						
						@Override
						public void onError(SocializeException error) {
							txtEntityCreateResult.setText("FAIL: " + ErrorHandler.handleApiError(EntityActivity.this, error));
							btnEntityGet.setEnabled(true);
						}
						
						@Override
						public void onGet(Entity entity) {
					
							btnEntityGet.setEnabled(true);
							
							if(entity != null) {
								txtEntityCreateResult.setText("SUCCESS");
								populateEntityData(entity);
							}
							else {
								txtEntityCreateResult.setText("NOT FOUND");
							}
						}
					});
				}
			});
		}
		else {
			// Not authorized, you would normally do a re-auth here
			txtEntityCreateResult.setText("AUTH FAIL");
		}
	}
	
	private void populateEntityData(Entity entity) {
		final TextView txtEntityIdCreated = (TextView) findViewById(R.id.txtEntityIdCreated);
		final TextView txtEntityKeyCreated = (TextView) findViewById(R.id.txtEntityKeyCreated);
		final TextView txtEntityNameCreated = (TextView) findViewById(R.id.txtEntityNameCreated);
		final TextView txtEntityShares = (TextView) findViewById(R.id.txtEntityShares);
		final TextView txtEntityLikes = (TextView) findViewById(R.id.txtEntityLikes);
		final TextView txtEntityViews = (TextView) findViewById(R.id.txtEntityViews);
		final TextView txtEntityComments = (TextView) findViewById(R.id.txtEntityComments);
		
		
		if(entity.getId() != null) {
			txtEntityIdCreated.setText(entity.getId());
		}
		
		if(entity.getName() != null) {
			txtEntityNameCreated.setText(entity.getName());
		}
		
		if(entity.getKey() != null) {
			txtEntityKeyCreated.setText(entity.getKey());
		}
		
		if(entity.getShares() != null) {
			txtEntityShares.setText(entity.getShares().toString());
		}
		
		if(entity.getLikes() != null) {
			txtEntityLikes.setText(entity.getLikes().toString());
		}
		
		if(entity.getViews() != null) {
			txtEntityViews.setText(entity.getViews().toString());
		}
		
		if(entity.getComments() != null) {
			txtEntityComments.setText(entity.getComments().toString());
		}
	}

	@Override
	protected void onDestroy() {
		Socialize.destroy(this);
		super.onDestroy();
	}
}
