.. include:: header.inc

.. _getting_started:		
	
===============
Getting Started
===============

.. note:: Socialize supports **Android v2.2 and above and does not support LDPI devices.**

**IMPORTANT NOTE FOR USERS UPGRADING**
--------------------------------------
If you are currently using an earlier version of Socialize (before v2.8) you will notice we no longer distribute a JAR file.

**YOU MUST DELETE THE EXISTING socialize-xxx.jar FILE** before importing the new version.

In addition, **a new activity definition is required in your AndroidManifest.xml for Facebook.** (see below)

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
The Socialize SDK is delivered as an Android Library Project, simply import the Socialize SDK project 
into your development environment and link it to your project.

Using Eclipse
~~~~~~~~~~~~~

The project folders contains in the SDK download are **not eclipse projects**, but you can easily create new projects
from the source.

In eclipse, select File->New->Other...

.. image:: images/2.8.4/setup0.png

Then select "Android Project from Existing Code"

.. image:: images/2.8.4/setup1.png

Browse to the location into which you unzipped the SDK

.. image:: images/2.8.4/setup2.png

You may want to change the names of the imported projects as the defaults are often less than ideal

.. image:: images/2.8.4/setup3.png

.. note::
    **If** you check the "Copy projects into workspace" option you will need to re-create the links to library projects as
    these will be broken during the import.

Occasionally Eclipse will not correctly build the projects immediately after they are imported, if this happens just do
a full "clean" of these projects:

.. image:: images/2.8.4/setup4.png

|

.. image:: images/2.8.4/setup5.png

|

.. image:: images/2.8.4/setup6.png

Required Libraries
~~~~~~~~~~~~~~~~~~

The Socialize SDK depends on 2 external libraries:

 - android-ioc
 - facebook-android-sdk

Both of these are Android Library Projects are are also included in the SDK however you can also reference these directly 
from their respective source repositories:

 - https://github.com/socialize/android-ioc
 - https://github.com/facebook/facebook-android-sdk

2. Set up your Socialize Keys
=============================
Once you have registered on the GetSocialize.com website and created an application, you will have
been given two "oAuth" authentication keys, a *consumer key* and a *consumer secret*.

Create a configuration file in the **assets** path of your project called **socialize.properties**

.. image:: images/socialize.properties.png

Within this file, enter your Socialize consumer key and secret:

.. literalinclude:: snippets/props.txt
   :language: properties

(Replace 00000000-0000-0000-000000000000 with your key/secret from your Socialize account)

3. Configure your AndroidManifest.xml
=====================================

Add the following lines to your AndroidManifest.xml within the **<manifest...>** element (above the <application...> element)

.. literalinclude:: snippets/permissions.txt
   :language: xml
   :tab-width: 4

Add the following lines to your AndroidManifest.xml within the **<application...>** element

.. literalinclude:: snippets/manifest.txt
   :language: xml
   :tab-width: 4
	
4. Configure Facebook Integration
=================================
To add Facebook authentication, you'll need a Facebook App ID.
  
If you already have a Facebook app, you can skip this section, otherwise refer to :doc:`facebook` for more information.

Once you have your facebook app ID, you can add it to the **socialize.properties** config file:

.. literalinclude:: snippets/props_fb.txt
   :language: properties
   
5. Include Socialize in your App
=================================
Now that you have your environment all setup, it's time to include Socialize.

You can either install the pre-packaged Socialize Action Bar which includes the entire suite of Socialize features in a matter of minutes

.. literalinclude:: ../../../../demo/src/com/socialize/demo/snippets/ActionBarSample.java
	:start-after: begin-snippet-0
	:end-before: end-snippet-0

.. image:: images/action_bar_std.png

Or if you prefer a more customized approach use our simple SDK interfaces to "roll your own"

.. image:: images/action_bar_mockup.jpg

Checkout the :doc:`action_bar` section for more details on the default Socialize Action Bar, or head over to the :doc:`sdk_user_guide` section to start customizing Socialize to suit your needs.

.. note:: 

	Each Action Bar instance in your app is bound to an *Entity*.  An Entity is simply an item of content in your app.
	Each Socialize action (comment, share, like etc.) is associated with an Entity.  
	
	An entity can be any item of content like a website, photo or person but MUST be given a unique key within your app.
	
	It is not necessary to explicitly create an Entity object when rendering the Action bar as this will be done for you, 
	however entities *can* be created manually.  

	Refer to the :doc:`entities` section for details on creating entities directly using the SDK.

.. raw:: html
   :file: snippets/setup_complete.html

Next Steps...
=============

Visit your App dashboard on the Socialize website to enable additional features like SmartAlerts.

http://www.getsocialize.com/apps/

.. include:: footer.inc	
