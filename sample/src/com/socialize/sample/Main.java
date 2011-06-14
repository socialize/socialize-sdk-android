package com.socialize.sample;

import android.app.Activity;
import android.os.Bundle;

public class Main extends Activity {
	
//	private Socialize socialize;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        
//        socialize = new Socialize();
//        socialize.init(this);
//        
//        socialize.authenticate("", "", new SocializeAuthListener() {
//			
//			@Override
//			public void onError(SocializeException error) {
//				// Handle error
//				
//			}
//			
//			@Override
//			public void onAuthFail(SocializeException error) {
//				// Handle auth fail
//			}
//			
//			@Override
//			public void onAuthSuccess(SocializeSession session) {
//				 socialize.addComment("", "", new CommentAddListener() {
//
//					@Override
//					public void onError(SocializeException error) {
//						// Handle error
//						
//					}
//
//					@Override
//					public void onCreate(Comment entity) {
//						// On 
//						
//					}
//					 
//				 });
//				
//			}
//		});
        
    }

	@Override
	protected void onDestroy() {
//		socialize.destroy();
		super.onDestroy();
	}
    
}