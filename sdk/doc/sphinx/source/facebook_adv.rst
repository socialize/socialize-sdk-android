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
	
.. _open_graph:

Using Facebook Open Graph
-------------------------
From v2.3 onwards Socialize supports Facebook "like" actions by default.  This means that when a user executes a "like" on Socialize and elects to 
share this on Facebook it will be posted as an "Open Graph Like" in the user's Facebook Activity stream.

This default behavior can be disabled via configuration in your **socialize.properties** file

.. literalinclude:: snippets/props_fb_like.txt
   :language: properties

In order to leverage the Facebook Open Graph (OG) it is first important to understand the distinction between "Default" OG actions and custom OG actions.

.. note:: If you are not already familiar with the Facebook Open Graph we recommend reviewing the `Facebook Documentation <https://developers.facebook.com/docs/opengraph/>`_ first

Default Open Graph Actions
###########################

The default OG actions provided by Facebook **do not require any configuration in your Facebook App** and are supported by default, however there is a limited
set of actions available and a corresponding limited set of object types.

The actions supported and the corresponding object types are as follows

+------------+-------------------+---------------+---------------+
| Action     | Path              | Object Types  | Parameter     |
+============+===================+===============+===============+
| like       | me/og.likes       | object        | object        |
+------------+-------------------+---------------+---------------+
| follow     | me/og.follows     | profile       | profile       |
+------------+-------------------+---------------+---------------+
| publish    | me/news.publishes | article       | article       |
+------------+-------------------+---------------+---------------+
| read       | me/news.reads     | article       | article       |
+------------+-------------------+---------------+---------------+
| watch      | me/video.watches  | video.movie   | movie         |
+------------+-------------------+---------------+---------------+
|            |                   | video.episode | movie         |
+------------+-------------------+---------------+---------------+
|            |                   | video.tv_show | movie         |
+------------+-------------------+---------------+---------------+
|            |                   | video.other   | movie         |
+------------+-------------------+---------------+---------------+

The Object Types specified in the Open Graph call **MUST** correspond to a valid *og:type* meta element in the HTML page that represents the object.

For example::

	<meta property="og:type" content="video.movie" /> 

Fortunately Socialize will **automatically generate OG tags** on your entity page, but you must specify the correct type on the entity object itself.

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-10
	:end-before: end-snippet-10
	
Once you have setup your Entity with the correct type you can force an Open Graph post by changing the PostData in the SocialNetworkListener.  
Here's a complete example

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-11
	:end-before: end-snippet-11
	
You can also force an OG Facbook post in a Share

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-13
	:end-before: end-snippet-13	
	
The corresponding entity page on Socialize will automatically populate the required OG meta tags::

	<meta property="og:title" content="My Entity Name" /> 
	<meta property="og:description" content="...parsed from your URL..." /> 
	<meta property="og:image" content="...parsed from your URL..." />
	<meta property="og:type" content="video.movie" />
	<meta property="og:url" content="...the URL of this page..." />
	
.. raw:: html
   :file: snippets/open_graph_notice.html

If you don't have an actual URL for your entity you can setup the correct description, image etc by changing the meta data on your entity.

Refer to :ref:`entity_no_url` for more details on customizing the entity page.

Custom Open Graph Actions
#########################
The Facebook Open Graph allows developers to create custom actions which can more closely represent the activity within your app.  

For example an app that shows movie clips may want to post the fact that a user "watched" a "movie" rather than simply saying a user "shared" a "link".

There is however a fairly lengthy setup process to correctly configure your application to handle custom OG actions.

We recommend reviewing the `Open Graph Tutorial <http://developers.facebook.com/docs/opengraph/tutorial/>`_ to understand how to configure your app for custom Open Graph actions.

Once you have configured your application to accept custom Open Graph actions you can post these actions to Facebook by changing the PostData in the SocialNetworkListener.  

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-12
	:end-before: end-snippet-12
	
Posting Photos to Facebook
--------------------------

You can easily post photos to a user's Facebook Photo Album using the **post** method on **FacebookUtils**
	
.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-9
	:end-before: end-snippet-9

Facebook Single Sign On (SSO)
-----------------------------

Some users have reported having problems with the Single Sign On implementation in the Facebook SDK.

If you have experienced problems with this (e.g. the "invalid_key" error), you can easily disable this feature in your **socialize.properties** file

.. literalinclude:: snippets/props_fb_sso.txt
   :language: properties
   
Or in code at runtime:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/FacebookSnippets.java
	:start-after: begin-snippet-2
	:end-before: end-snippet-2

.. include:: footer.inc	
