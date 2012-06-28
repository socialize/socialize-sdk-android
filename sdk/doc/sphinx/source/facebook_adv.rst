.. include:: header.inc

====================
Advanced Facebook 
====================

.. raw:: html
   :file: snippets/expert_warning.html

Linking Users with Facebook
---------------------------

If you are not using the ActionBar or want to manually link a user to their Facebook account you can do so with the **link** method

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0

If you already have Facebook integration in your app and do not wish to replace this with the Socialize implementation you 
can still link the user to Facebook within Socialize by providing the user's Facebook auth token

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-1
	:end-before: end-snippet-1
	
Unlinking Users from Facebook
-----------------------------
	
To unlink a user from their Facebook account simple call **unlink**.  This call executes synchronously.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-3
	:end-before: end-snippet-3

Posting to Facebook
-------------------
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
	
	
Posting Photos to Facebook
--------------------------

You can easily post photo's to a user's Facebook Photo Album using the **post** method on **FacebookUtils**
	
.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-9
	:end-before: end-snippet-9

Facebook Single Sign On (SSO)
-----------------------------

Some users have reported having problems with the Single Sign On implementation in the Facebook SDK.

If you have experienced problems with this (e.g. the "invalid_key" error), you can easily disable this feature:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-2
	:end-before: end-snippet-2

.. include:: footer.inc	
