.. include:: header.inc

.. _notifications:	
	
=============================
Enabling Push Notifications
=============================

Introduction
------------
In v1.3 of Socialize we introduced push notifications.  This provides your app with a simple and effective way to bring users back into the 
"viral loop" of the app.

.. image:: images/comment.png
.. image:: images/subscribe.png


LiveAlerts in Comment Threads
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When a user posts a comment they can elect to subscribe to updates for that topic.  

When another user then posts a comment, the original user will receive a push notification to their device bringing them back into the app.

Step 1: Enable Notifications on http://www.getsocialize.com
-----------------------------------------------------------

For Push Notifications to work they must be enabled on a compatible plan at http://getsocialize.com/apps

Select your app and click "Notification Settings"

.. image:: images/notification_settings.png

Then select "enabled"

.. image:: images/notification_enable.png

.. note:: You must enter your Android package name into the `Socialize website <http://getsocialize.com/apps>`_ to enable notifications

Step 2: Add Configuration to AndroidManifest.xml
------------------------------------------------

The default configuration for Socialize needs to be augmented slightly for push notifications. 

.. note:: Make sure you replace every occurrance of **your_package_name** with the package name of your app!


Add the following additional configurations to the **<application.../>** element of your **AndroidManifest.xml**

.. include:: snippets/notification_manifest_snippet0.txt

Add the following additional configurations to the **<manifest.../>** element of your **AndroidManifest.xml**

.. include:: snippets/notification_manifest_snippet1.txt

The full **AndroidManifest.xml** should look something like this

.. include:: snippets/notification_manifest.txt

I already have a BroadcastReceiver defined!
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you already have a BroadcastReceiver defined in your application, you can simply call the Socialize handler
in your existing broadcast receiver's **onReceive()** method:

.. include:: snippets/broadcast_handle.txt

Make sure however, that you add the intent filters and permissions required by Socialize to your existing receiver definition.

Step 3: Create an Entity Loader
-------------------------------

In order for Socialize to know how to respond to a notification it needs an "Entity Loader".  This is a class 
provided by you which tells Socialize how to load content in your app.

Refer to the :ref:`entity_loader` section for details on how to implement a Socialize Entity Loader.

.. include:: footer.inc