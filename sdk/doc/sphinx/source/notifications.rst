.. include:: header.inc

.. _notifications:	
	
==================
Push Notifications
==================

Introduction
------------
In v1.3 of Socialize we introduced push notifications.  This provides your app with a simple and effective way to bring users back into the 
"viral loop" of the app.

When a user posts a comment they are (optionally) subscribed to updates for that topic.  When another user then posts a comment, the original 
user will receive a push notification to their device bringing them back into the app.

Enabling push notifications for Socialize requires a few additional setup steps...


Step 1: Enable Notifications on http://www.getsocialize.com
-----------------------------------------------------------

.. image:: images/notification_settings.png

Step 2: Add Configuration to AndroidManifest.xml
------------------------------------------------

The default configuration for Socialize needs to be augemented slightly for push notifications. 

NOTE:  Make sure you replace every occurrance of **your_package_name** with the package name of your app!

.. include:: snippets/notification_manifest.txt

Step 3: Create an Entity Loader
-------------------------------

In order for Socialize to know how to respond to a notification it needs an "Entity Loader".  This is a class 
provided by you which tells Socialize how to load content in your app.

Refer to the :ref:`entity_loader` section for details on how to implement a Socialize Entity Loader.

.. include:: footer.inc