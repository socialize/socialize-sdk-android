.. include:: header.inc

====================
Facebook Integration
====================

Introduction
------------

It is strongly recommended that users be able to authenticate with Facebook when using Socialize so as to 
maximize the exposure and promotion of your app.

This provides significant benefits to both your application and your users including:

1. Improved user experience through personalized comments
2. Automatic profile creation (user name and profile picture)
3. Ability to automatically post user comments and likes to Facebook
4. Promotes your app on Facebook by associating your app with comments

To add Facebook authentication, you'll need a Facebook App ID.  If you already have a Facebook app, 
you can skip this section.

Setting up Facebook
-------------------

Creating a Facebook Application
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
If you **do not** already have a Facebook app just follow these simple steps:

	1. First create a Facebook app.  Go to https://developers.facebook.com/apps and create a new app:
	
		.. image:: images/fb_add.png
		
	2. Your newly created Facebook app will have an ID, which is the ID used in Socialize and can be found on your Facebook Developer page:
	
		For example, this is the Facebook App page for Socialize:
		
		.. image:: images/fb_id.png
		
	3. Export the hash key from your Android keystore.  All Android apps are required to be signed prior to running on a device and you will already have created a keystore while building your Android app.
		  
		Your keystore contains a public key which Facebook needs to identify your Android app.
		
		On your development machine (where you are building your Android app), run the following command to generate a key hash::

			keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore | openssl sha1 -binary | openssl base64
			
		**Make sure you use the correct key store to generate the hash**.  The example above (taken from the Facebook documentation)
		indicates use of the **debug.keystore**.  You should use whichever keystore you chose when building the version of your app 
		you are going to publish.
	
	4. Next add your key hash to your Facebook app.
		
		Paste your key into the "key hash" section of your Facebook app.
		
		.. image:: images/fb_hash.png
		
		Generally it makes sense to add BOTH your **debug** and your **release** keys to Facebook to avoid problems during development and testing.
	
Configuring Facebook in Socialize
---------------------------------
Once you have a facebook application, simply add your Facebook app Id your **socialize.properties** file:

.. literalinclude:: snippets/props_facebook.txt
   :language: properties

.. raw:: html
   :file: snippets/facebook_complete.html  

.. _propagate_fb:

Linking Users with Facebook
---------------------------

.. raw:: html
   :file: snippets/expert_warning.html

To be able to post to a user's Facebook wall the user must first link with their Facebook account

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0

If you already have Facebook integration in your app and do not wish to replace this with the Socialize implementation you 
can still link the user to Facebook within Socialize by providing the user's Facebook auth token

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-1
	:end-before: end-snippet-1
	
To unlink a user from their Facebook account simple call **unlink**.  This call executes synchronously.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-3
	:end-before: end-snippet-3

Posting to Facebook
-------------------

.. raw:: html
   :file: snippets/expert_warning.html
   
By default all actions created using the SDK will provide the end user with the opportunity to share their 
action with Facebook (if Facebook has been configured) however it is also possible to manually post content to a user's
Facebook wall.

Posting Socialize Entities to Facebook
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The simplest and most effective use of Facebook within Socialize is to post entities.  This approach takes full advantage of 
Socialize SmartDownloads as well as automatically supporting Facebook Open Graph which helps maximize the effectiveness of your social strategy.

To post an entity to a user's Facebook timeline simply call the **postEntity** method.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-4
	:end-before: end-snippet-4
	
Posting Directly to Facebook
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you want complete control over what is posted to Facebook, Socialize provides a simple interface to the Facebook Graph API.

Refer to the Facebook Graph API documentation for more specific implementation information

http://developers.facebook.com/docs/reference/api/

Executing a **POST**

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-5
	:end-before: end-snippet-5
	
If you want to take full advantage of Socialize SmartDownloads and post the auto-generated SmartDownload urls for your entity or app you can 
do this by creating a simple share object without propagation first.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-8
	:end-before: end-snippet-8
	
Executing a **GET**

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-6
	:end-before: end-snippet-6
	
Executing a **DELETE**

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-7
	:end-before: end-snippet-7	

Facebook Single Sign On (SSO)
-----------------------------

Some users have reported having problems with the Single Sign On implementation in the Facebook SDK.

If you have experienced problems with this (e.g. the "invalid_key" error), you can easily disable this feature:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-2
	:end-before: end-snippet-2

.. include:: footer.inc	
