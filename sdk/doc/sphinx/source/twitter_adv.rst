.. include:: header.inc

===================
Advanced Twitter
===================

.. raw:: html
   :file: snippets/expert_warning.html
   
Linking Users with Twitter
---------------------------

If you are not using the ActionBar or want to manually link a user to their Twitter account you can do so with the **link** method

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/TwitterSnippets.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0

If you already have Twitter integration in your app and do not wish to replace this with the Socialize implementation you 
can still link the user to Twitter within Socialize by providing the user's Twitter auth token and secret

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/TwitterSnippets.java
	:start-after: begin-snippet-1
	:end-before: end-snippet-1
	
Unlinking Users from Twitter
----------------------------	
	
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

Posting Directly to Twitter
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you want complete control over what is posted to Twitter, Socialize provides a simple interface to the Twitter REST API.

Refer to the Twitter REST API documentation for more specific implementation information

http://dev.twitter.com/docs/api

Sending a **Tweet**

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/TwitterSnippets.java
	:start-after: begin-snippet-5
	:end-before: end-snippet-5

Executing a **POST**

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/TwitterSnippets.java
	:start-after: begin-snippet-6
	:end-before: end-snippet-6
	
If you want to take full advantage of Socialize SmartDownloads and post the auto-generated SmartDownload urls for your entity or app you can 
do this by creating a simple share object without propagation first.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/TwitterSnippets.java
	:start-after: begin-snippet-8
	:end-before: end-snippet-8
	
Executing a **GET**

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/TwitterSnippets.java
	:start-after: begin-snippet-7
	:end-before: end-snippet-7
	
	
Posting Photos to Twitter
-------------------------

You can easily post photo's to a user's Twitter Photos using the **tweetPhoto** method on **TwitterUtils**
	
.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/TwitterSnippets.java
	:start-after: begin-snippet-9
	:end-before: end-snippet-9
	

.. include:: footer.inc	
