.. include:: header.inc

.. _getting_started:		
	
===============
Getting Started
===============

.. raw:: html

	<script type="text/javascript" charset="utf-8">
	  var is_ssl = ("https:" == document.location.protocol);
	  var asset_host = is_ssl ? "https://s3.amazonaws.com/getsatisfaction.com/" : "http://s3.amazonaws.com/getsatisfaction.com/";
	  document.write(unescape("%3Cscript src='" + asset_host + "javascripts/feedback-v2.js' type='text/javascript'%3E%3C/script%3E"));
	</script>
	
	<script type="text/javascript" charset="utf-8">
	  var feedback_widget_options = {};
	  feedback_widget_options.display = "overlay";  
	  feedback_widget_options.company = "socialize";
	  feedback_widget_options.placement = "right";
	  feedback_widget_options.color = "#222";
	  feedback_widget_options.style = "question";
	  feedback_widget_options.product = "socialize_android_sdk";
	  feedback_widget_options.limit = "3";
	  GSFN.feedback_widget.prototype.local_base_url = "http://support.getsocialize.com";
	  GSFN.feedback_widget.prototype.local_ssl_base_url = "http://support.getsocialize.com";
	  var feedback_widget = new GSFN.feedback_widget(feedback_widget_options);
	</script>

Introduction
------------
Socialize is a drop-in social platform for iOS and Android which allows developers to add social 
features to their app in a matter of minutes.

What's New
------------
If you're a Socialize veteran, check out our :ref:`whats_new` section for latest updates.

5 Steps to Using Socialize
--------------------------

1. Install the SDK 
==================
The Socialize SDK is delivered as a single JAR file, simply copy the socialize-x.x.x.jar file 
from the **dist** folder to the **libs** path of your Android project.

If you're using eclipse, you'll need to add the socialize-x.x.x.jar as a referenced library:

.. image:: images/library_refs.png

.. note:: Applications targeting older versions of Android (1.6 and below)**

Socialize does not support Android versions below v2.1-update1

2. Set up your Socialize Keys
=============================
Once you have registered on the GetSocialize.com website and created an application, you will have
been given two "oAuth" authentication keys.  A "consumer key", and a "consumer secret".

The recommended, and simples way to authenticate with Socialize is to create a configuration file in 
the **assets** path of your project called **socialize.properties**

.. image:: images/socialize.properties.png

Within this file, enter your Socialize consumer key and secret:

.. include:: snippets/props.txt

(Replace 00000000-0000-0000-000000000000 with your key/secret from your Socialize account)

3. Configure your AndroidManifest.xml
=====================================
Add the following lines to your AndroidManifest.xml under the **<application...>** element

.. include:: snippets/manifest.txt

Add the following lines to your AndroidManifest.xml under the **<manifest...>** element

.. include:: snippets/permissions.txt
	
4. Configure Facebook Integration (Optional)
============================================
It is strongly recommended that users be able to authenticate with Facebook when using Socialize so as to 
maximize the exposure and promotion of your app.

This provides significant benefits to both your application and your users including:

1. Improved user experience through personalized comments
2. Automatic profile creation (user name and profile picture)
3. Ability to automatically post user comments and likes to Facebook
4. Promotes your app on Facebook by associating your app with comments

To add Facebook authentication, you'll need a Facebook App ID.
  
If you already have a Facebook app, you can skip this section, otherwise refer to :doc:`facebook` for more information.

Once you have your facebook app ID, you can add it to the **socialize.properties** config file:

.. include:: snippets/props_fb.txt
	
5. Include Socialize in your App!
=================================
Now that you have your environment all setup, it's time to include Socialize.  

The core component of the Socialize SDK is the "Action Bar"

.. image:: images/action_bar.png

This is a general purpose toolbar that sits at the bottom of your app and provides a central "one-stop-shop" 
of social features for your users.

Each Action Bar instance in your app is bound to an *Entity*.  An Entity is simply an item of content in your app.
Each Socialize action (comment, share, like etc.) is associated with an Entity.  

An entity can be any item of content like a website, photo or person but MUST be given a unique key within your app.

It is not necessary to explicitly create an Entity object when rendering the Action bar as this will be done for you, 
however entities *can* be created manually.  

.. note:: Refer to the :ref:`entities` section for details on creating entities directly using the SDK.

The Action Bar is designed to automatically "pin" iteself to the bottom of your view.  
Adding the Action Bar to your app is done with a simple call to **showActionBar** from the SocializeUI instance:

.. include:: snippets/action_bar.txt

Next Steps...
=============

Visit your App dashboard on the Socialize website to enable additional features like SmartAlerts.

http://www.getsocialize.com/apps/

.. include:: footer.inc	
