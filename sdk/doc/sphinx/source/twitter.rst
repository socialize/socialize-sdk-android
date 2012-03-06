.. include:: header.inc

====================
Twitter Integration
====================

Introduction
------------

It is strongly recommended that users be able to authenticate with Facebook and/or Twitter when using Socialize so as to 
maximize the exposure, and promotion of your app.

This provides significant benefits to both your application, and your users including:

1. Improved user experience through personalized comments
2. Automatic profile creation (user name and profile picture)
3. Ability to automatically post user comments and likes to Facebook and/or Twitter
4. Promotes your app on Facebook and/or Twitter by associating your app with comments/tweets

To add Twitter authentication, you'll need a Twitter Application and the consumer key/consumer secret for the Twitter app.  
If you already have a Twitter app, you can skip this section.

Creating a Twitter Application
-------------------------------
If you **do not** already have a Twitter app just follow these simple steps:

	1. First create a Twitter app.  Go to https://dev.twitter.com/ and create a new app:
	
		.. image:: images/fb_add.png
		
	2. Your newly created Twitter app will have an ID, which is the ID used in Socialize and can be found on your Twitter Developer page:
	
		For example, this is the Twitter App page for Socialize:
		
		.. image:: images/fb_id.png
		
	3. Export the hash key from your Android keystore.  All Android apps are required to be signed prior to running on a device and you will already have created a keystore while building your Android app.
		  
		Your keystore contains a public key which Twitter needs to identify your Android app.
		
		On your development machine (where you are building your Android app), run the following command to generate a key hash::

			keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
			
		**Make sure you use the correct key store to generate the hash**.  The example above (taken from the Twitter documentation)
		indicates use of the **debug.keystore**.  You should use whichever keystore you chose when building the version of your app 
		you are going to publish.
	
	4. Next add your key hash to your Twitter app.
		
		Paste your key into the "key hash" section of your Twitter app.
		
		.. image:: images/fb_hash.png
		
		Generally it makes sense to add BOTH your **debug** and your **release** keys to Twitter to avoid problems during development and testing.
	
You should now be ready to authenticate user's of your app with Twitter when they are using Socialize, jump to the :ref:`fb_snippet` to find out how.  	

.. _propagate_fb:

Propagating Socialize Actions to Twitter
-----------------------------------------

Social actions like Comment and Like can optionally be propagated to social networks like Twitter via the SDK.

.. note:: 

	Propagation is automatic when using the Socialize Action Bar.  The following is only required for developers
	electing to use the SDK manually**

The following example shows posting a comment and simultaneously sharing the comment to the user's twitter wall.

.. include:: snippets/create_comment_fb.txt

Twitter Single Sign On (SSO)
-----------------------------

Some users have reported having problems with the Single Sign On implementation in the Twitter SDK.

If you have experienced problems with this (e.g. the "invalid_key" error), you can easily disable this feature:

.. include:: snippets/no_sso.txt

.. include:: footer.inc	
