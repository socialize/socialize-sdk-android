.. include:: header.inc

.. _SmartAlerts: http://blog.getsocialize.com/smartalerts

.. _notifications:	
	
================================
SmartAlerts (Push Notifications)
================================

Introduction
------------
In v1.3 of Socialize we introduced SmartAlerts_.  This provides your app with a simple and effective way to bring users back into the 
"viral loop" of the app.

.. image:: images/comment.png
.. image:: images/subscribe.png


SmartAlerts in Comment Threads
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When a user posts a comment they can elect to subscribe to updates for that topic.  

When another user then posts a comment, the original user will receive a push notification to their device bringing them back into the app.

Step 1: Enable Notifications on http://www.getsocialize.com
-----------------------------------------------------------

For SmartAlerts to work they must be enabled on a compatible plan at http://getsocialize.com/apps

Select your app and click "Notification Settings"

.. image:: images/notification_settings.png

Then select "enabled"

.. image:: images/notification_enable.png

.. note:: You must enter your Android package name into the `Socialize website <http://getsocialize.com/apps>`_ to enable notifications

Step 2: Add Configuration to AndroidManifest.xml
------------------------------------------------

The default configuration for Socialize needs to be augmented slightly for push notifications. 

.. note:: Make sure you replace every occurrance of **your_package_name** with the package name of your app!

.. note:: Make sure to add your **Entity Loader Class Name** to the service meta data!

Add the following additional configurations to the **<application.../>** element of your **AndroidManifest.xml**

.. include:: snippets/notification_manifest_snippet0.txt

Add the following additional configurations to the **<manifest.../>** element of your **AndroidManifest.xml**

.. include:: snippets/notification_manifest_snippet1.txt

The full **AndroidManifest.xml** should look something like this

.. include:: snippets/notification_manifest.txt

I already have a BroadcastReceiver defined
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you already have a BroadcastReceiver defined in your application, you can simply call the Socialize handler
in your existing broadcast receiver's **onReceive()** method:

.. include:: snippets/broadcast_handle.txt

Make sure however, that you add the intent filters and permissions required by Socialize to your existing receiver definition.

I am already using Google C2DM for Push Notifications
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

If you are already using Google's C2DM for push notifications you will need to enter your C2DM sender credentials into the 
Socialize website.  This is due to a limitation of Google's C2DM service which does not allow multiple senders for the same 
app.

.. note:: You will still need to configure SmartAlerts for Socialize in your app (follow the instructions in the BroadcastReceiver section above)

.. image:: images/c2dm.png 

Step 3: Create an Entity Loader
-------------------------------

In order for Socialize to know how to handle a notification it needs an "Entity Loader".  This is a class 
provided by you which tells Socialize how to load content in your app.

Refer to the :ref:`entity_loader` section for details on how to implement a Socialize Entity Loader.

Step 4: Registering for Google C2DM
-----------------------------------

Socialize provides a default quota of SmartAlerts that should satisfy the needs of most users, however if you are experiencing issues with SmartAlerts quota 
you can always simply register your own C2DM token with Socialize.

Google's Cloud To Device (C2DM) system allows any app developer to register and obtain a token. Configuring your application for SmartAlerts using your own C2DM token is a 3 step process:

Step 1: Register with Google for C2DM
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
To register your app with Google's C2DM system, just go to http://code.google.com/android/c2dm and select: "Sign Up for C2DM":

.. image:: images/c2dm_signup0.png

Fill out all the required information and submit, you should see a message like this:

.. image:: images/c2dm_signup1.png

.. note:: Make sure you retain the Sender ID you used during this sign up process (it will most likely be your Google email address), you will need this in the next step. 

 
Step 2: Add your C2DM sender ID to your **socialize.properties** file
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
The Socialize SDK needs to know who will be sending SmartAlerts to the device.  Simply add your C2DM Sender ID to your **socialize.properties** file 
under the key **socialize.c2dm.sender.id**

.. include:: snippets/props_c2dm.txt

Step 3: Add your C2DM token to the Socialize website
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Once you receive your C2DM token from Google, simply add it to your account on the getsocialize.com website:

.. image:: images/c2dm.png 

Testing SmartAlerts
-------------------

When implementing SmartAlerts it is useful to know whether all the setup steps have been completed successfully and that messages
are indeed being sent.  

The simplest way to verify that SmartAlerts are working for your app is to send a test message from the Socialize website.  

This can be found in the dashboard for your app:

.. image:: images/smart_alert.png 

Locating your Device Token
~~~~~~~~~~~~~~~~~~~~~~~~~~

.. note:: The Device Token is DIFFERENT to the C2DM Token.  Each Device has a unique token whereas your C2DM token (if you have one) corresponds to your Sender ID 


If you are still experiencing problems and want to post a question to our support forum, it is useful to include your device token 
when posting any question.  To determine your device token you will first need to enable the **INFO** level logging in Socialize.  

Refer to the :ref:`debug_logs` section for more information on enabling logs.

Once info logging is enabled, a **new** instance of your app should produce log entries that look something like this::
	
	INFO/Socialize(15860): Not registered with C2DM, sending registration request...
	INFO/Socialize(15860): Registration with C2DM successful: APA91bHd0aL-d75F6_7NcDf_nil8...OBg5-Ixk_8c9rg9b14fsVwvMXTy4
	INFO/Socialize(15860): Registration with Socialize for C2DM successful.	

The "Registration with C2DM successful" log entry shows the device token used for SmartAlerts


.. include:: footer.inc