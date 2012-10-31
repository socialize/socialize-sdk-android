.. include:: header.inc

.. _SmartAlerts: http://blog.getsocialize.com/smartalerts

.. _notifications:	
	
================================
SmartAlerts (Push Notifications)
================================

=================================	==========================================================================================
.. image:: images/smartalerts.png	.. raw:: html

										<div style="font-size:16pt;font-weight:bold;padding:10px">Welcome to The Loop</div>
										<div style="font-size:12pt;padding:10px">
									 	The Loop is a viral loop in your app that puts your users to work boosting 
									 	downloads and re-engagement in your app.
									 	<br/><br/> 
									 	Use Socialize SmartAlerts to create a viral loop in your app where a users' actions help 
									 	bring other users back into your app.
										</div>

=================================	==========================================================================================

Setup SmartAlerts
-----------------

Step 1: Enable Notifications on http://www.getsocialize.com
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

For SmartAlerts to work they must be enabled on a compatible plan at http://getsocialize.com/apps

Select your app and click "Notification Settings"

.. image:: images/notification_settings.png

Then select "enabled"

.. image:: images/notification_enable.png

.. note:: You must enter your Android package name into the `Socialize website <http://getsocialize.com/apps>`_ to enable notifications

Step 2: Add Configuration to AndroidManifest.xml
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The default configuration for Socialize needs to be augmented slightly for push notifications. 

.. note:: Make sure you replace every occurrence of **your_package_name** with the package name of your app!

Add the following additional configurations to the **<application.../>** element of your **AndroidManifest.xml**

.. literalinclude:: snippets/notification_manifest_snippet0.txt
   :language: xml
   :emphasize-lines: 15,19
   :tab-width: 4

Add the following additional configurations to the **<manifest.../>** element of your **AndroidManifest.xml**

.. literalinclude:: snippets/notification_manifest_snippet1.txt
   :language: xml
   :emphasize-lines: 5,7
   :tab-width: 4

The full **AndroidManifest.xml** should look something like this

.. literalinclude:: snippets/notification_manifest.txt
   :language: xml
   :emphasize-lines: 14,16,41,45
   :tab-width: 4

Step 3: Create an Entity Loader
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In order for Socialize to know how to handle a notification it needs an "Entity Loader".  This is a class 
provided by you which tells Socialize how to load content in your app.

Refer to the :ref:`entity_loader` section for details on how to implement a Socialize Entity Loader.

Testing SmartAlerts
-------------------

When implementing SmartAlerts it is useful to know whether all the setup steps have been completed successfully and that messages
are indeed being sent.  

The simplest way to verify that SmartAlerts are working for your app is to send a test message from the Socialize website.  

This can be found in the dash board for your app:

.. image:: images/smart_alert.png 

Locating your Device Token
~~~~~~~~~~~~~~~~~~~~~~~~~~

If you are still experiencing problems and want to post a question to our support forum, it is useful to include your device token 
when posting any question.  To determine your device token you will first need to enable the **INFO** level logging in Socialize.  

Refer to the :ref:`debug_logs` section for more information on enabling logs.

Once info logging is enabled, a **new** instance of your app should produce log entries that look something like this::
	
	INFO/Socialize(15860): Not registered with GCM, sending registration request...
	INFO/Socialize(15860): Registration with GCM successful: APA91bHd0aL-d75F6_7NcDf_nil8...OBg5-Ixk_8c9rg9b14fsVwvMXTy4
	INFO/Socialize(15860): Registration with Socialize for GCM successful.	

The "Registration with GCM successful" log entry shows the device token used for SmartAlerts

I already have a BroadcastReceiver
----------------------------------
.. raw:: html
   :file: snippets/expert_warning_push.html
   
If you already have a BroadcastReceiver defined in your application, you can simply call the Socialize handler
in your existing broadcast receiver's **onReceive()** method:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/SmartAlertSnippets.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0

Make sure however, that you add the intent filters and permissions required by Socialize to your existing receiver definition.

I am already using Google Cloud Messaging
-----------------------------------------
.. raw:: html
   :file: snippets/expert_warning_push.html
   
If you are already using Google Cloud Messaging you can simply add Socialize to your existing implementation.

The first step is to add your GCM sender ID to your **socialize.properties** file

.. literalinclude:: snippets/props_gcm.txt
   :language: properties

Next, in your **GCMIntentService** you simply need to give Socialize a chance to handle messages recieved by GCM:

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/SmartAlertSnippets.java
	:start-after: begin-snippet-1
	:end-before: end-snippet-1
	
The call to **SmartAlertUtils.onMessage** will return **true** if Socialize has handled the message (that is, if the message was intended for Socialize), 
otherwise it can be handled by you in the normal way. 

.. include:: footer.inc