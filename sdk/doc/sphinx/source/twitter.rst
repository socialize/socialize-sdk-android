.. include:: header.inc

===================
Twitter Integration
===================

Introduction
------------

It is strongly recommended that users be able to authenticate with Twitter when using Socialize so as to 
maximize the exposure, and promotion of your app.

This provides significant benefits to both your application, and your users including:

1. Improved user experience through personalized comments
2. Automatic profile creation (user name and profile picture)
3. Ability to automatically post user comments and likes to Twitter
4. Promotes your app on Twitter by associating your app with a users' tweets

To add Twitter authentication, you'll need a Twitter Application and the consumer key/consumer secret for the Twitter app.  
If you already have a Twitter app, you can skip this section.

Setting up Twitter
-------------------

Creating a Twitter Application
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
If you **do not** already have a Twitter app just follow these simple steps:

	1. First create a Twitter app.  Go to https://dev.twitter.com/ and create a new app:
	
		.. image:: images/tw_create_app.png
		
	2. When creating the app, make sure you specify a callback URL.  
	   This can be any value, and is not actually called during authentication but Twitter requires a 
	   valid URL for callback otherwise authentication will fail.
	   
		.. note:: 

			Make sure you specify a callback URL
	
		.. image:: images/tw_app_details.png
		
	3. Change the permissions on the app to Read/Write
		
		The default permissions for new Twitter Apps is Read Only, this must be changed to Read/Write.
		
		Your Twitter Consumer Key and Consumer Secret is also displayed on this page
		
		.. image:: images/tw_app_info.png
		
		These settings can be altered from the Settings tab on your Twitter App page
		
		.. image:: images/tw_app_permissions.png		
		
.. _propagate_tw:

Configuring Twitter in Socialize
--------------------------------
Once you have a twitter application, simply add your Twitter consumer key and secret to your **socialize.properties** file:

.. literalinclude:: snippets/props_twitter.txt
   :language: properties

Linking Users with Twitter
---------------------------

To be able to post to a user's Twitter feed the user must first link with their Twitter account

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/TwitterSnippets.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0

If you already have Twitter integration in your app and do not wish to replace this with the Socialize implementation you 
can still link the user to Twitter within Socialize by providing the user's Twitter auth token and secret

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/TwitterSnippets.java
	:start-after: begin-snippet-1
	:end-before: end-snippet-1
	
To unlink a user from their Twitter account simple call **unlink**.  This call executes synchronously.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/TwitterSnippets.java
	:start-after: begin-snippet-3
	:end-before: end-snippet-3

Posting to Twitter
-------------------

By default all actions created using the SDK will provide the end user with the opportunity to share their 
action with Twitter (if Twitter has been configured) however it is also possible to manually post content to a user's
Twitter feed.

Posting Socialize Entities to Twitter
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The simplest and most effective use of Twitter within Socialize is to post entities.  This approach takes full advantage of 
Socialize SmartDownloads which helps maximize the effectiveness of your social strategy.

To post an entity to a user's Twitter feed simply call the **tweetEntity** method.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/TwitterSnippets.java
	:start-after: begin-snippet-4
	:end-before: end-snippet-4

.. include:: footer.inc	
