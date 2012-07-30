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
   :emphasize-lines: 14,16,37,41
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

.. note:: The Device Token is DIFFERENT to the C2DM Token.  Each Device has a unique token whereas your C2DM token (if you have one) corresponds to your Sender ID 

If you are still experiencing problems and want to post a question to our support forum, it is useful to include your device token 
when posting any question.  To determine your device token you will first need to enable the **INFO** level logging in Socialize.  

Refer to the :ref:`debug_logs` section for more information on enabling logs.

Once info logging is enabled, a **new** instance of your app should produce log entries that look something like this::
	
	INFO/Socialize(15860): Not registered with C2DM, sending registration request...
	INFO/Socialize(15860): Registration with C2DM successful: APA91bHd0aL-d75F6_7NcDf_nil8...OBg5-Ixk_8c9rg9b14fsVwvMXTy4
	INFO/Socialize(15860): Registration with Socialize for C2DM successful.	

The "Registration with C2DM successful" log entry shows the device token used for SmartAlerts

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

I am already using Google C2DM
------------------------------
.. raw:: html
   :file: snippets/expert_warning_push.html

If you are already using Google's C2DM for push notifications you will need to enter your C2DM sender credentials into the 
Socialize website.  This is due to a limitation of Google's C2DM service which does not allow multiple senders for the same 
app.

.. note:: You will still need to configure SmartAlerts for Socialize in your app (follow the instructions in the BroadcastReceiver section above)

.. image:: images/c2dm.png 

High Volume Usage
-----------------
.. raw:: html
   :file: snippets/expert_warning_push.html
   
If you app is expected to have a high volume of SmartAlerts we recommend you register your own Google C2DM token to avoid any unexpected quota issues

Registering for Google C2DM
~~~~~~~~~~~~~~~~~~~~~~~~~~~

Socialize provides a default quota of SmartAlerts that should satisfy the needs of most users, however if you are experiencing issues with SmartAlerts quota 
you can always simply register your own C2DM token with Socialize.

Google's Cloud To Device (C2DM) system allows any app developer to register and obtain a token. Configuring your application for SmartAlerts using your own C2DM token is a 4 step process:

Step 1: Register with Google for C2DM
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
To register your app with Google's C2DM system, just go to http://code.google.com/android/c2dm and select: "Sign Up for C2DM":

.. image:: images/c2dm_signup0.png

Fill out all the required information and submit, you should see a message like this:

.. image:: images/c2dm_signup1.png

.. note:: 
	Make sure you retain the Sender ID you used during this sign up process (it will most likely be your Google email address), you will need this in the next step.
	
	You **may not receive an email from Google** despite the above message in which case you will need to retrieve your token manually in the next step. 

Step 2: Retrieve your C2DM token from Google
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Once you are registered for C2DM with Google you'll need to retrieve your C2DM token.  The simplest way to do this is by issuing a **curl** command 
from your command prompt::

	curl https://www.google.com/accounts/ClientLogin -d Email=<your sender ID> -d "Passwd=<your password>" -d accountType=HOSTED_OR_GOOGLE -d source=MDMServer -d service=ac2dm

This will respond with a message that looks something like this::

	SID=DQAAAMIAAACKP1yxVpldSaWB2ZvNnzZRQB4kk6MyMd...CR_BFwNIvjbwSSJHSe6tK5ix5IKQgOCZcD4AuMluH5C
	LSID=DQAAAMMAAADranxreScVQXuhmvY-fHDb4IlRgz09u...z-UrcSZPHNYr_FhRZ9FM3r6yyN86hZs-uWKOxMaC51H
	Auth=DQAAAMUAAABYV6-h9KWj9IrqIn4plD2kjtr8zvf5S...Kx81M9x0QCC_4Lbl5a13E8vx8kdgoEYzF6JpAiWykOY
	
The C2DM token you need is the alpha-numeric sequence found under the **Auth** key.

Step 3: Add your C2DM sender ID to your **socialize.properties** file
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
The Socialize SDK needs to know who will be sending SmartAlerts to the device.  Simply add your C2DM Sender ID to your **socialize.properties** file 
under the key **socialize.c2dm.sender.id**

.. literalinclude:: snippets/props_c2dm.txt
   :language: properties

Step 4: Add your C2DM token to the Socialize website
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Once you receive your C2DM token from Google, simply add it to your account on the getsocialize.com website:

.. image:: images/c2dm.png 

.. include:: footer.inc